package bee.creative._dev_.sts;

// TODO String durch STSText ersetzen -> Mapped-Sichten mit effizientem hash/equals
/** Diese Klasse implementiert einen Knoten eines Graphen, der in einem {@link #store() Graphspeicher} verwaltet wird.<br>
 * Die korrekte Ermittlung von {@link #equals(Object) Äquivalenz} und {@link #compareTo(STSNode) Ordnung} zweier Knoten setzt deren Verwaltung im gleichen
 * Graphspeicher voraus.
 * 
 * @see STSStore
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class STSNode extends STSItem implements Comparable<STSNode> {

	@SuppressWarnings ("javadoc")
	protected STSNode(final STSStore store, final int index) {
		super(store, index);
	}

	{}

	/** Diese Methode gibt den Lokalnamen dieses Knoten zurück. Dieser kennzeichnet den Knoten im Kontext seines {@link #namespace() Namensraums} eineindeutig.
	 *
	 * @return Lokalnamen. */
	public String localname() {
		return this.store.customGetNodeLocalname(this.index);
	}

	/** Diese Methode gibt den Namensraum dieses Knoten zurück.
	 *
	 * @return Namensraum. */
	public String namespace() {
		return this.store.customGetNodeNamespace(this.index);
	}

	{}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.hashImpl();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		return (object == this) || ((object instanceof STSNode) && this.equalsImpl((STSNode)object));
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final STSNode that) {
		return this.compareImpl(that);
	}

	/** Diese Methode gibt die Textdarstellung dieses Knoten zurück.<br>
	 * Diese besteht aus {@code <}{@link #namespace() Namensraum}{@code #}{@link #localname() Lokalname}{@code >}. */
	@Override
	public String toString() {
		return "<" + this.namespace() + "#" + this.localname() + ">";
	}

}
