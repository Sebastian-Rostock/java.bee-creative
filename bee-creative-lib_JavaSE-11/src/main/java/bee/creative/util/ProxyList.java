package bee.creative.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link AbstractProxyList}, deren Inhalt über ein gegebenes {@link Property} angebunden wird.
 *
 * @param <E> Typ der Elemente. */
public class ProxyList<E> extends AbstractProxyList<E, List<E>> {

	public static <E> ProxyList<E> from(Producer<List<E>> getValue, Consumer<List<E>> setValue) throws NullPointerException {
		return ProxyList.<E>from(Producers.translatedProducer(getValue, value -> {
			if (value instanceof ArrayList) return value;
			if (value != null) return new ArrayList<>(value);
			return new ArrayList<>();
		}), Consumers.translatedConsumer(setValue, value -> {
			if (value.size() > 1) return value;
			for (var item: value)
				return Collections.singletonList(item);
			return null;
		}), Producers.translatedProducer(getValue, value -> {
			if (value != null) return value;
			return Collections.emptyList();
		}));
	}

	/** Diese Methode ist eine Abkürzung für {@link ProxyList new ProxyList<>(getValue, setValue, getConst)}. **/
	public static <E> ProxyList<E> from(Producer<List<E>> getValue, Consumer<List<E>> setValue, Producer<List<E>> getConst) throws NullPointerException {
		return new ProxyList<>(getValue, setValue, getConst);
	}

	public final Producer<List<E>> getValue;

	public final Consumer<List<E>> setValue;

	public final Producer<List<E>> getConst;

	public ProxyList(Producer<List<E>> getValue, Consumer<List<E>> setValue, Producer<List<E>> getConst) throws NullPointerException {
		this.getValue = Objects.notNull(getValue);
		this.setValue = Objects.notNull(setValue);
		this.getConst = Objects.notNull(getConst);
	}

	@Override
	public List<E> getData(boolean readonly) {
		return (readonly ? this.getConst : this.getValue).get();
	}

	@Override
	protected void setData(List<E> items) {
		this.setValue.set(items);
	}

}