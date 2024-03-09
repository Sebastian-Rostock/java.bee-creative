//package bee.creative.ds;
//
//import bee.creative.qs.QE;
//import bee.creative.qs.QESet;
//import bee.creative.qs.QN;
//import bee.creative.qs.QO;
//import bee.creative.util.Set2;
//import bee.creative.util.Translator;
//import bee.creative.util.Translator2;
//import bee.creative.util.Translators.OptionalizedTranslator;
//
///** Diese Schnittstelle definiert ein Domänenmodell (domain-model), dass auf einem {@link #owner() Graphspeicher} aufbaut und Wissen in Form von {@link QE
// * Hyperkanten} mit einem bestimmten {@link #context() Kontextknoten} beschreibt. Die Prädikatknoten stehen objektrelational gesehen für {@link DL Datenfelder}
// * bzw. Spalten konkreter Tabellen.
// *
// * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
//interface DM extends QO {
//
//	/** Dieses Feld speichert den Textwert des {@link DS#installSet(String) Erkennungsknoten} für die {@link DS#modelsAsNodes() Domänenmodelle} eines {@link DS
//	 * Domänenspeichers}. */
//	String IDENT_IsModel = "DS:IsModel";
//
//	/** Diese Methode liefet das dieses Domänenmodelle verwaltenden Domänenspeicher.
//	 *
//	 * @return Domänenspeicher. */
//	DS parent();
//
//	DH history(); // log oder null
//
//	/** Diese Methode liefert den Als {@link QE#context() Kontextknoten} aller {@link QE Hyperkanten} dieses {@link #parent() Domänenmodells}.
//	 *
//	 * @return Kontextknoten. */
//	QN context();
//
//	/** Diese Methode erlaubt Zugriff auf alle {@link DL Datenfelder}.
//	 *
//	 * @return Datenfelder. */
//	DLSet links();
//
//	/** Diese Methode erlaubt Zugriff auf alle {@link DT Datentypen}.
//	 *
//	 * @return Datentypen. */
//	DTSet types();
//
//	/** Diese Methode liefert das {@link DL Datenfeld} mit dem gegebenen Erkennungstextwert oder {@code null}.
//	 *
//	 * @param ident {@link QN#value() Textwert} eines {@link DL#identsAsNodes() Erkennugnsknoten}.
//	 * @return {@link DL Datenfeld} oder {@code null}. */
//	DL getLink(String ident);
//
//	DL putLink(String ident);
//
//	/** Diese Methode liefert den {@link DT Datentyp} mit dem gegebenen Erkennungstextwert oder {@code null}.
//	 *
//	 * @param ident {@link QN#value() Textwert} eines {@link DT#identsAsNodes() Erkennugnsknoten}.
//	 * @return {@link DT Datentyp} oder {@code null}. */
//	DT getType(String ident);
//
//	DT putType(String ident);
//
//}
