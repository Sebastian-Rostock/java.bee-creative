package bee.creative.util;

import bee.creative.util.Properties.ObservableProperty;

/** Diese Schnittstelle ergänzt ein {@link Property} um eine Anbindung an die Methoden von {@link Properties}, {@link Producers}, {@link Consumers} und
 * {@link Fields}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Property2<GValue> extends Property<GValue>, Producer2<GValue>, Consumer2<GValue> {

	/** Diese Methode ist eine Abkürtung für {@link Properties#setup(Property, Producer) Properties.setup(this, setup)}. */
	public Property2<GValue> setup(Producer<? extends GValue> setup);

	/** Diese Methode ist eine Abkürtung für {@link Properties#observe(Property) Properties.observe(this)}. */
	public ObservableProperty<GValue> observe();

	/** Diese Methode ist eine Abkürtung für {@link Properties#translate(Property, Getter, Getter) Properties.translate(this, transGet, transSet)}. */
	public <GValue2> Property2<GValue2> translate(Getter<? super GValue, ? extends GValue2> transGet, Getter<? super GValue2, ? extends GValue> transSet);

	/** Diese Methode ist eine Abkürtung für {@link Properties#translate(Property, Translator) Properties.translate(this, trans)}. */
	public <GValue2> Property2<GValue2> translate(Translator<GValue, GValue2> trans);

	/** Diese Methode ist eine Abkürtung für {@link Properties#synchronize(Property) Properties.synchronize(this)}. */
	@Override
	public Property2<GValue> synchronize();

	/** Diese Methode ist eine Abkürtung für {@link Properties#synchronize(Property, Object) Properties.synchronize(this, mutex)}. */
	@Override
	public Property2<GValue> synchronize(Object mutex);

	/** Diese Methode ist eine Abkürtung für {@link Fields#from(Property) Fields.from(this)}. */
	public Field2<Object, GValue> toField();

	/** Diese Methode ist eine Abkürtung für {@link Producers#from(Producer) Producers.from(this)}. */
	public Producer3<GValue> toProducer();

	/** Diese Methode ist eine Abkürtung für {@link Consumers#from(Consumer) Consumers.from(this)}. */
	public Consumer3<GValue> toConsumer();

}
