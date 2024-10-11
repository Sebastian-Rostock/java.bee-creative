package bee.creative.str;

import java.io.IOException;
import java.util.Random;
import bee.creative.fem.FEMString;
import bee.creative.lang.Integers;
import bee.creative.util.Tester;

class STR_TEST {

	public static void main(String[] args) throws Exception {
		var r = new Random();

		var v1 = "yä€𝄞𤽜";
		var v2 = FEMString.from(v1);
		var v3 = v2.toString();
		System.out.println(v3);

		System.out.println(new Tester(() -> {
			for (var i = 0; i < 4600000; i++) {
				var a = r.ints(5).map(s-> s & 0x10fff).toArray();
				var s = new String(a, 0, a.length);
				s.codePoints().toArray();
			}
		}));
		System.out.println(new Tester(() -> {
		try {	for (var i = 0; i < 4600000; i++) {
				var a = r.ints(5).map(s-> s & 0x10fff).toArray();
				var s = FEMString.from(false, a, 0, a.length).toString();
				FEMString.from(s).value();
			}
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		}));
	}

	public static void main_(String[] args) throws IOException {

		System.out.println("BEREdges2");
		for (var a = 0; a < 10; a++) {
			var r = new Random(a);
			var s = new STRBuffer();
			var l = new Tester(() -> {
				for (var i = 0; i < (1000 * 1000);) {
					for (var j = 0; j < 1000; i++, j++) {
						s.putEdge(r.nextInt(5000) + 1, r.nextInt(50) + 1, r.nextInt(5000) + 1);
					}
					// var ddd = s.commit();
					// var ddd = s.rollback();
					// System.out.println(ddd.getPutState());
				}
			});
			System.out.println(l);
			System.out.println(Integers.printSize(s.toInts().length * 4));
			System.out.println(Integers.printSize(s.persist().length));
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
