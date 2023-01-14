package bee.creative.util;

import java.util.List;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link AbstractProxyList}, deren Inhalt über ein gegebenes {@link Property} angebunden wird.
 *
 * @param <GItem> Typ der Elemente. */
@SuppressWarnings ("javadoc")
public class ProxyList<GItem> extends AbstractProxyList<GItem, List<GItem>> {

	/** Diese Methode ist eine Abkürzung für {@link ProxyList new ProxyList<>(that)}. **/
	public static <GItem> ProxyList<GItem> from(final Property<List<GItem>> that) throws NullPointerException {
		return new ProxyList<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Property) ProxyList.from(Properties.from(that, item))}.
	 *
	 * @see Properties#from(Field, Object) */
	public static <GItem, GEntry> ProxyList<GEntry> from(final Field<? super GItem, List<GEntry>> that, final GItem item) throws NullPointerException {
		return ProxyList.from(Properties.from(that, item));
	}

	public final Property<List<GItem>> that;

	public ProxyList(final Property<List<GItem>> that) throws NullPointerException {
		this.that = Objects.notNull(that);
	}

	@Override
	public List<GItem> getData(final boolean readonly) {
		return this.that.get();
	}

	@Override
	protected void setData(final List<GItem> items) {
		this.that.set(items);
	}

}