package bee.creative.bind;

import bee.creative.bind.Fields.ObservableField;

/** Diese Schnittstelle definiert den Empfänger des {@link UpdateFieldEvent}. */
public interface UpdateFieldListener {

	/** Diese Methode wird bei Aktualisierung des Werts eiens {@link ObservableField} aufgerufen.
	 *
	 * @param event Ereignisnachricht. */
	public void onUpdateField(UpdateFieldEvent event);

}