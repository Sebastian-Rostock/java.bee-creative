package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert ein abstraktes {@link Property3} als {@link BaseObject}. */
public abstract class AbstractProperty<GValue> extends BaseObject implements Property3<GValue> {

	@Override
	public GValue get() {
		return null;
	}

	@Override
	public void set(GValue value) {
	}

}