package bee.creative.qs.ds;

import bee.creative.qs.QESet;
import bee.creative.qs.QN;

/** Diese Schnittstelle definiert das {@link DLNSet} für {@link DL#getSources(QN)}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DLSSet extends DLNSet {

	@Override
	default QESet edges() {
		return this.link().edges().havingObject(this.target());
	}

	@Override
	default QN source() {
		return null;
	}

	@Override
	default boolean setNodes(Iterable<? extends QN> nodes) {
		return this.link().setSourceSet(this.target(), nodes);
	}

	@Override
	default boolean putNodes(Iterable<? extends QN> nodes) {
		return this.link().putSourceSet(this.target(), nodes);
	}

	@Override
	default boolean popNodes(Iterable<? extends QN> nodes) {
		return this.link().popSourceSet(this.target(), nodes);
	}

}
