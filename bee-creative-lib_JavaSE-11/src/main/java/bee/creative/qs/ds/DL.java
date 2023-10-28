package bee.creative.qs.ds;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import bee.creative.lang.Objects;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.util.Properties;
import bee.creative.util.Property2;
import bee.creative.util.Set2;
import bee.creative.util.Translator2;
import bee.creative.util.Translators;

/** Diese Schnittstelle definiert ein Datenfeld (Domain-Link) als {@link #node() Feldnoten} mit Bezug zu einem {@link #parent() Domänenmodell}, {@link #label()
 * Beschriftung}, {@link #idents() Erkennungsmerkmalen} sowie Hinweisen zu gewünschten Datentypen von Quell- und Zielknoten.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DL extends DE {

	/** {@inheritDoc} Dieser wird als {@link QE#predicate() Prädikatknoten} der {@link #edges() Hyperkanten} verwendet. Als {@link QE#context() Kontextknoten}
	 * wird der des {@link #parent() Domänenmodells} verwendet. */
	@Override
	QN node();

 

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten {@link QE Hyperkanten} mit dem {@link QE#context() Kontextknoten} des {@link #parent()
	 * Domänenmodells} und dem {@link #node() Feldknoten} dieses Datenfeldes als {@link QE#predicate() Prädikatknoten}.
	 *
	 * @return Hyperkanten mit Kontext- und Prädikatbindung. */
	default QESet edges() {
		return this.parent().edges().havingPredicate(this.node());
	}

	/** Diese Methode liefert die {@link DT#node() Typknoten} der erwünschten {@link DT Datentypen} der {@link QE#subject() Quell- bzw. Subjektknoten}.
	 *
	 * @see DM#IDENT_IsLinkWithSourceType
	 * @return Quelltypknoten. */
	default Set2<QN> sourceTypes() {
		return this.parent().getLink(DM.IDENT_IsLinkWithSourceType).getTargets(this.node()).asNodeSet();
	}

	/** Diese Methode liefert die erwünschten {@link DT Datentypen} von {@link QE#subject() Quell- bzw. Subjektknoten}.
	 *
	 * @return Quelldatentypen. */
	default Set2<DT> sourceTypesAsTypes() {
		return this.sourceTypes().translate(this.parent().typeTrans());
	}

	/** Diese Methode liefert die Textknoten der erwünschten {@link FOLLOWING Klonbarkeit} vom {@link QE#subject() Quell- bzw. Subjektknoten}.
	 *
	 * @see DM#IDENT_IsLinkWithSourceClonability
	 * @return Quellklonbarkeitstextknoten. */
	default Property2<QN> sourceClonability() {
		return this.parent().getLink(DM.IDENT_IsLinkWithSourceClonability).asTargetProp(this.node());
	}

	/** Diese Methode liefert die erwünschte {@link FOLLOWING Klonbarkeit} vom {@link QE#subject() Quell- bzw. Subjektknoten}.
	 *
	 * @return Quellklonbarkeit. */
	default Property2<FOLLOWING> sourceClonabilityAsEnum() {
		return this.sourceClonabilityAsString().translate(FOLLOWING.trans);
	}

	/** Diese Methode liefert den Textwert der erwünschten {@link FOLLOWING Klonbarkeit} vom {@link QE#subject() Quell- bzw. Subjektknoten}.
	 *
	 * @see #sourceClonability()
	 * @return Quellklonbarkeitstextwert. */
	default Property2<String> sourceClonabilityAsString() {
		return this.sourceClonability().translate(this.owner().valueTrans());
	}

	default Property2<QN> sourceMultiplicity() {
		return this.parent().getLink(DM.IDENT_IsLinkWithSourceMultiplicity).asTargetProp(this.node());
	}

	default Property2<COUNTING> sourceMultiplicityAsEnum() {
		return this.sourceMultiplicityAsString().translate(COUNTING.trans);
	}

	default Property2<String> sourceMultiplicityAsString() {
		return this.sourceMultiplicity().translate(this.owner().valueTrans());
	}

	/** Diese Methode liefert die {@link DT#node() Typknoten} der erwünschten {@link DT Datentypen} von {@link QE#object() Objektknoten}.
	 *
	 * @return Objektdatentypknoten. */
	default Set2<QN> targetTypes() {
		return this.parent().getLink(DM.IDENT_IsLinkWithTargetType).getTargets(this.node()).asNodeSet();
	}

	/** Diese Methode liefert die erwünschten {@link DT Datentypen} von {@link QE#object() Objektknoten}.
	 *
	 * @return Objektdatentypen. */
	default Set2<DT> targetTypesAsTypes() {
		return this.targetTypes().translate(this.parent().typeTrans());
	}

	default Property2<QN> targetClonability() {
		return this.parent().getLink(DM.IDENT_IsLinkWithTargetClonability).asTargetProp(this.node());
	}

	default Property2<FOLLOWING> targetClonabilityAsEnum() {
		return this.targetClonabilityAsString().translate(FOLLOWING.trans);
	}

	default Property2<String> targetClonabilityAsString() {
		return this.targetClonability().translate(this.owner().valueTrans());
	}

	default Property2<QN> targetMultiplicity() {
		return this.parent().getLink(DM.IDENT_IsLinkWithTargetMultiplicity).asTargetProp(this.node());
	}

	default Property2<COUNTING> targetMultiplicityAsEnum() {
		return this.targetMultiplicityAsString().translate(COUNTING.trans);
	}

	default Property2<String> targetMultiplicityAsString() {
		return this.targetMultiplicity().translate(this.owner().valueTrans());
	}

	default QN getSource(QN target) throws NullPointerException, IllegalArgumentException {
		return this.getSources(target).first();
	}

	DNSet getSources(QN target) throws NullPointerException, IllegalArgumentException;

	default Set2<QN> getSourceSet(QN target) throws NullPointerException, IllegalArgumentException {
		return this.getSources(target).asNodeSet();
	}

	default Map<QN, QN> getSourceMap(Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		return DS.getSubjectMap(this.parent().context(), this.node(), targetSet);
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

	default Set2<QN> getTargetSet(QN source) throws NullPointerException, IllegalArgumentException {
		return this.getTargets(source).asNodeSet();
	}

	default Map<QN, QN> getTargetMap(Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		return DS.getObjectMap(this.parent().context(), this.node(), sourceSet);
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

	default Property2<QN> asSourceProp(QN target) {
		Objects.notNull(target);
		return Properties.from(() -> this.getSource(target), source -> this.setSource(target, source));
	}

	default Set2<QN> asTargetSet(QN source) throws NullPointerException, IllegalArgumentException {
		return this.getTargets(source).asNodeSet();
	}

	default Property2<QN> asTargetProp(QN source) throws NullPointerException, IllegalArgumentException {
		Objects.notNull(source);
		return Properties.from(() -> this.getTarget(source), target -> this.setTarget(source, target));
	}

	enum COUNTING {
	
		M01, M0N, M11, M1N;
	
		static final Translator2<String, COUNTING> trans = Translators.fromEnum(COUNTING.class).optionalize();
	
	}

	enum FOLLOWING {

		Ignored, Enabled, Cascade;

		static final Translator2<String, FOLLOWING> trans = Translators.fromEnum(FOLLOWING.class).optionalize();

	}

}
