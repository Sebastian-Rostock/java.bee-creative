package bee.creative.mmi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bee.creative.iam.IAMArray;
import bee.creative.io.IO;
import bee.creative.io.MappedBuffer;
import bee.creative.lang.Bytes;

/** Diese Klasse implementiert eine abstrakte {@link IAMArray Zahlenfolge}, deren Zahlen durch einen Speicherbereich gegeben sind. Zur Interpretation dieses
 * Speicherbereiches können über {@link #asINT8()}, {@link #asUINT8()}, {@link #asINT16()}, {@link #asUINT16()} bzw. {@link #asINT32()} entsprechende Sichten
 * erzeugte werden. Die Bytereihenfolge zur Interpretation des Speicherbereichs kann ebenfalls über entsprechende Sichten gewählt werden.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class MMIArray extends IAMArray {

	/** Dieses Feld speichert das leere {@link MMIArray}. */
	public static final MMIArray EMPTY = new MMIArrayS0D(ByteBuffer.wrap(new byte[0]), 0, 0);

	/** Diese Methode gibt den Speicherverbrauch einer Zahl in der gegebenen Kodierung zurück.
	 *
	 * @param mode Kodierung.
	 * @return Speicherverbrauch. */
	public static int size(final int mode) {
		return (0x021421 >> (mode << 4)) & 15;
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt ein {@link MMIArray} und gibt dieses zurück. Wenn das Objekt ein {@link MMIArray} ist, wird dieses
	 * geliefert. Wenn es ein {@link MappedBuffer} ist, wird dieser über {@link #from(MappedBuffer, long, int, int)} mit seiner aktuellen Größe in eine
	 * Zahlenfolge überführt. Wenn es ein {@link IAMArray} ist, wird dieses in eine {@link IAMArray#toBytes() Bytefolge} überführt und rekursiv weiter
	 * verarbeitet. Andernfalls wird das Objekt in einen {@link ByteBuffer} {@link IO#inputBufferFrom(Object) überführt} und dazu über
	 * {@link #from(ByteBuffer, int, int, int)} eine Zahlenfolge erzeugt.
	 *
	 * @param object Objekt.
	 * @return {@link MMIArray}.
	 * @throws IOException Wenn das {@link MMIArray} nicht erzeugt werden kann. */
	public static MMIArray from(final Object object) throws IOException {
		if (object instanceof MMIArray) return (MMIArray)object;
		try {
			if (object instanceof MappedBuffer) {
				final MappedBuffer source = (MappedBuffer)object;
				return MMIArray.from(source, 0, (int)Math.min(source.size(), Integer.MAX_VALUE), IAMArray.MODE_INT8);
			} else if (object instanceof IAMArray) {
				final IAMArray source = (IAMArray)object;
				return MMIArray.from((Object)source.toBytes());
			} else {
				final ByteBuffer source = IO.inputBufferFrom(object);
				return MMIArray.from(source, 0, source.limit(), IAMArray.MODE_INT8);
			}
		} catch (final IllegalArgumentException cause) {
			throw new IOException(cause);
		}
	}

	/** Diese Methode interpretiert den gegebenen Abschnitt eines {@link ByteBuffer} als Zahlenfolge und gibt diese zurück.
	 *
	 * @param buffer Quellpuffer.
	 * @param address Beginn des Abschnitts im Speicherbereich in Byte.
	 * @param length Anzahl der Zahlen im Speicherbereich.
	 * @param mode Kodierung der Zahlen im Speicherbereich ({@link IAMArray#MODE_INT8}, {@link IAMArray#MODE_INT16}, {@link IAMArray#MODE_INT32},
	 *        {@link IAMArray#MODE_UINT8}, {@link IAMArray#MODE_UINT16}).
	 * @return Zahlenfolge
	 * @throws IllegalArgumentException Wenn Beginn, Länge und/oder Kodierung ungültig sind. */
	public static MMIArrayS from(final ByteBuffer buffer, final int address, final int length, final int mode) throws IllegalArgumentException {
		if (mode == IAMArray.MODE_INT8) return new MMIArrayS0D(buffer, length, address);
		if (mode == IAMArray.MODE_INT16) return new MMIArrayS1D(buffer, length, address);
		if (mode == IAMArray.MODE_INT32) return new MMIArrayS2D(buffer, length, address);
		if (mode == IAMArray.MODE_UINT8) return new MMIArrayS3D(buffer, length, address);
		if (mode == IAMArray.MODE_UINT16) return new MMIArrayS4D(buffer, length, address);
		throw new IllegalArgumentException();
	}

	/** Diese Methode interpretiert den gegebenen Abschnitt eines {@link MappedBuffer} als Zahlenfolge und gibt diese zurück.
	 *
	 * @param buffer Quellpuffer.
	 * @param address Beginn des Abschnitts im Speicherbereich in Byte.
	 * @param length Anzahl der Zahlen im Speicherbereich.
	 * @param mode Kodierung der Zahlen im Speicherbereich ({@link IAMArray#MODE_INT8}, {@link IAMArray#MODE_INT16}, {@link IAMArray#MODE_INT32},
	 *        {@link IAMArray#MODE_UINT8}, {@link IAMArray#MODE_UINT16}).
	 * @return Zahlenfolge
	 * @throws IllegalArgumentException Wenn Beginn, Länge und/oder Kodierung ungültig sind. */
	public static MMIArrayL from(final MappedBuffer buffer, final long address, final int length, final int mode) throws IllegalArgumentException {
		if (mode == IAMArray.MODE_INT8) return new MMIArrayL0D(buffer, length, address);
		if (mode == IAMArray.MODE_INT16) return new MMIArrayL1D(buffer, length, address);
		if (mode == IAMArray.MODE_INT32) return new MMIArrayL2D(buffer, length, address);
		if (mode == IAMArray.MODE_UINT8) return new MMIArrayL3D(buffer, length, address);
		if (mode == IAMArray.MODE_UINT16) return new MMIArrayL4D(buffer, length, address);
		throw new IllegalArgumentException();
	}

	/** Dieser Konstruktor initialisiert die Länge.
	 *
	 * @param length Länge. */
	protected MMIArray(final int length) throws IllegalArgumentException {
		super(length);
	}

	/** Diese Methode gibt die Bytereihenfolge zur Interpretation der Zahlen im Speicherbereich dieser Zahlenfolge zurück.
	 *
	 * @see ByteOrder#BIG_ENDIAN
	 * @see ByteOrder#LITTLE_ENDIAN
	 * @return Bytereihenfolge. */
	public abstract ByteOrder order();

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge in der gegebenen Kodierung zurück.
	 *
	 * @see #MODE_INT8
	 * @see #MODE_INT16
	 * @see #MODE_INT32
	 * @see #MODE_UINT8
	 * @see #MODE_UINT16
	 * @param mode Zahlenkodierung.
	 * @return Zahlenfolge.
	 * @throws IllegalArgumentException Wenn die Interpretation ungültig ist. */
	public final MMIArray as(final int mode) throws IllegalArgumentException {
		if (mode == IAMArray.MODE_INT8) return this.asINT8();
		if (mode == IAMArray.MODE_INT16) return this.asINT16();
		if (mode == IAMArray.MODE_INT32) return this.asINT32();
		if (mode == IAMArray.MODE_UINT8) return this.asUINT8();
		if (mode == IAMArray.MODE_UINT16) return this.asUINT16();
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt eine Zahlenfolge zurück, welche die Zahlen im Speicherbereich dieser Zahlenfolge in der gegebenen Bytereihenfolge interpretiert. Die
	 * Bytereihenfolge {@code null} wird als {@link ByteOrder#BIG_ENDIAN} interpretiert.
	 *
	 * @param order Bytereihenfolge oder {@code null}.
	 * @return Zahlenfolge. */
	public final MMIArray as(final ByteOrder order) {
		return Bytes.directOrder(order) == this.order() ? this : this.asRO();
	}

	/** Diese Methode ist eine Abkürzung für {@link #as(ByteOrder) this.as(Bytes.NATIVE_ORDER)}.
	 *
	 * @return Zahlenfolge in {@link Bytes#NATIVE_ORDER}. */
	public final MMIArray asNE() {
		return this.as(Bytes.NATIVE_ORDER);
	}

	/** Diese Methode ist eine Abkürzung für {@link #as(ByteOrder) this.as(ByteOrder.BIG_ENDIAN)}.
	 *
	 * @return Zahlenfolge in {@link ByteOrder#BIG_ENDIAN}. */
	public final MMIArray asBE() {
		return this.as(ByteOrder.BIG_ENDIAN);
	}

	/** Diese Methode ist eine Abkürzung für {@link #as(ByteOrder) this.as(ByteOrder.LITTLE_ENDIAN)}.
	 *
	 * @return Zahlenfolge in {@link ByteOrder#LITTLE_ENDIAN}. */
	public final MMIArray asLE() {
		return this.as(ByteOrder.LITTLE_ENDIAN);
	}

	/** Diese Methode gibt eine Zahlenfolge zurück, die den Speicherbereich dieser Zahlenfolge in umgekehrter Bytereihenfolge interpretiert.
	 *
	 * @return Zahlenfolge. */
	public abstract MMIArray asRO();

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT8}-Zahlen ({@code byte}) interpretiert zurück.
	 *
	 * @return {@code byte}-{@link MMIArray}. */
	public abstract MMIArray asINT8();

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT16}-Zahlen ({@code short}) interpretiert zurück.
	 *
	 * @return {@code short}-{@link MMIArray}. */
	public abstract MMIArray asINT16();

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT32}-Zahlen ({@code int}) interpretiert zurück.
	 *
	 * @return {@code int}-{@link MMIArray}. */
	public abstract MMIArray asINT32();

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code UINT8}-Zahlen ({@code unsigned byte}) interpretiert zurück.
	 *
	 * @return {@code unsigned byte}-{@link MMIArray}. */
	public abstract MMIArray asUINT8();

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code UINT16}-Zahlen ({@code unsigned short}) interpretiert zurück.
	 *
	 * @see ByteBuffer#getShort()
	 * @return {@code unsigned short}-{@link MMIArray}. */
	public abstract MMIArray asUINT16();

	@Override
	public MMIArray section(final int offset) {
		return this.section(offset, this.length - offset);
	}

	@Override
	public MMIArray section(final int offset, final int length) {
		return (offset < 0) || (length <= 0) || ((offset + length) > this.length) ? this.customSection(0, 0) : this.customSection(offset, length);
	}

	@Override
	protected abstract MMIArray customSection(final int offset, final int length);

	@Override
	protected boolean customIsCompact() {
		return true;
	}

}