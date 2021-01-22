package bee.creative.bind;

import bee.creative.bind.Fields.ObservableField;

/** Diese Schnittstelle definiert den Empfänger des {@link UpdateFieldEvent}. */
public interface UpdateFieldObserver {

	/** Diese Methode wird bei Aktualisierung des Werts eiens {@link ObservableField#target Datenfeldes} aufgerufen.
	 *
	 * @param message Nachricht des Ereignisses. */
	public void onUpdateField(UpdateFieldMessage message);

}