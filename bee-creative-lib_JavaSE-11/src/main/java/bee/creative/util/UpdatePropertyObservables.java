package bee.creative.util;

class UpdatePropertyObservables extends Observables<UpdatePropertyEvent, UpdatePropertyListener> {

	public static final UpdatePropertyObservables INSTANCE = new UpdatePropertyObservables();

	@Override
	protected void customFire(Object sender, UpdatePropertyEvent message, UpdatePropertyListener observer) {
		observer.onUpdateProperty(message);
	}

}