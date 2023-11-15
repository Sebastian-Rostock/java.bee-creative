package bee.creative.qs.h2.ds;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import bee.creative.qs.QN;
import bee.creative.qs.ds.DH;
import bee.creative.qs.ds.DL;
import bee.creative.qs.ds.DL.Handling;
import bee.creative.qs.ds.DL.Multiplicity;
import bee.creative.qs.ds.DM;
import bee.creative.qs.ds.DS;
import bee.creative.qs.ds.DT;
import bee.creative.qs.h2.H2QESet;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QS;
import bee.creative.util.Getter;
import bee.creative.util.HashMap2;
import bee.creative.util.Iterables;
import bee.creative.util.Setter;
import bee.creative.util.Translator2;
import bee.creative.util.Translators;

public class H2DM implements DM {

	public final H2DS parent;

	public final H2QN context;

	public H2DM(H2DS parent) {
		this(parent, parent.owner().newNode());
		parent.models().add(this.context);
	}

	@Override
	public H2QESet edges() {
		return this.context.owner.edges().havingContext(this.context);
	}

	@Override
	public H2QS owner() {
		return this.context.owner;
	}

	@Override
	public DS parent() {
		return this.parent;
	}

	@Override
	public H2QN context() {
		return this.context;
	}

	@Override
	public DH history() {
		// TODO
		return null;
	}

	@Override
	public H2DL getLink(QN ident) {
		this.checkMaps();
		return this.linkByNodeMap.get(ident);
	}

	@Override
	public H2DL getLink(String ident) {
		this.checkMaps();
		return this.linkByTextMap.get(ident);
	}

	@Override
	public H2DT getType(QN ident) {
		this.checkMaps();
		return this.typeByNodeMap.get(ident);
	}

	@Override
	public H2DT getType(String ident) {
		this.checkMaps();
		return this.typeByTextMap.get(ident);
	}

	@Override
	public void updateIdents() {
		var rollback = true;
		var linkByTextMapBackup = this.linkByTextMap;
		var linkByNodeMapBackup = this.linkByNodeMap;
		var typeByTextMapBackup = this.typeByTextMap;
		var typeByNodeMapBackup = this.typeByNodeMap;
		try {
			linkByTextMap = new HashMap2<>(100);
			linkByNodeMap = new HashMap2<>(100);
			typeByTextMap = new HashMap2<>(100);
			typeByNodeMap = new HashMap2<>(100);

			var isLinkWithIdent_IdentText = DL.IDENT_IsLinkWithIdent;
			var isLinkWithIdent_IdentNode = this.owner().newNode(isLinkWithIdent_IdentText);

			var isLinkWithIdent_EdgeList = this.edges().havingObject(isLinkWithIdent_IdentNode) //
				.iterator().filter(edge -> edge.subject().equals(edge.predicate())).toList();

			if (isLinkWithIdent_EdgeList.size() > 1) //
				throw new IllegalArgumentException("DL with ident " + isLinkWithIdent_IdentText + " is not unique");

			if (isLinkWithIdent_EdgeList.isEmpty()) {

				var isLinkWithIdent = this.asLink(this.owner().newNode());
				isLinkWithIdent.setTarget(isLinkWithIdent.node(), isLinkWithIdent_IdentNode);
				linkByTextMap.put(isLinkWithIdent_IdentText, isLinkWithIdent);
				linkByNodeMap.put(isLinkWithIdent_IdentNode, isLinkWithIdent);

				this.customSetup();
				this.checkLinkByTextMap(this.linkByTextMap);
				this.checkTypeByTextMap(this.typeByTextMap);

			} else {
				var isLinkWithIdent = this.asLink(isLinkWithIdent_EdgeList.get(0).predicate());

				this.setupIdentMaps(isLinkWithIdent, linkByNodeMap::put, linkByTextMap::put, this::asLink, ident -> "DL with ident " + ident + " is not unique");
				this.checkLinkByTextMap(linkByTextMap);

				var isTypeWithIdent = linkByTextMap.get(DT.IDENT_IsTypeWithIdent);

				this.setupIdentMaps(isTypeWithIdent, typeByNodeMap::put, typeByTextMap::put, this::asType, ident -> "DT with ident " + ident + " is not unique");
				this.checkTypeByTextMap(typeByTextMap);

			}

			this.linkByTextMap.compact();
			this.linkByNodeMap.compact();
			this.typeByTextMap.compact();
			this.typeByNodeMap.compact();

			rollback = false;
		} finally {
			if (rollback) {
				this.linkByTextMap = linkByTextMapBackup;
				this.linkByNodeMap = linkByNodeMapBackup;
				this.typeByTextMap = typeByTextMapBackup;
				this.typeByNodeMap = typeByNodeMapBackup;
			}
		}
	}

	@Override
	public Translator2<QN, DL> linkTrans() {
		return this.linkTrans == null ? this.linkTrans = Translators.from(QN.class, DL.class, this::asLink, DL::node).optionalize() : this.linkTrans;
	}

	@Override
	public Translator2<QN, DT> typeTrans() {
		return this.typeTrans == null ? this.typeTrans = Translators.from(QN.class, DT.class, this::asType, DT::node).optionalize() : this.typeTrans;
	}

	protected H2DL asLink(QN node) {
		return new H2DL(this, this.context.owner.asQN(node));
	}

	protected H2DT asType(QN node) {
		return new H2DT(this, this.context.owner.asQN(node));
	}

	/** Diese Methode TODO */
	protected void checkIdentMap(Map<String, ?> itemByIdentTextMap, Getter<String, String> getErrorByIdentText, String... idents) {
		for (var ident: idents) {
			if (itemByIdentTextMap.get(ident) == null) throw new IllegalArgumentException(getErrorByIdentText.get(ident));
		}
	}

	/** Diese Methode TODO
	 *
	 * @param <GItem>
	 * @param isItemWithIdent
	 * @param setItemByIdentNode
	 * @param setItemByIdentText
	 * @param getItemByItemNode
	 * @param getErrorByIdentNode */
	protected <GItem> void setupIdentMaps(DL isItemWithIdent, Setter<QN, GItem> setItemByIdentNode, Setter<String, GItem> setItemByIdentText,
		Getter<QN, GItem> getItemByItemNode, Getter<QN, String> getErrorByIdentNode) {
		var itemNodeByIdentNodeMap = new HashMap2<QN, QN>(100);
		isItemWithIdent.edges().forEach(edge -> {
			if (itemNodeByIdentNodeMap.put(edge.object(), edge.subject()) != null) throw new IllegalArgumentException(getErrorByIdentNode.get(edge.object()));
		});

		var identTextByIdentNodeMap = new HashMap2<QN, String>(itemNodeByIdentNodeMap.size());
		this.owner().newNodes(itemNodeByIdentNodeMap.keySet()).values(identTextByIdentNodeMap::put);

		var linkByLinkNodeMap = new HashMap2<QN, GItem>(100);
		itemNodeByIdentNodeMap.values().iterator().toSet().forEach(linkNode -> linkByLinkNodeMap.put(linkNode, getItemByItemNode.get(linkNode)));

		itemNodeByIdentNodeMap.forEach((identNode, linkNode) -> {
			var link = linkByLinkNodeMap.get(linkNode);
			setItemByIdentNode.set(identNode, link);
			setItemByIdentText.set(identTextByIdentNodeMap.get(identNode), link);
		});
	}

	protected void customSetup() {
		var owner = this.owner();
		var setup = new Setup();

		this.customSetup(setup);

		this.checkLinkByTextMap(this.linkByTextMap);
		this.checkTypeByTextMap(this.typeByTextMap);

		// TODO
		
		this.getLink(DT.IDENT_IsTypeWithLabel).setTargetMap(null);

		this.typesAsTypes().addAll(this.typeByNodeMap.values());
		this.linksAsLinks().addAll(this.linkByNodeMap.values());

	}

	protected void customSetup(Setup setup) {

		setup.putType(DT.IDENT_IsType, "domain-type");

		setup.putLink(DT.IDENT_IsTypeWithIdent, "type-idents", DT.IDENT_IsType, Handling.Association, Multiplicity.Multiplicity11, null, Handling.Association,
			Multiplicity.Multiplicity1N);

		setup.putLink(DT.IDENT_IsTypeWithLabel, "type-label", DT.IDENT_IsType, Handling.Association, Multiplicity.Multiplicity1N, null, Handling.Aggregation,
			Multiplicity.Multiplicity01);

		setup.putLink(DT.IDENT_IsTypeWithInstance, "type-instances", DT.IDENT_IsType, Handling.Aggregation, Multiplicity.Multiplicity11, null, Handling.Association,
			Multiplicity.Multiplicity0N);

		setup.putType(DL.IDENT_IsLink, "domain-link");

		setup.putLink(DL.IDENT_IsLinkWithIdent, "link-idents", DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity11, null, Handling.Association,
			Multiplicity.Multiplicity1N);

		setup.putLink(DL.IDENT_IsLinkWithLabel, "link-label", DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity1N, null, Handling.Aggregation,
			Multiplicity.Multiplicity01);

		setup.putLink(DL.IDENT_IsLinkWithSourceType, "link-source-type", DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity0N, DT.IDENT_IsType,
			Handling.Aggregation, Multiplicity.Multiplicity01);

		setup.putLink(DL.IDENT_IsLinkWithSourceHandling, "link-source-handling", DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity0N, null,
			Handling.Aggregation, Multiplicity.Multiplicity11);

		setup.putLink(DL.IDENT_IsLinkWithSourceMultiplicity, "link-source-multiplicity", DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity0N, null,
			Handling.Aggregation, Multiplicity.Multiplicity11);

		setup.putLink(DL.IDENT_IsLinkWithTargetType, "link-target-type", DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity0N, DT.IDENT_IsType,
			Handling.Aggregation, Multiplicity.Multiplicity01);

		setup.putLink(DL.IDENT_IsLinkWithTargetHandling, "link-target-handling", DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity0N, null,
			Handling.Aggregation, Multiplicity.Multiplicity11);

		setup.putLink(DL.IDENT_IsLinkWithTargetMultiplicity, "link-target-multiplicity", DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity0N, null,
			Handling.Aggregation, Multiplicity.Multiplicity11);

		setup.apply();

	}

	H2DM(H2DS parent, H2QN context) {
		this.parent = parent;
		this.context = context;
	}

	private Translator2<QN, DL> linkTrans;

	protected HashMap2<String, H2DL> linkByTextMap;

	protected HashMap2<QN, H2DL> linkByNodeMap;

	private Translator2<QN, DT> typeTrans;

	protected HashMap2<String, H2DT> typeByTextMap;

	protected HashMap2<QN, H2DT> typeByNodeMap;

	private void checkMaps() {
		if (this.linkByNodeMap != null) return;
		this.updateIdents();
	}

	private void checkLinkByTextMap(HashMap2<String, H2DL> linkByTextMap) {
		this.checkIdentMap(linkByTextMap, ident -> "DL with ident " + ident + " is missing", //
			DT.IDENT_IsTypeWithIdent, DT.IDENT_IsTypeWithLabel, DT.IDENT_IsTypeWithInstance, //
			DL.IDENT_IsLinkWithIdent, DL.IDENT_IsLinkWithLabel, //
			DL.IDENT_IsLinkWithSourceType, DL.IDENT_IsLinkWithSourceHandling, DL.IDENT_IsLinkWithSourceMultiplicity, //
			DL.IDENT_IsLinkWithTargetType, DL.IDENT_IsLinkWithTargetHandling, DL.IDENT_IsLinkWithTargetMultiplicity);
	}

	private void checkTypeByTextMap(HashMap2<String, H2DT> typeByTextMap) {
		this.checkIdentMap(typeByTextMap, ident -> "DT with ident " + ident + " is missing", //
			DT.IDENT_IsType, DL.IDENT_IsLink);
	}

	protected final class Setup {
	
		public void putType(String ident, String label) {
			this.typeSetupMap.put(ident, Arrays.asList(label));
		}
	
		public void putLink(String ident, String label, String sourceTypeIdent, Handling sourceHandling, Multiplicity sourceMultiplicity, String targetTypeIdent,
			Handling targetHandling, Multiplicity targetMultiplicity) {
			this.linkSetupMap.put(ident, Arrays.asList(label, //
				sourceTypeIdent, Handling.trans.toSource(sourceHandling), Multiplicity.trans.toSource(sourceMultiplicity), //
				targetTypeIdent, Handling.trans.toSource(targetHandling), Multiplicity.trans.toSource(targetMultiplicity)));
		}
	
		HashMap2<String, QN> createNodeByValueMap(Iterable<?> values) {
			var valueSet = H2DM.this.owner().newValues(values);
			var nodeByValueMap = new HashMap2<String, QN>(100);
			valueSet.putAll();
			valueSet.nodes(nodeByValueMap::put);
			return nodeByValueMap;
		}
	
		public void apply() {
			var that = H2DM.this;
			var owner = that.owner();
			var nodeByValueMap = this.createNodeByValueMap(Iterables.concatAll(Iterables.fromArray(this.typeSetupMap.keySet(), this.linkSetupMap.keySet(),
				Iterables.concatAll(this.typeSetupMap.values()), Iterables.concatAll(this.linkSetupMap.values()))));
	
			this.linkSetupMap.keySet()
				.forEach(ident -> that.linkByNodeMap.put(nodeByValueMap.get(ident), that.linkByTextMap.install(ident, ignored -> that.asLink(owner.newNode()))));
	
			this.typeSetupMap.keySet()
				.forEach(ident -> that.typeByNodeMap.put(nodeByValueMap.get(ident), that.typeByTextMap.install(ident, ignored -> that.asType(owner.newNode()))));
	
		}
	
		HashMap2<String, List<String>> linkSetupMap = new HashMap2<>();
	
		HashMap2<String, List<String>> typeSetupMap = new HashMap2<>();
	
	}

	protected class Setup2 {

		public void putProps(String ident, String... props) {
			this.propValueMap.put(ident, Arrays.asList(props));
		}
		
		public HashMap2<QN, QN> getProp(int index){
			
		}

		HashMap2<String, QN> createNodeByValueMap(Iterable<?> values) {
			var valueSet = H2DM.this.owner().newValues(values);
			var nodeByValueMap = new HashMap2<String, QN>(100);
			valueSet.putAll();
			valueSet.nodes(nodeByValueMap::put);
			return nodeByValueMap;
		}

		public void apply() {
			var that = H2DM.this;
			var owner = that.owner();
			var nodeByValueMap = this.createNodeByValueMap(Iterables.concatAll(Iterables.fromArray(this.propValueMap.keySet(), this.linkSetupMap.keySet(),
				Iterables.concatAll(this.propValueMap.values()), Iterables.concatAll(this.linkSetupMap.values()))));

			this.linkSetupMap.keySet()
				.forEach(ident -> that.linkByNodeMap.put(nodeByValueMap.get(ident), that.linkByTextMap.install(ident, ignored -> that.asLink(owner.newNode()))));

			this.propValueMap.keySet()
				.forEach(ident -> that.typeByNodeMap.put(nodeByValueMap.get(ident), that.typeByTextMap.install(ident, ignored -> that.asType(owner.newNode()))));

		}

		HashMap2<String, List<String>> linkSetupMap = new HashMap2<>();

		HashMap2<String, List<String>> propValueMap = new HashMap2<>();

	}

}
