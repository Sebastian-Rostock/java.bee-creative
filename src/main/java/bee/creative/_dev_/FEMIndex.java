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
import bee.creative.fem.FEMTable.CompositeTable;
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

	@SuppressWarnings ("javadoc")
	protected class IndexArray extends FEMArray {

		public final FEMIndex index;

		public final IAMArray array;

		public IndexArray(final FEMIndex index, final IAMArray array) {
			super(array.length() - 1);
			this.hash = array.get(0);
			this.index = index;
			this.array = array;
		}

		@Override
		protected FEMValue customGet(final int index) {
			return this.index.value(this.array.get(index + 1));
		}

	}

	@SuppressWarnings ("javadoc")
	protected class IndexArray2 extends IndexArray {

		public final FEMValue[] values;

		public IndexArray2(final FEMIndex index, final IAMArray array) {
			super(index, array);
			this.values = new FEMValue[this.length()];
		}

		@Override
		protected FEMValue customGet(final int index) {
			final FEMValue result = this.values[index];
			if (result != null) return result;
			return this.values[index] = super.customGet(index);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class IndexTable extends CompositeTable {

		public static class Keys extends FEMArray {

			public final FEMArray items;

			public final IAMMapping mapping;

			public Keys(final FEMArray items, final IAMMapping mapping) {
				super(items.length());
				this.hash = items.hash();
				this.items = items;
				this.mapping = mapping;
			}

			@Override
			protected FEMValue customGet(final int index) {
				return this.items.get(index);
			}

			@Override
			protected int customFind(final FEMValue that, final int offset) {
				final int entry = this.mapping.find(that.hashCode());
				if (entry < 0) return -1;
				final IAMArray value = this.mapping.value(entry);
				final int length = value.length();
				for (int i = 0; i < length; i++) {
					final int result = value.get(i);
					if (that.equals(this.items.get(result))) return result < offset ? -1 : result;
				}
				return -1;
			}

		}

		public IndexTable(final FEMArray keys, final FEMArray values, final IAMMapping mapping) {
			super(new Keys(keys, mapping), values);
		}

	}

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

		/** Dieses Feld speichert die Auflistung der Tabellen, von denen jede aus drei {@code int}-Zahlen besteht (keyArrayIdx, valueArrayIdx, tableMappingIdx). Die
		 * dabei referenzierte Abbildung besitzt Einträge der Form (keyHash)::(keyIdx1, ..., keyIdxN). */
		protected IAMListingBuilder tableValuePool = new IAMListingBuilder();

		protected IAMListingBuilder proxyFunctionPool = new IAMListingBuilder();

		protected IAMListingBuilder concatFunctionPool = new IAMListingBuilder();

		protected IAMListingBuilder closureFunctionPool = new IAMListingBuilder();

		protected IAMListingBuilder compositeFunctionPool = new IAMListingBuilder();

		protected int propertyRef;

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
			return this.toRef(FEMIndex.TYPE_VOID_VALUE, 0);
		}

		/** Diese Methode fügt die gegebene Wertliste in den {@link #arrayValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putArrayValue(final FEMArray source) throws NullPointerException, IllegalArgumentException {
			final IAMArray hash = IAMArray.from(source.hash());
			final IAMArray items = IAMArray.from(this.putValues(source.value()));
			final IAMArray array = hash.concat(items).compact();
			final int index = this.arrayValuePool.put(array);
			return this.toRef(FEMIndex.TYPE_ARRAY_VALUE, index);
		}

		/** Diese Methode fügt den gegebenen Funktionszeiger in den {@link #handlerValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putHandlerValue(final FEMHandler source) throws NullPointerException, IllegalArgumentException {
			final IAMArray array = IAMArray.from(this.putFunction(source.value()));
			final int index = this.handlerValuePool.put(array);
			return this.toRef(FEMIndex.TYPE_HANDLER_VALUE, index);
		}

		/** Diese Methode gibt die Wertreferenz auf den gegebenen Wahrheitswert zurück. */
		protected int putBooleanValue(final FEMBoolean source) throws NullPointerException {
			return this.toRef(source.value() ? FEMIndex.TYPE_TRUE_VALUE : FEMIndex.TYPE_FALSE_VALUE, 0);
		}

		/** Diese Methode fügt die gegebene Zeichenkette in den {@link #stringValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putStringValue(final FEMString source) throws NullPointerException {
			final IAMArray array = source.toArray();
			final int index = this.stringValuePool.put(array);
			return this.toRef(FEMIndex.TYPE_STRING_VALUE, index);
		}

		/** Diese Methode fügt die gegebene Bytefolge in den {@link #binaryValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putBinaryValue(final FEMBinary source) throws NullPointerException {
			final IAMArray array = source.toArray();
			final int index = this.binaryValuePool.put(array);
			return this.toRef(FEMIndex.TYPE_BINARY_VALUE, index);
		}

		/** Diese Methode fügt die gegebene Dezimalzahl in den {@link #integerValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putIntegerValue(final FEMInteger source) throws NullPointerException {
			final long value = source.value();
			final IAMArray array = this.putLong(value);
			final int index = this.integerValuePool.put(array);
			return this.toRef(FEMIndex.TYPE_INTEGER_VALUE, index);
		}

		protected IAMArray putLong(final long value) {
			return IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
		}

		/** Diese Methode fügt den gegebenen Dezimalbruch in den {@link #decimalValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putDecimalValue(final FEMDecimal source) throws NullPointerException {
			final long value = Double.doubleToLongBits(source.value());
			final IAMArray array = this.putLong(value);
			final int index = this.decimalValuePool.put(array);
			return this.toRef(FEMIndex.TYPE_DECIMAL_VALUE, index);
		}

		/** Diese Methode fügt die gegebene Zeitspanne in den {@link #durationValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putDurationValue(final FEMDuration source) throws NullPointerException {
			final long value = source.value();
			final IAMArray array = this.putLong(value);
			final int index = this.durationValuePool.put(array);
			return this.toRef(FEMIndex.TYPE_DURATION_VALUE, index);
		}

		/** Diese Methode fügt die gegebene Zeitangabe in den {@link #datetimeValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putDatetimeValue(final FEMDatetime source) throws NullPointerException {
			final long value = source.value();
			final IAMArray array = this.putLong(value);
			final int index = this.datetimeValuePool.put(array);
			return this.toRef(FEMIndex.TYPE_DATETIME_VALUE, index);
		}

		/** Diese Methode fügt die gegebene Referenz in den {@link #objectValuePool} ein und gibt die Wertreferenz darauf zurück. */
		protected int putObjectValue(final FEMObject source) throws NullPointerException {
			final long value = source.value();
			final IAMArray array = this.putLong(value);
			final int index = this.objectValuePool.put(array);
			return this.toRef(FEMIndex.TYPE_OBJECT_VALUE, index);
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

			};// TODO korrigieren
			for (final FEMArray entry: source) {
				final FEMValue key = entry.get(0), value = entry.get(1);
				final Integer keyRef = new Integer(this.putValue(key)), keyHash = new Integer(key.hashCode()), valueRef = new Integer(this.putValue(value));
				final List<Integer> items = unique.get(keyHash);
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
			final int keysIdx = this.toIndex(this.putArrayValue(source.keys()));
			final int valuesIdx = this.toIndex(this.putArrayValue(source.values()));
			final int mappingIdx = this.indexBuilder.put(mapping);
			final IAMArray array = IAMArray.from(keysIdx, valuesIdx, mappingIdx);
			final int index = this.tableValuePool.put(array);
			return this.toRef(FEMIndex.TYPE_TABLE_VALUE, index);
		}

		public int putFunction(final FEMFunction source) throws FEMException {
			if (source instanceof FEMValue) return this.putValue((FEMValue)source);
			if (source instanceof FEMProxy) return this.putProxyFunction((FEMProxy)source);
			if (source instanceof FEMParam) return this.putParamFunction((FEMParam)source);
			if (source instanceof ConcatFunction) return this.putConcatFunction((ConcatFunction)source);
			if (source instanceof ClosureFunction) return this.putClosureFunction((ClosureFunction)source);
			if (source instanceof CompositeFunction) return this.putCompositeFunction((CompositeFunction)source);
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

		protected int putProxyFunction(final FEMProxy source) {
			final int nameIdx = this.toIndex(this.putStringValue(FEMString.from(source.name())));
			final int functionRef = this.putFunction(source.get());
			final IAMArray array = IAMArray.from(nameIdx, functionRef);
			final int index = this.proxyFunctionPool.put(array);
			return this.toRef(FEMIndex.TYPE_PROXY_FUNCTION, index);
		}

		protected int putParamFunction(final FEMParam source) throws NullPointerException {
			final int index = source.index();
			return this.toRef(FEMIndex.TYPE_PARAM_FUNCTION, index);
		}

		protected int putConcatFunction(final ConcatFunction source) throws NullPointerException, IllegalArgumentException {
			final IAMArray method = IAMArray.from(this.putFunction(source.function()));
			final IAMArray params = IAMArray.from(this.putFunctions(source.params()));
			final IAMArray array = method.concat(params).compact();
			final int index = this.concatFunctionPool.put(array);
			return this.toRef(FEMIndex.TYPE_CONCAT_FUNCTION, index);
		}

		protected int putClosureFunction(final ClosureFunction source) throws NullPointerException, IllegalArgumentException {
			final IAMArray array = IAMArray.from(this.putFunction(source.function()));
			final int index = this.closureFunctionPool.put(array);
			return this.toRef(FEMIndex.TYPE_CLOSURE_FUNCTION, index);
		}

		protected int putCompositeFunction(final CompositeFunction source) throws NullPointerException, IllegalArgumentException {
			final IAMArray method = IAMArray.from(this.putFunction(source.function()));
			final IAMArray params = IAMArray.from(this.putFunctions(source.params()));
			final IAMArray array = method.concat(params).compact();
			final int index = this.compositeFunctionPool.put(array);
			return this.toRef(FEMIndex.TYPE_COMPOSITE_FUNCTION, index);
		}

		@Override
		public FEMValue get() {
			return this.value(this.propertyRef);
		}

		@Override
		public void set(final FEMValue value) {
			this.propertyRef = this.putValue(value);
		}

		@Override
		protected FEMArray getArrayValue(final int index) {
			return this.getArrayValue(this.arrayValuePool.item(index));
		}

		@Override
		protected FEMString getStringValue(final int index) {
			return this.getStringValue(this.stringValuePool.item(index));
		}

		@Override
		protected FEMBinary getBinaryValue(final int index) {
			return this.getBinaryValue(this.binaryValuePool.item(index));
		}

		@Override
		protected FEMInteger getIntegerValue(final int index) {
			return this.getIntegerValue(this.integerValuePool.item(index));
		}

		@Override
		protected FEMDecimal getDecimalValue(final int index) {
			return this.getDecimalValue(this.decimalValuePool.item(index));
		}

		@Override
		protected FEMDatetime getDatetimeValue(final int index) {
			return this.getDatetimeValue(this.datetimeValuePool.item(index));
		}

		@Override
		protected FEMDuration getDurationValue(final int index) {
			return this.getDurationValue(this.durationValuePool.item(index));
		}

		@Override
		protected FEMHandler getHandlerValue(final int index) {
			return this.getHandlerValue(this.handlerValuePool.item(index));
		}

		@Override
		protected FEMObject getObjectValue(final int index) {
			return this.getObjectValue(this.objectValuePool.item(index));
		}

		@Override
		protected FEMTable getTableValue(final int index) {
			return this.getTableValue(this.tableValuePool.item(index));
		}

	}

	public static abstract class IndexLoader extends FEMIndex {

	}

	protected static final int TYPE_VOID_VALUE = 0;

	protected static final int TYPE_TRUE_VALUE = 1;

	protected static final int TYPE_FALSE_VALUE = 2;

	protected static final int TYPE_ARRAY_VALUE = 3;

	protected static final int TYPE_STRING_VALUE = 4;

	protected static final int TYPE_BINARY_VALUE = 5;

	protected static final int TYPE_INTEGER_VALUE = 6;

	protected static final int TYPE_DECIMAL_VALUE = 7;

	protected static final int TYPE_DATETIME_VALUE = 8;

	protected static final int TYPE_DURATION_VALUE = 9;

	protected static final int TYPE_HANDLER_VALUE = 10;

	protected static final int TYPE_OBJECT_VALUE = 11;

	protected static final int TYPE_TABLE_VALUE = 12;

	protected static final int TYPE_PROXY_FUNCTION = 13;

	protected static final int TYPE_PARAM_FUNCTION = 14;

	protected static final int TYPE_CONCAT_FUNCTION = 15;

	protected static final int TYPE_CLOSURE_FUNCTION = 16;

	protected static final int TYPE_COMPOSITE_FUNCTION = 17;

	@Override
	public FEMValue get() {
		return FEMVoid.INSTANCE;
	}

	/** Diese Methode gibt den Wert zu gegebenen Wertreferenz zurück. Wenn diese ungültig ist, wird {@link FEMVoid#INSTANCE} geliefert.
	 *
	 * @param ref Wertreferenz.
	 * @return Wert. */
	public FEMValue value(final int ref) {
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
			case TYPE_TABLE_VALUE:
				return this.getTableValue(index);
			default:
				return this.getCustomValue(type, index);
		}
	}

	public FEMFunction function(final int ref) {
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
			case TYPE_TABLE_VALUE:
				return this.getTableValue(index);
			case TYPE_PROXY_FUNCTION:
				return this.getProxyFunction(index);
			case TYPE_PARAM_FUNCTION:
				return this.getParamFunction(index);
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

	protected int toRef(final int type, final int index) {
		return (index << 5) | type;
	}

	protected int toType(final int ref) {
		return ref & 31;
	}

	protected int toIndex(final int ref) {
		return ref >>> 5;
	}

	protected long getLong(final IAMArray array) {
		return Integers.toLong(array.get(1), array.get(0));
	}

	protected FEMArray getArrayValue(final int index) {
		return FEMArray.EMPTY;
	}

	protected FEMArray getArrayValue(final IAMArray array) {
		return new IndexArray(this, array);
	}

	protected FEMString getStringValue(final int index) {
		return FEMString.EMPTY;
	}

	protected FEMString getStringValue(final IAMArray array) {
		return FEMString.from(array);
	}

	protected FEMBinary getBinaryValue(final int index) {
		return FEMBinary.EMPTY;
	}

	protected FEMBinary getBinaryValue(final IAMArray array) {
		return FEMBinary.from(array);
	}

	protected FEMInteger getIntegerValue(final int index) {
		return FEMInteger.EMPTY;
	}

	protected FEMInteger getIntegerValue(final IAMArray array) {
		return new FEMInteger(this.getLong(array));
	}

	protected FEMDecimal getDecimalValue(final int index) {
		return FEMDecimal.EMPTY;
	}

	protected FEMDecimal getDecimalValue(final IAMArray array) {
		return new FEMDecimal(Double.longBitsToDouble(this.getLong(array)));
	}

	protected FEMDatetime getDatetimeValue(final int index) {
		return FEMDatetime.EMPTY;
	}

	protected FEMDatetime getDatetimeValue(final IAMArray array) {
		return new FEMDatetime(this.getLong(array));
	}

	protected FEMDuration getDurationValue(final int index) {
		return FEMDuration.EMPTY;
	}

	protected FEMDuration getDurationValue(final IAMArray array) {
		return new FEMDuration(this.getLong(array));
	}

	protected FEMHandler getHandlerValue(final int index) {
		return FEMHandler.EMPTY;
	}

	protected FEMHandler getHandlerValue(final IAMArray array) {
		return FEMHandler.from(this.function(array.get(0)));
	}

	protected FEMObject getObjectValue(final int index) {
		return FEMObject.EMPTY;
	}

	protected FEMObject getObjectValue(final IAMArray array) {
		return new FEMObject(this.getLong(array));
	}

	protected FEMTable getTableValue(final int index) {
		return FEMTable.EMPTY;
	}

	protected FEMTable getTableValue(final IAMArray array) {
		return new IndexTable(this.getArrayValue(array.get(0)), this.getArrayValue(array.get(1)), this.getTableMapping(array.get(2)));
	}

	/**
	 * Abbildung von [key.hashCode] auf [keyIdx1, ..., keyIdxN] 
	 */
	protected IAMMapping getTableMapping(final int index) {
		return IAMMapping.EMPTY;
	}

	protected FEMValue getCustomValue(final int type, final int index) {
		return FEMVoid.INSTANCE;
	}

	protected FEMFunction getProxyFunction(final int index) {
		return null;
	}

	protected FEMFunction getParamFunction(final int index) {
		return null;
	}

	protected FEMFunction getConcatFunction(final int index) {
		return null;
	}

	protected FEMFunction getClosureFunction(final int index) {
		return null;
	}

	protected FEMFunction getCompositeFunction(final int index) {
		return null;
	}

	protected FEMFunction getCustomFunction(final int type, final int index) {
		return FEMVoid.INSTANCE;
	}

}
