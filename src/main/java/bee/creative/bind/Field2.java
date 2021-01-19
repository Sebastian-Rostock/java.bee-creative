package bee.creative.bind;

public interface Field2<GItem, GValue> extends Field<GItem, GValue>, Getter2<GItem, GValue>, Setter2<GItem, GValue> {

	@Override
	Field2<Iterable<? extends GItem>, GValue> toAggregated();


	Field2<Iterable<? extends GItem>, GValue> toAggregated(GValue emptyTarget, GValue mixedTarget);

 
	<GValue2> Field2<Iterable<? extends GItem>, GValue2> toAggregated(
		Getter<? super GValue, ? extends GValue2> toValue,Getter<? super GValue2, ? extends GValue> toValue2);


	<GValue2> Getter2<Iterable<? extends GItem>, GValue2> toAggregated(Getter<? super GValue, ? extends GValue2> toTarget, GValue2 emptyTarget,
		GValue2 mixedTarget);

	
 

	@Override
	Field2<GItem, GValue> toDefault();

	@Override
	Field2<GItem, GValue> toDefault(GValue value);

	@Override
	Field2<GItem, GValue> toSynchronized();

	@Override
	Field2<GItem, GValue> toSynchronized(Object mutex);

}
