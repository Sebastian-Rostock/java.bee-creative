package bee.creative.util;

/** Diese Klasse implementiert ein {@link Observable überwachbares} {@link Field Datenfeld}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public class ObservableField<GInput, GValue> extends ObservableValue<GValue, ObservableField.UpdateFieldMessage, ObservableField.UpdateFieldObserver>
	implements Field<GInput, GValue> {

	/** Diese Klasse implementiert das Ereignis, dass bei Aktualisierung des Werts eines {@link ObservableField} ausgelöst werden kann. */
	protected static class UpdateFieldEvent extends Event<UpdateFieldMessage, UpdateFieldObserver> {

		/** Dieses Feld speichert das {@link UpdateFieldEvent}. */
		public static final UpdateFieldEvent INSTANCE = new UpdateFieldEvent();

		/** {@inheritDoc} */
		@Override
		protected void customFire(final Object sender, final UpdateFieldMessage message, final UpdateFieldObserver observer) {
			observer.onUpdateField(message);
		}

	}

	/** Diese Klasse implementiert die Nachricht des {@link UpdateFieldEvent} */
	public static class UpdateFieldMessage {

		/** Dieses Feld speichert den Sender des Ereignisses.
		 *
		 * @see ObservableField#field */
		public final ObservableField<?, ?> sender;

		/** Dieses Feld speichert die Eingabe, dessen {@link ObservableField#field Datenfeld} geändert wurde. */
		public final Object input;

		/** Dieses Feld speichert den alten Wert des {@link ObservableField#field Datenfelds}. */
		public final Object oldValue;

		/** Dieses Feld speichert den neuen Wert des {@link ObservableField#field Datenfelds}. */
		public final Object newValue;

		/** Dieser Konstruktor initialisiert die Merkmale des Ereignisses. */
		public UpdateFieldMessage(final ObservableField<?, ?> sender, final Object input, final Object oldValue, final Object newValue) {
			this.sender = sender;
			this.input = input;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

	}

	/** Diese Schnittstelle definiert den Empfänger des {@link UpdateFieldEvent}. */
	public static interface UpdateFieldObserver {

		/** Diese Methode wird bei Aktualisierung des Werts eiens {@link ObservableField#field Datenfeldes} aufgerufen.
		 *
		 * @param message Nachricht des Ereignisses. */
		public void onUpdateField(UpdateFieldMessage message);

	}

	/** Dieses Feld speichert das Datenfel, an das in {@link #get(Object)} und {@link #set(Object, Object)} delegiert wird. */
	public final Field<? super GInput, GValue> field;

	/** Dieser Konstruktor initialisiert das überwachte Datenfeld.
	 *
	 * @param field überwachtes Datenfeld. */
	public ObservableField(final Field<? super GInput, GValue> field) {
		this.field = Objects.notNull(field);
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
		this.fire(new UpdateFieldMessage(this, input, oldValue, newValue));
	}

	/** {@inheritDoc} */
	@Override
	public ObservableField.UpdateFieldObserver put(final UpdateFieldObserver listener) throws IllegalArgumentException {
		return UpdateFieldEvent.INSTANCE.put(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public ObservableField.UpdateFieldObserver putWeak(final UpdateFieldObserver listener) throws IllegalArgumentException {
		return UpdateFieldEvent.INSTANCE.putWeak(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public void pop(final UpdateFieldObserver listener) throws IllegalArgumentException {
		UpdateFieldEvent.INSTANCE.pop(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public UpdateFieldMessage fire(final UpdateFieldMessage event) throws NullPointerException {
		return UpdateFieldEvent.INSTANCE.fire(this, event);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.field.toString();
	}

}