package bee.creative.util;

import java.util.Set;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein abstraktes {@link Set} als Platzhalter. Seinen Inhalt liest es über {@link #getData(boolean)}. Änderungen am Inhalt werden
 * über {@link #setData(Set)} geschrieben.
 *
 * @param <GItem> Typ der Elemente.
 * @param <GData> Typ des Inhalts. */
public abstract class AbstractProxySet<GItem, GData extends Set<GItem>> extends AbstractProxyCollection<GItem, GData> implements Set<GItem> {

	/** Diese Klasse implementiert {@link AbstractProxySet#toSet(Property)}. */
	static class PropertySet<GItem> extends AbstractProxySet<GItem, Set<GItem>> {
	
		public final Property<Set<GItem>> property;
	
		public PropertySet(final Property<Set<GItem>> property) {
			this.property = Objects.notNull(property);
		}
	
		@Override
		public Set<GItem> getData(final boolean readonly) {
			return this.property.get();
		}
	
		@Override
		protected void setData(final Set<GItem> items) {
			this.property.set(items);
		}
	
	}

	@Override
	protected abstract GData getData(boolean readonly);

	@Override
	protected abstract void setData(GData items);

	/** Diese Methode ist eine Abkürzung für {@link AbstractProxySet#toSet(Property) Properties.toSet(Fields.toProperty(item, field))}. */
	public static <GItem, GEntry> Set<GEntry> toSet(final GItem item, final Field<? super GItem, Set<GEntry>> field) throws NullPointerException {
		return AbstractProxySet.toSet(Properties.from(field, item));
	}

	/** Diese Methode gibt ein {@link Set} zurück, dessen Inhalt über das gegebene {@link Property} gelesen und geschrieben wird.
	 *
	 * @see AbstractProxySet
	 * @param property {@link Property}.
	 * @return {@link Set}-{@code Proxy}.
	 * @throws NullPointerException Wenn {@code property} {@code null} ist. */
	public static <GItem> Set<GItem> toSet(final Property<Set<GItem>> property) throws NullPointerException {
		return new PropertySet<>(property);
	}

}