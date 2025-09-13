package bee.creative.util;

import bee.creative.util.Fields.ObservableField;

/** Diese Schnittstelle definiert den Empfänger des {@link UpdateFieldEvent}. */
public interface UpdateFieldListener {

	/** Diese Methode wird bei Aktualisierung des Werts eiens {@link ObservableField} aufgerufen.
	 *
	 * @param event Ereignisnachricht. */
	void onUpdateField(UpdateFieldEvent event);

}