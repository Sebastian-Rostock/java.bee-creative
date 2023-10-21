package bee.creative.qs.ds;

import java.util.Set;
import bee.creative.qs.QN;
import bee.creative.qs.QO;
import bee.creative.qs.QS;
import bee.creative.util.Property;
import bee.creative.util.Property2;
import bee.creative.util.Set2;

/** Diese Schnittstelle definiert ein Datenelement (Domain-Element) als {@link #node() Hyperknoten} mit Bezug zu einem {@link #parent() Datenmodell} sowie mit
 * {@link #label() Beschriftung} und {@link #idents() Erkennungsmerkmalen}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DE extends QO {

	/** Diese Methode liefert den dieses Objekt repräsentierenden Hyperknoten.
	 *
	 * @return Hyperknoten, über den der Zustand dieses Objets gespeichert ist. */
	QN node();

	/** Diese Methode liefet den Graphspeicher des {@link #node() Hyperknoten}.
	 *
	 * @return Graphspeicher. */
	@Override
	default QS owner() {
		return this.parent().owner();
	}

	/** Diese Methode liefet das dieses Objekt verwaltende Datenmodell.
	 *
	 * @return Datenmodell. */
	DM parent();

	/** Diese Methode liefert den {@link QN Hyperknoten} zur Beschriftung dieses Objekts. Dieser Beschriftungsknoten sollte dazu einen {@link QN#value() Textwert}
	 * besitzen oder beschreiben.
	 *
	 * @return Beschriftungsknoten. */
	Property2<QN> label();

	/** Diese Methode liefert den {@link QN#value() Textwert} des {@link #label() Beschriftungsknoten} bzw. {@code null}.
	 * 
	 * @see DM#asString(Property) */
	default Property2<String> labelAsString() {
		return this.parent().asString(this.label());
	}

	/** Diese Methode liefert die Menge der {@link QN Hyperknoten} zur Erkennung dieses Objekts. Diese Erkennungsknoten sollten dazu einen {@link QN#value()
	 * Textwert} besitzen.
	 *
	 * @return Erkennungsknotenmenge. */
	Set2<QN> idents();

	/** Diese Methode liefert die {@link QN#value() Textwerte} der {@link #idents() Erkennungsknoten}.
	 * 
	 * @see DM#asStrings(Set) */
	default Set2<String> identsAsStrings() {
		return this.parent().asStrings(this.idents());
	}

}
