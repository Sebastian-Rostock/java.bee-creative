package bee.creative.str;

import java.util.Arrays;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuator;
import bee.creative.util.AbstractIterator;

/** Diese Klasse implementiert Methoden zur Verarbeitung einer steuwertbasierten Menge von Referenen ungleich {@code 0} mit durchschnittlich 4 Speicherzugriffen
 * zum {@link #getIdx(int[], int) Finden} einer vorhandenen Referenz. Die Menge ist als {@code int}-Array mit folgender Struktur umgesetzt:
 * <dl>
 * <dt>{@code (size, mask, free, (head, next, item)[mask + 1])}
 * <dd>
 * <dl>
 * <dt>{@code size}</dt>
 * <dd>Anzahl der verwalteten Elemente.</dd>
 * <dt>{@code mask}</dt>
 * <dd>Bitmaske zur Umrechnung eines Elements in die Position des zugehörigen Listenkopfes.<br>
 * Die Bitmaske ist stets größer als 0, kleiner als eine Milliarde und eins kleiner als eine Potenz von Zwei.</dd>
 * <dt>{@code free}</dt>
 * <dd>1-basierte Position des nächsten unbenutzten {@code item} oder {@code 0}.</dd>
 * <dt>{@code head}</dt>
 * <dd>1-basierte Position der ersten Referenz in der einfach verketteten Liste oder {@code 0}.</dd>
 * <dt>{@code next}</dt>
 * <dd>1-basierte Position des nächsten Referenz in der einfach verketteten Liste oder {@code 0}.</dd>
 * <dt>{@code item}</dt>
 * <dd>Referenz oder {@code 0}.</dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class REFSET {

	/** Diese Methode liefert eine neue leere Referenzmenge mit Kapazität 2. */
	public static int[] make() {
		return new int[]{0, 1, 1, 0, 2, 0, 0, 0, 0};
	}

	/** Diese Methode liefert eine neue Referenzmenge mit den gegebenen Referenzen {@code ref1} und {@code ref2}, wenn beide Referenzen ungleich {@code 0}
	 * sind. */
	public static int[] from(int ref1, int ref2) {
		var refset = REFSET.make();
		REFSET.putRef(refset, ref1);
		REFSET.putRef(refset, ref2);
		return refset;
	}

	/** Diese Methode liefert die 1-basierte Position der gegebenen Referenz {@code ref} in der gegebenen Referenzmenge {@code refset}, wenn die Referenz ungleich
	 * {@code 0} ist. Wenn die Referenz nicht in der Referenzmenge enthalten ist, wird {@code 0} geliefert. **/
	public static int getIdx(int[] refset, int ref) {
		var idx = ref & REFSET.getMask(refset);
		var res = /* refset.head_item_next[idx].head */ refset[(idx * 3) + 3];
		while (res != 0) {
			if (ref == /* refset.head_item_next[res-1].item */ refset[(res * 3) + 2]) return res;
			res = /* refset.head_item_next[res-1].next */ refset[(res * 3) + 1];
		}
		return 0;
	}

	/** Diese Methode liefert eine der Referenzen der gegebenen Referenzmenge {@code refset}. Wenn die Referenzmenge leer ist, wird {@code 0} geliefert. */
	public static int getRef(int[] refset) {
		if (REFSET.size(refset) == 0) return 0;
		for (var off = refset.length - 1; 3 < off; off -= 3) {
			var ref = refset[off];
			if (ref != 0) return ref;
		}
		return 0;
	}

	/** Diese Methode liefert die Referenz an der gegebenen {@link #getIdx(int[], int) 1-basierten Position} {@code idx}, wenn die Position ungleich {@code 0}
	 * ist. */
	public static int getRef(int[] refset, int idx) {
		return refset[(idx * 3) + 2];
	}

	/** Diese Methode liefert die 1-basierte Position der gegebenen Referenz {@code ref} in der gegebenen Referenzmenge {@code refset}, wenn die Referenz ungleich
	 * {@code 0} ist. Wenn die Referenz nicht in der Referenzmenge enthalten ist, wird sie eingefügt. Wenn die Kapazität erschöpft ist, wird {@code 0}
	 * geliefert. **/
	public static int putRef(int[] refset, int ref) {
		var idx = ref & REFSET.getMask(refset);
		var res = /* refset.head_item_next[idx].head */ refset[(idx * 3) + 3];
		while (res != 0) {
			if (ref == /* refset.head_item_next[res-1].item */ refset[(res * 3) + 2]) return res;
			res = /* refset.head_item_next[res-1].next */ refset[(res * 3) + 1];
		}
		res = REFSET.getFree(refset);
		if (res == 0) return 0;
		REFSET.setFree(refset, /* refset.head_item_next[res-1].next */ refset[(res * 3) + 1]);
		/* refset.head_item_next[res-1].next */ refset[(res * 3) + 1] = /* refset.head_item_next[idx].head */ refset[(idx * 3) + 3];
		/* refset.head_item_next[idx].head */ refset[(idx * 3) + 3] = res;
		/* refset.head_item_next[res-1].item */ refset[(res * 3) + 2] = ref;
		REFSET.setSize(refset, REFSET.getSize(refset) + 1);
		return res;
	}

	/** Diese Methode entfernt die gegebenen Referenz {@code ref} aus der gegebenen Referenzmenge {@code refset} und liefert die 1-basierte Position der Referenz,
	 * wenn die Referenz ungleich {@code 0} ist. Wenn die Referenz nicht in der Referenzmenge enthalten ist, wird {@code 0} geliefert. **/
	public static int popRef(int[] refset, int ref) {
		if (REFSET.size(refset) == 0) return 0;
		var idx = ref & REFSET.getMask(refset);
		var res = /* refset.head_item_next[idx].head */ refset[(idx * 3) + 3];
		if (res == 0) return 0;
		if (ref == /* refset.head_item_next[res-1].item */ refset[(res * 3) + 2]) {
			/* refset.head_item_next[idx].head */ refset[(idx * 3) + 3] = /* refset.head_item_next[res-1].next */ refset[(res * 3) + 1];
			/* refset.head_item_next[res-1].next */ refset[(res * 3) + 1] = /* refset.free */ refset[2];
			/* refset.head_item_next[res-1].item */ refset[(res * 3) + 2] = 0;
			REFSET.setFree(refset, res);
			REFSET.setSize(refset, REFSET.getSize(refset) - 1);
			return res;
		}
		while (true) {
			var res2 = /* refset.head_item_next[res-1].next */ refset[(res * 3) + 1];
			if (res2 == 0) return 0;
			if (ref == /* refset.head_item_next[res2-1].item */ refset[(res2 * 3) + 2]) {
				/* refset.head_item_next[res-1].head */ refset[(res * 3) + 1] = /* refset.head_item_next[res2-1].next */ refset[(res2 * 3) + 1];
				/* refset.head_item_next[res2-1].next */ refset[(res2 * 3) + 1] = REFSET.getFree(refset);
				/* refset.head_item_next[res2-1].item */ refset[(res2 * 3) + 2] = 0;
				REFSET.setFree(refset, res);
				REFSET.setSize(refset, REFSET.getSize(refset) - 1);
				return res2;
			}
			res = res2;
		}
	}

	/** Diese Methode liefert die Anzahl der Referenzen in der gegebenen Referenzmenge {@code refset}. */
	public static int size(int[] refset) {
		return REFSET.getSize(refset);
	}

	/** Diese Methode liefert die gegebenen Referenzmenge {@code refset}, wenn sie die maximale Kapazität besitzt oder darin noch Platz für mindestens eine
	 * weitere Referenz ist. Andernfalls liefert sie eine Kopie mit doppelter Kapazität. */
	public static int[] grow(int[] refset) {
		return REFSET.getFree(refset) != 0 ? refset : REFSET.tryCopy(refset, ((REFSET.getMask(refset) << 1) & 536870911) | 1);
	}

	/** Diese Methode liefert die gegebenen Referenzmenge {@code refset}, wenn sie die minimale Kapazität besitzt oder darin bei halber Kapazität kein Platz für
	 * eine weitere Referenz ist. Andernfalls liefert sie eine Kopie mit halber Kapazität. */
	public static int[] pack(int[] refset) {
		var mask = REFSET.getMask(refset) >> 1;
		return REFSET.size(refset) > mask ? refset : REFSET.tryCopy(refset, mask | 1);
	}

	/** Diese Methode liefert eine Kopie der gegebenen Referenzmenge {@code refset}. */
	public static int[] copy(int[] refset) {
		var refset2 = refset.clone();
		return refset2;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die gegebenen Referenzmengen {@code refset} die gleichen Referenzen enthalten. */
	public static boolean equals(int[] refset1, int[] refset2) {
		if (refset1 == refset2) return true;
		var size = REFSET.size(refset1);
		if (size != REFSET.size(refset2)) return false;
		if (size == 0) return true;
		for (var off = refset1.length - 1; 3 < off; off -= 3) {
			var ref = refset1[off];
			if (ref != 0) {
				if (REFSET.getIdx(refset2, ref) == 0) return false;
				size--;
			}
		}
		return size == 0;
	}

	/** Diese Methode übergibt alle Referenzen an {@link RUN#run(int) task.run()}. */
	public static void forEach(int[] refset, RUN task) {
		for (var off = refset.length - 1; 3 < off; off -= 3) {
			var ref = refset[off];
			if (ref != 0) {
				task.run(ref);
			}
		}
	}

	public static ITER iterator(int[] refset) {
		return new ITER(refset);
	}

	/** Diese Methode liefert alle Referenzen der gegebenen Referenzmenge {@code refset}. */
	public static int[] toArray(int[] refset) {
		var size = REFSET.size(refset);
		var refs = new int[size];
		for (var off = refset.length - 1; 3 < off; off -= 3) {
			var ref = refset[off];
			if (ref != 0) {
				refs[--size] = ref;
			}
		}
		return refs;
	}

	/** Diese Methode liefert die Textdarstellung der gegebenen Referenzmenge {@code refset}. */
	public static String toString(int[] refset) {
		var refs = REFSET.toArray(refset);
		Arrays.sort(refs);
		return Arrays.toString(refs);
	}

	/** @see Emuator#emu(Object) */
	public static long emu(int[] refset) {
		return EMU.fromArray(Integer.TYPE, refset.length);
	}

	/** Diese Schnittstelle definiert den Empfänger der Referenzen für {@link REFSET#forEach(int[], RUN)}.
	 *
	 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static interface RUN {

		/** Diese Methode verarbeitet die gegebene Referenz {@code ref}. */
		void run(int ref);

	}

	/** Diese Klasse implementiert {@link REFSET#iterator(int[])}.
	 * 
	 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class ITER extends AbstractIterator<Integer> {

		@Override
		public Integer next() {
			return this.nextRef();
		}

		/** Diese Methode liefert die nächsten Referenz oder {@code 0}. */
		public int nextRef() {
			var ref = this.ref;
			for (; (3 < this.index) && ((this.ref = this.refset[this.index]) != 0); this.index -= 3) {}
			return ref;
		}

		/** Diese Methode liefert die 1-basierte Position der nächsten von {@link #nextRef()} gelieferten Referenz oder {@code 0}. */
		public int nextIdx() {
			return this.index / 3;
		}

		@Override
		public boolean hasNext() {
			return 3 < this.index;
		}

		private int ref;

		private int index;

		private int[] refset;

		private ITER(int[] refset) {
			this.index = (this.refset = refset).length - 1;
			this.nextRef();
		}

	}

	static final int[] EMPTY = new int[]{0, 1, 0, 0, 0, 0, 0, 0, 0};

	static int getSize(int[] refset) {
		return /* refset.size */ refset[0];
	}

	static int setSize(int[] refset, int size) {
		return /* refset.size */ refset[0] = size;
	}

	static int getMask(int[] refset) {
		return /* refset.mask */ refset[1];
	}

	static int setMask(int[] refset, int mask) {
		return /* refset.mask */ refset[1] = mask;
	}

	static int getFree(int[] refset) {
		return /* refset.free */ refset[2];
	}

	static int setFree(int[] refset, int free) {
		return /* refset.free */ refset[2] = free;
	}

	static int[] tryCopy(int[] refset, int mask) {
		if (mask == REFSET.getMask(refset)) return refset;
		var free = 1;
		var refset2 = new int[(mask * 3) + 6];
		for (var off = refset.length - 1; 3 < off; off -= 3) {
			var ref = refset[off];
			if (ref != 0) {
				var idx = ref & mask;
				/* refset2.head_item_next[free-1].next */ refset2[(free * 3) + 1] = /* refset2.head_item_next[idx].head */ refset2[(idx * 3) + 3];
				/* refset2.head_item_next[idx].head */ refset2[(idx * 3) + 3] = free;
				/* refset2.head_item_next[free-1].item */ refset2[(free * 3) + 2] = ref;
				free++;
			}
		}
		REFSET.setSize(refset2, REFSET.size(refset));
		REFSET.setMask(refset2, mask);
		REFSET.setFree(refset2, free);
		while (free <= mask) {
			/* refset2.head_item_next[free-1].next */ refset2[(free * 3) + 1] = ++free;
		}
		return refset2;
	}

}