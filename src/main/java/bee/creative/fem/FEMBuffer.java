package bee.creative.fem;

import java.io.File;
import java.io.IOException;
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
import bee.creative.io.MappedBuffer;
import bee.creative.lang.Objects;
import bee.creative.util.HashMapLO;
import bee.creative.util.HashMapOL;

/** Diese Klasse implementiert einen Puffer zur Auslagerung von {@link FEMFunction Funktionen} in einen {@link MappedBuffer Dateipuffer}. Die darüber
 * angebundene Datei besitz dafür eine entsprechende Datenstruktur, deren Kopfdaten beim Öffnen erzeugt bzw. geprüft werden.
 * <p>
 * Achtung: {@link FEMFuture Ergebniswerte} und {@link FEMNative Nativwerte} werden bei der Kodierun zwar angeboten aber in dieser Implementation nicht
 * unterstützt. Da {@link FEMFuture Ergebniswerte} beim {@link #put(FEMFunction) Einfügen} mit {@link #useReuseEnabled(boolean) Wiederverwendung}
 * 
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMBuffer implements Property<FEMFunction>, Emuable {

	/** Diese Schnittstelle dient der Erkennung bereits gepufferter Werte. */
	public static interface MappedValue {

		/** Diese Methode gibt die Addresse der Nutzdaten dieses Werts zurück, wenn er im gegebenen Puffer abgelegt ist. Andernfalls wird {@code 0} geliefert. */
		public long addr(MappedBuffer store);

	}

	/** Diese Klasse implementiert eine Wertliste, deren Elemente als Referenzen gegeben sind und in {@link #customGet(int)} über einen gegebenen
	 * {@link FEMBuffer} in Werte {@link FEMBuffer#get(long) übersetzt} werden. */
	public static class MappedArrayA extends FEMArray implements MappedValue {

		final MappedBuffer store;

		final FEMBuffer codec;

		final long addr;

		MappedArrayA(final FEMBuffer codec, final long addr, final int length, final int hash) throws IllegalArgumentException {
			super(length);
			this.store = codec.getStore();
			this.codec = codec;
			this.addr = addr;
			this.hash = hash;
		}

		@Override
		public long addr(final MappedBuffer store) {
			return store == this.store ? this.addr - 12 : 0;
		}

		@Override
		protected FEMValue customGet(final int index) {
			/** this.addr: value[length] */
			return this.codec.get(this.store.getLong(this.addr + (index * 8L)), FEMValue.class);
		}

	}

	/** Diese Klasse implementiert eine indizierte Wertliste mit beschleunigter {@link #find(FEMValue, int) Einzelwertsuche}. */
	public static class MappedArrayB extends MappedArrayA {

		MappedArrayB(final FEMBuffer codec, final long addr, final int length, final int hash) throws IllegalArgumentException {
			super(codec, addr, length, hash);
		}

		@Override
		public long addr(final MappedBuffer store) {
			return store == this.store ? this.addr - 16 : 0;
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

	/** Diese Klasse implementiert eine Bytefolge als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedBinary extends FEMBinary implements MappedValue {

		final MappedBuffer store;

		final long addr;

		MappedBinary(final MappedBuffer store, final long addr, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(length);
			this.store = Objects.notNull(store);
			this.addr = addr;
			this.hash = hash;
		}

		@Override
		public long addr(final MappedBuffer store) {
			return store == this.store ? this.addr - 12 : 0;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.store.get(this.addr + index);
		}

	}

	/** Diese Klasse implementiert eine {@code byte}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedStringA extends FEMString implements MappedValue {

		final MappedBuffer store;

		final long addr;

		MappedStringA(final MappedBuffer store, final long addr, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(length);
			this.store = Objects.notNull(store);
			this.addr = addr;
			this.hash = hash;
		}

		@Override
		public long addr(final MappedBuffer store) {
			return store == this.store ? this.addr - 12 : 0;
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.store.get(this.addr + index) & 0xFF;
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

	}

	/** Dieses Feld speichert die Typkennung für {@link #getByAddr(long)}. */
	protected static final byte REF_ADDR = 0;

	/** Dieses Feld speichert die Typkennung für {@link #getByIdent(long)}. */
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

	/** Dieses Feld speichert die Typkennung für {@link CompactArray}. */
	protected static final int TYPE_ARRAY_A = 1;

	/** Dieses Feld speichert die Typkennung für {@link CompactArray3}. */
	protected static final int TYPE_ARRAY_B = 2;

	/** Dieses Feld speichert die Typkennung für {@link CompactStringINT8}. */
	protected static final int TYPE_STRING_A = 3;

	/** Dieses Feld speichert die Typkennung für {@link CompactStringINT16}. */
	protected static final int TYPE_STRING_B = 4;

	/** Dieses Feld speichert die Typkennung für {@link CompactStringINT32}. */
	protected static final int TYPE_STRING_C = 5;

	/** Dieses Feld speichert die Typkennung für {@link CompactBinary}. */
	protected static final int TYPE_BINARY = 6;

	/** Dieses Feld speichert die Typkennung für {@link FEMFuture}. */
	protected static final int TYPE_FUTURE = 7;

	/** Dieses Feld speichert die Typkennung für {@link FEMNative}. */
	protected static final int TYPE_NATIVE = 8;

	/** Dieses Feld speichert die Typkennung für {@link FEMProxy}. */
	protected static final int TYPE_PROXY = 9;

	/** Dieses Feld speichert die Typkennung für {@link FEMParam}. */
	protected static final int TYPE_PARAM = 10;

	/** Dieses Feld speichert die Typkennung für {@link ConcatFunction}. */
	protected static final int TYPE_CONCAT = 11;

	/** Dieses Feld speichert die Typkennung für {@link ClosureFunction}. */
	protected static final int TYPE_CLOSURE = 12;

	/** Dieses Feld speichert die Typkennung für {@link CompositeFunction}. */
	protected static final int TYPE_COMPOSITE = 13;

	/** Dieses Feld speichert die Konstantenkennung von {@link FEMVoid#INSTANCE}. */
	protected static final int IDENT_VOID = 1;

	/** Dieses Feld speichert die Konstantenkennung von {@link FEMBoolean#TRUE}. */
	protected static final int IDENT_TRUE = 2;

	/** Dieses Feld speichert die Konstantenkennung von {@link FEMBoolean#FALSE}. */
	protected static final int IDENT_FALSE = 3;

	/** Dieses Feld speichert die Konstantenkennung von {@link FEMArray#EMPTY}. */
	protected static final int IDENT_EMPTY_ARRAY = 4;

	/** Dieses Feld speichert die Konstantenkennung von {@link FEMString#EMPTY}. */
	protected static final int IDENT_EMPTY_STRING = 5;

	/** Dieses Feld speichert die Konstantenkennung von {@link FEMBinary#EMPTY}. */
	protected static final int IDENT_EMPTY_BINARY = 6;

	/** Dieses Feld speichert die Adresse des nächsten Speicherbereichs. */
	private long next;

	/** Dieses Feld speichert die Größe des {@link #store}. */
	private long limit;

	/** Dieses Feld speichert den Puffer, in dem die Zahlenfolgen abgelegt sind. */
	protected final MappedBuffer store;

	/** Dieses Feld speichert {@code true}, wenn Referenzen bei {@link #put(FEMFunction)} wiederverwendet werden sollen. */
	protected boolean reuseEnabled = true;

	/** Dieses Feld bildet von einer Funktion auf eine Referenz ab und wird zusammen mit {@link #reuseEnabled} in {@link #put(FEMFunction)} eingesetzt. */
	protected final HashMapOL<FEMFunction> reuseMapping = new HashMapOL<>();

	/** Dieses Feld speichert {@code true}, wenn Funktionen bei {@link #get(long)} wiederverwendet werden sollen. */
	protected boolean cacheEnabled;

	/** Dieses Feld bildet von einer Referenz auf eine Funktion ab und wird zusammen mit {@link #cacheEnabled} in {@link #get(long)} eingesetzt. */
	protected final HashMapLO<FEMFunction> cacheMapping = new HashMapLO<>();

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei.
	 *
	 * @see MappedBuffer#MappedBuffer(File, boolean)
	 * @param file Datei.
	 * @param readonly {@code true}, wenn die Datei nur mit Lesezugriff angebunden werden soll.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public FEMBuffer(final File file, final boolean readonly) throws IOException {
		this.store = new MappedBuffer(file, readonly);
		final long MAGIC = 0x31454c49464d4546L;
		this.limit = this.store.size();
		if (!readonly && (this.limit == 0)) {
			this.next = 24;
			this.store.grow(this.next);
			this.store.putLong(0, new long[]{MAGIC, 0, this.next});
		} else {
			if (this.limit < 24) throw new IllegalArgumentException();
			if (this.store.getLong(0) != MAGIC) throw new IllegalArgumentException();
			this.next = this.store.getLong(16);
			if (this.next < 24) throw new IllegalArgumentException();
		}
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn über {@link #put(FEMFunction)} keine Duplikate angefügt, sonder die Referenzen der angefügten
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
	public FEMBuffer useReuseEnabled(final boolean value) {
		this.reuseEnabled = value;
		return this;
	}

	/** Diese Methode gibt den in {@link #put(FEMFunction)} verwendeten Zwischenspeicher der Referenzen zurück.
	 * 
	 * @return Zwischenspeicher der Referenen. */
	public Map<FEMFunction, Long> getReuseMapping() {
		return this.reuseMapping;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die über {@link #get(long)} gelieferten Funktionen zur Wiederverwendng zwischengespeichert werden
	 * sollen. Dazu werden je Funktion ca. 16 Byte Verwaltungsdaten benötigt.
	 *
	 * @return Aktivierung der Zwischenspeicherung. */
	public boolean isCacheEnabled() {
		return this.cacheEnabled;
	}

	/** Diese Methode setzt die {@link #isCacheEnabled() Aktivierung der Zwischenspeicherung} von Funktionen und gibt {@code this} zurück.
	 *
	 * @param value Aktivierung der Zwischenspeicherung.
	 * @return {@code this}. */
	public FEMBuffer useCacheEnabled(final boolean value) {
		this.cacheEnabled = value;
		return this;
	}

	/** Diese Methode gibt den in {@link #get(long)} verwendeten Zwischenspeicher der Funktionen zurück.
	 *
	 * @return Zwischenspeicher der Funktionen. */
	public Map<Long, FEMFunction> getCacheMapping() {
		return this.cacheMapping;
	}

	@Override
	public FEMFunction get() {
		final long addr = this.store.getLong(8);
		return addr != 0 ? this.get(addr) : null;
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz zurück. Sofern {@link #useCacheEnabled(boolean) aktiviert}, wird das Ergebnis zur Wiederverwendung
	 * zwichengespeichert.
	 *
	 * @param ref Referenz.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMFunction get(final long ref) throws IllegalArgumentException {
		if (!this.cacheEnabled) return this.getByRef(ref);
		final Long key = new Long(ref);
		synchronized (this.cacheMapping) {
			FEMFunction result = this.cacheMapping.get(key);
			if (result != null) return result;
			result = this.getByRef(ref);
			this.cacheMapping.put(key, result);
			return result;
		}
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz als Instanz der gegebenen Klasse zurück und ist eine Abkürzung für {@link Class#cast(Object)
	 * clazz.cast(this.get(ref))}.
	 *
	 * @param ref Referenz.
	 * @param clazz Klasse der gelieferten Funktion.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public <GResult> GResult get(final long ref, final Class<GResult> clazz) throws IllegalArgumentException {
		try {
			return clazz.cast(this.get(ref));
		} catch (final IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt den {@link MappedBuffer Dateipuffer} zurück, in welchen die Funktionen abgelegt sind.
	 *
	 * @return Dateipuffer. */
	public MappedBuffer getStore() {
		return this.store;
	}

	@Override
	public void set(final FEMFunction value) {
		this.store.putLong(8, value != null ? this.put(value) : 0);
	}

	/** Diese Methode gibt die Funktion zu der Referenz zurück, die an der gegebenen Adresse steht, und ist damit eine Abkürzung für {@link #get(long)
	 * this.get(this.getStore().getLong(addr))}. */
	protected final FEMFunction getAt(final long addr) throws IllegalArgumentException {
		return this.get(this.store.getLong(addr));
	}

	/** Diese Methode gibt die Funktionen zur gegebene Anzahl an Referenzen im gegebenen Speicherbereich zurück.
	 *
	 * @see #getAt(long)
	 * @param addr Adresse des Speicherbereichs.
	 * @param count Anzahl der Referenzen.
	 * @return Funktionen. */
	protected final FEMFunction[] getAllAt(final long addr, final int count) throws IllegalArgumentException {
		final FEMFunction[] result = new FEMFunction[count];
		for (int i = 0; i < count; i++) {
			result[i] = this.getAt(addr + (i * 8));
		}
		return result;
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz zurück und wird von {@link #get(long)} aufgerufen. Abhängig von der Typkennung der Referenz kann
	 * diese Referenz auch als Adresse an {@link #getByAddr(long)} bzw. als Konstantenkennung an {@link #getByIdent(long)} weitergelietet werden.
	 *
	 * @param ref Referenz.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	protected FEMFunction getByRef(final long ref) throws IllegalArgumentException {
		switch ((int)ref & 7) {
			default:
			case FEMBuffer.REF_ADDR:
				return this.getByAddr(ref);
			case FEMBuffer.REF_INTEGER:
				return this.getIntegerValue(ref & -8L);
			case FEMBuffer.REF_DECIMAL:
				return this.getDecimalValue(ref & -8L);
			case FEMBuffer.REF_DURATION:
				return this.getDurationValue(ref & -8L);
			case FEMBuffer.REF_DATETIME:
				return this.getDatetimeValue(ref & -8L);
			case FEMBuffer.REF_HANDLER:
				return this.getHandlerValue(ref & -8L);
			case FEMBuffer.REF_OBJECT:
				return this.getObjectValue(ref & -8L);
			case FEMBuffer.REF_IDENT:
				return this.getByIdent(ref >>> 3);
		}
	}

	/** Diese Methode gibt die Funktion zur gegebenen Adresse zurück. Dazu wird die an der gegebenen Adresse gespeicherte Typkennung in einer Fallunterscheidung
	 * eingesetzt.
	 *
	 * @param addr Adresse.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Adresse ungültig ist, bspw. weil die Typkennung unbekannt ist. */
	protected FEMFunction getByAddr(final long addr) throws IllegalArgumentException {
		final int type = this.store.getInt(addr);
		switch (type) {
			case TYPE_ARRAY_A:
				return this.getArrayValueA(addr);
			case TYPE_ARRAY_B:
				return this.getArrayValueB(addr);
			case TYPE_STRING_A:
				return this.getStringValueA(addr);
			case TYPE_STRING_B:
				return this.getStringValueB(addr);
			case TYPE_STRING_C:
				return this.getStringValueC(addr);
			case TYPE_BINARY:
				return this.getBinaryValue(addr);
			case TYPE_FUTURE:
				return this.getFutureValue(addr);
			case TYPE_NATIVE:
				return this.getNativeValue(addr);
			case TYPE_PROXY:
				return this.getProxyFunction(addr);
			case TYPE_PARAM:
				return this.getParamFunction(addr);
			case TYPE_CONCAT:
				return this.getConcatFunction(addr);
			case TYPE_CLOSURE:
				return this.getClosureFunction(addr);
			case TYPE_COMPOSITE:
				return this.getCompositeFunction(addr);
			default:
				throw new IllegalArgumentException();
		}
	}

	/** Diese Methode gibt die Funktion zur gegebenen Konstantenkennung zurück.
	 *
	 * @param ident Identifikator.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn der Identifikator ungültig ist. */
	protected FEMFunction getByIdent(final long ident) throws IllegalArgumentException {
		if (ident == FEMBuffer.IDENT_VOID) return FEMVoid.INSTANCE;
		if (ident == FEMBuffer.IDENT_TRUE) return FEMBoolean.TRUE;
		if (ident == FEMBuffer.IDENT_FALSE) return FEMBoolean.FALSE;
		if (ident == FEMBuffer.IDENT_EMPTY_ARRAY) return FEMArray.EMPTY;
		if (ident == FEMBuffer.IDENT_EMPTY_STRING) return FEMString.EMPTY;
		if (ident == FEMBuffer.IDENT_EMPTY_BINARY) return FEMBinary.EMPTY;
		throw new IllegalArgumentException();
	}

	/** Diese Methode fügt die gegebene Funktion in den Puffer ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktion.
	 * @return Referenz auf die Funktion.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion nicht angefügt werden kann. */
	public long put(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (src instanceof MappedValue) {
			final long result = ((MappedValue)src).addr(this.store);
			if (result != 0) return result;
		}
		if (this.reuseEnabled) return this.putByRef(Objects.notNull(src));
		synchronized (this.reuseMapping) {
			Long result = this.reuseMapping.get(Objects.notNull(src));
			if (result != null) return result.longValue();
			result = new Long(this.putByRef(src));
			this.reuseMapping.put(this.get(result.longValue()), result);
			return result.longValue();
		}
	}

	/** Diese Methode {@link #put(FEMFunction) überführt} die gegebenen Funktionen in deren Referenzen und gibt die Liste dieser Referenzen zurück.
	 *
	 * @param src Funktionen.
	 * @return Referenzen auf die Funktionen.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn mindestens eine der Funktionen nicht angefügt werden kann. */
	public long[] putAll(final FEMFunction... src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.length;
		final long[] result = new long[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.put(src[i]);
		}
		return result;
	}

	/** Diese Methode gibt die Referenz zur gegebenen Funktion zurück und wird von {@link #put(FEMFunction)} aufgerufen. Die Funktion wird dazu zunächst an
	 * {@link #putByIdent(FEMFunction)} weitergeleitet, um dazu die Konstantenkennung zu ermiteln. Wenn diese existiert, wird sie als Referenz geliefert.
	 * Andernfalls erfolgt eine Falluntersheidung auf dem Typ der Funktion. Dabei kann die Funktion auch an {@link #putByAddr(FEMFunction)} weitergelietet werden.
	 *
	 * @param src Funktion.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn {@link #putData(long)} diese auslöst.
	 * @throws IllegalArgumentException Wenn die Funktion nicht ausgelagert werden kann. */
	protected long putByRef(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final long result = this.putByIdent(src);
		if (result != 0) return (result << 3) | FEMBuffer.REF_IDENT;
		if (src instanceof FEMInteger) return this.putIntegerValue((FEMInteger)src) | FEMBuffer.REF_INTEGER;
		if (src instanceof FEMDecimal) return this.putDecimalValue((FEMDecimal)src) | FEMBuffer.REF_DECIMAL;
		if (src instanceof FEMHandler) return this.putHandlerValue((FEMHandler)src) | FEMBuffer.REF_HANDLER;
		if (src instanceof FEMObject) return this.putObjectValue((FEMObject)src) | FEMBuffer.REF_OBJECT;
		if (src instanceof FEMDuration) return this.putDurationValue((FEMDuration)src) | FEMBuffer.REF_DURATION;
		if (src instanceof FEMDatetime) return this.putDatetimeValue((FEMDatetime)src) | FEMBuffer.REF_DATETIME;
		return this.putByAddr(src) | FEMBuffer.REF_ADDR;
	}

	/** Diese Methode fügt die gegebene Funktion in den Puffer ein und gibt die Adresse darauf zurück.
	 *
	 * @param src Funktion.
	 * @return Adresse.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn {@link #putData(long)} diese auslöst.
	 * @throws IllegalArgumentException Wenn die Funktion nicht ausgelagert werden kann. */
	protected long putByAddr(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (src instanceof FEMArray) return this.putArrayValue((FEMArray)src);
		if (src instanceof FEMString) return this.putStringValue((FEMString)src);
		if (src instanceof FEMBinary) return this.putBinaryValue((FEMBinary)src);
		if (src instanceof FEMFuture) return this.putFutureValue((FEMFuture)src);
		if (src instanceof FEMNative) return this.putNativeValue((FEMNative)src);
		if (src instanceof FEMProxy) return this.putProxyFunction((FEMProxy)src);
		if (src instanceof FEMParam) return this.putParamFunction((FEMParam)src);
		if (src instanceof ConcatFunction) return this.putConcatFunction((ConcatFunction)src);
		if (src instanceof ClosureFunction) return this.putClosureFunction((ClosureFunction)src);
		if (src instanceof CompositeFunction) return this.putCompositeFunction((CompositeFunction)src);
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Konstantenkennung der gegebenen Funktion oder zurück, sofern eine solche existiert. Andernfalls wird {@code 0} geliefert.
	 *
	 * @param src Funktion.
	 * @return Konstantenkennung oder {@code 0}.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist. */
	protected long putByIdent(final FEMFunction src) throws NullPointerException {
		if (src.equals(FEMVoid.INSTANCE)) return FEMBuffer.IDENT_VOID;
		if (src.equals(FEMBoolean.TRUE)) return FEMBuffer.IDENT_TRUE;
		if (src.equals(FEMBoolean.FALSE)) return FEMBuffer.IDENT_FALSE;
		if (src.equals(FEMArray.EMPTY)) return FEMBuffer.IDENT_EMPTY_ARRAY;
		if (src.equals(FEMString.EMPTY)) return FEMBuffer.IDENT_EMPTY_STRING;
		if (src.equals(FEMBinary.EMPTY)) return FEMBuffer.IDENT_EMPTY_BINARY;
		return 0;
	}

	/** Diese Methode reserviert einen neuen Speicherbereich mit der gegebenen Größe und gibt die Adresse seines Beginns zurück. Die Adresse ist stets ein
	 * Vielfaches von {@code 8}.
	 *
	 * @param size Größe des Speicherbereichs in Byte.
	 * @return Adresse des Beginns des Speicherbereichs.
	 * @throws IllegalStateException Wenn der Puffer nicht zum schreiben angebunden ist.
	 * @throws IllegalArgumentException Wenn {@code size} ungültig ist. */
	protected long putData(final long size) throws IllegalStateException, IllegalArgumentException {
		synchronized (this.store) {
			final long addr = this.next, next = (addr + size + 7) & -8L;
			if (next < addr) throw new IllegalArgumentException();
			if (next > this.limit) {
				this.store.grow(next);
				this.limit = next;
			}
			this.store.putLong(16, next);
			this.next = next;
			return addr;
		}
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Wertliste zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, align: int, valueRef: long[length])}. Sie beginnt mit dem {@link FEMArray#hashCode() Streuwert} und endet mit
	 * den über {@link #putAll(FEMFunction...) Referenzen} der Elemente der Wertliste. */
	protected FEMArray getArrayValueA(final long addr) throws IllegalArgumentException {
		return new MappedArrayA(this, addr + 16, this.store.getInt(addr + 8), this.store.getInt(addr + 4));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Wertliste zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, align: int, valueRef: long[length], count: int, range: int[count-1], index: int[length])}. Sie beginnt mit dem
	 * {@link FEMArray#hashCode() Streuwert} und {@link FEMArray#length() Länge} der Wertliste, gefolgt von den {@link #putAll(FEMFunction...) Referenzen} der
	 * Elemente der Wertliste sowie der {@link CompactArray3#table Streuwerttabelle} zur beschleunigten Einzelwertsuche. */
	protected FEMArray getArrayValueB(final long addr) throws IllegalArgumentException {
		return new MappedArrayB(this, addr + 16, this.store.getInt(addr + 8), this.store.getInt(addr + 4));
	}

	/** Diese Methode fügt die gegebene Wertliste in den Puffer ein und gibt die Adresse darauf zurück. Eine über {@link FEMArray#compact(boolean)} indizierte
	 * Wertliste wird mit der Indizierung kodiert. */
	protected long putArrayValue(final FEMArray src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.length();
		if (src instanceof CompactArray3) {
			final int[] table = ((CompactArray3)src).table;
			final long addr = this.putData((length * 8L) + (table.length * 4L) + 16);
			this.store.putInt(addr, new int[]{FEMBuffer.TYPE_ARRAY_B, src.hashCode(), length, 0});
			this.store.putLong(addr + 16, this.putAll(src.value()));
			this.store.putInt(addr + (length * 8L) + 16, table);
			return addr;
		} else {
			final long addr = this.putData((length * 8L) + 16);
			this.store.putInt(addr, new int[]{FEMBuffer.TYPE_ARRAY_A, src.hashCode(), length, 0});
			this.store.putLong(addr + 16, this.putAll(src.value()));
			return addr;
		}
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code byte}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, item: byte[length])}. */
	protected FEMString getStringValueA(final long addr) throws IllegalArgumentException {
		return new MappedStringA(this.store, addr + 12, this.store.getInt(addr + 8), this.store.getInt(addr + 4));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code short}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, item: short[length])}. */
	protected FEMString getStringValueB(final long addr) throws IllegalArgumentException {
		return new MappedStringB(this.store, addr + 12, this.store.getInt(addr + 8), this.store.getInt(addr + 4));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code int}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, item: int[length])}. */
	protected FEMString getStringValueC(final long addr) throws IllegalArgumentException {
		return new MappedStringC(this.store, addr + 12, this.store.getInt(addr + 8), this.store.getInt(addr + 4));
	}

	/** Diese Methode fügt die gegebene Zeichenkette in den Puffer ein und gibt die Adresse darauf zurück. Dazu werden die {@link FEMString#compact()
	 * kompaktierten} Formen der Zeichenkette analysiert und entsprechend als {@code byte}, {@code short} oder {@code int}-Codepoints gespeichert. */
	protected long putStringValue(FEMString src) throws NullPointerException, IllegalStateException {
		src = src.compact();
		final int length = src.length();
		if (src instanceof FEMString.CompactStringINT8) {
			final long addr = this.putData(length + 12);
			this.store.putInt(addr, new int[]{FEMBuffer.TYPE_STRING_A, src.hashCode(), length});
			this.store.put(addr + 12, src.toBytes());
			return addr;
		} else if (src instanceof FEMString.CompactStringINT16) {
			final long addr = this.putData((length * 2L) + 12);
			this.store.putInt(addr, new int[]{FEMBuffer.TYPE_STRING_B, src.hashCode(), length});
			this.store.putShort(addr + 12, src.toShorts());
			return addr;
		} else {
			final long addr = this.putData((length * 4L) + 12);
			this.store.putInt(addr, new int[]{FEMBuffer.TYPE_STRING_C, src.hashCode(), length});
			this.store.putInt(addr + 12, src.toInts());
			return addr;
		}
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Bytefolge zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, hash: int, length: int, item: byte[length])}. */
	protected FEMBinary getBinaryValue(final long addr) throws IllegalArgumentException {
		return new MappedBinary(this.store, addr + 12, this.store.getInt(addr + 8), this.store.getInt(addr + 4));
	}

	/** Diese Methode fügt die gegebene Bytefolge in den Puffer ein und gibt die Adresse darauf zurück. */
	protected long putBinaryValue(final FEMBinary src) throws NullPointerException, IllegalStateException {
		final int length = src.length();
		final long addr = this.putData(length + 12);
		this.store.putInt(addr, new int[]{FEMBuffer.TYPE_BINARY, src.hashCode(), length});
		this.store.put(addr + 12, src.value());
		return addr;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Dezimalzahl zurück. Die Struktur des Speicherbereichs ist {@code (value: long)}. */
	protected FEMInteger getIntegerValue(final long addr) throws IllegalArgumentException {
		return new FEMInteger(this.store.getLong(addr));
	}

	/** Diese Methode fügt die gegebene Dezimalzahl in den Puffer ein und gibt die Adresse darauf zurück. */
	protected long putIntegerValue(final FEMInteger src) throws NullPointerException, IllegalStateException {
		final long addr = this.putData(8);
		this.store.putLong(addr, src.value());
		return addr;
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltenen Dezimalbruch zurück. Die Struktur des Speicherbereichs ist {@code (value: double)}. */
	protected FEMDecimal getDecimalValue(final long addr) throws IllegalArgumentException {
		return new FEMDecimal(Double.longBitsToDouble(this.store.getLong(addr)));
	}

	/** Diese Methode fügt den gegebenen Dezimalbruch in den Puffer ein und gibt die Adresse darauf zurück. */
	protected long putDecimalValue(final FEMDecimal src) throws NullPointerException, IllegalStateException {
		final long addr = this.putData(8);
		this.store.putLong(addr, Double.doubleToRawLongBits(src.value()));
		return addr;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Zeitspanne zurück. Die Struktur des Speicherbereichs ist {@code (value: long)}. */
	protected FEMDuration getDurationValue(final long addr) throws IllegalArgumentException {
		return new FEMDuration(this.store.getLong(addr));
	}

	/** Diese Methode fügt den gegebenen Zeitspanne in den Puffer ein und gibt die Adresse darauf zurück. */
	protected long putDurationValue(final FEMDuration src) throws NullPointerException, IllegalStateException {
		final long addr = this.putData(8);
		this.store.putLong(addr, src.value());
		return addr;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Zeitangabe zurück. Die Struktur des Speicherbereichs ist {@code (value: long)}. */
	protected FEMDatetime getDatetimeValue(final long addr) throws IllegalArgumentException {
		return new FEMDatetime(this.store.getLong(addr));
	}

	/** Diese Methode fügt die gegebene Zeitangabe in den Puffer ein und gibt die Adresse darauf zurück. */
	protected long putDatetimeValue(final FEMDatetime src) throws NullPointerException, IllegalStateException {
		final long addr = this.putData(8);
		this.store.putLong(addr, src.value());
		return addr;
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltenen Funktionszeiger zurück. Die Struktur des Speicherbereichs ist
	 * {@code (functionRef: long)}. */
	protected FEMHandler getHandlerValue(final long addr) throws IllegalArgumentException {
		return new FEMHandler(this.getAt(addr));
	}

	/** Diese Methode fügt den gegebenen Funktionszeiger in den Puffer ein und gibt die Adresse darauf zurück. */
	protected long putHandlerValue(final FEMHandler src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final long addr = this.putData(8);
		this.store.putLong(addr, this.put(src.value()));
		return addr;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Objektreferenz zurück. Die Struktur des Speicherbereichs ist {@code (value: long)}. */
	protected FEMObject getObjectValue(final long addr) throws IllegalArgumentException {
		return new FEMObject(this.store.getLong(addr));
	}

	/** Diese Methode fügt den gegebenen Objektreferenz in den Puffer ein und gibt die Adresse darauf zurück. */
	protected long putObjectValue(final FEMObject src) throws NullPointerException, IllegalStateException {
		final long addr = this.putData(8);
		this.store.putLong(addr, src.value());
		return addr;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltenen Ergebniswert zurück. */
	protected FEMFuture getFutureValue(final long addr) throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode fügt den gegebenen Ergebniswert in den Puffer ein und gibt die Adresse darauf zurück. */
	protected int putFutureValue(final FEMFuture src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltenen Nativwert zurück. */
	protected FEMNative getNativeValue(final long addr) throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode fügt den gegebenen Nativwert in den Puffer ein und gibt die Adresse darauf zurück. */
	protected int putNativeValue(final FEMNative src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich enthaltenen Platzhalter zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, align: int, idRef: long, nameRef: long, functionRef: long)}. */
	protected FEMProxy getProxyFunction(final long addr) throws IllegalArgumentException {
		return new FEMProxy(this.get(this.store.getLong(addr + 8), FEMValue.class), this.get(this.store.getLong(addr + 16), FEMString.class),
			this.getAt(addr + 24));
	}

	/** Diese Methode fügt den gegebenen Platzhalter in den Puffer ein und gibt die Adresse darauf zurück. */
	protected long putProxyFunction(final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		final long addr = this.putData(32);
		this.store.putInt(addr, new int[]{FEMBuffer.TYPE_PROXY, 0});
		this.store.putLong(addr + 8, new long[]{this.put(src.id()), this.put(src.name()), this.put(src.get())});
		return addr;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Parameterfunktion zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, param: int)}. */
	protected FEMParam getParamFunction(final long addr) throws IllegalArgumentException {
		return FEMParam.from(this.store.getInt(addr + 4));
	}

	/** Diese Methode fügt die gegebene Parameterfunktion in den Puffer ein und gibt die Adresse darauf zurück. */
	protected long putParamFunction(final FEMParam src) throws NullPointerException, IllegalStateException {
		final long addr = this.putData(8);
		this.store.putInt(addr, new int[]{FEMBuffer.TYPE_PARAM, src.index()});
		return addr;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Funktionkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, count: int, function: long, param: long[count])}. */
	protected ConcatFunction getConcatFunction(final long addr) throws IllegalArgumentException {
		return new ConcatFunction(this.getAt(addr + 8), this.getAllAt(addr + 16, this.store.getInt(addr + 4)));
	}

	/** Diese Methode fügt die gegebene Funktionkette in den Puffer ein und gibt die Adresse darauf zurück. */
	protected long putConcatFunction(final ConcatFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.params.length;
		final long addr = this.putData((length * 8L) + 16L);
		this.store.putInt(addr, new int[]{FEMBuffer.TYPE_CONCAT, length});
		this.store.putLong(addr + 8, this.put(src.function));
		this.store.putLong(addr + 16, this.putAll(src.params));
		return addr;
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Funktionsbindung zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, ignore: int, functionRef: long}. */
	protected ClosureFunction getClosureFunction(final long addr) throws IllegalArgumentException {
		return new ClosureFunction(this.getAt(addr + 8));
	}

	/** Diese Methode fügt die gegebene Funktionsbindung in den Puffer ein und gibt die Adresse darauf zurück. */
	protected long putClosureFunction(final ClosureFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final long addr = this.putData(16);
		this.store.putInt(addr, new int[]{FEMBuffer.TYPE_CLOSURE, 0});
		this.store.putLong(addr + 8, (int)this.put(src.function));
		return addr;
	}

	/** Diese Methode gibt dden im gegebenen Speicherbereich enthaltenen Funktionsaufruf zurück. Die Struktur des Speicherbereichs ist
	 * {@code (type: int, count: int, function: long, param: long[count])}. */
	protected CompositeFunction getCompositeFunction(final long addr) throws IllegalArgumentException {
		return new CompositeFunction(this.getAt(addr + 8), this.getAllAt(addr + 16, this.store.getInt(addr + 4)));
	}

	/** Diese Methode fügt den gegebenen Funktionsaufruf in den Puffer ein und gibt die Adresse darauf zurück. */
	protected long putCompositeFunction(final CompositeFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.params.length;
		final long addr = this.putData((length * 8L) + 16L);
		this.store.putInt(addr, new int[]{FEMBuffer.TYPE_COMPOSITE, length});
		this.store.putLong(addr + 8, this.put(src.function));
		this.store.putLong(addr + 16, this.putAll(src.params));
		return addr;
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + this.store.emu() + this.reuseMapping.emu() + this.cacheMapping.emu();
	}

}
