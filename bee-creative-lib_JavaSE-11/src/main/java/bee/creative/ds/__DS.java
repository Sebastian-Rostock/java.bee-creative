package bee.creative.ds;

// als mutex verwenden
class __DS {

	final __DSSeqPool seqPool = new __DSSeqPool(this);

	final __DSSetPool setPool = new __DSSetPool(this);

	final __DSMapPool mapPool = new __DSMapPool(this);

	public static void main(String[] args) throws Exception {
		var d = new __DS();
		System.out.println(d);
	}

}
