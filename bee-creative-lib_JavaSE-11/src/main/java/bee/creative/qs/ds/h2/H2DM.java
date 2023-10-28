package bee.creative.qs.ds.h2;

import bee.creative.qs.QN;
import bee.creative.qs.ds.DH;
import bee.creative.qs.ds.DL;
import bee.creative.qs.ds.DM;
import bee.creative.qs.ds.DT;
import bee.creative.qs.h2.H2QESet;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QS;
import bee.creative.util.Translator2;
import bee.creative.util.Translators;

public class H2DM implements DM {


	public final H2QN context;

	public H2DM(  H2QN context) {
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

	@Override
	public void updateIdents() {
		var x = edges().havingPredicate(context).havingObject(context).subjects().first();
		
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
	
	H2DL isEnumWithIdent;
	
	final Translator2<QN, DL> linkTrans = Translators.from(QN.class, DL.class, this::asLink, DL::node).optionalize();

	final Translator2<QN, DT> typeTrans = Translators.from(QN.class, DT.class, this::asType, DT::node).optionalize();

	H2DL asLink(QN node) {
		return new H2DL(this, this.context.owner.asQN(node));
	}

	H2DT asType(QN node) {
		return new H2DT(this, this.context.owner.asQN(node));
	}

}
