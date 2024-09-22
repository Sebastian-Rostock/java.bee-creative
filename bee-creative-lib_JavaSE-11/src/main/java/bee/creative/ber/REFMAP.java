package bee.creative.ber;

import bee.creative.emu.EMU;
import bee.creative.emu.Emuator;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert Methoden zur Verarbeitung einer steuwertbasierten Abbildung von Referenen ungleich {@code 0} auf Elemente ungleich {@code null},
 * die als {@link Object}-Array mit folgender Struktur gegeben ist: <pre>(keys, value[keys.mask + 2])</pre> Die Datenfelder haben folgende Bedeutung:
 * <ul>
 * <li>{@code keys} - Referenen gemäß {@link REFSET}.</li>
 * <li>{@code value} - Element oder {@code null}.</li>
 * </ul>
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class REFMAP {

	/** Diese Methode liefert eine neue leere Referenzabbildung mit Kapazität 2. */
	public static Object[] make() {
		return new Object[]{REFSET.make(), null, null};
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

	/** Diese Methode liefert alle Schlüssel der gegebenen Referenzabbildung {@code refmap}. */
	public static int[] toArray(Object[] refmap) {
		return REFSET.toArray(REFMAP.getKeys(refmap));
	}

	/** Diese Methode liefert die Textdarstellung der gegebenen Referenzabbildung {@code refmap}. */
	public static String toString(Object[] refmap) {
		return REFSET.toString(REFMAP.getKeys(refmap));
	}

	/** @see Emuator#emu(Object) */
	public static long emu(Object[] refmap) {
		return REFSET.emu(REFMAP.getKeys(refmap)) + EMU.fromArray(Object.class, refmap.length);
	}

	static final Object[] EMPTY = new Object[]{REFSET.EMPTY, null, null, null};

	static int[] getKeys(Object[] refmap) {
		try {
			return (int[]) /* refmap.keys */ refmap[0];
		} catch (RuntimeException e) {
			throw e;
		}
	}

	static void setKeys(Object[] refmap, int[] keys) {
		/* refmap.keys */ refmap[0] = keys;
	}

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