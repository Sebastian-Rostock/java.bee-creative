package bee.creative.fem;

import java.util.ArrayList;
import bee.creative.bind.Property;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.fem.FEMArray.CompactArray3;
import bee.creative.fem.FEMFunction.ClosureFunction;
import bee.creative.fem.FEMFunction.CompositeFunction;
import bee.creative.fem.FEMFunction.ConcatFunction;
import bee.creative.iam.IAMArray;
import bee.creative.io.MappedBuffer;
import bee.creative.lang.Bytes;
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
	public static class ArrayString extends FEMString {

		public final IAMArray array;

		public final int offset;

		ArrayString(final IAMArray array, final int offset, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
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
			return new ArrayString(this.array, this.offset + offset, length, 0);
		}

		@Override
		public FEMString compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ArrayString2 extends ArrayString {

		ArrayString2(final IAMArray array, final int offset, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(array, offset, length, hash);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return super.customGet(index) & 0xFF;
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new ArrayString2(this.array, this.offset + offset, length, 0);
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ArrayString3 extends ArrayString {

		ArrayString3(final IAMArray array, final int offset, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			super(array, offset, length, hash);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return super.customGet(index) & 0xFFFF;
		}

		@Override
		protected FEMString customSection(final int offset, final int length) {
			return new ArrayString3(this.array, this.offset + offset, length, 0);
		}

	}

	protected static final int MAGIC_NUMBER = 0x6005BABE;

	/** Dieses Feld speichert die Typkennung für {@link FEMVoid#INSTANCE}. */
	protected static final int TYPE_VOID = 0;

	/** Dieses Feld speichert die Typkennung für {@link FEMBoolean#TRUE}. */
	protected static final int TYPE_TRUE = 1;

	/** Dieses Feld speichert die Typkennung für {@link FEMBoolean#FALSE}. */
	protected static final int TYPE_FALSE = 2;

	/** Dieses Feld speichert die Typkennung für {@link #putArrayValue(FEMArray)}. */
	protected static final int TYPE_ARRAY_LIST = 3;

	/** Dieses Feld speichert die Typkennung für {@link #putArrayValue(FEMArray)}. */
	protected static final int TYPE_ARRAY_HASH = 4;

	/** Dieses Feld speichert die Typkennung für {@link #putStringValue(FEMString)}. */
	protected static final int TYPE_STRING = 5;

	/** Dieses Feld speichert die Typkennung für {@link #putBinaryValue(FEMBinary)}. */
	protected static final int TYPE_BINARY = 6;

	/** Dieses Feld speichert die Typkennung für {@link #putIntegerValue(FEMInteger)}. */
	protected static final int TYPE_INTEGER = 7;

	/** Dieses Feld speichert die Typkennung für {@link #putDecimalValue(FEMDecimal)}. */
	protected static final int TYPE_DECIMAL = 8;

	/** Dieses Feld speichert die Typkennung für {@link #putDurationValue(FEMDuration)}. */
	protected static final int TYPE_DURATION = 9;

	/** Dieses Feld speichert die Typkennung für {@link #putDatetimeValue(FEMDatetime)}. */
	protected static final int TYPE_DATETIME = 10;

	/** Dieses Feld speichert die Typkennung für {@link #putHandlerValue(FEMHandler)}. */
	protected static final int TYPE_HANDLER = 11;

	/** Dieses Feld speichert die Typkennung für {@link #putObjectValue(FEMObject)}. */
	protected static final int TYPE_OBJECT = 12;

	/** Dieses Feld speichert die Typkennung für {@link #putProxyFunction(FEMProxy)}. */
	protected static final int TYPE_PROXY = 20;

	/** Dieses Feld speichert die Typkennung für {@link #putParamFunction(FEMParam)}. */
	protected static final int TYPE_PARAM_FUNCTION = 21;

	/** Dieses Feld speichert die Typkennung für {@link #putConcatFunction(ConcatFunction)}. */
	protected static final int TYPE_CONCAT_FUNCTION = 22;

	/** Dieses Feld speichert die Typkennung für {@link #putClosureFunction(ClosureFunction)}. */
	protected static final int TYPE_CLOSURE = 23;

	/** Dieses Feld speichert die Typkennung für {@link #putCompositeFunction(CompositeFunction)}. */
	protected static final int TYPE_COMPOSITE_FUNCTION = 24;

	static final IAMArray DATA_VOID = IAMArray.from(FEMCodec.TYPE_VOID);

	static final IAMArray DATA_TRUE = IAMArray.from(FEMCodec.TYPE_TRUE);

	static final IAMArray DATA_FALSE = IAMArray.from(FEMCodec.TYPE_FALSE);

	/** Dieses Feld speichert die Referenz der nächsten {@link #putImpl(IAMArray) angefügten} Zahlenfolge. */
	private int nextRef;

	/** Dieses Feld speichert die Referenz auf die als {@link #set(FEMFunction) Wurzel gesetzte} Funktion. */
	private int rootRef;

	/** Dieses Feld speichert den Puffer, in dem die Yahlenfolgen abgelegt sind. */
	private MappedBuffer buffer;

	/** Dieses Feld bildet von einer Zahlenfolge auf deren Referenz ab und wird zusammen mit {@link #reuseEnabled} inn {@link #putImpl(IAMArray)} eingesetzt. */
	private HashMap2<IAMArray, Integer> reuseMapping;

	private boolean reuseEnabled = true;

	private ArrayList<FEMFunction> cacheListing;

	private boolean cacheEnabled = true;

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
		final MMIArrayL data = this.getData(ref);
		final int type = data.buffer.getInt(data.address);
		return this.getFunction(type, data);
	}

	protected FEMFunction getFunction(final int type, final MMIArrayL data) throws IllegalArgumentException {
		switch (type) {
			case TYPE_VOID:
				return FEMVoid.INSTANCE;
			case TYPE_TRUE:
				return FEMBoolean.TRUE;
			case TYPE_FALSE:
				return FEMBoolean.FALSE;
			case TYPE_ARRAY_LIST:
				return this.toArrayValue(data);
			case TYPE_STRING:
				return this.getStringValue(data);
			case TYPE_BINARY:
				return this.getBinaryValue(data.asINT8());
			case TYPE_INTEGER:
				return this.getIntegerValue(data);
			case TYPE_DECIMAL:
				return this.getDecimalValue(data);
			case TYPE_DATETIME:
				return this.getDatetimeValue(data);
			case TYPE_DURATION:
				return this.getDurationValue(data);
			case TYPE_HANDLER:
				return this.getHandlerValue(data);
			case TYPE_OBJECT:
				return this.getObjectValue(data);
			case TYPE_PROXY:
				return this.getProxyFunction(data);
			case TYPE_PARAM_FUNCTION:
				return FEMParam.from(index);
			case TYPE_CONCAT_FUNCTION:
				return this.getConcatFunction(data);
			case TYPE_CLOSURE:
				return this.getClosureFunction(data);
			case TYPE_COMPOSITE_FUNCTION:
				return this.getCompositeFunction(data);
			default:
				return this.getCustomFunction(type, data);
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

	protected int putCustomValue(final FEMValue src) throws NullPointerException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	protected int putCustomFunction(final FEMFunction src) throws NullPointerException, IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	public final IAMArray getVoidData() {
		return FEMCodec.DATA_VOID;
	}

	/** Diese Methode fügt {@link FEMVoid#INSTANCE} hinzu und gibt die Referenz darauf zurück.
	 *
	 * @return Referenz. */
	public final int putVoidValue() {
		return this.putImpl(this.getVoidData());
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
		return this.getRefImpl(FEMCodec.TYPE_STRING, this.stringValuePool.put(src));
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
	 * <p>
	 * Diese Methode interpretiert die gegebene Zahlenfolge als Zeichenkette und gibt diese zurück. Bei der Kodierung mit Einzelwerten werden die ersten vier Byte
	 * der Zahlenfolge als {@link #hashCode() Streuwert}, die darauf folgenden Zahlenwerte als Auflistung der einzelwertkodierten Codepoints und der letzte
	 * Zahlenwert als abschließende {@code 0} interpretiert. Bei der Mehrwertkodierung werden dagegen die ersten vier Byte der Zahlenfolge als {@link #hashCode()
	 * Streuwert}, die nächsten vier Byte als {@link FEMString#length() Zeichenanzahl}, die darauf folgenden Zahlenwerte als Auflistung der mehrwertkodierten
	 * Codepoints und der letzte Zahlenwert als abschließende {@code 0} interpretiert. Ob eine 8-, 16- oder 32-Bit-Kodierung eingesetzt wird, hängt von der
	 * {@link IAMArray#mode() Kodierung der Zahlenwerte} ab.
	 *
	 * @param src Zahlenfolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMString getStringValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {

		final int mode = src.mode();
		if (mode == IAMArray.MODE_INT8) {
			final int length = src.length() - 8;
			if (length < 0) return null;
			final byte[] buffer = new byte[8]; // TODO pre alloc + sync?
			src.get(buffer, 0);
			final int type = Bytes.getInt4LE(buffer, 0);
			if (type != FEMCodec.TYPE_STRING) return null;
			final int hash = Bytes.getInt4LE(buffer, 4);
			return new ArrayString2(src, 0, length, hash);
		}
		switch (mode) {
			case IAMArray.MODE_INT8:
				return new ArrayString2(src);
			case IAMArray.MODE_INT16:
				return new ArrayString3(src);
			case IAMArray.MODE_INT32:
				return new ArrayString(src);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getBinaryValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Bytefolge enthält. */
	public final IAMArray getBinaryData(final FEMBinary src) throws NullPointerException {
		final byte[] buffer = new byte[src.length() + 8];
		Bytes.setInt4LE(buffer, 0, FEMCodec.TYPE_BINARY);
		Bytes.setInt4LE(buffer, 4, src.hashCode());
		src.extract(buffer, 8);
		return IAMArray.from(buffer);
	}

	/** Diese Methode gibt die Bytefolge zur gegebenen Referenz zurück. */
	public final FEMBinary getBinaryValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMBinary.class);
	}

	/** Diese Methode gibt die Bytefolge zur gegebenen Zahlenfolge zurück, deren Bytes in der gegebenen Zahlenfolge kodiert sind. Die Zahlenfolge muss dazu in
	 * {@link IAMArray#MODE_INT8} vorliegen und in ihren ersten vier Byte die Typkennung sowie in den zweiten vier Byte den Streuwert (beides little-endian)
	 * enthalten. */
	public final FEMBinary getBinaryValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		final int length = src.length() - 8;
		if ((length < 0) || (src.mode() != IAMArray.MODE_INT8)) throw new IllegalArgumentException();
		final byte[] buffer = new byte[8]; // TODO pre alloc + sync?
		src.get(buffer, 0);
		final int type = Bytes.getInt4LE(buffer, 0);
		if (type != FEMCodec.TYPE_BINARY) throw new IllegalArgumentException();
		final int hash = Bytes.getInt4LE(buffer, 4);
		return new ArrayBinary(src, 0, length, hash);
	}

	/** Diese Methode ergänzt die gegebene Bytefolge und gibt die Referenz darauf zurück. */
	public final int putBinaryValue(final FEMBinary src) throws NullPointerException, IllegalStateException {
		return this.putData(this.getBinaryData(src));
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
	 * bestehen: (1) Typkennung sowie (2) MSB-{@code int} und (3) LSB-{@code int} der {@link FEMInteger#value() internen Darstellung} der Dezimalzahl. Andernfalls
	 * wird {@code null} geliefert. */
	public final FEMInteger getIntegerValue(final IAMArray src) throws NullPointerException {
		if ((src.length() != 3) || (src.get(0) != FEMCodec.TYPE_INTEGER)) return null;
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
	 * bestehen: (1) Typkennung sowie (2) MSB-{@code int} und (3) LSB-{@code int} der {@link FEMDecimal#value() internen Darstellung} des Dezimalbruchs.
	 * Andernfalls wird {@code null} geliefert. */
	public final FEMDecimal getDecimalValue(final IAMArray src) throws NullPointerException {
		if ((src.length() != 3) || (src.get(0) != FEMCodec.TYPE_DECIMAL)) return null;
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
	 * (1) Typkennung sowie (2) MSB-{@code int} und (3) LSB-{@code int} der {@link FEMDuration#value() internen Darstellung} der Zeitspanne. Andernfalls wird
	 * {@code null} geliefert. */
	public final FEMDuration getDurationValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 3) || (src.get(0) != FEMCodec.TYPE_DURATION)) return null;
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
	 * (1) Typkennung sowie (2) MSB-{@code int} und (3) LSB-{@code int} der {@link FEMDatetime#value() internen Darstellung} der Zeitangabe. Andernfalls wird
	 * {@code null} geliefert. */
	public final FEMDatetime getDatetimeValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 3) || (src.get(0) != FEMCodec.TYPE_DATETIME)) return null;
		return new FEMDatetime(Integers.toLong(src.get(1), src.get(2)));
	}

	/** Diese Methode ergänzt die gegebene Zeitangabe und gibt die Referenz darauf zurück. */
	public final int putDatetimeValue(final FEMDatetime src) throws NullPointerException, IllegalStateException {
		return this.putData(this.getDatetimeData(src));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getBooleanValue(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Wahrheitswert enthält. */
	public final IAMArray getBooleanData(final FEMBoolean src) throws NullPointerException {
		return src.value() ? FEMCodec.DATA_TRUE : FEMCodec.DATA_FALSE;
	}

	/** Diese Methode gibt den Wahrheitswert zur gegebenen Referenz zurück. */
	public final FEMBoolean getBooleanValue(final int ref) throws IllegalArgumentException {
		return this.getFunction(ref, FEMBoolean.class);
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Zeitangabe und gibt diese zurück. Die Zahlenfolge muss dazu aus der Typkennung bestehen.
	 * Andernfalls wird {@code null} geliefert. */
	public final FEMBoolean getBooleanValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if (src.length() != 1) return null;
		final int type = src.get(0);
		if (type == FEMCodec.TYPE_TRUE) return FEMBoolean.TRUE;
		if (type == FEMCodec.TYPE_FALSE) return FEMBoolean.FALSE;
		return null;
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
	 * Referenz auf die Zielfunktion bestehen. Andernfalls wird {@code null} geliefert. */
	public final FEMHandler getHandlerValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 2) || (src.get(0) != FEMCodec.TYPE_HANDLER)) return null;
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

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Objektreferenz und gibt diesen zurück. Die Zahlenfolge muss dazu aus der Typkennung und dem
	 * MSB-{@code int} sowie dem LSB-{@code int} der {@link FEMObject#value() internen Darstellung} der Objektreferenz bestehen. Andernfalls wird {@code null}
	 * geliefert. */
	public final FEMObject getObjectValue(final IAMArray src) throws NullPointerException, IllegalArgumentException {
		if ((src.length() != 3) || (src.get(0) != FEMCodec.TYPE_OBJECT)) return null;
		return new FEMObject(Integers.toLong(src.get(1), src.get(2)));
	}

	/** Diese Methode ergänzt den gegebenen Objektreferenz und gibt die Referenz darauf zurück. */
	public final int putObjectValue(final FEMObject src) throws NullPointerException, IllegalStateException {
		return this.putData(this.getObjectData(src));
	}

	public int putFutureValue(final FEMFuture src) {
		throw new IllegalArgumentException();
	}

	public int putNativeValue(final FEMNative src) {
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getProxyFunction(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Funktionsplatzhalter
	 * enthält. */
	public final IAMArray getProxyData(final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		return IAMArray.from(FEMCodec.TYPE_PROXY, this.putValue(src.id()), this.putStringValue(src.name()), this.putFunction(src.get()));
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
		return this.putData(this.getProxyData(src));
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

	/** Diese Methode nimmt die gegebene Funktionsbindung in die Verwaltung auf und gibt die {@link #getRefImpl(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param src Funktionsbindung.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst. */
	public int putClosureFunction(final ClosureFunction src) throws NullPointerException, IllegalArgumentException {
		return this.getRefImpl(FEMCodec.TYPE_CLOSURE, this.closureFunctionPool.put(src));
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
		return EMU.fromObject(this) + this.reuseMapping.emu() + EMU.from(this.buffer);
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

}
