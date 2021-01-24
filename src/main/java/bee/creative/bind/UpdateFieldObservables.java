package bee.creative.bind;

class UpdateFieldObservables extends Observables<UpdateFieldEvent, UpdateFieldListener> {

	public static final UpdateFieldObservables INSTANCE = new UpdateFieldObservables();

	@Override
	protected void customFire(final Object sender, final UpdateFieldEvent message, final UpdateFieldListener observer) {
		observer.onUpdateField(message);
	}

}