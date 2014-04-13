package bee.creative.data;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import bee.creative.array.ByteArraySection;
import bee.creative.array.CompactByteArray;
import bee.creative.util.Bytes;

/**
 * Diese Schnittstelle definiert die modifizierbare Navigationsposition zum wahlfreien Lesen bzw. Schreben von Daten.
 * 
 * @see DataInput
 * @see DataOutput
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Data {

	/**
	 * Diese Schnittstelle definiert eine Erweiterung eines {@link Closeable} {@link DataInput} um die in {@link Data} spezifizierte Navigationsposition.
	 * 
	 * @see Data
	 * @see DataInput
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface DataSource extends Data, DataInput, Closeable {

		/**
		 * Diese Methode liest die gegebene Anzahl an {@code byte}s und gibt diese als {@code int} interpretiert zurück.
		 * 
		 * @see Bytes#getInt(byte[], int, int)
		 * @param size Anzahl der {@code byte}s (0..4).
		 * @return Zahlenwert.
		 * @throws IOException Wenn ein I/O Fehler eintritt.
		 */
		public int readInt(int size) throws IOException;

		/**
		 * Diese Methode liest die gegebene Anzahl an {@code byte}s und gibt diese als {@code long} interpretiert zurück.
		 * 
		 * @see Bytes#getLong(byte[], int, int)
		 * @param size Anzahl der {@code byte}s (0..8).
		 * @return Zahlenwert.
		 * @throws IOException Wenn ein I/O Fehler eintritt.
		 */
		public long readLong(int size) throws IOException;

	}

	/**
	 * Diese Schnittstelle definiert eine Erweiterung einer {@link Closeable} {@link DataOutput} um die in {@link Data} spezifizierte Navigationsposition.
	 * 
	 * @see Data
	 * @see DataOutput
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface DataTarget extends Data, DataOutput, Closeable {

		/**
		 * Diese Methode schreibt die gegebene Anzahl an {@code byte}s des gegebenen Zahlenwerts.
		 * 
		 * @see Bytes#setInt(byte[], int, int, int)
		 * @param v Zahlenwert.
		 * @param size Anzahl der {@code byte}s (0..4).
		 * @throws IOException Wenn ein I/O Fehler eintritt.
		 */
		public void writeInt(int v, int size) throws IOException;

		/**
		 * Diese Methode schreibt die gegebene Anzahl an {@code byte}s des gegebenen Zahlenwerts.
		 * 
		 * @see Bytes#setLong(byte[], int, long, int)
		 * @param v Zahlenwert.
		 * @param size Anzahl der {@code byte}s (0..8).
		 * @throws IOException Wenn ein I/O Fehler eintritt.
		 */
		public void writeLong(long v, int size) throws IOException;

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link DataSource}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class AbstractDataSource implements DataSource {

		/**
		 * Dieses Feld speichert den Lesepuffer.
		 */
		final byte[] array = new byte[8];

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void readFully(final byte[] b) throws IOException {
			this.readFully(b, 0, b.length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int skipBytes(final int n) throws IOException {
			this.seek(this.index() + n);
			return n;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean readBoolean() throws IOException {
			return this.readByte() != 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final byte readByte() throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, 1);
			return array[0];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int readUnsignedByte() throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, 1);
			return Bytes.get1(array, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final short readShort() throws IOException {
			return (short)this.readChar();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int readUnsignedShort() throws IOException {
			return this.readChar();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final char readChar() throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, 2);
			return (char)Bytes.get2(array, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int readInt() throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, 4);
			return Bytes.get4(array, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int readInt(final int size) throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, size);
			return Bytes.getInt(array, 0, size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final long readLong() throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, 8);
			return Bytes.get8(array, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final long readLong(final int size) throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, size);
			return Bytes.getLong(array, 0, size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final float readFloat() throws IOException {
			return Float.intBitsToFloat(this.readInt());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final double readDouble() throws IOException {
			return Double.longBitsToDouble(this.readLong());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final String readLine() throws IOException {
			final StringBuffer input = new StringBuffer();
			final byte[] array = this.array;
			try{
				for(int value; true;){
					this.readFully(array, 0, 1);
					switch(value = array[0]){
						case '\r':
							final long cur = this.index();
							this.readFully(array, 0, 1);
							if(array[0] == '\n') return input.toString();
							this.seek(cur);
						case '\n':
							return input.toString();
						default:
							input.append((char)value);
					}
				}
			}catch(final EOFException e){
				if(input.length() == 0) return null;
				return input.toString();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final String readUTF() throws IOException {
			return DataInputStream.readUTF(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link DataTarget}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class AbstractDataTarget implements DataTarget {

		/**
		 * Dieses Feld speichert den Schreibpuffer.
		 */
		final byte[] array = new byte[8];

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void write(final int b) throws IOException {
			this.writeByte(b);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void write(final byte[] b) throws IOException {
			this.write(b, 0, b.length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void writeBoolean(final boolean v) throws IOException {
			this.write(v ? 1 : 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void writeByte(final int v) throws IOException {
			final byte[] array = this.array;
			Bytes.set1(array, 0, v);
			this.write(array, 0, 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void writeShort(final int v) throws IOException {
			final byte[] array = this.array;
			Bytes.set2(array, 0, v);
			this.write(array, 0, 2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void writeChar(final int v) throws IOException {
			this.writeShort(v);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void writeInt(final int v) throws IOException {
			final byte[] array = this.array;
			Bytes.set4(array, 0, v);
			this.write(array, 0, 4);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeInt(final int v, final int size) throws IOException {
			final byte[] array = this.array;
			Bytes.setInt(array, 0, v, size);
			this.write(array, 0, size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void writeLong(final long v) throws IOException {
			final byte[] array = this.array;
			Bytes.set8(array, 0, v);
			this.write(array, 0, 8);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeLong(final long v, final int size) throws IOException {
			final byte[] array = this.array;
			Bytes.setLong(array, 0, v, size);
			this.write(array, 0, size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void writeFloat(final float v) throws IOException {
			this.writeInt(Float.floatToIntBits(v));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void writeDouble(final double v) throws IOException {
			this.writeLong(Double.doubleToLongBits(v));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void writeBytes(final String s) throws IOException {
			final int len = s.length();
			final byte[] data = new byte[len];
			for(int i = 0; i < len; i++){
				Bytes.set1(data, i, s.charAt(i));
			}
			this.write(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void writeChars(final String s) throws IOException {
			final int len = s.length();
			final byte[] data = new byte[len * 2];
			for(int i = 0; i < len; i++){
				Bytes.set2(data, i << 1, s.charAt(i));
			}
			this.write(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void writeUTF(final String s) throws IOException {
			new DataOutputStream(null) {

				{
					this.out = this;
				}

				@Override
				public synchronized void write(final byte[] b, final int off, final int len) throws IOException {
					AbstractDataTarget.this.write(b, off, len);
				}

			}.writeUTF(s);
		}

	}

	/**
	 * Diese Klasse implementiert die {@link DataSource}-Schnittstelle zu einem {@link RandomAccessFile}.
	 * 
	 * @see RandomAccessFile
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DataSourceFile extends AbstractDataSource {

		/**
		 * Dieses Feld speichert die Nutzdaten.
		 */
		final RandomAccessFile data;

		/**
		 * Dieser Konstruktor initialisiert das {@link RandomAccessFile} mit dem gegebenen {@link File} im Modus {@code "r"}.
		 * 
		 * @see RandomAccessFile#RandomAccessFile(File, String)
		 * @param file {@link File}.
		 * @throws FileNotFoundException Wenn der Dateiname ungültig ist.
		 */
		public DataSourceFile(final File file) throws FileNotFoundException {
			this(new RandomAccessFile(file, "r"));
		}

		/**
		 * Dieser Konstruktor initialisiert das {@link RandomAccessFile} mit dem gegebenen Dateinamen im Modus {@code "r"}.
		 * 
		 * @see RandomAccessFile#RandomAccessFile(String, String)
		 * @param name Dateiname.
		 * @throws FileNotFoundException Wenn der Dateiname ungültig ist.
		 */
		public DataSourceFile(final String name) throws FileNotFoundException {
			this(new RandomAccessFile(name, "r"));
		}

		/**
		 * Dieser Konstruktor initialisiert das {@link RandomAccessFile}.
		 * 
		 * @param file {@link RandomAccessFile}.
		 * @throws NullPointerException Wenn das {@link RandomAccessFile} {@code null} ist.
		 */
		public DataSourceFile(final RandomAccessFile file) throws NullPointerException {
			if(file == null) throw new NullPointerException();
			this.data = file;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final RandomAccessFile data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void readFully(final byte[] array, final int offset, final int length) throws IOException {
			this.data.readFully(array, offset, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void seek(final long index) throws IOException {
			this.data.seek(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final long index() throws IOException {
			return this.data.getFilePointer();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void close() throws IOException {
			this.data.close();
		}

	}

	/**
	 * Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einer {@link ByteArraySection}.
	 * 
	 * @see ByteArraySection
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DataSourceArray extends AbstractDataSource {

		/**
		 * Dieses Feld speichert die Nutzdaten.
		 */
		final ByteArraySection data;

		/**
		 * Dieses Feld speichert die Leseposition.
		 */
		int index;

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist.
		 */
		public DataSourceArray(final byte... data) throws NullPointerException {
			this.data = ByteArraySection.from(data);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist.
		 */
		public DataSourceArray(final ByteArraySection data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final ByteArraySection data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void readFully(final byte[] array, final int offset, final int length) throws IOException {
			final ByteArraySection data = this.data;
			final int index = this.index, index2 = index + length;
			if(index2 > data.size()) throw new EOFException();
			System.arraycopy(data.array(), data.startIndex() + index, array, offset, length);
			this.index = index2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void seek(final long index) throws IOException {
			this.index = (int)index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final long index() throws IOException {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void close() throws IOException {
		}

	}

	/**
	 * Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link RandomAccessFile}.
	 * 
	 * @see RandomAccessFile
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DataTargetFile extends AbstractDataTarget {

		/**
		 * Dieses Feld speichert die Nutzdaten.
		 */
		final RandomAccessFile data;

		/**
		 * Dieser Konstruktor initialisiert das {@link RandomAccessFile} mit dem gegebenen {@link File} im Modus {@code "rw"}.
		 * 
		 * @see RandomAccessFile#RandomAccessFile(File, String)
		 * @param file {@link File}.
		 * @throws FileNotFoundException Wenn der Dateiname ungültig ist.
		 */
		public DataTargetFile(final File file) throws FileNotFoundException {
			this(new RandomAccessFile(file, "rw"));
		}

		/**
		 * Dieser Konstruktor initialisiert das {@link RandomAccessFile} mit dem gegebenen Dateinamen im Modus {@code "rw"}.
		 * 
		 * @see RandomAccessFile#RandomAccessFile(String, String)
		 * @param name Dateiname.
		 * @throws FileNotFoundException Wenn der Dateiname ungültig ist.
		 */
		public DataTargetFile(final String name) throws FileNotFoundException {
			this(new RandomAccessFile(name, "rw"));
		}

		/**
		 * Dieser Konstruktor initialisiert das {@link RandomAccessFile}.
		 * 
		 * @param file {@link RandomAccessFile}.
		 * @throws NullPointerException Wenn das {@link RandomAccessFile} {@code null} ist.
		 */
		public DataTargetFile(final RandomAccessFile file) throws NullPointerException {
			if(file == null) throw new NullPointerException();
			this.data = file;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final RandomAccessFile data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void write(final byte[] array, final int offset, final int length) throws IOException {
			this.data.write(array, offset, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void seek(final long index) throws IOException {
			this.data.seek(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final long index() throws IOException {
			return this.data.getFilePointer();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void close() throws IOException {
			this.data.close();
		}

	}

	/**
	 * Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link CompactByteArray}.
	 * 
	 * @see CompactByteArray
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public class DataTargetArray extends AbstractDataTarget {

		/**
		 * Dieses Feld speichert die Nutzdaten.
		 */
		final CompactByteArray data;

		/**
		 * Dieses Feld speichert die Schreibeposition.
		 */
		int index;

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit 128 Byte Größe.
		 */
		public DataTargetArray() {
			this(128);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten mit der gegebenen Größe.
		 * 
		 * @see CompactByteArray#CompactByteArray(int)
		 * @param size Größe.
		 */
		public DataTargetArray(final int size) {
			this(new CompactByteArray(size));
			this.data.setAlignment(0);
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn die Nutzdaten {@code null} sind.
		 */
		public DataTargetArray(final CompactByteArray data) throws NullPointerException {
			if(data == null) throw new NullPointerException();
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final CompactByteArray data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void write(final byte[] array, final int offset, final int length) throws IOException {
			if((offset < 0) || ((offset + length) > array.length)) throw new IndexOutOfBoundsException();
			final CompactByteArray data = this.data;
			final int size = data.size(), index = this.index, index2 = index + length;
			data.insert(size, Math.max(index2 - size, 0));
			System.arraycopy(array, offset, data.array(), data.startIndex() + index, length);
			this.index = index2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void seek(final long index) throws IOException {
			this.index = (int)index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final long index() throws IOException {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void close() throws IOException {
		}

	}

	/**
	 * Diese Methode gibt die intern verwalteten Nutzdaten zurück.
	 * 
	 * @return Nutzdaten.
	 */
	public Object data();

	/**
	 * Diese Methode setzt die Navigationsposition der Eingabe, ab der die nächsten {@code byte}s gelesen bzw. geschrieben werden können.
	 * 
	 * @see #index()
	 * @param index Leseposition.
	 * @throws IOException Wenn die gegebene Position negativ ist oder ein I/O-Fehler auftritt.
	 */
	public void seek(long index) throws IOException;

	/**
	 * Diese Methode gibt die aktuelle Navigationsposition zurück, ab der die nächsten {@code byte}s gelesen bzw. geschrieben werden können.
	 * 
	 * @see #seek(long)
	 * @return Leseposition.
	 * @throws IOException Wenn ein I/O-Fehler auftritt.
	 */
	public long index() throws IOException;

}