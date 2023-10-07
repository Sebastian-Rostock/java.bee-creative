package bee.creative.qs.ds;

import java.util.Set;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;

/** Diese Schnittstelle definiert ein Datenelement als {@link #node() Hyperknoten} mit Bezug zu einem {@link #model() Datenmodell}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DE extends DO {

	/** Dieses Feld speichert den Textwert eines {@link #idents() Erkennungsknoten} für das {@link #label()}-{@link DL Datenfeld}. */
	String IDENT_ITEM_HAS_LABEL = "DM:ITEM_HAS_LABEL";

	/** Dieses Feld speichert den Textwert eines {@link #idents() Erkennungsknoten} für das {@link #idents()}-{@link DL Datenfeld}. */
	String IDENT_ITEM_HAS_IDENT = "DM:ITEM_HAS_IDENT";

	/** Diese Methode liefert den dieses Objekt repräsentierenden Hyperknoten.
	 *
	 * @return Hyperknoten, über den der Zustand dieses Objets gespeichert ist. */
	QN node();

	/** Diese Methode liefert den {@link QN Hyperknoten}, der zur Beschriftung dieses Objekts dient. Dazu sollte dieser Beschriftungsknoten einen
	 * {@link QN#value() Textwert} besitzen.
	 *
	 * @return Beschriftungsknoten. */
	default QN label() {
		return this.model().itemIdentLink().getTarget(this.node());
	}

	/** Diese Methode liefert den {@link QN#value() Textwert} des {@link #label() Beschriftungsknoten}. Wenn dieser {@code null} ist, wird die leere Zeichenkette
	 * geliefert. */
	default String labelAsString() {
		var label = this.label();
		return label != null ? label.value() : "";
	}

	/** Diese Methode liefert die Menge der {@link QN Hyperknoten}, die zur Erkennung dieses Objekts dient. Dazu sollten diese Erkennungsknoten einen
	 * {@link QN#value() Textwert} besitzen.
	 *
	 * @return Erkennungsknotenmenge. */
	default QNSet idents() {
		return this.model().itemIdentLink().getTargetSet(this.node());
	}

	/** Diese Methode liefert die {@link QN#value() Textwerte} der {@link #idents() Erkennungsknoten}. */
	default Set<String> identsAsStrings() {
		return this.idents().values().toSet();
	}

}
