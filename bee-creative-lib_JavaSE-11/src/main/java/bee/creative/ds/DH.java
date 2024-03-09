//package bee.creative.ds;
//
//import bee.creative.qs.QN;
//import bee.creative.util.Property2;
//import bee.creative.util.Set2;
//import bee.creative.util.Translator;
//import bee.creative.util.Translator2;
//import bee.creative.util.Translators.OptionalizedTranslator;
//
//// TODO
//// domain-history
//  interface DH extends DO {
//
//	String IDENT_IsModelWithChange = "DS:IsChangeWithPrevious";
//
//	String IDENT_IsModelWithHistory = "DS:IsChangeWithPrevious";
//
//	// gab es änderungen?
//	default boolean todo() {
//		var current = this.current().get();
//		return !current.putEdges().isEmpty() || !current.popEdges().isEmpty();
//	}
//
//	default boolean redo(Iterable<? extends DC> changes) throws NullPointerException, IllegalArgumentException {
//		var redoChangeNodes = changeTrans().toSource().concat(changes).toList();
//		if (redoChangeNodes.isEmpty()) return false;
//		if (redoChangeNodes.contains(null)) throw new NullPointerException();
//		var currentChangeNode = this.currentAsNode().get();
//		if (currentChangeNode == null) return false;
//		if (redoChangeNodes.contains(currentChangeNode)) throw new IllegalArgumentException();
//		var parent = parent();
//		var edges = parent.owner().edges();
//		var isChangeWithPutContext = parent.getLink(DC.IDENT_IsChangeWithPutContext);
//		var isChangeWithPopContext = parent.getLink(DC.IDENT_IsChangeWithPopContext);
//		var putContext = isChangeWithPutContext.getTarget(currentChangeNode);
//		if (putContext == null) return false;
//		var popContext = isChangeWithPopContext.getTarget(currentChangeNode);
//		if (popContext == null) return false;
//		var putContextByRedoChangeNode = isChangeWithPutContext.getTargetMap(redoChangeNodes);
//		if (putContextByRedoChangeNode.values().contains(null)) throw new NullPointerException();
//		var popContextByRedoChangeNode = isChangeWithPopContext.getTargetMap(redoChangeNodes);
//		if (popContextByRedoChangeNode.values().contains(null)) throw new NullPointerException();
//		redoChangeNodes.forEach(redoChangeNode -> {
//			DQ.putEdges(parent.context(), edges.havingContext(putContextByRedoChangeNode.get(redoChangeNode)), putContext, popContext);
//			DQ.popEdges(parent.context(), edges.havingContext(popContextByRedoChangeNode.get(redoChangeNode)), putContext, popContext);
//		});
//		return true;
//	}
//
//	default void undo(Iterable<? extends DC> changes) {
//
//	}
//
//	// änderung abschließen und neue beginnen
//	void done();
//
//	default QN putContext() {
//		var current = this.current().get();
//		return current != null ? current.putContext().get() : null;
//	}
//
//	default QN popContext() {
//		var current = this.current().get();
//		return current != null ? current.popContext().get() : null;
//	}
//
//	default Property2<DC> current() {
//		return this.currentAsNode().translate(this.changeTrans());
//	}
//
//	default Property2<QN> currentAsNode() {
//		var parent = this.parent();
//		return parent.getLink(DH.IDENT_IsModelWithChange).getTargets(parent.context()).asNode();
//	}
//
//	default Set2<DC> changes() {
//		return this.changesAsNodes().translate(this.changeTrans());
//	}
//
//	default Set2<QN> changesAsNodes() {
//		var parent = this.parent();
//		return parent.getLink(DH.IDENT_IsModelWithHistory).getTargets(parent.context()).asNodeSet();
//	}
//
//	/** Diese Methode liefert den {@link OptionalizedTranslator optionalisierten} {@link DC#node() Änderungsknoten}-{@link DC
//	 * Domänenänderung}-{@link Translator}. */
//	Translator2<QN, DC> changeTrans();
//
//}
