package bee.creative.io;

import java.io.DataOutputStream;
import java.io.IOException;
import bee.creative.lang.Bytes;
import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link DataTarget}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class BaseDataTarget extends BaseObject implements DataTarget {

	/** Dieses Feld speichert den Schreibpuffer. */
	protected final byte[] array = new byte[8];

	@Override
	public void write(final int b) throws IOException {
		this.writeByte(b);
	}

	@Override
	public void write(final byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}

	@Override
	public void writeBoolean(final boolean v) throws IOException {
		this.write(v ? 1 : 0);
	}

	@Override
	public void writeByte(final int v) throws IOException {
		final byte[] array = this.array;
		Bytes.setInt1(array, 0, v);
		this.write(array, 0, 1);
	}

	@Override
	public void writeShort(final int v) throws IOException {
		final byte[] array = this.array;
		Bytes.setInt2BE(array, 0, v);
		this.write(array, 0, 2);
	}

	@Override
	public void writeChar(final int v) throws IOException {
		this.writeShort(v);
	}

	@Override
	public void writeInt(final int v) throws IOException {
		final byte[] array = this.array;
		Bytes.setInt4BE(array, 0, v);
		this.write(array, 0, 4);
	}

	@Override
	public void writeInt(final int v, final int size) throws IOException {
		final byte[] array = this.array;
		Bytes.setIntBE(array, 0, size, v);
		this.write(array, 0, size);
	}

	@Override
	public void writeLong(final long v) throws IOException {
		final byte[] array = this.array;
		Bytes.setLong8BE(array, 0, v);
		this.write(array, 0, 8);
	}

	@Override
	public void writeLong(final long v, final int size) throws IOException {
		final byte[] array = this.array;
		Bytes.setLongBE(array, 0, size, v);
		this.write(array, 0, size);
	}

	@Override
	public void writeFloat(final float v) throws IOException {
		this.writeInt(Float.floatToIntBits(v));
	}

	@Override
	public void writeDouble(final double v) throws IOException {
		this.writeLong(Double.doubleToLongBits(v));
	}

	@Override
	@SuppressWarnings ("deprecation")
	public void writeBytes(final String s) throws IOException {
		final int len = s.length();
		final byte[] data = new byte[len];
		s.getBytes(0, len, data, 0);
		this.write(data);
	}

	@Override
	public void writeChars(final String s) throws IOException {
		for (int i = 0, size = s.length(); i < size; i++) {
			this.writeChar(s.charAt(i));
		}
	}

	@Override
	public void writeUTF(final String s) throws IOException {
		new DataOutputStream(null) {

			{
				this.out = this;
			}

			@Override
			public synchronized void write(final byte[] b, final int off, final int len) throws IOException {
				BaseDataTarget.this.write(b, off, len);
			}

			@Override
			public void flush() throws IOException {
			}

			@Override
			public void close() throws IOException {
			}

		}.writeUTF(s);
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

	@Override
	public void allocate(final long value) throws IOException {
		throw new IOException();
	}

}