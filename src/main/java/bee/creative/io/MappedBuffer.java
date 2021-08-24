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
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.iam.IAMArray;
import bee.creative.lang.Bytes;
import bee.creative.lang.Objects;
import bee.creative.mmi.MMIArray;
import bee.creative.mmi.MMIArrayL;

/** Diese Klasse implementiert eine threadsichere alternative zu {@link MappedByteBuffer}, der mit {@code long}-Adressen arbeitet und beliebig große Dateien per
 * momory-mapping zum Lesen und Schreiben zugänglich machen kann.
 * <p>
 * Die Anbindung der Datei erfolgt intern über einen {@link MappedByteBuffer} pro Gigabyte. Wenn eine der Methoden eine ungültige Adresse übergeben wird, welche
 * zu einer Zugriffsverletzung führt, wird grundsätzlich eine {@link IndexOutOfBoundsException} ausgelöst, auch wenn diese nicht deklariert ist. Analog dazu
 * wird auf negative Anzahlen mit einer {@link IllegalArgumentException} reagiert.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class MappedBuffer implements Emuable {

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

	/** Diese Methode gibt den Index eines {@link MappedByteBuffer} in {@link #buffers} zur gegebenen Adresse zurück. */
	private static int bufferIndex(final long address) {
		return (int)(address >> MappedBuffer.INDEX_SIZE);
	}

	/** Diese Methode gibt den Index eines Werts innerhalb eins {@link MappedByteBuffer} zur gegebenen Adresse zurück. */
	private static int valueIndex(final long address) {
		return (int)address & MappedBuffer.INDEX_MASK;
	}

	private static int valueCount1(final int index, final int length) {
		return Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 1);
	}

	private static int valueCount2(final int index, final int length) {
		return Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 2);
	}

	private static int valueCount4(final int index, final int length) {
		return Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 4);
	}

	private static int valueCount8(final int index, final int length) {
		return Math.min(length, (MappedBuffer.BUFFER_LENGTH - index) / 8);
	}

	private static void forceImpl(ByteBuffer buffer, final int minOffset, final int maxOffset) {
		buffer = buffer.duplicate();
		buffer.limit(maxOffset);
		buffer.position(minOffset);
		buffer = buffer.slice();
		((MappedByteBuffer)buffer).force();
	}

	private static void copyImpl(final ByteBuffer[] targetBuffers, long targetAddress, final ByteBuffer[] sourceBuffers, long sourceAddress, long length) {
		if (length < 0) throw new IllegalArgumentException();
		if ((targetBuffers == sourceBuffers) && (targetAddress == sourceAddress)) return;
		if (targetAddress < sourceAddress) {
			while (length != 0) {
				final int targetIndex = MappedBuffer.valueIndex(targetAddress);
				final int sourceIndex = MappedBuffer.valueIndex(sourceAddress);
				final int count = (int)Math.min(length, MappedBuffer.BUFFER_LENGTH - Math.max(targetIndex, sourceIndex));
				final ByteBuffer target = targetBuffers[MappedBuffer.bufferIndex(targetAddress)].duplicate();
				final ByteBuffer source = sourceBuffers[MappedBuffer.bufferIndex(sourceAddress)].duplicate();
				source.limit(sourceIndex + count);
				source.position(sourceIndex);
				target.position(targetIndex);
				target.put(source);
				length -= count;
				targetAddress += count;
				sourceAddress += count;
			}
		} else {
			targetAddress += length;
			sourceAddress += length;
			while (length != 0) {
				final int count = (int)Math.min(length, Math.min(MappedBuffer.valueIndex(targetAddress - 1), MappedBuffer.valueIndex(sourceAddress - 1)) + 1);
				length -= count;
				targetAddress -= count;
				sourceAddress -= count;
				final int targetIndex = MappedBuffer.valueIndex(targetAddress);
				final int sourceIndex = MappedBuffer.valueIndex(sourceAddress);
				final ByteBuffer target = targetBuffers[MappedBuffer.bufferIndex(targetAddress)].duplicate();
				final ByteBuffer source = sourceBuffers[MappedBuffer.bufferIndex(sourceAddress)].duplicate();
				source.limit(sourceIndex + count);
				source.position(sourceIndex);
				target.position(targetIndex);
				target.put(source);
			}
		}
	}

	/** Dieses Feld speichert die gebundene Datei. */
	private final File file;

	/** Dieses Feld speichert die Puffergröße. */
	private long size = -1;

	/** Dieses Feld speichert die Bytereihenfolge. */
	private ByteOrder order = Bytes.NATIVE_ORDER;

	/** Dieses Feld speichert die {@link MappedByteBuffer}, welche jeweils {@link #BUFFER_LENGTH} Byte der Datei anbinden. */
	private MappedByteBuffer[] buffers;

	/** Dieses Feld speichert {@code true} bei Schreibschutz. */
	private final boolean isReadonly;

	/** Dieses Feld speichert den Exponent der Wachstumsausrichtung, welche eine Potenz von 2 ist. */
	private byte growAlign = 16;

	/** Dieses Feld speichert den Wachstumsfaktor als Festkommazahl von 0 bis 64 für die Faktoren 0% bis 200%. */
	private byte growScale = 16;

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
		if (size < 0) throw new IOException();
		this.file = file.getAbsoluteFile();
		this.buffers = new MappedByteBuffer[1];
		this.isReadonly = readonly;
		this.resize(size);
	}

	/** Diese Methode gibt die Datei zurück, an die dieser Puffer gebunden ist.
	 *
	 * @return gebundene Datei. */
	public File file() {
		return this.file;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #order() Bytereihenfolge} der nativen entspricht.
	 *
	 * @return {@code true} bei nativer Bytereihenfolge. */
	public boolean isNE() {
		return this.order == Bytes.NATIVE_ORDER;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #order() Bytereihenfolge} {@link ByteOrder#LITTLE_ENDIAN} ist.
	 *
	 * @return {@code true} bei Bytereihenfolge {@link ByteOrder#LITTLE_ENDIAN}. */
	public boolean isLE() {
		return this.order == ByteOrder.LITTLE_ENDIAN;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #order() Bytereihenfolge} {@link ByteOrder#BIG_ENDIAN} ist.
	 *
	 * @return {@code true} bei Bytereihenfolge {@link ByteOrder#BIG_ENDIAN}. */
	public boolean isBE() {
		return this.order == ByteOrder.BIG_ENDIAN;
	}

	/** Diese Methode gibt gibt nur dann {@code true} zurück, wenn dieser Puffer die {@link #file() Datei} nur für den Lesezugriff angebunden hat.
	 *
	 * @return Zugriffsmodus. */
	public boolean isReadonly() {
		return this.isReadonly;
	}

	/** Diese Methode gibt die Größe des Puffers zurück.
	 *
	 * @return Puffergröße. */
	public long size() {
		return this.size;
	}

	/** Diese Methode vergrößert den Puffer, wenn seine Größe kleiner als die gegebene ist. Die neue Puffergröße ergibt sich aus der Summe der gegebenen minimalen
	 * Puffergröße und dieser um den {@link #growScale() Wachstumsfaktor} vergrößerten Puffergröße, aufgerundet zum nächsten ganzzahligen Vielfachen der
	 * {@link #growAlign() Wachstumsausrichtung}, d.h. aus {@code (minSize + minSize * growScale() / 100 + growAlign() - 1) / growAlign() * growAlign()}.
	 *
	 * @param minSize minimale Puffergröße.
	 * @return {@code true}, nur wenn die Puffergröße geändert wurde; sonst {@code false}.
	 * @throws IllegalArgumentException Wenn die Puffergröße ungültig ist. */
	public boolean grow(final long minSize) throws IllegalArgumentException, IllegalStateException {
		if (minSize < 0) throw new IllegalArgumentException();
		synchronized (this) {
			if (minSize <= this.size) return false;
			final long scale = this.growScale, align = this.growAlign() - 1;
			final long newSize = ((minSize + ((minSize * scale) / 32)) + align) & ~align;
			this.resizeImpl(newSize < 0 ? Long.MAX_VALUE - 7 : newSize);
			return true;
		}
	}

	/** Diese Methode gibt die Wachstumsausrichtung zurück, die in {@link #grow(long)} zur Berechnung der neuen Puffergröße verwendet wird.
	 *
	 * @return Wachstumsausrichtung in Byte (8..1073741824). */
	public int growAlign() {
		return 1 << this.growAlign;
	}

	/** Diese Methode setzt die {@link #growAlign() Wachstumsausrichtung}. Diese wird stets auf die größte Potenz von 2 gesetzt, die nicht größer als der gegebene
	 * Wert ist.
	 *
	 * @param align Wachstumsausrichtung in Byte (8..1073741824).
	 * @throws IllegalArgumentException Wenn {@code scale} ungültig ist. */
	public void growAlign(final int align) {
		if ((align < 8) || (align > 1073741824)) throw new IllegalArgumentException();
		this.growAlign = (byte)(31 - Integer.numberOfLeadingZeros(align));
	}

	/** Diese Methode gibt den Wachstumsfaktor zurück, der in {@link #grow(long)} zur Berechnung der neuen Puffergröße verwendet wird.
	 *
	 * @return Wachstumsfaktor in Prozent (0..200 Prozent). */
	public int growScale() {
		return (this.growScale * 100) / 32;
	}

	/** Diese Methode setzt den {@link #growScale() Wachstumsfaktor}.
	 *
	 * @param scale Wachstumsfaktor in Prozent (0..200).
	 * @throws IllegalArgumentException Wenn {@code scale} ungültig ist. */
	public void growScale(final int scale) throws IllegalArgumentException {
		if ((scale < 0) || (scale > 200)) throw new IllegalArgumentException();
		this.growScale = (byte)((scale * 32) / 100);
	}

	/** Diese Methode setzt die {@link #resize(long) Größe} des Puffers auf die Größe der {@link #file() Datei}. Dies kann notwendig werden, wenn Inhalt und Größe
	 * des Puffers durch einen anderen Prozess verändert werden. */
	public void resize() {
		this.resize(this.file.length());
	}

	/** Diese Methode setzt die Größe des Puffers auf die gegebene.<br>
	 * <b>Achtung:</b> Wegen der Fehlenden Kontrolle über die Lebenszeit der {@link MappedByteBuffer} kann die angebundene {@link #file() Datei} kann nur
	 * vergrößert werden, aich wenn dieser Puffer nur einen Teil davon zugänglich macht!
	 *
	 * @param newSize neue Puffergröße.
	 * @throws IllegalArgumentException Wenn die Puffergröße ungültig ist. */
	public void resize(final long newSize) throws IllegalArgumentException, IllegalStateException {
		if (newSize < 0) throw new IllegalArgumentException();
		synchronized (this) {
			if (this.size == newSize) return;
			this.resizeImpl(newSize);
		}
	}

	/** Diese Methode implementiert {@link #resize(long)} ohne {@code synchronized} und ohne Parameterprüfung. */
	private final void resizeImpl(final long newSize) throws IllegalStateException {
		final MappedByteBuffer[] oldBuffers = this.buffers, newBuffers;
		final int oldLength = oldBuffers.length, newLength = Math.max(MappedBuffer.bufferIndex(newSize - 1) + 1, 1);
		if (oldLength != newLength) {
			newBuffers = new MappedByteBuffer[newLength];
			System.arraycopy(oldBuffers, 0, newBuffers, 0, Math.min(oldLength, newLength));
		} else {
			newBuffers = oldBuffers;
		}
		try (final RandomAccessFile file = new RandomAccessFile(this.file, this.isReadonly ? "r" : "rw"); final FileChannel channel = file.getChannel()) {
			final long scale = MappedBuffer.BUFFER_LENGTH - MappedBuffer.BUFFER_GUARD;
			final MapMode mode = this.isReadonly ? MapMode.READ_ONLY : MapMode.READ_WRITE;
			final ByteOrder order = this.order;
			for (int i = oldLength; i < newLength; i++) {
				(newBuffers[i - 1] = channel.map(mode, (i - 1) * scale, MappedBuffer.BUFFER_LENGTH)).order(order);
			}
			final long offset = (newLength - 1) * scale;
			(newBuffers[newLength - 1] = channel.map(mode, offset, newSize - offset)).order(order);
		} catch (final IOException cause) {
			throw new IllegalStateException(cause);
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

	/** Diese Methode versucht die Änderungen im gegebenen Speicherbereich auf den Festspeicher zu übertragen.
	 *
	 * @see MappedByteBuffer#force()
	 * @param address Adresse, ab welcher der Speicherbereich beginnt.
	 * @param length Größe des Speicherbereichs. */
	public void force(final long address, final long length) {
		if (length == 0) return;
		if (length < 0) throw new IllegalArgumentException();
		final long address2 = address + length;
		final int minIndex = MappedBuffer.bufferIndex(address);
		final int minValue = MappedBuffer.valueIndex(address);
		final int maxIndex = MappedBuffer.bufferIndex(address2);
		final int maxValue = MappedBuffer.valueIndex(address2);
		if (minIndex == maxIndex) {
			MappedBuffer.forceImpl(this.buffers[minIndex], minValue, maxValue + 1);
		} else {
			MappedBuffer.forceImpl(this.buffers[minIndex], minValue, MappedBuffer.BUFFER_LENGTH - MappedBuffer.BUFFER_GUARD);
			for (int i = minIndex + 1; i < maxIndex; i++) {
				MappedBuffer.forceImpl(this.buffers[i], 0, MappedBuffer.BUFFER_LENGTH - MappedBuffer.BUFFER_GUARD);
			}
			MappedBuffer.forceImpl(this.buffers[maxIndex], 0, maxValue + 1);
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
		order = Bytes.directOrder(order);
		synchronized (this) {
			if (this.order == order) return;
			this.order = order;
			for (final MappedByteBuffer buffer: this.buffers) {
				buffer.order(order);
			}
		}
	}

	/** Diese Methode gibt den größtmöglichen Speicherbereich (16KB..1GB) ab der gegebenen Adresse als {@link ByteBuffer} zurück.
	 *
	 * @param address Adresse, ab welcher der Speicherbereich beginnt. */
	public ByteBuffer buffer(final long address) {
		final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate();
		source.position(MappedBuffer.valueIndex(address));
		return source;
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
	 * @param target {@code byte}-Array. */
	public void get(final long address, final byte[] target) {
		this.get(address, target, 0, target.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code byte}-Werten ab der gegebenen Adresse.
	 *
	 * @see ByteBuffer#get(byte[], int, int)
	 * @param address Adresse.
	 * @param target {@code byte}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void get(long address, final byte[] target, int offset, int length) {
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate();
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount1(index, length);
			source.position(index);
			source.get(target, offset, count);
			length -= count;
			offset += count;
			address += count * 1;
		}
	}

	/** Diese Methode füllt den gegebenen Puffer mit den {@code byte}-Werten ab der gegebenen Adresse.
	 *
	 * @param address Adresse.
	 * @param target {@code byte}-Puffer. */
	public void get(long address, final ByteBuffer target) {
		int length = target.remaining();
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate();
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount1(index, length);
			source.limit(index + count);
			source.position(index);
			target.put(source);
			length -= count;
			address += count * 1;
		}
	}

	/** Diese Methode füllt den gegebenen Pufferabschnitt mit den {@code byte}-Werten ab der gegebenen Adresse.
	 *
	 * @param address Adresse.
	 * @param target Puffer.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void get(final long address, final MappedBuffer target, final long offset, final long length) {
		MappedBuffer.copyImpl(target.buffers, offset, this.buffers, address, length);
	}

	/** Diese Methode schreibt den gegebenen {@code byte}-Wert an die gegebene Adresse.
	 *
	 * @see ByteBuffer#put(int, byte)
	 * @param address Adresse.
	 * @param source {@code byte}-Wert. */
	public void put(final long address, final byte source) {
		this.buffers[MappedBuffer.bufferIndex(address)].put(MappedBuffer.valueIndex(address), source);
	}

	/** Diese Methode schreibt die {@code byte}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see ByteBuffer#put(byte[])
	 * @param address Adresse.
	 * @param source {@code byte}-Array. */
	public void put(final long address, final byte[] source) {
		this.put(address, source, 0, source.length);
	}

	/** Diese Methode schreibt die {@code byte}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see ByteBuffer#put(byte[], int, int)
	 * @param address Adresse.
	 * @param source {@code byte}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void put(long address, final byte[] source, int offset, int length) {
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate();
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount1(index, length);
			target.position(index);
			target.put(source, offset, count);
			length -= count;
			offset += count;
			address += count * 1;
		}
	}

	/** Diese Methode schreibt die {@code byte}-Werte des gegebenen Puffers an die gegebene Adresse.
	 *
	 * @param address Adresse.
	 * @param source {@code byte}-Puffer. */
	public void put(long address, final ByteBuffer source) {
		int length = source.remaining();
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate();
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount1(index, length);
			source.limit(index + count);
			target.position(index);
			target.put(source);
			length -= count;
			address += count * 1;
		}
	}

	/** Diese Methode schreibt die {@code byte}-Werte des gegebenen Pufferabschnitts an die gegebene Adresse.
	 *
	 * @param address Adresse.
	 * @param source Puffer.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void put(final long address, final MappedBuffer source, final long offset, final long length) {
		MappedBuffer.copyImpl(this.buffers, address, source.buffers, offset, length);
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
	 * @param target {@code char}-Array. */
	public void getChar(final long address, final char[] target) {
		this.getChar(address, target, 0, target.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code char}-Werten ab der gegebenen Adresse.
	 *
	 * @see CharBuffer#get(char[], int, int)
	 * @param address Adresse.
	 * @param target {@code char}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void getChar(long address, final char[] target, int offset, int length) {
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount2(index, length);
			source.position(index);
			source.asCharBuffer().get(target, offset, count);
			length -= count;
			offset += count;
			address += count * 2;
		}
	}

	/** Diese Methode füllt den gegebenen Puffer mit den {@code char}-Werten ab der gegebenen Adresse.
	 *
	 * @param address Adresse.
	 * @param target {@code char}-Puffer. */
	public void getChar(long address, final CharBuffer target) {
		int length = target.remaining();
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount2(index, length);
			source.limit(index + count);
			source.position(index);
			target.put(source.asCharBuffer());
			length -= count;
			address += count * 2;
		}
	}

	/** Diese Methode schreibt den gegebenen {@code char}-Wert an die gegebene Adresse.
	 *
	 * @see CharBuffer#put(int, char)
	 * @param address Adresse.
	 * @param source {@code char}-Wert. */
	public void putChar(final long address, final char source) {
		this.buffers[MappedBuffer.bufferIndex(address)].putChar(MappedBuffer.valueIndex(address), source);
	}

	/** Diese Methode schreibt die {@code char}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see CharBuffer#put(char[])
	 * @param address Adresse.
	 * @param source {@code char}-Array. */
	public void putChar(final long address, final char[] source) {
		this.putChar(address, source, 0, source.length);
	}

	/** Diese Methode schreibt die {@code char}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see CharBuffer#put(char[], int, int)
	 * @param address Adresse.
	 * @param source {@code char}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void putChar(long address, final char[] source, int offset, int length) {
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount2(index, length);
			target.position(index);
			target.asCharBuffer().put(source, offset, count);
			length -= count;
			offset += count;
			address += count * 2;
		}
	}

	/** Diese Methode schreibt die {@code char}-Werte des gegebenen Puffers an die gegebene Adresse.
	 *
	 * @param address Adresse.
	 * @param source {@code char}-Puffer. */
	public void putChar(long address, final CharBuffer source) {
		int length = source.remaining();
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount2(index, length);
			source.limit(index + count);
			target.position(index);
			target.asCharBuffer().put(source);
			length -= count;
			address += count * 2;
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
	 * @param target {@code short}-Array. */
	public void getShort(final long address, final short[] target) {
		this.getShort(address, target, 0, target.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code short}-Werten ab der gegebenen Adresse.
	 *
	 * @see ShortBuffer#get(short[], int, int)
	 * @param address Adresse.
	 * @param target {@code short}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void getShort(long address, final short[] target, int offset, int length) {
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount2(index, length);
			source.position(index);
			source.asShortBuffer().get(target, offset, count);
			length -= count;
			offset += count;
			address += count * 2;
		}
	}

	/** Diese Methode füllt den gegebenen Puffer mit den {@code short}-Werten ab der gegebenen Adresse.
	 *
	 * @param address Adresse.
	 * @param target {@code short}-Puffer. */
	public void getShort(long address, final ShortBuffer target) {
		int length = target.remaining();
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount2(index, length);
			source.limit(index + count);
			source.position(index);
			target.put(source.asShortBuffer());
			length -= count;
			address += count * 2;
		}
	}

	/** Diese Methode schreibt den gegebenen {@code short}-Wert an die gegebene Adresse.
	 *
	 * @see ShortBuffer#put(int, short)
	 * @param address Adresse.
	 * @param source {@code short}-Wert. */
	public void putShort(final long address, final short source) {
		this.buffers[MappedBuffer.bufferIndex(address)].putShort(MappedBuffer.valueIndex(address), source);
	}

	/** Diese Methode schreibt die {@code short}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see ShortBuffer#put(short[])
	 * @param address Adresse.
	 * @param source {@code short}-Array. */
	public void putShort(final long address, final short[] source) {
		this.putShort(address, source, 0, source.length);
	}

	/** Diese Methode schreibt die {@code short}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see ShortBuffer#put(short[], int, int)
	 * @param address Adresse.
	 * @param source {@code short}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void putShort(long address, final short[] source, int offset, int length) {
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount2(index, length);
			target.position(index);
			target.asShortBuffer().put(source, offset, count);
			length -= count;
			offset += count;
			address += count * 2;
		}
	}

	/** Diese Methode schreibt die {@code short}-Werte des gegebenen Puffers an die gegebene Adresse.
	 *
	 * @param address Adresse.
	 * @param source {@code short}-Puffer. */
	public void putShort(long address, final ShortBuffer source) {
		int length = source.remaining();
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount2(index, length);
			source.limit(index + count);
			target.position(index);
			target.asShortBuffer().put(source);
			length -= count;
			address += count * 2;
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
	 * @param target {@code int}-Array. */
	public void getInt(final long address, final int[] target) {
		this.getInt(address, target, 0, target.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code int}-Werten ab der gegebenen Adresse.
	 *
	 * @see IntBuffer#get(int[], int, int)
	 * @param address Adresse.
	 * @param target {@code int}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void getInt(long address, final int[] target, int offset, int length) {
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount4(index, length);
			source.position(index);
			source.asIntBuffer().get(target, offset, count);
			length -= count;
			offset += count;
			address += count * 4;
		}
	}

	/** Diese Methode füllt den gegebenen Puffer mit den {@code int}-Werten ab der gegebenen Adresse.
	 *
	 * @param address Adresse.
	 * @param target {@code int}-Puffer. */
	public void getInt(long address, final IntBuffer target) {
		int length = target.remaining();
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount4(index, length);
			source.limit(index + count);
			source.position(index);
			target.put(source.asIntBuffer());
			length -= count;
			address += count * 4;
		}
	}

	/** Diese Methode schreibt den gegebenen {@code int}-Wert an die gegebene Adresse.
	 *
	 * @see IntBuffer#put(int, int)
	 * @param address Adresse.
	 * @param source {@code int}-Wert. */
	public void putInt(final long address, final int source) {
		this.buffers[MappedBuffer.bufferIndex(address)].putInt(MappedBuffer.valueIndex(address), source);
	}

	/** Diese Methode schreibt die {@code int}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see IntBuffer#put(int[])
	 * @param address Adresse.
	 * @param source {@code int}-Array. */
	public void putInt(final long address, final int[] source) {
		this.putInt(address, source, 0, source.length);
	}

	/** Diese Methode schreibt die {@code int}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see IntBuffer#put(int[], int, int)
	 * @param address Adresse.
	 * @param source {@code int}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void putInt(long address, final int[] source, int offset, int length) {
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount4(index, length);
			target.position(index);
			target.asIntBuffer().put(source, offset, count);
			length -= count;
			offset += count;
			address += count * 4;
		}
	}

	/** Diese Methode schreibt die {@code int}-Werte des gegebenen Puffers an die gegebene Adresse.
	 *
	 * @param address Adresse.
	 * @param source {@code int}-Puffer. */
	public void putInt(long address, final IntBuffer source) {
		int length = source.remaining();
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount4(index, length);
			source.limit(index + count);
			target.position(index);
			target.asIntBuffer().put(source);
			length -= count;
			address += count * 4;
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
	 * @param target {@code long}-Array. */
	public void getLong(final long address, final long[] target) {
		this.getLong(address, target, 0, target.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code long}-Werten ab der gegebenen Adresse.
	 *
	 * @see LongBuffer#get(long[], int, int)
	 * @param address Adresse.
	 * @param target {@code long}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void getLong(long address, final long[] target, int offset, int length) {
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount8(index, length);
			source.position(index);
			source.asLongBuffer().get(target, offset, count);
			length -= count;
			offset += count;
			address += count * 8;
		}
	}

	/** Diese Methode füllt den gegebenen Puffer mit den {@code long}-Werten ab der gegebenen Adresse.
	 *
	 * @param address Adresse.
	 * @param target {@code long}-Puffer. */
	public void getLong(long address, final LongBuffer target) {
		int length = target.remaining();
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount8(index, length);
			source.limit(index + count);
			source.position(index);
			target.put(source.asLongBuffer());
			length -= count;
			address += count * 8;
		}
	}

	/** Diese Methode schreibt den gegebenen {@code long}-Wert an die gegebene Adresse.
	 *
	 * @see LongBuffer#put(int, long)
	 * @param address Adresse.
	 * @param source {@code long}-Wert. */
	public void putLong(final long address, final long source) {
		this.buffers[MappedBuffer.bufferIndex(address)].putLong(MappedBuffer.valueIndex(address), source);
	}

	/** Diese Methode schreibt die {@code long}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see LongBuffer#put(long[])
	 * @param address Adresse.
	 * @param source {@code long}-Array. */
	public void putLong(final long address, final long[] source) {
		this.putLong(address, source, 0, source.length);
	}

	/** Diese Methode schreibt die {@code long}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see LongBuffer#put(long[], int, int)
	 * @param address Adresse.
	 * @param source {@code long}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void putLong(long address, final long[] source, int offset, int length) {
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount8(index, length);
			target.position(index);
			target.asLongBuffer().put(source, offset, count);
			length -= count;
			offset += count;
			address += count * 8;
		}
	}

	/** Diese Methode schreibt die {@code long}-Werte des gegebenen Puffers an die gegebene Adresse.
	 *
	 * @param address Adresse.
	 * @param source {@code long}-Puffer. */
	public void putLong(long address, final LongBuffer source) {
		int length = source.remaining();
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount8(index, length);
			source.limit(index + count);
			target.position(index);
			target.asLongBuffer().put(source);
			length -= count;
			address += count * 8;
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
	 * @param target {@code float}-Array. */
	public void getFloat(final long address, final float[] target) {
		this.getFloat(address, target, 0, target.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code float}-Werten ab der gegebenen Adresse.
	 *
	 * @see FloatBuffer#get(float[], int, int)
	 * @param address Adresse.
	 * @param target {@code float}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void getFloat(long address, final float[] target, int offset, int length) {
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount4(index, length);
			source.position(index);
			source.asFloatBuffer().get(target, offset, count);
			length -= count;
			offset += count;
			address += count * 4;
		}
	}

	/** Diese Methode füllt den gegebenen Puffer mit den {@code float}-Werten ab der gegebenen Adresse.
	 *
	 * @param address Adresse.
	 * @param target {@code float}-Puffer. */
	public void getFloat(long address, final FloatBuffer target) {
		int length = target.remaining();
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount4(index, length);
			source.limit(index + count);
			source.position(index);
			target.put(source.asFloatBuffer());
			length -= count;
			address += count * 4;
		}
	}

	/** Diese Methode schreibt den gegebenen {@code float}-Wert an die gegebene Adresse.
	 *
	 * @see FloatBuffer#put(int, float)
	 * @param address Adresse.
	 * @param source {@code float}-Wert. */
	public void putFloat(final long address, final float source) {
		this.buffers[MappedBuffer.bufferIndex(address)].putFloat(MappedBuffer.valueIndex(address), source);
	}

	/** Diese Methode schreibt die {@code float}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see FloatBuffer#put(float[])
	 * @param address Adresse.
	 * @param source {@code float}-Array. */
	public void putFloat(final long address, final float[] source) {
		this.putFloat(address, source, 0, source.length);
	}

	/** Diese Methode schreibt die {@code float}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see FloatBuffer#put(float[], int, int)
	 * @param address Adresse.
	 * @param source {@code float}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void putFloat(long address, final float[] source, int offset, int length) {
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount4(index, length);
			target.position(index);
			target.asFloatBuffer().put(source, offset, count);
			length -= count;
			offset += count;
			address += count * 4;
		}
	}

	/** Diese Methode schreibt die {@code float}-Werte des gegebenen Puffers an die gegebene Adresse.
	 *
	 * @param address Adresse.
	 * @param source {@code float}-Puffer. */
	public void putFloat(long address, final FloatBuffer source) {
		int length = source.remaining();
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount4(index, length);
			source.limit(index + count);
			target.position(index);
			target.asFloatBuffer().put(source);
			length -= count;
			address += count * 4;
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
	 * @param target {@code double}-Array. */
	public void getDouble(final long address, final double[] target) {
		this.getDouble(address, target, 0, target.length);
	}

	/** Diese Methode füllt den gegebenen Array-Abschnitt mit den {@code double}-Werten ab der gegebenen Adresse.
	 *
	 * @see DoubleBuffer#get(double[], int, int)
	 * @param address Adresse.
	 * @param target {@code double}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void getDouble(long address, final double[] target, int offset, int length) {
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount8(index, length);
			source.position(index);
			source.asDoubleBuffer().get(target, offset, count);
			length -= count;
			offset += count;
			address += count * 8;
		}
	}

	/** Diese Methode füllt den gegebenen Puffer mit den {@code double}-Werten ab der gegebenen Adresse.
	 *
	 * @param address Adresse.
	 * @param target {@code double}-Puffer. */
	public void getDouble(long address, final DoubleBuffer target) {
		int length = target.remaining();
		while (length != 0) {
			final ByteBuffer source = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount4(index, length);
			source.limit(index + count);
			source.position(index);
			target.put(source.asDoubleBuffer());
			length -= count;
			address += count * 4;
		}
	}

	/** Diese Methode schreibt den gegebenen {@code double}-Wert an die gegebene Adresse.
	 *
	 * @see DoubleBuffer#put(int, double)
	 * @param address Adresse.
	 * @param source {@code double}-Wert. */
	public void putDouble(final long address, final double source) {
		this.buffers[MappedBuffer.bufferIndex(address)].putDouble(MappedBuffer.valueIndex(address), source);
	}

	/** Diese Methode schreibt die {@code double}-Werte des gegebenen Arrays an die gegebene Adresse.
	 *
	 * @see DoubleBuffer#put(double[])
	 * @param address Adresse.
	 * @param source {@code double}-Array. */
	public void putDouble(final long address, final double[] source) {
		this.putDouble(address, source, 0, source.length);
	}

	/** Diese Methode schreibt die {@code double}-Werte des gegebenen Array-Abschnitts an die gegebene Adresse.
	 *
	 * @see DoubleBuffer#put(double[], int, int)
	 * @param address Adresse.
	 * @param source {@code double}-Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	public void putDouble(long address, final double[] source, int offset, int length) {
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount8(index, length);
			target.position(index);
			target.asDoubleBuffer().put(source, offset, count);
			length -= count;
			offset += count;
			address += count * 8;
		}
	}

	/** Diese Methode schreibt die {@code double}-Werte des gegebenen Puffers an die gegebene Adresse.
	 *
	 * @param address Adresse.
	 * @param source {@code double}-Puffer. */
	public void putDouble(long address, final DoubleBuffer source) {
		int length = source.remaining();
		while (length != 0) {
			final ByteBuffer target = this.buffers[MappedBuffer.bufferIndex(address)].duplicate().order(this.order);
			final int index = MappedBuffer.valueIndex(address);
			final int count = MappedBuffer.valueCount8(index, length);
			source.limit(index + count);
			target.position(index);
			target.asDoubleBuffer().put(source);
			length -= count;
			address += count * 8;
		}
	}

	/** Diese Methode gibt den Speicherbereich ab der gegebenen Adresse als {@link MMIArray Zahlenfolge} interpretiert zurück.
	 *
	 * @see IAMArray#MODE_INT8
	 * @see IAMArray#MODE_INT16
	 * @see IAMArray#MODE_INT32
	 * @see IAMArray#MODE_UINT8
	 * @see IAMArray#MODE_UINT16
	 * @param address Beginn des Speicherbereichs.
	 * @param length Anzahl der Zahlen im Speicherbereich.
	 * @param mode Zahlenkodierung zur Interpretation des Speicherbereichs.
	 * @return {@link MMIArray}-Sicht auf den Speicherbereich.
	 * @throws IllegalArgumentException Wenn {@link MMIArray#from(MappedBuffer, long, int, int)} diese auslöst. */
	public MMIArrayL getArray(final long address, final int length, final int mode) throws IllegalArgumentException {
		return MMIArray.from(this, address, length, mode);
	}

	/** Diese Methode schreibt die {@link MMIArray Zahlenfolge} an die gegebene Adresse. Diese wird dazu abhängig von ihrer {@link IAMArray#mode() Kodierung} in
	 * ein {@link IAMArray#toBytes() byte}-, {@link IAMArray#toShorts() short}- bzw. {@link IAMArray#toInts() int}-Array überführt, welches anschließend
	 * geschrieben wird.
	 *
	 * @see #put(long, byte[])
	 * @see #putShort(long, short[])
	 * @see #putInt(long, int[])
	 * @param address Adresse.
	 * @param source Zahlenfolge. */
	public void putArray(final long address, final IAMArray source) {
		final int mode = source.mode();
		if ((mode == IAMArray.MODE_INT8) || (mode == IAMArray.MODE_UINT8)) {
			this.put(address, source.toBytes());
		} else if ((mode == IAMArray.MODE_INT16) || (mode == IAMArray.MODE_UINT16)) {
			this.putShort(address, source.toShorts());
		} else {
			this.putInt(address, source.toInts());
		}
	}

	/** Diese Methode ist eine Abkürzung für {@link #put(long, MappedBuffer, long, long) this.put(target, this, source, length)}.
	 *
	 * @param target Beginn des Zielabschnitts.
	 * @param source Beginn des Quellabschnitts.
	 * @param length Länge des Abschnitts. */
	public void copy(final long target, final long source, final long length) {
		MappedBuffer.copyImpl(this.buffers, target, this.buffers, source, length);
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.fromArray(this.buffers) + EMU.fromAll(this.buffers);
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.file().toString(), this.size(), this.isReadonly());
	}

}
