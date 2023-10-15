package bee.creative.qs.ds;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.util.AbstractTranslator;
import bee.creative.util.Field2;
import bee.creative.util.Fields;
import bee.creative.util.Getter;
import bee.creative.util.Getter3;
import bee.creative.util.Getters;
import bee.creative.util.Properties;
import bee.creative.util.Property2;
import bee.creative.util.ProxySet;
import bee.creative.util.Set2;
import bee.creative.util.Setter3;
import bee.creative.util.Setters;
import bee.creative.util.Translator2;
import bee.creative.util.Translators;

/** Diese Schnittstelle definiert ein Datenfeld (Domain-Link) als {@link #node() Feldnoten} mit Bezug zu einem {@link #parent() Datenmodell}, {@link #label()
 * Beschriftung}, {@link #idents() Erkennungsmerkmalen} sowie Hinweisen zu gewünschten Datentypen von Quell- und Zielknoten.
 * 
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DL extends DE {

	/** Diese Methode liefert einen {@link Translator2}, der einen {@link QN Hyperknoten} bidirektional in ein {@link DL Datenfeld} übersetzt.
	 *
	 * @param nodeAsLink Methode liefet das {@link DL Datenfeld} mit dem gegebenen {@link DT#node() Feldknoten}.
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

	/** Diese Methode liefert die Mengensicht auf alle gespeicherten {@link QE Hyperkanten} mit dem {@link QE#context() Kontextknoten} des {@link #parent()
	 * Datenmodells} und dem {@link #node() Feldknoten} dieses Datenfeldes als {@link QE#predicate() Prädikatknoten}.
	 *
	 * @return Hyperkanten mit Kontext- und Prädikatbindung. */
	default QESet edges() {
		return this.parent().edges().havingPredicate(this.node());
	}

	@Override
	default Property2<QN> label() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithLabel).asTargetField().toProperty(this.node());
	}

	@Override
	default Set2<QN> idents() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithIdent).getTargetProxy(this.node());
	}

	/** Diese Methode liefert die {@link DT#node() Typknoten} der erwünschten {@link DT Datentypen} von {@link QE#subject() Subjektknoten}.
	 *
	 * @return Subjektdatentypknoten. */
	default Set2<QN> sourceTypes() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithSourceType).getTargetProxy(this.node());
	}

	/** Diese Methode liefert die erwünschten {@link DT Datentypen} von {@link QE#subject() Subjektknoten}.
	 *
	 * @return Subjektdatentypen. */
	default Set2<DT> sourceTypesAsTypes() {
		return this.sourceTypes().translate(this.parent().typeTrans());
	}

	/** Diese Methode liefert die Textknoten der erwünschten {@link CLONABILITY Klonbarkeit} vom {@link QE#subject() Subjektknoten}.
	 *
	 * @return Subjektklonbarkeit. */
	default Property2<QN> sourceClonability() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithSourceClonability).asTargetField().toProperty(this.node());
	}

	default Property2<CLONABILITY> sourceClonabilityAsEnum() {
		return this.sourceClonabilityAsString().translate(CLONABILITY.trans);
	}

	default Property2<String> sourceClonabilityAsString() {
		return this.sourceClonability().translate(this.owner().valueTrans());
	}

	default Property2<QN> sourceMultiplicity() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithSourceMultiplicity).asTargetField().toProperty(this.node());
	}

	default Property2<MULTIPLICITY> sourceMultiplicityAsEnum() {
		return this.sourceMultiplicityAsString().translate(MULTIPLICITY.trans);
	}

	default Property2<String> sourceMultiplicityAsString() {
		return this.sourceMultiplicity().translate(this.owner().valueTrans());
	}

	/** Diese Methode liefert die {@link DT#node() Typknoten} der erwünschten {@link DT Datentypen} von {@link QE#object() Objektknoten}.
	 *
	 * @return Objektdatentypknoten. */
	default Set2<QN> targetTypes() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithTargetType).getTargetProxy(this.node());
	}

	/** Diese Methode liefert die erwünschten {@link DT Datentypen} von {@link QE#object() Objektknoten}.
	 *
	 * @return Objektdatentypen. */
	default Set2<DT> targetTypesAsTypes() {
		return this.targetTypes().translate(this.parent().typeTrans());
	}

	default Property2<QN> targetClonability() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithTargetClonability).asTargetField().toProperty(this.node());
	}

	default Property2<CLONABILITY> targetClonabilityAsEnum() {
		return this.targetClonabilityAsString().translate(CLONABILITY.trans);
	}

	default Property2<String> targetClonabilityAsString() {
		return this.targetClonability().translate(this.owner().valueTrans());
	}

	default Property2<QN> targetMultiplicity() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithTargetMultiplicity).asTargetField().toProperty(this.node());
	}

	default Property2<MULTIPLICITY> targetMultiplicityAsEnum() {
		return this.targetMultiplicityAsString().translate(MULTIPLICITY.trans);
	}

	default Property2<String> targetMultiplicityAsString() {
		return this.targetMultiplicity().translate(this.owner().valueTrans());
	}

	default Field2<QN, QN> asSourceField() {
		return Fields.from(this.asSourceGetter(), this.asSourceSetter());
	}

	default Getter3<QN, QN> asSourceGetter() {
		return Getters.from(this::getSource);
	}

	default Setter3<QN, QN> asSourceSetter() {
		return Setters.from(this::setSource);
	}

	default Setter3<QN, QN> asSourcePutter() {
		return Setters.from(this::putSource);
	}

	default Setter3<QN, QN> asSourcePopper() {
		return Setters.from(this::popSource);
	}

	default Field2<QN, QNSet> asSourceSetField() {
		return Fields.from(this.asSourceSetGetter(), this.asSourceSetSetter());
	}

	default Getter3<QN, QNSet> asSourceSetGetter() {
		return Getters.from(this::getSourceSet);
	}

	default Setter3<QN, Iterable<? extends QN>> asSourceSetSetter() {
		return Setters.from(this::setSourceSet);
	}

	default Setter3<QN, Iterable<? extends QN>> asSourceSetPutter() {
		return Setters.from(this::putSourceSet);
	}

	default Setter3<QN, Iterable<? extends QN>> asSourceSetPopper() {
		return Setters.from(this::popSourceSet);
	}

	default Field2<QN, QN> asTargetField() {
		return Fields.from(this.asTargetGetter(), this.asTargetSetter());
	}

	default Getter3<QN, QN> asTargetGetter() {
		return Getters.from(this::getTarget);
	}

	default Setter3<QN, QN> asTargetSetter() {
		return Setters.from(this::setTarget);
	}

	default Setter3<QN, QN> asTargetPutter() {
		return Setters.from(this::putTarget);
	}

	default Setter3<QN, QN> asTargetPopper() {
		return Setters.from(this::popTarget);
	}

	default Field2<QN, QNSet> asTargetSetField() {
		return Fields.from(this.asTargetSetGetter(), this.asTargetSetSetter());
	}

	default Getter3<QN, QNSet> asTargetSetGetter() {
		return Getters.from(this::getTargetSet);
	}

	default Setter3<QN, Iterable<? extends QN>> asTargetSetSetter() {
		return Setters.from(this::setTargetSet);
	}

	default Setter3<QN, Iterable<? extends QN>> asTargetSetPutter() {
		return Setters.from(this::putTargetSet);
	}

	default Setter3<QN, Iterable<? extends QN>> asTargetSetPopper() {
		return Setters.from(this::popTargetSet);
	}

	default QN getSource(QN target) throws NullPointerException, IllegalArgumentException {
		return this.getSourceSet(target).first();
	}

	QNSetL getSourceSet(QN target) throws NullPointerException, IllegalArgumentException;

	default Map<QN, QN> getSourceMap(Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		return DS.getSubjectMap(this.parent().context(), this.node(), targetSet);
	}

	default Map<QN, List<QN>> getSourceSetMap(Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		return DS.getSubjectSetMap(this.parent().context(), this.node(), targetSet);
	}

	default Set2<QN> getSourceProxy(QN target) {
		return ProxySet.from(Properties.from(() -> this.getSourceSet(target).toSet(), sourceSet -> this.setSourceSet(target, sourceSet)));
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
		return this.getTargetSet(source).first();
	}

	QNSetL getTargetSet(QN source) throws NullPointerException, IllegalArgumentException;

	default Map<QN, QN> getTargetMap(Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		return DS.getObjectMap(this.parent().context(), this.node(), sourceSet);
	}

	default Map<QN, List<QN>> getTargetSetMap(Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		return DS.getObjectSetMap(this.parent().context(), this.node(), sourceSet);
	}

	default Set2<QN> getTargetProxy(QN source) {
		return ProxySet.from(Properties.from(() -> this.getTargetSet(source).toSet(), targetSet -> this.setTargetSet(source, targetSet)));
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

	enum CLONABILITY {

		Disabled, Enabled, Cascade;

		static final Translator2<String, CLONABILITY> trans = Translators.from(CLONABILITY.class);

	}

	enum MULTIPLICITY {

		M01, M0N, M11, M1N;

		static final Translator2<String, MULTIPLICITY> trans = Translators.from(MULTIPLICITY.class);

	}

}
