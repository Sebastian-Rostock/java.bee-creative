package bee.creative.fem;

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
import bee.creative.util.Integers;
import bee.creative.util.Objects;
import bee.creative.util.Property;

// TODO de-/coder für ausgewählte fem-datentypen in iam-format (und json-format)
// json-format aus string und object[] derselben, de-/coder ebenfalls in javascript

class FEMIndex implements Property<FEMValue> {

	/** Diese Klasse implementiert eine {@link FEMArray Wertliste}, dessen Elemente als {@link IAMArray Zahlenfolge} aus {@link FEMIndex#toRef(int, int)
	 * Wertreferenzen} gegeben sind und in {@link #customGet(int)} über einen gegebenen {@link FEMIndex} in Werte {@link FEMIndex#getValue(int) übersetzt}
	 * werden. */
	protected static class IndexArray extends FEMArray {

		/** Dieses Feld speichert den {@link FEMIndex} zur {@link FEMIndex#getValue(int) Übersetzung} der Wertreferenzen aus {@link #items}. */
		public final FEMIndex index;

		/** Dieses Feld speichert eine Zahlenfolge mit den Referenzen auf die Werte sowie dem {@link #hash() Streuwert} der Wertliste in der Struktur
		 * {@code (valueRef1, ..., valueRefN, hash)}. Die Zahlenfolge ist damit stets um ein länger als die Wertliste. Die Elemente werden über den {@link #index}
		 * aufgelöst. */
		public final IAMArray items;

		@SuppressWarnings ("javadoc")
		public IndexArray(final FEMIndex index, final IAMArray items) throws NullPointerException, IllegalArgumentException {
			super(items.length() - 1);
			this.hash = items.get(this.length);
			this.index = Objects.notNull(index);
			this.items = items;
		}

		/** {@inheritDoc} */
		@Override
		protected FEMValue customGet(final int index) {
			return this.index.getValue(this.items.get(index));
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
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return (byte)this.items.get(index);
		}

		@Override
		public byte[] value() {
			return this.items.section(0, this.length()).toBytes();
		}

	}

	/** Diese Klasse erweitert ein {@link IndexArray} um eine Streuwerttabelle zur Beschleunigung der {@link #customFind(FEMValue, int) Suche einzelner Werte}. */
	protected static class RangesArray extends IndexArray {

		/** Dieses Feld speichert eine Zahlenfolge, die ab dem dritten Element die Startpositionen der Suchbereiche mit gleichem Streuwert enthält, analog zur
		 * Kodierung der Streuwerttabelle eines {@link IAMMapping}. Die Struktur der Zahlenfolge ist {@code (keysIdx, valuesIdx, 0, range1, ..., rangeN)}. Die Länge
		 * der Zahlenfolge entspricht damit stets einer um drei erhöhte Potenz von {@code 2}. */
		public final IAMArray range;

		@SuppressWarnings ("javadoc")
		public RangesArray(final FEMIndex index, final IAMArray items, final IAMArray range) {
			super(index, items);
			this.range = Objects.notNull(range);
		}

		/** {@inheritDoc} */
		@Override
		protected int customFind(final FEMValue that, final int offset) {
			final int hash = that.hashCode(), mask = this.range.length() - 4, index = hash & mask;
			for (int l = this.range.get(index + 2), r = this.range.get(index + 3); l < r; l++) {
				if (that.equals(this.customGet(l))) return l < offset ? -1 : l;
			}
			return -1;
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class IndexArray2 extends IndexArray {

		public final FEMValue[] cache;

		public IndexArray2(final FEMIndex index, final IAMArray array) {
			super(index, array);
			this.cache = new FEMValue[this.length()];
		}

		@Override
		protected FEMValue customGet(final int index) {
			final FEMValue result = this.cache[index];
			if (result != null) return result;
			return this.cache[index] = super.customGet(index);
		}

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

	protected int propertyRef;

	public void switchToEmpty() {
		this.targetIndex = null;
		this.sourceIndex = IAMIndex.EMPTY;
	}

	public void setToLoader(final IAMIndex source) {

	}

	public void setToBuilder() {
		this.targetIndex = new IAMIndexBuilder();
		this.sourceIndex = this.targetIndex;
		this.targetArrayValuePool = new IAMListingBuilder();
		this.sourceArrayValuePool = this.targetArrayValuePool;
		this.targetHandlerValuePool = new IAMListingBuilder();
		this.targetStringValuePool = new IAMListingBuilder();
		this.targetBinaryValuePool = new IAMListingBuilder();
		this.targetIntegerValuePool = new IAMListingBuilder();
		this.targetDecimalValuePool = new IAMListingBuilder();
		this.targetDurationValuePool = new IAMListingBuilder();
		this.targetDatetimeValuePool = new IAMListingBuilder();
		this.targetObjectValuePool = new IAMListingBuilder();
		this.targetTableValuePool = new IAMListingBuilder();
		this.targetProxyFunctionPool = new IAMListingBuilder();
		this.targetClosureFunctionPool = new IAMListingBuilder();
		this.targetCompositeFunctionPool = new IAMListingBuilder();
		this.targetIndexListing = new IAMListingBuilder();
		this.targetIndexMapping = new IAMMappingBuilder();
		this.targetIndex.put(-1, this.targetIndexListing);
		this.targetIndex.put(-1, this.targetIndexMapping);
	}

	protected IAMIndex sourceIndex;

	/** Dieses Feld speichert die Auflistung der Wertlisten, von denen jede aus einer Liste von {@link #putValue(FEMValue) Wertreferenzen} sowie einem Streuwert
	 * besteht (ref1, ..., refN, hash). */
	protected IAMListing sourceArrayValuePool;

	/** Dieses Feld speichert die Auflistung der Funktionszeiger, von denen jeder aus einer {@link #putFunction(FEMFunction) Funktionsreferenz} besteht. */
	protected IAMListing sourceHandlerValuePool;

	/** Dieses Feld speichert die Auflistung der Zeichenketten, welche gemäß {@link FEMString#toArray()} kodiert sind. */
	protected IAMListing sourceStringValuePool;

	/** Dieses Feld speichert die Auflistung der Bytefolgen, welche gemäß {@link IndexBinary#items} kodiert sind. */
	protected IAMListing sourceBinaryValuePool;

	/** Dieses Feld speichert die Auflistung der Dezimalzahlen, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
	protected IAMListing sourceIntegerValuePool;

	/** Dieses Feld speichert die Auflistung der Dezimalbrüche, von denen jeder aus zwei {@code int}-Zahlen besteht (lo, hi). */
	protected IAMListing sourceDecimalValuePool;

	/** Dieses Feld speichert die Auflistung der Zeitspannen, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
	protected IAMListing sourceDurationValuePool;

	/** Dieses Feld speichert die Auflistung der Zeitangaben, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
	protected IAMListing sourceDatetimeValuePool;

	/** Dieses Feld speichert die Auflistung der Referenzen, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
	protected IAMListing sourceObjectValuePool;

	/** Dieses Feld speichert die Auflistung der Tabellen, von denen jede aus drei {@code int}-Zahlen besteht (keyArrayIdx, valueArrayIdx, tableMappingIdx). Die
	 * dabei referenzierte Abbildung besitzt Einträge der Form (keyHash)::(keyIdx1, ..., keyIdxN). */
	protected IAMListing sourceTableValuePool;

	protected IAMListing sourceProxyFunctionPool;

	protected IAMListing sourceClosureFunctionPool;

	protected IAMListing sourceCompositeFunctionPool;

	/** Dieses Feld speichert den {@link IAMIndexBuilder}, in welchen alle Daten einfließen. */
	protected IAMIndexBuilder targetIndex;

	/** Dieses Feld speichert die erste Auflistung im {@link #targetIndex}. */
	protected IAMListingBuilder targetIndexListing;

	/** Dieses Feld speichert die erste Abbildung im {@link #targetIndex}. */
	protected IAMMappingBuilder targetIndexMapping;

	/** Dieses Feld speichert die Auflistung der Wertlisten, von denen jede aus einer Liste von {@link #putValue(FEMValue) Wertreferenzen} sowie einem Streuwert
	 * besteht (ref1, ..., refN, hash). */
	protected IAMListingBuilder targetArrayValuePool;

	/** Dieses Feld speichert die Auflistung der Funktionszeiger, von denen jeder aus einer {@link #putFunction(FEMFunction) Funktionsreferenz} besteht. */
	protected IAMListingBuilder targetHandlerValuePool;

	/** Dieses Feld speichert die Auflistung der Zeichenketten, welche gemäß {@link FEMString#toArray()} kodiert sind. */
	protected IAMListingBuilder targetStringValuePool;

	/** Dieses Feld speichert die Auflistung der Bytefolgen, welche gemäß {@link IndexBinary#items} kodiert sind. */
	protected IAMListingBuilder targetBinaryValuePool;

	/** Dieses Feld speichert die Auflistung der Dezimalzahlen, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
	protected IAMListingBuilder targetIntegerValuePool;

	/** Dieses Feld speichert die Auflistung der Dezimalbrüche, von denen jeder aus zwei {@code int}-Zahlen besteht (lo, hi). */
	protected IAMListingBuilder targetDecimalValuePool;

	/** Dieses Feld speichert die Auflistung der Zeitspannen, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
	protected IAMListingBuilder targetDurationValuePool;

	/** Dieses Feld speichert die Auflistung der Zeitangaben, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
	protected IAMListingBuilder targetDatetimeValuePool;

	/** Dieses Feld speichert die Auflistung der Referenzen, von denen jede aus zwei {@code int}-Zahlen besteht (lo, hi). */
	protected IAMListingBuilder targetObjectValuePool;

	/** Dieses Feld speichert die Auflistung der Tabellen, von denen jede aus drei {@code int}-Zahlen besteht (keyArrayIdx, valueArrayIdx, tableMappingIdx). Die
	 * dabei referenzierte Abbildung besitzt Einträge der Form (keyHash)::(keyIdx1, ..., keyIdxN). */
	protected IAMListingBuilder targetTableValuePool;

	protected IAMListingBuilder targetProxyFunctionPool;

	protected IAMListingBuilder targetClosureFunctionPool = new IAMListingBuilder();

	protected IAMListingBuilder targetCompositeFunctionPool = new IAMListingBuilder();

	@Override
	public FEMValue get() {
		return this.getValue(this.propertyRef);
	}

	@Override
	public void set(final FEMValue value) {
		this.propertyRef = this.putValue(value);
	}

	/** Diese Methode gibt den Wert zu gegebenen {@link #toRef(int, int) Wertreferenz} zurück. Wenn deren {@link #toType(int) Typkennung} unbekannt ist, wird
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
			case TYPE_TABLE_VALUE:
				return this.getTableValue(index);
			default:
				return this.getCustomValue(type, index);
		}
	}

	protected FEMArray getArrayValue(final int index) throws IllegalArgumentException {
		return this.getArrayValue(this.sourceArrayValuePool.item(index));
	}

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

	/** Diese Methode gibt die Zeitangabe zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceDatetimeValuePool}.
	 * @return Zeitangabe.
	 * @throws IllegalArgumentException Wenn {@link #getDatetimeValue(IAMArray)} diese auslöst. */
	protected FEMDatetime getDatetimeValue(final int index) throws IllegalArgumentException {
		return this.getDatetimeValue(this.sourceDatetimeValuePool.item(index));
	}

	/** Diese Methode gibt die Zeitspanne zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceDatetimeValuePool}.
	 * @return Zeitspanne.
	 * @throws IllegalArgumentException Wenn {@link #getDurationValue(IAMArray)} diese auslöst. */
	protected FEMDuration getDurationValue(final int index) throws IllegalArgumentException {
		return this.getDurationValue(this.sourceDurationValuePool.item(index));
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

	/** Diese Methode gibt die Werttabelle zurück, die unter der gegebenen {@link #toIndex(int) Position} verwaltet wird.
	 *
	 * @param index Position in {@link #sourceTableValuePool}.
	 * @return Werttabelle.
	 * @throws IllegalArgumentException Wenn {@link #getTableValue(IAMArray)} diese auslöst. */
	protected FEMTable getTableValue(final int index) throws IllegalArgumentException {
		return this.getTableValue(this.sourceTableValuePool.item(index));
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

	protected FEMFunction getProxyFunction(final int index) {
		return FEMVoid.INSTANCE;
	}

	protected FEMFunction getParamFunction(final int index) {
		return FEMParam.from(index);
	}

	protected FEMFunction getConcatFunction(final int index) {
		return FEMVoid.INSTANCE;
	}

	protected FEMFunction getClosureFunction(final int index) {
		return FEMVoid.INSTANCE;
	}

	protected FEMFunction getCompositeFunction(final int index) {
		return FEMVoid.INSTANCE;
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

	public IAMArray getArrayArray(final FEMArray source) throws NullPointerException, IllegalArgumentException {
		final int length = source.length();
		final int[] items = new int[length + 1];
		for (int i = 0; i < length; i++) {
			items[i] = this.putValue(source.get(i));
		}
		items[length] = source.hash();
		final IAMArray array = IAMArray.from(items);
		return array;
	}

	public IAMArray getStringArray(final FEMString source) {
		final IAMArray array = source.toArray();
		return array;
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getBinaryValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebenen Bytefolge enthält.
	 *
	 * @param source Bytefolge.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getBinaryArray(final FEMBinary source) throws NullPointerException {
		final int hash = source.hash(), index = source.length();
		final byte[] result = new byte[index + 4];
		result[index + 0] = (byte)(hash >>> 0);
		result[index + 1] = (byte)(hash >>> 8);
		result[index + 2] = (byte)(hash >>> 16);
		result[index + 3] = (byte)(hash >>> 24);
		source.extract(result, 0);
		return IAMArray.from(result);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getIntegerValue(IAMArray)} und liefert eine Zahlenfolge, welche die {@link FEMInteger#value interne
	 * Darstellung} der gegebenen Dezimalzahl enthält.
	 *
	 * @param source Dezimalzahl.
	 * @return Zahlenfolge zur Dezimalzahl.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getIntegerArray(final FEMInteger source) throws NullPointerException {
		final long value = source.value();
		return IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getDecimalValue(IAMArray)} und liefert eine Zahlenfolge, welche die {@link FEMDecimal#value() internen
	 * Darstellung} des gegebenen Dezimalbruchs enthält.
	 *
	 * @return Zahlenfolge zum Dezimalbruch.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getDecimalArray(final FEMDecimal source) throws NullPointerException {
		final long value = Double.doubleToLongBits(source.value());
		return IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
	}

	public IAMArray getDatetimeArray(final FEMDatetime source) throws NullPointerException {
		final long value = source.value();
		return IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
	}

	public IAMArray getDurationArray(final FEMDuration source) throws NullPointerException {
		final long value = source.value();
		return IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
	}

	public IAMArray getHandlerArray(final FEMHandler source) {
		final IAMArray array = IAMArray.from(this.putFunction(source.value()));
		return array;
	}

	public IAMArray getObjectArray(final FEMObject source) throws NullPointerException {
		final long value = source.value();
		return IAMArray.from(Integers.toIntL(value), Integers.toIntH(value));
	}

	/* Diese Methode fügt die gegebene Tabelle in den {@link #targetTableValuePool} ein und gibt die Wertreferenz darauf zurück. Ihre Schlüssel- und Wertspalten
	 * werden dabei auch über {@link #putArrayValue(FEMArray)} in den {@link #targetArrayValuePool} eingetragen. Die Abbildung von Schlüsseln auf Werte wird dazu
	 * als {@link IAMMapping} realisiert und im {@link #targetIndex} eingetragen. Der {@link IAMEntry#key()} besteht aus dem Streuwert des Schlüssels. Der
	 * {@link IAMEntry#value()} enthält dazu die Auflistung entsprechenden Schlüsselreferenz-Wertreferenz-Paare. */
	public IAMArray getTableArray(final FEMTable source) throws NullPointerException, IllegalArgumentException {
		final int entryCount = source.length();
		final FEMArray keys = source.keys(), values = source.values();
		final int rangeMask = IAMMapping.mask(entryCount), rangeCount = rangeMask + 4;
		final int[] tableRanges = new int[rangeCount], bucketIndex = new int[entryCount];
		for (int i = 0; i < entryCount; i++) {
			final int bucket = (keys.get(i).hashCode() & rangeMask) + 3;
			tableRanges[bucket]++;
			bucketIndex[i] = bucket;
		}
		for (int i = 3, offset = 0; i < rangeCount; i++) {
			final int bucketSize = tableRanges[i];
			tableRanges[i] = offset;
			offset += bucketSize;
		}
		final FEMValue[] tableKeys = new FEMValue[entryCount], tableValues = new FEMValue[entryCount];
		for (int i = 0; i < entryCount; i++) {
			final int bucket = bucketIndex[i], offset = tableRanges[bucket];
			tableKeys[offset] = keys.get(i);
			tableValues[offset] = values.get(i);
			tableRanges[bucket] = offset + 1;
		}
		tableRanges[0] = this.toIndex(this.putArrayValue(FEMArray.from(tableKeys)));
		tableRanges[1] = this.toIndex(this.putArrayValue(FEMArray.from(tableValues)));
		final IAMArray array = IAMArray.from(tableRanges);
		return array;
	}

	/** Diese Methode gibt eine Wertliste zurück, deren Elemente in der gegebenen Zahlenfolge {@link IndexArray#items kodiert} sind. */
	public FEMArray getArrayValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		return new IndexArray(this, source);
	}

	/** Diese Methode gibt eine Zeichenkette zurück, deren Codepoints in der gegebenen Zahlenfolge {@link FEMString#toArray() kodiert} sind. */
	public FEMString getStringValue(final IAMArray array) {
		return FEMString.from(array);
	}

	/** Diese Methode gibt die Bytefolge zur gegebenen Zahlenfolge zurück. Dabei werden die ersten vier Byte der Zahlenfolge als {@link FEMBinary#hash()
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

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Dezimalzahl und gibt diese zurück. Die Zahlenfolge muss dazu aus zwei Zahlen bestehen, von denen
	 * die erste den MSB-{@code int} und die zweiten den LSB-{@code int} der {@link FEMInteger#value() internen Darstellung} der Dezimalzahl enthält.
	 *
	 * @return Dezimalzahl zur Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMInteger getIntegerValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() != 2) throw new IllegalArgumentException();
		return new FEMInteger(Integers.toLong(source.get(0), source.get(1)));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Dezimalbruch und gibt diesen zurück. Die Zahlenfolge muss dazu aus zwei Zahlen bestehen, von
	 * denen die erste den MSB-{@code int} und die zweiten den LSB-{@code int} der {@link FEMDecimal#value() internen Darstellung} des Dezimalbruchs enthält.
	 *
	 * @return Dezimalbruch zur Zahlenfolge.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMDecimal getDecimalValue(final IAMArray array) throws NullPointerException, IllegalArgumentException {
		if (array.length() != 2) throw new IllegalArgumentException();
		return new FEMDecimal(Double.longBitsToDouble(Integers.toLong(array.get(0), array.get(1))));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Zeitangabe und gibt diese zurück. Die Zahlenfolge muss dazu aus zwei Zahlen bestehen, von denen
	 * die erste den MSB-{@code int} und die zweiten den LSB-{@code int} der {@link FEMDatetime#value() internen Darstellung} der Zeitangabe enthält.
	 *
	 * @return Zeitangabe zur Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMDatetime getDatetimeValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() != 2) throw new IllegalArgumentException();
		return new FEMDatetime(Integers.toLong(source.get(0), source.get(1)));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Zeitspanne und gibt diese zurück. Die Zahlenfolge muss dazu aus zwei Zahlen bestehen, von denen
	 * die erste den MSB-{@code int} und die zweiten den LSB-{@code int} der {@link FEMDuration#value() internen Darstellung} der Zeitspanne enthält.
	 *
	 * @return Zeitspanne zur Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMDuration getDurationValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() != 2) throw new IllegalArgumentException();
		return new FEMDuration(Integers.toLong(source.get(0), source.get(1)));
	}

	public FEMHandler getHandlerValue(final IAMArray source) throws IllegalArgumentException {
		if (source.length() != 1) throw new IllegalArgumentException();
		return new FEMHandler(this.getFunction(source.get(0)));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Objektreferenz und gibt diesen zurück. Die Zahlenfolge muss dazu aus zwei Zahlen bestehen, von
	 * denen die erste den MSB-{@code int} und die zweiten den LSB-{@code int} der {@link FEMObject#value() internen Darstellung} der Objektreferenz enthält.
	 *
	 * @return Objektreferenz zur Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public FEMObject getObjectValue(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		if (source.length() != 2) throw new IllegalArgumentException();
		return new FEMObject(Integers.toLong(source.get(0), source.get(1)));
	}

	public FEMTable getTableValue(final IAMArray array) throws NullPointerException, IllegalArgumentException {
		return FEMTable.from(new RangesArray(this, this.sourceArrayValuePool.item(array.get(0)), array), this.getArrayValue(array.get(1)));
	}

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
			case FEMTable.ID:
				return this.putTableValue((FEMTable)source.data());
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Wertreferenz auf {@link FEMVoid#INSTANCE} zurück.
	 *
	 * @return Wertreferenz. */
	public int putVoidValue() {
		return this.toRef(FEMIndex.TYPE_VOID_VALUE, 0);
	}

	/** Diese Methode gibt die Wertreferenz auf {@link FEMBoolean#TRUE} zurück.
	 *
	 * @return Wertreferenz. */
	public int putTrueValue() {
		return this.toRef(FEMIndex.TYPE_TRUE_VALUE, 0);
	}

	/** Diese Methode gibt die Wertreferenz auf {@link FEMBoolean#FALSE} zurück.
	 *
	 * @return Wertreferenz. */
	public int putFalseValue() {
		return this.toRef(FEMIndex.TYPE_FALSE_VALUE, 0);
	}

	/** Diese Methode nimmt die gegebene Wertliste in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Dezimalbruch.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link #getArrayArray(FEMArray)} diese auslöst. */
	public int putArrayValue(final FEMArray source) throws NullPointerException, IllegalArgumentException {
		final IAMArray array = this.getArrayArray(source);
		final int index = this.targetArrayValuePool.put(array);
		return this.toRef(FEMIndex.TYPE_ARRAY_VALUE, index);
	}

	public int putStringValue(final FEMString source) throws NullPointerException {
		final IAMArray array = getStringArray(source);
		final int index = this.targetStringValuePool.put(array);
		return this.toRef(FEMIndex.TYPE_STRING_VALUE, index);
	}

	public int putBinaryValue(final FEMBinary source) throws NullPointerException {
		final IAMArray array = this.getBinaryArray(source);
		final int index = this.targetBinaryValuePool.put(array);
		return this.toRef(FEMIndex.TYPE_BINARY_VALUE, index);
	}

	/** Diese Methode nimmt die gegebene Dezimalzahl in die Verwaltung auf und gibt die Wertreferenz darauf zurück.
	 *
	 * @param source Dezimalzahl.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getIntegerArray(FEMInteger)} diese auslöst. */
	public int putIntegerValue(final FEMInteger source) throws NullPointerException {
		final IAMArray array = this.getIntegerArray(source);
		final int index = this.targetIntegerValuePool.put(array);
		return this.toRef(FEMIndex.TYPE_INTEGER_VALUE, index);
	}

	/** Diese Methode nimmt den gegebenen Dezimalbruch in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Dezimalbruch.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getDatetimeArray(FEMDatetime)} diese auslöst. */
	public int putDecimalValue(final FEMDecimal source) throws NullPointerException {
		final IAMArray array = this.getDecimalArray(source);
		final int index = this.targetDecimalValuePool.put(array);
		return this.toRef(FEMIndex.TYPE_DECIMAL_VALUE, index);
	}

	/** Diese Methode nimmt die gegebene Zeitangabe in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Zeitangabe.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getDatetimeArray(FEMDatetime)} diese auslöst. */
	public int putDatetimeValue(final FEMDatetime source) throws NullPointerException {
		final IAMArray array = this.getDatetimeArray(source);
		final int index = this.targetDatetimeValuePool.put(array);
		return this.toRef(FEMIndex.TYPE_DATETIME_VALUE, index);
	}

	/** Diese Methode nimmt die gegebene Zeitspanne in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Zeitspanne.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getDurationArray(FEMDuration)} diese auslöst. */
	protected int putDurationValue(final FEMDuration source) throws NullPointerException {
		final IAMArray array = this.getDurationArray(source);
		final int index = this.targetDurationValuePool.put(array);
		return this.toRef(FEMIndex.TYPE_DURATION_VALUE, index);
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
		final IAMArray array = this.getHandlerArray(source);
		final int index = this.targetHandlerValuePool.put(array);
		return this.toRef(FEMIndex.TYPE_HANDLER_VALUE, index);
	}

	/** Diese Methode nimmt die gegebene Objektreferenz in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Objektreferenz.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getObjectArray(FEMObject)} diese auslöst. */
	public int putObjectValue(final FEMObject source) throws NullPointerException {
		final IAMArray array = this.getObjectArray(source);
		final int index = this.targetObjectValuePool.put(array);
		return this.toRef(FEMIndex.TYPE_OBJECT_VALUE, index);
	}

	public int putTableValue(final FEMTable source) throws NullPointerException, IllegalArgumentException {
		final IAMArray array = this.getTableArray(source);
		final int index = this.targetTableValuePool.put(array);
		return this.toRef(FEMIndex.TYPE_TABLE_VALUE, index);
	}

	public int putFunction(final FEMFunction source) throws NullPointerException, IllegalArgumentException {
		if (source instanceof FEMValue) return this.putValue((FEMValue)source);
		if (source instanceof FEMProxy) return this.putProxyFunction((FEMProxy)source);
		if (source instanceof FEMParam) return this.putParamFunction((FEMParam)source);
		if (source instanceof ConcatFunction) return this.putConcatFunction((ConcatFunction)source);
		if (source instanceof ClosureFunction) return this.putClosureFunction((ClosureFunction)source);
		if (source instanceof CompositeFunction) return this.putCompositeFunction((CompositeFunction)source);
		throw new IllegalArgumentException();
	}

	protected int putProxyFunction(final FEMProxy source) {
		final int nameIdx = this.toIndex(this.putStringValue(FEMString.from(source.name())));
		final int functionRef = this.putFunction(source.get());
		final IAMArray array = IAMArray.from(nameIdx, functionRef);
		final int index = this.targetProxyFunctionPool.put(array);
		return this.toRef(FEMIndex.TYPE_PROXY_FUNCTION, index);
	}

	protected int putParamFunction(final FEMParam source) throws NullPointerException {
		final int index = source.index();
		return this.toRef(FEMIndex.TYPE_PARAM_FUNCTION, index);
	}

	protected int putConcatFunction(final ConcatFunction source) throws NullPointerException, IllegalArgumentException {
		return this.toRef(FEMIndex.TYPE_CONCAT_FUNCTION, this.toIndex(this.putCompositeFunction(source)));
	}

	protected int putClosureFunction(final ClosureFunction source) throws NullPointerException, IllegalArgumentException {
		final IAMArray array = IAMArray.from(this.putFunction(source.function()));
		final int index = this.targetClosureFunctionPool.put(array);
		return this.toRef(FEMIndex.TYPE_CLOSURE_FUNCTION, index);
	}

	protected int putCompositeFunction(final CompositeFunction source) throws NullPointerException, IllegalArgumentException {
		final FEMFunction[] params = source.params();
		final int length = params.length;
		final int[] result = new int[length + 1];
		result[0] = this.putFunction(source.function());
		for (int i = 0; i < length; i++) {
			result[i + 1] = this.putFunction(params[i]);
		}
		final IAMArray array = IAMArray.from(result);
		final int index = this.targetCompositeFunctionPool.put(array);
		return this.toRef(FEMIndex.TYPE_COMPOSITE_FUNCTION, index);
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

}
