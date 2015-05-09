package bee.creative.data;

import java.util.Collection;
import java.util.Iterator;
import bee.creative.util.Field;

/**
 * Diese Schnittstelle definiert einen Sammlung von {@link Item}s und kann als Abstraktion einer Tabelle einer Datenbank verstenden werden. Die {@link Item}s
 * können über ihren Schlüssel {@link #get(long) identifiziert} werden. Ein {@link Pool} hat darüber hinaus einen Besitzer.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Datensätze.
 */
public interface Pool<GItem extends Item> extends Part, Selection<GItem> {

	/**
	 * Diese Methode gibt den Besitzer zurück, welcher diesen {@link Pool} erzeugt und verwaltet hat.
	 */
	@Override
	public Object owner();

	/**
	 * Diese Methode gibt das {@link Field} zurück, über welches dieser {@link Pool} von seinem {@link #owner()} aus erreichbar ist.
	 * 
	 * @return {@link Field} des {@link Pool}s in seinem {@link #owner()}.
	 */
	@Override
	public Field<?, ? extends Pool<GItem>> field();

	/**
	 * Diese Methode gibt den {@link Type} der {@link Item}s zurück.
	 * 
	 * @return {@link Type} der {@link Item}s.
	 */
	@Override
	public Type<GItem> type();

	/**
	 * Diese Methode gibt den Datensatz mit dem gegebenen Schlüssel oder {@code null} zurück.
	 * 
	 * @param key Schlüssel.
	 * @return Datensatz oder {@code null}.
	 */
	public GItem get(final long key);

	/**
	 * Diese Methode gibt ein {@link Item} im {@value Item#APPEND_STATE} zurück, das für die als {@link Field} gegebene Eigenschaft den gegebenen Wert besitzen.
	 * 
	 * @see Pool#find(Field, Object, int)
	 */
	@Override
	public <GValue> GItem find(Field<? super GItem, ? extends GValue> field, GValue value) throws NullPointerException;

	/**
	 * Diese Methode gibt ein {@link Item} im gegebenen Status zurück, das für die als {@link Field} gegebene Eigenschaft den gegebenen Wert besitzen.
	 * 
	 * @see Pool#find(Field, Object)
	 * @param <GValue> Typ des Werts.
	 * @param field {@link Field} der Eigenschaft.
	 * @param value Wert.
	 * @param states Status-Bitmaske ({@link Item#APPEND_STATE}, {@link Item#REMOVE_STATE}, {@link Item#UPDATE_STATE}).
	 * @return {@link Item} oder {@code null}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 */
	public <GValue> GItem find(Field<? super GItem, ? extends GValue> field, GValue value, int states) throws NullPointerException;

	/**
	 * Diese Methode gibt die {@link Selection} der {@link Item}s im {@link Item#APPEND_STATE} zurück, die für die als {@link Field} gegebene Eigenschaft den
	 * gegebenen Wert besitzen.
	 */
	@Override
	public <GValue> Selection<GItem> findAll(Field<? super GItem, ? extends GValue> field, GValue value) throws NullPointerException;

	/**
	 * Diese Methode gibt die {@link Selection} der {@link Item}s im gegebenen Status zurück, die für die als {@link Field} gegebene Eigenschaft den gegebenen
	 * Wert besitzen.
	 * 
	 * @param <GValue> Typ des Werts.
	 * @param field {@link Field} der Eigenschaft.
	 * @param value Wert.
	 * @param states Status-Bitmaske ({@link Item#APPEND_STATE}, {@link Item#REMOVE_STATE}, {@link Item#UPDATE_STATE}).
	 * @return {@link Selection}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 */
	public <GValue> Selection<GItem> findAll(Field<? super GItem, ? extends GValue> field, GValue value, int states) throws NullPointerException;

	/**
	 * Diese Methode gibt die Anzahl der Datensätze zurück. Hierzu zählen nur die im Zustand {@link Item#APPEND_STATE}.
	 * 
	 * @return Datensatzanzahl.
	 */
	public int size();

	/**
	 * Diese Methode gibt eine {@link Collection}-Sicht auf die {@link Item}s im {@link Item#APPEND_STATE} zurück. Das einfügen von Elementen ist unzulässig.<br>
	 * <i>Achtung:</i> Beim Entfernen von Datensätzen werden diese in den {@link Item#CREATE_STATE} überführt.
	 * 
	 * @see Item#state()
	 * @see Pool#items(int)
	 * @see Pool#iterator()
	 * @return {@link Collection} über die {@link Item}s im {@link Item#APPEND_STATE}.
	 */
	public Collection<? extends GItem> items();

	/**
	 * Diese Methode gibt eine {@link Collection}-Sicht auf die {@link Item}s im gegebenen Status zurück. Das einfügen von Elementen ist unzulässig.<br>
	 * <i>Achtung:</i> Beim Entfernen von Datensätzen werden diese in den {@link Item#CREATE_STATE} überführt.
	 * 
	 * @see Item#state()
	 * @see Pool#items()
	 * @param states Status-Bitmaske ({@link Item#APPEND_STATE}, {@link Item#REMOVE_STATE}, {@link Item#UPDATE_STATE}).
	 * @return {@link Item}s im gegebenen Status.
	 */
	public Collection<? extends GItem> items(int states);

	/**
	 * Diese Methode erstellt ein neues {@link Item} im {@link Item#CREATE_STATE} und gibt dieses zurück.
	 * 
	 * @see Item#state()
	 * @return neues {@link Item}.
	 */
	public GItem create();

	/**
	 * Diese Methode gibt den {@link Iterator} über die {@link Item}s im {@link Item#APPEND_STATE} zurück.
	 * 
	 * @see Pool#items()
	 */
	@Override
	public Iterator<GItem> iterator();

}
