# bee-creative.util

Wenn sich Probleme mit einem verkettenden, filternden, konvertierenden oder begrenzenden `Iterator` bzw. `Iterable` oder mit einem verkettenden oder konvertierenden `Comparator` bzw. `Comparable` lösen lassen, kann `bee-creative.util` die Lösung sein.

In dieser Bibliothek finden sich dazu `Filter` für verschiedene logische Operationen, verkettende, filternde sowie gepufferte `Converter`, statische, dynamische sowie inverse `Conversion`, gepufferte sowie konvertierende `Builder`, `Pointer` unterschiedlicher Stärke sowie `Tester` zur Ermittlung von Rechenzeit und Speicherbelegung einer Testmethode.


### Filter

Ein `Filter` ist eine Methode, die mit den beiden Rückgabewerten *true* und *false* einen gegebenen Eingabewert entweder akzeptieren oder ablehnen kann. Beispielsweise könnten die `Filter` *prefixFilter* und *suffixFilter* einen gegebenen `String` akzeptieren, wenn dieser mit einem bestimmten *prefix* beginnt bzw. *suffix* endet.

	String prefix = ...
	String suffix = ...

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
	Filter<String> prefixAndSuffixFilter = Filters.disjunctionFilter(prefixFilter, suffixFilter);
	Filter<String> prefixXorSuffixFilter = Filters.negationFilter(Filters.equivalenceFilter(prefixFilter, suffixFilter));

Die Klasse `Filters` bietet folgende Hilfsmethoden zur erzeugung allgemeiner `Filter` an:
`nullFilter()`,
`classFilter(Class)`,
`acceptFilter()`,
`rejectFilter()`,
`negationFilter(Filter)`,
`containsFilter(Object...)`,
`containsFilter(Collection)`,
`convertedFilter(Converter, Filter)`,
`disjunctionFilter(Filter, Filter)`,
`conjunctionFilter(Filter, Filter)`,
`equivalenceFilter(Filter, Filter)` und
`synchronizedFilter(Filter)`.

### Converter & Conversion

Ein `Converter` ist eine Methode, die ein gegebenes Eingabeobjekt in ein Ausgabeobjekt umwandelt. Bei dieser Umwandlung kann es sich beispielsweise um eine Navigation in einem Objektgraph oder auch das Parsen, Formatieren bzw. Umkodieren des Eingabeobjekts handel. Das Paar aus Ein- und Ausgabeobjekt eines `Converter` wird als `Conversion` bezeichnet.

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

Die Klasse `Converters` bietet die Hilfsmethoden 
`voidConverter()`, 
`fieldConverter(Field)`, 
`fieldConverter(String)`, 
`fieldConverter(String, Class)`, 
`fieldConverter(Field)`, 
`valueConverter(GValue)`, 
`methodConverter(String)`, 
`methodConverter(String, Class)`, 
`methodConverter(Method)`, 
`cachedConverter(Converter)`, 
`cachedConverter(int, int, int, Converter)`, 
`chainedConverter(Converter, Converter)`, 
`conditionalConverter(Filter, Converter, Converter)` und 
`synchronizedConverter(Converter)`
zur erzeugung allgemeiner `Converter` an.

Die Klasse `Conversions` bietet die Hilfsmethoden 
`staticConversion(GInput, GOutput)`, 
`staticConversionConverter(Converter)`,
`inverseConversion(Conversion)` und
`dynamicConversion(GInput, Converter)`.
zur erzeugung allgemeiner `Conversion` an. Darüber hinaus stellt sie die `Converter` 
`conversionInputConverter()` und
`conversionOutputConverter()`
zur Reduktion einer `Conversion` auf ihr Ein- bzw. Ausgabeobjekt bereit.

### Iterators & Iterables


### Comparators & Comparables


### Assigner & Assignables


### Field


### Unique


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

### Hash

...


---

##### [cc-by] Sebastian Rostock ( bee-creative@gmx.de )

Dieses Werk ist unter einem Creative Commons Namensnennung 3.0 Deutschland Lizenzvertrag lizenziert. Um die Lizenz anzusehen, gehen Sie bitte zu: [ http://creativecommons.org/licenses/by/3.0/de/ ] oder schicken Sie einen Brief an: [ Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA. ]
