package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;
import bee.creative.util.Properties.ObservableProperty;

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
	public <GValue2> Property2<GValue2> concat(Field<? super GValue, GValue2> target) {
		return Properties.from(this, target);
	}

	@Override
	public <GValue2> Producer3<GValue2> concat(Getter<? super GValue, GValue2> target) {
		return Producers.translate(this, target);
	}

	@Override
	public <GValue2> Consumer3<GValue2> concat(Setter<? super GValue, GValue2> target) {
		return Consumers.from(this, target);
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
	public Consumer3<GValue> toConsumer() {
		return Consumers.from(this);
	}

	@Override
	public Field2<Object, GValue> toField() {
		return Fields.from(this);
	}

	@Override
	public Getter3<Object, GValue> toGetter() {
		return Getters.from(this);
	}

	@Override
	public ObservableProperty<GValue> toObservable() {
		return Properties.toObservable(this);
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
	public Property2<GValue> synchronize() {
		return Properties.synchronize(this);
	}

	@Override
	public Property2<GValue> synchronize(final Object mutex) {
		return Properties.synchronize(this, mutex);
	}

	@Override
	public <GValue2> Property2<GValue2> translate(final Getter<? super GValue, ? extends GValue2> transGet,
		final Getter<? super GValue2, ? extends GValue> transSet) {
		return Properties.translate(this, transGet, transSet);
	}

	@Override
	public <GValue2> Property2<GValue2> translate(final Translator<GValue, GValue2> trans) {
		return Properties.translate(this, trans);
	}

}