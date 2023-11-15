package bee.creative.qs.h2.ds;

import java.util.Collections;
import bee.creative.lang.Objects;
import bee.creative.qs.QN;
import bee.creative.qs.ds.DM;
import bee.creative.qs.ds.DQ;
import bee.creative.qs.ds.DS;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QQ;
import bee.creative.qs.h2.H2QS;
import bee.creative.util.HashMap2;
import bee.creative.util.Set2;
import bee.creative.util.Translator2;
import bee.creative.util.Translators;

public class H2DS implements DS {

	public H2DS(H2QS owner) {
		this.node = owner.newNode("");
		this.update();
	}

	@Override
	public H2QS owner() {
		return this.node.owner;
	}

	@Override
	public H2QN install(String value) throws NullPointerException {
		return this.installMap.install(Objects.notNull(value), this::installItem);
	}

	@Override
	public Set2<QN> installSet(String value) throws NullPointerException {
		return this.installSetMap.install(Objects.notNull(value), this::installItems).asNodeSet();
	}

	/** Diese Methode aktualisiert den Puffer der über {@link #install(String)} bereitgestellten {@link QN Hyperknoten}. */
	public void update() throws IllegalStateException {
		var node = this.node;
		var owner = node.owner;
		var itemNodeByIdentNodeMap = new HashMap2<QN, QN>(100);
		owner.edges().havingContext(node).havingPredicate(node).forEach(edge -> {
			if (itemNodeByIdentNodeMap.put(edge.subject(), edge.object()) != null) throw new IllegalStateException();
		});
		if (itemNodeByIdentNodeMap.size() != itemNodeByIdentNodeMap.values().iterator().toSet().size()) throw new IllegalStateException();
		this.installMap = new HashMap2<>(itemNodeByIdentNodeMap.size());
		this.installSetMap = new HashMap2<>();
		owner.newNodes(itemNodeByIdentNodeMap.keySet()).values((identNode, identValue) -> {
			this.installMap.put(identValue, owner.asQN(itemNodeByIdentNodeMap.get(identNode)));
		});
	}

	@Override
	public Translator2<QN, DM> modelTrans() {
		return this.domainTrans == null ? this.domainTrans = Translators.from(QN.class, DM.class, this::asDomain, DM::context).optionalize() : this.domainTrans;
	}

	protected H2DM asDomain(QN node) {
		return new H2DM(this, this.node.owner.asQN(node));
	}

	private final H2QN node;

	private HashMap2<String, H2QN> installMap;

	private HashMap2<String, Items> installSetMap;

	private Translator2<QN, DM> domainTrans;

	private H2QN installItem(String value) {
		var node = this.node;
		var owner = node.owner;
		var result = owner.newNode();
		owner.newEdge(node, owner.newNode(value), node, result).put();
		return result;
	}

	private Items installItems(String value) {
		return new Items(this, this.install(value));
	}

	static class Items extends H2DSNSet {

		public Items(H2DS parent, H2QN predicate) {
			super(parent.node.owner, new H2QQ().push(parent.node.owner.edges() //
				.havingContext(parent.node).havingSubject(parent.node).havingPredicate(predicate).objects()));
			this.parent = parent;
			this.predicate = predicate;
		}

		@Override
		public boolean setNodes(Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
			return DQ.setObjectSetMap(this.parent.node, this.predicate, Collections.singletonMap(this.parent.node, nodes), null, null);
		}

		@Override
		public boolean putNodes(Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
			return DQ.putObjectSetMap(this.parent.node, this.predicate, Collections.singletonMap(this.parent.node, nodes), null, null);
		}

		@Override
		public boolean popNodes(Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
			return DQ.popObjectSetMap(this.parent.node, this.predicate, Collections.singletonMap(this.parent.node, nodes), null, null);
		}

		private H2DS parent;

		private H2QN predicate;

	}

}
