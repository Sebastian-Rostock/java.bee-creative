package bee.creative.util;

import java.util.Collections;
import java.util.Set;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein {@link AbstractProxySet}, dessen Inhalt über ein gegebenes {@link Property} angebunden wird.
 *
 * @param <E> Typ der Elemente. */
public class ProxySet<E> extends AbstractProxySet<E, Set<E>> {

	public static <E> ProxySet<E> from(Producer<Set<E>> getValue, Consumer<Set<E>> setValue) throws NullPointerException {
		return ProxySet.<E>from(Producers.translate(getValue, value -> {
			if (value instanceof HashSet2) return value;
			if (value != null) return new HashSet2<>(value);
			return new HashSet2<>();
		}), Consumers.translate(setValue, value -> {
			if (value.size() > 1) return value;
			for (var item: value)
				return Collections.singleton(item);
			return null;
		}), Producers.translate(getValue, value -> {
			if (value != null) return value;
			return Collections.emptySet();
		}));
	}

	/** Diese Methode ist eine Abkürzung für {@link ProxySet new ProxySet<>(getValue, setValue, getConst)}. **/
	public static <E> ProxySet<E> from(Producer<Set<E>> getValue, Consumer<Set<E>> setValue, Producer<Set<E>> getConst) throws NullPointerException {
		return new ProxySet<>(getValue, setValue, getConst);
	}

	public final Producer<Set<E>> getValue;

	public final Consumer<Set<E>> setValue;

	public final Producer<Set<E>> getConst;

	public ProxySet(Producer<Set<E>> getValue, Consumer<Set<E>> setValue, Producer<Set<E>> getConst) throws NullPointerException {
		this.getValue = Objects.notNull(getValue);
		this.setValue = Objects.notNull(setValue);
		this.getConst = Objects.notNull(getConst);
	}

	@Override
	public Set<E> getData(boolean readonly) {
		return (readonly ? this.getConst : this.getValue).get();
	}

	@Override
	protected void setData(Set<E> items) {
		this.setValue.set(items);
	}

}