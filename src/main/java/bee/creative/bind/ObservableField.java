package bee.creative.bind;

import bee.creative.util.Objects;

/** Diese Klasse implementiert ein {@link Observable überwachbares} {@link Field Datenfeld}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public class ObservableField<GItem, GValue> extends ObservableValue<GValue, ObservableField.UpdateFieldMessage, ObservableField.UpdateFieldObserver>
	implements Field<GItem, GValue> {

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
		public final Object item;

		/** Dieses Feld speichert den alten Wert des {@link ObservableField#field Datenfelds}. */
		public final Object oldValue;

		/** Dieses Feld speichert den neuen Wert des {@link ObservableField#field Datenfelds}. */
		public final Object newValue;

		/** Dieser Konstruktor initialisiert die Merkmale des Ereignisses. */
		public <GItem, GValue> UpdateFieldMessage(final ObservableField<? super GItem, GValue> sender, final GItem item, final GValue oldValue,
			final GValue newValue) {
			this.sender = sender;
			this.item = item;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		@SuppressWarnings ({"unchecked", "javadoc", "rawtypes"})
		private void setValue(final Object value) {
			((ObservableField)this.sender).set(this.item, value);
		}

		/** Diese Methode setzt den Wert des {@link #sender Datenfeldes} der {@link #item Einagbe} auf den {@link #oldValue alten Wert}. */
		public void setOldValue() {
			this.setValue(this.oldValue);
		}

		/** Diese Methode setzt den Wert des {@link #sender Datenfeldes} der {@link #item Einagbe} auf den {@link #newValue neuen Wert}. */
		public void setNewValue() {
			this.setValue(this.newValue);
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.sender, this.item, this.oldValue, this.newValue);
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
	public final Field<? super GItem, GValue> field;

	/** Dieser Konstruktor initialisiert das überwachte Datenfeld.
	 *
	 * @param field überwachtes Datenfeld. */
	public ObservableField(final Field<? super GItem, GValue> field) {
		this.field = Objects.notNull(field);
	}

	/** {@inheritDoc} */
	@Override
	public GValue get(final GItem input) {
		return this.field.get(input);
	}

	/** {@inheritDoc} */
	@Override
	public void set(final GItem input, final GValue newValue) {
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