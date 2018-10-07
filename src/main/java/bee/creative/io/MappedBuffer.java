package bee.creative.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import bee.creative.util.Objects;

/** Diese Klasse implementiert eine alternative zu {@link MappedByteBuffer}, welche auf {@code long}-Adressen arbeitet und beliebig große Dateien per
 * momory-mapping zum Lesen und Schreiben zugänglich machen kann.
 * <p>
 * Die Anbindung der Datei erfolgt intern über {@link MappedByteBuffer} pro Gigabyte.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class MappedBuffer {

	/** Dieses Feld speichert die Anzahl der niederwertigen Bit einer Adresse, die zur Positionsangabe innerhalb eines {@link MappedByteBuffer} eingesetzt
	 * werden. */
	private static final int INDEX_SIZE = 30;

	/** Dieses Feld speichert die Bitmaske zur Auswahl der {@link #INDEX_SIZE niederwertigen Bit einer Adresse}. */
	private static final int INDEX_MASK = (1 << MappedBuffer.INDEX_SIZE) - 1;

	/** Dieses Feld speichert die Größe des Speicherbereichs, den je zwei in {@link #buffers} auf einander folgende {@link MappedByteBuffer} gleichzeitig
	 * anbinden. */
	private static final int BUFFER_GUARD = 1 << 14;

	/** Dieses Feld speichert die Größe der {@link MappedByteBuffer}, die vor dem letzten in {@link #buffers} verwaltet werden. */
	private static final int BUFFER_LENGTH = MappedBuffer.BUFFER_GUARD + MappedBuffer.INDEX_MASK + 1;

	/** Dieses Feld speichert die Anzahl an Elementen eines {@code byte}-Arrays, ab welcher das Array nicht mehr elementweise verarbeitet werden soll. */
	private static final int BUFFER_BYTE_THRESHOLD = 9;

	/** Dieses Feld speichert die Anzahl an Elementen eines nicht {@code byte}-Arrays, ab welcher das Array nicht mehr elementweise verarbeitet werden soll. */
	private static final int BUFFER_OTHER_THRESHOLD = 27;

	/** Diese Methode gibt den Index eines Werts innerhalb eins {@link MappedByteBuffer} zur gegebenen Adresse zurück.
	 *
	 * @param address Adresse.
	 * @return niederwertiger Adressteil als Index eines Werts. */
	private static int valueIndex(final long address) {
		return (int)address & MappedBuffer.INDEX_MASK;
	}

	/** Diese Methode gibt den Index eines {@link MappedByteBuffer} in {@link #buffers} zur gegebenen Adresse zurück.
	 *
	 * @param address Adresse.
	 * @return höherwertiger Adressteil als Index eines Puffers. */
	private static int bufferIndex(final long address) {
		return (int)(address >> MappedBuffer.INDEX_SIZE);
	}

	/** Dieses Feld speichert die gebundene Datei. */
	private final File file;

	/** Dieses Feld speichert den Zugriffsmodus. */
	private final boolean readonly;

	/** Dieses Feld speichert die Puffergröße.. */
	private long size = -1;

	/** Dieses Feld speichert die aktuelle Bytereihenfolge. */
	private ByteOrder order = ByteOrder.nativeOrder();

	/** Dieses Feld speichert die {@link MappedByteBuffer}, welche jeweils {@link #BUFFER_LENGTH} Byte der Datei anbinden. */
	private MappedByteBuffer[] buffers;

	/** Dieser Konstruktor initialisiert den Puffer zum Lesen und Schreiben der gegebenen Datei.
	 *
	 * @see #MappedBuffer(File, boolean)
	 * @param file Datei.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public MappedBuffer(final File file) throws IOException {
		this(file, false);
	}

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei.
	 *
	 * @see #MappedBuffer(File, long, boolean)
	 * @param file Datei.
	 * @param readonly {@code true}, wenn die Datei nur mit Lesezugriff angebunden werden soll.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public MappedBuffer(final File file, final boolean readonly) throws IOException {
		this(file, file.length(), readonly);
	}

	/** Dieser Konstruktor initialisiert den Puffer zum Lesen und Schreiben des Beginns der gegebenen Datei.
	 *
	 * @see #MappedBuffer(File, long, boolean)
	 * @param file Datei.
	 * @param size Größe des anzubindenden Speicherbereiches zu Beginn der Datei.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public MappedBuffer(final File file, final long size) throws IOException {
		this(file, size, false);
	}

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf den Beginn der gegebenen Datei.
	 *
	 * @see #resize(long)
	 * @param file Datei.
	 * @param size Größe des anzubindenden Speicherbereiches zu Beginn der Datei.
	 * @param readonly {@code true}, wenn die Datei nur zum Lesezugriff angebunden werden soll.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public MappedBuffer(final File file, final long size, final boolean readonly) throws IOException {
		if (size < 0) throw new IllegalArgumentException();
		this.file = file.getAbsoluteFile();
		this.buffers = new MappedByteBuffer[1];
		this.readonly = readonly;
		this.resize(size);
	}

	/** Diese Methode gibt die Größe des Puffers zurück.
	 *
	 * @return Puffergröße. */
	public long size() {
		return this.size;
	}

	/** Diese Methode setzt die Größe des Puffers auf die gegebene. <b>Achtung:</b> Die angebundene {@link #file() Datei} kann nur vergrößert werden!
	 *
	 * @param newSize neue Puffergröße.
	 * @throws IOException Wenn die Puffergröße ungültig ist. */
	public void resize(final long newSize) throws IOException {
		if (newSize < 0) throw new IOException();
		final long oldSize = this.size;
		if (oldSize == newSize) return;
		final MappedByteBuffer[] oldBuffers = this.buffers, newBuffers;
		final int oldLength = oldBuffers.length, newLength = Math.max(MappedBuffer.bufferIndex(newSize - 1) + 1, 1);
		if (oldLength != newLength) {
			newBuffers = new MappedByteBuffer[newLength];
			System.arraycopy(oldBuffers, 0, newBuffers, 0, Math.min(oldLength, newLength));
		} else {
			newBuffers = oldBuffers;
		}
		try (final RandomAccessFile file = new RandomAccessFile(this.file, this.readonly ? "r" : "rw")) {
			try (final FileChannel fileChannel = file.getChannel()) {
				final MapMode mode = this.readonly ? MapMode.READ_ONLY : MapMode.READ_WRITE;
				final long scale = MappedBuffer.BUFFER_LENGTH - MappedBuffer.BUFFER_GUARD;
				for (int i = oldLength; i < newLength; i++) {
					(newBuffers[i - 1] = fileChannel.map(mode, (i - 1) * scale, MappedBuffer.BUFFER_LENGTH)).order(this.order);
				}
				final long offset = (newLength - 1) * scale;
				(newBuffers[newLength - 1] = fileChannel.map(mode, offset, newSize - offset)).order(this.order);
			}
		}
		this.size = newSize;
		this.buffers = newBuffers;
	}

	/** Diese Methode versucht alle Änderungen auf den Festspeicher zu übertragen.
	 *
	 * @see MappedByteBuffer#force() */
	public void force() {
		for (final MappedByteBuffer buffer: this.buffers) {
			buffer.force();
		}
	}

	/** Diese Methode versucht die Änderungen an der gegebenen Adresse auf den Festspeicher zu übertragen.
	 *
	 * @see MappedByteBuffer#force()
	 * @param address Adresse, zu welcher die Speicherung versucht werden soll. */
	public void force(final long address) {
		this.buffers[MappedBuffer.bufferIndex(address)].force();
	}

	/** Diese Methode versucht die Änderungen im gegebenen Speicherbereich auf den Festspeicher zu übertragen.
	 *
	 * @see MappedByteBuffer#force()
	 * @param minAddress minimale Adresse, ab welcher die Speicherung versucht werden soll.
	 * @param maxAddress maximale Adresse, ab welcher die Speicherung nicht mehr versucht werden soll. */
	public void force(final long minAddress, final long maxAddress) {
		final int minIndex = MappedBuffer.bufferIndex(minAddress), maxIndex = MappedBuffer.bufferIndex(maxAddress - 1);
		for (int i = minIndex; i <= maxIndex; i++) {
			this.buffers[i].force();
		}
	}

	/** Diese Methode gibt die Bytereihenfolge zurück.
	 *
	 * @see ByteBuffer#order()
	 * @return Bytereihenfolge. */
	public ByteOrder order() {
		return this.order;
	}

	/** Diese Methode setzt die Bytereihenfolge.
	 *
	 * @see ByteBuffer#order(ByteOrder)
	 * @param order Bytereihenfolge. */
	public void order(ByteOrder order) {
		this.order = order = order == ByteOrder.BIG_ENDIAN ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		for (final MappedByteBuffer buffer: this.buffers) {
			buffer.order(order);
		}
	}

	/** Diese Methode gibt die Datei zurück, an die dieser Puffer gebunden ist.
	 *
	 * @return gebundene Datei. */
	public File file() {
		return this.file;
	}

	/** Diese Methode gibt gibt nur dann {@code true} zurück, wenn dieser Puffer die {@link #file() Datei} nur für den Lesezugriff angebunden hat.
	 *
	 * @return Zugriffsmodus. */
	public boolean isReadonly() {
		return this.readonly;
	}

	/** Diese Methode gibt das {@code byte} an der gegebenen Adresse zurück.
	 *
	 * @see ByteBuffer#get(int)
	 * @param address Adresse.
	 * @return {@code byte}-Wert. */
	public byte get(final long address) {
		return this.buffers[MappedBuffer.bufferIndex(address)].get(MappedBuffer.valueIndex(address));
	}

	/** Diese Methode füllt das gegebene Array mit den {@code byte}-Werten ab der gegebenen Adresse.
	 *
	 * @see ByteBuffer#get(byte[])
	 * @param address Adresse.
	 * @param array {@code byte}-Array. */
	public void get(final long address, final byte[] array) {
		this.get(address, array, 0, array.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code byte}-Werten ab der gegebenen Adresse.
	 *
	 * @see ByteBuffer#get(byte[], int, int)
	 * @param address Adresse.
	 * @param array {@code byte}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void get(long address, final byte[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 1);
			length -= count;
			address += count * 1;
			if (count < MappedBuffer.BUFFER_BYTE_THRESHOLD) {
				for (count += offset; offset < count; index += 1, offset += 1) {
					array[offset] = source.get(index);
				}
			} else {
				source.position(index);
				source.get(array, offset, count);
			}
		}
	}

	/** Diese Methode schreibt den gegebenen {@code byte}-Wert an die gegebene Adresse.
	 *
	 * @see ByteBuffer#put(int, byte)
	 * @param address Adresse.
	 * @param value {@code byte}-Wert. */
	public void put(final long address, final byte value) {
		this.buffers[MappedBuffer.bufferIndex(address)].put(MappedBuffer.valueIndex(address), value);
	}

	/** Diese Methode schreibt die {@code byte}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see ByteBuffer#put(byte[])
	 * @param address Adresse.
	 * @param array {@code byte}-Array. */
	public void put(final long address, final byte[] array) {
		this.put(address, array, 0, array.length);
	}

	/** Diese Methode schreibt die {@code byte}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see ByteBuffer#put(byte[], int, int)
	 * @param address Adresse.
	 * @param array {@code byte}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void put(long address, final byte[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 1);
			length -= count;
			address += count * 1;
			if (count < MappedBuffer.BUFFER_BYTE_THRESHOLD) {
				for (count += offset; offset < count; index += 1, offset += 1) {
					source.put(index, array[offset]);
				}
			} else {
				source.position(index);
				source.put(array, offset, count);
			}
		}
	}

	/** Diese Methode gibt das {@code char} an der gegebenen Adresse zurück.
	 *
	 * @see CharBuffer#get(int)
	 * @param address Adresse.
	 * @return {@code char}-Wert. */
	public char getChar(final long address) {
		return this.buffers[MappedBuffer.bufferIndex(address)].getChar(MappedBuffer.valueIndex(address));
	}

	/** Diese Methode füllt das gegebene Array mit den {@code char}-Werten ab der gegebenen Adresse.
	 *
	 * @see CharBuffer#get(char[])
	 * @param address Adresse.
	 * @param array {@code char}-Array. */
	public void getChar(final long address, final char[] array) {
		this.getChar(address, array, 0, array.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code char}-Werten ab der gegebenen Adresse.
	 *
	 * @see CharBuffer#get(char[], int, int)
	 * @param address Adresse.
	 * @param array {@code char}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void getChar(long address, final char[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 2);
			length -= count;
			address += count * 2;
			if (count < MappedBuffer.BUFFER_OTHER_THRESHOLD) {
				for (count += offset; offset < count; index += 2, offset += 1) {
					array[offset] = source.getChar(index);
				}
			} else {
				source.position(index);
				source.asCharBuffer().get(array, offset, count);
			}
		}
	}

	/** Diese Methode schreibt den gegebenen {@code char}-Wert an die gegebene Adresse.
	 *
	 * @see CharBuffer#put(int, char)
	 * @param address Adresse.
	 * @param value {@code char}-Wert. */
	public void putChar(final long address, final char value) {
		this.buffers[MappedBuffer.bufferIndex(address)].putChar(MappedBuffer.valueIndex(address), value);
	}

	/** Diese Methode schreibt die {@code char}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see CharBuffer#put(char[])
	 * @param address Adresse.
	 * @param array {@code char}-Array. */
	public void putChar(final long address, final char[] array) {
		this.putChar(address, array, 0, array.length);
	}

	/** Diese Methode schreibt die {@code char}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see CharBuffer#put(char[], int, int)
	 * @param address Adresse.
	 * @param array {@code char}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void putChar(long address, final char[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 2);
			length -= count;
			address += count * 2;
			if (count < MappedBuffer.BUFFER_OTHER_THRESHOLD) {
				for (count += offset; offset < count; index += 2, offset += 1) {
					source.putChar(index, array[offset]);
				}
			} else {
				source.position(index);
				source.asCharBuffer().put(array, offset, count);
			}
		}
	}

	/** Diese Methode gibt das {@code short} an der gegebenen Adresse zurück.
	 *
	 * @see ShortBuffer#get(int)
	 * @param address Adresse.
	 * @return {@code short}-Wert. */
	public short getShort(final long address) {
		return this.buffers[MappedBuffer.bufferIndex(address)].getShort(MappedBuffer.valueIndex(address));
	}

	/** Diese Methode füllt das gegebene Array mit den {@code short}-Werten ab der gegebenen Adresse.
	 *
	 * @see ShortBuffer#get(short[])
	 * @param address Adresse.
	 * @param array {@code short}-Array. */
	public void getShort(final long address, final short[] array) {
		this.getShort(address, array, 0, array.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code short}-Werten ab der gegebenen Adresse.
	 *
	 * @see ShortBuffer#get(short[], int, int)
	 * @param address Adresse.
	 * @param array {@code short}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void getShort(long address, final short[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 2);
			length -= count;
			address += count * 2;
			if (count < MappedBuffer.BUFFER_OTHER_THRESHOLD) {
				for (count += offset; offset < count; index += 2, offset += 1) {
					array[offset] = source.getShort(index);
				}
			} else {
				source.position(index);
				source.asShortBuffer().get(array, offset, count);
			}
		}
	}

	/** Diese Methode schreibt den gegebenen {@code short}-Wert an die gegebene Adresse.
	 *
	 * @see ShortBuffer#put(int, short)
	 * @param address Adresse.
	 * @param value {@code short}-Wert. */
	public void putShort(final long address, final short value) {
		this.buffers[MappedBuffer.bufferIndex(address)].putShort(MappedBuffer.valueIndex(address), value);
	}

	/** Diese Methode schreibt die {@code short}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see ShortBuffer#put(short[])
	 * @param address Adresse.
	 * @param array {@code short}-Array. */
	public void putShort(final long address, final short[] array) {
		this.putShort(address, array, 0, array.length);
	}

	/** Diese Methode schreibt die {@code short}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see ShortBuffer#put(short[], int, int)
	 * @param address Adresse.
	 * @param array {@code short}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void putShort(long address, final short[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 2);
			length -= count;
			address += count * 2;
			if (count < MappedBuffer.BUFFER_OTHER_THRESHOLD) {
				for (count += offset; offset < count; index += 2, offset += 1) {
					source.putShort(index, array[offset]);
				}
			} else {
				source.position(index);
				source.asShortBuffer().put(array, offset, count);
			}
		}
	}

	/** Diese Methode gibt das {@code int} an der gegebenen Adresse zurück.
	 *
	 * @see IntBuffer#get(int)
	 * @param address Adresse.
	 * @return {@code int}-Wert. */
	public int getInt(final long address) {
		return this.buffers[MappedBuffer.bufferIndex(address)].getInt(MappedBuffer.valueIndex(address));
	}

	/** Diese Methode füllt das gegebene Array mit den {@code int}-Werten ab der gegebenen Adresse.
	 *
	 * @see IntBuffer#get(int[])
	 * @param address Adresse.
	 * @param array {@code int}-Array. */
	public void getInt(final long address, final int[] array) {
		this.getInt(address, array, 0, array.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code int}-Werten ab der gegebenen Adresse.
	 *
	 * @see IntBuffer#get(int[], int, int)
	 * @param address Adresse.
	 * @param array {@code int}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void getInt(long address, final int[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 4);
			length -= count;
			address += count * 4;
			if (count < MappedBuffer.BUFFER_OTHER_THRESHOLD) {
				for (count += offset; offset < count; index += 4, offset += 1) {
					array[offset] = source.getInt(index);
				}
			} else {
				source.position(index);
				source.asIntBuffer().get(array, offset, count);
			}
		}
	}

	/** Diese Methode schreibt den gegebenen {@code int}-Wert an die gegebene Adresse.
	 *
	 * @see IntBuffer#put(int, int)
	 * @param address Adresse.
	 * @param value {@code int}-Wert. */
	public void putInt(final long address, final int value) {
		this.buffers[MappedBuffer.bufferIndex(address)].putInt(MappedBuffer.valueIndex(address), value);
	}

	/** Diese Methode schreibt die {@code int}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see IntBuffer#put(int[])
	 * @param address Adresse.
	 * @param array {@code int}-Array. */
	public void putInt(final long address, final int[] array) {
		this.putInt(address, array, 0, array.length);
	}

	/** Diese Methode schreibt die {@code int}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see IntBuffer#put(int[], int, int)
	 * @param address Adresse.
	 * @param array {@code int}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void putInt(long address, final int[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 4);
			length -= count;
			address += count * 4;
			if (count < MappedBuffer.BUFFER_OTHER_THRESHOLD) {
				for (count += offset; offset < count; index += 4, offset += 1) {
					source.putInt(index, array[offset]);
				}
			} else {
				source.position(index);
				source.asIntBuffer().put(array, offset, count);
			}
		}
	}

	/** Diese Methode gibt das {@code long} an der gegebenen Adresse zurück.
	 *
	 * @see LongBuffer#get(int)
	 * @param address Adresse.
	 * @return {@code long}-Wert. */
	public long getLong(final long address) {
		return this.buffers[MappedBuffer.bufferIndex(address)].getLong(MappedBuffer.valueIndex(address));
	}

	/** Diese Methode füllt das gegebene Array mit den {@code long}-Werten ab der gegebenen Adresse.
	 *
	 * @see LongBuffer#get(long[])
	 * @param address Adresse.
	 * @param array {@code long}-Array. */
	public void getLong(final long address, final long[] array) {
		this.getLong(address, array, 0, array.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code long}-Werten ab der gegebenen Adresse.
	 *
	 * @see LongBuffer#get(long[], int, int)
	 * @param address Adresse.
	 * @param array {@code long}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void getLong(long address, final long[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 8);
			length -= count;
			address += count * 8;
			if (count < MappedBuffer.BUFFER_OTHER_THRESHOLD) {
				for (count += offset; offset < count; index += 8, offset += 1) {
					array[offset] = source.getLong(index);
				}
			} else {
				source.position(index);
				source.asLongBuffer().get(array, offset, count);
			}
		}
	}

	/** Diese Methode schreibt den gegebenen {@code long}-Wert an die gegebene Adresse.
	 *
	 * @see LongBuffer#put(int, long)
	 * @param address Adresse.
	 * @param value {@code long}-Wert. */
	public void putLong(final long address, final long value) {
		this.buffers[MappedBuffer.bufferIndex(address)].putLong(MappedBuffer.valueIndex(address), value);
	}

	/** Diese Methode schreibt die {@code long}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see LongBuffer#put(long[])
	 * @param address Adresse.
	 * @param array {@code long}-Array. */
	public void putLong(final long address, final long[] array) {
		this.putLong(address, array, 0, array.length);
	}

	/** Diese Methode schreibt die {@code long}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see LongBuffer#put(long[], int, int)
	 * @param address Adresse.
	 * @param array {@code long}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void putLong(long address, final long[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 8);
			length -= count;
			address += count * 8;
			if (count < MappedBuffer.BUFFER_OTHER_THRESHOLD) {
				for (count += offset; offset < count; index += 8, offset += 1) {
					source.putLong(index, array[offset]);
				}
			} else {
				source.position(index);
				source.asLongBuffer().put(array, offset, count);
			}
		}
	}

	/** Diese Methode gibt das {@code float} an der gegebenen Adresse zurück.
	 *
	 * @see FloatBuffer#get(int)
	 * @param address Adresse.
	 * @return {@code float}-Wert. */
	public float getFloat(final long address) {
		return this.buffers[MappedBuffer.bufferIndex(address)].getFloat(MappedBuffer.valueIndex(address));
	}

	/** Diese Methode füllt das gegebene Array mit den {@code float}-Werten ab der gegebenen Adresse.
	 *
	 * @see FloatBuffer#get(float[])
	 * @param address Adresse.
	 * @param array {@code float}-Array. */
	public void getFloat(final long address, final float[] array) {
		this.getFloat(address, array, 0, array.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code float}-Werten ab der gegebenen Adresse.
	 *
	 * @see FloatBuffer#get(float[], int, int)
	 * @param address Adresse.
	 * @param array {@code float}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void getFloat(long address, final float[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 4);
			length -= count;
			address += count * 4;
			if (count < MappedBuffer.BUFFER_OTHER_THRESHOLD) {
				for (count += offset; offset < count; index += 4, offset += 1) {
					array[offset] = source.getFloat(index);
				}
			} else {
				source.position(index);
				source.asFloatBuffer().get(array, offset, count);
			}
		}
	}

	/** Diese Methode schreibt den gegebenen {@code float}-Wert an die gegebene Adresse.
	 *
	 * @see FloatBuffer#put(int, float)
	 * @param address Adresse.
	 * @param value {@code float}-Wert. */
	public void putFloat(final long address, final float value) {
		this.buffers[MappedBuffer.bufferIndex(address)].putFloat(MappedBuffer.valueIndex(address), value);
	}

	/** Diese Methode schreibt die {@code float}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see FloatBuffer#put(float[])
	 * @param address Adresse.
	 * @param array {@code float}-Array. */
	public void putFloat(final long address, final float[] array) {
		this.putFloat(address, array, 0, array.length);
	}

	/** Diese Methode schreibt die {@code float}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see FloatBuffer#put(float[], int, int)
	 * @param address Adresse.
	 * @param array {@code float}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void putFloat(long address, final float[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 4);
			length -= count;
			address += count * 4;
			if (count < MappedBuffer.BUFFER_OTHER_THRESHOLD) {
				for (count += offset; offset < count; index += 4, offset += 1) {
					source.putFloat(index, array[offset]);
				}
			} else {
				source.position(index);
				source.asFloatBuffer().put(array, offset, count);
			}
		}
	}

	/** Diese Methode gibt das {@code double} an der gegebenen Adresse zurück.
	 *
	 * @see DoubleBuffer#get(int)
	 * @param address Adresse.
	 * @return {@code double}-Wert. */
	public double getDouble(final long address) {
		return this.buffers[MappedBuffer.bufferIndex(address)].getDouble(MappedBuffer.valueIndex(address));
	}

	/** Diese Methode füllt das gegebene Array mit den {@code double}-Werten ab der gegebenen Adresse.
	 *
	 * @see DoubleBuffer#get(double[])
	 * @param address Adresse.
	 * @param array {@code double}-Array. */
	public void getDouble(final long address, final double[] array) {
		this.getDouble(address, array, 0, array.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code double}-Werten ab der gegebenen Adresse.
	 *
	 * @see DoubleBuffer#get(double[], int, int)
	 * @param address Adresse.
	 * @param array {@code double}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void getDouble(long address, final double[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 8);
			length -= count;
			address += count * 8;
			if (count < MappedBuffer.BUFFER_OTHER_THRESHOLD) {
				for (count += offset; offset < count; index += 8, offset += 1) {
					array[offset] = source.getDouble(index);
				}
			} else {
				source.position(index);
				source.asDoubleBuffer().get(array, offset, count);
			}
		}
	}

	/** Diese Methode schreibt den gegebenen {@code double}-Wert an die gegebene Adresse.
	 *
	 * @see DoubleBuffer#put(int, double)
	 * @param address Adresse.
	 * @param value {@code double}-Wert. */
	public void putDouble(final long address, final double value) {
		this.buffers[MappedBuffer.bufferIndex(address)].putDouble(MappedBuffer.valueIndex(address), value);
	}

	/** Diese Methode schreibt die {@code double}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see DoubleBuffer#put(double[])
	 * @param address Adresse.
	 * @param array {@code double}-Array. */
	public void putDouble(final long address, final double[] array) {
		this.putDouble(address, array, 0, array.length);
	}

	/** Diese Methode schreibt die {@code double}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see DoubleBuffer#put(double[], int, int)
	 * @param address Adresse.
	 * @param array {@code double}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void putDouble(long address, final double[] array, int offset, int length) {
		if (length < 0) throw new IllegalArgumentException();
		while (length != 0) {
			final MappedByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)];
			int index = MappedBuffer.valueIndex(address), count = Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 8);
			length -= count;
			address += count * 8;
			if (count < MappedBuffer.BUFFER_OTHER_THRESHOLD) {
				for (count += offset; offset < count; index += 8, offset += 1) {
					source.putDouble(index, array[offset]);
				}
			} else {
				source.position(index);
				source.asDoubleBuffer().put(array, offset, count);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.file, this.size, this.readonly);
	}

}
