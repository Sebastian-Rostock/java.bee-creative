package bee.creative.qs.ds;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import bee.creative.lang.Objects;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.util.AbstractTranslator;
import bee.creative.util.Field2;
import bee.creative.util.Fields;
import bee.creative.util.Getter;
import bee.creative.util.Property2;
import bee.creative.util.Set2;
import bee.creative.util.Translator2;
import bee.creative.util.Translators;

/** Diese Schnittstelle definiert ein Datenfeld (Domain-Link) als {@link #node() Feldnoten} mit Bezug zu einem {@link #parent() Datenmodell}, {@link #label()
 * Beschriftung}, {@link #idents() Erkennungsmerkmalen} sowie Hinweisen zu gewünschten Datentypen von Quell- und Zielknoten.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DL extends DE {

	/** Diese Methode liefert einen {@link Translator2}, der einen {@link QN Hyperknoten} bidirektional in ein {@link DL Datenfeld} übersetzt.
	 *
	 * @param nodeAsLink Methode zur Übersetzung eines {@link DL#node() Feldknoten} in das zugehörige {@link DL Datenfeld}.
	 * @return Hyperknoten-Datenfeld-Übersetzer. */
	static Translator2<QN, DL> linkTrans(Getter<QN, DL> nodeAsLink) {
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
				return object != null ? nodeAsLink.get((QN)object) : null;
			}

			@Override
			public QN toSource(Object object) throws ClassCastException, IllegalArgumentException {
				return object != null ? ((DL)object).node() : null;
			}

		};
	}

	/** {@inheritDoc} Dieser wird als {@link QE#predicate() Prädikatknoten} der {@link #edges() Hyperkanten} verwendet. Als {@link QE#context() Kontextknoten}
	 * wird der des {@link #parent() Datenmodells} verwendet. */
	@Override
	QN node();

	/** {@inheritDoc}
	 *
	 * @see DM#LINK_IDENT_IsLinkWithLabel */
	@Override
	default Property2<QN> label() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithLabel).getTargets(this.node()).asNode();
	}

	/** {@inheritDoc}
	 *
	 * @see DM#LINK_IDENT_IsLinkWithLabel */
	@Override
	default Property2<String> labelAsString() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithLabel).getTargets(this.node()).asValue();
	}

	/** {@inheritDoc}
	 *
	 * @see DM#LINK_IDENT_IsLinkWithIdent */
	@Override
	default Set2<QN> idents() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithIdent).getTargets(this.node()).asNodeSet();
	}

	/** {@inheritDoc}
	 *
	 * @see DM#LINK_IDENT_IsLinkWithIdent */
	@Override
	default Set2<String> identsAsStrings() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithIdent).getTargets(this.node()).asValueSet();
	}

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten {@link QE Hyperkanten} mit dem {@link QE#context() Kontextknoten} des {@link #parent()
	 * Datenmodells} und dem {@link #node() Feldknoten} dieses Datenfeldes als {@link QE#predicate() Prädikatknoten}.
	 *
	 * @return Hyperkanten mit Kontext- und Prädikatbindung. */
	default QESet edges() {
		return this.parent().edges().havingPredicate(this.node());
	}

	/** Diese Methode liefert die {@link DT#node() Typknoten} der erwünschten {@link DT Datentypen} der {@link QE#subject() Quell- bzw. Subjektknoten}.
	 *
	 * @see DM#LINK_IDENT_IsLinkWithSourceType
	 * @return Quelltypknoten. */
	default Set2<QN> sourceTypes() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithSourceType).getTargets(this.node()).asNodeSet();
	}

	/** Diese Methode liefert die erwünschten {@link DT Datentypen} von {@link QE#subject() Quell- bzw. Subjektknoten}.
	 *
	 * @return Quelldatentypen. */
	default Set2<DT> sourceTypesAsTypes() {
		return this.parent().asTypes(this.sourceTypes());
	}

	/** Diese Methode liefert die Textknoten der erwünschten {@link CLONABILITY Klonbarkeit} vom {@link QE#subject() Quell- bzw. Subjektknoten}.
	 *
	 * @see DM#LINK_IDENT_IsLinkWithSourceClonability
	 * @return Quellklonbarkeitstextknoten. */
	default Property2<QN> sourceClonability() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithSourceClonability).asTargetProperty(this.node());
	}

	/** Diese Methode liefert die erwünschte {@link CLONABILITY Klonbarkeit} vom {@link QE#subject() Quell- bzw. Subjektknoten}.
	 *
	 * @return Quellklonbarkeit. */
	default Property2<CLONABILITY> sourceClonabilityAsEnum() {
		return this.sourceClonabilityAsString().translate(CLONABILITY.trans);
	}

	/** Diese Methode liefert den Textwert der erwünschten {@link CLONABILITY Klonbarkeit} vom {@link QE#subject() Quell- bzw. Subjektknoten}.
	 *
	 * @see #sourceClonability()
	 * @return Quellklonbarkeitstextwert. */
	default Property2<String> sourceClonabilityAsString() {
		return this.parent().asString(this.sourceClonability());
	}

	default Property2<QN> sourceMultiplicity() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithSourceMultiplicity).asTargetField().toProperty(this.node());
	}

	default Property2<MULTIPLICITY> sourceMultiplicityAsEnum() {
		return this.sourceMultiplicityAsString().translate(MULTIPLICITY.trans);
	}

	default Property2<String> sourceMultiplicityAsString() {
		return this.parent().asString(this.sourceMultiplicity());
	}

	/** Diese Methode liefert die {@link DT#node() Typknoten} der erwünschten {@link DT Datentypen} von {@link QE#object() Objektknoten}.
	 *
	 * @return Objektdatentypknoten. */
	default Set2<QN> targetTypes() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithTargetType).getTargets(this.node()).asNodeSet();
	}

	/** Diese Methode liefert die erwünschten {@link DT Datentypen} von {@link QE#object() Objektknoten}.
	 *
	 * @return Objektdatentypen. */
	default Set2<DT> targetTypesAsTypes() {
		return this.parent().asTypes(this.targetTypes());
	}

	default Property2<QN> targetClonability() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithTargetClonability).asTargetField().toProperty(this.node());
	}

	default Property2<CLONABILITY> targetClonabilityAsEnum() {
		return this.targetClonabilityAsString().translate(CLONABILITY.trans);
	}

	default Property2<String> targetClonabilityAsString() {
		return this.parent().asString(this.targetClonability());
	}

	default Property2<QN> targetMultiplicity() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithTargetMultiplicity).asTargetField().toProperty(this.node());
	}

	default Property2<MULTIPLICITY> targetMultiplicityAsEnum() {
		return this.targetMultiplicityAsString().translate(MULTIPLICITY.trans);
	}

	default Property2<String> targetMultiplicityAsString() {
		return this.parent().asString(this.targetMultiplicity());
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

	default boolean setSourceMap(final Map<? extends QN, ? extends QN> targetSourceMap) {
		var model = this.parent();
		var history = model.history();
		return DS.setSubjectMap(model.context(), this.node(), targetSourceMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean setSourceSetMap(final Map<? extends QN, ? extends Iterable<? extends QN>> targetSourceSetMap) {
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

	default boolean putSourceMap(final Map<? extends QN, ? extends QN> targetSourceMap) {
		var model = this.parent();
		var history = model.history();
		return DS.putSubjectMap(model.context(), this.node(), targetSourceMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean putSourceSetMap(final Map<? extends QN, ? extends Iterable<? extends QN>> targetSourceSetMap) {
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

	default boolean popSourceMap(final Map<? extends QN, ? extends QN> targetSourceMap) {
		var model = this.parent();
		var history = model.history();
		return DS.popSubjectMap(model.context(), this.node(), targetSourceMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popSourceSetMap(final Map<? extends QN, ? extends Iterable<? extends QN>> targetSourceSetMap) {
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

	default boolean setTargetMap(final Map<? extends QN, ? extends QN> sourceTargetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.setObjectMap(model.context(), this.node(), sourceTargetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean setTargetSetMap(final Map<? extends QN, ? extends Iterable<? extends QN>> sourceTargetSetMap) {
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

	default boolean putTargetMap(final Map<? extends QN, ? extends QN> sourceTargetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.putObjectMap(model.context(), this.node(), sourceTargetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean putTargetSetMap(final Map<? extends QN, ? extends Iterable<? extends QN>> sourceTargetSetMap) {
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

	default boolean popTargetMap(final Map<? extends QN, ? extends QN> sourceTargetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.popObjectMap(model.context(), this.node(), sourceTargetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default boolean popTargetSetMap(final Map<? extends QN, ? extends Iterable<? extends QN>> sourceTargetSetMap) {
		var model = this.parent();
		var history = model.history();
		return DS.popObjectSetMap(model.context(), this.node(), sourceTargetSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default Field2<QN, QN> asSourceField() {
		return Fields.from(this::getSource, this::setSource);
	}

	default Property2<QN> asSourceProperty(QN target) {
		return this.asSourceField().toProperty(Objects.notNull(target));
	}

	default Field2<QN, Set<QN>> asSourceSetField() {
		return Fields.from(this::getSourceSet, this::setSourceSet);
	}

	default Property2<Set<QN>> asSourceSetProperty(QN target) {
		return this.asSourceSetField().toProperty(Objects.notNull(target));
	}

	default Field2<QN, QN> asTargetField() {
		return Fields.from(this::getTarget, this::setTarget);
	}

	default Property2<QN> asTargetProperty(QN source) {
		return this.asTargetField().toProperty(Objects.notNull(source));
	}

	default Field2<QN, Set<QN>> asTargetSetField() {
		return Fields.from(this::getTargetSet, this::setTargetSet);
	}

	enum CLONABILITY {

		Disabled, Enabled, Cascade;

		static final Translator2<String, CLONABILITY> trans = Translators.from(CLONABILITY.class);

	}

	enum MULTIPLICITY {

		M01, M0N, M11, M1N;

		static final Translator2<String, MULTIPLICITY> trans = Translators.from(MULTIPLICITY.class);

	}

}
