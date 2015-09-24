package bee.creative.iam;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import bee.creative.iam.IAMDecoder.IAMIndexDecoder;
import bee.creative.iam.IAMEncoder.IAMIndexEncoder;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Comparators;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert grundlegende Klassen und Methoden zur Umsetzung des {@code IAM - Integer Array Model}.
 * 
 * @see IAMEncoder
 * @see IAMDecoder
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class IAM {

	/**
	 * Diese Klasse implementiert eine abstrakte {@link IAMList}. Die von {@link #items()} gelieferte {@link List} delegiert an {@link #item(int)} und
	 * {@link #itemCount()}. Die Methoden {@link #item(int, int)} und {@link #itemLength(int)} delegieren an {@link IAMList#item(int)}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class IAMBaseList implements IAMList {

		/**
		 * Dieses Feld speichert die leere {@link IAMBaseList}.
		 */
		public static final IAMBaseList EMPTY = new IAMBaseList() {

			@Override
			public IAMArray item(final int itemIndex) {
				return IAMBaseArray.EMPTY;
			}

			@Override
			public int itemCount() {
				return 0;
			}

		};

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int item(final int itemIndex, final int index) {
			return this.item(index).get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int itemLength(final int itemIndex) {
			return this.item(itemIndex).length();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<IAMArray> items() {
			return new AbstractList<IAMArray>() {

				@Override
				public IAMArray get(final int index) {
					if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
					return IAMBaseList.this.item(index);
				}

				@Override
				public int size() {
					return IAMBaseList.this.itemCount();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toInvokeString("IAMList", this.itemCount());
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link IAMMap}. Die Methode {@link #entry(int)} liefert einen {@link IAMEntry} mit den von {@link #key(int)} und
	 * {@link #value(int)} gelieferten Zahlenfolgen, welcher über {@link IAM#toEntry(IAMArray, IAMArray)} erzeugt wird. Die von {@link #entries()} gelieferte
	 * {@link List} delegiert an {@link #entry(int)} und {@link #entryCount()} .
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class IAMBaseMap implements IAMMap {

		/**
		 * Dieses Feld speichert die leere {@link IAMBaseMap}.
		 */
		public static final IAMBaseMap EMPTY = new IAMBaseMap() {

			@Override
			public IAMArray key(final int entryIndex) {
				return IAMBaseArray.EMPTY;
			}

			@Override
			public IAMArray value(final int entryIndex) {
				return IAMBaseArray.EMPTY;
			}

			@Override
			public IAMEntry entry(final int entryIndex) throws IndexOutOfBoundsException {
				return IAMBaseEntry.EMPTY;
			}

			@Override
			public int entryCount() {
				return 0;
			}

			@Override
			public int find(final int... key) throws NullPointerException {
				if (key == null) throw new NullPointerException();
				return -1;
			}

		};

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int key(final int entryIndex, final int index) {
			return this.key(entryIndex).get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int keyLength(final int entryIndex) {
			return this.key(entryIndex).length();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int value(final int entryIndex, final int index) {
			return this.value(entryIndex).get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int valueLength(final int entryIndex) {
			return this.value(entryIndex).length();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMEntry entry(final int entryIndex) throws IndexOutOfBoundsException {
			if ((entryIndex < 0) || (entryIndex >= this.entryCount())) return IAMBaseEntry.EMPTY;
			return IAM.toEntry(this.key(entryIndex), this.value(entryIndex));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<IAMEntry> entries() {
			return new AbstractList<IAMEntry>() {

				@Override
				public IAMEntry get(final int index) {
					if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
					return IAMBaseMap.this.entry(index);
				}

				@Override
				public int size() {
					return IAMBaseMap.this.entryCount();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toInvokeString("IAMMap", this.entryCount());
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link IAMEntry}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class IAMBaseEntry implements IAMEntry {

		/**
		 * Dieses Feld speichert das leere {@link IAMBaseEntry}.
		 */
		public static final IAMBaseEntry EMPTY = new IAMBaseEntry() {

			@Override
			public IAMArray value() {
				return IAMBaseArray.EMPTY;
			}

			@Override
			public IAMArray key() {
				return IAMBaseArray.EMPTY;
			}

		};

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int key(final int index) {
			return this.key().get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int keyLength() {
			return this.key().length();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int value(final int index) {
			return this.value().get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int valueLength() {
			return this.value().length();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.key(), this.value());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof IAMEntry)) return false;
			final IAMEntry data = (IAMEntry)object;
			return this.key().equals(data.key()) && this.value().equals(data.value());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.key() + "=" + this.value();
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link IAMArray} ohne {@link #get(int)}- und {@link #length()}-Methoden.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class IAMBaseArray implements IAMArray {

		/**
		 * Dieses Feld speichert das leere {@link IAMBaseArray}.
		 */
		public static final IAMBaseArray EMPTY = new IAMBaseArray() {

			@Override
			public int get(final int index) {
				return 0;
			}

			@Override
			public int length() {
				return 0;
			}

		};

		{}

		/**
		 * Diese Methode implementiert die Erzeugung des Abschnitts in {@link #section(int, int)}. die Parameter sind bereits geprüft.
		 * 
		 * @param offset Beginn des Abschnitts.
		 * @param length Länge des Abschnitts.
		 * @return Abschnitt.
		 */
		protected IAMArray newSection(final int offset, final int length) {
			if (length == 0) return IAMBaseArray.EMPTY;
			return new IAMBaseArray() {

				@Override
				public int get(final int index) {
					if ((index < 0) || (index >= length)) return 0;
					return IAMBaseArray.this.get(offset + index);
				}

				@Override
				public int length() {
					return length;
				}

				@Override
				public IAMArray section(final int offset2, final int length2) {
					return IAMBaseArray.this.section(offset + offset2, length2);
				}

			};
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hash() {
			int hash = 0x811C9DC5;
			for (int i = 0, size = this.length(); i < size; i++) {
				hash = (hash * 0x01000193) ^ this.get(i);
			}
			return hash;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final IAMArray value) throws NullPointerException {
			final int length = this.length();
			if (length != value.length()) return false;
			for (int i = 0; i < length; i++)
				if (this.get(i) != value.get(i)) return false;
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final IAMArray value) throws NullPointerException {
			final int length1 = this.length(), length2 = value.length();
			for (int i = 0, length = length1 < length2 ? length1 : length2, result; i < length; i++)
				if ((result = Comparators.compare(this.get(i), value.get(i))) != 0) return result;
			return length1 - length2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IAMArray section(final int offset, final int length) {
			if ((offset < 0) || (length <= 0) || ((offset + length) > this.length())) return this.newSection(0, 0);
			return this.newSection(offset, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int[] toArray() {
			final int length = this.length();
			final int[] result = new int[length];
			for (int i = 0; i < length; i++) {
				result[i] = this.get(i);
			}
			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Integer> iterator() {
			return new Iterator<Integer>() {

				int index = 0;

				@Override
				public boolean hasNext() {
					return this.index < IAMBaseArray.this.length();
				}

				@Override
				public Integer next() {
					return Integer.valueOf(IAMBaseArray.this.get(this.index++));
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.hash();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compareTo(final IAMArray value) {
			return this.compare(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof IAMArray)) return false;
			return this.equals((IAMArray)object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.length() > 50 ? Objects.toString(Iterables.chainedIterable(this.section(0, 45), Iterables.itemIterator("..."))) : Objects.toString(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link IAMIndex}, dessen {@link #maps()}- und {@link #lists()}-Methoden je eine {@link List} liefern, die an
	 * {@link #map(int)} und {@link #mapCount()} bzw. {@link #list(int)} und {@link #listCount()} delegeirt.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class IAMBaseIndex implements IAMIndex {

		/**
		 * Dieses Feld speichert den leeren {@link IAMBaseIndex}.
		 */
		public static final IAMBaseIndex EMPTY = new IAMBaseIndex() {

			@Override
			public IAMMap map(final int index) {
				return IAMBaseMap.EMPTY;
			}

			@Override
			public int mapCount() {
				return 0;
			}

			@Override
			public IAMList list(final int index) {
				return IAMBaseList.EMPTY;
			}

			@Override
			public int listCount() {
				return 0;
			}

		};

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<IAMMap> maps() {
			return new AbstractList<IAMMap>() {

				@Override
				public IAMMap get(final int index) {
					if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
					return IAMBaseIndex.this.map(index);
				}

				@Override
				public int size() {
					return IAMBaseIndex.this.mapCount();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<IAMList> lists() {
			return new AbstractList<IAMList>() {

				@Override
				public IAMList get(final int index) {
					if ((index < 0) || (index >= this.size())) throw new IndexOutOfBoundsException();
					return IAMBaseIndex.this.list(index);
				}

				@Override
				public int size() {
					return IAMBaseIndex.this.listCount();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toInvokeString("IAMIndex", this.maps(), this.lists());
		}

	}

	{}

	/**
	 * Diese Methode gibt ein neues {@link IAMArray} als Sicht auf die gegebenen Zahlen zurück. Änderungen am Inhalt von {@code array} werden auf das gelieferte
	 * {@link IAMArray} übertragen!
	 * 
	 * @param array Zahlen.
	 * @return {@link IAMArray}-Sicht auf {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 */
	public static IAMArray toArray(final byte[] array) {
		if (array.length == 0) return IAMBaseArray.EMPTY;
		return new IAMBaseArray() {

			@Override
			public int get(final int index) {
				if ((index < 0) || (index >= array.length)) return 0;
				return array[index];
			}

			@Override
			public int length() {
				return array.length;
			}

		};
	}

	/**
	 * Diese Methode gibt ein neues {@link IAMArray} als Sicht auf die gegebenen Zahlen zurück. Änderungen am Inhalt von {@code array} werden auf das gelieferte
	 * {@link IAMArray} übertragen!
	 * 
	 * @param array Zahlen.
	 * @return {@link IAMArray}-Sicht auf {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 */
	public static IAMArray toArray(final short[] array) {
		if (array.length == 0) return IAMBaseArray.EMPTY;
		return new IAMBaseArray() {

			@Override
			public int get(final int index) {
				if ((index < 0) || (index >= array.length)) return 0;
				return array[index];
			}

			@Override
			public int length() {
				return array.length;
			}

		};
	}

	/**
	 * Diese Methode gibt ein neues {@link IAMArray} als Sicht auf die gegebenen Zahlen zurück. Änderungen am Inhalt von {@code array} werden auf das gelieferte
	 * {@link IAMArray} übertragen!
	 * 
	 * @param array Zahlen.
	 * @return {@link IAMArray}-Sicht auf {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 */
	public static IAMArray toArray(final int... array) {
		if (array.length == 0) return IAMBaseArray.EMPTY;
		return new IAMBaseArray() {

			@Override
			public int get(final int index) {
				if ((index < 0) || (index >= array.length)) return 0;
				return array[index];
			}

			@Override
			public int length() {
				return array.length;
			}

		};
	}

	/**
	 * Diese Methode ein neues {@link IAMEntry} als Sicht auf den gegebenen Schlüssel sowie dem gegebenen Wert zurück.
	 * 
	 * @param key Schlüssel.
	 * @param value Wert.
	 * @return {@link IAMEntry}-Sicht auf {@code key} und {@code value}.
	 * @throws NullPointerException Wenn {@code key} bzw. {@code value} {@code null} ist.
	 */
	public static IAMEntry toEntry(final IAMArray key, final IAMArray value) throws NullPointerException {
		if ((key.length() == 0) && (value.length() == 0)) return IAMBaseEntry.EMPTY;
		return new IAMBaseEntry() {

			@Override
			public IAMArray key() {
				return key;
			}

			@Override
			public IAMArray value() {
				return value;
			}

		};
	}

	{}

	/**
	 * Diese Methode gibt den Streuwert der gegebenen Zahlenfolge zurück.
	 * 
	 * @see IAMArray#hash()
	 * @param array Zahlenfolge.
	 * @return Streuwert.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 */
	public static int hash(final int[] array) throws NullPointerException {
		int hash = 0x811C9DC5;
		for (int i = 0, size = array.length; i < size; i++) {
			hash = (hash * 0x01000193) ^ array[i];
		}
		return hash;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn die gegebenen Zahlenfolgen gleich sind.
	 * 
	 * @see IAMArray#equals(IAMArray)
	 * @param array1 erste Zahlenfolge.
	 * @param array2 zweite Zahlenfolge.
	 * @return {@code true}, wenn die Zahlenfolgen gleich sind.
	 * @throws NullPointerException Wenn {@code array1} bzw. {@code array2} {@code null} ist.
	 */
	public static boolean equals(final int[] array1, final int[] array2) throws NullPointerException {
		final int length1 = array1.length, length2 = array2.length;
		if (length1 != length2) return false;
		for (int i = 0; i < length1; i++)
			if (array1[i] != array2[i]) return false;
		return true;
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn die Ordnung der ersten Zahlenfolge lexikografisch kleiner, gleich bzw.
	 * größer als die der zweiten Zahlenfolge ist.
	 * 
	 * @see IAMArray#compare(IAMArray)
	 * @param array1 erste Zahlenfolge.
	 * @param array2 zweite Zahlenfolge.
	 * @return Vergleichswert der Ordnungen.
	 * @throws NullPointerException Wenn {@code array1} bzw. {@code array2} {@code null} ist.
	 */
	public static int compare(final int[] array1, final int[] array2) throws NullPointerException {
		final int length1 = array1.length, length2 = array2.length;
		for (int i = 0, length = length1 < length2 ? length1 : length2, result; i < length; i++)
			if ((result = Comparators.compare(array1[i], array2[i])) != 0) return result;
		return length1 - length2;
	}

	{}

	/**
	 * Diese Methode gibt die kleinste Länge eines {@code INT32} Arrays zurück, in dessen Speicherbereich ein {@code INT8} Array mit der gegebenen Länge passen.
	 * 
	 * @param byteCount Länge eines {@code INT8} Arrays.
	 * @return Länge des {@code INT32} Arrays.
	 */
	public static int byteAlign(final int byteCount) {
		return (byteCount + 3) >> 2;
	}

	/**
	 * Diese Methode gibt die Byteanzahl des gegebenen Datengrößentyps zurück.
	 * 
	 * @param dataType Datengrößentyps ({@code 1}, {@code 2} oder {@code 3}).
	 * @return Byteanzahl ({@code 1}, {@code 2} oder {@code 4}).
	 */
	public static int byteCount(final int dataType) {
		return (1 << dataType) >> 1;
	}

	{}

	/**
	 * Diese Methode gibt einen neuen {@link IAMIndexEncoder} zurück.
	 * 
	 * @see IAMIndexEncoder#IAMIndexEncoder()
	 * @return neuer {@link IAMIndexEncoder}.
	 */
	public static IAMIndexEncoder encoder() {
		return new IAMIndexEncoder();
	}

	/**
	 * Diese Methode gibt einen neuen {@link IAMIndexDecoder} zurück.
	 * 
	 * @param array Speicherbereich mit {@code INT32} Zahlen.
	 * @return neuer {@link IAMIndexDecoder}.
	 * @throws IAMException Wenn beim dekodieren des Speicherbereichs ein Fehler erkannt wird.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 */
	public static IAMIndexDecoder decoder(final MMFArray array) throws IAMException, NullPointerException {
		return new IAMIndexDecoder(array);
	}

}