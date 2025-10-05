package bee.creative.util;

/** Diese Schnittstelle definiert einen {@link Consumer} mit {@link Consumer3}-Schnittstelle.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <V> Typ des Werts. */
public interface Consumer2<V> extends Consumer<V> {

	/** Diese Methode liefert die {@link Consumer3}-Schnittstelle zu {@link #set(Object)}. */
	default Consumer3<V> asConsumer() {
		return this::set;
	}

}
