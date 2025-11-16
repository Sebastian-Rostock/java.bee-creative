package bee.creative.qs.ds;

import bee.creative.qs.QESet;
import bee.creative.qs.QN;

/** Diese Schnittstelle definiert das {@link DLNSet} für {@link DL#getObjects(QN)}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DLTSet extends DLNSet {

	@Override
	default QESet edges() {
		return this.link().edges().havingSubject(this.subject());
	}

	@Override
	default QN object() {
		return null;
	}

	@Override
	default boolean setNodes(Iterable<? extends QN> nodes) {
		return this.link().setObjectSet(this.subject(), nodes);
	}

	@Override
	default boolean putNodes(Iterable<? extends QN> nodes) {
		return this.link().putObjectSet(this.subject(), nodes);
	}

	@Override
	default boolean popNodes(Iterable<? extends QN> nodes) {
		return this.link().popObjectSet(this.subject(), nodes);
	}

}
