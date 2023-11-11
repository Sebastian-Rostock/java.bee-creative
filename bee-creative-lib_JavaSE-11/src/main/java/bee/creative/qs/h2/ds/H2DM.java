package bee.creative.qs.h2.ds;

import java.util.Arrays;
import java.util.Map;
import bee.creative.qs.QN;
import bee.creative.qs.ds.DH;
import bee.creative.qs.ds.DL;
import bee.creative.qs.ds.DM;
import bee.creative.qs.ds.DT;
import bee.creative.qs.ds.DL.Handling;
import bee.creative.qs.ds.DL.Multiplicity;
import bee.creative.qs.h2.H2QESet;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QS;
import bee.creative.util.Getter;
import bee.creative.util.HashMap2;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterables;
import bee.creative.util.Setter;
import bee.creative.util.Translator;
import bee.creative.util.Translator2;
import bee.creative.util.Translators;

public class H2DM implements DM {

	public final H2QN context;

	public H2DM(H2QN context) {
		this.context = context;
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
	public H2QN context() {
		return this.context;
	}

	@Override
	public DH history() {
		return null;
	}

	@Override
	public H2DL getLink(QN ident) {
		return this.linkByNodeMap.get(ident);
	}

	@Override
	public H2DL getLink(String ident) {
		return this.linkByTextMap.get(ident);
	}

	@Override
	public H2DT getType(QN ident) {
		return this.typeByNodeMap.get(ident);
	}

	@Override
	public H2DT getType(String ident) {
		return this.typeByTextMap.get(ident);
	}

	@Override
	public void updateIdents() {

		var linkByTextMap = new HashMap2<String, H2DL>(100);
		var linkByNodeMap = new HashMap2<QN, H2DL>(100);
		var typeByTextMap = new HashMap2<String, H2DT>(100);
		var typeByNodeMap = new HashMap2<QN, H2DT>(100);

		var isLinkWithIdent_IdentText = DL.IDENT_IsLinkWithIdent;
		var isLinkWithIdent_IdentNode = this.owner().newNode(isLinkWithIdent_IdentText);

		var isLinkWithIdent_EdgeList = this.edges().havingObject(isLinkWithIdent_IdentNode) //
			.iterator().filter(edge -> edge.subject().equals(edge.predicate())).toList();

		if (isLinkWithIdent_EdgeList.size() > 1) //
			throw new IllegalArgumentException("DL with ident " + isLinkWithIdent_IdentText + " is not unique");

		if (isLinkWithIdent_EdgeList.isEmpty()) {

			var isLinkWithIdent = this.asLink(this.owner().newNode());
			isLinkWithIdent.setTarget(isLinkWithIdent.node, isLinkWithIdent_IdentNode);
			linkByTextMap.put(isLinkWithIdent_IdentText, isLinkWithIdent);
			linkByNodeMap.put(isLinkWithIdent_IdentNode, isLinkWithIdent);

			var rollbackSetup = true;
			var linkByTextMap2 = this.linkByTextMap;
			var linkByNodeMap2 = this.linkByNodeMap;
			var typeByTextMap2 = this.typeByTextMap;
			var typeByNodeMap2 = this.typeByNodeMap;

			try {

				this.linkByTextMap = linkByTextMap;
				this.linkByNodeMap = linkByNodeMap;
				this.typeByTextMap = typeByTextMap;
				this.typeByNodeMap = typeByNodeMap;

				this.customSetup();
				this.checkLinkByTextMap(this.linkByTextMap);
				this.checkTypeByTextMap(this.typeByTextMap);
				rollbackSetup = false;

			} finally {

				if (rollbackSetup) {
					this.linkByTextMap = linkByTextMap2;
					this.linkByNodeMap = linkByNodeMap2;
					this.typeByTextMap = typeByTextMap2;
					this.typeByNodeMap = typeByNodeMap2;
				}

			}
		} else {
			var isLinkWithIdent = this.asLink(isLinkWithIdent_EdgeList.get(0).predicate());

			this.setupIdentMaps(isLinkWithIdent, linkByNodeMap::put, linkByTextMap::put, this::asLink, ident -> "DL with ident " + ident + " is not unique");
			this.checkLinkByTextMap(linkByTextMap);

			var isTypeWithIdent = linkByTextMap.get(DT.IDENT_IsTypeWithIdent);

			this.setupIdentMaps(isTypeWithIdent, typeByNodeMap::put, typeByTextMap::put, this::asType, ident -> "DT with ident " + ident + " is not unique");
			this.checkTypeByTextMap(typeByTextMap);

			this.linkByTextMap = linkByTextMap;
			this.linkByNodeMap = linkByNodeMap;
			this.typeByTextMap = typeByTextMap;
			this.typeByNodeMap = typeByNodeMap;

		}

		this.linkByTextMap.compact();
		this.linkByNodeMap.compact();
		this.typeByTextMap.compact();
		this.typeByNodeMap.compact();

	}

	@Override
	public Translator2<QN, DL> linkTrans() {
		return this.linkTrans;
	}

	@Override
	public Translator2<QN, DT> typeTrans() {
		return this.typeTrans;
	}

	/** Diese Methode liefet die {@link DL#node() Feldknoten} der {@link DL Datenfelder} mit den gegebenen {@link DL#identsAsStrings() Erkennungstextwerten}. */
	protected Iterable2<QN> getLinkNodes(String... linkIdents) {
		return Iterables.fromArray(linkIdents).translate(this::getLink).translate(H2DL::node);
	}

	/** Diese Methode liefet die {@link DT#node() Typknoten} der {@link DT Datentypen} mit den gegebenen {@link DT#identsAsStrings() Erkennungstextwerten}. */
	protected Iterable2<QN> getTypeNodes(String... typeIdents) {
		return Iterables.fromArray(typeIdents).translate(this::getType).translate(H2DT::node);
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

		var nodeByValueMap = new HashMap2<String, QN>(100);
		var valueSet = this.owner().newValues(Iterables.concatAll(Iterables.translate(//
			Arrays.asList(setup.typeLabelMap, setup.linkLabelMap, setup.linkSourceTypeMap, setup.linkSourceHandlingMap, setup.linkSourceMultiplicityMap),
			map -> map.keySet().concat(map.values()))));
		valueSet.putAll();
		valueSet.nodes(nodeByValueMap::put);

		var tr = Translators.fromEnum(nodeByValueMap).reverse();

	var linkIdentTrans =	linkByTextMap.translate(Translators.neutral(), Translators.neutral());
		
		
		setup.linkLabelMap.keySet()
			.forEach(ident -> this.linkByNodeMap.put(nodeByValueMap.get(ident), this.linkByTextMap.install(ident, ignored -> this.asLink(owner.newNode()))));

		setup.typeLabelMap.keySet()
			.forEach(ident -> this.typeByNodeMap.put(nodeByValueMap.get(ident), this.typeByTextMap.install(ident, ignored -> this.asType(owner.newNode()))));

		this.checkLinkByTextMap(this.linkByTextMap);
		this.checkTypeByTextMap(this.typeByTextMap);

		setup.typeLabelMap.translate(typeIdentTrans, tr);
		
		getLink(DT.IDENT_IsTypeWithLabel).setTargetMap(null);
		
		setup.setupLinkMap.forEach((linkIdent, linkSetupList) -> {
			var link = this.getLink(linkIdent);
			linkSetupList.forEach(linkSetup -> linkSetup.set(link));
		});

		setup.setupTypeMap.forEach((typeIdent, typeSetupList) -> {
			var type = this.getType(typeIdent);
			typeSetupList.forEach(typeSetup -> typeSetup.set(type));
		});

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

	}

	protected H2DL asLink(QN node) {
		return new H2DL(this, this.context.owner.asQN(node));
	}

	protected H2DT asType(QN node) {
		return new H2DT(this, this.context.owner.asQN(node));
	}

	protected HashMap2<String, H2DL> linkByTextMap = new HashMap2<>();

	protected HashMap2<QN, H2DL> linkByNodeMap = new HashMap2<>();

	protected HashMap2<String, H2DT> typeByTextMap = new HashMap2<>();

	protected HashMap2<QN, H2DT> typeByNodeMap = new HashMap2<>();

	private final Translator2<QN, DL> linkTrans = Translators.from(QN.class, DL.class, this::asLink, DL::node).optionalize();

	private final Translator2<QN, DT> typeTrans = Translators.from(QN.class, DT.class, this::asType, DT::node).optionalize();

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

	protected class Setup {

		public void putType(String ident, String label) {

			var t = H2DM.this.typeByTextMap.install(ident, ignored -> asType(owner().newNode()));

			this.typeLabelMap.put(ident, label);
		}

		public void putLink(String ident, String label, String sourceTypeIdent, Handling sourceHandling, Multiplicity sourceMultiplicity, String targetTypeIdent,
			DL.Handling targetHandling, DL.Multiplicity targetMultiplicity) {
			this.linkLabelMap.put(ident, label);
			this.linkSourceTypeMap.put(ident, sourceTypeIdent);
			this.linkSourceHandlingMap.put(ident, Handling.trans.toSource(sourceHandling));
			this.linkSourceMultiplicityMap.put(ident, Multiplicity.trans.toSource(sourceMultiplicity));
			this.linkTargetTypeMap.put(ident, targetTypeIdent);
			this.linkTargetHandlingMap.put(ident, Handling.trans.toSource(targetHandling));
			this.linkTargetMultiplicityMap.put(ident, Multiplicity.trans.toSource(targetMultiplicity));
		}

		HashMap2<String, String> typeLabelMap = new HashMap2<>();

		HashMap2<String, String> linkLabelMap = new HashMap2<>();

		HashMap2<String, String> linkSourceTypeMap = new HashMap2<>();

		HashMap2<String, String> linkSourceHandlingMap = new HashMap2<>();

		HashMap2<String, String> linkSourceMultiplicityMap = new HashMap2<>();

		HashMap2<String, String> linkTargetTypeMap = new HashMap2<>();

		HashMap2<String, String> linkTargetHandlingMap = new HashMap2<>();

		HashMap2<String, String> linkTargetMultiplicityMap = new HashMap2<>();

	}

}
