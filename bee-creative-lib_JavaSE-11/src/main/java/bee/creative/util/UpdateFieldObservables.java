package bee.creative.util;

class UpdateFieldObservables extends Observables<UpdateFieldEvent, UpdateFieldListener> {

	public static final UpdateFieldObservables INSTANCE = new UpdateFieldObservables();

	@Override
	protected void customFire(Object sender, UpdateFieldEvent message, UpdateFieldListener observer) {
		observer.onUpdateField(message);
	}

}