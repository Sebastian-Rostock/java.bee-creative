package bee.creative.bind;

/** Diese Schnittstelle definiert einen Adapter zum Lesen einer Eigenschaft eines gegebenen Datensatzes. Das Lesen der Eigenschaft kann auch als Umwandlung oder
 * Navigation verstanden werden.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public interface Getter<GItem, GValue> {

	/** Diese Methode gibt den Wert der Eigenschaft des gegebenen Datensatzes zurück.
	 *
	 * @param item Datensatz.
	 * @return Wert der Eigenschaft. */
	public abstract GValue get(GItem item);

}