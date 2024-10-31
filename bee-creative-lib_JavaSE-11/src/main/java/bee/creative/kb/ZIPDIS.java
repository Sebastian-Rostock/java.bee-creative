package bee.creative.kb;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import bee.creative.fem.FEMString;
import bee.creative.fem.FEMString.HashString;

/** Diese Klasse implementiert einen {@link InflaterInputStream}, der primitive Werte über einen {@link ByteBuffer} mit gegebener {@link ByteOrder} liest.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class ZIPDIS extends InflaterInputStream {

	public static void inflate(byte[] source, RUN task) throws IOException {
		ZIPDIS.inflate(source, ByteOrder.nativeOrder(), task);
	}

	public static void inflate(byte[] source, ByteOrder order, RUN task) throws IOException {
		try (var zipdis = new ZIPDIS(new ByteArrayInputStream(source), order)) {
			task.run(zipdis);
		}
	}

	public ZIPDIS(InputStream source) throws IOException {
		this(source, ByteOrder.nativeOrder());
	}

	/** Dieser Konstruktor initialisiert den gegebenen {@link InputStream}. */
	public ZIPDIS(InputStream source, ByteOrder order) throws IOException {
		super(source, new Inflater(true), ZIPDIS.BUFFER_SIZE);
		this.bufferAsByte = ByteBuffer.allocateDirect(ZIPDIS.BUFFER_SIZE).order(order);
		this.bufferAsInt = this.bufferAsByte.asIntBuffer();
		this.bufferAsChar = this.bufferAsByte.asCharBuffer();
		this.bufferAsLong = this.bufferAsByte.asLongBuffer();
		this.bufferAsShort = this.bufferAsByte.asShortBuffer();
		this.bufferAsFloat = this.bufferAsByte.asFloatBuffer();
		this.bufferAsDouble = this.bufferAsByte.asDoubleBuffer();
	}

	public int[] readInt(int length) throws IOException {
		return this.readInt(new int[length], 0, length);
	}

	public int[] readInt(int[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, ZIPDIS.BUFFER_SIZE / 4);
			this.readBuffer(count * 4);
			this.bufferAsInt.rewind().get(values, offset, count);
			offset += count;
			length -= count;
		}
		return values;
	}

	public byte[] readByte(int length) throws IOException {
		return this.readByte(new byte[length], 0, length);
	}

	public byte[] readByte(byte[] values, int offset, int length) throws IOException {
		this.bufferAsByte.limit(ZIPDIS.BUFFER_SIZE);
		while (length > 0) {
			var count = Math.min(length, ZIPDIS.BUFFER_SIZE);
			this.readBuffer(count);
			this.bufferAsByte.rewind().get(values, offset, count);
			offset += count;
			length -= count;
		}
		return values;
	}

	public char[] readChar(int length) throws IOException {
		return this.readChar(new char[length], 0, length);
	}

	public char[] readChar(char[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, ZIPDIS.BUFFER_SIZE / 2);
			this.readBuffer(count * 2);
			this.bufferAsChar.rewind().get(values, offset, count);
			offset += count;
			length -= count;
		}
		return values;
	}

	public long[] readLong(int length) throws IOException {
		return this.readLong(new long[length], 0, length);
	}

	public long[] readLong(long[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, ZIPDIS.BUFFER_SIZE / 8);
			this.readBuffer(count * 8);
			this.bufferAsLong.rewind().get(values, offset, count);
			offset += count;
			length -= count;
		}
		return values;
	}

	public short[] readShort(int length) throws IOException {
		return this.readShort(new short[length], 0, length);
	}

	public short[] readShort(short[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, ZIPDIS.BUFFER_SIZE / 2);
			this.readBuffer(count * 2);
			this.bufferAsShort.rewind().get(values, offset, count);
			offset += count;
			length -= count;
		}
		return values;
	}

	public float[] readFloat(int length) throws IOException {
		return this.readFloat(new float[length], 0, length);
	}

	public float[] readFloat(float[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, ZIPDIS.BUFFER_SIZE / 4);
			this.readBuffer(count * 4);
			this.bufferAsFloat.rewind().get(values, offset, count);
			offset += count;
			length -= count;
		}
		return values;
	}

	public double[] readDouble(int length) throws IOException {
		return this.readDouble(new double[length], 0, length);
	}

	public double[] readDouble(double[] values, int offset, int length) throws IOException {
		while (length > 0) {
			var count = Math.min(length, ZIPDIS.BUFFER_SIZE / 8);
			this.readBuffer(count * 8);
			this.bufferAsDouble.rewind().get(values, offset, count);
			offset += count;
			length -= count;
		}
		return values;
	}

	public FEMString readString() throws IOException {
		var hash_count = this.readInt(2);
		var result = FEMString.from(false, true, this.readByte(hash_count[1]));
		HashString.setHash(result, hash_count[0]);
		return result;
	}

	public interface RUN {

		void run(ZIPDIS source) throws IOException;

	}

	private static final int BUFFER_SIZE = 524288;

	private final ByteBuffer bufferAsByte;

	private final IntBuffer bufferAsInt;

	private final CharBuffer bufferAsChar;

	private final LongBuffer bufferAsLong;

	private final ShortBuffer bufferAsShort;

	private final FloatBuffer bufferAsFloat;

	private final DoubleBuffer bufferAsDouble;

	private void readBuffer(int length) throws IOException {
		this.bufferAsByte.rewind().limit(length);
		while (true) {
			try {
				this.inf.inflate(this.bufferAsByte);
			} catch (DataFormatException cause) {
				throw new IOException(cause);
			}
			if (!this.bufferAsByte.hasRemaining()) return;
			if (this.inf.finished() || this.inf.needsDictionary()) throw new EOFException();
			if (this.inf.needsInput()) {
				this.fill();
			}
		}
	}

}
