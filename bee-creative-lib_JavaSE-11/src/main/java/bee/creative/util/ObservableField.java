package bee.creative.util;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein {@link Observable überwachbares} {@link Field Datenfeld}.
 *
 * @param <T> Typ der Eingabe.
 * @param <V> Typ des Werts der Eigenschaft. */
public class ObservableField<T, V> implements Field3<T, V>, Observable<UpdateFieldEvent, UpdateFieldListener> {

	/** Dieses Feld speichert das Datenfel, an das in {@link #get(Object)} und {@link #set(Object, Object)} delegiert wird. */
	public final Field<? super T, V> that;

	/** Dieser Konstruktor initialisiert das überwachte Datenfeld. */
	public ObservableField(Field<? super T, V> that) throws NullPointerException {
		this.that = Objects.notNull(that);
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
	public UpdateFieldListener put(UpdateFieldListener listener) throws IllegalArgumentException {
		return UpdateFieldObservables.INSTANCE.put(this, listener);
	}

	@Override
	public UpdateFieldListener putWeak(UpdateFieldListener listener) throws IllegalArgumentException {
		return UpdateFieldObservables.INSTANCE.putWeak(this, listener);
	}

	@Override
	public void pop(UpdateFieldListener listener) throws IllegalArgumentException {
		UpdateFieldObservables.INSTANCE.pop(this, listener);
	}

	@Override
	public UpdateFieldEvent fire(UpdateFieldEvent event) throws NullPointerException {
		return UpdateFieldObservables.INSTANCE.fire(this, event);
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

}