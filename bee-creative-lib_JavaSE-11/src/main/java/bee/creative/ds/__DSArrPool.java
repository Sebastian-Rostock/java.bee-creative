package bee.creative.ds;

import java.util.Arrays;

class __DSArrPool {

	final __DS owner;

	/** Dieses Feld speichert die Indizes der in {@link #delete(int)} gelöschten Arrays zur Wiederverwendung. */
	final __DSSeq indices;

	/** Dieses Feld speichert die Indizes der in {@link #insert(int[])}, {@link #update(int)}, {@link #update(int, int[])} oder {@link #delete(int)}
	 * veränderten Arrays. */
	final __DSSeq changes; // TODO

	int[][] prevArrays;

	int[][] nextArrays;

	int count;

	__DSArrPool(__DS owner) {
		this.owner = owner;
		this.prevArrays = new int[10][];
		this.nextArrays = new int[10][];
		if (this instanceof __DSSeqPool) {
			var that = (__DSSeqPool)this;
			this.count = 2;
			this.nextArrays[1] = new int[]{0, 2};
			this.nextArrays[2] = new int[2];
			this.indices = new __DSSeq(that, 1);
			this.changes = new __DSSeq(that, 2);
		} else {
			this.indices = owner.seqPool.createSeq();
			this.changes = owner.seqPool.createSeq();
		}
	}

	int[] select(int index) {
		if (index == 0) return null;
		var array = this.nextArrays[index];
		if (array != null) return array;
		return this.prevArrays[index];
	}

	// liefert ref
	int insert(int[] array) {
		if (array == null) return 0;
		var index = this.indices.pop(); // wiederverwenden oder erzeugen
		if (index >= this.nextArrays.length) {
			var len = (index / 2) + index + 1;
			this.prevArrays = Arrays.copyOf(this.prevArrays, len);
			this.nextArrays = Arrays.copyOf(this.nextArrays, len);
		}
		this.nextArrays[index] = array;
		this.count++;
		return index;
	}

	int[] update(int index) {
		if (index == 0) return null;
		var array = this.nextArrays[index];
		if (array != null) return array;
		return this.nextArrays[index] = this.prevArrays[index].clone();
	}

	void update(int index, int[] array) {
		if ((index == 0) || (array == null)) return;
		this.nextArrays[index] = array;
	}

	//
	void delete(int index) {
		if ((index == 0) || (this.nextArrays[index] == null)) return;
		this.nextArrays[index] = null;
		this.indices.put(index); // zur wiederverwendung speichern
		// dealloc?
		this.count--;

	}

}
