# bee-creative.function

In dieser Bibliothek findet man Hilfsklassen und Hilfsmethoden zur Realisirung funktionaler Operatoren mit call-by-value sowie call-by-reference Semantik.

---

### Type & Value

Ein `Type` kennzeichnet den Datentyp eines Werts, analog zur `Class` eines `Object`. Werte werden als Ergebnis oder Parameter von Funktion verwendet und durch die Schnittstelle `Value` vertreten. Wie und ob die Werte unterschiedlicher Datentypen ineinander umgewandelt werden können, gibt der `Context` vor, der als Kontextobjekt einer Funktion bereitgestellt werden kann.

---

### Scope & Function

Eine `Funktion` besitzt eine Berechnungsmethode, welche mit einem Ausführungskontext aufgerufen wird und einen Ergebniswert liefert. Aus dem Kontextobjekt des Ausführungskontexts können hierbei Informationen für die Berechnungen extrahiert oder auch der Zustand dieses Objekts modifiziert werden. Ein Ausführungskontext wird als `Scope` einer Funktion bezeichnet und stellt eine unveränderliche Liste von Parameterwerten sowie ein konstantes Kontextobjekt zur Verfügung. Über die Anzahl der Parameterwerte hinaus, können zusätzliche Parameterwerte eines übergeordneten Ausführungskontexts bereitgestellt werden (vgl. *Stack-Frame*).

---

##### [cc-by] Sebastian Rostock ( bee-creative@gmx.de )

Dieses Werk ist unter einem Creative Commons Namensnennung 3.0 Deutschland Lizenzvertrag lizenziert. Um die Lizenz anzusehen, gehen Sie bitte zu: [ http://creativecommons.org/licenses/by/3.0/de/ ] oder schicken Sie einen Brief an: [ Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA. ]
