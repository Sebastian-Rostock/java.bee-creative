package bee.creative.util;

/** Diese Schnittstelle definiert eine Methode zur Bereitstellung eines Werts, welcher bspw. durch den Besitzer dieser Methode erzeugt, verwaltet oder
 * konfiguriert werden kann.
 *
 * @see Getter
 * @see Property
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <VALUE> Typ des Werts. */
public interface Producer<VALUE> {

	/** Diese Methode gibt den Werts zurück, der durch dieses Objekt erzeugt, verwaltet, konfiguriert oder anderweitig bereitgestellt wird.
	 *
	 * @return Wert. */
	VALUE get();

}
