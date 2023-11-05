package bee.creative.qs.dm.h2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import bee.creative.qs.QN;
import bee.creative.qs.dm.DH;
import bee.creative.qs.dm.DL;
import bee.creative.qs.dm.DM;
import bee.creative.qs.dm.DT;
import bee.creative.qs.dm.DL.Association;
import bee.creative.qs.dm.DL.Multiplicity;
import bee.creative.qs.h2.H2QESet;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QS;
import bee.creative.util.Consumer;
import bee.creative.util.Getter;
import bee.creative.util.HashMap2;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterables;
import bee.creative.util.Setter;
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

		var identTextSet = setup.setupLinkMap.keySet().concat(setup.setupTypeMap.keySet());
		var identNodeByIdentTextMap = new HashMap2<String, QN>(identTextSet.size());

		var textSet = this.owner().newValues(identTextSet);
		textSet.putAll();
		textSet.nodes(identNodeByIdentTextMap::put);

		setup.setupLinkMap.keySet().forEach(identText -> this.linkByNodeMap.put(identNodeByIdentTextMap.get(identText),
			this.linkByTextMap.install(identText, identText2 -> this.asLink(owner.newNode()))));

		setup.setupTypeMap.keySet().forEach(identText -> this.typeByNodeMap.put(identNodeByIdentTextMap.get(identText),
			this.typeByTextMap.install(identText, identText2 -> this.asType(owner.newNode()))));

		setup.setupLinkMap.forEach((linkIdent, linkSetupList) -> {
			var link = this.getLink(linkIdent);
			linkSetupList.forEach(linkSetup -> linkSetup.set(link));
		});

		setup.setupTypeMap.forEach((typeIdent, typeSetupList) -> {
			var type = this.getType(typeIdent);
			typeSetupList.forEach(typeSetup -> typeSetup.set(type));
		});

	}

	protected void customSetup(Setup setup) {

		setup.putTypeLabel(DT.IDENT_IsType, "domain-type");
		setup.putType(DT.IDENT_IsType, typeType -> {
			typeType.instances().addAll(this.getTypeNodes( //
				DT.IDENT_IsType, //
				DL.IDENT_IsLink));
		});

		setup.putLinkLabel(DT.IDENT_IsTypeWithIdent, "type-idents");
		setup.putLinkSources(DT.IDENT_IsTypeWithIdent, Association.Association, Multiplicity.Multiplicity11, DT.IDENT_IsType);
		setup.putLinkTargets(DT.IDENT_IsTypeWithIdent, Association.Association, Multiplicity.Multiplicity1N, null);

		setup.putLinkLabel(DT.IDENT_IsTypeWithLabel, "type-label");
		setup.putLinkSources(DT.IDENT_IsTypeWithLabel, Association.Association, Multiplicity.Multiplicity1N, DT.IDENT_IsType);
		setup.putLinkTargets(DT.IDENT_IsTypeWithLabel, Association.Aggregation, Multiplicity.Multiplicity01, null);

		setup.putLinkLabel(DT.IDENT_IsTypeWithInstance, "type-instances");
		setup.putLinkSources(DT.IDENT_IsTypeWithInstance, Association.Aggregation, Multiplicity.Multiplicity11, DT.IDENT_IsType);
		setup.putLinkTargets(DT.IDENT_IsTypeWithInstance, Association.Association, Multiplicity.Multiplicity0N, null);

		setup.putTypeLabel(DL.IDENT_IsLink, "domain-link");
		setup.putType(DL.IDENT_IsLink, linkType -> {
			linkType.instances().addAll(this.getLinkNodes(//
				DT.IDENT_IsTypeWithIdent, //
				DT.IDENT_IsTypeWithLabel, //
				DT.IDENT_IsTypeWithInstance, //
				DL.IDENT_IsLinkWithIdent, //
				DL.IDENT_IsLinkWithSourceType, //
				DL.IDENT_IsLinkWithSourceAssociation, //
				DL.IDENT_IsLinkWithSourceMultiplicity, //
				DL.IDENT_IsLinkWithTargetType, //
				DL.IDENT_IsLinkWithTargetAssociation, //
				DL.IDENT_IsLinkWithTargetMultiplicity));
		});

		setup.putLinkLabel(DL.IDENT_IsLinkWithIdent, "link-idents");
		setup.putLinkSources(DL.IDENT_IsLinkWithIdent, Association.Association, Multiplicity.Multiplicity11, DL.IDENT_IsLink);
		setup.putLinkTargets(DL.IDENT_IsLinkWithIdent, Association.Association, Multiplicity.Multiplicity1N, null);

		setup.putLinkLabel(DL.IDENT_IsLinkWithLabel, "link-label");
		setup.putLinkSources(DL.IDENT_IsLinkWithLabel, Association.Association, Multiplicity.Multiplicity1N, DL.IDENT_IsLink);
		setup.putLinkTargets(DL.IDENT_IsLinkWithLabel, Association.Aggregation, Multiplicity.Multiplicity01, null);

		setup.putLinkLabel(DL.IDENT_IsLinkWithSourceType, "link-source-types");
		setup.putLinkSources(DL.IDENT_IsLinkWithSourceType, Association.Association, Multiplicity.Multiplicity0N, DL.IDENT_IsLink);
		setup.putLinkTargets(DL.IDENT_IsLinkWithSourceType, Association.Aggregation, Multiplicity.Multiplicity01, DT.IDENT_IsType);

		setup.putLinkLabel(DL.IDENT_IsLinkWithSourceAssociation, "link-source-association");
		setup.putLinkSources(DL.IDENT_IsLinkWithSourceAssociation, Association.Association, Multiplicity.Multiplicity0N, DL.IDENT_IsLink);
		setup.putLinkTargets(DL.IDENT_IsLinkWithSourceAssociation, Association.Aggregation, Multiplicity.Multiplicity11, null);

		setup.putLinkLabel(DL.IDENT_IsLinkWithSourceMultiplicity, "link-source-multiplicity");
		setup.putLinkSources(DL.IDENT_IsLinkWithSourceMultiplicity, Association.Association, Multiplicity.Multiplicity0N, DL.IDENT_IsLink);
		setup.putLinkTargets(DL.IDENT_IsLinkWithSourceMultiplicity, Association.Aggregation, Multiplicity.Multiplicity11, null);
		
		setup.putLinkLabel(DL.IDENT_IsLinkWithTargetType, "link-target-types");
		setup.putLinkSources(DL.IDENT_IsLinkWithTargetType, Association.Association, Multiplicity.Multiplicity0N, DL.IDENT_IsLink);
		setup.putLinkTargets(DL.IDENT_IsLinkWithTargetType, Association.Aggregation, Multiplicity.Multiplicity01, DT.IDENT_IsType);
		
		setup.putLinkLabel(DL.IDENT_IsLinkWithTargetAssociation, "link-target-association");
		setup.putLinkSources(DL.IDENT_IsLinkWithTargetAssociation, Association.Association, Multiplicity.Multiplicity0N, DL.IDENT_IsLink);
		setup.putLinkTargets(DL.IDENT_IsLinkWithTargetAssociation, Association.Aggregation, Multiplicity.Multiplicity11, null);
		
		setup.putLinkLabel(DL.IDENT_IsLinkWithTargetMultiplicity, "link-target-multiplicity");
		setup.putLinkSources(DL.IDENT_IsLinkWithTargetMultiplicity, Association.Association, Multiplicity.Multiplicity0N, DL.IDENT_IsLink);
		setup.putLinkTargets(DL.IDENT_IsLinkWithTargetMultiplicity, Association.Aggregation, Multiplicity.Multiplicity11, null);
		
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
			DT.IDENT_IsTypeWithIdent, DT.IDENT_IsTypeWithLabel, DT.IDENT_IsTypeWithInstance, DL.IDENT_IsLinkWithIdent, DL.IDENT_IsLinkWithLabel,
			DL.IDENT_IsLinkWithSourceType, DL.IDENT_IsLinkWithSourceAssociation, DL.IDENT_IsLinkWithSourceMultiplicity, DL.IDENT_IsLinkWithTargetType,
			DL.IDENT_IsLinkWithTargetAssociation, DL.IDENT_IsLinkWithTargetMultiplicity);
	}

	private void checkTypeByTextMap(HashMap2<String, H2DT> typeByTextMap) {
		this.checkIdentMap(typeByTextMap, ident -> "DT with ident " + ident + " is missing", DT.IDENT_IsType, DL.IDENT_IsLink);
	}

	protected static class Setup {

		public void putLink(String ident, Consumer<H2DL> setup) {
			this.setupLinkMap.install(ident, ignored -> new ArrayList<>()).add(setup);
		}

		public void putLinkLabel(String ident, String label) {
			this.putLink(ident, link -> link.labelAsString().set(label));
		}

		public void putLinkSources(String ident, DL.Association sourceAssociationAsEnum, DL.Multiplicity sourceMultiplicityAsEnum, String sourceTypeAsIdent) {
			this.putLink(ident, link -> {
				link.sourceAssociationAsEnum().set(sourceAssociationAsEnum);
				link.sourceMultiplicityAsEnum().set(sourceMultiplicityAsEnum);
				link.sourceTypeAsType().set(link.parent.getType(sourceTypeAsIdent));
			});
		}

		public void putLinkTargets(String ident, DL.Association association, DL.Multiplicity multiplicity, String  typeIdents) {
			this.putLink(ident, link -> {
				link.targetAssociationAsEnum().set(association);
				link.targetMultiplicityAsEnum().set(multiplicity);
				link.targetTypeAsType().set(link.parent.getType(typeIdents));
			});
		}

		public void putType(String ident, Consumer<H2DT> setup) {
			this.setupTypeMap.install(ident, ignored -> new ArrayList<>()).add(setup);
		}

		public void putTypeLabel(String ident, String label) {
			this.putType(ident, type -> type.labelAsString().set(label));
		}

		final HashMap2<String, List<Consumer<H2DL>>> setupLinkMap = new HashMap2<>();

		final HashMap2<String, List<Consumer<H2DT>>> setupTypeMap = new HashMap2<>();

		 
		
	}

}
