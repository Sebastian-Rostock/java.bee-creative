package bee.creative.util;

/** Diese Schnittstelle definiert eine Methode zur Bereitstellung eines Datensatzes, welcher bspw. durch den Besitzer dieser Methode erzeugt, verwaltet oder
 * konfiguriert werden kann.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Datensatzes. */
public interface Producer<GValue> {

	/** Diese Methode gibt den Datensatz zurück, der durch dieses Objekt erzeugt, verwaltet, konfiguriert oder anderweitig bereitgestellt wird.
	 *
	 * @return Datensatz. */
	public GValue get();

}
