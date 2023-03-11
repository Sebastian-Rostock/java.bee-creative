package bee.creative.ref;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen harten {@link Reference2} auf einen Datensatz. Die Referenz auf den Datensatz eines solcher {@link Reference2} wird nicht
 * automatisch aufgelöst.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Datensatzes. */
public final class HardReference3<GValue> implements Reference2<GValue> {

	static int hash(final Reference2<?> thiz) {
		return Objects.deepHash(thiz.get());
	}

	static boolean equals(final Reference2<?> thiz, final Object object) {
		if (object == thiz) return true;
		if (!(object instanceof Reference2<?>)) return false;
		final Reference2<?> that = (Reference2<?>)object;
		return Objects.deepEquals(thiz.get(), that.get());
	}

	/** Dieses Feld speichert den Datensatz. */
	private final GValue value;

	/** Dieser Konstruktor initialisiert den Datensatz.
	 *
	 * @param value Datensatz. */
	public HardReference3(final GValue value) {
		this.value = value;
	}

	@Override
	public GValue get() {
		return this.value;
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