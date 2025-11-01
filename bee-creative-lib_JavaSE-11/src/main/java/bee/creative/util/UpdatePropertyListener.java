package bee.creative.util;

/** Diese Schnittstelle definiert den Empfänger des {@link UpdatePropertyEvent}. */
public interface UpdatePropertyListener {

	/** Diese Methode wird bei Aktualisierung des Werts eiens {@link ObservableProperty} aufgerufen.
	 *
	 * @param event Ereignisnachricht. */
	void onUpdateProperty(UpdatePropertyEvent event);

}