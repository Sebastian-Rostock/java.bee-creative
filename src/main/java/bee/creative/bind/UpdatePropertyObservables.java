package bee.creative.bind;

class UpdatePropertyObservables extends Observables<UpdatePropertyEvent, UpdatePropertyListener> {

	public static final UpdatePropertyObservables INSTANCE = new UpdatePropertyObservables();

	@Override
	protected void customFire(final Object sender, final UpdatePropertyEvent message, final UpdatePropertyListener observer) {
		observer.onUpdateProperty(message);
	}

}