package bee.creative.qs.ds;

import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QO;
import bee.creative.util.AbstractTranslator;
import bee.creative.util.Properties;
import bee.creative.util.Property;
import bee.creative.util.Property2;
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

	default Translator2<QN, DL> linkTrans() {
		return new AbstractTranslator<>() {

			@Override
			public boolean isTarget(Object object) {
				return object instanceof DL;
			}

			@Override
			public boolean isSource(Object object) {
				return object instanceof QN;
			}

			@Override
			public DL toTarget(Object object) throws ClassCastException, IllegalArgumentException {
				return DM.this.getLink((QN)object);
			}

			@Override
			public QN toSource(Object object) throws ClassCastException, IllegalArgumentException {
				return ((DL)object).node();
			}

		};
	}

	default Set2<QN> links() { // datenfelder, beziehungen
		return this.getLink(DM.LINK_IDENT_IsTypeWithInstance).getTargets(this.getType(DM.TYPE_IDENT_IsLink).node()).asSet();
	}

	default Set2<DL> linksAsLinks() {
		return this.links().translate(this.linkTrans());
	}

	default Translator2<QN, DT> typeTrans() {
		return new AbstractTranslator<>() {

			@Override
			public boolean isTarget(Object object) {
				return object instanceof DT;
			}

			@Override
			public boolean isSource(Object object) {
				return object instanceof QN;
			}

			@Override
			public DT toTarget(Object object) throws ClassCastException, IllegalArgumentException {
				return DM.this.getType((QN)object);
			}

			@Override
			public QN toSource(Object object) throws ClassCastException, IllegalArgumentException {
				return ((DT)object).node();
			}

		};
	}

	default Set2<QN> types() { // datentypen
		return this.getLink(DM.LINK_IDENT_IsTypeWithInstance).getTargets(this.getType(DM.TYPE_IDENT_IsType).node()).asSet();
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

	/** Diese Methode signalisiert dem Datenmodell Änderungen an {@link DE#idents()}. Daraufhin können interne Puffer zur Beschleunigung von {@link #getLink(QN)}
	 * und {@link #getType(QN)} entsprechend aktualisiert werden. */
	void updateIdents();

	public default Property2<String> asString(Property<QN> prop) {
		return Properties.translate(prop, node -> node != null ? node.value() : null, value -> value != null ? this.owner().newNode(value) : null);
	}

	Set2<String> asString(Set2<QN> nodes);

}
