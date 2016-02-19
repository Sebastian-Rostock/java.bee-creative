package bee.creative.iam;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import bee.creative.iam.IAMDecoder.IAMIndexDecoder;
import bee.creative.iam.IAMDecoder.IAMListDecoder;
import bee.creative.iam.IAMDecoder.IAMMapDecoder;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Comparators;
import bee.creative.util.Unique.UniqueMap;

/** Diese Klasse implementiert die Klassen und Methoden zur Kodierung der {code Integer Array Model} Datenstrukturen.
 * 
 * @see MapData
 * @see IAMMapEncoder
 * @see ListData
 * @see IAMListEncoder
 * @see IAMIndexEncoder
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IAMEncoder {

	/** Diese Schnittstelle definiert ein Objekt, das seine Daten gemäß einer gegebenen Bytereihenfolge in eine Zahlenfolge kodieren kann.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface DataEncoder {

		/** Diese Methode kompiliert die gesammelten Daten in eine optimierte Datenstruktur und gibt diese zurück.
		 * 
		 * @param order Bytereihenfolge.
		 * @return optimierte Datenstruktur.
		 * @throws NullPointerException Wenn {@code order} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Datenstruktur nicht in der gegebenen Bytereihenfolge kodiert werden konnte. */
		public byte[] encode(final ByteOrder order) throws NullPointerException, IllegalArgumentException;

	}

	/** Diese Schnittstelle erweitert einen {@link DataEncoder} zur Kodierung und Bereitstellung einer {@link IAMMap}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static interface MapDataEncoder extends DataEncoder {

		/** Diese Methode gibt die durch dieses Objekt bereitgestellte {@link IAMMap} zurück.
		 * 
		 * @return bereitgestellte {@link IAMMap}. */
		public IAMMap toMap();

	}

	/** Diese Schnittstelle erweitert einen {@link DataEncoder} zur Kodierung und Bereitstellung einer {@link IAMList}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static interface ListDataEncoder extends DataEncoder {

		/** Diese Methode gibt die durch dieses Objekt bereitgestellte {@link IAMList} zurück.
		 * 
		 * @return bereitgestellte {@link IAMList}. */
		public IAMList toList();

	}

	/** Diese Klasse implementiert ein Objekt zur Ermittlung der Längen gegebener Zahlenlisten.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class LengthStats {

		/** Dieses Feld speichert den Größentyp.<br>
		 * Der Wert {@code 0} legt fest, dass alle Zahlenfolgen die gleiche Länge {@link #dataLength} besitzen.<br>
		 * Die Werte {@code 1}, {@code 2} und {@code 2} legen fest, dass die kummulierten Längen der Zahlenlisten als {@code UINT8}, {@code UINT16} bzw.
		 * {@code UINT32} in {@link #dataOffset} gespeichert sind.
		 * 
		 * @see IAMEncoder#_computeSizeType_(int) */
		public final int type;

		/** Dieses Feld speichert die Größe des Speicherbereichs für {@link #dataOffset}. */
		public final int bytes;

		/** Dieses Feld speichert die homogene Länge der Zahlenlisten. */
		public final int dataLength;

		/** Dieses Feld speichert die heterogenen Längen der Zahlenlisten. */
		public final int[] dataOffset;

		/** Dieser Konstruktor analysiert die gegebene Zahlenliste und initialisiert die Felder.
		 * 
		 * @param arrays Zahlenliste. */
		LengthStats(final List<int[]> arrays) {
			final int count = arrays.size();
			this.dataOffset = new int[count + 1];
			int minLength = 1073741823, maxLength = 0;
			for (int i = 0, offset = 0; i < count;) {
				final int length = arrays.get(i++).length;
				offset += length;
				this.dataOffset[i] = offset;
				if (length > maxLength) {
					maxLength = length;
				}
				if (length < minLength) {
					minLength = length;
				}
			}
			if (minLength >= maxLength) {
				this.type = 0;
				this.dataLength = maxLength;

				this.bytes = 4;
			} else {
				this.type = IAMEncoder._computeSizeType_(this.dataOffset[count]);
				this.dataLength = 0;
				final int dataOffsetBytes = this.dataOffset.length * IAM._byteCount_(this.type);
				this.bytes = (dataOffsetBytes + 3) & -4;
			}

		}

		{}

		/** Diese Methode schreibt die {@link #dataLength} bzw. das {@link #dataOffset} gemäß {@link #type} in den gegebenen Puffer.
		 * 
		 * @param buffer Puffer. */
		public final void putSize(final ByteBuffer buffer) {
			if (this.type == 0) {
				buffer.putInt(this.dataLength);
			} else {
				IAMEncoder._putArray_(buffer, this.type, this.dataOffset);
			}
		}

	}

	/** Diese Klasse implementiert ein Objekt zur Ermittlung der Kodierung gegebener Zahlenlisten.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class ContentStats {

		/** Dieses Feld speichert den Datentyp.<br>
		 * Die Werte {@code 1}, {@code 2} und {@code 2} legen fest, dass die Werte der Zahlenlisten als {@code INT8}, {@code INT16} bzw. {@code INT32} in
		 * {@link #dataValue} gespeichert sind.
		 * 
		 * @see IAMEncoder#_computeDataType_(int) */
		public final int type;

		/** Dieses Feld speichert die Größe des Speicherbereichs für {@link #dataValue}. */
		public final int bytes;

		/** Dieses Feld speichert die Längen gegebener Zahlenlisten. */
		public final LengthStats dataSize;

		/** Dieses Feld speichert die Werte der Zahlenlisten. */
		public final int[] dataValue;

		/** Dieser Konstruktor analysiert die gegebene Zahlenliste und initialisiert die Felder.
		 * 
		 * @param arrays Zahlenliste. */
		ContentStats(final List<int[]> arrays) {
			this.dataSize = new LengthStats(arrays);
			final int count = arrays.size();
			this.dataValue = new int[this.dataSize.dataOffset[count]];
			int minValue = 0, maxValue = 0;
			for (int i = 0, offset = 0; i < count; i++) {
				final int[] item = arrays.get(i);
				for (int i2 = 0, length = item.length; i2 < length; i2++) {
					final int value = item[i2];
					if (value > maxValue) {
						maxValue = value;
					}
					if (value < minValue) {
						minValue = value;
					}
					this.dataValue[offset++] = value;
				}
			}
			this.type = Math.max(IAMEncoder._computeDataType_(minValue), IAMEncoder._computeDataType_(maxValue));
			final int dataValueBytes = this.dataValue.length * IAM._byteCount_(this.type);
			this.bytes = (dataValueBytes + 3) & -4;
		}

		{}

		/** Diese Methode schreibt die {@link #dataValue} gemäß {@link #type} in den gegebenen Puffer.
		 * 
		 * @param buffer Puffer. */
		public final void putData(final ByteBuffer buffer) {
			IAMEncoder._putArray_(buffer, this.type, this.dataValue);
		}

	}

	/** Diese Klasse implementiert einen {@link MapDataEncoder}, dessen {@link IAMMap} aus einer gegebenen Zahlenfolge dekodiert wird.
	 * 
	 * @see IAMMapDecoder
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class MapData implements MapDataEncoder {

		/** Dieses Feld speichert die {@link IAMMap}. */
		final IAMMap _data_;

		/** Dieses Feld speichert die kodierte Zahlenfolge. */
		final byte[] _array_;

		/** Dieses Feld speichert die Bytereihenfolge. */
		final ByteOrder _order_;

		/** Dieser Konstruktor initialisiert die Zahlenfolge mit den kodierten Daten eines {@link IAMMapDecoder}.
		 * 
		 * @param bytes Zahlenfolge.
		 * @throws NullPointerException Wenn {@code bytes} {@code null} ist.
		 * @throws IAMException Wenn beim dekodieren der Zahlenfolge ein Fehler erkannt wird. */
		public MapData(final byte[] bytes) throws NullPointerException, IAMException {
			this._array_ = bytes.clone();
			this._order_ = IAMListDecoder.HEADER.orderOf(bytes);
			this._data_ = new IAMMapDecoder(new MMFArray(this._array_, this._order_));
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final byte[] encode(final ByteOrder order) throws NullPointerException, IllegalArgumentException {
			if (!order.equals(this._order_)) throw new IllegalArgumentException("order invalid");
			return this._array_.clone();
		}

		/** {@inheritDoc} */
		@Override
		public final IAMMap toMap() {
			return this._data_;
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return this._data_.toString();
		}

	}

	/** Diese Klasse implementiert einen {@link ListDataEncoder}, dessen {@link IAMList} aus einer gegebenen Zahlenfolge dekodiert wird.
	 * 
	 * @see IAMListDecoder
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class ListData implements ListDataEncoder {

		/** Dieses Feld speichert die {@link IAMList}. */
		final IAMList _data_;

		/** Dieses Feld speichert die kodierte Zahlenfolge. */
		final byte[] _array_;

		/** Dieses Feld speichert die Bytereihenfolge. */
		final ByteOrder _order_;

		/** Dieser Konstruktor initialisiert die Zahlenfolge mit den kodierten Daten eines {@link IAMListDecoder}.
		 * 
		 * @param bytes Zahlenfolge.
		 * @throws NullPointerException Wenn {@code bytes} {@code null} ist.
		 * @throws IAMException Wenn beim dekodieren der Zahlenfolge ein Fehler erkannt wird. */
		public ListData(final byte[] bytes) throws NullPointerException, IAMException {
			this._array_ = bytes.clone();
			this._order_ = IAMListDecoder.HEADER.orderOf(this._array_);
			this._data_ = new IAMListDecoder(new MMFArray(this._array_, this._order_));
		}

		{}

		/** {@inheritDoc} */
		@Override
		public byte[] encode(final ByteOrder order) throws NullPointerException, IllegalArgumentException {
			if (!order.equals(this._order_)) throw new IllegalArgumentException("order invalid");
			return this._array_.clone();
		}

		/** {@inheritDoc} */
		@Override
		public IAMList toList() {
			return this._data_;
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return this._data_.toString();
		}

	}

	/** Diese Klasse implementiert ein Element einer {@link UniqueItemPool} eines {@link IAMListEncoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class ItemData {

		/** Dieses Feld speichert den Index, unter dem dieses Objekt in {@link UniquePool#datas} verwaltet wird. */
		public int index;

		/** Dieses Feld speichert die Zahlenfolge des Elements. */
		public int[] item;

	}

	/** Diese Klasse implementiert ein Element einer {@link UniqueEntryPool} eines {@link IAMMapEncoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class EntryData {

		/** Dieses Feld speichert den Index, unter dem dieses Objekt in {@link UniquePool#datas} verwaltet wird. */
		public int index;

		/** Dieses Feld speichert die Zahlenfolge des Schlüssels. */
		public int[] key;

		/** Dieses Feld speichert die Zahlenfolge des Werts. */
		public int[] value;

	}

	/** Diese Klasse implementiert eine abstrakte {@link UniqueMap} mit Zahlenlisten als Eingabe.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten (Ausgabe). */
	static abstract class UniquePool<GData> extends UniqueMap<int[], GData> {

		/** Dieses Feld speichert die gesammelten Nutzdaten. */
		public final List<GData> datas = new ArrayList<>();

		{}

		/** Diese Methode erzeugt einen neuen Nutzdatensatz und gibt diesen zurück.
		 * 
		 * @param index Index, unter dem der Nutzdatensatz in {@link #datas} verwaltet wird.
		 * @param array Zahlenliste.
		 * @return Nutzdatensatz. */
		protected abstract GData create(int index, int[] array);

		/** Diese Methode nimmt einen neuen Nutzdatensatz mit der gegebenen Zahlenliste in die Verwaltung auf und gibt den Index zurück, unter dem diese in
		 * {@link #datas} verwaltet werden.
		 * 
		 * @param array Zahlenliste.
		 * @return Nutzdatensatz.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
		public final GData put(final int[] array) throws NullPointerException {
			final GData data = this.create(this.datas.size(), array.clone());
			this.datas.add(data);
			return data;
		}

		/** {@inheritDoc} */
		@Override
		public final void clear() {
			super.clear();
			this.datas.clear();
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final boolean check(final Object input) {
			return input instanceof int[];
		}

		/** {@inheritDoc} */
		@Override
		protected final GData compile(final int[] array) {
			return this.put(array);
		}

		/** {@inheritDoc} */
		@Override
		public final int hash(final int[] array) throws NullPointerException {
			return IAMEncoder._hash_(array);
		}

		/** {@inheritDoc} */
		@Override
		public final boolean equals(final int[] array1, final int[] array2) throws NullPointerException {
			return IAMEncoder._equals_(array1, array2);
		}

		/** {@inheritDoc} */
		@Override
		public final int compare(final int[] array1, final int[] array2) throws NullPointerException {
			return IAMEncoder._compare_(array1, array2);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UniqueItemPool extends UniquePool<ItemData> {

		@Override
		protected final ItemData create(final int index, final int[] array) {
			final ItemData data = new ItemData();
			data.index = index;
			data.item = array;
			return data;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UniqueEntryPool extends UniquePool<EntryData> {

		@Override
		protected final EntryData create(final int index, final int[] array) {
			final EntryData data = new EntryData();
			data.index = index;
			data.key = array;
			data.value = array;
			return data;
		}

	}

	/** Diese Klasse implementiert eine {@link IAMMap} zur Zusammenstellung und Kodierung der Daten für einen {@link IAMMapDecoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class IAMMapEncoder extends IAMMap implements MapDataEncoder {

		/** Dieses Feld speichert den Modus. */
		boolean _mode_ = IAMMap.MODE_HASHED;

		/** Dieses Feld speichert die Einträge. */
		final UniqueEntryPool _entries_ = new UniqueEntryPool();

		/** Dieser Konstruktor initialisiert einen leeren {@link IAMMapEncoder}. */
		public IAMMapEncoder() {
		}

		{}

		/** Diese Methode gibt das modifizierbare {@code int}-Array des Werts des {@code entryIndex}-ten Eintrags zurück.
		 * 
		 * @see #entryCount()
		 * @param entryIndex Index des Eintrags.
		 * @return {@code int}-Array des Werts des {@code entryIndex}-ten Eintrags.
		 * @throws IndexOutOfBoundsException Wenn {@code entryIndex} ungültig ist. */
		public final int[] get(final int entryIndex) throws IndexOutOfBoundsException {
			return this._entries_.datas.get(entryIndex).value;
		}

		/** Diese Methode fügt einen Eintrag mit dem gegebenen Schlüssel sowie dem gegebenen Wert hinzu. Wenn bereits ein Eintrag mit diesem Schlüssel existiert,
		 * wird dessen Wert ersetzt.
		 * 
		 * @param key Schlüssel.
		 * @param value Wert.
		 * @throws NullPointerException Wenn {@code key} bzw. {@code value} {@code null} ist. */
		public final void put(final int[] key, final int[] value) throws NullPointerException {
			if (key == null) throw new NullPointerException("key = null");
			if (value == null) throw new NullPointerException("value = null");
			this._entries_.get(key).value = value;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean mode() {
			return this._mode_;
		}

		/** Diese Methode setzt den Modus.
		 * 
		 * @see #MODE_HASHED
		 * @see #MODE_SORTED
		 * @param mode Modus. */
		public final void mode(final boolean mode) {
			this._mode_ = mode;
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public final void clear() {
			this._entries_.clear();
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final IAMArray key(final int entryIndex) {
			final List<EntryData> datas = this._entries_.datas;
			if ((entryIndex < 0) || (entryIndex >= datas.size())) return IAMArray.EMPTY;
			return IAMArray.from(datas.get(entryIndex).key);
		}

		/** {@inheritDoc} */
		@Override
		public final IAMArray value(final int entryIndex) {
			final List<EntryData> datas = this._entries_.datas;
			if ((entryIndex < 0) || (entryIndex >= datas.size())) return IAMArray.EMPTY;
			return IAMArray.from(datas.get(entryIndex).value);
		}

		/** {@inheritDoc} */
		@Override
		public final int entryCount() {
			return this._entries_.datas.size();
		}

		/** {@inheritDoc} */
		@Override
		public final int find(final IAMArray key) throws NullPointerException {
			final EntryData result = this._entries_.entryMap().get(key.toArray());
			return result == null ? -1 : result.index;
		}

		/** {@inheritDoc}
		 * 
		 * @see #MODE_HASHED
		 * @see #MODE_SORTED
		 * @see #put(int[], int[])
		 * @return {@code IAM_MAP}-Datenstruktur. */
		@Override
		public final byte[] encode(final ByteOrder order) throws NullPointerException, IllegalArgumentException {
			if (order == null) throw new NullPointerException("owder = null");
			final List<EntryData> datas = this._entries_.datas;
			final int count = datas.size();
			final EntryData[] entries = datas.toArray(new EntryData[count]);
			int rangeMask;
			int rangeCount;
			int[] rangeData;
			int rangeDataType;
			int rangeDataBytes;
			int rangeBytes;
			if (this._mode_ == IAMMap.MODE_HASHED) {
				rangeMask = IAMEncoder._computeRangeMask_(count);
				rangeCount = rangeMask + 2;
				rangeData = new int[rangeCount];
				rangeDataType = IAMEncoder._computeSizeType_(count);
				rangeDataBytes = rangeCount * IAM._byteCount_(rangeDataType);
				rangeBytes = ((rangeDataBytes + 3) & -4) + 4;
				final int[] rangeIndex = new int[count];
				for (int i = 0; i < count; i++) {
					final int index = IAMEncoder._hash_(entries[i].key) & rangeMask;
					rangeData[index]++;
					rangeIndex[i] = index;
				}
				int offset = 0;
				for (int i = 0; i < rangeCount; i++) {
					final int value = rangeData[i];
					rangeData[i] = offset;
					offset += value;
				}
				Arrays.sort(entries, new Comparator<EntryData>() {

					@Override
					public int compare(final EntryData o1, final EntryData o2) {
						return rangeIndex[o1.index] - rangeIndex[o2.index];
					}

				});
			} else {
				Arrays.sort(entries, new Comparator<EntryData>() {

					@Override
					public int compare(final EntryData o1, final EntryData o2) {
						return IAMEncoder._compare_(o1.key, o2.key);
					}

				});
				rangeMask = 0;
				rangeData = null;
				rangeDataType = 0;
				rangeDataBytes = 0;
				rangeBytes = 0;
			}
			final ContentStats keyData = new ContentStats(new AbstractList<int[]>() {

				@Override
				public int[] get(final int index) {
					return entries[index].key;
				}

				@Override
				public int size() {
					return count;
				}

			});
			final LengthStats keySize = keyData.dataSize;
			final ContentStats valueData = new ContentStats(new AbstractList<int[]>() {

				@Override
				public int[] get(final int index) {
					return entries[index].value;
				}

				@Override
				public int size() {
					return count;
				}

			});
			final LengthStats valueSize = valueData.dataSize;
			final int length = 8 + rangeBytes + keySize.bytes + keyData.bytes + valueSize.bytes + valueData.bytes;
			final byte[] result = new byte[length];
			final ByteBuffer buffer = ByteBuffer.wrap(result).order(order);
			buffer.putInt(0xF00D1000 | (keyData.type << 8) | (keySize.type << 6) | (rangeDataType << 4) | (valueData.type << 2) | (valueSize.type << 0));
			buffer.putInt(count);
			if (rangeDataType != 0) {
				buffer.putInt(rangeMask);
				IAMEncoder._putArray_(buffer, rangeDataType, rangeData);
			}
			keySize.putSize(buffer);
			keyData.putData(buffer);
			valueSize.putSize(buffer);
			valueData.putData(buffer);
			return result;
		}

		/** {@inheritDoc} */
		@Override
		public final IAMMap toMap() {
			return this;
		}

	}

	/** Diese Klasse implementiert eine {@link IAMList} zur Zusammenstellung und Kodierung der Daten für einen {@link IAMListDecoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class IAMListEncoder extends IAMList implements ListDataEncoder {

		/** Dieses Feld speichert die bisher gesammelten Elemente. */
		final UniqueItemPool _items_ = new UniqueItemPool();

		/** Dieser Konstruktor initialisiert einen leeren {@link IAMListEncoder}. */
		public IAMListEncoder() {
		}

		{}

		/** Diese Methode gibt das modifizierbare {@code int}-Array des {@code itemIndex}-te Elements zurück.<br>
		 * Dieses Array sollte nur dann verändert werden, wenn es über {@link #put(int[], boolean)} ohne Wiederverwendung hunzugefügt wurde.
		 * 
		 * @see #put(int[], boolean)
		 * @see #itemCount()
		 * @param itemIndex Index des Elements.
		 * @return {@code int}-Array des {@code itemIndex}-ten Elements.
		 * @throws IndexOutOfBoundsException Wenn eine der Eingaben ungültig ist. */
		public final int[] get(final int itemIndex) throws IndexOutOfBoundsException {
			return this._items_.datas.get(itemIndex).item;
		}

		/** Diese Methode fügt das gegebene Element hinzu und gibt den Index zurück, unter dem das Element verwaltet wird. Wenn bereits ein Element mit dem gleichen
		 * Daten existiert, wird dessen Index zurück gegeben.
		 * <p>
		 * <u>Achtung:</u> Der Index ist garantiert der gleiche, der im {@link IAMListDecoder} verwerden wird.
		 * 
		 * @see #put(int[], boolean)
		 * @param value Element.
		 * @return Index, unter dem die Zahlenliste in der optimierten Datenstruktur registriert ist.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public final int put(final int[] value) throws NullPointerException {
			return this._items_.put(value).index;
		}

		/** Diese Methode fügt das gegebene Element hinzu und gibt den Index zurück, unter dem das Element verwaltet wird. Wenn die Wiederverwendung aktiviert ist
		 * und bereits ein Element mit dem gleichen Daten existiert, wird dessen Index zurück gegeben.
		 * <p>
		 * <u>Achtung:</u> Der Index ist garantiert der gleiche, der im {@link IAMListDecoder} verwerden wird.
		 * 
		 * @param value Element.
		 * @param reuse {@code true}, wenn die Wiederverwendung aktiviert ist.
		 * @return Index, unter dem die Zahlenliste in der optimierten Datenstruktur registriert ist.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public final int put(final int[] value, final boolean reuse) throws NullPointerException {
			if (reuse) return this._items_.get(value).index;
			return this._items_.put(value).index;
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public final void clear() {
			this._items_.clear();
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final IAMArray item(final int itemIndex) {
			final List<ItemData> datas = this._items_.datas;
			if ((itemIndex < 0) || (itemIndex >= datas.size())) return IAMArray.EMPTY;
			return IAMArray.from(datas.get(itemIndex).item);
		}

		/** {@inheritDoc} */
		@Override
		public final int itemCount() {
			return this._items_.datas.size();
		}

		/** {@inheritDoc}
		 * 
		 * @see #put(int[])
		 * @see #put(int[], boolean)
		 * @return {@code IAM_LIST}-Datenstruktur. */
		@Override
		public final byte[] encode(final ByteOrder order) throws NullPointerException, IllegalArgumentException {
			if (order == null) throw new NullPointerException("order = null");
			final List<ItemData> datas = this._items_.datas;
			final int count = datas.size();
			final ContentStats itemData = new ContentStats(new AbstractList<int[]>() {

				@Override
				public int[] get(final int index) {
					return datas.get(index).item;
				}

				@Override
				public int size() {
					return count;
				}

			});
			final LengthStats itemSize = itemData.dataSize;
			final int length = 8 + itemSize.bytes + itemData.bytes;
			final byte[] result = new byte[length];
			final ByteBuffer buffer = ByteBuffer.wrap(result).order(order);
			buffer.putInt(0xF00D2000 | (itemData.type << 2) | (itemSize.type << 0));
			buffer.putInt(count);
			itemSize.putSize(buffer);
			itemData.putData(buffer);
			return result;
		}

		/** {@inheritDoc} */
		@Override
		public final IAMList toList() {
			return this;
		}

	}

	/** Diese Klasse implementiert eine {@link IAMIndex} zur Zusammenstellung und Kodierung der Daten für einen {@link IAMIndexDecoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class IAMIndexEncoder extends IAMIndex implements DataEncoder {

		/** Dieses Feld speichert die {@link IAMMapEncoder}. */
		final List<MapDataEncoder> _maps_ = new ArrayList<>();

		/** Dieses Feld speichert die {@link IAMListEncoder}. */
		final List<ListDataEncoder> _lists_ = new ArrayList<>();

		/** Dieses Feld speichert die Bytereihenfolge oder {@code null}.
		 * 
		 * @see #__setOrder(ByteOrder) */
		ByteOrder _order_ = null;

		/** Dieser Konstruktor initialisiert einen leeren {@link IAMIndexEncoder}. */
		public IAMIndexEncoder() {
		}

		{}

		/** Diese Methode setzt die Bytereihenfolge.
		 * 
		 * @param value Bytereihenfolge.
		 * @throws IllegalArgumentException Wenn die Bytereihenfolge ungültig ist. */
		final void __setOrder(final ByteOrder value) throws IllegalArgumentException {
			this.__checkOrder(value);
			this._order_ = value;
		}

		/** Diese Methode prüft, ob die gegebene Bytereihenfolge kompatibel zur aktuellen ist.
		 * 
		 * @param value Bytereihenfolge.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Bytereihenfolge ungültig ist. */
		final void __checkOrder(final ByteOrder value) throws NullPointerException, IllegalArgumentException {
			final ByteOrder order = this._order_;
			if ((order != null) && !value.equals(order)) throw new IllegalArgumentException("order invalid");
		}

		@SuppressWarnings ("javadoc")
		final int __putMap(final MapDataEncoder map) throws NullPointerException {
			final int result = this._maps_.size();
			this._maps_.add(result, map);
			return result;
		}

		@SuppressWarnings ("javadoc")
		final int __putList(final ListDataEncoder list) throws NullPointerException {
			final int result = this._lists_.size();
			this._lists_.add(result, list);
			return result;
		}

		/** Diese Methode fügt die gegebene {@code IAM_MAP}-Datenstruktur hinzu und gibt den Index zurück, unter dem die dadurch beschriebene {@link IAMMap}
		 * verwaltet wird.<br>
		 * Die {@code IAM_MAP}-Datenstruktur kann beispielsweise über {@link IAMMapEncoder#encode(ByteOrder)} erzeugt werden.
		 * 
		 * @param value {@code IAM_MAP}-Datenstruktur.
		 * @return Index der {@link IAMMap}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Bytereihenfolge der gegbenen Datenstruktur inkompatibel zu der bereits hinzugefügter Datenstrukturen ist. */
		public final int putMap(final byte[] value) throws NullPointerException, IllegalArgumentException {
			final MapData data = new MapData(value);
			this.__setOrder(data._order_);
			return this.__putMap(data);
		}

		/** Diese Methode fügt den gegebenen {@link IAMMapEncoder} hinzu und gibt den Index zurück, unter dem die dadurch beschriebene {@link IAMMap} verwaltet wird.
		 * 
		 * @param map {@link IAMMapEncoder}.
		 * @return Index der {@link IAMMap}.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public final int putMap(final IAMMapEncoder map) throws NullPointerException {
			map.toMap();
			return this.__putMap(map);
		}

		/** Diese Methode fügt die gegebene {@code IAM_LIST}-Datenstruktur hinzu und gibt den Index zurück, unter dem die dadurch beschriebene {@link IAMList}
		 * verwaltet wird.<br>
		 * Die {@code IAM_LIST}-Datenstruktur kann beispielsweise über {@link IAMListEncoder#encode(ByteOrder)} erzeugt werden.
		 * 
		 * @param list {@code IAM_LIST}-Datenstruktur.
		 * @return Index der {@link IAMList}.
		 * @throws NullPointerException Wenn {@code list} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Bytereihenfolge der gegbenen Datenstruktur inkompatibel zu der bereits hinzugefügter Datenstrukturen ist. */
		public final int putList(final byte[] list) throws NullPointerException, IllegalArgumentException {
			final ListData data = new ListData(list);
			this.__setOrder(data._order_);
			return this.__putList(data);
		}

		/** Diese Methode fügt den gegebenen {@link IAMListEncoder} hinzu und gibt den Index zurück, unter dem die dadurch beschriebene {@link IAMList} verwaltet
		 * wird.
		 * 
		 * @see ListData
		 * @see IAMListEncoder
		 * @param list {@link IAMListEncoder}.
		 * @return Index der {@link IAMList}.
		 * @throws NullPointerException Wenn {@code list} {@code null} ist. */
		public final int putList(final IAMListEncoder list) throws NullPointerException {
			list.toList();
			return this.__putList(list);
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public final void clear() {
			this._maps_.clear();
			this._lists_.clear();
			this._order_ = null;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final IAMMap map(final int index) {
			if ((index < 0) || (index >= this._maps_.size())) return IAMMap.EMPTY;
			return this._maps_.get(index).toMap();
		}

		/** {@inheritDoc} */
		@Override
		public final int mapCount() {
			return this._maps_.size();
		}

		/** {@inheritDoc} */
		@Override
		public final IAMList list(final int index) {
			if ((index < 0) || (index >= this._lists_.size())) return IAMList.EMPTY;
			return this._lists_.get(index).toList();
		}

		/** {@inheritDoc} */
		@Override
		public final int listCount() {
			return this._lists_.size();
		}

		/** {@inheritDoc}
		 * 
		 * @see #putMap(byte[])
		 * @see #putMap(IAMMapEncoder)
		 * @see #putList(byte[])
		 * @see #putList(IAMListEncoder)
		 * @return {@code IAM_INDEX}-Datenstruktur.
		 * @throws IllegalArgumentException Wenn die Bytereihenfolge inkompatibel zu der bereits hinzugefügter Datenstrukturen ist. */
		@Override
		public final byte[] encode(final ByteOrder order) throws NullPointerException, IllegalArgumentException {
			this.__checkOrder(order);
			final byte[][] maps = IAMEncoder._encodeBytes_(this._maps_, order);
			final byte[][] lists = IAMEncoder._encodeBytes_(this._lists_, order);
			int length = 12;
			length += (maps.length + lists.length + 2) << 2;
			length += IAMEncoder.length(maps);
			length += IAMEncoder.length(lists);
			final byte[] result = new byte[length];
			final ByteBuffer buffer = ByteBuffer.wrap(result).order(order);
			buffer.putInt(0xF00DBA5E);
			buffer.putInt(maps.length);
			buffer.putInt(lists.length);
			IAMEncoder._putSize_(buffer, maps);
			IAMEncoder._putSize_(buffer, lists);
			IAMEncoder._putData_(buffer, maps);
			IAMEncoder._putData_(buffer, lists);
			return result;
		}

	}

	{}

	/** Diese Methode füht die Startpositionen der gegebenen Zahlenfolgen an den gegebenen {@link ByteBuffer} an. Die Zahlenfolgen repräsentieren die von
	 * {@link IAMMapEncoder#encode(ByteOrder)} bzw. {@link IAMListEncoder#encode(ByteOrder)} kodierten Datenstrukturen.
	 * 
	 * @param buffer {@link ByteBuffer}.
	 * @param source Zahlenfolgen. */
	static final void _putSize_(final ByteBuffer buffer, final byte[][] source) {
		int offset = 0;
		buffer.putInt(0);
		for (final byte[] data: source) {
			offset += data.length >> 2;
			buffer.putInt(offset);
		}
	}

	/** Diese Methode füht die gegebenen Zahlenfolgen an den gegebenen {@link ByteBuffer} an. Die Zahlenfolgen repräsentieren die von
	 * {@link IAMMapEncoder#encode(ByteOrder)} bzw. {@link IAMListEncoder#encode(ByteOrder)} kodierten Datenstrukturen.
	 * 
	 * @param buffer {@link ByteBuffer}.
	 * @param source Zahlenfolgen. */
	static final void _putData_(final ByteBuffer buffer, final byte[][] source) {
		IAMEncoder.write(source, buffer);
	}

	/** Diese Methode speichert die gegebene Zahlenfolge an den gegebenen {@link ByteBuffer} an. Der geschriebene Speicherbereich wird mit Nullwerten ergänzt, um
	 * eine restlos durch vier teilbare Größe zu erreichen.
	 * 
	 * @see ByteBuffer#put(byte)
	 * @see ByteBuffer#putInt(int)
	 * @see ByteBuffer#putShort(short)
	 * @param buffer {@link ByteBuffer}.
	 * @param type Datentyp ({@code 1=INT8/UINT8}, {@code 2=INT16/UINT16}, {@code 3=INT32}).
	 * @param values Zahlenfolge. */
	static final void _putArray_(final ByteBuffer buffer, final int type, final int[] values) {
		switch (type) {
			case 1:
				for (int i = 0, length = values.length; i < length; i++) {
					buffer.put((byte)values[i]);
				}
				switch (values.length & 3) {
					case 1:
						buffer.put((byte)0);
					case 2:
						buffer.put((byte)0);
					case 3:
						buffer.put((byte)0);
				}
			break;
			case 2:
				for (int i = 0, length = values.length; i < length; i++) {
					buffer.putShort((short)values[i]);
				}
				if ((values.length & 1) == 1) {
					buffer.putShort((short)0);
				}
			break;
			case 3:
				for (int i = 0, length = values.length; i < length; i++) {
					buffer.putInt(values[i]);
				}
			break;
		}
	}

	/** Diese Methode kodiert die gegebenen {@link DataEncoder} in Zahlenfolgen.
	 * 
	 * @see DataEncoder#encode(ByteOrder)
	 * @param source Quelldaten.
	 * @param order Bytereihenfolge
	 * @return Zahlenfolgen. */
	static final byte[][] _encodeBytes_(final List<? extends DataEncoder> source, final ByteOrder order) {
		final int count = source.size();
		final byte[][] result = new byte[count][];
		for (int i = 0; i < count; i++) {
			result[i] = source.get(i).encode(order);
		}
		return result;
	}

	/** Diese Methode gibt den Streuwert der gegebenen Zahlenfolge zurück.
	 * 
	 * @see IAMArray#hash()
	 * @param array Zahlenfolge.
	 * @return Streuwert.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	static final int _hash_(final int[] array) throws NullPointerException {
		int hash = 0x811C9DC5;
		for (int i = 0, size = array.length; i < size; i++) {
			hash = (hash * 0x01000193) ^ array[i];
		}
		return hash;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebenen Zahlenfolgen gleich sind.
	 * 
	 * @see IAMArray#equals(IAMArray)
	 * @param array1 erste Zahlenfolge.
	 * @param array2 zweite Zahlenfolge.
	 * @return {@code true}, wenn die Zahlenfolgen gleich sind.
	 * @throws NullPointerException Wenn {@code array1} bzw. {@code array2} {@code null} ist. */
	static final boolean _equals_(final int[] array1, final int[] array2) throws NullPointerException {
		final int length1 = array1.length, length2 = array2.length;
		if (length1 != length2) return false;
		for (int i = 0; i < length1; i++)
			if (array1[i] != array2[i]) return false;
		return true;
	}

	/** Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn die Ordnung der ersten Zahlenfolge lexikografisch kleiner, gleich bzw.
	 * größer als die der zweiten Zahlenfolge ist.
	 * 
	 * @see IAMArray#compare(IAMArray)
	 * @param array1 erste Zahlenfolge.
	 * @param array2 zweite Zahlenfolge.
	 * @return Vergleichswert der Ordnungen.
	 * @throws NullPointerException Wenn {@code array1} bzw. {@code array2} {@code null} ist. */
	static final int _compare_(final int[] array1, final int[] array2) throws NullPointerException {
		final int length1 = array1.length, length2 = array2.length;
		for (int i = 0, length = length1 < length2 ? length1 : length2, result; i < length; i++)
			if ((result = Comparators.compare(array1[i], array2[i])) != 0) return result;
		return length1 - length2;
	}

	/** Diese Methode gibt den Datentyp für die gegebene Größe zurück.<br>
	 * Die Datentypen {@code 1}, {@code 2} und {@code 3} stehen für {@code UINT8}, {@code UINT16} und {@code UINT32}.
	 * 
	 * @param value Größe.
	 * @return Datentyp ({@code 1..3}). */
	static final int _computeSizeType_(final int value) {
		if (value <= 255) return 1;
		if (value <= 65535) return 2;
		return 3;
	}

	/** Diese Methode gibt den Datentyp für den gegebenen Wert zurück.<br>
	 * Die Datentypen {@code 1}, {@code 2} und {@code 3} stehen für {@code INT8}, {@code INT16} und {@code INT32}.
	 * 
	 * @param value Wert.
	 * @return Datengrößentyps ({@code 1..3}). */
	static final int _computeDataType_(final int value) {
		if ((-128 <= value) && (value <= 127)) return 1;
		if ((-32768 <= value) && (value <= 32767)) return 2;
		return 3;
	}

	/** Diese Methode gibt die Bitmaske zur Umrechnung von Streuwerten zurück.
	 * 
	 * @param entryCount Anzahl der Einträge der Abbildung.
	 * @return Bitmaske. */
	static final int _computeRangeMask_(final int entryCount) {
		int result = 2;
		while (result < entryCount) {
			result <<= 1;
		}
		return (result - 1) & 536870911;
	}

	/** Diese Methode speichert die gegebenen Bytefolgen in die gegebene Datei.
	 * 
	 * @see #write(byte[][], RandomAccessFile)
	 * @param source Bytefolgen.
	 * @param target Datei.
	 * @throws IOException Wenn ein E/A-Fehler eintritt.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist oder enthält bzw. {@code target} {@code null} ist. */
	public static final void write(final byte[][] source, final File target) throws IOException, NullPointerException {
		final RandomAccessFile file = new RandomAccessFile(target, "rw");
		try {
			IAMEncoder.write(source, file);
		} finally {
			file.close();
		}
	}

	/** Diese Methode fügt die gegebenen Bytefolgen an den gegebenen {@link ByteBuffer} an.
	 * 
	 * @see ByteBuffer#put(byte[])
	 * @param source Bytefolgen.
	 * @param target {@link ByteBuffer}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist oder enthält bzw. {@code target} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Bytefolge aus {@code source} nicht in {@code target} passen. */
	public static final void write(final byte[][] source, final ByteBuffer target) throws NullPointerException, IllegalArgumentException {
		if (target.remaining() < IAMEncoder.length(source)) throw new IllegalArgumentException();
		for (final byte[] data: source) {
			target.put(data);
		}
	}

	/** Diese Methode speichert die gegebenen Bytefolgen in die gegebene Datei.
	 * 
	 * @see RandomAccessFile#write(byte[])
	 * @param source Bytefolgen.
	 * @param target Datei.
	 * @throws IOException Wenn ein E/A-Fehler eintritt.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist oder enthält bzw. {@code target} {@code null} ist. */
	public static final void write(final byte[][] source, final RandomAccessFile target) throws IOException, NullPointerException {
		for (final byte[] data: source) {
			target.write(data);
		}
	}

	/** Diese Methode gibt die Summe der Anzahl der Bytes in den gegebenen Bytefolgen zurück.
	 * 
	 * @param source Bytefolgen.
	 * @return Anzahl der Bytes.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist oder enthält. */
	public static final int length(final byte[][] source) throws NullPointerException {
		int length = 0;
		for (final byte[] bytes: source) {
			length += bytes.length;
		}
		return length;
	}

	/** Diese Methode gibt die Summe der Anzahl der Bytes in den gegebenen Bytefolgen zurück.
	 * 
	 * @see #write(byte[][], ByteBuffer)
	 * @param source Bytefolgen.
	 * @return Anzahl der Bytes.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist oder enthält. */
	public static final byte[] compact(final byte[][] source) throws NullPointerException {
		final byte[] target = new byte[IAMEncoder.length(source)];
		IAMEncoder.write(source, ByteBuffer.wrap(target));
		return target;
	}

}
