package bee.creative.util;

import java.util.Map.Entry;

/** Diese Schnittstelle ergänzt einen {@link Entry} insb. um eine Anbindung an Methoden von {@link Entries}.
 *
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <K> Typ des Schlüssels.
 * @param <V> Typ des Werts. */
public interface Entry2<K, V> extends Entry<K, V> {

	/** Diese Methode setzt den Schlüssel. Wenn dies nicht möglich ist, wird eine {@link UnsupportedOperationException} ausgelöst. */
	default K setKey(K key) {
		var result = this.getKey();
		this.useKey(key);
		return result;
	}

	/** Diese Methode setzt den Wert. Wenn dies nicht möglich ist, wird eine {@link UnsupportedOperationException} ausgelöst. */
	@Override
	default V setValue(V value) {
		var result = this.getValue();
		this.useValue(value);
		return result;
	}

	/** Diese Methode setzt den {@link #getKey() Schlüssel} und gibt {@code this} zurück. */
	default Entry2<K, V> useKey(K key) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/** Diese Methode setzt den {@link #getValue() Wert} und gibt {@code this} zurück. */
	default Entry2<K, V> useValue(V value) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/** Diese Methode ist eine Abkürzung für {@link Entries#reverseEntry(Entry) Entries.reverse(this)}. */
	default Entry2<V, K> reverse() {
		return Entries.reverseEntry(this);
	}

}
