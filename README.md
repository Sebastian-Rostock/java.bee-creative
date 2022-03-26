##### [cc-by] Sebastian Rostock ( bee-creative@gmx.de )

Dieses Werk ist unter einer Creative Commons Lizenz vom Typ Namensnennung 3.0 Deutschland zugänglich. Um eine Kopie dieser Lizenz einzusehen, konsultieren Sie 
http://creativecommons.org/licenses/by/3.0/de/ oder wenden Sie sich brieflich an Creative Commons, Postfach 1866, Mountain View, California, 94042, USA.

# FEATURE

- Effizientes *HashSet* mit **33..44 %** Speicher und **45..85%** Rechenzeit (*contains*, *add*, *remove*) der `java.util.HashSet` (`HashSet`, `HashSet2`, `HashSet3`)
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
- Überwachung der *garbage collection* für Referenzen (`ReferenceQueue2`, `SoftReference2`, `WeakReference2`)

- Operatoren auf Datenfeldern, Eigenschaften und deren Bestandteilen (`Field`, `Getter`, `Setter`, `Cunsumer`, `Producer`, `Property`, `Filter`)

- Operatoren auf `Iterator`, `Iterable`, `Comparator` und `Comparable`

# PACKAGES

## bee.creative.log

Die Klasse `LOGBuilder` dient der Erfassung hierarchischer Protokollzeilen.
Diese werden in der Textdarstellung des Protokolls durch den `LOGPrinter` entsprechend eingerückt dargestellt.


## bee.creative.emu

Die Klasse `EMU` implementiert statische Methoden zur Schätzung des Speicherverbrauchs gegebener Objekte oder Arrays (*Estimated Memory Usage*).
Neben *reflection* nutzt sie dazu auch die Schnittstellen `Emuable` und `Emuator`, welche jeweils eine Methode zur Ermittlung des geschätzten Speicherverbrauchs definieren.
Dieser Speicherverbrauch sollte dann auch den der ausschließlich intern verwalteten Objekte mit einschließen.


## bee.creative.ref

Die Schnittstelle `Reference2` bildet die Grundlage für erweiterte Varianten von `SoftReference` und `WeakReference`, welche ihren Streuwert und ihre Äquivalenz bezüglich des referenzierten Datensatzes ermitteln.

Daneben realisiert die Klasse `ReferenceQueue2` einen `ReferenceQueue`, der automatisch bereinigt wird.
Jede über `poll()`, `remove()` oder `remove(long)` entfernte Reference wird dabei an `customRemove(Reference)` übergeben, sofern sie nicht *null* ist.
In dieser Methode kann dann auf das Entfernen der Reference reagiert werden.
Die Klassen `SoftReference2` und `WeakReference2` nutzen ihren eigenen `ReferenceQueue2`, um den Aufruf an ihre eigene `customRemove()` weiterzuleiten.


## bee.creative.array

Die Schnittstelle `Array` definiert eien modifizierbare Sicht auf ein primitives Array mit `List`- und `ArraySection`-Sicht, wobei es für `byte`, `char`-, `short`-, `int`-, `long`-, `float`-, `double`-, `boolean`- und `Object`-Arrays jeweils spezielle `Array`-Schnittstellen und Implementation gibt (*size*, *clear*, *isEmpty*, *getAll*, *setAll*, *addAll*, *insert*, *remove*, *values*, *section*, *toArray*, *subArray*).

Die `CompactArray`-Implementationen haben dazu im Vergleich zur `ArrayList` einen deutlich geringen Speicherverbrauch und benötigen im Durchschnitt auch weniger Rechenzeit beim Einfügen und Entfernen von Elementen an beliebigen Positionen (*capacity*, *allocate*, *compact*, *array*, *startIndex*, *finalIndex*, *getAlignment*, *setAlignment*).

Die Schnittstelle `ArraySection` erweitert Abschnitte primitiver Arrays um die Methoden `hashCode()`, `equals()`, `toString()` und `compareTo()`, wobei es für `byte`, `char`-, `short`-, `int`-, `long`-, `float`-, `double`-, `boolean`- und `Object`-Arrays jeweils spezielle `ArraySection`-Implementation gibt (*size*, *array*, *arrayLength*, *startIndex*, *finalIndex*, *compareTo*, *hashCode*, *toString*).


## bee.creative.csv

Die Klassen `CSVReader` und `CSVWriter` implementieren Parser und Formatter für Daten im *CSV* Format. Maskierungs- und Trennzeichen können eingestellt werden. 

Die CSV-Datenstruktur ist eine Abfolge beliegig vieler Einträge, welche durch Zeilenumbrüche \r\n voneinander separiert sind.
Beim Parsen werden auch einzelne Vorkommen der Zeichen \r und \n als Zeilenumbruch akzeptiert.
Ein Eintrag selbst ist eine Abfolge beliegig vieler ggf. maskierter Werte.


## bee.creative.ini

Die Klassen `INIReader` und `INIWriter` implementieren Parser und Formatter für Daten im *INI* Format. 

Die INI-Datenstruktur ist eine Abfolge beliegig vieler Abschnitte, Eigenschaften, Kommentare und Leerzeilen, welche durch Zeilenumbrüche \r\n voneinander separiert sind.
Beim Parsen werden auch einzelne Vorkommen der Zeichen \r und \n als Zeilenumbruch akzeptiert.
Ein Abschnitt besteht aus dem Zeichen [, dem maskierten Namen des Abschnitts und dem Zeichen ].
Eine Eigenschaft besteht aus dem maskierten Schlüssel der Eigenschaft, dem Zeichen = und dem maskierten Wert der Eigenschaft.
Eine Kommentar besteht aus dem Zeichen ; und dem maskierten Text des Kommentars.
Die Maskierung der Zeichen \t, \r, \n, \\, =, ;, [ und ] erfolgt durch das Voranstellen des Zeichens \\.


## bee.creative.iam

Das *Integer Array Model* oder kurz *IAM* ist ein abstraktes Datenmodell, welches aus Auflistungen von Zahlenfolgen sowie Abbildungen von Zahlenfolgen auf Zahlenfolgen besteht.
Sein binäres Datenformat lagert diese Bestandteile derart in eine Datei aus, dass diese als *memory-mapped-file* in den Arbeitsspeicher abgebildet werden kann und darauf sehr effiziente Lese- und Suchoperationen ausgeführt werden können.

Ausgangspunkt des Datenmodells ist ein Inhaltsverzeichnis (`IAMIndex`), über welches auf die Abbildungen (`IAMMapping`) und Auflistungen (`IAMListing`) zugegriffen werden kann.
Die Elemente der Auflistungen sowie die Schlüssel und Werte der Einträge (`IAMEntry`) in den Abbildungen sind Zahlenfolgen (`IAMArray`).


## bee.creative.mmi

Die Klasse `MMIArray` implementiert ein `IAMArray`, dessen Zahlen durch einen Abschnitt eines `ByteBuffer` bzw. `MappedBuffer` gegeben sind.
Zur Interpretation dieses Speicherbereiches können entsprechende Sichten über `asINT8()`, `asUINT8()`, `asINT16()`, `asUINT16()` bzw. `asINT32()` erzeugte werden.
Die Bytereihenfolge kann ebenfalls eingestellt werden.


## bee.creative.bex

Das *Binary Encoded XML* oder kurz *BEX* ist ein abstraktes Datenmodell, welches eine aus konstanten Knoten und Listen bestehende Vereinfachung des *Document Object Model* darstellt und im Rahmen des *IAM* als binären optimierten Datenformat in einer Datei abgelegt und per *memory-mapped-file* ressourcenschonend in den Arbeitsspeicher abgebildet werden kann.

Die Schnittstelle `BEXFile` bildet den Ausgangspunkt des Datenmodells und steht für ein Dokument (vgl. *XML* Datei).
Element-, Text- und Attributknoten werden homogen über die Schnittstelle `BEXNode` repräsentiert.
Die Kind- und Atributknotenlisten von Elementknoten werden über die Schnittstelle `BEXList` vereinheitlicht abgebildet.

Dazu gibt es auch einen Adapter zur Überführung von `BEXFile`, `BEXNode` und `BEXList` in die *DOM* Strukturen `Document`, `Text`, `Attr`, `Element`, `NodeList` und `NamedNodeMap`.


## bee.creative.qs

Die Schnittstellen `QS`, `QE` und `QN` definieren einen Graphspeicher für einen Hypergraphen vierter Ordnung (Quad-Store), dessen Hyperknoten über einen optionalenidentifizierenden Textwert verfügen und dessen Hyperkanten jeweils vier Hyperknoten in den Rollen Kontext, Prädikat, Subjekt und Objekt referenzieren. Ein Hyperknoten kann dazu in jeder dieser Rollenvorkommen.

Die Klassen `H2QS`, `H2QE` und `H2QN` implementieren dazu einen Graphspeicher, dessen Hyperkanten und Textwerte in einer Datenbank (vorzugsweise embedded H2) gespeichert sind.


## bee.creative.io

Die Klasse `IO` bietet bequeme Methoden zur Erzeugung von Objekten zum Lesen (`Reader`, `InputStream`, `DataInput`, `ByteBuffer`) aus sowie Schreiben (`Writer`, `OutputStream`, `DataOutput`, `ByteBuffer`) in Datenströme (*copyBytes*, *copyChars*, *readBytes*, *readChars*, *writeBytes*, *writeChars*, *inputDataFrom*, *inputBufferFrom*, *inputStreamFrom*, *inputReaderFrom*, *outputDataFrom*, *outputBufferFrom*, *outputStreamFrom*, *outputWriterFrom*, *listFiles*).

Mit Hilfe der Klassen `CountingReader`, `CountingInputStream`, `CountingWriter` und `CountingOutputStream` kann der Fortschritt von datenstrombasierten Lese- bzw. Schreibprozesse überwacht werden.

Die Klasse `MappedBuffer` implementiert eine alternative zu `MappedByteBuffer`, welche mit *long*-Adressen arbeitet und beliebig große Dateien als *memory-mapped-file* zum Lesen und Schreiben zugänglich machen kann.

Die Klasse `MappedBuffer2` ergänzt einen `MappedBuffer` um Methoden zur Reservierung und Freigabe von Speicherbereichen.
Die darüber angebundene Datei besitz dafür eine entsprechende Datenstruktur, deren Kopfdaten beim Öffnen erzeugt bzw. geprüft werden.


## bee-creative.xml

Die Klasse `XML` implementiert statische Methoden zur zur Erzeugung von Konfiguratoren für das Parsen, Formatieren, Transformieren, Modifizieren, Evaluieren und Validieren von *XML* Dokumenten und Knoten.

Die dahiner liegende Bibliothek beinhaltet Hilfsmethoden zur zur Erzeugung und Formatierung von `org.w3c.dom.Document`, zur Verarbeitung von `javax.xml.xpath.XPath`, `org.w3c.dom.Node`, `javax.xml.transform.Templates` und `javax.xml.transform.Transformer`.


## bee.creative.util TODO

Die Klasse `Collections` implementiert umordnende, verkettende, zusammenführende bzw. umwandelnde Sichten für `Set`, `Map`, `List` und `Collection` (*union*, *except*, *intersect*, *cartesian*, *reverse*, *concat*, *translate*).

Die Klasse `Iterables` implementiert Funktionen zur Verarbeitung sowie Erzeugung von Iterierbaren (*empty*, *from*, *fromItem*, *fromCount*, *size*, *addAll*, *retainAll*, *removeAll*, *containsAll*, *concat*, *concatAll*, *union*, *unionAll*, *except*, *intersect*, *intersectAll*, *limit*, *filter*, *repeat*, *unique*, *translate*, *unmodifiable*, *iterator*, *toSet*, *toList*, *toArray*).

Die Klasse `Iterators` implementiert Funktionen zur Verarbeitung sowie Erzeugung von Iteratoren (*empty*, *from*, *fromItem*, *fromArray*, *fromCount*, *get*, *skip*, *addAll*, *retainAll*, *removeAll*, *containsAll*, *concat*, *concatAll*, *union*, *unionAll*, *except*, *intersect*, *intersectAll*, *limit*, *filter*, *unique*, *translate*, *unmodifiable*).

Die Klasse `Comparables` implementiert Funktionen zur stabilen binären Suche mit `Comparable` als Suchkriterium sowie zur Erzeugung solcher Suchkriterien (*binarySearch*, *binarySearchFirst*, *binarySearchLast*, *empty*, *from*, *concat*, *reverse*, *translate*, *optionalize*).

Die Klasse `Comparators` implementiert Funktionen zur Verarbeitung sowie Erzeugung von Komparatoren (*min*, *max*, *compare*, *neutral*, *natural*, *from*, *concat*, *reverse*, *iterable*, *translate*, *optionalize*).






Builders.java
ItemSetBuilder
TreeSetBuilder
HashSetBuilder
ProxySetBuilder
ItemListBuilder
ArrayListBuilder
LinkedListBuilder
ProxyListBuilder
ItemMapBuilder
TreeMapBuilder
HashMapBuilder
ProxyMapBuilder


Consumer.java
Consumers.java

Die Klasse `Entries` implementiert Funktionen zur Verarbeitung sowie Erzeugung von Schlüssel-Wert-Paaren (*empty*, *from*, *fromKey*, *fromValue*, *reverse*, *key*, *value*).


Field.java
Fields.java

Filter.java
Filters.java

Getter.java
Getters.java

Hasher.java
Diese Schnittstelle definiert Methoden zur Berechnung von Streuwert und Äquivalenz gegebenerEingaben.



HashMap
HashSet

Observable.java
Observables.java

Parser.java

Producer.java
Producers.java

Property.java
Properties.java

Setter.java
Setters.java

Test.java
Tester.java

Translator.java
Translators.java

  


## bee.creative.fem TODO

*Function Evaluation Model*

FEMDomain.java

FEMToken.java
FEMParser.java
FEMPrinter.java

FEMType.java

FEMVoid.java
FEMValue.java
FEMArray.java
FEMBinary.java
FEMBoolean.java
FEMDatetime.java
FEMDecimal.java
FEMInteger.java
FEMString.java
FEMDuration.java
FEMFuture.java
FEMHandler.java
FEMNative.java
FEMObject.java

FEMBuffer.java

FEMFrame.java
FEMContext.java
FEMTracer.java
FEMException.java

FEMParam.java
FEMProxy.java
FEMBinding.java
FEMClosure.java
FEMComposite.java
FEMFunction.java

FEMUtil.java
FEMVariable.java
FEMReflection.java


## bee.creative.lang

Die Klasse `Bytes` implementiert Methoden zur Interpretation von Bytefolgen als Dezimalzahlen unterschiedlicher Längen und Bytereihenfolgen (*getIntBE*, *getIntLE*, *getLongBE*, *getLongLE*, *setIntBE*, *setIntLE*, *setLongBE*, *setLongLE*, *lengthOf*, *nativeOrder*, *directOrder*, *reverseOrder*).

Die Klasse `Exception2` implementiert eine `RuntimeException`, an welche mehrere Nachrichten angefügt werden können und welche als Behandelt markiert werden kann.

Die Klasse `Integers` stellt Methoden zum Parsen und Formatieren von Dezimalzahlen zur Verfügung (*getSize*, *parseInt*, *parseLong*, *printInt*, *printLong*, *printSize*, *printTime*, *toInt*, *toIntL*, *toIntH*, *toByteL*, *toByteH*, *toShort*, *toShortL*, *toShortH*, *toLong*).

Die Klasse `Natives` implementiert Methoden zum Parsen von Klassen, Datenfeldern, Methoden und Konstruktoren aus deren Textdarstellung sowie zur Erzeugung dieser Textdarstellungen (*parseField*, *parseClass*, *parseMethod*, *parseConstructor*, *printField*, *printClass*, *printMethod*, *printConstructor*).

Die Klasse `Objects` implementiert Methoden zur Berechnung von Streuwerten, Äquivalenzen und Textdarstelungen (*printMap*, *printChar*, *printArray*, *printString*, *printIterable*, *printFuture*, *indent*, *hash*, *hashInit*, *hashPush*, *equals*, *deepHash*, *deepEquals*, *identityHash*, *identityEquals*, *toString*, *toStringCall*, *toStringFuture*, *toInvokeString*, *notNull*).

Die Klasse `Strings` stellt Methoden zur Verarbeitung von regulären Ausdrücken und Zeichenketten zur Verfügung (*join*, *split*, *splitAll*, *match*, *matchAll*, *splatch*, *splatchAll*, *substringAfterFirst*, *substringAfterLast*, *substringBeforeFirst*, *substringBeforeLast*, *parseSequence, *printSequence*).

Die Klasse `ThreadPool` implementiert einen `Thread`-Puffer, welcher Methoden zum Starten, Überwachen, Unterbrechen und Abwarten der Auswertung beliebiger Berechnungenbereitstellt.
Name und Priorität der zur Auswerung eingesetzten Threads können jederzeit angepasstwerden und werden beim Starten neuer Berechnungen angewendet.
Nach der Auswertung ihrer Berechnung warten diese Threads auf ihre Wiederverwendung.
Dazu kanneingestellt werden, wieviele auf ihre Wiederverwendung wartende Threads mindestens vorgehalten werden und wie lange die nichthierfür reservierten Threads maximal warten, bevor sie verworfen werden. 
