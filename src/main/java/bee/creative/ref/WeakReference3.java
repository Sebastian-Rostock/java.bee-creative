package bee.creative.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/** Diese Klasse implementiert eine {@link WeakReference} als {@link Reference2} auf einen Datensatz. Die Referenz auf den Datensatz eines solcher {@link Reference2}
 * wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link WeakReference} erreichbar ist.
 *
 * @see WeakReference
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Datensatzes. */
public final class WeakReference3<GValue> extends WeakReference<GValue> implements Reference2<GValue> {

	/** Dieser Konstruktor initialisiert den Datensatz.
	 *
	 * @param value Datensatz. */
	public WeakReference3(final GValue value) {
		super(value);
	}

	/** Dieser Konstruktor initialisiert Datensatz und Warteschlange.
	 *
	 * @param value Datensatz.
	 * @param queue Warteschlange, in welche dieser {@link WeakReference3} eingetragen wird, wenn die Referenz auf den Datensatz aufgelöst wurde. */
	public WeakReference3(final GValue value, final ReferenceQueue<? super GValue> queue) {
		super(value, queue);
	}

	@Override
	public int hashCode() {
		return HardReference3.hash(this);
	}

	@Override
	public boolean equals(final Object object) {
		return HardReference3.equals(this, object);
	}

	@Override
	public String toString() {
		return String.valueOf(this.get());
	}

}