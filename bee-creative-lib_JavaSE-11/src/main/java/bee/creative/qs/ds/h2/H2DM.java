package bee.creative.qs.ds.h2;

import static bee.creative.qs.ds.DL.Handling.handlingTrans;
import static bee.creative.qs.ds.DL.Multiplicity.multiplicityTrans;
import static bee.creative.util.Translators.translatorFromClass;
import bee.creative.qs.QN;
import bee.creative.qs.ds.DC;
import bee.creative.qs.ds.DE;
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
import bee.creative.util.Getters;
import bee.creative.util.HashMap2;
import bee.creative.util.Hashers;
import bee.creative.util.Translator3;

public class H2DM implements DM {

	public final H2DS parent;

	public final H2QN context;

	public H2DM(H2DS parent) {
		this(parent, parent.store().newNode());
		parent.modelsAsNodes().add(this.context);
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
	public H2DL getLink(String ident) {
		if (this.linkMap == null) {
			this.linkMap = new HashMap2<>(100);
			var edgeList = this.edges().havingObject(this.owner().newNode(DL.IDENT_IsLinkWithIdent)).iterator() //
				.filter(edge -> edge.subject().equals(edge.predicate())).toList();
			if (edgeList.isEmpty()) return null;
			var asError = (Getter<Object, String>)value -> "DL with ident " + value + " is not unique";
			if (edgeList.size() > 1) throw new IllegalArgumentException(asError.get(DL.IDENT_IsLinkWithIdent));
			this.setupItemMap(this.linkMap, this.asLink(edgeList.get(0).predicate()), this::asLink, asError);
		}
		return this.linkMap.get(ident);
	}

	@Override
	public H2DT getType(String ident) {
		if (this.typeMap == null) {
			this.typeMap = new HashMap2<>(100);
			var identLink = this.getLink(DT.IDENT_IsTypeWithIdent);
			if (identLink == null) return null;
			this.setupItemMap(this.typeMap, identLink, this::asType, value -> "DT with ident " + value + " is not unique");
		}
		return this.typeMap.get(ident);
	}

	public void install() {
		this.getType(null);

		this.installType(DT.IDENT_IsType, "domain-type");
		this.installType(DL.IDENT_IsLink, "domain-link");

		this.installLink(DT.IDENT_IsTypeWithIdent, "type-idents", //
			DT.IDENT_IsType, Handling.Association, Multiplicity.Multiplicity11, null, Handling.Association, Multiplicity.Multiplicity1N);
		this.installLink(DT.IDENT_IsTypeWithLabel, "type-label", DT.IDENT_IsType, Handling.Association, Multiplicity.Multiplicity1N, null, Handling.Aggregation,
			Multiplicity.Multiplicity01);
		this.installLink(DT.IDENT_IsTypeWithInstance, "type-instances", //
			DT.IDENT_IsType, Handling.Aggregation, Multiplicity.Multiplicity11, null, Handling.Association, Multiplicity.Multiplicity0N);
		this.installLink(DL.IDENT_IsLinkWithIdent, "link-idents", //
			DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity11, null, Handling.Association, Multiplicity.Multiplicity1N);
		this.installLink(DL.IDENT_IsLinkWithLabel, "link-label", //
			DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity1N, null, Handling.Aggregation, Multiplicity.Multiplicity01);
		this.installLink(DL.IDENT_IsLinkWithSubjectType, "link-source-type", //
			DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity0N, DT.IDENT_IsType, Handling.Aggregation, Multiplicity.Multiplicity01);
		this.installLink(DL.IDENT_IsLinkWithSubjectHandling, "link-source-handling", //
			DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity0N, null, Handling.Aggregation, Multiplicity.Multiplicity11);
		this.installLink(DL.IDENT_IsLinkWithSubjectMultiplicity, "link-source-multiplicity", //
			DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity0N, null, Handling.Aggregation, Multiplicity.Multiplicity11);
		this.installLink(DL.IDENT_IsLinkWithObjectType, "link-target-type", //
			DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity0N, DT.IDENT_IsType, Handling.Aggregation, Multiplicity.Multiplicity01);
		this.installLink(DL.IDENT_IsLinkWithObjectHandling, "link-target-handling", //
			DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity0N, null, Handling.Aggregation, Multiplicity.Multiplicity11);
		this.installLink(DL.IDENT_IsLinkWithObjectMultiplicity, "link-target-multiplicity", //
			DL.IDENT_IsLink, Handling.Association, Multiplicity.Multiplicity0N, null, Handling.Aggregation, Multiplicity.Multiplicity11);

		this.installInstances(DT.IDENT_IsType, this.typeMap.values());
		this.installInstances(DL.IDENT_IsLink, this.linkMap.values());
	}

	/** Diese Methode übernimmt Änderungen an {@link DE#identsAsNodes()} auf die internen Puffer zur Beschleunigung von {@link #getLink(String)} und
	 * {@link #getType(String)}. Wenn das {@link DL Datenfeld} zu {@link DL#IDENT_IsLinkWithIdent} nicht verwendet wird, wird das Datenmodell initialisiert. Wenn
	 * essentielle Datenfelder oder Datentypen nicht ermittelt werden können, wird eine Ausnahme ausgelöst. */
	public void invalidateIdents() {
		this.linkMap = null;
		this.typeMap = null;
	}

	@Override
	public Translator3<QN, DL> linkTrans() {
		return this.linkTrans == null ? this.linkTrans = translatorFromClass(QN.class, DL.class, this::asLink, DL::node).optionalize() : this.linkTrans;
	}

	@Override
	public Translator3<QN, DT> typeTrans() {
		return this.typeTrans == null ? this.typeTrans = translatorFromClass(QN.class, DT.class, this::asType, DT::node).optionalize() : this.typeTrans;
	}

	@Override
	public Translator3<QN, DC> changeTrans() {
		return this.changeTrans == null ? this.changeTrans = translatorFromClass(QN.class, DC.class, this::asChange, DC::node).optionalize() : this.changeTrans;
	}

	protected H2DL asLink(QN node) {
		return new H2DL(this, this.context.owner.asQN(node));
	}

	protected H2DT asType(QN node) {
		return new H2DT(this, this.context.owner.asQN(node));
	}

	protected H2DC asChange(QN node) {
		return new H2DC(this, this.context.owner.asQN(node));
	}

	/** Diese Methode liefert den unter dem gegebenen {@link DL#idents() Erkennungstextwert} in der {@link #linkMap} hinterlegten {@link DL Datentyp} und
	 * {@link HashMap2#install(Object, Getter) erzeugt} diesen bei Bedarf, sofern {@code ident} nicht {@code null} ist. Andernfalls wird {@code null}
	 * geliefert. */
	protected H2DL installLink(String ident) {
		return ident != null ? this.linkMap.install(ident, value -> this.asLink(this.context.owner.newNode())) : null;
	}

	protected H2DT installType(String ident, String label) {
		var type = this.installType(ident);
		this.installTargets(DT.IDENT_IsTypeWithIdent, type).asValueSet().add(ident);
		this.installTargets(DT.IDENT_IsTypeWithLabel, type).asValue().set(label);
		return type;
	}

	protected H2DT installType(String ident) {
		return ident != null ? this.typeMap.install(ident, value -> this.asType(this.context.owner.newNode())) : null;
	}

	protected H2DL installLink(String ident, String label, String sourceTypeIdent, Handling sourceHandling, Multiplicity sourceMultiplicity,
		String targetTypeIdent, Handling targetHandling, Multiplicity targetMultiplicity) {
		var link = this.installLink(ident);
		this.installTargets(DL.IDENT_IsLinkWithIdent, link).asValueSet().add(ident);
		this.installTargets(DL.IDENT_IsLinkWithLabel, link).asValue().set(label);
		this.installTargets(DL.IDENT_IsLinkWithSubjectType, link).asNode().translate(this.typeTrans()).set(this.installType(sourceTypeIdent));
		this.installTargets(DL.IDENT_IsLinkWithSubjectHandling, link).asValue().set(handlingTrans().toSource(sourceHandling));
		this.installTargets(DL.IDENT_IsLinkWithSubjectMultiplicity, link).asValue().set(multiplicityTrans().toSource(sourceMultiplicity));
		this.installTargets(DL.IDENT_IsLinkWithObjectType, link).asNode().translate(this.typeTrans()).set(this.installType(targetTypeIdent));
		this.installTargets(DL.IDENT_IsLinkWithObjectHandling, link).asValue().set(handlingTrans().toSource(targetHandling));
		this.installTargets(DL.IDENT_IsLinkWithObjectMultiplicity, link).asValue().set(multiplicityTrans().toSource(targetMultiplicity));
		this.installSources(DT.IDENT_IsTypeWithInstance, link).asNode().translate(this.typeTrans()).set(this.installType(DL.IDENT_IsLink));
		return link;
	}

	protected H2DLTSet installTargets(String linkIdent, H2DN source) {
		return this.installLink(linkIdent).getObjects(source.node);
	}

	protected H2DLSSet installSources(String linkIdent, H2DN target) {
		return this.installLink(linkIdent).getSubjects(target.node);
	}

	protected void installInstances(String typeIdent, Iterable<? extends H2DN> instances) {
		var typeMap = new HashMap2<QN, QN>();
		typeMap.putAll(instances, H2DN::node, Getters.getterFromValue(this.installType(typeIdent).node));
		this.installLink(DT.IDENT_IsTypeWithInstance).setSubjectMap(typeMap);
	}

	protected <GItem> void setupItemMap(HashMap2<String, GItem> itemByIdentMap, H2DL identLink, Getter<QN, GItem> asItem, Getter<Object, String> asError) {
		var itemNodeByIdentNodeMap = new HashMap2<QN, QN>(100);
		identLink.edges().forEach(edge -> {
			if (itemNodeByIdentNodeMap.put(edge.object(), edge.subject()) != null) throw new IllegalArgumentException(asError.get(edge.object()));
		});
		var identByNodeMap = new HashMap2<QN, String>(itemNodeByIdentNodeMap.size());
		identLink.owner().newNodes(itemNodeByIdentNodeMap.keySet()).values(identByNodeMap::put);
		var itemByNodeMap = HashMap2.hashMapFrom(Hashers.naturalHasher(), asItem);
		itemNodeByIdentNodeMap.forEach((identNode, itemNode) -> itemByIdentMap.put(identByNodeMap.get(identNode), itemByNodeMap.install(itemNode)));
	}

	H2DM(H2DS parent, H2QN context) {
		this.parent = parent;
		this.context = context;
	}

	private HashMap2<String, H2DL> linkMap;

	private Translator3<QN, DL> linkTrans;

	private HashMap2<String, H2DT> typeMap;

	private Translator3<QN, DT> typeTrans;

	private Translator3<QN, DC> changeTrans;

}
