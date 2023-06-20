package bee.creative.ref;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import bee.creative.util.Producer;

/** Diese Klasse implementiert eine {@link WeakReference}, die über ihr {@link #customRemove() Bereinigen} informiert wird.
 *
 * @see ReferenceQueue2
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des referenzierten Objekts. */
public class WeakReference2<GValue> extends WeakReference<GValue> implements Producer<GValue> {

	static private final ReferenceQueue2<Object> QUEUE = new ReferenceQueue2<>() {

		@Override
		protected void customRemove(final Reference<?> reference) {
			((WeakReference2<?>)reference).customRemove();
		}

	};

	/** Dieser Konstruktor initialisiert das referenzierte Objekt. */
	public WeakReference2(final GValue value) {
		super(value, WeakReference2.QUEUE);
	}

	/** Diese Methode wird beim Bereinigen dieser {@link WeakReference2} aufgerufen. Das Bereinigen sollte so schnell es geht behandelt werden. Der aktuelle
	 * {@link Thread} sollte hierfür keinesfalls längere Zeit warten müssen.
	 *
	 * @see ReferenceQueue2#customRemove(Reference) */
	protected void customRemove() {
	}

}
