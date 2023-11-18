package bee.creative.qs.h2.ds;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import bee.creative.qs.QN;
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
import bee.creative.util.Filters;
import bee.creative.util.Getter;
import bee.creative.util.HashMap2;
import bee.creative.util.Iterables;
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
	public H2DL getLink(String ident) {
		if (this.linkMap == null) {
			this.update();
		}
		return this.linkMap.get(ident);
	}

	@Override
	public H2DT getType(String ident) {
		if (this.typeMap == null) {
			this.update();
		}
		return this.typeMap.get(ident);
	}

	@Override
	public Translator2<QN, DL> linkTrans() {
		return this.linkTrans == null ? this.linkTrans = Translators.from(QN.class, DL.class, this::asLink, DL::node).optionalize() : this.linkTrans;
	}

	@Override
	public Translator2<QN, DT> typeTrans() {
		return this.typeTrans == null ? this.typeTrans = Translators.from(QN.class, DT.class, this::asType, DT::node).optionalize() : this.typeTrans;
	}

	/** Diese Methode übernimmt Änderungen an {@link DE#idents()} auf die internen Puffer zur Beschleunigung von {@link #getLink(String)} und
	 * {@link #getType(String)}. Wenn das {@link DL Datenfeld} zu {@link DL#IDENT_IsLinkWithIdent} nicht verwendet wird, wird das Datenmodell initialisiert. Wenn
	 * essentielle Datenfelder oder Datentypen nicht ermittelt werden können, wird eine Ausnahme ausgelöst. */
	public void update() {
		var rollback = true;
		var linkMapBackup = this.linkMap;
		var typeMapBackup = this.typeMap;
		try {
			this.linkMap = new HashMap2<>(100);
			this.typeMap = new HashMap2<>(100);
			var isLinkWithIdent = this.asLink(this.parent.install(DL.IDENT_IsLinkWithIdent));
			this.setupIdentMap(isLinkWithIdent, this.linkMap, this::asLink, ident -> "DL with ident " + ident + " is not unique");
			if (this.linkMap.isEmpty()) {
				this.linkMap.put(DL.IDENT_IsLinkWithIdent, isLinkWithIdent);
				this.setup();
			} else {
				if (!isLinkWithIdent.equals(this.linkMap.get(DL.IDENT_IsLinkWithIdent))) throw new IllegalStateException();
			}
			this.checkIdentMap(this.linkMap, ident -> "DL with ident " + ident + " is missing", //
				DT.IDENT_IsTypeWithIdent, DT.IDENT_IsTypeWithLabel, DT.IDENT_IsTypeWithInstance, //
				DL.IDENT_IsLinkWithIdent, DL.IDENT_IsLinkWithLabel, //
				DL.IDENT_IsLinkWithSourceType, DL.IDENT_IsLinkWithSourceHandling, DL.IDENT_IsLinkWithSourceMultiplicity, //
				DL.IDENT_IsLinkWithTargetType, DL.IDENT_IsLinkWithTargetHandling, DL.IDENT_IsLinkWithTargetMultiplicity);
			var isTypeWithIdent = this.getLink(DT.IDENT_IsTypeWithIdent);
			this.setupIdentMap(isTypeWithIdent, this.typeMap, this::asType, ident -> "DT with ident " + ident + " is not unique");
			this.checkIdentMap(this.typeMap, ident -> "DT with ident " + ident + " is missing", //
				DT.IDENT_IsType, DL.IDENT_IsLink);
			this.linkMap.compact();
			this.typeMap.compact();
			rollback = false;
		} finally {
			if (rollback) {
				this.linkMap = linkMapBackup;
				this.typeMap = typeMapBackup;
			}
		}
	}

	protected H2DL asLink(QN node) {
		return new H2DL(this, this.context.owner.asQN(node));
	}

	protected H2DT asType(QN node) {
		return new H2DT(this, this.context.owner.asQN(node));
	}

	protected void setup() {
		var setup = new Setup();
		this.setup(setup);
		setup.install();
	}

	protected void setup(Setup setup) {
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

	protected H2DL installLink(String ident) {
		return this.linkMap.install(ident, value -> this.asLink(this.context.owner.newNode()));
	}

	protected H2QN installLinkNode(String ident) {
		return this.installLink(ident).node;
	}

	protected H2DT installType(String ident) {
		return this.typeMap.install(ident, value -> this.asType(this.context.owner.newNode()));
	}

	protected H2QN installTypeNode(String ident) {
		return this.installType(ident).node;
	}

	/** Diese Methode liest die {@link DL#edges() Hyperkanten} des gegebenen {@link DL Datenfelds} ein und bestück damit die gegebenen Abbildungen.
	 *
	 * @param <GItem>
	 * @param isItemWithIdent Datenfeld, dessen {@link DL#edges() Hyperkanten} eingelesen werden.
	 * @param itemByIdentText
	 * @param nodeAsItem
	 * @param errorMessageByIdentNode */
	protected <GItem> void setupIdentMap(DL isItemWithIdent, Map<? super String, GItem> itemByIdentText, Getter<QN, GItem> nodeAsItem,
		Getter<QN, String> errorMessageByIdentNode) {
		var itemNodeByIdentNodeMap = new HashMap2<QN, QN>(100);
		isItemWithIdent.edges().forEach(edge -> {
			if (itemNodeByIdentNodeMap.put(edge.object(), edge.subject()) != null) throw new IllegalArgumentException(errorMessageByIdentNode.get(edge.object()));
		});

		var identTextByIdentNodeMap = new HashMap2<QN, String>(itemNodeByIdentNodeMap.size());
		isItemWithIdent.owner().newNodes(itemNodeByIdentNodeMap.keySet()).values(identTextByIdentNodeMap::put);

		var itemByItemNodeMap = new HashMap2<QN, GItem>(100);
		itemNodeByIdentNodeMap.values().iterator().toSet().forEach(itemNode -> itemByItemNodeMap.put(itemNode, nodeAsItem.get(itemNode)));

		itemNodeByIdentNodeMap.forEach((identNode, itemNode) -> {
			itemByIdentText.put(identTextByIdentNodeMap.get(identNode), itemByItemNodeMap.get(itemNode));
		});
	}

	/** Diese Methode TODO */
	protected void checkIdentMap(Map<String, ?> itemByIdentTextMap, Getter<String, String> getErrorByIdentText, String... idents) {
		for (var ident: idents) {
			if (itemByIdentTextMap.get(ident) == null) throw new IllegalArgumentException(getErrorByIdentText.get(ident));
		}
	}

	H2DM(H2DS parent, H2QN context) {
		this.parent = parent;
		this.context = context;
	}

	private Translator2<QN, DL> linkTrans;

	private HashMap2<String, H2DL> linkMap;

	private Translator2<QN, DT> typeTrans;

	private HashMap2<String, H2DT> typeMap;

	{}

	protected class Setup {

		public void putType(String ident, String label) {
			this.typeSetupMap.put(ident, Arrays.asList(ident, label));
		}

		public void putLink(String ident, String label, String sourceTypeIdent, Handling sourceHandling, Multiplicity sourceMultiplicity, String targetTypeIdent,
			Handling targetHandling, Multiplicity targetMultiplicity) {
			this.linkSetupMap.put(ident, Arrays.asList(ident, label, //
				sourceTypeIdent, Handling.trans.toSource(sourceHandling), Multiplicity.trans.toSource(sourceMultiplicity), //
				targetTypeIdent, Handling.trans.toSource(targetHandling), Multiplicity.trans.toSource(targetMultiplicity)));
		}

		public void install() {
			var that = H2DM.this;
			var installValueNode = this.installValues();

			// TODO

			that.installLink(DT.IDENT_IsTypeWithIdent).setTargetMap(this.gm(this.typeSetupMap, 0, that::installTypeNode, installValueNode::get));
			that.installLink(DT.IDENT_IsTypeWithLabel).setTargetMap(this.gm(this.typeSetupMap, 1, that::installTypeNode, installValueNode::get));

			that.installLink(DL.IDENT_IsLinkWithIdent).setTargetMap(this.gm(this.linkSetupMap, 0, that::installLinkNode, installValueNode::get));
			that.installLink(DL.IDENT_IsLinkWithLabel).setTargetMap(this.gm(this.linkSetupMap, 1, that::installLinkNode, installValueNode::get));
			that.installLink(DL.IDENT_IsLinkWithSourceType).setTargetMap(this.gm(this.linkSetupMap, 2, that::installLinkNode, that::installTypeNode));
			that.installLink(DL.IDENT_IsLinkWithSourceHandling).setTargetMap(this.gm(this.linkSetupMap, 3, that::installLinkNode, installValueNode::get));
			that.installLink(DL.IDENT_IsLinkWithSourceMultiplicity).setTargetMap(this.gm(this.linkSetupMap, 4, that::installLinkNode, installValueNode::get));
			that.installLink(DL.IDENT_IsLinkWithTargetType).setTargetMap(this.gm(this.linkSetupMap, 5, that::installLinkNode, that::installTypeNode));
			that.installLink(DL.IDENT_IsLinkWithTargetHandling).setTargetMap(this.gm(this.linkSetupMap, 6, that::installLinkNode, installValueNode::get));
			that.installLink(DL.IDENT_IsLinkWithTargetMultiplicity).setTargetMap(this.gm(this.linkSetupMap, 7, that::installLinkNode, installValueNode::get));

			that.typesAsTypes().addAll(that.typeMap.values());
			that.linksAsLinks().addAll(that.linkMap.values());
		}

		HashMap2<String, QN> installValues() {
			var values = H2DM.this.owner().newValues(Iterables.concat(this.linkSetupMap.keySet().concat(this.typeSetupMap.keySet()),
				Iterables.concatAll(this.linkSetupMap.values().concat(this.typeSetupMap.values()))).filter(Filters.empty()));
			var result = new HashMap2<String, QN>();
			values.putAll();
			values.nodes(result::put);
			return result;
		}

		HashMap2<QN, QN> gm(HashMap2<String, List<String>> setupMap, int index, Getter<String, QN> asKey, Getter<String, QN> asValue) {
			var result = new HashMap2<QN, QN>();
			setupMap.forEach((key, values) -> {
				var value = values.get(index);
				if (value != null) {
					result.put(asKey.get(key), asValue.get(value));
				}
			});
			return result;
		}

		final HashMap2<String, List<String>> linkSetupMap = new HashMap2<>();

		final HashMap2<String, List<String>> typeSetupMap = new HashMap2<>();

	}

}
