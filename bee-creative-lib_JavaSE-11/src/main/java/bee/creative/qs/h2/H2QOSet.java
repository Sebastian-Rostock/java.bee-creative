package bee.creative.qs.h2;

import bee.creative.qs.QOSet;

/** Diese Klasse implementiert ein {@link QOSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GI> Typ der Einträge.
 * @param <GISet> Typ dieser Menge. */
public abstract class H2QOSet<GI, GISet> extends H2QISet<GI> implements QOSet<GI, GISet> {

	// /** Diese Methode indiziert diese Menge zur schnelleren Suche bzw. gibt eine Suche indizierte Kopie dieser Menge zurück.
	// *
	// * @return indizierte Menge. */
	// public abstract GISet index();

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Wenn letztre {@code null} ist, wird sie über
	 * {@link H2QQ#H2QQ(H2QS)} erzeugt. */
	protected H2QOSet(H2QS owner, H2QQ table) throws NullPointerException {
		super(owner, table);
	}

}
