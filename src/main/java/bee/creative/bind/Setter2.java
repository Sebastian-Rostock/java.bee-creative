package bee.creative.bind;

public interface Setter2<GItem, GValue> extends Setter<GItem, GValue> {

	Field2<GItem, GValue> toField(final Getter<? super GItem, ? extends GValue> getter);

	Setter2<GItem, GValue> toDefault();

	Setter2<Iterable<? extends GItem>, GValue> toAggregated();

	Setter2<GItem, GValue> toSynchronized();

	Setter2<GItem, GValue> toSynchronized(final Object mutex);

	Consumer3<GValue> toConsumer();

	Consumer3<GValue> toConsumer(final GItem item);

}
