package bee.creative.data;

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bee.creative.util.Bytes;

/**
 * Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link ByteBuffer}.
 * 
 * @see ByteBuffer
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class BufferDataSource extends BaseDataSource {

	/**
	 * Dieses Feld speichert die Nutzdaten.
	 */
	final ByteBuffer __data;

	/**
	 * Dieser Konstruktor initialisiert die Nutzdaten.
	 * 
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist.
	 */
	public BufferDataSource(final byte... data) throws NullPointerException {
		this(ByteBuffer.wrap(data));
	}

	/**
	 * Dieser Konstruktor initialisiert die Nutzdaten.
	 * 
	 * @param data Nutzdaten.
	 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist.
	 */
	public BufferDataSource(final ByteBuffer data) throws NullPointerException {
		if (data == null) throw new NullPointerException("data = null");
		this.__data = data.slice().order(ByteOrder.BIG_ENDIAN);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ByteBuffer data() {
		return this.__data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void readFully(final byte[] array, final int offset, final int length) throws IOException {
		try {
			this.__data.get(array, offset, length);
		} catch (final BufferUnderflowException e) {
			throw new EOFException();
		} catch (final IndexOutOfBoundsException e) {
			throw new EOFException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean readBoolean() throws IOException {
		return this.readByte() != 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte readByte() throws IOException {
		return this.__data.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int readUnsignedByte() throws IOException {
		return this.__data.get() & 0xFF;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short readShort() throws IOException {
		return this.__data.getShort();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int readUnsignedShort() throws IOException {
		return this.__data.getShort() & 0xFFFF;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public char readChar() throws IOException {
		return this.__data.getChar();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int readInt() throws IOException {
		return this.__data.getInt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int readInt(final int size) throws IOException {
		switch (size) {
			case 0:
				return 0;
			case 1:
				return this.__data.get() & 0xFF;
			case 2:
				return this.__data.getShort() & 0xFFFF;
			case 3:
				this.__data.get(this.array, 0, 3);
				return Bytes.getInt3BE(this.array, 0);
			case 4:
				return this.__data.getInt();
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long readLong() throws IOException {
		return this.__data.getLong();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long readLong(final int size) throws IOException {
		switch (size) {
			case 0:
				return 0;
			case 1:
				return this.__data.get() & 0xFF;
			case 2:
				return this.__data.getShort() & 0xFFFF;
			case 3:
				this.__data.get(this.array, 0, 3);
				return Bytes.getInt3BE(this.array, 0);
			case 4:
				return this.__data.getInt();
			case 5:
				this.__data.get(this.array, 0, 5);
				return Bytes.getLong5BE(this.array, 0);
			case 6:
				this.__data.get(this.array, 0, 6);
				return Bytes.getLong6BE(this.array, 0);
			case 7:
				this.__data.get(this.array, 0, 7);
				return Bytes.getLong7BE(this.array, 0);
			case 8:
				return this.__data.getLong();
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float readFloat() throws IOException {
		return this.__data.getFloat();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double readDouble() throws IOException {
		return this.__data.getDouble();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void seek(final long index) throws IOException {
		this.__data.position((int)index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long index() throws IOException {
		return this.__data.position();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long length() throws IOException {
		return this.__data.limit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
	}

}