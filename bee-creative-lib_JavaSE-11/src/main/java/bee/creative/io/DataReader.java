package bee.creative.io;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import bee.creative.lang.Bytes;

/** Diese Klasse implementiert einen {@link Closeable} {@link DataInput} .
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class DataReader implements DataInput, Closeable {

	@Override
	public int skipBytes(int n) throws IOException {
		this.seek(this.index() + n);
		return n;
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		this.readFully(b, 0, b.length);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return this.readByte() != 0;
	}

	@Override
	public byte readByte() throws IOException {
		this.readFully(this.array, 0, 1);
		return this.array[0];
	}

	@Override
	public int readUnsignedByte() throws IOException {
		this.readFully(this.array, 0, 1);
		return Bytes.getInt1(this.array, 0);
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
		this.readFully(this.array, 0, 2);
		return (char)Bytes.getInt2BE(this.array, 0);
	}

	@Override
	public int readInt() throws IOException {
		this.readFully(this.array, 0, 4);
		return Bytes.getInt4BE(this.array, 0);
	}

	/** Diese Methode liest die gegebene Anzahl an {@code byte} und gibt diese als {@code int} interpretiert zurück.
	 *
	 * @see Bytes#getIntBE(byte[], int, int)
	 * @param size Anzahl der {@code byte} (0..4).
	 * @return Zahlenwert.
	 * @throws IOException Wenn ein I/O Fehler eintritt. */
	public int readInt(int size) throws IOException {
		this.readFully(this.array, 0, size);
		return Bytes.getIntBE(this.array, 0, size);
	}

	@Override
	public long readLong() throws IOException {
		this.readFully(this.array, 0, 8);
		return Bytes.getLong8BE(this.array, 0);
	}

	/** Diese Methode liest die gegebene Anzahl an {@code byte} und gibt diese als {@code long} interpretiert zurück.
	 *
	 * @see Bytes#getLongBE(byte[], int, int)
	 * @param size Anzahl der {@code byte} (0..8).
	 * @return Zahlenwert.
	 * @throws IOException Wenn ein I/O Fehler eintritt. */
	public long readLong(int size) throws IOException {
		this.readFully(this.array, 0, size);
		return Bytes.getLongBE(this.array, 0, size);
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
		var result = new StringBuffer();
		try {
			for (int value; true;) {
				switch (value = this.readUnsignedByte()) {
					case '\r':
						var cur = this.index();
						if (this.readUnsignedByte() == '\n') return result.toString();
						this.seek(cur);
					case '\n':
						return result.toString();
					default:
						result.append((char)value);
				}
			}
		} catch (EOFException cause) {
			if (result.length() == 0) return null;
			return result.toString();
		}
	}

	@Override
	public String readUTF() throws IOException {
		return DataInputStream.readUTF(this);
	}

	/** Diese Methode setzt die Navigationsposition der Eingabe, ab der die nächsten {@code byte} gelesen bzw. geschrieben werden können.
	 *
	 * @see #index()
	 * @param index Leseposition.
	 * @throws IOException Wenn die gegebene Position negativ ist, ein I/O-Fehler auftritt, oder die Position nicht gesetzt werden kann. */
	public void seek(long index) throws IOException {
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

	/** Diese Methode gibt die intern verwalteten Nutzdaten zurück.
	 *
	 * @return Nutzdaten. */
	public abstract Object wrappedSource();

	/** Dieses Feld speichert den Lesepuffer. */
	protected final byte[] array = new byte[8];

}