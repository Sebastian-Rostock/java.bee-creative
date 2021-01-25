package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Consumer3} als {@link BaseObject}.
 * 
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public abstract class AbstractConsumer<GValue> extends BaseObject implements Consumer3<GValue> {

	@Override
	public void set(final GValue value) {
	}

	@Override
	public Property2<GValue> toProperty() {
		return Properties.from(this);
	}

	@Override
	public Property2<GValue> toProperty(final Producer<? extends GValue> get) {
		return Properties.from(get, this);
	}

	@Override
	public Setter3<Object, GValue> toSetter() {
		return Setters.from(this);
	}

	@Override
	public Consumer3<GValue> toSynchronized() {
		return Consumers.toSynchronized(this);
	}

	@Override
	public Consumer3<GValue> toSynchronized(final Object mutex) {
		return Consumers.toSynchronized(this, mutex);
	}

	@Override
	public <GValue2> Consumer3<GValue2> toTranslated(final Getter<? super GValue2, ? extends GValue> trans) {
		return Consumers.toTranslated(this, trans);
	}

}