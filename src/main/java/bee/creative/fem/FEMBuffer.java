package bee.creative.fem;

import java.io.File;
import java.io.IOException;
import bee.creative.bind.Property;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.fem.FEMArray.CompactArray3;
import bee.creative.io.MappedBuffer;
import bee.creative.lang.Integers;
import bee.creative.lang.Objects;
import bee.creative.util.HashMapLO;
import bee.creative.util.HashMapOL;

/** Diese Klasse implementiert einen Puffer zur Auslagerung von {@link FEMFunction Funktionen} in einen {@link MappedBuffer Dateipuffer}. Die darüber
 * angebundene Datei besitz dafür eine entsprechende Datenstruktur, deren Kopfdaten beim Öffnen erzeugt bzw. geprüft werden.
 * <p>
 * Beim {@link #put(FEMFunction) Einfügen} eines {@link FEMProxy Platzhalters} wird dessen {@link FEMProxy#get() Zielfunktion} aktualisiert, wenn sie dabei von
 * bzw. auf {@code null} wechselt.
 * <p>
 * {@link FEMNative Nativwerte} werden bei der Kodierun zwar angeboten aber in dieser Implementation nicht unterstützt.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMBuffer implements Property<FEMFunction>, Emuable {

	/** Diese Klasse implementiert eine Wertliste, deren Elemente als Referenzen gegeben sind und in {@link #customGet(int)} über einen gegebenen
	 * {@link FEMBuffer} in Werte {@link FEMBuffer#get(long) übersetzt} werden. */
	public static class MappedArray1 extends FEMArray {

		final FEMBuffer buffer;

		final long addr;

		MappedArray1(final int length, final FEMBuffer buffer, final long addr) throws IllegalArgumentException {
			super(length);
			this.buffer = buffer;
			this.addr = addr;
		}

		@Override
		protected FEMValue customGet(final int index) {
			/** this.addr: value[length] */
			return this.buffer.getAt(this.addr + (index * 8L), FEMValue.class);
		}

		@Override
		protected boolean customEquals(final FEMArray that) throws NullPointerException {
			if (that instanceof MappedArray1) {
				final MappedArray1 that2 = (MappedArray1)that;
				if ((this.addr == that2.addr) && (this.buffer.buffer == that2.buffer.buffer)) return true;
			}
			return super.customEquals(that);
		}

		@Override
		public int hashCode() {
			return this.buffer.buffer.getInt(this.addr - 4);
		}

	}

	/** Diese Klasse implementiert eine indizierte Wertliste mit beschleunigter {@link #find(FEMValue, int) Einzelwertsuche}. */
	public static class MappedArray2 extends MappedArray1 {

		MappedArray2(final int length, final FEMBuffer buffer, final long addr) throws IllegalArgumentException {
			super(length, buffer, addr);
		}

		@Override
		protected int customFind(final FEMValue that, final int offset, int length, final boolean foreward) {
			/** this.addr: value: long[length], count: int, range: int[count-1], index: int[length] */
			final long addr = this.addr + (this.length * 8L);
			final int count = this.buffer.buffer.getInt(addr), hash = that.hashCode() & (count - 2);
			final long addr2 = addr + (hash * 4L);
			int l = this.buffer.buffer.getInt(addr2), r = this.buffer.buffer.getInt(addr2 + 4) - 1;
			length += offset;
			if (foreward) {
				for (; l <= r; l++) {
					final int result = this.buffer.buffer.getInt(addr + (l * 4L));
					if (length <= result) return -1;
					if ((offset <= result) && that.equals(this.customGet(result))) return result;
				}
			} else {
				for (; l <= r; r--) {
					final int result = this.buffer.buffer.getInt(addr + (r * 4L));
					if (result < offset) return -1;
					if ((result < length) && that.equals(this.customGet(result))) return result;
				}
			}
			return -1;
		}

	}

	/** Diese Klasse implementiert eine Bytefolge als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedBinary1 extends FEMBinary {

		final MappedBuffer buffer;

		final long addr;

		MappedBinary1(final int length, final MappedBuffer buffer, final long addr) throws NullPointerException, IllegalArgumentException {
			super(length);
			this.buffer = Objects.notNull(buffer);
			this.addr = addr;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.buffer.get(this.addr + index);
		}

		@Override
		protected boolean customEquals(final FEMBinary that) throws NullPointerException {
			if (that instanceof MappedBinary1) {
				final MappedBinary1 that2 = (MappedBinary1)that;
				if ((this.addr == that2.addr) && (this.buffer == that2.buffer)) return true;
			}
			return super.customEquals(that);
		}

		@Override
		public int hashCode() {
			return this.buffer.getInt(this.addr - 4);
		}

	}

	/** Diese Klasse implementiert eine {@code byte}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedString1 extends FEMString {

		final MappedBuffer buffer;

		final long addr;

		MappedString1(final int length, final MappedBuffer buffer, final long addr) throws NullPointerException, IllegalArgumentException {
			super(length);
			this.buffer = Objects.notNull(buffer);
			this.addr = addr;
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.buffer.get(this.addr + index) & 0xFF;
		}

		@Override
		protected boolean customEquals(final FEMString that) throws NullPointerException {
			if (that instanceof MappedString1) {
				final MappedString1 that2 = (MappedString1)that;
				if ((this.addr == that2.addr) && (this.buffer == that2.buffer)) return true;
			}
			return super.customEquals(that);
		}

		@Override
		public int hashCode() {
			return this.buffer.getInt(this.addr - 4);
		}

	}

	/** Diese Klasse implementiert eine {@code short}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedString2 extends MappedString1 {

		MappedString2(final int length, final MappedBuffer buffer, final long addr) throws NullPointerException, IllegalArgumentException {
			super(length, buffer, addr);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.buffer.getShort(this.addr + (index * 2L)) & 0xFFFF;
		}

	}

	/** Diese Klasse implementiert eine {@code int}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedString3 extends MappedString1 {

		MappedString3(final int length, final MappedBuffer buffer, final long addr) throws NullPointerException, IllegalArgumentException {
			super(length, buffer, addr);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.buffer.getInt(this.addr + (index * 4L));
		}

	}

	/** Dieses Feld speichert die Typkennung für {@link FEMVoid}. */
	protected static final byte TYPE_VOID = 0;

	/** Dieses Feld speichert die Typkennung für {@link FEMBoolean#TRUE}. */
	protected static final byte TYPE_TRUE = 1;

	/** Dieses Feld speichert die Typkennung für {@link FEMBoolean#FALSE}. */
	protected static final byte TYPE_FALSE = 2;

	/** Dieses Feld speichert die Typkennung für {@link FEMArray#EMPTY}. */
	protected static final byte TYPE_ARRAY_DATA1 = 3;

	/** Dieses Feld speichert die Typkennung für {@link MappedArray1}. */
	protected static final byte TYPE_ARRAY_ADDR1 = 4;

	/** Dieses Feld speichert die Typkennung für {@link MappedArray2}. */
	protected static final byte TYPE_ARRAY_ADDR2 = 5;

	/** Dieses Feld speichert die Typkennung für {@link FEMString#EMPTY}. */
	protected static final byte TYPE_STRING_DATA1 = 6;

	/** Dieses Feld speichert die Typkennung für {@link FEMString.UniformString} mit einem Zeichen. */
	protected static final byte TYPE_STRING_DATA2 = 7;

	/** Dieses Feld speichert die Typkennung für {@link MappedString1}. */
	protected static final byte TYPE_STRING_ADDR1 = 8;

	/** Dieses Feld speichert die Typkennung für {@link MappedString2}. */
	protected static final byte TYPE_STRING_ADDR2 = 9;

	/** Dieses Feld speichert die Typkennung für {@link MappedString3}. */
	protected static final byte TYPE_STRING_ADDR3 = 10;

	/** Dieses Feld speichert die Typkennung für {@link FEMBinary#EMPTY}. */
	protected static final byte TYPE_BINARY_DATA1 = 11;

	/** Dieses Feld speichert die Typkennung für {@link FEMBinary.UniformBinary}. */
	protected static final byte TYPE_BINARY_DATA2 = 12;

	/** Dieses Feld speichert die Typkennung für {@link MappedBinary1}. */
	protected static final byte TYPE_BINARY_ADDR1 = 13;

	/** Dieses Feld speichert die Typkennung für {@link FEMObject} mit reduziertem {@link FEMObject#refValue()}. */
	protected static final byte TYPE_OBJECT_DATA1 = 14;

	/** Dieses Feld speichert die Typkennung für {@link FEMObject} mit reduziertem {@link FEMObject#typeValue()}. */
	protected static final byte TYPE_OBJECT_DATA2 = 15;

	/** Dieses Feld speichert die Typkennung für {@link #getObjectByAddr(long)}. */
	protected static final byte TYPE_OBJECT_ADDR1 = 16;

	/** Dieses Feld speichert die Typkennung für {@link FEMInteger} mit positivem reduziertem {@link FEMInteger#value()}. */
	protected static final byte TYPE_INTEGER_DATA1 = 17;

	/** Dieses Feld speichert die Typkennung für {@link FEMInteger} mit negativem reduziertem {@link FEMInteger#value()}. */
	protected static final byte TYPE_INTEGER_DATA2 = 18;

	/** Dieses Feld speichert die Typkennung für {@link FEMInteger}. */
	protected static final byte TYPE_INTEGER_ADDR1 = 19;

	/** Dieses Feld speichert die Typkennung für {@link FEMDecimal}. */
	protected static final byte TYPE_DECIMAL_ADDR1 = 20;

	/** Dieses Feld speichert die Typkennung für {@link FEMHandler}. */
	protected static final byte TYPE_HANDLER_ADDR1 = 21;

	/** Dieses Feld speichert die Typkennung für {@link FEMDatetime} ohne {@link FEMDatetime#hasDate()}. */
	protected static final byte TYPE_DATETIME_DATA1 = 22;

	/** Dieses Feld speichert die Typkennung für {@link FEMDatetime} ohne {@link FEMDatetime#hasTime()}. */
	protected static final byte TYPE_DATETIME_DATA2 = 23;

	/** Dieses Feld speichert die Typkennung für {@link FEMDatetime}. */
	protected static final byte TYPE_DATETIME_ADDR1 = 24;

	/** Dieses Feld speichert die Typkennung für {@link FEMDuration} mit reduziertem {@link FEMDuration#durationmillisValue()}. */
	protected static final byte TYPE_DURATION_DATA1 = 25;

	/** Dieses Feld speichert die Typkennung für {@link FEMDuration} mit reduziertem {@link FEMDuration#durationmillisValue()}. */
	protected static final byte TYPE_DURATION_DATA2 = 26;

	/** Dieses Feld speichert die Typkennung für {@link FEMDuration}. */
	protected static final byte TYPE_DURATION_ADDR1 = 27;

	/** Dieses Feld speichert die Typkennung für {@link FEMProxy}. */
	protected static final byte TYPE_PROXY_ADDR1 = 28;

	/** Dieses Feld speichert die Typkennung für {@link FEMParam}. */
	protected static final byte TYPE_PARAM_DATA1 = 29;

	/** Dieses Feld speichert die Typkennung für {@link FEMClosure}. */
	protected static final byte TYPE_CLOSURE_ADDR1 = 30;

	/** Dieses Feld speichert die Typkennung für {@link FEMComposite}. */
	protected static final byte TYPE_COMPOSITE_ADDR1 = 31;

	/** Dieses Feld speichert die Adresse des nächsten Speicherbereichs. */
	private long next;

	/** Dieses Feld bildet von einer Funktion auf deren Referenz ab und wird in {@link #put(FEMFunction)} eingesetzt. */
	private final HashMapOL<FEMFunction> reuseMap = new HashMapOL<>();

	/** Dieses Feld bildet von einer Adresse auf einen Platzhalter ab und dient in {@link #getProxyByAddr(long)} de Behandlung der Rekursion. */
	private final HashMapLO<FEMProxy> proxyGetMap = new HashMapLO<>();

	/** Dieses Feld bildet von der Kennung eines Platzhalters auf dessen Adresse ab und dient in {@link #putProxyAsRef(FEMProxy)} de Behandlung der Rekursion. */
	private final HashMapOL<FEMFunction> proxyPutMap = new HashMapOL<>();

	/** Dieses Feld speichert den Puffer, in dem die Zahlenfolgen abgelegt sind. */
	protected final MappedBuffer buffer;

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei.
	 *
	 * @see MappedBuffer#MappedBuffer(File, boolean)
	 * @param file Datei.
	 * @param readonly {@code true}, wenn die Datei nur mit Lesezugriff angebunden werden soll.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public FEMBuffer(final File file, final boolean readonly) throws IOException {
		this.buffer = new MappedBuffer(file, readonly);
		this.buffer.growScale(1);
		final long MAGIC = 0x31454c49464d4546L;
		if (!readonly && (this.buffer.size() == 0)) {
			this.next = 24;
			this.buffer.grow(this.next);
			this.buffer.putLong(0, new long[]{MAGIC, 0, this.next});
		} else {
			if (this.buffer.size() < 24) throw new IllegalArgumentException();
			if (this.buffer.getLong(0) != MAGIC) throw new IllegalArgumentException();
			this.next = this.buffer.getLong(16);
			if (this.next < 24) throw new IllegalArgumentException();
		}
	}

	@Override
	public FEMFunction get() {
		this.buffer.resize();
		return this.get(this.buffer.getLong(8));
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz zurück.
	 *
	 * @param ref Referenz.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMFunction get(final long ref) throws IllegalArgumentException {
		return this.customGet(this.getRefHead(ref), this.getRefBody(ref));
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

	/** Diese Methode gibt die Funktion zu der Referenz zurück, die an der gegebenen Adresse steht, und ist eine Abkürzung für {@link #get(long)
	 * this.get(this.buffer().getLong(addr))}. */
	public FEMFunction getAt(final long addr) throws IllegalArgumentException {
		return this.get(this.buffer.getLong(addr));
	}

	/** Diese Methode gibt die Funktion zu der Referenz zurück, die an der gegebenen Adresse steht, und ist eine Abkürzung für {@link #get(long, Class)
	 * this.get(this.buffer().getLong(addr), clazz)}. */
	public <GResult> GResult getAt(final long addr, final Class<GResult> clazz) throws IllegalArgumentException {
		return this.get(this.buffer.getLong(addr), clazz);
	}

	/** Diese Methode gibt die Funktionen zur gegebene Anzahl an Referenzen im gegebenen Speicherbereich zurück.
	 *
	 * @see #getAt(long)
	 * @param addr Adresse des Speicherbereichs.
	 * @param count Anzahl der Referenzen.
	 * @return Funktionen. */
	public FEMFunction[] getAllAt(final long addr, final int count) throws IllegalArgumentException {
		final FEMFunction[] result = new FEMFunction[count];
		for (int i = 0; i < count; i++) {
			result[i] = this.getAt(addr + (i * 8));
		}
		return result;
	}

	/** Diese Methode gibt die Referenz mit den gegebenen Eigenschaften zurück.
	 *
	 * @param head Kopfdaten als Typkennung (6 Bit).
	 * @param body Rumpfdaten als Adresse oder Nutzdaten (58 Bit).
	 * @return Referenz. */
	protected long getRef(final int head, final long body) {
		return (body << 6) | head;
	}

	/** Diese Methode gibt die Kopfdaten der gegebenen Referenz zurück. */
	protected int getRefHead(final long ref) {
		return (int)(ref & 63);
	}

	/** Diese Methode gibt die Rumpfdaten der gegebenen Referenz zurück. */
	protected long getRefBody(final long ref) {
		return ref >>> 6;
	}

	@Override
	public void set(final FEMFunction value) {
		this.buffer.putLong(8, this.put(value));
	}

	/** Diese Methode fügt die gegebene Funktion in den Puffer ein und gibt die Referenz darauf zurück.
	 *
	 * @param src Funktion.
	 * @return Referenz auf die Funktion.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion nicht angefügt werden kann. */
	public long put(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		synchronized (this.buffer) {
			final Long result = this.reuseMap.get(Objects.notNull(src));
			if (result != null) return result.longValue();
		}
		return this.customPut(src);
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

	/** Diese Methode gibt die {@link #getRef(int, long) Referenz mit den gegebenen Eigenschaften} zurück und speichert diese zur {@link #cleanup()
	 * Wiederverwendung}.
	 *
	 * @param head Kopfdaten, bspw. Typkennung (6 Bit).
	 * @param body Rumpfdaten, bspw. Adresse (58 Bit).
	 * @return Referenz. */
	protected final long putRef(final int head, final long body) {
		final long ref = this.getRef(head, body);
		synchronized (this.buffer) {
			this.reuseMap.put(this.get(ref), new Long(ref));
		}
		return ref;
	}

	/** Diese Methode reserviert einen neuen Speicherbereich mit der gegebenen Größe und gibt seine Adresse zurück. Diese Adresse ist stets ein Vielfaches von
	 * {@code 8}.
	 *
	 * @param size Größe des Speicherbereichs in Byte.
	 * @return Adresse des Speicherbereichs.
	 * @throws IllegalStateException Wenn der Puffer nicht zum schreiben angebunden ist.
	 * @throws IllegalArgumentException Wenn {@code size} ungültig ist. */
	protected final long putData(final long size) throws IllegalStateException, IllegalArgumentException {
		final MappedBuffer buffer = this.buffer;
		synchronized (buffer) {
			final long addr = this.next, next = (addr + size + 7) & -8L;
			if (next < addr) throw new IllegalArgumentException();
			buffer.grow(next);
			buffer.putLong(16, next);
			this.next = next;
			return addr;
		}
	}

	/** Diese Methode gibt die Funktion zu den gegebenen Kopf- und Rumpfdaten zurück.
	 *
	 * @param head Kopfdaten als Typkennung.
	 * @param body Rumpfdaten als Adresse oder Nutzdaten.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die gegebenen Daten ungültig sind. */
	protected FEMFunction customGet(final int head, final long body) {
		switch (head) {
			case TYPE_VOID:
				return FEMVoid.INSTANCE;
			case TYPE_TRUE:
				return FEMBoolean.TRUE;
			case TYPE_FALSE:
				return FEMBoolean.FALSE;
			case TYPE_ARRAY_DATA1:
				return FEMArray.EMPTY;
			case TYPE_ARRAY_ADDR1:
				return this.getArrayByAddr1(body);
			case TYPE_ARRAY_ADDR2:
				return this.getArrayByAddr2(body);
			case TYPE_STRING_DATA1:
				return FEMString.EMPTY;
			case TYPE_STRING_DATA2:
				return this.getStringByData(body);
			case TYPE_STRING_ADDR1:
				return this.getStringByAddr1(body);
			case TYPE_STRING_ADDR2:
				return this.getStringByAddr2(body);
			case TYPE_STRING_ADDR3:
				return this.getStringByAddr3(body);
			case TYPE_BINARY_DATA1:
				return FEMBinary.EMPTY;
			case TYPE_BINARY_DATA2:
				return this.getBinaryByData(body);
			case TYPE_BINARY_ADDR1:
				return this.getBinaryByAddr(body);
			case TYPE_OBJECT_DATA1:
				return this.getObjectByData1(body);
			case TYPE_OBJECT_DATA2:
				return this.getObjectByData2(body);
			case TYPE_OBJECT_ADDR1:
				return this.getObjectByAddr(body);
			case TYPE_INTEGER_DATA1:
				return this.getIntegerByData1(body);
			case TYPE_INTEGER_DATA2:
				return this.getIntegerByData2(body);
			case TYPE_INTEGER_ADDR1:
				return this.getIntegerByAddr(body);
			case TYPE_DECIMAL_ADDR1:
				return this.getDecimalByAddr(body);
			case TYPE_HANDLER_ADDR1:
				return this.getHandlerByAddr(body);
			case TYPE_DATETIME_DATA1:
				return this.getDatetimeByData1(body);
			case TYPE_DATETIME_DATA2:
				return this.getDatetimeByData2(body);
			case TYPE_DATETIME_ADDR1:
				return this.getDatetimeByAddr(body);
			case TYPE_DURATION_DATA1:
				return this.getDurationByData1(body);
			case TYPE_DURATION_DATA2:
				return this.getDurationByData2(body);
			case TYPE_DURATION_ADDR1:
				return this.getDurationByAddr(body);
			case TYPE_PROXY_ADDR1:
				return this.getProxyByAddr(body);
			case TYPE_PARAM_DATA1:
				return this.getParamByData(body);
			case TYPE_CLOSURE_ADDR1:
				return this.getClosureByAddr(body);
			case TYPE_COMPOSITE_ADDR1:
				return this.getCompositeByAddr(body);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Referenz zur gegebenen Funktion zurück. Dazu erfolgt auf dem Typ der Funktion eine Fallunterscheidung.
	 *
	 * @param src Funktion.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn {@link #putData(long)} diese auslöst.
	 * @throws IllegalArgumentException Wenn die Funktion nicht ausgelagert werden kann. */
	protected long customPut(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (src instanceof FEMVoid) return this.putVoidAsRef();
		if (src instanceof FEMBoolean) return this.putBooleanAsRef((FEMBoolean)src);
		if (src instanceof FEMFuture) return this.putFutureAsRef((FEMFuture)src);
		if (src instanceof FEMArray) return this.putArrayAsRef((FEMArray)src);
		if (src instanceof FEMString) return this.putStringAsRef((FEMString)src);
		if (src instanceof FEMBinary) return this.putBinaryAsRef((FEMBinary)src);
		if (src instanceof FEMObject) return this.putObjectAsRef((FEMObject)src);
		if (src instanceof FEMInteger) return this.putIntegerAsRef((FEMInteger)src);
		if (src instanceof FEMDecimal) return this.putDecimalAsRef((FEMDecimal)src);
		if (src instanceof FEMHandler) return this.putHandlerAsRef((FEMHandler)src);
		if (src instanceof FEMDatetime) return this.putDatetimeAsRef((FEMDatetime)src);
		if (src instanceof FEMDuration) return this.putDurationAsRef((FEMDuration)src);
		if (src instanceof FEMProxy) return this.putProxyAsRef((FEMProxy)src);
		if (src instanceof FEMParam) return this.putParamAsRef((FEMParam)src);
		if (src instanceof FEMClosure) return this.putClosureAsRef((FEMClosure)src);
		if (src instanceof FEMComposite) return this.putCompositeAsRef((FEMComposite)src);
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Referenz auf {@link FEMVoid#INSTANCE} zurück. */
	protected long putVoidAsRef() {
		return this.getRef(FEMBuffer.TYPE_VOID, 0);
	}

	/** Diese Methode fügt den gegebenen Ergebniswert in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putFutureAsRef(final FEMFuture src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this.put(src.result());
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Wertliste zurück. Die Struktur des Speicherbereichs ist
	 * {@code (length: int, hash: int, valueRef: long[length])}. Sie beginnt mit dem {@link FEMArray#hashCode() Streuwert} und endet mit den über
	 * {@link #putAll(FEMFunction...) Referenzen} der Elemente der Wertliste. */
	protected FEMArray getArrayByAddr1(final long addr) throws IllegalArgumentException {
		return new MappedArray1(this.buffer.getInt(addr), this, addr + 8);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Wertliste zurück. Die Struktur des Speicherbereichs ist
	 * {@code (length: int, hash: int, valueRef: long[length], count: int, range: int[count-1], index: int[length])}. Sie beginnt mit dem
	 * {@link FEMArray#hashCode() Streuwert} und {@link FEMArray#length() Länge} der Wertliste, gefolgt von den {@link #putAll(FEMFunction...) Referenzen} der
	 * Elemente der Wertliste sowie der {@link CompactArray3#table Streuwerttabelle} zur beschleunigten Einzelwertsuche. */
	protected FEMArray getArrayByAddr2(final long addr) throws IllegalArgumentException {
		return new MappedArray2(this.buffer.getInt(addr), this, addr + 8);
	}

	/** Diese Methode fügt die gegebene Wertliste in den Puffer ein und gibt die Adresse darauf zurück. Eine über {@link FEMArray#compact(boolean)} indizierte
	 * Wertliste wird mit der Indizierung kodiert. */
	protected long putArrayAsRef(final FEMArray src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.length();
		if (length == 0) return this.getRef(FEMBuffer.TYPE_ARRAY_DATA1, 0);
		final int hash = src.hashCode();
		if (src instanceof CompactArray3) {
			final int[] table = ((CompactArray3)src).table;
			final long addr = this.putData((length * 8L) + (table.length * 4L) + 8);
			this.buffer.putInt(addr, new int[]{length, hash});
			this.buffer.putLong(addr + 8, this.putAll(src.value()));
			this.buffer.putInt(addr + (length * 8L) + 8, table);
			return this.putRef(FEMBuffer.TYPE_ARRAY_ADDR2, addr);
		} else {
			final long addr = this.putData((length * 8L) + 8);
			this.buffer.putInt(addr, new int[]{length, hash});
			this.buffer.putLong(addr + 8, this.putAll(src.value()));
			return this.putRef(FEMBuffer.TYPE_ARRAY_ADDR1, addr);
		}
	}

	/** Diese Methode gibt die Zeichenkette mit den gegebenen Daten ({@code item: 31}) zurück. */
	protected FEMString getStringByData(final long data) {
		return FEMString.from(1, (int)data);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code byte}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (length: int, hash: int, item: byte[length])}. */
	protected FEMString getStringByAddr1(final long addr) throws IllegalArgumentException {
		return new MappedString1(this.buffer.getInt(addr), this.buffer, addr + 8);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code short}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (length: int, hash: int, item: short[length])}. */
	protected FEMString getStringByAddr2(final long addr) throws IllegalArgumentException {
		return new MappedString2(this.buffer.getInt(addr), this.buffer, addr + 8);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code int}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (length: int, hash: int, item: int[length])}. */
	protected FEMString getStringByAddr3(final long addr) throws IllegalArgumentException {
		return new MappedString3(this.buffer.getInt(addr), this.buffer, addr + 8);
	}

	/** Diese Methode fügt die gegebene Zeichenkette in den Puffer ein und gibt die Adresse darauf zurück. Dazu werden die {@link FEMString#compact()
	 * kompaktierten} Formen der Zeichenkette analysiert und entsprechend als {@code byte}, {@code short} oder {@code int}-Codepoints gespeichert. */
	protected long putStringAsRef(FEMString src) throws NullPointerException, IllegalStateException {
		src = src.compact();
		final int length = src.length();
		if (length == 0) return this.getRef(FEMBuffer.TYPE_STRING_DATA1, 0);
		if (length == 1) return this.getRef(FEMBuffer.TYPE_STRING_DATA2, src.get(0));
		final int hash = src.hashCode();
		if (src instanceof FEMString.CompactStringINT8) {
			final long addr = this.putData(length + 8);
			this.buffer.putInt(addr, new int[]{length, hash});
			this.buffer.put(addr + 8, src.toBytes());
			return this.putRef(FEMBuffer.TYPE_STRING_ADDR1, addr);
		} else if (src instanceof FEMString.CompactStringINT16) {
			final long addr = this.putData((length * 2L) + 8);
			this.buffer.putInt(addr, new int[]{length, hash});
			this.buffer.putShort(addr + 8, src.toShorts());
			return this.putRef(FEMBuffer.TYPE_STRING_ADDR2, addr);
		} else {
			final long addr = this.putData((length * 4L) + 8);
			this.buffer.putInt(addr, new int[]{length, hash});
			this.buffer.putInt(addr + 8, src.toInts());
			return this.putRef(FEMBuffer.TYPE_STRING_ADDR3, addr);
		}
	}

	/** Diese Methode gibt die Bytefolge mit den gegebenen Daten ({@code item: 8}) zurück. */
	protected FEMBinary getBinaryByData(final long data) {
		return FEMBinary.from(1, (byte)data);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich ({@code length: int, hash: int, item: byte[length]}) enthaltene Bytefolge zurück. */
	protected FEMBinary getBinaryByAddr(final long addr) throws IllegalArgumentException {
		return new MappedBinary1(this.buffer.getInt(addr), this.buffer, addr + 8);
	}

	/** Diese Methode fügt die gegebene Bytefolge in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putBinaryAsRef(final FEMBinary src) throws NullPointerException, IllegalStateException {
		final int length = src.length();
		if (length == 0) return this.getRef(FEMBuffer.TYPE_BINARY_DATA1, 0);
		if (length == 1) return this.getRef(FEMBuffer.TYPE_BINARY_DATA2, src.get(0) & 0xFF);
		final long addr = this.putData(length + 8);
		this.buffer.putInt(addr, new int[]{length, src.hashCode()});
		this.buffer.put(addr + 8, src.value());
		return this.putRef(FEMBuffer.TYPE_BINARY_ADDR1, addr);
	}

	/** Diese Methode gibt die Dezimalzahl mit den gegebenen Daten ({@code +value: 58}) zurück. */
	protected FEMInteger getIntegerByData1(final long data) throws IllegalArgumentException {
		return new FEMInteger(+data);
	}

	/** Diese Methode gibt die Dezimalzahl mit den gegebenen Daten ({@code -value: 58}) zurück. */
	protected FEMInteger getIntegerByData2(final long data) throws IllegalArgumentException {
		return new FEMInteger(-data);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich ({@code value: long}) enthaltene Dezimalzahl zurück. */
	protected FEMInteger getIntegerByAddr(final long addr) throws IllegalArgumentException {
		return new FEMInteger(this.buffer.getLong(addr));
	}

	/** Diese Methode fügt die gegebene Dezimalzahl in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putIntegerAsRef(final FEMInteger src) throws NullPointerException, IllegalStateException {
		final long data1 = src.value(), data2 = -data1;
		if ((0 <= data1) && (data1 <= 0x03FFFFFFFFFFFFFFL)) return this.getRef(FEMBuffer.TYPE_INTEGER_DATA1, data1);
		if ((0 <= data2) && (data2 <= 0x03FFFFFFFFFFFFFFL)) return this.getRef(FEMBuffer.TYPE_INTEGER_DATA2, data2);
		final long addr = this.putData(8);
		this.buffer.putLong(addr, data1);
		return this.putRef(FEMBuffer.TYPE_INTEGER_ADDR1, addr);
	}

	/** Diese Methode gibt die Referenz auf den gegebenen Wahrheitswert zurück. */
	protected long putBooleanAsRef(final FEMBoolean src) {
		return this.getRef(src.value() ? FEMBuffer.TYPE_TRUE : FEMBuffer.TYPE_FALSE, 0);
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich ({@code value: double}) enthaltenen Dezimalbruch zurück. */
	protected FEMDecimal getDecimalByAddr(final long addr) throws IllegalArgumentException {
		return new FEMDecimal(this.buffer.getDouble(addr));
	}

	/** Diese Methode fügt den gegebenen Dezimalbruch in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putDecimalAsRef(final FEMDecimal src) throws NullPointerException, IllegalStateException {
		final long addr = this.putData(8);
		this.buffer.putDouble(addr, src.value());
		return this.putRef(FEMBuffer.TYPE_DECIMAL_ADDR1, addr);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich ({@code value: long}) enthaltene Zeitspanne zurück. */
	protected FEMDuration getDurationByAddr(final long addr) throws IllegalArgumentException {
		return new FEMDuration(this.buffer.getLong(addr));
	}

	/** Diese Methode gibt die Zeitspanne mit den gegebenen Daten
	 * ({@code years: 8, sign: 1, hours: 5, minutes: 6, seconds: 6, days: 18, months: 4, milliseconds: 10}) zurück. */
	protected FEMDuration getDurationByData1(final long data) throws IllegalArgumentException {
		return new FEMDuration(data);
	}

	/** Diese Methode gibt die Zeitspanne mit den gegebenen Daten
	 * ({@code years: 14, sign: 1, hours: 5, minutes: 6, seconds: 6, days: 12, months: 4, milliseconds: 10}) zurück. */
	protected FEMDuration getDurationByData2(final long data) throws IllegalArgumentException {
		return new FEMDuration((data & 0x03FFFFFF) | ((data << 6) & 0xFFFFFFFF00000000L));
	}

	/** Diese Methode fügt den gegebenen Zeitspanne in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putDurationAsRef(final FEMDuration src) throws NullPointerException, IllegalStateException {
		final long data = src.value();
		if (src.yearsValue() <= 0xFF) return this.getRef(FEMBuffer.TYPE_DURATION_DATA1, data);
		if (src.daysValue() <= 0x0FFF) return this.getRef(FEMBuffer.TYPE_DURATION_DATA2, (data & 0x03FFFFFF) | ((data & 0xFFFFFFFF00000000L) >>> 6));
		final long addr = this.putData(8);
		this.buffer.putLong(addr, data);
		return this.putRef(FEMBuffer.TYPE_DURATION_ADDR1, addr);
	}

	/** Diese Methode gibt die Zeitangabe mit den gegebenen Daten
	 * ({@code minute: 6, second: 6, 0: 1, hasTime: 1, hasZone: 1, zone: 11, 0: 5, hour: 5, millisecond: 10}) zurück. */
	protected FEMDatetime getDatetimeByData1(final long data) throws IllegalArgumentException {
		return new FEMDatetime(data);
	}

	/** Diese Methode gibt die Zeitangabe mit den gegebenen Daten ({@code year: 14, month: 4, 0: 12, hasDate: 1, 0: 1, hasZone: 1, zone: 11, date: 5, 0: 9})
	 * zurück. */
	protected FEMDatetime getDatetimeByData2(final long data) throws IllegalArgumentException {
		return new FEMDatetime(data << 6);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich ({@code value: long}) enthaltene Zeitangabe zurück. */
	protected FEMDatetime getDatetimeByAddr(final long addr) throws IllegalArgumentException {
		return new FEMDatetime(this.buffer.getLong(addr));
	}

	/** Diese Methode fügt die gegebene Zeitangabe in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putDatetimeAsRef(final FEMDatetime src) throws NullPointerException, IllegalStateException {
		final long data = src.value();
		if (!src.hasDate()) return this.getRef(FEMBuffer.TYPE_DATETIME_DATA1, data);
		if (!src.hasTime()) return this.getRef(FEMBuffer.TYPE_DATETIME_DATA2, data >>> 6);
		final long addr = this.putData(8);
		this.buffer.putLong(addr, data);
		return this.putRef(FEMBuffer.TYPE_DATETIME_ADDR1, addr);
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich ({@code functionRef: long}) enthaltenen Funktionszeiger zurück. */
	protected FEMHandler getHandlerByAddr(final long addr) throws IllegalArgumentException {
		return new FEMHandler(this.getAt(addr));
	}

	/** Diese Methode fügt den gegebenen Funktionszeiger in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putHandlerAsRef(final FEMHandler src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final long addr = this.putData(8);
		this.buffer.putLong(addr, this.put(src.value));
		return this.putRef(FEMBuffer.TYPE_HANDLER_ADDR1, addr);
	}

	/** Diese Methode gibt die Objektreferenz mit den gegebenen Daten ({@code ref: 26, type: 16, owner: 16}) zurück. */
	protected FEMObject getObjectByData1(final long data) throws IllegalArgumentException {
		return new FEMObject(data);
	}

	/** Diese Methode gibt die Objektreferenz mit den gegebenen Daten ({@code ref: 31, type: 10, owner: 16}) zurück. */
	protected FEMObject getObjectByData2(final long data) throws IllegalArgumentException {
		return new FEMObject((data & 0x03FFFFFFL) | ((data << 6) & 0xFFFFFFFF00000000L));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich ({@code value: long}) enthaltene Objektreferenz zurück. */
	protected FEMObject getObjectByAddr(final long addr) throws IllegalArgumentException {
		return new FEMObject(this.buffer.getLong(addr));
	}

	/** Diese Methode fügt den gegebenen Objektreferenz in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putObjectAsRef(final FEMObject src) throws NullPointerException, IllegalStateException {
		final long data = src.value();
		if (src.refValue() <= 0x03FFFFFF) return this.getRef(FEMBuffer.TYPE_OBJECT_DATA1, data);
		if (src.typeValue() <= 0x03FF) return this.getRef(FEMBuffer.TYPE_OBJECT_DATA2, (data & 0x03FFFFFFL) | ((data & 0xFFFFFFFF00000000L) >>> 6));
		final long addr = this.putData(8);
		this.buffer.putLong(addr, data);
		return this.putRef(FEMBuffer.TYPE_OBJECT_ADDR1, addr);
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich ({@code idRef: long, nameRef: long, targetRef: long}) enthaltenen Platzhalter zurück. */
	protected FEMProxy getProxyByAddr(final long addr) throws IllegalArgumentException {
		FEMProxy result;
		synchronized (this.buffer) {
			final Long key = new Long(addr);
			result = this.proxyGetMap.get(key);
			if (result != null) return result;
			final FEMValue id = this.getAt(addr, FEMValue.class);
			final FEMString name = this.getAt(addr + 8, FEMString.class);
			result = new FEMProxy(id, name, null);
			this.proxyGetMap.put(key, result);
		}
		final long ref = this.buffer.getLong(addr + 16);
		if (ref == FEMBuffer.TYPE_PROXY_ADDR1) return result;
		result.set(this.get(ref));
		return result;
	}

	/** Diese Methode fügt den gegebenen Platzhalter in den Puffer ein und gibt die Referenz darauf zurück. Das {@link FEMProxy#get() Ziel} des Platzhalters wird
	 * hierbei nur dann ebenfalls eingefügt, wenn es nicht {@code null} ist. */
	protected long putProxyAsRef(final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		final long addr;
		synchronized (this.buffer) {
			final Long key = this.proxyPutMap.get(src.id());
			if (key != null) {
				addr = key.longValue();
				long ref = this.buffer.getLong(addr + 16);
				final FEMFunction fun = src.get();
				if (ref == FEMBuffer.TYPE_PROXY_ADDR1) {
					if (fun != null) {
						ref = this.put(fun);
						this.buffer.putLong(addr + 16, ref);
						final FEMProxy prx = this.proxyGetMap.get(key);
						if (prx != null) {
							prx.set(this.get(ref));
						}
					}
				} else {
					if (fun == null) {
						this.buffer.putLong(addr + 16, FEMBuffer.TYPE_PROXY_ADDR1);
						final FEMProxy prx = this.proxyGetMap.get(key);
						if (prx != null) {
							prx.set(null);
						}
					}
				}
				return this.getRef(FEMBuffer.TYPE_PROXY_ADDR1, addr);
			}
			final long ref = this.put(src.id());
			addr = this.putData(24);
			this.buffer.putLong(addr, ref);
			this.proxyPutMap.put(this.get(ref), new Long(addr));
		}
		this.buffer.putLong(addr + 8, this.put(src.name()));
		final FEMFunction fun = src.get();
		this.buffer.putLong(addr + 16, fun != null ? this.put(fun) : FEMBuffer.TYPE_PROXY_ADDR1);
		return this.getRef(FEMBuffer.TYPE_PROXY_ADDR1, addr);
	}

	/** Diese Methode gibt die Parameterfunktion zum gegebenen Index zurück. */
	protected FEMParam getParamByData(final long data) throws IllegalArgumentException {
		return FEMParam.from((int)data);
	}

	/** Diese Methode fügt die gegebene Parameterfunktion in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putParamAsRef(final FEMParam src) throws NullPointerException, IllegalStateException {
		return this.getRef(FEMBuffer.TYPE_PARAM_DATA1, src.index());
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich ({@code targetRef: long}) enthaltene Funktionsbindung zurück. */
	protected FEMClosure getClosureByAddr(final long addr) throws IllegalArgumentException {
		return new FEMClosure(this.getAt(addr));
	}

	/** Diese Methode fügt die gegebene Funktionsbindung in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putClosureAsRef(final FEMClosure src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final long addr = this.putData(8);
		this.buffer.putLong(addr, this.put(src.target));
		return this.putRef(FEMBuffer.TYPE_CLOSURE_ADDR1, addr);
	}

	/** Diese Methode gibt dden im gegebenen Speicherbereich ({@code count: int, concat: int, targetRef: long, paramRef: long[count]}) enthaltenen Funktionsaufruf
	 * zurück. */
	protected FEMFunction getCompositeByAddr(final long addr) throws IllegalArgumentException {
		return FEMComposite.from(this.buffer.getInt(addr + 4) != 0, this.getAt(addr + 8), this.getAllAt(addr + 16, this.buffer.getInt(addr)));
	}

	/** Diese Methode fügt den gegebenen Funktionsaufruf in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putCompositeAsRef(final FEMComposite src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int count = src.params.length;
		final long addr = this.putData((count * 8L) + 16L);
		this.buffer.putInt(addr, new int[]{count, src.concat() ? -1 : 0});
		this.buffer.putLong(addr + 8, this.put(src.target));
		this.buffer.putLong(addr + 16, this.putAll(src.params));
		return this.putRef(FEMBuffer.TYPE_COMPOSITE_ADDR1, addr);
	}

	@Override
	public long emu() {
		synchronized (this.buffer) {
			return EMU.fromObject(this) + EMU.fromAll(this.buffer, this.reuseMap, this.proxyGetMap, this.proxyPutMap) + EMU.fromAll(this.reuseMap.keySet())
				+ EMU.fromAll(this.proxyGetMap.values()) + EMU.fromAll(this.proxyPutMap.keySet());
		}
	}

	/** Diese Methode gibt den {@link MappedBuffer Dateipuffer} zurück, in welchen die Funktionen ausgelagert sind.
	 *
	 * @return Dateipuffer. */
	public MappedBuffer buffer() {
		return this.buffer;
	}

	/** Diese Methode leert den {@link MappedBuffer Dateipuffer} und entfernt damit alle bisher ausgelagerten Funktionen. */
	public void reset() throws IllegalStateException {
		if (this.buffer.isReadonly()) throw new IllegalStateException();
		synchronized (this.buffer) {
			this.cleanup();
			this.buffer.putLong(16, 0);
			this.buffer.putLong(24, this.next = 24);
		}
	}

	/** Diese Methode leert den Puffer zur Wiederverwendung der Referenzen der verwalteten Funktionen sowie die zur Behandlung der Rekursion bei Platzhaltern. */
	public void cleanup() {
		synchronized (this.buffer) {
			this.reuseMap.clear();
			this.proxyGetMap.clear();
			this.proxyPutMap.clear();
			this.reuseMap.compact();
			this.proxyGetMap.compact();
			this.proxyPutMap.compact();
		}
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.buffer.file().toString(),
			"file=" + Integers.printSize(this.buffer.file().length()) + " used=" + Integers.printSize(this.next) + " heap=" + Integers.printSize(this.emu()));
	}

}
