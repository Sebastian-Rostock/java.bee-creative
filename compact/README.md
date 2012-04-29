# bee.creative.compact

Wer speicherminimale, modifizierbare Implementationen von `Map`, `NavigableMap`, `Set`, `NavigableSet` oder `List` sucht, dem kann mit `bee.creative.compact` geholfen werden.

__________________________________________________________________________________________


### CompactList

Der Speicherverbrauch einer `CompactList` ist genau so groß, wie der einer `ArrayList`.

Der Bereich es internen Arrays, in dem die Nutzdaten verwaltet werden, kann aber im Gegensatz zur `ArrayList` beliebig im internen Array positioniert und ausgerichtet werden. Jenachdem, ob dieser Nutzdatenbereich am Anfang, in der Mitte oder am Ende des internen Arrays ausgerichtet ist, wird dann das häufige Einfügen von Elementen am Ende, in der Mitte bzw. am Anfang beschleunigt. Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und liegen im Mittel bei **50 %** der Rechenzeit, die eine `ArrayList` dazu benötigen würde.

__________________________________________________________________________________________


### CompactHashSet

Der Speicherverbrauch eines `CompactHashSets` liegt bei **13 %** des Speicherverbrauchs eines `HashSets`.

Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im Mittel auf **245 %** der Rechenzeit, die ein `HashSet` dazu benötigen würde. Bei einer Anzahl von ca. *100* Elementen benötigen Beide Sets dafür in etwa die gleichen Rechenzeiten. Bei weniger Elementen ist das CompactHashSet schneller, bei mehr Elementen ist das HashSet schneller.  Bei der erhöhung der Anzahl der Elemente auf das *32*-fache (*5* Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einem CompactHashSet auf *8827 %* der Rechenzeit, die ein HashSet hierfür benötigen würde.

Für das Finden von Elementen und das Iterieren über die Elemente benötigt das `CompactHashSet` im Mittel nur noch **75 %** der Rechenzeit des `HashSets`, unabhängig von der Anzahl der Elemente.

__________________________________________________________________________________________


### CompactNavigableSet

Der Speicherverbrauch eines `CompactNavigableSet` liegt bei **13 %** des Speicherverbrauchs eines `TreeSets`.

Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer dieser Anzahl im Mittel auf **208 %** der Rechenzeit, die ein `TreeSet` dazu benötigen würde. Bei einer Anzahl von ca. *8000* Elementen benötigen Beide `NavigableSets` dafür in etwa die gleichen Rechenzeiten. Bei
weniger Elementen ist das `CompactNavigableSet` schneller, bei mehr Elementen ist das `TreeSet` schneller. Bei der erhöhung der Anzahl der Elemente auf das *32*-fache (*5* Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einem `CompactNavigableSet` auf *3900 %* der Rechenzeit, die ein `TreeSet` hierfür
benötigen würde.

Für das Finden von Elementen benötigt das `CompactNavigableSet` im Mittel nur noch **25 %** und für das Iterieren über die Elemente nur noch **75 %** der Rechenzeit des `TreeSets`, unabhängig von der Anzahl der Elemente.

__________________________________________________________________________________________


### CompactEntryHashMap

Der Speicherverbrauch einer `CompactEntryHashMap` liegt bei **28 %** des Speicherverbrauchs einer `HashMap`.

Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im Mittel auf **150 %** der Rechenzeit, die ein `HashMap` dazu benötigen würde. Bei der erhöhung der Anzahl der Elemente auf das *32*-fache (*5* Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einer `CompactEntryHashMap` auf *760 %* der Rechenzeit, die eine `HashMap` hierfür benötigen würde.

Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide `Maps` in etwa die gleichen Rechenzeiten, unabhängig von der Anzahl der Elemente.

__________________________________________________________________________________________


### CompactNavigableMap

Der Speicherverbrauch einer `CompactNavigableEntryMap` liegt bei **28 %** des Speicherverbrauchs einer `TreeMap`.

Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im Mittel auf **160 %** der Rechenzeit, die eine `TreeMap` dazu benötigen würde. Bei der erhöhung der Anzahl der Elemente auf das *32*-fache (*5* Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einer `CompactNavigableEntryMap` auf *1050 %* der Rechenzeit, die eine `TreeMap` hierfür benötigen würde.

Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide `Maps` in etwa die gleichen Rechenzeiten, unabhängig von der Anzahl der Elemente.

__________________________________________________________________________________________


##### [cc-by] Sebastian Rostock ( bee-creative@gmx.de )

Dieses Werk ist unter einem Creative Commons Namensnennung 3.0 Deutschland Lizenzvertrag lizenziert. Um die Lizenz anzusehen, gehen Sie bitte zu: [ http://creativecommons.org/licenses/by/3.0/de/ ] oder schicken Sie einen Brief an: [ Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA. ]
