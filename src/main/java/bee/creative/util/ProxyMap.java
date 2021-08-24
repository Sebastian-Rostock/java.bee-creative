package bee.creative.util;

import java.util.Map;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link AbstractProxyMap}, deren Inhalt über ein gegebenen {@link Property} angebunden wird.
 *
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
@SuppressWarnings ("javadoc")
public class ProxyMap<GKey, GValue> extends AbstractProxyMap<GKey, GValue, Map<GKey, GValue>> {

	/** Diese Methode ist eine Abkürzung für {@link ProxyMap new ProxyMap<>(that)}. */
	public static <GKey, GValue> Map<GKey, GValue> from(final Property<Map<GKey, GValue>> property) throws NullPointerException {
		return new ProxyMap<>(property);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Property) ProxyMap.from(Properties.from(that, item))}.
	 *
	 * @see Properties#from(Field, Object) */
	public static <GItem, GKey, GValue> Map<GKey, GValue> from(final GItem item, final Field<? super GItem, Map<GKey, GValue>> field)
		throws NullPointerException {
		return ProxyMap.from(Properties.from(field, item));
	}

	public final Property<Map<GKey, GValue>> that;

	public ProxyMap(final Property<Map<GKey, GValue>> that) throws NullPointerException {
		this.that = Objects.notNull(that);
	}

	@Override
	public Map<GKey, GValue> getData(final boolean readonly) {
		return this.that.get();
	}

	@Override
	protected void setData(final Map<GKey, GValue> items) {
		this.that.set(items);
	}

}