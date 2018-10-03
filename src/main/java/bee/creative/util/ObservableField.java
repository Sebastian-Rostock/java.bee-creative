package bee.creative.util;

/** Diese Klasse implementiert ein überwachbares {@link Field Datenfeld}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public class ObservableField<GInput, GValue>
	implements Field<GInput, GValue>, Observable<ObservableField.ChangeFieldMessage, ObservableField.ChangeFieldObserver> {

	/** Diese Klasse implementiert das Ereignis, dass bei Änderung des Werts eines {@link ObservableField} ausgelöst werden kann. */
	protected static class ChangeFieldEvent extends Event<ChangeFieldMessage, ChangeFieldObserver> {

		/** Dieses Feld speichert das {@link ChangeFieldEvent}. */
		public static final ChangeFieldEvent INSTANCE = new ChangeFieldEvent();

		/** {@inheritDoc} */
		@Override
		protected void customFire(final Object sender, final ChangeFieldMessage message, final ChangeFieldObserver observer) {
			observer.onChangeField(message);
		}

	}

	/** Diese Klasse implementiert die Nachricht des {@link ChangeFieldEvent} */
	public static class ChangeFieldMessage {

		/** Dieses Feld speichert den Sender des Änderungsereignisses.
		 *
		 * @see ObservableField#field */
		public final ObservableField<?, ?> sender;

		/** Dieses Feld speichert die Eingabe, dessen {@link ObservableField#field Datenfeld} geändert wurde. */
		public final Object input;

		/** Dieses Feld speichert den alten Wert des {@link ObservableField#field Datenfelds}. */
		public final Object oldValue;

		/** Dieses Feld speichert den neuen Wert des {@link ObservableField#field Datenfelds}. */
		public final Object newValue;

		/** Dieser Konstruktor initialisiert die Merkmale des Änderungsereignisses. */
		@SuppressWarnings ("javadoc")
		public ChangeFieldMessage(final ObservableField<?, ?> sender, final Object input, final Object oldValue, final Object newValue) {
			this.sender = sender;
			this.input = input;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

	}

	/** Diese Schnittstelle definiert den Empfänger des {@link ChangeFieldEvent}. */
	public static interface ChangeFieldObserver {

		/** Diese Methode wird bei Änderung des Werts eiens {@link ObservableField#field Datenfeldes} aufgerufen.
		 *
		 * @param message Nachricht des Änderungsereignisses. */
		public void onChangeField(ChangeFieldMessage message);

	}

	/** Dieses Feld speichert das Datenfel, an das in {@link #get(Object)} und {@link #set(Object, Object)} delegiert wird. */
	public final Field<? super GInput, GValue> field;

	/** Dieser Konstruktor initialisiert das überwachte Datenfeld.
	 *
	 * @param field überwachtes Datenfeld. */
	public ObservableField(final Field<? super GInput, GValue> field) {
		this.field = Objects.notNull(field);
	}

	/** Diese Methode gibt eine Kopie des gegebenen Werts oder diesen unverändert zurück. Vor dem {@link #set(Object, Object) Schreiben} des neuen Werts des
	 * {@link #field Datenfeldes} wird vom alten Wert eine Kopie erzeugt, welche nach dem Schreiben beim {@link #fire(ChangeFieldMessage) auslösen des
	 * Änderungsereignisses} eingesetzt wird. Eine Kopie ist nur dann nötig, wenn der alte Wert sich durch das Schreiben ändert.
	 *
	 * @param value alter Wert.
	 * @return kopierter Wert. */
	protected GValue customClone(final GValue value) {
		return value;
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte zurück. Sie wird beim {@link #set(Object, Object) Setzen} des Werts
	 * des {@link #field Datenfeldes} eingesetzt zur Erkennung einer Änderung des Werts zu erkennen. des
	 *
	 * @param value1 alter Wert.
	 * @param value2 neuer Wert.
	 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
	protected boolean customEquals(final GValue value1, final GValue value2) {
		return Objects.equals(value1, value2);
	}

	/** {@inheritDoc} */
	@Override
	public GValue get(final GInput input) {
		return this.field.get(input);
	}

	/** {@inheritDoc} */
	@Override
	public void set(final GInput input, final GValue newValue) {
		GValue oldValue = this.field.get(input);
		if (this.customEquals(oldValue, newValue)) return;
		oldValue = this.customClone(oldValue);
		this.field.set(input, newValue);
		this.fire(new ChangeFieldMessage(this, input, oldValue, newValue));
	}

	/** {@inheritDoc} */
	@Override
	public ObservableField.ChangeFieldObserver put(final ChangeFieldObserver listener) throws IllegalArgumentException {
		return ChangeFieldEvent.INSTANCE.put(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public ObservableField.ChangeFieldObserver putWeak(final ChangeFieldObserver listener) throws IllegalArgumentException {
		return ChangeFieldEvent.INSTANCE.putWeak(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public void pop(final ChangeFieldObserver listener) throws IllegalArgumentException {
		ChangeFieldEvent.INSTANCE.pop(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public ChangeFieldMessage fire(final ChangeFieldMessage event) throws NullPointerException {
		return ChangeFieldEvent.INSTANCE.fire(this, event);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.field.toString();
	}

}