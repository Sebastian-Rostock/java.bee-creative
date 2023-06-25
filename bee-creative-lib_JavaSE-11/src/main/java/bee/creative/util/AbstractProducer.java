package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Producer3} als {@link BaseObject}. */
public abstract class AbstractProducer<GValue> extends BaseObject implements Producer3<GValue> {

	@Override
	public GValue get() {
		return null;
	}

}