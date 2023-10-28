package bee.creative.qs.ds.h2;

import java.util.ArrayList;
import java.util.List;
import bee.creative.qs.QN;
import bee.creative.qs.ds.DH;
import bee.creative.qs.ds.DL;
import bee.creative.qs.ds.DM;
import bee.creative.qs.ds.DT;
import bee.creative.qs.h2.H2QESet;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QS;
import bee.creative.util.Consumer;
import bee.creative.util.Entries;
import bee.creative.util.Getter;
import bee.creative.util.HashMap2;
import bee.creative.util.HashSet2;
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
	public DL getLink(String ident) {
		// TODO
		return null;
	}

	@Override
	public DT getType(String ident) {
		// TODO
		return null;
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
	public void updateIdents() {
		final var owner = this.context.owner;
		var isEnumWithIdentNode = owner.edges().havingContext(this.context).havingPredicate(this.context).havingObject(this.context).subjects().first();
		if (isEnumWithIdentNode == null) {
			isEnumWithIdentNode = owner.newNode();
			owner.newEdge(this.context, isEnumWithIdentNode, this.context, this.context).put();
		}
		this.isLinkWithIdent = this.asLink(isEnumWithIdentNode);

		var asLink = (Getter<QN, H2DL>)this::asLink;
		var asLinkMap = new HashMap2<QN, H2DL>(100);
		this.linkByNodeMap.clear();
		this.linkByTextMap.clear();
		this.isLinkWithIdent.edges().iterator().collectAll(edge -> this.linkByNodeMap.put(edge.object(), asLinkMap.install(edge.subject(), asLink)));
		owner.newNodes(this.linkByNodeMap.keySet()).values((node, text) -> this.linkByTextMap.put(text, this.linkByNodeMap.get(node)));

		this.setupLinkMap = new HashMap2<>(10);
		this.setupTypeMap = new HashMap2<>(10);
		this.customSetup();
		var textSet = owner.newValues(this.setupLinkMap.keySet().concat(this.setupTypeMap.keySet()));
		var textNodeMap = new HashMap2<String, QN>(100);
		textSet.putAll();
		textSet.nodes(textNodeMap::put);

		var l= new HashSet2<String>(100);
		
		var asLink2 = (Getter<String, H2DL>)text -> this.linkByNodeMap.install(textNodeMap.get(text), asLink);
		
		this.setupLinkMap.forEach((linkIdent, setupList) -> {
			// TODO fehlende knoten ergänzen
			this.linkByTextMap.install(linkIdent, asLink2);
		});

		// todo setup für ergänzung fehlender links

		// todo types

		this.setupLinkMap = null;
		this.setupTypeMap = null;

		var m = new HashMap2<String, QN>();

		// TODO
	}

	@Override
	public Translator2<QN, DL> linkTrans() {
		return this.linkTrans;
	}

	@Override
	public Translator2<QN, DT> typeTrans() {
		return this.typeTrans;
	}

	H2DL isEnumWithLabel;

	H2DL isLinkWithIdent;

	H2DL isTypeWithIdent;

	final Translator2<QN, DL> linkTrans = Translators.from(QN.class, DL.class, this::asLink, DL::node).optionalize();

	final Translator2<QN, DT> typeTrans = Translators.from(QN.class, DT.class, this::asType, DT::node).optionalize();

	HashMap2<QN, H2DL> linkByNodeMap = new HashMap2<>();

	HashMap2<String, H2DL> linkByTextMap = new HashMap2<>();

	private HashMap2<String, List<Consumer<H2DL>>> setupLinkMap;

	private HashMap2<String, List<Consumer<H2DT>>> setupTypeMap;

	H2DL asLink(QN node) {
		return new H2DL(this, this.context.owner.asQN(node));
	}

	H2DT asType(QN node) {
		return new H2DT(this, this.context.owner.asQN(node));
	}

}
