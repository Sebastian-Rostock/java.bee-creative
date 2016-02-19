package bee.creative.data;

import java.util.Iterator;
import bee.creative.util.Field;

/** Diese Schnittstelle definiert eine Teilmenge der Datensätze (z.B. der {@link Item}s) einer Sammlung (z.B. eines {@link Pool}s) als Ergebnis eienr Suche, auf
 * welcher auch weiter gesucht werden kann.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Datensätze. */
public interface Selection<GItem> extends Iterable<GItem> {

	/** Diese Methode gibt ein {@link Item} zurück, das für die als {@link Field} gegebene Eigenschaft den gegebenen Wert besitzen.
	 * 
	 * @param <GValue> Typ des Werts.
	 * @param field {@link Field} der Eigenschaft.
	 * @param value Wert.
	 * @return {@link Item} oder {@code null}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public <GValue> GItem find(Field<? super GItem, ? extends GValue> field, GValue value) throws NullPointerException;

	/** Diese Methode gibt die {@link Selection} der {@link Item}s zurück, die für die als {@link Field} gegebene Eigenschaft den gegebenen Wert besitzen.
	 * 
	 * @param <GValue> Typ des Werts.
	 * @param field {@link Field} der Eigenschaft.
	 * @param value Wert.
	 * @return {@link Selection}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public <GValue> Selection<GItem> findAll(Field<? super GItem, ? extends GValue> field, GValue value) throws NullPointerException;

	/** Diese Methode gibt den {@link Iterator} über die {@link Item}s der {@link Selection} zurück. */
	@Override
	public Iterator<GItem> iterator();

}
