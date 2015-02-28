# bee.creative.iam

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

---

##### [cc-by] Sebastian Rostock ( bee-creative@gmx.de )

Dieses Werk ist unter einem Creative Commons Namensnennung 3.0 Deutschland Lizenzvertrag lizenziert. Um die Lizenz anzusehen, gehen Sie bitte zu: [ http://creativecommons.org/licenses/by/3.0/de/ ] oder schicken Sie einen Brief an: [ Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA. ]
