package bee.creative.qs.ds;

import bee.creative.qs.QN;
import bee.creative.util.Property3;
import bee.creative.util.Set2;

// domain-history
public interface DH extends DO {

	String IDENT_IsModelWithCurrentChange = "DS:IsModelWithCurrentChange";

	String IDENT_IsModelWithChangeHistory = "DS:IsModelWithChangeHistory";

	default void commit() {
		var parent = this.parent();
		var parentNode = parent.context();
		var isChangeWithNextChangeNode = parent.getLink(DC.IDENT_IsChangeWithNextChange).node();
		var isChangeWithPutContextNode = parent.getLink(DC.IDENT_IsChangeWithPutContext).node();
		var isChangeWithPopContextNode = parent.getLink(DC.IDENT_IsChangeWithPopContext).node();
		var isModelWithCurrentChangeNode = parent.getLink(DH.IDENT_IsModelWithCurrentChange).node();
		var isModelWithChangeHistoryNode = parent.getLink(DH.IDENT_IsModelWithChangeHistory).node();
		var owner = parent.owner();
		var oldEdges = owner.edges().havingContext(parentNode).havingSubject(parentNode).havingPredicate(isModelWithCurrentChangeNode);
		var oldCurrentChangeNode = oldEdges.objects().first();
		var newCurrentChangeNode = owner.newNode();
		var newRecordingPutContextNode = owner.newNode();
		var newRecordingPopContextNode = owner.newNode();
		var newEdges = owner.newEdges( //
			owner.newEdge(parentNode, isModelWithCurrentChangeNode, parentNode, newCurrentChangeNode), //
			owner.newEdge(parentNode, isModelWithChangeHistoryNode, parentNode, newCurrentChangeNode), //
			owner.newEdge(parentNode, isChangeWithNextChangeNode, oldCurrentChangeNode, newCurrentChangeNode), //
			owner.newEdge(parentNode, isChangeWithPutContextNode, newCurrentChangeNode, newRecordingPutContextNode), //
			owner.newEdge(parentNode, isChangeWithPopContextNode, newCurrentChangeNode, newRecordingPopContextNode) //
		);
		oldEdges.popAll();
		newEdges.putAll();
	}

	default Property3<DC> currentChange() {
		return this.currentChangeAsNode().translate(this.parent().changeTrans());
	}

	default Property3<QN> currentChangeAsNode() {
		var parent = this.parent();
		return parent.getLink(DH.IDENT_IsModelWithCurrentChange).getObjects(parent.context()).asNode();
	}

	default QN currentPutContext() {
		var current = this.currentChange().get();
		return current != null ? current.recordingPutContext().get() : null;
	}

	default QN currentPopContext() {
		var current = this.currentChange().get();
		return current != null ? current.recordingPopContext().get() : null;
	}

	default Set2<DC> changes() {
		return this.changesAsNodes().asTranslatedSet(this.parent().changeTrans());
	}

	default Set2<QN> changesAsNodes() {
		var parent = this.parent();
		return parent.getLink(DH.IDENT_IsModelWithChangeHistory).getObjects(parent.context()).asNodeSet();
	}

}
