package bee.creative.qs.ds;

import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QO;
import bee.creative.util.Set2;
import bee.creative.util.Translator2;

/** Diese Schnittstelle definiert ein Domänenmodell ({@code domain-model}).
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DM extends QO {

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für den {@link DT Datentyp} von {@link DL}. */
	String TYPE_IDENT_IsLink = "DM:IsLink";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für den {@link DT Datentyp} von {@link DT}. */
	String TYPE_IDENT_IsType = "DM:IsType";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#label()}-{@link DL Datenfeld}. */
	String LINK_IDENT_IsLinkWithLabel = "DM:IsLinkWithLabel";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#idents()}-{@link DL Datenfeld}. */
	String LINK_IDENT_IsLinkWithIdent = "DM:IsLinkWithIdent";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#sourceTypes()}-{@link DL Datenfeld}. */
	String LINK_IDENT_IsLinkWithSourceType = "DM:IsLinkWithSourceType";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#sourceClonability()}-{@link DL Datenfeld}. */
	String LINK_IDENT_IsLinkWithSourceClonability = "DM:IsLinkWithSourceClonability";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#sourceMultiplicity()}-{@link DL Datenfeld}. */
	String LINK_IDENT_IsLinkWithSourceMultiplicity = "DM:IsLinkWithSourceMultiplicity";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#targetTypes()}-{@link DL Datenfeld}. */
	String LINK_IDENT_IsLinkWithTargetType = "DM:IsLinkWithTargetType";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#targetClonability()}-{@link DL Datenfeld}. */
	String LINK_IDENT_IsLinkWithTargetClonability = "DM:IsLinkWithTargetClonability";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#targetMultiplicity()}-{@link DL Datenfeld}. */
	String LINK_IDENT_IsLinkWithTargetMultiplicity = "DM:IsLinkWithTargetMultiplicity";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DT#label()}-{@link DL Datenfeld}. */
	String LINK_IDENT_IsTypeWithLabel = "DM:IsTypeWithLabel";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DT#idents()}-{@link DL Datenfeld}. */
	String LINK_IDENT_IsTypeWithIdent = "DM:IsTypeWithIdent";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DT#instances()}-{@link DL Datenfeld}. */
	String LINK_IDENT_IsTypeWithInstance = "DM:IsTypeWithInstance";

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten {@link QE Hyperkanten} mit dem {@link #context() Kontextknoten} dieses Datenmodells.
	 *
	 * @return Hyperkanten mit {@link #context()}. */
	QESet edges();

	QN model(); // sobjekt des domänenmodells

	QN context(); // kontext für alles

	DH history(); // log oder null

	DL getLink(String ident);

	Translator2<QN, DL> linkTrans();

	default Set2<QN> links() { // datenfelder, beziehungen
		return this.getLink(DM.LINK_IDENT_IsTypeWithInstance).getTargetProxy(this.getType(DM.TYPE_IDENT_IsLink).node());
	}

	default Set2<DL> linksAsLinks() {
		return this.links().translate(this.linkTrans());
	}

	DT getType(String ident);

	Translator2<QN, DT> typeTrans();

	default Set2<QN> types() { // datentypen
		return this.getLink(DM.LINK_IDENT_IsTypeWithInstance).getTargetProxy(this.getType(DM.TYPE_IDENT_IsType).node());
	}

	default Set2<DT> typesAsTypes() {
		return this.types().translate(this.typeTrans());
	}

	default boolean putEdges(Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException {
		var history = this.history();
		return DS.putEdges(this.context(), edges, history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popEdges(Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException {
		var history = this.history();
		return DS.popEdges(this.context(), edges, history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

}
