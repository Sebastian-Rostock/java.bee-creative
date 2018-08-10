package bee.creative.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/** Diese Klasse implementiert eine {@link SoftReference} als {@link Pointer} auf einen Datensatz. Die Referenz auf den Datensatz eines solcher {@link Pointer}
 * wird nur dann automatisch aufgelöst, wenn der Datensatz nur noch über {@link SoftReference} erreichbar ist und der Garbage Collector dies entscheidet.
 *
 * @see SoftReference
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Datensatzes. */
public final class SoftPointer<GValue> extends SoftReference<GValue> implements Pointer<GValue> {

	/** Dieser Konstruktor initialisiert den Datensatz.
	 *
	 * @param value Datensatz. */
	public SoftPointer(final GValue value) {
		super(value);
	}

	/** Dieser Konstruktor initialisiert Datensatz und Warteschlange.
	 *
	 * @param value Datensatz.
	 * @param queue Warteschlange, in welche dieser {@link WeakPointer} eingetragen wird, wenn die Referenz auf den Datensatz aufgelöst wurde. */
	public SoftPointer(final GValue value, final ReferenceQueue<? super GValue> queue) {
		super(value, queue);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return HardPointer.hash(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		return HardPointer.equals(this, object);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return String.valueOf(this.get());
	}

}