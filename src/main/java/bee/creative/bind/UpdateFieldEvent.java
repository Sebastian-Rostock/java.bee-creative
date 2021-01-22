package bee.creative.bind;

import bee.creative.bind.Fields.ObservableField;

/** Diese Klasse implementiert das Ereignis, dass bei Aktualisierung des Werts eines {@link ObservableField} ausgelöst werden kann. */
class UpdateFieldEvent extends Event<UpdateFieldMessage, UpdateFieldObserver> {

	/** Dieses Feld speichert das {@link UpdateFieldEvent}. */
	public static final UpdateFieldEvent INSTANCE = new UpdateFieldEvent();

	@Override
	protected void customFire(final Object sender, final UpdateFieldMessage message, final UpdateFieldObserver observer) {
		observer.onUpdateField(message);
	}

}