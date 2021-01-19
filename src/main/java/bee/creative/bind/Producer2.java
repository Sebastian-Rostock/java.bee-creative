package bee.creative.bind;

public interface Producer2<GValue> extends Producer<GValue> {

	Producer2<GValue> toBuffered();

	Producer2<GValue> toBuffered(int mode);

	Getter3<Object, GValue> toGetter();

	Property2<GValue> toProperty(Consumer<? super GValue> set);

	Producer2<GValue> toSynchronized();

	Producer2<GValue> toSynchronized(final Object mutex);

}
