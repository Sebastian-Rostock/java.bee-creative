# bee.creative

TODO hier alles überarbeiten. vieles ist anders.

---

## bee.creative.io




## bee.creative.util

Wenn sich Probleme mit einem verkettenden, filternden, konvertierenden oder begrenzenden `Iterator` bzw. `Iterable` oder mit einem verkettenden oder konvertierenden `Comparator` bzw. `Comparable` lösen lassen, kann `bee-creative.util` die Lösung sein.

In dieser Bibliothek finden sich dazu `Filter` für verschiedene logische Operationen, verkettende, filternde sowie gepufferte `Getter`, statische, dynamische sowie inverse `Conversion`, gepufferte sowie konvertierende `Builder`, `Pointer` unterschiedlicher Stärke sowie `Tester` zur Ermittlung von Rechenzeit und Speicherbelegung einer Testmethode.


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

In Java 1.8 Syntax vereinfacht sich dies zu:
	
	Filter<String> prefixFilter = (input)-> input.startsWith(prefix);
	Filter<String> suffixFilter = (input)->input.endsWith(suffix);
	
`Filter` entsprechen damit logischen Eigenschaften bzw. Prädikaten und können als soche über logische Operatoren miteinander verknüpft werden. Einige Beispiele hierfür sind:

	Filter<String> prefixOrSuffixFilter = Filters.disjunctionFilter(prefixFilter, suffixFilter);
	Filter<String> prefixAndSuffixFilter = Filters.conjunctionFilter(prefixFilter, suffixFilter);
	Filter<String> prefixXorSuffixFilter = Filters.negationFilter(Filters.equivalenceFilter(prefixFilter, suffixFilter));

### Getter & Setter & Field

Ein `Getter` ist eine Methode, die ein gegebenes Eingabeobjekt in ein Ausgabeobjekt umwandelt. Bei dieser Umwandlung kann es sich beispielsweise um eine Navigation in einem Objektgraph oder auch das Parsen, Formatieren bzw. Umkodieren des Eingabeobjekts handel. Das Paar aus Ein- und Ausgabeobjekt eines `Getter` wird als `Conversion` bezeichnet.
 
### Iterators & Iterables


### Comparators & Comparables


### Assigner & Assignables


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

TODO
	
### Pointer

...

### Tester

...

### Hash

...

___


## bee.creative.array

Benötigt man `hashCode()`, `equals()`, `toString()` oder `compareTo()` für Abschnitte primitiver Arrays, vielleicht auch noch schnelle, speicherminimale und modifizierbare Arrays mit `List`-Schnittstelle, dann ist man bei `bee.creative.array` genau an der richtigen Adresse.

---

### ArraySection

Mit `ArraySection` werden Abschnitte primitiver Arrays um die Methoden `hashCode()`, `equals()`, `toString()` und `compareTo()` erweitert, wobei es für `byte`, `char`-, `short`-, `int`-, `long`-, `float`-, `double`-, `boolean`- und `Object`-Arrays je eine spezielle `ArraySection`-Implementation gibt. `ArraySection` kann man aus einem `int`-Array beispielsweise so erzeugen:

	int[] array = { 123, 456, 789, 101, 112, 131, 415, 161};
	ArraySection section1 = IntegerArraySection.from(array);
	ArraySection section2 = IntegerArraySection.from(array, 2, 6);

In `section1` sind allen Werte des `int`-Arrays enthalte, `section2` umfasst dagegen nur den Abschnitt von einschließlich `2` bis ausschließlich `6`, d.h. die Werte `{ 789, 101, 112, 131 }`.

---

### Array

Die Schnittstelle `Array` definiert eien modifizierbare Sicht auf ein primitives Array mit `List`- und `ArraySection`-Sicht, wobei es für `byte`, `char`-, `short`-, `int`-, `long`-, `float`-, `double`-, `boolean`- und `Object`-Arrays je spezielle `Array`-Schnittstellen und Implementation gibt. Die kompakten `Array`-Implementationen haben im Vergleich zur `ArrayList` einen deutlich geringen Speicherverbrauch und benötigen auch weniger Rechenzeit beim Einfügen und Entfernen von Elementen.

	List<Byte> byteList1 = new CompactByteArray().values();
	List<Byte> byteList2 = new ArrayList<Byte>();

Die `ArrayList` (`byteList2`) benötigt `2`-mal soviel Rechenzeit beim Einfügen und Entfernen von Elementen und ca. `16`-mal soviel Speicher für ihre Elemente im vergleich zur 
`List`-Sicht des `CompactByteArray`.

___


## bee.creative.compact

Wer speicherminimale und modifizierbare Implementationen von `Map`, `NavigableMap`, `Set`, `NavigableSet` oder `List` sucht, dem kann mit `bee.creative.compact` geholfen werden.

---

### CompactList

Der Speicherverbrauch einer `CompactList` ist genau so groß, wie der einer `ArrayList`.

Der Bereich es internen Arrays, in dem die Nutzdaten verwaltet werden, kann aber im Gegensatz zur `ArrayList` beliebig positioniert und ausgerichtet werden. Jenachdem, ob dieser Nutzdatenbereich am Anfang, in der Mitte oder am Ende des internen Arrays ausgerichtet ist, wird dann das häufige Einfügen von Elementen am Ende, in der Mitte bzw. am Anfang beschleunigt. Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und liegen im Mittel bei **50 %** der Rechenzeit, die eine `ArrayList` dazu benötigen würde.

---

### CompactHashSet

Der Speicherverbrauch eines `CompactHashSet` liegt bei **13 %** des Speicherverbrauchs eines `HashSet`.

Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im Mittel auf **245 %** der Rechenzeit, die ein `HashSet` dazu benötigen würde. Bei einer Anzahl von ca. *100* Elementen benötigen Beide `Set` dafür in etwa die gleichen Rechenzeiten. Bei weniger Elementen ist das `CompactHashSet` schneller, bei mehr Elementen ist das `HashSet` schneller.  Bei der erhöhung der Anzahl der Elemente auf das *32*-fache (*5* Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einem `CompactHashSet` auf *8827 %* der Rechenzeit, die ein `HashSet` hierfür benötigen würde.

Für das Finden von Elementen und das Iterieren über die Elemente benötigt das `CompactHashSet` im Mittel nur noch **75 %** der Rechenzeit des `HashSet`, unabhängig von der Anzahl der Elemente.

---

### CompactNavigableSet

Der Speicherverbrauch eines `CompactNavigableSet` liegt bei **13 %** des Speicherverbrauchs eines `TreeSet`.

Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer dieser Anzahl im Mittel auf **208 %** der Rechenzeit, die ein `TreeSet` dazu benötigen würde. Bei einer Anzahl von ca. *8000* Elementen benötigen Beide `NavigableSet` dafür in etwa die gleichen Rechenzeiten. Bei weniger Elementen ist das `CompactNavigableSet` schneller, bei mehr Elementen ist das `TreeSet` schneller. Bei der erhöhung der Anzahl der Elemente auf das *32*-fache (*5* Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einem `CompactNavigableSet` auf *3900 %* der Rechenzeit, die ein `TreeSet` hierfür benötigen würde.

Für das Finden von Elementen benötigt das `CompactNavigableSet` im Mittel nur noch **25 %** und für das Iterieren über die Elemente nur noch **75 %** der Rechenzeit des `TreeSet`, unabhängig von der Anzahl der Elemente.

---

### CompactEntryHashMap

Der Speicherverbrauch einer `CompactEntryHashMap` liegt bei **28 %** des Speicherverbrauchs einer `HashMap`.

Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im Mittel auf **150 %** der Rechenzeit, die ein `HashMap` dazu benötigen würde. Bei der erhöhung der Anzahl der Elemente auf das *32*-fache (*5* Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einer `CompactEntryHashMap` auf *760 %* der Rechenzeit, die eine `HashMap` hierfür benötigen würde.

Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide `Map` in etwa die gleichen Rechenzeiten, unabhängig von der Anzahl der Elemente.

---

### CompactNavigableMap

Der Speicherverbrauch einer `CompactNavigableEntryMap` liegt bei **28 %** des Speicherverbrauchs einer `TreeMap`.

Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im Mittel auf **160 %** der Rechenzeit, die eine `TreeMap` dazu benötigen würde. Bei der erhöhung der Anzahl der Elemente auf das *32*-fache (*5* Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einer `CompactNavigableEntryMap` auf *1050 %* der Rechenzeit, die eine `TreeMap` hierfür benötigen würde.

Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide `Maps` in etwa die gleichen Rechenzeiten, unabhängig von der Anzahl der Elemente.

___

 
## bee.creative.function

In dieser Bibliothek findet man Hilfsklassen und Hilfsmethoden zur Realisirung funktionaler Operatoren mit call-by-value sowie call-by-reference Semantik.

---

### Type & Value

Ein `Type` kennzeichnet den Datentyp eines Werts, analog zur `Class` eines `Object`. Werte werden als Ergebnis oder Parameter von Funktion verwendet und durch die Schnittstelle `Value` vertreten. Wie und ob die Werte unterschiedlicher Datentypen ineinander umgewandelt werden können, gibt der `Context` vor, der als Kontextobjekt einer Funktion bereitgestellt werden kann.

---

### Scope & Function

Eine `Funktion` besitzt eine Berechnungsmethode, welche mit einem Ausführungskontext aufgerufen wird und einen Ergebniswert liefert. Aus dem Kontextobjekt des Ausführungskontexts können hierbei Informationen für die Berechnungen extrahiert oder auch der Zustand dieses Objekts modifiziert werden. Ein Ausführungskontext wird als `Scope` einer Funktion bezeichnet und stellt eine unveränderliche Liste von Parameterwerten sowie ein konstantes Kontextobjekt zur Verfügung. Über die Anzahl der Parameterwerte hinaus, können zusätzliche Parameterwerte eines übergeordneten Ausführungskontexts bereitgestellt werden (vgl. *Stack-Frame*).

---

...

## bee.creative.xml

...

---
---


## bee.creative.iam

`IAM` – `Integer Array Model` beschreibt ein abstraktes Datenmodell aus Listen und Abbildungen sowie ein binäres und optimiertes Datenformat zur Auslagerung dieser Listen und Abbildungen in eine Datei. Ziel des Datenformats ist es, entsprechende Dateien per *file-mapping* in den Arbeitsspeicher abzubilden und darauf sehr effiziente Lese- und Such-operationen ausführen zu können. Die Modifikation der Daten ist nicht vorgesehen.

---

### IAMIndex

Ein `IAMIndex` ist eine Zusammenstellung beliebig vieler Listen (`IAMList`) und Abbildungen (`IAMMap`).
Die Methoden dieser Schnittstelle sind
`map(int)`, 
`mapCount()`, 
`list(int)` und
`listCount()`.

---

### IAMList

Eine `IAMList` ist eine geordnete Liste von Elementen, welche selbst Zahlenfolgen (`IAMArray`) sind.
Die Methoden dieser Schnittstelle sind
`item(int)`, 
`item(int, int)`, 
`itemLength(int)` und
`itemCount()`.
Für die Kodierung einer Liste gibt es 12 Varianten.

---

### IAMArray

Ein `IAMArray` ist eine Zahlenfolge, welche in einer Liste (`IAMList`) für die Elemente sowie einer Abbildung (`IAMMap`) für die Schlüssel und Werte der Einträge (`IAMEntry`) verwendet wird.
Die Methoden dieser Schnittstelle sind
`get(int)`, 
`length()`, 
`hash()`, 
`equals(IAMArray)`, 
`compare(IAMArray)` und
`section(int, int)`.

---

### IAMMap

Eine `IAMMap` ist eine Abbildung von Schlüsseln auf Werte, welche beide selbst Zahlenfolgen (`IAMArray`) sind.
Die Methoden dieser Schnittstelle sind
`key(int)`, 
`key(int, int)`, 
`keyLength(int)`, 
`value(int)`, 
`value(int, int)`, 
`valueLength(int)`, 
`entry(int)`, 
`entryCount()` und
`find(IAMArray)`.
Für die Kodierung einer Abbildung gibt es 576 Varianten.

---

### IAMEntry

Ein `IAMEntry` ist ein Eintrag einer Abbildung (`IAMMap`) und besteht aus einem Schlüssel und einem Wert, welche selbst Zahlenfolgen (`IAMArray`) sind.
Die Methoden dieser Schnittstelle sind
`key()`, 
`key(int)`, 
`keyLength()`, 
`value()`, 
`value(int)` und
`valueLength()`.

___


## bee-creative.xml

Diese Bibliothek beinhaltet Hilfsmethoden zur zur Erzeugung und Formatierung von org.w3c.dom.Document, zur Verarbeitung von javax.xml.xpath.XPath, org.w3c.dom.Node, javax.xml.transform.Templates und javax.xml.transform.Transformer.

___


##### [cc-by] Sebastian Rostock ( bee-creative@gmx.de )

Dieses Werk ist unter einem Creative Commons Namensnennung 3.0 Deutschland Lizenzvertrag lizenziert. Um die Lizenz anzusehen, gehen Sie bitte zu: [ http://creativecommons.org/licenses/by/3.0/de/ ] oder schicken Sie einen Brief an: [ Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA. ]
