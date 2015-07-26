package bee.creative.mmf;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import bee.creative.iam.IAM.IAMBaseArray;
import bee.creative.iam.IAMArray;

/**
 * Diese Klasse implementiert ein {@link IAMArray}, dessen Zahlen durch ein {@link File} oder einen {@link ByteBuffer} gegeben sind.<br>
 * Die Methoden {@link #get(int)}, {@link #length()} und {@link #section(int, int)} liefern in dieser Basisklasse immer {@code 0} bzw. {@code this}.<br>
 * Zur Interpretation des Speicherbereichs muss eine entsprechende, über {@link #toINT8()}, {@link #toUINT8()}, {@link #toINT16()}, {@link #toUINT16()} oder
 * {@link #toINT32()} erzeugte Sicht verwendet werden.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class MMFArray extends IAMBaseArray {

	/**
	 * Dieses Feld speichert das leere {@link MMFArray}.
	 */
	public static final MMFArray EMPTY = new MMFArray(null, 0, 0);

	{}

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

	/**
	 * Dieses Feld speichert den Speicherbereich.
	 */
	protected final ByteBuffer byteBuffer;

	/**
	 * Dieses Feld speichert die Startposition.
	 */
	protected final int byteOffset;

	/**
	 * Dieses Feld speichert die Länge.
	 */
	protected final int byteLength;

	/**
	 * Dieser Konstruktor initialisiert den Speicherbereich.
	 * 
	 * @param buffer Speicherbereich.
	 * @throws NullPointerException Wenn {@code buffer} {@code null} ist.
	 */
	public MMFArray(final ByteBuffer buffer) throws NullPointerException {
		this(buffer, 0, Math.min(buffer.limit(), 1073741823));
	}

	/**
	 * Dieser Konstruktor initialisiert den Speicherbereich mit dem aus der gegebene Datei und der gegebenen Bytereihenfolge erzeugten {@link MappedByteBuffer}.
	 * Wenn die Bytereihenfolge {@code null} ist, wird die native Bytereihenfolge verwendet.
	 * 
	 * @param file Datei.
	 * @param order Bytereihenfolge.
	 * @throws IOException Wenn die Datei nicht geöffnet werden kann.
	 * @throws NullPointerException Wenn {@code file} {@code null} ist.
	 */
	public MMFArray(final File file, final ByteOrder order) throws IOException, NullPointerException {
		this(MMFArray.buffer(file, order));
	}

	/**
	 * Dieser Konstruktor initialisiert den Speicherbereich mit dem aus den gegebenen Bytes erzeugten {@link ByteBuffer}. Wenn die Bytereihenfolge {@code null}
	 * ist, wird die native Bytereihenfolge verwendet.
	 * 
	 * @param bytes Bytes.
	 * @param order Bytereihenfolge.
	 * @throws NullPointerException Wenn {@code bytes} {@code null} ist.
	 */
	public MMFArray(final byte[] bytes, final ByteOrder order) throws NullPointerException {
		this(ByteBuffer.wrap(bytes).order(MMFArray.order(order)));
	}

	@SuppressWarnings ("javadoc")
	protected MMFArray(final ByteBuffer byteBuffer, final int byteOffset, final int byteLength) throws NullPointerException, IllegalArgumentException {
		this.byteBuffer = byteBuffer;
		this.byteOffset = byteOffset;
		this.byteLength = byteLength;
	}

	{}

	/**
	 * Diese Methode gibt eine Kopie des Speicherbereichs als {@code byte[]} zurück.
	 * 
	 * @return Kopie des Speicherbereichs.
	 */
	public final byte[] toBytes() {
		final int length = this.byteLength;
		final byte[] result = new byte[length];
		if (length == 0) return result;
		final ByteBuffer buffer = this.byteBuffer;
		final int position = buffer.position();
		try {
			buffer.position(position + this.byteOffset);
			buffer.get(result);
		} finally {
			buffer.position(position);
		}
		return result;
	}

	/**
	 * Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT8}-Zahlen ({@code byte}) interpretiert zurück.
	 * 
	 * @return {@link MMFINT8Array}
	 */
	public final MMFArray toINT8() {
		return new MMFINT8Array(this.byteBuffer, this.byteOffset, this.byteLength);
	}

	/**
	 * Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT16}-Zahlen ({@code short}) interpretiert zurück.
	 * 
	 * @return {@link MMFINT16Array}
	 */
	public final MMFArray toINT16() {
		return new MMFINT16Array(this.byteBuffer, this.byteOffset, this.byteLength);
	}

	/**
	 * Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT32}-Zahlen ({@code int}) interpretiert zurück.
	 * 
	 * @return {@link MMFINT32Array}
	 */
	public final MMFArray toINT32() {
		return new MMFINT32Array(this.byteBuffer, this.byteOffset, this.byteLength);
	}

	/**
	 * Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code UINT8}-Zahlen ({@code unsigned byte}) interpretiert zurück.
	 * 
	 * @return {@link MMFUINT8Array}
	 */
	public final MMFArray toUINT8() {
		return new MMFUINT8Array(this.byteBuffer, this.byteOffset, this.byteLength);
	}

	/**
	 * Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code UINT16}-Zahlen ({@code unsigned short}) interpretiert zurück.
	 * 
	 * @return {@link MMFUINT16Array}
	 */
	public final MMFArray toUINT16() {
		return new MMFUINT16Array(this.byteBuffer, this.byteOffset, this.byteLength);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected MMFArray newSection(final int offset, final int length) {
		return MMFArray.EMPTY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MMFArray section(final int offset, final int length) {
		if ((offset < 0) || (length <= 0) || ((offset + length) > this.length())) return MMFArray.EMPTY;
		return this.newSection(offset, length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int get(final int index) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int length() {
		return 0;
	}

}