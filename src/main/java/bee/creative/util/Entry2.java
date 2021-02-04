package bee.creative.util;

import java.util.Map.Entry;

/** Diese Schnittstelle ergänzt einen {@link Entry} insb. um eine Anbindung an Methoden von {@link Entries}.
 *
 * @see Entries
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ des Schlüssels.
 * @param <GValue> Typ des Werts. */
public interface Entry2<GKey, GValue> extends Entry<GKey, GValue> {

	/** Diese Methode setzt den Schlüssel. Wenn dies nicht möglich ist, wird eine {@link UnsupportedOperationException} ausgelöst. */
	public GKey setKey(GKey key) throws UnsupportedOperationException;

	/** Diese Methode ist eine Abkürzung für {@link Entries#reverse(Entry) Entries.reverse(this)}. */
	public Entry2<GValue, GKey> reverse();

}
