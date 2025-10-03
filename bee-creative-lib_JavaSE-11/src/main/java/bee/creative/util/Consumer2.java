package bee.creative.util;

import static bee.creative.util.Consumers.synchronizedConsumer;
import static bee.creative.util.Properties.propertyFrom;
import static bee.creative.util.Setters.setterFrom;

/** Diese Schnittstelle ergänzt einen {@link Consumer} insb. um eine Anbindung an Methoden von {@link Consumers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <VALUE> Typ des Werts. */
public interface Consumer2<VALUE> extends Consumer<VALUE> {

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Consumer2<VALUE> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#synchronizedConsumer(Consumer, Object) synchronizedConsumer(this, mutex)}. */
	default Consumer2<VALUE> synchronize(Object mutex) {
		return synchronizedConsumer(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#setterFrom(Consumer) setterFromConsumer(this)}. */
	default Setter3<Object, VALUE> toSetter() {
		return setterFrom(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#propertyFrom(Producer, Consumer) propertyFrom(get, this)}. */
	default Property2<VALUE> toProperty(Producer<? extends VALUE> get) {
		return propertyFrom(get, this);
	}

}
