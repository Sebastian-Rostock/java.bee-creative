package bee.creative.qs.ds;

import bee.creative.qs.QN;

public interface DT extends DE {

	String LINK_NAME_HAS_NAME = "DM:TYPE_HAS_NAME";

	String LINK_NAME_HAS_LABEL = "DM:TYPE_HAS_LABEL";

	default void set(QN item) {
		// model().nodeTypeLink()
	};

	void setAll(Iterable<? extends QN> items);

	/** Diese Methode liefert die {@link DL Datenfelder}, die von Instanzen dieses Datentyps ausgehen. */
	DLSet sourceLinks();

	/** Diese Methode liefert die {@link DL Datenfelder}, die auf Instanzen dieses Datentyps verweisen. */
	DLSet targetLinks();

}
