package bee.creative.bind;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein {@link Observable überwachbares} {@link Field Datenfeld}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public class ObservableField<GItem, GValue> extends AbstractField<GItem, GValue> implements Observable<UpdateFieldMessage, UpdateFieldObserver> {

	/** Dieses Feld speichert das Datenfel, an das in {@link #get(Object)} und {@link #set(Object, Object)} delegiert wird. */
	public final Field<? super GItem, GValue> field;

	/** Dieser Konstruktor initialisiert das überwachte Datenfeld.
	 *
	 * @param field überwachtes Datenfeld. */
	public ObservableField(final Field<? super GItem, GValue> field) {
		this.field = Objects.notNull(field);
	}

	@Override
	public GValue get(final GItem input) {
		return this.field.get(input);
	}

	@Override
	public void set(final GItem input, final GValue newValue) {
		GValue oldValue = this.field.get(input);
		if (this.customEquals(oldValue, newValue)) return;
		oldValue = this.customClone(oldValue);
		this.field.set(input, newValue);
		this.fire(new UpdateFieldMessage(this, input, oldValue, newValue));
	}

	@Override
	public UpdateFieldObserver put(final UpdateFieldObserver listener) throws IllegalArgumentException {
		return UpdateFieldEvent.INSTANCE.put(this, listener);
	}

	@Override
	public UpdateFieldObserver putWeak(final UpdateFieldObserver listener) throws IllegalArgumentException {
		return UpdateFieldEvent.INSTANCE.putWeak(this, listener);
	}

	@Override
	public void pop(final UpdateFieldObserver listener) throws IllegalArgumentException {
		UpdateFieldEvent.INSTANCE.pop(this, listener);
	}

	@Override
	public UpdateFieldMessage fire(final UpdateFieldMessage event) throws NullPointerException {
		return UpdateFieldEvent.INSTANCE.fire(this, event);
	}

	@Override
	public String toString() {
		return this.field.toString();
	}

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