package bee.creative.iam;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import bee.creative.util.Objects;
import bee.creative.util.Unique.UniqueMap;

/**
 * Diese Klasse implementiert Klassen und Metoden zur Bereitstellung und Verarbeitung von Informationen eines {@code IAM} ({@code Integer Array Model}). Die
 * grundlegende Schnittstelle zum Zugriff auf ein {@code IAM} wird im {@link IndexView} definiert.
 * 
 * @see IndexView
 * @see MapView
 * @see ListView
 * @see ArrayView
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class IAM {

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Iterator}, der beginnend bei Index {@code 0} über eine gegebene Anzahl von Elementen iteriert. Die
	 * {@link #next()}-Methode soll das Element zu dem von {@link #nextIndex()} gelieferten Index liefern.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class AbstractIterator<GItem> implements Iterator<GItem> {

		/**
		 * Dieses Feld speichert das Index des nächsten Elements.
		 */
		int index = 0;

		/**
		 * Dieses Feld speichert die Anzahl der Elemente.
		 */
		int count;

		/**
		 * Dieser Konstruktor initialisiert dei Anzahl der Elemente.
		 * 
		 * @param count Anzahl der Elemente.
		 */
		public AbstractIterator(final int count) {
			this.count = count;
		}

		/**
		 * Diese Methode gibt den Index für das nächste Element in {@link #next()} zurück und erhöht diesen für den nächsten Aufruf.
		 * 
		 * @return Index für das nächste Element.
		 */
		public int nextIndex() {
			return this.index++;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.index < this.count;
		}

	};

	/**
	 * Diese Klasse implementiert einen abstrakten {@link ListView}, dessen {@link #item(int)}-Methode einen {@link ArrayView} liefert, der an
	 * {@link #item(int, int)} sowie {@link #itemSize(int)} delegeirt.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class AbstractListView implements ListView {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayView item(final int itemIndex) throws IndexOutOfBoundsException {
			if((itemIndex < 0) || (itemIndex >= this.itemCount())) throw new IndexOutOfBoundsException();
			return new AbstractArrayView() {

				@Override
				public int get(final int index) throws IndexOutOfBoundsException {
					return AbstractListView.this.item(itemIndex, index);
				}

				@Override
				public int length() {
					return AbstractListView.this.itemSize(itemIndex);
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<ArrayView> iterator() {
			return new AbstractIterator<ArrayView>(this.itemCount()) {

				@Override
				public ArrayView next() {
					return AbstractListView.this.item(this.nextIndex());
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link MapView}, dessen {@link #key(int)}- und {@link #value(int)}-Methoden je einen {@link ArrayView} liefern,
	 * der an {@link #key(int, int)} sowie {@link #keySize()} bzw. {@link #value(int, int)} sowie {@link #valueSize()} delegeirt. Der von #en gelieferte
	 * {@link EntryView} delegiert dazu an alle zuvor genannten Methoden. Die {@code find}-Methoden mit {@code 1} bis {@code 5} {@code int}-Parametern delegieren
	 * an die mit dem {@code int[]}-Parameter.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class AbstractMapView implements MapView {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayView key(final int entryIndex) throws IndexOutOfBoundsException {
			if((entryIndex < 0) || (entryIndex >= this.entryCount())) throw new IndexOutOfBoundsException();
			return new AbstractArrayView() {

				@Override
				public int get(final int index) throws IndexOutOfBoundsException {
					return AbstractMapView.this.key(entryIndex, index);
				}

				@Override
				public int length() {
					return AbstractMapView.this.keySize();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayView value(final int entryIndex) throws IndexOutOfBoundsException {
			if((entryIndex < 0) || (entryIndex >= this.entryCount())) throw new IndexOutOfBoundsException();
			return new AbstractArrayView() {

				@Override
				public int get(final int index) throws IndexOutOfBoundsException {
					return AbstractMapView.this.value(entryIndex, index);
				}

				@Override
				public int length() {
					return AbstractMapView.this.valueSize();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EntryView entry(final int entryIndex) throws IndexOutOfBoundsException {
			if((entryIndex < 0) || (entryIndex >= this.entryCount())) throw new IndexOutOfBoundsException();
			return new AbstractEntryView() {

				@Override
				public ArrayView key() {
					return AbstractMapView.this.key(entryIndex);
				}

				@Override
				public int key(final int index) throws IndexOutOfBoundsException {
					return AbstractMapView.this.key(entryIndex, index);
				}

				@Override
				public int keySize() {
					return AbstractMapView.this.keySize();
				}

				@Override
				public ArrayView value() {
					return AbstractMapView.this.value(entryIndex);
				}

				@Override
				public int value(final int index) throws IndexOutOfBoundsException {
					return AbstractMapView.this.value(entryIndex, index);
				}

				@Override
				public int valueSize() {
					return AbstractMapView.this.valueSize();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int key) {
			return this.find(new int[]{key});
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int key1, final int key2) {
			return this.find(new int[]{key1, key2});
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int key1, final int key2, final int key3) {
			return this.find(new int[]{key1, key2, key3});
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int key1, final int key2, final int key3, final int key4) {
			return this.find(new int[]{key1, key2, key3, key4});
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int find(final int key1, final int key2, final int key3, final int key4, final int key5) {
			return this.find(new int[]{key1, key2, key3, key4, key5});
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<EntryView> iterator() {
			return new AbstractIterator<EntryView>(this.entryCount()) {

				@Override
				public EntryView next() {
					return AbstractMapView.this.entry(this.nextIndex());
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link EntryView}, dessen {@link #hashCode()}-, {@link #equals(Object)}- und {@link #toString()}-Methoden an
	 * {@link #key()} und {@link #value()} delegieren.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class AbstractEntryView implements EntryView {

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
			if(object == this) return true;
			if(!(object instanceof EntryView)) return false;
			final EntryView data = (EntryView)object;
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
	 * Diese Klasse implementiert einen abstrakten {@link ArrayView}, dessen {@link #iterator()}-, {@link #hashCode()}-, {@link #equals(Object)}- und
	 * {@link #toArray()}-Methoden an {@link #get(int)} und {@link #length()} delegieren. Die Methoden {@link #section(int)} und {@link #section(int, int)}
	 * erzeugen je einen {@link ArrayView}.
	 * 
	 * @see IAM#array(ArrayView, int, int)
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class AbstractArrayView implements ArrayView {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayView section(final int offset) throws IndexOutOfBoundsException {
			return IAM.array(this, offset, this.length() - offset);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ArrayView section(final int offset, final int length) throws IndexOutOfBoundsException {
			return IAM.array(this, offset, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Integer> iterator() {
			return new AbstractIterator<Integer>(this.length()) {

				@Override
				public Integer next() {
					return AbstractArrayView.this.get(this.nextIndex());
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			int hash = 0x811C9DC5;
			for(int i = 0, size = this.length(); i < size; i++){
				hash = (hash * 0x01000193) ^ this.get(i);
			}
			return hash;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ArrayView)) return false;
			final ArrayView data = (ArrayView)object;
			final int length = this.length();
			if(length != data.length()) return false;
			for(int i = 0; i < length; i++)
				if(this.get(i) != data.get(i)) return false;
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int[] toArray() {
			final int size = this.length();
			final int[] array = new int[size];
			for(int i = 0; i < size; i++){
				array[i] = this.get(i);
			}
			return array;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toString(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link IndexView}, dessen {@link #maps()}- und {@link #lists()}-Methoden je eine {@link List} liefern, die an
	 * {@link #map(int)} sowie {@link #mapCount()} bzw. {@link #list(int)} sowie {@link #listCount()} delegeirt.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class AbstractIndexView implements IndexView {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<MapView> maps() {
			return new AbstractList<MapView>() {

				@Override
				public MapView get(final int index) {
					return AbstractIndexView.this.map(index);
				}

				@Override
				public int size() {
					return AbstractIndexView.this.mapCount();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<ListView> lists() {
			return new AbstractList<ListView>() {

				@Override
				public ListView get(final int index) {
					return AbstractIndexView.this.list(index);
				}

				@Override
				public int size() {
					return AbstractIndexView.this.listCount();
				}
			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.maps(), this.lists());
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link UniqueMap} zur Nummerierung (einzigartiger) {@code int}-Arrays.
	 * 
	 * @see #put(boolean,int...)
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class AbstractUniqueList extends UniqueMap<int[], Integer> {

		/**
		 * Dieses Feld speichert die bisher gesammelten Elemente in der Reihenfolge ihrer Erfassung.
		 */
		final List<int[]> entries = new ArrayList<int[]>();

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean check(final Object input) {
			return input instanceof int[];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Integer compile(final int[] input) {
			return Integer.valueOf(this.put(false, input));
		}

		/**
		 * Diese Methode gibt den Index zurück, unter dem in {@link #entries()} ein zum gegebenen Array äquivalentes Array verwaltet wird.<br>
		 * Sollte die Wiederverwendung deaktiviert oder in {@link #entries()} kein wiederverwendbares Array enthalten sein, wird das gegebene Array an
		 * {@link #entries()} angefügt.
		 * 
		 * @param reuse {@code true}, wenn die Wiederverwendung aktiviert und das gegebene Array wiederverwendbar sind.
		 * @param array Array.
		 * @return Index, unter dem ein äquivalentes Array verwaltet wird.
		 * @throws NullPointerException Wenn das Array {@code null} ist.
		 */
		public int put(final boolean reuse, final int... array) throws NullPointerException {
			if(reuse) return this.get(array).intValue();
			final List<int[]> entryList = this.entries;
			final int result = entryList.size();
			entryList.add(result, array.clone());
			return result;
		}

		/**
		 * Diese Methode gibt die bisher gesammelten Elemente in der Reihenfolge ihrer Erfassung zurück.
		 * 
		 * @see Collections#unmodifiableList(List)
		 * @return bisher gesammelte Elemente.
		 */
		public List<int[]> entries() {
			return Collections.unmodifiableList(this.entries);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			int hash = 0x811C9DC5;
			for(final int[] item: this.entries){
				hash = (hash * 0x01000193) ^ Arrays.hashCode(item);
			}
			return hash;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof AbstractUniqueList)) return false;
			final AbstractUniqueList data = (AbstractUniqueList)object;
			final List<int[]> list1 = this.entries, list2 = data.entries;
			final int size = list1.size();
			if(size != list2.size()) return false;
			for(int i = 0; i < size; i++){
				if(!Arrays.equals(list1.get(i), list2.get(i))) return false;
			}
			return true;
		}

	};

	/**
	 * Dieses Feld speichert die Kennzeichnung von Datenstruktur und Bytereihenfolge. <br>
	 * {@value #MAGIC} = GOODFOOD.
	 */
	public static final int MAGIC = 0x600DF00D;

	/**
	 * Diese Methode gibt einen {@link ArrayView} zurück, dessen Daten durch einen zu einer Datei erzeugten {@link MappedByteBuffer} bereitgestellt werden.
	 * 
	 * @see RandomAccessFile#getChannel()
	 * @see FileChannel#map(MapMode, long, long)
	 * @see ByteOrder#nativeOrder()
	 * @see ByteBuffer#order(ByteOrder)
	 * @see ByteBuffer#asIntBuffer()
	 * @see #array(IntBuffer)
	 * @param file Datei.
	 * @return {@link ArrayView}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 * @throws IOException Wenn ein I/O-Fehler eintritt.
	 */
	public static ArrayView array(final File file) throws NullPointerException, IOException {
		return IAM.array(new RandomAccessFile(file, "r").getChannel().map(MapMode.READ_ONLY, 0, file.length()).order(ByteOrder.nativeOrder()).asIntBuffer());
	}

	/**
	 * Diese Methode gibt einen {@link ArrayView} zurück, dessen Daten durch einen primitives {@code int}-Array bereitgestellt werden.
	 * 
	 * @see IntBuffer#wrap(int[])
	 * @see #array(IntBuffer)
	 * @param array {@code int}-Array.
	 * @return {@link ArrayView}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static ArrayView array(final int... array) throws NullPointerException {
		return IAM.array(IntBuffer.wrap(array));
	}

	/**
	 * Diese Methode gibt einen {@link ArrayView} zurück, dessen Daten durch einen {@link IntBuffer} bereitgestellt werden.
	 * 
	 * @param buffer {@link IntBuffer}.
	 * @return {@link ArrayView}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static ArrayView array(final IntBuffer buffer) throws NullPointerException {
		if(buffer == null) throw new NullPointerException();
		return new AbstractArrayView() {

			@Override
			public int get(final int index) throws IndexOutOfBoundsException {
				return buffer.get(index);
			}

			@Override
			public int length() {
				return buffer.limit();
			}

			@Override
			public ArrayView section(final int offset) throws IndexOutOfBoundsException {
				final IntBuffer buffer2 = buffer;
				buffer2.position(offset);
				return IAM.array(buffer2.slice());
			}

			@Override
			public ArrayView section(final int offset, final int length) throws IndexOutOfBoundsException {
				IntBuffer buffer2 = buffer;
				buffer2.position(offset);
				buffer2 = buffer2.slice();
				if(length > buffer2.limit()) throw new IndexOutOfBoundsException();
				buffer2.limit(length);
				return IAM.array(buffer2);
			}

		};
	}

	/**
	 * Diese Methode gibt einen Abschnitt eines {@link ArrayView}s zurück.
	 * 
	 * @see AbstractArrayView#section(int)
	 * @see AbstractArrayView#section(int, int)
	 * @param array {@link ArrayView}, dessen Abschnitt erzeugt werden soll.
	 * @param offset Position, bei der der Abschnitt beginnt.
	 * @param length Länge des Abschnitts.
	 * @return Abschnitt.
	 * @throws NullPointerException Wenn der gegebene {@link ArrayView} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die Position bzw. Länge ungültig ist.
	 */
	public static ArrayView array(final ArrayView array, final int offset, final int length) throws NullPointerException, IndexOutOfBoundsException {
		if((offset < 0) || (length < 0) || ((offset + length) > array.length())) throw new IndexOutOfBoundsException();
		return new AbstractArrayView() {

			@Override
			public int get(final int index) throws IndexOutOfBoundsException {
				if((index < 0) || (index >= length)) throw new IndexOutOfBoundsException();
				return array.get(offset + index);
			}

			@Override
			public int length() {
				return length;
			}

			@Override
			public ArrayView section(final int offset2) throws IndexOutOfBoundsException {
				return array.section(offset + offset2, length - offset2);
			}

			@Override
			public ArrayView section(final int offset2, final int length2) throws IndexOutOfBoundsException {
				if((offset2 + length2) > length) throw new IndexOutOfBoundsException();
				return array.section(offset + offset2, length2);
			}

		};
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link Encoder} und gibt diesen zurück.
	 * 
	 * @return neuer {@link Encoder}.
	 */
	public static Encoder encoder() {
		return new Encoder();
	}

	/**
	 * Diese Methode erzeugt einen neuen {@link Decoder} und gibt diesen zurück.
	 * 
	 * @return neuer {@link Decoder}.
	 */
	public static Decoder decoder() {
		return new Decoder();
	}

	/**
	 * Diese Methode gibt den Streuwert des gegebenen Schlüssels zurück.
	 * 
	 * @param key Schlüssel.
	 * @return Streuwert.
	 */
	public static int hash(final int key) {
		return ((0x811C9DC5 * 0x01000193) ^ key);
	}

	/**
	 * Diese Methode gibt den Streuwert des gegebenen Schlüssels zurück.
	 * 
	 * @param key1 erster Wert des Schlüssels.
	 * @param key2 zweiter Wert des Schlüssels.
	 * @return Streuwert.
	 */
	public static int hash(final int key1, final int key2) {
		return ((((0x811C9DC5 * 0x01000193) ^ key1) * 0x01000193) ^ key2);
	}

	/**
	 * Diese Methode gibt den Streuwert des gegebenen Schlüssels zurück.
	 * 
	 * @param key1 erster Wert des Schlüssels.
	 * @param key2 zweiter Wert des Schlüssels.
	 * @param key3 dritter Wert des Schlüssels.
	 * @return Streuwert.
	 */
	public static int hash(final int key1, final int key2, final int key3) {
		return ((((((0x811C9DC5 * 0x01000193) ^ key1) * 0x01000193) ^ key2) * 0x01000193) ^ key3);
	}

	/**
	 * Diese Methode gibt den Streuwert des gegebenen Schlüssels zurück.
	 * 
	 * @param key1 erster Wert des Schlüssels.
	 * @param key2 zweiter Wert des Schlüssels.
	 * @param key3 dritter Wert des Schlüssels.
	 * @param key4 vierter Wert des Schlüssels.
	 * @return Streuwert.
	 */
	public static int hash(final int key1, final int key2, final int key3, final int key4) {
		return ((((((((0x811C9DC5 * 0x01000193) ^ key1) * 0x01000193) ^ key2) * 0x01000193) ^ key3) * 0x01000193) ^ key4);
	}

	/**
	 * Diese Methode gibt den Streuwert des gegebenen Schlüssels zurück.
	 * 
	 * @param key1 erster Wert des Schlüssels.
	 * @param key2 zweiter Wert des Schlüssels.
	 * @param key3 dritter Wert des Schlüssels.
	 * @param key4 vierter Wert des Schlüssels.
	 * @param key5 fünfter Wert des Schlüssels.
	 * @return Streuwert.
	 */
	public static int hash(final int key1, final int key2, final int key3, final int key4, final int key5) {
		return ((((((((((0x811C9DC5 * 0x01000193) ^ key1) * 0x01000193) ^ key2) * 0x01000193) ^ key3) * 0x01000193) ^ key4) * 0x01000193) ^ key5);
	}

	/**
	 * Diese Methode gibt den Streuwert des gegebenen Eintrags zurück.
	 * 
	 * @param array Array, dass mit dem Schlüssel beginnt.
	 * @param length Länge des Schlüssels.
	 * @return Streuwert.
	 */
	public static int hash(final int[] array, final int length) {
		int hash = 0x811C9DC5;
		for(int i = 0, size = length; i < size; i++){
			hash = (hash * 0x01000193) ^ array[i];
		}
		return hash;
	}

	/**
	 * Diese Methode gibt die Bitmaske zur Umrechnung von Streuwerten zurück.
	 * 
	 * @param entryCount Anzahl der Einträge der Abbildung.
	 * @return Bitmaske.
	 */
	public static int mask(final int entryCount) {
		if(entryCount <= 0) return 0;
		int result = 1;
		while(result < entryCount){
			result <<= 1;
		}
		return result - 1;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn die ersten {@code length} Werte der gegebenen Arrays gleich sind.
	 * 
	 * @param array1 erstes Array.
	 * @param array2 zweites Array.
	 * @param length Anzahl der zu vergleichenden Werte.
	 * @return {@code true}, wenn Arrays gleich beginnen.
	 */
	public static boolean equals(final int[] array1, final int[] array2, final int length) {
		for(int i = 0, size = length; i < size; i++){
			if(array1[i] != array2[i]) return false;
		}
		return true;
	}

}
