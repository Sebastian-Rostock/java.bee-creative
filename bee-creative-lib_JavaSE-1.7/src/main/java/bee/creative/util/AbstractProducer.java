package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Producer3} als {@link BaseObject}. */
public abstract class AbstractProducer<GValue> extends BaseObject implements Producer3<GValue> {

	@Override
	public GValue get() {
		return null;
	}

	@Override
	public <GValue2> Property2<GValue2> concat(final Field<? super GValue, GValue2> target) {
		return Properties.from(this, target);
	}

	@Override
	public <GValue2> Consumer3<GValue2> concat(final Setter<? super GValue, GValue2> target) {
		return Consumers.from(this, target);
	}

	@Override
	public <GValue2> Producer3<GValue2> concat(final Getter<? super GValue, GValue2> target) {
		return Producers.translate(this, target);
	}

	@Override
	public Producer3<GValue> buffer() {
		return Producers.buffer(this);
	}

	@Override
	public Producer3<GValue> buffer(final int mode, final Hasher hasher) {
		return Producers.buffer(this, mode, hasher);
	}

	@Override
	public Producer3<GValue> synchronize() {
		return Producers.synchronize(this);
	}

	@Override
	public Producer3<GValue> synchronize(final Object mutex) {
		return Producers.synchronize(this, mutex);
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

}