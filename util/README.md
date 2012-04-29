# bee-creative.util - [cc-by] Sebastian Rostock

Wenn sich Probleme mit einem verkettenden, filternden, konvertierenden oder begrenzenden `Iterator` bzw. `Iterable` oder
mit einem verkettenden oder konvertierenden `Comparator` bzw. `Comparable` lösen lassen, kann man sich mit `bee-creative.util` viel Arbeit sparen.
Dazu finden sich in dieser Bibliothek `Filter` für verschiedene logische Operationen, verkettende, filternde sowie gepufferte `Converter`,
statische, dynamische sowie inverse `Conversion`, gepufferte sowie konvertierende `Builder`, `Pointer` unterschiedlicher 
Stärke sowie `Tester` zur Ermittlung von Rechenzeit und Speicherbelegung einer Testmethode.

 

---

TODO Beispiel: CachedConverter


      final CachedConverter<String, String> uniqueString = Converters.cachedConverter(-1, Pointers.WEAK, Pointers.WEAK, Converters.<String>voidConverter());

__________________________________________________________________________________________

#### Beispiel: Filter, Iterable, Converter

In diesem Beispiel soll aus einer Menge benannter Objekte `NamedEntry` die Menge der Objekte ermittelt werden, deren Name mit einem gegebenen Präfix beginnt oder einem gegebenen Suffix endet.

	class NamedEntry {
		String name;
		...
	}
	
	void work(...) {

		// Eingabe
		final String prefix = ...
		final String suffix = ...
		Iterable<NamedEntry> namedEntris = ...

		// Converter
		Converter<NamedEntry, String> nameConverter = new Converter<NamedEntry, String>() {
			public String convert(final NamedEntry input) {
				return input.name;
			}
		};

		// Filter
		Filter<String> prefixFilter = new Filter<String>() {
			public boolean accept(final String input) {
				return input.startsWith(prefix);
			}
		};
		Filter<String> suffixFilter = new Filter<String>() {
			public boolean accept(final String input) {
				return input.endsWith(suffix);
			}
		};
		Filter<NamedEntry> namePrefixFilter = Filters.convertedFilter(nameConverter, prefixFilter);
		Filter<NamedEntry> nameSuffixFilter = Filters.convertedFilter(nameConverter, suffixFilter);
		Filter<NamedEntry> filter = Filters.disjunctionFilter(namePrefixFilter, nameSuffixFilter);
		
		// Ausgabe
		Iterable<NamedEntry> filteredNamedEntris = Iterables.filteredIterable(filter, namedEntris);
		
	}
	
__________________________________________________________________________________________

Beispiel: Iterable, Converter, Conversion, Comparator

In diesem Beispiel soll eine Menge komplexer Objekte (ComplexEntry) zur Anzeige in einer
sortierten Auswahlliste aufbereitet werden. Die für die Anzeige benötigten Texte
(String) werden hierbei mit Hilfe einer aufwändigen Formatierungsmethode
(ComplexFormatConverter) berechnet. Die Sortierung der Auswahlliste soll an Hand der
berechneten Texte erfolgen.

----------------------------------------------------------------------------------------

Eingabe:
- Iterable<ComplexEntry> - Menge der komplexen Objekte

----------------------------------------------------------------------------------------

Ausgabe:
- List<String> - sortierte Liste der Texte zu den komplexen Objekte
- List<ComplexEntry> - nach Text sortierte Liste der komplexen Objekte

----------------------------------------------------------------------------------------

Quelltext:

class ComplexEntry {

static Converter<ComplexEntry, String> ComplexFormatConverter =
new Converter<ComplexEntry, String>() {

@Override
public String convert(final ComplexEntry input) {
final StringBuilder builder = new StringBuilder();
// ... aufwändige Formatierung
return builder.toString();
}

};

// ...

}

void work(...) {

// Eingabe
Iterable<ComplexEntry> complexEntries = ...

// Ausgabe
List<ComplexEntry> sortedComplexEntryList = new ArrayList<ComplexEntry>();
List<String> sortedComplexFormatList = new ArrayList<String>();

// Conversion
List<Conversion<ComplexEntry, String>> conversionList =
new ArrayList<Conversion<ComplexEntry, String>>();
Converter<ComplexEntry, Conversion<ComplexEntry, String>> conversionConverter =
Conversions.staticConversionConverter(ComplexEntry.ComplexFormatConverter);

ConvertedIterable<ComplexEntry, Conversion<ComplexEntry, String>>
conversionIterable = Iterables.convertedIterable(conversionConverter,
complexEntries);

Iterables.appendAll(conversionList, conversionIterable);

// Conversion.Output
Converter<Conversion<?, ? extends String>, String> conversionOutputConverter =
Conversions.conversionOutputConverter();

ConvertedComparator<Conversion<?, ? extends String>, String>
conversionOutputComparator = Comparators.convertedComparator(
conversionOutputConverter, Comparators.stringAlphanumericalComparator());

Collections.sort(conversionList, conversionOutputComparator);

ConvertedIterable<Conversion<ComplexEntry, String>, String>
conversionOutputIterable = Iterables.convertedIterable(conversionOutputConverter,
conversionList);

Iterables.appendAll(sortedComplexFormatList, conversionOutputIterable);

// Conversion.Input
Converter<Conversion<? extends ComplexEntry, ?>, ComplexEntry>
conversionInputConverter = Conversions.<ComplexEntry>conversionInputConverter();

ConvertedIterable<Conversion<ComplexEntry, String>, ComplexEntry>
conversionInputIterable = Iterables.convertedIterable(conversionInputConverter,
conversionList);

Iterables.appendAll(sortedComplexEntryList, conversionInputIterable);

}

__________________________________________________________________________________________

Beispiel: Comparable

In diesem Beispiel soll in einer sortierten Liste von sich nicht überlappenden Bereichen
(Region) nach einem Bereich gesucht werden, der eine gegebene Position enthält.

----------------------------------------------------------------------------------------

Eingabe:
- int - Position
- List<Region> - sortierte Liste der Bereiche

----------------------------------------------------------------------------------------

Ausgabe:
- Region - Bereich, der die Position enthält

----------------------------------------------------------------------------------------

Quelltext:

class Region {

int start;

int length;

// ...

}

Region find(List<Region> ranges, final int position) {

Comparable<Region> comparable = new Comparable<Region>() {

@Override
public int compareTo(Region region) {
if(position < region.start) return -1;
if(position >= (region.start + region.length)) return +1;
return 0;
}

};

int index = Comparables.binarySearch(ranges, comparable);
if(index < 0) return null;
return ranges.get(index);
}

__________________________________________________________________________________________

Beispiel: Pointer, Builder

In diesem Beispiel soll ein statisches Hilfsobjekt (Helper) unter Verwendung einer
automatischen Erzeugung und Zwischenspeicherung zur Verfügung gestellt werden. Wenn das
Hilfsobjekt dann nicht mehr verwendet wird soll es bei Speichermangel und automatisch
gelöscht werden.

----------------------------------------------------------------------------------------

Eingabe:
- Builder<Helper> - Methode zur Erzeugung des Hilfsobjekts

----------------------------------------------------------------------------------------

Ausgabe:
- Builder<Helper> - Methode zur Erzeugung des Hilfsobjekts mit speichersensitiver
Zwischenspeicherung

----------------------------------------------------------------------------------------

Quelltext:

public final class Helper {

private static final Builder<Helper> BUILDER = Builders.synchronizedBuilder(
Builders.cachedBuilder(Pointers.SOFT, new Builder<Helper>() {

@Override
public Helper build() {
return new Helper();
}

}));

public static Helper get() {
return Helper.BUILDER.build();
}

Helper() {
// ...
}

// ...

}


public void work() {

Helper helper = Helper.get();

// ...

}



__________________________________________________________________________________________


##### [cc-by] Sebastian Rostock ( bee-creative@gmx.de )

Dieses Werk ist unter einem Creative Commons Namensnennung 3.0 Deutschland Lizenzvertrag lizenziert. Um die Lizenz anzusehen, gehen Sie bitte zu: [ http://creativecommons.org/licenses/by/3.0/de/ ] oder schicken Sie einen Brief an: [ Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA. ]
