package bee.creative._deprecated_;

import java.util.Collection;
import bee.creative.bind.Field;

/** Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Collection}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der Objektsammlung, welche nach ihrer Modifikation über {@link Field#set(Object, Object)}
 * zugewiesen wird.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GEntry> Typ der Elemente. */
public interface ItemsField<GItem, GEntry> {

	/** Diese Methode verändert die Sammlung analog zu {@link Collection#add(Object)}.
	 *
	 * @param item Eingabe.
	 * @param entry Element. */
	public void put(final GItem item, final GEntry entry);

	/** Diese Methode verändert die Sammlung analog zu {@link Collection#addAll(Collection)}.
	 *
	 * @param item Eingabe.
	 * @param entries Elemente. */
	public void putAll(final GItem item, final Iterable<? extends GEntry> entries);

	/** Diese Methode verändert die Sammlung analog zu {@link Collection#remove(Object)}.
	 *
	 * @param item Eingabe.
	 * @param entry Element. */
	public void pop(final GItem item, final Object entry);

	/** Diese Methode verändert die Sammlung analog zu {@link Collection#removeAll(Collection)}.
	 *
	 * @param item Eingabe.
	 * @param entries Elemente. */
	public void popAll(final GItem item, final Iterable<?> entries);

	/** Diese Methode verändert die Sammlung analog zu {@link Collection#clear()}.
	 *
	 * @param item Eingabe. */
	public void clear(final GItem item);

}