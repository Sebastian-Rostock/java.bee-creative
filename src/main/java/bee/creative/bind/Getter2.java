package bee.creative.bind;

public interface Getter2<GItem, GValue> extends Getter<GItem, GValue> {

	Getter2<GItem, GValue> toDefault();

	Getter2<GItem, GValue> toDefault(final GValue value);

	Getter2<Iterable<? extends GItem>, GValue> toAggregated();

	Getter2<GItem, GValue> toSynchronized();

	Getter2<GItem, GValue> toSynchronized(Object mutex);

	Field2<GItem, GValue> toField(Setter<? super GItem, ? super GValue> setter);

	Producer2<GValue> toProducer();

	Producer2<GValue> toProducer(final GItem item);

}
