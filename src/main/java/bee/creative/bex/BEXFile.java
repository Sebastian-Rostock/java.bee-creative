package bee.creative.bex;

import java.io.IOException;
import bee.creative.bex.BEXLoader.BEXFileLoader;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMException;
import bee.creative.iam.IAMIndex;
import bee.creative.util.Objects;

/** Diese Klasse implementiert die Verwaltung aller Element-, Text- und Attributknoten sowie aller Kind- und Attributknotenlisten, die in einem XML Dokument
 * enthalten sind.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class BEXFile {

	/** Dieses Feld speichert das leere {@link BEXFile}. */
	public static final BEXFile EMPTY = new BEXFile();

	{}

	/** Diese Methode erzeugt aus dem gegebenen Objekt ein {@link BEXFile} und gibt dieses zurück.<br>
	 * Wenn das Objekt ein {@link BEXFile} ist, wird dieses geliefert. Andernfalls wird {@code new BEXFileLoader(IAMIndex.from(object))} geliefert.
	 *
	 * @see IAMIndex#from(Object)
	 * @see BEXFileLoader#BEXFileLoader(IAMIndex)
	 * @param object Objekt.
	 * @return {@link BEXFile}.
	 * @throws IOException Wenn {@link IAMIndex#from(Object)} eine entsprechende Ausnahme auslöst.
	 * @throws IAMException Wenn {@link IAMIndex#from(Object)} bzw. {@link BEXFileLoader#BEXFileLoader(IAMIndex)} eine entsprechende Ausnahme auslöst. */
	public static BEXFile from(final Object object) throws IOException, IAMException {
		if (object instanceof BEXFile) return (BEXFile)object;
		return new BEXFileLoader(IAMIndex.from(object));
	}

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

	/** Diese Methode ist eine Abkürzung für {@code IAMArray.from(BEXFile.valueFrom(string))}.
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
	public BEXNode root() {
		return BEXNode.EMPTY;
	}

	/** Diese Methode gibt die Knotenliste mit dem gegebenen Identifikator zurück. Wenn der Identifikator unbekannt ist, wird eine undefinierte Knotenliste
	 * geliefert. Der gegebene Identifikator kann von dem der gelieferten Knotenliste abweichen.
	 *
	 * @see BEXList#VOID_LIST
	 * @param key Identifikator.
	 * @return Knotenliste. */
	public BEXList list(final int key) {
		return BEXList.EMPTY;
	}

	/** Diese Methode gibt den Knoten mit dem gegebenen Identifikator zurück. Wenn der Identifikator unbekannt ist, wird ein undefinierter Knoten geliefert. Der
	 * gegebene Identifikator kann von dem des gelieferten Knoten abweichen.
	 *
	 * @see BEXNode#VOID_NODE
	 * @param key Identifikator.
	 * @return Knoten. */
	public BEXNode node(final int key) {
		return BEXNode.EMPTY;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.root());
	}

}
