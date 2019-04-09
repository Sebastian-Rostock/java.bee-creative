package bee.creative._dev_.qhs;

import java.util.Set;

/** Diese Klasse implementiert den Quad-Store.
 * 
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class QS {

	
	
	
	
	QN putImpl(QN n){
	
		return null;
	}
	
	
	public void put(final QE e) throws NullPointerException {

	}

	public void putAll(final Iterable<? extends QE> es) throws NullPointerException {

	}

	public void pop(final QE e) throws NullPointerException {

	}

	public void popAll(final Iterable<? extends QE> es) throws NullPointerException {

	}

	/** Diese Methode gibt die {@link Set Menge der Kanten} zurück, in denen die gegebenen Subjekte, Prädikate bzw. Objekte enthalten sind. Für uneingeschränkte
	 * Komponenten ist {@code null} anzugeben.
	 *
	 * @param s Subjektfilter oder {@code null}.
	 * @param p Prädikatfilter oder {@code null}.
	 * @param o Objektfilter oder {@code null}.
	 * @return gefilterte Kanten. */
	public Set<QE> getSet(final QN s, final QN p, final QN o, final QN c) {

		return null;
	}

	/** Diese Methode gibt die {@link Set Menge der Kanten} zurück, deren Subjekte, Prädikate bzw. Objekte in den gegebenen Mengen enthalten sind. Für
	 * uneingeschränkte Komponenten ist {@code null} anzugeben.
	 *
	 * @param s Subjektfilter oder {@code null}.
	 * @param p Prädikatfilter oder {@code null}.
	 * @param o Objektfilter oder {@code null}.
	 * @return gefilterte Kanten. */
	public Set<QE> getSet(final Set<QN> s, final Set<QN> p, final Set<QN> o, final Set<QN> c) {
		return null;
	}

	/** Diese Methode gibt die Sicht auf die {@link Set Menge aller verwalteten Kanten} zurück.
	 *
	 * @return Kantenmenge. */
	public Set<QE> getEdgeSet() {
		return null;
	}

	/** Diese Methode gibt die Sicht auf die {@link Set Menge aller verwalteten Knoten} zurück.
	 *
	 * @return Knotenmenge. */
	public Set<QN> getNodeSet() {
		return null;
	}

}
