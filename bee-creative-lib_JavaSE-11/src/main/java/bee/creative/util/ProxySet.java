package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Consumers.translatedConsumer;
import static bee.creative.util.Producers.translatedProducer;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import java.util.Set;

/** Diese Klasse implementiert ein {@link AbstractProxySet}, dessen Inhalt über ein gegebenes {@link Property} angebunden wird.
 *
 * @param <E> Typ der Elemente. */
public class ProxySet<E> extends AbstractProxySet<E, Set<E>> {

	public static <E> ProxySet<E> from(Producer<Set<E>> getValue, Consumer<Set<E>> setValue) throws NullPointerException {
		return ProxySet.<E>from(translatedProducer(getValue, value -> {
			if (value instanceof HashSet2) return value;
			if (value != null) return new HashSet2<>(value);
			return new HashSet2<>();
		}), translatedConsumer(setValue, value -> {
			if (value.size() > 1) return value;
			for (var item: value)
				return singleton(item);
			return null;
		}), translatedProducer(getValue, value -> {
			if (value != null) return value;
			return emptySet();
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
		this.getValue = notNull(getValue);
		this.setValue = notNull(setValue);
		this.getConst = notNull(getConst);
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