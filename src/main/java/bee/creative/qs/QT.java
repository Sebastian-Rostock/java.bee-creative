package bee.creative.qs;

import java.util.List;
import bee.creative.lang.Array2;

/** Diese Schnittstelle definiert eine Hypertupel, welches eine bestimmte {@link #size() Anzahl} an {@link QN Hyperknoten} mit bestimmten {@link #get(int)
 * Positionen} miteinander verbindet. {@link #hashCode() Streuwert} und {@link #equals(Object) Äquivalenz} basieren auf den der Hyperknoten.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface QT extends QO, Array2<QN> {

	public List<QN> toList();

}
