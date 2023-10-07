package bee.creative.qs.ds;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.util.Field2;
import bee.creative.util.Fields;
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

public interface DL extends DE {

	/** Dieses Feld speichert den Textwert eines {@link #idents() Erkennungsknoten} für das {@link #sources()}-{@link DL Datenfeld}. */
	String IDENT_LINK_HAS_SOURCE = "DM:LINK_HAS_SOURCE";

	/** Dieses Feld speichert den Textwert eines {@link #idents() Erkennungsknoten} für das {@link #targets()}-{@link DL Datenfeld}. */
	String IDENT_LINK_HAS_TARGET = "DM:LINK_HAS_TARGET";

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

	default QNSet getSourceSet(QN target) throws NullPointerException, IllegalArgumentException {
		return DS.getSubjectSet(this.model().context(), this.node(), target);
	}

	default Map<QN, QN> getSourceMap(Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		return DS.getSubjectMap(this.model().context(), this.node(), targetSet);
	}

	default Map<QN, List<QN>> getSourceSetMap(Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		return DS.getSubjectSetMap(this.model().context(), this.node(), targetSet);
	}

	default Set2<QN> getSourceProxy(QN target) {
		return ProxySet.from(Properties.from(() -> this.getSourceSet(target).toSet(), sourceSet -> this.setSourceSet(target, sourceSet)));
	}

	default void setSource(QN target, QN source) throws NullPointerException, IllegalArgumentException {
		this.setSourceMap(Collections.singletonMap(target, source));
	}

	default void setSourceSet(QN target, Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		this.setSourceSetMap(Collections.singletonMap(target, sourceSet));
	}

	default void setSourceMap(final Map<? extends QN, ? extends QN> targetSourceMap) {
		var model = this.model();
		var history = model.history();
		DS.setSubjectMap(model.context(), this.node(), targetSourceMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default void setSourceSetMap(final Map<? extends QN, ? extends Iterable<? extends QN>> targetSourceSetMap) {
		var model = this.model();
		var history = model.history();
		DS.setSubjectSetMap(model.context(), this.node(), targetSourceSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default void putSource(QN target, QN source) throws NullPointerException, IllegalArgumentException {
		this.putSourceMap(Collections.singletonMap(target, source));
	}

	default void putSourceSet(QN target, Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		this.setSourceSetMap(Collections.singletonMap(target, sourceSet));
	}

	default void putSourceMap(final Map<? extends QN, ? extends QN> targetSourceMap) {
		var model = this.model();
		var history = model.history();
		DS.putSubjectMap(model.context(), this.node(), targetSourceMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default void putSourceSetMap(final Map<? extends QN, ? extends Iterable<? extends QN>> targetSourceSetMap) {
		var model = this.model();
		var history = model.history();
		DS.putSubjectSetMap(model.context(), this.node(), targetSourceSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default void popSource(QN target, QN source) throws NullPointerException, IllegalArgumentException {
		this.popSourceMap(Collections.singletonMap(target, source));
	}

	default void popSourceSet(QN target, Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		this.popSourceSetMap(Collections.singletonMap(target, sourceSet));
	}

	default void popSourceMap(final Map<? extends QN, ? extends QN> targetSourceMap) {
		var model = this.model();
		var history = model.history();
		DS.popSubjectMap(model.context(), this.node(), targetSourceMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default void popSourceSetMap(final Map<? extends QN, ? extends Iterable<? extends QN>> targetSourceSetMap) {
		var model = this.model();
		var history = model.history();
		DS.popSubjectSetMap(model.context(), this.node(), targetSourceSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default QN getTarget(QN source) throws NullPointerException, IllegalArgumentException {
		return this.getTargetSet(source).first();
	}

	default QNSet getTargetSet(QN source) throws NullPointerException, IllegalArgumentException {
		return DS.getObjectSet(this.model().context(), this.node(), source);
	}

	default Map<QN, QN> getTargetMap(Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		return DS.getObjectMap(this.model().context(), this.node(), sourceSet);
	}

	default Map<QN, List<QN>> getTargetSetMap(Iterable<? extends QN> sourceSet) throws NullPointerException, IllegalArgumentException {
		return DS.getObjectSetMap(this.model().context(), this.node(), sourceSet);
	}

	default Set2<QN> getTargetProxy(QN source) {
		return ProxySet.from(Properties.from(() -> this.getTargetSet(source).toSet(), targetSet -> this.setTargetSet(source, targetSet)));
	}

	default void setTarget(QN source, QN target) throws NullPointerException, IllegalArgumentException {
		this.setTargetMap(Collections.singletonMap(source, target));
	}

	default void setTargetSet(QN source, Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		this.setTargetSetMap(Collections.singletonMap(source, targetSet));
	}

	default void setTargetMap(final Map<? extends QN, ? extends QN> sourceTargetMap) {
		var model = this.model();
		var history = model.history();
		DS.setObjectMap(model.context(), this.node(), sourceTargetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default void setTargetSetMap(final Map<? extends QN, ? extends Iterable<? extends QN>> sourceTargetSetMap) {
		var model = this.model();
		var history = model.history();
		DS.setObjectSetMap(model.context(), this.node(), sourceTargetSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default void putTarget(QN source, QN target) throws NullPointerException, IllegalArgumentException {
		this.putTargetMap(Collections.singletonMap(source, target));
	}

	default void putTargetSet(QN source, Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		this.setTargetSetMap(Collections.singletonMap(source, targetSet));
	}

	default void putTargetMap(final Map<? extends QN, ? extends QN> sourceTargetMap) {
		var model = this.model();
		var history = model.history();
		DS.putObjectMap(model.context(), this.node(), sourceTargetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default void putTargetSetMap(final Map<? extends QN, ? extends Iterable<? extends QN>> sourceTargetSetMap) {
		var model = this.model();
		var history = model.history();
		DS.putObjectSetMap(model.context(), this.node(), sourceTargetSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default void popTarget(QN source, QN target) throws NullPointerException, IllegalArgumentException {
		this.popTargetMap(Collections.singletonMap(source, target));
	}

	default void popTargetSet(QN source, Iterable<? extends QN> targetSet) throws NullPointerException, IllegalArgumentException {
		this.popTargetSetMap(Collections.singletonMap(source, targetSet));
	}

	default void popTargetMap(final Map<? extends QN, ? extends QN> sourceTargetMap) {
		var model = this.model();
		var history = model.history();
		DS.popObjectMap(model.context(), this.node(), sourceTargetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	default void popTargetSetMap(final Map<? extends QN, ? extends Iterable<? extends QN>> sourceTargetSetMap) {
		var model = this.model();
		var history = model.history();
		DS.popObjectSetMap(model.context(), this.node(), sourceTargetSetMap, //
			history != null ? history.putContext() : null, history != null ? history.popContext() : null);
	}

	boolean getAllowMultipleObjects();

	boolean getAllowMultipleSujects();

	boolean getCloneEdgeWithObject();

	boolean getCloneEdgeWithSubject();

	boolean getCloneNodeWithObject();

	boolean getCloneNodeWithSubject();

	/** {@inheritDoc} Dieser wird als {@link QE#predicate() Prädikatknoten} von {@link QE Hyperkanten} verwendet. Als {@link QE#context() Kontextknoten} wird der
	 * des {@link #model() Datenmodells} verwendet. */
	@Override
	default QN node() {
		return null;
	}

	default QESet edges() {
		return this.model().edges().havingPredicate(this.node());
	}

	/** Diese Methode liefert die {@link DT#node() Typknoten} der erwünschten {@link DT Datentypen} von {@link QE#subject() Subjektknoten}.
	 *
	 * @return Subjektdatentypknoten. */
	default Set2<QN> sources() {
		return this.model().linkSourceLink().getTargetProxy(this.node());
	}

	/** Diese Methode liefert die erwünschten {@link DT Datentypen} von {@link QE#subject() Subjektknoten}.
	 *
	 * @return Subjektdatentypen. */
	default Set2<DT> sourcesAsTypes() {
		return this.sources().translate(this.model().typeTrans());
	}

	/** Diese Methode liefert die {@link DT#node() Typknoten} der erwünschten {@link DT Datentypen} von {@link QE#object() Objektknoten}.
	 *
	 * @return Objektdatentypknoten. */
	default Set2<QN> targets() {
		return this.model().linkTargetLink().getTargetProxy(this.node());
	}

	/** Diese Methode liefert die erwünschten {@link DT Datentypen} von {@link QE#object() Objektknoten}.
	 *
	 * @return Objektdatentypen. */
	default Set2<DT> targetsAsTypes() {
		return this.targets().translate(this.model().typeTrans());
	}

	default Property2<QN> clonability() {
		return this.model().linkClonabilityLink().asTargetField().toProperty(this.node());
	}

	default Property2<CLONABILITY> clonabilityAsEnum() {
		return this.clonabilityAsString().translate(CLONABILITY.trans);
	}

	default Property2<String> clonabilityAsString() {
		return this.clonability().translate(this.owner().valueTrans());
	}

	default Property2<QN> multiplicity() {
		return this.model().linkMultiplicityLink().asTargetField().toProperty(this.node());
	}

	default Property2<MULTIPLICITY> multiplicityAsEnum() {
		return this.multiplicityAsString().translate(MULTIPLICITY.trans);
	}

	default Property2<String> multiplicityAsString() {
		return this.multiplicity().translate(this.owner().valueTrans());
	}

	enum CLONABILITY {

		SD

		
		
		
		;

		static final Translator2<String, CLONABILITY> trans = Translators.from(CLONABILITY.class);

	}

	enum MULTIPLICITY {

		SS_ST,

		SS_MT,

		MS_ST,

		MS_MT;

		static final Translator2<String, MULTIPLICITY> trans = Translators.from(MULTIPLICITY.class);

	}

}
