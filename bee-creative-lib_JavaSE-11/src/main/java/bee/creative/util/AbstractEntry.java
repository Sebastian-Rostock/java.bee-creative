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
	public GKey setKey(GKey key) {
		var result = this.getKey();
		this.useKey(key);
		return result;
	}

	@Override
	public GValue setValue(GValue value) {
		var result = this.getValue();
		this.useValue(value);
		return result;
	}

	/** Diese Methode setzt den {@link #getKey() Schlüssel} und gibt {@code this} zurück. */
	public Entry2<GKey, GValue> useKey(GKey key) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/** Diese Methode setzt den {@link #getValue() Wert} und gibt {@code this} zurück. */
	public Entry2<GKey, GValue> useValue(GValue value) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getKey()) ^ Objects.hash(this.getValue());
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof Entry<?, ?>)) return false;
		var that = (Entry<?, ?>)object;
		return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.getValue(), that.getValue());
	}

	@Override
	public String toString() {
		return this.getKey() + "=" + this.getValue();
	}

}