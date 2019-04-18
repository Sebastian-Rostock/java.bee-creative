package bee.creative.ref;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen harten {@link Pointer} auf einen Datensatz. Die Referenz auf den Datensatz eines solcher {@link Pointer} wird nicht
 * automatisch aufgelöst.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Datensatzes. */
public final class HardPointer<GValue> implements Pointer<GValue> {

	static int hash(final Pointer<?> thiz) {
		return Objects.deepHash(thiz.get());
	}

	static boolean equals(final Pointer<?> thiz, final Object object) {
		if (object == thiz) return true;
		if (!(object instanceof Pointer<?>)) return false;
		final Pointer<?> that = (Pointer<?>)object;
		return Objects.deepEquals(thiz.get(), that.get());
	}

	/** Dieses Feld speichert den Datensatz. */
	private final GValue value;

	/** Dieser Konstruktor initialisiert den Datensatz.
	 *
	 * @param value Datensatz. */
	public HardPointer(final GValue value) {
		this.value = value;
	}

	/** {@inheritDoc} */
	@Override
	public GValue get() {
		return this.value;
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