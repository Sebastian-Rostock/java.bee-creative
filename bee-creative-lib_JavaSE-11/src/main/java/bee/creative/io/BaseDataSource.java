package bee.creative.io;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import bee.creative.lang.Bytes;
import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link DataSource}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class BaseDataSource extends BaseObject implements DataSource {

	/** Dieses Feld speichert den Lesepuffer. */
	protected final byte[] array = new byte[8];

	@Override
	public int skipBytes(final int n) throws IOException {
		this.seek(this.index() + n);
		return n;
	}

	@Override
	public void readFully(final byte[] b) throws IOException {
		this.readFully(b, 0, b.length);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return this.readByte() != 0;
	}

	@Override
	public byte readByte() throws IOException {
		final byte[] array = this.array;
		this.readFully(array, 0, 1);
		return array[0];
	}

	@Override
	public int readUnsignedByte() throws IOException {
		final byte[] array = this.array;
		this.readFully(array, 0, 1);
		return Bytes.getInt1(array, 0);
	}

	@Override
	public short readShort() throws IOException {
		return (short)this.readChar();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return this.readChar();
	}

	@Override
	public char readChar() throws IOException {
		final byte[] array = this.array;
		this.readFully(array, 0, 2);
		return (char)Bytes.getInt2BE(array, 0);
	}

	@Override
	public int readInt() throws IOException {
		final byte[] array = this.array;
		this.readFully(array, 0, 4);
		return Bytes.getInt4BE(array, 0);
	}

	@Override
	public int readInt(final int size) throws IOException {
		final byte[] array = this.array;
		this.readFully(array, 0, size);
		return Bytes.getIntBE(array, 0, size);
	}

	@Override
	public long readLong() throws IOException {
		final byte[] array = this.array;
		this.readFully(array, 0, 8);
		return Bytes.getLong8BE(array, 0);
	}

	@Override
	public long readLong(final int size) throws IOException {
		final byte[] array = this.array;
		this.readFully(array, 0, size);
		return Bytes.getLongBE(array, 0, size);
	}

	@Override
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(this.readInt());
	}

	@Override
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(this.readLong());
	}

	@Override
	public String readLine() throws IOException {
		final StringBuffer result = new StringBuffer();
		try {
			for (int value; true;) {
				switch (value = this.readUnsignedByte()) {
					case '\r':
						final long cur = this.index();
						if (this.readUnsignedByte() == '\n') return result.toString();
						this.seek(cur);
					case '\n':
						return result.toString();
					default:
						result.append((char)value);
				}
			}
		} catch (final EOFException cause) {
			if (result.length() == 0) return null;
			return result.toString();
		}
	}

	@Override
	public String readUTF() throws IOException {
		return DataInputStream.readUTF(this);
	}

	@Override
	public void seek(final long index) throws IOException {
		throw new IOException();
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public long index() throws IOException {
		throw new IOException();
	}

	@Override
	public long length() throws IOException {
		throw new IOException();
	}

}