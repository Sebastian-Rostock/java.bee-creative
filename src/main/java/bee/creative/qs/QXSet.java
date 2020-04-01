package bee.creative.qs;

/** Diese Klasse implementiert eine abstrakte Menge mit Bezug zu einem {@link #store() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Einträge. */
interface QXSet<GItem> extends QX, Iterable<GItem> {

	long size();

	long minSize();

	long maxSize();

	/** Diese Methode gibt eine temporäre Kopie dieser Menge zurück. Änderungen an dieser Menge werden nicht in den Graphspeicher übertragen.
	 * 
	 * @return Mengenkopie. */
	QXSet<GItem> clone();

	boolean has(GItem item) throws NullPointerException;

	boolean hasAll(Iterable<? extends GItem> item) throws NullPointerException;

	boolean put(GItem item) throws NullPointerException, IllegalArgumentException, IllegalStateException;

	boolean putAll(Iterable<? extends GItem> item) throws NullPointerException, IllegalArgumentException, IllegalStateException;

	boolean pop(GItem item) throws NullPointerException, IllegalStateException;

	boolean popAll(Iterable<? extends GItem> item) throws NullPointerException, IllegalStateException;

	boolean clear() throws IllegalStateException;

	boolean union(QXSet<GItem> item) throws NullPointerException, IllegalArgumentException;

	boolean section(QXSet<GItem> item) throws NullPointerException, IllegalArgumentException;

}