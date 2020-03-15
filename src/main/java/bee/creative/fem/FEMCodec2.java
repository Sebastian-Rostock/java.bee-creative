package bee.creative.fem;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
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
import bee.creative.io.MappedBuffer2;
import bee.creative.lang.Objects;
import bee.creative.util.HashMapLO;
import bee.creative.util.HashMapOL;
import bee.creative.util.HashSet;

/** Diese Klasse implementiert ein Objekt zur Auslagerung von {@link FEMFunction Funktionen} in einen {@link MappedBuffer2 Dateipuffer}.
 * <p>
 * Achtung: {@link FEMFuture} und {@link FEMNative} werden bei der Kodierun zwar angeboten aber in dieser Implementation nicht unterstützt.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMCodec2 implements Property<FEMFunction>, Emuable {

	/** Diese Klasse implementiert eine Wertliste, deren Elemente als Referenzen gegeben sind und in {@link #customGet(int)} über einen gegebenen
	 * {@link FEMCodec2} in Werte {@link FEMCodec2#get_DONE(long) übersetzt} werden. */
	public static class MappedArrayA extends FEMArray {

		final MappedBuffer2 store;

		final FEMCodec2 codec;

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

	protected static class CacheMap extends HashMapLO<FEMFunction> {

	}

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

	/** Dieses Feld speichert die Typkennung für {@link #getByAddr_DONE(long)}. */
	protected static final byte REF_ADDR = 0;

	/** Dieses Feld speichert die Typkennung für {@link #getByIdent_DONE(long)}. */
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

	private static final IdentSet FUNCTION_BY_IDENT =
		new IdentSet().putAll(FEMVoid.INSTANCE, FEMBoolean.TRUE, FEMBoolean.FALSE, FEMArray.EMPTY, FEMString.EMPTY, FEMBinary.EMPTY).putAll(FEMParam.CACHE);

	/** Dieses Feld speichert den Puffer, in dem die Yahlenfolgen abgelegt sind. */
	protected final MappedBuffer2 store;

	/** Dieses Feld speichert {@code true}, wenn Funktionen bei {@link #get_DONE(long)} wiederverwendet werden sollen. */
	protected boolean cacheEnabled = true;

	/** Dieses Feld bildet von einer Referenz auf eine Funktion ab und wird zusammen mit {@link #cacheEnabled} in {@link #get_DONE(long)} eingesetzt. */
	protected final Map<Long, FEMFunction> cacheMapping = Collections.synchronizedMap(new CacheMap());

	protected boolean reuseEnabled = true;

	/** Dieses Feld bildet von einer Funktion auf eine Referenz ab und wird zusammen mit {@link #reuseEnabled} in {@link #put_DONE(FEMFunction)} eingesetzt. */
	protected final Map<FEMFunction, Long> reuseMapping = Collections.synchronizedMap(new ReuseMap());

	private long pageLast;

	private long pageNext;

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei.
	 *
	 * @see MappedBuffer2#MappedBuffer2(File, boolean)
	 * @param file Datei.
	 * @param readonly {@code true}, wenn die Datei nur mit Lesezugriff angebunden werden soll.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public FEMCodec2(final File file, final boolean readonly) throws IOException {
		this.store = new MappedBuffer2(file, readonly);
	}

	@Override
	public FEMFunction get() {
		final long addr = this.store.getRoot();
		return addr != 0 ? this.get_DONE(addr) : null;
	}

	@Override
	public void set(final FEMFunction value) {
		this.store.putRoot(value != null ? this.put_DONE(value) : 0);
	}

	/** Diese Methode gibt den {@link MappedBuffer2 Dateipuffer} zurück, in welchen die Funktionen abgelegt sind.
	 *
	 * @return Dateipuffer. */
	public MappedBuffer2 getStore() {
		return this.store;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn über {@link #put_DONE(FEMFunction)} keine Duplikate angefügt, sonder die Referenzen der angefügten
	 * Funktionen wiederverwendet werden sollen. Dazu werden je Funktion ca. 16 Byte Verwaltungsdaten benötigt.
	 *
	 * @return Aktivierung der Wiederverwendung. */
	public boolean isReuseEnabled() {
		return this.reuseEnabled;
	}

	/** Diese Methode setzt die {@link #isCacheEnabled() Aktivierung der Wiederverwendung} von Referenzen und gibt {@code this} zurück.
	 *
	 * @param value Aktivierung der Wiederverwendung.
	 * @return {@code this}. */
	public FEMCodec2 useReuseEnabled(final boolean value) {
		this.reuseEnabled = value;
		return this;
	}

	/** Diese Methode gibt den in {@link #put_DONE(FEMFunction)} verwendeten Zwischenspeicher der Referenzen zurück.
	 *
	 * @return Zwischenspeicher der Referenen. */
	public Map<FEMFunction, Long> getReuseMapping() {
		return this.reuseMapping;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die über {@link #get_DONE(long)} gelieferten Funktionen zur Wiederverwendng zwischengespeichert
	 * werden sollen. Dazu werden je Funktion ca. 16 Byte Verwaltungsdaten benötigt.
	 *
	 * @return Aktivierung der Zwischenspeicherung. */
	public boolean isCacheEnabled() {
		return this.cacheEnabled;
	}

	/** Diese Methode setzt die {@link #isCacheEnabled() Aktivierung der Zwischenspeicherung} von Funktionen und gibt {@code this} zurück.
	 *
	 * @param value Aktivierung der Zwischenspeicherung.
	 * @return {@code this}. */
	public FEMCodec2 useCacheEnabled(final boolean value) {
		this.cacheEnabled = value;
		return this;
	}

	/** Diese Methode gibt den in {@link #get_DONE(long)} verwendeten Zwischenspeicher der Funktionen zurück.
	 *
	 * @return Zwischenspeicher der Funktionen. */
	public Map<Long, FEMFunction> getCacheMapping() {
		return this.cacheMapping;
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz zurück. Sofern {@link #useCacheEnabled(boolean) aktiviert}, wird das Ergebnis zur Wiederverwendung
	 * zwichengespeichert.
	 *
	 * @param ref Referenz.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMFunction get_DONE(final long ref) throws IllegalArgumentException {
		if (!this.cacheEnabled) return this.getByRef_DONE(ref);
		final Long key = ref;
		FEMFunction result = this.cacheMapping.get(key);
		if (result != null) return result;
		result = this.getByRef_DONE(ref);
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
		} catch (final IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die Funktion zu der Referenz zurück, die an der gegebenen Adresse steht, und ist damit eine Abkürzung für {@link #get_DONE(long)
	 * this.get(this.getStore().getLong(addr))}. */
	protected final FEMFunction getAt_DONE(final long addr) throws IllegalArgumentException {
		return this.get_DONE(this.store.getLong(addr));
	}

	/** Diese Methode gibt die Funktionen zur gegebene Anzahl an Referenzen im gegebenen Speicherbereich zurück.
	 *
	 * @see #getAt_DONE(long)
	 * @param addr Adresse des Speicherbereichs.
	 * @param count Anzahl der Referenzen.
	 * @return Funktionen. */
	protected final FEMFunction[] getAllAt_DONE(final long addr, final int count) throws IllegalArgumentException {
		final FEMFunction[] result = new FEMFunction[count];
		for (int i = 0; i < count; i++) {
			result[i] = this.getAt_DONE(addr + (i * 8));
		}
		return result;
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz zurück. Abhängig von der Typkennung der Referenz kann diese Referenz auch als Adresse an
	 * {@link #getByAddr_DONE(long)} bzw. als Identifikator an {@link #getByIdent_DONE(long)} weitergelietet werden.
	 *
	 * @param ref Referenz.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	protected FEMFunction getByRef_DONE(final long ref) throws IllegalArgumentException {
		switch ((int)ref & 7) {
			default:
			case FEMCodec2.REF_ADDR:
				return this.getByAddr_DONE(ref);
			case FEMCodec2.REF_INTEGER:
				return this.getIntegerValue_DONE(ref & -8L);
			case FEMCodec2.REF_DECIMAL:
				return this.getDecimalValue_DONE(ref & -8L);
			case FEMCodec2.REF_DURATION:
				return this.getDurationValue_DONE(ref & -8L);
			case FEMCodec2.REF_DATETIME:
				return this.getDatetimeValue_DONE(ref & -8L);
			case FEMCodec2.REF_HANDLER:
				return this.getHandlerValue_DONE(ref & -8L);
			case FEMCodec2.REF_OBJECT:
				return this.getObjectValue_DONE(ref & -8L);
			case FEMCodec2.REF_IDENT:
				return this.getByIdent_DONE(ref);
		}
	}

	/** Diese Methode gibt die Funktion zur gegebenen Adresse zurück. Dazu wird der an der gegebenen Adresse gespeicherte {@code int} als Typkennung interpretiert
	 * und in einer Fallunterscheidung eingesetzt. */
	protected FEMFunction getByAddr_DONE(final long addr) throws IllegalArgumentException {
		final int type = this.store.getInt(addr);
		switch (type) {
			case TYPE_ARRAY_A:
				return this.getArrayValueA_DONE(addr);
			case TYPE_ARRAY_B:
				return this.getArrayValueB_DONE(addr);
			case TYPE_STRING_A:
				return this.getStringValueA_DONE(addr);
			case TYPE_STRING_B:
				return this.getStringValueB_DONE(addr);
			case TYPE_STRING_C:
				return this.getStringValueC_DONE(addr);
			case TYPE_BINARY:
				return this.getBinaryValue_DONE(addr);
			case TYPE_FUTURE:
				return this.getFutureValue_DONE(addr);
			case TYPE_NATIVE:
				return this.getNativeValue_DONE(addr);
			case TYPE_PROXY:
				return this.getProxyFunction_DONE(addr);
			case TYPE_PARAM:
				return this.getParamFunction_DONE(addr);
			case TYPE_CONCAT:
				return this.getConcatFunction_DONE(addr);
			case TYPE_CLOSURE:
				return this.getClosureFunction_DONE(addr);
			case TYPE_COMPOSITE:
				return this.getCompositeFunction_DONE(addr);
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode gibt die Funktion zum gegebenen Identifikator zurück bzw. löst eine {@link IllegalArgumentException}, wenn der Identifikator ungültig
	 * ist. */
	protected FEMFunction getByIdent_DONE(final long ident) throws IllegalArgumentException {
		return FEMCodec2.FUNCTION_BY_IDENT.getByIdent(ident);
	}

	/** Diese Methode fügt die gegebene Funktion in den Puffer ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktion.
	 * @return Referenz auf die Funktion.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion nicht angefügt werden kann. */
	public long put_DONE(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (this.reuseEnabled) return this.putByRef(src);
		Long result = this.reuseMapping.get(Objects.notNull(src));
		if (result != null) return result;
		result = this.putByRef(src);
		this.reuseMapping.put(src, result);
		return result;
	}

	/** Diese Methode {@link #put_DONE(FEMFunction) überführt} die gegebenen Funktionen in deren Referenzen und gibt die Liste dieser Referenzen zurück.
	 *
	 * @param src Funktionen.
	 * @return Referenzen auf die Funktionen.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn mindestens eine der Funktionen nicht angefügt werden kann. */
	public long[] putAll_DONE(final FEMFunction... src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.length;
		final long[] result = new long[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.put_DONE(src[i]);
		}
		return result;
	}

	/** Diese Methode gibt das zurück. <br>
	 * Nachfahren sollten diese Methode zur weiteren Fallunterscheidungen überschreiben.
	 *
	 * @param src
	 * @return
	 * @throws NullPointerException
	 * @throws IllegalStateException
	 * @throws IllegalArgumentException */
	protected long putByRef(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final long result = this.putByIdent(src);
		if (result != 0) return result;
		if (src instanceof FEMInteger) return this.putIntegerValue_DONE((FEMInteger)src);
		if (src instanceof FEMDecimal) return this.putDecimalValue_DONE((FEMDecimal)src);
		if (src instanceof FEMHandler) return this.putHandlerValue_DONE((FEMHandler)src);
		if (src instanceof FEMObject) return this.putObjectValue_DONE((FEMObject)src);
		if (src instanceof FEMDuration) return this.putDurationValue_DONE((FEMDuration)src);
		if (src instanceof FEMDatetime) return this.putDatetimeValue_DONE((FEMDatetime)src);
		return this.putByAddr(src);
	}

	protected long putByAddr(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (src instanceof FEMArray) return this.putArrayValue((FEMArray)src);
		if (src instanceof FEMString) return this.putStringValue_DONE((FEMString)src);
		if (src instanceof FEMBinary) return this.putBinaryValue_DONE((FEMBinary)src);
		if (src instanceof FEMFuture) return this.putFutureValue_DONE((FEMFuture)src);
		if (src instanceof FEMNative) return this.putNativeValue_DONE((FEMNative)src);
		if (src instanceof FEMProxy) return this.putProxyFunction_DONE((FEMProxy)src);
		if (src instanceof FEMParam) return this.putParamFunction_DONE((FEMParam)src);
		if (src instanceof ConcatFunction) return this.putConcatFunction((ConcatFunction)src);
		if (src instanceof ClosureFunction) return this.putClosureFunction_DONE((ClosureFunction)src);
		if (src instanceof CompositeFunction) return this.putCompositeFunction_DONE((CompositeFunction)src);
		throw new IllegalArgumentException();
	}

	protected long putByIdent(final FEMFunction function) throws NullPointerException {
		return FEMCodec2.FUNCTION_BY_IDENT.putByIdent(function);
	}

	long putByRef_DONE(final int type, final long value) throws IllegalStateException {
		final long addr = this.TODO_putLongByRef();
		this.store.putLong(addr, value);
		return addr | type;
	}

	protected synchronized long TODO_putLongByRef() {
		long result = this.pageNext;
		if (result >= this.pageLast) {
			final int size = 1 << 13;
			result = this.store.insertRegion(size);
			this.pageLast = result + size;
		}
		this.pageNext = result + 8;
		return result;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Wertliste zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, align: int, valueRef: long[length])}. Sie beginnt mit dem {@link FEMArray#hashCode() Streuwert} und endet mit
	 * den über {@link #putAll_DONE(FEMFunction...) Referenzen} der Elemente der Wertliste. */
	protected FEMArray getArrayValueA_DONE(final long addr) throws IllegalArgumentException {
		return new MappedArrayA(this, addr + 16, this.store.getInt(addr + 8), this.store.getInt(addr + 4));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Wertliste zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, align: int, valueRef: long[length], count: int, range: int[count-1], index: int[length])}. Sie beginnt mit dem
	 * {@link FEMArray#hashCode() Streuwert} und {@link FEMArray#length() Länge} der Wertliste, gefolgt von den {@link #putAll_DONE(FEMFunction...) Referenzen}
	 * der Elemente der Wertliste sowie der {@link CompactArray3#table Streuwerttabelle} zur beschleunigten Einzelwertsuche. */
	protected FEMArray getArrayValueB_DONE(final long addr) throws IllegalArgumentException {
		return new MappedArrayB(this, addr + 16, this.store.getInt(addr + 8), this.store.getInt(addr + 4));
	}

	/** Diese Methode fügt die gegebene Wertliste in den Puffer ein und gibt die Referenz darauf zurück. Eine über {@link FEMArray#compact(boolean)} indizierte
	 * Wertliste wird mit der Indizierung kodiert.
	 *
	 * @param src Wertliste.
	 * @return Referenz auf die Wertliste.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist. */
	protected long putArrayValue(final FEMArray src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.length();
		if (src instanceof CompactArray3) {
			final int[] table = ((CompactArray3)src).table;
			final long addr = this.store.insertRegion((length * 8L) + (table.length * 4L) + 16);
			this.store.putInt(addr, new int[]{FEMCodec2.TYPE_ARRAY_B, src.hashCode(), length, 0});
			this.store.putLong(addr + 16, this.putAll_DONE(src.value()));
			this.store.putInt(addr + (length * 8L) + 16, table);
			return addr;
		} else {
			final long addr = this.store.insertRegion((length * 8L) + 16);
			this.store.putInt(addr, new int[]{FEMCodec2.TYPE_ARRAY_A, src.hashCode(), length, 0});
			this.store.putLong(addr + 16, this.putAll_DONE(src.value()));
			return addr;
		}
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code byte}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, item: byte[length])}. */
	protected FEMString getStringValueA_DONE(final long addr) throws IllegalArgumentException {
		return new MappedStringA(this.store, addr + 12, this.store.getInt(addr + 4), this.store.getInt(addr + 8));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code short}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, item: short[length])}. */
	protected FEMString getStringValueB_DONE(final long addr) throws IllegalArgumentException {
		return new MappedStringB(this.store, addr + 12, this.store.getInt(addr + 4), this.store.getInt(addr + 8));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code int}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, item: int[length])}. */
	protected FEMString getStringValueC_DONE(final long addr) throws IllegalArgumentException {
		return new MappedStringC(this.store, addr + 12, this.store.getInt(addr + 4), this.store.getInt(addr + 8));
	}

	/** Diese Methode fügt die gegebene Zeichenkette in den Puffer ein und gibt die Referenz darauf zurück. Dazu werden die {@link FEMString#compact()
	 * kompaktierten} Formen der Zeichenkette analysiert und entsprechend als {@code byte}, {@code short} oder {@code int}-Codepoints gespeichert. */
	protected long putStringValue_DONE(FEMString src) throws NullPointerException, IllegalStateException {
		src = src.compact();
		final int length = src.length();
		if (src instanceof FEMString.CompactStringINT8) {
			final long addr = this.store.insertRegion(length + 12);
			this.store.putInt(addr, new int[]{FEMCodec2.TYPE_STRING_A, src.hashCode(), length});
			this.store.put(addr + 12, src.toBytes());
			return addr;
		} else if (src instanceof FEMString.CompactStringINT16) {
			final long addr = this.store.insertRegion((length * 2L) + 12);
			this.store.putInt(addr, new int[]{FEMCodec2.TYPE_STRING_B, src.hashCode(), length});
			this.store.putShort(addr + 12, src.toShorts());
			return addr;
		} else {
			final long addr = this.store.insertRegion((length * 4L) + 12);
			this.store.putInt(addr, new int[]{FEMCodec2.TYPE_STRING_C, src.hashCode(), length});
			this.store.putInt(addr + 12, src.toInts());
			return addr;
		}
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Bytefolge zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, item: byte[length])}. */
	protected FEMBinary getBinaryValue_DONE(final long addr) throws IllegalArgumentException {
		return new MappedBinary(this.store, addr + 12, this.store.getInt(addr + 8), this.store.getInt(addr + 4));
	}

	/** Diese Methode fügt die gegebene Bytefolge in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putBinaryValue_DONE(final FEMBinary src) throws NullPointerException, IllegalStateException {
		final int length = src.length();
		final long addr = this.store.insertRegion(length + 12);
		this.store.putInt(addr, new int[]{FEMCodec2.TYPE_BINARY, src.hashCode(), length});
		this.store.put(addr + 12, src.value());
		return addr;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Dezimalzahl zurück. Die Struktur des Speicherbereichs ist {@code (value: long)}. */
	protected FEMInteger getIntegerValue_DONE(final long addr) throws IllegalArgumentException {
		return new FEMInteger(this.store.getLong(addr));
	}

	/** Diese Methode fügt die gegebene Dezimalzahl in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putIntegerValue_DONE(final FEMInteger src) throws NullPointerException, IllegalStateException {
		return this.putByRef_DONE(FEMCodec2.REF_INTEGER, src.value());
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltenen Dezimalbruch zurück. Die Struktur des Speicherbereichs ist {@code (value: double)}. */
	protected FEMDecimal getDecimalValue_DONE(final long addr) throws IllegalArgumentException {
		return new FEMDecimal(Double.longBitsToDouble(this.store.getLong(addr)));
	}

	/** Diese Methode fügt den gegebenen Dezimalbruch in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putDecimalValue_DONE(final FEMDecimal src) throws NullPointerException, IllegalStateException {
		return this.putByRef_DONE(FEMCodec2.REF_DECIMAL, Double.doubleToRawLongBits(src.value()));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Zeitspanne zurück. Die Struktur des Speicherbereichs ist {@code (value: long)}. */
	protected FEMDuration getDurationValue_DONE(final long addr) throws IllegalArgumentException {
		return new FEMDuration(this.store.getLong(addr));
	}

	/** Diese Methode fügt den gegebenen Zeitspanne in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putDurationValue_DONE(final FEMDuration src) throws NullPointerException, IllegalStateException {
		return this.putByRef_DONE(FEMCodec2.REF_DURATION, src.value());
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Zeitangabe zurück. Die Struktur des Speicherbereichs ist {@code (value: long)}. */
	protected FEMDatetime getDatetimeValue_DONE(final long addr) throws IllegalArgumentException {
		return new FEMDatetime(this.store.getLong(addr));
	}

	/** Diese Methode fügt die gegebene Zeitangabe in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putDatetimeValue_DONE(final FEMDatetime src) throws NullPointerException, IllegalStateException {
		return this.putByRef_DONE(FEMCodec2.REF_DATETIME, src.value());
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltenen Funktionszeiger zurück. Die Struktur des Speicherbereichs ist
	 * {@code (functionRef: long)}. */
	protected FEMHandler getHandlerValue_DONE(final long addr) throws IllegalArgumentException {
		return new FEMHandler(this.getAt_DONE(addr));
	}

	/** Diese Methode fügt den gegebenen Funktionszeiger in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putHandlerValue_DONE(final FEMHandler src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this.putByRef_DONE(FEMCodec2.REF_HANDLER, this.put_DONE(src.value()));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Objektreferenz zurück. Die Struktur des Speicherbereichs ist {@code (value: long)}. */
	protected FEMObject getObjectValue_DONE(final long addr) throws IllegalArgumentException {
		return new FEMObject(this.store.getLong(addr));
	}

	/** Diese Methode fügt den gegebenen Objektreferenz in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putObjectValue_DONE(final FEMObject src) throws NullPointerException, IllegalStateException {
		return this.putByRef_DONE(FEMCodec2.REF_OBJECT, src.value());
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltenen Ergebniswert zurück. */
	protected FEMFuture getFutureValue_DONE(final long addr) throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode fügt den gegebenen Ergebniswert in den Puffer ein und gibt die Referenz darauf zurück. */
	protected int putFutureValue_DONE(final FEMFuture src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltenen Nativwert zurück. */
	protected FEMNative getNativeValue_DONE(final long addr) throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode fügt den gegebenen Nativwert in den Puffer ein und gibt die Referenz darauf zurück. */
	protected int putNativeValue_DONE(final FEMNative src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltenen Platzhalter zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, align: int, idRef: long, nameRef: long, functionRef: long)}. */
	protected FEMProxy getProxyFunction_DONE(final long addr) throws IllegalArgumentException {
		return new FEMProxy(this.get_DONE(this.store.getLong(addr + 8), FEMValue.class), this.get_DONE(this.store.getLong(addr + 16), FEMString.class),
			this.getAt_DONE(addr + 24));
	}

	/** Diese Methode fügt den gegebenen Platzhalter in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putProxyFunction_DONE(final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		final long addr = this.store.insertRegion(32);
		this.store.putInt(addr, new int[]{FEMCodec2.TYPE_PROXY, 0});
		this.store.putLong(addr + 8, new long[]{this.put_DONE(src.id()), this.put_DONE(src.name()), this.put_DONE(src.get())});
		return addr;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Parameterfunktion zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, param: int)}. */
	protected FEMParam getParamFunction_DONE(final long addr) throws IllegalArgumentException {
		return FEMParam.from(this.store.getInt(addr + 4));
	}

	/** Diese Methode fügt die gegebene Parameterfunktion in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putParamFunction_DONE(final FEMParam src) throws NullPointerException, IllegalStateException {
		final long addr = this.store.insertRegion(8);
		this.store.putInt(addr, new int[]{FEMCodec2.TYPE_PARAM, src.index()});
		return addr;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Funktionkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, count: int, function: long, param: long[count])}. */
	protected ConcatFunction getConcatFunction_DONE(final long addr) throws IllegalArgumentException {
		return new ConcatFunction(this.getAt_DONE(addr + 8), this.getAllAt_DONE(addr + 16, this.store.getInt(addr + 4)));
	}

	/** Diese Methode fügt die gegebene Funktionkette in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putConcatFunction(final ConcatFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.params.length;
		final long addr = this.store.insertRegion((length * 8L) + 16L);
		this.store.putInt(addr, new int[]{FEMCodec2.TYPE_CONCAT, length});
		this.store.putLong(addr + 8, this.put_DONE(src.function));
		this.store.putLong(addr + 16, this.putAll_DONE(src.params));
		return addr;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Funktionsbindung zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, ignore: int, functionRef: long}. */
	protected ClosureFunction getClosureFunction_DONE(final long addr) throws IllegalArgumentException {
		return new ClosureFunction(this.getAt_DONE(addr + 8));
	}

	/** Diese Methode fügt die gegebene Funktionsbindung in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putClosureFunction_DONE(final ClosureFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final long addr = this.store.insertRegion(16);
		this.store.putInt(addr, new int[]{FEMCodec2.TYPE_CLOSURE, 0});
		this.store.putLong(addr + 8, (int)this.put_DONE(src.function));
		return addr;
	}

	/** Diese Methode gibt dden im gegebenen Speicherbereich enthaltenen Funktionsaufruf zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, count: int, function: long, param: long[count])}. */
	protected CompositeFunction getCompositeFunction_DONE(final long addr) throws IllegalArgumentException {
		return new CompositeFunction(this.getAt_DONE(addr + 8), this.getAllAt_DONE(addr + 16, this.store.getInt(addr + 4)));
	}

	/** Diese Methode fügt den gegebenen Funktionsaufruf in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putCompositeFunction_DONE(final CompositeFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.params.length;
		final long addr = this.store.insertRegion((length * 8L) + 16L);
		this.store.putInt(addr, new int[]{FEMCodec2.TYPE_COMPOSITE, length});
		this.store.putLong(addr + 8, this.put_DONE(src.function));
		this.store.putLong(addr + 16, this.putAll_DONE(src.params));
		return addr;
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + this.store.emu() + this.cacheMapping.emu();
	}

}
