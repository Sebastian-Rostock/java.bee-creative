package bee.creative.ber;

import java.util.Random;
import bee.creative.util.HashSetI;
import bee.creative.util.Tester;

class REFSET_TEST {

	public static void main(String[] args) {

		// System.out.println("BERStore");
		// for (int a = 0; a < 10; a++) {
		// var r = new Random(a);
		// var s = new BERStore(null);
		// System.out.println(new Tester(() -> {
		// for (int i = 0; i < 100000; i++) {
		// s.put(r.nextInt(1000) + 1, r.nextInt(50) + 1, r.nextInt(1000) + 1);
		// }
		// }));
		// BERCodec.persistEdges(s);
		// }
		System.out.println("BEREdges2");
		for (int a = 0; a < 10; a++) {
			var r = new Random(a);
			var s = new BEREdges();
			var l = new Tester(() -> {
				for (int i = 0; i < 100000; i++) {
					s.put(r.nextInt(1000) + 1, r.nextInt(50) + 1, r.nextInt(1000) + 1);
				}
			});
			System.out.println(l);
//			l.cause.printStackTrace();
			BERCodec.persistEdges(s);
		}

 

	}

}
