package bee.creative.util;

import static bee.creative.util.Consumers.consumerFromSetter;
import static bee.creative.util.Consumers.emptyConsumer;
import static bee.creative.util.Getters.getterFromProducer;
import static bee.creative.util.Getters.RefMode.SOFT_REF_MODE;
import static bee.creative.util.Hashers.naturalHasher;
import static bee.creative.util.Producers.synchronizedProducer;
import static bee.creative.util.Producers.translatedProducer;
import static bee.creative.util.Properties.propertyFrom;
import bee.creative.util.Getters.RefMode;

/** Diese Schnittstelle ergänzt einen {@link Producer2} insb. um eine erweiterte Anbindung an Methoden von {@link Producers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <V> Typ des Werts. */
public interface Producer3<V> extends Producer2<V> {

	/** Diese Methode ist eine Abkürzung für {@link #buffer(RefMode, Hasher) this.buffer(SOFT_REF, naturalHasher())}. */
	default Producer3<V> buffer() {
		return this.buffer(SOFT_REF_MODE, naturalHasher());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#bufferedGetter(Getter, RefMode, Hasher) this.asGetter().buffer(mode, hasher).asProducer()}. */
	default Producer3<V> buffer(RefMode mode, Hasher hasher) {
		return this.asGetter().buffer(mode, hasher).asProducer();
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#translatedProducer(Producer, Getter) translatedProducer(this, trans)}. */
	default <VALUE2> Producer3<VALUE2> translate(Getter<? super V, ? extends VALUE2> trans) {
		return translatedProducer(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Producer2<V> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#synchronizedProducer(Producer, Object) synchronizedProducer(this, mutex)}. */
	default Producer2<V> synchronize(Object mutex) {
		return synchronizedProducer(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link #asProperty(Consumer) this.asProperty(emptyConsumer())}. */
	default Property3<V> asProperty() {
		return this.asProperty(emptyConsumer());
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#propertyFrom(Producer, Consumer) propertyFrom(this, set)}. */
	default Property3<V> asProperty(Consumer<? super V> set) {
		return propertyFrom(this, set);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#getterFromProducer(Producer) producerToGetter(this)}. */
	default Getter3<Object, V> asGetter() {
		return getterFromProducer(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#consumerFromSetter(Setter, Producer) consumerFromSetter(this, set)}. */
	default <VALUE2> Consumer3<VALUE2> asConsumer(Setter<? super V, ? super VALUE2> set) {
		return consumerFromSetter(set, this);
	}

}
