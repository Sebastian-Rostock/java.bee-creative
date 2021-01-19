package bee.creative.bind;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Producer3} als {@link BaseObject}. */
public abstract class AbstractProducer<GValue> extends BaseObject implements Producer3<GValue> {

	@Override
	public GValue get() {
		return null;
	}

	@Override
	public Producer3<GValue> toBuffered() {
		return Producers.toBuffered(this);
	}

	@Override
	public Producer3<GValue> toBuffered(final int mode) {
		return Producers.toBuffered(this, mode);
	}

	@Override
	public Getter3<Object, GValue> toGetter() {
		return Getters.from(this);
	}

	@Override
	public Property2<GValue> toProperty() {
		return Properties.from(this);
	}

	@Override
	public Property2<GValue> toProperty(final Consumer<? super GValue> set) {
		return Properties.from(this, set);
	}

	@Override
	public Producer3<GValue> toSynchronized() {
		return Producers.toSynchronized(this);
	}

	@Override
	public Producer3<GValue> toSynchronized(final Object mutex) {
		return Producers.toSynchronized(this, mutex);
	}

	@Override
	public <GValue2> Producer3<GValue2> toTranslated(final Getter<? super GValue, ? extends GValue2> trans) throws NullPointerException {
		return Producers.toTranslated(this, trans);
	}

}