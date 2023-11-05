package bee.creative.qs.dm;

import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.util.Property2;

/** Diese Schnittstelle definiert das {@link DNSet} für {@link DL#getTargets(QN)}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DNSetT extends DNSet {

	@Override
	default QESet edges() {
		return this.link().edges().havingSubject(this.source());
	}

	@Override
	default QN target() {
		return null;
	}

	@Override
	default boolean setNodes(Iterable<? extends QN> nodes) {
		return this.link().setTargetSet(this.source(), nodes);
	}

	@Override
	default boolean putNodes(Iterable<? extends QN> nodes) {
		return this.link().putTargetSet(this.source(), nodes);
	}

	@Override
	default boolean popNodes(Iterable<? extends QN> nodes) {
		return this.link().popTargetSet(this.source(), nodes);
	}

	@Override
	default Property2<QN> asNode() {
		return this.link().asTargetProperty(this.source());
	}

}
