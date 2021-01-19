package bee.creative.bind;

import bee.creative.bind.Properties.ObservableProperty;

/** Diese Klasse implementiert das Ereignis, dass bei Aktualisierung des Werts eines {@link ObservableProperty} ausgelöst werden kann. */
class UpdatePropertyEvent extends Event<UpdatePropertyMessage, UpdatePropertyObserver> {

	/** Dieses Feld speichert das {@link UpdatePropertyEvent}. */
	public static final UpdatePropertyEvent INSTANCE = new UpdatePropertyEvent();

	@Override
	protected void customFire(final Object sender, final UpdatePropertyMessage message, final UpdatePropertyObserver observer) {
		observer.onUpdateProperty(message);
	}

}