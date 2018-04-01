package bee.creative._dev_.sts;

import bee.creative.fem.FEMBinary;

/** Diese Klasse implementiert den {@link STSStore#getNodeSet() Knoten eines Graphen}, der in einem {@link #store() Graphspeicher} verwaltet wird. Der Knoten
 * ist dazu über seinen {@link #value() Wert} eindeutig gekennzeichnet und kann darüber {@link STSStore#getNode(FEMBinary) identifiziert} werden.
 * <p>
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

	/** Diese Methode gibt den Wert dieses Knoten zurück. Dieser kennzeichnet den Knoten im Kontext seines {@link #store() Graphspeichers} eineindeutig und
	 * verbindet ihn inhaltlich mit einem Element eines externen Wissensspeichers. Der Knotenwert könnte bspw. der Binärkodierung der Verkettung von
	 * Namensraum(-prefix) und Lakolnamen entsprechen.
	 *
	 * @return Knotenwert. */
	public FEMBinary value() {
		return this.store.customGetNodeValue(this.index);
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

	/** Diese Methode gibt die Textdarstellung dieses Knoten zurück. */
	@Override
	public String toString() {
		return this.store.customGetNodeString(this.index);
	}

}
