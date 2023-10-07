package bee.creative.qs.ds;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;

public interface DL extends DE {

	/** Dieses Feld speichert den Textwert eines {@link #idents() Erkennungsknoten} für das {@link #sourceTypes()}-{@link DL Datenfeld}. */
	String IDENT_LINK_HAS_SOURCE = "DM:LINK_HAS_SOURCE";

	/** Dieses Feld speichert den Textwert eines {@link #idents() Erkennungsknoten} für das {@link #targetTypes()}-{@link DL Datenfeld}. */
	String IDENT_LINK_HAS_TARGET = "DM:LINK_HAS_TARGET";

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

	default DTSet sourceTypes() {
		final var model = this.model();
		return model.nodesAsTypes(model.linkSourceLink().getTargetSet(this.node()));
	}

	default DTSet targetTypes() {
		final var model = this.model();
		return model.nodesAsTypes(model.linkTargetLink().getTargetSet(this.node()));
	}

}
