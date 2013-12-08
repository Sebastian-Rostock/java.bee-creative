package bee.creative.data;

import java.util.Iterator;
import bee.creative.util.Assignable;
import bee.creative.util.Assigner;

/**
 * Diese Schnittstelle definiert ein Objekt als Datensatz einer Datenbank. Ein solcher Datensatz hat einen {@link Item#type() Datentyp} und besitzt zur
 * Identifikation einen {@link Item#key() Schlüssel}. Der Schlüssel entspricht dem Identifikator, den auch die Datenbank verwendet.<br>
 * {@link Item}s werden von {@link Pool}s verwaltet und sind {@link Assignable}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des Besitzers.
 */
public interface Item<GOwner> extends Owned<GOwner>, Assignable<Item<?>> {

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
	public GOwner owner();

	/**
	 * Diese Methode gibt den {@link Pool} zurück, der dieses {@link Item} verwaltet.
	 * 
	 * @return {@link Pool}.
	 */
	public Pool<? extends Item<? super GOwner>, ? extends GOwner> pool();

	/**
	 * Diese Methode gibt den {@link Type} zurück. Dieser wird über den {@link #pool()} ermittelt.
	 * 
	 * @see #pool()
	 * @return {@link Type}.
	 */
	public Type<?> type();

	/**
	 * Diese Methode gibt den identifizierenden Schlüssel zurück.
	 * 
	 * @return identifizierender Schlüssel.
	 */
	public long key();

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
	 * Diese Methode gibt überführt das {@link Item} in den Status {@link #APPEND_STATE}.
	 */
	public void append();

	/**
	 * Diese Methode gibt überführt das {@link Item} in den Status {@link #REMOVE_STATE}, sofern es sich in einem der Status {@link #APPEND_STATE} oder
	 * {@link #UPDATE_STATE} befindet.
	 */
	public void remove();

	/**
	 * Diese Methode gibt überführt das {@link Item} in den Status {@link #UPDATE_STATE}, sofern es sich in einem der Status {@link #CREATE_STATE} oder
	 * {@link #REMOVE_STATE} befindet.
	 */
	public void update();

	/**
	 * Diese Methode überträgt die Informationen des im gegebenen {@link Assigner} gehaltenen {@link Item}s auf dieses {@link Item}.
	 */
	@Override
	public void assign(Assigner<? extends Item<?>> assigner) throws NullPointerException, IllegalArgumentException;

}
