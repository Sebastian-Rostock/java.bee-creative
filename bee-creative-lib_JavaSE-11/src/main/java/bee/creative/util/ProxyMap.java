package bee.creative.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link AbstractProxyMap}, deren Inhalt über ein gegebenen {@link Property} angebunden werden kann.
 *
 * @param <K> Typ der Schlüssel.
 * @param <V> Typ der Werte. */
public class ProxyMap<K, V> extends AbstractProxyMap<K, V, Map<K, V>> {

	/** Diese Methode liefert eine {@link ProxyMap}, die über {@code getValue} eine {@link Map} oder {@code null} liest und über {@code setValue} eine {@link Map}
	 * oder {@code null} schreibt.
	 *
	 * @throws NullPointerException */
	public static <K, V> ProxyMap<K, V> from(Producer<Map<K, V>> getValue, Consumer<Map<K, V>> setValue) throws NullPointerException {
		return ProxyMap.from(Producers.translatedProducer(getValue, value -> {
			if (value instanceof HashMap2) return value;
			if (value != null) return new HashMap2<>(value);
			return new HashMap2<>();
		}), Consumers.translatedConsumer(setValue, value -> {
			if (value.size() > 1) return value;
			for (var entry: value.entrySet())
				return Collections.singletonMap(entry.getKey(), entry.getValue());
			return null;
		}), Producers.translatedProducer(getValue, value -> {
			if (value != null) return value;
			return Collections.emptyMap();
		}));
	}

	/** Diese Methode ist eine Abkürzung für {@link ProxyMap new ProxyMap<>(getValue, setValue, getConst)}. **/
	public static <K, V> ProxyMap<K, V> from(Producer<Map<K, V>> getValue, Consumer<Map<K, V>> setValue, Producer<Map<K, V>> getConst)
		throws NullPointerException {
		return new ProxyMap<>(getValue, setValue, getConst);
	}

	/** Dieses Feld speichert die Funktion zum Lesen der veränderlichen und ggf. kopierten {@link Map}. */
	public final Producer<Map<K, V>> getValue;

	/** Dieses Feld speichert die Funktion zum Schreiben der {@link Map}, die über {@link #getValue} ermittelt wurde. */
	public final Consumer<Map<K, V>> setValue;

	/** Dieses Feld speichert die Funktion zum Lesen der ggf. unveränderlichen {@link Map}. */
	public final Producer<Map<K, V>> getConst;

	/** Dieser Konstruktor initialisiert {@link #getValue}, {@link KeySetView} und {@link #getConst}.
	 *
	 * @throws NullPointerException */
	public ProxyMap(Producer<Map<K, V>> getValue, Consumer<Map<K, V>> setValue, Producer<Map<K, V>> getConst) throws NullPointerException {
		this.getValue = Objects.notNull(getValue);
		this.setValue = Objects.notNull(setValue);
		this.getConst = Objects.notNull(getConst);
	}

	@Override
	public Map<K, V> getData(boolean readonly) {
		return (readonly ? this.getConst : this.getValue).get();
	}

	@Override
	protected void setData(Map<K, V> items) {
		this.setValue.set(items);
	}

}