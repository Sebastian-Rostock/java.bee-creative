package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Observers.observersFrom;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein {@link Observable überwachbares} {@link Field Datenfeld}.
 *
 * @param <T> Typ der Eingabe.
 * @param <V> Typ des Werts der Eigenschaft. */
public class ObservableField<T, V> implements Field3<T, V>, Observable2<UpdateFieldEvent, UpdateFieldListener> {

	/** Dieser Konstruktor initialisiert das überwachte Datenfeld. */
	public ObservableField(Field<? super T, V> that) throws NullPointerException {
		this.that = notNull(that);
	}

	@Override
	public V get(T input) {
		return this.that.get(input);
	}

	@Override
	public void set(T item, V newValue) {
		var oldValue = this.that.get(item);
		if (this.customEquals(oldValue, newValue)) return;
		oldValue = this.customClone(oldValue);
		this.that.set(item, newValue);
		this.fire(new UpdateFieldEvent(this, item, oldValue, newValue));
	}

	@Override
	public Observers<UpdateFieldEvent, UpdateFieldListener> observers() {
		return observers;
	}

	@Override
	public String toString() {
		return this.that.toString();
	}

	/** Diese Methode gibt eine Kopie des gegebenen Werts oder diesen unverändert zurück. Vor dem Schreiben des neuen Werts wird vom alten Wert über diese Methode
	 * eine Kopie erzeugt, welche nach dem Schreiben beim auslösen des Ereignisses zur Aktualisierung eingesetzt wird. Eine Kopie ist hierbei nur dann nötig, wenn
	 * der alte Wert sich durch das Schreiben des neuen ändert.
	 *
	 * @param value alter Wert.
	 * @return gegebener oder kopierter Wert. */
	protected V customClone(V value) {
		return value;
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Werte zurück. Sie wird beim Setzen des Werts zur Erkennung einer
	 * Wertänderung eingesetzt.
	 *
	 * @param value1 alter Wert.
	 * @param value2 neuer Wert.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
	protected boolean customEquals(V value1, V value2) {
		return Objects.deepEquals(value1, value2);
	}

	private static final Observers<UpdateFieldEvent, UpdateFieldListener> observers = observersFrom(UpdateFieldListener::onUpdateField);

	private final Field<? super T, V> that;

}