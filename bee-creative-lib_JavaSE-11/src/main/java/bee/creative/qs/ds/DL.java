package bee.creative.qs.ds;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;

public interface DL extends DE {

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
		this.setTargetSet(source, Collections.singleton(target));
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
		this.putTargetSet(source, Collections.singleton(target));
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

	boolean getAllowMultipleObjects();

	boolean getAllowMultipleSujects();

	boolean getCloneEdgeWithObject();

	boolean getCloneEdgeWithSubject();

	boolean getCloneNodeWithObject();

	boolean getCloneNodeWithSubject();

	DTSet sourceTypes();

	DTSet targetTypes();

}
