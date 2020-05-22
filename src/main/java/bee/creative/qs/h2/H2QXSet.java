package bee.creative.qs.h2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import bee.creative.qs.QS;
import bee.creative.qs.QXSet;
import bee.creative.util.Iterables;

public abstract class H2QXSet<GI, GISet extends Iterable<GI>> implements QXSet<GI, GISet> {

	protected final H2QS owner;

	/** Dieses Feld speichert die SQL-Anfrage zur Ermittlung der Tabelle. */
	protected final String select;

	H2QXSet(final H2QS owner, final String select) {
		this.owner = owner;
		this.select = select;
	}

	@Override
	public long size() {
		return this.owner.sizeImpl(this);
	}

	@Override
	public QS owner() {
		return this.owner;
	}

	@Override
	public boolean hasAny() {
		return this.owner.hasImpl(this);
	}

	@Override
	public Set<GI> toSet() {
		final Set<GI> result = new HashSet<>();
		Iterables.addAll(result, this);
		return result;
	}

	@Override
	public List<GI> toList() {
		final List<GI> result = new ArrayList<>();
		Iterables.addAll(result, this.order());
		return result;
	}

	@Override
	public String toString() {
		return this.toList().toString();
	}

}
