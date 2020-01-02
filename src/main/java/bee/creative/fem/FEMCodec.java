package bee.creative.fem;

import bee.creative.bind.Property;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.fem.FEMArray.CompactArray3;
import bee.creative.fem.FEMFunction.ClosureFunction;
import bee.creative.fem.FEMFunction.CompositeFunction;
import bee.creative.fem.FEMFunction.ConcatFunction;
import bee.creative.iam.IAMArray;
import bee.creative.io.MappedBuffer;
import bee.creative.lang.Integers;
import bee.creative.lang.Objects;
import bee.creative.mmi.MMIArray;
import bee.creative.mmi.MMIArrayL;
import bee.creative.util.HashMap2;

/** Diese Klasse implementiert ein Objekt zur Kodierung und Dekodierung von {@link FEMFunction Funktionen} in {@link IAMArray Zahlenlisten}, die in einen
 * {@link MappedBuffer Dateipuffer} ausgelagert sind.
 * <p>
 * HEADER <br>
 * 8 Byte MAGIC NUMBER; 4 Byte size, 4 byte value ref.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMCodec implements Property<FEMFunction>, Emuable {

	/** Diese Klasse implementiert eine {@link FEMArray Wertliste}, deren Elemente als {@link IAMArray Zahlenfolge} aus Referenzen gegeben sind und in
	 * {@link #customGet(int)} über einen gegebenen {@link FEMCodec} in Werte {@link FEMCodec#getValue(int) übersetzt} werden. */
	protected static class IndexArray extends FEMArray {

		/** Dieses Feld speichert den {@link FEMCodec} zur {@link FEMCodec#getValue(int) Übersetzung} der Referenzen aus {@link #items}. */
		public final FEMCodec index;

		/** Dieses Feld speichert die Zahlenfolge mit den Referenzen. Ihre Struktur wird in {@link FEMCodec#toArrayValue(IAMArray)} beschrieben. */
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

		// TODO offset hier oder in array?
		public final int offset;

		ArrayBinary(final IAMArray array) throws NullPointerException, IllegalArgumentException {
			this(array, 4, array.length() - 4);
			this.hash = Integers.toInt(array.get(3), array.get(2), array.get(1), array.get(0));
		}

		public ArrayBinary(final IAMArray array, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
			super(length);
			this.array = Objects.notNull(array);
			this.offset = offset;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return (byte)this.array.get(this.offset + index);
		}

		@Override
		protected FEMBinary customSection(final int offset, final int length) {
			return new ArrayBinary(this.array, this.offset + offset, length);
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ArrayString extends FEMString {

		public final IAMArray array;

		// TODO offset hier oder in array?
		public final int offset;

		ArrayString(final IAMArray array) throws NullPointerException, IllegalArgumentException {
			this(array, 1, array.length() - 2);
			this.hash = array.get(0);
		}

		public ArrayString(final IAMArray array, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
			super(length);
			this.array = Objects.notNull(array);
			this.offset = offset;
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.array.get(this.offset + index);
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new ArrayString(this.array, this.offset + offset, length);
		}

		@Override
		public FEMString compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ArrayStringINT8 extends ArrayString {

		ArrayStringINT8(final IAMArray array) throws NullPointerException, IllegalArgumentException {
			this(array, 4, array.length() - 5, Integers.toInt(array.get(3), array.get(2), array.get(1), array.get(0)));
		}

		public ArrayStringINT8(final IAMArray array, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
			super(array, offset, length);
		}

		public ArrayStringINT8(final IAMArray array, final int offset, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(array, offset, length);
			this.hash = hash;
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.array.get(this.offset + index) & 0xFF;
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new ArrayStringINT8(this.array, this.offset + offset, length);
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ArrayStringINT16 extends ArrayString {

		ArrayStringINT16(final IAMArray array) throws NullPointerException, IllegalArgumentException {
			this(array, 2, array.length() - 3, Integers.toInt(array.get(1), array.get(0)));
		}

		public ArrayStringINT16(final IAMArray array, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
			super(array, offset, length);
		}

		public ArrayStringINT16(final IAMArray array, final int offset, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(array, offset, length);
			this.hash = hash;
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.array.get(this.offset + index) & 0xFFFF;
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new ArrayStringINT16(this.array, this.offset + offset, length);
		}

	}

	protected static final int MAGIC_NUMBER = 0x6005BABE;

	/** Dieses Feld speichert die Typkennung für {@link FEMVoid#INSTANCE}. */
	protected static final int TYPE_VOID = 0;

	/** Dieses Feld speichert die Typkennung für {@link FEMBoolean#TRUE}. */
	protected static final int TYPE_TRUE = 1;

	/** Dieses Feld speichert die Typkennung für {@link #putFalseValue()}. */
	protected static final int TYPE_FALSE = 2;

	/** Dieses Feld speichert die Typkennung für {@link #putArrayValue(FEMArray)}. */
	protected static final int TYPE_ARRAY_LIST = 3;

	/** Dieses Feld speichert die Typkennung für {@link #putArrayValue(FEMArray)}. */
	protected static final int TYPE_ARRAY_HASH = 3;

	/** Dieses Feld speichert die Typkennung für {@link #putStringValue(FEMString)}. */
	protected static final int TYPE_STRING_INT8 = 4;

	protected static final int TYPE_STRING_INT16 = 4;

	protected static final int TYPE_STRING_INT32 = 4;

	/** Dieses Feld speichert die Typkennung für {@link #putBinaryValue(FEMBinary)}. */
	protected static final int TYPE_BINARY_VALUE = 5;

	/** Dieses Feld speichert die Typkennung für {@link #putIntegerValue(FEMInteger)}. */
	protected static final int TYPE_INTEGER = 6;

	/** Dieses Feld speichert die Typkennung für {@link #putDecimalValue(FEMDecimal)}. */
	protected static final int TYPE_DECIMAL = 7;

	/** Dieses Feld speichert die Typkennung für {@link #putDurationValue(FEMDuration)}. */
	protected static final int TYPE_DURATION = 8;

	/** Dieses Feld speichert die Typkennung für {@link #putDatetimeValue(FEMDatetime)}. */
	protected static final int TYPE_DATETIME = 9;

	/** Dieses Feld speichert die Typkennung für {@link #putHandlerValue(FEMHandler)}. */
	protected static final int TYPE_HANDLER = 10;

	/** Dieses Feld speichert die Typkennung für {@link #putObjectValue(FEMObject)}. */
	protected static final int TYPE_OBJECT = 11;

	/** Dieses Feld speichert die Typkennung für {@link #putProxyFunction(FEMProxy)}. */
	protected static final int TYPE_PROXY_FUNCTION = 12;

	/** Dieses Feld speichert die Typkennung für {@link #putParamFunction(FEMParam)}. */
	protected static final int TYPE_PARAM_FUNCTION = 13;

	/** Dieses Feld speichert die Typkennung für {@link #putConcatFunction(ConcatFunction)}. */
	protected static final int TYPE_CONCAT_FUNCTION = 14;

	/** Dieses Feld speichert die Typkennung für {@link #putClosureFunction(ClosureFunction)}. */
	protected static final int TYPE_CLOSURE_FUNCTION = 15;

	/** Dieses Feld speichert die Typkennung für {@link #putCompositeFunction(CompositeFunction)}. */
	protected static final int TYPE_COMPOSITE_FUNCTION = 16;

	/** Dieses Feld speichert die Referenz der nächsten {@link #putImpl(IAMArray) angefügten} Zahlenfolge. */
	private int nextRef;

	/** Dieses Feld speichert die Referenz auf die als {@link #set(FEMFunction) Wurzel gesetzte} Funktion. */
	private int rootRef;

	/** Dieses Feld speichert den Puffer, in dem die Yahlenfolgen abgelegt sind. */
	private MappedBuffer buffer;

	private boolean reuseEnabled = true;

	/** Dieses Feld bildet von einer Zahlenfolge auf deren Referenz ab und wird zusammen mit {@link #reuseEnabled} inn {@link #putImpl(IAMArray)} eingesetzt. */
	private HashMap2<IAMArray, Integer> reuseMapping;

	private final boolean cacheEnabled = true;

	@Override
	public final FEMFunction get() {
		return this.getFunction(this.rootRef);
	}

	@Override
	public final void set(final FEMFunction value) {
		this.checkPut();
		this.buffer.putInt(8, this.rootRef = this.putFunction(value));
	}

	/** Diese Methode gibt den {@link MappedBuffer Puffer} zurück, in welchem die kodierten {@link FEMFunction Funktionen} abgelegt sind.
	 *
	 * @return Puffer. */
	public final MappedBuffer getBuffer() {
		return this.buffer;
	}

	/** Diese Methode setzt die Aktivierung der Wiederverwendung von Zahlenfolgen und gibt {@code this} zurück. Wenn diese aktiv ist, werden durch
	 * {@link #putImpl(IAMArray)} keine redundanten Zahlenfolgen in den {@link #getBuffer() Puffer} übertragen.
	 *
	 * @param enabled Aktivierung der Wiederverwendung.
	 * @return {@code this}. */
	public FEMCodec useReuse(final boolean enabled) {
		this.reuseEnabled = enabled;
		return this;
	}

	public FEMCodec useCache(final boolean enabled) {
		this.cacheEnabled = enabled;
		return this;
	}

	/** Diese Methode bestückt diesen {@link FEMCodec} zum Lesen des gegebenen {@link MappedBuffer Puffers} und gibt {@code this} zurück.
	 *
	 * @param src Datenquelle.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist. */
	public FEMCodec useSource(final MappedBuffer src) throws NullPointerException {
		this.buffer = Objects.notNull(src);
		this.reuseMapping = null;

		this.nextRef = src.getInt(4);
		this.rootRef = src.getInt(8);
		// TODO check MAGIC-NUMBER
		return this;
	}

	/** Diese Methode bestückt diesen {@link FEMCodec} zum Lesen und schreiben des gegebenen {@link MappedBuffer Puffers} und gibt {@code this} zurück.
	 *
	 * @return {@code this}. */
	public FEMCodec useTarget(final MappedBuffer target) throws NullPointerException, IllegalArgumentException {
		if (target.isReadonly()) throw new IllegalArgumentException();
		this.reuseMapping = new HashMap2<>();
		this.buffer = target;
		this.nextRef = 1;
		// TODO check MAGIC-NUMBER + setup root/next
		return this;
	}

	/** Diese Methode gibt die Referenz zur gegebenen {@link #getAddrImpl(int) Adresse} zurück.
	 *
	 * @param addr Adresse.
	 * @return Referenz. */
	private final int getRefImpl(final long addr) {
		return (int)(addr >>> 4);
	}

	/** Diese Methode gibt die Adresse des Speicherbereichs der {@link MMIArray Zahlenfolge} zur gegebenen {@link #getRefImpl(long) Refernez} zurück.<ber> Die
	 * Kopfdaten dieses Speicherbereichs stehten in den vier Byte davor und nennen Länge sowie Kodierung der Zahlenfolge. Bei den Kodierungen werden nur
	 * {@link IAMArray#MODE_INT8}, {@link IAMArray#MODE_INT16} und {@link IAMArray#MODE_INT32} unterschieden.
	 * <p>
	 * TODO Die Kopfdaten der im {@link #buffer} angebundenen Datei besteht dabei aus drei {@code int}-Werten: der MAGIC-NUMBER, der {@link #nextRef NEXT-REF} und
	 * der {@link #rootRef ROOT-REF}.
	 *
	 * @param ref Referenz (1..2147483647).
	 * @return Adresse. */
	private final long getAddrImpl(final int ref) {
		return (long)ref << 4;
	}

	private final int putImpl(final IAMArray src) throws NullPointerException, IllegalStateException {
		final int result = this.nextRef, length = src.length(), size = MMIArray.size(src.mode());
		final long address = this.getAddrImpl(result), capacity = (19 + address + (size * (long)length)) & -15;
		try {
			final MappedBuffer buffer = this.buffer;
			buffer.grow(capacity);
			buffer.putInt(address - 4, (length << 2) | (size >> 1));
			buffer.putArray(address, src);
			this.nextRef = this.getRefImpl(capacity);
			return result;
		} catch (final IllegalStateException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode gibt die {@link MMIArray Zahlenfolge} zur gegebenen {@link #putImpl(IAMArray) Referenz} zurück.
	 *
	 * @param ref Referenz.
	 * @return Zahlenfolge.
	 * @throws IllegalArgumentException Wenn {@link MappedBuffer#getInt(long)} bzw. {@link MappedBuffer#getArray(long, int, int)} eine ausnahme auslöst. */
	protected final MMIArrayL getData(final int ref) throws IllegalArgumentException {
		try {
			final long address = this.getAddrImpl(ref);
			final int head = this.buffer.getInt(address - 4);
			return this.buffer.getArray(address, head >>> 2, head & 3);
		} catch (final IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	protected final int putData(final IAMArray src) throws NullPointerException, IllegalStateException {
		this.checkPut();
		if (!this.reuseEnabled) return this.putImpl(src);
		final Integer value = this.reuseMapping.get(src);
		if (value != null) return value.intValue();
		final int result = this.putImpl(src);
		final MMIArrayL data = this.getData(result);
		this.reuseMapping.put(data, new Integer(result));
		return result;
	}

	void checkPut() throws IllegalStateException {
		if (this.reuseMapping == null) throw new IllegalStateException();
	}

	public final FEMValue getValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMValue.class);
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz zurück. Wenn deren Typkennung unbekannt ist, wird {@link FEMVoid#INSTANCE} geliefert.
	 *
	 * @param ref Referenz.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMFunction getFunction(final int ref) throws IllegalArgumentException {
		long addr = this.getAddrImpl(ref);
		final int type = this.buffer.getInt(addr);
		addr += 4;
		final int size = this.buffer.getInt(addr);
		addr += 4;
		return this.get(type, size, addr);
	}

	/** Diese Methode gibt die Funktion zur gegebenen {@link #getRefImpl(int, int) Funktionsreferenz} zurück. Wenn deren Typkennung unbekannt ist, wird
	 * {@link FEMVoid#INSTANCE} geliefert.
	 *
	 * @param ref Funktionsreferenz.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Funktionsreferenz ungültig ist. */
	protected FEMFunction get(final int type, final int size, final long addr) throws IllegalArgumentException {
		switch (type) {
			case TYPE_VOID:
				return FEMVoid.INSTANCE;
			case TYPE_TRUE:
				return FEMBoolean.TRUE;
			case TYPE_FALSE:
				return FEMBoolean.FALSE;
			case TYPE_ARRAY_LIST:
				return this.toArrayValue(index);
			case TYPE_STRING_INT8:
				return this.getStringValue(index);
			case TYPE_BINARY_VALUE:
				return this.getBinaryValue(index);
			case TYPE_INTEGER:
				return this.getIntegerValue(index);
			case TYPE_DECIMAL:
				return this.getDecimalValue(index);
			case TYPE_DATETIME:
				return this.getDatetimeValue(index);
			case TYPE_DURATION:
				return this.getDurationValue(index);
			case TYPE_HANDLER:
				return this.getHandlerValue(index);
			case TYPE_OBJECT:
				return this.getObjectValue(index);
			case TYPE_PROXY_FUNCTION:
				return this.getProxyFunction(index);
			case TYPE_PARAM_FUNCTION:
				return FEMParam.from(index);
			case TYPE_CONCAT_FUNCTION:
				return this.getConcatFunction(index);
			case TYPE_CLOSURE_FUNCTION:
				return this.getClosureFunction(index);
			case TYPE_COMPOSITE_FUNCTION:
				return this.getCompositeFunction(index);
			default:
				return this.getCustomFunction(type, index);
		}
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

	public int putFutureValue(final FEMFuture src) {
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

	/** Diese Methode gibt die Funktion zur gegebenen Typkennung und {@link #toIndex(int) Position} zurück.
	 *
	 * @param type Typkennung
	 * @param index Position.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Funktionsreferenz ungültig ist. */
	protected FEMFunction getCustomFunction(final int type, final int index) throws IllegalArgumentException {
		return FEMVoid.INSTANCE;
	}

	/** Diese Methode nimmt den gegebenen Wert in die Verwaltung auf und gibt die Referenz darauf zurück.
	 *
	 * @param src Wert.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Wert nicht aufgenommen werden kann. */
	public int putValue(final FEMValue src) throws NullPointerException, IllegalArgumentException {
		if (src instanceof FEMVoid) return this.putVoidValue();
		if (src instanceof FEMArray) return this.putArrayValue((FEMArray)src.data());
		if (src instanceof FEMHandler) return this.putHandlerValue((FEMHandler)src.data());
		if (src instanceof FEMBoolean) return this.putBooleanValue((FEMBoolean)src.data());
		if (src instanceof FEMString) return this.putStringValue((FEMString)src.data());
		if (src instanceof FEMBinary) return this.putBinaryValue((FEMBinary)src.data());
		if (src instanceof FEMInteger) return this.putIntegerValue((FEMInteger)src.data());
		if (src instanceof FEMDecimal) return this.putDecimalValue((FEMDecimal)src.data());
		if (src instanceof FEMDuration) return this.putDurationValue((FEMDuration)src.data());
		if (src instanceof FEMDatetime) return this.putDatetimeValue((FEMDatetime)src.data());
		if (src instanceof FEMObject) return this.putObjectValue((FEMObject)src.data());
		if (src instanceof FEMFuture) return this.putFutureValue((FEMFuture)src);
		if (src instanceof FEMNative) return this.putNativeValue((FEMNative)src);
		return this.putCustomValue(src);
	}

	public int putNativeValue(final FEMNative src) {
		throw new IllegalArgumentException();
	}

	protected int putCustomValue(final FEMValue src) throws NullPointerException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	protected int putCustomFunction(final FEMFunction src) throws NullPointerException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode fügt {@link FEMVoid#INSTANCE} hinzu und gibt die Referenz darauf zurück.
	 *
	 * @return Referenz. */
	public final int putVoidValue() {
		return this.putImpl(IAMArray.from(FEMCodec.TYPE_VOID));
	}

	/** Diese Methode fügt {@link FEMBoolean#FALSE} hinzu und gibt die Referenz darauf zurück.
	 *
	 * @return Referenz. */
	public final int putFalseValue() {
		return this.putImpl(IAMArray.from(FEMCodec.TYPE_FALSE));
	}

	/** Diese Methode gibt die Wertliste zur gegebenen Referenz zurück. */
	public final FEMArray getArrayValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMArray.class);
	}

	/** Diese Methode nimmt die gegebene Wertliste in die Verwaltung auf und gibt die {@link #getRefImpl(int, int)} Referenz} darauf zurück.
	 *
	 * @param src Dezimalbruch.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #toArrayArray(FEMArray)} diese auslöst. */
	public int putArrayValue(final FEMArray src) throws NullPointerException, IllegalArgumentException {
		return this.getRefImpl(FEMCodec.TYPE_ARRAY_LIST, this.arrayValuePool.put(src));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #toArrayValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Wertliste enthält. Eine über
	 * {@link FEMArray#compact(boolean)} indizierte Wertliste wird mit der Indizierung kodiert.
	 *
	 * @param src Wertliste.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putValue(FEMValue)} diese auslöst. */
	public IAMArray toArrayArray(final FEMArray src) throws NullPointerException, IllegalArgumentException {
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
	 * Die Zahlenfolge beginnt mit den über {@link #putValue(FEMValue)} ermittelten {@link #getRefImpl(int, int) Referenzen} der Elemente der gegebenen Wertliste
	 * und endet mit dem {@link FEMArray#hashCode() Streuwert} sowie der {@link FEMArray#length() Länge} der Wertliste.</li>
	 * <li>Indiziert - {@code (value[length], hash[1], index[length], range[count], length[1])}<br>
	 * Die Zahlenfolge beginnt ebenfalls mit den Referenzen sowie dem Streuwert und endet auch mit der Länge der Wertliste. Dazwischen enthält sie die Inhalte
	 * sowie die Größen der Streuwertbereiche.</li>
	 * </ul>
	 *
	 * @param src Zahlenfolge.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMArray toArrayValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		final int length1 = src.length() - 2, length2 = src.get(length1 + 1);
		if (length1 == length2) return new IndexArray(length1, this, src);
		return new IndexArray2(length2, this, src);
	}

	/** Diese Methode gibt die Zeichenkette zur gegebenen Referenz zurück. */
	public final FEMString getStringValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMString.class);
	}

	/** Diese Methode nimmt die gegebene Zeichenkette in die Verwaltung auf und gibt die {@link #getRefImpl(int, int)} Referenz} darauf zurück.
	 *
	 * @param src Zeichenkette.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@link #getStringArray(FEMString)} diese auslöst. */
	public int putStringValue(final FEMString src) throws NullPointerException {
		return this.getRefImpl(FEMCodec.TYPE_STRING_INT8, this.stringValuePool.put(src));
	}

	protected final <GResult> GResult getFunction(final int ref, final Class<GResult> functionClass) {
		try {
			return functionClass.cast(this.getFunction(ref));
		} catch (final IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getStringValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Zeichenkette enthält.
	 *
	 * @param src Zeichenkette.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist. */
	public IAMArray getStringArray(final FEMString src) {
		return FEMCodec.toArray_(src);
	}

	/** Diese Methode gibt eine Zeichenkette zurück, deren Codepoints in der gegebenen Zahlenfolge {@link FEMString#toArray() kodiert} sind.
	 *
	 * @param src Zahlenfolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMString getStringValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		return FEMCodec.from(src);
	}

	/** Diese Methode nimmt die gegebene Bytefolge in die Verwaltung auf und gibt die {@link #getRefImpl(int, int)} Referenz} darauf zurück.
	 *
	 * @param src Bytefolge.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@link #getBinaryArray(FEMBinary)} diese auslöst. */
	public int putBinaryValue(final FEMBinary src) throws NullPointerException {
		return this.getRefImpl(FEMCodec.TYPE_BINARY_VALUE, this.binaryValuePool.put(src));
	}

	/** Diese Methode gibt die Bytefolge zur gegebenen Referenz zurück. */
	public final FEMBinary getBinaryValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMBinary.class);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getBinaryValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Bytefolge enthält.
	 *
	 * @param src Bytefolge.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist. */
	public IAMArray getBinaryArray(final FEMBinary src) throws NullPointerException {
		return FEMCodec.toArray(src);
	}

	/** Diese Methode gibt die Bytefolge zur gegebenen Zahlenfolge zurück, deren Bytes in der gegebenen Zahlenfolge {@link FEMBinary#toArray() kodiert} sind.
	 *
	 * @param src Zahlenfolge.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMBinary getBinaryValue(final IAMArray src) {
		return FEMCodec.from(src);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getIntegerValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Dezimalzahl enthält. */
	public final IAMArray getIntegerData(final FEMInteger src) throws NullPointerException {
		final long val = src.value();
		return IAMArray.from(FEMCodec.TYPE_INTEGER, Integers.toIntH(val), Integers.toIntL(val));
	}

	/** Diese Methode gibt die Dezimalzahl zur gegebenen Referenz zurück. */
	public final FEMInteger getIntegerValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMInteger.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Dezimalzahl und gibt diese zurück. Die Zahlenfolge muss dazu aus den folgenden drei Zahlen
	 * bestehen: (1) Typkennung sowie (2) MSB-{@code int} und (3) LSB-{@code int} der {@link FEMInteger#value() internen Darstellung} der Dezimalzahl. */
	public final FEMInteger getIntegerValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 3) || (src.get(0) != FEMCodec.TYPE_INTEGER)) throw new IllegalArgumentException();
		return new FEMInteger(Integers.toLong(src.get(1), src.get(2)));
	}

	/** Diese Methode ergänzt die gegebene Dezimalzahl und gibt die Referenz darauf zurück. */
	public final int putIntegerValue(final FEMInteger src) throws NullPointerException, IllegalStateException {
		return this.putData(this.getIntegerData(src));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getDecimalValue(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Dezimalbruch enthält. */
	public final IAMArray getDecimalData(final FEMDecimal src) throws NullPointerException {
		final long val = Double.doubleToLongBits(src.value());
		return IAMArray.from(FEMCodec.TYPE_DECIMAL, Integers.toIntH(val), Integers.toIntL(val));
	}

	/** Diese Methode gibt den Dezimalbruch zur gegebenen Referenz zurück. */
	public final FEMDecimal getDecimalValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMDecimal.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Dezimalbruch und gibt diesen zurück. Die Zahlenfolge muss dazu aus den folgenden drei Zahlen
	 * bestehen: (1) Typkennung sowie (2) MSB-{@code int} und (3) LSB-{@code int} der {@link FEMDecimal#value() internen Darstellung} des Dezimalbruchs. */
	public final FEMDecimal getDecimalValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 3) || (src.get(0) != FEMCodec.TYPE_DECIMAL)) throw new IllegalArgumentException();
		return new FEMDecimal(Double.longBitsToDouble(Integers.toLong(src.get(1), src.get(2))));
	}

	/** Diese Methode ergänzt den gegebenen Dezimalbruch und gibt die Referenz darauf zurück. */
	public final int putDecimalValue(final FEMDecimal src) throws NullPointerException, IllegalStateException {
		return this.putData(this.getDecimalData(src));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getDurationValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Zeitspanne enthält. */
	public final IAMArray getDurationData(final FEMDuration src) throws NullPointerException {
		final long val = src.value();
		return IAMArray.from(FEMCodec.TYPE_DURATION, Integers.toIntH(val), Integers.toIntL(val));
	}

	/** Diese Methode gibt die Zeitspanne zur gegebenen Referenz zurück. */
	public final FEMDuration getDurationValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMDuration.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Zeitspanne und gibt diese zurück. Die Zahlenfolge muss dazu aus folgenden drei Zahlen bestehen:
	 * (1) Typkennung sowie (2) MSB-{@code int} und (3) LSB-{@code int} der {@link FEMDuration#value() internen Darstellung} der Zeitspanne. */
	public final FEMDuration getDurationValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 3) || (src.get(0) != FEMCodec.TYPE_DURATION)) throw new IllegalArgumentException();
		return new FEMDuration(Integers.toLong(src.get(1), src.get(2)));
	}

	/** Diese Methode ergänzt die gegebene Zeitspanne und gibt die Referenz darauf zurück. */
	public final int putDurationValue(final FEMDuration src) throws NullPointerException, IllegalStateException {
		return this.putData(this.getDurationData(src));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getDatetimeValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Zeitangabe enthält. */
	public final IAMArray getDatetimeData(final FEMDatetime src) throws NullPointerException {
		final long val = src.value();
		return IAMArray.from(FEMCodec.TYPE_DATETIME, Integers.toIntH(val), Integers.toIntL(val));
	}

	/** Diese Methode gibt die Zeitangabe zur gegebenen Referenz zurück. */
	public final FEMDatetime getDatetimeValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMDatetime.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Zeitangabe und gibt diese zurück. Die Zahlenfolge muss dazu aus folgenden drei Zahlen bestehen:
	 * (1) Typkennung sowie (2) MSB-{@code int} und (3) LSB-{@code int} der {@link FEMDatetime#value() internen Darstellung} der Zeitangabe. */
	public final FEMDatetime getDatetimeValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 3) || (src.get(0) != FEMCodec.TYPE_DATETIME)) throw new IllegalArgumentException();
		return new FEMDatetime(Integers.toLong(src.get(1), src.get(2)));
	}

	/** Diese Methode ergänzt die gegebene Zeitangabe und gibt die Referenz darauf zurück. */
	public final int putDatetimeValue(final FEMDatetime src) throws NullPointerException, IllegalStateException {
		return this.putData(this.getDatetimeData(src));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getBooleanValue(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Wahrheitswert enthält. */
	public final IAMArray getBooleanData(final FEMBoolean src) throws NullPointerException {
		return IAMArray.from(src.value() ? FEMCodec.TYPE_TRUE : FEMCodec.TYPE_FALSE);
	}

	/** Diese Methode gibt den Wahrheitswert zur gegebenen Referenz zurück. */
	public final FEMBoolean getBooleanValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMBoolean.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Zeitangabe und gibt diese zurück. Die Zahlenfolge muss dazu aus der Typkennung bestehen. */
	public final FEMBoolean getBooleanValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if (src.length() != 1) throw new IllegalArgumentException();
		final int type = src.get(0);
		if (type == FEMCodec.TYPE_TRUE) return FEMBoolean.TRUE;
		if (type == FEMCodec.TYPE_FALSE) return FEMBoolean.FALSE;
		throw new IllegalArgumentException();
	}

	/** Diese Methode ergänzt den gegebenen Wahrheitswert und gibt die Referenz darauf zurück. */
	public final int putBooleanValue(final FEMBoolean src) throws NullPointerException, IllegalStateException {
		return this.putData(this.getBooleanData(src));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getHandlerValue(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Funktionszeiger enthält. */
	public final IAMArray getHandlerData(final FEMHandler src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return IAMArray.from(FEMCodec.TYPE_HANDLER, this.putFunction(src.value()));
	}

	/** Diese Methode gibt den Funktionszeiger zur gegebenen Referenz zurück. */
	public final FEMHandler getHandlerValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMHandler.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionszeiger und gibt diese zurück. Die Zahlenfolge muss dazu aus der Typkennung sowie der
	 * Referenz auf die Zielfunktion bestehen. */
	public final FEMHandler getHandlerValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 2) || (src.get(0) != FEMCodec.TYPE_HANDLER)) throw new IllegalArgumentException();
		return new FEMHandler(this.getFunction(src.get(1)));
	}

	/** Diese Methode ergänzt den gegebenen Funktionszeiger und gibt die Referenz darauf zurück. */
	public final int putHandlerValue(final FEMHandler src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this.putData(this.getHandlerData(src));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getObjectValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Objektreferenz enthält. */
	public final IAMArray getObjectData(final FEMObject src) throws NullPointerException {
		final long val = src.value();
		return IAMArray.from(FEMCodec.TYPE_OBJECT, Integers.toIntH(val), Integers.toIntL(val));
	}

	/** Diese Methode gibt die Objektreferenz zur gegebenen Referenz zurück. */
	public final FEMObject getObjectValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMObject.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Objektreferenz und gibt diesen zurück. Die Zahlenfolge muss dazu aus zwei Zahlen bestehen, von
	 * denen die erste den MSB-{@code int} und die zweiten den LSB-{@code int} der {@link FEMObject#value() internen Darstellung} der Objektreferenz enthält.
	 *
	 * @param src Zahlenfolge.
	 * @return Objektreferenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMObject getObjectValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 3) || (src.get(0) != FEMCodec.TYPE_OBJECT)) throw new IllegalArgumentException();
		return new FEMObject(Integers.toLong(src.get(1), src.get(2)));
	}

	/** Diese Methode ergänzt den gegebenen Objektreferenz und gibt die Referenz darauf zurück. */
	public final int putObjectValue(final FEMObject src) throws NullPointerException, IllegalStateException {
		return this.putData(this.getObjectData(src));
	}

	/** Diese Methode ist eine Abkürzung für {@link #getProxyFunction(IAMArray) this.toProxyFunction(this.getData(ref))}.
	 *
	 * @param ref Referenz.
	 * @return Funktionsplatzhalter. */
	public final FEMProxy getProxyFunction(final int ref) throws IllegalArgumentException {
		return this.getProxyFunction(this.getData(ref));
	}

	/** Diese Methode ist eine Abkürzung für {@link #getProxyData(FEMProxy) this.putData(this.toProxyArray(src))}.
	 *
	 * @param src Funktionsaufruf.
	 * @return Referenz. */
	public final int putProxyFunction(final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		return this.putImpl(this.getProxyData(src));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getProxyFunction(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Funktionsplatzhalter
	 * enthält. */
	public IAMArray getProxyData(final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		return IAMArray.from(FEMCodec.TYPE_PROXY_FUNCTION, this.putValue(src.id()), this.putStringValue(src.name()), this.putFunction(src.get()));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionsplatzhalter und gibt diese zurück. Die Zahlenfolge muss dazu aus den folgenden vier
	 * Zahlen bestehen: (1) Typkennung, (2) {@link #putValue(FEMValue) Referenz} der {@link FEMProxy#id() Kennung}, (3) {@link #putValue(FEMValue) Referenz} des
	 * {@link FEMProxy#name() Namnes} und (4) {@link #putFunction(FEMFunction) Referenz} des {@link FEMProxy#get() Ziels}. */
	public FEMProxy getProxyFunction(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 4) || (src.get(0) != FEMCodec.TYPE_PROXY_FUNCTION)) throw new IllegalArgumentException();
		return new FEMProxy(this.getValue(src.get(1)), this.getStringValue(src.get(2)), this.getFunction(src.get(3)));
	}

	/** Diese Methode gibt die {@link #getRefImpl(int, int)} Funktionsreferenz} auf die gegebene Parameterfunktion zurück.
	 *
	 * @param src Parameterfunktion.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist. */
	public int putParamFunction(final FEMParam src) throws NullPointerException, IllegalStateException {
		return this.getRefImpl(FEMCodec.TYPE_PARAM_FUNCTION, src.index());
	}

	/** Diese Methode nimmt die gegebene Funktionkette in die Verwaltung auf und gibt die {@link #getRefImpl(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param src Funktionkette.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getConcatArray(ConcatFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getConcatArray(ConcatFunction)} diese auslöst. */
	public int putConcatFunction(final ConcatFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this.getRefImpl(FEMCodec.TYPE_CONCAT_FUNCTION, this.concatFunctionPool.put(src));
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
	 * {@link #putFunction(FEMFunction)} ermittelten {@link #getRefImpl(int, int) Funktionsreferenzen} der {@link ConcatFunction#function() verketteten Funktion}
	 * und iher {@link ConcatFunction#params() Parameterfunktionen} bestehen.
	 *
	 * @param src Zahlenfolge.
	 * @return Funktionkette.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public ConcatFunction getConcatFunction(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		return new ConcatFunction(this.getCompositeFunctionImpl(src), this.getCompositeParamsImpl(src));
	}

	/** Diese Methode nimmt die gegebene Funktionsbindung in die Verwaltung auf und gibt die {@link #getRefImpl(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param src Funktionsbindung.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst. */
	public int putClosureFunction(final ClosureFunction src) throws NullPointerException, IllegalArgumentException {
		return this.getRefImpl(FEMCodec.TYPE_CLOSURE_FUNCTION, this.closureFunctionPool.put(src));
	}

	/** Diese Methode gibt die Funktionsbindung zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #closureFunctionPool}.
	 * @return Funktionsbindung.
	 * @throws IllegalArgumentException Wenn {@link #getClosureFunction(IAMArray)} diese auslöst. */
	/** Diese Methode gibt die Wertliste zur gegebenen Referenz zurück. */
	public final ClosureFunction getClosureFunction(final int index) throws IllegalArgumentException {
		return this.getFunction(ref, FEMDatetime.class);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getClosureFunction(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Funktionsbindung enthält.
	 *
	 * @param src Funktionsbindung.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putFunction(FEMFunction)} diese auslöst. */
	public IAMArray getClosureArray(final ClosureFunction src) throws NullPointerException, IllegalArgumentException {
		return IAMArray.from(this.putFunction(src.function()));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionsbindungen und gibt diese zurück. Die Zahlenfolge muss dazu aus einer
	 * {@link #getRefImpl(int, int) Funktionsreferenz} bestehen, welche über {@link #getFunction(int)} interpretiert wird.
	 *
	 * @param src Zahlenfolge.
	 * @return Funktionsbindungen.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public ClosureFunction getClosureFunction(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if (src.length() != 1) throw new IllegalArgumentException();
		return new ClosureFunction(this.getFunction(src.get(0)));
	}

	/** Diese Methode nimmt den gegebenen Funktionsaufruf in die Verwaltung auf und gibt die {@link #getRefImpl(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param src Funktionsaufruf.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getCompositeArray(CompositeFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getCompositeArray(CompositeFunction)} diese auslöst. */
	public int putCompositeFunction(final CompositeFunction src) throws NullPointerException, IllegalArgumentException {
		return this.getRefImpl(FEMCodec.TYPE_COMPOSITE_FUNCTION, this.compositeFunctionPool.put(src));
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
	 * {@link #putFunction(FEMFunction)} ermittelten {@link #getRefImpl(int, int) Funktionsreferenzen} der {@link CompositeFunction#function() aufgerufenen
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

	/** {@inheritDoc} */
	@Override
	public long emu() {
		return EMU.fromObject(this) + this.arrayValuePool.emu() + this.stringValuePool.emu() + this.binaryValuePool.emu() + this.integerValuePool.emu()
			+ this.decimalValuePool.emu() + this.durationValuePool.emu() + this.datetimeValuePool.emu() + this.handlerValuePool.emu() + this.objectValuePool.emu()
			+ this.proxyFunctionPool.emu() + this.concatFunctionPool.emu() + this.closureFunctionPool.emu() + this.compositeFunctionPool.emu();
	}

	/** Diese Methode leert die Puffer der aus {@link #getSource()} gelesenen Datensätze. */
	public void cleanup() {
		this.arrayValuePool.cleanup();
		this.stringValuePool.cleanup();
		this.binaryValuePool.cleanup();
		this.integerValuePool.cleanup();
		this.decimalValuePool.cleanup();
		this.durationValuePool.cleanup();
		this.datetimeValuePool.cleanup();
		this.handlerValuePool.cleanup();
		this.objectValuePool.cleanup();
		this.proxyFunctionPool.cleanup();
		this.concatFunctionPool.cleanup();
		this.closureFunctionPool.cleanup();
		this.compositeFunctionPool.cleanup();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.arrayValuePool, this.stringValuePool, this.binaryValuePool, this.integerValuePool, this.decimalValuePool,
			this.durationValuePool, this.datetimeValuePool, this.handlerValuePool, this.objectValuePool, this.proxyFunctionPool, this.concatFunctionPool,
			this.closureFunctionPool, this.compositeFunctionPool);
	}

	/** Diese Methode gibt eine Zahlenfolge zurück, welche die einzelwertkodierten Codepoints dieser Zeichenkette enthält. Sie ist die Umkehroperation zu
	 * {@link from}.
	 *
	 * @return Zahlenfolge mit den entsprechend kodierten Codepoints. */
	public static IAMArray toArray_(final FEMString s) {
		final FEMString arr = s.compact();
		if (arr instanceof FEMString.CompactStringINT8) return FEMCodec.toArray_(s, 1, false);
		if (arr instanceof FEMString.CompactStringINT16) return FEMCodec.toArray_(s, 2, false);
		return FEMCodec.toArray_(s, 4, false);
	}

	/** Diese Methode gibt eine Zahlenfolge zurück, welche die einzel- oder mehrwertkodierten Codepoints dieser Zeichenkette enthält. Sie ist die Umkehroperation
	 * zu {@link from}.
	 *
	 * @param mode Größe der Zahlen der Zahlenfolge: {@code 1} für {@code 8-Bit}, {@code 2} für {@code 16-Bit} und {@code 4} für {@code 32-Bit}.
	 * @param asUTFx {@code true}, wenn die Codepoints mehrwertkodiert werden sollen. {@code false}, wenn die Codepoints einzelwertkodiert werden sollen.
	 * @return Zahlenfolge mit den entsprechend kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static IAMArray toArray_(final FEMString str, final int mode, final boolean asUTFx) {
		final int hash = str.hashCode(), length = str.length();
		switch (mode) {
			case 1: {
				if (asUTFx) {
					final FEMString.UTF8Counter counter = new FEMString.UTF8Counter();
					str.extract(counter);
					final byte[] array = new byte[counter.count + 9];
					array[0] = (byte)(hash >>> 0);
					array[1] = (byte)(hash >>> 8);
					array[2] = (byte)(hash >>> 16);
					array[3] = (byte)(hash >>> 24);
					array[4] = (byte)(length >>> 0);
					array[5] = (byte)(length >>> 8);
					array[6] = (byte)(length >>> 16);
					array[7] = (byte)(length >>> 24);
					str.extract(new FEMString.UTF8Encoder(array, 8));
					return IAMArray.from(array);
				} else {
					final byte[] array = new byte[length + 5];
					array[0] = (byte)(hash >>> 0);
					array[1] = (byte)(hash >>> 8);
					array[2] = (byte)(hash >>> 16);
					array[3] = (byte)(hash >>> 24);
					str.extract(new FEMString.INT8Encoder(array, 4));
					return IAMArray.from(array);
				}
			}
			case 2: {
				if (asUTFx) {
					final FEMString.UTF16Counter counter = new FEMString.UTF16Counter();
					str.extract(counter);
					final short[] array = new short[counter.count + 5];
					array[0] = (short)(hash >>> 0);
					array[1] = (short)(hash >>> 16);
					array[2] = (short)(length >>> 0);
					array[3] = (short)(length >>> 16);
					str.extract(new FEMString.UTF16Encoder2(array, 4));
					return IAMArray.from(array);
				} else {
					final short[] array = new short[length + 3];
					array[0] = (short)(hash >>> 0);
					array[1] = (short)(hash >>> 16);
					str.extract(new FEMString.INT16Encoder(array, 2));
					return IAMArray.from(array);
				}
			}
			case 4: {
				final int[] array = new int[length + 2];
				array[0] = hash;
				str.extract(new FEMString.UTF32Encoder(array, 1));
				return IAMArray.from(array);
			}
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@code from(array, false)} und die Umkehroperation zu {@link #toArray(int)}.
	 *
	 * @see #from(boolean, IAMArray)
	 * @param array Zahlenfolge.
	 * @return {@link FEMString}-Sicht auf die gegebene Zahlenfolge.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final IAMArray array) throws NullPointerException, IllegalArgumentException {
		return FEMCodec.from(false, array);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Zeichenkette und gibt diese zurück. Bei der Kodierung mit Einzelwerten werden die ersten vier
	 * Byte der Zahlenfolge als {@link #hashCode() Streuwert}, die darauf folgenden Zahlenwerte als Auflistung der einzelwertkodierten Codepoints und der letzte
	 * Zahlenwert als abschließende {@code 0} interpretiert. Bei der Mehrwertkodierung werden dagegen die ersten vier Byte der Zahlenfolge als {@link #hashCode()
	 * Streuwert}, die nächsten vier Byte als {@link #length() Zeichenanzahl}, die darauf folgenden Zahlenwerte als Auflistung der mehrwertkodierten Codepoints
	 * und der letzte Zahlenwert als abschließende {@code 0} interpretiert. Ob eine 8-, 16- oder 32-Bit-Kodierung eingesetzt wird, hängt von der
	 * {@link IAMArray#mode() Kodierung der Zahlenwerte} ab.
	 *
	 * @param asUTFx {@code true}, wenn die Codepoints mehrwertkodiert sind. {@code false}, wenn die Codepoints einzelwertkodiert sind.
	 * @param array Zahlenfolge.
	 * @return {@link FEMString}-Sicht auf die gegebene Zahlenfolge.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final boolean asUTFx, final IAMArray array) throws NullPointerException, IllegalArgumentException {
		switch (array.mode()) {
			case IAMArray.MODE_INT8:
			case IAMArray.MODE_UINT8:
				if (asUTFx) return new ArrayStringUTF8(array);
				return new ArrayStringINT8(array);
			case IAMArray.MODE_INT16:
			case IAMArray.MODE_UINT16:
				if (asUTFx) return new ArrayStringUTF16(array);
				return new ArrayStringINT16(array);
			case IAMArray.MODE_INT32:
				return new ArrayString(array);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt eine Zahlenfolge zurück, welche die Bytes dieser Bytefolge enthält. Sie ist die Umkehroperation zu {@link FEMCodec#from(IAMArray)}.
	 *
	 * @return Zahlenfolge mit den kodierten Bytes dieser Bytefolge. */
	public static IAMArray toArray(final FEMBinary b) {
		final byte[] array = new byte[b.length() + 4];
		final int hash = b.hashCode();
		array[0] = (byte)(hash >>> 0);
		array[1] = (byte)(hash >>> 8);
		array[2] = (byte)(hash >>> 16);
		array[3] = (byte)(hash >>> 24);
		b.extract(array, 4);
		return IAMArray.from(array);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Bytefolge und gibt diese zurück. Die ersten vier Byte der Zahlenfolge werden als
	 * {@link #hashCode() Streuwert} und die darauf folgenden Zahlenwerte als Auflistung der Bytes interpretiert. Die {@link IAMArray#mode() Kodierung der
	 * Zahlenwerte} muss eine 8-Bit-Kodierung anzeigen.
	 *
	 * @param array Zahlenfolge.
	 * @return {@link FEMBinary}-Sicht auf die gegebene Zahlenfolge.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMBinary from(final IAMArray array) throws NullPointerException, IllegalArgumentException {
		final int mode = array.mode();
		if ((mode != IAMArray.MODE_INT8) && (mode != IAMArray.MODE_UINT8)) throw new IllegalArgumentException();
		return new ArrayBinary(array);
	}

}
