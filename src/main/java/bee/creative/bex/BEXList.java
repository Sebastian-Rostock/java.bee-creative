package bee.creative.bex;

import java.util.Iterator;
import bee.creative.lang.Array;
import bee.creative.lang.Objects;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert die homogene Sicht auf Kind- und Attributknotenlisten. Die aufsteigende Navigation von einer Knotenliste zu deren Elternknoten
 * ist optional.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class BEXList implements Array<BEXNode>, Iterable<BEXNode> {

	static class EmptyList extends BEXList {

		@Override
		public int key() {
			return 0;
		}

		@Override
		public int type() {
			return BEXList.VOID_LIST;
		}

		@Override
		public BEXFile owner() {
			return BEXFile.EMPTY;
		}

		@Override
		public BEXNode get(final int index) {
			return BEXNode.EMPTY;
		}

		@Override
		public int length() {
			return 0;
		}

		@Override
		public BEXNode parent() {
			return BEXNode.EMPTY;
		}

	}

	/** Dieses Feld speichert die leere {@link BEXList} als undefinierte Knotenliste im {@link BEXFile#EMPTY}. */
	public static final BEXList EMPTY = new EmptyList();

	/** Dieses Feld speichert die Typkennung einer undefinierten Knotenliste. */
	public static final int VOID_LIST = 0;

	/** Dieses Feld speichert die Typkennung einer Kindknotenliste. */
	public static final int CHLD_LIST = 1;

	/** Dieses Feld speichert die Typkennung einer Attributknotenliste. */
	public static final int ATTR_LIST = 2;

	/** Diese Methode gibt den Identifikator dieser Knotenliste zurück.
	 *
	 * @return Identifikator. */
	public abstract int key();

	/** Diese Methode gibt die Typkennung dieser Knotenliste zurück. Die Typkennung ist bei einer Attributknotenliste {@link #ATTR_LIST}, bei einer allgemeinen
	 * Kindknotenliste {@link #CHLD_LIST} und bei einer undefinierten Knotenliste {@link #VOID_LIST}.
	 *
	 * @return Typkennung. */
	public abstract int type();

	/** Diese Methode gibt das diese Knotenliste verwaltende {@link BEXFile} zurück.
	 *
	 * @return Besitzer. */
	public abstract BEXFile owner();

	/** Diese Methode gibt den {@code index}-ten Knoten dieser Knotenliste zurück. Bei einem ungültigen {@code index} wird ein undefinierter Knoten geliefert. */
	@Override
	public abstract BEXNode get(int index);

	/** Diese Methode sucht linear ab der gegebenen {@code start}-Position den ersten Element- bzw. Attribut-knoten mit der gegebenen {@code uri} sowie dem
	 * gegebenen {@code name} und gibt dessen Position zurück. Bei einer erfolglosen Suche wird {@code -1} geliefert. Ein leerer {@code uri} bzw. {@code name}
	 * wird bei der Suche ignoriert, d.h. der gesuchte Knoten hat einen beliebigen URI bzw. Namen. Bei einer negativen {@code start}-Position wird immer
	 * {@code -1} geliefert.
	 *
	 * @param uri URI.
	 * @param name Name.
	 * @param start Position, ab der die Suche beginnt.
	 * @return Position des Treffers oder {@code -1}.
	 * @throws NullPointerException Wenn {@code uri} bzw. {@code name} {@code null} ist. */
	public int find(final String uri, final String name, final int start) throws NullPointerException {
		if (start < 0) return -1;
		final boolean useUri = uri.length() != 0, useName = name.length() != 0;
		for (int i = start, length = this.length(); i < length; i++) {
			final BEXNode node = this.get(i);
			if (useUri && !node.uri().equals(uri)) {
				continue;
			}
			if (useName && !node.name().equals(name)) {
				continue;
			}
			return i;
		}
		return -1;
	}

	/** Diese Methode gibt die Länge dieser Knotenliste zurück. Die Länge ist bei einer undefinierten Knotenliste {@code 0}.
	 *
	 * @return Länge. */
	public abstract int length();

	/** Diese Methode gibt den Elternknoten dieser Knotenliste zurück (optional). Der Elternknoten ist bei einer undefinierten Knotenliste ein undefinierter
	 * Knoten. Wenn die Navigation zum Elternknoten deaktiviert ist, ist der Elternknoten jeder Knotenliste ein undefinierter Knoten.
	 *
	 * @return Elternknoten. */
	public abstract BEXNode parent();

	@Override
	public Iterator<BEXNode> iterator() {
		return Iterators.fromArray(this, 0, this.length());
	}

	@Override
	public int hashCode() {
		return this.key() ^ this.owner().hashCode();
	}

	@Override
	public boolean equals(final Object object) {
		if (this == object) return true;
		if (!(object instanceof BEXList)) return false;
		final BEXList that = (BEXList)object;
		return (this.key() == that.key()) && this.owner().equals(that.owner());
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this);
	}

}
