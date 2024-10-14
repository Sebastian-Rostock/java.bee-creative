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
			s.putValue(FEMString.from("ABC"));
			s.putValue(FEMString.from("DEF"));
			s.putEdge(1, 2, 3);
			s.putEdge(4, 5, 6);
			System.out.println(s.toString());
			System.out.println(s.values().exceptValueRefs(2) );
		}
		
		
		for (var a = 0; a < 3; a++) {
			System.out.println("KBBuffer " + a);
			var r = new Random(a);
			var s = new KBBuffer();
			var l = new Tester(() -> {
				for (var i = 0; i < (1000 * 1000);) {
					for (var j = 0; j < 1000; i++, j++) {
						s.putEdge(r.nextInt(1000) + 1, r.nextInt(50) + 1, r.nextInt(1000) + 1);
					}
					// var ddd = s.commit();
					 // var ddd = s.rollback();
					//  System.out.println(ddd.getInserts());
				}
			});
			System.out.println(l);
			 
		}
	 
	}

}
