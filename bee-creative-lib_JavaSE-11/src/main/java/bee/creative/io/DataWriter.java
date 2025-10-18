package bee.creative.io;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import bee.creative.lang.Bytes;

/** Diese Klasse implementiert einen {@link Closeable} {@link DataOutput}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class DataWriter implements DataOutput, Closeable {

	@Override
	public void write(int b) throws IOException {
		this.writeByte(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		this.write(b, 0, b.length);
	}

	@Override
	public void writeBoolean(boolean v) throws IOException {
		this.write(v ? 1 : 0);
	}

	@Override
	public void writeByte(int v) throws IOException {
		Bytes.setInt1(this.array, 0, v);
		this.write(this.array, 0, 1);
	}

	@Override
	public void writeShort(int v) throws IOException {
		Bytes.setInt2BE(this.array, 0, v);
		this.write(this.array, 0, 2);
	}

	@Override
	public void writeChar(int v) throws IOException {
		this.writeShort(v);
	}

	@Override
	public void writeInt(int v) throws IOException {
		Bytes.setInt4BE(this.array, 0, v);
		this.write(this.array, 0, 4);
	}

	/** Diese Methode schreibt die gegebene Anzahl an {@code byte} des gegebenen Zahlenwerts.
	 *
	 * @see Bytes#setIntBE(byte[], int, int, int)
	 * @param v Zahlenwert.
	 * @param size Anzahl der {@code byte} (0..4).
	 * @throws IOException Wenn ein I/O Fehler eintritt. */
	public void writeInt(int v, int size) throws IOException {
		Bytes.setIntBE(this.array, 0, size, v);
		this.write(this.array, 0, size);
	}

	@Override
	public void writeLong(long v) throws IOException {
		Bytes.setLong8BE(this.array, 0, v);
		this.write(this.array, 0, 8);
	}

	/** Diese Methode schreibt die gegebene Anzahl an {@code byte} des gegebenen Zahlenwerts.
	 *
	 * @see Bytes#setLongBE(byte[], int, int, long)
	 * @param v Zahlenwert.
	 * @param size Anzahl der {@code byte} (0..8).
	 * @throws IOException Wenn ein I/O Fehler eintritt. */
	public void writeLong(long v, int size) throws IOException {
		Bytes.setLongBE(this.array, 0, size, v);
		this.write(this.array, 0, size);
	}

	@Override
	public void writeFloat(float v) throws IOException {
		this.writeInt(Float.floatToIntBits(v));
	}

	@Override
	public void writeDouble(double v) throws IOException {
		this.writeLong(Double.doubleToLongBits(v));
	}

	@Override
	public void writeBytes(String s) throws IOException {
		for (int i = 0, size = s.length(); i < size; i++) {
			this.write(s.charAt(i));
		}
	}

	@Override
	public void writeChars(String s) throws IOException {
		for (int i = 0, size = s.length(); i < size; i++) {
			this.writeChar(s.charAt(i));
		}
	}

	@Override
	public void writeUTF(String s) throws IOException {
		new DataOutputStream(null) {

			{
				this.out = this;
			}

			@Override
			public synchronized void write(byte[] b, int off, int len) throws IOException {
				DataWriter.this.write(b, off, len);
			}

			@Override
			public void flush() throws IOException {
			}

			@Override
			public void close() throws IOException {
			}

		}.writeUTF(s);
	}

	/** Diese Methode setzt die Navigationsposition der Eingabe, ab der die nächsten {@code byte} gelesen bzw. geschrieben werden können.
	 *
	 * @see #index()
	 * @param index Leseposition.
	 * @throws IOException Wenn die gegebene Position negativ ist, ein I/O-Fehler auftritt, oder die Position nicht gesetzt werden kann. */
	public void seek(final long index) throws IOException {
		throw new IOException();
	}

	/** Diese Methode gibt die aktuelle Navigationsposition zurück, ab der die nächsten {@code byte} gelesen bzw. geschrieben werden können.
	 *
	 * @see #seek(long)
	 * @return Leseposition.
	 * @throws IOException Wenn ein I/O-Fehler auftritt oder die Position nicht bestimmt werden kann. */
	public long index() throws IOException {
		throw new IOException();
	}

	/** Diese Methode gibt die aktuelle Länge der Nutzdaten als Anzahl von {@code byte} zurück. Wenn die Navigationsposition bem Lesen größer oder gleich dieser
	 * Anzahl werden würde, wird beim Lesen eine {@link EOFException} ausgelöst. Beim Schreiben wird die Anzahl automatisch vergrößert, wenn dies nötig wird.
	 *
	 * @see #index()
	 * @return Anzahl verfügbarer {@code byte}.
	 * @throws IOException Wenn ein I/O-Fehler auftritt oder die Länge nicht bestimmt werden kann. */
	public long length() throws IOException {
		throw new IOException();
	}

	/** Diese Methode setzt die Länge der Nutzdaten. Die Navigationsposition wird dabei falls nötig auf den gegebenen Wert verkleinert.
	 *
	 * @see #length()
	 * @param value Anzahl verfügbarer {@code byte}.
	 * @throws IOException Wenn ein I/O-Fehler auftritt oder die Lönge nicht gesetzt werden kann. */
	public void allocate(long value) throws IOException {
		throw new IOException();
	}

	/** Diese Methode gibt die intern verwalteten Nutzdaten zurück.
	 *
	 * @return Nutzdaten. */
	public abstract Object wrappedTarget();

	/** Dieses Feld speichert den Schreibpuffer. */
	protected final byte[] array = new byte[8];

}