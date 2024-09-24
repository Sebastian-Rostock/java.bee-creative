package bee.creative.str;

import java.util.Random;
import bee.creative.util.HashSetI;
import bee.creative.util.Tester;

class STR_TEST {

	public static void main(String[] args) {

		System.out.println("BEREdges2");
		for (int a = 0; a < 10; a++) {
			var r = new Random(a);
			var s = new STRStore();
			var l = new Tester(() -> {
				for (int i = 0; i < 1000;) {
					for (int j = 0; j < 10; i++, j++) {
						s.put(r.nextInt(5000) + 1, r.nextInt(50) + 1, r.nextInt(5000) + 1);
					}
			var ddd=		 s.commit();
				}
			});
			System.out.println(l);
//			 l.cause.printStackTrace();
		}

	}

}
