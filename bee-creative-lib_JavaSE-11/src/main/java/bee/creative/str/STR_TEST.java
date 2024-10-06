package bee.creative.str;

import java.io.IOException;
import java.util.Random;
import bee.creative.util.Tester;

class STR_TEST {

	public static void main(String[] args) throws IOException {

		System.out.println("BEREdges2");
		for (int a = 0; a < 10; a++) {
			var r = new Random(a);
			var s = new STRBuffer();
			var l = new Tester(() -> {
				for (int i = 0; i < 1000 * 1000;) {
					for (int j = 0; j < 1000; i++, j++) {
						s.putEdge(r.nextInt(5000) + 1, r.nextInt(50) + 1, r.nextInt(5000) + 1);
					}
					// var ddd = s.commit();
//					var ddd = s.rollback();
					// System.out.println(ddd.getPutState());
				}
			});
			System.out.println(l); 
			int[] sc = {0};
			s.edges().forEach((x, b, c) -> sc[0]++);
			System.out.println(sc[0]);
			int[] kc = {0};
			var k = STRState.from(s.toBytes());
			k.edges().forEach((x, b, c) -> kc[0]++);
			System.out.println(kc[0]);
			// l.cause.printStackTrace();
		}
		// 320 1000 add x 1000 => put = 320n
		// 800 mit c => c = 480000n 
		// 177 1000 add mit max 1000 cap
	}

}
