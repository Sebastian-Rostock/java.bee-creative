package bee.creative._deprecated_;

import java.util.Collections;
import java.util.Set;
import bee.creative.bind.Fields;
import bee.creative.bind.Fields.BaseField;
import bee.creative.util.HashSet2;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein abstraktes {@link SetField}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GEntry> Typ der Elemente. */
public abstract class BaseSetField<GItem, GEntry> extends BaseField<GItem, Set<GEntry>> implements SetField<GItem, GEntry> {

	/** Diese Methode gibt eine Bearbeitungskopie des gegebenen {@link Set} zurück.
	 *
	 * @param value {@link Set}.
	 * @return Bearbeitungskopie. */
	protected Set<GEntry> customCopy(final Set<GEntry> value) {
		return new HashSet2<>(value);
	}

	/** {@inheritDoc} */
	@Override
	public void clear(final GItem item) {
		if (this.get(item).isEmpty()) return;
		this.set(item, Collections.<GEntry>emptySet());
	}

	/** {@inheritDoc} */
	@Override
	public void put(final GItem item, final GEntry entry) {
		Set<GEntry> value = this.get(item);
		if (value.contains(entry)) return;
		value = this.customCopy(this.get(item));
		value.add(entry);
		this.set(item, value);
	}

	/** {@inheritDoc} */
	@Override
	public void putAll(final GItem item, final Iterable<? extends GEntry> entries) {
		final Set<GEntry> value = this.customCopy(this.get(item));
		if (!Iterables.addAll(value, entries)) return;
		this.set(item, value);
	}

	/** {@inheritDoc} */
	@Override
	public void pop(final GItem item, final Object entry) {
		Set<GEntry> value = this.get(item);
		if (!value.contains(entry)) return;
		value = this.customCopy(this.get(item));
		value.remove(entry);
		this.set(item, value);
	}

	/** {@inheritDoc} */
	@Override
	public void popAll(final GItem item, final Iterable<?> entries) {
		final Set<GEntry> value = this.customCopy(this.get(item));
		if (!Iterables.removeAll(value, entries)) return;
		this.set(item, value);
	}

}