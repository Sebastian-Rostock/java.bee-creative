package bee.creative.fem;

import java.util.AbstractList;
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
import bee.creative.util.AbstractHashData;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Integers;
import bee.creative.util.Objects;
import bee.creative.util.Property;

// TODO de-/coder für ausgewählte fem-datentypen in iam-format (und json-format)
// json-format aus string und object[] derselben, de-/coder ebenfalls in javascript

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
			this.items = Objects.notNull(items);
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
			// (value[length], hash[1], index[length], range[count], length[1])
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

	/** Diese Klasse implementiert ein {@link FEMBinary Bytefolge}, deren Bytes als {@link IAMArray Zahlenfolge} gegeben sind. */
	protected static class IndexBinary extends FEMBinary {

		/** Dieses Feld speichert eine Zahlenfolge mit den Bytes sowie dem Streuwert der Bytefolge in der Struktur
		 * {@code (byte1, ..., byteN, hash1, hash2, hash3, hash4)}. Die Zahlenfolge ist damit stets um vier länger als die Bytefolge. */
		public final IAMArray items;

		@SuppressWarnings ("javadoc")
		public IndexBinary(final IAMArray items) throws NullPointerException, IllegalArgumentException {
			super(items.length() - 4);
			final int index = this.length();
			this.hash = Integers.toInt(items.get(index + 0), items.get(index + 1), items.get(index + 2), items.get(index + 3));
			this.items = items;
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return (byte)this.items.get(index);
		}

	}

	protected static abstract class BaseCache<GItem> implements Items<GItem> {

		Object[] items = {};

		@Override
		public GItem get(int index) {

			return null;
		}

		protected abstract GItem customGet(final int index);

		public void clear() {
			Object[] items = this.items;
			for (int i = 0, size = items.length; i < size; i++) {

			}
		}

	}

	/** Dieses Feld speichert den leeren {@link FEMCodec} als Leser des leeren {@link IAMIndex}. */
	public static final FEMCodec EMPTY = new FEMCodec().switchToLoader(IAMIndex.EMPTY);

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

	/** Dieses Feld speichert den in {@link #switchToLoader(IAMIndex)} initialisierten {@link IAMIndex}, in aus welchem alle übrogen {@code source}-Datenfelder
	 * bestückt werden müssen. */
	protected IAMIndex sourceIndex;

	/** Dieses Feld speichert die erste Auflistung im {@link #sourceIndex}. */
	protected IAMListing sourceIndexListing;

	/** Dieses Feld speichert die erste Abbildung im {@link #sourceIndex}. */
	protected IAMMapping sourceIndexMapping;

	/** Dieses Feld speichert die Auflistung der Wertlisten für {@link #getArrayValue(int)}. */
	protected IAMListing sourceArrayValuePool;

	/** Dieses Feld speichert die Auflistung der Zeichenketten für {@link #getStringValue(int)}. */
	protected IAMListing sourceStringValuePool;

	/** Dieses Feld speichert die Auflistung der Bytefolgen für {@link #getBinaryValue(int)}. */
	protected IAMListing sourceBinaryValuePool;

	/** Dieses Feld speichert die Auflistung der Dezimalzahlen für #{@link #getIntegerValue(int)}. */
	protected IAMListing sourceIntegerValuePool;

	/** Dieses Feld speichert die Auflistung der Dezimalbrüche für {@link #getDecimalValue(int)}. */
	protected IAMListing sourceDecimalValuePool;

	/** Dieses Feld speichert die Auflistung der Zeitspannen für {@link #getDurationValue(int)}. */
	protected IAMListing sourceDurationValuePool;

	/** Dieses Feld speichert die Auflistung der Zeitangaben für {@link #getDatetimeValue(int)}. */
	protected IAMListing sourceDatetimeValuePool;

	/** Dieses Feld speichert die Auflistung der Funktionszeiger für {@link #getHandlerValue(int)}. */
	protected IAMListing sourceHandlerValuePool;

	/** Dieses Feld speichert die Auflistung der Objektreferenzen für {@link #getObjectValue(int)}. */
	protected IAMListing sourceObjectValuePool;

	/** Dieses Feld speichert die Auflistung der Funktionsplatzhalter für {@link #getProxyFunction(int)}. */
	protected IAMListing sourceProxyFunctionPool;

	/** Dieses Feld speichert die Auflistung der Funktionsaufrufe für {@link #getConcatFunction(int)}. */
	protected IAMListing sourceConcatFunctionPool;

	/** Dieses Feld speichert die Auflistung der Funktionsbindungen für {@link #getClosureFunction(int)}. */
	protected IAMListing sourceClosureFunctionPool;

	/** Dieses Feld speichert die Auflistung der Funktionsaufrufe für {@link #getCompositeFunction(int)}. */
	protected IAMListing sourceCompositeFunctionPool;

	/** Dieses Feld speichert den on {@link #switchToBuilder()} initialisierten {@link IAMIndexBuilder}, in welchen bis aus {@link #targetIndexListing} und
	 * {@link #targetIndexMapping} alle übrigen {@code target}-Datenfelder eingefügt werden müssen. */
	protected IAMIndexBuilder targetIndex;

	/** Dieses Feld speichert die erste Auflistung im {@link #targetIndex}. */
	protected IAMListingBuilder targetIndexListing;

	/** Dieses Feld speichert die erste Abbildung im {@link #targetIndex}. */
	protected IAMMappingBuilder targetIndexMapping;

	/** Dieses Feld speichert die Auflistung über {@link #putArrayValue(FEMArray)} hinzugefügten Wertlisten. */
	protected IAMListingBuilder targetArrayValuePool;

	/** Dieses Feld speichert die Auflistung über {@link #putHandlerValue(FEMHandler)} hinzugefügten Funktionszeiger. */
	protected IAMListingBuilder targetHandlerValuePool;

	/** Dieses Feld speichert die Auflistung über {@link #putStringValue(FEMString)} hinzugefügten Zeichenketten. */
	protected IAMListingBuilder targetStringValuePool;

	/** Dieses Feld speichert die Auflistung über {@link #putBinaryValue(FEMBinary)} hinzugefügten Bytefolgen. */
	protected IAMListingBuilder targetBinaryValuePool;

	/** Dieses Feld speichert die Auflistung über {@link #putIntegerValue(FEMInteger)} hinzugefügten Dezimalzahlen. */
	protected IAMListingBuilder targetIntegerValuePool;

	/** Dieses Feld speichert die Auflistung über {@link #putDecimalValue(FEMDecimal)} hinzugefügten Dezimalbrüche. */
	protected IAMListingBuilder targetDecimalValuePool;

	/** Dieses Feld speichert die Auflistung über {@link #putDurationValue(FEMDuration)} hinzugefügten Zeitspannen. */
	protected IAMListingBuilder targetDurationValuePool;

	/** Dieses Feld speichert die Auflistung über {@link #putDatetimeValue(FEMDatetime)} hinzugefügten Zeitangaben. */
	protected IAMListingBuilder targetDatetimeValuePool;

	/** Dieses Feld speichert die Auflistung über {@link #putObjectValue(FEMObject)} hinzugefügten Objektreferenzen. */
	protected IAMListingBuilder targetObjectValuePool;

	/** Dieses Feld speichert die Auflistung über {@link #putProxyFunction(FEMProxy)} hinzugefügten Funktionsplatzhalter. */
	protected IAMListingBuilder targetProxyFunctionPool;

	/** Dieses Feld speichert die Auflistung über {@link #putConcatFunction(ConcatFunction)} hinzugefügten Funktionsaufrufe. */
	protected IAMListingBuilder targetConcatFunctionPool;

	/** Dieses Feld speichert die Auflistung über {@link #putClosureFunction(ClosureFunction)} hinzugefügten Funktionsbindungen. */
	protected IAMListingBuilder targetClosureFunctionPool;

	/** Dieses Feld speichert die Auflistung über {@link #putCompositeFunction(CompositeFunction)} hinzugefügten Funktionsaufrufe. */
	protected IAMListingBuilder targetCompositeFunctionPool;

	@Override
	public FEMValue get() {
		return this.getValue(this.propertyRef);
	}

	@Override
	public void set(final FEMValue value) {
		this.propertyRef = this.putValue(value);
	}

	public IAMIndexBuilder getTarget() {
		return targetIndex;
	}

	public IAMIndex getSource() {
		return sourceIndex;
	}

	/** Diese Methode bestückt die Felder zum Lesen des gegebenen {@link IAMIndex} und gibt {@code this} zurück.
	 *
	 * @param source Datenquelle.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public FEMCodec switchToLoader(final IAMIndex source) throws NullPointerException {
		this.sourceIndex = Objects.notNull(source);
		this.sourceIndexListing = source.listing(0);
		this.sourceIndexMapping = source.mapping(0);
		this.sourceArrayValuePool = this.getListing(FEMCodec.TYPE_ARRAY_VALUE);
		this.sourceStringValuePool = this.getListing(FEMCodec.TYPE_STRING_VALUE);
		this.sourceBinaryValuePool = this.getListing(FEMCodec.TYPE_BINARY_VALUE);
		this.sourceIntegerValuePool = this.getListing(FEMCodec.TYPE_INTEGER_VALUE);
		this.sourceDecimalValuePool = this.getListing(FEMCodec.TYPE_DECIMAL_VALUE);
		this.sourceDurationValuePool = this.getListing(FEMCodec.TYPE_DURATION_VALUE);
		this.sourceDatetimeValuePool = this.getListing(FEMCodec.TYPE_DATETIME_VALUE);
		this.sourceHandlerValuePool = this.getListing(FEMCodec.TYPE_HANDLER_VALUE);
		this.sourceObjectValuePool = this.getListing(FEMCodec.TYPE_OBJECT_VALUE);
		this.sourceProxyFunctionPool = this.getListing(FEMCodec.TYPE_PROXY_FUNCTION);
		this.sourceConcatFunctionPool = this.getListing(FEMCodec.TYPE_CONCAT_FUNCTION);
		this.sourceClosureFunctionPool = this.getListing(FEMCodec.TYPE_CLOSURE_FUNCTION);
		this.sourceCompositeFunctionPool = this.getListing(FEMCodec.TYPE_COMPOSITE_FUNCTION);
		this.targetIndex = null;
		this.targetIndexListing = null;
		this.targetIndexMapping = null;
		this.targetArrayValuePool = null;
		this.targetStringValuePool = null;
		this.targetBinaryValuePool = null;
		this.targetIntegerValuePool = null;
		this.targetDecimalValuePool = null;
		this.targetDurationValuePool = null;
		this.targetDatetimeValuePool = null;
		this.targetHandlerValuePool = null;
		this.targetObjectValuePool = null;
		this.targetProxyFunctionPool = null;
		this.targetConcatFunctionPool = null;
		this.targetClosureFunctionPool = null;
		this.targetCompositeFunctionPool = null;
		return this;
	}

	/** Diese Methode bestückt die Felder zur Befüllung eines neuen {@link IAMIndexBuilder} und gibt {@code this} zurück.
	 *
	 * @return {@code this}. */
	public FEMCodec switchToBuilder() {
		this.sourceIndex = this.targetIndex = new IAMIndexBuilder();
		this.targetIndex.put(-1, this.sourceIndexListing = this.targetIndexListing = new IAMListingBuilder());
		this.targetIndex.put(-1, this.sourceIndexMapping = this.targetIndexMapping = new IAMMappingBuilder());
		this.sourceArrayValuePool = this.targetArrayValuePool = this.putListing(FEMCodec.TYPE_ARRAY_VALUE);
		this.sourceStringValuePool = this.targetStringValuePool = this.putListing(FEMCodec.TYPE_STRING_VALUE);
		this.sourceBinaryValuePool = this.targetBinaryValuePool = this.putListing(FEMCodec.TYPE_BINARY_VALUE);
		this.sourceIntegerValuePool = this.targetIntegerValuePool = this.putListing(FEMCodec.TYPE_INTEGER_VALUE);
		this.sourceDecimalValuePool = this.targetDecimalValuePool = this.putListing(FEMCodec.TYPE_DECIMAL_VALUE);
		this.sourceDurationValuePool = this.targetDurationValuePool = this.putListing(FEMCodec.TYPE_DURATION_VALUE);
		this.sourceDatetimeValuePool = this.targetDatetimeValuePool = this.putListing(FEMCodec.TYPE_DATETIME_VALUE);
		this.sourceHandlerValuePool = this.targetHandlerValuePool = this.putListing(FEMCodec.TYPE_HANDLER_VALUE);
		this.sourceObjectValuePool = this.targetObjectValuePool = this.putListing(FEMCodec.TYPE_OBJECT_VALUE);
		this.sourceProxyFunctionPool = this.targetProxyFunctionPool = this.putListing(FEMCodec.TYPE_PROXY_FUNCTION);
		this.sourceConcatFunctionPool = this.targetConcatFunctionPool = this.putListing(FEMCodec.TYPE_CONCAT_FUNCTION);
		this.sourceClosureFunctionPool = this.targetClosureFunctionPool = this.putListing(FEMCodec.TYPE_CLOSURE_FUNCTION);
		this.sourceCompositeFunctionPool = this.targetCompositeFunctionPool = this.putListing(FEMCodec.TYPE_COMPOSITE_FUNCTION);
		return this;
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

	/** Diese Methode gibt die Wertliste zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceArrayValuePool}.
	 * @return Wertliste.
	 * @throws IllegalArgumentException Wenn {@link #getArrayValue(IAMArray)} diese auslöst. */
	protected FEMArray getArrayValue(final int index) throws IllegalArgumentException {
		// getItem(sourceArrayValueCache, sourceArrayValueItems, index);
		return getArrayValueImpl(index);
	}

	protected FEMArray getArrayValueImpl(final int index) throws IllegalArgumentException {
		return this.getArrayValue(this.sourceArrayValuePool.item(index));
	}

	protected Items<FEMArray> sourceArrayValueItems = new Items<FEMArray>() {

		@Override
		public FEMArray get(int index) {
			return getArrayValueImpl(index);
		}

	};

	/** Diese Methode gibt die Zeichenkette zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceStringValuePool}.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn {@link #getStringValue(IAMArray)} diese auslöst. */
	protected FEMString getStringValue(final int index) throws IllegalArgumentException {
		return this.getStringValue(this.sourceStringValuePool.item(index));
	}

	/** Diese Methode gibt die Bytefolge zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceBinaryValuePool}.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn {@link #getBinaryValue(IAMArray)} diese auslöst. */
	protected FEMBinary getBinaryValue(final int index) throws IllegalArgumentException {
		return this.getBinaryValue(this.sourceBinaryValuePool.item(index));
	}

	/** Diese Methode gibt die Dezimalzanl zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceIntegerValuePool}.
	 * @return Dezimalzahl.
	 * @throws IllegalArgumentException Wenn {@link #getIntegerValue(IAMArray)} diese auslöst. */
	protected FEMInteger getIntegerValue(final int index) throws IllegalArgumentException {
		return this.getIntegerValue(this.sourceIntegerValuePool.item(index));
	}

	/** Diese Methode gibt den Dezimalbruch zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceDecimalValuePool}.
	 * @return Dezimalbruch.
	 * @throws IllegalArgumentException Wenn {@link #getDecimalValue(IAMArray)} diese auslöst. */
	protected FEMDecimal getDecimalValue(final int index) throws IllegalArgumentException {
		return this.getDecimalValue(this.sourceDecimalValuePool.item(index));
	}

	/** Diese Methode gibt die Zeitspanne zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceDatetimeValuePool}.
	 * @return Zeitspanne.
	 * @throws IllegalArgumentException Wenn {@link #getDurationValue(IAMArray)} diese auslöst. */
	protected FEMDuration getDurationValue(final int index) throws IllegalArgumentException {
		return this.getDurationValue(this.sourceDurationValuePool.item(index));
	}

	/** Diese Methode gibt die Zeitangabe zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceDatetimeValuePool}.
	 * @return Zeitangabe.
	 * @throws IllegalArgumentException Wenn {@link #getDatetimeValue(IAMArray)} diese auslöst. */
	protected FEMDatetime getDatetimeValue(final int index) throws IllegalArgumentException {
		return this.getDatetimeValue(this.sourceDatetimeValuePool.item(index));
	}

	/** Diese Methode gibt den Funktionszeiger zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceHandlerValuePool}.
	 * @return Funktionszeiger.
	 * @throws IllegalArgumentException Wenn {@link #getHandlerValue(IAMArray)} diese auslöst. */
	protected FEMHandler getHandlerValue(final int index) throws IllegalArgumentException {
		return this.getHandlerValue(this.sourceHandlerValuePool.item(index));
	}

	/** Diese Methode gibt die Objektreferenz zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceObjectValuePool}.
	 * @return Objektreferenz.
	 * @throws IllegalArgumentException Wenn {@link #getObjectValue(IAMArray)} diese auslöst. */
	protected FEMObject getObjectValue(final int index) throws IllegalArgumentException {
		return this.getObjectValue(this.sourceObjectValuePool.item(index));
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
	 * @param index Position in {@link #sourceProxyFunctionPool}.
	 * @return Funktionsplatzhalter.
	 * @throws IllegalArgumentException Wenn {@link #getProxyFunction(IAMArray)} diese auslöst. */
	protected FEMProxy getProxyFunction(final int index) throws IllegalArgumentException {
		return this.getProxyFunction(this.sourceProxyFunctionPool.item(index));
	}

	/** Diese Methode gibt den Funktionsaufruf zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceConcatFunctionPool}.
	 * @return Funktionsaufruf.
	 * @throws IllegalArgumentException Wenn {@link #getConcatFunction(IAMArray)} diese auslöst. */
	protected ConcatFunction getConcatFunction(final int index) throws IllegalArgumentException {
		return this.getConcatFunction(this.sourceConcatFunctionPool.item(index));
	}

	/** Diese Methode gibt die Funktionsbindung zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceClosureFunctionPool}.
	 * @return Funktionsbindung.
	 * @throws IllegalArgumentException Wenn {@link #getClosureFunction(IAMArray)} diese auslöst. */
	protected ClosureFunction getClosureFunction(final int index) throws IllegalArgumentException {
		return this.getClosureFunction(this.sourceClosureFunctionPool.item(index));
	}

	/** Diese Methode gibt den Funktionsaufruf zurück, der unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceCompositeFunctionPool}.
	 * @return Funktionsaufruf.
	 * @throws IllegalArgumentException Wenn {@link #getCompositeFunction(IAMArray)} diese auslöst. */
	protected CompositeFunction getCompositeFunction(final int index) throws IllegalArgumentException {
		return this.getCompositeFunction(this.sourceCompositeFunctionPool.item(index));
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
		final int hash = source.hashCode(), index = source.length();
		final byte[] result = new byte[index + 4];
		result[index + 0] = (byte)(hash >>> 0);
		result[index + 1] = (byte)(hash >>> 8);
		result[index + 2] = (byte)(hash >>> 16);
		result[index + 3] = (byte)(hash >>> 24);
		source.extract(result, 0);
		return IAMArray.from(result);
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

	
	// TODO 
	/** Diese Methode ist die Umkehroperation zu {@link #getProxyFunction(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Funktionsaufruf enthält.
	 *
	 * @param source Funktionsaufruf.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #putFunction(FEMFunction)} diese auslöst. */
	public IAMArray getProxyArray(final FEMProxy source) throws NullPointerException, IllegalArgumentException {
		return IAMArray.from(this.toIndex(this.putStringValue(source.name())), this.putFunction(source.get()));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getConcatFunction(IAMArray)} und liefert eine Zahlenfolge, welche den gegebenen Funktionsaufruf enthält.
	 *
	 * @param source Funktionsaufruf.
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
	*/
	public FEMArray getArrayValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		final int length1 = source.length() - 2, length2 = source.get(length1 + 1);
		if (length1 == length2) return new IndexArray(length1, this, source);
		return new IndexArray2(length2, this, source);
	}

	/** Diese Methode gibt eine Sicht auf die Liste aller Wertlisten zurück.
	 *
	 * @return Wertlisten. */
	public List<FEMArray> getArrayValues() {
		return new AbstractList<FEMArray>() {

			@Override
			public FEMArray get(final int index) {
				if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
				return FEMCodec.this.getArrayValue(index);
			}

			@Override
			public int size() {
				return FEMCodec.this.sourceArrayValuePool.itemCount();
			}

		};
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
		return new AbstractList<FEMString>() {

			@Override
			public FEMString get(final int index) {
				if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
				return FEMCodec.this.getStringValue(index);
			}

			@Override
			public int size() {
				return FEMCodec.this.sourceStringValuePool.itemCount();
			}

		};
	}

	/** Diese Methode gibt die Bytefolge zur gegebenen Zahlenfolge zurück. Dabei werden die ersten vier Byte der Zahlenfolge als {@link FEMBinary#hashCode()
	 * Streuwert} und die darauf folgenden als Bytes der Bytefolge interpretiert.
	 *
	 * @param source Zahlenfolge.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zahlenfolge nicht als {@link IAMArray#mode() INT8/UINT8} vorliegt. */
	public FEMBinary getBinaryValue(final IAMArray source) {
		if (source.mode() != 1) throw new IllegalArgumentException();
		return new IndexBinary(source);
	}

	/** Diese Methode gibt eine Sicht auf die Liste aller Bytefolgen zurück.
	 *
	 * @return Bytefolgen. */
	public List<FEMBinary> getBinaryValues() {
		return new AbstractList<FEMBinary>() {

			@Override
			public FEMBinary get(final int index) {
				if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
				return FEMCodec.this.getBinaryValue(index);
			}

			@Override
			public int size() {
				return FEMCodec.this.sourceBinaryValuePool.itemCount();
			}

		};
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
		return new AbstractList<FEMInteger>() {

			@Override
			public FEMInteger get(final int index) {
				if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
				return FEMCodec.this.getIntegerValue(index);
			}

			@Override
			public int size() {
				return FEMCodec.this.sourceIntegerValuePool.itemCount();
			}

		};
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
		return new AbstractList<FEMDecimal>() {

			@Override
			public FEMDecimal get(final int index) {
				if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
				return FEMCodec.this.getDecimalValue(index);
			}

			@Override
			public int size() {
				return FEMCodec.this.sourceDecimalValuePool.itemCount();
			}

		};
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
		return new AbstractList<FEMDuration>() {

			@Override
			public FEMDuration get(final int index) {
				if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
				return FEMCodec.this.getDurationValue(index);
			}

			@Override
			public int size() {
				return FEMCodec.this.sourceDurationValuePool.itemCount();
			}

		};
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
		return new AbstractList<FEMDatetime>() {

			@Override
			public FEMDatetime get(final int index) {
				if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
				return FEMCodec.this.getDatetimeValue(index);
			}

			@Override
			public int size() {
				return FEMCodec.this.sourceDatetimeValuePool.itemCount();
			}

		};
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Funktionszeiger und gibt diese zurück. Die Zahlenfolge muss dazu aus einer
	 * {@link #toRef(int, int) Funktionsreferenz } bestehen, welche über {@link #getFunction(int)} interpretiert wird.
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
		return new AbstractList<FEMHandler>() {

			@Override
			public FEMHandler get(final int index) {
				if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
				return FEMCodec.this.getHandlerValue(index);
			}

			@Override
			public int size() {
				return FEMCodec.this.sourceHandlerValuePool.itemCount();
			}

		};
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
		return new AbstractList<FEMObject>() {

			@Override
			public FEMObject get(final int index) {
				if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
				return FEMCodec.this.getObjectValue(index);
			}

			@Override
			public int size() {
				return FEMCodec.this.sourceObjectValuePool.itemCount();
			}

		};
	}

	// TODO
	public FEMProxy getProxyFunction(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() != 3) throw new IllegalArgumentException();
		return new FEMProxy(getValue(source.get(0)), this.getStringValue(source.get(1)), this.getFunction(source.get(2)));
	}

	public ConcatFunction getConcatFunction(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return new ConcatFunction(this.getCompositeFunctionImpl(source), this.getCompositeParamsImpl(source));
	}

	public ClosureFunction getClosureFunction(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() == 0) throw new IllegalArgumentException();
		return new ClosureFunction(this.getFunction(source.get(0)));
	}

	public CompositeFunction getCompositeFunction(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return new CompositeFunction(this.getCompositeFunctionImpl(source), this.getCompositeParamsImpl(source));
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
		return this.toRef(FEMCodec.TYPE_ARRAY_VALUE, this.targetArrayValuePool.put(this.getArrayArray(source)));
	}

	/** Diese Methode nimmt die gegebene Zeichenkette in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Zeichenkette.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getStringArray(FEMString)} diese auslöst. */
	public int putStringValue(final FEMString source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_STRING_VALUE, this.targetStringValuePool.put(this.getStringArray(source)));
	}

	/** Diese Methode nimmt die gegebene Bytefolge in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Bytefolge.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getBinaryArray(FEMBinary)} diese auslöst. */
	public int putBinaryValue(final FEMBinary source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_BINARY_VALUE, this.targetBinaryValuePool.put(this.getBinaryArray(source)));
	}

	/** Diese Methode nimmt die gegebene Dezimalzahl in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Dezimalzahl.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getIntegerArray(FEMInteger)} diese auslöst. */
	public int putIntegerValue(final FEMInteger source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_INTEGER_VALUE, this.targetIntegerValuePool.put(this.getIntegerArray(source)));
	}

	/** Diese Methode nimmt den gegebenen Dezimalbruch in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Dezimalbruch.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getDatetimeArray(FEMDatetime)} diese auslöst. */
	public int putDecimalValue(final FEMDecimal source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_DECIMAL_VALUE, this.targetDecimalValuePool.put(this.getDecimalArray(source)));
	}

	/** Diese Methode nimmt die gegebene Zeitspanne in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Zeitspanne.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getDurationArray(FEMDuration)} diese auslöst. */
	public int putDurationValue(final FEMDuration source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_DURATION_VALUE, this.targetDurationValuePool.put(this.getDurationArray(source)));
	}

	/** Diese Methode nimmt die gegebene Zeitangabe in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Zeitangabe.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getDatetimeArray(FEMDatetime)} diese auslöst. */
	public int putDatetimeValue(final FEMDatetime source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_DATETIME_VALUE, this.targetDatetimeValuePool.put(this.getDatetimeArray(source)));
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
		return this.toRef(FEMCodec.TYPE_HANDLER_VALUE, this.targetHandlerValuePool.put(this.getHandlerArray(source)));
	}

	/** Diese Methode nimmt die gegebene Objektreferenz in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Objektreferenz.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getObjectArray(FEMObject)} diese auslöst. */
	public int putObjectValue(final FEMObject source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_OBJECT_VALUE, this.targetObjectValuePool.put(this.getObjectArray(source)));
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
		return this.toRef(FEMCodec.TYPE_PROXY_FUNCTION, this.targetProxyFunctionPool.put(this.getProxyArray(source)));
	}

	/** Diese Methode gibt die {@link #toRef(int, int)} Funktionsreferenz} auf die gegebene Parameterfunktion zurück.
	 *
	 * @param source Parameterfunktion.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public int putParamFunction(final FEMParam source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_PARAM_FUNCTION, source.index());
	}

	/** Diese Methode nimmt den gegebenen Funktionsaufruf in die Verwaltung auf und gibt die {@link #toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktionsaufruf.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getConcatArray(ConcatFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getConcatArray(ConcatFunction)} diese auslöst. */
	public int putConcatFunction(final ConcatFunction source) throws NullPointerException, IllegalArgumentException {
		return this.toRef(FEMCodec.TYPE_CONCAT_FUNCTION, this.targetConcatFunctionPool.put(this.getConcatArray(source)));
	}

	/** Diese Methode nimmt die gegebene Funktionsbindung in die Verwaltung auf und gibt die {@link #toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktionsbindung.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst. */
	public int putClosureFunction(final ClosureFunction source) throws NullPointerException, IllegalArgumentException {
		return this.toRef(FEMCodec.TYPE_CLOSURE_FUNCTION, this.targetClosureFunctionPool.put(this.getClosureArray(source)));
	}

	/** Diese Methode nimmt den gegebenen Funktionsaufruf in die Verwaltung auf und gibt die {@link #toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktionsaufruf.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getCompositeArray(CompositeFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getCompositeArray(CompositeFunction)} diese auslöst. */
	public int putCompositeFunction(final CompositeFunction source) throws NullPointerException, IllegalArgumentException {
		return this.toRef(FEMCodec.TYPE_COMPOSITE_FUNCTION, this.targetCompositeFunctionPool.put(this.getCompositeArray(source)));
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

	@Override
	public String toString() {
		return Objects.toFormatString(true, true, this, "arrayValues", this.getArrayValues(), "stringValues", this.getStringValues(), "binaryValues",
			this.getBinaryValues(), "integerValues", this.getIntegerValues(), "decimalValues", this.getDecimalValues(), "durationValues", this.getDurationValues(),
			"datetimeValues", this.getDatetimeValues(), "handlerValues", this.getHandlerValues(), "objectValues", this.getObjectValues());
	}

}
