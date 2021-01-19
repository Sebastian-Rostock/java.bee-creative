package bee.creative.bind;

public interface Consumer3<GValue> extends Consumer2<GValue> {

	Property2<GValue> toProperty();

	Setter2<Object, GValue> toSetter();

	@Override
	Consumer3<GValue> toSynchronized();

	@Override
	Consumer3<GValue> toSynchronized(Object mutex);

	<GValue2> Consumer3<GValue2> toTranslated(final Getter<? super GValue2, ? extends GValue> trans);

}
