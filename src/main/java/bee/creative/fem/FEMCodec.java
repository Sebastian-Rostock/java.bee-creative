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
import bee.creative.io.MappedBuffer;
import bee.creative.io.MappedBuffer2;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein Objekt zur Kodierung und Dekodierung von {@link FEMFunction Funktionen} in {@link IAMArray Zahlenlisten}, die in einen
 * {@link MappedBuffer2 Dateipuffer} ausgelagert sind.
 * <p>
 * FILE = [HEAD][BODY...]<br>
 * HEAD = [MAGIC:4][COUNT:4][VALUE:4][ZERO:4]<br>
 * BODY = [TYPE:4][SIZE:4][DATA:SIZE][ZERO:0..15]<br>
 * <p>
 * {@link FEMFuture} und {@link FEMNative} werden zwar angeboten aber bei der Kodierun nicht unterstützt.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMCodec implements Property<FEMFunction>, Emuable {

	/** Diese Klasse implementiert eine {@link FEMArray Wertliste}, deren Elemente als {@link IAMArray Zahlenfolge} aus Referenzen gegeben sind und in
	 * {@link #customGet(int)} über einen gegebenen {@link FEMCodec} in Werte {@link FEMCodec#__getValue(int) übersetzt} werden. */
	protected static class IndexArray extends FEMArray {

		/** Dieses Feld speichert den {@link FEMCodec} zur {@link FEMCodec#__getValue(int) Übersetzung} der Referenzen aus {@link #items}. */
		public final FEMCodec index;

		/** Dieses Feld speichert die Zahlenfolge mit den Referenzen. Ihre Struktur wird in {@link FEMCodec#__getArrayValue(IAMArray)} beschrieben. */
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
			return this.index.__getValue(this.items.get(index));
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
	public static class MappedBinary extends FEMBinary {

		public final MappedBuffer store;

		public final long addr;

		MappedBinary(final MappedBuffer store, final long addr, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(length);
			this.store = Objects.notNull(store);
			this.addr = addr;
			this.hash = hash;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.store.get(this.addr + index);
		}

		@Override
		protected FEMBinary customSection(final int offset, final int length) {
			return new MappedBinary(this.store, this.addr + offset, length, 0);
		}

	}

	@SuppressWarnings ("javadoc")
	public static class MappedStringA extends FEMString {

		public final MappedBuffer store;

		public final long addr;

		MappedStringA(final MappedBuffer store, final long addr, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(length);
			this.store = Objects.notNull(store);
			this.addr = addr;
			this.hash = hash;
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.store.get(this.addr + index) & 0xFF;
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new MappedStringA(this.store, this.addr + offset, length, 0);
		}

	}

	@SuppressWarnings ("javadoc")
	public static class MappedStringB extends MappedStringA {

		MappedStringB(final MappedBuffer store, final long addr, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(store, addr, length, hash);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.store.getShort(this.addr + (index * 2)) & 0xFFFF;
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new MappedStringB(this.store, this.addr + (offset * 2), length, 0);
		}

	}

	@SuppressWarnings ("javadoc")
	public static class MappedStringC extends MappedStringA {

		MappedStringC(final MappedBuffer store, final long addr, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(store, addr, length, hash);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.store.getInt(this.addr + (index * 4));
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new MappedStringC(this.store, this.addr + (offset * 4), length, 0);
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
	protected static final int TYPE_STRING_A = 6;

	/** Dieses Feld speichert die Typkennung für {@link CompactStringINT16}. */
	protected static final int TYPE_STRING_B = 7;

	/** Dieses Feld speichert die Typkennung für {@link CompactStringINT32}. */
	protected static final int TYPE_STRING_C = 8;

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

	/** {@inheritDoc} Sie ist eine Abkürzung für {@link MappedBuffer2#getRoot() this.getFunction(this.getStore().getRoot())}. */
	@Override
	public FEMFunction get() {
		return this.getFunction(this.store.getRoot());
	}

	/** {@inheritDoc} Sie ist eine Abkürzung für {@link MappedBuffer2#setRoot(int) this.getStore().setRoot(this.putFunction(value))}. */
	@Override
	public void set(final FEMFunction value) {
		this.store.setRoot(this.putFunction(value));
	}

	/** Diese Methode gibt den angebundenen {@link MappedBuffer2 Datenspeicher} zurück, in welchem die kodierten {@link FEMFunction Funktionen} abgelegt sind.
	 *
	 * @return Datenspeicher. */
	public MappedBuffer2 getStore() {
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

	/** Diese Methode gibt den Wert zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Wert.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMValue __getValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMValue.class);
	}

	/** Diese Methode fügt den gegebenen Wert in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Wert.
	 * @return Referenz auf den Wert.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putValue(final FEMValue src) throws NullPointerException, IllegalArgumentException {
		if (src instanceof FEMVoid) return this.__putVoidValue();
		if (src instanceof FEMArray) return this.putArrayValue((FEMArray)src.data());
		if (src instanceof FEMHandler) return this.__putHandlerValue((FEMHandler)src.data());
		if (src instanceof FEMBoolean) return this.__putBooleanValue((FEMBoolean)src.data());
		if (src instanceof FEMString) return this.__putStringValue((FEMString)src.data());
		if (src instanceof FEMBinary) return this.__putBinaryValue((FEMBinary)src.data());
		if (src instanceof FEMInteger) return this.__putIntegerValue((FEMInteger)src.data());
		if (src instanceof FEMDecimal) return this.__putDecimalValue((FEMDecimal)src.data());
		if (src instanceof FEMDuration) return this.__putDurationValue((FEMDuration)src.data());
		if (src instanceof FEMDatetime) return this.__putDatetimeValue((FEMDatetime)src.data());
		if (src instanceof FEMObject) return this.__putObjectValue((FEMObject)src.data());
		if (src instanceof FEMFuture) return this.__putFutureValue((FEMFuture)src);
		if (src instanceof FEMNative) return this.__putNativeValue((FEMNative)src);
		throw new IllegalArgumentException();
	}

	/** Diese Methode fügt {@link FEMVoid#INSTANCE} in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @return Referenz auf {@link FEMVoid#INSTANCE}.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putVoidValue() throws IllegalStateException {
		this.store.setupRegion(FEMCodec.TYPE_VOID, 0);
		return this.store.commitRegion();
	}

	/** Diese Methode gibt die Wertliste zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Wertliste.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMArray __getArrayValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMArray.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Wertliste zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Wertliste. */

	/** Diese Methode gibt eine Wertliste zurück, deren Elemente in der gegebenen Zahlenfolge sind. Die Zahlenfolge kann dazu in einer der folgenden Strukturen
	 * vorliegen:
	 * <ul>
	 * <li>Einfach - {@code (value[length], hash[1], length[1])}<br>
	 * Die Zahlenfolge beginnt mit den über {@link #__putValue(FEMValue)} ermittelten {@link #getDataRef(int, int) Referenzen} der Elemente der gegebenen
	 * Wertliste und endet mit dem {@link FEMArray#hashCode() Streuwert} sowie der {@link FEMArray#length() Länge} der Wertliste.</li>
	 * </ul>
	 *
	 * @param src Zahlenfolge.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	protected FEMArray getArrayValueA(final long addr, final int size) throws NullPointerException, IllegalArgumentException {
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
	protected FEMArray getArrayValueB(final long addr, final int size) throws NullPointerException, IllegalArgumentException {
		final int length1 = src.length() - 2, length2 = src.get(length1 + 1);
		if (length1 == length2) return new IndexArray(length1, this, src);
		return new IndexArray2(length2, this, src);
	}

	/** Diese Methode fügt die gegebene Wertliste in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.Eine über
	 * {@link FEMArray#compact(boolean)} indizierte Wertliste wird mit der Indizierung kodiert.
	 *
	 * @param src Wertliste.
	 * @return Referenz auf die Wertliste.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
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
			result[i] = this.__putValue(src.customGet(i));
		}
		return IAMArray.from(result);
	}

	/** Diese Methode gibt die Zeichenkette zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMString __getStringValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMString.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code byte}-Zeichenkette zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Zeichenkette. */
	protected FEMString __getStringValueA(final long addr, final int size) {
		return new MappedStringA(this.store, addr + 4, size - 4, this.store.getInt(addr));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code short}-Zeichenkette zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Zeichenkette. */
	protected FEMString __getStringValueB(final long addr, final int size) {
		return new MappedStringB(this.store, addr + 4, (size - 4) / 2, this.store.getInt(addr));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code int}-Zeichenkette zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Zeichenkette. */
	protected FEMString __getStringValueC(final long addr, final int size) {
		return new MappedStringC(this.store, addr + 4, (size - 4) / 4, this.store.getInt(addr));
	}

	/** Diese Methode fügt die gegebene Zeichenkette in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück. Dazu werden die
	 * {@link FEMString#compact() kompaktierten} Formen der Zeichenkette analysiert und entsprechend als {@code byte}, {@code short} oder {@code int}-Codepoints
	 * gespeichert.
	 *
	 * @param src Zeichenkette.
	 * @return Referenz auf die Zeichenkette.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putStringValue(final FEMString src) throws NullPointerException, IllegalStateException {
		final FEMString str = src.compact();
		if (str instanceof FEMString.CompactStringINT8) {
			final long addr = this.store.setupRegion(FEMCodec.TYPE_STRING_A, str.length() + 4);
			this.store.putInt(addr, str.hashCode());
			this.store.put(addr, str.toBytes());
			return this.store.commitRegion();
		} else if (str instanceof FEMString.CompactStringINT16) {
			final long addr = this.store.setupRegion(FEMCodec.TYPE_STRING_B, (str.length() * 2) + 2);
			this.store.putInt(addr, str.hashCode());
			this.store.putShort(addr, str.toShorts());
			return this.store.commitRegion();
		} else {
			final long addr = this.store.setupRegion(FEMCodec.TYPE_STRING_C, (str.length() * 4) + 1);
			this.store.putInt(addr, str.hashCode());
			this.store.putInt(addr, str.toInts());
			return this.store.commitRegion();
		}
	}

	/** Diese Methode gibt die Bytefolge zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMBinary __getBinaryValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMBinary.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Bytefolge zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Bytefolge. */
	protected FEMBinary __getBinaryValue(final long addr, final int size) {
		return new MappedBinary(this.store, addr + 4, size - 4, this.store.getInt(addr));
	}

	/** Diese Methode fügt die gegebene Bytefolge in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Bytefolge.
	 * @return Referenz auf die Bytefolge.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putBinaryValue(final FEMBinary src) throws NullPointerException, IllegalStateException {
		final long addr = this.store.setupRegion(FEMCodec.TYPE_BINARY, src.length() + 4);
		this.store.putInt(addr, src.hashCode());
		this.store.put(addr, src.value());
		return this.store.commitRegion();
	}

	/** Diese Methode gibt die Dezimalzahl zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Dezimalzahl.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMInteger __getIntegerValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMInteger.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Dezimalzahl zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Dezimalzahl. */
	protected FEMInteger __getIntegerValue(final long addr, final int size) {
		return new FEMInteger(this.store.getLong(addr));
	}

	/** Diese Methode fügt die gegebene Dezimalzahl in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Dezimalzahl.
	 * @return Referenz auf die Dezimalzahl.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putIntegerValue(final FEMInteger src) throws NullPointerException, IllegalStateException {
		this.store.putLong(this.store.setupRegion(FEMCodec.TYPE_INTEGER, 8), src.value());
		return this.store.commitRegion();
	}

	/** Diese Methode gibt den Dezimalbruch zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Dezimalbruch.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMDecimal __getDecimalValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMDecimal.class);
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltene Dezimalbruch zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Dezimalbruch. */
	protected FEMDecimal __getDecimalValue(final long addr, final int size) {
		return new FEMDecimal(this.store.getDouble(addr));
	}

	/** Diese Methode fügt den gegebenen Dezimalbruch in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Dezimalbruch.
	 * @return Referenz auf den Dezimalbruch.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putDecimalValue(final FEMDecimal src) throws NullPointerException, IllegalStateException {
		this.store.putDouble(this.store.setupRegion(FEMCodec.TYPE_DECIMAL, 8), src.value());
		return this.store.commitRegion();
	}

	/** Diese Methode gibt die Zeitspanne zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Zeitspanne.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMDuration __getDurationValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMDuration.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Zeitspanne zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Zeitspanne. */
	protected FEMDuration __getDurationValue(final long addr, final int size) {
		return new FEMDuration(this.store.getLong(addr));
	}

	/** Diese Methode fügt den gegebenen Zeitspanne in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Zeitspanne.
	 * @return Referenz auf den Zeitspanne.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putDurationValue(final FEMDuration src) throws NullPointerException, IllegalStateException {
		this.store.putDouble(this.store.setupRegion(FEMCodec.TYPE_DURATION, 8), src.value());
		return this.store.commitRegion();
	}

	/** Diese Methode gibt die Zeitangabe zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMDatetime __getDatetimeValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMDatetime.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Zeitangabe zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Zeitangabe. */
	protected FEMDatetime __getDatetimeValue(final long addr, final int size) {
		return new FEMDatetime(this.store.getLong(addr));
	}

	/** Diese Methode fügt die gegebene Zeitangabe in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Zeitangabe.
	 * @return Referenz auf die Zeitangabe.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putDatetimeValue(final FEMDatetime src) throws NullPointerException, IllegalStateException {
		this.store.putDouble(this.store.setupRegion(FEMCodec.TYPE_DATETIME, 8), src.value());
		return this.store.commitRegion();
	}

	/** Diese Methode gibt den Wahrheitswert zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Wahrheitswert.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMBoolean __getBooleanValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMBoolean.class);
	}

	/** Diese Methode fügt den gegebenen Wahrheitswert in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Wahrheitswert.
	 * @return Referenz auf den Wahrheitswert.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putBooleanValue(final FEMBoolean src) throws NullPointerException, IllegalStateException {
		this.store.setupRegion(src.value() ? FEMCodec.TYPE_TRUE : FEMCodec.TYPE_FALSE, 0);
		return this.store.commitRegion();
	}

	/** Diese Methode gibt den Funktionszeiger zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Funktionszeiger.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMHandler __getHandlerValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMHandler.class);
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltene Funktionszeiger zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Funktionszeiger. */
	protected FEMHandler __getHandlerValue(final long addr, final int size) {
		return new FEMHandler(this.getFunction(this.store.getInt(addr)));
	}

	/** Diese Methode fügt den gegebenen Funktionszeiger in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktionszeiger.
	 * @return Referenz auf den Funktionszeiger.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion des Funktionszeigers nicht angefügt werden kann. */
	public int __putHandlerValue(final FEMHandler src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		this.store.putInt(this.store.setupRegion(FEMCodec.TYPE_HANDLER, 4), this.putFunction(src.value()));
		return this.store.commitRegion();
	}

	/** Diese Methode gibt die Objektreferenz zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Objektreferenz.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMObject __getObjectValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMObject.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Objektreferenz zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Objektreferenz. */
	protected FEMObject __getObjectValue(final long addr, final int size) {
		return new FEMObject(this.store.getLong(addr));
	}

	/** Diese Methode fügt den gegebenen Objektreferenz in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Objektreferenz.
	 * @return Referenz auf den Objektreferenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putObjectValue(final FEMObject src) throws NullPointerException, IllegalStateException {
		this.store.putLong(this.store.setupRegion(FEMCodec.TYPE_OBJECT, 8), src.value());
		return this.store.commitRegion();
	}

	/** Diese Methode gibt den Ergebniswert zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Ergebniswert.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMFuture __getFutureValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMFuture.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Ergebniswert zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Ergebniswert. */
	protected FEMFuture __getFutureValue(final long addr, final int size) {
		throw new IllegalArgumentException();
	}

	/** Diese Methode fügt den gegebenen Ergebniswert in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Ergebniswert.
	 * @return Referenz auf den Ergebniswert.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion des Funktionszeigers nicht angefügt werden kann. */
	public int __putFutureValue(final FEMFuture src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den Nativwert zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Nativwert.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMNative __getNativeValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMNative.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Nativwert zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Nativwert. */
	protected FEMNative __getNativeValue(final long addr, final int size) {
		throw new IllegalArgumentException();
	}

	/** Diese Methode fügt den gegebenen Nativwert in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Nativwert.
	 * @return Referenz auf den Ergebniswert.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion des Funktionszeigers nicht angefügt werden kann. */
	public int __putNativeValue(final FEMNative src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
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

	protected <GResult> GResult getFunction(final int ref, final Class<GResult> functionClass) throws IllegalArgumentException {
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
				return this.__getArrayValue(addr, size);
			case TYPE_STRING_A:
				return this.__getStringValueA(addr, size);
			case TYPE_BINARY:
				return this.__getBinaryValue(addr, size);
			case TYPE_INTEGER:
				return this.__getIntegerValue(addr, size);
			case TYPE_DECIMAL:
				return this.__getDecimalValue(addr, size);
			case TYPE_DATETIME:
				return this.__getDatetimeValue(addr, size);
			case TYPE_DURATION:
				return this.__getDurationValue(addr, size);
			case TYPE_HANDLER:
				return this.__getHandlerValue(addr, size);
			case TYPE_OBJECT:
				return this.__getObjectValue(addr, size);
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
	 * @throws IllegalArgumentException Wenn die Funktion nicht angefügt werden kann. */
	public int putFunction(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (src instanceof FEMValue) return this.__putValue((FEMValue)src);
		if (src instanceof FEMProxy) return this.putProxyFunction((FEMProxy)src);
		if (src instanceof FEMParam) return this.putParamFunction((FEMParam)src);
		if (src instanceof ConcatFunction) return this.putConcatFunction((ConcatFunction)src);
		if (src instanceof ClosureFunction) return this.putClosureFunction((ClosureFunction)src);
		if (src instanceof CompositeFunction) return this.putCompositeFunction((CompositeFunction)src);
		return this.putCustomFunction(src);
	}

	/** Diese Methode gibt die Zeichenkette zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public final FEMProxy getProxyFunction(final int ref) throws IllegalArgumentException {
		return this.getProxyFunction(this.getData(ref));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionsplatzhalter und gibt diese zurück. Die Zahlenfolge muss dazu aus den folgenden vier
	 * Zahlen bestehen: (1) Typkennung, (2) {@link #__putValue(FEMValue) Referenz} der {@link FEMProxy#id() Kennung}, (3) {@link #__putValue(FEMValue) Referenz}
	 * des {@link FEMProxy#name() Namnes} und (4) {@link #putFunction(FEMFunction) Referenz} des {@link FEMProxy#get() Ziels}. Andernfalls wird {@code null}
	 * geliefert. */
	public final FEMProxy getProxyFunction(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 4) || (src.get(0) != FEMCodec.TYPE_PROXY)) return null;
		return new FEMProxy(this.__getValue(src.get(1)), this.__getStringValue(src.get(2)), this.getFunction(src.get(3)));
	}

	/** Diese Methode fügt den gegebenen Dezimalbruch in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Dezimalbruch.
	 * @return Referenz auf den Dezimalbruch.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public final int putProxyFunction(final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		return this.putData(FEMCodec.TYPE_PROXY, this.__putValue(src.id()), this.__putStringValue(src.name()), this.putFunction(src.get()));
	}

	/** Diese Methode gibt die {@link #getDataRef(int, int)} Funktionsreferenz} auf die gegebene Parameterfunktion zurück.
	 *
	 * @param src Parameterfunktion.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist. */
	/** Diese Methode fügt den gegebenen Dezimalbruch in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Dezimalbruch.
	 * @return Referenz auf den Dezimalbruch.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putParamFunction(final FEMParam src) throws NullPointerException, IllegalStateException {
		return this.getDataRef(FEMCodec.TYPE_PARAM_FUNCTION, src.index());
	}

	/** Diese Methode nimmt die gegebene Funktionkette in die Verwaltung auf und gibt die {@link #getDataRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param src Funktionkette.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getConcatArray(ConcatFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getConcatArray(ConcatFunction)} diese auslöst. */
	/** Diese Methode fügt den gegebenen Dezimalbruch in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Dezimalbruch.
	 * @return Referenz auf den Dezimalbruch.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putConcatFunction(final ConcatFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this.getDataRef(FEMCodec.TYPE_CONCAT_FUNCTION, this.concatFunctionPool.put(src));
	}

	/** Diese Methode gibt die Funktionkette zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #concatFunctionPool}.
	 * @return Funktionkette.
	 * @throws IllegalArgumentException Wenn {@link #getConcatFunction(IAMArray)} diese auslöst. */
	/** Diese Methode gibt die Wertliste zur gegebenen Referenz zurück. */
	/** Diese Methode gibt die Zeichenkette zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
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
	/** Diese Methode gibt die Zeichenkette zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
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
	/** Diese Methode fügt den gegebenen Dezimalbruch in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Dezimalbruch.
	 * @return Referenz auf den Dezimalbruch.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
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
	/** Diese Methode gibt die Zeichenkette zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
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
