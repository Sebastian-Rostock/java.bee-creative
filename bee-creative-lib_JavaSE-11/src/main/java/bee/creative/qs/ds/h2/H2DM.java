package bee.creative.qs.ds.h2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import bee.creative.qs.QN;
import bee.creative.qs.ds.DH;
import bee.creative.qs.ds.DL;
import bee.creative.qs.ds.DM;
import bee.creative.qs.ds.DT;
import bee.creative.qs.h2.H2QESet;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QS;
import bee.creative.util.Consumer;
import bee.creative.util.Getter;
import bee.creative.util.HashMap2;
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
		H2DL isLinkWithIdent;
		H2DL isTypeWithIdent;
		{
			var isLinkWithIdent_Ident = this.owner().newNode(DL.IDENT_IsLinkWithIdent);
			var isLinkWithIdent_EdgeList = this.edges().havingObject(isLinkWithIdent_Ident) //
				.iterator().filter(edge -> edge.subject().equals(edge.predicate())).toList();
			if (isLinkWithIdent_EdgeList.size() > 1) //
				throw new IllegalArgumentException("DL with ident " + DL.IDENT_IsLinkWithIdent + " is not unique");
			if (isLinkWithIdent_EdgeList.isEmpty()) {
				isLinkWithIdent = this.asLink(this.owner().newNode());
				isLinkWithIdent.setTarget(isLinkWithIdent.node, isLinkWithIdent_Ident);
				// TODO alle installieren

				var nodeMap = new HashMap2<String, QN>(100);
				var textSet = this.owner().newValues(this.setupLinkMap.keySet().concat(this.setupTypeMap.keySet()));
				textSet.putAll();
				textSet.nodes(nodeMap::put);

				this.setupLinkMap.keySet().removeAll(this.linkByTextMap.keySet());
				this.setupLinkMap.keySet().forEach(text -> {
					var node = nodeMap.get(text);
					var link = this.asLink(node);
					this.linkByNodeMap.put(node, link);
					this.linkByTextMap.put(text, link);
				});

			} else {
				isLinkWithIdent = this.asLink(isLinkWithIdent_EdgeList.get(0).predicate());
			}
		}
		var linkByTextMap = new HashMap2<String, H2DL>(100);
		var linkByNodeMap = new HashMap2<QN, H2DL>(100);
		{
			this.setupIdentMaps(isLinkWithIdent, linkByNodeMap::put, linkByTextMap::put, this::asLink, ident -> "DL with ident " + ident + " is not unique");
			linkByTextMap.compact();
			linkByNodeMap.compact();
			this.checkIdentMap(linkByTextMap, ident -> "DL with ident " + ident + " is missing", //
				DT.IDENT_IsTypeWithIdent, DT.IDENT_IsTypeWithLabel, DT.IDENT_IsTypeWithInstance, DL.IDENT_IsLinkWithIdent, DL.IDENT_IsLinkWithLabel,
				DL.IDENT_IsLinkWithSourceType, DL.IDENT_IsLinkWithSourceAssociation, DL.IDENT_IsLinkWithSourceMultiplicity, DL.IDENT_IsLinkWithTargetType,
				DL.IDENT_IsLinkWithTargetAssociation, DL.IDENT_IsLinkWithTargetMultiplicity);
		}
		var typeByTextMap = new HashMap2<String, H2DT>(100);
		var typeByNodeMap = new HashMap2<QN, H2DT>(100);
		{
			isTypeWithIdent = linkByTextMap.get(DT.IDENT_IsTypeWithIdent);
			this.setupIdentMaps(isTypeWithIdent, typeByNodeMap::put, typeByTextMap::put, this::asType, ident -> "DT with ident " + ident + " is not unique");
			typeByNodeMap.compact();
			typeByTextMap.compact();
			this.checkIdentMap(typeByTextMap, ident -> "DT with ident " + ident + " is missing", DT.IDENT_IsType, DL.IDENT_IsLink);
		}
		this.linkByNodeMap = linkByNodeMap;
		this.linkByTextMap = linkByTextMap;
		this.typeByNodeMap = typeByNodeMap;
		this.typeByTextMap = typeByTextMap;
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

	protected void customSetupLink(String ident, Consumer<H2DL> onSetup) {
		this.setupLinkMap.install(ident, v -> new ArrayList<>()).add(onSetup);
	}

	protected void customSetup() {
	}

	protected void customSetupType(HashMap2<String, Consumer<H2DL>> res, String ident, String label, Iterable<String> sourceTypes,
		DL.Association sourceAssociation, DL.Multiplicity sourceMultiplicity, Iterable<String> targetTypes, DL.Association targetAssociation,
		DL.Multiplicity targetMultiplicity) {

	}

	protected void customSetupLink(String ident, String label, Iterable<String> sourceTypes, DL.Association sourceAssociation, DL.Multiplicity sourceMultiplicity,
		Iterable<String> targetTypes, DL.Association targetAssociation, DL.Multiplicity targetMultiplicity) {

	}

	protected void setupLinkSourceTypes(String linkIdent, String... sourceTypeIdents) {

	}

	protected void setupLinkTargetTypes(String linkIdent, String... targetTypeIdents) {

	}

	@Override
	public Translator2<QN, DL> linkTrans() {
		return this.linkTrans;
	}

	@Override
	public Translator2<QN, DT> typeTrans() {
		return this.typeTrans;
	}

	private final Translator2<QN, DL> linkTrans = Translators.from(QN.class, DL.class, this::asLink, DL::node).optionalize();

	private HashMap2<String, H2DL> linkByTextMap = new HashMap2<>();

	private HashMap2<QN, H2DL> linkByNodeMap = new HashMap2<>();

	private final Translator2<QN, DT> typeTrans = Translators.from(QN.class, DT.class, this::asType, DT::node).optionalize();

	private HashMap2<String, H2DT> typeByTextMap = new HashMap2<>();

	private HashMap2<QN, H2DT> typeByNodeMap = new HashMap2<>();

	H2DL asLink(QN node) {
		return new H2DL(this, this.context.owner.asQN(node));
	}

	H2DT asType(QN node) {
		return new H2DT(this, this.context.owner.asQN(node));
	}

	private final HashMap2<String, List<Consumer<H2DL>>> setupLinkMap = new HashMap2<>();

	private final HashMap2<String, List<Consumer<H2DT>>> setupTypeMap = new HashMap2<>();

}
