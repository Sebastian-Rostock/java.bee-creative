package bee.creative.util2;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Diese Schnittstelle definiert Methoden zum Hinzufügen von Schlüssel-Wert-Paaren.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 */
public interface EntriesBuilder<GKey, GValue> {

	/**
	 * Diese Methode fügt das gegebenen Schlüssel-Wert-Paar hinzu.
	 * 
	 * @param key Schlüssel.
	 * @param value Wert.
	 * @return {@link EntriesBuilder}.
	 */
	public EntriesBuilder<GKey, GValue> add(GKey key, GValue value);

	/**
	 * Diese Methode fügt das gegebenen Schlüssel-Wert-Paar hinzu.
	 * 
	 * @param entry Schlüssel-Wert-Paar.
	 * @return {@link EntriesBuilder}.
	 */
	public EntriesBuilder<GKey, GValue> add(Entry<? extends GKey, ? extends GValue> entry);

	/**
	 * Diese Methode fügt die gegebenen Schlüssel-Wert-Paare hinzu.
	 * 
	 * @param entries Schlüssel-Wert-Paare.
	 * @return {@link EntriesBuilder}.
	 */
	public EntriesBuilder<GKey, GValue> addAll(Entry<? extends GKey, ? extends GValue>... entries);

	/**
	 * Diese Methode fügt die gegebenen Schlüssel-Wert-Paare hinzu.
	 * 
	 * @param entries Schlüssel-Wert-Paare.
	 * @return {@link EntriesBuilder}.
	 */
	public EntriesBuilder<GKey, GValue> addAll(Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries);

	/**
	 * Diese Methode fügt die gegebenen Schlüssel-Wert-Paare hinzu.
	 * 
	 * @param entries Schlüssel-Wert-Paare.
	 * @return {@link EntriesBuilder}.
	 */
	public EntriesBuilder<GKey, GValue> addAll(Map<? extends GKey, ? extends GValue> entries);

	/**
	 * Diese Methode entfernt den gegebenen Schlüssel.
	 * 
	 * @param key Schlüssel.
	 * @return {@link EntriesBuilder}.
	 */
	public EntriesBuilder<GKey, GValue> remove(GKey key);

	/**
	 * Diese Methode entfernt die gegebenen Schlüssel.
	 * 
	 * @param keys Schlüssel.
	 * @return {@link EntriesBuilder}.
	 */
	public EntriesBuilder<GKey, GValue> removeAll(GKey... keys);

	/**
	 * Diese Methode entfernt die gegebenen Schlüssel.
	 * 
	 * @param keys Schlüssel.
	 * @return {@link EntriesBuilder}.
	 */
	public EntriesBuilder<GKey, GValue> removeAll(Iterable<? extends GKey> keys);

}