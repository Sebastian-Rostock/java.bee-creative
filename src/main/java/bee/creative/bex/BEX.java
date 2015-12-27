package bee.creative.bex;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import bee.creative.bex.BEXDecoder.BEXFileDecoder;
import bee.creative.bex.BEXEncoder.BEXFileEncoder;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMException;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Iterators;
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
			return Objects.toInvokeString(this, this.root());
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
			return Iterators.itemsIterator(this, 0, this.length());
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
			return Objects.toInvokeString(this, this);
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
			return Objects.toInvokeString(this, this.key(), this.type(), this.index(), //
				this.uri(), this.name(), this.value(), this.children().length(), this.attributes().length());
		}

	}

	{}

	/**
	 * Dieses Feld speichert das {@code UTF8}-{@link Charset} zur kodierung und dekodierung von Zeichenketten.
	 */
	public static final Charset CHARSET = Charset.forName("UTF8");

	{}

	/**
	 * Diese Methode kodiert die gegebene Zeichenkette via {@link String#getBytes(Charset)} in eine Bytefolge, wandelt diese in eine nullterminierte Zahlenfolge
	 * um gibt diese zurück.
	 * 
	 * @param value Zeichenkette.
	 * @return Zahlenfolge.
	 */
	public static final int[] toItem(final String value) {
		final byte[] bytes = value.getBytes(BEX.CHARSET);
		final int length = bytes.length;
		final int[] result = new int[length + 1];
		for (int i = 0; i < length; i++) {
			result[i] = bytes[i];
		}
		return result;
	}

	/**
	 * Diese Methode kodiert die gegebene Zeichenkette via {@link String#getBytes(Charset)} in eine Bytefolge, wandelt diese in eine nullterminierte Zahlenfolge
	 * um gibt diese zurück.
	 * 
	 * @see IAMArray#from(byte[])
	 * @param value Zeichenkette.
	 * @return Zahlenfolge.
	 */
	public static final IAMArray toArray(final String value) {
		final byte[] bytes = value.getBytes(BEX.CHARSET);
		return IAMArray.from(Arrays.copyOf(bytes, bytes.length + 1));
	}

	/**
	 * Diese Methode wandelt die gegebene nullterminierte Zahlenfolge mit dem {@link #CHARSET} in eine Zeichenkette um und gibt diese zurück.
	 * 
	 * @see String#String(byte[], Charset)
	 * @see MMFArray#toBytes()
	 * @param value Zahlenfolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static final String toString(final MMFArray value) {
		return new String(value.section(0, value.length() - 1).toBytes(), BEX.CHARSET);
	}

	/**
	 * Diese Methode gibt einen neuen {@link BEXFileEncoder} zurück.
	 * 
	 * @see BEXFileEncoder#BEXFileEncoder()
	 * @return neuer {@link BEXFileEncoder}.
	 */
	public static final BEXFileEncoder encoder() {
		return new BEXFileEncoder();
	}

	/**
	 * Diese Methode gibt einen neuen {@link BEXFileDecoder} zurück.
	 * 
	 * @param array Speicherbereich mit {@code INT32} Zahlen.
	 * @return neuer {@link BEXFileDecoder}.
	 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 */
	public static final BEXFileDecoder decoder(final MMFArray array) throws IAMException, NullPointerException {
		return new BEXFileDecoder(array);
	}

}
