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
		 * @see #keySize()
		 * @see #valueSize()
		 * @param keySize Größe der Schlüssel (größer oder gleich 1).
		 * @param valueSize Größe der Werte (größer oder gleich 0).
		 * @throws IllegalArgumentException Wenn eine der Eingaben ungültig ist.
		 */
		public MapEncoder(final int keySize, final int valueSize) throws IllegalArgumentException {
			if((keySize < 1) || (valueSize < 0)) throw new IllegalArgumentException();
			this.entrySize = (this.keySize = keySize) + (this.valueSize = valueSize);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int key(final int entryIndex, final int index) throws IndexOutOfBoundsException {
			if((index < 0) || (index >= this.keySize)) throw new IndexOutOfBoundsException();
			return this.entryList.entries.get(entryIndex)[index];
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
			return this.entryList.entries.get(entryIndex)[index + this.keySize];
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
			return this.entryList.entries.size();
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
		 * Diese Methode gibt das modifizierbare {@code int}-Array des {@code entryIndex}-ten Eintrags zurück. In diesem Array sollten nur die für den Wert
		 * stehenden {@code int}s verändert werden.
		 * 
		 * @see #entryCount()
		 * @param entryIndex Index des Eintrags.
		 * @return {@code int}-Array des {@code entryIndex}-ten Eintrags.
		 * @throws IndexOutOfBoundsException Wenn eine der Eingaben ungültig ist.
		 */
		public int[] get(final int entryIndex) {
			return this.entryList.entries.get(entryIndex);
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
			return this.entryList.put(true, entry);
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
				entryList = this.entryList.entries;
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.entryList.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof MapEncoder)) return false;
			final MapEncoder data = (MapEncoder)object;
			return this.entryList.equals(data.entryList);
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
		 */
		public ListEncoder() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int item(final int itemIndex, final int index) throws IndexOutOfBoundsException {
			return this.itemList.entries.get(itemIndex)[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int itemSize(final int itemIndex) {
			return this.itemList.entries.get(itemIndex).length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int itemCount() {
			return this.itemList.entries.size();
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
			return this.itemList.entries.get(itemIndex);
		}

		/**
		 * Diese Methode fügt das gegebene Element hinzu und gibt den Index zurück, unter dem das Element verwaltet wird. Wenn bereits ein Element mit dem gleichen
		 * Daten existiert, wird dessen Index zurück gegeben.
		 * <p>
		 * <u>Achtung:</u> Der Index ist garantiert der gleiche, der im {@link ListDecoder} verwerden wird.
		 * 
		 * @see #put(boolean, int...)
		 * @param value Element.
		 * @return Index, unter dem die Zahlenliste in der optimierten Datenstruktur registriert ist.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public int put(final int... value) throws NullPointerException {
			return this.itemList.put(true, value);
		}

		/**
		 * Diese Methode fügt das gegebene Element hinzu und gibt den Index zurück, unter dem das Element verwaltet wird. Wenn die Wiederverwendung aktiviert ist
		 * und bereits ein Element mit dem gleichen Daten existiert, wird dessen Index zurück gegeben.
		 * <p>
		 * <u>Achtung:</u> Der Index ist garantiert der gleiche, der im {@link ListDecoder} verwerden wird.
		 * 
		 * @see AbstractUniqueList#put(boolean, int...)
		 * @param reuse {@code true}, wenn die Wiederverwendung aktiviert ist.
		 * @param value Element.
		 * @return Index, unter dem die Zahlenliste in der optimierten Datenstruktur registriert ist.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public int put(final boolean reuse, final int... value) throws NullPointerException {
			return this.itemList.put(reuse, value);
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
				itemList = this.itemList.entries;
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.itemList.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ListEncoder)) return false;
			final ListEncoder data = (ListEncoder)object;
			return this.itemList.equals(data.itemList);
		}

	}

	/**
	 * Dieses Feld speichert die {@link MapEncoder}.
	 */
	final List<MapEncoder> mapList = new ArrayList<MapEncoder>();

	/**
	 * Dieses Feld speichert die {@link ListEncoder}.
	 */
	final List<ListEncoder> listList = new ArrayList<ListEncoder>();

	/**
	 * Dieser Konstruktor initialisiert die internen Datenstrukturen zum Sammeln der Einträge von {@link MapView}s und Elementen der {@link ListView}s.
	 */
	public Encoder() {
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
	 * Diese Methode fügt den gegebene {@link MapEncoder} hinzu und gibt den Index zurück, unter dem er verwaltet wird.
	 * 
	 * @param map {@link MapEncoder}.
	 * @return Index des {@link MapEncoder}s.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public int put(final MapEncoder map) throws NullPointerException {
		if(map == null) throw new NullPointerException();
		final int result = this.mapList.size();
		this.mapList.add(result, map);
		return result;
	}

	/**
	 * Diese Methode fügt den gegebene {@link ListEncoder} hinzu und gibt den Index zurück, unter dem er verwaltet wird.
	 * 
	 * @param list {@link ListEncoder}.
	 * @return Index des {@link ListEncoder}s.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public int put(final ListEncoder list) throws NullPointerException {
		if(list == null) throw new NullPointerException();
		final int result = this.listList.size();
		this.listList.add(result, list);
		return result;
	}

	/**
	 * Diese Methode kompiliert die gesammelten Informationen in eine optimierte Datenstruktur und gibt diese zurück.
	 * 
	 * @see #put(MapEncoder)
	 * @see #put(ListEncoder)
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
