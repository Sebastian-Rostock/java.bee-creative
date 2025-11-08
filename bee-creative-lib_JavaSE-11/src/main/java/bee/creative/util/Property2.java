package bee.creative.util;

import static bee.creative.util.Properties.propertyFrom;

/** Diese Schnittstelle definiert ein {@link Property} mit {@link Property3}-Schnittstelle.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <V> Typ des Werts. */
public interface Property2<V> extends Property<V> {

	/** Diese Methode liefert die {@link Property3}-Schnittstelle zu {@link #get()} und {@link #set(Object)}. */
	default Property3<V> asProperty() {
		return propertyFrom(this);
	}

}
