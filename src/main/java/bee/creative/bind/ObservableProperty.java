package bee.creative.bind;

import bee.creative.util.Objects;

/** Diese Klasse implementiert eine {@link Observable überwachbare} {@link Property Eigenschaft}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts der Eigenschaft. */
public class ObservableProperty<GValue> extends ObservableValue<GValue, ObservableProperty.UpdatePropertyMessage, ObservableProperty.UpdatePropertyObserver>
	implements Property<GValue> {

	/** Diese Klasse implementiert das Ereignis, dass bei Aktualisierung des Werts eines {@link ObservableProperty} ausgelöst werden kann. */
	protected static class UpdatePropertyEvent extends Event<UpdatePropertyMessage, UpdatePropertyObserver> {

		/** Dieses Feld speichert das {@link UpdatePropertyEvent}. */
		public static final UpdatePropertyEvent INSTANCE = new UpdatePropertyEvent();

		/** {@inheritDoc} */
		@Override
		protected void customFire(final Object sender, final UpdatePropertyMessage message, final UpdatePropertyObserver observer) {
			observer.onUpdateProperty(message);
		}

	}

	/** Diese Klasse implementiert die Nachricht des {@link UpdatePropertyEvent} */
	public static class UpdatePropertyMessage {

		/** Dieses Feld speichert den Sender des Ereignisses.
		 *
		 * @see ObservableProperty#property */
		public final ObservableProperty<?> sender;

		/** Dieses Feld speichert den alten Wert des {@link ObservableProperty#property Datenfelds}. */
		public final Object oldValue;

		/** Dieses Feld speichert den neuen Wert des {@link ObservableProperty#property Datenfelds}. */
		public final Object newValue;

		/** Dieser Konstruktor initialisiert die Merkmale des Ereignisses. */
		public <GValue> UpdatePropertyMessage(final ObservableProperty<GValue> sender, final GValue oldValue, final GValue newValue) {
			this.sender = sender;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		@SuppressWarnings ({"unchecked", "javadoc", "rawtypes"})
		private void setValue(final Object value) {
			((ObservableProperty)this.sender).set(value);
		}

		/** Diese Methode setzt den Wert der {@link #sender Eigenschaft} auf den {@link #oldValue alten Wert}. */
		public void setOldValue() {
			this.setValue(this.oldValue);
		}

		/** Diese Methode setzt den Wert der {@link #sender Eigenschaft} auf den {@link #newValue neuen Wert}. */
		public void setNewValue() {
			this.setValue(this.newValue);
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.sender, this.oldValue, this.newValue);
		}

	}

	/** Diese Schnittstelle definiert den Empfänger des {@link UpdatePropertyEvent}. */
	public static interface UpdatePropertyObserver {

		/** Diese Methode wird bei Aktualisierung des Werts eiens {@link ObservableProperty#property Datenfeldes} aufgerufen.
		 *
		 * @param message Nachricht des Ereignisses. */
		public void onUpdateProperty(UpdatePropertyMessage message);

	}

	/** Dieses Feld speichert die Eigenschaft, an die in {@link #get()} und {@link #set(Object)} delegiert wird. */
	public final Property<GValue> property;

	/** Dieser Konstruktor initialisiert die überwachte Eigenschaft.
	 *
	 * @param property überwachte Eigenschaft. */
	public ObservableProperty(final Property<GValue> property) {
		this.property = Objects.notNull(property);
	}

	/** {@inheritDoc} */
	@Override
	public GValue get() {
		return this.property.get();
	}

	/** {@inheritDoc} */
	@Override
	public void set(final GValue newValue) {
		GValue oldValue = this.property.get();
		if (this.customEquals(oldValue, newValue)) return;
		oldValue = this.customClone(oldValue);
		this.property.set(newValue);
		this.fire(new UpdatePropertyMessage(this, oldValue, newValue));
	}

	/** {@inheritDoc} */
	@Override
	public ObservableProperty.UpdatePropertyObserver put(final UpdatePropertyObserver listener) throws IllegalArgumentException {
		return UpdatePropertyEvent.INSTANCE.put(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public ObservableProperty.UpdatePropertyObserver putWeak(final UpdatePropertyObserver listener) throws IllegalArgumentException {
		return UpdatePropertyEvent.INSTANCE.putWeak(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public void pop(final UpdatePropertyObserver listener) throws IllegalArgumentException {
		UpdatePropertyEvent.INSTANCE.pop(this, listener);
	}

	/** {@inheritDoc} */
	@Override
	public UpdatePropertyMessage fire(final UpdatePropertyMessage event) throws NullPointerException {
		return UpdatePropertyEvent.INSTANCE.fire(this, event);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.property.toString();
	}

}