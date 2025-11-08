package bee.creative.util;

import bee.creative.lang.Objects;
import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Hasher3}. */
public abstract class AbstractHasher extends BaseObject implements Hasher3 {

	@Override
	public int hash(Object input) {
		return Objects.hash(input);
	}

	@Override
	public boolean equals(Object input1, Object input2) {
		return Objects.equals(input1, input2);
	}

}