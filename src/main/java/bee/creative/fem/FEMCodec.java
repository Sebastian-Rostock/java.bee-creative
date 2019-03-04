package bee.creative.fem;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import bee.creative.fem.FEMArray.CompactArray3;
import bee.creative.fem.FEMFunction.ClosureFunction;
import bee.creative.fem.FEMFunction.CompositeFunction;
import bee.creative.fem.FEMFunction.ConcatFunction;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMBuilder.IAMIndexBuilder;
import bee.creative.iam.IAMBuilder.IAMListingBuilder;
import bee.creative.iam.IAMBuilder.IAMMappingBuilder;
import bee.creative.iam.IAMIndex;
import bee.creative.iam.IAMListing;
import bee.creative.iam.IAMMapping;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Integers;
import bee.creative.util.Objects;
import bee.creative.util.Property;

/** Diese Klasse implementiert ein Objekt zur Kodierung und Dekodierung von {@link FEMValue Werten} und {@link FEMFunction Funktionen} in {@link IAMArray
 * Zahlenlisten}. Damit ist es möglich, beliebig große Wert- und Funktionsgraphen über ein {@link MMFArray} in eine Binärdatei auszulagern.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class FEMCodec implements Property<FEMValue> {

	/** Diese Klasse implementiert eine {@link FEMArray Wertliste}, deren Elemente als {@link IAMArray Zahlenfolge} aus {@link FEMCodec#toRef(int, int)
	 * Wertreferenzen} gegeben sind und in {@link #customGet(int)} über einen gegebenen {@link FEMCodec} in Werte {@link FEMCodec#getValue(int) übersetzt}
	 * werden. */
	protected static class IndexArray extends FEMArray {

		/** Dieses Feld speichert den {@link FEMCodec} zur {@link FEMCodec#getValue(int) Übersetzung} der Wertreferenzen aus {@link #items}. */
		public final FEMCodec index;

		/** Dieses Feld speichert die Zahlenfolge mit den Wertreferenzen. Ihre Struktur wird in {@link FEMCodec#getArrayValue(IAMArray)} beschrieben. */
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

	protected static abstract class BasePool<GItem> implements Items<GItem> {

		@SuppressWarnings ("javadoc")
		final class ItemList extends AbstractList<GItem> {

			@Override
			public GItem get(final int index) {
				return BasePool.this.get(index);
			}

			@Override
			public int size() {
				return BasePool.this.source.itemCount();
			}

		}

		/** Dieses Feld speichert den Besitzer. */
		protected final FEMCodec codec;

		/** Dieses Feld speichert den Puffer der über {@link #get(int)} gelieferten Datensätze. */
		protected Object[] cache = {};

		/** Dieses Feld speichert die Zahlenfolgen der über {@link #get(int)} gelesenen Datensätzen. */
		protected IAMListing source = IAMListing.EMPTY;

		/** Dieses Feld speichert die Zahlenfolgen der über {@link #put(Object)} angefügten Datensätzen. */
		protected IAMListingBuilder target;

		@SuppressWarnings ("javadoc")
		public BasePool(final FEMCodec codec) {
			this.codec = codec;
		}

		/** {@inheritDoc} */
		@Override
		public GItem get(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException();
			final IAMListing pool = this.source;
			final int poolSize = pool.itemCount();
			if (index >= poolSize) throw new IndexOutOfBoundsException();
			Object[] cache = this.cache;
			final int cacheSize = cache.length;
			if (index >= cacheSize) {
				cache = Arrays.copyOf(cache, poolSize);
				this.cache = cache;
			}
			@SuppressWarnings ("unchecked")
			GItem item = (GItem)cache[index];
			if (item != null) return item;
			item = this.toItem(pool.item(index));
			cache[index] = item;
			return item;
		}

		public int put(final GItem source) {
			return this.toRef(this.target.put(this.toArray(source)));
		}

		public void clear() {
			this.cache = new Object[0];
		}

		public IAMListing getSource() {
			return this.source;
		}

		protected void setSource(final IAMListing source) {
			this.source = source;
			this.target = null;
			this.clear();
		}

		public IAMListingBuilder getTarget() {
			return this.target;
		}

		protected void setTarget(final IAMListingBuilder target) {
			this.source = target;
			this.target = target;
			this.clear();
		}

		/** Diese Methode gibt die Referenz zur gegebenen Position zurück.
		 *
		 * @param index {@link #toIndex(int) Position} des Datensatzes in {@link #getSource()}.
		 * @return Referenz. */
		public abstract int toRef(int index);

		/** Diese Methode interpretiert die gegebene Zahlenfolge als Datensatz und gibt diesen zurück.
		 *
		 * @param source Zahlenfolge.
		 * @return Datensatz.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
		public abstract GItem toItem(final IAMArray source) throws NullPointerException, IllegalArgumentException;

		/** Diese Methode ist die Umkehroperation zu {@link #toItem(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Datensatz enthält.
		 *
		 * @param source Datensatz.
		 * @return Zahlenfolge.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public abstract IAMArray toArray(final GItem source) throws NullPointerException, IllegalArgumentException;

		/** Diese Methode gibt eine Sicht auf die Liste aller Datensätze zurück.
		 *
		 * @return {@link List}-Sicht auf die Datensätze. */
		public List<GItem> toList() {
			return new ItemList();
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toList());
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class ArrayValuePool extends BasePool<FEMArray> {

		public ArrayValuePool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_ARRAY_VALUE, index);
		}

		@Override
		public FEMArray toItem(final IAMArray source) {
			return this.codec.getArrayValue(source);
		}

		@Override
		public IAMArray toArray(final FEMArray source) {
			return this.codec.getArrayArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class StringValuePool extends BasePool<FEMString> {

		public StringValuePool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_STRING_VALUE, index);
		}

		@Override
		public FEMString toItem(final IAMArray source) {
			return this.codec.getStringValue(source);
		}

		@Override
		public IAMArray toArray(final FEMString source) {
			return this.codec.getStringArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class BinaryValuePool extends BasePool<FEMBinary> {

		public BinaryValuePool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_BINARY_VALUE, index);
		}

		@Override
		public FEMBinary toItem(final IAMArray source) {
			return this.codec.getBinaryValue(source);
		}

		@Override
		public IAMArray toArray(final FEMBinary source) {
			return this.codec.getBinaryArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class IntegerValuePool extends BasePool<FEMInteger> {

		public IntegerValuePool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_INTEGER_VALUE, index);
		}

		@Override
		public FEMInteger toItem(final IAMArray source) {
			return this.codec.getIntegerValue(source);
		}

		@Override
		public IAMArray toArray(final FEMInteger source) {
			return this.codec.getIntegerArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class DecimalValuePool extends BasePool<FEMDecimal> {

		public DecimalValuePool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_DECIMAL_VALUE, index);
		}

		@Override
		public FEMDecimal toItem(final IAMArray source) {
			return this.codec.getDecimalValue(source);
		}

		@Override
		public IAMArray toArray(final FEMDecimal source) {
			return this.codec.getDecimalArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class DurationValuePool extends BasePool<FEMDuration> {

		public DurationValuePool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_DURATION_VALUE, index);
		}

		@Override
		public FEMDuration toItem(final IAMArray source) {
			return this.codec.getDurationValue(source);
		}

		@Override
		public IAMArray toArray(final FEMDuration source) {
			return this.codec.getDurationArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class DatetimeValuePool extends BasePool<FEMDatetime> {

		public DatetimeValuePool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_DATETIME_VALUE, index);
		}

		@Override
		public FEMDatetime toItem(final IAMArray source) {
			return this.codec.getDatetimeValue(source);
		}

		@Override
		public IAMArray toArray(final FEMDatetime source) {
			return this.codec.getDatetimeArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class HandlerValuePool extends BasePool<FEMHandler> {

		public HandlerValuePool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_HANDLER_VALUE, index);
		}

		@Override
		public FEMHandler toItem(final IAMArray source) {
			return this.codec.getHandlerValue(source);
		}

		@Override
		public IAMArray toArray(final FEMHandler source) {
			return this.codec.getHandlerArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class ObjectValuePool extends BasePool<FEMObject> {

		public ObjectValuePool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_OBJECT_VALUE, index);
		}

		@Override
		public FEMObject toItem(final IAMArray source) {
			return this.codec.getObjectValue(source);
		}

		@Override
		public IAMArray toArray(final FEMObject source) {
			return this.codec.getObjectArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class ProxyFunctionPool extends BasePool<FEMProxy> {

		public ProxyFunctionPool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_PROXY_FUNCTION, index);
		}

		@Override
		public FEMProxy toItem(final IAMArray source) {
			return this.codec.getProxyFunction(source);
		}

		@Override
		public IAMArray toArray(final FEMProxy source) {
			return this.codec.getProxyArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class ConcatFunctionPool extends BasePool<ConcatFunction> {

		public ConcatFunctionPool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_CONCAT_FUNCTION, index);
		}

		@Override
		public ConcatFunction toItem(final IAMArray source) {
			return this.codec.getConcatFunction(source);
		}

		@Override
		public IAMArray toArray(final ConcatFunction source) {
			return this.codec.getConcatArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class ClosureFunctionPool extends BasePool<ClosureFunction> {

		public ClosureFunctionPool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_CLOSURE_FUNCTION, index);
		}

		@Override
		public ClosureFunction toItem(final IAMArray source) {
			return this.codec.getClosureFunction(source);
		}

		@Override
		public IAMArray toArray(final ClosureFunction source) {
			return this.codec.getClosureArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class CompositeFunctionPool extends BasePool<CompositeFunction> {

		public CompositeFunctionPool(final FEMCodec codec) {
			super(codec);
		}

		@Override
		public int toRef(final int index) {
			return this.codec.toRef(FEMCodec.TYPE_COMPOSITE_FUNCTION, index);
		}

		@Override
		public CompositeFunction toItem(final IAMArray source) {
			return this.codec.getCompositeFunction(source);
		}

		@Override
		public IAMArray toArray(final CompositeFunction source) {
			return this.codec.getCompositeArray(source);
		}

	}

	/** Dieses Feld speichert den leeren {@link FEMCodec} als Leser des leeren {@link IAMIndex}. */
	public static final FEMCodec EMPTY = new FEMCodec().setSource(IAMIndex.EMPTY);

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putVoidValue()}. */
	protected static final int TYPE_VOID_VALUE = 0;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putTrueValue()}. */
	protected static final int TYPE_TRUE_VALUE = 1;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putFalseValue()}. */
	protected static final int TYPE_FALSE_VALUE = 2;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putArrayValue(FEMArray)}. */
	protected static final int TYPE_ARRAY_VALUE = 3;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putStringValue(FEMString)}. */
	protected static final int TYPE_STRING_VALUE = 4;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putBinaryValue(FEMBinary)}. */
	protected static final int TYPE_BINARY_VALUE = 5;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putIntegerValue(FEMInteger)}. */
	protected static final int TYPE_INTEGER_VALUE = 6;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putDecimalValue(FEMDecimal)}. */
	protected static final int TYPE_DECIMAL_VALUE = 7;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putDurationValue(FEMDuration)}. */
	protected static final int TYPE_DURATION_VALUE = 8;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putDatetimeValue(FEMDatetime)}. */
	protected static final int TYPE_DATETIME_VALUE = 9;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putHandlerValue(FEMHandler)}. */
	protected static final int TYPE_HANDLER_VALUE = 10;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putObjectValue(FEMObject)}. */
	protected static final int TYPE_OBJECT_VALUE = 11;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putProxyFunction(FEMProxy)}. */
	protected static final int TYPE_PROXY_FUNCTION = 12;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putParamFunction(FEMParam)}. */
	protected static final int TYPE_PARAM_FUNCTION = 13;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putConcatFunction(ConcatFunction)}. */
	protected static final int TYPE_CONCAT_FUNCTION = 14;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putClosureFunction(ClosureFunction)}. */
	protected static final int TYPE_CLOSURE_FUNCTION = 15;

	/** Dieses Feld speichert die {@link #toType(int) Typkennung} für {@link #putCompositeFunction(CompositeFunction)}. */
	protected static final int TYPE_COMPOSITE_FUNCTION = 16;

	/** Dieses Feld speichert die {@link #toRef(int, int)} Wertreferenz} des über {@link #get()} und {@link #set(FEMValue)} modifizierbaren Werts. */
	protected int propertyRef;

	/** Dieses Feld speichert den in {@link #setSource(IAMIndex)} initialisierten {@link IAMIndex}, in aus welchem alle übrogen {@code source}-Datenfelder
	 * bestückt werden müssen. */
	protected IAMIndex sourceIndex = IAMIndex.EMPTY;

	/** Dieses Feld speichert die erste Auflistung im {@link #sourceIndex}. */
	protected IAMListing sourceIndexListing = IAMListing.EMPTY;

	/** Dieses Feld speichert die erste Abbildung im {@link #sourceIndex}. */
	protected IAMMapping sourceIndexMapping = IAMMapping.EMPTY;

	/** Dieses Feld speichert den in {@link #setTarget()} initialisierten {@link IAMIndexBuilder}, in welchen bis aus {@link #targetIndexListing} und
	 * {@link #targetIndexMapping} alle übrigen {@code target}-Datenfelder eingefügt werden müssen. */
	protected IAMIndexBuilder targetIndex;

	/** Dieses Feld speichert die erste Auflistung im {@link #targetIndex}. */
	protected IAMListingBuilder targetIndexListing;

	/** Dieses Feld speichert die erste Abbildung im {@link #targetIndex}. */
	protected IAMMappingBuilder targetIndexMapping;

	/** Dieses Feld speichert die Auflistung der Wertlisten. */
	protected ArrayValuePool arrayValuePool = new ArrayValuePool(this);

	/** Dieses Feld speichert die Auflistung der Zeichenketten. */
	protected StringValuePool stringValuePool = new StringValuePool(this);

	/** Dieses Feld speichert die Auflistung der Bytefolgen. */
	protected BinaryValuePool binaryValuePool = new BinaryValuePool(this);

	/** Dieses Feld speichert die Auflistung der Dezimalzahlen. */
	protected IntegerValuePool integerValuePool = new IntegerValuePool(this);

	/** Dieses Feld speichert die Auflistung der Dezimalbrüche. */
	protected DecimalValuePool decimalValuePool = new DecimalValuePool(this);

	/** Dieses Feld speichert die Auflistung der Zeitspannen. */
	protected DurationValuePool durationValuePool = new DurationValuePool(this);

	/** Dieses Feld speichert die Auflistung der Zeitangaben. */
	protected DatetimeValuePool datetimeValuePool = new DatetimeValuePool(this);

	/** Dieses Feld speichert die Auflistung der Funktionszeiger. */
	protected HandlerValuePool handlerValuePool = new HandlerValuePool(this);

	/** Dieses Feld speichert die Auflistung der Objektreferenzen. */
	protected ObjectValuePool objectValuePool = new ObjectValuePool(this);

	/** Dieses Feld speichert die Auflistung der Funktionsplatzhalter. */
	protected ProxyFunctionPool proxyFunctionPool = new ProxyFunctionPool(this);

	/** Dieses Feld speichert die Auflistung der Funktionketten. */
	protected ConcatFunctionPool concatFunctionPool = new ConcatFunctionPool(this);

	/** Dieses Feld speichert die Auflistung der Funktionsbindungen. */
	protected ClosureFunctionPool closureFunctionPool = new ClosureFunctionPool(this);

	/** Dieses Feld speichert die Auflistung der Funktionsaufrufe. */
	protected CompositeFunctionPool compositeFunctionPool = new CompositeFunctionPool(this);

	@Override
	public FEMValue get() {
		return this.getValue(this.propertyRef);
	}

	@Override
	public void set(final FEMValue value) {
		this.propertyRef = this.putValue(value);
	}

	/** Diese Methode gibt die Datenquelle zurück, auf welche alle lesenden Methoden zugreifen.
	 *
	 * @return Datenquelle. */
	public IAMIndex getSource() {
		return this.sourceIndex;
	}

	/** Diese Methode bestückt diesen {@link FEMCodec} zum Lesen der gegebenen {@link IAMIndex Datenquelle} und gibt {@code this} zurück. Die {@link #getTarget()
	 * Datensenke} wird dabei auf {@code null} gesetzt.
	 * 
	 * @param source Datenquelle.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public FEMCodec setSource(final IAMIndex source) throws NullPointerException {
		this.sourceIndex = Objects.notNull(source);
		this.sourceIndexListing = source.listing(0);
		this.sourceIndexMapping = source.mapping(0);
		this.targetIndex = null;
		this.targetIndexListing = null;
		this.targetIndexMapping = null;
		this.setSource(this.arrayValuePool, FEMCodec.TYPE_ARRAY_VALUE);
		this.setSource(this.stringValuePool, FEMCodec.TYPE_STRING_VALUE);
		this.setSource(this.binaryValuePool, FEMCodec.TYPE_BINARY_VALUE);
		this.setSource(this.integerValuePool, FEMCodec.TYPE_INTEGER_VALUE);
		this.setSource(this.decimalValuePool, FEMCodec.TYPE_DECIMAL_VALUE);
		this.setSource(this.durationValuePool, FEMCodec.TYPE_DURATION_VALUE);
		this.setSource(this.datetimeValuePool, FEMCodec.TYPE_DATETIME_VALUE);
		this.setSource(this.handlerValuePool, FEMCodec.TYPE_HANDLER_VALUE);
		this.setSource(this.objectValuePool, FEMCodec.TYPE_OBJECT_VALUE);
		this.setSource(this.proxyFunctionPool, FEMCodec.TYPE_PROXY_FUNCTION);
		this.setSource(this.concatFunctionPool, FEMCodec.TYPE_CONCAT_FUNCTION);
		this.setSource(this.closureFunctionPool, FEMCodec.TYPE_CLOSURE_FUNCTION);
		this.setSource(this.compositeFunctionPool, FEMCodec.TYPE_COMPOSITE_FUNCTION);
		return this;
	}

	/** Diese Methode ist eine Abkürzung für {@link BasePool#setSource(IAMListing) pool.setSource(this.getListing(type))}.
	 *
	 * @see #getListing(int) */
	protected void setSource(final BasePool<?> pool, final int type) throws NullPointerException {
		pool.setSource(this.getListing(type));
	}

	/** Diese Methode gibt die Datensenke zurück, auf welche alle schreibenden Methoden zugreifen.
	 *
	 * @return Datensenke oder {@code null}. */
	public IAMIndexBuilder getTarget() {
		return this.targetIndex;
	}

	/** Diese Methode bestückt diesen {@link FEMCodec} zur Befüllung einer neuen {@link IAMIndexBuilder Datensenke} und gibt {@code this} zurück. Die
	 * {@link #getSource() Datenquelle} ist dabei gleich dieser Datensenke.
	 * 
	 * @return {@code this}. */
	public FEMCodec setTarget() {
		this.sourceIndex = this.targetIndex = new IAMIndexBuilder();
		this.targetIndex.put(-1, this.sourceIndexListing = (this.targetIndexListing = new IAMListingBuilder()));
		this.targetIndex.put(-1, this.sourceIndexMapping = (this.targetIndexMapping = new IAMMappingBuilder()));
		this.setTarget(this.arrayValuePool, FEMCodec.TYPE_ARRAY_VALUE);
		this.setTarget(this.stringValuePool, FEMCodec.TYPE_STRING_VALUE);
		this.setTarget(this.binaryValuePool, FEMCodec.TYPE_BINARY_VALUE);
		this.setTarget(this.integerValuePool, FEMCodec.TYPE_INTEGER_VALUE);
		this.setTarget(this.decimalValuePool, FEMCodec.TYPE_DECIMAL_VALUE);
		this.setTarget(this.durationValuePool, FEMCodec.TYPE_DURATION_VALUE);
		this.setTarget(this.datetimeValuePool, FEMCodec.TYPE_DATETIME_VALUE);
		this.setTarget(this.handlerValuePool, FEMCodec.TYPE_HANDLER_VALUE);
		this.setTarget(this.objectValuePool, FEMCodec.TYPE_OBJECT_VALUE);
		this.setTarget(this.proxyFunctionPool, FEMCodec.TYPE_PROXY_FUNCTION);
		this.setTarget(this.concatFunctionPool, FEMCodec.TYPE_CONCAT_FUNCTION);
		this.setTarget(this.closureFunctionPool, FEMCodec.TYPE_CLOSURE_FUNCTION);
		this.setTarget(this.compositeFunctionPool, FEMCodec.TYPE_COMPOSITE_FUNCTION);
		return this;
	}

	/** Diese Methode ist eine Abkürzung für {@link BasePool#setTarget(IAMListingBuilder) pool.setTarget(this.putListing(type))}.
	 *
	 * @see #putListing(int) */
	protected void setTarget(final BasePool<?> pool, final int type) throws NullPointerException {
		pool.setTarget(this.putListing(type));
	}

	/** Diese Methode gibt den Wert zur gegebenen {@link #toRef(int, int) Wertreferenz} zurück. Wenn deren {@link #toType(int) Typkennung} unbekannt ist, wird
	 * {@link FEMVoid#INSTANCE} geliefert.
	 *
	 * @param ref Wertreferenz.
	 * @return Wert.
	 * @throws IllegalArgumentException Wenn die Wertreferenz ungültig ist. */
	public FEMValue getValue(final int ref) throws IllegalArgumentException {
		final int type = this.toType(ref), index = this.toIndex(ref);
		switch (type) {
			case TYPE_VOID_VALUE:
				return FEMVoid.INSTANCE;
			case TYPE_TRUE_VALUE:
				return FEMBoolean.TRUE;
			case TYPE_FALSE_VALUE:
				return FEMBoolean.FALSE;
			case TYPE_ARRAY_VALUE:
				return this.getArrayValue(index);
			case TYPE_STRING_VALUE:
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
			default:
				return this.getCustomValue(type, index);
		}
	}

	/** Diese Methode gibt die Funktion zur gegebenen {@link #toRef(int, int) Funktionsreferenz} zurück. Wenn deren {@link #toType(int) Typkennung} unbekannt ist,
	 * wird {@link FEMVoid#INSTANCE} geliefert.
	 *
	 * @param ref Funktionsreferenz.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Funktionsreferenz ungültig ist. */
	public FEMFunction getFunction(final int ref) throws IllegalArgumentException {
		final int type = this.toType(ref), index = this.toIndex(ref);
		switch (type) {
			case TYPE_VOID_VALUE:
				return FEMVoid.INSTANCE;
			case TYPE_TRUE_VALUE:
				return FEMBoolean.TRUE;
			case TYPE_FALSE_VALUE:
				return FEMBoolean.FALSE;
			case TYPE_ARRAY_VALUE:
				return this.getArrayValue(index);
			case TYPE_STRING_VALUE:
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

	/** Diese Methode gibt die Auflistung zur gegebenen Typkennung zurück. Die Typkennung wird dazu in eine Zahlenfolge {@link IAMArray#from(int...) umgewandelt}
	 * und als Schlüssel im {@link #sourceIndexMapping} {@link IAMMapping#find(IAMArray) gesucht}. Die erste Zahl des zum Schlüssel ermittelten
	 * {@link IAMMapping#value(int) Werts} wird dann als {@link IAMIndex#listing(int) Position der Auflistung} interpretiert, unter welcher sie in
	 * {@link #sourceIndex} verwaltet wird.
	 *
	 * @param type {@link #toType(int) Typkennunng} der Auflistung.
	 * @return Auflistung zur Typkennung. */
	protected IAMListing getListing(final int type) {
		return this.sourceIndex.listing(this.sourceIndexMapping.value(this.sourceIndexMapping.find(type), 0));
	}

	/** Diese Methode gibt die Wertliste zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #arrayValuePool}.
	 * @return Wertliste.
	 * @throws IllegalArgumentException Wenn {@link #getArrayValue(IAMArray)} diese auslöst. */
	protected FEMArray getArrayValue(final int index) throws IllegalArgumentException {
		return this.arrayValuePool.get(index);
	}

	/** Diese Methode gibt die Zeichenkette zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #stringValuePool}.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn {@link #getStringValue(IAMArray)} diese auslöst. */
	protected FEMString getStringValue(final int index) throws IllegalArgumentException {
		return this.stringValuePool.get(index);
	}

	/** Diese Methode gibt die Bytefolge zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #binaryValuePool}.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn {@link #getBinaryValue(IAMArray)} diese auslöst. */
	protected FEMBinary getBinaryValue(final int index) throws IllegalArgumentException {
		return this.binaryValuePool.get(index);
	}

	/** Diese Methode gibt die Dezimalzanl zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #integerValuePool}.
	 * @return Dezimalzahl.
	 * @throws IllegalArgumentException Wenn {@link #getIntegerValue(IAMArray)} diese auslöst. */
	protected FEMInteger getIntegerValue(final int index) throws IllegalArgumentException {
		return this.integerValuePool.get(index);
	}

	/** Diese Methode gibt den Dezimalbruch zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #decimalValuePool}.
	 * @return Dezimalbruch.
	 * @throws IllegalArgumentException Wenn {@link #getDecimalValue(IAMArray)} diese auslöst. */
	protected FEMDecimal getDecimalValue(final int index) throws IllegalArgumentException {
		return this.decimalValuePool.get(index);
	}

	/** Diese Methode gibt die Zeitspanne zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #datetimeValuePool}.
	 * @return Zeitspanne.
	 * @throws IllegalArgumentException Wenn {@link #getDurationValue(IAMArray)} diese auslöst. */
	protected FEMDuration getDurationValue(final int index) throws IllegalArgumentException {
		return this.durationValuePool.get(index);
	}

	/** Diese Methode gibt die Zeitangabe zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #datetimeValuePool}.
	 * @return Zeitangabe.
	 * @throws IllegalArgumentException Wenn {@link #getDatetimeValue(IAMArray)} diese auslöst. */
	protected FEMDatetime getDatetimeValue(final int index) throws IllegalArgumentException {
		return this.datetimeValuePool.get(index);
	}

	/** Diese Methode gibt den Funktionszeiger zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #handlerValuePool}.
	 * @return Funktionszeiger.
	 * @throws IllegalArgumentException Wenn {@link #getHandlerValue(IAMArray)} diese auslöst. */
	protected FEMHandler getHandlerValue(final int index) throws IllegalArgumentException {
		return this.handlerValuePool.get(index);
	}

	/** Diese Methode gibt die Objektreferenz zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #objectValuePool}.
	 * @return Objektreferenz.
	 * @throws IllegalArgumentException Wenn {@link #getObjectValue(IAMArray)} diese auslöst. */
	protected FEMObject getObjectValue(final int index) throws IllegalArgumentException {
		return this.objectValuePool.get(index);
	}

	/** Diese Methode gibt den Wert zur gegebenen {@link #toType(int) Typkennung} und {@link #toIndex(int) Position} zurück.
	 *
	 * @param type Typkennung
	 * @param index Position.
	 * @return Wert.
	 * @throws IllegalArgumentException Wenn die Wertreferenz ungültig ist. */
	protected FEMValue getCustomValue(final int type, final int index) throws IllegalArgumentException {
		return FEMVoid.INSTANCE;
	}

	/** Diese Methode gibt den Funktionsplatzhalter zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #proxyFunctionPool}.
	 * @return Funktionsplatzhalter.
	 * @throws IllegalArgumentException Wenn {@link #getProxyFunction(IAMArray)} diese auslöst. */
	protected FEMProxy getProxyFunction(final int index) throws IllegalArgumentException {
		return this.proxyFunctionPool.get(index);
	}

	/** Diese Methode gibt die Funktionkette zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #concatFunctionPool}.
	 * @return Funktionkette.
	 * @throws IllegalArgumentException Wenn {@link #getConcatFunction(IAMArray)} diese auslöst. */
	protected ConcatFunction getConcatFunction(final int index) throws IllegalArgumentException {
		return this.concatFunctionPool.get(index);
	}

	/** Diese Methode gibt die Funktionsbindung zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #closureFunctionPool}.
	 * @return Funktionsbindung.
	 * @throws IllegalArgumentException Wenn {@link #getClosureFunction(IAMArray)} diese auslöst. */
	protected ClosureFunction getClosureFunction(final int index) throws IllegalArgumentException {
		return this.closureFunctionPool.get(index);
	}

	/** Diese Methode gibt den Funktionsaufruf zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #compositeFunctionPool}.
	 * @return Funktionsaufruf.
	 * @throws IllegalArgumentException Wenn {@link #getCompositeFunction(IAMArray)} diese auslöst. */
	protected CompositeFunction getCompositeFunction(final int index) throws IllegalArgumentException {
		return this.compositeFunctionPool.get(index);
	}

	/** Diese Methode gibt die Funktion zur gegebenen {@link #toType(int) Typkennung} und {@link #toIndex(int) Position} zurück.
	 *
	 * @param type Typkennung
	 * @param index Position.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Funktionsreferenz ungültig ist. */
	protected FEMFunction getCustomFunction(final int type, final int index) throws IllegalArgumentException {
		return FEMVoid.INSTANCE;
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getArrayValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Wertliste enthält. Eine über
	 * {@link FEMArray#compact(boolean)} indizierte Wertliste wird mit der Indizierung kodiert.
	 *
	 * @param source Wertliste.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putValue(FEMValue)} diese auslöst. */
	public IAMArray getArrayArray(final FEMArray source) throws NullPointerException, IllegalArgumentException {
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

	/** Diese Methode ist die Umkehroperation zu {@link #getStringValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Zeichenkette enthält.
	 *
	 * @param source Zeichenkette.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getStringArray(final FEMString source) {
		return source.toArray();
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getBinaryValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Bytefolge enthält.
	 *
	 * @param source Bytefolge.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getBinaryArray(final FEMBinary source) throws NullPointerException {
		return source.toArray();
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getIntegerValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Dezimalzahl enthält.
	 *
	 * @param source Dezimalzahl.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getIntegerArray(final FEMInteger source) throws NullPointerException {
		return this.getIntegerArrayImpl(source.value());
	}

	@SuppressWarnings ("javadoc")
	IAMArray getIntegerArrayImpl(final long value) {
		return IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getDecimalValue(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Dezimalbruch enthält.
	 *
	 * @param source Dezimalbruch.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getDecimalArray(final FEMDecimal source) throws NullPointerException {
		return this.getIntegerArrayImpl(Double.doubleToLongBits(source.value()));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getDurationValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Zeitspanne enthält.
	 *
	 * @param source Zeitspanne.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getDurationArray(final FEMDuration source) throws NullPointerException {
		return this.getIntegerArrayImpl(source.value());
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getDatetimeValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Zeitangabe enthält.
	 *
	 * @param source Zeitangabe.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getDatetimeArray(final FEMDatetime source) throws NullPointerException {
		return this.getIntegerArrayImpl(source.value());
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

	/** Diese Methode ist die Umkehroperation zu {@link #getObjectValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Objektreferenz enthält.
	 *
	 * @param source Objektreferenz.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getObjectArray(final FEMObject source) throws NullPointerException {
		return this.getIntegerArrayImpl(source.value());
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getProxyFunction(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Funktionsplatzhalter
	 * enthält.
	 *
	 * @param source Funktionsplatzhalter.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putFunction(FEMFunction)} diese auslöst. */
	public IAMArray getProxyArray(final FEMProxy source) throws NullPointerException, IllegalArgumentException {
		return IAMArray.from(this.toIndex(this.putStringValue(source.name())), this.putFunction(source.get()));
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

	/** Diese Methode ist die Umkehroperation zu {@link #getClosureFunction(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Funktionsbindung enthält.
	 *
	 * @param source Funktionsbindung.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putFunction(FEMFunction)} diese auslöst. */
	public IAMArray getClosureArray(final ClosureFunction source) throws NullPointerException, IllegalArgumentException {
		return IAMArray.from(this.putFunction(source.function()));
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

	@SuppressWarnings ("javadoc")
	IAMArray getCompositeArrayImpl(final FEMFunction function, final FEMFunction... params) throws NullPointerException, IllegalArgumentException {
		final int length = params.length;
		final int[] result = new int[length + 1];
		result[0] = this.putFunction(function);
		for (int i = 0; i < length; i++) {
			result[i + 1] = this.putFunction(params[i]);
		}
		return IAMArray.from(result);
	}

	/** Diese Methode gibt eine Wertliste zurück, deren Elemente in der gegebenen Zahlenfolge sind. Die Zahlenfolge kann dazu in einer der folgenden Strukturen
	 * vorliegen:
	 * <ul>
	 * <li>Einfach - {@code (value[length], hash[1], length[1])}<br>
	 * Die Zahlenfolge beginnt mit den über {@link #putValue(FEMValue)} ermittelten {@link #toRef(int, int) Wertreferenzen} der Elemente der gegebenen Wertliste
	 * und endet mit dem {@link FEMArray#hashCode() Streuwert} sowie der {@link FEMArray#length() Länge} der Wertliste.</li>
	 * <li>Indiziert - {@code (value[length], hash[1], index[length], range[count], length[1])}<br>
	 * Die Zahlenfolge beginnt ebenfalls mit den Wertreferenzen sowie dem Streuwert und endet auch mit der Länge der Wertliste. Dazwischen enthält sie die Inhalte
	 * sowie die Größen der Streuwertbereiche.</li>
	 * </ul>
	 * 
	 * @param source Zahlenfolge.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMArray getArrayValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		final int length1 = source.length() - 2, length2 = source.get(length1 + 1);
		if (length1 == length2) return new IndexArray(length1, this, source);
		return new IndexArray2(length2, this, source);
	}

	/** Diese Methode gibt eine Sicht auf die Liste aller Wertlisten zurück.
	 *
	 * @return Wertlisten. */
	public List<FEMArray> getArrayValues() {
		return this.arrayValuePool.toList();
	}

	/** Diese Methode gibt eine Zeichenkette zurück, deren Codepoints in der gegebenen Zahlenfolge {@link FEMString#toArray() kodiert} sind.
	 *
	 * @param source Zahlenfolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMString getStringValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return FEMString.from(source);
	}

	/** Diese Methode gibt eine Sicht auf die Liste aller Zeichenketten zurück.
	 *
	 * @return Zeichenketten. */
	public List<FEMString> getStringValues() {
		return this.stringValuePool.toList();
	}

	/** Diese Methode gibt die Bytefolge zur gegebenen Zahlenfolge zurück, deren Bytes in der gegebenen Zahlenfolge {@link FEMBinary#toArray() kodiert} sind.
	 *
	 * @param source Zahlenfolge.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMBinary getBinaryValue(final IAMArray source) {
		return FEMBinary.from(source);
	}

	/** Diese Methode gibt eine Sicht auf die Liste aller Bytefolgen zurück.
	 *
	 * @return Bytefolgen. */
	public List<FEMBinary> getBinaryValues() {
		return this.binaryValuePool.toList();
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

	@SuppressWarnings ("javadoc")
	long getIntegerValueImpl(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() != 2) throw new IllegalArgumentException();
		return Integers.toLong(source.get(1), source.get(0));
	}

	/** Diese Methode gibt eine Sicht auf die Liste aller Dezimalzahlen zurück.
	 *
	 * @return Dezimalzahlen. */
	public List<FEMInteger> getIntegerValues() {
		return this.integerValuePool.toList();
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

	/** Diese Methode gibt eine Sicht auf die Liste aller Dezimalbrüche zurück.
	 *
	 * @return Dezimalbrüche. */
	public List<FEMDecimal> getDecimalValues() {
		return this.decimalValuePool.toList();
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

	/** Diese Methode gibt eine Sicht auf die Liste aller Zeitspannen zurück.
	 *
	 * @return Zeitspannen. */
	public List<FEMDuration> getDurationValues() {
		return this.durationValuePool.toList();
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

	/** Diese Methode gibt eine Sicht auf die Liste aller Zeitangaben zurück.
	 *
	 * @return Zeitangaben. */
	public List<FEMDatetime> getDatetimeValues() {
		return this.datetimeValuePool.toList();
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionszeiger und gibt diese zurück. Die Zahlenfolge muss dazu aus einer
	 * {@link #toRef(int, int) Funktionsreferenz} bestehen, welche über {@link #getFunction(int)} interpretiert wird.
	 *
	 * @param source Zahlenfolge.
	 * @return Funktionszeiger.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMHandler getHandlerValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() != 1) throw new IllegalArgumentException();
		return new FEMHandler(this.getFunction(source.get(0)));
	}

	/** Diese Methode gibt eine Sicht auf die Liste aller Funktionszeiger zurück.
	 *
	 * @return Funktionszeiger. */
	public List<FEMHandler> getHandlerValues() {
		return this.handlerValuePool.toList();
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

	/** Diese Methode gibt eine Sicht auf die Liste aller Objektreferenzen zurück.
	 *
	 * @return Objektreferenzen. */
	public List<FEMObject> getObjectValues() {
		return this.objectValuePool.toList();
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionsplatzhalter und gibt diese zurück. Die Zahlenfolge muss dazu aus drei Zahlen bestehen,
	 * der über {@link #putValue(FEMValue)} ermittelten Wertreferenzen seiner {@link FEMProxy#id() Kennung}, der über {@link #putStringValue(FEMString)}
	 * ermittelten Position seines {@link FEMProxy#name() Namnes} sowie der über {@link #putFunction(FEMFunction)} ermittelten Funktionsreferenz seines
	 * {@link FEMProxy#get() Ziels}.
	 *
	 * @param source Zahlenfolge.
	 * @return Funktionsplatzhalter.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMProxy getProxyFunction(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() != 3) throw new IllegalArgumentException();
		return new FEMProxy(this.getValue(source.get(0)), this.getStringValue(source.get(1)), this.getFunction(source.get(2)));
	}

	/** Diese Methode gibt eine Sicht auf die Liste aller Funktionsplatzhalter zurück.
	 *
	 * @return Funktionsplatzhalter. */
	public List<FEMProxy> getProxyFunctions() {
		return this.proxyFunctionPool.toList();
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionkette und gibt diese zurück. Die Zahlenfolge muss dazu aus den über
	 * {@link #putFunction(FEMFunction)} ermittelten {@link #toRef(int, int) Funktionsreferenzen} der {@link ConcatFunction#function() verketteten Funktion} und
	 * iher {@link ConcatFunction#params() Parameterfunktionen} bestehen.
	 *
	 * @param source Zahlenfolge.
	 * @return Funktionkette.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public ConcatFunction getConcatFunction(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return new ConcatFunction(this.getCompositeFunctionImpl(source), this.getCompositeParamsImpl(source));
	}

	/** Diese Methode gibt eine Sicht auf die Liste aller Funktionketten zurück.
	 *
	 * @return Funktionketten. */
	public List<ConcatFunction> getConcatFunctions() {
		return this.concatFunctionPool.toList();
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionsbindungen und gibt diese zurück. Die Zahlenfolge muss dazu aus einer
	 * {@link #toRef(int, int) Funktionsreferenz} bestehen, welche über {@link #getFunction(int)} interpretiert wird.
	 *
	 * @param source Zahlenfolge.
	 * @return Funktionsbindungen.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public ClosureFunction getClosureFunction(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() != 1) throw new IllegalArgumentException();
		return new ClosureFunction(this.getFunction(source.get(0)));
	}

	/** Diese Methode gibt eine Sicht auf die Liste aller Funktionsbindungen zurück.
	 *
	 * @return Funktionsbindungen. */
	public List<ClosureFunction> getClosureFunctions() {
		return this.closureFunctionPool.toList();
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionsaufruf und gibt diese zurück. Die Zahlenfolge muss dazu aus den über
	 * {@link #putFunction(FEMFunction)} ermittelten {@link #toRef(int, int) Funktionsreferenzen} der {@link CompositeFunction#function() aufgerufenen Funktion}
	 * und iher {@link CompositeFunction#params() Parameterfunktionen} bestehen.
	 *
	 * @param source Zahlenfolge.
	 * @return Funktionsaufruf.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public CompositeFunction getCompositeFunction(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return new CompositeFunction(this.getCompositeFunctionImpl(source), this.getCompositeParamsImpl(source));
	}

	/** Diese Methode gibt eine Sicht auf die Liste aller Funktionsaufrufe zurück.
	 *
	 * @return Funktionsaufrufe. */
	public List<CompositeFunction> getCompositeFunctions() {
		return this.compositeFunctionPool.toList();
	}

	@SuppressWarnings ("javadoc")
	FEMFunction[] getCompositeParamsImpl(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		final int length = source.length() - 1;
		if (length < 0) throw new IllegalArgumentException();
		final FEMFunction[] result = new FEMFunction[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.getFunction(source.get(i + 1));
		}
		return result;
	}

	@SuppressWarnings ("javadoc")
	FEMFunction getCompositeFunctionImpl(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() == 0) throw new IllegalArgumentException();
		return this.getFunction(source.get(0));
	}

	/** Diese Methode erzeugt die Auflistung zur gegebenen Typkennung und gibt sie zurück. Die Position der Auflistung, unter welcher sie in {@link #targetIndex}
	 * verwaltet wird, wird dazu auch inter der Typkennung als Schlüssel im {@link #targetIndexMapping} hinterlegt.
	 *
	 * @param type {@link #toType(int) Typkennunng} der Auflistung.
	 * @return Auflistung zur Typkennung. */
	protected IAMListingBuilder putListing(final int type) {
		final IAMListingBuilder result = new IAMListingBuilder();
		this.targetIndexMapping.put(IAMArray.from(type), IAMArray.from(this.targetIndex.put(-1, result)));
		return result;
	}

	/** Diese Methode nimmt den gegebenen Wert in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Wert.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Wert nicht aufgenommen werden kann. */
	public int putValue(final FEMValue source) throws NullPointerException, IllegalArgumentException {
		switch (source.type().id()) {
			case FEMVoid.ID:
				return this.putVoidValue();
			case FEMArray.ID:
				return this.putArrayValue((FEMArray)source.data());
			case FEMHandler.ID:
				return this.putHandlerValue((FEMHandler)source.data());
			case FEMBoolean.ID:
				return this.putBooleanValue((FEMBoolean)source.data());
			case FEMString.ID:
				return this.putStringValue((FEMString)source.data());
			case FEMBinary.ID:
				return this.putBinaryValue((FEMBinary)source.data());
			case FEMInteger.ID:
				return this.putIntegerValue((FEMInteger)source.data());
			case FEMDecimal.ID:
				return this.putDecimalValue((FEMDecimal)source.data());
			case FEMDuration.ID:
				return this.putDurationValue((FEMDuration)source.data());
			case FEMDatetime.ID:
				return this.putDatetimeValue((FEMDatetime)source.data());
			case FEMObject.ID:
				return this.putObjectValue((FEMObject)source.data());
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die {@link #toRef(int, int)} Wertreferenz} auf {@link FEMVoid#INSTANCE} zurück.
	 *
	 * @return Wertreferenz. */
	public int putVoidValue() {
		return this.toRef(FEMCodec.TYPE_VOID_VALUE, 0);
	}

	/** Diese Methode gibt die {@link #toRef(int, int)} Wertreferenz} auf {@link FEMBoolean#TRUE} zurück.
	 *
	 * @return Wertreferenz. */
	public int putTrueValue() {
		return this.toRef(FEMCodec.TYPE_TRUE_VALUE, 0);
	}

	/** Diese Methode gibt die {@link #toRef(int, int)} Wertreferenz} auf {@link FEMBoolean#FALSE} zurück.
	 *
	 * @return Wertreferenz. */
	public int putFalseValue() {
		return this.toRef(FEMCodec.TYPE_FALSE_VALUE, 0);
	}

	/** Diese Methode nimmt die gegebene Wertliste in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Dezimalbruch.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #getArrayArray(FEMArray)} diese auslöst. */
	public int putArrayValue(final FEMArray source) throws NullPointerException, IllegalArgumentException {
		return this.arrayValuePool.put(source);
	}

	/** Diese Methode nimmt die gegebene Zeichenkette in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Zeichenkette.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getStringArray(FEMString)} diese auslöst. */
	public int putStringValue(final FEMString source) throws NullPointerException {
		return this.stringValuePool.put(source);
	}

	/** Diese Methode nimmt die gegebene Bytefolge in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Bytefolge.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getBinaryArray(FEMBinary)} diese auslöst. */
	public int putBinaryValue(final FEMBinary source) throws NullPointerException {
		return this.binaryValuePool.put(source);
	}

	/** Diese Methode nimmt die gegebene Dezimalzahl in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Dezimalzahl.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getIntegerArray(FEMInteger)} diese auslöst. */
	public int putIntegerValue(final FEMInteger source) throws NullPointerException {
		return this.integerValuePool.put(source);
	}

	/** Diese Methode nimmt den gegebenen Dezimalbruch in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Dezimalbruch.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getDatetimeArray(FEMDatetime)} diese auslöst. */
	public int putDecimalValue(final FEMDecimal source) throws NullPointerException {
		return this.decimalValuePool.put(source);
	}

	/** Diese Methode nimmt die gegebene Zeitspanne in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Zeitspanne.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getDurationArray(FEMDuration)} diese auslöst. */
	public int putDurationValue(final FEMDuration source) throws NullPointerException {
		return this.durationValuePool.put(source);
	}

	/** Diese Methode nimmt die gegebene Zeitangabe in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Zeitangabe.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getDatetimeArray(FEMDatetime)} diese auslöst. */
	public int putDatetimeValue(final FEMDatetime source) throws NullPointerException {
		return this.datetimeValuePool.put(source);
	}

	/** Diese Methode gibt die Wertreferenz auf den gegebenen Wahrheitswert zurück. */
	public int putBooleanValue(final FEMBoolean source) throws NullPointerException {
		return source.value() ? this.putTrueValue() : this.putFalseValue();
	}

	/** Diese Methode nimmt den gegebenen Funktionszeiger in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Funktionszeiger.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getHandlerArray(FEMHandler)} diese auslöst. */
	public int putHandlerValue(final FEMHandler source) throws NullPointerException, IllegalArgumentException {
		return this.handlerValuePool.put(source);
	}

	/** Diese Methode nimmt die gegebene Objektreferenz in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Objektreferenz.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getObjectArray(FEMObject)} diese auslöst. */
	public int putObjectValue(final FEMObject source) throws NullPointerException {
		return this.objectValuePool.put(source);
	}

	/** Diese Methode nimmt die gegebene Funktion in die Verwaltung auf und gibt die {@link #toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktion.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Funktion nicht aufgenommen werden kann. */
	public int putFunction(final FEMFunction source) throws NullPointerException, IllegalArgumentException {
		if (source instanceof FEMValue) return this.putValue((FEMValue)source);
		if (source instanceof FEMProxy) return this.putProxyFunction((FEMProxy)source);
		if (source instanceof FEMParam) return this.putParamFunction((FEMParam)source);
		if (source instanceof ConcatFunction) return this.putConcatFunction((ConcatFunction)source);
		if (source instanceof ClosureFunction) return this.putClosureFunction((ClosureFunction)source);
		if (source instanceof CompositeFunction) return this.putCompositeFunction((CompositeFunction)source);
		throw new IllegalArgumentException();
	}

	/** Diese Methode nimmt den gegebenen Funktionsaufruf in die Verwaltung auf und gibt die {@link #toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktionsaufruf.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getProxyArray(FEMProxy)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getProxyArray(FEMProxy)} diese auslöst. */
	public int putProxyFunction(final FEMProxy source) throws NullPointerException, IllegalArgumentException {
		return this.proxyFunctionPool.put(source);
	}

	/** Diese Methode gibt die {@link #toRef(int, int)} Funktionsreferenz} auf die gegebene Parameterfunktion zurück.
	 *
	 * @param source Parameterfunktion.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public int putParamFunction(final FEMParam source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_PARAM_FUNCTION, source.index());
	}

	/** Diese Methode nimmt die gegebene Funktionkette in die Verwaltung auf und gibt die {@link #toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktionkette.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getConcatArray(ConcatFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getConcatArray(ConcatFunction)} diese auslöst. */
	public int putConcatFunction(final ConcatFunction source) throws NullPointerException, IllegalArgumentException {
		return this.concatFunctionPool.put(source);
	}

	/** Diese Methode nimmt die gegebene Funktionsbindung in die Verwaltung auf und gibt die {@link #toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktionsbindung.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst. */
	public int putClosureFunction(final ClosureFunction source) throws NullPointerException, IllegalArgumentException {
		return this.closureFunctionPool.put(source);
	}

	/** Diese Methode nimmt den gegebenen Funktionsaufruf in die Verwaltung auf und gibt die {@link #toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktionsaufruf.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getCompositeArray(CompositeFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getCompositeArray(CompositeFunction)} diese auslöst. */
	public int putCompositeFunction(final CompositeFunction source) throws NullPointerException, IllegalArgumentException {
		return this.compositeFunctionPool.put(source);
	}

	/** Diese Methode gibt eine Wert- bzw. Funktionsreferenz mit den gegebenen Markmalen zurück.
	 *
	 * @param type {@link #toType(int) Typkennung} der Referenz (0..31).
	 * @param index {@link #toIndex(int) Position} des Werts bzw. der Funktion in der ihn bzw. sie verwaltenden {@link IAMListing Auflistung}.
	 * @return Referenz auf einen Wert oder eine Funktion. */
	public int toRef(final int type, final int index) {
		return (index << 5) | this.toType(type);
	}

	/** Diese Methode gibt die Typkennung der gegebenen Wert- bzw. Funktionsreferenz zurück.
	 *
	 * @param ref {@link #toRef(int, int) Referenz} auf einen Wert oder eine Funktion
	 * @return Typkennung der Referenz (0..31). */
	public int toType(final int ref) {
		return ref & 31;
	}

	/** Diese Methode gibt die Position der gegebenen Wert- bzw. Funktionsreferenz zurück.
	 *
	 * @param ref {@link #toRef(int, int) Referenz} auf einen Wert oder eine Funktion
	 * @return Position des Werts bzw. der Funktion in der ihn bzw. sie verwaltenden {@link IAMListing Auflistung}. */
	public int toIndex(final int ref) {
		return ref >>> 5;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.arrayValuePool, this.stringValuePool, this.binaryValuePool, this.integerValuePool, this.decimalValuePool,
			this.durationValuePool, this.datetimeValuePool, this.handlerValuePool, this.objectValuePool, this.proxyFunctionPool, this.concatFunctionPool,
			this.closureFunctionPool, this.compositeFunctionPool);
	}

}
