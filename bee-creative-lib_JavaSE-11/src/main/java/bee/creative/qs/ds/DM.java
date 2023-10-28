package bee.creative.qs.ds;

import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QO;
import bee.creative.util.Set2;
import bee.creative.util.Translator2;

/** Diese Schnittstelle definiert ein Domänenmodell (domain-model).
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DM extends QO {

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für den {@link DT Datentyp} von {@link DL}. */
	String IDENT_IsLink = "DM:IsLink";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für den {@link DT Datentyp} von {@link DT}. */
	String IDENT_IsType = "DM:IsType";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#sourceTypes()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithSourceType = "DM:IsLinkWithSourceType";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#sourceClonability()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithSourceClonability = "DM:IsLinkWithSourceClonability";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#sourceMultiplicity()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithSourceMultiplicity = "DM:IsLinkWithSourceMultiplicity";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#targetTypes()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithTargetType = "DM:IsLinkWithTargetType";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#targetClonability()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithTargetClonability = "DM:IsLinkWithTargetClonability";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DL#targetMultiplicity()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithTargetMultiplicity = "DM:IsLinkWithTargetMultiplicity";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DT#instances()}-{@link DL Datenfeld}. */
	String IDENT_IsTypeWithInstance = "DM:IsTypeWithInstance";

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten {@link QE Hyperkanten} mit dem {@link #context() Kontextknoten} dieses Domänenmodells.
	 *
	 * @return Hyperkanten mit {@link #context()}. */
	QESet edges();

	QN context(); // kontext für alles

	DH history(); // log oder null

	default Set2<QN> links() { // datenfelder, beziehungen
		return this.getType(DM.IDENT_IsLink).instances();
	}

	default Set2<DL> linksAsLinks() {
		return this.links().translate(this.linkTrans());
	}

	default Set2<QN> types() { // datentypen
		return this.getType(DM.IDENT_IsType).instances();
	}

	default Set2<DT> typesAsTypes() {
		return this.types().translate(this.typeTrans());
	}

	default DL getLink(QN ident) {
		return this.getLink(ident.value());
	}

	/** Diese Methode liefert das {@link DL Datenfeld} mit dem gegebenen Erkennungstextwert.
	 *
	 * @see #updateIdents()
	 * @param ident {@link QN#value() Textwert} eines {@link DL#idents() Erkennugnsknoten}.
	 * @return {@link DL Datenfeld} oder {@code null}. */
	DL getLink(String ident);

	default DT getType(QN ident) {
		return this.getType(ident.value());
	}

	DT getType(String ident);

	default boolean putEdges(Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException {
		var history = this.history();
		return DS.putEdges(this.context(), edges, history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popEdges(Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException {
		var history = this.history();
		return DS.popEdges(this.context(), edges, history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	/** Diese Methode signalisiert dem Domänenmodell Änderungen an {@link DE#idents()}. Daraufhin können interne Puffer zur Beschleunigung von
	 * {@link #getLink(String)} und {@link #getType(String)} entsprechend aktualisiert werden. */
	void updateIdents();

	Translator2<QN, DL> linkTrans();

	Translator2<QN, DT> typeTrans();

}
