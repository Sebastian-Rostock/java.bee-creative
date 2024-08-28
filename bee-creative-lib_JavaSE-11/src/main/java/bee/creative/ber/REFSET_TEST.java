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

	}

	private static void print(int[] a) {
		System.out.println(REFSET.toString(a));
	}

}
