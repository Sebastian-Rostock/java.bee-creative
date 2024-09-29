package bee.creative.str;

import java.util.Random;
import bee.creative.util.Tester;

class STR_TEST {

	public static void main(String[] args) {

		System.out.println("BEREdges2");
		for (int a = 0; a < 10; a++) {
			var r = new Random(a);
			var s = new STRStore();
			var l = new Tester(() -> {
				for (int i = 0; i < 1000000;) {
					for (int j = 0; j < 1000; i++, j++) {
						s.put(r.nextInt(5000) + 1, r.nextInt(50) + 1, r.nextInt(5000) + 1);
					}
//					var ddd = s.commit();
				//	System.out.println(ddd.getPutState());
				}
			});
			System.out.println(l);
			int[] sc= {0};
			s.forEach((x,b,c)->sc[0]++);
			System.out.println(sc[0]);
			int[] kc= {0};
			var k = STRState.from(s.toBytes());
			k.forEach((x,b,c)->kc[0]++);
			System.out.println(kc[0]);
			// l.cause.printStackTrace();
		}

	}

}
