package bee.creative.iam;

import bee.creative.iam.IAM.AbstractIndexView;
import bee.creative.iam.IAM.AbstractListView;
import bee.creative.iam.IAM.AbstractMapView;

/**
 * Diese Klasse implementiert einen {@link IndexView}, der auf einer durch einen {@link Encoder} optimierten Datenstruktur arbeitet und diese in Form von
 * {@link ArrayView}s bereitgestellt bekommt.
 * 
 * @see Decoder
 * @see IndexView
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Decoder extends AbstractIndexView {

	/**
	 * Diese Klasse implementiert eine Streuwert-Abbildung von Schlüssel Werte, bei welcher jeder Schlüssel und jeder Wert selbst eine Liste von Zahlen
	 * (Integer-Array) ist.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class MapDecoder extends AbstractMapView {

		/**
		 * Dieses Feld speichert die Anzahl der Zahlen in einem Schlüssel. Ist größer oder gleich 1.
		 */
		int keySize;

		/**
		 * Dieses Feld speichert die Anzahl der Zahlen in einem Wert. Ist größer oder gleich 0.
		 */
		int valueSize;

		/**
		 * Dieses Feld speichert die Anzahl der Einträge in der Abbildung, d.h. der Schlüssel-Wert-Paare. Ist größer oder gleich 0.
		 */
		int entryCount;

		/**
		 * Dieses Feld speichert die Bitmaske zur Umrechnung des Streuwerts eines gesuchten Schlüssels in den Index des einzigen Schlüsselbereichs, in dem dieser
		 * Schlüssel enthalten sein kann.
		 */
		int rangeMask;

		/**
		 * Dieses Feld speichert die Startpositionen der Schlüsselbereiche im Speicherbereich keyArray. Die Schlüssel des i-ten Schlüsselbereichs befinden sich dort
		 * an den Positionen von inklusive rangeArray[i] bis exklusive rangeArray[i+1]. Der Wert rangeArray[0] ist 0.
		 */
		ArrayView rangeArray;

		/**
		 * Dieses Feld speichert den Speicherbereich mit den Zahlen der Schlüssel.
		 */
		ArrayView keyArray;

		/**
		 * Dieses Feld speichert den Speicherbereich mit den Zahlen der Werte.
		 */
		ArrayView valueArray;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayView key(final int entryIndex) throws IndexOutOfBoundsException {
			final int keySize = this.keySize;
			return this.keyArray.section(entryIndex * keySize, keySize);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int key(final int entryIndex, final int index) throws IndexOutOfBoundsException {
			final int keySize = this.keySize;
			if((index < 0) || (index >= keySize)) throw new IndexOutOfBoundsException();
			return this.keyArray.get((entryIndex * keySize) + index);
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
		public ArrayView value(final int entryIndex) throws IndexOutOfBoundsException {
			final int valueSize = this.valueSize;
			return this.valueArray.section(entryIndex * valueSize, valueSize);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int value(final int entryIndex, final int index) throws IndexOutOfBoundsException {
			final int valueSize = this.valueSize;
			if((index < 0) || (index >= valueSize)) throw new IndexOutOfBoundsException();
			return this.valueArray.get((entryIndex * valueSize) + index);
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
			return this.entryCount;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int key) {
			if(this.keySize != 1) return -1;
			final int index = IAM.hash(key) & this.rangeMask;
			final ArrayView rangeArray = this.rangeArray, keyArray = this.keyArray;
			for(int i = rangeArray.get(index), size = rangeArray.get(index + 1); i < size; i++){
				final int pos = (i * 1);
				if(key == keyArray.get(pos)) return i;
			}
			return -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int key1, final int key2) {
			if(this.keySize != 2) return -1;
			final int index = IAM.hash(key1, key2) & this.rangeMask;
			final ArrayView rangeArray = this.rangeArray, keyArray = this.keyArray;
			for(int i = rangeArray.get(index), size = rangeArray.get(index + 1); i < size; i++){
				final int pos = (i * 2);
				if((key1 == keyArray.get(pos)) && (key2 == keyArray.get(pos + 1))) return i;
			}
			return -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int key1, final int key2, final int key3) {
			if(this.keySize != 3) return -1;
			final int index = IAM.hash(key1, key2, key3) & this.rangeMask;
			final ArrayView rangeArray = this.rangeArray, keyArray = this.keyArray;
			for(int i = rangeArray.get(index), size = rangeArray.get(index + 1); i < size; i++){
				final int pos = (i * 3);
				if((key1 == keyArray.get(pos)) && (key2 == keyArray.get(pos + 1)) && (key3 == keyArray.get(pos + 2))) return i;
			}
			return -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int key1, final int key2, final int key3, final int key4) {
			if(this.keySize != 4) return -1;
			final int index = IAM.hash(key1, key2, key3, key4) & this.rangeMask;
			final ArrayView rangeArray = this.rangeArray, keyArray = this.keyArray;
			for(int i = rangeArray.get(index), size = rangeArray.get(index + 1); i < size; i++){
				final int pos = (i * 4);
				if((key1 == keyArray.get(pos)) && (key2 == keyArray.get(pos + 1)) && (key3 == keyArray.get(pos + 2)) && (key4 == keyArray.get(pos + 3))) return i;
			}
			return -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int key1, final int key2, final int key3, final int key4, final int key5) {
			if(this.keySize != 5) return -1;
			final int index = IAM.hash(key1, key2, key3, key4, key5) & this.rangeMask;
			final ArrayView rangeArray = this.rangeArray, keyArray = this.keyArray;
			for(int i = rangeArray.get(index), size = rangeArray.get(index + 1); i < size; i++){
				final int pos = (i * 5);
				if((key1 == keyArray.get(pos)) && (key2 == keyArray.get(pos + 1)) && (key3 == keyArray.get(pos + 2)) && (key4 == keyArray.get(pos + 3))
					&& (key5 == keyArray.get(pos + 4))) return i;
			}
			return -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int... key) {
			final int length = key.length;
			if(this.keySize != length) return -1;
			// TODO ggf switch auf length und an 1-5 argument variante delegieren
			final int index = IAM.hash(key, length) & this.rangeMask;
			final ArrayView rangeArray = this.rangeArray, keyArray = this.keyArray;
			LOOP: for(int i = rangeArray.get(index), size = rangeArray.get(index + 1); i < size; i++){
				final int pos = (i * length);
				for(int j = 0; j < length; j++){
					if(keyArray.get(pos + j) != key[j]){
						continue LOOP;
					}
				}
				return i;
			}
			return -1;
		}

		/**
		 * Diese Methode lädt das Inhaltsverzeichnis aus dem gegebenen Array und gibt den Abschnitt des Arrays nach den ermittelten Nutzdaten zurück.
		 * 
		 * @param array Array mit den optimierten Datenstrukturen dieser Abbildung.
		 * @return Abschnitt des Arrays nach den Nutzdaten dieser Abbildung.
		 * @throws IllegalArgumentException Wenn das Inhaltsverzeichnis ungültig ist.
		 */
		public ArrayView decode(final ArrayView array) throws IllegalArgumentException {
			final int keySize = array.get(0), valueSize = array.get(1), entryCount = array.get(2), rangeMask = IAM.mask(entryCount);
			if((keySize < 1) || (valueSize < 0) || (entryCount < 0)) throw new IllegalArgumentException();
			final int o1 = 3, o2 = o1 + rangeMask + 2, o3 = o2 + (keySize * entryCount), o4 = o3 + (valueSize * entryCount);
			this.keySize = keySize;
			this.valueSize = valueSize;
			this.entryCount = entryCount;
			this.rangeMask = rangeMask;
			this.rangeArray = array.section(o1, o2 - o1);
			this.keyArray = array.section(o2, o3 - o2);
			this.valueArray = array.section(o3, o4 - o3);
			return array.section(o4);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link ListView}, der seine Elemente als optimierte Datenstruktur in einer {@link ArrayView} hinterlegt und von dirt
	 * wahlfrei nachgeladen werden.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ListDecoder extends AbstractListView {

		/**
		 * Dieses Feld speichert die Anzahl der Elemente.
		 */
		int itemCount;

		/**
		 * Dieses Feld speichert die Startpositionen der Elemente im Speicherbereich {@link #valueArray}. Die Zahlen des i-ten Elements befinden sich dort an den
		 * Positionen von inklusive rangeArray[i] bis exklusive rangeArray[i+1]. Der Wert rangeArray[0] ist 0.
		 */
		ArrayView rangeArray;

		/**
		 * Dieses Feld speichert den Speicherbereich mit den Zahlen der Elemente. Die erste Zahl jedes Elements kann als Strukturkennung zur Interpretation der
		 * restlichen Zahlen des Elements dienen.
		 */
		ArrayView valueArray;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayView item(final int itemIndex) throws IndexOutOfBoundsException {
			final ArrayView rangeArray = this.rangeArray;
			final int offset = rangeArray.get(itemIndex), length = rangeArray.get(itemIndex + 1) - offset;
			return this.valueArray.section(offset, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int item(final int itemIndex, final int index) throws IndexOutOfBoundsException {
			final ArrayView rangeArray = this.rangeArray;
			final int position = rangeArray.get(itemIndex) + index;
			if(position >= rangeArray.get(itemIndex + 1)) throw new IndexOutOfBoundsException();
			return this.valueArray.get(position);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int itemSize(final int itemIndex) throws IndexOutOfBoundsException {
			final ArrayView rangeArray = this.rangeArray;
			return rangeArray.get(itemIndex + 1) - rangeArray.get(itemIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int itemCount() {
			return this.itemCount;
		}

		/**
		 * Diese Methode lädt das Inhaltsverzeichnis aus dem gegebenen Array und gibt den Abschnitt des Arrays nach den ermittelten Nutzdaten zurück.
		 * 
		 * @param array Array mit den optimierten Datenstrukturen dieser Liste.
		 * @return Abschnitt des Arrays nach den Nutzdaten dieser Liste.
		 * @throws IllegalArgumentException Wenn das Inhaltsverzeichnis ungültig ist.
		 */
		public ArrayView decode(final ArrayView array) throws IllegalArgumentException {
			final int itemCount = array.get(0);
			if(itemCount < 0) throw new IllegalArgumentException();
			final int o1 = 1, o2 = itemCount + 1 + o1, o3 = array.get(o2 - 1) + o2;
			this.itemCount = itemCount;
			this.rangeArray = array.section(o1, o2 - o1);
			this.valueArray = array.section(o2, o3 - o2);
			return array.section(o3);
		}

	}

	/**
	 * Dieses Feld speichert die Abbildungen.
	 */
	MapDecoder[] mapArray = {};

	/**
	 * Dieses Feld speichert die Listen.
	 */
	ListDecoder[] listArray = {};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MapDecoder map(final int index) throws IndexOutOfBoundsException {
		return this.mapArray[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int mapCount() {
		return this.mapArray.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListDecoder list(final int index) throws IndexOutOfBoundsException {
		return this.listArray[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int listCount() {
		return this.listArray.length;
	}

	/**
	 * Diese Methode lädt das Inhaltsverzeichnis aus dem gegebenen Array und gibt den Abschnitt des Arrays nach den ermittelten Nutzdaten zurück.
	 * 
	 * @see ArrayView#section(int)
	 * @see MapDecoder#decode(ArrayView)
	 * @see ListDecoder#decode(ArrayView)
	 * @param array Array mit den optimierten Datenstrukturen.
	 * @return Abschnitt des Arrays nach den Nutzdaten.
	 * @throws IllegalArgumentException Wenn das Inhaltsverzeichnis ungültig ist.
	 */
	public ArrayView decode(ArrayView array) throws IllegalArgumentException {
		final int magic = array.get(0);
		if(magic != IAM.MAGIC) throw new IllegalArgumentException();
		final int mapCount = array.get(1);
		final MapDecoder[] mapArray = new MapDecoder[mapCount];
		final int listCount = array.get(2);
		final ListDecoder[] listArray = new ListDecoder[listCount];
		array = array.section(3);
		for(int i = 0; i < mapCount; i++){
			array = (mapArray[i] = new MapDecoder()).decode(array);
		}
		for(int i = 0; i < listCount; i++){
			array = (listArray[i] = new ListDecoder()).decode(array);
		}
		this.mapArray = mapArray;
		this.listArray = listArray;
		return array;
	}

}
