package bee.creative.ber;

/** Diese Klasse implementiert Methoden zur Verarbeitung einer steuwertbasierten Menge von Referenen ungleich Null, die als {@code int}-Array mit folgender
 * Struktur gegeben ist: <pre>(size, mask, free, (head, next, item)[mask + 1])</pre> Die Datenfelder haben folgende Bedeutung:
 * <ul>
 * <li>{@code size} - Anzahl der verwalteten Elemente.</li>
 * <li>{@code mask} - Bitmaske zur Umrechnung eines Elements in die Position des zugehörigen Listenkopfes. Die Bitmaske ist stets größer als 0, kleiner als eine
 * Milliarde und eins kleiner als eine Potenz von Zwei.</li>
 * <li>{@code free} - 1-basierte Position des nächsten unbenutzten {@code item} oder {@code 0}.</li>
 * <li>{@code head} - 1-basierte Position der ersten Referenz in der einfach verketteten Liste oder {@code 0}.</li>
 * <li>{@code next} - 1-basierte Position des nächsten Referenz in der einfach verketteten Liste oder {@code 0}.</li>
 * <li>{@code item} - Referenz oder {@code 0}.</li>
 * </ul>
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
abstract class REFSET {

	/** Diese Methode liefert eine neue Referenzmenge mit den gegebenen Referenzen {@code ref1} und {@code ref2}. */
	public static int[] from(int ref1, int ref2) {
		var refset = new int[]{0, 1, 1, 0, 2, 0, 0, 0, 0};
		REFSET.putRef(refset, ref1);
		REFSET.putRef(refset, ref2);
		return refset;
	}

	/** Diese Methode liefert die 1-basierte Position der gegebenen Referenz {@code ref} in der gegebenen Referenzmenge {@code refset}. Wenn die Referenz nicht in
	 * der Referenzmenge enthalten ist, wird {@code 0} geliefert. **/
	public static int getIdx(int[] refset, int ref) {
		if (ref == 0) return 0;
		var idx = ref & /* refset.mask */ refset[1];
		var res = /* refset.head_item_next[idx].head */ refset[(idx * 3) + 3];
		while (res != 0) {
			if (ref == /* refset.head_item_next[res-1].item */ refset[(res * 3) + 2]) return res;
			res = /* refset.head_item_next[res-1].next */ refset[(res * 3) + 1];
		}
		return 0;
	}

	/** Diese Methode liefert eine der Referenzen der gegebenen Referenzmenge {@code refset}. Wenn die Referenzmenge leer ist, wird {@code 0} geliefert. */
	public static int getRef(int[] refset) {
		if (/* refset.size */ refset[0] == 0) return 0;
		for (var off = refset.length - 1; (3 < off); off -= 3) {
			var ref = refset[off];
			if (ref != 0) return ref;
		}
		return 0;
	}

	/** Diese Methode liefert die Referenz an der gegebenen {@link #getIdx(int[], int) 1-basierten Position} {@code idx}. */
	static int getRef(int[] refset, int idx) {
		return refset[(idx * 3) + 2];
	}

	/** Diese Methode liefert die 1-basierte Position der gegebenen Referenz {@code ref} in der gegebenen Referenzmenge {@code refset}. Wenn die Referenz nicht in
	 * der Referenzmenge enthalten ist, wird sie eingefügt. Wenn sie {@code 0} ist oder die Kapazität erschöpft ist, wird {@code 0} geliefert. **/
	public static int putRef(int[] refset, int ref) {
		if (ref == 0) return 0;
		var idx = ref & /* refset.mask */ refset[1];
		var res = /* refset.head_item_next[idx].head */ refset[(idx * 3) + 3];
		while (res != 0) {
			if (ref == /* refset.head_item_next[res-1].item */ refset[(res * 3) + 2]) return res;
			res = /* refset.head_item_next[res-1].next */ refset[(res * 3) + 1];
		}
		res = /* refset.free */ refset[2];
		if (res == 0) return 0;
		/* refset.free */ refset[2] = /* refset.head_item_next[res-1].next */ refset[(res * 3) + 1];
		/* refset.head_item_next[res-1].next */ refset[(res * 3) + 1] = /* refset.head_item_next[idx].head */ refset[(idx * 3) + 3];
		/* refset.head_item_next[idx].head */ refset[(idx * 3) + 3] = res;
		/* refset.head_item_next[res-1].item */ refset[(res * 3) + 2] = ref;
		/* refset.size */ refset[0]++;
		return res;
	}

	public static int popRef(int[] refset, int ref) {
		if (ref == 0) return 0;
		if (/* refset.size */ refset[0] == 0) return 0;
		var idx = ref & /* refset.mask */ refset[1];
		var res = /* refset.head_item_next[idx].head */ refset[(idx * 3) + 3];
		if (res == 0) return 0;
		if (ref == /* refset.head_item_next[res-1].item */ refset[(res * 3) + 2]) {
			/* refset.head_item_next[idx].head */ refset[(idx * 3) + 3] = /* refset.head_item_next[res-1].next */ refset[(res * 3) + 1];
			/* refset.head_item_next[res-1].next */ refset[(res * 3) + 1] = /* refset.free */ refset[2];
			/* refset.head_item_next[res-1].item */ refset[(res * 3) + 2] = 0;
			/* refset.free */ refset[2] = res;
			/* refset.size */ refset[0]--;
			return res;
		}
		while (true) {
			var res2 = /* refset.head_item_next[res-1].next */ refset[(res * 3) + 1];
			if (res2 == 0) return 0;
			if (ref == /* refset.head_item_next[res2-1].item */ refset[(res2 * 3) + 2]) {
				/* refset.head_item_next[res-1].head */ refset[(res * 3) + 1] = /* refset.head_item_next[res2-1].next */ refset[(res2 * 3) + 1];
				/* refset.head_item_next[res2-1].next */ refset[(res2 * 3) + 1] = /* refset.free */ refset[2];
				/* refset.head_item_next[res2-1].item */ refset[(res2 * 3) + 2] = 0;
				/* refset.free */ refset[2] = res2;
				/* refset.size */ refset[0]--;
				return res2;
			}
			res = res2;
		}
	}

	/** Diese Methode liefert die Anzahl der Referenzen in der gegebenen Referenzmenge {@code refset}. */
	public static int size(int[] refset) {
		return /* refset.size */ refset[0];
	}

	/** Diese Methode liefert die gegebenen Referenzmenge {@code refset}, wenn darin noch Platz für mindestens eine weitere Referenz ist. Andernfalls liefert sie
	 * eine {@link #copy(int[]) Kopie}. */
	public static int[] grow(int[] refset) {
		return /* refset.size */ refset[0] <= /* refset.mask */ refset[1] ? refset : REFSET.copy(refset);
	}

	/** Diese Methode liefert eine Kopie des gegebenen {@code intset}, in welcher Platz für mindestens ein weiteres Element ist. Wenn die notwendige Größe unter
	 * der halben Kapazität liegt, wird letztere halbiert. Wenn sie nicht ausreicht, wird sie verdoppelt. */
	static int[] copy(int[] refset) {
		var size = /* refset.size */ refset[0];
		var mask = /* refset.mask */ refset[1];
		var mask2 = (size > mask) ? ((mask * 2) + 1) & 536870911 : ((size * 2) < mask) ? (mask / 2) | 1 : mask;
		if (mask == mask2) return refset.clone();
		var refset2 = new int[(mask2 * 3) + 6];
		var free = 1;
		for (var off = refset.length - 1; 3 < off; off -= 3) {
			var ref = refset[off];
			if (ref != 0) {
				var idx = ref & mask2;
				/* refset2.head_item_next[free-1].next */ refset2[(free * 3) + 1] = /* refset2.head_item_next[idx].head */ refset2[(idx * 3) + 3];
				/* refset2.head_item_next[idx].head */ refset2[(idx * 3) + 3] = free;
				/* refset2.head_item_next[free-1].item */ refset2[(free * 3) + 2] = ref;
				free++;
			}
		}
		/* refset2.size*/ refset2[0] = size;
		/* refset2.mask */ refset2[1] = mask2;
		/* refset2.free */ refset2[2] = free;
		while (free <= mask2) {
			/* refset2.head_item_next[free-1].next */ refset2[(free * 3) + 1] = ++free;
		}
		return refset2;
	}

	/** Diese Methode liefert alle Referenzen der gegebenen Referenzmenge {@code refset}. */
	public static int[] toArray(int[] refset) {
		var idx = /* refset.size */ refset[0];
		var refs = new int[idx];
		for (var off = refset.length - 1; (0 < idx) && (3 < off); off -= 3) {
			var ref = refset[off];
			if (ref != 0) {
				refs[--idx] = ref;
			}
		}
		return refs;
	}

}