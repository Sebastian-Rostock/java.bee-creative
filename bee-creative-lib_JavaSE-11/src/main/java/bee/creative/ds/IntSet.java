//package bee.creative.ds;
//
//import java.util.Arrays;
//import bee.creative.iam.IAMMapping;
//import bee.creative.util.AbstractHashData;
//import bee.creative.util.HashSetI;
//import bee.creative.util.Builders.ItemSetBuilder;
//
//// doman utils
///** Diese Klasse implementiert ein int-hash-set auf einem int-Array.
// * <p>
// * Die Struktur des Array ist (size, mask, pool, free, head[mask+1], (item, next)[pool]). Die mask ist (2^n)-1, die length = pool*2+mask+5.
// * 
// * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
//class IntSet {
//
//	static final int[] empty = new int[6];
//
//	private static final int MAX_CAPACITY = (2147483647 - 8 - 536870911 - 5) / 2;
//
//	public static int[] create(int capacity) {
//
//	}
//
//	/** Diese Methode liefert die Anzahl der Einträge des gegebenen {@link IntSet}. */
//	public static int size(int[] thiz) {
//		return getSize(thiz);
//	}
//
//	/** Diese Methode liefert die Anzahl der aktuell verwaltbaren Einträge des gegebenen {@link IntSet}. */
//	public static int capacity(int[] thiz) {
//		return getPool(thiz);
//	}
//
//	public static boolean contains(int[] thiz, int item) {
//		return getIndex_(thiz, item) != 0;
//	}
//
//	/**
//	 * Diese Methode fügt das gegebene Element in das gegebene {@link IntSet} ein 
//	 * und liefert das veränderte {@link IntSet}.
//	 * Wenn das Element bereits enthalten ist, wird {@code null} geliefert.
//	 */
//	public static	int[] insert(int[] thiz, int item) {
//			if(contains(thiz, item))return null;
//			var size = getSize(thiz);
//			thiz = grow(thiz, size + 1);
//			
//			
//			var mask = getMask(thiz);
//
//			var hash = mask&item;
//			var free = getFree(thiz);
//			var x = free !=0 ? free : 
//			var next = getNext(thiz, mask+size+5);
//			
//			var index = getHead(thiz, hash);
//			
//			setNext(thiz, index, index);
//			
//			var headIndex = index + 3;
//			var itemIndex = (thiz[2] * 2) + 3;
//			thiz[index]
//	
//		}
//	
//	public static	void insertImpl(int[] thiz, int item) {
//		var size = getSize(thiz);
//		var mask = getMask(thiz);
//		var free = getFree(thiz);
//		var hash = mask & item;
//		var x = free !=0 ? free : 
//		var next = getNext(thiz, mask+size+5);
//		
//		var index = getHead(thiz, hash);
//		
//		setNext(thiz, index, index);
//		
//		var headIndex = index + 3;
//		var itemIndex = (thiz[2] * 2) + 3;
//		thiz[index]
//
//	}
//
//	private static int[] grow(int[] thiz, int size) {
//		final int capacity = getPool(thiz);
//		if (size > MAX_CAPACITY) throw new OutOfMemoryError();
//		if (size <= capacity) return thiz;
//		final int allocate = size + (size >> 1);
//		return allocate(thiz, allocate > MAX_CAPACITY ? MAX_CAPACITY : allocate);
//	}
//
//	// items wird verändert
//	int[] insertAll(int[] that, int[] items, int count) {
//		var insertCount = 0;
//
//		for (var i = 0; i < count; i++) {
//			if (!this.contains(that, items[i])) {
//				items[insertCount++] = items[i];
//			}
//		}
//		if (insertCount == 0) return that;
//		that = this.grow(IntSet.size(that) + insertCount);
//
//	}
//
//	private static int getSize(int[] thiz) {
//		return thiz[0];
//	}
//
//	private static void setSize(int[] thiz, int size) {
//		thiz[0] = size;
//	}
//
//	private static int getMask(int[] thiz) {
//		return thiz[1];
//	}
//
//	private static void setMask(int[] thiz, int mask) {
//		thiz[1] = mask;
//	}
//
//	private static int getPool(int[] thiz) {
//		return thiz[2];
//	}
//
//	private static void setPool(int[] thiz, int pool) {
//		thiz[2] = pool;
//	}
//
//	private static int getFree(int[] thiz) {
//		return thiz[3];
//	}
//
//	private static void setFree(int[] thiz, int free) {
//		thiz[3] = free;
//	}
//
//	private static int getHead(int[] thiz, int hash) {
//		return thiz[hash + 4];
//	}
//
//	private static int getItem(int[] thiz, int index) {
//		return thiz[index];
//	}
//
//	private static int getNext(int[] thiz, int index) {
//		return thiz[index + 1];
//	}
//
//	private static void setNext(int[] thiz, int index, int next) {
//		thiz[index + 1] = next;
//	}
//
//	// leeren
//	public static void clear(int[] thiz) {
//		if (IntSet.size(thiz) == 0) return;
//		IntSet.setupTableImpl(thiz);
//		IntSet.setupNextsImpl(thiz);
//		setSize(thiz, 0);
//		setFree(thiz, getMask(thiz) + 4);
//	}
//
//	private static void setupNextsImpl(int[] that) {
//		for (int i = 0, size = array.length; i < size; array[i] = ++i) {}
//	}
//
//	private static void setupTableImpl(int[] that) {
//		Arrays.fill(that, 3, that[1] + 4, -1);
//	}
//
//	// null wenn schon enthalten / that oder new wenn geändert
//	protected final int putIndexImpl(final GKey key) {
//		final int keyHash = this.customHash(key), result = this.getIndexImpl2(key, keyHash);
//		if (result >= 0) return result;
//		final int count = this.count + 1, capacity = this.capacityImpl();
//		if (count > AbstractHashData.MAX_CAPACITY) throw new OutOfMemoryError();
//		this.count = count;
//		if (count <= capacity) return this.putIndexImpl2(key, keyHash);
//		final var allocate = count + (count >> 1);
//		this.allocateImpl((allocate < 0) || (allocate > AbstractHashData.MAX_CAPACITY) ? AbstractHashData.MAX_CAPACITY : allocate);
//		return this.putIndexImpl2(key, keyHash);
//	}
//
//	private int putIndexImpl2(final GKey key, final int keyHash) {
//		final int[] table = this.table, nexts = this.nexts;
//		final int index = keyHash & (table.length - 1), result = IntSet.empty;
//		IntSet.empty = nexts[result];
//		nexts[result] = table[index];
//		table[index] = result;
//		this.customSetKey(result, key, keyHash);
//		return result;
//	}
//
//	private int getIndexImpl2(final Object key, final int keyHash) {
//		final int[] table = this.table, nexts = this.nexts;
//		final int index = keyHash & (table.length - 1), entry = table[index];
//		for (var result = entry; 0 <= result; result = nexts[result]) {
//			if (this.customEqualsKey(result, key, keyHash)) return result;
//		}
//		return -1;
//	}
//
//	private int[] grow(int i) {
//	}
//
//	int[] delete(int[] that, int item) {
//
//	}
//
//	// items wird verändert
//	int[] deleteAll(int[] that, int[] items, int count) {
//
//	}
//
//	/** Diese Methode liefer die Position des gegebenen Elements item gibt das zurück.
//	 * 
//	 * @param thiz
//	 * @param item
//	 * @return */
//	private static int getIndex_(int[] thiz, int item) {
//		var index = getHead(thiz, getMask(thiz) & item);
//		while (index > 0) {
//			if (getItem(thiz, index) == item) return index;
//			index = getNext(thiz, index);
//		}
//		return 0;
//	}
//
//	public static int[] allocate(int[] thiz, int capacity) {
//		if (capacity < IntSet.size(thiz)) throw new IllegalArgumentException();
//		if (IntSet.capacity(thiz) == capacity) return thiz;
//		if (capacity == 0) return IntSet.empty;
//
//		if (capacity <= IntSet.MAX_CAPACITY) {
//			final var newMask = IAMMapping.mask(capacity);
//			var res = IntSet.create(capacity);
//			final int[] oldTable = this.table, newTable = new int[newMask + 1];
//			final var newNexts = new int[capacity];
//			AbstractHashData.setupTableImpl(newTable);
//			AbstractHashData.setupNextsImpl(newNexts);
//			var newEntryIndex = 0;
//			for (int i = 0, size = oldTable.length; i < size; i++) {
//				for (var oldEntryIndex = oldTable[i]; 0 <= oldEntryIndex; oldEntryIndex = oldNexts[oldEntryIndex]) {
//					final int hash = this.customHashKey(oldEntryIndex);
//					final var index = hash & newMask;
//					newNexts[newEntryIndex] = newTable[index];
//					newTable[index] = newEntryIndex;
//					allocator.copy(oldEntryIndex, newEntryIndex);
//					newEntryIndex++;
//				}
//			}
//			this.empty = newEntryIndex;
//			this.table = newTable;
//			this.nexts = newNexts;
//		} else throw new OutOfMemoryError();
//	}
//
//	int[] compact(int[] that) {
//
//	}
//
//	void copy(int[] source, int[] target) {
//
//	}
//
//	int[] clone(int[] source) {
//
//	}
//
//}