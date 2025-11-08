package bee.creative.util;

/** Diese Schnittstelle definiert den Empfänger des {@link UpdateFieldEvent}. */
public interface UpdateFieldListener {

	/** Diese Methode wird bei Aktualisierung des Werts eiens {@link ObservableField} aufgerufen.
	 *
	 * @param event Ereignisnachricht. */
	void onUpdateField(UpdateFieldEvent event);

}