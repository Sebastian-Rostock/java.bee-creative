package bee.creative.util;

import bee.creative.lang.Objects;
import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Hasher2}. */
public abstract class AbstractHasher extends BaseObject implements Hasher2 {

	@Override
	public int hash(final Object input) {
		return Objects.hash(input);
	}

	@Override
	public boolean equals(final Object input1, final Object input2) {
		return Objects.equals(input1, input2);
	}

	@Override
	public Hasher2 translate(final Getter<? super Object, ?> trans) throws NullPointerException {
		return Hashers.translate(this, trans);
	}

}