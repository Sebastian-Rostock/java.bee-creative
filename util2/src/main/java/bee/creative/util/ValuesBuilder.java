package bee.creative.util;

/**
 * Diese Schnittstelle definiert methoden zum Hinzufügen von Werten.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
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

}