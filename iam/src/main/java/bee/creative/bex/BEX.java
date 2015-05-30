package bee.creative.bex;

import java.nio.charset.Charset;
import java.util.Iterator;
import bee.creative.iam.IAM;
import bee.creative.iam.IAMArray;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Iterators.GetIterator;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert grundlegende Klassen und Methoden zur Umsetzung des {@code BEX – Binary Encoded XML}.
 * 
 * @see BEXEncoder
 * @see BEXDecoder
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class BEX {

	/**
	 * Diese Klasse implementiert ein abstraktes {@link BEXFile}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BEXBaseFile implements BEXFile {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.root());
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link BEXNode}.<br>
	 * Die Mathoden {@link #hashCode()} und {@link #equals(Object)} basieren auf {@link #key()} und {@link #owner()}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BEXBaseNode implements BEXNode {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.key() ^ this.owner().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (this == object) return true;
			if (!(object instanceof BEXNode)) return false;
			final BEXNode data = (BEXNode)object;
			return (this.key() == data.key()) && this.owner().equals(data.owner());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.key(), this.type(), this.index(), this.uri(), this.name(), this.value(), this.children().length(), this
				.attributes().length());
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link BEXList}.<br>
	 * Die Mathoden {@link #hashCode()} und {@link #equals(Object)} basieren auf {@link #key()} und {@link #owner()}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BEXBaseList implements BEXList {

		/**
		 * {@inheritDoc}
		 */
		@Override
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<BEXNode> iterator() {
			return new GetIterator<>(this, this.length());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.key() ^ this.owner().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (this == object) return true;
			if (!(object instanceof BEXList)) return false;
			final BEXList data = (BEXList)object;
			return (this.key() == data.key()) && this.owner().equals(data.owner());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this);
		}

	}

	{}

	// Typkennung für den undefinierten Knoten bzw. die undefinierte Knotenliste.
	static final int BEX_VOID_TYPE = 0;

	// Typkennung für einen Attributknoten.
	static final int BEX_ATTR_NODE = 1;

	// Typkennung für einen Elementknoten.
	static final int BEX_ELEM_NODE = 2;

	// Typkennung für einen Textknoten.
	static final int BEX_TEXT_NODE = 3;

	// Typkennung für den Textknoten eines Elementknoten.
	static final int BEX_TEXTELEM_NODE = 4;

	// Typkennung für eine Attributknotenliste.
	static final int BEX_ATTR_LIST = 5;

	// Typkennung für eine Kindknotenliste.
	static final int BEX_CHLD_LIST = 6;

	// Typkennung für die Kindknotenliste dem Textknoten eines Elementknoten.
	static final int BEX_CHLDTEXT_LIST = 7;

	{}

	/**
	 * Dieses Feld speichert das {@code UTF8}-{@link Charset} für {@link #toBytes(String)} und {@link #toString(byte[])}.
	 */
	public static final Charset CHARSET = Charset.forName("UTF8");

	{}

//	/**
//	 * Diese Methode wandelt die gegebene Zeichenkette via {@link #toBytes(String)} und {@link IAM#toArray(byte[])} eine Zahlenfolge um und gibt diese zurück.
//	 * 
//	 * @see #toBytes(String)
//	 * @see IAM#toArray(byte[])
//	 * @param value Zeichenkette.
//	 * @return Zahlenfolge.
//	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
//	 */
//	static IAMArray toArray(final String value) {
//		return IAM.toArray(BEX.toBytes(value));
//	}

	/**
	 * Diese Methode kodiert die gegebene Zeichenkette mit dem {@link #CHARSET} in eine Bytefolge und gibt diese zurück.
	 * 
	 * @see String#getBytes(Charset)
	 * @param value Zeichenkette.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static byte[] toBytes(final String value) throws NullPointerException {
		return value.getBytes(BEX.CHARSET);
	}

	/**
	 * Diese Methode dekodiert die gegebene Bytefolge mit dem {@link #CHARSET} in eine Zeichenkette und gibt diese zurück.
	 * 
	 * @see String#String(byte[], Charset)
	 * @param value Bytefolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static String toString(final byte[] value) {
		return new String(value, BEX.CHARSET);
	}

	/**
	 * Diese Methode wandelt die gegebene Zahlenfolge via {@link MMFArray#toBytes()} und {@link #toString(byte[])} eine Zeichenkette um und gibt diese zurück.
	 * 
	 * @see #toString(byte[])
	 * @see MMFArray#toBytes()
	 * @param value Zahlenfolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static String toString(final MMFArray value) {
		return BEX.toString(value.toBytes());
	}

	final static int key(final int type, final int index) {
		return (index << 3) | (type << 0);
	}

	final static int type(final int key) {
		return (key >> 0) & 7;
	}

	final static int index(final int key) {
		return (key >> 3) & 0x1FFFFFFF;
	}

}
