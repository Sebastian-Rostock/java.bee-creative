package bee.creative.util;

import static bee.creative.util.Consumers.consumerFrom;

/** Diese Schnittstelle definiert einen {@link Consumer} mit {@link Consumer3}-Schnittstelle.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <V> Typ des Werts. */
public interface Consumer2<V> extends Consumer<V> {

	/** Diese Methode ist eine Abkürzung für {@link Consumers#consumerFrom(Consumer) consumerFrom(this)}. */
	default Consumer3<V> asConsumer() {
		return consumerFrom(this);
	}

}
