//package bee.creative.ds;
//
//import bee.creative.qs.QE;
//import bee.creative.qs.QN;
//import bee.creative.qs.QS;
//import bee.creative.util.Set2;
//import bee.creative.util.Translator;
//import bee.creative.util.Translator2;
//import bee.creative.util.Translators.OptionalizedTranslator;
//
///** Diese Schnittstelle definiert einen Domänenspeicher.
// *
// * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
//public interface DS {
// 
//	DMSet models();
//	
//	DN getNode(String value);
//
//	DN putNode(String value);
//
//	
//	
//	/** Diese Methode liefert den zum gegebenen {@link QN#value() externen Textwert} hintelegten internen {@link QN Hyperknoten}. Dieser wird bei Bedarf erzeugt
//	 * und über die {@link QE Hyperkante} {@code ("", value, "", result)} registriert.
//	 *
//	 * @param value Textwert eines externen Hyperknoten.
//	 * @return dem externen Hyperknoten zugeordneter interne Hyperknoten. */
//	QN install(String value) throws NullPointerException;
//
//	/** Diese Methode liefert die zum gegebenen {@link QN#value() externen Textwert} hintelegte änderbare Menge von {@link QN Hyperknoten}. Diese wird bei Bedarf
//	 * erzeugt. Die Menge wird über die {@link QE Hyperkanten} {@code ("", "", install(value), items)} registriert.
//	 *
//	 * @see #install(String)
//	 * @param value Textwert eines externen Hyperknoten.
//	 * @return dem externen Hyperknoten zugeordnete interne Hyperknoten. */
//	Set2<QN> installSet(String value) throws NullPointerException;
//
//
//}
