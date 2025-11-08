package bee.creative.util;

import static bee.creative.util.Fields.fieldFrom;

/** Diese Schnittstelle definiert ein {@link Field} mit {@link Field3}-Schnittstelle.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ des Datensatzes.
 * @param <V> Typ des Werts der Eigenschaft. */
public interface Field2<T, V> extends Field<T, V> {

	/** Diese Methode liefert die {@link Field3}-Schnittstelle zu {@link #get(Object)} und {@link #set(Object, Object)}. */
	default Field3<T, V> asField() {
		return fieldFrom(this);
	}

}
