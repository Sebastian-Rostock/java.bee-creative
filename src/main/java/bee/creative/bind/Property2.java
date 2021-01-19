package bee.creative.bind;

public interface Property2<GValue> extends Property<GValue>, Producer2<GValue>, Consumer2<GValue> {

	Property2<GValue> toSetup(Producer<? extends GValue> setupValue);

	<GValue2> Property2<GValue2> toTranslated(Translator<GValue, GValue2> translator);

	<GValue2> Property2<GValue2> toTranslated(Getter<? super GValue, ? extends GValue2> g, Getter<? super GValue2, ? extends GValue> f);

	@Override
	Property2<GValue> toSynchronized();

	@Override
	Property2<GValue> toSynchronized(Object mutex);

	Field2<Object, GValue> toField();

	Producer3<GValue> toProducer();
	Consumer3<GValue> toConsumer();

}
