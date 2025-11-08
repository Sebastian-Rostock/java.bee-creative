package bee.creative.util;

import java.util.Collection;
import java.util.Set;
import bee.creative.lang.Objects;

/** Diese Klasse erweitert ein {@link HashSet} mit geringem {@link AbstractHashData Speicherverbrauch}, dessen Elemente über Reverenzvergleich und
 * {@link System#identityHashCode(Object)} abgeglichen werden.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <E> Typ der Elemente. */
public class HashSet3<E> extends HashSet<E> {

	/** Dieser Konstruktor initialisiert die Kapazität mit {@code 0}. */
	public HashSet3() {
	}

	/** Dieser Konstruktor initialisiert die Kapazität.
	 *
	 * @param capacity Kapazität. */
	public HashSet3(int capacity) {
		this.allocateImpl(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet3} mit dem Inhalt der gegebenen {@link Set}.
	 *
	 * @param source gegebene Einträge. */
	public HashSet3(Set<? extends E> source) {
		this.allocateImpl(source.size());
		this.addAll(source);
	}

	/** Dieser Konstruktor initialisiert das {@link HashSet3} mit dem Inhalt der gegebenen {@link Collection}.
	 *
	 * @param source gegebene Einträge. */
	public HashSet3(Collection<? extends E> source) {
		this.addAll(source);
	}

	@Override
	protected int customHash(Object key) {
		return Objects.identityHash(key);
	}

	@Override
	protected boolean customEqualsKey(int entryIndex, Object key) {
		return Objects.identityEquals(this.customGetKey(entryIndex), key);
	}

	private static final long serialVersionUID = -472585728645140310L;

}
