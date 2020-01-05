package bee.creative.fem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import bee.creative.array.CompactArray;
import bee.creative.bind.Property;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.fem.FEMArray.CompactArray3;
import bee.creative.fem.FEMBinary.CompactBinary;
import bee.creative.fem.FEMFunction.ClosureFunction;
import bee.creative.fem.FEMFunction.CompositeFunction;
import bee.creative.fem.FEMFunction.ConcatFunction;
import bee.creative.fem.FEMString.CompactStringINT16;
import bee.creative.fem.FEMString.CompactStringINT32;
import bee.creative.fem.FEMString.CompactStringINT8;
import bee.creative.iam.IAMArray;
import bee.creative.io.MappedBuffer2;
import bee.creative.lang.Bytes;
import bee.creative.lang.Integers;
import bee.creative.lang.Objects;
import bee.creative.mmi.MMIArrayL;

/** Diese Klasse implementiert ein Objekt zur Kodierung und Dekodierung von {@link FEMFunction Funktionen} in {@link IAMArray Zahlenlisten}, die in einen
 * {@link MappedBuffer2 Dateipuffer} ausgelagert sind.
 * <p>
 * FILE = [HEAD][BODY...]<br>
 * HEAD = [MAGIC:4][COUNT:4][VALUE:4][ZERO:4]<br>
 * BODY = [TYPE:4][SIZE:4][DATA:SIZE][ZERO:0..15]<br>
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMCodec implements Property<FEMFunction>, Emuable {

	/** Diese Klasse implementiert eine {@link FEMArray Wertliste}, deren Elemente als {@link IAMArray Zahlenfolge} aus Referenzen gegeben sind und in
	 * {@link #customGet(int)} über einen gegebenen {@link FEMCodec} in Werte {@link FEMCodec#getValue(int) übersetzt} werden. */
	protected static class IndexArray extends FEMArray {

		/** Dieses Feld speichert den {@link FEMCodec} zur {@link FEMCodec#getValue(int) Übersetzung} der Referenzen aus {@link #items}. */
		public final FEMCodec index;

		/** Dieses Feld speichert die Zahlenfolge mit den Referenzen. Ihre Struktur wird in {@link FEMCodec#OK_getArrayValue(IAMArray)} beschrieben. */
		public final IAMArray items;

		@SuppressWarnings ("javadoc")
		public IndexArray(final int length, final FEMCodec index, final IAMArray items) throws IllegalArgumentException {
			super(length);
			this.index = Objects.notNull(index);
			this.items = items;
			this.hash = items.get(length);
		}

		/** {@inheritDoc} */
		@Override
		protected FEMValue customGet(final int index) {
			return this.index.getValue(this.items.get(index));
		}

	}

	/** Diese Klasse implementiert ein indiziertes {@link IndexArray} mit beschleunigter der {@link #find(FEMValue, int) Einzelwertsuche}. */
	protected static class IndexArray2 extends IndexArray {

		@SuppressWarnings ("javadoc")
		public IndexArray2(final int length, final FEMCodec index, final IAMArray items) throws IllegalArgumentException {
			super(length, index, items);
		}

		/** {@inheritDoc} */
		@Override
		protected int customFind(final FEMValue that, final int offset, int length, final boolean foreward) {
			// items = (value[length], hash[1], index[length], range[count], length[1])
			final int count = (this.length() * 2) + 1, index = (that.hashCode() & (this.items.length() - count - 3)) + count;
			int l = this.items.get(index), r = this.items.get(index + 1) - 1;
			length += offset;
			if (foreward) {
				for (; l <= r; l++) {
					final int result = this.items.get(l);
					if (length <= result) return -1;
					if ((offset <= result) && that.equals(this.customGet(result))) return result;
				}
			} else {
				for (; l <= r; r--) {
					final int result = this.items.get(r);
					if (result < offset) return -1;
					if ((result < length) && that.equals(this.customGet(result))) return result;
				}
			}
			return -1;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ArrayBinary extends FEMBinary {

		public final IAMArray array;

		public final int offset;

		ArrayBinary(final IAMArray array, final int offset, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(length);
			this.array = Objects.notNull(array);
			this.offset = offset;
			this.hash = hash;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return (byte)this.array.get(this.offset + index);
		}

		@Override
		protected FEMBinary customSection(final int offset, final int length) {
			return new ArrayBinary(this.array, this.offset + offset, length, 0);
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ArrayString32 extends FEMString {

		public final IAMArray array;

		public final int offset;

		ArrayString32(final IAMArray array, final int offset, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(length);
			this.array = Objects.notNull(array);
			this.offset = offset;
			this.hash = hash;
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.array.get(this.offset + index);
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new ArrayString32(this.array, this.offset + offset, length, 0);
		}

		@Override
		public FEMString compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ArrayString8 extends ArrayString32 {

		ArrayString8(final IAMArray array, final int offset, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(array, offset, length, hash);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return super.customGet(index) & 0xFF;
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new ArrayString8(this.array, this.offset + offset, length, 0);
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ArrayString16 extends ArrayString32 {

		ArrayString16(final IAMArray array, final int offset, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(array, offset, length, hash);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return super.customGet(index) & 0xFFFF;
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new ArrayString16(this.array, this.offset + offset, length, 0);
		}

	}

	/** Dieses Feld speichert die Typkennung für {@link FEMVoid#INSTANCE}. */
	protected static final int TYPE_VOID = 1;

	/** Dieses Feld speichert die Typkennung für {@link FEMBoolean#TRUE}. */
	protected static final int TYPE_TRUE = 2;

	/** Dieses Feld speichert die Typkennung für {@link FEMBoolean#FALSE}. */
	protected static final int TYPE_FALSE = 3;

	/** Dieses Feld speichert die Typkennung für {@link CompactArray}. */
	protected static final int TYPE_ARRAY_A = 4;

	/** Dieses Feld speichert die Typkennung für {@link CompactArray3}. */
	protected static final int TYPE_ARRAY_B = 5;

	/** Dieses Feld speichert die Typkennung für {@link CompactStringINT8}. */
	protected static final int TYPE_STRING8 = 6;

	/** Dieses Feld speichert die Typkennung für {@link CompactStringINT16}. */
	protected static final int TYPE_STRING16 = 7;

	/** Dieses Feld speichert die Typkennung für {@link CompactStringINT32}. */
	protected static final int TYPE_STRING32 = 8;

	/** Dieses Feld speichert die Typkennung für {@link CompactBinary}. */
	protected static final int TYPE_BINARY = 9;

	/** Dieses Feld speichert die Typkennung für {@link FEMInteger}. */
	protected static final int TYPE_INTEGER = 10;

	/** Dieses Feld speichert die Typkennung für {@link FEMDecimal}. */
	protected static final int TYPE_DECIMAL = 11;

	/** Dieses Feld speichert die Typkennung für {@link FEMDuration}. */
	protected static final int TYPE_DURATION = 12;

	/** Dieses Feld speichert die Typkennung für {@link FEMDatetime}. */
	protected static final int TYPE_DATETIME = 13;

	/** Dieses Feld speichert die Typkennung für {@link FEMHandler}. */
	protected static final int TYPE_HANDLER = 14;

	/** Dieses Feld speichert die Typkennung für {@link FEMObject}. */
	protected static final int TYPE_OBJECT = 15;

	/** Dieses Feld speichert die Typkennung für {@link FEMProxy}. */
	protected static final int TYPE_PROXY = 20;

	/** Dieses Feld speichert die Typkennung für {@link FEMParam}. */
	protected static final int TYPE_PARAM_FUNCTION = 21;

	/** Dieses Feld speichert die Typkennung für {@link ConcatFunction}. */
	protected static final int TYPE_CONCAT_FUNCTION = 22;

	/** Dieses Feld speichert die Typkennung für {@link ClosureFunction}. */
	protected static final int TYPE_CLOSURE = 23;

	/** Dieses Feld speichert die Typkennung für {@link CompositeFunction}. */
	protected static final int TYPE_COMPOSITE_FUNCTION = 24;

	/** Dieses Feld speichert den Puffer, in dem die Yahlenfolgen abgelegt sind. */
	private final MappedBuffer2 store;

	private ArrayList<FEMFunction> cacheListing;

	private boolean cacheEnabled = true;

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei.
	 *
	 * @see MappedBuffer2#MappedBuffer2(File, boolean)
	 * @param file Datei.
	 * @param readonly {@code true}, wenn die Datei nur mit Lesezugriff angebunden werden soll.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */

	public FEMCodec(final File file, final boolean readonly) throws IOException {
		this.store = new MappedBuffer2(file, readonly);
	}

	@Override
	public final FEMFunction get() {
		return this.getFunction(this.store.getRoot());
	}

	@Override
	public final void set(final FEMFunction value) {
		this.store.setRoot(this.putFunction(value));
	}

	/** Diese Methode gibt den angebundenen {@link MappedBuffer2 Puffer} zurück, in welchem die kodierten {@link FEMFunction Funktionen} abgelegt sind.
	 *
	 * @see #useSource(MappedBuffer2)
	 * @see #useTarget(MappedBuffer2)
	 * @return Puffer oder {@code null}. */
	public final MappedBuffer2 getBuffer() {
		return this.store;
	}

	/** Diese Methode setzt die {@link MappedBuffer2#getReusing() Aktivierung der Wiederverwendung von Speicherbereichen} und gibt {@code this} zurück.
	 *
	 * @param enabled Aktivierung der Wiederverwendung.
	 * @return {@code this}. */
	public FEMCodec useReuse(final boolean enabled) {
		this.store.setReusing(enabled);
		return this;
	}

	public FEMCodec useCache(final boolean enabled) {
		this.cacheEnabled = enabled;
		return this;
	}

	public FEMValue getValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMValue.class);
	}

	/** Diese Methode nimmt den gegebenen Wert in die Verwaltung auf und gibt die Referenz darauf zurück.
	 *
	 * @param src Wert.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Wert nicht aufgenommen werden kann. */
	public int putValue(final FEMValue src) throws NullPointerException, IllegalArgumentException {
		if (src instanceof FEMVoid) return this.OK_putVoidValue();
		if (src instanceof FEMArray) return this.putArrayValue((FEMArray)src.data());
		if (src instanceof FEMHandler) return this.putHandlerValue((FEMHandler)src.data());
		if (src instanceof FEMBoolean) return this.putBooleanValue((FEMBoolean)src.data());
		if (src instanceof FEMString) return this.putStringValue((FEMString)src.data());
		if (src instanceof FEMBinary) return this.putBinaryValue((FEMBinary)src.data());
		if (src instanceof FEMInteger) return this.OK_putIntegerValue((FEMInteger)src.data());
		if (src instanceof FEMDecimal) return this.OK_putDecimalValue((FEMDecimal)src.data());
		if (src instanceof FEMDuration) return this.putDurationValue((FEMDuration)src.data());
		if (src instanceof FEMDatetime) return this.putDatetimeValue((FEMDatetime)src.data());
		if (src instanceof FEMObject) return this.putObjectValue((FEMObject)src.data());
		if (src instanceof FEMFuture) return this.putFutureValue((FEMFuture)src);
		if (src instanceof FEMNative) return this.putNativeValue((FEMNative)src);
		return this.putCustomValue(src);
	}

	/** Diese Methode fügt {@link FEMVoid#INSTANCE} in den {@link #getBuffer() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @return Referenz.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int OK_putVoidValue() throws IllegalStateException {
		this.store.setupRegion(FEMCodec.TYPE_VOID, 0);
		return this.store.commitRegion();
	}

	/** Diese Methode gibt die Wertliste zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Wertliste.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMArray OK_getArrayValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMArray.class);
	}

	/** Diese Methode nimmt die gegebene Wertliste in die Verwaltung auf und gibt die {@link #getDataRef(int, int)} Referenz} darauf zurück. Eine über *
	 * {@link FEMArray#compact(boolean)} indizierte Wertliste wird mit der Indizierung kodiert.
	 * 
	 * @param src Dezimalbruch.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #toArrayArray(FEMArray)} diese auslöst. */
	public int putArrayValue(final FEMArray src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.length();
		final int[] result;
		if (src instanceof CompactArray3) {
			// value[length], hash[1], index[length], range[count], length[1] => 2xlength + count + 2
			final int[] table = ((CompactArray3)src).table;
			final int count = table[0], offset1 = length + 1, offset2 = offset1 + length, offset3 = offset1 - count;
			result = new int[offset2 + count + 1];
			System.arraycopy(table, count, result, offset1, length);
			for (int i = 0; i < count; i++) {
				result[i + offset2] = table[i] + offset3;
			}
		} else {
			// value[length], hash[1], length[1] => 1xlength + 2
			result = new int[length + 2];
		}
		result[length] = src.hashCode();
		result[result.length - 1] = length;
		for (int i = 0; i < length; i++) {
			result[i] = this.putValue(src.customGet(i));
		}
		return IAMArray.from(result);
	}

	/** Diese Methode gibt eine Wertliste zurück, deren Elemente in der gegebenen Zahlenfolge sind. Die Zahlenfolge kann dazu in einer der folgenden Strukturen
	 * vorliegen:
	 * <ul>
	 * <li>Einfach - {@code (value[length], hash[1], length[1])}<br>
	 * Die Zahlenfolge beginnt mit den über {@link #putValue(FEMValue)} ermittelten {@link #getDataRef(int, int) Referenzen} der Elemente der gegebenen Wertliste
	 * und endet mit dem {@link FEMArray#hashCode() Streuwert} sowie der {@link FEMArray#length() Länge} der Wertliste.</li>
	 * </ul>
	 *
	 * @param src Zahlenfolge.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	protected FEMArray getArrayValueA(long addr, int size) throws NullPointerException, IllegalArgumentException {
		final int length1 = src.length() - 2, length2 = src.get(length1 + 1);
		if (length1 == length2) return new IndexArray(length1, this, src);
		return new IndexArray2(length2, this, src);
	}

	/** Diese Methode gibt eine Wertliste zurück, deren Elemente in der gegebenen Zahlenfolge sind. Die Zahlenfolge kann dazu in einer der folgenden Strukturen
	 * vorliegen:
	 * <ul>
	 * <li>Indiziert - {@code (value[length], hash[1], index[length], range[count], length[1])}<br>
	 * Die Zahlenfolge beginnt ebenfalls mit den Referenzen sowie dem Streuwert und endet auch mit der Länge der Wertliste. Dazwischen enthält sie die Inhalte
	 * sowie die Größen der Streuwertbereiche.</li>
	 * </ul>
	 *
	 * @param src Zahlenfolge.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	protected FEMArray getArrayValueB(long addr, int size) throws NullPointerException, IllegalArgumentException {
		final int length1 = src.length() - 2, length2 = src.get(length1 + 1);
		if (length1 == length2) return new IndexArray(length1, this, src);
		return new IndexArray2(length2, this, src);
	}

	/** Diese Methode gibt die Zeichenkette zur gegebenen Referenz zurück. */
	public final FEMString getStringValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMString.class);
	}

	/** Diese Methode gibt eine Zeichenkette zurück, deren Codepoints in der gegebenen Zahlenfolge kodiert sind.
	 * <p>
	 * Diese Methode interpretiert die gegebene Zahlenfolge als Zeichenkette und gibt diese zurück. Bei der Kodierung mit Einzelwerten werden die ersten vier Byte
	 * der Zahlenfolge als {@link #hashCode() Streuwert}, die darauf folgenden Zahlenwerte als Auflistung der einzelwertkodierten Codepoints und der letzte
	 * Zahlenwert als abschließende {@code 0} interpretiert. Bei der Mehrwertkodierung werden dagegen die ersten vier Byte der Zahlenfolge als {@link #hashCode()
	 * Streuwert}, die nächsten vier Byte als {@link FEMString#length() Zeichenanzahl}, die darauf folgenden Zahlenwerte als Auflistung der mehrwertkodierten
	 * Codepoints und der letzte Zahlenwert als abschließende {@code 0} interpretiert. Ob eine 8-, 16- oder 32-Bit-Kodierung eingesetzt wird, hängt von der
	 * {@link IAMArray#mode() Kodierung der Zahlenwerte} ab. */
	protected FEMString getStringValue(final long addr, final int size) throws NullPointerException, IllegalArgumentException {
		final MappedBuffer2 store = this.store;
		final int length = size - 4;
		final byte[] temp = new byte[4];
		store.get(addr, temp);
		final int hash = Integers.toInt(temp[0], temp[1], temp[2], temp[3]);
		final IAMArray array = store.getArray(addr + 4, length, IAMArray.MODE_INT8);
		return new ArrayString8(array, 0, length, hash);
	}

	protected FEMString getStringValue16(final long addr, final int size) throws NullPointerException, IllegalArgumentException {
		final MappedBuffer2 store = this.store;
		final int length = (size - 4) / 2;
		final short[] temp = new short[2];
		store.getShort(addr, temp);
		final int hash = Integers.toInt(temp[0], temp[1]);
		final IAMArray array = store.getArray(addr + 4, length, IAMArray.MODE_INT16);
		return new ArrayString16(array, 0, length, hash);
	}

	protected FEMString getStringValue32(final long addr, final int size) throws NullPointerException, IllegalArgumentException {
		final MappedBuffer2 store = this.store;
		final int length = (size - 4) / 4;
		final int hash = store.getInt(addr);
		final MMIArrayL array = store.getArray(addr + 4, length, IAMArray.MODE_INT32);
		return new ArrayString32(array, 0, length, hash);
	}

	public int putStringValue(final FEMString src) throws NullPointerException {
		final FEMString str = src.compact();
		final int hash = str.hashCode(), length = str.length();
		if (str instanceof FEMString.CompactStringINT8) {
			final byte[] data = new byte[length + 4];
			data[0] = (byte)(hash >>> 24);
			data[1] = (byte)(hash >>> 16);
			data[2] = (byte)(hash >>> 8);
			data[3] = (byte)(hash >>> 0);
			str.extract(new FEMString.INT8Encoder(data, 4));
			return this.putData(FEMCodec.TYPE_STRING8, data);
		} else if (str instanceof FEMString.CompactStringINT16) {
			final short[] data = new short[length + 2];
			data[0] = (short)(hash >>> 16);
			data[1] = (short)(hash >>> 0);
			str.extract(new FEMString.INT16Encoder(data, 2));
			return this.putData(FEMCodec.TYPE_STRING16, data);
		} else {
			final int[] data = new int[length + 1];
			data[0] = hash;
			str.extract(new FEMString.INT32Encoder(data, 1));
			return this.putData(FEMCodec.TYPE_STRING32, data);
		}
	}

	/** Diese Methode gibt die Bytefolge zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMBinary OK_getBinaryValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMBinary.class);
	}

	/** Diese Methode gibt die Bytefolge zur gegebenen Zahlenfolge zurück, deren Bytes in der gegebenen Zahlenfolge kodiert sind. Die Zahlenfolge muss dazu in
	 * {@link IAMArray#MODE_INT8} vorliegen und in ihren ersten vier Byte die Typkennung sowie in den zweiten vier Byte den Streuwert (beides little-endian)
	 * enthalten. */
	protected final FEMBinary getBinaryValue(final long addr, final int size) throws NullPointerException, IllegalArgumentException {
		final int length = size - 4;
		if (length < 0) throw new IllegalArgumentException();
		if (length == 0) return FEMBinary.EMPTY;
		final byte[] temp = new byte[4];
		final MappedBuffer2 store = this.store;
		store.get(addr, temp);
		final int hash = Bytes.getInt4LE(temp, 0);
		final IAMArray array = store.getArray(addr + 4, length, IAMArray.MODE_INT8);
		return new ArrayBinary(array, 0, length, hash);
	}

	/** Diese Methode ergänzt die gegebene Bytefolge und gibt die Referenz darauf zurück. */
	public final int putBinaryValue(final FEMBinary src) throws NullPointerException, IllegalStateException {
		final byte[] data = new byte[src.length() + 4];
		Bytes.setInt4LE(data, 0, src.hashCode());
		src.extract(data, 4);
		return this.putData(FEMCodec.TYPE_BINARY, data);
	}

	/** Diese Methode gibt die Dezimalzahl zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Dezimalzahl.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMInteger OK_getIntegerValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMInteger.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Dezimalzahl zurück.<br>
	 * Diese muss den MSB-{@code int} sowie den LSB-{@code int} der {@link FEMInteger#value() internen Darstellung} der Dezimalzahl enthalten.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Dezimalzahl. */
	protected FEMInteger OK_getIntegerValue(final long addr, final int size) {
		final MappedBuffer2 store = this.store;
		return new FEMInteger(Integers.toLong(store.getInt(addr), store.getInt(addr + 4)));
	}

	/** Diese Methode fügt die gegebene Dezimalzahl in den {@link #getBuffer() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Dezimalzahl.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int OK_putIntegerValue(final FEMInteger src) throws NullPointerException, IllegalStateException {
		this.store.putLong(this.store.setupRegion(FEMCodec.TYPE_INTEGER, 8), src.value());
		return this.store.commitRegion();
	}

	/** Diese Methode gibt den Dezimalbruch zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Dezimalbruch.
	 * @throws IllegalStateException Wenn kein {@link #getBuffer() Puffer} angebunden ist.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMDecimal OK_getDecimalValue(final int ref) throws IllegalStateException, IllegalArgumentException {
		return this.getFunction(ref, FEMDecimal.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Dezimalbruch und gibt diesen zurück. Die Zahlenfolge muss dazu aus den folgenden drei Zahlen
	 * bestehen: (1) Typkennung sowie (2) MSB-{@code int} und (3) LSB-{@code int} der {@link FEMDecimal#value() internen Darstellung} des Dezimalbruchs.
	 * Andernfalls wird {@code null} geliefert. */
	final FEMDecimal getDecimalValue(final long addr, final int size) throws NullPointerException {
		final MappedBuffer2 store = this.store;
		return new FEMDecimal(Double.longBitsToDouble(Integers.toLong(store.getInt(addr), store.getInt(addr + 4))));
	}

	/** Diese Methode fügt den gegebenen Dezimalbruch in den {@link #getBuffer() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Dezimalbruch.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum {@link #useSource(MappedBuffer2) Lesen} angebunden ist. */
	public int OK_putDecimalValue(final FEMDecimal src) throws NullPointerException, IllegalStateException {
		final long value = Double.doubleToLongBits(src.value());
		return this.putData(FEMCodec.TYPE_DECIMAL, Integers.toIntH(value), Integers.toIntL(value));
	}

	/** Diese Methode gibt die Zeitspanne zur gegebenen Referenz zurück. */
	public final FEMDuration getDurationValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMDuration.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Zeitspanne und gibt diese zurück. Die Zahlenfolge muss dazu aus folgenden drei Zahlen bestehen:
	 * (1) Typkennung sowie (2) MSB-{@code int} und (3) LSB-{@code int} der {@link FEMDuration#value() internen Darstellung} der Zeitspanne. Andernfalls wird
	 * {@code null} geliefert. */
	final FEMDuration getDurationValue(final long addr, final int size) throws NullPointerException, IllegalArgumentException {
		final MappedBuffer2 store = this.store;
		return new FEMDuration(Integers.toLong(store.getInt(addr), store.getInt(addr + 4)));
	}

	/** Diese Methode ergänzt die gegebene Zeitspanne und gibt die Referenz darauf zurück. */
	public final int putDurationValue(final FEMDuration src) throws NullPointerException, IllegalStateException {
		final long value = src.value();
		return this.putData(FEMCodec.TYPE_DURATION, Integers.toIntH(value), Integers.toIntL(value));
	}

	/** Diese Methode gibt die Zeitangabe zur gegebenen Referenz zurück. */
	public final FEMDatetime getDatetimeValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMDatetime.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Zeitangabe und gibt diese zurück. Die Zahlenfolge muss dazu aus folgenden drei Zahlen bestehen:
	 * (1) Typkennung sowie (2) MSB-{@code int} und (3) LSB-{@code int} der {@link FEMDatetime#value() internen Darstellung} der Zeitangabe. Andernfalls wird
	 * {@code null} geliefert. */
	public final FEMDatetime getDatetimeValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 3) || (src.get(0) != FEMCodec.TYPE_DATETIME)) return null;
		return new FEMDatetime(Integers.toLong(src.get(1), src.get(2)));
	}

	/** Diese Methode ergänzt die gegebene Zeitangabe und gibt die Referenz darauf zurück. */
	public final int putDatetimeValue(final FEMDatetime src) throws NullPointerException, IllegalStateException {
		final long value = src.value();
		return this.putData(FEMCodec.TYPE_DATETIME, Integers.toIntH(value), Integers.toIntL(value));
	}

	/** Diese Methode gibt den Wahrheitswert zur gegebenen Referenz zurück. */
	public final FEMBoolean getBooleanValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMBoolean.class);
	}

	/** Diese Methode ergänzt den gegebenen Wahrheitswert und gibt die Referenz darauf zurück. */
	public final int putBooleanValue(final FEMBoolean src) throws NullPointerException, IllegalStateException {
		return this.putData(src.value() ? FEMCodec.TYPE_TRUE : FEMCodec.TYPE_FALSE);
	}

	/** Diese Methode gibt den Funktionszeiger zur gegebenen Referenz zurück. */
	public final FEMHandler getHandlerValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMHandler.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionszeiger und gibt diese zurück. Die Zahlenfolge muss dazu aus der Typkennung sowie der
	 * Referenz auf die Zielfunktion bestehen. Andernfalls wird {@code null} geliefert. */
	public final FEMHandler getHandlerValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 2) || (src.get(0) != FEMCodec.TYPE_HANDLER)) return null;
		return new FEMHandler(this.getFunction(src.get(1)));
	}

	/** Diese Methode ergänzt den gegebenen Funktionszeiger und gibt die Referenz darauf zurück. */
	public final int putHandlerValue(final FEMHandler src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this.putData(FEMCodec.TYPE_HANDLER, this.putFunction(src.value()));
	}

	/** Diese Methode gibt die Objektreferenz zur gegebenen Referenz zurück. */
	public final FEMObject getObjectValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMObject.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Objektreferenz und gibt diesen zurück. Die Zahlenfolge muss dazu aus der Typkennung und dem
	 * MSB-{@code int} sowie dem LSB-{@code int} der {@link FEMObject#value() internen Darstellung} der Objektreferenz bestehen. Andernfalls wird {@code null}
	 * geliefert. */
	public final FEMObject getObjectValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 3) || (src.get(0) != FEMCodec.TYPE_OBJECT)) return null;
		return new FEMObject(Integers.toLong(src.get(1), src.get(2)));
	}

	/** Diese Methode ergänzt den gegebenen Objektreferenz und gibt die Referenz darauf zurück. */
	public final int putObjectValue(final FEMObject src) throws NullPointerException, IllegalStateException {
		final long value = src.value();
		return this.putData(FEMCodec.TYPE_OBJECT, Integers.toIntH(value), Integers.toIntL(value));
	}

	public int putFutureValue(final FEMFuture src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	public int putNativeValue(final FEMNative src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den Wert zur gegebenen Typkennung und {@link #toIndex(int) Position} zurück.
	 *
	 * @param type Typkennung
	 * @param index Position.
	 * @return Wert.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	protected FEMValue getCustomValue(final int type, final int index) throws IllegalArgumentException {
		return FEMVoid.INSTANCE;
	}

	protected int putCustomValue(final FEMValue src) throws NullPointerException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz zurück. Wenn deren Typkennung unbekannt ist, wird {@link FEMVoid#INSTANCE} geliefert.
	 *
	 * @param ref Referenz.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMFunction getFunction(final int ref) throws IllegalArgumentException {
		final MappedBuffer2 store = this.store;
		final long addr = store.getRegionAddr(ref);
		return this.OK_getFunction(store.getInt(addr), addr + 8, store.getInt(addr + 4));
	}

	protected final <GResult> GResult getFunction(final int ref, final Class<GResult> functionClass) throws IllegalArgumentException {
		try {
			return functionClass.cast(this.getFunction(ref));
		} catch (final IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die Funktion zu den gegebenen Merkmalen zurück. Wenn die Typkennung unbekannt ist, wird {@link FEMVoid#INSTANCE} geliefert. Nachfahren
	 * sollten diese Methode zur weiteren Fallunterscheidungen überschreiben.
	 * 
	 * @param type Typkennung.
	 * @param addr Adresse des Speicherbereichs im {@link #store}.
	 * @param size Größe des Speicherbereichs im {@link #store}.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	protected FEMFunction OK_getFunction(final int type, final long addr, final int size) throws IllegalArgumentException {
		switch (type) {
			case TYPE_TRUE:
				return FEMBoolean.TRUE;
			case TYPE_FALSE:
				return FEMBoolean.FALSE;
			case TYPE_ARRAY_A:
				return this.OK_getArrayValue(addr, size);
			case TYPE_STRING8:
				return this.getStringValue(addr, size);
			case TYPE_BINARY:
				return this.getBinaryValue(addr, size);
			case TYPE_INTEGER:
				return this.OK_getIntegerValue(addr, size);
			case TYPE_DECIMAL:
				return this.OK_getDecimalValue(addr, size);
			case TYPE_DATETIME:
				return this.getDatetimeValue(addr, size);
			case TYPE_DURATION:
				return this.getDurationValue(addr, size);
			case TYPE_HANDLER:
				return this.getHandlerValue(addr, size);
			case TYPE_OBJECT:
				return this.getObjectValue(addr, size);
			case TYPE_PROXY:
				return this.getProxyFunction(addr, size);
			case TYPE_PARAM_FUNCTION:
				return FEMParam.from(index);
			case TYPE_CONCAT_FUNCTION:
				return this.getConcatFunction(addr, size);
			case TYPE_CLOSURE:
				return this.getClosureFunction(addr, size);
			case TYPE_COMPOSITE_FUNCTION:
				return this.getCompositeFunction(addr, size);
		}
		return FEMVoid.INSTANCE;
	}

	/** Diese Methode nimmt die gegebene Funktion in die Verwaltung auf und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktion.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Funktion nicht aufgenommen werden kann. */
	public int putFunction(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (src instanceof FEMValue) return this.putValue((FEMValue)src);
		if (src instanceof FEMProxy) return this.putProxyFunction((FEMProxy)src);
		if (src instanceof FEMParam) return this.putParamFunction((FEMParam)src);
		if (src instanceof ConcatFunction) return this.putConcatFunction((ConcatFunction)src);
		if (src instanceof ClosureFunction) return this.putClosureFunction((ClosureFunction)src);
		if (src instanceof CompositeFunction) return this.putCompositeFunction((CompositeFunction)src);
		return this.putCustomFunction(src);
	}

	public final FEMProxy getProxyFunction(final int ref) throws IllegalArgumentException {
		return this.getProxyFunction(this.getData(ref));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionsplatzhalter und gibt diese zurück. Die Zahlenfolge muss dazu aus den folgenden vier
	 * Zahlen bestehen: (1) Typkennung, (2) {@link #putValue(FEMValue) Referenz} der {@link FEMProxy#id() Kennung}, (3) {@link #putValue(FEMValue) Referenz} des
	 * {@link FEMProxy#name() Namnes} und (4) {@link #putFunction(FEMFunction) Referenz} des {@link FEMProxy#get() Ziels}. Andernfalls wird {@code null}
	 * geliefert. */
	public final FEMProxy getProxyFunction(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 4) || (src.get(0) != FEMCodec.TYPE_PROXY)) return null;
		return new FEMProxy(this.getValue(src.get(1)), this.getStringValue(src.get(2)), this.getFunction(src.get(3)));
	}

	public final int putProxyFunction(final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		return this.putData(FEMCodec.TYPE_PROXY, this.putValue(src.id()), this.putStringValue(src.name()), this.putFunction(src.get()));
	}

	/** Diese Methode gibt die {@link #getDataRef(int, int)} Funktionsreferenz} auf die gegebene Parameterfunktion zurück.
	 *
	 * @param src Parameterfunktion.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist. */
	public int putParamFunction(final FEMParam src) throws NullPointerException, IllegalStateException {
		return this.getDataRef(FEMCodec.TYPE_PARAM_FUNCTION, src.index());
	}

	/** Diese Methode nimmt die gegebene Funktionkette in die Verwaltung auf und gibt die {@link #getDataRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param src Funktionkette.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getConcatArray(ConcatFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getConcatArray(ConcatFunction)} diese auslöst. */
	public int putConcatFunction(final ConcatFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this.getDataRef(FEMCodec.TYPE_CONCAT_FUNCTION, this.concatFunctionPool.put(src));
	}

	/** Diese Methode gibt die Funktionkette zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #concatFunctionPool}.
	 * @return Funktionkette.
	 * @throws IllegalArgumentException Wenn {@link #getConcatFunction(IAMArray)} diese auslöst. */
	/** Diese Methode gibt die Wertliste zur gegebenen Referenz zurück. */
	public final ConcatFunction getConcatFunction(final int index) throws IllegalArgumentException {
		return this.getFunction(ref, FEMDatetime.class);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getConcatFunction(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Funktionkette enthält.
	 *
	 * @param src Funktionkette.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putFunction(FEMFunction)} diese auslöst. */
	public IAMArray getConcatArray(final ConcatFunction src) throws NullPointerException, IllegalArgumentException {
		return this.getCompositeArrayImpl(src.function(), src.params());
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionkette und gibt diese zurück. Die Zahlenfolge muss dazu aus den über
	 * {@link #putFunction(FEMFunction)} ermittelten {@link #getDataRef(int, int) Funktionsreferenzen} der {@link ConcatFunction#function() verketteten Funktion}
	 * und iher {@link ConcatFunction#params() Parameterfunktionen} bestehen.
	 *
	 * @param src Zahlenfolge.
	 * @return Funktionkette.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public ConcatFunction getConcatFunction(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		return new ConcatFunction(this.getCompositeFunctionImpl(src), this.getCompositeParamsImpl(src));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getClosureFunction(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Funktionsbindung
	 * enthält. */
	public final IAMArray getClosureArray(final ClosureFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return IAMArray.from(FEMCodec.TYPE_CLOSURE, this.putFunction(src.function()));
	}

	/** Diese Methode gibt die Funktionsbindung zur gegebenen Referenz zurück. */
	public final ClosureFunction getClosureFunction(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, ClosureFunction.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionsbindungen und gibt diese zurück. Die Zahlenfolge muss dazu aus einer
	 * {@link #getDataRef(int, int) Funktionsreferenz} bestehen, welche über {@link #getFunction(int)} interpretiert wird.
	 *
	 * @param src Zahlenfolge.
	 * @return Funktionsbindungen.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public ClosureFunction getClosureFunction(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if (src.length() != 1) throw new IllegalArgumentException();
		return new ClosureFunction(this.getFunction(src.get(0)));
	}

	/** Diese Methode nimmt die gegebene Funktionsbindung in die Verwaltung auf und gibt die {@link #getDataRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param src Funktionsbindung.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst. */
	public int putClosureFunction(final ClosureFunction src) throws NullPointerException, IllegalArgumentException {
		return this.getDataRef(FEMCodec.TYPE_CLOSURE, this.closureFunctionPool.put(src));
	}

	/** Diese Methode nimmt den gegebenen Funktionsaufruf in die Verwaltung auf und gibt die {@link #getDataRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param src Funktionsaufruf.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getCompositeArray(CompositeFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getCompositeArray(CompositeFunction)} diese auslöst. */
	public int putCompositeFunction(final CompositeFunction src) throws NullPointerException, IllegalArgumentException {
		return this.getDataRef(FEMCodec.TYPE_COMPOSITE_FUNCTION, this.compositeFunctionPool.put(src));
	}

	/** Diese Methode gibt den Funktionsaufruf zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #compositeFunctionPool}.
	 * @return Funktionsaufruf.
	 * @throws IllegalArgumentException Wenn {@link #getCompositeFunction(IAMArray)} diese auslöst. */
	/** Diese Methode gibt die Wertliste zur gegebenen Referenz zurück. */
	public final CompositeFunction getCompositeFunction(final int index) throws IllegalArgumentException {
		return this.getFunction(ref, FEMDatetime.class);
	}

	IAMArray getCompositeArrayImpl(final FEMFunction function, final FEMFunction... params) throws NullPointerException, IllegalArgumentException {
		final int length = params.length;
		final int[] result = new int[length + 1];
		result[0] = this.putFunction(function);
		for (int i = 0; i < length; i++) {
			result[i + 1] = this.putFunction(params[i]);
		}
		return IAMArray.from(result);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getCompositeFunction(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Funktionsaufruf
	 * enthält.
	 *
	 * @param src Funktionsaufruf.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putFunction(FEMFunction)} diese auslöst. */
	public IAMArray getCompositeArray(final CompositeFunction src) throws NullPointerException, IllegalArgumentException {
		return this.getCompositeArrayImpl(src.function(), src.params());
	}

	FEMFunction getCompositeFunctionImpl(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if (src.length() == 0) throw new IllegalArgumentException();
		return this.getFunction(src.get(0));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionsaufruf und gibt diese zurück. Die Zahlenfolge muss dazu aus den über
	 * {@link #putFunction(FEMFunction)} ermittelten {@link #getDataRef(int, int) Funktionsreferenzen} der {@link CompositeFunction#function() aufgerufenen
	 * Funktion} und iher {@link CompositeFunction#params() Parameterfunktionen} bestehen.
	 *
	 * @param src Zahlenfolge.
	 * @return Funktionsaufruf.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public CompositeFunction getCompositeFunction(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		return new CompositeFunction(this.getCompositeFunctionImpl(src), this.getCompositeParamsImpl(src));
	}

	FEMFunction[] getCompositeParamsImpl(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		final int length = src.length() - 1;
		if (length < 0) throw new IllegalArgumentException();
		final FEMFunction[] result = new FEMFunction[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.getFunction(src.get(i + 1));
		}
		return result;
	}

	/** Diese Methode gibt die Funktion zur gegebenen Typkennung und {@link #toIndex(int) Position} zurück.
	 *
	 * @param type Typkennung
	 * @param index Position.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Funktionsreferenz ungültig ist. */
	protected FEMFunction getCustomFunction(final int type, final int index) throws IllegalArgumentException {
		return FEMVoid.INSTANCE;
	}

	protected int putCustomFunction(final FEMFunction src) throws NullPointerException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** {@inheritDoc} */
	@Override
	public long emu() {
		return EMU.fromObject(this) + this.reuseMapping.emu() + EMU.from(this.store);
	}

	/** Diese Methode leert den Cache der {@link #getFunction(int) gelesenen} Funktionen. */
	public void cleanup() {
		// TODO
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.arrayValuePool, this.stringValuePool, this.binaryValuePool, this.integerValuePool, this.decimalValuePool,
			this.durationValuePool, this.datetimeValuePool, this.handlerValuePool, this.objectValuePool, this.proxyFunctionPool, this.concatFunctionPool,
			this.closureFunctionPool, this.compositeFunctionPool);
	}

}
