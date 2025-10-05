package bee.creative.util;

/** Diese Schnittstelle definiert einen {@link Property} mit {@link Property3}-Schnittstelle.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <V> Typ des Werts. */
public interface Property2<V> extends Property<V>, Producer2<V>, Consumer2<V> {

	/** Diese Methode liefert die {@link Property3}-Schnittstelle zu {@link #get()} und {@link #set(Object)}. */
	default Property3<V> asProperty() {
		return Properties.propertyFrom(this::get, this::set);
	}

}
