package bee.creative.qs.ds;

import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QO;
import bee.creative.util.Set2;
import bee.creative.util.Translator;
import bee.creative.util.Translator2;
import bee.creative.util.Translators.OptionalizedTranslator;

/** Diese Schnittstelle definiert ein Domänenmodell (domain-model).
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DM extends QO {

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten {@link QE Hyperkanten} mit dem {@link #context() Kontextknoten} dieses Domänenmodells.
	 *
	 * @return Hyperkanten mit {@link #context()}. */
	QESet edges();

	QN context(); // kontext für alles

	DH history(); // log oder null

	/** Diese Methode liefert den {@link OptionalizedTranslator optionalisierten} {@link DL#node() Feldknoten}-{@link DL Datenfeld}-{@link Translator}. */
	Translator2<QN, DL> linkTrans();

	default Set2<QN> links() { // datenfelder, beziehungen
		return this.getType(DL.IDENT_IsLink).instances();
	}

	default Set2<DL> linksAsLinks() {
		return this.links().translate(this.linkTrans());
	}

	/** Diese Methode liefert den {@link OptionalizedTranslator optionalisierten} {@link DT#node() Typknoten}-{@link DT Datentyp}-{@link Translator}. */
	Translator2<QN, DT> typeTrans();

	default Set2<QN> types() { // datentypen
		return this.getType(DT.IDENT_IsType).instances();
	}

	default Set2<DT> typesAsTypes() {
		return this.types().translate(this.typeTrans());
	}

	/** Diese Methode liefert das {@link DL Datenfeld} mit dem gegebenen Erkennungsknoten oder {@code null}.
	 *
	 * @see #updateIdents()
	 * @param ident {@link DL#idents() Erkennugnsknoten}.
	 * @return {@link DL Datenfeld} oder {@code null}. */
	default DL getLink(QN ident) {
		return this.getLink(ident.value());
	}

	/** Diese Methode liefert das {@link DL Datenfeld} mit dem gegebenen Erkennungstextwert oder {@code null}.
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

	/** Diese Methode übernimmt Änderungen an {@link DE#idents()} auf die internen Puffer zur Beschleunigung von {@link #getLink(String)} und
	 * {@link #getType(String)}. Wenn das {@link DL Datenfeld} zu {@link DL#IDENT_IsLinkWithIdent} nicht ermittelt werden kann, wird das Datenmodell
	 * initialisiert. Wenn essentielle Datenfelder oder Datentypen nicht ermittelt werden können, wird eine Ausnahme ausgelöst. Dies verursachende Änderungen an
	 * den {@link #edges()} sollten über die {@link #history()} rückgängig gemacht werden. */
	void updateIdents() throws IllegalArgumentException;

}
