package bee.creative.kb;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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

/** Diese Klasse implementiert einen {@link DeflaterOutputStream}, der primitive Werte we dein {@link DataOutputStream} schreiben kann.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ZIPDOS extends DeflaterOutputStream {

	public static byte[] deflate(RUN task) throws IOException {
		return ZIPDOS.deflate(Deflater.DEFAULT_COMPRESSION, task);
	}

	public static byte[] deflate(int level, RUN task) throws IOException {
		return ZIPDOS.deflate(level, ByteOrder.nativeOrder(), task);
	}

	public static byte[] deflate(int level, ByteOrder order, RUN task) throws IOException {
		try (var result = new ByteArrayOutputStream(ZIPDOS.BUFFER_SIZE); var target = new ZIPDOS(result, level, order)) {
			task.run(target);
			target.flush();
			return result.toByteArray();
		}
	}

	/** Dieser Konstruktor initialisiert den {@link ByteArrayOutputStream} mit dem gegebenen und den {@link Deflater} mit {@link Deflater#DEFAULT_COMPRESSION}. */
	public ZIPDOS(OutputStream target) throws IOException {
		this(target, Deflater.DEFAULT_COMPRESSION);
	}

	/** Dieser Konstruktor initialisiert den {@link ByteArrayOutputStream} mit dem gegebenen und den {@link Deflater} mit der gegebenen Kompressionsstufe
	 * ({@link Deflater#DEFAULT_COMPRESSION}, {@link Deflater#NO_COMPRESSION}..{@link Deflater#BEST_COMPRESSION}). */
	public ZIPDOS(OutputStream target, int level) throws IOException {
		this(target, level, ByteOrder.nativeOrder());
	}

	public ZIPDOS(OutputStream target, int level, ByteOrder order) throws IOException {
		super(target, new Deflater(level, true), ZIPDOS.BUFFER_SIZE, true);
		this.bufferAsByte = ByteBuffer.allocateDirect(ZIPDOS.BUFFER_SIZE).order(order);
		this.bufferAsInt = this.bufferAsByte.asIntBuffer();
		this.bufferAsChar = this.bufferAsByte.asCharBuffer();
		this.bufferAsLong = this.bufferAsByte.asLongBuffer();
		this.bufferAsShort = this.bufferAsByte.asShortBuffer();
		this.bufferAsFloat = this.bufferAsByte.asFloatBuffer();
		this.bufferAsDouble = this.bufferAsByte.asDoubleBuffer();
	}

	public void writeInt(int... values) throws IOException {
		this.writeInt(values, 0, values.length);
	}

	public void writeInt(int[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, ZIPDOS.BUFFER_SIZE / 4);
			this.bufferAsInt.rewind().put(values, offset, length);
			this.writeBuffer(length * 4);
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
			var count = Math.min(length, ZIPDOS.BUFFER_SIZE / 2);
			this.bufferAsChar.rewind().put(values, offset, length);
			this.writeBuffer(length * 2);
			offset += count;
			length -= count;
		}
	}

	public void writeLong(long... values) throws IOException {
		this.writeLong(values, 0, values.length);
	}

	public void writeLong(long[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, ZIPDOS.BUFFER_SIZE / 8);
			this.bufferAsLong.rewind().put(values, offset, length);
			this.writeBuffer(length * 8);
			offset += count;
			length -= count;
		}
	}

	public void writeShort(short... values) throws IOException {
		this.writeShort(values, 0, values.length);
	}

	public void writeShort(short[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, ZIPDOS.BUFFER_SIZE / 2);
			this.bufferAsShort.rewind().put(values, offset, length);
			this.writeBuffer(length * 2);
			offset += count;
			length -= count;
		}
	}

	public void writeFloat(float... values) throws IOException {
		this.writeFloat(values, 0, values.length);
	}

	public void writeFloat(float[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, ZIPDOS.BUFFER_SIZE / 4);
			this.bufferAsFloat.rewind().put(values, offset, length);
			this.writeBuffer(length * 4);
			offset += count;
			length -= count;
		}
	}

	public void writeDouble(double... values) throws IOException {
		this.writeDouble(values, 0, values.length);
	}

	public void writeDouble(double[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, ZIPDOS.BUFFER_SIZE / 8);
			this.bufferAsDouble.rewind().put(values, offset, length);
			this.writeBuffer(length * 8);
			offset += count;
			length -= count;
		}
	}

	public void writeString(String value) throws IOException {
		this.writeString(FEMString.from(value));
	}

	public void writeString(FEMString value) throws IOException {
		var bytes = value.toBytes(true);
		this.writeInt(value.hashCode(), bytes.length);
		this.writeByte(bytes);
	}

	public interface RUN {

		void run(ZIPDOS target) throws IOException;

	}

	private static final int BUFFER_SIZE = 524288;

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
