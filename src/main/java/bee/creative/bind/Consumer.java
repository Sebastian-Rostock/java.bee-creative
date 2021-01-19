package bee.creative.bind;

/** Diese Schnittstelle definiert eine Methode zur Entgegennahme eines Werts, welcher bspw. durch den Besitzer dieser Methode verwaltet oder konfiguriert werden
 * kann.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public interface Consumer<GValue> {

	/** Diese Methode setzt den Werts, der durch dieses Objekt verwaltet, konfiguriert oder anderweitig verwendet wird.
	 *
	 * @param value Wert. */
	public void set(GValue value);

}
