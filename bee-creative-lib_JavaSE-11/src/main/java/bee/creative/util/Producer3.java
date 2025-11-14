package bee.creative.util;

import static bee.creative.util.Consumers.consumerFromSetter;
import static bee.creative.util.Consumers.emptyConsumer;
import static bee.creative.util.Getters.getterFromProducer;
import static bee.creative.util.Getters.RefMode.SOFT_REF;
import static bee.creative.util.Hashers.naturalHasher;
import static bee.creative.util.Producers.producerFromGetter;
import static bee.creative.util.Producers.synchronizedProducer;
import static bee.creative.util.Producers.translatedProducer;
import static bee.creative.util.Properties.propertyFrom;
import static bee.creative.util.Properties.propertyFromField;
import bee.creative.util.Getters.RefMode;

/** Diese Schnittstelle ergänzt einen {@link Producer3} insb. um eine erweiterte Anbindung an Methoden von {@link Producers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <V> Typ des Werts. */
public interface Producer3<V> extends Producer<V> {

	/** Diese Methode ist eine Abkürzung für {@link Properties#propertyFromField(Field, Producer) concatProperty(this, that)}. */
	default <V2> Property3<V2> concat(Field<? super V, V2> that) {
		return propertyFromField(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#producerFromGetter(Getter, Producer) concatProducer(this, that)}. */
	default <V2> Producer3<V2> concat(Getter<? super V, ? extends V2> that) {
		return producerFromGetter(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#consumerFromSetter(Setter, Producer) consumerFromSetter(that, this)}. */
	default <V2> Consumer3<V2> concat(Setter<? super V, ? super V2> that) {
		return consumerFromSetter(that, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link #buffer(RefMode, Hasher) this.buffer(SOFT_REF, naturalHasher())}. */
	default Producer3<V> buffer() {
		return this.buffer(SOFT_REF, naturalHasher());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#bufferedGetter(Getter, RefMode, Hasher) this.asGetter().buffer(mode, hasher).asProducer()}. */
	default Producer3<V> buffer(RefMode mode, Hasher hasher) {
		return this.asGetter().buffer(mode, hasher).asProducer();
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#translatedProducer(Producer, Getter) translatedProducer(this, trans)}. */
	default <V2> Producer3<V2> translate(Getter<? super V, ? extends V2> trans) {
		return translatedProducer(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Producer3<V> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#synchronizedProducer(Producer, Object) synchronizedProducer(this, mutex)}. */
	default Producer3<V> synchronize(Object mutex) {
		return synchronizedProducer(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#getterFromProducer(Producer) producerToGetter(this)}. */
	default Getter3<Object, V> asGetter() {
		return getterFromProducer(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link #asProperty(Consumer) this.asProperty(emptyConsumer())}. */
	default Property3<V> asProperty() {
		return this.asProperty(emptyConsumer());
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#propertyFrom(Producer, Consumer) propertyFrom(this, set)}. */
	default Property3<V> asProperty(Consumer<? super V> set) {
		return propertyFrom(this, set);
	}

}
