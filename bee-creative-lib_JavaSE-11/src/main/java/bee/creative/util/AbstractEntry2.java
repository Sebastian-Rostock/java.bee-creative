package bee.creative.util;

import java.util.Map.Entry;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein abstraktes {@link Entry2}.
 *
 * @param <K> Typ des Schlüssels.
 * @param <V> Typ des Werts. */
public abstract class AbstractEntry2<K, V> implements Entry2<K, V> {

	@Override
	public K getKey() {
		return null;
	}

	@Override
	public V getValue() {
		return null;
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