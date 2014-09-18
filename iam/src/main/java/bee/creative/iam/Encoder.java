package bee.creative.iam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import bee.creative.iam.Decoder.ListDecoder;
import bee.creative.iam.Decoder.MapDecoder;
import bee.creative.iam.IAM.AbstractIndexView;
import bee.creative.iam.IAM.AbstractListView;
import bee.creative.iam.IAM.AbstractMapView;
import bee.creative.iam.IAM.AbstractUniqueList;

/**
 * Diese Klasse implementiert einen {@link IndexView}, mit dem die optimierte Datenstruktur eines {@link Decoder}s erzeugt werden kann.
 * 
 * @see Decoder
 * @see IndexView
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Encoder extends AbstractIndexView {

	/**
	 * Diese Klasse implementiert einen {@link MapView}, mit dem die optimierte Datenstruktur eines {@link MapDecoder}s erzeugt werden kann.
	 * 
	 * @see MapDecoder
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class MapEncoder extends AbstractMapView {

		/**
		 * Dieses Feld speichert den Index für {@link #index()}.
		 */
		final int index;

		/**
		 * Dieses Feld speichert die Größe der Schlüssel.
		 */
		final int keySize;

		/**
		 * Dieses Feld speichert die Größe der Werte.
		 */
		final int valueSize;

		/**
		 * Dieses Feld speichert die Größe der Einträge.
		 */
		final int entrySize;

		/**
		 * Dieses Feld speichert die Einträge.
		 */
		final AbstractUniqueList entryList = new AbstractUniqueList() {

			@Override
			public int hash(final int[] input) throws NullPointerException {
				return IAM.hash(input, MapEncoder.this.keySize);
			}

			@Override
			public boolean equals(final int[] input1, final int[] input2) throws NullPointerException {
				return IAM.equals(input1, input2, MapEncoder.this.keySize);
			}

		};

		/**
		 * Dieser Konstruktor initialisiert die internen Datenstrukturen zum Sammeln der Einträge, den Index dieses {@link MapView} sowie die Größen der Schlüssel
		 * und Werte.
		 * 
		 * @see #index()
		 * @see #keySize()
		 * @see #valueSize()
		 * @param index Index (größer oder gleich 0).
		 * @param keySize Größe der Schlüssel (größer oder gleich 1).
		 * @param valueSize Größe der Werte (größer oder gleich 0).
		 * @throws IllegalArgumentException Wenn eine der Eingaben ungültig ist.
		 */
		public MapEncoder(final int index, final int keySize, final int valueSize) throws IllegalArgumentException {
			if((index < 0) || (keySize < 1) || (valueSize < 0)) throw new IllegalArgumentException();
			this.index = index;
			this.entrySize = (this.keySize = keySize) + (this.valueSize = valueSize);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int key(final int entryIndex, final int index) throws IndexOutOfBoundsException {
			if((index < 0) || (index >= this.keySize)) throw new IndexOutOfBoundsException();
			return this.entryList.entries().get(entryIndex)[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int keySize() {
			return this.keySize;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int value(final int entryIndex, final int index) throws IndexOutOfBoundsException {
			if((index < 0) || (index >= this.valueSize)) throw new IndexOutOfBoundsException();
			return this.entryList.entries().get(entryIndex)[index + this.keySize];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int valueSize() {
			return this.valueSize;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int entryCount() {
			return this.entryList.entries().size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int... key) {
			if(key.length != this.keySize) return -1;
			final Integer result = this.entryList.entryMap().get(key);
			return result == null ? -1 : result.intValue();
		}

		/**
		 * Diese Methode fügt den gegebnenen Eintrag hinzu und gibt den Index zurück, unter dem der Eintrag verwaltet wird. Wenn bereits ein Eintrag mit dem
		 * gleichen Schlüssel existiert, wird dessen Index zurück gegeben. Wer Wert des existierenden Eintrags bleibt unverändert.
		 * <p>
		 * <u>Achtung:</u> Der Index ist nicht der gleiche, der in einem {@link MapDecoder} verwerden wird.
		 * 
		 * @param entry Eintrag als Verkettung von Schlüssel und Wert.
		 * @return Index des Eintrags.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Größe des Eintrags ungültig ist.
		 */
		public int put(final int... entry) throws NullPointerException, IllegalArgumentException {
			if(entry.length != this.entrySize) throw new IllegalArgumentException();
			return this.entryList.get(entry).intValue();
		}

		/**
		 * Diese Methode fügt den Eintrag mit dem gegebenen Schlüssel und dem gegebnenen Wert hinzu und gibt den Index zurück, unter dem der Eintrag verwaltet wird.
		 * Wenn bereits ein Eintrag mit diesem Schlüssel existiert, wird dessen Index zurück gegeben. Wer Wert des existierenden Eintrags bleibt unverändert.
		 * <p>
		 * <u>Achtung:</u> Der Index ist nicht der gleiche, der im {@link MapDecoder} verwerden wird.
		 * 
		 * @param key Schlüssel.
		 * @param value Wert.
		 * @return Index des Eintrags.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Größe von Schlüssel bzw. Wert ungültig sind.
		 */
		public int put(final int[] key, final int... value) throws NullPointerException, IllegalArgumentException {
			final int keySize = this.keySize, valueSize = this.valueSize;
			if((key.length != keySize) || (value.length != valueSize)) throw new IllegalArgumentException();
			final int[] entry = new int[keySize + valueSize];
			System.arraycopy(key, 0, entry, 0, keySize);
			System.arraycopy(value, 0, entry, keySize, valueSize);
			return this.entryList.get(entry).intValue();
		}

		/**
		 * Diese Methode gibt den Index dieses {@link MapEncoder}s zurück, unter welchem er im übergeordneten {@link Encoder} verwaltet wird.
		 * 
		 * @return Index dieses {@link MapEncoder}s.
		 */
		public int index() {
			return this.index;
		}

		/**
		 * Diese Methode kompiliert die gesammelten Einträge in eine optimierte Datenstruktur und gibt diese zurück.
		 * 
		 * @see MapDecoder
		 * @return optimierte Datenstruktur.
		 */
		public int[] encode() {
			final int keySize, valueSize, entryCount;
			final List<int[]> entryList;
			{
				keySize = this.keySize;
				valueSize = this.valueSize;
				entryList = this.entryList.entries();
				entryCount = entryList.size();
			}
			final int rangeMask, rangeCount;
			{
				rangeMask = IAM.mask(entryCount);
				rangeCount = rangeMask + 2;
			}
			final int[] result;
			final int rangeOffset, keyOffset, valueOffset;
			{
				result = new int[3 + rangeCount + ((keySize + valueSize) * entryCount)];
				rangeOffset = 3;
				keyOffset = rangeOffset + rangeCount;
				valueOffset = keyOffset + (keySize * entryCount);
			}
			{
				result[0] = keySize;
				result[1] = valueSize;
				result[2] = entryCount;
			}
			final int[] rangeSize, entryIndex;
			int[][] entryArray;
			{
				rangeSize = new int[rangeCount];
				entryIndex = new int[entryCount];
				entryArray = new int[entryCount][];
				int i = 0;
				for(final int[] entry: entryList){
					final int index = IAM.hash(entry, keySize) & rangeMask;
					rangeSize[index]++;
					entryIndex[i] = index;
					entryArray[i] = entry;
					i++;
				}
			}
			{
				for(int i = 0, size = 0; i < rangeCount; i++){
					result[rangeOffset + i] = size;
					size += rangeSize[i];
					rangeSize[i] = 0;
				}
			}
			{
				for(int i = 0; i < entryCount; i++){
					final int index = entryIndex[i];
					final int offset = result[rangeOffset + index] + rangeSize[index];
					final int[] entry = entryArray[i];
					System.arraycopy(entry, 0, result, keyOffset + (keySize * offset), keySize);
					System.arraycopy(entry, keySize, result, valueOffset + (valueSize * offset), valueSize);
					rangeSize[index]++;
				}
			}
			return result;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link ListView}, mit dem die optimierte Datenstruktur eines {@link ListDecoder}s erzeugt werden kann.
	 * 
	 * @see ListDecoder
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ListEncoder extends AbstractListView {

		/**
		 * Dieses Feld speichert den Index für {@link #index()}.
		 */
		final int index;

		/**
		 * Dieses Feld speichert die bisher gesammelten Elemente.
		 */
		final AbstractUniqueList itemList = new AbstractUniqueList() {

			@Override
			public int hash(final int[] input) throws NullPointerException {
				return IAM.hash(input, input.length);
			}

			@Override
			public boolean equals(final int[] input1, final int[] input2) throws NullPointerException {
				return Arrays.equals(input1, input2);
			}

		};

		/**
		 * Dieser Konstruktor initialisiert die internen Datenstrukturen zum Sammeln der Elemente sowie den Index dieses {@link ListView}.
		 * 
		 * @see #index()
		 * @param index Index (größer oder gleich 0).
		 * @throws IllegalArgumentException Wenn die Eingabe ungültig ist.
		 */
		public ListEncoder(final int index) throws IllegalArgumentException {
			if(index < 0) throw new IllegalArgumentException();
			this.index = index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int item(final int itemIndex, final int index) throws IndexOutOfBoundsException {
			return this.itemList.entries().get(itemIndex)[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int itemSize(final int itemIndex) {
			return this.itemList.entries().get(itemIndex).length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int itemCount() {
			return this.itemList.entries().size();
		}

		/**
		 * Diese Methode fügt das gegebene Element hinzu und gibt den Index zurück, unter dem das Element verwaltet wird. Wenn bereits ein Element mit dem gleichen
		 * Daten existiert, wird dessen Index zurück gegeben.
		 * <p>
		 * <u>Achtung:</u> Der Index ist garantiert der gleiche, der im {@link ListDecoder} verwerden wird.
		 * 
		 * @param value Element.
		 * @return Index, unter dem die Zahlenliste in der optimierten Datenstruktur registriert ist.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public int put(final int... value) throws NullPointerException {
			return this.itemList.get(value).intValue();
		}

		/**
		 * Diese Methode gibt den Index dieses {@link ListEncoder}s zurück, unter welchem er im übergeordneten {@link Encoder} verwaltet wird.
		 * 
		 * @return Index dieses {@link ListEncoder}s.
		 */

		public int index() {
			return this.index;
		}

		/**
		 * Diese Methode kompiliert die gesammelten Elemente in eine optimierte Datenstruktur und gibt diese zurück.
		 * 
		 * @see ListDecoder
		 * @return optimierte Datenstruktur.
		 */
		public int[] encode() {
			final List<int[]> itemList;
			final int itemCount;
			{
				itemList = this.itemList.entries();
				itemCount = itemList.size();
			}
			int rangeValue;
			final int[] rangeArray;
			{
				rangeArray = new int[itemCount];
				rangeValue = 0;
				for(int i = 0; i < itemCount; i++){
					rangeValue += itemList.get(i).length;
					rangeArray[i] = rangeValue;
				}
			}
			int offset;
			final int[] result;
			{
				offset = 2 + itemCount;
				result = new int[offset + rangeValue];
				result[0] = itemCount;
				result[1] = 0;
			}
			{
				System.arraycopy(rangeArray, 0, result, 2, itemCount);
			}
			{
				for(int i = 0; i < itemCount; i++){
					final int[] item = itemList.get(i);
					final int length = item.length;
					System.arraycopy(item, 0, result, offset, length);
					offset += length;
				}
			}
			return result;
		}

	}

	/**
	 * Dieses Feld speichert die {@link MapEncoder}.
	 */
	final List<MapEncoder> mapList;

	/**
	 * Dieses Feld speichert die {@link ListEncoder}.
	 */
	final List<ListEncoder> listList;

	/**
	 * Dieser Konstruktor initialisiert die internen Datenstrukturen zum Sammeln der Einträge von {@link MapView}s und Elementen der {@link ListView}s.
	 */
	public Encoder() {
		this.mapList = new ArrayList<MapEncoder>();
		this.listList = new ArrayList<ListEncoder>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MapEncoder map(final int index) throws IndexOutOfBoundsException {
		return this.mapList.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int mapCount() {
		return this.mapList.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListEncoder list(final int index) throws IndexOutOfBoundsException {
		return this.listList.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int listCount() {
		return this.listList.size();
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link MapEncoder} und gibt ihn zurück.
	 * 
	 * @param keySize Größe der Schlüssel (größer oder gleich 1).
	 * @param valueSize Größe der Werte (größer oder gleich 0).
	 * @return neuer {@link MapEncoder}.
	 * @throws IllegalArgumentException Wenn eine der Eingaben ungültig ist.
	 */
	public MapEncoder newMap(final int keySize, final int valueSize) throws IllegalArgumentException {
		final MapEncoder result = new MapEncoder(this.mapList.size(), keySize, valueSize);
		this.mapList.add(result.index, result);
		return result;
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link ListEncoder} und gibt ihn zurück.
	 * 
	 * @return neuer {@link ListEncoder}.
	 */
	public ListEncoder newList() {
		final ListEncoder result = new ListEncoder(this.listList.size());
		this.listList.add(result.index, result);
		return result;
	}

	/**
	 * Diese Methode kompiliert die gesammelten Informationen in eine optimierte Datenstruktur und gibt diese zurück.
	 * 
	 * @see #newMap(int, int)
	 * @see #newList()
	 * @see MapEncoder#encode()
	 * @see ListEncoder#encode()
	 * @return optimierte Datenstruktur.
	 */
	public int[] encode() {
		final List<MapEncoder> mapList;
		final List<ListEncoder> listList;
		final int[][] mapArray, listArray;
		final int mapCount, listCount;
		{
			mapList = this.mapList;
			mapCount = mapList.size();
			mapArray = new int[mapCount][];
			listList = this.listList;
			listCount = listList.size();
			listArray = new int[listCount][];
		}
		int length;
		{
			length = 3;
			for(int i = 0; i < mapCount; i++){
				length += (mapArray[i] = mapList.get(i).encode()).length;
			}
			for(int i = 0; i < listCount; i++){
				length += (listArray[i] = listList.get(i).encode()).length;
			}
		}
		final int[] result;
		{
			result = new int[length];
			result[0] = IAM.MAGIC;
			result[1] = mapCount;
			result[2] = listCount;
		}
		{
			int offset = 3;
			for(final int[] item: mapArray){
				System.arraycopy(item, 0, result, offset, item.length);
				offset += item.length;
			}
			for(final int[] item: listArray){
				System.arraycopy(item, 0, result, offset, item.length);
				offset += item.length;
			}
		}
		return result;
	}

}
