package bee.creative.util2;

/**
 * Diese Schnittstelle definiert Methoden zum Hinzufügen von Werten.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ der Werte.
 */
public interface ValuesBuilder<GValue> {

	/**
	 * Diese Methode fügt den gegebenen Wert hinzu.
	 * 
	 * @param value Wert.
	 * @return {@link ValuesBuilder}.
	 */
	public ValuesBuilder<GValue> add(GValue value);

	/**
	 * Diese Methode fügt die gegebenen Werte hinzu.
	 * 
	 * @param value Werte.
	 * @return {@link ValuesBuilder}.
	 */
	public ValuesBuilder<GValue> addAll(GValue... value);

	/**
	 * Diese Methode fügt die gegebenen Werte hinzu.
	 * 
	 * @param value Werte.
	 * @return {@link ValuesBuilder}.
	 */
	public ValuesBuilder<GValue> addAll(Iterable<? extends GValue> value);

	/**
	 * Diese Methode entfernt den gegebenen Wert.
	 * 
	 * @param value Wert.
	 * @return {@link ValuesBuilder}.
	 */
	public ValuesBuilder<GValue> remove(GValue value);

	/**
	 * Diese Methode entfernt die gegebenen Werte.
	 * 
	 * @param value Werte.
	 * @return {@link ValuesBuilder}.
	 */
	public ValuesBuilder<GValue> removeAll(GValue... value);

	/**
	 * Diese Methode entfernt die gegebenen Werte.
	 * 
	 * @param value Werte.
	 * @return {@link ValuesBuilder}.
	 */
	public ValuesBuilder<GValue> removeAll(Iterable<? extends GValue> value);

}