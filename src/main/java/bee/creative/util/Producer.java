package bee.creative.util;

/** Diese Schnittstelle definiert eine Methode zur Bereitstellung eines Datensatzes.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Datensatzes. */
public interface Producer<GValue> {

	/** Diese Methode gibt den Datensatz zurück. Dieser kann bspw. durch dieses Objekt erzeugt, verwaltet oder konfiguriert worden sien.
	 *
	 * @return Datensatz. */
	public GValue get();

}
