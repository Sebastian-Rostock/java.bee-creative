package bee.creative.util;

import java.util.Map.Entry;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein abstraktes {@link Entry2}.
 *
 * @param <GKey> Typ des Schlüssels.
 * @param <GValue> Typ des Werts. */
public abstract class AbstractEntry<GKey, GValue> implements Entry2<GKey, GValue> {

	@Override
	public GKey getKey() {
		return null;
	}

	@Override
	public GValue getValue() {
		return null;
	}

	@Override
	public final GKey setKey(final GKey key) {
		final GKey result = this.getKey();
		this.useKey(key);
		return result;
	}

	@Override
	public final GValue setValue(final GValue value) {
		final GValue result = this.getValue();
		this.useValue(value);
		return result;
	}

	/** Diese Methode setzt den {@link #getKey() Schlüssel} und gibt {@code this} zurück. */
	public Entry2<GKey, GValue> useKey(final GKey key) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/** Diese Methode setzt den {@link #getValue() Wert} und gibt {@code this} zurück. */
	public Entry2<GKey, GValue> useValue(final GValue value) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getKey()) ^ Objects.hash(this.getValue());
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Entry<?, ?>)) return false;
		final Entry<?, ?> that = (Entry<?, ?>)object;
		return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.getValue(), that.getValue());
	}

	@Override
	public String toString() {
		return this.getKey() + "=" + this.getValue();
	}

	@Override
	public Entry2<GValue, GKey> reverse() {
		return Entries.reverse(this);
	}

}