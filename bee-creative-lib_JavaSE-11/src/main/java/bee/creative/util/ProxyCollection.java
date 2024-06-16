package bee.creative.util;

import java.util.Collection;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link AbstractProxyCollection}, deren Inhalt über ein gegebenes {@link Property} angebunden wird.
 *
 * @param <E> Typ der Elemente. */
public class ProxyCollection<E> extends AbstractProxyCollection<E, Collection<E>> {

	/** Diese Methode ist eine Abkürzung für {@link ProxyCollection new ProxyCollection<>(getValue, setValue, getConst)}. **/
	public static <E> ProxyCollection<E> from(Producer<Collection<E>> getValue, Consumer<Collection<E>> setValue, Producer<Collection<E>> getConst)
		throws NullPointerException {
		return new ProxyCollection<>(getValue, setValue, getConst);
	}

	public final Producer<Collection<E>> getValue;

	public final Consumer<Collection<E>> setValue;

	public final Producer<Collection<E>> getConst;

	public ProxyCollection(Producer<Collection<E>> getValue, Consumer<Collection<E>> setValue, Producer<Collection<E>> getConst) throws NullPointerException {
		this.getValue = Objects.notNull(getValue);
		this.setValue = Objects.notNull(setValue);
		this.getConst = Objects.notNull(getConst);
	}

	@Override
	public Collection<E> getData(boolean readonly) {
		return (readonly ? this.getConst : this.getValue).get();
	}

	@Override
	protected void setData(Collection<E> items) {
		this.setValue.set(items);
	}

}