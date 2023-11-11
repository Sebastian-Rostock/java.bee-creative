package bee.creative.qs.ds;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import bee.creative.lang.Objects;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QS;
import bee.creative.util.Properties;
import bee.creative.util.Property2;
import bee.creative.util.Set2;
import bee.creative.util.Translator2;
import bee.creative.util.Translators;
import bee.creative.util.Translators.EnumTranslator;
import bee.creative.util.Translators.OptionalizedTranslator;

/** Diese Schnittstelle definiert ein Datenfeld (Domain-Link) als {@link #label() beschriftete} und {@link #idents() erkennbarer} {@link #node() Prädikatknoten}
 * mit Festlegungen zur Vielzahl, Handhabung und Typisierung der damit verbundenen Objekt- und Subjektknoten.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DL extends DE {

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für den {@link DT Datentyp} von {@link DL}. */
	String IDENT_IsLink = "DM:IsLink";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link #label()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithLabel = "DM:IsLinkWithLabel";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link #idents()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithIdent = "DM:IsLinkWithIdent";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link #sourceType()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithSourceType = "DM:IsLinkWithSourceType";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link #sourceHandling()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithSourceHandling = "DM:IsLinkWithSourceHandling";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link #sourceMultiplicity()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithSourceMultiplicity = "DM:IsLinkWithSourceMultiplicity";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link #targetType()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithTargetType = "DM:IsLinkWithTargetType";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link #targetHandling()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithTargetHandling = "DM:IsLinkWithTargetHandling";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link #targetMultiplicity()}-{@link DL Datenfeld}. */
	String IDENT_IsLinkWithTargetMultiplicity = "DM:IsLinkWithTargetMultiplicity";

	/** {@inheritDoc} Dieser wird als {@link QE#predicate() Prädikatknoten} der {@link #edges() Hyperkanten} verwendet. Als {@link QE#context() Kontextknoten}
	 * wird der des {@link #parent() Domänenmodells} verwendet. */
	@Override
	QN node();

	/** {@inheritDoc}
	 *
	 * @see #IDENT_IsLinkWithLabel */
	@Override
	default Property2<QN> label() {
		return this.parent().getLink(DL.IDENT_IsLinkWithLabel).asTargetProperty(this.node());
	}

	/** {@inheritDoc}
	 *
	 * @see #IDENT_IsLinkWithIdent */
	@Override
	default Set2<QN> idents() {
		return this.parent().getLink(DL.IDENT_IsLinkWithIdent).asTargetSet(this.node());
	}

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten {@link QE Hyperkanten} mit dem {@link QE#context() Kontextknoten} des {@link #parent()
	 * Domänenmodells} und dem {@link #node() Hyperknoten} dieses Datenfeldes als {@link QE#predicate() Prädikatknoten}.
	 *
	 * @return Hyperkanten mit Kontext- und Prädikatbindung. */
	default QESet edges() {
		return this.parent().edges().havingPredicate(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf den {@link DT#node() Hyperknoten} des {@link DT Datentyps} zulässiger {@link QE#subject() Subjektknoten}. Wenn dieser
	 * {@code null} ist, sind die Subjektknoten nicht eingeschränkt.
	 *
	 * @see DL#IDENT_IsLinkWithSourceType
	 * @see DL#asTargetProperty(QN)
	 * @return Subjektdatentypknoten. */
	default Property2<QN> sourceType() {
		return this.parent().getLink(DL.IDENT_IsLinkWithSourceType).asTargetProperty(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf den {@link DT Datentyp} zulässiger {@link QE#subject() Subjektknoten}. Wenn dieser {@code null} ist, sind die
	 * Subjektknoten nicht eingeschränkt.
	 *
	 * @see DM#typeTrans()
	 * @see DL#sourceType()
	 * @return Subjektdatentyp. */
	default Property2<DT> sourceTypeAsType() {
		return this.sourceType().translate(this.parent().typeTrans());
	}

	/** Diese Methode erlaubt Zugriff auf den {@link DT#node() Hyperknoten} der {@link Handling Handhabung} von {@link QE#subject() Subjektknoten} beim Entfernen
	 * oder Duplizieren eines {@link QE#object() Objektknoten}.
	 *
	 * @see DL#IDENT_IsLinkWithSourceHandling
	 * @return Subjekthandhabungsknoten. */
	default Property2<QN> sourceHandling() {
		return this.parent().getLink(DL.IDENT_IsLinkWithSourceHandling).asTargetProperty(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link Handling Handhabung} von {@link QE#subject() Subjektknoten} beim Entfernen oder Duplizieren eines
	 * {@link QE#object() Objektknoten}.
	 *
	 * @see DL#sourceHandling()
	 * @see QS#valueTrans()
	 * @see Handling#trans
	 * @return Subjekthandhabung. */
	default Property2<Handling> sourceHandlingAsEnum() {
		return this.sourceHandling().translate(this.owner().valueTrans()).translate(Handling.trans);
	}

	/** Diese Methode erlaubt Zugriff auf den {@link DT#node() Hyperknoten} der zulässigen {@link Multiplicity Vielzahl} von {@link QE#subject() Subjektknoten} je
	 * {@link QE#object() Objektknoten}.
	 *
	 * @see DL#IDENT_IsLinkWithSourceMultiplicity
	 * @return Subjektvielzahlknoten. */
	default Property2<QN> sourceMultiplicity() {
		return this.parent().getLink(DL.IDENT_IsLinkWithSourceMultiplicity).asTargetProperty(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die zulässige {@link Multiplicity Vielzahl} von {@link QE#subject() Subjektknoten} je {@link QE#object() Objektknoten}.
	 *
	 * @see DL#sourceMultiplicity()
	 * @see QS#valueTrans()
	 * @see Multiplicity#trans
	 * @return Subjektvielzahl. */
	default Property2<Multiplicity> sourceMultiplicityAsEnum() {
		return this.sourceMultiplicity().translate(this.owner().valueTrans()).translate(Multiplicity.trans);
	}

	/** Diese Methode erlaubt Zugriff auf den {@link DT#node() Hyperknoten} des {@link DT Datentyps} zulässiger {@link QE#object() Objektknoten}. Wenn dieser
	 * {@code null} ist, sind die Objektknoten nicht eingeschränkt.
	 *
	 * @see DL#IDENT_IsLinkWithTargetType
	 * @see DL#asTargetProperty(QN)
	 * @return Objektdatentypknoten. */
	default Property2<QN> targetType() {
		return this.parent().getLink(DL.IDENT_IsLinkWithTargetType).asTargetProperty(this.node());
	}

	default Property2<DT> targetTypeAsType() {
		return this.targetType().translate(this.parent().typeTrans());
	}

	default Property2<QN> targetHandling() {
		return this.parent().getLink(DL.IDENT_IsLinkWithTargetHandling).asTargetProperty(this.node());
	}

	default Property2<Handling> targetHandlingAsEnum() {
		return this.targetHandling().translate(this.owner().valueTrans()).translate(Handling.trans);
	}

	default Property2<QN> targetMultiplicity() {
		return this.parent().getLink(DL.IDENT_IsLinkWithTargetMultiplicity).asTargetProperty(this.node());
	}

	default Property2<Multiplicity> targetMultiplicityAsEnum() {
		return this.targetMultiplicity().translate(this.owner().valueTrans()).translate(Multiplicity.trans);
	}

	default QN getSource(QN target) throws NullPointerException, IllegalArgumentException {
		return this.getSources(target).first();
	}

	DNSet getSources(QN target) throws NullPointerException, IllegalArgumentException;

	default List<QN> getSourceSet(QN target) throws NullPointerException, IllegalArgumentException {
		return this.getSources(target).toList();
	}

	default Map<QN, QN> getSourceMap() throws NullPointerException, IllegalArgumentException {
		return DS.getSubjectMap(this.parent().context(), this.node());
	}

	default Map<QN, QN> getSourceMap(Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		return DS.getSubjectMap(this.parent().context(), this.node(), targetSet);
	}

	default Map<QN, List<QN>> getSourceSetMap() throws NullPointerException, IllegalArgumentException {
		return DS.getSubjectSetMap(this.parent().context(), this.node());
	}

	default Map<QN, List<QN>> getSourceSetMap(Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		return DS.getSubjectSetMap(this.parent().context(), this.node(), targetSet);
	}

	default boolean setSource(QN target, QN source) throws NullPointerException, IllegalArgumentException {
		return this.setSourceMap(Collections.singletonMap(target, source));
	}

	default boolean setSourceSet(QN target, Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		return this.setSourceSetMap(Collections.singletonMap(target, sourceSet));
	}

	default boolean setSourceMap(Map<? extends QN, ? extends QN> targetSourceMap) {
		var model = this.parent();
		var history = model.history();
		return DS.setSubjectMap(model.context(), this.node(), targetSourceMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean setSourceSetMap(Map<? extends QN, ? extends Iterable<? extends QN>> targetSourceSetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.setSubjectSetMap(model.context(), this.node(), targetSourceSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean putSource(QN target, QN source) throws NullPointerException, IllegalArgumentException {
		return this.putSourceMap(Collections.singletonMap(target, source));
	}

	default boolean putSourceSet(QN target, Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		return this.putSourceSetMap(Collections.singletonMap(target, sourceSet));
	}

	default boolean putSourceMap(Map<? extends QN, ? extends QN> targetSourceMap) {
		var model = this.parent();
		var history = model.history();
		return DS.putSubjectMap(model.context(), this.node(), targetSourceMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean putSourceSetMap(Map<? extends QN, ? extends Iterable<? extends QN>> targetSourceSetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.putSubjectSetMap(model.context(), this.node(), targetSourceSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popSource(QN target, QN source) throws NullPointerException, IllegalArgumentException {
		return this.popSourceMap(Collections.singletonMap(target, source));
	}

	default boolean popSourceSet(QN target, Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		return this.popSourceSetMap(Collections.singletonMap(target, sourceSet));
	}

	default boolean popSourceMap(Map<? extends QN, ? extends QN> targetSourceMap) {
		var model = this.parent();
		var history = model.history();
		return DS.popSubjectMap(model.context(), this.node(), targetSourceMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popSourceSetMap(Map<? extends QN, ? extends Iterable<? extends QN>> targetSourceSetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.popSubjectSetMap(model.context(), this.node(), targetSourceSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default QN getTarget(QN source) throws NullPointerException, IllegalArgumentException {
		return this.getTargets(source).first();
	}

	DNSet getTargets(QN source) throws NullPointerException, IllegalArgumentException;

	default List<QN> getTargetSet(QN source) throws NullPointerException, IllegalArgumentException {
		return this.getTargets(source).toList();
	}

	default Map<QN, QN> getTargetMap() throws NullPointerException, IllegalArgumentException {
		return DS.getObjectMap(this.parent().context(), this.node());
	}

	default Map<QN, QN> getTargetMap(Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		return DS.getObjectMap(this.parent().context(), this.node(), sourceSet);
	}

	default Map<QN, List<QN>> getTargetSetMap() throws NullPointerException, IllegalArgumentException {
		return DS.getObjectSetMap(this.parent().context(), this.node());
	}

	default Map<QN, List<QN>> getTargetSetMap(Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		return DS.getObjectSetMap(this.parent().context(), this.node(), sourceSet);
	}

	default boolean setTarget(QN source, QN target) throws NullPointerException, IllegalArgumentException {
		return this.setTargetMap(Collections.singletonMap(source, target));
	}

	default boolean setTargetSet(QN source, Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		return this.setTargetSetMap(Collections.singletonMap(source, targetSet));
	}

	default boolean setTargetMap(Map<? extends QN, ? extends QN> sourceTargetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.setObjectMap(model.context(), this.node(), sourceTargetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean setTargetSetMap(Map<? extends QN, ? extends Iterable<? extends QN>> sourceTargetSetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.setObjectSetMap(model.context(), this.node(), sourceTargetSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean putTarget(QN source, QN target) throws NullPointerException, IllegalArgumentException {
		return this.putTargetMap(Collections.singletonMap(source, target));
	}

	default boolean putTargetSet(QN source, Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		return this.putTargetSetMap(Collections.singletonMap(source, targetSet));
	}

	default boolean putTargetMap(Map<? extends QN, ? extends QN> sourceTargetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.putObjectMap(model.context(), this.node(), sourceTargetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean putTargetSetMap(Map<? extends QN, ? extends Iterable<? extends QN>> sourceTargetSetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.putObjectSetMap(model.context(), this.node(), sourceTargetSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popTarget(QN source, QN target) throws NullPointerException, IllegalArgumentException {
		return this.popTargetMap(Collections.singletonMap(source, target));
	}

	default boolean popTargetSet(QN source, Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		return this.popTargetSetMap(Collections.singletonMap(source, targetSet));
	}

	default boolean popTargetMap(Map<? extends QN, ? extends QN> sourceTargetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.popObjectMap(model.context(), this.node(), sourceTargetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popTargetSetMap(Map<? extends QN, ? extends Iterable<? extends QN>> sourceTargetSetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.popObjectSetMap(model.context(), this.node(), sourceTargetSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default Set2<QN> asSourceSet(QN target) {
		return this.getSources(target).asNodeSet();
	}

	default Property2<QN> asSourceProperty(QN target) {
		Objects.notNull(target);
		return Properties.from(() -> this.getSource(target), source -> this.setSource(target, source));
	}

	default Set2<QN> asTargetSet(QN source) throws NullPointerException, IllegalArgumentException {
		return this.getTargets(source).asNodeSet();
	}

	default Property2<QN> asTargetProperty(QN source) throws NullPointerException, IllegalArgumentException {
		Objects.notNull(source);
		return Properties.from(() -> this.getTarget(source), target -> this.setTarget(source, target));
	}

	/** Diese Klasse definiert die Handhabung eines referenzierten {@link QN Hyperknoten} beim Entfernen oder Duplizieren eines referenzierenden {@link QN
	 * Hyperknoten}. */
	enum Handling {

		/** Beim Entfernen des referenzierenden {@link QN Hyperknoten} sollen die {@link QE Hyperkanten} zu den referenzierten Hyperknoten ebenfalls entfernt
		 * werden. Beim Duplizieren sollen die Hyperkanten nicht dupliziert werden. */
		Association,

		/** Beim Entfernen bzw. Duplizieren des referenzierenden {@link QN Hyperknoten} sollen die {@link QE Hyperkanten} zu den referenzierten Hyperknoten
		 * ebenfalls entfernt bzw. dupliziert werden. */
		Aggregation,

		/** Beim Entfernen bzw. Duplizieren des referenzierenden {@link QN Hyperknoten} sollen sowohl die {@link QE Hyperkanten} zu den referenzierten Hyperknoten
		 * als auch die referenzierten Hyperknoten ebenfalls entfernt bzw. dupliziert werden. */
		Composition;

		/** Dieses Feld speichert den {@link OptionalizedTranslator optionalisierten} {@link Handling}-{@link EnumTranslator}. */
		public static final Translator2<String, Handling> trans = Translators.fromEnum(Handling.class).optionalize();

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

		/** Dieses Feld speichert den {@link OptionalizedTranslator optionalisierten} {@link Multiplicity}-{@link EnumTranslator}. */
		public static final Translator2<String, Multiplicity> trans = Translators.fromEnum(Multiplicity.class).optionalize();

	}

}
