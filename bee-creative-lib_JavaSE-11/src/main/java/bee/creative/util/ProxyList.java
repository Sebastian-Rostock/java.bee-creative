package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Consumers.translatedConsumer;
import static bee.creative.util.Producers.translatedProducer;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

/** Diese Klasse implementiert eine {@link AbstractProxyList}.
 *
 * @param <E> Typ der Elemente. */
public class ProxyList<E> extends AbstractProxyList<E, List<E>> {

	/** Diese Methode liefert eine {@link ProxyList}, deren Inhalt über den gegebenen {@code getValue} gelesen und über {@code setValue} geschrieben wird. Wenn
	 * der Inahlt beim Lesen {@code null} ist, wird eine leere {@link ArrayList} gelesen. Wenn er beim Schreiben leer ist, wird {@code null} geschrieben. */
	public static <E> ProxyList<E> proxyListFrom(Producer<List<E>> getValue, Consumer<List<E>> setValue) throws NullPointerException {
		return proxyListFrom(translatedProducer(getValue, value -> {
			if (value instanceof ArrayList) return value;
			if (value != null) return new ArrayList<>(value);
			return new ArrayList<E>();
		}), translatedConsumer(setValue, value -> {
			if (value.size() > 1) return value;
			for (E item: value)
				return singletonList(item);
			return null;
		}), translatedProducer(getValue, value -> {
			if (value != null) return value;
			return emptyList();
		}));
	}

	/** Diese Methode ist eine Abkürzung für {@link ProxyList new ProxyList<>(getValue, setValue, getConst)}. **/
	public static <E> ProxyList<E> proxyListFrom(Producer<List<E>> getValue, Consumer<List<E>> setValue, Producer<List<E>> getConst) throws NullPointerException {
		return new ProxyList<>(getValue, setValue, getConst);
	}

	public final Producer<List<E>> getValue;

	public final Consumer<List<E>> setValue;

	public final Producer<List<E>> getConst;

	/** Dieser Konstruktor initialisiert {@link #getValue}, {@link KeySetView} und {@link #getConst}. */
	public ProxyList(Producer<List<E>> getValue, Consumer<List<E>> setValue, Producer<List<E>> getConst) throws NullPointerException {
		this.getValue = notNull(getValue);
		this.setValue = notNull(setValue);
		this.getConst = notNull(getConst);
	}

	@Override
	protected List<E> getData(boolean readonly) {
		return (readonly ? this.getConst : this.getValue).get();
	}

	@Override
	protected void setData(List<E> items) {
		this.setValue.set(items);
	}

}