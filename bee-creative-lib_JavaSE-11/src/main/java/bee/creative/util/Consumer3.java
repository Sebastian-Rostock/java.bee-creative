package bee.creative.util;

import static bee.creative.util.Consumers.synchronizedConsumer;
import static bee.creative.util.Consumers.translatedConsumer;
import static bee.creative.util.Properties.emptyProperty;
import static bee.creative.util.Properties.propertyFrom;

/** Diese Schnittstelle ergänzt einen {@link Consumer2} insb. um eine erweiterte Anbindung an Methoden von {@link Consumers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <VALUE> Typ des Werts. */
public interface Consumer3<VALUE> extends Consumer2<VALUE> {

	/** Diese Methode ist eine Abkürzung für {@link Consumers#translatedConsumer(Consumer, Getter) translateConsumer(this, trans)}. */
	default <VALUE2> Consumer3<VALUE2> translate(Getter<? super VALUE2, ? extends VALUE> trans) {
		return translatedConsumer(this, trans);
	}

	@Override
	default Consumer3<VALUE> synchronize() {
		return this.synchronize(this);
	}

	@Override
	default Consumer3<VALUE> synchronize(Object mutex) {
		return synchronizedConsumer(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#propertyFrom(Producer, Consumer) propertyFrom(emptyProperty(), this)}. */
	default Property2<VALUE> toProperty() {
		return propertyFrom(emptyProperty(), this);
	}

}
