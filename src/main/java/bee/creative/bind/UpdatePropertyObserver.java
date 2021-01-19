package bee.creative.bind;

import bee.creative.bind.Properties.ObservableProperty;

/** Diese Schnittstelle definiert den Empfänger des {@link UpdatePropertyEvent}. */
public interface UpdatePropertyObserver {

	/** Diese Methode wird bei Aktualisierung des Werts eiens {@link ObservableProperty#target Datenfeldes} aufgerufen.
	 *
	 * @param message Nachricht des Ereignisses. */
	public void onUpdateProperty(UpdatePropertyMessage message);

}