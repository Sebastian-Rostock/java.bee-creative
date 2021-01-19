package bee.creative.bind;

public interface Producer3<GValue> extends Producer2<GValue> {

	@Override
	Producer3<GValue> toBuffered();

	@Override
	Producer3<GValue> toBuffered(int mode);

	Property2<GValue> toProperty();

	@Override
	Producer3<GValue> toSynchronized();

	@Override
	Producer3<GValue> toSynchronized(Object mutex);

	<GValue2> Producer3<GValue2> toTranslated(final Getter<? super GValue, ? extends GValue2> trans);

}
