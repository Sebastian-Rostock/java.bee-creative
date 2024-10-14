package bee.creative.kb;

import java.io.IOException;
import java.util.Random;
import java.util.zip.Deflater;
import bee.creative.fem.FEMString;
import bee.creative.lang.Integers;
import bee.creative.util.Tester;

class TEST {

	public static void main(String[] args) throws IOException {

		{
			System.out.println("texttest");
			var s = new KBBuffer();
			s.putEdge(1, 2, 3);
			s.putEdge(4, 5, 6);
			s.TODO_putValue(FEMString.from("ABC"));
			s.TODO_putValue(FEMString.from("DEF"));
			System.out.println(s.toString());
		}
		
		
		for (var a = 0; a < 3; a++) {
			System.out.println("KBBuffer " + a);
			var r = new Random(a);
			var s = new KBBuffer();
			var l = new Tester(() -> {
				for (var i = 0; i < (1000 * 1000);) {
					
					
					for (var j = 0; j < 100; i++, j++) {
						s.putEdge(r.nextInt(1000) + 1, r.nextInt(20) + 1, r.nextInt(1000) + 1);
					}
					// var ddd = s.commit();
					// var ddd = s.rollback();
					// System.out.println(ddd.getPutState());
				}
			});
			System.out.println(l);
			System.out.println("toInts " + Tester.get(() -> Integers.printSize(s.toInts().length * 4)));
			System.out.println("deflate(toBytes) " + Tester.get(() -> Integers.printSize(ZIPDOS.deflate(s.toBytes(),Deflater.BEST_SPEED).length)));
			 
			// l.cause.printStackTrace();
		}
		// 320 1000 add x 1000 => put = 320n
		// 800 mit c => c = 480000n
		// 177 1000 add mit max 1000 cap
	}

}
