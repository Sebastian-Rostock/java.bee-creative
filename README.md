# bee.creative

TODO hier alles überarbeiten. vieles ist anders.


## bee.creative.io

Die Klasse `IO` bietet bequeme Methoden zur Erzeugung von Objekten zum Lesen (`Reader`, `InputStream`, `DataInput`, `ByteBuffer`) sowie Schreiben (`Writer`, `OutputStream`, `DataOutput`, `ByteBuffer`) von Datenströmen.

Mit Hilfe der Klassen `CountingReader`, `CountingInputStream`, `CountingWriter` und `CountingOutputStream` kann der Fortschritt von datenstrombasierten Lese- bzw. Schreibprozesse überwacht werden.

Diese Klasse `MappedBuffer` implementiert eine alternative zu `MappedByteBuffer`, welche mit *long*-Adressen arbeitet und beliebig große Dateien als *memory-mapped-file* zum Lesen und Schreiben zugänglich machen kann.


## bee.creative.csv

Die Klassen `CSVReader` und `CSVWriter` implementieren Parser und Formatter für Daten im *CSV* Format. Maskierungszeichen und Trennzeichen können eingestellt werden. 

Die CSV-Datenstruktur ist eine Abfolge beliegig vieler Einträge, welche durch Zeilenumbrüche '\r\n' voneinander separiert sind. Beim Parsen werden auch einzelne Vorkommen der Zeichen '\r' und '\n' als Zeilenumbruch akzeptiert.
Ein Eintrag selbst ist eine Abfolge beliegig vieler und ggf. maskierter Werte.


## bee.creative.ini

Die Klassen `INIReader` und `INIWriter` implementieren Parser und Formatter für Daten im *INI* Format. 

Die INI-Datenstruktur ist eine Abfolge beliegig vieler `INIToken` (Abschnitte, Eigenschaften, Kommentare) und Leerzeilen, welche durch Zeilenumbrüche '\r\n' voneinander separiert sind. Beim Parsen werden auch einzelne Vorkommen der Zeichen '\r' und '\n' als Zeilenumbruch akzeptiert.
Ein Abschnitt besteht aus dem Zeichen '[', dem maskierten Namen des Abschnitts und dem Zeichen ']'.
Eine Eigenschaft besteht aus dem maskierten Schlüssel der Eigenschaft, dem Zeichen '=' und dem maskierten Wert der Eigenschaft.
Eine Kommentar besteht aus dem Zeichen ';' und dem maskierten Text des Kommentars.
Die Maskierung der Zeichen '\t', '\r', '\n', '\\', '=', ';', '[' und ']' erfolgt durch das Voranstellen des Zeichens '\\'.


## bee.creative.mmf TODO


## bee.creative.iam

Das *Integer Array Model* oder kurz *IAM* ist ein abstraktes Datenmodell, welches aus Auflistungen von Zahlenfolgen sowie Abbildungen von Zahlenfolgen auf Zahlenfolgen besteht. Sein binäres Datenformat lagert diese Bestandteile derart in eine Datei aus, dass diese als *memory-mapped-file* in den Arbeitsspeicher abgebildet werden kann und darauf sehr effiziente Lese- und Suchoperationen ausgeführt werden können.

Ausgangspunkt des Datenmodells ist ein Inhaltsverzeichnis (`IAMIndex`), über welches auf die Abbildungen (`IAMMapping`) und Auflistungen (`IAMListing`) zugegriffen werden kann. Die Elemente der Auflistungen sowie die Schlüssel und Werte der Einträge (`IAMEntry`) in den Abbildungen sind Zahlenfolgen (`IAMArray`).


## bee.creative.bex

Das *Binary Encoded XML* oder kurz *BEX* ist ein abstraktes Datenmodell, welches eine aus konstanten Knoten und Listen bestehende Vereinfachung des 
*Document Object Model* darstellt und im Rahmen des *IAM* als binären optimierten Datenformat in einer Datei abgelegt und per *memory-mapped-file* 
ressourcenschonend in den Arbeitsspeicher abgebildet werden kann.

Die Schnittstelle `BEXFile` bildet den Ausgangspunkt des Datenmodells und steht für ein Dokument (vgl. *XML* Datei). Element-, Text- und Attributknoten 
werden homogen über die Schnittstelle `BEXNode` repräsentiert. Die Kind- und Attributknotenlisten von Elementknoten werden über die Schnittstelle `BEXList`
vereinheitlicht abgebildet.

Dazu gibt es auch einen Adapter zur Überführung von `BEXFile`, `BEXNode` und `BEXList` in die *DOM* Strukturen `Document`, `Text`, `Attr`, `Element`, `NodeList` und `NamedNodeMap`.


## bee.creative.xml TODO

## bee-creative.xml

Diese Bibliothek beinhaltet Hilfsmethoden zur zur Erzeugung und Formatierung von org.w3c.dom.Document, zur Verarbeitung von javax.xml.xpath.XPath, org.w3c.dom.Node, javax.xml.transform.Templates und javax.xml.transform.Transformer.

___

## bee.creative.emu TODO


## bee.creative.ref TODO

 
...

## bee.creative.array

Benötigt man `hashCode()`, `equals()`, `toString()` oder `compareTo()` für Abschnitte primitiver Arrays, vielleicht auch noch schnelle, speicherminimale und modifizierbare Arrays mit `List`-Schnittstelle, dann ist man bei `bee.creative.array` genau an der richtigen Adresse.

---
Array

Die Schnittstelle `Array` definiert eien modifizierbare Sicht auf ein primitives Array mit `List`- und `ArraySection`-Sicht, wobei es für `byte`, `char`-, `short`-, `int`-, `long`-, `float`-, `double`-, `boolean`- und `Object`-Arrays je spezielle `Array`-Schnittstellen und Implementation gibt. Die kompakten `Array`-Implementationen haben im Vergleich zur `ArrayList` einen deutlich geringen Speicherverbrauch und benötigen auch weniger Rechenzeit beim Einfügen und Entfernen von Elementen.

	List<Byte> byteList1 = new CompactByteArray().values();
	List<Byte> byteList2 = new ArrayList<Byte>();

Die `ArrayList` (`byteList2`) benötigt `2`-mal soviel Rechenzeit beim Einfügen und Entfernen von Elementen und ca. `16`-mal soviel Speicher für ihre Elemente im vergleich zur 
`List`-Sicht des `CompactByteArray`.

___

  ArraySection

Mit `ArraySection` werden Abschnitte primitiver Arrays um die Methoden `hashCode()`, `equals()`, `toString()` und `compareTo()` erweitert, wobei es für `byte`, `char`-, `short`-, `int`-, `long`-, `float`-, `double`-, `boolean`- und `Object`-Arrays je eine spezielle `ArraySection`-Implementation gibt. `ArraySection` kann man aus einem `int`-Array beispielsweise so erzeugen:

	int[] array = { 123, 456, 789, 101, 112, 131, 415, 161};
	ArraySection section1 = IntegerArraySection.from(array);
	ArraySection section2 = IntegerArraySection.from(array, 2, 6);

In `section1` sind allen Werte des `int`-Arrays enthalte, `section2` umfasst dagegen nur den Abschnitt von einschließlich `2` bis ausschließlich `6`, d.h. die Werte `{ 789, 101, 112, 131 }`.

---



## bee.creative.util TODO

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
	
### Tester

...

### Hash

...

___


## bee.creative.fem TODO

In dieser Bibliothek findet man Hilfsklassen und Hilfsmethoden zur Realisirung funktionaler Operatoren mit call-by-value sowie call-by-reference Semantik.

---

### Type & Value

Ein `Type` kennzeichnet den Datentyp eines Werts, analog zur `Class` eines `Object`. Werte werden als Ergebnis oder Parameter von Funktion verwendet und durch die Schnittstelle `Value` vertreten. Wie und ob die Werte unterschiedlicher Datentypen ineinander umgewandelt werden können, gibt der `Context` vor, der als Kontextobjekt einer Funktion bereitgestellt werden kann.

---

### Scope & Function

Eine `Funktion` besitzt eine Berechnungsmethode, welche mit einem Ausführungskontext aufgerufen wird und einen Ergebniswert liefert. Aus dem Kontextobjekt des Ausführungskontexts können hierbei Informationen für die Berechnungen extrahiert oder auch der Zustand dieses Objekts modifiziert werden. Ein Ausführungskontext wird als `Scope` einer Funktion bezeichnet und stellt eine unveränderliche Liste von Parameterwerten sowie ein konstantes Kontextobjekt zur Verfügung. Über die Anzahl der Parameterwerte hinaus, können zusätzliche Parameterwerte eines übergeordneten Ausführungskontexts bereitgestellt werden (vgl. *Stack-Frame*).

---

...



##### [cc-by] Sebastian Rostock ( bee-creative@gmx.de )

Dieses Werk ist unter einem Creative Commons Namensnennung 3.0 Deutschland Lizenzvertrag lizenziert. Um die Lizenz anzusehen, gehen Sie bitte zu: [ http://creativecommons.org/licenses/by/3.0/de/ ] oder schicken Sie einen Brief an: [ Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA. ]

