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
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

		/**
		 * Diese Methode setzt die Länge der Nutzdaten. Die Navigationsposition wird dabei falls nötig auf den gegebenen Wert verkleinert.
		 * 
		 * @see #length()
		 * @param value Anzahl verfügbarer {@code byte}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 */
		public void allocate(long value) throws IOException;

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
		protected final byte[] array = new byte[8];

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int skipBytes(final int n) throws IOException {
			this.seek(this.index() + n);
			return n;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void readFully(final byte[] b) throws IOException {
			this.readFully(b, 0, b.length);
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
			final byte[] array = this.array;
			this.readFully(array, 0, 1);
			return array[0];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int readUnsignedByte() throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, 1);
			return Bytes.get1(array, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public short readShort() throws IOException {
			return (short)this.readChar();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int readUnsignedShort() throws IOException {
			return this.readChar();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public char readChar() throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, 2);
			return (char)Bytes.get2(array, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int readInt() throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, 4);
			return Bytes.get4(array, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int readInt(final int size) throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, size);
			return Bytes.getInt(array, 0, size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long readLong() throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, 8);
			return Bytes.get8(array, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long readLong(final int size) throws IOException {
			final byte[] array = this.array;
			this.readFully(array, 0, size);
			return Bytes.getLong(array, 0, size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public float readFloat() throws IOException {
			return Float.intBitsToFloat(this.readInt());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public double readDouble() throws IOException {
			return Double.longBitsToDouble(this.readLong());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String readLine() throws IOException {
			final StringBuffer result = new StringBuffer();
			try{
				for(int value; true;){
					switch(value = this.readUnsignedByte()){
						case '\r':
							final long cur = this.index();
							if(this.readUnsignedByte() == '\n') return result.toString();
							this.seek(cur);
						case '\n':
							return result.toString();
						default:
							result.append((char)value);
					}
				}
			}catch(final EOFException e){
				if(result.length() == 0) return null;
				return result.toString();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String readUTF() throws IOException {
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
		protected final byte[] array = new byte[8];

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final int b) throws IOException {
			this.writeByte(b);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final byte[] b) throws IOException {
			this.write(b, 0, b.length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeBoolean(final boolean v) throws IOException {
			this.write(v ? 1 : 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeByte(final int v) throws IOException {
			final byte[] array = this.array;
			Bytes.set1(array, 0, v);
			this.write(array, 0, 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeShort(final int v) throws IOException {
			final byte[] array = this.array;
			Bytes.set2(array, 0, v);
			this.write(array, 0, 2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeChar(final int v) throws IOException {
			this.writeShort(v);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeInt(final int v) throws IOException {
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
		public void writeLong(final long v) throws IOException {
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
		public void writeFloat(final float v) throws IOException {
			this.writeInt(Float.floatToIntBits(v));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeDouble(final double v) throws IOException {
			this.writeLong(Double.doubleToLongBits(v));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeBytes(final String s) throws IOException {
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
		public void writeChars(final String s) throws IOException {
			for(int i = 0, size = s.length(); i < size; i++){
				this.writeChar(s.charAt(i));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeUTF(final String s) throws IOException {
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
		private final RandomAccessFile data;

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
		public RandomAccessFile data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void readFully(final byte[] array, final int offset, final int length) throws IOException {
			this.data.readFully(array, offset, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void seek(final long index) throws IOException {
			this.data.seek(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long index() throws IOException {
			return this.data.getFilePointer();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long length() throws IOException {
			return this.data.length();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() throws IOException {
			this.data.close();
		}

	}

	/**
	 * Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einer {@link ByteArraySection}. Die Dekidierung der Zahlen erfolgt via {@link Bytes} und
	 * damit in {@link ByteOrder#BIG_ENDIAN}.
	 * 
	 * @see ByteArraySection
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DataSourceArray extends AbstractDataSource {

		/**
		 * Dieses Feld speichert die Nutzdaten.
		 */
		private final ByteArraySection data;

		/**
		 * Dieses Feld speichert die Leseposition.
		 */
		private int index;

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
		public ByteArraySection data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void readFully(final byte[] array, final int offset, final int length) throws IOException {
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
		public void seek(final long index) throws IOException {
			this.index = (int)index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long index() throws IOException {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long length() throws IOException {
			return this.data.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() throws IOException {
		}

	}

	/**
	 * Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link ByteBuffer}.
	 * 
	 * @see ByteBuffer
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DataSourceBuffer extends AbstractDataSource {

		/**
		 * Dieses Feld speichert die Nutzdaten.
		 */
		private final ByteBuffer data;

		/**
		 * Dieses Feld speichert den {@link ByteBuffer} zu {@link #array}.
		 */
		private final ByteBuffer buffer;

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist.
		 */
		public DataSourceBuffer(final byte... data) throws NullPointerException {
			this(ByteBuffer.wrap(data));
		}

		/**
		 * Dieser Konstruktor initialisiert die Nutzdaten.
		 * 
		 * @param data Nutzdaten.
		 * @throws NullPointerException Wenn die gegebenen Nutzdaten {@code null} ist.
		 * @throws IllegalArgumentException Wenn {@link ByteBuffer#order()} nicht {@link ByteOrder#BIG_ENDIAN} ist.
		 */
		public DataSourceBuffer(final ByteBuffer data) throws NullPointerException, IllegalArgumentException {
			if(data.order() != ByteOrder.BIG_ENDIAN) throw new IllegalArgumentException();
			this.data = data;
			this.buffer = ByteBuffer.wrap(this.array);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ByteBuffer data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void readFully(final byte[] array, final int offset, final int length) throws IOException {
			try{
				this.data.get(array, offset, length);
			}catch(final BufferUnderflowException e){
				throw new EOFException();
			}catch(final IndexOutOfBoundsException e){
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
			return this.data.get();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int readUnsignedByte() throws IOException {
			return this.data.get() & 0xFF;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public short readShort() throws IOException {
			return this.data.getShort();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int readUnsignedShort() throws IOException {
			return this.data.getShort() & 0xFFFF;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public char readChar() throws IOException {
			return this.data.getChar();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int readInt() throws IOException {
			return this.data.getInt();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int readInt(final int size) throws IOException {
			switch(size){
				case 0:
					return 0;
				case 1:
					return this.data.get() & 0xFF;
				case 2:
					return this.data.getShort() & 0xFFFF;
				case 3:
					this.data.get(this.array, 1, 3);
					return this.buffer.getInt(0) & 0xFFFFFF;
				case 4:
					return this.data.getInt();
				default:
					throw new IllegalArgumentException();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long readLong() throws IOException {
			return this.data.getLong();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long readLong(final int size) throws IOException {
			switch(size){
				case 0:
					return 0;
				case 1:
					return this.data.get() & 0xFF;
				case 2:
					return this.data.getShort() & 0xFFFF;
				case 3:
					this.data.get(this.array, 1, 3);
					return this.buffer.getInt(0) & 0xFFFFFF;
				case 4:
					return this.data.getInt();
				case 5:
					this.data.get(this.array, 3, 5);
					return this.buffer.getLong(0) & 0xFFFFFFFFFFL;
				case 6:
					this.data.get(this.array, 2, 6);
					return this.buffer.getLong(0) & 0xFFFFFFFFFFFFL;
				case 7:
					this.data.get(this.array, 1, 7);
					return this.buffer.getLong(0) & 0xFFFFFFFFFFFFFFL;
				case 8:
					return this.data.getLong();
				default:
					throw new IllegalArgumentException();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public float readFloat() throws IOException {
			return this.data.getFloat();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public double readDouble() throws IOException {
			return this.data.getDouble();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void seek(final long index) throws IOException {
			this.data.position((int)index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long index() throws IOException {
			return this.data.position();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long length() throws IOException {
			return this.data.limit();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() throws IOException {
		}

	}

	/**
	 * Diese Klasse implementiert eine gepufferte {@link DataSource}, welche Datenauszüge einer gegebenen {@link DataSource} in einer internen Verwaltung zur
	 * Wiederverwendung vorhält. Wenn der {@link #getCacheSize() Pufferspeicher} voll ist, wird die weniger häufig genutzte Hälfte der Datenauszüge wieder
	 * freigegeben. Die Auszüge werden in Blöcken von {@code 2 KB} Größe verwaltet.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DataSourceCache extends AbstractDataSource {

		/**
		 * Diese Klasse implementiert einen Datenauszug.
		 * 
		 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		private static final class Page {

			/**
			 * Dieses Feld definiert die Bitbreite der Anzahl der Byte in {@link #data}.
			 */
			public static final int BITS = 11;

			/**
			 * Dieses Feld speichert die Nutzdaten als einen Auszug einer {@link DataSource}.
			 */
			public final byte[] data;

			/**
			 * Dieses Feld speichert die Anzahl der Wiederverwendungen.
			 */
			public int uses = 1;

			/**
			 * Dieser Konstruktor initialisiert den Datenauszug.
			 */
			public Page() {
				this.data = new byte[1 << Page.BITS];
				this.uses = 1;
			}

		}

		/**
		 * Dieses Feld speichert die {@link DataSource}.
		 */
		private final DataSource source;

		/**
		 * Dieses Feld speichert die Größe der {@link #source}.
		 */
		private final int length;

		/**
		 * Dieses Feld speichert die Datenauszüge.
		 */
		private final Page[] pages;

		/**
		 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten {@link Page}s.
		 */
		private int limit;

		/**
		 * Dieses Feld speichert die Anzahl der aktuell verwalteten {@link Page}s. Dies wird in {@link #page(int)} modifiziert.
		 */
		private int count;

		/**
		 * Dieses Feld speichert die Navigationsposition.
		 */
		private int index;

		/**
		 * Dieser Konstruktor initialisiert das die {@link DataSource}. Wenn sich die {@link DataSource#length() Länge} der Nutzdaten in der gegebenen
		 * {@link DataSource} später ändert, ist das Verhalten des {@link DataSourceCache} undefiniert.
		 * 
		 * @param source {@link DataSource}.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn die {@link DataSource} {@code null} ist.
		 */
		public DataSourceCache(final DataSource source) throws IOException, NullPointerException {
			this.source = source;
			this.length = (int)source.length();
			this.pages = new Page[((this.length + (1 << Page.BITS)) - 1) >> Page.BITS];
			this.setCacheSize(0x010000);
		}

		/**
		 * Diese Methode gibt den {@link Page#data Nutzdatenblock} der {@code index}-ten {@link Page} zurück. Diese wird bei Bedarf aus der {@link #source}
		 * nachgeladen.
		 * 
		 * @param index Index der {@link Page}.
		 * @return {@link Page#data Nutzdatenblock}.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 */
		private byte[] page(final int index) throws IOException {
			final Page[] pages = this.pages;
			Page page = pages[index];
			if(page == null){
				page = new Page();
				final byte[] data = page.data;
				final int offset = index << Page.BITS;
				final DataSource source = this.source;
				source.seek(offset);
				source.readFully(data, 0, Math.min(1 << Page.BITS, this.length - offset));
				int pageCount = this.count, pageLimit = this.limit;
				if(pageCount < pageLimit) return data;
				pageLimit = pageLimit < 0 ? 1 : (pageLimit + 1) / 2;
				for(final int size = pages.length; pageCount > pageLimit;){
					int uses = 0;
					final int maxUses = Integer.MAX_VALUE / pageCount;
					for(int i = 0; i < size; i++){
						final Page item = pages[i];
						if(item != null){
							uses += (item.uses = Math.min(item.uses, maxUses - i));
						}
					}
					final int minUses = uses / pageCount;
					for(int i = 0; i < size; i++){
						final Page item = pages[i];
						if((item != null) && ((item.uses -= minUses) <= 0)){
							pages[i] = null;
							pageCount--;
						}
					}
				}
				this.count = pageCount + 1;
				pages[index] = page;
				return data;
			}else{
				page.uses++;
				return page.data;
			}
		}

		/**
		 * Diese Methode gibt die Größe des Pufferspeichers zurück.
		 * 
		 * @return Größe des Pufferspeichers.
		 */
		public int getCacheSize() {
			return this.limit << Page.BITS;
		}

		/**
		 * Diese Methode setzt die Größe des Pufferspeichers.
		 * 
		 * @param value Größe des Pufferspeichers.
		 */
		public void setCacheSize(final int value) {
			this.limit = Math.min(Math.max(1, value >> Page.BITS), this.pages.length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DataSource data() {
			return this.source;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void seek(final long index) throws IOException {
			this.index = (int)index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long index() throws IOException {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long length() throws IOException {
			return this.length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void readFully(final byte[] array, final int offset, final int length) throws IOException {
			final int index = this.index;
			int page = index >> Page.BITS;
			int srcPos = index & ((1 << Page.BITS) - 1);
			final int destLength = offset + length;
			for(int destPos = offset; destPos < destLength; page++){
				final int count = Math.min(destLength - destPos, (1 << Page.BITS) - srcPos);
				System.arraycopy(this.page(page), srcPos, array, destPos, count);
				destPos += count;
				srcPos = 0;
			}
			this.index = index + length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() throws IOException {
			this.source.close();
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
		private final RandomAccessFile data;

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
		public RandomAccessFile data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final byte[] array, final int offset, final int length) throws IOException {
			this.data.write(array, offset, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void seek(final long index) throws IOException {
			this.data.seek(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long index() throws IOException {
			return this.data.getFilePointer();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long length() throws IOException {
			return this.data.length();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void allocate(final long value) throws IOException {
			this.data.setLength(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() throws IOException {
			this.data.close();
		}

	}

	/**
	 * Diese Klasse implementiert die {@link DataTarget}-Schnittstelle zu einem {@link CompactByteArray}.
	 * 
	 * @see CompactByteArray
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DataTargetArray extends AbstractDataTarget {

		/**
		 * Dieses Feld speichert die Nutzdaten.
		 */
		private final CompactByteArray data;

		/**
		 * Dieses Feld speichert die Schreibeposition.
		 */
		private int index;

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
		public CompactByteArray data() {
			return this.data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(final byte[] array, final int offset, final int length) throws IOException {
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
		public void seek(final long index) throws IOException {
			this.index = (int)index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long index() throws IOException {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long length() throws IOException {
			return this.data.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void allocate(final long value) throws IOException {
			final int size = this.data.size();
			final int count = (int)value - size;
			if(count < 0){
				this.data.remove(size - count, count);
				this.index = Math.min(this.index, size - count);
			}else if(count > 0){
				this.data.insert(size, count);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() throws IOException {
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

	/**
	 * Diese Methode gibt die aktuelle Länge der Nutzdaten als Anzahl von {@code byte}s zurück. Wenn die Navigationsposition bem Lesen größer oder gleich dieser
	 * Anzahl werden würde, wird beim Lesen eine {@link EOFException} ausgelöst. Beim Schreiben wird die Anzahl automatisch vergrößert, wenn dies nötig wird.
	 * 
	 * @see #index()
	 * @return Anzahl verfügbarer {@code byte}s.
	 * @throws IOException Wenn ein I/O-Fehler auftritt.
	 */
	public long length() throws IOException;

}