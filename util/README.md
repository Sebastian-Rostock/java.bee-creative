# bee-creative.util

	[cc-by] Sebastian Rostock (bee-creative@gmx.de)

Wenn sich Probleme mit einem verkettenden, filternden, konvertierenden oder begrenzenden `Iterator` bzw. `Iterable` oder mit einem verkettenden oder konvertierenden `Comparator` bzw. `Comparable` lösen lassen, kann `bee-creative.util` die Lösung sein.

In dieser Bibliothek finden sich dazu `Filter` für verschiedene logische Operationen, verkettende, filternde sowie gepufferte `Converter`, statische, dynamische sowie inverse `Conversion`, gepufferte sowie konvertierende `Builder`, `Pointer` unterschiedlicher Stärke sowie `Tester` zur Ermittlung von Rechenzeit und Speicherbelegung einer Testmethode.

### Filter

Ein `Filter` ist eine Methode, die mit den beiden Rückgabewerten *true* und *false* einen gegebenen Eingabewert entweder akzeptieren oder ablehnen kann. Beispielsweise könnten die `Filter` *prefixFilter* und *suffixFilter* einen gegebenen `String` akzeptieren, wenn dieser mit einem bestimmten *prefix* beginnt bzw. *suffix* endet.

	Filter<String> prefixFilter = new Filter<String>() {
		public boolean accept(String input) {
			return input.startsWith(prefix);
		}
	};
	
	Filter<String> suffixFilter = new Filter<String>() {
		public boolean accept(String input) {
			return input.endsWith(suffix);
		}
	};
	
`Filter` entsprechen damit logischen Eigenschaften bzw. Prädikaten und können als soche über logische Operatoren miteinander verklüpft werden. Einige Beispiele hierfür sind:

	Filter<String> prefixOrSuffixFilter = Filters.disjunctionFilter(prefixFilter, suffixFilter);
	Filter<String> prefixAndSuffixFilter = Filters.conjunctionFilter(prefixFilter, suffixFilter);
	Filter<String> prefixXorSuffixFilter = Filters.inverseFilter(Filters.equivalenceFilter(prefixFilter, suffixFilter));

Ein konvertierender `Filter` verbindet einen speziellen `Filter` meist mit einem navigierenden `Converter`, da der `Converter` das Eingabeobjekt für diesen speziellen `Filter` ermittelt. 
Der konvertierende `Filter` *itemNameFilter* könnte beispielsweise ein `Item` akzeptieren, dessen Name mit einem bestimmten *prefix* beginnt oder *suffix* endet.

	class Item {
		String name;
		...
	}
	
	Converter<Item, String> itemNameConverter = new Converter<Item, String>() {
		public String convert(Item input) {
			return input.name;
		}
	};
	
	Filter<Item> itemNameFilter = Filters.convertedFilter(itemNameConverter, prefixOrSuffixFilter);

### Converter

Ein `Converter` ist eine Methode, die ein gegebenes Eingabeobjekt in ein Ausgabeobjekt umwandelt. Bei dieser Umwandlung kann es sich beispielsweise um eine Navigation in einem Objektgraph oder auch das Parsen, Formatieren bzw. Umkodieren des Eingabeobjekts handel.

Der unnötigen Speicherverschwendung, die beim Landen identischer Texten aus Dateien auftreten kann, lässt sich beispielsweise mit dem gepufferter `Converter` *uniqueStringConverter* begegnen, dessen Ausgabeobjekt dem einzigartigen `String` entspricht, der zum Eingabeobjekt äquivalent ist.

	Converter<String, String> voidConverter = Converters.voidConverter();
	Converter<String, String> uniqueStringConverter = Converters.cachedConverter(-1, Pointers.WEAK, Pointers.WEAK, voidConverter);

Der Puffer in diesem `Converter` besteht aus einer `Map`, deren Schlüssel und Werte schwach auf die Eingabe- bzw. Ausgabeobjekte verweisen. Diese schwachen Verweise werden dann automatisch aufgelöst, wenn diese Objekte nur noch über eine `WeakReference` erreichbar sind.

`Converter` zur Navigation 

...

### Conversion

In diesem Beispiel soll eine Menge komplexer Objekte (ComplexEntry) zur Anzeige in einer
sortierten Auswahlliste aufbereitet werden. Die für die Anzeige benötigten Texte
(String) werden hierbei mit Hilfe einer aufwändigen Formatierungsmethode
(ComplexFormatConverter) berechnet. Die Sortierung der Auswahlliste soll an Hand der
berechneten Texte erfolgen.

*Eingabe:*

- Iterable<ComplexEntry> - Menge der komplexen Objekte

*Ausgabe:*

- List<String> - sortierte Liste der Texte zu den komplexen Objekte
- List<ComplexEntry> - nach Text sortierte Liste der komplexen Objekte

*Quelltext:*

	class ComplexEntry {
		static Converter<ComplexEntry, String> ComplexFormatConverter = new Converter<ComplexEntry, String>() {
			@Override
			public String convert(final ComplexEntry input) {
				final StringBuilder builder = new StringBuilder();
				... aufwändige Formatierung
				return builder.toString();
			}
		};
		...
	}
	
	void work(...) {
		// Eingabe
		Iterable<ComplexEntry> complexEntries = ...
		// Ausgabe
		List<ComplexEntry> sortedComplexEntryList = new ArrayList<ComplexEntry>();
		List<String> sortedComplexFormatList = new ArrayList<String>();
		// Conversion
		List<Conversion<ComplexEntry, String>> conversionList = new ArrayList<Conversion<ComplexEntry, String>>();
		Iterables.appendAll(conversionList, Iterables.convertedIterable(Conversions.staticConversionConverter(ComplexEntry.ComplexFormatConverter), complexEntries));
		// Conversion.Output
		Converter<Conversion<?, ? extends String>, String> conversionOutputConverter = Conversions.conversionOutputConverter();
		ConvertedComparator<Conversion<?, ? extends String>, String> conversionOutputComparator = Comparators.convertedComparator(conversionOutputConverter, Comparators.stringAlphanumericalComparator());
		Collections.sort(conversionList, conversionOutputComparator);
		ConvertedIterable<Conversion<ComplexEntry, String>, String> conversionOutputIterable = Iterables.convertedIterable(conversionOutputConverter, conversionList);
		Iterables.appendAll(sortedComplexFormatList, conversionOutputIterable);
		// Conversion.Input
		Converter<Conversion<? extends ComplexEntry, ?>, ComplexEntry> conversionInputConverter = Conversions.<ComplexEntry>conversionInputConverter();
		ConvertedIterable<Conversion<ComplexEntry, String>, ComplexEntry> conversionInputIterable = Iterables.convertedIterable(conversionInputConverter, conversionList);
		Iterables.appendAll(sortedComplexEntryList, conversionInputIterable);
		...
	}
	
### Builder

In diesem Beispiel soll ein statisches Hilfsobjekt (Helper) unter Verwendung einer
automatischen Erzeugung und Zwischenspeicherung zur Verfügung gestellt werden. Wenn das
Hilfsobjekt dann nicht mehr verwendet wird soll es bei Speichermangel und automatisch
gelöscht werden.

*Eingabe:*

- Builder<Helper> - Methode zur Erzeugung des Hilfsobjekts

*Ausgabe:*

- Builder<Helper> - Methode zur Erzeugung des Hilfsobjekts mit speichersensitiver
Zwischenspeicherung

*Quelltext:*

	public final class Helper {
		private static final Builder<Helper> BUILDER = SynchronizedBuilder.of(CachedBuilder.of(Pointers.SOFT, new Builder<Helper>() {	
			@Override
			public Helper create() {
				return new Helper();
			}			
		}));	
		public static Helper get() {
			return Helper.BUILDER.create();
		}	
		Helper() {
			...
		}
		...	
	}
	public void work(...) {
		Helper helper = Helper.get();
		...	
	}
	
### Pointer

...

### Tester

...

---

##### [cc-by] Sebastian Rostock ( bee-creative@gmx.de )

Dieses Werk ist unter einem Creative Commons Namensnennung 3.0 Deutschland Lizenzvertrag lizenziert. Um die Lizenz anzusehen, gehen Sie bitte zu: [ http://creativecommons.org/licenses/by/3.0/de/ ] oder schicken Sie einen Brief an: [ Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA. ]
