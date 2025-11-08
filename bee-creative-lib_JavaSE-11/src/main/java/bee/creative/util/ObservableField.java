package bee.creative.util;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein {@link Observable überwachbares} {@link Field Datenfeld}.
 *
 * @param <ITEM> Typ der Eingabe.
 * @param <VALUE> Typ des Werts der Eigenschaft. */
public class ObservableField<ITEM, VALUE> extends AbstractField<ITEM, VALUE> implements Observable<UpdateFieldEvent, UpdateFieldListener> {

	/** Dieses Feld speichert das Datenfel, an das in {@link #get(Object)} und {@link #set(Object, Object)} delegiert wird. */
	public final Field<? super ITEM, VALUE> that;

	/** Dieser Konstruktor initialisiert das überwachte Datenfeld. */
	public ObservableField(final Field<? super ITEM, VALUE> that) throws NullPointerException {
		this.that = Objects.notNull(that);
	}

	@Override
	public VALUE get(final ITEM input) {
		return this.that.get(input);
	}

	@Override
	public void set(final ITEM item, final VALUE newValue) {
		var oldValue = this.that.get(item);
		if (this.customEquals(oldValue, newValue)) return;
		oldValue = this.customClone(oldValue);
		this.that.set(item, newValue);
		this.fire(new UpdateFieldEvent(this, item, oldValue, newValue));
	}

	@Override
	public UpdateFieldListener put(final UpdateFieldListener listener) throws IllegalArgumentException {
		return UpdateFieldObservables.INSTANCE.put(this, listener);
	}

	@Override
	public UpdateFieldListener putWeak(final UpdateFieldListener listener) throws IllegalArgumentException {
		return UpdateFieldObservables.INSTANCE.putWeak(this, listener);
	}

	@Override
	public void pop(final UpdateFieldListener listener) throws IllegalArgumentException {
		UpdateFieldObservables.INSTANCE.pop(this, listener);
	}

	@Override
	public UpdateFieldEvent fire(final UpdateFieldEvent event) throws NullPointerException {
		return UpdateFieldObservables.INSTANCE.fire(this, event);
	}

	@Override
	public String toString() {
		return this.that.toString();
	}

	/** Diese Methode gibt eine Kopie des gegebenen Werts oder diesen unverändert zurück. Vor dem Schreiben des neuen Werts wird vom alten Wert über diese
	 * Methode eine Kopie erzeugt, welche nach dem Schreiben beim auslösen des Ereignisses zur Aktualisierung eingesetzt wird. Eine Kopie ist hierbei nur dann
	 * nötig, wenn der alte Wert sich durch das Schreiben des neuen ändert.
	 *
	 * @param value alter Wert.
	 * @return gegebener oder kopierter Wert. */
	protected VALUE customClone(final VALUE value) {
		return value;
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Werte zurück. Sie wird beim Setzen des Werts zur Erkennung einer
	 * Wertänderung eingesetzt.
	 *
	 * @param value1 alter Wert.
	 * @param value2 neuer Wert.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
	protected boolean customEquals(final VALUE value1, final VALUE value2) {
		return Objects.deepEquals(value1, value2);
	}

}