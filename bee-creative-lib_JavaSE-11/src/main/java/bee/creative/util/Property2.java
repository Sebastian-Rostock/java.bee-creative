package bee.creative.util;

import bee.creative.util.Properties.ObservableProperty;

/** Diese Schnittstelle ergänzt ein {@link Property} um eine Anbindung an die Methoden von {@link Properties}, {@link Producers}, {@link Consumers} und
 * {@link Fields}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Property2<GValue> extends Property<GValue>, Producer2<GValue>, Consumer2<GValue> {

	/** Diese Methode ist eine Abkürzung für {@link Properties#setupProperty(Property, Producer) Properties.setup(this, setup)}. */
	default Property2<GValue> setup(final Producer<? extends GValue> setup) {
		return Properties.setupProperty(this, setup);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#observableProperty(Property) Properties.observe(this)}. */
	default ObservableProperty<GValue> observe() {
		return Properties.observableProperty(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#translatedProperty(Property, Getter, Getter) Properties.translate(this, transGet, transSet)}. */
	default <GValue2> Property2<GValue2> translate(final Getter<? super GValue, ? extends GValue2> transGet,
		final Getter<? super GValue2, ? extends GValue> transSet) {
		return Properties.translatedProperty(this, transGet, transSet);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#translatedProperty(Property, Translator) Properties.translate(this, trans)}. */
	default <GValue2> Property2<GValue2> translate(Translator<GValue, GValue2> trans) {
		return Properties.translatedProperty(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#synchronizedProperty(Property) Properties.synchronize(this)}. */
	@Override
	default Property2<GValue> synchronize() {
		return Properties.synchronizedProperty(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#synchronizedProperty(Property, Object) Properties.synchronize(this, mutex)}. */
	@Override
	default Property2<GValue> synchronize(final Object mutex) {
		return Properties.synchronizedProperty(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#from(Property) Fields.from(this)}. */
	default Field2<Object, GValue> toField() {
		return Fields.fieldFrom(Getters.getterFromProducer(this),Setters.setterFrom(null));
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#producerFrom(Producer) Producers.from(this)}. */
	default Producer3<GValue> toProducer() {
		return Producers.producerFrom(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#consumerFrom(Consumer) Consumers.from(this)}. */
	default Consumer3<GValue> toConsumer() {
		return Consumers.consumerFrom(this);
	}

}
