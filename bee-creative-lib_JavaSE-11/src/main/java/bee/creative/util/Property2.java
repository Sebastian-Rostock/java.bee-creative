package bee.creative.util;

import static bee.creative.util.Fields.fieldFrom;
import static bee.creative.util.Properties.observableProperty;
import static bee.creative.util.Properties.setupProperty;
import static bee.creative.util.Properties.synchronizedProperty;
import static bee.creative.util.Properties.translatedProperty;
import bee.creative.util.Properties.ObservableProperty;

/** Diese Schnittstelle ergänzt ein {@link Property} um eine Anbindung an die Methoden von {@link Properties}, {@link Producers}, {@link Consumers} und
 * {@link Fields}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <VALUE> Typ des Werts. */
public interface Property2<VALUE> extends Property<VALUE>, Producer2<VALUE>, Consumer2<VALUE> {

	/** Diese Methode ist eine Abkürzung für {@link Properties#setupProperty(Property, Producer) setupProperty(this, setup)}. */
	default Property2<VALUE> setup(Producer<? extends VALUE> setup) {
		return setupProperty(this, setup);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#observableProperty(Property) observableProperty(this)}. */
	default ObservableProperty<VALUE> observe() {
		return observableProperty(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#translatedProperty(Property, Getter, Getter) translatedProperty(this, transGet, transSet)}. */
	default <VALUE2> Property2<VALUE2> translate(Getter<? super VALUE, ? extends VALUE2> transGet, Getter<? super VALUE2, ? extends VALUE> transSet) {
		return translatedProperty(this, transGet, transSet);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#translatedProperty(Property, Translator) translatedProperty(this, trans)}. */
	default <VALUE2> Property2<VALUE2> translate(Translator<VALUE, VALUE2> trans) {
		return translatedProperty(this, trans);
	}

	@Override
	default Property2<VALUE> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#synchronizedProperty(Property, Object) synchronizedProperty(this, mutex)}. */
	@Override
	default Property2<VALUE> synchronize(Object mutex) {
		return synchronizedProperty(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fieldFrom(Property) fieldFrom(this)}. */
	default Field2<Object, VALUE> toField() {
		return fieldFrom(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#producerFrom(Producer) Producers.from(this)}. */
	default Producer3<VALUE> toProducer() {
		return Producers.producerFrom(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#consumerFrom(Consumer) Consumers.from(this)}. */
	default Consumer3<VALUE> toConsumer() {
		return Consumers.consumerFrom(this);
	}

}
