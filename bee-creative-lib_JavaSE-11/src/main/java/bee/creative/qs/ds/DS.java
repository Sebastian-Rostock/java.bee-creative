package bee.creative.qs.ds;

import bee.creative.qs.QE;
import bee.creative.qs.QN;
import bee.creative.qs.QS;
import bee.creative.util.Set2;
import bee.creative.util.Translator;
import bee.creative.util.Translator3;
import bee.creative.util.Translators;

/** Diese Schnittstelle definiert einen Domänenspeicher, der seinen Zustand in einem {@link #store() Graphspeicher} speichert.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DS {

	/** Diese Methode liefert den Graphspeicher, in welchem alle Daten dieses Domänenspeichers gespeichert sind. Der {@link QS#owner() Besitzer} des
	 * Graphspeichers ist dieser Domänenspeicher.
	 *
	 * @return Graphspeicher. */
	QS store();

	default Set2<DM> models() {
		return this.modelsAsNodes().asTranslatedSet(this.modelTrans());
	}
	
	default Set2<QN> modelsAsNodes() {
		return this.installSet(DM.IDENT_IsModel);
	}

	/** Diese Methode liefert den zum gegebenen {@link QN#value() externen Textwert} hintelegten internen {@link QN Hyperknoten}. Dieser wird bei Bedarf erzeugt
	 * und über die {@link QE Hyperkante} {@code ("", value, "", result)} registriert.
	 *
	 * @param ident Textwert eines externen Hyperknoten.
	 * @return dem externen Hyperknoten zugeordneter interne Hyperknoten. */
	QN install(String ident) throws NullPointerException;

	/** Diese Methode liefert die zum gegebenen {@link QN#value() externen Textwert} hintelegte änderbare Menge von {@link QN Hyperknoten}. Diese wird bei Bedarf
	 * erzeugt. Die Menge wird über die {@link QE Hyperkanten} {@code ("", "", install(value), item)} registriert.
	 *
	 * @see #install(String)
	 * @param ident Textwert eines externen Hyperknoten.
	 * @return dem externen Hyperknoten zugeordnete interne Hyperknoten. */
	Set2<QN> installSet(String ident) throws NullPointerException;

	/** Diese Methode liefert den {@link Translators#optionalizedTranslator(Translator) optionalisierten} {@link DM#context() Kontextknoten}-{@link DM Datenmodell}-{@link Translator}. */
	Translator3<QN, DM> modelTrans();

}
