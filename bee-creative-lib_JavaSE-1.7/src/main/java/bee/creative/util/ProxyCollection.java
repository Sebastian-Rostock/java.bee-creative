package bee.creative.util;

import java.util.Collection;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link AbstractProxyCollection}, deren Inhalt über ein gegebenes {@link Property} angebunden wird.
 *
 * @param <GItem> Typ der Elemente. */
@SuppressWarnings ("javadoc")
public class ProxyCollection<GItem> extends AbstractProxyCollection<GItem, Collection<GItem>> {

	/** Diese Methode ist eine Abkürzung für {@link ProxyCollection new ProxyCollection<>(that)}. */
	public static <GItem> ProxyCollection<GItem> from(final Property<Collection<GItem>> that) throws NullPointerException {
		return new ProxyCollection<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Property) ProxyCollection.from(Properties.from(that, item))}.
	 *
	 * @see Properties#from(Field, Object) */
	public static <GItem, GEntry> ProxyCollection<GEntry> from(final Field<? super GItem, Collection<GEntry>> that, final GItem item)
		throws NullPointerException {
		return ProxyCollection.from(Properties.from(that, item));
	}

	public final Property<Collection<GItem>> that;

	public ProxyCollection(final Property<Collection<GItem>> that) throws NullPointerException {
		this.that = Objects.notNull(that);
	}

	@Override
	public Collection<GItem> getData(final boolean readonly) {
		return this.that.get();
	}

	@Override
	protected void setData(final Collection<GItem> items) {
		this.that.set(items);
	}

}