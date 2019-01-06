##### [cc-by] Sebastian Rostock ( bee-creative@gmx.de )

Dieses Werk ist unter einem Creative Commons Namensnennung 3.0 Deutschland Lizenzvertrag lizenziert. Um die Lizenz anzusehen, gehen Sie bitte zu: [ http://creativecommons.org/licenses/by/3.0/de/ ] oder schicken Sie einen Brief an: [ Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA. ]

# FEATURE

- Effiziente *HashSet* mit **33..44 %** Speicher und **45..85%** Rechenzeit (*contains*, *add*, *remove*) der `java.util.HashSet` (`HashSet`, `HashSet2`, `HashSet3`)
- Effiziente *HashMap* mit **44..56 %** Speicher und **55..95%** Rechenzeit (*get*, *put*, *remove*) der `java.util.HashMap` (`HashMap`, `HashMap2`, `HashMap3`) 
- Effiziente *ArrayList* mit **37..45%** Rechenzeit (*add*, *remove*) der `java.util.ArrayList` (`CompactObjectArray`) 


- Zeichenketten mit Codepoints in *UTF8*-, *UTF16*-, *INT8*-, *INT16*- und *INT32*-Kodierung (`FEMString`)
- Zeitspannen mit Monaten und Millisekunden, vgl. **xsd:duration** aus *XML Schema Part 2: Datatypes Second Edition* (`FEMDuration`) 
- Zeitangaben mit Datum, Uhrzeit und/oder Zeitzone im Gregorianischen Kalender, vgl. **xsd:dateTime** aus *XML Schema Part 2: Datatypes Second Edition* (`FEMDatetime`)


- Beliebig große **memory-mapped-files** (`MappedBuffer` )
- Modifizierbare Sichten auf ein primitives Arrays (`Array`, `ArraySection`, `CompactArray`)


- Bequeme Abschätzung von Speicherverbräuchen ( `EMU`, `Emuable`, `Emuator`) 
- Bequeme Handhabung von Datenströmen (`Reader`, `Writer`, `InputStream`, `OutputStream`, `DataInput`, `DataOutput`, `ByteBuffer`)
- Bequeme Verarbeitung von **CSV**, **INI** und **XML** Dateien (`CSVReader`, `CSVWriter`, `INIReader`, `INIWriter`, `XMLParser`, `XMLSchema`, `XMLEvaluator`, `XMLFormatter`, `XMLMarshaller`, `XMLUnmarshaller`)


- Zahlenlisten in persistenten Auflistungen und ordnungs- bzw. streuwertbasierte Abbildungen (`IAMIndex`, `IAMMapping`, `IAMListing`, `IAMEntry`, `IAMArray`)
- Binärkodiertes *XML* als *memory-mapped-file* (`BEXFile`, `BEXNode`, `BEXList`)
- Überwachung der *garbage collection* für Referenzen (`PointerQueue`)


- Operatoren auf Datenfeldern, Eigenschaften und deren Bestandteilen (Field, Getter, Setter ...)
- ... util


- ... fem


# PACKAGES

## bee.creative.io

Die Klasse `IO` bietet bequeme Methoden zur Erzeugung von Objekten zum Lesen (`Reader`, `InputStream`, `DataInput`, `ByteBuffer`) aus sowie Schreiben (`Writer`, `OutputStream`, `DataOutput`, `ByteBuffer`) in Datenströme (*copyBytes*, *copyChars*, *readBytes*, *readChars*, *writeBytes*, *writeChars*, *inputDataFrom*, *inputBufferFrom*, *inputStreamFrom*, *inputReaderFrom*, *outputDataFrom*, *outputBufferFrom*, *outputStreamFrom*, *outputWriterFrom*, *listFiles*).

Mit Hilfe der Klassen `CountingReader`, `CountingInputStream`, `CountingWriter` und `CountingOutputStream` kann der Fortschritt von datenstrombasierten Lese- bzw. Schreibprozesse überwacht werden.

Diese Klasse `MappedBuffer` implementiert eine alternative zu `MappedByteBuffer`, welche mit *long*-Adressen arbeitet und beliebig große Dateien als *memory-mapped-file* zum Lesen und Schreiben zugänglich machen kann.


## bee.creative.csv

Die Klassen `CSVReader` und `CSVWriter` implementieren Parser und Formatter für Daten im *CSV* Format. Maskierungs- und Trennzeichen können eingestellt werden. 

Die CSV-Datenstruktur ist eine Abfolge beliegig vieler Einträge, welche durch Zeilenumbrüche \r\n voneinander separiert sind. Beim Parsen werden auch einzelne Vorkommen der Zeichen \r und \n als Zeilenumbruch akzeptiert.
Ein Eintrag selbst ist eine Abfolge beliegig vieler ggf. maskierter Werte.


## bee.creative.ini

Die Klassen `INIReader` und `INIWriter` implementieren Parser und Formatter für Daten im *INI* Format. 

Die INI-Datenstruktur ist eine Abfolge beliegig vieler Abschnitte, Eigenschaften, Kommentare und Leerzeilen, welche durch Zeilenumbrüche \r\n voneinander separiert sind. Beim Parsen werden auch einzelne Vorkommen der Zeichen \r und \n als Zeilenumbruch akzeptiert.
Ein Abschnitt besteht aus dem Zeichen [, dem maskierten Namen des Abschnitts und dem Zeichen ].
Eine Eigenschaft besteht aus dem maskierten Schlüssel der Eigenschaft, dem Zeichen = und dem maskierten Wert der Eigenschaft.
Eine Kommentar besteht aus dem Zeichen ; und dem maskierten Text des Kommentars.
Die Maskierung der Zeichen \t, \r, \n, \\, =, ;, [ und ] erfolgt durch das Voranstellen des Zeichens \\.


## bee.creative.iam

Das *Integer Array Model* oder kurz *IAM* ist ein abstraktes Datenmodell, welches aus Auflistungen von Zahlenfolgen sowie Abbildungen von Zahlenfolgen auf Zahlenfolgen besteht. Sein binäres Datenformat lagert diese Bestandteile derart in eine Datei aus, dass diese als *memory-mapped-file* in den Arbeitsspeicher abgebildet werden kann und darauf sehr effiziente Lese- und Suchoperationen ausgeführt werden können.

Ausgangspunkt des Datenmodells ist ein Inhaltsverzeichnis (`IAMIndex`), über welches auf die Abbildungen (`IAMMapping`) und Auflistungen (`IAMListing`) zugegriffen werden kann. Die Elemente der Auflistungen sowie die Schlüssel und Werte der Einträge (`IAMEntry`) in den Abbildungen sind Zahlenfolgen (`IAMArray`).


## bee.creative.mmf

Die Klasse `MMFArray` implementiert ein `IAMArray`, dessen Zahlen durch einen `ByteBuffer` gegeben sind. Zur Interpretation dieses Speicherbereiches können entsprechende Sichten über `toINT8()`, `toUINT8()`, `toINT16()`, `toUINT16()` bzw. `toINT32()` erzeugte werden. Die Bytereihenfolge kann ebenfalls eingestellt werden.

## bee.creative.bex

Das *Binary Encoded XML* oder kurz *BEX* ist ein abstraktes Datenmodell, welches eine aus konstanten Knoten und Listen bestehende Vereinfachung des *Document Object Model* darstellt und im Rahmen des *IAM* als binären optimierten Datenformat in einer Datei abgelegt und per *memory-mapped-file* ressourcenschonend in den Arbeitsspeicher abgebildet werden kann.

Die Schnittstelle `BEXFile` bildet den Ausgangspunkt des Datenmodells und steht für ein Dokument (vgl. *XML* Datei). Element-, Text- und Attributknoten werden homogen über die Schnittstelle `BEXNode` repräsentiert. Die Kind- und Atributknotenlisten von Elementknoten werden über die Schnittstelle `BEXList` vereinheitlicht abgebildet.

Dazu gibt es auch einen Adapter zur Überführung von `BEXFile`, `BEXNode` und `BEXList` in die *DOM* Strukturen `Document`, `Text`, `Attr`, `Element`, `NodeList` und `NamedNodeMap`.


## bee-creative.xml

Diese Klasse `XML` implementiert statische Methoden zur zur Erzeugung von Konfiguratoren für das Parsen, Formatieren, Transformieren, Modifizieren, Evaluieren und Validieren von *XML* Dokumenten und Knoten.

Die dahiner liegende Bibliothek beinhaltet Hilfsmethoden zur zur Erzeugung und Formatierung von `org.w3c.dom.Document`, zur Verarbeitung von `javax.xml.xpath.XPath`, `org.w3c.dom.Node`, `javax.xml.transform.Templates` und `javax.xml.transform.Transformer`.


## bee.creative.emu

Die Klasse `EMU` implementiert statische Methoden zur Schätzung des Speicherverbrauchs gegebener Objekte oder Arrays (*Estimated Memory Usage*). Neben *reflection* nutzt sie dazu auch die Schnittstellen `Emuable` und `Emuator`, welche jeweils eine Methode zur Ermittlung des geschätzten Speicherverbrauchs definieren. Dieser Speicherverbrauch sollte dann auch den der ausschließlich intern verwalteten Objekte mit einschließen.


## bee.creative.ref

Die Schnittstelle `Pointer` bildet die Grundlage für erweiterte Varianten von `SoftReference` und `WeakReference`, welche ihren Streuwert und ihre Äquivalenz bezüglich des referenzierten Datensatzes ermitteln.

Daneben realisiert die Klasse `PointerQueue` einen `ReferenceQueue`, der automatisch bereinigt wird. Jede über `poll()`, `remove()` oder `remove(long)` entfernte Reference wird dabei an `customRemove(Reference)` übergeben, sofern sie nicht *null* ist. In dieser Methode kann dann auf das Entfernen der Reference reagiert werden.


## bee.creative.array

Die Schnittstelle `Array` definiert eien modifizierbare Sicht auf ein primitives Array mit `List`- und `ArraySection`-Sicht, wobei es für `byte`, `char`-, `short`-, `int`-, `long`-, `float`-, `double`-, `boolean`- und `Object`-Arrays jeweils spezielle `Array`-Schnittstellen und Implementation gibt (*size*, *clear*, *isEmpty*, *getAll*, *setAll*, *addAll*, *insert*, *remove*, *values*, *section*, *toArray*, *subArray*).

Die `CompactArray`-Implementationen haben dazu im Vergleich zur `ArrayList` einen deutlich geringen Speicherverbrauch und benötigen im Durchschnitt auch weniger Rechenzeit beim Einfügen und Entfernen von Elementen an beliebigen Positionen (*capacity*, *allocate*, *compact*, *array*, *startIndex*, *finalIndex*, *getAlignment*, *setAlignment*).

Die Schnittstelle `ArraySection` erweitert Abschnitte primitiver Arrays um die Methoden `hashCode()`, `equals()`, `toString()` und `compareTo()`, wobei es für `byte`, `char`-, `short`-, `int`-, `long`-, `float`-, `double`-, `boolean`- und `Object`-Arrays jeweils spezielle `ArraySection`-Implementation gibt (*size*, *array*, *arrayLength*, *startIndex*, *finalIndex*, *compareTo*, *hashCode*, *toString*).


## bee.creative.util TODO

Die *util* Bibliothek soll die gleichnamige von Java bereitgestellte um fehlende Komponenten bzw. effizientere Alternativen ergänzen.

In der Klasse `Bytes` werden Methoden zum Lesen und Schreiben von Dezimalzahlen aus bzw in Bytefolgen bereitgestellt, wobei Dezimalzahlen mit 0 bis 8 Byte Länge in beiden Bytereichenfolgen unterstützt werden (*getInt1*, *getInt2BE*, *getInt2LE*, *getInt3BE*, *getInt3LE*, *getInt4BE*, *getInt4LE*, *getIntBE*, *getIntLE*, *getLong5BE*, *getLong5LE*, *getLong6BE*, *getLong6LE*, *getLong7BE*, *getLong7LE*, *getLong8BE*, *getLong8LE*, *getLongBE*, *getLongLE*, *setInt1*, *setInt2BE*, *setInt2LE*, *setInt3BE*, *setInt3LE*, *setInt4BE*, *setInt4LE*, *setIntBE*, *setIntLE*, *setLong5BE*, *setLong5LE*, *setLong6BE*, *setLong6LE*, *setLong7BE*, *setLong7LE*, *setLong8BE*, *setLong8LE*, *setLongBE*, *setLongLE*, *lengthOf*).

Die Klasse `Integers` beitet effiziente Parser und Formatter für Dezimalzahlen (*parseInt*, *parseLong*, *formatInt*, *formatLong*, *stringSize*, *integerSize*, *toInt*, *toIntL*, *toIntH*, *toShort*, *toShortL*, *toShortH*, *toLong*).

Die Klasse `Strings` realisiert insbesondere die Methoden `join(...)`, `split(...)` und `match(...)` zur Überführung von Zeichenketten in Objektlisten und umgekehrt (*join*, *split*, *splitAll*, *match*, *matchAll*, *splatch*, *splatchAll*, *patternCompiler*, *parseSequence*, *formatSequence*).

In der Klasse `Objects` sind dazu Methoden zur Berechnung von Streuwert, Äquivalenz sowie Textdarstellung von Objekten und Arrays hinterlegt (*indent*, *format*, *formatMap*, *formatChar*, *formatArray*, *formatString*, *formatIterable*, *hash*, *hashInit*, *hashPush*, *equals*, *deepHash*, *deepEquals*, *identityEquals*, *toString*, *toStringObject*, *toInvokeString*, *toFormatString*, *notNull*, *translatedHasher*).

Die Klasse `Natives` implementiert Methoden zum Parsen von Klassen, Datenfeldern, Methoden und Konstruktoren aus deren Textdarstellung sowie zur Erzeugung dieser Textdarstellungen (*parse*, *parseField*, *parseField*, *parseClass*, *parseParams*, *parseMethod*, *parseConstructor*, *formatField*, *formatClass*, *formatParams*, *formatMethod*, *formatConstructor*).

Die Klasse `Collections` implementiert umordnende, verkettende, zusammenführende bzw. umwandelnde Sichten für `Set`, `Map`, `List` und `Collection` (*unionSet*, *cartesianSet*, *intersectionSet*, *reverseList*, *chainedList*, *chainedCollection*, *translatedMap*, *translatedList*, *translatedSet*, *translatedCollection*).



Producer
Producers

Consumer
Consumers

Property
Properties
ObservableProperty

Getter
Getters

Setter
Setters

Field
Fields
ObservableField

Filter
Filters

Translator
Translators

Iterables
Iterators

Comparables
Comparators

Conversion
Conversions

Hasher
AbstractHashData
HashMap
HashMap2
HashMap3
HashSet
HashSet2
HashSet3

Unique

Event
Observable


Builders

Parser

Tester


## bee.creative.fem TODO

In dieser Bibliothek findet man Hilfsklassen und Hilfsmethoden zur Realisirung funktionaler Operatoren mit call-by-value sowie call-by-reference Semantik.



  Type & Value

Ein `Type` kennzeichnet den Datentyp eines Werts, analog zur `Class` eines `Object`. Werte werden als Ergebnis oder Parameter von Funktion verwendet und durch die Schnittstelle `Value` vertreten. Wie und ob die Werte unterschiedlicher Datentypen ineinander umgewandelt werden können, gibt der `Context` vor, der als Kontextobjekt einer Funktion bereitgestellt werden kann.



  Scope & Function

Eine `Funktion` besitzt eine Berechnungsmethode, welche mit einem Ausführungskontext aufgerufen wird und einen Ergebniswert liefert. Aus dem Kontextobjekt des Ausführungskontexts können hierbei Informationen für die Berechnungen extrahiert oder auch der Zustand dieses Objekts modifiziert werden. Ein Ausführungskontext wird als `Scope` einer Funktion bezeichnet und stellt eine unveränderliche Liste von Parameterwerten sowie ein konstantes Kontextobjekt zur Verfügung. Über die Anzahl der Parameterwerte hinaus, können zusätzliche Parameterwerte eines übergeordneten Ausführungskontexts bereitgestellt werden (vgl. *Stack-Frame*).
