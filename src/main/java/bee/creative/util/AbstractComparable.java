package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

public abstract class AbstractComparable<GItem> extends BaseObject implements Comparable2<GItem> {

	@Override
	public int compareTo(final GItem item) {
		return 0;
	}

}