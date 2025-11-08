package bee.creative.qs.dev;

import bee.creative.util.Filters;

public class FilterTestMain {

	public static void main(String[] args) throws Exception {
		var f = Filters.filterFromValue(true);
		var g = f.synchronize();
		var h = f.synchronize(null);
		var a = f.accepts(null);
		var b = g.accepts(null);
		var c = h.accepts(null);
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
	}
	
}
