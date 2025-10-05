package bee.creative.util;

import static bee.creative.util.Consumers.synchronizedConsumer;
import static bee.creative.util.Consumers.translatedConsumer;
import static bee.creative.util.Properties.emptyProperty;
import static bee.creative.util.Properties.propertyFrom;
import static bee.creative.util.Setters.setterFrom;

/** Diese Schnittstelle ergänzt einen {@link Consumer2} insb. um eine erweiterte Anbindung an Methoden von {@link Consumers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <VALUE> Typ des Werts. */
public interface Consumer3<VALUE> extends Consumer2<VALUE> {

	/** Diese Methode ist eine Abkürzung für {@link Consumers#translatedConsumer(Consumer, Getter) translateConsumer(this, trans)}. */
	default <VALUE2> Consumer3<VALUE2> translate(Getter<? super VALUE2, ? extends VALUE> trans) {
		return translatedConsumer(this, trans);
	}

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
	default Property3<VALUE> toProperty(Producer<? extends VALUE> get) {
		return propertyFrom(get, this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#propertyFrom(Producer, Consumer) propertyFrom(emptyProperty(), this)}. */
	default Property3<VALUE> toProperty() {
		return propertyFrom(emptyProperty(), this);
	}

}
