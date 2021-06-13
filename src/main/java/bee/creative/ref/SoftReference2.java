package bee.creative.ref;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import bee.creative.util.Producer;

/** Diese Klasse implementiert eine {@link SoftReference}, die über ihr {@link #customRemove() Bereinigen} informiert wird.
 *
 * @see ReferenceQueue2
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des referenzierten Objekts. */
public class SoftReference2<GValue> extends SoftReference<GValue> implements Producer<GValue> {

	static private final ReferenceQueue2<Object> QUEUE = new ReferenceQueue2<Object>() {

		@Override
		protected void customRemove(final Reference<?> reference) {
			((SoftReference2<?>)reference).customRemove();
		}

	};

	/** Dieser Konstruktor initialisiert das referenzierte Objekt. */
	public SoftReference2(final GValue value) {
		super(value, SoftReference2.QUEUE);
	}

	/** Diese Methode wird beim Bereinigen dieser {@link SoftReference2} aufgerufen. Das Bereinigen sollte so schnell es geht behandelt werden. Der aktuelle
	 * {@link Thread} sollte hierfür keinesfalls längere Zeit warten müssen.
	 *
	 * @see ReferenceQueue2#customRemove(Reference) */
	protected void customRemove() {
	}

}
