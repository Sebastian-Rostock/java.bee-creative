# bee.creative.array

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

---

##### [cc-by] Sebastian Rostock ( bee-creative@gmx.de )

Dieses Werk ist unter einem Creative Commons Namensnennung 3.0 Deutschland Lizenzvertrag lizenziert. Um die Lizenz anzusehen, gehen Sie bitte zu: [ http://creativecommons.org/licenses/by/3.0/de/ ] oder schicken Sie einen Brief an: [ Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA. ]
