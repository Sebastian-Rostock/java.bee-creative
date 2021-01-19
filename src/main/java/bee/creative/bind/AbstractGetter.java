package bee.creative.bind;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Getter3} als {@link BaseObject}. */
public abstract class AbstractGetter<GItem, GValue> extends BaseObject implements Getter3<GItem, GValue> {

	@Override
	public GValue get(GItem item) {
		return null;
	}

	@Override
	public Getter3<Iterable<? extends GItem>, GValue> toAggregated() {
		return null;
	}

	@Override
	public <GValue2> Getter3<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> toTarget) {
		return null;
	}

	@Override
	public <GValue2> Getter3<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> toTarget, GValue2 emptyTarget,
		GValue2 mixedTarget, Getter<? super GItem, GValue> getter) {
		return null;
	}

	@Override
	public Getter3<Iterable<? extends GItem>, GValue> toAggregated(GValue emptyTarget, GValue mixedTarget) {
		return null;
	}

	@Override
	public Getter3<GItem, GValue> toDefault() {
		return null;
	}

	@Override
	public Getter3<GItem, GValue> toDefault(GValue value) {
		return null;
	}

	@Override
	public Field2<GItem, GValue> toField() {
		return null;
	}

	@Override
	public Field2<GItem, GValue> toField(Setter<? super GItem, ? super GValue> setter) {
		return null;
	}

	@Override
	public Producer2<GValue> toProducer() {
		return Producers.from(this);
	}

	@Override
	public Producer2<GValue> toProducer(GItem item) {
		return Producers.from(this, item);
	}

	@Override
	public Getter3<GItem, GValue> toSynchronized() {
		return null;
	}

	@Override
	public Getter3<GItem, GValue> toSynchronized(Object mutex) {
		return null;
	}

	@Override
	public <GItem2, GValue2> Getter3<GItem2, GValue2> toTranslated(Getter<? super GItem2, ? extends GItem> toItem,
		Getter<? super GValue, ? extends GValue2> toValue) throws NullPointerException {
		return null;
	}

}