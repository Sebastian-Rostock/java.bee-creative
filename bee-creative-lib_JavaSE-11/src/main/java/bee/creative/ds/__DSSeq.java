package bee.creative.ds;

import java.util.Arrays;
import bee.creative.array.IntegerArraySection;

/** Diese Klasse implementiert eine Referenzsequenz.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class __DSSeq {

	/** Dieses Feld speichert die Nutzdaten dieses Objekts als {@code int[]} der Struktur {@code (size[1], last[1], next[size])}. */
	final __DSSeqPool pool;

	/** Dieses Feld speichert den Index für den Zugriff auf den {@link #pool}. */
	int index;

	__DSSeq(__DSSeqPool pool, int index) {
		this.pool = pool;
		this.index = index;
	}

	void dispose() {
		synchronized (this.pool) {
			this.pool.delete(this.index);
			this.index = 0;
		}
	}

	int pop() {
		synchronized (this.pool) {
			if (this.index == 0) throw new IllegalStateException("disposed");
			var item = this.pool.update(this.index);
			var size = item[0];
			if (size == 0) return ++item[1];
			// dealloc testen
			item[0] = size - 1;
			var next = item[size + 1];
			item[size + 1] = 0;
			return next;
		}
	}

	/** Diese Methode fügt die gegebene Referenz an, sofern sie nicht {@code 0} ist. */
	void put(int ref) {
		synchronized (this.pool) {
			if (this.index == 0) throw new IllegalStateException("disposed");
			if (ref == 0) return;
			var item = this.pool.update(ref);
			var size = item[0];
			if ((size + 2) > item.length) {
				this.pool.update(ref, item = Arrays.copyOf(item, (size / 2) + size + 3));
			}
			item[0] = size + 1;
			item[size + 2] = ref;
		}
	}

	/** Diese Methode liefert die Anzahl der hinterlegten Referenzen. */
	int size() {
		synchronized (this.pool) {
			if (this.index == 0) throw new IllegalStateException("disposed");
			return this.pool.select(this.index)[0];
		}
	}

	void reset() {
		synchronized (this.pool) {
			if (this.index == 0) throw new IllegalStateException("disposed");
			if (this.pool.select(this.index)[0] == 0) return;
			this.pool.update(this.index, new int[2]);
		}
	}

	int[] toArray() {
		synchronized (this.pool) {
			if (this.index == 0) throw new IllegalStateException("disposed");
			var array = this.pool.select(this.index);
			return Arrays.copyOfRange(array, 2, array[0] + 2);
		}
	}

	@Override
	public String toString() {
		synchronized (this.pool) {
			if (this.index == 0) return "SEQ-disposed";
			var array = this.pool.select(this.index);
			return "SEQ-" + this.index + "(last: " + array[1] + ", size: " + array[0] + ", items: " + IntegerArraySection.from(array, 2, array[0] + 2) + ")";
		}
	}

}
