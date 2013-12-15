package bee.creative.data;

import java.util.Iterator;
import bee.creative.util.Field;

/**
 * Diese Schnittstelle definiert eine Teilmenge der {@link Item}s eines {@link Pool}s als Ergebnis eienr Suche, auf welcher auch weiter gesucht werden kann.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Datensätze.
 */
public interface Selection<GItem extends Item<?>> extends Iterable<GItem> {

	/**
	 * Diese Methode gibt ein {@link Item} zurück, das für die als {@link Field} gegebene Eigenschaft den gegebenen Wert besitzen.
	 * 
	 * @param <GValue> Typ des Werts.
	 * @param field {@link Field} der Eigenschaft.
	 * @param value Wert.
	 * @return {@link Item} oder {@code null}.
	 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
	 */
	public <GValue> GItem find(Field<? super GItem, ? extends GValue> field, GValue value) throws NullPointerException;

	/**
	 * Diese Methode gibt die {@link Selection} der {@link Item}s zurück, die für die als {@link Field} gegebene Eigenschaft den gegebenen Wert besitzen.
	 * 
	 * @param <GValue> Typ des Werts.
	 * @param field {@link Field} der Eigenschaft.
	 * @param value Wert.
	 * @return {@link Selection}.
	 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
	 */
	public <GValue> Selection<GItem> findAll(Field<? super GItem, ? extends GValue> field, GValue value) throws NullPointerException;

	/**
	 * Diese Methode gibt den {@link Iterator} über die {@link Items} der {@link Selection} zurück.
	 */
	@Override
	public Iterator<GItem> iterator();

}
