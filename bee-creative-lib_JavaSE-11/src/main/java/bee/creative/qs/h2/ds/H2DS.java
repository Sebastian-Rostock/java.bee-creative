package bee.creative.qs.h2.ds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import bee.creative.lang.Objects;
import bee.creative.qs.QN;
import bee.creative.qs.ds.DM;
import bee.creative.qs.ds.DQ;
import bee.creative.qs.ds.DS;
import bee.creative.qs.h2.H2C;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QQ;
import bee.creative.qs.h2.H2QS;
import bee.creative.util.HashMap2;
import bee.creative.util.Set2;
import bee.creative.util.Translator2;
import bee.creative.util.Translators;

public class H2DS implements DS, AutoCloseable {

	public static H2DS from(String file) throws NullPointerException, ClassNotFoundException, SQLException {
		return new H2DS(H2C.from(file));
	}

	public H2DS(Connection conn) throws NullPointerException, SQLException {
		this.store = new H2QS(conn, this);
		this.domain = this.store.newNode("");
	}

	@Override
	public H2QS store() {
		return this.store;
	}

	@Override
	public H2QN install(String value) throws NullPointerException {
		return (this.installMap == null ? this.installMap = this.createInstallMap() : this.installMap).install(Objects.notNull(value), item -> {
			var result = this.store.newNode();
			this.store.newEdge(this.domain, this.store.newNode(item), this.domain, result).put();
			return result;
		});
	}

	@Override
	public Set2<QN> installSet(String value) throws NullPointerException {
		return (this.installSetMap == null ? this.installSetMap = this.createInstallSetMap() : this.installSetMap)
			.install(Objects.notNull(value), item -> new Items(this, this.install(item))).asNodeSet();
	}

	@Override
	public Translator2<QN, DM> modelTrans() {
		return this.modelTrans == null ? this.modelTrans = this.createModelTrans() : this.modelTrans;
	}

	@Override
	public void close() throws SQLException {
		this.store.close();
	}

	/** Diese Methode verwirft die Puffer hinter {@link #install(String)} und {@link #installSet(String)}, wodurch sie beim nächsten Aufruf dieser Methoden neu
	 * eingelesen werden. */
	public void invalidateIdents() throws IllegalStateException {
		this.installMap = null;
		this.installSetMap = null;
	}

	protected H2DM asModel(QN node) {
		return new H2DM(this, this.store.asQN(node));
	}

	private final H2QS store;

	private final H2QN domain;

	private Translator2<QN, DM> modelTrans;

	private HashMap2<String, H2QN> installMap;

	private HashMap2<String, Items> installSetMap;

	private HashMap2<String, H2QN> createInstallMap() throws IllegalStateException {
		var itemNodeByIdentNodeMap = new HashMap2<QN, QN>(100);
		this.store.edges().havingContext(this.domain).havingPredicate(this.domain).forEach(edge -> {
			if (itemNodeByIdentNodeMap.put(edge.subject(), edge.object()) != null) throw new IllegalStateException();
		});
		if (itemNodeByIdentNodeMap.size() != itemNodeByIdentNodeMap.values().iterator().toSet().size()) throw new IllegalStateException();
		var installMap = new HashMap2<String, H2QN>(itemNodeByIdentNodeMap.size());
		this.store.newNodes(itemNodeByIdentNodeMap.keySet()).values((identNode, identValue) -> {
			installMap.put(identValue, this.store.asQN(itemNodeByIdentNodeMap.get(identNode)));
		});
		return installMap;
	}

	private HashMap2<String, Items> createInstallSetMap() {
		return new HashMap2<>();
	}

	private Translator2<QN, DM> createModelTrans() {
		return Translators.from(QN.class, DM.class, this::asModel, DM::context).optionalize();
	}

	private static class Items extends H2DSNSet {

		public Items(H2DS parent, H2QN predicate) {
			super(parent.store, new H2QQ().push(parent.store.edges() //
				.havingContext(parent.domain).havingSubject(parent.domain).havingPredicate(predicate).objects()));
			this.parent = parent;
			this.predicate = predicate;
		}

		@Override
		public boolean setNodes(Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
			return DQ.setObjectSetMap(this.parent.domain, this.predicate, Collections.singletonMap(this.parent.domain, nodes), null, null);
		}

		@Override
		public boolean putNodes(Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
			return DQ.putObjectSetMap(this.parent.domain, this.predicate, Collections.singletonMap(this.parent.domain, nodes), null, null);
		}

		@Override
		public boolean popNodes(Iterable<? extends QN> nodes) throws NullPointerException, IllegalArgumentException {
			return DQ.popObjectSetMap(this.parent.domain, this.predicate, Collections.singletonMap(this.parent.domain, nodes), null, null);
		}

		private H2DS parent;

		private H2QN predicate;

	}

}
