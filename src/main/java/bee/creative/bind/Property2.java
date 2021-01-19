package bee.creative.bind;

/** Diese Schnittstelle ergänzt ein {@link Property} um eine Anbindung an die Methoden von {@link Properties}, {@link Producers}, {@link Consumers} und
 * {@link Fields}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Property2<GValue> extends Property<GValue>, Producer2<GValue>, Consumer2<GValue> {

	Consumer3<GValue> toConsumer();

	Field2<Object, GValue> toField();

	ObservableProperty<GValue> toObservable();

	Producer3<GValue> toProducer();

	Property2<GValue> toSetup(Producer<? extends GValue> setup);

	@Override
	Property2<GValue> toSynchronized();

	@Override
	Property2<GValue> toSynchronized(Object mutex);

	<GValue2> Property2<GValue2> toTranslated(Getter<? super GValue, ? extends GValue2> transGet, Getter<? super GValue2, ? extends GValue> transSet);

	<GValue2> Property2<GValue2> toTranslated(Translator<GValue2, GValue> trans);

}
