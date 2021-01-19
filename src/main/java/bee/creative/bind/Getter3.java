package bee.creative.bind;

public interface Getter3<GItem, GValue> extends Getter2<GItem, GValue> {

	Field2<GItem, GValue> toField();

	@Override
	Getter3<GItem, GValue> toDefault();

	@Override
	Getter3<GItem, GValue> toDefault(GValue value);

	<GItem2, GValue2> Getter3<GItem2, GValue2> toTranslated(final Getter<? super GItem2, ? extends GItem> toItem,
		final Getter<? super GValue, ? extends GValue2> toValue) throws NullPointerException;

	@Override
	Getter3<Iterable<? extends GItem>, GValue> toAggregated();

	<GValue2> Getter3<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> toTarget);

	<GValue2> Getter3<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> toTarget, GValue2 emptyTarget,
		GValue2 mixedTarget, Getter<? super GItem, GValue> getter);

	Getter3<Iterable<? extends GItem>, GValue> toAggregated(GValue emptyTarget, GValue mixedTarget);

	@Override
	Getter3<GItem, GValue> toSynchronized();

	@Override
	Getter3<GItem, GValue> toSynchronized(Object mutex);

	
}
