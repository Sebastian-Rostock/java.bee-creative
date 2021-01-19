package bee.creative.bind;

public interface Setter3<GItem, GValue> extends Setter2<GItem, GValue> {

	Field2<GItem, GValue> toField();

	@Override
	Setter3<GItem, GValue> toDefault();

	@Override
	Setter3<Iterable<? extends GItem>, GValue> toAggregated();
	
	<GValue2> Setter3<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue2, ? extends GValue> toValue);
	
	<GItem2, GValue2> Setter3<GItem2, GValue2> toTranslated(final Getter<? super GItem, ? extends GItem2> toItem,
		final Getter<? super GValue2, ? extends GValue> toValue);

	@Override
	Setter3<GItem, GValue> toSynchronized();

	@Override
	Setter3<GItem, GValue> toSynchronized(Object mutex);

}
