package bee.creative.ber;

import java.util.Random;
import bee.creative.util.Tester;

class REFSET_TEST {

	public static void main(String[] args) {

		System.out.println("BERStore");
		for (int a = 0; a < 10; a++) {
			var r = new Random(a);
			var s = new BERStore(null);
			System.out.println(new Tester(() -> {
				for (int i = 0; i < 2000000; i++) {
					s.put(r.nextInt(1000) + 1, r.nextInt(50) + 1, r.nextInt(1000) + 1);
				}
			}));
		}
		System.out.println("BEREdges2");
		for (int a = 0; a < 10; a++) {
			var r = new Random(a);
			var s = new BEREdges2();
			System.out.println(new Tester(() -> {
				for (int i = 0; i < 2000000; i++) {
					s.put(r.nextInt(1000) + 1, r.nextInt(50) + 1, r.nextInt(1000) + 1);
				}
			}));
		}

		var x = REFSET.EMPTY;
		System.out.println(x);
		System.out.println(REFSET.putRef(x, 9));
		System.out.println(REFSET.pack(x));

		var a = REFSET.EMPTY;
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

		var e = new BERStore(null);
		e.put(1, 2, 3);
		e.put(1, 2, 4);
		e.put(1, 3, 4);
		e.put(2, 2, 4);
		e.pop(2, 2, 4);
		System.out.println(e);

	}

	private static void print(int[] a) {
		System.out.println(REFSET.toString(a));
	}

}
