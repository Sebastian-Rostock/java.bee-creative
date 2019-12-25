package bee.creative.fem;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import bee.creative.bind.Property;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.fem.FEMArray.CompactArray3;
import bee.creative.fem.FEMFunction.ClosureFunction;
import bee.creative.fem.FEMFunction.CompositeFunction;
import bee.creative.fem.FEMFunction.ConcatFunction;
import bee.creative.fem.FEMString.ArrayString;
import bee.creative.fem.FEMString.Collector;
import bee.creative.fem.FEMString.CompactStringINT16;
import bee.creative.fem.FEMString.CompactStringINT8;
import bee.creative.fem.FEMString.INT16Encoder;
import bee.creative.fem.FEMString.INT8Encoder;
import bee.creative.fem.FEMString.UTF16Counter;
import bee.creative.fem.FEMString.UTF16Encoder2;
import bee.creative.fem.FEMString.UTF32Encoder;
import bee.creative.fem.FEMString.UTF8Counter;
import bee.creative.fem.FEMString.UTF8Encoder;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMIndex;
import bee.creative.iam.IAMIndexBuilder;
import bee.creative.iam.IAMListing;
import bee.creative.iam.IAMListingBuilder;
import bee.creative.iam.IAMMapping;
import bee.creative.iam.IAMMappingBuilder;
import bee.creative.lang.Integers;
import bee.creative.lang.Objects;
import bee.creative.mmf.MMIArray;
import bee.creative.util.Comparables.Items;

/** Diese Klasse implementiert ein Objekt zur Kodierung und Dekodierung von {@link FEMValue Werten} und {@link FEMFunction Funktionen} in {@link IAMArray
 * Zahlenlisten}. Damit ist es möglich, beliebig große Wert- und Funktionsgraphen über ein {@link MMIArray} in eine Binärdatei auszulagern.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMCodec implements Property<FEMValue>, Emuable {

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

	/** Diese Klasse implementiert eine abstrakte Verwaltung von Datensätzen, welche in Zahlenfolgen kodiert abgelegt werden. Zur Umwandlung beim {@link #get(int)
	 * Lesen} und {@link #put(Object) Schreiben} werden die Methoden {@link #toItem(IAMArray)} bsw. {@link #toArray(Object)} eingesetzt.
	 *
	 * @param <GItem> Typ der Datensätze. */
	protected static abstract class BasePool<GItem> implements Items<GItem> {

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

		/** Dieses Feld speichert den Puffer der über {@link #get(int)} gelieferten Datensätze. */
		@Deprecated
		protected Object[] cache = {};

		/** Dieses Feld speichert die Zahlenfolgen der über {@link #get(int)} gelesenen Datensätzen. */
		protected IAMListing source = IAMListing.EMPTY;

		/** Dieses Feld speichert die Zahlenfolgen der über {@link #put(Object)} angefügten Datensätzen. */
		protected IAMListingBuilder target;

		/** Diese Methode gibt den Datensätzen zur gegebenen Position zurück.
		 *
		 * @see #toItem(IAMArray)
		 * @see IAMListing#item(int)
		 * @param index Position.
		 * @return Datensatz.
		 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist. */
		@Override
		public GItem get(final int index) {
			if (index < 0) throw new IndexOutOfBoundsException();
			final IAMListing pool = this.source;
			final int poolSize = pool.itemCount();
			if (index >= poolSize) throw new IndexOutOfBoundsException();
//			Object[] cache = this.cache;
//			final int cacheSize = cache.length;
//			if (index >= cacheSize) {
//				cache = Arrays.copyOf(cache, poolSize);
//				this.cache = cache;
//			}
//			@SuppressWarnings ("unchecked")
//			GItem item = (GItem)cache[index];
//			if (item != null) return item;
			//			cache[index] = item;
			return this.toItem(pool.item(index));
		}

		/** Diese Methode nimmt den gegebenen Datensatz in die Verwaltung auf und gibt dessen Position zurück.
		 *
		 * @see #toArray(Object)
		 * @see IAMListingBuilder#put(IAMArray)
		 * @param source Datensatz.
		 * @return Position.
		 * @throws NullPointerException Wenn {@link #toArray(Object)} diese auslöst. */
		public int put(final GItem source) {
			return this.target.put(this.toArray(source));
		}

		/** Diese Methode gibt die Datenquelle zurück, auf welche {@link #get(int)} zugreift.
		 *
		 * @return Datenquelle. */
		public IAMListing getSource() {
			return this.source;
		}

		/** Diese Methode bestückt diesen {@link BasePool} zum Lesen der gegebenen {@link IAMListing Datenquelle}. Die {@link #getTarget() Datensenke} wird dabei
		 * auf {@code null} gesetzt und der {@link #cleanup() Puffer zur Datenquelle wird geleert}.
		 *
		 * @param source Datenquelle.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		protected void setSource(final IAMListing source) throws NullPointerException {
			this.source = Objects.notNull(source);
			this.target = null;
			this.cleanup();
		}

		/** Diese Methode gibt die Datensenke zurück, auf welche {@link #put(Object)} zugreift.
		 *
		 * @return Datensenke oder {@code null}. */
		public IAMListingBuilder getTarget() {
			return this.target;
		}

		/** Diese Methode bestückt diesen {@link BasePool} zum Lesen und Schreiben der gegebenen {@link IAMListingBuilder Datensenke}. Die {@link #getSource()
		 * Datenquelle} ist dabei gleich dieser Datensenke und der {@link #cleanup() Puffer zur Datenquelle wird geleert}.
		 *
		 * @param target Datensenke.
		 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
		protected void setTarget(final IAMListingBuilder target) throws NullPointerException {
			this.source = Objects.notNull(target);
			this.target = target;
			this.cleanup();
		}

 

		/** Diese Methode leert den Puffer der über {@link #get(int)} gelieferten Datensätze. */
		@Deprecated
		public void cleanup() {
			this.cache = new Object[0];
		}

		/** Diese Methode gibt eine Sicht auf die Liste aller Datensätze zurück.
		 *
		 * @return {@link List}-Sicht auf die Datensätze. */
		public List<GItem> toList() {
			return new ItemList();
		}

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

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toList());
		}

	}

	@SuppressWarnings ("javadoc")
	protected class ArrayValuePool extends BasePool<FEMArray> {

		@Override
		public FEMArray toItem(final IAMArray source) {
			return FEMCodec.this.getArrayValue(source);
		}

		@Override
		public IAMArray toArray(final FEMArray source) {
			return FEMCodec.this.getArrayArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected class StringValuePool extends BasePool<FEMString> {

		@Override
		public FEMString toItem(final IAMArray source) {
			return FEMCodec.this.getStringValue(source);
		}

		@Override
		public IAMArray toArray(final FEMString source) {
			return FEMCodec.this.getStringArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected class BinaryValuePool extends BasePool<FEMBinary> {

		@Override
		public FEMBinary toItem(final IAMArray source) {
			return FEMCodec.this.getBinaryValue(source);
		}

		@Override
		public IAMArray toArray(final FEMBinary source) {
			return FEMCodec.this.getBinaryArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected class IntegerValuePool extends BasePool<FEMInteger> {

		@Override
		public FEMInteger toItem(final IAMArray source) {
			return FEMCodec.this.getIntegerValue(source);
		}

		@Override
		public IAMArray toArray(final FEMInteger source) {
			return FEMCodec.this.getIntegerArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected class DecimalValuePool extends BasePool<FEMDecimal> {

		@Override
		public FEMDecimal toItem(final IAMArray source) {
			return FEMCodec.this.getDecimalValue(source);
		}

		@Override
		public IAMArray toArray(final FEMDecimal source) {
			return FEMCodec.this.getDecimalArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected class DurationValuePool extends BasePool<FEMDuration> {

		@Override
		public FEMDuration toItem(final IAMArray source) {
			return FEMCodec.this.getDurationValue(source);
		}

		@Override
		public IAMArray toArray(final FEMDuration source) {
			return FEMCodec.this.getDurationArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected class DatetimeValuePool extends BasePool<FEMDatetime> {

		@Override
		public FEMDatetime toItem(final IAMArray source) {
			return FEMCodec.this.getDatetimeValue(source);
		}

		@Override
		public IAMArray toArray(final FEMDatetime source) {
			return FEMCodec.this.getDatetimeArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected class HandlerValuePool extends BasePool<FEMHandler> {

		@Override
		public FEMHandler toItem(final IAMArray source) {
			return FEMCodec.this.getHandlerValue(source);
		}

		@Override
		public IAMArray toArray(final FEMHandler source) {
			return FEMCodec.this.getHandlerArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected class ObjectValuePool extends BasePool<FEMObject> {

		@Override
		public FEMObject toItem(final IAMArray source) {
			return FEMCodec.this.getObjectValue(source);
		}

		@Override
		public IAMArray toArray(final FEMObject source) {
			return FEMCodec.this.getObjectArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected class ProxyFunctionPool extends BasePool<FEMProxy> {

		@Override
		public FEMProxy toItem(final IAMArray source) {
			return FEMCodec.this.getProxyFunction(source);
		}

		@Override
		public IAMArray toArray(final FEMProxy source) {
			return FEMCodec.this.getProxyArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected class ConcatFunctionPool extends BasePool<ConcatFunction> {

		@Override
		public ConcatFunction toItem(final IAMArray source) {
			return FEMCodec.this.getConcatFunction(source);
		}

		@Override
		public IAMArray toArray(final ConcatFunction source) {
			return FEMCodec.this.getConcatArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected class ClosureFunctionPool extends BasePool<ClosureFunction> {

		@Override
		public ClosureFunction toItem(final IAMArray source) {
			return FEMCodec.this.getClosureFunction(source);
		}

		@Override
		public IAMArray toArray(final ClosureFunction source) {
			return FEMCodec.this.getClosureArray(source);
		}

	}

	@SuppressWarnings ("javadoc")
	protected class CompositeFunctionPool extends BasePool<CompositeFunction> {

		@Override
		public CompositeFunction toItem(final IAMArray source) {
			return FEMCodec.this.getCompositeFunction(source);
		}

		@Override
		public IAMArray toArray(final CompositeFunction source) {
			return FEMCodec.this.getCompositeArray(source);
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

	@SuppressWarnings ("javadoc")
	public static class ArrayStringUTF8 extends FEMString {
	
		public final IAMArray array;
	
		ArrayStringUTF8(final IAMArray array) {
			super(Integers.toInt(array.get(7), array.get(6), array.get(5), array.get(4)));
			this.array = array;
			this.hash = Integers.toInt(array.get(3), array.get(2), array.get(1), array.get(0));
		}
	
		@Override
		protected int customGet(int index) throws IndexOutOfBoundsException {
			int offset = 8;
			while (index > 0) {
				index--;
				offset += FEMString.utf8Size(this.array.get(offset));
			}
			return FEMCodec.utf8Codepoint(this.array, offset);
		}
	
		@Override
		protected boolean customExtract(final FEMString.Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 8;
				while (offset > 0) {
					offset--;
					index += FEMString.utf8Size(this.array.get(index));
				}
				while (length > 0) {
					if (!target.push(FEMCodec.utf8Codepoint(this.array, index))) return false;
					length--;
					index += FEMString.utf8Size(this.array.get(index));
				}
			} else {
				int index = 8;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString.utf8Size(this.array.get(index));
				}
				while (length > 0) {
					while (!FEMString.utf8Header(this.array.get(--index)))
						if (!target.push(FEMCodec.utf8Codepoint(this.array, index))) return false;
					length--;
				}
			}
			return true;
		}
	
	}

	@SuppressWarnings ("javadoc")
	public static class ArrayStringUTF16 extends FEMString {
	
		public final IAMArray array;
	
		ArrayStringUTF16(final IAMArray array) {
			super(Integers.toInt(array.get(3), array.get(2)));
			this.array = array;
			this.hash = Integers.toInt(array.get(1), array.get(0));
		}
		// TODO offset und constructor
	
		@Override
		protected int customGet(int index) throws IndexOutOfBoundsException {
			int offset = 4;
			while (index > 0) {
				index--;
				offset += FEMString.utf16Length(this.array.get(offset));
			}
			return FEMCodec.utf16Codepoint(this.array, offset);
		}
	
		@Override
		protected boolean customExtract(final FEMString.Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 4;
				while (offset > 0) {
					offset--;
					index += FEMString.utf16Length(this.array.get(index));
				}
				while (length > 0) {
					if (!target.push(FEMCodec.utf16Codepoint(this.array, index))) return false;
					length--;
					index += FEMString.utf16Length(this.array.get(index));
				}
			} else {
				int index = 4;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString.utf16Length(this.array.get(index));
				}
				while (length > 0) {
					while (!FEMString.utf16Header(this.array.get(--index)))
						if (!target.push(FEMCodec.utf16Codepoint(this.array, index))) return false;
					length--;
				}
			}
			return true;
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

	/** Dieses Feld speichert den in {@link #setSource(IAMIndex)} initialisierten {@link IAMIndex}, in aus welchem alle übrogen {@code source}-Datenfelder
	 * bestückt werden müssen. */
	protected IAMIndex sourceIndex = IAMIndex.EMPTY;

	/** Dieses Feld speichert die erste Auflistung im {@link #sourceIndex}. Ihrre erste Zahlenfolge enthält die Referenz auf den {@link #get() Wert} dieser
	 * {@link Property Eigenschaft}. */
	protected IAMListing sourceIndexListing = IAMListing.EMPTY;

	/** Dieses Feld speichert die erste Abbildung im {@link #sourceIndex}. Unter den Typkennungen als Schlüssel werden die Positionen der Auflistungen
	 * registriert, in welchen die Zahlenfolgen der entsprechenden Datensätze abgelegt sind.
	 *
	 * @see #getListing(int)
	 * @see #putListing(int) */
	protected IAMMapping sourceIndexMapping = IAMMapping.EMPTY;

	/** Dieses Feld speichert den in {@link #setTarget()} initialisierten {@link IAMIndexBuilder}, in welchen bis aus {@link #targetIndexListing} und
	 * {@link #targetIndexMapping} alle übrigen {@code target}-Datenfelder eingefügt werden müssen. */
	protected IAMIndexBuilder targetIndex;

	/** Dieses Feld speichert die erste Auflistung im {@link #targetIndex}. */
	protected IAMListingBuilder targetIndexListing;

	/** Dieses Feld speichert die erste Abbildung im {@link #targetIndex}. */
	protected IAMMappingBuilder targetIndexMapping;

	/** Dieses Feld speichert die Auflistung der Wertlisten. */
	protected ArrayValuePool arrayValuePool = new ArrayValuePool();

	/** Dieses Feld speichert die Auflistung der Zeichenketten. */
	protected StringValuePool stringValuePool = new StringValuePool();

	/** Dieses Feld speichert die Auflistung der Bytefolgen. */
	protected BinaryValuePool binaryValuePool = new BinaryValuePool();

	/** Dieses Feld speichert die Auflistung der Dezimalzahlen. */
	protected IntegerValuePool integerValuePool = new IntegerValuePool();

	/** Dieses Feld speichert die Auflistung der Dezimalbrüche. */
	protected DecimalValuePool decimalValuePool = new DecimalValuePool();

	/** Dieses Feld speichert die Auflistung der Zeitspannen. */
	protected DurationValuePool durationValuePool = new DurationValuePool();

	/** Dieses Feld speichert die Auflistung der Zeitangaben. */
	protected DatetimeValuePool datetimeValuePool = new DatetimeValuePool();

	/** Dieses Feld speichert die Auflistung der Funktionszeiger. */
	protected HandlerValuePool handlerValuePool = new HandlerValuePool();

	/** Dieses Feld speichert die Auflistung der Objektreferenzen. */
	protected ObjectValuePool objectValuePool = new ObjectValuePool();

	/** Dieses Feld speichert die Auflistung der Funktionsplatzhalter. */
	protected ProxyFunctionPool proxyFunctionPool = new ProxyFunctionPool();

	/** Dieses Feld speichert die Auflistung der Funktionketten. */
	protected ConcatFunctionPool concatFunctionPool = new ConcatFunctionPool();

	/** Dieses Feld speichert die Auflistung der Funktionsbindungen. */
	protected ClosureFunctionPool closureFunctionPool = new ClosureFunctionPool();

	/** Dieses Feld speichert die Auflistung der Funktionsaufrufe. */
	protected CompositeFunctionPool compositeFunctionPool = new CompositeFunctionPool();

	@Override
	public FEMValue get() {
		return this.getValue(this.sourceIndexListing.item(0, 0));
	}

	@Override
	public void set(final FEMValue value) {
		this.targetIndexListing.put(0, IAMArray.from(this.putValue(value)));
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
		this.targetIndexListing.put(-1, IAMArray.EMPTY);
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
		return FEMCodec.toArray_(source);
	}

	/** Diese Methode ist die Umkehroperation zu {@link #getBinaryValue(IAMArray)} und liefert eine Zahlenfolge, welche die gegebene Bytefolge enthält.
	 *
	 * @param source Bytefolge.
	 * @return Zahlenfolge.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public IAMArray getBinaryArray(final FEMBinary source) throws NullPointerException {
		return FEMCodec.toArray(source);
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
		return FEMCodec.from(source);
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
		return FEMCodec.from(source);
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

	FEMFunction[] getCompositeParamsImpl(final IAMArray source) throws NullPointerException, IllegalArgumentException {
		final int length = source.length() - 1;
		if (length < 0) throw new IllegalArgumentException();
		final FEMFunction[] result = new FEMFunction[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.getFunction(source.get(i + 1));
		}
		return result;
	}

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
		return this.toRef(FEMCodec.TYPE_ARRAY_VALUE, this.arrayValuePool.put(source));
	}

	/** Diese Methode nimmt die gegebene Zeichenkette in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Zeichenkette.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getStringArray(FEMString)} diese auslöst. */
	public int putStringValue(final FEMString source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_STRING_VALUE, this.stringValuePool.put(source));
	}

	/** Diese Methode nimmt die gegebene Bytefolge in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Bytefolge.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getBinaryArray(FEMBinary)} diese auslöst. */
	public int putBinaryValue(final FEMBinary source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_BINARY_VALUE, this.binaryValuePool.put(source));
	}

	/** Diese Methode nimmt die gegebene Dezimalzahl in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Dezimalzahl.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getIntegerArray(FEMInteger)} diese auslöst. */
	public int putIntegerValue(final FEMInteger source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_INTEGER_VALUE, this.integerValuePool.put(source));
	}

	/** Diese Methode nimmt den gegebenen Dezimalbruch in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Dezimalbruch.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getDatetimeArray(FEMDatetime)} diese auslöst. */
	public int putDecimalValue(final FEMDecimal source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_DECIMAL_VALUE, this.decimalValuePool.put(source));
	}

	/** Diese Methode nimmt die gegebene Zeitspanne in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Zeitspanne.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getDurationArray(FEMDuration)} diese auslöst. */
	public int putDurationValue(final FEMDuration source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_DURATION_VALUE, this.durationValuePool.put(source));
	}

	/** Diese Methode nimmt die gegebene Zeitangabe in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Zeitangabe.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getDatetimeArray(FEMDatetime)} diese auslöst. */
	public int putDatetimeValue(final FEMDatetime source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_DATETIME_VALUE, this.datetimeValuePool.put(source));
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
		return this.toRef(FEMCodec.TYPE_HANDLER_VALUE, this.handlerValuePool.put(source));
	}

	/** Diese Methode nimmt die gegebene Objektreferenz in die Verwaltung auf und gibt die {@link #toRef(int, int)} Wertreferenz} darauf zurück.
	 *
	 * @param source Objektreferenz.
	 * @return Wertreferenz.
	 * @throws NullPointerException Wenn {@link #getObjectArray(FEMObject)} diese auslöst. */
	public int putObjectValue(final FEMObject source) throws NullPointerException {
		return this.toRef(FEMCodec.TYPE_OBJECT_VALUE, this.objectValuePool.put(source));
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
		return this.toRef(FEMCodec.TYPE_PROXY_FUNCTION, this.proxyFunctionPool.put(source));
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
		return this.toRef(FEMCodec.TYPE_CONCAT_FUNCTION, this.concatFunctionPool.put(source));
	}

	/** Diese Methode nimmt die gegebene Funktionsbindung in die Verwaltung auf und gibt die {@link #toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktionsbindung.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getClosureArray(ClosureFunction)} diese auslöst. */
	public int putClosureFunction(final ClosureFunction source) throws NullPointerException, IllegalArgumentException {
		return this.toRef(FEMCodec.TYPE_CLOSURE_FUNCTION, this.closureFunctionPool.put(source));
	}

	/** Diese Methode nimmt den gegebenen Funktionsaufruf in die Verwaltung auf und gibt die {@link #toRef(int, int)} Funktionsreferenz} darauf zurück.
	 *
	 * @param source Funktionsaufruf.
	 * @return Funktionsreferenz.
	 * @throws NullPointerException Wenn {@link #getCompositeArray(CompositeFunction)} diese auslöst.
	 * @throws IllegalArgumentException Wenn {@link #getCompositeArray(CompositeFunction)} diese auslöst. */
	public int putCompositeFunction(final CompositeFunction source) throws NullPointerException, IllegalArgumentException {
		return this.toRef(FEMCodec.TYPE_COMPOSITE_FUNCTION, this.compositeFunctionPool.put(source));
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

	/** Diese Methode gibt eine Zahlenfolge zurück, welche die einzelwertkodierten Codepoints dieser Zeichenkette enthält. Sie ist die Umkehroperation zu
	 * {@link from}.
	 *
	 * @return Zahlenfolge mit den entsprechend kodierten Codepoints. */
	public static IAMArray toArray_(FEMString s) {
		final FEMString arr = s.compact();
		if (arr instanceof FEMString.CompactStringINT8) return toArray_(s, 1, false);
		if (arr instanceof FEMString.CompactStringINT16) return toArray_(s, 2, false);
		return toArray_(s, 4, false);
	}

	/** Diese Methode gibt eine Zahlenfolge zurück, welche die einzel- oder mehrwertkodierten Codepoints dieser Zeichenkette enthält. Sie ist die Umkehroperation
	 * zu {@link from}.
	 *
	 * @param mode Größe der Zahlen der Zahlenfolge: {@code 1} für {@code 8-Bit}, {@code 2} für {@code 16-Bit} und {@code 4} für {@code 32-Bit}.
	 * @param asUTFx {@code true}, wenn die Codepoints mehrwertkodiert werden sollen. {@code false}, wenn die Codepoints einzelwertkodiert werden sollen.
	 * @return Zahlenfolge mit den entsprechend kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static IAMArray toArray_(FEMString str, final int mode, final boolean asUTFx) {
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

	/** Diese Methode gibt die Anzahl an UTF8-kodierten Codepoints in der gegebenen Tokenliste zurück.
	 *
	 * @param array Tokenliste.
	 * @return Anzahl an UTF8-kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int utf8Size(final IAMArray array) throws IllegalArgumentException {
		int index = 0, result = 0;
		final int length = array.length();
		while (index < length) {
			result++;
			index += FEMString.utf8Size(array.get(index));
		}
		if (index != length) throw new IllegalArgumentException();
		return result;
	}

	/** Diese Methode gibt den UTF8-kodierten Codepoint zurück, der an der gegebenen Position beginnt.
	 *
	 * @param array Tokenliste.
	 * @param offset Position des Tokens, an dem der UTF8-kodierte Codepoint beginnt..
	 * @return Codepoint.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int utf8Codepoint(final IAMArray array, final int offset) throws IllegalArgumentException {
		switch ((array.get(offset) >> 4) & 15) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				return array.get(offset) & 127;
			case 12:
			case 13:
				return ((array.get(offset) & 31) << 6) | (array.get(offset + 1) & 63);
			case 14:
				return ((array.get(offset) & 15) << 12) | ((array.get(offset + 1) & 63) << 6) | (array.get(offset + 2) & 63);
			case 15:
				return ((array.get(offset) & 7) << 18) | ((array.get(offset + 1) & 63) << 12) | ((array.get(offset + 2) & 63) << 6) | (array.get(offset + 3) & 63);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Anzahl an UTF16-kodierten Codepoints in der gegebenen Tokenliste zurück.
	 *
	 * @param array Tokenliste.
	 * @return Anzahl an UTF16-kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int utf16Length(final IAMArray array) throws IllegalArgumentException {
		int index = 0, result = 0;
		final int length = array.length();
		while (index < length) {
			result++;
			index += FEMString.utf16Length(array.get(index));
		}
		if (index != length) throw new IllegalArgumentException();
		return result;
	}

	/** Diese Methode gibt den UTF16-kodierten Codepoint zurück, der an der gegebenen Position beginnt.
	 *
	 * @param array Tokenliste.
	 * @param offset Position des Tokens, an dem der UTF16-kodierte Codepoint beginnt..
	 * @return Codepoint.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int utf16Codepoint(final IAMArray array, final int offset) throws IllegalArgumentException {
		final int token = array.get(offset), value = token & 64512;
		if (value == 55296) return (((token & 1023) << 10) | (array.get(offset + 1) & 1023)) + 65536;
		if (value != 56320) return token;
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
	 * @param asUTFx {@code true}, wenn die Codepoints mehrwertkodiert sind. {@code false}, wenn die Codepoints einzelwertkodiert sind.
	 * @param array Zahlenfolge.
	 *
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
	 * @return Zahlenfolge mit den kodierten Bytes dieser Bytefolge. */	public static IAMArray toArray(FEMBinary b) {
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
