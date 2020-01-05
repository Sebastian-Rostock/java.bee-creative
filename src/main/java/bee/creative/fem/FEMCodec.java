package bee.creative.fem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import bee.creative.mmi.MMIArrayL;

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

	/** Diese Klasse implementiert eine Wertliste, deren Elemente als Referenzen gegeben sind und in {@link #customGet(int)} über einen gegebenen {@link FEMCodec}
	 * in Werte {@link FEMCodec#__getValue(int) übersetzt} werden. */
	public static class MappedArrayA extends FEMArray {

		final MappedBuffer2 store;

		/** Dieses Feld speichert den {@link FEMCodec} zur {@link FEMCodec#__getValue(int) Übersetzung} der Referenzen. */
		final FEMCodec codec;

		/** Dieses Feld speichert die Adresse der Zahlenfolge mit den Referenzen. */
		final long addr;

		MappedArrayA(final FEMCodec codec, final long addr, final int length, final int hash) throws IllegalArgumentException {
			super(length);
			this.store = codec.store;
			this.codec = codec;
			this.addr = addr;
			this.hash = hash;
		}

		@Override
		protected FEMValue customGet(final int index) {
			// addr: value[length]
			return this.codec.__getValue(this.store.getInt(this.addr + (index * 4L)));
		}

	}

	/** Diese Klasse implementiert eine indizierte Wertliste mit beschleunigter {@link #find(FEMValue, int) Einzelwertsuche}. */
	static class MappedArrayB extends MappedArrayA {

		MappedArrayB(final FEMCodec codec, final long addr, final int length, final int hash) throws IllegalArgumentException {
			super(codec, addr, length, hash);
		}

		@Override
		protected int customFind(final FEMValue that, final int offset, int length, final boolean foreward) {
			// addr: value[length], count[1], range[count], index[length], length[1]
			final long addr = this.addr + (this.length * 4L);
			final int count = this.store.getInt(addr), hash = that.hashCode() & (count - 2);
			final long addr2 = addr + (hash * 4L);
			int l = this.store.getInt(addr2), r = this.store.getInt(addr2 + 4) - 1;
			length += offset;
			if (foreward) {
				for (; l <= r; l++) {
					final int result = this.store.getInt(addr + (l * 4L));
					if (length <= result) return -1;
					if ((offset <= result) && that.equals(this.customGet(result))) return result;
				}
			} else {
				for (; l <= r; r--) {
					final int result = this.store.getInt(addr + (r * 4L));
					if (result < offset) return -1;
					if ((result < length) && that.equals(this.customGet(result))) return result;
				}
			}
			return -1;
		}

	}

	/** Diese Klasse implementiert eine Bytefolge als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedBinary extends FEMBinary {

		final MappedBuffer store;

		final long addr;

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

	/** Diese Klasse implementiert eine {@code byte}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedStringA extends FEMString {

		final MappedBuffer store;

		final long addr;

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

	/** Diese Klasse implementiert eine {@code short}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedStringB extends MappedStringA {

		MappedStringB(final MappedBuffer store, final long addr, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(store, addr, length, hash);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.store.getShort(this.addr + (index * 2L)) & 0xFFFF;
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new MappedStringB(this.store, this.addr + (offset * 2L), length, 0);
		}

	}

	/** Diese Klasse implementiert eine {@code int}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedStringC extends MappedStringA {

		MappedStringC(final MappedBuffer store, final long addr, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(store, addr, length, hash);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.store.getInt(this.addr + (index * 4L));
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new MappedStringC(this.store, this.addr + (offset * 4L), length, 0);
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

	/** Dieses Feld speichert die Typkennung für {@link FEMFuture}. */
	protected static final int TYPE_FUTURE = 16;

	/** Dieses Feld speichert die Typkennung für {@link FEMNative}. */
	protected static final int TYPE_NATIVE = 17;

	/** Dieses Feld speichert die Typkennung für {@link FEMProxy}. */
	protected static final int TYPE_PROXY = 18;

	/** Dieses Feld speichert die Typkennung für {@link FEMParam}. */
	protected static final int TYPE_PARAM = 19;

	/** Dieses Feld speichert die Typkennung für {@link ConcatFunction}. */
	protected static final int TYPE_CONCAT = 20;

	/** Dieses Feld speichert die Typkennung für {@link ClosureFunction}. */
	protected static final int TYPE_CLOSURE = 21;

	/** Dieses Feld speichert die Typkennung für {@link CompositeFunction}. */
	protected static final int TYPE_COMPOSITE = 22;

	/** Dieses Feld speichert den Puffer, in dem die Yahlenfolgen abgelegt sind. */
	final MappedBuffer2 store;

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
		this.store.setRoot(this.put(value));
	}

	public final int put(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this.putFunction(src);
	}

	public final int[] putAll(final FEMFunction... src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.length;
		final int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.put(src[i]);
		}
		return result;
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
		return this.getFunctionAs(ref, FEMValue.class);
	}

	/** Diese Methode fügt den gegebenen Wert in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.<br>
	 * Nachfahren sollten diese Methode zur weiteren Fallunterscheidungen überschreiben.
	 *
	 * @param src Wert.
	 * @return Referenz auf den Wert.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putValue(final FEMValue src) throws NullPointerException, IllegalArgumentException {
		if (src instanceof FEMVoid) return this.__putVoidValue();
		if (src instanceof FEMArray) return this.__putArrayValue((FEMArray)src.data());
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
		this.store.openRegion(FEMCodec.TYPE_VOID, 0);
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die Wertliste zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Wertliste.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMArray __getArrayValue(final int ref) throws IllegalArgumentException {
		return this.getFunctionAs(ref, FEMArray.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Wertliste zurück. Die Struktur des Speicherbereichs ist {@code (hash[1], value[length])}.
	 * Sie beginnt mit dem {@link FEMArray#hashCode() Streuwert} und endet mit den über {@link #putAll(FEMFunction...) Referenzen} der Elemente der Wertliste.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Wertliste. */
	protected FEMArray __getArrayValueA(final long addr, final int size) {
		return new MappedArrayA(this, addr + 4, (size - 4) / 4, this.store.getInt(addr));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Wertliste zurück. Die Struktur des Speicherbereichs ist
	 * {@code (hash[1], length[1], value[length], count[1], range[count], index[length])}. Sie beginnt mit dem {@link FEMArray#hashCode() Streuwert} und
	 * {@link FEMArray#length() Länge} der Wertliste, gefolgt von den {@link #putAll(FEMFunction...) Referenzen} der Elemente der Wertliste sowie der
	 * {@link CompactArray3#table Streuwerttabelle} zur beschleunigten Einzelwertsuche.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Wertliste. */
	protected FEMArray __getArrayValueB(final long addr, final int size) throws NullPointerException, IllegalArgumentException {
		return new MappedArrayB(this, addr + 8, this.store.getInt(addr + 4), this.store.getInt(addr));
	}

	public static void main(final String[] args) throws Exception {
		final FEMCodec codec = new FEMCodec(File.createTempFile("fem-codec", ".bin"), false);

		final FEMInteger x = FEMInteger.from(3);
		final FEMArray arr = FEMArray.from(FEMInteger.from(1), FEMInteger.from(2), x, FEMInteger.from(4), FEMInteger.from(5)).compact(true);

		codec.set(arr);
		final FEMArray arr2 = (FEMArray)codec.get();

		final MappedBuffer2 s = codec.store;
		final MMIArrayL h = s.getArray(0, (int)s.size() / 4, IAMArray.MODE_INT32);
		System.out.println(Arrays.toString(h.toInts()));

		System.out.println(arr);
		System.out.println(arr2);
		System.out.println(Arrays.toString(((CompactArray3)arr).table));
	}

	/** Diese Methode fügt die gegebene Wertliste in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.Eine über
	 * {@link FEMArray#compact(boolean)} indizierte Wertliste wird mit der Indizierung kodiert.
	 *
	 * @param src Wertliste.
	 * @return Referenz auf die Wertliste.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putArrayValue(final FEMArray src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int[] values = this.putAll(src.value());
		if (src instanceof CompactArray3) {
			// addr: hash[1], length[1], value[length], count[1], range[count], index[length]
			final int[] table = ((CompactArray3)src).table;
			final int length = src.length(), count = table[0];
			final long addr = this.store.openRegion(FEMCodec.TYPE_ARRAY_B, (length * 8) + (count * 4) + 12);
			this.store.putInt(addr, src.hashCode());
			this.store.putInt(addr + 4, length);
			this.store.putInt(addr + 8, values);
			this.store.putInt(addr + (length * 4) + 8, table);
			return this.store.closeRegion();
		} else {
			// hash[1], value[length]
			final long addr = this.store.openRegion(FEMCodec.TYPE_ARRAY_A, (src.length() * 4) + 4);
			this.store.putInt(addr, src.hashCode());
			this.store.putInt(addr + 4, values);
			return this.store.closeRegion();
		}
	}

	/** Diese Methode gibt die Zeichenkette zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMString __getStringValue(final int ref) throws IllegalArgumentException {
		return this.getFunctionAs(ref, FEMString.class);
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
			final long addr = this.store.openRegion(FEMCodec.TYPE_STRING_A, str.length() + 4);
			this.store.putInt(addr, str.hashCode());
			this.store.put(addr, str.toBytes());
			return this.store.closeRegion();
		} else if (str instanceof FEMString.CompactStringINT16) {
			final long addr = this.store.openRegion(FEMCodec.TYPE_STRING_B, (str.length() * 2) + 2);
			this.store.putInt(addr, str.hashCode());
			this.store.putShort(addr, str.toShorts());
			return this.store.closeRegion();
		} else {
			final long addr = this.store.openRegion(FEMCodec.TYPE_STRING_C, (str.length() * 4) + 1);
			this.store.putInt(addr, str.hashCode());
			this.store.putInt(addr, str.toInts());
			return this.store.closeRegion();
		}
	}

	/** Diese Methode gibt die Bytefolge zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMBinary __getBinaryValue(final int ref) throws IllegalArgumentException {
		return this.getFunctionAs(ref, FEMBinary.class);
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
		final long addr = this.store.openRegion(FEMCodec.TYPE_BINARY, src.length() + 4);
		this.store.putInt(addr, src.hashCode());
		this.store.put(addr, src.value());
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die Dezimalzahl zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Dezimalzahl.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMInteger __getIntegerValue(final int ref) throws IllegalArgumentException {
		return this.getFunctionAs(ref, FEMInteger.class);
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
		this.store.putLong(this.store.openRegion(FEMCodec.TYPE_INTEGER, 8), src.value());
		return this.store.closeRegion();
	}

	/** Diese Methode gibt den Dezimalbruch zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Dezimalbruch.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMDecimal __getDecimalValue(final int ref) throws IllegalArgumentException {
		return this.getFunctionAs(ref, FEMDecimal.class);
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltenen Dezimalbruch zurück.
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
		this.store.putDouble(this.store.openRegion(FEMCodec.TYPE_DECIMAL, 8), src.value());
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die Zeitspanne zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Zeitspanne.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMDuration __getDurationValue(final int ref) throws IllegalArgumentException {
		return this.getFunctionAs(ref, FEMDuration.class);
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
		this.store.putDouble(this.store.openRegion(FEMCodec.TYPE_DURATION, 8), src.value());
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die Zeitangabe zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMDatetime __getDatetimeValue(final int ref) throws IllegalArgumentException {
		return this.getFunctionAs(ref, FEMDatetime.class);
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
		this.store.putDouble(this.store.openRegion(FEMCodec.TYPE_DATETIME, 8), src.value());
		return this.store.closeRegion();
	}

	/** Diese Methode gibt den Wahrheitswert zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Wahrheitswert.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMBoolean __getBooleanValue(final int ref) throws IllegalArgumentException {
		return this.getFunctionAs(ref, FEMBoolean.class);
	}

	/** Diese Methode fügt den gegebenen Wahrheitswert in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Wahrheitswert.
	 * @return Referenz auf den Wahrheitswert.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putBooleanValue(final FEMBoolean src) throws NullPointerException, IllegalStateException {
		this.store.openRegion(src.value() ? FEMCodec.TYPE_TRUE : FEMCodec.TYPE_FALSE, 0);
		return this.store.closeRegion();
	}

	/** Diese Methode gibt den Funktionszeiger zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Funktionszeiger.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMHandler __getHandlerValue(final int ref) throws IllegalArgumentException {
		return this.getFunctionAs(ref, FEMHandler.class);
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltenen Funktionszeiger zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Funktionszeiger. */
	protected FEMHandler __getHandlerValue(final long addr, final int size) {
		return new FEMHandler(this.getFunctionAt(addr));
	}

	/** Diese Methode fügt den gegebenen Funktionszeiger in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktionszeiger.
	 * @return Referenz auf den Funktionszeiger.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion des Funktionszeigers nicht angefügt werden kann. */
	public int __putHandlerValue(final FEMHandler src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int invokeRef = this.putFunction(src.value());
		this.store.putInt(this.store.openRegion(FEMCodec.TYPE_HANDLER, 4), invokeRef);
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die Objektreferenz zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Objektreferenz.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMObject __getObjectValue(final int ref) throws IllegalArgumentException {
		return this.getFunctionAs(ref, FEMObject.class);
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
		this.store.putLong(this.store.openRegion(FEMCodec.TYPE_OBJECT, 8), src.value());
		return this.store.closeRegion();
	}

	/** Diese Methode gibt den Ergebniswert zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Ergebniswert.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMFuture __getFutureValue(final int ref) throws IllegalArgumentException {
		return this.getFunctionAs(ref, FEMFuture.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltenen Ergebniswert zurück.
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
		return this.getFunctionAs(ref, FEMNative.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltenen Nativwert zurück.
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
		final long addr = this.store.getRegionAddr(ref);
		return this.getFunction(this.store.getInt(addr), addr + 8, this.store.getInt(addr + 4));
	}

	/** Diese Methode gibt die Funktion zu den gegebenen Merkmalen zurück. Wenn die Typkennung unbekannt ist, wird {@link FEMVoid#INSTANCE} geliefert. Nachfahren
	 * sollten diese Methode zur weiteren Fallunterscheidungen überschreiben.
	 *
	 * @param type Typkennung.
	 * @param addr Adresse des Speicherbereichs im {@link #store}.
	 * @param size Größe des Speicherbereichs im {@link #store}.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	protected FEMFunction getFunction(final int type, final long addr, final int size) throws IllegalArgumentException {
		switch (type) {
			case TYPE_TRUE:
				return FEMBoolean.TRUE;
			case TYPE_FALSE:
				return FEMBoolean.FALSE;
			case TYPE_ARRAY_A:
				return this.__getArrayValueA(addr, size);
			case TYPE_ARRAY_B:
				return this.__getArrayValueB(addr, size);
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
			case TYPE_FUTURE:
				return this.__getFutureValue(addr, size);
			case TYPE_NATIVE:
				return this.__getNativeValue(addr, size);
			case TYPE_PROXY:
				return this.__getProxyFunction(addr, size);
			case TYPE_PARAM:
				return this.__getParamFunction(addr, size);
			case TYPE_CONCAT:
				return this.__getConcatFunction(addr, size);
			case TYPE_CLOSURE:
				return this.__getClosureFunction(addr, size);
			case TYPE_COMPOSITE:
				return this.__getCompositeFunction(addr, size);
		}
		return FEMVoid.INSTANCE;
	}

	public <GResult> GResult getFunctionAs(final int ref, final Class<GResult> functionClass) throws IllegalArgumentException {
		try {
			return functionClass.cast(this.getFunction(ref));
		} catch (final IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	protected FEMFunction getFunctionAt(final long addr) {
		return this.getFunction(this.store.getInt(addr));
	}

	protected FEMFunction[] getFunctionAt(final long addr, final int size) {
		final int length = size / 4;
		final FEMFunction[] result = new FEMFunction[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.getFunctionAt(addr + (i * 4));
		}
		return result;
	}

	/** Diese Methode nimmt die gegebene Funktion in die Verwaltung auf und gibt die Referenz darauf zurück.<br>
	 * Nachfahren sollten diese Methode zur weiteren Fallunterscheidungen überschreiben.
	 *
	 * @param src Funktion.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Funktion nicht angefügt werden kann. */
	public int putFunction(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (src instanceof FEMValue) return this.__putValue((FEMValue)src);
		if (src instanceof FEMProxy) return this.__putProxyFunction((FEMProxy)src);
		if (src instanceof FEMParam) return this.__putParamFunction((FEMParam)src);
		if (src instanceof ConcatFunction) return this.__putConcatFunction((ConcatFunction)src);
		if (src instanceof ClosureFunction) return this.__putClosureFunction((ClosureFunction)src);
		if (src instanceof CompositeFunction) return this.__putCompositeFunction((CompositeFunction)src);
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den Platzhalter zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Platzhalter.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMProxy __getProxyFunction(final int ref) throws IllegalArgumentException {
		return this.getFunctionAs(ref, FEMProxy.class);
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltenen Platzhalter zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Platzhalter. */
	protected FEMProxy __getProxyFunction(final long addr, final int size) throws NullPointerException, IllegalArgumentException {
		return new FEMProxy(this.__getValue(this.store.getInt(addr)), this.__getStringValue(this.store.getInt(addr + 4)), this.getFunctionAt(addr + 8));
	}

	/** Diese Methode fügt den gegebenen Platzhalter in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Platzhalter.
	 * @return Referenz auf den Platzhalter.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putProxyFunction(final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		final int idRef = this.__putValue(src.id()), nameRef = this.__putStringValue(src.name()), invokeRef = this.putFunction(src.get());
		final long addr = this.store.openRegion(FEMCodec.TYPE_PROXY, 12);
		this.store.putInt(addr, idRef);
		this.store.putInt(addr + 4, nameRef);
		this.store.putInt(addr + 8, invokeRef);
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Parameterfunktion zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Parameterfunktion. */
	protected FEMParam __getParamFunction(final long addr, final int size) {
		return FEMParam.from(this.store.getInt(addr));
	}

	/** Diese Methode fügt die gegebene Parameterfunktion in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Parameterfunktion.
	 * @return Referenz auf die Parameterfunktion.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putParamFunction(final FEMParam src) throws NullPointerException, IllegalStateException {
		this.store.putInt(this.store.openRegion(FEMCodec.TYPE_PARAM, 4), src.index());
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Funktionkette zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Funktionkette. */
	protected ConcatFunction __getConcatFunction(final long addr, final int size) {
		return new ConcatFunction(this.getFunctionAt(addr), this.getFunctionAt(addr + 4, size - 4));
	}

	/** Diese Methode fügt die gegebene Funktionkette in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktionkette.
	 * @return Referenz auf die Funktionkette.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putConcatFunction(final ConcatFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int invokeRef = this.putFunction(src.function);
		final int[] paramRefs = this.putAll(src.params);
		final long addr = this.store.openRegion(FEMCodec.TYPE_CONCAT, (paramRefs.length * 4) + 4);
		this.store.putInt(addr, invokeRef);
		this.store.putInt(addr + 4, paramRefs);
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Funktionsbindung zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Funktionsbindung. */
	protected ClosureFunction __getClosureFunction(final long addr, final int size) {
		return new ClosureFunction(this.getFunctionAt(addr));
	}

	/** Diese Methode fügt die gegebene Funktionsbindung in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktionsbindung.
	 * @return Referenz auf die Funktionsbindung.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion des Funktionszeigers nicht angefügt werden kann. */
	public int __putClosureFunction(final ClosureFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int invokeRef = this.putFunction(src.function);
		this.store.putInt(this.store.openRegion(FEMCodec.TYPE_CLOSURE, 4), invokeRef);
		return this.store.closeRegion();
	}

	/** Diese Methode gibt dden im gegebenen Speicherbereich enthaltenen Funktionsaufruf zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs
	 * @return Funktionsaufruf. */
	protected CompositeFunction __getCompositeFunction(final long addr, final int size) {
		return new CompositeFunction(this.getFunctionAt(addr), this.getFunctionAt(addr + 4, size - 4));
	}

	/** Diese Methode fügt den gegebenen Funktionsaufruf in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktionsaufruf.
	 * @return Referenz auf den Funktionsaufruf.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int __putCompositeFunction(final CompositeFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int invokeRef = this.putFunction(src.function);
		final int[] paramRefs = this.putAll(src.params);
		final long addr = this.store.openRegion(FEMCodec.TYPE_COMPOSITE, (paramRefs.length * 4) + 4);
		this.store.putInt(addr, invokeRef);
		this.store.putInt(addr + 4, paramRefs);
		return this.store.closeRegion();
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + this.store.emu();
	}

	/** Diese Methode leert den Cache der {@link #getFunction(int) gelesenen} Funktionen. */
	public void cleanup() {
		// TODO
	}

}
