package bee.creative.util;

import java.util.Set;

/** Diese Klasse implementiert ein abstraktes {@link Set} als Platzhalter. Seinen Inhalt liest es über {@link #getData(boolean)}. Änderungen am Inhalt werden
 * über {@link #setData(Set)} geschrieben.
 *
 * @param <GItem> Typ der Elemente.
 * @param <GData> Typ des Inhalts. */
public abstract class AbstractProxySet<GItem, GData extends Set<GItem>> extends AbstractProxyCollection<GItem, GData> implements Set<GItem> {

	/** {@inheritDoc} */
	@Override
	protected abstract GData getData(boolean readonly);

	/** {@inheritDoc} */
	@Override
	protected abstract void setData(GData items);

}