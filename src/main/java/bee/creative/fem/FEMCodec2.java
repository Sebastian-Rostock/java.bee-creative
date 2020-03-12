package bee.creative.fem;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
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
import bee.creative.lang.Objects;
import bee.creative.util.HashMapLO;
import bee.creative.util.HashMapOL;
import bee.creative.util.HashSet;

// ref: LONG
//
// immediate vs. pointer für integer, object, datetime, duration, param, void, true, false
// p

/** Diese Klasse implementiert ein Objekt zur Kodierung und Dekodierung von {@link FEMFunction Funktionen} in {@link IAMArray Zahlenlisten}, die in einen
 * {@link MappedBuffer2 Dateipuffer} ausgelagert sind.
 * <p>
 * Achtung: {@link FEMFuture} und {@link FEMNative} werden bei der Kodierun zwar angeboten aber in dieser Implementation nicht unterstützt.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMCodec2 implements Property<FEMFunction>, Emuable {

	protected static class ReuseMap extends HashMapOL<FEMFunction> {

		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object key) {
			// TODO indizierte arrys besonders
			return super.customEqualsKey(entryIndex, key);
		}

		@Override
		protected boolean customEqualsKey(final int entryIndex, final Object key, final int keyHash) {
			return this.customEqualsKey(entryIndex, key);
		}

	}

	protected static class IdentSet extends HashSet<FEMFunction> {

		public FEMFunction getByIdent(final long ident) throws IllegalArgumentException {
			try {
				return Objects.notNull(this.customGetKey((int)(ident >> 3)));
			} catch (final Exception cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		public long putByIdent(final FEMFunction item) throws NullPointerException {
			final int result = this.getIndexImpl(Objects.notNull(item));
			if (result < 0) return 0;
			return (result << 3) | FEMCodec2.REF_IDENT;
		}

		public IdentSet putAll(final FEMFunction... items) throws NullPointerException {
			return this.putAll(Arrays.asList(items));
		}

		public IdentSet putAll(final Collection<? extends FEMFunction> items) throws NullPointerException {
			if (items.contains(null)) throw new NullPointerException();
			this.addAll(items);
			return this;
		}

	}

	/** Diese Klasse implementiert eine Wertliste, deren Elemente als Referenzen gegeben sind und in {@link #customGet(int)} über einen gegebenen
	 * {@link FEMCodec2} in Werte {@link FEMCodec2#get_DONE(long) übersetzt} werden. */
	public static class MappedArrayA extends FEMArray {

		final MappedBuffer2 store;

		/** Dieses Feld speichert den {@link FEMCodec2} zur {@link FEMCodec2#get_DONE(long) Übersetzung} der Referenzen. */
		final FEMCodec2 codec;

		/** Dieses Feld speichert die Adresse der Zahlenfolge mit den Referenzen. */
		final long addr;

		MappedArrayA(final FEMCodec2 codec, final long addr, final int length, final int hash) throws IllegalArgumentException {
			super(length);
			this.store = codec.getStore();
			this.codec = codec;
			this.addr = addr;
			this.hash = hash;
		}

		@Override
		protected FEMValue customGet(final int index) {
			/** this.addr: value[length] */
			return this.codec.get_DONE(this.store.getLong(this.addr + (index * 8L)), FEMValue.class);
		}

	}

	/** Diese Klasse implementiert eine indizierte Wertliste mit beschleunigter {@link #find(FEMValue, int) Einzelwertsuche}. */
	public static class MappedArrayB extends MappedArrayA {

		MappedArrayB(final FEMCodec2 codec, final long addr, final int length, final int hash) throws IllegalArgumentException {
			super(codec, addr, length, hash);
		}

		@Override
		protected int customFind(final FEMValue that, final int offset, int length, final boolean foreward) {
			/** this.addr: value: long[length], count: int, range: int[count-1], index: int[length] */
			final long addr = this.addr + (this.length * 8L);
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

	/** Diese Klasse implementiert eine Bytefolge als Sicht auf eine Speicherbereich eines {@link MappedBuffer2}. */
	public static class MappedBinary extends FEMBinary {

		final MappedBuffer2 store;

		final long addr;

		MappedBinary(final MappedBuffer2 store, final long addr, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
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

	/** Diese Klasse implementiert eine {@code byte}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer2}. */
	public static class MappedStringA extends FEMString {

		final MappedBuffer2 store;

		final long addr;

		MappedStringA(final MappedBuffer2 store, final long addr, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
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

	/** Diese Klasse implementiert eine {@code short}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer2}. */
	public static class MappedStringB extends MappedStringA {

		MappedStringB(final MappedBuffer2 store, final long addr, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
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

	/** Diese Klasse implementiert eine {@code int}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer2}. */
	public static class MappedStringC extends MappedStringA {

		MappedStringC(final MappedBuffer2 store, final long addr, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
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

	/** Dieses Feld speichert die Typkennung für {@link #getAsAddr(long)}. */
	protected static final byte REF_ADDR = 0;

	/** Dieses Feld speichert die Typkennung für {@link FEMInteger}. */
	protected static final byte REF_IDENT = 7;

	/** Dieses Feld speichert die Typkennung für {@link FEMObject}. */
	protected static final byte REF_OBJECT = 1;

	/** Dieses Feld speichert die Typkennung für {@link FEMInteger}. */
	protected static final byte REF_INTEGER = 2;

	/** Dieses Feld speichert die Typkennung für {@link FEMDecimal}. */
	protected static final byte REF_DECIMAL = 3;

	/** Dieses Feld speichert die Typkennung für {@link FEMHandler}. */
	protected static final byte REF_HANDLER = 4;

	/** Dieses Feld speichert die Typkennung für {@link FEMDatetime}. */
	protected static final byte REF_DATETIME = 5;

	/** Dieses Feld speichert die Typkennung für {@link FEMDuration}. */
	protected static final byte REF_DURATION = 6;

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

	private static final IdentSet IDENT_CACHE =
		new IdentSet().putAll(FEMVoid.INSTANCE, FEMBoolean.TRUE, FEMBoolean.FALSE, FEMArray.EMPTY, FEMString.EMPTY, FEMBinary.EMPTY).putAll(FEMParam.CACHE);

	/** Dieses Feld speichert den Puffer, in dem die Yahlenfolgen abgelegt sind. */
	private final MappedBuffer2 store;

	/** Dieses Feld speichert {@code true}, wenn Funktionen bei {@link #get_DONE(int)} wiederverwendet werden sollen. */
	private boolean cacheEnabled = true;

	/** Dieses Feld bildet von einer Referenz auf deren Funktion ab und wird zusammen mit {@link #cacheEnabled} in {@link #get_DONE(long)} eingesetzt. */
	private final HashMapLO<FEMFunction> cacheMapping = new HashMapLO<>();

	private final boolean reuseEnabled = true;

	long rootAddr;

	long pageLast;

	long pageNext;

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei.
	 *
	 * @see MappedBuffer2#MappedBuffer2(File, boolean)
	 * @param file Datei.
	 * @param readonly {@code true}, wenn die Datei nur mit Lesezugriff angebunden werden soll.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public FEMCodec2(final File file, final boolean readonly) throws IOException {
		this.store = new MappedBuffer2(file, readonly);
		long root = this.store.getRoot();
		if (root == 0) {
			root = this.store.insertRegion(32);
			// TODO magic, value
			this.store.putLong(root, new long[]{0, 0, 0, 0});
		}
		this.rootAddr = root;
		this.pageNext = this.store.getLong(root + 16);
		this.pageLast = this.store.getLong(root + 24);

	}

	/** Diese Methode gibt den angebundenen {@link MappedBuffer2 Datenspeicher} zurück, in welchem die kodierten {@link FEMFunction Funktionen} abgelegt sind.
	 *
	 * @return Datenspeicher. */
	public MappedBuffer2 getStore() {
		return this.store;
	}

	/** Diese Methode gibt die {@link MappedBuffer2#isReusing() Aktivierung der Wiederverwendung von Speicherbereichen des Puffers} zurück.
	 *
	 * @return Aktivierung der Wiederverwendung. */
	public boolean isReusing() {
		return this.store.isReusing();
	}

	/** Diese Methode setzt die {@link MappedBuffer2#isReusing() Aktivierung der Wiederverwendung von Speicherbereichen des Puffers} und gibt {@code this} zurück.
	 *
	 * @param enabled Aktivierung der Wiederverwendung.
	 * @return {@code this}. */
	public FEMCodec2 useReusing(final boolean enabled) {
		this.store.setReusing(enabled);
		return this;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die über {@link #get_DONE(int)} gelieferten Funktionen zur Wiederverwendng Zwischengespeichert
	 * werden sollen. Dazu werden je Funktion ca. 16 Byte Verwaltungsdaten benötigt.
	 *
	 * @return Aktivierung der Wiederverwendung. */
	public boolean isCaching() {
		return this.cacheEnabled;
	}

	/** Diese Methode setzt die {@link #isCaching() Aktivierung der Zwischenspeicherung} von Speicherbereichen.
	 *
	 * @param value Aktivierung der Zwischenspeicherung. */
	public FEMCodec2 useCaching(final boolean value) {
		this.cacheEnabled = value;
		return this;
	}

	@Override
	public FEMFunction get() {
		return this.get_DONE(this.store.getLong(this.rootAddr + 8));
	}

	@Override
	public void set(final FEMFunction value) {
		this.store.putLong(this.rootAddr + 8, this.put(value));
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz zurück. Sofern {@link #useCaching(boolean) aktiviert}, wird das Ergebnis zur Wiederverwendung
	 * zwichengespeichert.
	 *
	 * @param ref Referenz.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMFunction get_DONE(final long ref) throws IllegalArgumentException {
		if (!this.cacheEnabled) return this.getAsRef_DONE(ref);
		final Long key = ref;
		FEMFunction result = this.cacheMapping.get(key);
		if (result != null) return result;
		result = this.getAsRef_DONE(ref);
		this.cacheMapping.put(key, result);
		return result;
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz als Instanz der gegebenen Klasse zurück und ist eine Abkürzung für {@link Class#cast(Object)
	 * clazz.cast(this.get(ref))}.
	 *
	 * @param ref Referenz.
	 * @param clazz Klasse der gelieferten Funktion.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public <GResult> GResult get_DONE(final long ref, final Class<GResult> clazz) throws IllegalArgumentException {
		try {
			return clazz.cast(this.get_DONE(ref));
		} catch (final ClassCastException cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die Funktion zu der Referenz zurück, die an der gegebenen Adresse steht, und ist damit eine Abkürzung für {@link #get_DONE(long)
	 * this.get(this.store.getLong(addr))}.
	 *
	 * @param addr Adresse der Referenz auf die Funktion.
	 * @return Funktion. */
	protected FEMFunction getAt_DONE(final long addr) {
		return this.get_DONE(this.store.getLong(addr));
	}

	/** Diese Methode gibt die Funktionen zu den Referenzen zurück, die im gegebenen Speicherbereich stehen.
	 *
	 * @see #getAt_DONE(long)
	 * @param addr Adresse des Speicherbereichs.
	 * @param count Anzahl der Referenzen.
	 * @return Funktionen. */
	protected FEMFunction[] getAllAt_DONE(final long addr, final int count) {
		final FEMFunction[] result = new FEMFunction[count];
		for (int i = 0; i < count; i++) {
			result[i] = this.getAt_DONE(addr + (i * 8));
		}
		return result;
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz zurück. Abhängig von der Typkennung der Referenz kann diese Referenz auch als Adresse an
	 * {@link #getAsAddr(long)} bzw. als Identifikator an {@link #getAsIdent(long)} weitergelietet werden.
	 *
	 * @param ref Referenz.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	protected FEMFunction getAsRef_DONE(final long ref) throws IllegalArgumentException {
		switch ((int)ref & 7) {
			default:
			case FEMCodec2.REF_ADDR:
				return this.getAsAddr(ref);
			case FEMCodec2.REF_INTEGER:
				return this.DONE_getIntegerValue(ref);
			case FEMCodec2.REF_DECIMAL:
				return this.DONE_getDecimalValue(ref);
			case FEMCodec2.REF_DURATION:
				return this.DONE_getDurationValue(ref);
			case FEMCodec2.REF_DATETIME:
				return this.DONE_getDatetimeValue(ref);
			case FEMCodec2.REF_HANDLER:
				return this.DONE_getHandlerValue(ref);
			case FEMCodec2.REF_OBJECT:
				return this.DONE_getObjectValue(ref);
			case FEMCodec2.REF_IDENT:
				return this.getAsIdent(ref);
		}
	}

	/** Diese Methode gibt die Funktion zur gegebenen Adresse zurück.
	 *
	 * @param addr Adresse.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Adresse ungültig ist. */
	protected FEMFunction getAsAddr(final long addr) throws IllegalArgumentException {
		final int type = this.store.getInt(addr);
		switch (type) {
			case TYPE_ARRAY_A:
				return this.getArrayValueA_DONE(addr);
			case TYPE_ARRAY_B:
				return this.getArrayValueB_DONE(addr);
			case TYPE_STRING_A:
				return this.getStringValueA_DONE(addr);
			case TYPE_STRING_B:
				return this.getStringValueB_DONE(addr, size);
			case TYPE_STRING_C:
				return this.getStringValueC_DONE(addr, size);
			case TYPE_BINARY:
				return this.getBinaryValue(addr, size);
			case TYPE_FUTURE:
				return this.getFutureValue(addr, size);
			case TYPE_NATIVE:
				return this.getNativeValue(addr, size);
			case TYPE_PROXY:
				return this.getProxyFunction(addr, size);
			case TYPE_PARAM:
				return this.getParamFunction(addr, size);
			case TYPE_CONCAT:
				return this.getConcatFunction(addr, size);
			case TYPE_CLOSURE:
				return this.getClosureFunction(addr, size);
			case TYPE_COMPOSITE:
				return this.getCompositeFunction(addr, size);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Funktion zum gegebenen Identifikator zurück.
	 *
	 * @see IdentSet#getByIdent(long)
	 * @param ident Identifikator.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn der Identifikator ungültig ist. */
	protected FEMFunction getAsIdent(final long ident) throws IllegalArgumentException {
		return FEMCodec2.IDENT_CACHE.getByIdent(ident);
	}

	/** Diese Methode fügt die gegebene Funktion in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.<br>
	 * Nachfahren sollten diese Methode zur weiteren Fallunterscheidungen überschreiben.
	 *
	 * @param src Funktion.
	 * @return Referenz auf die Funktion.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion nicht angefügt werden kann. */
	public long put(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (this.reuseEnabled) return this.putFunction(src);

	}

	protected long putFunctionAsIdent(final FEMFunction function) {
		final int result = FEMCodec2.IDENT_CACHE.putByIdent(function);
		if (result < 0) return 0;
		return (result << 3) | FEMCodec2.REF_IDENT;
	}

	protected int putFunction(final FEMFunction src) {
		if (src instanceof FEMVoid) return this.putVoidValue();
		if (src instanceof FEMArray) return this.putArrayValue((FEMArray)src);
		if (src instanceof FEMHandler) return this.putHandlerValue((FEMHandler)src);
		if (src instanceof FEMBoolean) return this.putBooleanValue((FEMBoolean)src);
		if (src instanceof FEMString) return this.putStringValue((FEMString)src);
		if (src instanceof FEMBinary) return this.putBinaryValue((FEMBinary)src);
		if (src instanceof FEMInteger) return this.putIntegerValue((FEMInteger)src);
		if (src instanceof FEMDecimal) return this.putDecimalValue((FEMDecimal)src);
		if (src instanceof FEMDuration) return this.putDurationValue((FEMDuration)src);
		if (src instanceof FEMDatetime) return this.putDatetimeValue((FEMDatetime)src);
		if (src instanceof FEMObject) return this.putObjectValue((FEMObject)src);
		if (src instanceof FEMFuture) return this.putFutureValue((FEMFuture)src);
		if (src instanceof FEMNative) return this.putNativeValue((FEMNative)src);
		if (src instanceof FEMProxy) return this.putProxyFunction((FEMProxy)src);
		if (src instanceof FEMParam) return this.putParamFunction((FEMParam)src);
		if (src instanceof ConcatFunction) return this.putConcatFunction((ConcatFunction)src);
		if (src instanceof ClosureFunction) return this.putClosureFunction((ClosureFunction)src);
		if (src instanceof CompositeFunction) return this.putCompositeFunction((CompositeFunction)src);
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Adresse des nächsten zurück das zurück.
	 *
	 * @return
	 * @throws IOException */
	protected synchronized long TODO_putLongByRef() throws IOException {
		long result = this.pageNext;
		if (result >= this.pageLast) {
			final int size = 1 << 13;
			result = this.store.insertRegion(size);
			this.pageLast = result + size;
		}
		this.pageNext = result + 8;
		return result;
	}

	protected void setupPage() throws IOException {
		final int size = 1 << 13;
		this.pageLast = this.store.insertRegion(size);
		this.pageNext = this.pageLast + size;
	}

	/** Diese Methode {@link #put(FEMFunction) überführt} die gegebenen Funktionen in deren Referenzen und gibt die Liste dieser Referenzen zurück.
	 *
	 * @param src Funktionen.
	 * @return Referenzen auf die Funktionen.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn mindestens eine der Funktionen nicht angefügt werden kann. */
	public int[] putAll(final FEMFunction... src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.length;
		final int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.put(src[i]);
		}
		return result;
	}

	/** Diese Methode fügt {@link FEMVoid#INSTANCE} in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @return Referenz auf {@link FEMVoid#INSTANCE}.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putVoidValue() throws IllegalStateException {
		this.store.openRegion(FEMCodec2.TYPE_VOID, 0);
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die Wertliste zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Wertliste.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMArray getArrayValue(final int ref) throws IllegalArgumentException {
		return this.get_DONE(ref, FEMArray.class);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Wertliste zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, ignore: int, value: long[length])}. Sie beginnt mit dem {@link FEMArray#hashCode() Streuwert} und endet mit den
	 * über {@link #putAll(FEMFunction...) Referenzen} der Elemente der Wertliste. */
	protected FEMArray getArrayValueA_DONE(final long addr) {
		return new MappedArrayA(this, addr + 16, this.store.getInt(addr + 8), this.store.getInt(addr + 4));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Wertliste zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, ignore: int, value: long[length], count: int, range: int[count-1], index: int[length])}. Sie beginnt mit dem
	 * {@link FEMArray#hashCode() Streuwert} und {@link FEMArray#length() Länge} der Wertliste, gefolgt von den {@link #putAll(FEMFunction...) Referenzen} der
	 * Elemente der Wertliste sowie der {@link CompactArray3#table Streuwerttabelle} zur beschleunigten Einzelwertsuche. */
	protected FEMArray getArrayValueB_DONE(final long addr) {
		return new MappedArrayB(this, addr + 16, this.store.getInt(addr + 8), this.store.getInt(addr + 4));
	}

	/** Diese Methode fügt die gegebene Wertliste in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück. Eine über
	 * {@link FEMArray#compact(boolean)} indizierte Wertliste wird mit der Indizierung kodiert.
	 *
	 * @param src Wertliste.
	 * @return Referenz auf die Wertliste.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putArrayValue(final FEMArray src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int[] values = this.putAll(src.value());
		if (src instanceof CompactArray3) {
			// addr: hash[1], length[1], value[length], count[1], range[count], index[length]
			final int[] table = ((CompactArray3)src).table;
			final int length = src.length(), count = table[0];
			final long addr = this.store.openRegion(FEMCodec2.TYPE_ARRAY_B, (length * 8) + (count * 4) + 12);
			this.store.putInt(addr, src.hashCode());
			this.store.putInt(addr + 4, length);
			this.store.putInt(addr + 8, values);
			this.store.putInt(addr + (length * 4) + 8, table);
			return this.store.closeRegion();
		} else {
			// hash[1], value[length]
			final long addr = this.store.openRegion(FEMCodec2.TYPE_ARRAY_A, (src.length() * 4) + 4);
			this.store.putInt(addr, src.hashCode());
			this.store.putInt(addr + 4, values);
			return this.store.closeRegion();
		}
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code byte}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, value: byte[length])}. */
	protected FEMString getStringValueA_DONE(final long addr) {
		return new MappedStringA(this.store, addr + 12, this.store.getInt(addr + 4), this.store.getInt(addr + 8));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code short}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, value: short[length])}. */
	protected FEMString getStringValueB_DONE(final long addr) {
		return new MappedStringB(this.store, addr + 12, this.store.getInt(addr + 4), this.store.getInt(addr + 8));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code int}-Zeichenkette zurück.Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, value: int[length])}. */
	protected FEMString getStringValueC_DONE(final long addr, final int size) {
		return new MappedStringC(this.store, addr + 12, this.store.getInt(addr + 4), this.store.getInt(addr + 8));
	}

	/** Diese Methode fügt die gegebene Zeichenkette in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück. Dazu werden die
	 * {@link FEMString#compact() kompaktierten} Formen der Zeichenkette analysiert und entsprechend als {@code byte}, {@code short} oder {@code int}-Codepoints
	 * gespeichert.
	 *
	 * @param src Zeichenkette.
	 * @return Referenz auf die Zeichenkette.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putStringValue(final FEMString src) throws NullPointerException, IllegalStateException {
		final FEMString str = src.compact();
		if (str instanceof FEMString.CompactStringINT8) {

			final long addr = this.putRegion(FEMCodec2.TYPE_STRING_A, str.length() + 12);
			this.store.putInt(addr, str.hashCode());
			this.store.put(addr, str.toBytes());
			return this.store.closeRegion();
		} else if (str instanceof FEMString.CompactStringINT16) {
			final long addr = this.store.openRegion(FEMCodec2.TYPE_STRING_B, (str.length() * 2) + 2);
			this.store.putInt(addr, str.hashCode());
			this.store.putShort(addr, str.toShorts());
			return this.store.closeRegion();
		} else {
			final long addr = this.store.openRegion(FEMCodec2.TYPE_STRING_C, (str.length() * 4) + 1);
			this.store.putInt(addr, str.hashCode());
			this.store.putInt(addr, str.toInts());
			return this.store.closeRegion();
		}
	}

	private long putRegion(int type, long size) {
		long result = store.insertRegion(size);
		store.putInt(result, type);
		return result;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Bytefolge zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs.
	 * @return Bytefolge. */
	protected FEMBinary getBinaryValue(final long addr, final int size) {
		return new MappedBinary(this.store, addr + 4, size - 4, this.store.getInt(addr));
	}

	/** Diese Methode fügt die gegebene Bytefolge in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Bytefolge.
	 * @return Referenz auf die Bytefolge.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putBinaryValue(final FEMBinary src) throws NullPointerException, IllegalStateException {
		final long addr = this.store.openRegion(FEMCodec2.TYPE_BINARY, src.length() + 4);
		this.store.putInt(addr, src.hashCode());
		this.store.put(addr, src.value());
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Dezimalzahl zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @return Dezimalzahl. */
	protected FEMInteger DONE_getIntegerValue(final long addr) {
		return new FEMInteger(this.store.getLong(addr));
	}

	/** Diese Methode fügt die gegebene Dezimalzahl in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Dezimalzahl.
	 * @return Referenz auf die Dezimalzahl.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public long putIntegerValue(final FEMInteger src) throws NullPointerException, IllegalStateException {
		final long value = src.value();
		final long addr = this.TODO_putLongByRef();
		this.store.putLong(addr, src.value());
		return addr | FEMCodec2.REF_INTEGER;
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltenen Dezimalbruch zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @return Dezimalbruch. */
	protected FEMDecimal DONE_getDecimalValue(final long addr) {
		Double.
		return new FEMDecimal(this.store.getDouble(addr));
	}

	/** Diese Methode fügt den gegebenen Dezimalbruch in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Dezimalbruch.
	 * @return Referenz auf den Dezimalbruch.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putDecimalValue(final FEMDecimal src) throws NullPointerException, IllegalStateException {
		this.store.putDouble(this.store.openRegion(FEMCodec2.TYPE_DECIMAL, 8), src.value());
		return this.putlstore.closeRegion();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Zeitspanne zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @return Zeitspanne. */
	protected FEMDuration DONE_getDurationValue(final long addr) {
		return new FEMDuration(this.store.getLong(addr));
	}

	/** Diese Methode fügt den gegebenen Zeitspanne in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Zeitspanne.
	 * @return Referenz auf den Zeitspanne.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putDurationValue(final FEMDuration src) throws NullPointerException, IllegalStateException {
		this.store.putDouble(this.store.openRegion(FEMCodec2.REF_DURATION, 8), src.value());
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Zeitangabe zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @return Zeitangabe. */
	protected FEMDatetime DONE_getDatetimeValue(final long addr) {
		return new FEMDatetime(this.store.getLong(addr));
	}

	/** Diese Methode fügt die gegebene Zeitangabe in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Zeitangabe.
	 * @return Referenz auf die Zeitangabe.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putDatetimeValue(final FEMDatetime src) throws NullPointerException, IllegalStateException {
		this.store.putDouble(this.store.openRegion(FEMCodec2.REF_DATETIME, 8), src.value());
		return this.store.closeRegion();
	}

	/** Diese Methode fügt den gegebenen Wahrheitswert in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Wahrheitswert.
	 * @return Referenz auf den Wahrheitswert.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putBooleanValue(final FEMBoolean src) throws NullPointerException, IllegalStateException {
		this.store.openRegion(src.value() ? FEMCodec2.TYPE_TRUE : FEMCodec2.TYPE_FALSE, 0);
		return this.store.closeRegion();
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltenen Funktionszeiger zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @return Funktionszeiger. */
	protected FEMHandler DONE_getHandlerValue(final long addr) {
		return new FEMHandler(this.getAt_DONE(addr));
	}

	/** Diese Methode fügt den gegebenen Funktionszeiger in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktionszeiger.
	 * @return Referenz auf den Funktionszeiger.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion des Funktionszeigers nicht angefügt werden kann. */
	public int putHandlerValue(final FEMHandler src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int invokeRef = this.put(src.value());
		this.store.putInt(this.store.openRegion(FEMCodec2.REF_HANDLER, 4), invokeRef);
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Objektreferenz zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @return Objektreferenz. */
	protected FEMObject DONE_getObjectValue(final long addr) {
		return new FEMObject(this.store.getLong(addr));
	}

	/** Diese Methode fügt den gegebenen Objektreferenz in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Objektreferenz.
	 * @return Referenz auf den Objektreferenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putObjectValue(final FEMObject src) throws NullPointerException, IllegalStateException {
		this.store.putLong(this.store.openRegion(FEMCodec2.TYPE_OBJECT, 8), src.value());
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltenen Ergebniswert zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs.
	 * @return Ergebniswert. */
	protected FEMFuture getFutureValue(final long addr, final int size) {
		throw new IllegalArgumentException();
	}

	/** Diese Methode fügt den gegebenen Ergebniswert in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Ergebniswert.
	 * @return Referenz auf den Ergebniswert.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion des Funktionszeigers nicht angefügt werden kann. */
	public int putFutureValue(final FEMFuture src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltenen Nativwert zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs.
	 * @return Nativwert. */
	protected FEMNative getNativeValue(final long addr, final int size) {
		throw new IllegalArgumentException();
	}

	/** Diese Methode fügt den gegebenen Nativwert in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Nativwert.
	 * @return Referenz auf den Ergebniswert.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion des Funktionszeigers nicht angefügt werden kann. */
	public int putNativeValue(final FEMNative src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltenen Platzhalter zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs.
	 * @return Platzhalter. */
	protected FEMProxy getProxyFunction(final long addr, final int size) throws NullPointerException, IllegalArgumentException {
		return new FEMProxy(this.get_DONE(this.store.getInt(addr), FEMValue.class), this.get_DONE(this.store.getInt(addr + 4), FEMString.class),
			this.getAt_DONE(addr + 8));
	}

	/** Diese Methode fügt den gegebenen Platzhalter in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Platzhalter.
	 * @return Referenz auf den Platzhalter.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putProxyFunction(final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		final int idRef = this.put(src.id()), nameRef = this.putStringValue(src.name()), invokeRef = this.put(src.get());
		final long addr = this.store.openRegion(FEMCodec2.TYPE_PROXY, 12);
		this.store.putInt(addr, idRef);
		this.store.putInt(addr + 4, nameRef);
		this.store.putInt(addr + 8, invokeRef);
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Parameterfunktion zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs.
	 * @return Parameterfunktion. */
	protected FEMParam getParamFunction(final long addr, final int size) {
		return FEMParam.from(this.store.getInt(addr));
	}

	/** Diese Methode fügt die gegebene Parameterfunktion in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Parameterfunktion.
	 * @return Referenz auf die Parameterfunktion.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putParamFunction(final FEMParam src) throws NullPointerException, IllegalStateException {
		this.store.putInt(this.store.openRegion(FEMCodec2.TYPE_PARAM, 4), src.index());
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Funktionkette zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs.
	 * @return Funktionkette. */
	protected ConcatFunction getConcatFunction(final long addr, final int size) {
		return new ConcatFunction(this.getAt_DONE(addr), this.getAllAt(addr + 4, size - 4));
	}

	/** Diese Methode fügt die gegebene Funktionkette in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktionkette.
	 * @return Referenz auf die Funktionkette.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putConcatFunction(final ConcatFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int invokeRef = this.put(src.function);
		final int[] paramRefs = this.putAll(src.params);
		final long addr = this.store.openRegion(FEMCodec2.TYPE_CONCAT, (paramRefs.length * 4) + 4);
		this.store.putInt(addr, invokeRef);
		this.store.putInt(addr + 4, paramRefs);
		return this.store.closeRegion();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Funktionsbindung zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs.
	 * @return Funktionsbindung. */
	protected ClosureFunction getClosureFunction(final long addr, final int size) {
		return new ClosureFunction(this.getAt_DONE(addr));
	}

	/** Diese Methode fügt die gegebene Funktionsbindung in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktionsbindung.
	 * @return Referenz auf die Funktionsbindung.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion des Funktionszeigers nicht angefügt werden kann. */
	public int putClosureFunction(final ClosureFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int invokeRef = this.put(src.function);
		this.store.putInt(this.store.openRegion(FEMCodec2.TYPE_CLOSURE, 4), invokeRef);
		return this.store.closeRegion();
	}

	/** Diese Methode gibt dden im gegebenen Speicherbereich enthaltenen Funktionsaufruf zurück.
	 *
	 * @param addr Adresse des Speicherbereichs.
	 * @param size Größe des Speicherbereichs.
	 * @return Funktionsaufruf. */
	protected CompositeFunction getCompositeFunction(final long addr, final int size) {
		return new CompositeFunction(this.getAt_DONE(addr), this.getAllAt(addr + 4, size - 4));
	}

	/** Diese Methode fügt den gegebenen Funktionsaufruf in den {@link #getStore() Puffer} ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktionsaufruf.
	 * @return Referenz auf den Funktionsaufruf.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	public int putCompositeFunction(final CompositeFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int invokeRef = this.put(src.function);
		final int[] paramRefs = this.putAll(src.params);
		final long addr = this.store.openRegion(FEMCodec2.TYPE_COMPOSITE, (paramRefs.length * 4) + 4);
		this.store.putInt(addr, invokeRef);
		this.store.putInt(addr + 4, paramRefs);
		return this.store.closeRegion();
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + this.store.emu() + this.cacheMapping.emu();
	}

	/** Diese Methode leert den Cache der {@link #get_DONE(int) gelesenen} Funktionen.
	 *
	 * @see #useCaching(boolean) */
	public void cleanup() {
		this.cacheMapping.clear();
	}

}
