package bee.creative.util;

import static bee.creative.util.Properties.propertyFrom;

/** Diese Schnittstelle definiert einen {@link Property} mit {@link Property3}-Schnittstelle.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <V> Typ des Werts. */
public interface Property2<V> extends Property<V>, Producer2<V>, Consumer2<V> {

	/** Diese Methode ist eine Abkürzung für {@link Properties#propertyFrom(Property) propertyFrom(this)}. */
	default Property3<V> asProperty() {
		return propertyFrom(this);
	}

}
