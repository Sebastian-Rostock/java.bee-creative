package bee.creative.fem;

import bee.creative.bind.Property;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.fem.FEMArray.CompactArray3;
import bee.creative.fem.FEMFunction.ClosureFunction;
import bee.creative.fem.FEMFunction.CompositeFunction;
import bee.creative.fem.FEMFunction.ConcatFunction;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMIndex;
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

	/** Diese Klasse implementiert eine {@link FEMArray Wertliste}, deren Elemente als {@link IAMArray Zahlenfolge} aus {@link FEMCodec#___toRef(int, int)
	 * Referenzen} gegeben sind und in {@link #customGet(int)} über einen gegebenen {@link FEMCodec} in Werte {@link FEMCodec#getValue(int) übersetzt} werden. */
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

	/** Dieses Feld speichert den leeren {@link FEMCodec} als Leser des leeren {@link IAMIndex}. */
	public static final FEMCodec EMPTY = new FEMCodec().useSource(IAMIndex.EMPTY);

	protected static final int MAGIC_NUMBER = 0x6005BABE;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putVoidValue()}. */
	protected static final int TYPE_VOID = 0;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putTrueValue()}. */
	protected static final int TYPE_TRUE = 1;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putFalseValue()}. */
	protected static final int TYPE_FALSE = 2;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putArrayValue(FEMArray)}. */
	protected static final int TYPE_ARRAY_LIST = 3;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putArrayValue(FEMArray)}. */
	protected static final int TYPE_ARRAY_HASH = 3;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putStringValue(FEMString)}. */
	protected static final int TYPE_STRING_INT8 = 4;

	protected static final int TYPE_STRING_INT16 = 4;

	protected static final int TYPE_STRING_INT32 = 4;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putBinaryValue(FEMBinary)}. */
	protected static final int TYPE_BINARY_VALUE = 5;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putIntegerValue(FEMInteger)}. */
	protected static final int TYPE_INTEGER_VALUE = 6;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putDecimalValue(FEMDecimal)}. */
	protected static final int TYPE_DECIMAL_VALUE = 7;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putDurationValue(FEMDuration)}. */
	protected static final int TYPE_DURATION_VALUE = 8;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putDatetimeValue(FEMDatetime)}. */
	protected static final int TYPE_DATETIME_VALUE = 9;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putHandlerValue(FEMHandler)}. */
	protected static final int TYPE_HANDLER_VALUE = 10;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putObjectValue(FEMObject)}. */
	protected static final int TYPE_OBJECT_VALUE = 11;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putProxyFunction(FEMProxy)}. */
	protected static final int TYPE_PROXY_FUNCTION = 12;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putParamFunction(FEMParam)}. */
	protected static final int TYPE_PARAM_FUNCTION = 13;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putConcatFunction(ConcatFunction)}. */
	protected static final int TYPE_CONCAT_FUNCTION = 14;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putClosureFunction(ClosureFunction)}. */
	protected static final int TYPE_CLOSURE_FUNCTION = 15;

	/** Dieses Feld speichert die {@link #_getType(int) Typkennung} für {@link #putCompositeFunction(CompositeFunction)}. */
	protected static final int TYPE_COMPOSITE_FUNCTION = 16;

	/** Dieses Feld speichert die Referenz der nächsten {@link #putData(IAMArray) angefügten} Zahlenfolge. */
	private int nextRef;

	/** Dieses Feld speichert die Referenz auf die als {@link #set(FEMValue) Wurzel gesetzten} Wert. */
	private int rootRef;

	/** Dieses Feld speichert den Puffer, in dem die Yahlenfolgen abgelegt sind. */
	private MappedBuffer buffer;

	private boolean reuseEnabled = true;

	/** Dieses Feld bildet von einer Zahlenfolge auf deren Referenz ab und wird zusammen mit {@link #reuseEnabled} inn {@link #putData(IAMArray)} eingesetzt. */
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

	/** Diese Methode setzt die Aktivierung der Wiederverwendung von Zahlenfolgen und gibt {@code this} zurück. Wenn {@link #putData(IAMArray)}
	 *
	 * @param enabled
	 * @return */
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
	 * @param source Datenquelle.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public FEMCodec useSource(final MappedBuffer source) throws NullPointerException {
		this.buffer = Objects.notNull(source);
		this.reuseMapping = null;

		this.nextRef = source.getInt(4);
		this.rootRef = source.getInt(8);
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

	/** Diese Methode gibt die Referenz zur gegebenen {@link #___toAddr(int) Adresse} zurück.
	 *
	 * @param addr Adresse.
	 * @return Referenz. */
	final int ___toRef(final long addr) {
		return (int)(addr >>> 4);
	}

	/** Diese Methode gibt die Adresse des Speicherbereichs der {@link MMIArray Zahlenfolge} zur gegebenen {@link #___toRef(long) Refernez} zurück.<ber> Die
	 * Kopfdaten dieses Speicherbereichs stehten in den vier Byte davor und nennen Länge sowie Kodierung der Zahlenfolge. Bei den Kodierungen werden nur
	 * {@link IAMArray#MODE_INT8}, {@link IAMArray#MODE_INT16} und {@link IAMArray#MODE_INT32} unterschieden.
	 * <p>
	 * TODO Die Kopfdaten der im {@link #buffer} angebundenen Datei besteht dabei aus drei {@code int}-Werten: der MAGIC-NUMBER, der {@link #nextRef NEXT-REF} und
	 * der {@link #rootRef ROOT-REF}.
	 *
	 * @param ref Referenz (1..2147483647).
	 * @return Adresse. */
	final long ___toAddr(final int ref) {
		return (long)ref << 4;
	}

	/** Diese Methode gibt die {@link MMIArray Zahlenfolge} zur gegebenen {@link #putData(IAMArray) Referenz} zurück.
	 *
	 * @param ref Referenz.
	 * @return Zahlenfolge.
	 * @throws IllegalArgumentException Wenn {@link MappedBuffer#getInt(long)} bzw. {@link MappedBuffer#getArray(long, int, int)} eine ausnahme auslöst. */
	public final MMIArrayL getData(final int ref) throws IllegalArgumentException {
		try {
			final long addr = this.___toAddr(ref);
			final int head = this.buffer.getInt(addr - 4);
			return this.buffer.getArray(addr, head >>> 2, head & 3);
		} catch (final IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	public final int putData(final IAMArray array) throws NullPointerException, IllegalStateException {
		this.checkPut();
		if (this.reuseEnabled) {
			final Integer result = this.reuseMapping.get(array);
			if (result != null) return result.intValue();
		}
		try {
			final int length = array.length(), size = MMIArray.size(array.mode()), ref = this.nextRef;
			final long addr = this.___toAddr(ref), capacity = (19 + addr + (size * (long)length)) & -15;
			this.buffer.grow(capacity);
			this.buffer.putInt(addr - 4, (length << 2) | (size >> 1));
			this.buffer.putArray(addr, array);
			this.nextRef = this.___toRef(capacity);
			if (this.reuseEnabled) {
				final MMIArrayL data = this.getData(ref);
				this.reuseMapping.put(data, new Integer(ref));
			}
			return ref;
		} catch (final IllegalStateException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalStateException(cause);
		}
	}

	void checkPut() throws IllegalStateException {
		if (this.reuseMapping == null) throw new IllegalStateException();
	}

	public FEMValue getValue(final int ref) throws IllegalArgumentException {
		return (FEMValue)this.getFunction(ref);
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz zurück. Wenn deren Typkennung unbekannt ist, wird {@link FEMVoid#INSTANCE} geliefert.
	 *
	 * @param ref Referenz.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMFunction getFunction(final int ref) throws IllegalArgumentException {
		long addr = this.___toAddr(ref);
		final int type = this.buffer.getInt(addr);
		addr += 4;
		final int size = this.buffer.getInt(addr);
		addr += 4;
		return this.get(type, size, addr);
	}

	/** Diese Methode gibt die Funktion zur gegebenen {@link #___toRef(int, int) Funktionsreferenz} zurück. Wenn deren {@link #_getType(int) Typkennung} unbekannt
	 * ist, wird {@link FEMVoid#INSTANCE} geliefert.
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
			case TYPE_INTEGER_VALUE:
				return this.getIntegerValue(index);
			case TYPE_DECIMAL_VALUE:
				return this.getDecimalValue(index);
			case TYPE_DATETIME_VALUE:
				return this.getDatetimeValue(index);
			case TYPE_DURATION_VALUE:
				return this.getDurationValue(index);
			case TYPE_HANDLER_VALUE:
				return this.getHandlerValue(index);
			case TYPE_OBJECT_VALUE:
				return this.getObjectValue(index);
			case TYPE_PROXY_FUNCTION:
				return this.toProxyFunction(index);
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
	 * @param source Funktion.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Funktion nicht aufgenommen werden kann. */
	public int putFunction(final FEMFunction source) throws NullPointerException, IllegalArgumentException {
		if (source instanceof FEMValue) return this.putValue((FEMValue)source);
		if (source instanceof FEMProxy) return this.putProxyFunction((FEMProxy)source);
		if (source instanceof FEMParam) return this.putParamFunction((FEMParam)source);
		if (source instanceof ConcatFunction) return this.putConcatFunction((ConcatFunction)source);
		if (source instanceof ClosureFunction) return this.putClosureFunction((ClosureFunction)source);
		if (source instanceof CompositeFunction) return this.putCompositeFunction((CompositeFunction)source);
		putCustomFunction(source);
	}

	public int putFutureValue(final FEMFuture source) {
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den Wert zur gegebenen {@link #_getType(int) Typkennung} und {@link #toIndex(int) Position} zurück.
	 *
	 * @param type Typkennung
	 * @param index Position.
	 * @return Wert.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	protected FEMValue getCustomValue(final int type, final int index) throws IllegalArgumentException {
		return FEMVoid.INSTANCE;
	}

	/** Diese Methode gibt die Funktion zur gegebenen {@link #_getType(int) Typkennung} und {@link #toIndex(int) Position} zurück.
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
	 * @param source Wert.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Wert nicht aufgenommen werden kann. */
	public int putValue(final FEMValue source) throws NullPointerException, IllegalArgumentException {
		if (source instanceof FEMVoid) return this.putVoidValue();
		if (source instanceof FEMArray) return this.putArrayValue((FEMArray)source.data());
		if (source instanceof FEMHandler) return this.putHandlerValue((FEMHandler)source.data());
		if (source instanceof FEMBoolean) return this.putBooleanValue((FEMBoolean)source.data());
		if (source instanceof FEMString) return this.putStringValue((FEMString)source.data());
		if (source instanceof FEMBinary) return this.putBinaryValue((FEMBinary)source.data());
		if (source instanceof FEMInteger) return this.putIntegerValue((FEMInteger)source.data());
		if (source instanceof FEMDecimal) return this.putDecimalValue((FEMDecimal)source.data());
		if (source instanceof FEMDuration) return this.putDurationValue((FEMDuration)source.data());
		if (source instanceof FEMDatetime) return this.putDatetimeValue((FEMDatetime)source.data());
		if (source instanceof FEMObject) return this.putObjectValue((FEMObject)source.data());
		if (source instanceof FEMFuture) return this.putFutureValue((FEMFuture)source);
		if (source instanceof FEMNative) return this.putNativeValue((FEMNative)source);
		return this.putCustomValue(source);
	}

	public int putNativeValue(FEMNative source) {
		throw new IllegalArgumentException();
	}

	protected int putCustomValue(final FEMValue source) throws NullPointerException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	protected int putCustomFunction(final FEMFunction source) throws NullPointerException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	/** Diese Methode fügt {@link FEMVoid#INSTANCE} hinzu und gibt die Referenz darauf zurück.
	 *
	 * @return Referenz. */
	public int putVoidValue() {
		return this.putData(IAMArray.from(TYPE_VOID));
	}

	/** Diese Methode fügt {@link FEMBoolean#TRUE} hinzu und gibt die Referenz darauf zurück.
	 *
	 * @return Referenz. */
	public int putTrueValue() {
		return this.putData(IAMArray.from(TYPE_TRUE));
	}

	/** Diese Methode fügt {@link FEMBoolean#FALSE} hinzu und gibt die Referenz darauf zurück.
	 *
	 * @return Referenz. */
	public int putFalseValue() {
		return this.putData(IAMArray.from(TYPE_FALSE));
	}

	/** Diese Methode gibt die Wertliste zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #arrayValuePool}.
	 * @return Wertliste.
	 * @throws IllegalArgumentException Wenn {@link #toArrayValue(IAMArray)} diese auslöst. */
	protected FEMArray getArrayValue(final int index) throws IllegalArgumentException {
		return this.arrayValuePool.get(index);
	}

	/** Diese Methode nimmt die gegebene Wertliste in die Verwaltung auf und gibt die {@link #___toRef(int, int)} Referenz} darauf zurück.
	 *
	 * @param source Dezimalbruch.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #toArrayArray(FEMArray)} diese auslöst. */
	public int putArrayValue(final FEMArray source) throws NullPointerException, IllegalArgumentException {
		return this.___toRef(FEMCodec.TYPE_ARRAY_LIST, this.arrayValuePool.put(source));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #toArrayValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Wertliste enthält. Eine über
	 * {@link FEMArray#compact(boolean)} indizierte Wertliste wird mit der Indizierung kodiert.
	 *
	 * @param source Wertliste.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putValue(FEMValue)} diese auslöst. */
	public IAMArray toArrayArray(final FEMArray source) throws NullPointerException, IllegalArgumentException {
		final int length = source.length();
		final int[] result;
		if (source instanceof CompactArray3) {
			// value[length], hash[1], index[length], range[count], length[1] => 2xlength + count + 2
			final int[] table = ((CompactArray3)source).table;
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
		result[length] = source.hashCode();
		result[result.length - 1] = length;
		for (int i = 0; i < length; i++) {
			result[i] = this.putValue(source.customGet(i));
		}
		return IAMArray.from(result);
	}

	/** Diese Methode gibt eine Wertliste zurück, deren Elemente in der gegebenen Zahlenfolge sind. Die Zahlenfolge kann dazu in einer der folgenden Strukturen
	 * vorliegen:
	 * <ul>
	 * <li>Einfach - {@code (value[length], hash[1], length[1])}<br>
	 * Die Zahlenfolge beginnt mit den über {@link #putValue(FEMValue)} ermittelten {@link #___toRef(int, int) Referenzen} der Elemente der gegebenen Wertliste
	 * und endet mit dem {@link FEMArray#hashCode() Streuwert} sowie der {@link FEMArray#length() Länge} der Wertliste.</li>
	 * <li>Indiziert - {@code (value[length], hash[1], index[length], range[count], length[1])}<br>
	 * Die Zahlenfolge beginnt ebenfalls mit den Referenzen sowie dem Streuwert und endet auch mit der Länge der Wertliste. Dazwischen enthält sie die Inhalte
	 * sowie die Größen der Streuwertbereiche.</li>
	 * </ul>
	 *
	 * @param source Zahlenfolge.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMArray toArrayValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		final int length1 = source.length() - 2, length2 = source.get(length1 + 1);
		if (length1 == length2) return new IndexArray(length1, this, source);
		return new IndexArray2(length2, this, source);
	}

	/** Diese Methode nimmt die gegebene Zeichenkette in die Verwaltung auf und gibt die {@link #___toRef(int, int)} Referenz} darauf zurück.
	 *
	 * @param source Zeichenkette.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@link #getStringArray(FEMString)} diese auslöst. */
	public int putStringValue(final FEMString source) throws NullPointerException {
		return this.___toRef(FEMCodec.TYPE_STRING_INT8, this.stringValuePool.put(source));
	}

	/** Diese Methode gibt die Zeichenkette zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #stringValuePool}.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn {@link #getStringValue(IAMArray)} diese auslöst. */
	protected FEMString getStringValue(final int index) throws IllegalArgumentException {
		return this.stringValuePool.get(index);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getStringValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Zeichenkette enthält.
	 *
	 * @param source Zeichenkette.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getStringArray(final FEMString source) {
		return FEMCodec.toArray_(source);
	}

	/** Diese Methode gibt eine Zeichenkette zurück, deren Codepoints in der gegebenen Zahlenfolge {@link FEMString#toArray() kodiert} sind.
	 *
	 * @param source Zahlenfolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMString getStringValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return FEMCodec.from(source);
	}

	/** Diese Methode nimmt die gegebene Bytefolge in die Verwaltung auf und gibt die {@link #___toRef(int, int)} Referenz} darauf zurück.
	 *
	 * @param source Bytefolge.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@link #getBinaryArray(FEMBinary)} diese auslöst. */
	public int putBinaryValue(final FEMBinary source) throws NullPointerException {
		return this.___toRef(FEMCodec.TYPE_BINARY_VALUE, this.binaryValuePool.put(source));
	}

	/** Diese Methode gibt die Bytefolge zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #binaryValuePool}.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn {@link #getBinaryValue(IAMArray)} diese auslöst. */
	protected FEMBinary getBinaryValue(final int index) throws IllegalArgumentException {
		return this.binaryValuePool.get(index);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getBinaryValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Bytefolge enthält.
	 *
	 * @param source Bytefolge.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getBinaryArray(final FEMBinary source) throws NullPointerException {
		return FEMCodec.toArray(source);
	}

	/** Diese Methode gibt die Bytefolge zur gegebenen Zahlenfolge zurück, deren Bytes in der gegebenen Zahlenfolge {@link FEMBinary#toArray() kodiert} sind.
	 *
	 * @param source Zahlenfolge.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMBinary getBinaryValue(final IAMArray source) {
		return FEMCodec.from(source);
	}

	/** Diese Methode nimmt die gegebene Dezimalzahl in die Verwaltung auf und gibt die {@link #___toRef(int, int)} Referenz} darauf zurück.
	 *
	 * @param source Dezimalzahl.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@link #getIntegerArray(FEMInteger)} diese auslöst. */
	public int putIntegerValue(final FEMInteger source) throws NullPointerException {
		return this.___toRef(FEMCodec.TYPE_INTEGER_VALUE, this.integerValuePool.put(source));
	}

	/** Diese Methode gibt die Dezimalzanl zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #integerValuePool}.
	 * @return Dezimalzahl.
	 * @throws IllegalArgumentException Wenn {@link #getIntegerValue(IAMArray)} diese auslöst. */
	protected FEMInteger getIntegerValue(final int index) throws IllegalArgumentException {
		return this.integerValuePool.get(index);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getIntegerValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Dezimalzahl enthält.
	 *
	 * @param source Dezimalzahl.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getIntegerArray(final FEMInteger source) throws NullPointerException {
		return this.getIntegerArrayImpl(source.value());
	}

	IAMArray getIntegerArrayImpl(final long value) {
		return IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Dezimalzahl und gibt diese zurück. Die Zahlenfolge muss dazu aus zwei Zahlen bestehen, von denen
	 * die erste den MSB-{@code int} und die zweiten den LSB-{@code int} der {@link FEMInteger#value() internen Darstellung} der Dezimalzahl enthält.
	 *
	 * @param source Zahlenfolge.
	 * @return Dezimalzahl.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMInteger getIntegerValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return new FEMInteger(this.getIntegerValueImpl(source));
	}

	long getIntegerValueImpl(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() != 2) throw new IllegalArgumentException();
		return Integers.toLong(source.get(1), source.get(0));
	}

	/** Diese Methode nimmt den gegebenen Dezimalbruch in die Verwaltung auf und gibt die {@link #___toRef(int, int)} Referenz} darauf zurück.
	 *
	 * @param source Dezimalbruch.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@link #getDatetimeArray(FEMDatetime)} diese auslöst. */
	public int putDecimalValue(final FEMDecimal source) throws NullPointerException {
		return this.___toRef(FEMCodec.TYPE_DECIMAL_VALUE, this.decimalValuePool.put(source));
	}

	/** Diese Methode gibt den Dezimalbruch zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #decimalValuePool}.
	 * @return Dezimalbruch.
	 * @throws IllegalArgumentException Wenn {@link #getDecimalValue(IAMArray)} diese auslöst. */
	protected FEMDecimal getDecimalValue(final int index) throws IllegalArgumentException {
		return this.decimalValuePool.get(index);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getDecimalValue(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Dezimalbruch enthält.
	 *
	 * @param source Dezimalbruch.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getDecimalArray(final FEMDecimal source) throws NullPointerException {
		return this.getIntegerArrayImpl(Double.doubleToLongBits(source.value()));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Dezimalbruch und gibt diesen zurück. Die Zahlenfolge muss dazu aus zwei Zahlen bestehen, von
	 * denen die erste den MSB-{@code int} und die zweiten den LSB-{@code int} der {@link FEMDecimal#value() internen Darstellung} des Dezimalbruchs enthält.
	 *
	 * @param source Zahlenfolge.
	 * @return Dezimalbruch.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMDecimal getDecimalValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return new FEMDecimal(Double.longBitsToDouble(this.getIntegerValueImpl(source)));
	}

	/** Diese Methode nimmt die gegebene Zeitspanne in die Verwaltung auf und gibt die {@link #___toRef(int, int)} Referenz} darauf zurück.
	 *
	 * @param source Zeitspanne.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@link #getDurationArray(FEMDuration)} diese auslöst. */
	public int putDurationValue(final FEMDuration source) throws NullPointerException {
		return this.___toRef(FEMCodec.TYPE_DURATION_VALUE, this.durationValuePool.put(source));
	}

	/** Diese Methode gibt die Zeitspanne zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #datetimeValuePool}.
	 * @return Zeitspanne.
	 * @throws IllegalArgumentException Wenn {@link #getDurationValue(IAMArray)} diese auslöst. */
	protected FEMDuration getDurationValue(final int index) throws IllegalArgumentException {
		return this.durationValuePool.get(index);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getDurationValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Zeitspanne enthält.
	 *
	 * @param source Zeitspanne.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getDurationArray(final FEMDuration source) throws NullPointerException {
		return this.getIntegerArrayImpl(source.value());
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Zeitspanne und gibt diese zurück. Die Zahlenfolge muss dazu aus zwei Zahlen bestehen, von denen
	 * die erste den MSB-{@code int} und die zweiten den LSB-{@code int} der {@link FEMDuration#value() internen Darstellung} der Zeitspanne enthält.
	 *
	 * @param source Zahlenfolge.
	 * @return Zeitspanne.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMDuration getDurationValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return new FEMDuration(this.getIntegerValueImpl(source));
	}

	/** Diese Methode nimmt die gegebene Zeitangabe in die Verwaltung auf und gibt die {@link #___toRef(int, int)} Referenz} darauf zurück.
	 *
	 * @param source Zeitangabe.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@link #getDatetimeArray(FEMDatetime)} diese auslöst. */
	public int putDatetimeValue(final FEMDatetime source) throws NullPointerException {
		return this.___toRef(FEMCodec.TYPE_DATETIME_VALUE, this.datetimeValuePool.put(source));
	}

	/** Diese Methode gibt die Zeitangabe zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #datetimeValuePool}.
	 * @return Zeitangabe.
	 * @throws IllegalArgumentException Wenn {@link #getDatetimeValue(IAMArray)} diese auslöst. */
	protected FEMDatetime getDatetimeValue(final int index) throws IllegalArgumentException {
		return this.datetimeValuePool.get(index);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getDatetimeValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Zeitangabe enthält.
	 *
	 * @param source Zeitangabe.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getDatetimeArray(final FEMDatetime source) throws NullPointerException {
		return this.getIntegerArrayImpl(source.value());
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Zeitangabe und gibt diese zurück. Die Zahlenfolge muss dazu aus zwei Zahlen bestehen, von denen
	 * die erste den MSB-{@code int} und die zweiten den LSB-{@code int} der {@link FEMDatetime#value() internen Darstellung} der Zeitangabe enthält.
	 *
	 * @return Zeitangabe zur Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMDatetime getDatetimeValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return new FEMDatetime(this.getIntegerValueImpl(source));
	}

	/** Diese Methode gibt die Referenz auf den gegebenen Wahrheitswert zurück. */
	public int putBooleanValue(final FEMBoolean source) throws NullPointerException {
		return source.value() ? this.putTrueValue() : this.putFalseValue();
	}

	/** Diese Methode nimmt den gegebenen Funktionszeiger in die Verwaltung auf und gibt die {@link #___toRef(int, int)} Referenz} darauf zurück.
	 *
	 * @param source Funktionszeiger.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@link #getHandlerArray(FEMHandler)} diese auslöst. */
	public int putHandlerValue(final FEMHandler source) throws NullPointerException, IllegalArgumentException {
		return this.___toRef(FEMCodec.TYPE_HANDLER_VALUE, this.handlerValuePool.put(source));
	}

	/** Diese Methode gibt den Funktionszeiger zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #handlerValuePool}.
	 * @return Funktionszeiger.
	 * @throws IllegalArgumentException Wenn {@link #getHandlerValue(IAMArray)} diese auslöst. */
	protected FEMHandler getHandlerValue(final int index) throws IllegalArgumentException {
		return this.handlerValuePool.get(index);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getHandlerValue(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Funktionszeiger enthält.
	 *
	 * @param source Funktionszeiger.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putFunction(FEMFunction)} diese auslöst. */
	public IAMArray getHandlerArray(final FEMHandler source) throws NullPointerException, IllegalArgumentException {
		return IAMArray.from(this.putFunction(source.value()));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionszeiger und gibt diese zurück. Die Zahlenfolge muss dazu aus einer
	 * {@link #___toRef(int, int) Funktionsreferenz} bestehen, welche über {@link #getFunction(int)} interpretiert wird.
	 *
	 * @param source Zahlenfolge.
	 * @return Funktionszeiger.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMHandler getHandlerValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() != 1) throw new IllegalArgumentException();
		return new FEMHandler(this.getFunction(source.get(0)));
	}

	/** Diese Methode nimmt die gegebene Objektreferenz in die Verwaltung auf und gibt die {@link #___toRef(int, int)} Referenz} darauf zurück.
	 *
	 * @param source Objektreferenz.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@link #getObjectArray(FEMObject)} diese auslöst. */
	public int putObjectValue(final FEMObject source) throws NullPointerException {
		return this.___toRef(FEMCodec.TYPE_OBJECT_VALUE, this.objectValuePool.put(source));
	}

	/** Diese Methode gibt die Objektreferenz zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #objectValuePool}.
	 * @return Objektreferenz.
	 * @throws IllegalArgumentException Wenn {@link #getObjectValue(IAMArray)} diese auslöst. */
	protected FEMObject getObjectValue(final int index) throws IllegalArgumentException {
		return this.objectValuePool.get(index);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getObjectValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Objektreferenz enthält.
	 *
	 * @param source Objektreferenz.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getObjectArray(final FEMObject source) throws NullPointerException {
		return this.getIntegerArrayImpl(source.value());
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Objektreferenz und gibt diesen zurück. Die Zahlenfolge muss dazu aus zwei Zahlen bestehen, von
	 * denen die erste den MSB-{@code int} und die zweiten den LSB-{@code int} der {@link FEMObject#value() internen Darstellung} der Objektreferenz enthält.
	 *
	 * @param source Zahlenfolge.
	 * @return Objektreferenz.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMObject getObjectValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return new FEMObject(this.getIntegerValueImpl(source));
	}

	/** Diese Methode ist eine Abkürzung für {@link #toProxyFunction(IAMArray) this.toProxyFunction(this.getData(ref))}.
	 *
	 * @param ref Referenz.
	 * @return Funktionsplatzhalter. */
	public final FEMProxy getProxyFunction(final int ref) throws IllegalArgumentException {
		return this.toProxyFunction(this.getData(ref));
	}

	/** Diese Methode ist eine Abkürzung für {@link #toProxyArray(FEMProxy) this.putData(this.toProxyArray(source))}.
	 *
	 * @param src Funktionsaufruf.
	 * @return Referenz. */
	public final int putProxyFunction(final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		return this.putData(this.toProxyArray(src));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #toProxyFunction(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Funktionsplatzhalter
	 * enthält.
	 *
	 * @param source Funktionsplatzhalter.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putFunction(FEMFunction)} diese auslöst. */
	public IAMArray toProxyArray(final FEMProxy source) throws NullPointerException, IllegalArgumentException {
		return IAMArray.from(FEMCodec.TYPE_PROXY_FUNCTION, this.putValue(source.id()), this.putStringValue(source.name()), this.putFunction(source.get()));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionsplatzhalter und gibt diese zurück. Die Zahlenfolge muss dazu aus vier Zahlen bestehen:
	 * der Typkennung, der über {@link #putValue(FEMValue) Referenz} seiner {@link FEMProxy#id() Kennung}, der {@link #putValue(FEMValue) Referenz} seines
	 * {@link FEMProxy#name() Namnes} sowie der {@link #putFunction(FEMFunction) Referenz} seines {@link FEMProxy#get() Ziels}.
	 *
	 * @param source Zahlenfolge.
	 * @return Funktionsplatzhalter.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMProxy toProxyFunction(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if ((source.length() != 4) || (source.get(0) != FEMCodec.TYPE_PROXY_FUNCTION)) throw new IllegalArgumentException();
		return new FEMProxy(this.getValue(source.get(1)), this.getStringValue(source.get(2)), this.getFunction(source.get(3)));
	}

	/** Diese Methode gibt die {@link #___toRef(int, int)} Funktionsreferenz} auf die gegebene Parameterfunktion zurück.
	 *
	 * @param source Parameterfunktion.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public int putParamFunction(final FEMParam source) throws NullPointerException {
		return this.___toRef(FEMCodec.TYPE_PARAM_FUNCTION, source.index());
	}

	/** Diese Methode nimmt die gegebene Funktionkette in die Verwaltung auf und gibt die {@link #___toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktionkette.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getConcatArray(ConcatFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getConcatArray(ConcatFunction)} diese auslöst. */
	public int putConcatFunction(final ConcatFunction source) throws NullPointerException, IllegalArgumentException {
		return this.___toRef(FEMCodec.TYPE_CONCAT_FUNCTION, this.concatFunctionPool.put(source));
	}

	/** Diese Methode gibt die Funktionkette zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #concatFunctionPool}.
	 * @return Funktionkette.
	 * @throws IllegalArgumentException Wenn {@link #getConcatFunction(IAMArray)} diese auslöst. */
	protected ConcatFunction getConcatFunction(final int index) throws IllegalArgumentException {
		return this.concatFunctionPool.get(index);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getConcatFunction(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Funktionkette enthält.
	 *
	 * @param source Funktionkette.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putFunction(FEMFunction)} diese auslöst. */
	public IAMArray getConcatArray(final ConcatFunction source) throws NullPointerException, IllegalArgumentException {
		return this.getCompositeArrayImpl(source.function(), source.params());
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionkette und gibt diese zurück. Die Zahlenfolge muss dazu aus den über
	 * {@link #putFunction(FEMFunction)} ermittelten {@link #___toRef(int, int) Funktionsreferenzen} der {@link ConcatFunction#function() verketteten Funktion}
	 * und iher {@link ConcatFunction#params() Parameterfunktionen} bestehen.
	 *
	 * @param source Zahlenfolge.
	 * @return Funktionkette.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public ConcatFunction getConcatFunction(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return new ConcatFunction(this.getCompositeFunctionImpl(source), this.getCompositeParamsImpl(source));
	}

	/** Diese Methode nimmt die gegebene Funktionsbindung in die Verwaltung auf und gibt die {@link #___toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktionsbindung.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst. */
	public int putClosureFunction(final ClosureFunction source) throws NullPointerException, IllegalArgumentException {
		return this.___toRef(FEMCodec.TYPE_CLOSURE_FUNCTION, this.closureFunctionPool.put(source));
	}

	/** Diese Methode gibt die Funktionsbindung zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #closureFunctionPool}.
	 * @return Funktionsbindung.
	 * @throws IllegalArgumentException Wenn {@link #getClosureFunction(IAMArray)} diese auslöst. */
	protected ClosureFunction getClosureFunction(final int index) throws IllegalArgumentException {
		return this.closureFunctionPool.get(index);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getClosureFunction(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Funktionsbindung enthält.
	 *
	 * @param source Funktionsbindung.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putFunction(FEMFunction)} diese auslöst. */
	public IAMArray getClosureArray(final ClosureFunction source) throws NullPointerException, IllegalArgumentException {
		return IAMArray.from(this.putFunction(source.function()));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionsbindungen und gibt diese zurück. Die Zahlenfolge muss dazu aus einer
	 * {@link #___toRef(int, int) Funktionsreferenz} bestehen, welche über {@link #getFunction(int)} interpretiert wird.
	 *
	 * @param source Zahlenfolge.
	 * @return Funktionsbindungen.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public ClosureFunction getClosureFunction(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() != 1) throw new IllegalArgumentException();
		return new ClosureFunction(this.getFunction(source.get(0)));
	}

	/** Diese Methode nimmt den gegebenen Funktionsaufruf in die Verwaltung auf und gibt die {@link #___toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktionsaufruf.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getCompositeArray(CompositeFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getCompositeArray(CompositeFunction)} diese auslöst. */
	public int putCompositeFunction(final CompositeFunction source) throws NullPointerException, IllegalArgumentException {
		return this.___toRef(FEMCodec.TYPE_COMPOSITE_FUNCTION, this.compositeFunctionPool.put(source));
	}

	/** Diese Methode gibt den Funktionsaufruf zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #compositeFunctionPool}.
	 * @return Funktionsaufruf.
	 * @throws IllegalArgumentException Wenn {@link #getCompositeFunction(IAMArray)} diese auslöst. */
	protected CompositeFunction getCompositeFunction(final int index) throws IllegalArgumentException {
		return this.compositeFunctionPool.get(index);
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
	 * @param source Funktionsaufruf.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putFunction(FEMFunction)} diese auslöst. */
	public IAMArray getCompositeArray(final CompositeFunction source) throws NullPointerException, IllegalArgumentException {
		return this.getCompositeArrayImpl(source.function(), source.params());
	}

	FEMFunction getCompositeFunctionImpl(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() == 0) throw new IllegalArgumentException();
		return this.getFunction(source.get(0));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionsaufruf und gibt diese zurück. Die Zahlenfolge muss dazu aus den über
	 * {@link #putFunction(FEMFunction)} ermittelten {@link #___toRef(int, int) Funktionsreferenzen} der {@link CompositeFunction#function() aufgerufenen
	 * Funktion} und iher {@link CompositeFunction#params() Parameterfunktionen} bestehen.
	 *
	 * @param source Zahlenfolge.
	 * @return Funktionsaufruf.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public CompositeFunction getCompositeFunction(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return new CompositeFunction(this.getCompositeFunctionImpl(source), this.getCompositeParamsImpl(source));
	}

	FEMFunction[] getCompositeParamsImpl(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		final int length = source.length() - 1;
		if (length < 0) throw new IllegalArgumentException();
		final FEMFunction[] result = new FEMFunction[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.getFunction(source.get(i + 1));
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
