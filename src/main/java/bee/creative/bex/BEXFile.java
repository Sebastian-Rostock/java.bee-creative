package bee.creative.bex;

import bee.creative.iam.IAMArray;
import bee.creative.util.Objects;

/** Diese Schnittstelle definiert die Verwaltung aller Element-, Text- und Attributknoten sowie aller Kind- und Attributknotenlisten, die in einem Dokument
 * (vgl. {@code XML} Datei) enthalten sind.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class BEXFile {

	@SuppressWarnings ("javadoc")
	static final class EmptyFile extends BEXFile {

		@Override
		public final BEXNode root() {
			return BEXNode.EMPTY;
		}

		@Override
		public final BEXList list(final int key) {
			return BEXList.EMPTY;
		}

		@Override
		public final BEXNode node(final int key) {
			return BEXNode.EMPTY;
		}

	}

	{}

	/** Dieses Feld speichert das leere {@link BEXFile}. */
	public static final BEXFile EMPTY = new EmptyFile();

	{}

	/** Diese Methode wandelt die gegebene Zeichenkette in eine nullterminierte Folge von UTF-16-Token um und gibt diese als Zahlenfolge zurück.<br>
	 * Die gelieferte Zahlenfolge ist damit immer um eins länger, als {@link String#length()}.
	 *
	 * @param string Zeichenkette.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public static int[] valueFrom(final String string) throws NullPointerException {
		final int length = string.length();
		final int[] result = new int[length + 1];
		for (int i = 0; i < length; i++) {
			result[i] = (short)string.charAt(i);
		}
		return result;
	}

	/** Diese Methode ist eine Abkürzung für {@code IAMArray.from(BEX.toItem(string))}.
	 *
	 * @param string Zeichenkette.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public static IAMArray arrayFrom(final String string) throws NullPointerException {
		return IAMArray.from(BEXFile.valueFrom(string));
	}

	/** Diese Methode wandelt die gegebene nullterminierte Zahlenfolge in eine Zeichenkette um und gibt diese zurück.
	 *
	 * @param array Zahlenfolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code array} leer ist oder nicht mit {@code 0} endet. */
	public static String stringFrom(final IAMArray array) throws NullPointerException, IllegalArgumentException {
		final int length = array.length() - 1;
		if ((length < 0) || (array.get(length) != 0)) throw new IllegalArgumentException();
		if (length == 0) return "";
		return new String(IAMArray.toChars(array.section(0, length)));
	}

	{}

	/** Diese Methode gibt das Wurzelelement des Dokuments zurück.
	 *
	 * @return Wurzelelement. */
	public abstract BEXNode root();

	/** Diese Methode gibt die Knotenliste mit dem gegebenen Identifikator zurück. Wenn der Identifikator unbekannt ist, wird eine undefinierte Knotenliste
	 * geliefert. Der gegebene Identifikator kann von dem der gelieferten Knotenliste abweichen.
	 *
	 * @see BEXList#VOID_LIST
	 * @param key Identifikator.
	 * @return Knotenliste. */
	public abstract BEXList list(int key);

	/** Diese Methode gibt den Knoten mit dem gegebenen Identifikator zurück. Wenn der Identifikator unbekannt ist, wird ein undefinierter Knoten geliefert. Der
	 * gegebene Identifikator kann von dem des gelieferten Knoten abweichen.
	 *
	 * @see BEXNode#VOID_NODE
	 * @param key Identifikator.
	 * @return Knoten. */
	public abstract BEXNode node(int key);

	{}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.root());
	}

}
