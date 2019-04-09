package bee.creative._deprecated_;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import bee.creative.bind.Fields;
import bee.creative.bind.Fields.BaseField;
import bee.creative.lang.Objects;
import bee.creative.util.HashMap2;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein abstraktes {@link MapField}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public abstract class BaseMapField<GItem, GKey, GValue> extends BaseField<GItem, Map<GKey, GValue>> implements MapField<GItem, GKey, GValue> {

	/** Diese Methode gibt eine Bearbeitungskopie der gegebenen {@link Map} zurück.
	 *
	 * @param value {@link Map}.
	 * @return Bearbeitungskopie. */
	protected Map<GKey, GValue> customCopy(final Map<GKey, GValue> value) {
		return new HashMap2<>(value);
	}

	/** {@inheritDoc} */
	@Override
	public void clear(final GItem item) {
		if (this.get(item).isEmpty()) return;
		this.set(item, Collections.<GKey, GValue>emptyMap());
	}

	/** {@inheritDoc} */
	@Override
	public void put(final GItem item, final GKey key, final GValue value) {
		Map<GKey, GValue> map = this.get(item);
		if (Objects.equals(map.get(key), value) && ((value != null) || map.containsKey(key))) return;
		map = this.customCopy(this.get(item));
		map.put(key, value);
		this.set(item, map);
	}

	/** {@inheritDoc} */
	@Override
	public void putAll(final GItem item, final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries) {
		final Map<GKey, GValue> map = this.customCopy(this.get(item));
		boolean modified = false;
		for (final Entry<? extends GKey, ? extends GValue> entry: entries) {
			modified = !Objects.equals(map.put(entry.getKey(), entry.getValue()), entry.getValue()) || modified;
		}
		if (!modified) return;
		this.set(item, map);
	}

	/** {@inheritDoc} */
	@Override
	public void pop(final GItem item, final Object key) {
		Map<GKey, GValue> map = this.get(item);
		if (!map.containsKey(key)) return;
		map = this.customCopy(map);
		map.remove(key);
		this.set(item, map);
	}

	/** {@inheritDoc} */
	@Override
	public void popAll(final GItem item, final Iterable<?> keys) {
		final Map<GKey, GValue> map = this.customCopy(this.get(item));
		if (!Iterables.removeAll(map.keySet(), keys)) return;
		this.set(item, map);
	}

}