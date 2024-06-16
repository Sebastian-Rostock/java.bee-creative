package bee.creative.util;

import java.util.Set;

/** Diese Klasse implementiert ein abstraktes {@link Set2} als Platzhalter. Seinen Inhalt liest es über {@link #getData(boolean)}. Änderungen am Inhalt werden
 * über {@link #setData(Set)} geschrieben.
 *
 * @param <E> Typ der Elemente.
 * @param <D> Typ des Inhalts. */
public abstract class AbstractProxySet<E, D extends Set<E>> extends AbstractProxyCollection<E, D> implements Set2<E> {

	@Override
	protected abstract D getData(boolean readonly);

	@Override
	protected abstract void setData(D items);

}