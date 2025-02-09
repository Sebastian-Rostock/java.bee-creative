package bee.creative.kb;

import java.io.IOException;
import java.util.Random;
import bee.creative.emu.EMU;
import bee.creative.fem.FEMString;
import bee.creative.lang.Integers;
import bee.creative.lang.Objects;
import bee.creative.log.LOGBuilder;
import bee.creative.util.HashMapOI;
import bee.creative.util.Tester;

class TEST {

	public static void main(String[] args) throws IOException {

		var log = new LOGBuilder();
		log.enterScope("LALALA\nAAAA\nBBB");
		log.pushEntry("CC\nDD\nEE\n");
		log.pushEntry("54646546546.");
		log.leaveScope("close");
		
		System.out.println(log);
		
		
		{
			System.out.println("Value Test");
			var buf = new KBBuffer();
			var res1 = buf.putAllValues(FEMString.from("ABC"), FEMString.from("DEF"));
			System.out.println(res1);
			buf.putEdge(1, 3, 2);
			buf.putEdge(4, 6, 5);
			System.out.println(buf);
			System.out.println(buf.values().exceptValueRefs(-2));

			var persisted = ZIPDOS.deflate(buf::persist);
			var restored = ZIPDIS.inflate(persisted, KBState::from);
			System.out.println(restored);
		}
		new TEST();
	}

	private int edgeCount;

	public TEST() throws IOException {

		this.putRoot();

		// 1000 x TP
		// 1000000 BW
		// 1 BT
		// 1 VB
		// 100 BD
		// je note ein type und ein owner

		var TYPE_istTarifmodul = this.putType("istTarifmodul");

		var TYPE_istWegevariante = this.putType("istWegevariante");
		var LINK_istWegevarianteVonTarifmodul = this.putLink("istWegevarianteVonTarifmodul");

		var TYPE_istTarifpunkt = this.putType("istTarifpunkt");
		var LINK_istTarifpunktVonTarifmodul = this.putLink("istTarifpunktVonTarifmodul");

		var TYPE_istBewertungstyp = this.putType("istBewertungstyp");
		var LINK_istBewertungstypVonTarifmodul = this.putLink("istBewertungstypVonTarifmodul");
		var TYPE_istBewertungsdaten = this.putType("istBewertungsdaten");
		var LINK_istBewertungsdatenVonTarifmodul = this.putLink("istBewertungsdatenVonTarifmodul");

		var TYPE_istVerkehrsmittellinienbezug = this.putType("istVerkehrsmittellinienbezug");
		var LINK_istVerkehrsmittellinienbezugVonTarifmodul = this.putLink("istVerkehrsmittellinienbezugVonTarifmodul");

		var TYPE_istBewertung = this.putType("istBewertung");
		var LINK_istBewertungVonTarifmodul = this.putLink("istBewertungVonTarifmodul");
		var LINK_istBewertungMitBewertungstyp = this.putLink("istBewertungMitBewertungstyp");
		var LINK_istBewertungMitVontarifpunkt = this.putLink("istBewertungMitVontarifpunkt");
		var LINK_istBewertungMitNachtarifpunkt = this.putLink("istBewertungMitNachtarifpunkt");
		var LINK_istBewertungMitUeberwegevariante = this.putLink("istBewertungMitUeberwegevariante");
		var LINK_istBewertungMitBewertungsdaten = this.putLink("istBewertungMitBewertungsdaten");
		var LINK_istBewertungMitVerkehrsmittellinienbezug = this.putLink("istBewertungMitVerkehrsmittellinienbezug");

		var tarifmodul = this.putItem(TYPE_istTarifmodul);
		var wegevarianteList = this.putItems(TYPE_istWegevariante, LINK_istWegevarianteVonTarifmodul, tarifmodul, 50);
		var tarifpunktList = this.putItems(TYPE_istTarifpunkt, LINK_istTarifpunktVonTarifmodul, tarifmodul, 1000);
		var bewertungstyp = this.putItem(TYPE_istBewertungstyp, LINK_istBewertungstypVonTarifmodul, tarifmodul);
		var bewertungsdatenList = this.putItems(TYPE_istBewertungsdaten, LINK_istBewertungsdatenVonTarifmodul, tarifmodul, 100);
		var verkehrsmittellinienbezug = this.putItem(TYPE_istVerkehrsmittellinienbezug, LINK_istVerkehrsmittellinienbezugVonTarifmodul, tarifmodul);

		var r = new Random(0);

		for (int vontarifpunkt: tarifpunktList) {
			for (int nachtarifpunkt: tarifpunktList) {
				for (int wegevarianteCount = r.nextInt(4) + 1; wegevarianteCount != 0; wegevarianteCount--) {

					var bewertung = this.putItem(TYPE_istBewertung);

					this.putEdge(bewertung, LINK_istBewertungVonTarifmodul, tarifmodul);
					this.putEdge(bewertung, LINK_istBewertungMitBewertungstyp, bewertungstyp);
					this.putEdge(bewertung, LINK_istBewertungMitUeberwegevariante, wegevarianteList[r.nextInt(wegevarianteList.length)]);
					this.putEdge(bewertung, LINK_istBewertungMitVerkehrsmittellinienbezug, verkehrsmittellinienbezug);
					this.putEdge(bewertung, LINK_istBewertungMitBewertungsdaten, bewertungsdatenList[r.nextInt(bewertungsdatenList.length)]);
					this.putEdge(bewertung, LINK_istBewertungMitVontarifpunkt, vontarifpunkt);
					this.putEdge(bewertung, LINK_istBewertungMitNachtarifpunkt, nachtarifpunkt);

				}

			}
		}
		buffer.commit();
//		System.out //
//		.append(": EDGES=").append(this.edgeCount + "") //
//		.append(": EMU=").append(Integers.printSize(this.buffer.emu())) //
//		.append(" BYTES=").append(Integers.printSize(EMU.from(ZIPDOS.deflate(this.buffer::persist)))) //
//		;
		System.out.println();

		System.out.println(Objects.toStringCall(true, true, this, "linkMap", this.linkMap, "typeMap", this.typeMap, "buffer", this.buffer));

		System.out.println("PERSIST");
		var persisted = Tester.get(() -> ZIPDOS.deflate(this.buffer::persist));
		System.out.println(Integers.printSize(persisted.length));

		System.out.println("RESTORE");
		var restored = Tester.get(() -> ZIPDIS.inflate(persisted, KBState::from));
		System.out.println(Integers.printSize(restored.emu()));

//		System.out.println("INSERTS");
//		var ins = Tester.get(() -> KBState.from(this.buffer, restored));
//		System.out.println(ins);
		
//		System.out.println("DELETES");
//		var del = Tester.get(() -> KBState.from(restored, this.buffer));
//		System.out.println(del);
		
//		System.out.println(restored);
	}

	int putItem() {
		return this.buffer.getNextInternalRef();
	}

	int putItem(int typeRef) {
		var itemRef = this.putItem();
		this.setItemType(itemRef, typeRef);
		return itemRef;
	}

	int putItem(int typeRef, int istItemVonOwnerRef, int ownerRef) {
		var itemRef = this.putItem(typeRef);
		this.putEdge(itemRef, istItemVonOwnerRef, ownerRef);
		return itemRef;
	}

	int[] putItems(int typeRef, int count) {
		var itemRefs = new int[count];
		while (count > 0) {
			itemRefs[--count] = this.putItem(typeRef);
		}
		return itemRefs;
	}

	int[] putItems(int typeRef, int istItemVonOwnerRef, int ownerRef, int count) {
		var itemRefs = this.putItems(typeRef, count);
		for (var itemRef: itemRefs) {
			this.putEdge(itemRef, istItemVonOwnerRef, ownerRef);
		}
		return itemRefs;
	}

	int putText(String text) {
		return this.buffer.putValue(FEMString.from(text));
	}

	int putLink(String name) {
		return this.linkMap.install(name, item -> {
			var linkRef = this.putItem();
			this.setLinkName(linkRef, item);
			this.setItemType(linkRef, this.TYPE_istLink);
			return linkRef;
		});
	}

	int putType(String name) {
		return this.typeMap.install(name, item -> {
			var typeRef = this.putItem();
			this.setTypeName(typeRef, item);
			this.setItemType(typeRef, this.TYPE_istType);
			return typeRef;
		});
	}

	void putEdge(int sourceRef, int relationRef, int targetRef) {
		this.edgeCount++;
		this.buffer.putEdge(sourceRef, targetRef, relationRef);
	}

	private KBBuffer buffer = new KBBuffer();

	private HashMapOI<String> linkMap = new HashMapOI<>(64);

	private HashMapOI<String> typeMap = new HashMapOI<>(64);

	private int TYPE_istType;

	private int TYPE_istLink;

	private int LINK_istNameVonLink;

	private int LINK_istNameVonType;

	private int LINK_istItemVonType;

	private void putRoot() {

		this.TYPE_istType = this.putItem();
		this.TYPE_istLink = this.putItem();
		this.LINK_istNameVonLink = this.putItem();
		this.LINK_istNameVonType = this.putLink("istNameVonType");
		this.LINK_istItemVonType = this.putLink("istItemVonType");

		this.setTypeName(this.TYPE_istType, "istType");
		this.setItemType(this.TYPE_istType, this.TYPE_istType);
		this.setItemType(this.TYPE_istLink, this.TYPE_istType);

		this.setLinkName(this.LINK_istNameVonLink, "istNameVonLink");
		this.setItemType(this.LINK_istNameVonLink, this.TYPE_istLink);

	}

	private void setItemType(int itemRef, int typeRef) {
		this.putEdge(itemRef, this.LINK_istItemVonType, typeRef);
	}

	private void setLinkName(int linkRef, String name) {
		this.putEdge(linkRef, this.LINK_istNameVonLink, this.putText(name));
	}

	private void setTypeName(int typeRef, String name) {
		this.putEdge(typeRef, this.LINK_istNameVonType, this.putText(name));
	}

}
