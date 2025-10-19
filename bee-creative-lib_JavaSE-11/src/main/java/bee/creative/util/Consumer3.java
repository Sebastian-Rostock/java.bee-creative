package bee.creative.util;

import static bee.creative.util.Consumers.synchronizedConsumer;
import static bee.creative.util.Consumers.translatedConsumer;
import static bee.creative.util.Properties.emptyProperty;
import static bee.creative.util.Properties.propertyFrom;
import static bee.creative.util.Setters.setterFrom;

/** Diese Schnittstelle ergänzt einen {@link Consumer2} insb. um eine erweiterte Anbindung an Methoden von {@link Consumers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <V> Typ des Werts. */
public interface Consumer3<V> extends Consumer<V> {

	/** Diese Methode ist eine Abkürzung für {@link Consumers#translatedConsumer(Consumer, Getter) translateConsumer(this, trans)}. */
	default <V2> Consumer3<V2> translate(Getter<? super V2, ? extends V> trans) {
		return translatedConsumer(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Consumer3<V> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#synchronizedConsumer(Consumer, Object) synchronizedConsumer(this, mutex)}. */
	default Consumer3<V> synchronize(Object mutex) {
		return synchronizedConsumer(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#setterFrom(Consumer) setterFromConsumer(this)}. */
	default Setter3<Object, V> asSetter() {
		return setterFrom(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link #asProperty(Producer) this.asProperty(emptyProperty())}. */
	default Property3<V> asProperty() {
		return this.asProperty(emptyProperty());
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#propertyFrom(Producer, Consumer) propertyFrom(get, this)}. */
	default Property3<V> asProperty(Producer<? extends V> get) {
		return propertyFrom(get, this);
	}

}
