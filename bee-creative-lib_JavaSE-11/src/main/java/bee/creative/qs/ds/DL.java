package bee.creative.qs.ds;

import static bee.creative.qs.ds.DL.Handling.handlingTrans;
import static bee.creative.util.Properties.propertyFrom;
import static bee.creative.util.Translators.translatorFromEnum;
import static java.util.Collections.singletonMap;
import java.util.List;
import java.util.Map;
import bee.creative.lang.Objects;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QS;
import bee.creative.util.Property3;
import bee.creative.util.Set2;
import bee.creative.util.Translator;
import bee.creative.util.Translator3;
import bee.creative.util.Translators;

/** Diese Schnittstelle definiert ein Datenfeld (Domain-Link) als {@link #labelAsNode() beschriftete} und {@link #identsAsNodes() erkennbarer} {@link #node()
 * Prädikatknoten} mit Festlegungen zur Vielzahl, Handhabung und Typisierung der damit verbundenen Objekt- und Subjektknoten.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DL extends DE {

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für den {@link DT Datentyp} von {@link DL}. */
	String IDENT_IsLink = "DS:IsLink";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #labelAsNode()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithLabel = "DS:IsLinkWithLabel";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #identsAsNodes()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithIdent = "DS:IsLinkWithIdent";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #subjectTypeAsNode()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithSubjectType = "DS:IsLinkWithSubjectType";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #subjectHandlingAsNode()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithSubjectHandling = "DS:IsLinkWithSubjectHandling";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #subjectMultiplicityAsNode()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithSubjectMultiplicity = "DS:IsLinkWithSubjectMultiplicity";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #objectTypeAsNode()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithObjectType = "DS:IsLinkWithObjectType";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #objectHandlingAsNode()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithObjectHandling = "DS:IsLinkWithObjectHandling";

	/** Dieses Feld speichert den Textwert eines {@link DE#identsAsNodes() Erkennungsknoten} für das {@link #objectMultiplicityAsNode()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithObjectMultiplicity = "DS:IsLinkWithObjectMultiplicity";

	/** {@inheritDoc} Dieser wird als {@link QE#predicate() Prädikatknoten} der {@link #edges() Hyperkanten} verwendet. Als {@link QE#context() Kontextknoten}
	 * wird der des {@link #parent() Domänenmodells} verwendet. */
	@Override
	QN node();

	/** {@inheritDoc}
	 *
	 * @see #IDENT_IsLinkWithLabel */
	@Override
	default Property3<QN> labelAsNode() {
		return this.parent().getLink(DL.IDENT_IsLinkWithLabel).asObjectProperty(this.node());
	}

	/** {@inheritDoc}
	 *
	 * @see #IDENT_IsLinkWithIdent */
	@Override
	default Set2<QN> identsAsNodes() {
		return this.parent().getLink(DL.IDENT_IsLinkWithIdent).asObjectSet(this.node());
	}

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten {@link QE Hyperkanten} mit dem {@link QE#context() Kontextknoten} des {@link #parent()
	 * Domänenmodells} und dem {@link #node() Hyperknoten} dieses Datenfeldes als {@link QE#predicate() Prädikatknoten}.
	 *
	 * @return Hyperkanten mit Kontext- und Prädikatbindung. */
	default QESet edges() {
		return this.parent().edges().havingPredicate(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf den {@link DT Datentyp} zulässiger {@link QE#subject() Subjektknoten}. Wenn dieser {@code null} ist, sind die
	 * Subjektknoten nicht eingeschränkt.
	 *
	 * @see DM#typeTrans()
	 * @see DL#subjectTypeAsNode()
	 * @return Subjektdatentyp. */
	default Property3<DT> subjectType() {
		return this.subjectTypeAsNode().translate(this.parent().typeTrans());
	}

	/** Diese Methode erlaubt Zugriff auf den {@link DT#node() Hyperknoten} des {@link DT Datentyps} zulässiger {@link QE#subject() Subjektknoten}. Wenn dieser
	 * {@code null} ist, sind die Subjektknoten nicht eingeschränkt.
	 *
	 * @see DL#IDENT_IsLinkWithSubjectType
	 * @see DL#asObjectProperty(QN)
	 * @return Subjektdatentypknoten. */
	default Property3<QN> subjectTypeAsNode() {
		return this.parent().getLink(DL.IDENT_IsLinkWithSubjectType).asObjectProperty(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link Handling Handhabung} von {@link QE#subject() Subjektknoten} beim Entfernen oder Duplizieren eines
	 * {@link QE#object() Objektknoten}.
	 *
	 * @see DL#subjectHandlingAsNode()
	 * @see QS#valueTrans()
	 * @see Handling#handlingTrans()
	 * @return Subjekthandhabung. */
	default Property3<Handling> subjectHandling() {
		return this.subjectHandlingAsNode().translate(this.owner().valueTrans()).translate(handlingTrans());
	}

	/** Diese Methode erlaubt Zugriff auf den {@link DT#node() Hyperknoten} der {@link Handling Handhabung} von {@link QE#subject() Subjektknoten} beim Entfernen
	 * oder Duplizieren eines {@link QE#object() Objektknoten}.
	 *
	 * @see DL#IDENT_IsLinkWithSubjectHandling
	 * @return Subjekthandhabungsknoten. */
	default Property3<QN> subjectHandlingAsNode() {
		return this.parent().getLink(DL.IDENT_IsLinkWithSubjectHandling).asObjectProperty(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die zulässige {@link Multiplicity Vielzahl} von {@link QE#subject() Subjektknoten} je {@link QE#object() Objektknoten}.
	 *
	 * @see DL#subjectMultiplicityAsNode()
	 * @see QS#valueTrans()
	 * @see Multiplicity#multiplicityTrans()
	 * @return Subjektvielzahl. */
	default Property3<Multiplicity> subjectMultiplicity() {
		return this.subjectMultiplicityAsNode().translate(this.owner().valueTrans()).translate(Multiplicity.trans);
	}

	/** Diese Methode erlaubt Zugriff auf den {@link DT#node() Hyperknoten} der zulässigen {@link Multiplicity Vielzahl} von {@link QE#subject() Subjektknoten} je
	 * {@link QE#object() Objektknoten}.
	 *
	 * @see DL#IDENT_IsLinkWithSubjectMultiplicity
	 * @return Subjektvielzahlknoten. */
	default Property3<QN> subjectMultiplicityAsNode() {
		return this.parent().getLink(DL.IDENT_IsLinkWithSubjectMultiplicity).asObjectProperty(this.node());
	}

	default Property3<DT> objectType() {
		return this.objectTypeAsNode().translate(this.parent().typeTrans());
	}

	/** Diese Methode erlaubt Zugriff auf den {@link DT#node() Hyperknoten} des {@link DT Datentyps} zulässiger {@link QE#object() Objektknoten}. Wenn dieser
	 * {@code null} ist, sind die Objektknoten nicht eingeschränkt.
	 *
	 * @see DL#IDENT_IsLinkWithObjectType
	 * @see DL#asObjectProperty(QN)
	 * @return Objektdatentypknoten. */
	default Property3<QN> objectTypeAsNode() {
		return this.parent().getLink(DL.IDENT_IsLinkWithObjectType).asObjectProperty(this.node());
	}

	default Property3<Handling> objectHandling() {
		return this.objectHandlingAsNode().translate(this.owner().valueTrans()).translate(handlingTrans());
	}

	default Property3<QN> objectHandlingAsNode() {
		return this.parent().getLink(DL.IDENT_IsLinkWithObjectHandling).asObjectProperty(this.node());
	}

	default Property3<Multiplicity> objectMultiplicity() {
		return this.objectMultiplicityAsNode().translate(this.owner().valueTrans()).translate(Multiplicity.trans);
	}

	default Property3<QN> objectMultiplicityAsNode() {
		return this.parent().getLink(DL.IDENT_IsLinkWithObjectMultiplicity).asObjectProperty(this.node());
	}

	default QN getSubject(QN object) throws NullPointerException, IllegalArgumentException {
		return this.getSubjects(object).first();
	}

	DLNSet getSubjects(QN object) throws NullPointerException, IllegalArgumentException;

	default Set2<QN> getSubjectSet(QN object) throws NullPointerException, IllegalArgumentException {
		return this.getSubjects(object).toSet();
	}

	default Map<QN, QN> getSubjectMap() throws NullPointerException, IllegalArgumentException {
		return DQ.getSubjectMap(this.parent().context(), this.node());
	}

	default Map<QN, QN> getSubjectMap(Iterable<? extends QN> objectSet) throws NullPointerException, IllegalArgumentException {
		return DQ.getSubjectMap(this.parent().context(), this.node(), objectSet);
	}

	default Map<QN, List<QN>> getSubjectSetMap() throws NullPointerException, IllegalArgumentException {
		return DQ.getSubjectSetMap(this.parent().context(), this.node());
	}

	default Map<QN, List<QN>> getSubjectSetMap(Iterable<? extends QN> objectSet) throws NullPointerException, IllegalArgumentException {
		return DQ.getSubjectSetMap(this.parent().context(), this.node(), objectSet);
	}

	default boolean setSubject(QN object, QN subject) throws NullPointerException, IllegalArgumentException {
		return this.setSubjectMap(singletonMap(object, subject));
	}

	default boolean setSubjectSet(QN object, Iterable<? extends QN> subjectSet) throws NullPointerException, IllegalArgumentException {
		return this.setSubjectSetMap(singletonMap(object, subjectSet));
	}

	default boolean setSubjectMap(Map<? extends QN, ? extends QN> objectSubjectMap) {
		var model = this.parent();
		var history = model.history();
		return DQ.setSubjectMap(model.context(), this.node(), objectSubjectMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean setSubjectSetMap(Map<? extends QN, ? extends Iterable<? extends QN>> objectSubjectSetMap) {
		var model = this.parent();
		var history = model.history();
		return DQ.setSubjectSetMap(model.context(), this.node(), objectSubjectSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean putSubject(QN object, QN subject) throws NullPointerException, IllegalArgumentException {
		return this.putSubjectMap(singletonMap(object, subject));
	}

	default boolean putSubjectSet(QN object, Iterable<? extends QN> subjectSet) throws NullPointerException, IllegalArgumentException {
		return this.putSubjectSetMap(singletonMap(object, subjectSet));
	}

	default boolean putSubjectMap(Map<? extends QN, ? extends QN> objectSubjectMap) {
		var model = this.parent();
		var history = model.history();
		return DQ.putSubjectMap(model.context(), this.node(), objectSubjectMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean putSubjectSetMap(Map<? extends QN, ? extends Iterable<? extends QN>> objectSubjectSetMap) {
		var model = this.parent();
		var history = model.history();
		return DQ.putSubjectSetMap(model.context(), this.node(), objectSubjectSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popSubject(QN object, QN subject) throws NullPointerException, IllegalArgumentException {
		return this.popSubjectMap(singletonMap(object, subject));
	}

	default boolean popSubjectSet(QN object, Iterable<? extends QN> subjectSet) throws NullPointerException, IllegalArgumentException {
		return this.popSubjectSetMap(singletonMap(object, subjectSet));
	}

	default boolean popSubjectMap(Map<? extends QN, ? extends QN> objectSubjectMap) {
		var model = this.parent();
		var history = model.history();
		return DQ.popSubjectMap(model.context(), this.node(), objectSubjectMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popSubjectSetMap(Map<? extends QN, ? extends Iterable<? extends QN>> objectSubjectSetMap) {
		var model = this.parent();
		var history = model.history();
		return DQ.popSubjectSetMap(model.context(), this.node(), objectSubjectSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default QN getObject(QN subject) throws NullPointerException, IllegalArgumentException {
		return this.getObjects(subject).first();
	}

	DLNSet getObjects(QN subject) throws NullPointerException, IllegalArgumentException;

	default List<QN> getObjectSet(QN subject) throws NullPointerException, IllegalArgumentException {
		return this.getObjects(subject).toList();
	}

	default Map<QN, QN> getObjectMap() throws NullPointerException, IllegalArgumentException {
		return DQ.getObjectMap(this.parent().context(), this.node());
	}

	default Map<QN, QN> getObjectMap(Iterable<? extends QN> subjectSet) throws NullPointerException, IllegalArgumentException {
		return DQ.getObjectMap(this.parent().context(), this.node(), subjectSet);
	}

	default Map<QN, List<QN>> getObjectSetMap() throws NullPointerException, IllegalArgumentException {
		return DQ.getObjectSetMap(this.parent().context(), this.node());
	}

	default Map<QN, List<QN>> getObjectSetMap(Iterable<? extends QN> subjectSet) throws NullPointerException, IllegalArgumentException {
		return DQ.getObjectSetMap(this.parent().context(), this.node(), subjectSet);
	}

	default boolean setObject(QN subject, QN object) throws NullPointerException, IllegalArgumentException {
		return this.setObjectMap(singletonMap(subject, object));
	}

	default boolean setObjectSet(QN subject, Iterable<? extends QN> objectSet) throws NullPointerException, IllegalArgumentException {
		return this.setObjectSetMap(singletonMap(subject, objectSet));
	}

	default boolean setObjectMap(Map<? extends QN, ? extends QN> subjectObjectMap) {
		var model = this.parent();
		var history = model.history();
		return DQ.setObjectMap(model.context(), this.node(), subjectObjectMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean setObjectSetMap(Map<? extends QN, ? extends Iterable<? extends QN>> subjectObjectSetMap) {
		var model = this.parent();
		var history = model.history();
		return DQ.setObjectSetMap(model.context(), this.node(), subjectObjectSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean putObject(QN subject, QN object) throws NullPointerException, IllegalArgumentException {
		return this.putObjectMap(singletonMap(subject, object));
	}

	default boolean putObjectSet(QN subject, Iterable<? extends QN> objectSet) throws NullPointerException, IllegalArgumentException {
		return this.putObjectSetMap(singletonMap(subject, objectSet));
	}

	default boolean putObjectMap(Map<? extends QN, ? extends QN> subjectObjectMap) {
		var model = this.parent();
		var history = model.history();
		return DQ.putObjectMap(model.context(), this.node(), subjectObjectMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean putObjectSetMap(Map<? extends QN, ? extends Iterable<? extends QN>> subjectObjectSetMap) {
		var model = this.parent();
		var history = model.history();
		return DQ.putObjectSetMap(model.context(), this.node(), subjectObjectSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popObject(QN subject, QN object) throws NullPointerException, IllegalArgumentException {
		return this.popObjectMap(singletonMap(subject, object));
	}

	default boolean popObjectSet(QN subject, Iterable<? extends QN> objectSet) throws NullPointerException, IllegalArgumentException {
		return this.popObjectSetMap(singletonMap(subject, objectSet));
	}

	default boolean popObjectMap(Map<? extends QN, ? extends QN> subjectObjectMap) {
		var model = this.parent();
		var history = model.history();
		return DQ.popObjectMap(model.context(), this.node(), subjectObjectMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popObjectSetMap(Map<? extends QN, ? extends Iterable<? extends QN>> subjectObjectSetMap) {
		var model = this.parent();
		var history = model.history();
		return DQ.popObjectSetMap(model.context(), this.node(), subjectObjectSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default Set2<QN> asSubjectSet(QN object) {
		return this.getSubjects(object).asNodeSet();
	}

	default Property3<QN> asSubjectProperty(QN object) {
		Objects.notNull(object);
		return propertyFrom(() -> this.getSubject(object), subject -> this.setSubject(object, subject));
	}

	default Set2<QN> asObjectSet(QN subject) throws NullPointerException, IllegalArgumentException {
		return this.getObjects(subject).asNodeSet();
	}

	default Property3<QN> asObjectProperty(QN subject) throws NullPointerException, IllegalArgumentException {
		Objects.notNull(subject);
		return propertyFrom(() -> this.getObject(subject), object -> this.setObject(subject, object));
	}

	/** Diese Klasse definiert die Handhabung eines referenzierten {@link QN Hyperknoten} beim Entfernen oder Duplizieren eines referenzierenden {@link QN
	 * Hyperknoten}. */
	enum Handling {

		/** Beim Entfernen des referenzierenden {@link QN Hyperknoten} sollen die {@link QE Hyperkanten} zu den referenzierten Hyperknoten ebenfalls entfernt
		 * werden. Beim Duplizieren sollen die Hyperkanten ignoriert werden. */
		Association,

		/** Beim Entfernen bzw. Duplizieren des referenzierenden {@link QN Hyperknoten} sollen die {@link QE Hyperkanten} zu den referenzierten Hyperknoten
		 * ebenfalls entfernt bzw. dupliziert werden. */
		Aggregation,

		/** Beim Entfernen bzw. Duplizieren des referenzierenden {@link QN Hyperknoten} sollen sowohl die {@link QE Hyperkanten} zu den referenzierten Hyperknoten
		 * als auch die referenzierten Hyperknoten entfernt bzw. dupliziert werden. */
		Composition;

		/** Diese Methode liefert den {@link Translators#optionalizedTranslator(Translator) optionalisierten} {@link Handling}-{@link Translator3}. */
		public static final Translator3<String, Handling> handlingTrans() {
			return trans == null ? trans = translatorFromEnum(Handling.class).optionalize() : trans;
		}

		private static Translator3<String, Handling> trans;

	}

	/** Diese Klasse definiert die Vielzahl von referenzierten {@link QN Hyperknoten} bezüglich eines referenzierenden {@link QN Hyperknoten}. */
	enum Multiplicity {

		/** Einem referenzierenden {@link QN Hyperknoten} soll höchstens ein referenzierter Hyperknoten zugeordnet werden können. */
		Multiplicity01,

		/** Einem referenzierenden {@link QN Hyperknoten} sollen beliebig viele referenzierte Hyperknoten zugeordnet werden können. */
		Multiplicity0N,

		/** Einem referenzierenden {@link QN Hyperknoten} soll genau ein referenzierter Hyperknoten zugeordnet werden können. */
		Multiplicity11,

		/** Einem referenzierenden {@link QN Hyperknoten} soll mindestens ein referenzierter Hyperknoten zugeordnet werden können. */
		Multiplicity1N;

		/** Diese Methode liefert den {@link Translators#optionalizedTranslator(Translator) optionalisierten} {@link Multiplicity}-{@link Translator3}. */
		public static final Translator3<String, Multiplicity> multiplicityTrans() {
			return trans == null ? trans = translatorFromEnum(Multiplicity.class).optionalize() : trans;
		}

		private static Translator3<String, Multiplicity> trans;

	}

}
