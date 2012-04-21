# bee.creative.compact



__________________________________________________________________________________________

CompactList

  Der Speicherverbrauch einer CompactList liegt bei ca. 100% des Speicherverbrauchs einer
  ArrayList. Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der
  Anzahl der Elemente abhängig und liegen im Mittel bei 50% der Rechenzeit, die eine
  ArrayList dazu benötigen würde.

__________________________________________________________________________________________

CompactHashSet

  Der Speicherverbrauch eines CompactHashSets liegt bei ca. 13% des Speicherverbrauchs
  eines HashSets.

  Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von
  der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl
  im Mittel auf ca. 245% der Rechenzeit, die HashSet dazu benötigen würde. Bei einer
  Anzahl von ca. 100 Elementen benötigen Beide Sets dafür in etwa die gleichen
  Rechenzeiten. Bei weniger Elementen ist das CompactHashSet schneller, bei mehr Elementen
  ist das HashSet schneller.  Bei der erhöhung der Anzahl der Elemente auf das 32-fache (5
  Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in
  einem CompactHashSet auf 8827% der Rechenzeit, die ein HashSet hierfür benötigen würde.

  Für das Finden von Elementen und das Iterieren über die Elemente benötigt das
  CompactHashSet im Mittel nur noch 75% der Rechenzeit des HashSets, unabhängig von der
  Anzahl der Elemente.

__________________________________________________________________________________________

CompactNavigableSet

  Der Speicherverbrauch eines CompactNavigableSet liegt bei ca. 13% des Speicherverbrauchs
  eines TreeSets.

  Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der
  Elemente abhängig und erhöhen sich bei einer dieser Anzahl im Mittel auf ca. 208% der
  Rechenzeit, die ein TreeSet dazu benötigen würde. Bei einer Anzahl von ca. 8000
  Elementen benötigen Beide NavigableSet dafür in etwa die gleichen Rechenzeiten. Bei
  weniger Elementen ist das CompactNavigableSet schneller, bei mehr Elementen ist das
  TreeSet schneller. Bei der erhöhung der Anzahl der Elemente auf das 32-fache (5
  Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in
  einem CompactNavigableSet auf ca. 3900% der Rechenzeit, die ein TreeSet hierfür
  benötigen würde.

  Für das Finden von Elementen und das Iterieren über die Elemente benötigt das
  CompactNavigableSet im Mittel nur noch 25% bzw. 75% der Rechenzeit des TreeSets,
  unabhängig von der Anzahl der Elemente.

__________________________________________________________________________________________

CompactEntryHashMap

  Der Speicherverbrauch einer CompactEntryHashMap liegt bei ca. 28% des Speicherverbrauchs
  einer HashMap.

  Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der
  Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im Mittel auf
  ca. 150%. Bei der erhöhung der Anzahl der Elemente auf das 32-fache (5 Verdopplungen)
  steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einer
  CompactEntryHashMap auf 760% der Rechenzeit, die eine HashMap hierfür benötigen würde.

  Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide Maps in
  etwa die gleichen Rechenzeiten, unabhängig von der Anzahl der Elemente.

__________________________________________________________________________________________

CompactNavigableMap

  Der Speicherverbrauch einer CompactNavigableEntryMap liegt bei ca. 28% des
  Speicherverbrauchs einer TreeMap.

  Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der
  Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im Mittel auf
  ca. 160% der Rechenzeit, die eine TreeMap dazu benötigen würde. Bei der erhöhung der
  Anzahl der Elemente auf das 32-fache (5 Verdopplungen) steigt die Rechenzeit beim
  Hinzufügen und Entfernen von Elementen in einer CompactNavigableEntryMap auf ca. 1050%
  der Rechenzeit, die eine TreeMap hierfür benötigen würde.

  Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide Maps in
  etwa die gleichen Rechenzeiten, unabhängig von der Anzahl der Elemente.

__________________________________________________________________________________________


##### [cc-by] Sebastian Rostock ( bee-creative@gmx.de )

Dieses Werk ist unter einem Creative Commons Namensnennung 3.0 Deutschland Lizenzvertrag lizenziert. Um die Lizenz anzusehen, gehen Sie bitte zu: [ http://creativecommons.org/licenses/by/3.0/de/ ] oder schicken Sie einen Brief an: [ Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA. ]
