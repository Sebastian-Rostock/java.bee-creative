package bee.creative.bind;

public interface Consumer2<GValue> extends Consumer<GValue> {

	Property2<GValue> toProperty(Producer<? extends GValue> get);

	Setter2<Object, GValue> toSetter();

	Consumer2<GValue> toSynchronized();

	Consumer2<GValue> toSynchronized(final Object mutex);

}
