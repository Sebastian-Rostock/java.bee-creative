package bee.creative.util;

import static bee.creative.util.Producers.producerFrom;

/** Diese Schnittstelle definiert einen {@link Producer} mit {@link Producer3}-Schnittstelle.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <V> Typ des Werts. */
public interface Producer2<V> extends Producer<V> {

	/** Diese Methode liefert die {@link Producer3}-Schnittstelle zu {@link #get()}. */
	default Producer3<V> asProducer() {
		return producerFrom(this);
	}

}
