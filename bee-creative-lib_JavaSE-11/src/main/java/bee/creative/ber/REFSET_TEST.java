package bee.creative.ber;

class REFSET_TEST {

	public static void main(String[] args) {

		var a = REFSET.from(0, 0);
		print(a);

		a = REFSET.grow(a);
		REFSET.putRef(a, 1);
		print(a);

		a = REFSET.grow(a);
		REFSET.putRef(a, 2);
		print(a);

		a = REFSET.grow(a);
		REFSET.putRef(a, 4);
		print(a);
		
		a = REFSET.grow(a);
		REFSET.putRef(a, 5);
		print(a);

		a = REFSET.grow(a);
		REFSET.putRef(a, 8);
		print(a);

		REFSET.popRef(a, 2);
		a = REFSET.pack(a);
		print(a);

		REFSET.popRef(a, 5);
		a = REFSET.pack(a);
		print(a);

		
		var e = new BERStore();
		e.putEdge(1, 2, 3);
		e.putEdge(1, 2, 4);
		e.putEdge(1, 3, 4);
		e.putEdge(2, 2, 4);
		System.out.println(e);
		
	}

	private static void print(int[] a) {
		System.out.println(REFSET.toString(a));
	}

}
