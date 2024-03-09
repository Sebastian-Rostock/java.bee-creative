//package bee.creative.ds;
//
//import bee.creative.qs.QN;
//import bee.creative.qs.QS;
//import bee.creative.util.Property2;
//import bee.creative.util.Set2;
//import bee.creative.util.Setter;
//
///** Diese Schnittstelle definiert einen Datentyp (Domain-type) als {@link #labelAsNode() beschriftete} und {@link #identsAsNodes() erkennbare} {@link #instancesAsNodes()
// * Instanzmenge}.
// *
// * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
//public interface DT extends DO {
//
//	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für den {@link DT Datentyp} von {@link DT}. */
//	String IDENT_IsType = "DS:IsType";
//
//	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link DE#labelAsNode()}-{@link DL Datenfeld}. */
//	String IDENT_IsTypeWithLabel = "DS:IsTypeWithLabel";
//
//	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link DE#identsAsNodes()}-{@link DL Datenfeld}. */
//	String IDENT_IsTypeWithIdent = "DS:IsTypeWithIdent";
//
//	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link DT#instances()}-{@link DL Datenfeld}. */
//	String IDENT_IsTypeWithInstance = "DS:IsTypeWithInstance";
//
//	
//	/** Diese Methode erlaubt Zugriff auf die {@link QN#value() Textwerte} zur {@link #identsAsNodes() Erkennung} dieses Objekts.
//	 *
//	 * @see QS#valueTrans()
//	 * @return Erkennungstextwerte. */
//	default Set2<String> idents() {
//		return this.identsAsNodes().translate(this.owner().valueTrans());
//	}
//	DNVal ident();
//	
//	DN create();
//	
//	
//
//	/** Diese Methode erlaubt Zugriff auf die {@link QN Hyperknoten} der Instanzen dieses Datentyps. Eine Instanz darf nur einen Datentyp besitzen. Hyperknoten
//	 * mit {@link QN#value() Textwert} sind als Instanz nicht zulässig.
//	 *
//	 * @see DT#IDENT_IsTypeWithInstance
//	 * @see DL#asTargetSet(QN)
//	 * @return Instanzknoten. */
//	DNSet instances();
//
//	Property2<Setter<DT, DN>> onCreate();
//	
//	Property2<Setter<DT, DN>> onDelete();
//	
//	/** Diese Methode erlaubt Zugriff auf die diesen Datentyp als {@link DL#targetType() Objektdatentyp} zulassenden {@link DL Datenfelder}.
//	 *
//	 * @return Objektdatenfelder. */
//	DLSet targetLinks();
//
//	/** Diese Methode erlaubt Zugriff auf die diesen Datentyp als {@link DL#sourceType() Subjektdatentyp} zulassenden {@link DL Datenfelder}.
//	 *
//	 * @return Subjektdatenfelder. */
//	DLSet sourceLinks();
//
//
//}
