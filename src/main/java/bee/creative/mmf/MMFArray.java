package bee.creative.mmf;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import bee.creative.iam.IAMArray;
import bee.creative.util.IO;

/** Diese Klasse implementiert ein {@link IAMArray}, dessen Zahlen durch ein {@link File} oder einen {@link ByteBuffer} gegeben sind.<br>
 * Die Methoden {@link #get(int)}, {@link #length()} und {@link #section(int, int)} liefern in dieser Basisklasse immer {@code 0} bzw. {@code this}.<br>
 * Zur Interpretation des Speicherbereichs muss eine entsprechende, über {@link #toINT8()}, {@link #toUINT8()}, {@link #toINT16()}, {@link #toUINT16()} oder
 * {@link #toINT32()} erzeugte Sicht verwendet werden.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class MMFArray extends IAMArray {

	/** Dieses Feld speichert das leere {@link MMFArray}. */
	public static final MMFArray EMPTY = new MMFArray(ByteBuffer.wrap(new byte[0]));

	{}

	/** Diese Methode erzeugt aus dem gegebenen Objekt ein {@link MMFArray} und gibt dieses zurück.<br>
	 * Wenn das Objekt ein {@link MMFArray} ist, wird dieses geliefert. Andernfalls wird das Objekt in einen {@link ByteBuffer} {@link IO#inputBufferFrom(Object)
	 * überführt}.
	 *
	 * @see IO#inputBufferFrom(Object)
	 * @see MMFArray#MMFArray(ByteBuffer)
	 * @param object Objekt.
	 * @return {@link MMFArray}.
	 * @throws IOException Wenn das {@link MMFArray} nicht erzeugt werden kann. */
	public static MMFArray from(final Object object) throws IOException {
		if (object instanceof MMFArray) return (MMFArray)object;
		return new MMFArray(IO.inputBufferFrom(object));
	}

	@SuppressWarnings ("javadoc")
	static ByteOrder order(final ByteOrder order) {
		return order != null ? order : ByteOrder.nativeOrder();
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer buffer(final File file, final ByteOrder order) throws IOException, NullPointerException {
		try (final RandomAccessFile data = new RandomAccessFile(file, "r")) {
			return data.getChannel().map(MapMode.READ_ONLY, 0, file.length()).order(MMFArray.order(order));
		}
	}

	{}

	/** Dieses Feld speichert den Speicherbereich. */
	protected final ByteBuffer byteBuffer;

	/** Dieses Feld speichert die Startposition. */
	protected final int byteOffset;

	/** Dieses Feld speichert die Länge. */
	protected final int byteLength;

	/** Dieser Konstruktor initialisiert den Speicherbereich.
	 *
	 * @param buffer Speicherbereich.
	 * @throws NullPointerException Wenn {@code buffer} {@code null} ist. */
	public MMFArray(final ByteBuffer buffer) throws NullPointerException {
		this(0, buffer, 0, Math.min(buffer.limit(), 1073741823));
	}

	/** Dieser Konstruktor initialisiert den Speicherbereich mit dem aus der gegebene Datei und der gegebenen Bytereihenfolge erzeugten {@link MappedByteBuffer}.
	 * Wenn die Bytereihenfolge {@code null} ist, wird die native Bytereihenfolge verwendet.
	 *
	 * @param file Datei.
	 * @param order Bytereihenfolge oder {@code null} für {@link ByteOrder#nativeOrder()}.
	 * @throws IOException Wenn die Datei nicht geöffnet werden kann.
	 * @throws NullPointerException Wenn {@code file} {@code null} ist. */
	public MMFArray(final File file, final ByteOrder order) throws IOException, NullPointerException {
		this(MMFArray.buffer(file, order));
	}

	/** Dieser Konstruktor initialisiert den Speicherbereich mit dem aus den gegebenen Bytes erzeugten {@link ByteBuffer}. Wenn die Bytereihenfolge {@code null}
	 * ist, wird die native Bytereihenfolge verwendet.
	 *
	 * @param bytes Bytes.
	 * @param order Bytereihenfolge oder {@code null} für {@link ByteOrder#nativeOrder()}.
	 * @throws NullPointerException Wenn {@code bytes} {@code null} ist. */
	public MMFArray(final byte[] bytes, final ByteOrder order) throws NullPointerException {
		this(ByteBuffer.wrap(bytes).order(MMFArray.order(order)));
	}

	@SuppressWarnings ("javadoc")
	protected MMFArray(final int length, final ByteBuffer byteBuffer, final int byteOffset, final int byteLength)
		throws NullPointerException, IllegalArgumentException {
		super(length);
		this.byteBuffer = byteBuffer;
		this.byteOffset = byteOffset;
		this.byteLength = byteLength;
	}

	{}

	/** Diese Methode gibt die Bytereihenfolge des internen {@link ByteBuffer} zurück.
	 *
	 * @see #withOrder(ByteOrder)
	 * @see ByteBuffer#order()
	 * @return Bytereihenfolge. */
	public final ByteOrder order() {
		return this.byteBuffer.order();
	}

	/** Diese Methode gibt eine Kopie des Speicherbereichs als {@code byte[]} zurück.
	 *
	 * @return Kopie des Speicherbereichs. */
	public final byte[] toBytes() {
		final int length = this.byteLength;
		final byte[] result = new byte[length];
		if (length == 0) return result;
		final ByteBuffer buffer = this.byteBuffer;
		final int position = buffer.position();
		try {
			buffer.position(position + this.byteOffset);
			buffer.get(result, 0, length);
		} finally {
			buffer.position(position);
		}
		return result;
	}

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT8}-Zahlen ({@code byte}) interpretiert zurück.
	 *
	 * @see ByteBuffer#get(int)
	 * @return {@link MMFINT8Array}. */
	public final MMFArray toINT8() {
		return new MMFINT8Array(this.byteBuffer, this.byteOffset, this.byteLength);
	}

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT16}-Zahlen ({@code short}) interpretiert zurück.
	 *
	 * @see ByteBuffer#getShort()
	 * @return {@link MMFINT16Array}. */
	public final MMFArray toINT16() {
		return new MMFINT16Array(this.byteBuffer, this.byteOffset, this.byteLength);
	}

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT32}-Zahlen ({@code int}) interpretiert zurück.
	 *
	 * @see ByteBuffer#getInt()
	 * @return {@link MMFINT32Array}. */
	public final MMFArray toINT32() {
		return new MMFINT32Array(this.byteBuffer, this.byteOffset, this.byteLength);
	}

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code UINT8}-Zahlen ({@code unsigned byte}) interpretiert zurück.
	 *
	 * @see ByteBuffer#get()
	 * @return {@link MMFUINT8Array}. */
	public final MMFArray toUINT8() {
		return new MMFUINT8Array(this.byteBuffer, this.byteOffset, this.byteLength);
	}

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code UINT16}-Zahlen ({@code unsigned short}) interpretiert zurück.
	 *
	 * @see ByteBuffer#getShort()
	 * @return {@link MMFUINT16Array}. */
	public final MMFArray toUINT16() {
		return new MMFUINT16Array(this.byteBuffer, this.byteOffset, this.byteLength);
	}

	/** Diese Methode gibt eine Kopie dieses {@link MMFArray} mit der gegebenen Bytereihenfolge zurück.
	 *
	 * @see #order()
	 * @see ByteBuffer#order()
	 * @param order Bytereihenfolge.
	 * @return {@link MMFArray}. */
	public MMFArray withOrder(final ByteOrder order) {
		return new MMFArray(this.length, this.byteBuffer.duplicate().order(order), this.byteOffset, this.byteLength);
	}

	/** Diese Methode gibt eine Kopie dieses {@link MMFArray} mit umgekehrter Bytereihenfolge zurück.
	 *
	 * @see #order()
	 * @see #withOrder(ByteOrder)
	 * @return {@link MMFArray}. */
	public final MMFArray withInverseOrder() {
		return this.withOrder(this.order() == ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return MMFArray.EMPTY;
	}

	/** {@inheritDoc} */
	@Override
	public final MMFArray section(final int offset, final int length) {
		return (MMFArray)super.section(offset, length);
	}

}