package bee.creative.bind;

import bee.creative.lang.Objects;
import bee.creative.lang.Objects.BaseObject;

/** Diese abstrakte Klasse implementiert einen {@link Observable überwachbaren} Wert.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts.
 * @param <GMessage> Typ der Ereignisnachricht.
 * @param <GObserver> Typ der Ereignisempfänger. */
public abstract class ObservableValue<GValue, GMessage, GObserver> extends BaseObject implements Observable<GMessage, GObserver> {

	/** Diese Methode gibt eine Kopie des gegebenen Werts oder diesen unverändert zurück. Vor dem Schreiben des neuen Werts wird vom alten Wert über diese Methode
	 * eine Kopie erzeugt, welche nach dem Schreiben beim auslösen des Ereignisses zur Aktualisierung eingesetzt wird. Eine Kopie ist hierbei nur dann nötig, wenn
	 * der alte Wert sich durch das Schreiben des neuen ändert.
	 *
	 * @param value alter Wert.
	 * @return gegebener oder kopierter Wert. */
	protected GValue customClone(final GValue value) {
		return value;
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Werte zurück. Sie wird beim Setzen des Werts zur Erkennung einer
	 * Wertänderung eingesetzt.
	 *
	 * @param value1 alter Wert.
	 * @param value2 neuer Wert.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
	protected boolean customEquals(final GValue value1, final GValue value2) {
		return Objects.deepEquals(value1, value2);
	}

}