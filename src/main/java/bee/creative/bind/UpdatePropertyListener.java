package bee.creative.bind;

import bee.creative.bind.Properties.ObservableProperty;

/** Diese Schnittstelle definiert den Empfänger des {@link UpdatePropertyEvent}. */
public interface UpdatePropertyListener {

	/** Diese Methode wird bei Aktualisierung des Werts eiens {@link ObservableProperty} aufgerufen.
	 *
	 * @param event Ereignisnachricht. */
	public void onUpdateProperty(UpdatePropertyEvent event);

}