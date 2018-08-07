package bee.creative.data;

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bee.creative.util.Bytes;

/** Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link ByteBuffer}.
 *
 * @see ByteBuffer
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class BufferDataSource extends BaseDataSource {

	/** Dieses Feld speichert die Nutzdaten. */
	final ByteBuffer data;

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist. */
	public BufferDataSource(final byte... data) throws NullPointerException {
		this(ByteBuffer.wrap(data));
	}

	/** Dieser Konstruktor initialisiert die Nutzdaten.
	 *
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist. */
	public BufferDataSource(final ByteBuffer data) throws NullPointerException {
		this.data = data.slice().order(ByteOrder.BIG_ENDIAN);
	}

	/** {@inheritDoc} */
	@Override
	public final ByteBuffer data() {
		return this.data;
	}

	/** {@inheritDoc} */
	@Override
	public void readFully(final byte[] array, final int offset, final int length) throws IOException {
		try {
			this.data.get(array, offset, length);
		} catch (final BufferUnderflowException e) {
			throw new EOFException();
		} catch (final IndexOutOfBoundsException e) {
			throw new EOFException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean readBoolean() throws IOException {
		return this.readByte() != 0;
	}

	/** {@inheritDoc} */
	@Override
	public byte readByte() throws IOException {
		return this.data.get();
	}

	/** {@inheritDoc} */
	@Override
	public int readUnsignedByte() throws IOException {
		return this.data.get() & 0xFF;
	}

	/** {@inheritDoc} */
	@Override
	public short readShort() throws IOException {
		return this.data.getShort();
	}

	/** {@inheritDoc} */
	@Override
	public int readUnsignedShort() throws IOException {
		return this.data.getShort() & 0xFFFF;
	}

	/** {@inheritDoc} */
	@Override
	public char readChar() throws IOException {
		return this.data.getChar();
	}

	/** {@inheritDoc} */
	@Override
	public int readInt() throws IOException {
		return this.data.getInt();
	}

	/** {@inheritDoc} */
	@Override
	public int readInt(final int size) throws IOException {
		switch (size) {
			case 0:
				return 0;
			case 1:
				return this.data.get() & 0xFF;
			case 2:
				return this.data.getShort() & 0xFFFF;
			case 3:
				this.data.get(this.array, 0, 3);
				return Bytes.getInt3BE(this.array, 0);
			case 4:
				return this.data.getInt();
			default:
				throw new IllegalArgumentException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public long readLong() throws IOException {
		return this.data.getLong();
	}

	/** {@inheritDoc} */
	@Override
	public long readLong(final int size) throws IOException {
		switch (size) {
			case 0:
				return 0;
			case 1:
				return this.data.get() & 0xFF;
			case 2:
				return this.data.getShort() & 0xFFFF;
			case 3:
				this.data.get(this.array, 0, 3);
				return Bytes.getInt3BE(this.array, 0);
			case 4:
				return this.data.getInt();
			case 5:
				this.data.get(this.array, 0, 5);
				return Bytes.getLong5BE(this.array, 0);
			case 6:
				this.data.get(this.array, 0, 6);
				return Bytes.getLong6BE(this.array, 0);
			case 7:
				this.data.get(this.array, 0, 7);
				return Bytes.getLong7BE(this.array, 0);
			case 8:
				return this.data.getLong();
			default:
				throw new IllegalArgumentException();
		}
	}

	/** {@inheritDoc} */
	@Override
	public float readFloat() throws IOException {
		return this.data.getFloat();
	}

	/** {@inheritDoc} */
	@Override
	public double readDouble() throws IOException {
		return this.data.getDouble();
	}

	/** {@inheritDoc} */
	@Override
	public void seek(final long index) throws IOException {
		this.data.position((int)index);
	}

	/** {@inheritDoc} */
	@Override
	public long index() throws IOException {
		return this.data.position();
	}

	/** {@inheritDoc} */
	@Override
	public long length() throws IOException {
		return this.data.limit();
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws IOException {
	}

}