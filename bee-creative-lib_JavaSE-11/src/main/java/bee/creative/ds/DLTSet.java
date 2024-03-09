//package bee.creative.ds;
//
//import bee.creative.qs.QESet;
//import bee.creative.qs.QN;
//
///** Diese Schnittstelle definiert das {@link DLNSet} für {@link DL#getTargets(QN)}.
// *
// * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
//public interface DLTSet extends DLNSet {
//
//	@Override
//	default QESet edges() {
//		return this.link().edges().havingSubject(this.source());
//	}
//
//	@Override
//	default QN target() {
//		return null;
//	}
//
//	@Override
//	default boolean setNodes(Iterable<? extends QN> nodes) {
//		return this.link().setTargetSet(this.source(), nodes);
//	}
//
//	@Override
//	default boolean putNodes(Iterable<? extends QN> nodes) {
//		return this.link().putTargetSet(this.source(), nodes);
//	}
//
//	@Override
//	default boolean popNodes(Iterable<? extends QN> nodes) {
//		return this.link().popTargetSet(this.source(), nodes);
//	}
//
//}
