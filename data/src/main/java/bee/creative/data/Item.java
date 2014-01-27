package bee.creative.data;

import java.util.Iterator;
import bee.creative.util.Assignable;
import bee.creative.util.Assignment;

/**
 * Diese Schnittstelle definiert ein von einem {@link Pool} verwalteten Datensatz, welcher als Abstraktion eiens Eintrags einer Tabelle einer Datenbank
 * verstanden werden kann. Ein solcher Datensatz hat einen {@link #type() Datentyp} und besitzt zur Identifikation einen {@link #key() Schlüssel}. Der Schlüssel
 * entspricht dem Identifikator, den auch die Datenbank verwendet.<br>
 * {@link Item}s werden von {@link Pool}s verwaltet und sind {@link Assignable}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Item extends Part, Assignable<Item> {

	/**
	 * Dieses Feld speichert den Status, der ein flüchtiges {@link Item} als erzeugt und vorübergehend vorhanden kennzeichnet. Ein {@link Item} mit diesem Status
	 * kann ausgehend von einem {@link Pool} nur über {@link Pool#get(long)} und {@link Pool#create()} ermittelt werden. Über {@link Iterator#remove()} kann ein
	 * {@link Item} in diesen Status überführt werden.
	 */
	static public final int CREATE_STATE = 0;

	/**
	 * Dieses Feld speichert den Status, der ein dauerhaftes {@link Item} als hinzugefügt kennzeichnet. Ein {@link Item} mit diesem Status gilt als allgemein
	 * nutzbar. Über {@link Item#append()} kann ein {@link Item} in diesen Status überführt werden.
	 */
	static public final int APPEND_STATE = 1;

	/**
	 * Dieses Feld speichert den Status, der ein dauerhaftes {@link Item} als entfernt kennzeichnet. Ein {@link Item} mit diesem Status gilt als nicht nutzbar.
	 * Über {@link Item#remove()} kann ein {@link Item} in diesen Status überführt werden.
	 */
	static public final int REMOVE_STATE = 2;

	/**
	 * Dieses Feld speichert den Status, der ein dauerhaftes {@link Item} als aktualisierung kennzeichnet. Ein {@link Item} mit diesem Status gilt als partiell
	 * nutzbar. Über {@link Item#update()} kann ein {@link Item} in diesen Status überführt werden.
	 */
	static public final int UPDATE_STATE = 4;

	/**
	 * {@inheritDoc} Dieser wird über den {@link #pool()} ermittelt.
	 * 
	 * @see #pool()
	 */
	@Override
	public Object owner();

	/**
	 * Diese Methode gibt den Datentyp dieses {@link Item}s zurück. Dieser wird über den {@link #pool()} ermittelt.
	 * 
	 * @see #pool()
	 * @see Pool#type()
	 * @return Datentyp.
	 */
	@Override
	public Type<?> type();

	/**
	 * Diese Methode gibt den identifizierenden Schlüssel zurück.
	 * 
	 * @return identifizierender Schlüssel.
	 */
	public long key();

	/**
	 * Diese Methode gibt den {@link Pool} zurück, der dieses {@link Item} verwaltet.
	 * 
	 * @return {@link Pool}.
	 */
	public Pool<? extends Item> pool();

	/**
	 * Diese Methode gibt den Status zurück.
	 * 
	 * @see #CREATE_STATE
	 * @see #APPEND_STATE
	 * @see #REMOVE_STATE
	 * @see #UPDATE_STATE
	 * @return Status.
	 */
	public int state();

	/**
	 * Diese Methode gibt eine Auflistung der {@link Item}s zurück, die dieses {@link Item} verwenden und darauf verweisen. Über {@link Iterator#remove()} wird
	 * die Assoziation des verwendenden {@link Item}s zu diesem {@link Item} aufgelöst.
	 * 
	 * @return {@link Item}s, die dieses {@link Item} verwenden und darauf verweisen.
	 */
	public Iterable<? extends Item> users();

	/**
	 * Diese Methode überträgt die Informationen des im gegebenen {@link Assignment} gehaltenen {@link Item}s auf dieses {@link Item}.
	 */
	@Override
	public void assign(Assignment<? extends Item> assignment) throws NullPointerException, IllegalArgumentException;

	/**
	 * Diese Methode überführt das {@link Item} in den Status {@link #CREATE_STATE}.
	 */
	public void delete();

	/**
	 * Diese Methode überführt das {@link Item} in den Status {@link #APPEND_STATE}.
	 */
	public void append();

	/**
	 * Diese Methode überführt das {@link Item} in den Status {@link #REMOVE_STATE}, sofern es sich in einem der Status {@link #APPEND_STATE} oder
	 * {@link #UPDATE_STATE} befindet.
	 * 
	 * @throws IllegalStateException Wenn sich das {@link Item} im Status {@link #CREATE_STATE} befindet.
	 */
	public void remove() throws IllegalStateException;

	/**
	 * Diese Methode überführt das {@link Item} in den Status {@link #UPDATE_STATE}, sofern es sich in einem der Status {@link #CREATE_STATE} oder
	 * {@link #REMOVE_STATE} befindet.
	 * 
	 * @throws IllegalStateException Wenn sich das {@link Item} im Status {@link #APPEND_STATE} befindet.
	 */
	public void update() throws IllegalStateException;

}
