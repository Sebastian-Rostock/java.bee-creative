package bee.creative.util;

import java.util.Comparator;
import java.util.Map;

// TODO
public class TreeMap {

	public static <GKey, GValue> java.util.TreeMap<GKey, GValue> from(final Filter<Object> filter, final Comparator<? super GKey> comparator) {
		return new java.util.TreeMap(comparator);
	}

}
