package bee.creative.io;

import static java.nio.ByteOrder.nativeOrder;
import static java.util.zip.Deflater.DEFAULT_COMPRESSION;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import bee.creative.fem.FEMString;

/** Diese Klasse implementiert einen {@link DeflaterOutputStream}, der primitive Werte über einen {@link ByteBuffer} mit gegebener {@link ByteOrder} schreibt.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class DZIPOutputStream extends DeflaterOutputStream {

	/** Dieser Konstruktor initialisiert den {@link OutputStream} mit {@code target}, die Kompressionsstufe des {@link Deflater} mit
	 * {@link Deflater#DEFAULT_COMPRESSION} und die Bytereihenfolge mit der nativen. */
	public DZIPOutputStream(OutputStream target) throws IOException {
		this(target, DEFAULT_COMPRESSION);
	}

	/** Dieser Konstruktor initialisiert den {@link OutputStream} mit {@code target}, die Kompressionsstufe des {@link Deflater} mit {@code level}
	 * ({@link Deflater#DEFAULT_COMPRESSION}, {@link Deflater#NO_COMPRESSION}..{@link Deflater#BEST_COMPRESSION}) und die Bytereihenfolge mit der nativen. */
	public DZIPOutputStream(OutputStream target, int level) throws IOException {
		this(target, level, nativeOrder());
	}

	/** Dieser Konstruktor initialisiert den {@link OutputStream} mit {@code target}, die Kompressionsstufe des {@link Deflater} mit {@code level}
	 * ({@link Deflater#DEFAULT_COMPRESSION}, {@link Deflater#NO_COMPRESSION}..{@link Deflater#BEST_COMPRESSION}) und die Bytereihenfolge mit {@code order}. */
	public DZIPOutputStream(OutputStream target, int level, ByteOrder order) throws IOException {
		super(target, new Deflater(level, true), BUFFER_SIZE, true);
		this.level = level;
		this.bufferAsByte = ByteBuffer.allocateDirect(BUFFER_SIZE).order(order);
		this.bufferAsInt = this.bufferAsByte.asIntBuffer();
		this.bufferAsChar = this.bufferAsByte.asCharBuffer();
		this.bufferAsLong = this.bufferAsByte.asLongBuffer();
		this.bufferAsShort = this.bufferAsByte.asShortBuffer();
		this.bufferAsFloat = this.bufferAsByte.asFloatBuffer();
		this.bufferAsDouble = this.bufferAsByte.asDoubleBuffer();
	}

	public int getLevel() {
		return this.level;
	}

	public DZIPOutputStream useLevel(int level) {
		this.def.setLevel(level);
		this.level = level;
		return this;
	}

	public ByteOrder getOrder() {
		return this.bufferAsByte.order();
	}

	public DZIPOutputStream useOrder(ByteOrder order) {
		this.bufferAsByte.order(order);
		return this;
	}

	public void writeInt(int... values) throws IOException {
		this.writeInt(values, 0, values.length);
	}

	public void writeInt(int[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, BUFFER_SIZE / 4);
			this.bufferAsInt.rewind().put(values, offset, count);
			this.writeBuffer(count * 4);
			offset += count;
			length -= count;
		}
	}

	public void writeByte(byte... values) throws IOException {
		this.writeByte(values, 0, values.length);
	}

	public void writeByte(byte[] values, int offset, int length) throws IOException {
		this.write(values, offset, length);
	}

	public void writeChar(char... values) throws IOException {
		this.writeChar(values, 0, values.length);
	}

	public void writeChar(char[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, BUFFER_SIZE / 2);
			this.bufferAsChar.rewind().put(values, offset, count);
			this.writeBuffer(count * 2);
			offset += count;
			length -= count;
		}
	}

	public void writeLong(long... values) throws IOException {
		this.writeLong(values, 0, values.length);
	}

	public void writeLong(long[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, BUFFER_SIZE / 8);
			this.bufferAsLong.rewind().put(values, offset, count);
			this.writeBuffer(count * 8);
			offset += count;
			length -= count;
		}
	}

	public void writeShort(short... values) throws IOException {
		this.writeShort(values, 0, values.length);
	}

	public void writeShort(short[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, BUFFER_SIZE / 2);
			this.bufferAsShort.rewind().put(values, offset, count);
			this.writeBuffer(count * 2);
			offset += count;
			length -= count;
		}
	}

	public void writeFloat(float... values) throws IOException {
		this.writeFloat(values, 0, values.length);
	}

	public void writeFloat(float[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, BUFFER_SIZE / 4);
			this.bufferAsFloat.rewind().put(values, offset, count);
			this.writeBuffer(count * 4);
			offset += count;
			length -= count;
		}
	}

	public void writeDouble(double... values) throws IOException {
		this.writeDouble(values, 0, values.length);
	}

	public void writeDouble(double[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, BUFFER_SIZE / 8);
			this.bufferAsDouble.rewind().put(values, offset, count);
			this.writeBuffer(count * 8);
			offset += count;
			length -= count;
		}
	}

	public void writeStrings(FEMString... values) throws IOException {
		this.writeStrings(values, 0, values.length);
	}

	/** Diese Methode persistiert die Zeichenketten im gegebenen Aschnitt in folgender Struktur:
	 * {@code (valueHash: int[length], valueLength: int[length], (valueSize: int[length], valueBytes: byte[valueSize][length]))}. */
	public void writeStrings(FEMString[] values, int offset, int length) throws IOException {
		var hashArray = new int[length];
		var lengthArray = new int[length];
		var stringArray = new byte[length][];
		for (var i = 0; i < length; i++) {
			var value = values[offset + i];
			hashArray[i] = value.hashCode();
			stringArray[i] = value.toBytes(true);
			lengthArray[i] = value.length();
		}
		this.writeInt(hashArray);
		this.writeInt(lengthArray);
		this.writeBinaries(stringArray);
	}

	public void writeBinaries(byte[]... values) throws IOException {
		this.writeBinaries(values, 0, values.length);
	}

	/** Diese Methode persistiert die Bytefolgen im gegebenen Aschnitt in folgender Struktur:
	 * {@code (valueSize: int[length], valueBytes: byte[valueSize][length])}. */
	public void writeBinaries(byte[][] values, int offset, int length) throws IOException {
		var sizeArray = new int[length];
		for (var i = 0; i < length; i++) {
			sizeArray[i] = values[offset + i].length;
		}
		this.writeInt(sizeArray);
		for (var i = 0; i < length; i++) {
			this.writeByte(values[offset + i]);
		}
	}

	private static final int BUFFER_SIZE = 1 << 19;

	private int level;

	private final ByteBuffer bufferAsByte;

	private final IntBuffer bufferAsInt;

	private final CharBuffer bufferAsChar;

	private final LongBuffer bufferAsLong;

	private final ShortBuffer bufferAsShort;

	private final FloatBuffer bufferAsFloat;

	private final DoubleBuffer bufferAsDouble;

	private void writeBuffer(int length) throws IOException {
		if (this.def.finished()) throw new IOException();
		this.def.setInput(this.bufferAsByte.rewind().limit(length));
		while (!this.def.needsInput()) {
			this.deflate();
		}
	}

}
