package bee.creative.util;

import static bee.creative.util.Consumers.consumerFromSetter;
import static bee.creative.util.Getters.BufferedGetter.SOFT_REF_MODE;
import static bee.creative.util.Hashers.naturalHasher;
import static bee.creative.util.Producers.synchronizedProducer;
import static bee.creative.util.Producers.translatedProducer;
import static bee.creative.util.Properties.propertyFrom;

/** Diese Schnittstelle ergänzt einen {@link Producer} insb. um eine Anbindung an Methoden von {@link Producers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <VALUE> Typ des Werts. */
public interface Producer2<VALUE> extends Producer<VALUE> {

	/** Diese Methode ist eine Abkürzung für {@link Consumers#consumerFromSetter(Setter, Producer) consumerFromSetter(this, set)}. */
	default <VALUE2> Consumer3<VALUE2> concat(Setter<? super VALUE, ? super VALUE2> set) {
		return consumerFromSetter(set, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link #buffer(int, Hasher) this.buffer(SOFT_REF, naturalHasher())}. */
	default Producer3<VALUE> buffer() {
		return this.buffer(SOFT_REF_MODE, naturalHasher());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#bufferedGetter(Getter, int, Hasher) this.toGetter().buffer(mode, hasher).toProducer()}. */
	default Producer3<VALUE> buffer(int mode, Hasher hasher) {
		return this.toGetter().buffer(mode, hasher).toProducer();
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#translatedProducer(Producer, Getter) translatedProducer(this, trans)}. */
	default <VALUE2> Producer3<VALUE2> translate(Getter<? super VALUE, ? extends VALUE2> trans) {
		return translatedProducer(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Producer2<VALUE> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#synchronizedProducer(Producer, Object) synchronizedProducer(this, mutex)}. */
	default Producer2<VALUE> synchronize(Object mutex) {
		return synchronizedProducer(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#getterFromProducer(Producer) getterFromProducer(this)}. */
	default Getter3<Object, VALUE> toGetter() {
		return Getters.getterFromProducer(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#propertyFrom(Producer, Consumer) propertyFrom(this, set)}. */
	default Property2<VALUE> toProperty(Consumer<? super VALUE> set) {
		return propertyFrom(this, set);
	}

}
