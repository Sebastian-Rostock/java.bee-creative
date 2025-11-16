package bee.creative.qs.ds;

import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QO;
import bee.creative.util.Set2;
import bee.creative.util.Translator;
import bee.creative.util.Translator3;
import bee.creative.util.Translators;

/** Diese Schnittstelle definiert ein Domänenmodell (domain-model), dass auf einem {@link #owner() Graphspeicher} aufbaut und Wissen in Form von {@link QE
 * Hyperkanten} mit einem bestimmten {@link #context() Kontextknoten} beschreibt. Die Prädikatknoten stehen objektrelational gesehen für {@link DL Datenfelder}
 * bzw. Spalten konkreter Tabellen.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DM extends QO {

	/** Dieses Feld speichert den Textwert des {@link DS#installSet(String) Erkennungsknoten} für die {@link DS#modelsAsNodes() Domänenmodelle} eines {@link DS
	 * Domänenspeichers}. */
	String IDENT_IsModel = "DS:IsModel";

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten {@link QE Hyperkanten} mit dem {@link #context() Kontextknoten} dieses Domänenmodells.
	 *
	 * @return Hyperkanten mit {@link #context()}. */
	default QESet edges() {
		return this.owner().edges().havingContext(this.context());
	}

	/** Diese Methode liefet den dieses Domänenmodell verwaltenden Domänenspeicher.
	 *
	 * @return Domänenspeicher. */
	DS parent();

	DH history(); // log oder null

	/** Diese Methode liefert den Als {@link QE#context() Kontextknoten} aller {@link QE Hyperkanten} dieses {@link #parent() Domänenmodells}.
	 *
	 * @return Kontextknoten. */
	QN context();

	/** Diese Methode erlaubt Zugriff auf alle {@link DL Datenfelder}.
	 *
	 * @see #linksAsNodes()
	 * @see #links()
	 * @return Datenfelder. */
	default Set2<DL> links() {
		return this.linksAsNodes().asTranslatedSet(this.linkTrans());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link DL#node() Hyperknoten} aller {@link DL Datenfelder}.
	 *
	 * @see DL#IDENT_IsLink
	 * @see DL#node()
	 * @see DT#instancesAsNodes()
	 * @return Datenfeldknoten. */
	default Set2<QN> linksAsNodes() {
		return this.getType(DL.IDENT_IsLink).instancesAsNodes();
	}

	/** Diese Methode erlaubt Zugriff auf alle {@link DT Datentypen}.
	 *
	 * @see #typesAsNodes()
	 * @see #typeTrans()
	 * @return Datentypen. */
	default Set2<DT> types() {
		return this.typesAsNodes().asTranslatedSet(this.typeTrans());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link DT#node() Hyperknoten} aller {@link DT Datentypen}.
	 *
	 * @see DT#IDENT_IsType
	 * @see DT#node()
	 * @see DT#instancesAsNodes()
	 * @return Datentypknoten. */
	default Set2<QN> typesAsNodes() {
		return this.getType(DT.IDENT_IsType).instancesAsNodes();
	}

	/** Diese Methode liefert das {@link DL Datenfeld} mit dem gegebenen Erkennungstextwert oder {@code null}.
	 *
	 * @param ident {@link QN#value() Textwert} eines {@link DL#identsAsNodes() Erkennugnsknoten}.
	 * @return {@link DL Datenfeld} oder {@code null}. */
	DL getLink(String ident);

	/** Diese Methode liefert den {@link DT Datentyp} mit dem gegebenen Erkennungstextwert oder {@code null}.
	 *
	 * @param ident {@link QN#value() Textwert} eines {@link DT#identsAsNodes() Erkennugnsknoten}.
	 * @return {@link DT Datentyp} oder {@code null}. */
	DT getType(String ident);

	/** Diese Methode speichert die als {@link QE Hyperkanten} gegebenen Prädikat-Subjekt-Objekt-Tripel mit dem {@link #context() Kontextknoten} dieses
	 * Datenmodells im {@link #owner() Graphspeicher}. Wenn die {@link #history()} vorliegt, wird der Unterschied gegenüber des vorherigen Datenstandes erfasst.
	 *
	 * @see DQ#putEdges(QN, Iterable, QN, QN)
	 * @param edges Hinzuzufügende Prädikat-Subjekt-Objekt-Tripel.
	 * @return {@code true} bei Änderung des Graphspeicherinhalts; {@code false} sonst. */
	default boolean putEdges(Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException {
		var history = this.history();
		return DQ.putEdges(this.context(), edges, history != null ? history.currentPutContext() : null, history != null ? history.currentPopContext() : null);
	}

	/** Diese Methode entfernt die als {@link QE Hyperkanten} gegebenen Prädikat-Subjekt-Objekt-Tripel mit dem {@link #context() Kontextknoten} dieses
	 * Datenmodells aus dem {@link #owner() Graphspeicher}. Wenn die {@link #history()} vorliegt, wird der Unterschied gegenüber des vorherigen Datenstandes
	 * erfasst.
	 *
	 * @see DQ#popEdges(QN, Iterable, QN, QN)
	 * @param edges Zuentfernende Prädikat-Subjekt-Objekt-Tripel.
	 * @return {@code true} bei Änderung des Graphspeicherinhalts; {@code false} sonst. */
	default boolean popEdges(Iterable<? extends QE> edges) throws NullPointerException, IllegalArgumentException {
		var history = this.history();
		return DQ.popEdges(this.context(), edges, history != null ? history.currentPutContext() : null, history != null ? history.currentPopContext() : null);
	}

	/** Diese Methode liefert den {@link Translators#optionalizedTranslator(Translator) optionalisierten} {@link DL#node() Feldknoten}-{@link DL
	 * Datenfeld}-{@link Translator}. */
	Translator3<QN, DL> linkTrans();

	/** Diese Methode liefert den {@link Translators#optionalizedTranslator(Translator) optionalisierten} {@link DT#node() Typknoten}-{@link DT
	 * Datentyp}-{@link Translator}. */
	Translator3<QN, DT> typeTrans();

}
