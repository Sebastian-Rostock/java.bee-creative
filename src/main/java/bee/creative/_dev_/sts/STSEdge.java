package bee.creative._dev_.sts;

/** Diese Klasse implementiert eine gerichtete Kante eines Graphen, der in einem {@link #store() Graphspeicher} verwaltet wird. Die korrekte Ermittlung von
 * {@link #equals(Object) Äquivalenz} und {@link #compareTo(STSEdge) Ordnung} zweier Kanten setzt deren Verwaltung im gleichen Graphspeicher voraus.
 *
 * @see STSStore
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
  final class STSEdge extends STSItem implements Comparable<STSEdge> {

	@SuppressWarnings ("javadoc")
	protected STSEdge(final STSStore store, final int index) {
		super(store, index);
	}

	/** Diese Methode gibt {@link STSNode Knoten} zurück, der für das Objekt bzw. das Ziel der Kante steht.
	 *
	 * @return Objekt. */
	public STSNode object() {
		return this.store.customGetEdgeObjectNode(this.index);
	}

	/** Diese Methode gibt {@link STSNode Knoten} zurück, der für das Subjekt bzw. den Beginn der Kante steht.
	 *
	 * @return Subjekt. */
	public STSNode subject() {
		return this.store.customGetEdgeSubjectNode(this.index);
	}

	/** Diese Methode gibt {@link STSNode Knoten} zurück, der für das Prädikat bzw. die Bedeutung der Kante steht.
	 *
	 * @return Prädikat. */
	public STSNode predicate() {
		return this.store.customGetEdgePredicateNode(this.index);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.hashImpl();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		return (object == this) || ((object instanceof STSEdge) && this.equalsImpl((STSEdge)object));
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final STSEdge that) {
		return this.compareImpl(that);
	}

	/** Diese Methode gibt die Textdarstellung dieses Knoten zurück. */
	@Override
	public String toString() {
		return this.store.customGetEdgeString(this.index);
	}

}
