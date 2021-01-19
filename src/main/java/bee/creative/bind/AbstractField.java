package bee.creative.bind;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert ein abstraktes {@link Field} als {@link BaseObject}. */
public abstract class AbstractField<GItem, GValue> extends BaseObject implements Field2<GItem, GValue> {

	public AbstractField<GItem, GValue> toSetup(final Getter<? super GItem, ? extends GValue> setup) {
		return Fields.toSetup(this, setup);
	}

	@Override
	public AbstractField<GItem, GValue> toDefault() {
		return Fields.toDefault(this);
	}

	@Override
	public AbstractField<GItem, GValue> toDefault(final GValue value) {
		return Fields.toDefault(this, value);
	}

	@Override
	public Property2<GValue> toProperty() {
		return Properties.from(this);
	}

	@Override
	public Property2<GValue> toProperty(final GItem item) {
		return Properties.from(this, item);
	}

	public <GItem2> AbstractField<GItem2, GValue> toNavigated(final Getter<? super GItem2, ? extends GItem> toTarget) {
		return Fields.navigatedField(toTarget, this);
	}

	public <GValue2> AbstractField<GItem, GValue2> toTranslated(final Translator<GValue, GValue2> translator) {
		return Fields.translatedField(translator, this);
	}

	public <GValue2> AbstractField<GItem, GValue2> toTranslated(final Getter<? super GValue, ? extends GValue2> toTarget,
		final Getter<? super GValue2, ? extends GValue> toSource) {
		return Fields.translatedField(toTarget, toSource, this);
	}

	@Override
	public Field2<GItem, GValue> toSynchronized() {
		return Fields.toSynchronized(this);
	}

	@Override
	public Field2<GItem, GValue> toSynchronized(final Object mutex) {
		return Fields.toSynchronized(this, mutex);
	}

	@Override
	public GValue get(final GItem item) {
		return null;
	}

	@Override
	public void set(final GItem item, final GValue value) {
	}

	@Override
	public Field2<GItem, GValue> toField(final Setter<? super GItem, ? super GValue> setter) {
		return null;
	}

	@Override
	public Producer2<GValue> toProducer() {
		return null;
	}

	@Override
	public Producer2<GValue> toProducer(final GItem item) {
		return null;
	}

	@Override
	public Field2<GItem, GValue> toField(final Getter<? super GItem, ? extends GValue> getter) {
		return null;
	}

	@Override
	public Consumer3<GValue> toConsumer() {
		return null;
	}

	@Override
	public Consumer3<GValue> toConsumer(final GItem item) {
		return null;
	}

	@Override
	public Field2<Iterable<? extends GItem>, GValue> toAggregated() {
		return null;
	}

	@Override
	public Field2<Iterable<? extends GItem>, GValue> toAggregated(final GValue emptyTarget, final GValue mixedTarget) {
		return null;
	}

	@Override
	public <GValue2> Field2<Iterable<? extends GItem>, GValue2> toAggregated(final Getter<? super GValue, ? extends GValue2> toValue,
		final Getter<? super GValue2, ? extends GValue> toValue2) {
		return null;
	}

	@Override
	public <GValue2> Getter2<Iterable<? extends GItem>, GValue2> toAggregated(final Getter<? super GValue, ? extends GValue2> toTarget, final GValue2 emptyTarget,
		final GValue2 mixedTarget) {
		return null;
	}

}