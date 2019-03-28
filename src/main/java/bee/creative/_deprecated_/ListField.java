package bee.creative._deprecated_;

import java.util.Collection;
import java.util.List;
import bee.creative.bind.Field;

/** Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link List}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der {@link List}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen
 * wird.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GEntry> Typ der Elemente. */
public interface ListField<GItem, GEntry> extends ItemsField<GItem, GEntry>, Field<GItem, List<GEntry>> {

	/** Diese Methode verändert die Sammlung analog zu {@link List#add(int, Object)}.
	 *
	 * @param item Eingabe.
	 * @param index Index.
	 * @param entry Element. */
	public void put(final GItem item, final int index, final GEntry entry);

	/** Diese Methode verändert die Sammlung analog zu {@link List#addAll(int, Collection)}.
	 *
	 * @param item Eingabe.
	 * @param index Index.
	 * @param entries Elemente. */
	public void putAll(final GItem item, final int index, final Iterable<? extends GEntry> entries);

	/** Diese Methode verändert die Sammlung analog zu {@link List#remove(int)}.
	 *
	 * @param item Eingabe.
	 * @param index Index. */
	public void pop(final GItem item, final int index);

}