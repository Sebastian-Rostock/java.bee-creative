package bee.creative.ber;

import bee.creative.emu.EMU;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert Methoden zur Verarbeitung einer steuwertbasierten Abbildung von Referenen ungleich {@code 0} auf Elemente ungleich {@code null},
 * die als {@link Object}-Array mit folgender Struktur gegeben ist: <pre>(keys, value[keys.mask + 1])</pre> Die Datenfelder haben folgende Bedeutung:
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

	/** Diese Methode liefert die 1-basierte Position der gegebenen Referenz {@code ref} in der gegebenen Referenzabbildung {@code refmap}. Wenn die Referenz
	 * nicht in der Referenzabbildung enthalten ist, wird {@code 0} geliefert. **/
	public static int getIdx(Object[] refmap, int ref) {
		return REFSET.getIdx(REFMAP.refset(refmap), ref);
	}

	/** Diese Methode liefert die Referenz an der gegebenen {@link #getIdx(Object[], int) 1-basierten Position} {@code idx} der gegebenen Referenzabbildung
	 * {@code refmap}. */
	public static int getRef(Object[] refmap, int idx) {
		return REFSET.getRef(REFMAP.refset(refmap), idx);
	}

	/** Diese Methode liefert das Element zur Referenz an der gegebenen {@link #getIdx(Object[], int) 1-basierten Position} {@code idx} der gegebenen
	 * Referenzabbildung {@code refmap}. */
	public static Object getVal(Object[] refmap, int idx) {
		return refmap[idx];
	}

	/** Diese Methode setzt das Element {@code val} zur Referenz an der gegebenen {@link #getIdx(Object[], int) 1-basierten Position} {@code idx} der gegebenen
	 * Referenzabbildung {@code refmap}. */
	public static void setVal(Object[] refmap, int idx, Object val) {
		refmap[idx] = val;
	}

	/** Diese Methode liefert die 1-basierte Position der gegebenen Referenz {@code ref} in der gegebenen Referenzabbildung {@code refmap}. Wenn die Referenz
	 * nicht in der Referenzmenge enthalten ist, wird sie eingefügt. Wenn sie {@code 0} ist oder die Kapazität erschöpft ist, wird {@code 0} geliefert. **/
	public static int putRef(Object[] refmap, int ref) {
		return REFSET.putRef(REFMAP.refset(refmap), ref);
	}

	public static int popRef(Object[] refmap, int ref) {
		return REFSET.popRef(REFMAP.refset(refmap), ref);
	}

	/** Diese Methode liefert die Anzahl der Referenzen in der gegebenen Referenzabbildung {@code refmap}. */
	public static int size(Object[] refmap) {
		return REFSET.size(REFMAP.refset(refmap));
	}

	/** Diese Methode liefert die gegebenen Referenzabbildung {@code refmap}, wenn sie die maximale Kapazität besitzt oder darin noch Platz für mindestens eine
	 * weitere Referenz ist. Andernfalls liefert sie eine Kopie mit doppelter Kapazität. */
	public static Object[] grow(Object[] refmap) {
		var refset = REFMAP.refset(refmap);
		return REFMAP.copy(refmap, refset, REFSET.grow(refset));
	}

	/** Diese Methode liefert die gegebenen Referenzabbildung {@code refmap}, wenn sie die minimale Kapazität besitzt oder darin bei halber Kapazität kein Platz
	 * für eine weitere Referenz ist. Andernfalls liefert sie eine Kopie mit halber Kapazität. */
	public static Object[] pack(Object[] refmap) {
		var refset = REFMAP.refset(refmap);
		return REFMAP.copy(refmap, refset, REFSET.pack(refset));
	}

	public static Object[] copy(Object[] refmap) {
		var refmap2 = refmap.clone();
		refmap2[0] = REFSET.copy(refset(refmap));
		return refmap2;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die gegebenen Referenzabbildungn {@code refmap} die gleichen Paare aus Referenzen und Elementen
	 * enthalten. */
	public static boolean equals(Object[] refmap1, Object[] refmap2) {
		if (refmap1 == refmap2) return true;
		var refset1 = REFMAP.refset(refmap1);
		var refset2 = REFMAP.refset(refmap2);
		var size = REFSET.size(refset1);
		if (size != REFSET.size(refset2)) return false;
		if (size == 0) return true;
		for (var idx1 = REFSET.mask(refset1); 0 < idx1; idx1--) {
			var ref = REFSET.getRef(refset1, idx1);
			if (ref != 0) {
				var idx2 = REFSET.getIdx(refset2, ref);
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
		return REFSET.toArray(REFMAP.refset(refmap));
	}

	/** Diese Methode liefert die Textdarstellung der gegebenen Referenzabbildung {@code refmap}. */
	public static String toString(Object[] refmap) {
		return REFSET.toString(REFMAP.refset(refmap));
	}

	public static long emu(Object[] refmap) {
		return REFSET.emu(REFMAP.refset(refmap)) + EMU.fromArray(Object.class, refmap.length);
	}

	static final Object[] EMPTY = new Object[]{REFSET.EMPTY, null};

	static Object[] copy(Object[] refmap1, int[] refset1, int[] refset2) {
		if (refset1 == refset2) return refmap1;
		var refmap2 = new Object[REFSET.mask(refset2) + 1];
		refmap2[0] = refset2;
		for (var idx1 = REFSET.mask(refset1); 0 < idx1; idx1--) {
			var ref = REFSET.getRef(refset1, idx1);
			if (ref != 0) {
				REFMAP.setVal(refmap2, REFSET.getIdx(refset2, ref), REFMAP.getVal(refmap1, idx1));
			}
		}
		return refmap2;
	}

	static int[] refset(Object[] refmap) {
		return (int[]) /* refmap.keys */ refmap[0];
	}

}