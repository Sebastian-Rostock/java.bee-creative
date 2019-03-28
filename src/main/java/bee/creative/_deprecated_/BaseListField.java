package bee.creative._deprecated_;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import bee.creative.bind.Fields;
import bee.creative.bind.Fields.BaseField;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein abstraktes {@link ListField}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GEntry> Typ der Elemente. */
public abstract class BaseListField<GItem, GEntry> extends BaseField<GItem, List<GEntry>> implements ListField<GItem, GEntry> {

	/** Diese Methode gibt eine Bearbeitungskopie der gegebenen {@link List} zurück.
	 *
	 * @param value {@link List}.
	 * @return Bearbeitungskopie. */
	protected List<GEntry> customCopy(final List<GEntry> value) {
		return new ArrayList<>(value);
	}

	/** {@inheritDoc} */
	@Override
	public void clear(final GItem item) {
		if (this.get(item).isEmpty()) return;
		this.set(item, Collections.<GEntry>emptyList());
	}

	/** {@inheritDoc} */
	@Override
	public void put(final GItem item, final GEntry entry) {
		final List<GEntry> value = this.customCopy(this.get(item));
		value.add(entry);
		this.set(item, value);
	}

	/** {@inheritDoc} */
	@Override
	public void putAll(final GItem item, final Iterable<? extends GEntry> entries) {
		final List<GEntry> value = this.customCopy(this.get(item));
		if (!Iterables.addAll(value, entries)) return;
		this.set(item, value);
	}

	/** {@inheritDoc} */
	@Override
	public void put(final GItem item, final int index, final GEntry entry) {
		final List<GEntry> value = this.customCopy(this.get(item));
		value.add(index, entry);
		this.set(item, value);
	}

	/** {@inheritDoc} */
	@Override
	public void putAll(final GItem item, final int index, final Iterable<? extends GEntry> entries) {
		final List<GEntry> value = this.customCopy(this.get(item));
		if (!Iterables.addAll(value.subList(index, index), entries)) return;
		this.set(item, value);
	}

	/** {@inheritDoc} */
	@Override
	public void pop(final GItem item, final int index) {
		final List<GEntry> value = this.customCopy(this.get(item));
		value.remove(index);
		this.set(item, value);
	}

	/** {@inheritDoc} */
	@Override
	public void pop(final GItem item, final Object entry) {
		final List<GEntry> value = this.get(item);
		final int index = value.indexOf(entry);
		if (index < 0) return;
		this.pop(item, index);
	}

	/** {@inheritDoc} */
	@Override
	public void popAll(final GItem item, final Iterable<?> entries) {
		final List<GEntry> value = this.customCopy(this.get(item));
		if (!Iterables.removeAll(value, entries)) return;
		this.set(item, value);
	}

}