package bee.creative.bind;

import bee.creative.bind.Properties.ObservableProperty;

/** Diese Schnittstelle ergänzt ein {@link Property} um eine Anbindung an die Methoden von {@link Properties}, {@link Producers}, {@link Consumers} und
 * {@link Fields}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Property2<GValue> extends Property<GValue>, Producer2<GValue>, Consumer2<GValue> {

	/** Diese Methode ist eine Abkürtung für {@link Consumers#from(Consumer) Consumers.from(this)}. */
	Consumer3<GValue> toConsumer();

	Field2<Object, GValue> toField();

	/** Diese Methode ist eine Abkürtung für {@link Properties#toObservable(Property) Properties.toObservable(this)}. */
	ObservableProperty<GValue> toObservable();

	/** Diese Methode ist eine Abkürtung für {@link Producers#from(Producer) Producers.from(this)}. */
	Producer3<GValue> toProducer();

	/** Diese Methode ist eine Abkürtung für {@link Properties#toSetup(Property, Producer) Properties.toSetup(this, setup)}. */
	Property2<GValue> toSetup(Producer<? extends GValue> setup);

	/** Diese Methode ist eine Abkürtung für {@link Properties#toSynchronized(Property) Properties.toSynchronized(this)}. */
	@Override
	Property2<GValue> toSynchronized();

	/** Diese Methode ist eine Abkürtung für {@link Properties#toSynchronized(Property, Object) Properties.toSynchronized(this, mutex)}. */
	@Override
	Property2<GValue> toSynchronized(Object mutex);

	/** Diese Methode ist eine Abkürtung für {@link Properties#toTranslated(Property, Getter, Getter) Properties.toTranslated(this, transGet, transSet)}. */
	<GValue2> Property2<GValue2> toTranslated(Getter<? super GValue, ? extends GValue2> transGet, Getter<? super GValue2, ? extends GValue> transSet);

	/** Diese Methode ist eine Abkürtung für {@link Properties#toTranslated(Property, Translator) Properties.toTranslated(this, trans)}. */
	<GValue2> Property2<GValue2> toTranslated(Translator<GValue2, GValue> trans);

}
