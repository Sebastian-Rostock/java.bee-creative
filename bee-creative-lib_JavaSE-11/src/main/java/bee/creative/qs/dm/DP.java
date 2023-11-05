package bee.creative.qs.dm;

import java.util.List;
import bee.creative.qs.QN;

/** Diese Schnittstelle definiert einen Domänenpfad (domain-path), der die Adresse eines Hyperknoten in einer Baumprojektion angibt.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
interface DP {

	List<QN> locals();

	DP parent();

}
