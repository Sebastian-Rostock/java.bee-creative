package bee.creative.ber;

import java.util.Arrays;

interface BERBuilder {

	default boolean put(BEREdge edge) {
		return this.put(edge.sourceRef, edge.relationRef, edge.targetRef);
	}

	boolean put(int sourceRef, int relationRef, int targetRef);

	default boolean putAll(BEREdge... edges) {
		return this.putAll(Arrays.asList(edges));
	}

	default boolean putAll(Iterable<BEREdge> edges) {
		if (edges instanceof BERState) return this.putAll((BERState)edges);
		var res = false;
		for (var edge: edges) {
			res = this.put(edge) | res;
		}
		return res;
	}

	boolean putAll(BERState edges);

	default boolean pop(BEREdge edge) {
		return this.pop(edge.sourceRef, edge.relationRef, edge.targetRef);
	}

	boolean pop(int sourceRef, int relationRef, int targetRef);

	default boolean popAll(BEREdge... edges) {
		return this.popAll(Arrays.asList(edges));
	}

	default boolean popAll(Iterable<BEREdge> edges) {
		if (edges instanceof BERState) return this.popAll((BERState)edges);
		var res = false;
		for (var edge: edges) {
			res = this.pop(edge) | res;
		}
		return res;
	}

	boolean popAll(BERState edges);

}
