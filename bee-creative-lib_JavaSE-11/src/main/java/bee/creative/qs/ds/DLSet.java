package bee.creative.qs.ds;

import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;

public interface DLSet extends QNSet {

	DL link();

	QESet linkEdges();

	QN linkSource();

	QN linkTarget();

	boolean setLinkNodes(Iterable<? extends QN> nodes);

	boolean putLinkNodes(Iterable<? extends QN> nodes);

	boolean popLinkNodes(Iterable<? extends QN> nodes);

}
