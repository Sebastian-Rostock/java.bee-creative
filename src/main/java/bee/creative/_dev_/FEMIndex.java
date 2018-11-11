package bee.creative._dev_;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
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
import bee.creative.iam.IAMEntry;
import bee.creative.iam.IAMMapping;
import bee.creative.util.Integers;
import bee.creative.util.Producer;
import bee.creative.util.Property;
import bee.creative.util.Unique;

// TODO de-/coder für ausgewählte fem-datentypen in iam-format (und json-format)
// json-format aus string und object[] derselben, de-/coder ebenfalls in javascript

public class FEMIndex implements Producer<FEMValue> {

	public static class IndexBuilder extends FEMIndex implements Property<FEMValue> {

		/** Dieses Feld speichert den {@link IAMIndexBuilder}, in welchen alle Daten einfließen. */
		protected final IAMIndexBuilder indexBuilder = new IAMIndexBuilder();

		/** Dieses Feld speichert die erste Auflistung im {@link #indexBuilder}. */
		protected final IAMListingBuilder indexListing = new IAMListingBuilder();

		/** Dieses Feld speichert die erste Abbildung im {@link #indexBuilder}. */
		protected final IAMMappingBuilder indexMapping = new IAMMappingBuilder();

		/** Dieses Feld speichert die Auflistung der Wertlisten, von denen jede aus einem Streuwert und einer Liste von {@link #putValue(FEMValue) Wertreferenzen}
		 * besteht. */
		protected IAMListingBuilder arrayValuePool = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Funktionszeiger, von denen jeder aus einer {@link #putFunction(FEMFunction) Funktionsreferenz} besteht. */
		protected IAMListingBuilder handlerValuePool = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Zeichenketten, welche gemäß {@link FEMString#toArray()} kodiert sind. */
		protected IAMListingBuilder stringValuePool = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Bytefolgen, welche gemäß {@link FEMBinary#toArray()} kodiert sind. */
		protected IAMListingBuilder binaryValuePool = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Dezimalzahlen, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
		protected IAMListingBuilder integerValuePool = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Dezimalbrüche, von denen jeder aus zwei {@code int}-Zahlen besteht (lo, hi). */
		protected IAMListingBuilder decimalValuePool = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Zeitspannen, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
		protected IAMListingBuilder durationValuePool = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Zeitangaben, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
		protected IAMListingBuilder datetimeValuePool = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Referenzen, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
		protected IAMListingBuilder objectValuePool = new IAMListingBuilder();

		/** Dieses Feld speichert die Auflistung der Tabellen, von denen jede aus drei {@code int}-Zahlen besteht (keyArrayRef, valueArrayRef, tableMappingIdx). Die
		 * dabei referenzierte Abbildung besitzt Einträge der Form (keyHash)::(key1, value1, ..., keyN, valueN). */
		protected IAMListingBuilder tableValuePool = new IAMListingBuilder();

		IAMListingBuilder proxyFunctionPool = new IAMListingBuilder();

		IAMListingBuilder concatFunctionPool = new IAMListingBuilder();

		IAMListingBuilder closureFunctionPool = new IAMListingBuilder();

		IAMListingBuilder compositeFunctionPool = new IAMListingBuilder();

		protected FEMValue property;

		public IndexBuilder() {
			this.indexBuilder.put(-1, this.indexListing);
			this.indexBuilder.put(-1, this.indexMapping);
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
					return this.putIntegerValue((FEMInteger)source.data());
				case FEMDecimal.ID:
					return this.putDecimalValue((FEMDecimal)source.data());
				case FEMDuration.ID:
					return this.putDurationValue((FEMDuration)source.data());
				case FEMDatetime.ID:
					return this.putDatetimeValue((FEMDatetime)source.data());
				case FEMObject.ID:
					return this.putObjectValue((FEMObject)source.data());
				case FEMTable.ID:
					return this.putTableValue((FEMTable)source.data());
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

		/** Diese Methode gibt die Wertreferenz auf {@link FEMVoid#INSTANCE} zurück. */
		protected int putVoidValue() {
			return FEMIndex.toRef(FEMIndex.TYPE_CONST, FEMIndex.DATA_VOID);
		}

		/** Diese Methode fügt die gegebene Wertliste in den {@link #arrayValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putArrayValue(final FEMArray source) throws NullPointerException, IllegalArgumentException {
			if (source.equals(FEMArray.EMPTY)) return FEMIndex.toRef(FEMIndex.TYPE_CONST, FEMIndex.DATA_ARRAY_EMPTY);
			final IAMArray hash = IAMArray.from(source.hash());
			final IAMArray items = IAMArray.from(this.putValues(source.value()));
			final IAMArray array = hash.concat(items).compact();
			final int index = this.arrayValuePool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_ARRAY, index);
		}

		/** Diese Methode fügt den gegebenen Funktionszeiger in den {@link #handlerValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putHandlerValue(final FEMHandler source) throws NullPointerException, IllegalArgumentException {
			final IAMArray array = IAMArray.from(this.putFunction(source.value()));
			final int index = this.handlerValuePool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_HANDLER, index);
		}

		/** Diese Methode gibt die Wertreferenz auf den gegebenen Wahrheitswert zurück. */
		protected int putBooleanValue(final FEMBoolean source) throws NullPointerException {
			return FEMIndex.toRef(FEMIndex.TYPE_CONST, source.value() ? FEMIndex.DATA_TRUE : FEMIndex.DATA_FALSE);
		}

		/** Diese Methode fügt die gegebene Zeichenkette in den {@link #stringValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putStringValue(final FEMString source) throws NullPointerException {
			if (source.equals(FEMString.EMPTY)) return FEMIndex.toRef(FEMIndex.TYPE_CONST, FEMIndex.DATA_STRING_EMPTY);
			final IAMArray array = source.toArray();
			final int index = this.stringValuePool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_STRING, index);
		}

		/** Diese Methode fügt die gegebene Bytefolge in den {@link #binaryValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putBinaryValue(final FEMBinary source) throws NullPointerException {
			if (source.equals(FEMBinary.EMPTY)) return FEMIndex.toRef(FEMIndex.TYPE_CONST, FEMIndex.DATA_BINARY_EMPTY);
			final IAMArray array = source.toArray();
			final int index = this.binaryValuePool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_BINARY, index);
		}

		/** Diese Methode fügt die gegebene Dezimalzahl in den {@link #integerValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putIntegerValue(final FEMInteger source) throws NullPointerException {
			final long value = source.value();
			final IAMArray array = IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
			final int index = this.integerValuePool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_INTEGER, index);
		}

		/** Diese Methode fügt den gegebenen Dezimalbruch in den {@link #decimalValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putDecimalValue(final FEMDecimal source) throws NullPointerException {
			final long value = Double.doubleToLongBits(source.value());
			final IAMArray array = IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
			final int index = this.decimalValuePool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_DECIMAL, index);
		}

		/** Diese Methode fügt die gegebene Zeitspanne in den {@link #durationValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putDurationValue(final FEMDuration source) throws NullPointerException {
			final long value = source.value();
			final IAMArray array = IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
			final int index = this.durationValuePool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_DURATION, index);
		}

		/** Diese Methode fügt die gegebene Zeitangabe in den {@link #datetimeValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putDatetimeValue(final FEMDatetime source) throws NullPointerException {
			final long value = source.value();
			final IAMArray array = IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
			final int index = this.datetimeValuePool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_DATETIME, index);
		}

		/** Diese Methode fügt die gegebene Referenz in den {@link #objectValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putObjectValue(final FEMObject source) throws NullPointerException {
			final long value = source.value();
			final IAMArray array = IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
			final int index = this.objectValuePool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_OBJECT, index);
		}

		/** Diese Methode fügt die gegebene Tabelle in den {@link #tableValuePool} ein und gibt die Wertreferenz darauf zurück. Ihre Schlüssel- und Wertspalten
		 * werden dabei auch über {@link #putArrayValue(FEMArray)} in den {@link #arrayValuePool} eingetragen. Die Abbildung von Schlüsseln auf Werte wird dazu als
		 * {@link IAMMapping} realisiert und im {@link #indexBuilder} eingetragen. Der {@link IAMEntry#key()} besteht aus dem Streuwert des Schlüssels. Der
		 * {@link IAMEntry#value()} enthält dazu die Auflistung entsprechenden Schlüsselreferenz-Wertreferenz-Paare. */
		protected int putTableValue(final FEMTable source) throws NullPointerException {
			final Unique<Integer, List<Integer>> unique = new Unique<Integer, List<Integer>>() {
		
				@Override
				protected List<Integer> customBuild(final Integer source) {
					return new ArrayList<>();
				}
		
			};
			for (final FEMArray entry: source) {
				final Integer keyRef = new Integer(this.putValue(entry.get(0)));
				final Integer valueRef = new Integer(this.putValue(entry.get(1)));
				final List<Integer> items = unique.get(keyRef);
				items.add(keyRef);
				items.add(valueRef);
			}
			final IAMMappingBuilder mapping = new IAMMappingBuilder();
			for (final Entry<Integer, List<Integer>> entry: unique.mapping().entrySet()) {
				final IAMArray key = IAMArray.from(entry.getKey().intValue());
				final IAMArray value = IAMArray.from(entry.getValue());
				mapping.put(key, value);
			}
			if (unique.mapping().size() != mapping.entryCount()) throw new IllegalArgumentException();
			final int keysRef = this.putArrayValue(source.keys());
			final int valuesRef = this.putArrayValue(source.values());
			final int mappingRef = this.indexBuilder.put(mapping);
			final IAMArray array = IAMArray.from(keysRef, valuesRef, mappingRef);
			final int index = this.tableValuePool.put(array);
			return FEMIndex.toRef(FEMIndex.TYPE_TABLE, index);
		}

		public int putFunction(final FEMFunction source) throws FEMException {
			if (source instanceof FEMValue) return this.putValue((FEMValue)source);
			if (source instanceof FEMProxy) return this.customPutProxy((FEMProxy)source);
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

		protected int customPutProxy(final FEMProxy source) {
		
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

	public static final int TYPE_FUNCTION_COMPOSITE = 0;

	public static final int TYPE_FUNCTION_CONCAT = 0;

	public static final int TYPE_FUNCTION_CLOSURE = 0;

	public static final int TYPE_FUNCTION_PARAM = 0;

	static final int DATA_VOID = 0;

	static final int DATA_TRUE = 0;

	static final int DATA_FALSE = 0;

	static final int DATA_ARRAY_EMPTY = 0;

	static final int DATA_STRING_EMPTY = 0;

	static final int DATA_BINARY_EMPTY = 0;

	static int toRef(final int type, final int data) {
		return (data << 4) | type;
	}

	// niederwertige 4 bit
	// PT bei VALUE in array oder als Parameterliste

	// 00 const VALUE
	// 00.00 void
	// 00.01 true
	// 00.02 false
	// 00.xx CUSTOM VALUE
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
