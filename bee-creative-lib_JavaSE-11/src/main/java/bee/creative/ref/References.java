package bee.creative.ref;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/** Diese Schnittstelle definiert identifiziert Referenzen.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface References {

	/** Dieses Feld identifiziert die direkte Referenz. */
	int HARD = 0;

	/** Dieses Feld identifiziert die {@link WeakReference}. */
	int WEAK = 1;

	/** Dieses Feld identifiziert die {@link SoftReference}. */
	int SOFT = 2;

}
