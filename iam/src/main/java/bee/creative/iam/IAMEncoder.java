package bee.creative.iam;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.swing.text.html.ListView;
import bee.creative.iam.IAM.IAMBaseIndex;
import bee.creative.iam.IAM.IAMBaseList;
import bee.creative.iam.IAM.IAMBaseMap;
import bee.creative.iam.IAM.IAMEmptyArray;
import bee.creative.iam.IAM.IAMEmptyList;
import bee.creative.iam.IAM.IAMEmptyMap;
import bee.creative.iam.IAM.IAMValueArray;
import bee.creative.iam.IAMDecoder.IAMIndexDecoder;
import bee.creative.iam.IAMDecoder.IAMListDecoder;
import bee.creative.iam.IAMDecoder.IAMMapDecoder;
import bee.creative.util.Unique.UniqueMap;

/**
 * Diese Klasse implementiert die Klassen und Methoden zur Kodierung der {@link IAM} Datenstrukturen.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class IAMEncoder {

	/**
	 * Diese Klasse implementiert ein Objekt zur Ermittlung der Längen gegebener Zahlenlisten.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static final class IAMSize {

		/**
		 * Dieses Feld speichert den Größentyp.<br>
		 * Der Wert {@code 0} legt fest, dass alle Zahlenfolgen die gleiche Länge {@link #dataLength} besitzen.<br>
		 * Die Werte {@code 1}, {@code 2} und {@code 2} legen fest, dass die kummulierten Längen der Zahlenlisten als {@code UINT8}, {@code UINT16} bzw.
		 * {@code UINT32} in {@link #dataOffset} gespeichert sind.
		 * 
		 * @see IAMEncoder#computeSizeType(int)
		 */
		public final int type;

		/**
		 * Dieses Feld speichert die Größe des Speicherbereichs für {@link #dataOffset}.
		 */
		public final int bytes;

		/**
		 * Dieses Feld speichert die homogene Länge der Zahlenlisten.
		 */
		public final int dataLength;

		/**
		 * Dieses Feld speichert die heterogenen Längen der Zahlenlisten.
		 */
		public final int[] dataOffset;

		/**
		 * Dieser Konstruktor analysiert die gegebene Zahlenliste und initialisiert die Felder.
		 * 
		 * @param arrays Zahlenliste.
		 */
		public IAMSize(final List<int[]> arrays) {
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
				this.type = IAMEncoder.computeSizeType(this.dataOffset[count]);
				this.dataLength = 0;
				final int dataOffsetBytes = this.dataOffset.length * IAM.byteCount(this.type);
				this.bytes = (dataOffsetBytes + 3) & -4;
			}
		}

		/**
		 * Diese Methode schreibt die {@link #dataLength} bzw. {@link #dataOffset} gemäß {@link #type} in den gegebenen Puffer.
		 * 
		 * @param buffer Puffer.
		 */
		public void putSize(final ByteBuffer buffer) {
			if (this.type == 0) {
				buffer.putInt(this.dataLength);
			} else {
				IAMEncoder.putArray(buffer, this.type, this.dataOffset);
			}
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Ermittlung der Kodierung gegebener Zahlenlisten.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static final class IAMData {

		/**
		 * Dieses Feld speichert den Datentyp.<br>
		 * Die Werte {@code 1}, {@code 2} und {@code 2} legen fest, dass die Werte der Zahlenlisten als {@code INT8}, {@code INT16} bzw. {@code INT32} in
		 * {@link #dataValue} gespeichert sind.
		 * 
		 * @see IAMEncoder#computeDataType(int)
		 */
		public final int type;

		/**
		 * Dieses Feld speichert die Größe des Speicherbereichs für {@link #dataValue}.
		 */
		public final int bytes;

		/**
		 * Dieses Feld speichert die Längen gegebener Zahlenlisten.
		 */
		public final IAMSize dataSize;

		/**
		 * Dieses Feld speichert die Werte der Zahlenlisten.
		 */
		public final int[] dataValue;

		/**
		 * Dieser Konstruktor analysiert die gegebene Zahlenliste und initialisiert die Felder.
		 * 
		 * @param arrays Zahlenliste.
		 */
		public IAMData(final List<int[]> arrays) {
			this.dataSize = new IAMSize(arrays);
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
			this.type = Math.max(IAMEncoder.computeDataType(minValue), IAMEncoder.computeDataType(maxValue));
			final int dataValueBytes = this.dataValue.length * IAM.byteCount(this.type);
			this.bytes = (dataValueBytes + 3) & -4;
		}

		/**
		 * Diese Methode schreibt die {@link #dataValue} gemäß {@link #type} in den gegebenen Puffer.
		 * 
		 * @param buffer Puffer.
		 */
		public void putData(final ByteBuffer buffer) {
			IAMEncoder.putArray(buffer, this.type, this.dataValue);
		}

	}

	{}

	/**
	 * Diese Klasse implementiert ein Element einer {@link IAMUniqueItemMap} bzw. eines {@link IAMListEncoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static final class IAMItem {

		/**
		 * Dieses Feld speichert den Index, unter dem dieses Objekt in {@link IAMUniqueMap#datas} verwaltet wird.
		 */
		public int index;

		/**
		 * Dieses Feld speichert die Zahlenfolge des Elements.
		 */
		public int[] item;

	}

	/**
	 * Diese Klasse implementiert ein Element einer {@link IAMUniqueEntryMap} bzw. eines {@link IAMMapEncoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static final class IAMEntry {

		/**
		 * Dieses Feld speichert den Index, unter dem dieses Objekt in {@link IAMUniqueMap#datas} verwaltet wird.
		 */
		public int index;

		/**
		 * Dieses Feld speichert die Zahlenfolge des Schlüssels.
		 */
		public int[] key;

		/**
		 * Dieses Feld speichert die Zahlenfolge des Werts.
		 */
		public int[] value;

	}

	{}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link UniqueMap} mit Zahlenlisten als Eingabe.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten (Ausgabe).
	 */
	protected static abstract class IAMUniqueMap<GData> extends UniqueMap<int[], GData> {

		/**
		 * Dieses Feld speichert die gesammelten Nutzdaten.
		 */
		public final List<GData> datas = new ArrayList<GData>();

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean check(final Object input) {
			return input instanceof int[];
		}

		/**
		 * Diese Methode erzeugt einen neuen Nutzdatensatz und gibt diesen zurück.
		 * 
		 * @param index Index, unter dem der Nutzdatensatz in {@link #datas} verwaltet wird.
		 * @param array Zahlenliste.
		 * @return Nutzdatensatz.
		 */
		protected abstract GData create(int index, int[] array);

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GData compile(final int[] array) {
			return this.put(array);
		}

		/**
		 * Diese Methode nimmt einen neuen Nutzdatensatz mit der gegebenen Zahlenliste in die Verwaltung auf und gibt den Index zurück, unter dem diese in
		 * {@link #datas} verwaltet werden.
		 * 
		 * @param array Zahlenliste.
		 * @return Nutzdatensatz.
		 */
		public GData put(final int[] array) {
			final GData data = this.create(this.datas.size(), array.clone());
			this.datas.add(data);
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hash(final int[] array) throws NullPointerException {
			return IAM.hash(array);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final int[] array1, final int[] array2) throws NullPointerException {
			return IAM.equals(array1, array2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final int[] array1, final int[] array2) throws NullPointerException {
			return IAM.compare(array1, array2);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link UniqueMap} zur verwaltung der Elemente eines {@link IAMListEncoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static final class IAMUniqueItemMap extends IAMUniqueMap<IAMItem> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected IAMItem create(final int index, final int[] array) {
			final IAMItem data = new IAMItem();
			data.index = index;
			data.item = array;
			return data;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link UniqueMap} zur verwaltung der Einträge eines {@link IAMMapEncoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static final class IAMUniqueEntryMap extends IAMUniqueMap<IAMEntry> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected IAMEntry create(final int index, final int[] array) {
			final IAMEntry data = new IAMEntry();
			data.index = index;
			data.key = array;
			data.value = array;
			return data;
		}

	}

	{}

	/**
	 * Diese Schnittstelle definiert ein Objekt, das seine Daten gemäß einer gegebenen Bytereihenfolge in eine Zahlenfolge kodieren kann.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface IAMEncoding {

		/**
		 * Diese Methode kompiliert die gesammelten Daten in eine optimierte Datenstruktur und gibt diese zurück.
		 * 
		 * @param order Bytereihenfolge.
		 * @return optimierte Datenstruktur.
		 */
		public byte[] encode(final ByteOrder order);

	}

	/**
	 * Diese Klasse implementiert eine {@link IAMMap} zur Zusammenstellung und Kodierung der Daten für einen {@link IAMMapDecoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMMapEncoder extends IAMBaseMap implements IAMEncoding {

		/**
		 * Dieses Feld speichert den Mods, unter dem über {@link #encode(ByteOrder)} eine Abbildung kodiert wird, deren Einträge über den Streuwert ihrer Schlüssel
		 * gesucht werden.
		 */
		public static final boolean MODE_HASHED = true;

		/**
		 * Dieses Feld speichert den Mods, unter dem über {@link #encode(ByteOrder)} eine Abbildung kodiert wird, deren Einträge binär über die Ordnung ihrer
		 * Schlüssel gesucht werden.
		 */
		public static final boolean MODE_SORTED = false;

		/**
		 * Dieses Feld speichert den Modus.
		 */
		protected boolean mode = IAMMapEncoder.MODE_HASHED;

		/**
		 * Dieses Feld speichert die Einträge.
		 */
		protected final IAMUniqueEntryMap entries = new IAMUniqueEntryMap();

		/**
		 * Diese Methode gibt den Modus für {@link #encode(ByteOrder)} zurück.
		 * 
		 * @see #MODE_HASHED
		 * @see #MODE_SORTED
		 * @return Modus.
		 */
		public boolean mode() {
			return this.mode;
		}

		/**
		 * Diese Methode setzt den Modus.
		 * 
		 * @param mode Modus.
		 */
		public void mode(final boolean mode) {
			this.mode = mode;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMArray key(final int entryIndex) {
			final List<IAMEntry> datas = this.entries.datas;
			if ((entryIndex < 0) || (entryIndex >= datas.size())) return IAMEmptyArray.INSTANCE;
			return new IAMValueArray(datas.get(entryIndex).key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMArray value(final int entryIndex) {
			final List<IAMEntry> datas = this.entries.datas;
			if ((entryIndex < 0) || (entryIndex >= datas.size())) return IAMEmptyArray.INSTANCE;
			return new IAMValueArray(datas.get(entryIndex).value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int entryCount() {
			return this.entries.datas.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int[] key) {
			final IAMEntry result = this.entries.entryMap().get(key);
			return result == null ? -1 : result.index;
		}

		/**
		 * Diese Methode gibt das modifizierbare {@code int}-Array des {@code entryIndex}-ten Eintrags zurück. In diesem Array sollten nur die für den Wert
		 * stehenden {@code int}s verändert werden.
		 * 
		 * @see #entryCount()
		 * @param entryIndex Index des Eintrags.
		 * @return {@code int}-Array des {@code entryIndex}-ten Eintrags.
		 * @throws IndexOutOfBoundsException Wenn eine der Eingaben ungültig ist.
		 */
		public int[] get(final int entryIndex) {
			return this.entries.datas.get(entryIndex).value;
		}

		/**
		 * Diese Methode fügt einen Eintrag mit dem gegebenen Schlüssel sowie dem gegebenen Wert hinzu. Wenn bereits ein Eintrag mit diesem Schlüssel existiert,
		 * wird dessen Wert ersetzt.
		 * 
		 * @param key Schlüssel.
		 * @param value Wert.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public void put(final int[] key, final int[] value) throws NullPointerException {
			if ((key == null) || (value == null)) throw new NullPointerException();
			this.entries.get(key).value = value;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see #put(int[], int[])
		 */
		@Override
		public byte[] encode(final ByteOrder order) {
			final List<IAMEntry> datas = this.entries.datas;
			final int count = datas.size();
			final IAMEntry[] entries = datas.toArray(new IAMEntry[count]);
			int rangeMask;
			int rangeCount;
			int[] rangeData;
			int rangeDataType;
			int rangeDataBytes;
			int rangeBytes;
			if (this.mode == IAMMapEncoder.MODE_HASHED) {
				rangeMask = IAMEncoder.computeRangeMask(count);
				rangeCount = rangeMask + 2;
				rangeData = new int[rangeCount];
				rangeDataType = IAMEncoder.computeSizeType(count);
				rangeDataBytes = rangeCount * IAM.byteCount(rangeDataType);
				rangeBytes = ((rangeDataBytes + 3) & -4) + 4;
				final int[] rangeIndex = new int[count];
				for (int i = 0; i < count; i++) {
					final int index = IAM.hash(entries[i].key) & rangeMask;
					rangeData[index]++;
					rangeIndex[i] = index;
				}
				int offset = 0;
				for (int i = 0; i < rangeCount; i++) {
					final int value = rangeData[i];
					rangeData[i] = offset;
					offset += value;
				}
				Arrays.sort(entries, new Comparator<IAMEntry>() {

					@Override
					public int compare(final IAMEntry o1, final IAMEntry o2) {
						return rangeIndex[o1.index] - rangeIndex[o2.index];
					}

				});
			} else {
				Arrays.sort(entries, new Comparator<IAMEntry>() {

					@Override
					public int compare(final IAMEntry o1, final IAMEntry o2) {
						return IAM.compare(o1.key, o2.key);
					}

				});
				rangeMask = 0;
				rangeData = null;
				rangeDataType = 0;
				rangeDataBytes = 0;
				rangeBytes = 0;
			}
			final IAMData keyData = new IAMData(new AbstractList<int[]>() {

				@Override
				public int[] get(final int index) {
					return entries[index].key;
				}

				@Override
				public int size() {
					return count;
				}

			});
			final IAMSize keySize = keyData.dataSize;
			final IAMData valueData = new IAMData(new AbstractList<int[]>() {

				@Override
				public int[] get(final int index) {
					return entries[index].value;
				}

				@Override
				public int size() {
					return count;
				}

			});
			final IAMSize valueSize = valueData.dataSize;
			final int length = 8 + rangeBytes + keySize.bytes + keyData.bytes + valueSize.bytes + valueData.bytes;
			final byte[] result = new byte[length];
			final ByteBuffer buffer = ByteBuffer.wrap(result).order(order);
			buffer.putInt(0xF00D1000 | (keyData.type << 8) | (keySize.type << 6) | (rangeDataType << 4) | (valueData.type << 2) | (valueSize.type << 0));
			buffer.putInt(count);
			if (rangeDataType != 0) {
				buffer.putInt(rangeMask);
				IAMEncoder.putArray(buffer, rangeDataType, rangeData);
			}
			keySize.putSize(buffer);
			keyData.putData(buffer);
			valueSize.putSize(buffer);
			valueData.putData(buffer);
			return result;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link IAMList} zur Zusammenstellung und Kodierung der Daten für einen {@link IAMListDecoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMListEncoder extends IAMBaseList implements IAMEncoding {

		/**
		 * Dieses Feld speichert die bisher gesammelten Elemente.
		 */
		protected final IAMUniqueItemMap items = new IAMUniqueItemMap();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMArray item(final int itemIndex) {
			final List<IAMItem> datas = this.items.datas;
			if ((itemIndex < 0) || (itemIndex >= datas.size())) return IAMEmptyArray.INSTANCE;
			return new IAMValueArray(datas.get(itemIndex).item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int itemCount() {
			return this.items.datas.size();
		}

		/**
		 * Diese Methode gibt das modifizierbare {@code int}-Array des {@code itemIndex}-te Element zurück. Dieses Array sollte nur dann verändert werden, wenn es
		 * über {@link #put(boolean, int...)} ohne Wiederverwendung hunzugefügt wurde.
		 * 
		 * @see #put(boolean, int...)
		 * @see #itemCount()
		 * @param itemIndex Index des Elements.
		 * @return {@code int}-Array des {@code itemIndex}-ten Elements.
		 * @throws IndexOutOfBoundsException Wenn eine der Eingaben ungültig ist.
		 */
		public int[] get(final int itemIndex) throws IndexOutOfBoundsException {

			return this.items.datas.get(itemIndex).item;
		}

		/**
		 * Diese Methode fügt das gegebene Element hinzu und gibt den Index zurück, unter dem das Element verwaltet wird. Wenn bereits ein Element mit dem gleichen
		 * Daten existiert, wird dessen Index zurück gegeben.
		 * <p>
		 * <u>Achtung:</u> Der Index ist garantiert der gleiche, der im {@link IAMListDecoder} verwerden wird.
		 * 
		 * @see #put(boolean, int...)
		 * @param value Element.
		 * @return Index, unter dem die Zahlenliste in der optimierten Datenstruktur registriert ist.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public int put(final int[] value) throws NullPointerException {
			return this.items.put(value).index;
		}

		/**
		 * Diese Methode fügt das gegebene Element hinzu und gibt den Index zurück, unter dem das Element verwaltet wird. Wenn die Wiederverwendung aktiviert ist
		 * und bereits ein Element mit dem gleichen Daten existiert, wird dessen Index zurück gegeben.
		 * <p>
		 * <u>Achtung:</u> Der Index ist garantiert der gleiche, der im {@link IAMListDecoder} verwerden wird.
		 * 
		 * @param reuse {@code true}, wenn die Wiederverwendung aktiviert ist.
		 * @param value Element.
		 * @return Index, unter dem die Zahlenliste in der optimierten Datenstruktur registriert ist.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public int put(final boolean reuse, final int[] value) throws NullPointerException {
			if (reuse) return this.items.get(value).index;
			return this.items.put(value).index;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see #put(int[])
		 * @see #put(boolean, int[])
		 */
		@Override
		public byte[] encode(final ByteOrder order) {
			final List<IAMItem> datas = this.items.datas;
			final int count = datas.size();
			final IAMData itemData = new IAMData(new AbstractList<int[]>() {

				@Override
				public int[] get(final int index) {
					return datas.get(index).item;
				}

				@Override
				public int size() {
					return count;
				}

			});
			final IAMSize itemSize = itemData.dataSize;
			final int length = 8 + itemSize.bytes + itemData.bytes;
			final byte[] result = new byte[length];
			final ByteBuffer buffer = ByteBuffer.wrap(result).order(order);
			buffer.putInt(0xF00D2000 | (itemData.type << 2) | (itemSize.type << 0));
			buffer.putInt(count);
			itemSize.putSize(buffer);
			itemData.putData(buffer);
			return result;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link IAMIndex} zur Zusammenstellung und Kodierung der Daten für einen {@link IAMIndexDecoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IAMIndexEncoder extends IAMBaseIndex implements IAMEncoding {

		/**
		 * Dieses Feld speichert die {@link IAMMapEncoder}.
		 */
		protected final List<IAMMapEncoder> maps = new ArrayList<IAMMapEncoder>();

		/**
		 * Dieses Feld speichert die {@link IAMListEncoder}.
		 */
		protected final List<IAMListEncoder> lists = new ArrayList<IAMListEncoder>();

		/**
		 * Dieser Konstruktor initialisiert die internen Datenstrukturen zum Sammeln der Einträge von {@link IAMMap}s und Elementen der {@link ListView}s.
		 */
		public IAMIndexEncoder() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMMap map(final int index) {
			if ((index < 0) || (index >= this.mapCount())) return IAMEmptyMap.INSTANCE;
			return this.maps.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int mapCount() {
			return this.maps.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMList list(final int index) {
			if ((index < 0) || (index >= this.listCount())) return IAMEmptyList.INSTANCE;
			return this.lists.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int listCount() {
			return this.lists.size();
		}

		/**
		 * Diese Methode fügt den gegebene {@link IAMMapEncoder} hinzu und gibt den Index zurück, unter dem er verwaltet wird.
		 * 
		 * @param map {@link IAMMapEncoder}.
		 * @return Index des {@link IAMMapEncoder}s.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public int put(final IAMMapEncoder map) throws NullPointerException {
			if (map == null) throw new NullPointerException();
			final int result = this.maps.size();
			this.maps.add(result, map);
			return result;
		}

		/**
		 * Diese Methode fügt den gegebene {@link IAMListEncoder} hinzu und gibt den Index zurück, unter dem er verwaltet wird.
		 * 
		 * @param list {@link IAMListEncoder}.
		 * @return Index des {@link IAMListEncoder}s.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public int put(final IAMListEncoder list) throws NullPointerException {
			if (list == null) throw new NullPointerException();
			final int result = this.lists.size();
			this.lists.add(result, list);
			return result;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see #put(IAMMapEncoder)
		 * @see #put(IAMListEncoder)
		 */
		@Override
		public byte[] encode(final ByteOrder order) {
			final byte[][] maps = IAMEncoder.encodeBytes(this.maps, order);
			final byte[][] lists = IAMEncoder.encodeBytes(this.lists, order);
			int length = 12;
			length += (maps.length + lists.length + 2) << 2;
			length += IAMEncoder.countBytes(maps);
			length += IAMEncoder.countBytes(lists);
			final byte[] result = new byte[length];
			final ByteBuffer buffer = ByteBuffer.wrap(result).order(order);
			buffer.putInt(0xF00DBA5E);
			buffer.putInt(maps.length);
			buffer.putInt(lists.length);
			IAMEncoder.putSize(buffer, maps);
			IAMEncoder.putSize(buffer, lists);
			IAMEncoder.putData(buffer, maps);
			IAMEncoder.putData(buffer, lists);
			return result;
		}

	}

	{}

	/**
	 * Diese Methode füht die Startpositionen der gegebenen Zahlenfolgen an den gegebenen {@link ByteBuffer} an. Die Zahlenfolgen repräsentieren die von
	 * {@link IAMMapEncoder#encode(ByteOrder)} bzw. {@link IAMListEncoder#encode(ByteOrder)} kodierten Datenstrukturen.
	 * 
	 * @param buffer {@link ByteBuffer}.
	 * @param source Zahlenfolgen.
	 */
	protected static void putSize(final ByteBuffer buffer, final byte[][] source) {
		int offset = 0;
		buffer.putInt(0);
		for (final byte[] data: source) {
			offset += data.length >> 2;
			buffer.putInt(offset);
		}
	}

	/**
	 * Diese Methode füht die gegebenen Zahlenfolgen an den gegebenen {@link ByteBuffer} an. Die Zahlenfolgen repräsentieren die von
	 * {@link IAMMapEncoder#encode(ByteOrder)} bzw. {@link IAMListEncoder#encode(ByteOrder)} kodierten Datenstrukturen.
	 * 
	 * @param buffer {@link ByteBuffer}.
	 * @param source Zahlenfolgen.
	 */
	protected static void putData(final ByteBuffer buffer, final byte[][] source) {
		for (final byte[] data: source) {
			buffer.put(data);
		}
	}

	/**
	 * Diese Methode speichert die gegebene Zahlenfolge an den gegebenen {@link ByteBuffer} an. Der geschriebene Speicherbereich wird mit Nullwerten ergänzt, um
	 * eine restlos durch vier teilbare Größe zu erreichen.
	 * 
	 * @see ByteBuffer#put(byte)
	 * @see ByteBuffer#putInt(int)
	 * @see ByteBuffer#putShort(short)
	 * @param buffer {@link ByteBuffer}.
	 * @param type Datentyp ({@code 1=INT8/UINT8}, {@code 2=INT16/UINT16}, {@code 3=INT32}).
	 * @param values Zahlenfolge.
	 */
	protected static void putArray(final ByteBuffer buffer, final int type, final int[] values) {
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

	{}

	/**
	 * Diese Methode gibt die Summe der Anzahl der Bytes in den gegebenen Zahlenfolgen zurück.
	 * 
	 * @param source Zahlenfolgen.
	 * @return Anzahl der Bytes.
	 */
	protected static int countBytes(final byte[][] source) {
		int length = 0;
		for (final byte[] bytes: source) {
			length += bytes.length;
		}
		return length;
	}

	/**
	 * Diese Methode kodiert die gegebenen {@link IAMEncoding} in Zahlenfolgen.
	 * 
	 * @see IAMEncoding#encode(ByteOrder)
	 * @param source Quelldaten.
	 * @param order Bytereihenfolge
	 * @return Zahlenfolgen.
	 */
	protected static byte[][] encodeBytes(final List<? extends IAMEncoding> source, final ByteOrder order) {
		final int count = source.size();
		final byte[][] result = new byte[count][];
		for (int i = 0; i < count; i++) {
			result[i] = source.get(i).encode(order);
		}
		return result;
	}

	{}

	/**
	 * Diese Methode gibt den Datentyp für die gegebene Größe zurück.<br>
	 * Die Datentypen {@code 1}, {@code 2} und {@code 3} stehen für {@code UINT8}, {@code UINT16} und {@code UINT32}.
	 * 
	 * @param value Größe.
	 * @return Datentyp ({@code 1..3}).
	 */
	protected static int computeSizeType(final int value) {
		if (value <= 255) return 1;
		if (value <= 65535) return 2;
		return 3;
	}

	/**
	 * Diese Methode gibt den Datentyp für den gegebenen Wert zurück.<br>
	 * Die Datentypen {@code 1}, {@code 2} und {@code 3} stehen für {@code INT8}, {@code INT16} und {@code INT32}.
	 * 
	 * @param value Wert.
	 * @return Datengrößentyps ({@code 1..3}).
	 */
	protected static int computeDataType(final int value) {
		if ((-128 <= value) && (value <= 127)) return 1;
		if ((-32768 <= value) && (value <= 32767)) return 2;
		return 3;
	}

	/**
	 * Diese Methode gibt die Bitmaske zur Umrechnung von Streuwerten zurück.
	 * 
	 * @param entryCount Anzahl der Einträge der Abbildung.
	 * @return Bitmaske.
	 */
	protected static int computeRangeMask(final int entryCount) {
		if (entryCount <= 0) return 0;
		int result = 2;
		while (result < entryCount) {
			result <<= 1;
		}
		return result - 1;
	}

}
