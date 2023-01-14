package bee.creative.util;

import java.util.Set;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein {@link AbstractProxySet}, dessen Inhalt über ein gegebenes {@link Property} angebunden wird.
 *
 * @param <GItem> Typ der Elemente. */
@SuppressWarnings ("javadoc")
public class ProxySet<GItem> extends AbstractProxySet<GItem, Set<GItem>> {

	/** Diese Methode ist eine Abkürzung für {@link ProxySet new ProxySet<>(that)}. */
	public static <GItem> ProxySet<GItem> from(final Property<Set<GItem>> that) throws NullPointerException {
		return new ProxySet<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Property) ProxySet.from(Properties.from(that, item))}.
	 *
	 * @see Properties#from(Field, Object) */
	public static <GItem, GEntry> ProxySet<GEntry> from(final Field<? super GItem, Set<GEntry>> that, final GItem item) throws NullPointerException {
		return ProxySet.from(Properties.from(that, item));
	}

	public final Property<Set<GItem>> that;

	public ProxySet(final Property<Set<GItem>> that) throws NullPointerException {
		this.that = Objects.notNull(that);
	}

	@Override
	public Set<GItem> getData(final boolean readonly) {
		return this.that.get();
	}

	@Override
	protected void setData(final Set<GItem> items) {
		this.that.set(items);
	}

}