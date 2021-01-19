package bee.creative.bind;

/** Diese Schnittstelle definiert den Empfänger des {@link UpdateFieldEvent}. */
public interface UpdateFieldObserver {

	/** Diese Methode wird bei Aktualisierung des Werts eiens {@link ObservableField#field Datenfeldes} aufgerufen.
	 *
	 * @param message Nachricht des Ereignisses. */
	public void onUpdateField(UpdateFieldMessage message);

}