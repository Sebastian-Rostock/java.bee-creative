package bee.creative.util;

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
		@SuppressWarnings ("javadoc")
		public UpdatePropertyMessage(final ObservableProperty<?> sender, final Object oldValue, final Object newValue) {
			this.sender = sender;
			this.oldValue = oldValue;
			this.newValue = newValue;
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