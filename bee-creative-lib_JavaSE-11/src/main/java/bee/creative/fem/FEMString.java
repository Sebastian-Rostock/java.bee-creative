package bee.creative.fem;

import java.nio.charset.StandardCharsets;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.UseToString;
import bee.creative.util.AbstractIterator;
import bee.creative.util.Comparators;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert eine unveränderliche Zeichenkette sowie Methoden zur Erzeugung solcher Zeichenketten.
 * <p>
 * Die {@link #length() Länge} einer Zeichenkette nennt anders als bei {@link String} stets die Anzahl ihrer Zeichen, d.h. der Unicode Codepoints. Abhängig von
 * der internen Kodierung können je Codepoint ein bis vier Byte Speicher belegt werden. Hierbei werden sowohl Einzel- als auch Mehrwertkodierungen unterstützt.
 * </p>
 * <h5>INT - Einzelwertkodierungen</h5>
 * <p>
 * Einzelwertkodierungen speichern einen Codepoint in genau einem {@code byte}-, {@code char}- bzw. {@code int}-Wert. Sie erlauben den effizientesten
 * {@link #get(int) Zugriff} auf die Codepoint der Zeichenkette. Eine {@link #compactINT8() 8-Bit} bzw. {@link #compactINT16() 16-Bit} einzelwertkodierte
 * Zeichenkette kann jedoch nur Codepoints bis {@code 255} bzw. {@code 65535} darstellen.
 * </p>
 * <h5>UTF - Mehrwertkodierungen</h5>
 * <p>
 * Mehrwertkodierungen speichern einen Codepoint in mindestens einem {@code byte}- bzw. {@code char}-Wert. Sie schränken den Wertebereich der Codepoints bei
 * geringerem Speicherverbrauch nicht ein, erlauben wegen dieser Kompression jedoch nur einen langsamen {@link #get(int) Zugriff} auf die Codepoints.
 * </p>
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMString implements FEMValue, Iterable<Integer>, Comparable<FEMString>, UseToString {

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 4;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMString> TYPE = FEMType.from(FEMString.ID);

	/** Dieses Feld speichert die leere Zeichenkette. */
	public static final FEMString EMPTY = new UniformString(0, 0);

	/** Diese Methode gibt eine uniforme Zeichenkette mit der gegebenen Länge zurück, deren Codepoints alle gleich dem gegebenen sind.
	 *
	 * @param length Länge.
	 * @param item Codepoint.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	public static FEMString from(int length, int item) throws IllegalArgumentException {
		if (length == 0) return FEMString.EMPTY;
		return new UniformString(length, item);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, int[], int, int) FEMString.from(true, items, 0, items.length)}.
	 *
	 * @param items Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static FEMString from(int[] items) throws NullPointerException {
		return FEMString.from(true, items, 0, items.length);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, int[], int, int) FEMString.from(true, items, offset, length)}.
	 *
	 * @param items Codepoints.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMString from(int[] items, int offset, int length) throws NullPointerException, IllegalArgumentException {
		return FEMString.from(true, items, offset, length);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, int[], int, int) FEMString.from(copy, items, 0, items.length)}.
	 *
	 * @param copy {@code true}, wenn das gegebene Array falls nötig kopiert werden soll.
	 * @param items Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static FEMString from(boolean copy, int[] items) throws NullPointerException {
		return FEMString.from(copy, items, 0, items.length);
	}

	/** Diese Methode gibt eine Zeichenkette mit den Codepoints im gegebenen Abschnitt zurück.
	 *
	 * @param copy {@code true}, wenn das gegebene Array falls nötig kopiert werden soll.
	 * @param items Codepoints.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMString from(boolean copy, int[] items, int offset, int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > items.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMString.EMPTY;
		if (length == 1) return new UniformString(1, items[offset]);
		if (!copy) return new CompactStringINT32(0, items, offset, length);
		var items2 = new int[length];
		System.arraycopy(items, offset, items2, 0, length);
		return new CompactStringINT32(0, items2, 0, length);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, boolean, byte[], int, int) FEMString.from(true, false, items, 0, items.length)}.
	 *
	 * @param items 8-Bit-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(byte[] items) throws NullPointerException, IllegalArgumentException {
		return FEMString.from(true, false, items, 0, items.length);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, boolean, byte[], int, int) FEMString.from(true, false, items, offset, length)}.
	 *
	 * @param items 8-Bit-kodierte Codepoints.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMString from(byte[] items, int offset, int length) throws NullPointerException, IllegalArgumentException {
		return FEMString.from(true, false, items, offset, length);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, boolean, byte[], int, int) FEMString.from(copy, false, items, 0, items.length)}.
	 *
	 * @param copy {@code true}, wenn das gegebene Array falls nötig kopiert werden soll.
	 * @param items 8-Bit-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMString from(boolean copy, byte[] items) throws NullPointerException, IllegalArgumentException {
		return FEMString.from(copy, false, items, 0, items.length);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, boolean, byte[], int, int) FEMString.from(copy, false, items, offset, length)}.
	 *
	 * @param copy {@code true}, wenn das gegebene Array falls nötig kopiert werden soll.
	 * @param items 8-Bit-kodierte Codepoints.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMString from(boolean copy, byte[] items, int offset, int length) throws NullPointerException, IllegalArgumentException {
		return FEMString.from(copy, false, items, offset, length);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, boolean, byte[], int, int) FEMString.from(copy, asUTF8, items, 0, items.length)}.
	 *
	 * @param copy {@code true}, wenn das gegebene Array falls nötig kopiert werden soll.
	 * @param asUTF8 {@code true}, wenn die Codepoints mehrwertkodiert sind. {@code false}, wenn die Codepoints einzelwertkodiert sind.
	 * @param items 8-Bit-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMString from(boolean copy, boolean asUTF8, byte[] items) {
		return FEMString.from(copy, asUTF8, items, 0, items.length);
	}

	/** Diese Methode gibt eine Zeichenkette mit den 8-Bit-kodierten Codepoints im gegebenen Abschnitt zurück.
	 *
	 * @param copy {@code true}, wenn das gegebene Array falls nötig kopiert werden soll.
	 * @param asUTF8 {@code true}, wenn die Codepoints mehrwertkodiert sind. {@code false}, wenn die Codepoints einzelwertkodiert sind.
	 * @param items 8-Bit-kodierte Codepoints.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt bzw. die Kodierung ungültig ist. */
	public static FEMString from(boolean copy, boolean asUTF8, byte[] items, int offset, int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > items.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMString.EMPTY;
		if (asUTF8) {
			var count = FEMString.utf8Count(items, offset, length);
			if (count == 1) return new UniformString(1, FEMString.utf8Value(items, offset));
			if (!copy) return new CompactStringUTF8(0, items, offset, count);
			var items2 = new byte[length];
			System.arraycopy(items, offset, items2, 0, length);
			return new CompactStringUTF8(0, items2, 0, count);
		} else {
			if (length == 1) return new UniformString(1, items[offset] & 255);
			if (!copy) return new CompactStringINT8(0, items, offset, length);
			var items2 = new byte[length];
			System.arraycopy(items, offset, items2, 0, length);
			return new CompactStringINT8(0, items2, 0, length);
		}
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, short[], int, int) FEMString.from(true, items, 0, items.length)}.
	 *
	 * @param items 16-Bit-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(short[] items) throws NullPointerException, IllegalArgumentException {
		return FEMString.from(true, items, 0, items.length);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, short[], int, int) FEMString.from(true, items, offset, length)}. *
	 *
	 * @param items 16-Bit-kodierte Codepoints.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMString from(short[] items, int offset, int length) throws NullPointerException, IllegalArgumentException {
		return FEMString.from(true, items, offset, length);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, short[], int, int) FEMString.from(copy, items, 0, items.length)}.
	 *
	 * @param copy {@code true}, wenn das gegebene Array falls nötig kopiert werden soll.
	 * @param items 16-Bit-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMString from(boolean copy, short[] items) throws NullPointerException, IllegalArgumentException {
		return FEMString.from(copy, items, 0, items.length);
	}

	/** Diese Methode gibt eine Zeichenkette mit den 16-Bit-kodierten Codepoints im gegebenen Abschnitt zurück.
	 *
	 * @param copy {@code true}, wenn das gegebene Array falls nötig kopiert werden soll.
	 * @param items 16-Bit-kodierte Codepoints.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt bzw. die Kodierung ungültig ist. */
	public static FEMString from(boolean copy, short[] items, int offset, int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > items.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMString.EMPTY;
		if (length == 1) return new UniformString(1, items[offset] & 65535);
		if (!copy) return new CompactStringINT16(0, items, offset, length);
		var items2 = new short[length];
		System.arraycopy(items, offset, items2, 0, length);
		return new CompactStringINT16(0, items2, 0, length);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, char[]) FEMString.from(false, string.toCharArray())}.
	 *
	 * @param string Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public static FEMString from(String string) throws NullPointerException {
		if (string.isEmpty()) return FEMString.EMPTY;
		return FEMString.from(false, string.toCharArray());
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, char[], int, int) FEMString.from(true, items, 0, items.length)}.
	 *
	 * @param items UTF16-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(char[] items) throws NullPointerException, IllegalArgumentException {
		return FEMString.from(true, items, 0, items.length);
	}

	/** Diese Methode gibt eine Zeichenkette mit den UTF16-kodierten Codepoints im gegebenen Abschnitt zurück.
	 *
	 * @param items UTF16-kodierte Codepoints.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMString from(char[] items, int offset, int length) throws NullPointerException, IllegalArgumentException {
		return FEMString.from(true, items, offset, length);
	}

	public static FEMString from(boolean copy, char[] items) throws NullPointerException, IllegalArgumentException {
		return FEMString.from(copy, items, 0, items.length);
	}

	public static FEMString from(boolean copy, char[] items, int offset, int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > items.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMString.EMPTY;
		var count = FEMString.utf16Count(items, offset, length);
		if (count == 1) return new UniformString(1, FEMString.utf16Value(items, offset));
		if (!copy) return new CompactStringUTF16(0, items, offset, count);
		var items2 = new char[length];
		System.arraycopy(items, offset, items2, 0, length);
		return new CompactStringUTF16(0, items2, 0, count);
	}

	/** Diese Methode konvertiert die gegebenen Codepoints in eine Zeichenkette und gibt diese zurück.
	 *
	 * @see #from(int[])
	 * @see Number#intValue()
	 * @param items Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält. */
	public static FEMString from(List<? extends Number> items) throws NullPointerException {
		final var length = items.size();
		if (length == 0) return FEMString.EMPTY;
		if (length == 1) return new UniformString(1, items.get(0).intValue());
		var items2 = new int[length];
		for (var i = 0; i < length; i++) {
			items2[i] = items.get(i).intValue();
		}
		return FEMString.from(false, items2);
	}

	/** Diese Methode konvertiert die gegebenen Codepoints in eine Zeichenkette und gibt diese zurück.
	 *
	 * @see #from(List)
	 * @see Iterables#toList(Iterable)
	 * @param items Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält. */
	public static FEMString from(Iterable<? extends Number> items) throws NullPointerException {
		if (items instanceof FEMString) return (FEMString)items;
		return FEMString.from(Iterables.toList(items));
	}

	/** Diese Methode gibt die Verkettung der gegebenen Zeichenketten zurück.
	 *
	 * @see #concat(FEMString)
	 * @param values Zeichenketten.
	 * @return Verkettung der Zeichenketten.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält. */
	public static FEMString concatAll(FEMString... values) throws NullPointerException {
		var length = values.length;
		if (length == 0) return FEMString.EMPTY;
		if (length == 1) return values[0].data();
		return FEMString.concatAll(values, 0, length - 1);
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMString data() {
		return this;
	}

	@Override
	public final FEMType<FEMString> type() {
		return FEMString.TYPE;
	}

	/** Diese Methode gibt die Codepoint zurück.
	 *
	 * @see #toInts()
	 * @return Array mit den Codepoints. */
	public final int[] value() {
		return this.toInts();
	}

	/** Diese Methode gibt das {@code index}-te Zeichen als Codepoint zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-tes Zeichen.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist. */
	public final int get(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
		return this.customGet(index);
	}

	/** Diese Methode gibt die Länge, d.h. die Anzahl der Zeichen in der Zeichenkette zurück.
	 *
	 * @return Länge der Zeichenkette. */
	public final int length() {
		return this.length;
	}

	/** Diese Methode gibt eine Sicht auf die Verkettung dieser Zeichenkette mit der gegebenen Zeichenkette zurück.
	 *
	 * @param that Zeichenkette.
	 * @return {@link FEMString}-Sicht auf die Verkettung dieser mit der gegebenen Zeichenkette.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final FEMString concat(FEMString that) throws NullPointerException {
		if (that.length == 0) return this;
		if (this.length == 0) return that;
		return ConcatString.from(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #section(int, int) this.section(offset, this.length() - offset)}.
	 *
	 * @see #length() */
	public final FEMString section(int offset) throws IllegalArgumentException {
		return this.section(offset, this.length - offset);
	}

	/** Diese Methode gibt eine Sicht auf einen Abschnitt dieser Zeichenkette zurück.
	 *
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Bytes im Abschnitt.
	 * @return {@link FEMString}-Sicht auf einen Abschnitt dieser Zeichenkette.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Zeichenkette liegt oder eine negative Länge hätte. */
	public final FEMString section(int offset, int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this.length)) return this;
		if ((offset < 0) || ((offset + length) > this.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMString.EMPTY;
		return this.customSection(offset, length);
	}

	/** Diese Methode gibt eine rückwärts geordnete Sicht auf diese Zeichenkette zurück.
	 *
	 * @return rückwärts geordnete {@link FEMString}-Sicht auf diese Zeichenkette. */
	public FEMString reverse() {
		if (this.length < 2) return this;
		return new ReverseString(this);
	}

	/** Diese Methode gibt diese Zeichenkette mit optimierter Leistungsfähigkeit des {@link #get(int) Codepointzugriffs} zurück. Abhängig vom Wertebereich der
	 * Codepoints kann hierfür eine {@link #compactINT8() 8-Bit}-, {@link #compactINT16() 16-Bit}- oder {@link #compactINT32() 32-Bit}-Einzelwertkodierung zum
	 * Einsatz kommen.
	 *
	 * @return performantere Zeichenkette oder {@code this}. */
	public FEMString compact() {
		if (this.isEmpty()) return FEMString.EMPTY;
		if (this.isUniform()) return new UniformString(this.length, this.customGet(0));
		var collector = new GetRange();
		this.collect(collector);
		if (collector.range < 256) return this.compactINT8();
		if (collector.range < 65536) return this.compactINT16();
		return this.compactINT32();
	}

	/** Diese Methode gibt diese Zeichenkette mit 8-Bit Einzelwertkodierung zurück. Codepoints größer als {@code 255} werden bei dieser Kodierung zu {@code 0}.
	 *
	 * @see #from(boolean, boolean, byte[])
	 * @see #toBytes()
	 * @return Zeichenkette in 8-Bit-Einzelwertkodierung. */
	public FEMString compactINT8() {
		return FEMString.from(false, this.toBytes());
	}

	/** Diese Methode gibt diese Zeichenkette mit 16-Bit Einzelwertkodierung zurück. Codepoints größer als {@code 65535} werden bei dieser Kodierung zu {@code 0}.
	 *
	 * @see #from(boolean, short[])
	 * @see #toShorts()
	 * @return Zeichenkette in 16-Bit-Einzelwertkodierung. */
	public FEMString compactINT16() {
		return FEMString.from(false, this.toShorts());
	}

	/** Diese Methode gibt diese Zeichenkette mit 32-Bit Einzelwertkodierung zurück.
	 *
	 * @see #from(boolean, int[])
	 * @see #toInts()
	 * @return Zeichenkette in 32-Bit-Einzelwertkodierung. */
	public FEMString compactINT32() {
		return FEMString.from(false, this.toInts());
	}

	/** Diese Methode gibt die Position des ersten Vorkommens des gegebenen Zeichens innerhalb dieser Zeichenkette zurück. Die Suche beginnt an der gegebenen
	 * Position. Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 *
	 * @param that gesuchtes Zeichen.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens des gegebenen Zeichens ({@code offset..this.length()-1}) oder {@code -1}.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(int that, int offset) throws IllegalArgumentException {
		if (offset == this.length) return -1;
		if ((offset < 0) || (offset > this.length)) throw new IllegalArgumentException();
		if (offset == this.length) return -1;
		return this.customFind(that, offset, this.length - offset, true);
	}

	/** Diese Methode gibt die Position des ersten Vorkommens der gegebenen Zeichenkette innerhalb dieser Zeichenkette zurück. Die Suche beginnt an der gegebenen
	 * Position. Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 *
	 * @param that gesuchte Zeichenkette.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens der gegebene Zeichenkette ({@code offset..this.length()-that.length()}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(FEMString that, int offset) throws NullPointerException, IllegalArgumentException {
		if (that.length == 1) return this.find(that.customGet(0), offset);
		if ((offset < 0) || (offset > this.length)) throw new IllegalArgumentException();
		if (that.length == 0) return offset;
		if (that.length > (this.length - offset)) return -1;
		return this.customFind(that, offset);
	}

	/** Diese Methode fügt alle Codepoints dieser Zeichenkette vom ersten zum letzten geordnet an den gegebenen {@link Collector} an. Das Anfügen wird vorzeitig
	 * abgebrochen, wenn {@link Collector#push(int)} {@code false} liefert.
	 *
	 * @param target {@link Collector}, an den die Codepoints geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	public final boolean collect(Collector target) throws NullPointerException {
		return this.customCollect(Objects.notNull(target), 0, this.length, true);
	}

	@Override
	public final FEMString result(boolean deep) {
		return deep ? this.compact() : this;
	}

	@Override
	public int hashCode() {
		var hasher = new GetHash();
		this.collect(hasher);
		var result = hasher.hash;
		return result != 0 ? result : -1;
	}

	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMString)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMString)) return false;
		}
		return this.customEquals((FEMString)object);
	}

	@Override
	public final Iterator<Integer> iterator() {
		return new ItemIter();
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn die lexikographische Ordnung dieser Zeichenkette kleiner, gleich oder größer als die
	 * der gegebenen Zeichenkette ist.
	 *
	 * @param that Zeichenkette.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	@Override
	public int compareTo(FEMString that) throws NullPointerException {
		var length = Math.min(this.length, that.length);
		for (var i = 0; i < length; i++) {
			var result = Comparators.compare(this.customGet(i), that.customGet(i));
			if (result != 0) return result;
		}
		return Comparators.compare(this.length, that.length);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeichenkette leer ist.
	 *
	 * @return {@code true} bei Leerheit. */
	public final boolean isEmpty() {
		return this.length == 0;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeichenkette keine sich unterscheidenden Zeichen enthält.
	 *
	 * @return {@code true} bei Uniformität. */
	public boolean isUniform() {
		return this.isEmpty() || this.collect(new GetUniform(this.customGet(0)));
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die Kompaktierung aktiviert, d.h die Leistungsfähigkeit des {@link #get(int) Codepointzugriffs}
	 * optimiert ist.
	 *
	 * @return {@code true} bei Kompaktierung. */
	public boolean isCompacted() {
		return false;
	}

	/** Diese Methode gibt eine unveränderliche {@link List} als Sicht auf die Codepoints dieser Zeichenkette zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return {@link List}-Sicht. */
	public final List<Integer> toList() {
		return new ItemList();
	}

	/** Diese Methode gibt die 32-Bit-einzelwertkodierten Codepoint zurück.
	 *
	 * @return Array mit den Codepoints in 32-Bit-Kodierung. */
	public final int[] toInts() {
		var items = new int[this.length];
		this.toInts(items, 0);
		return items;
	}

	public void toInts(int[] items, int offset) throws IndexOutOfBoundsException {
		if ((offset < 0) || ((offset + this.length) > items.length)) throw new IllegalArgumentException();
		var encoder = new INT32Encoder(items, offset);
		this.collect(encoder);
	}

	/** Diese Methode gibt die 8-Bit-einzelwertkodierten Codepoint zurück. Codepoint größer als {@code 255} werden zu {@code 0}.
	 *
	 * @return Array mit den Codepoints in 8-Bit-Kodierung. */
	public final byte[] toBytes() {
		var items = new byte[this.length];
		this.toBytes(items, 0);
		return items;
	}

	public void toBytes(byte[] items, int offset) throws IndexOutOfBoundsException {
		if ((offset < 0) || ((offset + this.length) > items.length)) throw new IllegalArgumentException();
		var encoder = new INT8Encoder(items, offset);
		this.collect(encoder);
	}

	/** Diese Methode gibt die Codepoint in 8-Bit-Kodierung zurück.
	 *
	 * @param asUTF8 {@code true}, wenn die Codepoint in UTF8 kodiert werden sollen. {@code false}, wenn die Codepoint als Einzelwerte kodiert werden sollen
	 *        (Codepoint größer als {@code 255} werden zu {@code 0}).
	 * @return Array mit den Codepoints in 8-Bit-Kodierung. */
	public byte[] toBytes(boolean asUTF8) {
		if (!asUTF8) return this.toBytes();
		var counter = new UTF8Counter();
		this.collect(counter);
		var encoder = new UTF8Encoder(new byte[counter.count], 0);
		this.collect(encoder);
		return encoder.items;
	}

	/** Diese Methode gibt die 16-Bit-einzelwertkodierten Codepoint zurück. Codepoint größer als {@code 65535} werden zu {@code 0}.
	 *
	 * @return Array mit den Codepoints in 16-Bit-Kodierung. */
	public final short[] toShorts() {
		var items = new short[this.length];
		this.toShorts(items, 0);
		return items;
	}

	public void toShorts(short[] items, int offset) throws IndexOutOfBoundsException {
		if ((offset < 0) || ((offset + this.length) > items.length)) throw new IllegalArgumentException();
		var encoder = new INT16Encoder(items, offset);
		this.collect(encoder);
	}

	/** Diese Methode gibt die Codepoint in UTF16-Kodierung zurück.
	 *
	 * @return Array mit den Codepoints in UTF16-Kodierung. */
	public char[] toChars() {
		var counter = new UTF16Counter();
		this.collect(counter);
		var encoder = new UTF16Encoder(new char[counter.count], 0);
		this.collect(encoder);
		return encoder.items;
	}

	/** Diese Methode gibt diesen Zeichenkette als {@link String} zurück.
	 *
	 * @return {@link String}. */
	@Override
	public String toString() {
		return new String(this.toChars());
	}

	/** Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Codepoints einer Zeichenkette in der Methode {@link FEMString#collect(Collector)}. */
	public static interface Collector {

		/** Diese Methode fügt den gegebenen Codepoint an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammeln fortgeführt werden soll.
		 *
		 * @param value Codepoints.
		 * @return {@code true}, wenn das Sammlen fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll. */
		public boolean push(int value);

	}

	/** Diese Klasse implementiert ein abstrakte {@link FEMString Zeichenkette} mit {@link #hash Streuwertpuffer}.
	 *
	 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static abstract class HashString extends FEMString {

		@Override
		public int hashCode() {
			var result = this.hash;
			if (result != 0) return result;
			return this.hash = super.hashCode();
		}

		/** Dieses Feld speichert den Streuwert oder {@code 0}. Es wird in {@link #hashCode()} initialisiert. */
		protected int hash;

		/** Dieser Konstruktor initialisiert die Länge.
		 *
		 * @param length Länge.
		 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
		protected HashString(int length) throws IllegalArgumentException {
			super(length);
		}

	}

	public static class ConcatString extends HashString implements Emuable {

		public static ConcatString from(FEMString string1, FEMString string2) throws IllegalArgumentException {
			var size1 = ConcatString.size(string1);
			var size2 = ConcatString.size(string2);
			if ((size1 + 1) < size2) {
				var cs2 = (ConcatString)string2;
				if (!(cs2 instanceof ConcatString1)) return ConcatString.from(ConcatString.from(string1, cs2.string1), cs2.string2);
				var cs21 = (ConcatString)cs2.string1;
				return ConcatString.from(ConcatString.from(string1, cs21.string1), ConcatString.from(cs21.string2, cs2.string2));
			}
			if ((size2 + 1) < size1) {
				var cs1 = (ConcatString)string1;
				if (!(cs1 instanceof ConcatString2)) return ConcatString.from(cs1.string1, ConcatString.from(cs1.string2, string2));
				var cs12 = (ConcatString)cs1.string2;
				return ConcatString.from(ConcatString.from(cs1.string1, cs12.string1), ConcatString.from(cs12.string2, string2));
			}
			if (size1 > size2) return new ConcatString1(string1, string2);
			if (size1 < size2) return new ConcatString2(string1, string2);
			return new ConcatString(string1, string2);
		}

		public final FEMString string1;

		public final FEMString string2;

		public ConcatString(FEMString string1, FEMString string2) throws IllegalArgumentException {
			super(string1.length + string2.length);
			this.string1 = string1;
			this.string2 = string2;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.string1) + EMU.from(this.string2);
		}

		@Override
		protected int customGet(int index) throws IndexOutOfBoundsException {
			var index2 = index - this.string1.length;
			return index2 < 0 ? this.string1.customGet(index) : this.string2.customGet(index2);
		}

		@Override
		protected boolean customCollect(Collector target, int offset, int length, boolean foreward) {
			var offset2 = offset - this.string1.length;
			if (offset2 >= 0) return this.string2.customCollect(target, offset2, length, foreward);
			var length2 = offset2 + length;
			if (length2 <= 0) return this.string1.customCollect(target, offset, length, foreward);
			if (foreward) {
				if (!this.string1.customCollect(target, offset, -offset2, foreward)) return false;
				return this.string2.customCollect(target, 0, length2, foreward);
			} else {
				if (!this.string2.customCollect(target, 0, length2, foreward)) return false;
				return this.string1.customCollect(target, offset, -offset2, foreward);
			}
		}

		@Override
		protected FEMString customSection(int offset, int length) throws IllegalArgumentException {
			var offset2 = offset - this.string1.length;
			if (offset2 >= 0) return this.string2.section(offset2, length);
			var length2 = offset2 + length;
			if (length2 <= 0) return this.string1.section(offset, length);
			return this.string1.section(offset, -offset2).concat(this.string2.section(0, length2));
		}

		private static int size(FEMString string) {
			for (var size = 0; true; size++) {
				if (string instanceof ConcatString2) {
					string = ((ConcatString)string).string2;
				} else if (string instanceof ConcatString) {
					string = ((ConcatString)string).string1;
				} else return size;
			}
		}

	}

	public static final class ConcatString1 extends ConcatString {

		public ConcatString1(FEMString string1, FEMString string2) throws IllegalArgumentException {
			super(string1, string2);
		}

	}

	public static final class ConcatString2 extends ConcatString {

		public ConcatString2(FEMString string1, FEMString string2) throws IllegalArgumentException {
			super(string1, string2);
		}

	}

	public static final class SectionString extends HashString implements Emuable {

		public final FEMString string;

		public final int offset;

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.string);
		}

		public SectionString(FEMString string, int offset, int length) throws IllegalArgumentException {
			super(length);
			this.string = string;
			this.offset = offset;
		}

		@Override
		protected int customGet(int index) throws IndexOutOfBoundsException {
			return this.string.customGet(index + this.offset);
		}

		@Override
		protected int customFind(int that, int offset, int length, boolean foreward) {
			var result = this.string.customFind(that, offset + this.offset, length, foreward);
			return result >= 0 ? result - this.offset : -1;
		}

		@Override
		protected boolean customCollect(Collector target, int offset2, int length2, boolean foreward) {
			return this.string.customCollect(target, this.offset + offset2, length2, foreward);
		}

		@Override
		protected FEMString customSection(int offset2, int length2) throws IllegalArgumentException {
			return this.string.section(this.offset + offset2, length2);
		}

	}

	public static final class ReverseString extends HashString implements Emuable {

		public final FEMString string;

		public ReverseString(FEMString string) throws IllegalArgumentException {
			super(string.length);
			this.string = string;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.string);
		}

		@Override
		public FEMString reverse() {
			return this.string;
		}

		@Override
		public boolean isUniform() {
			return this.string.isUniform();
		}

		@Override
		protected int customGet(int index) throws IndexOutOfBoundsException {
			return this.string.customGet(this.length - index - 1);
		}

		@Override
		protected int customFind(int that, int offset, int length, boolean foreward) {
			var result = this.string.customFind(that, this.length - offset - length, length, !foreward);
			return result >= 0 ? this.length - result - 1 : -1;
		}

		@Override
		protected boolean customCollect(Collector target, int offset, int length, boolean foreward) {
			return this.string.customCollect(target, this.length - offset - length, length, !foreward);
		}

		@Override
		protected FEMString customSection(int offset2, int length2) throws IllegalArgumentException {
			return this.string.section(this.length - offset2 - length2, length2).reverse();
		}

	}

	public static final class UniformString extends HashString {

		public final int item;

		public UniformString(int length, int value) throws IllegalArgumentException {
			super(length);
			this.item = value;
		}

		@Override
		public FEMString reverse() {
			return this;
		}

		@Override
		public FEMString compact() {
			return this;
		}

		@Override
		public boolean isUniform() {
			return true;
		}

		@Override
		public boolean isCompacted() {
			return true;
		}

		@Override
		protected int customGet(int index) throws IndexOutOfBoundsException {
			return this.item;
		}

		@Override
		protected int customFind(int that, int offset, int length, boolean foreward) {
			return this.item == that ? offset : -1;
		}

		@Override
		protected boolean customCollect(Collector target, int offset, int length, boolean foreward) {
			while (length > 0) {
				if (!target.push(this.item)) return false;
				length--;
			}
			return true;
		}

	}

	public static final class CompactStringINT8 extends HashString implements Emuable {

		public CompactStringINT8(int hash, byte[] items, int offset, int length) throws IllegalArgumentException {
			super(length);
			this.items = items;
			this.offset = offset;
			this.hash = hash;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + ((this.offset == 0) && (this.length == this.items.length) ? EMU.fromArray(this.items) : 0);
		}

		@Override
		public FEMString compactINT8() {
			return this;
		}

		@Override
		public boolean isCompacted() {
			return true;
		}

		@Override
		public void toBytes(byte[] items, int offset) throws IndexOutOfBoundsException {
			System.arraycopy(this.items, this.offset, items, offset, this.length);
		}

		@Override
		protected int customGet(int index) throws IndexOutOfBoundsException {
			return this.items[this.offset + index] & 255;
		}

		@Override
		protected FEMString customSection(int offset, int length) {
			return new CompactStringINT8(0, this.items, this.offset + offset, length);
		}

		private final byte[] items;

		private final int offset;

	}

	public static final class CompactStringINT16 extends HashString implements Emuable {

		public CompactStringINT16(int hash, short[] items, int offset, int length) throws IllegalArgumentException {
			super(length);
			this.items = items;
			this.offset = offset;
			this.hash = hash;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + ((this.offset == 0) && (this.length == this.items.length) ? EMU.fromArray(this.items) : 0);
		}

		@Override
		public FEMString compactINT16() {
			return this;
		}

		@Override
		public boolean isCompacted() {
			return true;
		}

		@Override
		public void toShorts(short[] items, int offset) throws IndexOutOfBoundsException {
			System.arraycopy(this.items, this.offset, items, offset, this.length);
		}

		@Override
		protected int customGet(int index) throws IndexOutOfBoundsException {
			return this.items[this.offset + index] & 65535;
		}

		@Override
		protected FEMString customSection(int offset, int length) {
			return new CompactStringINT16(0, this.items, this.offset + offset, length);
		}

		private final short[] items;

		private final int offset;

	}

	public static final class CompactStringINT32 extends HashString implements Emuable {

		public CompactStringINT32(int hash, int[] items, int offset, int length) throws IllegalArgumentException {
			super(length);
			this.items = items;
			this.offset = offset;
			this.hash = hash;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + ((this.offset == 0) && (this.length == this.items.length) ? EMU.fromArray(this.items) : 0);
		}

		@Override
		public FEMString compactINT32() {
			return this;
		}

		@Override
		public boolean isCompacted() {
			return true;
		}

		@Override
		public void toInts(int[] items, int offset) throws IndexOutOfBoundsException {
			System.arraycopy(this.items, this.offset, items, offset, this.length);
		}

		@Override
		public String toString() {
			return new String(this.items, this.offset, this.length);
		}

		@Override
		protected int customGet(int index) throws IndexOutOfBoundsException {
			return this.items[this.offset + index];
		}

		@Override
		protected FEMString customSection(int offset, int length) {
			return new CompactStringINT32(0, this.items, this.offset + offset, length);
		}

		private final int[] items;

		private final int offset;

	}

	public static final class CompactStringUTF8 extends HashString implements Emuable {

		public CompactStringUTF8(int hash, byte[] items, int offset, int length) throws IllegalArgumentException {
			super(length);
			this.items = items;
			this.offset = offset;
			this.hash = hash;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + ((this.offset == 0) && (this.length == this.items.length) ? EMU.fromArray(this.items) : 0);
		}

		@Override
		public byte[] toBytes(boolean asUTF8) {
			return asUTF8 ? this.items.clone() : this.toBytes();
		}

		@Override
		public String toString() {
			return new String(this.items, this.offset, this.length, StandardCharsets.UTF_8);
		}

		@Override
		protected int customGet(int index) throws IndexOutOfBoundsException {
			return FEMString.utf8Value(this.items, FEMString.utf8Offset(this.items, this.offset, index));
		}

		@Override
		protected boolean customCollect(Collector target, int offset, int length, boolean foreward) {
			if (foreward) {
				var offset2 = FEMString.utf8Offset(this.items, this.offset, offset);
				var length2 = length;
				while (length2 > 0) {
					if (!target.push(FEMString.utf8Value(this.items, offset2))) return false;
					length2--;
					offset2 += FEMString.utf8Size(this.items[offset2]);
				}
			} else {
				var offset2 = FEMString.utf8Offset(this.items, this.offset, offset + length);
				var length2 = length;
				while (length2 > 0) {
					while (!FEMString.utf8Header(this.items[--offset2])) {
						if (!target.push(FEMString.utf8Value(this.items, offset2))) return false;
					}
					length2--;
				}
			}
			return true;
		}

		@Override
		protected FEMString customSection(int offset, int length) {
			return new CompactStringUTF8(0, this.items, FEMString.utf8Offset(this.items, this.offset, offset), length);
		}

		private final byte[] items;

		private final int offset;

	}

	public static final class CompactStringUTF16 extends HashString implements Emuable {

		public CompactStringUTF16(int hash, char[] items, int offset, int length) throws IllegalArgumentException {
			super(length);
			this.items = items;
			this.offset = offset;
			this.hash = hash;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + ((this.offset == 0) && (this.length == this.items.length) ? EMU.fromArray(this.items) : 0);
		}

		@Override
		public String toString() {
			return new String(this.items, this.offset, this.length);
		}

		@Override
		protected int customGet(int index) throws IndexOutOfBoundsException {
			return FEMString.utf16Value(this.items, FEMString.utf16Offset(this.items, this.offset, index));
		}

		@Override
		protected boolean customCollect(Collector target, int offset, int length, boolean foreward) {
			if (foreward) {
				var offset2 = FEMString.utf16Offset(this.items, this.offset, offset);
				var length2 = length;
				while (length2 > 0) {
					if (!target.push(FEMString.utf16Value(this.items, offset2))) return false;
					length2--;
					offset2 += FEMString.utf16Size(this.items[offset2]);
				}
			} else {
				var offset2 = FEMString.utf16Offset(this.items, this.offset, offset + length);
				var length2 = length;
				while (length2 > 0) {
					while (!FEMString.utf16Header(this.items[--offset2])) {
						if (!target.push(FEMString.utf16Value(this.items, offset2))) return false;
					}
					length2--;
				}
			}
			return true;
		}

		@Override
		protected FEMString customSection(int offset, int length) {
			return new CompactStringUTF16(0, this.items, FEMString.utf16Offset(this.items, this.offset, offset), length);
		}

		private final char[] items;

		private final int offset;

	}

	/** Dieses Feld speichert die Länge. */
	protected final int length;

	/** Dieser Konstruktor initialisiert die Länge.
	 *
	 * @param length Länge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	protected FEMString(int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException();
		this.length = length;
	}

	/** Diese Methode gibt den {@code index}-ten Codepoint zurück. Sie Implementiert {@link #get(int)} ohne Wertebereichsprüfung.
	 *
	 * @param index Index.
	 * @return {@code index}-ter Codepoint. */
	protected int customGet(int index) {
		return 0;
	}

	/** Diese Methode gibt die Position des ersten Vorkommens der gegebenen Zeichenkette innerhalb dieser Zeichenkette zurück. Sie Implementiert
	 * {@link #find(FEMString, int)} ohne Wertebereichsprüfung.
	 *
	 * @param that nicht leere gesuchte Zeichenkette.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens der gegebene Zeichenkette ({@code offset..this.length()-that.length()}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	protected int customFind(FEMString that, int offset) {
		var value = that.customGet(0);
		var count = (this.length - that.length) + 1;
		for (var result = offset; true; result++) {
			result = this.customFind(value, result, count - result, true);
			if (result < 0) return -1;
			if (this.customEquals(that, result)) return result;
		}
	}

	/** Diese Methode gibt die Position des ersten Vorkommens des gegebenen Zeichens im gegebenen Abschnitt zurück. Sie Implementiert {@link #find(int, int)} ohne
	 * Wertebereichsprüfung.
	 *
	 * @param that gesuchtes Zeichen.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Zeichen im Abschnitt.
	 * @return Position des ersten Vorkommens des gegebenen Zeichens oder {@code -1}.
	 * @param foreward {@code true}, wenn die Reihenfolge vorwärts ist, bzw. {@code false}, wenn sie rückwärts ist. */
	protected int customFind(int that, int offset, int length, boolean foreward) {
		var finder = new GetIndex(that);
		if (this.customCollect(finder, offset, length, foreward)) return -1;
		return foreward ? (finder.index + offset) : (length - finder.index - 1);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Bytefolge gleich der gegebenen ist. Sie Implementiert {@link #equals(Object)}. */
	protected boolean customEquals(FEMString that) throws NullPointerException {
		var length = this.length;
		if ((length != that.length) || (this.hashCode() != that.hashCode())) return false;
		return this.customEquals(that, 0);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebenen Zeichenkette an der gegebenen Position in dieser Zeichenkette liegt. */
	protected boolean customEquals(FEMString that, int offset) {
		var length = that.length;
		for (var i = 0; i < length; i++) {
			if (this.customGet(offset + i) != that.customGet(i)) return false;
		}
		return true;
	}

	/** Diese Methode fügt alle Codepoints im gegebenen Abschnitt in der gegebenen Reihenfolge geordnet an den gegebenen {@link Collector} an. Das Anfügen wird
	 * vorzeitig abgebrochen, wenn {@link Collector#push(int)} {@code false} liefert.
	 *
	 * @param target {@link Collector}, an den die Codepoints geordnet angefügt werden.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @param foreward {@code true}, wenn die Reihenfolge vorwärts ist, bzw. {@code false}, wenn sie rückwärts ist.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde. */
	protected boolean customCollect(Collector target, int offset, int length, boolean foreward) {
		if (foreward) {
			for (length += offset; offset < length; offset++) {
				if (!target.push(this.customGet(offset))) return false;
			}
		} else {
			for (length += offset - 1; offset <= length; length--) {
				if (!target.push(this.customGet(length))) return false;
			}
		}
		return true;
	}

	/** Diese Methode gibt eine Sicht auf einen Abschnitt dieser Zeichenkette zurück. Sie Implementiert {@link #section(int, int)} ohne Wertebereichsprüfung.
	 *
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Zeichen im Abschnitt.
	 * @return {@link FEMString}-Sicht auf einen Abschnitt dieser Zeichenkette. */
	protected FEMString customSection(int offset, int length) {
		return new SectionString(this, offset, length);
	}

	/** Diese Methode gibt die Anzahl der Token für den UTF8-kodierten Codepoint zurück, der mit dem gegebenen Token beginnt.
	 *
	 * @param item Token, mit dem ein UTF8-kodierter Codepoint beginnt.
	 * @return Anzahl der Token für den UTF8-kodierten Codepoint.
	 * @throws IllegalArgumentException Wenn der Token ungültig ist. */
	private static int utf8Size(int item) throws IllegalArgumentException {
		switch ((item >> 4) & 15) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				return 1;
			case 12:
			case 13:
				return 2;
			case 14:
				return 3;
			case 15:
				return 4;
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den UTF8-kodierten Codepoint zurück, der an der gegebenen Position beginnt.
	 *
	 * @param items Tokenliste.
	 * @param offset Position des Tokens, an dem der UTF8-kodierte Codepoint beginnt.
	 * @return Codepoint.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	private static int utf8Value(byte[] items, int offset) throws IllegalArgumentException {
		switch ((items[offset] >> 4) & 15) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				return items[offset] & 127;
			case 12:
			case 13:
				return ((items[offset] & 31) << 6) | (items[offset + 1] & 63);
			case 14:
				return ((items[offset] & 15) << 12) | ((items[offset + 1] & 63) << 6) | (items[offset + 2] & 63);
			case 15:
				return ((items[offset] & 7) << 18) | ((items[offset + 1] & 63) << 12) | ((items[offset + 2] & 63) << 6) | (items[offset + 3] & 63);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode verschiebt die gegebene Position um die gegebene Anzal an UTF8-kodierten Codepoint in der gegebenen Tokenliste und gibt sie zurück.
	 *
	 * @param items Tokenliste.
	 * @param offset Position des Tokens, an dem der erste UTF8-kodierte Codepoint beginnt.
	 * @param count Anzahl der Codepoints.
	 * @return Position des ersten Tokens nach der gegebenen Anzahl an Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	private static int utf8Offset(byte[] items, int offset, int count) throws IllegalArgumentException {
		while (count > 0) {
			count--;
			offset += FEMString.utf8Size(items[offset]);
		}
		return offset;
	}

	/** Diese Methode gibt die Anzahl an UTF8-kodierten Codepoints in der gegebenen Tokenliste zurück.
	 *
	 * @param items Tokenliste.
	 * @param offset Position des Tokens, an dem der erste UTF8-kodierte Codepoint beginnt.
	 * @param length Anzahl der Token.
	 * @return Anzahl an UTF8-kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	private static int utf8Count(byte[] items, int offset, int length) throws NullPointerException, IllegalArgumentException {
		int result = 0, offset2 = offset;
		var length2 = offset + length;
		while (offset2 < length2) {
			result++;
			offset2 += FEMString.utf8Size(items[offset2]);
		}
		if (offset2 != length2) throw new IllegalArgumentException();
		return result;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Token den Beginn eines UTF8-kodierten Codepoints anzeigt.
	 *
	 * @param item Token.
	 * @return {@code true}, wenn ein UTF8-kodierter Codepoint am Token beginnt. */
	private static boolean utf8Header(int item) {
		return (item & 192) != 128;
	}

	/** Diese Methode gibt die Anzahl der Token für den UTF16-kodierten Codepoint zurück, der mit dem gegebenen Token beginnt.
	 *
	 * @param item Token, mit dem ein UTF16-kodierter Codepoint beginnt.
	 * @return Anzahl der Token für den UTF16-kodierten Codepoint.
	 * @throws IllegalArgumentException Wenn der Token ungültig ist. */
	private static int utf16Size(int item) throws IllegalArgumentException {
		var value = item & 64512;
		if (value == 55296) return 2;
		if (value != 56320) return 1;
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den UTF16-kodierten Codepoint zurück, der an der gegebenen Position beginnt.
	 *
	 * @param items Tokenliste.
	 * @param offset Position des Tokens, an dem der UTF16-kodierte Codepoint beginnt.
	 * @return Codepoint.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	private static int utf16Value(char[] items, int offset) throws IllegalArgumentException {
		var token = items[offset];
		var value = token & 64512;
		if (value == 55296) return (((token & 1023) << 10) | (items[offset + 1] & 1023)) + 65536;
		if (value != 56320) return token & 65535;
		throw new IllegalArgumentException();
	}

	/** Diese Methode verschiebt die gegebene Position um die gegebene Anzal an UTF16-kodierten Codepoint in der gegebenen Tokenliste und gibt sie zurück.
	 *
	 * @param items Tokenliste.
	 * @param offset Position des Tokens, an dem der erste UTF16-kodierte Codepoint beginnt.
	 * @param count Anzahl der Codepoints.
	 * @return Position des ersten Tokens nach der gegebenen Anzahl an Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	private static int utf16Offset(char[] items, int offset, int count) throws IllegalArgumentException {
		while (count > 0) {
			count--;
			offset += FEMString.utf16Size(items[offset]);
		}
		return offset;
	}

	/** Diese Methode gibt die Anzahl an UTF16-kodierten Codepoints in der gegebenen Tokenliste zurück.
	 *
	 * @param items Tokenliste.
	 * @param offset Position des Tokens, an dem der erste UTF8-kodierte Codepoint beginnt.
	 * @param length Anzahl der Token.
	 * @return Anzahl an UTF16-kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	private static int utf16Count(char[] items, int offset, int length) throws NullPointerException, IllegalArgumentException {
		var result = 0;
		var offset2 = offset;
		var length2 = offset + length;
		while (offset2 < length2) {
			result++;
			offset2 += FEMString.utf16Size(items[offset2]);
		}
		if (offset2 != length2) throw new IllegalArgumentException();
		return result;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Token den Beginn eines UTF16-kodierten Codepoints anzeigt.
	 *
	 * @param item Token.
	 * @return {@code true}, wenn ein UTF16-kodierter Codepoint am Token beginnt. */
	private static boolean utf16Header(int item) {
		return (item & 64512) != 56320;
	}

	/** Diese Methode implementiert {@link #concatAll(FEMString...)}. */
	private static FEMString concatAll(FEMString[] values, int min, int max) throws NullPointerException {
		if (min == max) return values[min];
		var mid = (min + max) >> 1;
		return FEMString.concatAll(values, min, mid).concat(FEMString.concatAll(values, mid + 1, max));
	}

	private final class ItemIter extends AbstractIterator<Integer> {

		int index = 0;

		@Override
		public Integer next() {
			return FEMString.this.customGet(this.index++);
		}

		@Override
		public boolean hasNext() {
			return this.index < FEMString.this.length;
		}

	}

	private final class ItemList extends AbstractList<Integer> implements RandomAccess {

		@Override
		public Integer get(int index) {
			return FEMString.this.get(index);
		}

		@Override
		public int size() {
			return FEMString.this.length;
		}

		@Override
		public Iterator<Integer> iterator() {
			return FEMString.this.iterator();
		}

		@Override
		public boolean contains(Object o) {
			return this.indexOf(o) >= 0;
		}

		@Override
		public int indexOf(Object o) {
			if ((FEMString.this.length == 0) || !(o instanceof Integer)) return -1;
			return FEMString.this.customFind((Integer)o, 0, FEMString.this.length, true);
		}

		@Override
		public int lastIndexOf(Object o) {
			if ((FEMString.this.length == 0) || !(o instanceof Integer)) return -1;
			return FEMString.this.customFind((Integer)o, 0, FEMString.this.length, false);
		}

		@Override
		public List<Integer> subList(int fromIndex, int toIndex) {
			return FEMString.this.section(fromIndex, toIndex - fromIndex).toList();
		}

	}

	private static final class GetHash implements Collector {

		public int hash = Objects.hashInit();

		@Override
		public boolean push(int value) {
			this.hash = Objects.hashPush(this.hash, value);
			return true;
		}

	}

	private static final class GetIndex implements Collector {

		public final int value;

		public int index;

		public GetIndex(int value) {
			this.value = value;
		}

		@Override
		public boolean push(int value) {
			if (this.value == value) return false;
			this.index++;
			return true;
		}

	}

	private static final class GetRange implements Collector {

		public int range;

		@Override
		public boolean push(int value) {
			this.range |= value;
			return true;
		}

	}

	private static final class GetUniform implements Collector {

		public int value;

		public GetUniform(int value) {
			this.value = value;
		}

		@Override
		public boolean push(int value) {
			return this.value == value;
		}

	}

	private static final class INT8Encoder implements Collector {

		public final byte[] items;

		public int index;

		public INT8Encoder(byte[] array, int index) {
			this.items = array;
			this.index = index;
		}

		@Override
		public boolean push(int value) {
			if (value < 0) throw new IllegalArgumentException();
			this.items[this.index++] = value < 256 ? (byte)value : 0;
			return true;
		}

	}

	private static final class INT16Encoder implements Collector {

		public final short[] items;

		public int index;

		public INT16Encoder(short[] items, int index) {
			this.items = items;
			this.index = index;
		}

		@Override
		public boolean push(int value) {
			if (value < 0) throw new IllegalArgumentException();
			this.items[this.index++] = value < 65536 ? (short)value : 0;
			return true;
		}

	}

	private static final class INT32Encoder implements Collector {

		public final int[] items;

		public int index;

		public INT32Encoder(int[] items, int index) {
			this.items = items;
			this.index = index;
		}

		@Override
		public boolean push(int value) {
			if (value < 0) throw new IllegalArgumentException();
			this.items[this.index++] = value;
			return true;
		}

	}

	private static final class UTF8Counter implements Collector {

		public int count;

		@Override
		public boolean push(int value) {
			if (value < 0) throw new IllegalArgumentException();
			if (value < 128) {
				this.count += 1;
			} else if (value < 2048) {
				this.count += 2;
			} else if (value < 65536) {
				this.count += 3;
			} else {
				this.count += 4;
			}
			return true;
		}

	}

	private static final class UTF8Encoder implements Collector {

		public final byte[] items;

		public int index;

		public UTF8Encoder(byte[] items, int index) {
			this.items = items;
			this.index = index;
		}

		@Override
		public boolean push(int value) {
			if (value < 0) throw new IllegalArgumentException();
			var index = this.index;
			if (value < 128) {
				this.items[index++] = (byte)value;
			} else if (value < 2048) {
				this.items[index++] = (byte)(192 | (value >> 6));
				this.items[index++] = (byte)(128 | (value & 63));
			} else if (value < 65536) {
				this.items[index++] = (byte)(224 | (value >> 12));
				this.items[index++] = (byte)(128 | ((value >> 6) & 63));
				this.items[index++] = (byte)(128 | (value & 63));
			} else {
				this.items[index++] = (byte)(240 | (value >> 18));
				this.items[index++] = (byte)(128 | ((value >> 12) & 63));
				this.items[index++] = (byte)(128 | ((value >> 6) & 63));
				this.items[index++] = (byte)(128 | (value & 63));
			}
			this.index = index;
			return true;
		}

	}

	private static final class UTF16Counter implements Collector {

		public int count;

		@Override
		public boolean push(int value) {
			if (value < 0) throw new IllegalArgumentException();
			if (value < 65536) {
				this.count += 1;
			} else {
				this.count += 2;
			}
			return true;
		}

	}

	private static final class UTF16Encoder implements Collector {

		public final char[] items;

		public int index;

		public UTF16Encoder(char[] items, int index) {
			this.items = items;
			this.index = index;
		}

		@Override
		public boolean push(int value) {
			if (value < 0) throw new IllegalArgumentException();
			var value2 = value - 65536;
			var index = this.index;
			if (value2 < 0) {
				this.items[index++] = (char)value;
			} else {
				this.items[index++] = (char)(55296 | (value2 >> 10));
				this.items[index++] = (char)(56320 | (value2 & 1023));
			}
			this.index = index;
			return true;
		}

	}

}