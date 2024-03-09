package bee.creative.ds;

class __DSSeqPool extends __DSArrPool {

	__DSSeqPool(__DS owner) {
		super(owner);
	}

	__DSSeq createSeq() {
		return new __DSSeq(this, insert(new int[2]));
	}

}
