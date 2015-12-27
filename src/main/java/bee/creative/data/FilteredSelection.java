package bee.creative.data;

import java.util.Iterator;
import bee.creative.util.Field;
import bee.creative.util.Filters;
import bee.creative.util.Iterables;

/**
 * Diese Klasse implementiert eine {@link Selection}, die auf einem {@link Iterable} aufsetzt.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Datensätze.
 */
public final class FilteredSelection<GItem> implements Selection<GItem> {

	/**
	 * Dieses Feld speichert das {@link Iterable}.
	 */
	final Iterable<? extends GItem> __items;

	/**
	 * Dieser Konstruktor initialisiert das {@link Iterable}.
	 * 
	 * @param items {@link Iterable} der {@link Item}s.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 */
	public FilteredSelection(final Iterable<? extends GItem> items) throws NullPointerException {
		if (items == null) throw new NullPointerException("items = null");
		this.__items = items;
	}

	{}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Diese Implementation gibt das erste {@link Item} der {@link Selection} zurück, die von {@link #findAll(Field, Object)} für die gegebenen Parameter
	 * geliefert wird.
	 */
	@Override
	public <GValue> GItem find(final Field<? super GItem, ? extends GValue> field, final GValue value) throws NullPointerException {
		for (final GItem item: this.findAll(field, value))
			return item;
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <GValue> Selection<GItem> findAll(final Field<? super GItem, ? extends GValue> field, final GValue value) throws NullPointerException {
		if (field == null) throw new NullPointerException("field = null");
		return new FilteredSelection<GItem>(Iterables.filteredIterable(Filters.navigatedFilter(field, Filters.containsFilter(value)), this.__items));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings ("unchecked")
	@Override
	public Iterator<GItem> iterator() {
		return (Iterator<GItem>)this.__items.iterator();
	}

}