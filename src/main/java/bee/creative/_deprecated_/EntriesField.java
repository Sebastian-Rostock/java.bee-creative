package bee.creative._deprecated_;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import bee.creative.bind.Field;

/** Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Map}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der {@link Map}, welche nach ihrer Modifikation über {@link Field#set(Object, Object)} zugewiesen
 * wird.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public interface EntriesField<GItem, GKey, GValue> {

	/** Diese Methode verändert die {@link Map} analog zu {@link Map#put(Object, Object)}.
	 *
	 * @param item Eingabe.
	 * @param key Schlüssel.
	 * @param value Wert. */
	public void put(final GItem item, final GKey key, GValue value);

	/** Diese Methode verändert die {@link Map} analog zu {@link Map#putAll(Map)}.
	 *
	 * @param item Eingabe.
	 * @param entries Elemente. */
	public void putAll(final GItem item, final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries);

	/** Diese Methode verändert die {@link Map} analog zu {@link Map#remove(Object)}.
	 *
	 * @param item Eingabe.
	 * @param key Schlüssel. */
	public void pop(final GItem item, final Object key);

	/** Diese Methode verändert die {@link Map} analog zu {@link Map#keySet()} mit {@link Set#removeAll(Collection)}.
	 *
	 * @param item Eingabe.
	 * @param keys Schlüssel. */
	public void popAll(final GItem item, final Iterable<?> keys);

	/** Diese Methode verändert die {@link Map} analog zu {@link Map#clear()}.
	 *
	 * @param item Eingabe. */
	public void clear(final GItem item);

}