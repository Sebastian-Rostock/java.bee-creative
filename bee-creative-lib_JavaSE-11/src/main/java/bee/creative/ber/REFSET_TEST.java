package bee.creative.ber;

import java.util.Random;
import bee.creative.util.HashSetI;
import bee.creative.util.Tester;

class REFSET_TEST {

	public static void main(String[] args) {

		System.out.println("BEREdges2");
		for (int a = 0; a < 10; a++) {
			var r = new Random(a);
			var s = new BERStore();
			var l = new Tester(() -> {
				for (int i = 0; i < 1000000;) {
					for (int j = 0; j < 100; i++, j++) {
						s.put(r.nextInt(5000) + 1, r.nextInt(50) + 1, r.nextInt(5000) + 1);
					}
					// s.commit();
				}
			});
			System.out.println(l);
			// l.cause.printStackTrace();
			BERCodec.persistEdges(s);
		}

	}

}
