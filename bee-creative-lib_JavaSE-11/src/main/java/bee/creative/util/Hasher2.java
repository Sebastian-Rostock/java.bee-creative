package bee.creative.util;

import static bee.creative.util.Hashers.hasherFrom;

/** Diese Schnittstelle definiert einen {@link Hasher} mit {@link Hasher3}-Schnittstelle.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Hasher2 extends Hasher {

	/** Diese Methode liefert die {@link Hasher3}-Schnittstelle zu {@link #hash(Object)} und {@link #equals(Object, Object)}. */
	default Hasher3 asHasher() {
		return hasherFrom(this);
	}

}
