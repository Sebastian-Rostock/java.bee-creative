package bee.creative.qs;

import java.util.List;
import bee.creative.lang.Array2;

/** Diese Schnittstelle definiert eine Hypertupel, welches {@link QN Hyperknoten} {@link #get(int) positionsbezogen} miteinander verbindet. {@link #hashCode()
 * Streuwert} und {@link #equals(Object) Äquivalenz} basieren auf den der Hyperknoten. Die {@link #size() Anzahl} der Hyperknoten ist stets größer als
 * {@code 0}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QT extends QO, Array2<QN> {

	/** Diese Methode liefert eine {@link List} als kopie der Hyperknoten dieses Hypertupel. */
	List<QN> toList();

	/** Diese Methode liefert ein Array als Kopie der Hyperknoten dieses Hypertupel. */
	QN[] toArray();

}
