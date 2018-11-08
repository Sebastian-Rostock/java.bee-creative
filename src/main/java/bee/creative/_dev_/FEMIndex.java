package bee.creative._dev_;

import java.io.IOException;
import java.nio.ByteOrder;
import bee.creative.fem.FEMArray;
import bee.creative.fem.FEMBinary;
import bee.creative.fem.FEMBoolean;
import bee.creative.fem.FEMDatetime;
import bee.creative.fem.FEMDecimal;
import bee.creative.fem.FEMDuration;
import bee.creative.fem.FEMException;
import bee.creative.fem.FEMFunction;
import bee.creative.fem.FEMFunction.ClosureFunction;
import bee.creative.fem.FEMFunction.CompositeFunction;
import bee.creative.fem.FEMFunction.ConcatFunction;
import bee.creative.fem.FEMHandler;
import bee.creative.fem.FEMInteger;
import bee.creative.fem.FEMNative;
import bee.creative.fem.FEMObject;
import bee.creative.fem.FEMParam;
import bee.creative.fem.FEMProxy;
import bee.creative.fem.FEMString;
import bee.creative.fem.FEMTable;
import bee.creative.fem.FEMValue;
import bee.creative.fem.FEMVoid;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMBuilder.IAMIndexBuilder;
import bee.creative.iam.IAMBuilder.IAMListingBuilder;
import bee.creative.iam.IAMBuilder.IAMMappingBuilder;
import bee.creative.iam.IAMMapping;
import bee.creative.util.Objects;
import bee.creative.util.Producer;
import bee.creative.util.Property;
import bee.creative.util.Unique;

// TODO de-/coder für ausgewählte fem-datentypen in iam-format (und json-format)
// json-format aus string und object[] derselben, de-/coder ebenfalls in javascript

public class FEMIndex implements Producer<FEMValue> {

	public static final int TYPE_FUNCTION_COMPOSITE = 0;

	public static final int TYPE_FUNCTION_CONCAT = 0;

	public static final int TYPE_FUNCTION_CLOSURE = 0;

	public static final int TYPE_FUNCTION_PARAM = 0;

	public static class IndexBuilder extends FEMIndex implements Property<FEMValue> {

		private static final int DATA_INTEGER_EMPTY = 0;

		private static final int DATA_INTEGER_MINIMUM = 0;

		private static final int DATA_INTEGER_MAXIMUM = 0;

		ByteOrder byteOrder = ByteOrder.nativeOrder();

		IAMIndexBuilder indexBuilder = new IAMIndexBuilder();

		protected IAMListingBuilder arrayValueListing = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Funktionszeiger, von denen jeder aus einer von {@link #putFunction(FEMFunction)} gelieferten {@code int}-Zahl
		 * besteht. */
		protected IAMListingBuilder handlerValuePool = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Zeichenketten, welche gemäß {@link FEMString#toArray()} kodiert sind. */
		protected IAMListingBuilder stringValueListing = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Bytefolgen, welche gemäß {@link FEMBinary#toArray()} kodiert sind. */
		protected IAMListingBuilder binaryValueListing = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Dezimalzahlen, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
		protected IAMListingBuilder integerValueListing = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Dezimalbrüche, von denen jeder aus zwei {@code int}-Zahlen besteht (lo, hi). */
		protected IAMListingBuilder decimalValueListing = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Zeitspannen, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
		protected IAMListingBuilder durationValueListing = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Zeitangaben, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
		protected IAMListingBuilder datetimeValueListing = new IAMListingBuilder();

		/** Dieses Feld speichert die über {@link #customPutObject(FEMObject)} erfassten {@link FEMObject Referenzen}. Der {@link FEMObject#value()} jedes
		 * {@link FEMObject} wird als zweielementige {@link IAMArray Zahlenfolge} kodiert. */
		protected IAMListingBuilder objectValueListing = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Tabellen, von denen jede aus drei {@code int}-Zahlen besteht (keyArrayIdx, valueArrayIdx, tableMappingIdx). */
		protected IAMListingBuilder tableValueListing = new IAMListingBuilder();

		protected Unique<byte[], Integer> tableMappingUnique = new Unique<byte[], Integer>() {

			@Override
			protected Integer customBuild(final byte[] source) {
				try {
					return new Integer(IndexBuilder.this.indexBuilder.putMapping(IAMMapping.from(source)));
				} catch (final IOException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public int hash(final Object source) {
				return Objects.deepHash(source);
			}

			@Override
			public boolean equals(final Object source1, final Object source2) {
				return Objects.deepEquals(source1, source2);
			}

		};

		IAMListingBuilder proxyFunctionPool = new IAMListingBuilder();

		IAMListingBuilder concatFunctionPool = new IAMListingBuilder();

		IAMListingBuilder closureFunctionPool = new IAMListingBuilder();

		IAMListingBuilder compositeFunctionPool = new IAMListingBuilder();

		protected FEMValue property;

		protected void fillBuilder() {
			this.indexBuilder.putListing(this.arrayValueListing);
		}

		public int putValue(final FEMValue source) throws IllegalArgumentException {
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
					return this.customPutInteger((FEMInteger)source.data());
				case FEMDecimal.ID:
					return this.customPutDecimal((FEMDecimal)source.data());
				case FEMDuration.ID:
					return this.customPutDuration((FEMDuration)source.data());
				case FEMDatetime.ID:
					return this.customPutDatetime((FEMDatetime)source.data());
				case FEMObject.ID:
					return this.customPutObject((FEMObject)source.data());
				case FEMTable.ID:
					return this.customPutTable((FEMTable)source.data());
			}
			throw new IllegalArgumentException();
		}

		public int[] putValues(final FEMValue... source) throws IllegalArgumentException {
			final int length = source.length;
			final int[] result = new int[length];
			for (int i = 0; i < length; i++) {
				result[i] = this.putValue(source[i]);
			}
			return result;
		}

		public int putFunction(final FEMFunction source) throws FEMException {
			if (source instanceof FEMValue) return this.putValue((FEMValue)source);
			if (source instanceof FEMProxy) return this.customPutProxy((FEMProxy)source);
			if (source instanceof FEMParam) return this.putParamFunction((FEMParam)source);
			if (source instanceof FEMParam) return this.putParamFunction((FEMParam)source);
			if (source instanceof ConcatFunction) return this.putConcatFunction((ConcatFunction)source);
			if (source instanceof ClosureFunction) return this.customPutClosure((ClosureFunction)source);
			if (source instanceof CompositeFunction) return this.customPutComposite((CompositeFunction)source);
			throw new IllegalArgumentException();
		}

		public int[] putFunctions(final FEMFunction... source) throws NullPointerException, IllegalArgumentException {
			final int length = source.length;
			final int[] result = new int[length];
			for (int i = 0; i < length; i++) {
				result[i] = this.putFunction(source[i]);
			}
			return result;
		}

		private int putLong(final IAMListingBuilder integerPool2, final long value) {
			return 0;
		}

		protected int customPutProxy(final FEMProxy source) {

			throw new IllegalArgumentException();
		}

		protected int customPutProxy(final int nameRef, final int functionRef) {

			throw new IllegalArgumentException();
		}

		protected int putParamFunction(final FEMParam source) throws NullPointerException {
			final int index = source.index();
			return FEMIndex.toRef(FEMIndex.TYPE_FUNCTION_PARAM, index);
		}

		protected int putConcatFunction(final ConcatFunction source) throws NullPointerException, IllegalArgumentException {
			final IAMArray method = IAMArray.from(this.putFunction(source.function()));
			final IAMArray params = IAMArray.from(this.putFunctions(source.params()));
			final IAMArray array = method.concat(params).compact();
			final int index = this.concatFunctionPool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_FUNCTION_CONCAT, index);
		}

		protected int customPutClosure(final ClosureFunction source) throws NullPointerException, IllegalArgumentException {
			final IAMArray array = IAMArray.from(this.putFunction(source.function()));
			final int index = this.closureFunctionPool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_FUNCTION_CLOSURE, index);
		}

		protected int customPutComposite(final CompositeFunction source) throws NullPointerException, IllegalArgumentException {
			final IAMArray method = IAMArray.from(this.putFunction(source.function()));
			final IAMArray params = IAMArray.from(this.putFunctions(source.params()));
			final IAMArray array = method.concat(params).compact();
			final int index = this.compositeFunctionPool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_FUNCTION_COMPOSITE, index);
		}

		protected int putVoidValue() {
			return FEMIndex.toRef(FEMIndex.TYPE_CONST, FEMIndex.DATA_VOID);
		}

		protected int putArrayValue(final FEMArray source) throws NullPointerException, IllegalArgumentException {
			final IAMArray array = IAMArray.from(this.putValues(source.value()));
			final int index = this.arrayValueListing.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_ARRAY, index);
		}

		protected int putHandlerValue(final FEMHandler source) throws NullPointerException, IllegalArgumentException {
			final IAMArray array = IAMArray.from(this.putFunction(source.value()));
			final int index = this.handlerValuePool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_HANDLER, index);
		}

		protected int putBooleanValue(final FEMBoolean source) throws NullPointerException {
			return FEMIndex.toRef(FEMIndex.TYPE_CONST, source.value() ? FEMIndex.DATA_TRUE : FEMIndex.DATA_FALSE);
		}

		protected int putStringValue(final FEMString source) throws NullPointerException {
			final IAMArray array = source.toArray();
			final int index = this.stringValueListing.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_STRING, index);
		}

		protected int putBinaryValue(final FEMBinary source) throws NullPointerException {
			final IAMArray array = source.toArray();
			final int index = this.binaryValueListing.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_BINARY, index);
		}

		protected int customPutInteger(final FEMInteger source) throws NullPointerException {
			if(source.equals(FEMInteger.EMPTY)) return toRef(TYPE_CONST, DATA_INTEGER_EMPTY); // XXX muss das sein?
			if(source.equals(FEMInteger.MINIMUM)) return toRef(TYPE_CONST, DATA_INTEGER_MINIMUM);
			if(source.equals(FEMInteger.MAXIMUM)) return toRef(TYPE_CONST, DATA_INTEGER_MAXIMUM);
			final long value = source.value();
			final int index = this.putLong(this.integerValueListing, value);
			return FEMIndex.toRef(FEMIndex.TYPE_INTEGER, index);
		}

		protected int customPutDecimal(final FEMDecimal source) throws NullPointerException {
			final long value = Double.doubleToLongBits(source.value());
			final int index = this.putLong(this.decimalValueListing, value);
			return FEMIndex.toRef(FEMIndex.TYPE_DECIMAL, index);
		}

		protected int customPutDuration(final FEMDuration source) throws NullPointerException {
			final long value = source.value();
			final int index = this.putLong(this.durationValueListing, value);
			return FEMIndex.toRef(FEMIndex.TYPE_DURATION, index);
		}

		protected int customPutDatetime(final FEMDatetime source) throws NullPointerException {
			final long value = source.value();
			final int index = this.putLong(this.datetimeValueListing, value);
			return FEMIndex.toRef(FEMIndex.TYPE_DATETIME, index);
		}

		protected int customPutObject(final FEMObject source) throws NullPointerException {
			final long value = source.value();
			final int index = this.putLong(this.integerValueListing, value);
			return FEMIndex.toRef(FEMIndex.TYPE_OBJECT, index);
		}

		protected int customPutTable(final FEMTable data) {
			final IAMMapping mapping = new IAMMappingBuilder();
			// TODO

			final int tableIdx = this.tableMappingUnique.get(mapping.toBytes(this.byteOrder));
			return FEMIndex.toRef(FEMIndex.TYPE_TABLE, tableIdx);
		}

		@Override
		public FEMValue get() {
			return super.get();
		}

		@Override
		public void set(final FEMValue value) {

		}

	}

	public static abstract class IndexLoader extends FEMIndex {

	}

	public static final int TYPE_TABLE = 0;

	protected static final int TYPE_DURATION = 0;

	protected static final int TYPE_DECIMAL = 0;

	protected static final int TYPE_HANDLER = 0;

	protected static final int TYPE_DATETIME = 0;

	protected static final int TYPE_OBJECT = 0;

	protected static final int TYPE_ARRAY = 0;

	protected static final int TYPE_STRING = 0;

	protected static final int TYPE_BINARY = 0;

	protected static final int TYPE_INTEGER = 0;

	public static final int TYPE_CONST = 0;

	public static final int DATA_VOID = 0;

	public static final int DATA_TRUE = 0;

	public static final int DATA_FALSE = 0;

	static int toRef(final int type, final int data) {
		return (data << 4) | type;
	}

	// niederwertige 4 bit
	// PT bei VALUE in array oder als Parameterliste

	// 00 const VALUE
	// 00.00 void
	// 00.01 true
	// 00.02 false
	// 00.xx min/max/empty von integer, decimal, datetime, duration usw + CUSTOM VALUE ab ???
	// 01 aray data = [valueRef, ..., valueRef]
	// 10 table data = mappingIdx
	// 02 binary
	// 03 string
	// 09 object
	// 04 integer
	// 05 decimal
	// 06 duration
	// 07 datetime
	// 08 handler -> data = handlerIdx -> handlerItem = VALUE/FUNCTION
	// 00..10 kommen in array vor
	// 11..14 für value reserviert
	// 15 function -> weiter 4 bit unterscheiden den Typ

	// 15.00 const FUNCTION -> data ist ID (CUSTOM/SYSTEM)
	// 15.01 param fun -> data ist FEMParam.index()
	// 15.02 closure -> data analog zu handlerIdx
	// 15.03 proxy fun -> data ist proxyIdx, proxy = [strRef, funRef]
	// 15.04 concat
	// 15.05 composite
	// 15.06..15.15 reserviert

	static int typeOf(final int ref) {
		return ref & 15;
	}

	static int dataOf(final int ref) {
		return ref >>> 4;
	}

	@Override
	public FEMValue get() {
		return FEMVoid.INSTANCE;
	}

	public FEMValue value(final int ref) {
		return FEMVoid.INSTANCE;
	}

	public FEMFunction function(final int ref) {
		return FEMVoid.INSTANCE;
	}

}
