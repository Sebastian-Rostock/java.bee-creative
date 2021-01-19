package bee.creative.bind;

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

	@Override
	public Producer2<GValue> toBuffered() {
		return null;
	}

	@Override
	public Producer2<GValue> toBuffered(final int mode) {
		return null;
	}

	@Override
	public Consumer3<GValue> toConsumer() {
		return Consumers.from(this);
	}

	@Override
	public Field2<Object, GValue> toField() {
		return null;
	}

	@Override
	public Getter3<Object, GValue> toGetter() {
		return null;
	}

	@Override
	public Producer3<GValue> toProducer() {
		return Producers.from(this);
	}

	@Override
	public Property2<GValue> toProperty(final Consumer<? super GValue> set) {
		return Properties.from(this, set);
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
	public Property2<GValue> toSetup(final Producer<? extends GValue> setup) {
		return Properties.toSetup(this, setup);
	}

	@Override
	public Property2<GValue> toSynchronized() {
		return Properties.toSynchronized(this);
	}

	@Override
	public Property2<GValue> toSynchronized(final Object mutex) {
		return Properties.toSynchronized(this, mutex);
	}

	@Override
	public <GValue2> Property2<GValue2> toTranslated(final Getter<? super GValue, ? extends GValue2> g, final Getter<? super GValue2, ? extends GValue> f) {
		return null;
	}

	@Override
	public <GValue2> Property2<GValue2> toTranslated(final Translator<GValue, GValue2> trans) {
		return Properties.toTranslated(this, trans);
	}

}