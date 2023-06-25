package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert ein abstraktes {@link Property2} als {@link BaseObject}. */
public abstract class AbstractProperty<GValue> extends BaseObject implements Property2<GValue> {

	@Override
	public GValue get() {
		return null;
	}

	@Override
	public void set(final GValue value) {
	}

}