package bee.creative.util;

import bee.creative.util.Properties.ObservableProperty;

/** Diese Schnittstelle ergänzt ein {@link Property} um eine Anbindung an die Methoden von {@link Properties}, {@link Producers}, {@link Consumers} und
 * {@link Fields}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Property2<GValue> extends Property<GValue>, Producer2<GValue>, Consumer2<GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Properties#setup(Property, Producer) Properties.setup(this, setup)}. */
	default Property2<GValue> setup(final Producer<? extends GValue> setup) {
		return Properties.setup(this, setup);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#observe(Property) Properties.observe(this)}. */
	default ObservableProperty<GValue> observe() {
		return Properties.observe(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#translate(Property, Getter, Getter) Properties.translate(this, transGet, transSet)}. */
	default <GValue2> Property2<GValue2> translate(final Getter<? super GValue, ? extends GValue2> transGet,
		final Getter<? super GValue2, ? extends GValue> transSet) {
		return Properties.translate(this, transGet, transSet);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#translate(Property, Translator) Properties.translate(this, trans)}. */
	default <GValue2> Property2<GValue2> translate(final Translator<GValue, GValue2> trans) {
		return Properties.translate(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#synchronize(Property) Properties.synchronize(this)}. */
	@Override
	default Property2<GValue> synchronize() {
		return Properties.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#synchronize(Property, Object) Properties.synchronize(this, mutex)}. */
	@Override
	default Property2<GValue> synchronize(final Object mutex) {
		return Properties.synchronize(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#from(Property) Fields.from(this)}. */
	default Field2<Object, GValue> toField() {
		return Fields.from(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#from(Producer) Producers.from(this)}. */
	default Producer3<GValue> toProducer() {
		return Producers.from(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#from(Consumer) Consumers.from(this)}. */
	default Consumer3<GValue> toConsumer() {
		return Consumers.from(this);
	}

}
