package bee.creative.util;

/** Diese Schnittstelle definiert eine Methode zur Entgegennahme eines Datensatzes, welcher bspw. durch den Besitzer dieser Methode bspw. verwaltet oder
 * konfiguriert werden kann.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Datensatzes. */
public interface Consumer<GValue> {

	/** Diese Methode setzt den Datensatz, der durch dieses Objekt verwaltet, konfiguriert oder anderweitig verwendet wird.
	 *
	 * @param value Datensatz. */
	public void set(GValue value);

}
