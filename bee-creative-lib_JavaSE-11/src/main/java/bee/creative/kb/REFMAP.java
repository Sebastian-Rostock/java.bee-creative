package bee.creative.kb;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuator;
import bee.creative.lang.Objects;
import bee.creative.util.AbstractIterator;
import bee.creative.util.Entries;
import bee.creative.util.Iterator2;

/** Diese Klasse implementiert Methoden zur Verarbeitung einer steuwertbasierten Abbildung von Referenen ungleich {@code 0} auf Elemente ungleich {@code null}.
 * Die Methoden verzichten weitgehend auf die Prüfung der Wertebereicht ihrer Argumente für maximale Effizienz. Die Abbildung ist als {@code Object}-Array mit
 * folgender Struktur umgesetzt:
 * <dl>
 * <dt>{@code (keys, value[keys.mask + 1])}
 * <dd>
 * <dl>
 * <dt>{@code keys}</dt>
 * <dd>Referenen gemäß {@link REFSET}.</dd>
 * <dt>{@code value}</dt>
 * <dd>Element oder {@code null}.</dd>
 * </dl>
 * </dd>
 * </dl>
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class REFMAP {

	/** Diese Methode liefert eine neue leere Referenzabbildung mit Kapazität 2. */
	public static Object[] create() {
		return new Object[]{REFSET.create(), null, null};
	}

	/** Diese Methode liefert die 1-basierte Position der gegebenen Referenz {@code ref} in der gegebenen Referenzabbildung {@code refmap}, wenn die Referenz
	 * ungleich {@code 0} ist. Wenn die Referenz nicht in der Referenzabbildung enthalten ist, wird {@code 0} geliefert. **/
	public static int getIdx(Object[] refmap, int ref) {
		return REFSET.getIdx(REFMAP.getKeys(refmap), ref);
	}

	/** Diese Methode liefert die Referenz an der gegebenen {@link #getIdx(Object[], int) 1-basierten Position} {@code idx} der gegebenen Referenzabbildung
	 * {@code refmap}, wenn die Position ungleich {@code 0} ist. */
	public static int getRef(Object[] refmap, int idx) {
		return REFSET.getRef(REFMAP.getKeys(refmap), idx);
	}

	/** Diese Methode liefert das Element zur Referenz an der gegebenen {@link #getIdx(Object[], int) 1-basierten Position} {@code idx} der gegebenen
	 * Referenzabbildung {@code refmap}, wenn die Position ungleich {@code 0} ist. */
	public static Object getVal(Object[] refmap, int idx) {
		return refmap[idx];
	}

	/** Diese Methode setzt das Element {@code val} zur Referenz an der gegebenen {@link #getIdx(Object[], int) 1-basierten Position} {@code idx} der gegebenen
	 * Referenzabbildung {@code refmap}, wenn die Position ungleich {@code 0} ist. */
	public static void setVal(Object[] refmap, int idx, Object val) {
		refmap[idx] = val;
	}

	/** Diese Methode liefert die 1-basierte Position der gegebenen Referenz {@code ref} in der gegebenen Referenzabbildung {@code refmap}. Wenn die Referenz
	 * nicht in der Referenzmenge enthalten ist, wird sie eingefügt. Wenn die Kapazität erschöpft ist, wird {@code 0} geliefert. **/
	public static int putRef(Object[] refmap, int ref) {
		return REFSET.putRef(REFMAP.getKeys(refmap), ref);
	}

	/** Diese Methode entfernt die gegebenen Referenz {@code ref} aus der gegebenen Referenzabbildung {@code refmap} und liefert die 1-basierte Position der
	 * Referenz, wenn die Referenz ungleich {@code 0} ist. Wenn die Referenz nicht in der Referenzabbildung enthalten ist, wird {@code 0} geliefert. **/
	public static int popRef(Object[] refmap, int ref) {
		return REFSET.popRef(REFMAP.getKeys(refmap), ref);
	}

	/** @see Emuator#emu(Object) */
	public static long emu(Object[] refmap) {
		return REFSET.emu(REFMAP.getKeys(refmap)) + EMU.fromArray(Object.class, refmap.length);
	}

	/** Diese Methode liefert die Anzahl der Referenzen in der gegebenen Referenzabbildung {@code refmap}. */
	public static int size(Object[] refmap) {
		return REFSET.size(REFMAP.getKeys(refmap));
	}

	/** Diese Methode liefert die gegebenen Referenzabbildung {@code refmap}, wenn sie die maximale Kapazität besitzt oder darin noch Platz für mindestens eine
	 * weitere Referenz ist. Andernfalls liefert sie eine Kopie mit doppelter Kapazität. */
	public static Object[] grow(Object[] refmap) {
		var keys = REFMAP.getKeys(refmap);
		return REFMAP.tryCopy(refmap, keys, REFSET.grow(keys));
	}

	/** Diese Methode liefert die gegebenen Referenzabbildung {@code refmap}, wenn sie die minimale Kapazität besitzt oder darin bei halber Kapazität kein Platz
	 * für eine weitere Referenz ist. Andernfalls liefert sie eine Kopie mit halber Kapazität. */
	public static Object[] pack(Object[] refmap) {
		var keys = REFMAP.getKeys(refmap);
		return REFMAP.tryCopy(refmap, keys, REFSET.pack(keys));
	}

	/** Diese Methode liefert eine Kopie der gegebenen Referenzabbildung {@code refmap}. */
	public static Object[] copy(Object[] refmap) {
		var refmap2 = refmap.clone();
		refmap2[0] = REFSET.copy(REFMAP.getKeys(refmap));
		return refmap2;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die gegebenen Referenzabbildungn {@code refmap} die gleichen Paare aus Referenzen und Elementen
	 * enthalten. */
	public static boolean equals(Object[] refmap1, Object[] refmap2) {
		if (refmap1 == refmap2) return true;
		var keys1 = REFMAP.getKeys(refmap1);
		var keys2 = REFMAP.getKeys(refmap2);
		var size = REFSET.size(keys1);
		if (size != REFSET.size(keys2)) return false;
		if (size == 0) return true;
		for (var idx1 = REFSET.getMask(keys1); 0 < idx1; idx1--) {
			var ref = REFSET.getRef(keys1, idx1);
			if (ref != 0) {
				var idx2 = REFSET.getIdx(keys2, ref);
				if (idx2 == 0) return false;
				var val1 = REFMAP.getVal(refmap1, idx1);
				var val2 = REFMAP.getVal(refmap2, idx2);
				if (!Objects.equals(val1, val2)) return false;
				size--;
			}
		}
		return size == 0;
	}

	/** Diese Methode übergibt alle Referenzen und deren Elemente an {@link RUN#run(int, Object) task.run()}. */
	public static void forEach(Object[] refmap, RUN task) {
		var keys = REFMAP.getKeys(refmap);
		for (var idx = refmap.length - 1; 0 < idx; idx--) {
			var val = refmap[idx];
			if (val != null) {
				task.run(REFSET.getRef(keys, idx), val);
			}
		}
	}

	/** Diese Methode liefert den {@link Iterator} über die Referenzen und Elemente der gegebenen Referenzabbildung {@code refmap}. */
	public static ITER iterator(Object[] refmap) {
		return new ITER(refmap, null, null);
	}

	/** Diese Methode liefert den {@link Iterator2} über die Referenzen und Elemente der gegebenen Referenzabbildung {@code refmap}, deren Referenzen in der
	 * ersten gegebenen {@link REFSET Referenzmenge} {@code acceptRefset_or_null} und nicht in der zweiten gegebenen {@link REFSET Referenzmenge}
	 * {@code refuseRefset_or_null} enthalten sind. */
	public static ITER iterator(Object[] refmap, int[] acceptRefset_or_null, int[] refuseRefset_or_null) {
		return new ITER(refmap, acceptRefset_or_null, refuseRefset_or_null);
	}

	/** Diese Methode liefert alle Schlüssel der gegebenen Referenzabbildung {@code refmap}. */
	public static int[] toArray(Object[] refmap) {
		return REFSET.toArray(REFMAP.getKeys(refmap));
	}

	/** Diese Methode liefert die Textdarstellung der gegebenen Referenzabbildung {@code refmap}. */
	public static String toString(Object[] refmap) {
		return REFSET.toString(REFMAP.getKeys(refmap));
	}

	/** Diese Schnittstelle definiert den Empfänger der Referenzen und Elementen für {@link REFMAP#forEach(Object[], RUN)}. */
	public interface RUN {

		/** Diese Methode verarbeitet die gegebene Referenz {@code ref} und das gegebene Elemente {@code val}. */
		void run(int ref, Object val);

	}

	/** Diese Klasse implementiert {@link REFMAP#iterator(Object[])}. */
	public static final class ITER extends AbstractIterator<Entry<Integer, Object>> {

		@Override
		public Entry<Integer, Object> next() {
			var val = this.val;
			return Entries.from(this.nextRef(), val);
		}

		/** Diese Methode liefert die nächsten Referenz oder {@code 0}. */
		public int nextRef() {
			if (!this.hasNext()) throw new NoSuchElementException();
			var ref = this.iterator.nextRef();
			this.val = this.refmap[this.nextIdx()];
			return ref;
		}

		/** Diese Methode liefert das Element der nächsten von {@link #nextRef()} gelieferten Referenz oder {@code null}. */
		public Object nextVal() {
			return this.val;
		}

		/** Diese Methode liefert die 1-basierte Position der nächsten von {@link #nextRef()} gelieferten Referenz oder {@code 0}. */
		public int nextIdx() {
			return this.iterator.nextIdx();
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		Object val;

		final Object[] refmap;

		final REFSET.ITER iterator;

		ITER(Object[] refmap, int[] accept, int[] refuse) {
			this.iterator = REFSET.iterator(REFMAP.getKeys(this.refmap = refmap), accept, refuse);
			this.val = this.refmap[this.nextIdx()];
		}

	}

	static final Object[] EMPTY = new Object[]{REFSET.EMPTY};

	static int[] getKeys(Object[] refmap) {
		return (int[]) /* refmap.keys */ refmap[0];
	}

	static void setKeys(Object[] refmap, int[] keys) {
		/* refmap.keys */ refmap[0] = keys;
	}

	/** Diese Methode liefert die gegebene Referenzabbildung {@code refmap1}, wenn ihre {@link #getKeys(Object[]) Schlüsselmenge} {@code keys1} gleich der
	 * gegebenen {@code keys2} ist. Andernfalls liefert sie eine Kopie mit der Schlüsselmenge {@code keys2}. */
	static Object[] tryCopy(Object[] refmap1, int[] keys1, int[] keys2) {
		if (keys1 == keys2) return refmap1;
		var refmap2 = new Object[REFSET.getMask(keys2) + 2];
		REFMAP.setKeys(refmap2, keys2);
		for (var idx1 = REFSET.getMask(keys1) + 1; 0 < idx1; idx1--) {
			var ref = REFSET.getRef(keys1, idx1);
			if (ref != 0) {
				REFMAP.setVal(refmap2, REFSET.getIdx(keys2, ref), REFMAP.getVal(refmap1, idx1));
			}
		}
		return refmap2;
	}

}