package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Consumers.translatedConsumer;
import static bee.creative.util.Producers.translatedProducer;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

/** Diese Klasse implementiert eine {@link AbstractProxyMap}.
 *
 * @param <K> Typ der Schlüssel.
 * @param <V> Typ der Werte. */
public class ProxyMap<K, V> extends AbstractProxyMap<K, V, Map<K, V>> {

	/** Diese Methode liefert eine {@link ProxyMap}, deren Inhalt über den gegebenen {@code getValue} gelesen und über {@code setValue} geschrieben wird. Wenn der
	 * Inahlt beim Lesen {@code null} ist, wird eine leere {@link HashMap2} gelesen. Wenn er beim Schreiben leer ist, wird {@code null} geschrieben. */
	public static <K, V> ProxyMap<K, V> proxyMapFrom(Producer<Map<K, V>> getValue, Consumer<Map<K, V>> setValue) throws NullPointerException {
		return proxyMapFrom(translatedProducer(getValue, value -> {
			if (value instanceof HashMap2) return value;
			if (value != null) return new HashMap2<>(value);
			return new HashMap2<>();
		}), translatedConsumer(setValue, value -> {
			if (value.size() > 1) return value;
			for (var entry: value.entrySet())
				return singletonMap(entry.getKey(), entry.getValue());
			return null;
		}), translatedProducer(getValue, value -> {
			if (value != null) return value;
			return emptyMap();
		}));
	}

	/** Diese Methode ist eine Abkürzung für {@link ProxyMap new ProxyMap<>(getValue, setValue, getConst)}. **/
	public static <K, V> ProxyMap<K, V> proxyMapFrom(Producer<Map<K, V>> getValue, Consumer<Map<K, V>> setValue, Producer<Map<K, V>> getConst)
		throws NullPointerException {
		return new ProxyMap<>(getValue, setValue, getConst);
	}

	/** Dieses Feld speichert die Funktion zum Lesen der veränderlichen und ggf. kopierten {@link Map}. */
	public final Producer<Map<K, V>> getValue;

	/** Dieses Feld speichert die Funktion zum Schreiben der {@link Map}, die über {@link #getValue} ermittelt wurde. */
	public final Consumer<Map<K, V>> setValue;

	/** Dieses Feld speichert die Funktion zum Lesen der ggf. unveränderlichen {@link Map}. */
	public final Producer<Map<K, V>> getConst;

	/** Dieser Konstruktor initialisiert {@link #getValue}, {@link KeySetView} und {@link #getConst}. */
	public ProxyMap(Producer<Map<K, V>> getValue, Consumer<Map<K, V>> setValue, Producer<Map<K, V>> getConst) throws NullPointerException {
		this.getValue = notNull(getValue);
		this.setValue = notNull(setValue);
		this.getConst = notNull(getConst);
	}

	@Override
	protected Map<K, V> getData(boolean readonly) {
		return (readonly ? this.getConst : this.getValue).get();
	}

	@Override
	protected void setData(Map<K, V> items) {
		this.setValue.set(items);
	}

}