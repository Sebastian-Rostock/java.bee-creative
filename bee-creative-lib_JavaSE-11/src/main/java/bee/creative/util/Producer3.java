package bee.creative.util;

import static bee.creative.util.Consumers.emptyConsumer;
import static bee.creative.util.Producers.synchronizedProducer;
import static bee.creative.util.Properties.propertyFrom;

/** Diese Schnittstelle ergänzt einen {@link Producer2} insb. um eine erweiterte Anbindung an Methoden von {@link Producers}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <VALUE> Typ des Werts. */
public interface Producer3<VALUE> extends Producer2<VALUE> {

	@Override
	default Producer3<VALUE> synchronize() {
		return this.synchronize(this);
	}

	@Override
	default Producer3<VALUE> synchronize(Object mutex) {
		return synchronizedProducer(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#propertyFrom(Producer, Consumer) propertyFrom(this, emptyConsumer())}. */
	default Property2<VALUE> toProperty() {
		return propertyFrom(this, emptyConsumer());
	}

}
