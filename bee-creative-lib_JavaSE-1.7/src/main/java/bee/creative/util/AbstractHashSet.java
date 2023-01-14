package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/** Diese Klasse implementiert ein auf {@link AbstractHashData} aufbauendes {@link Set} mit geringem {@link AbstractHashData Speicherverbrauch}.
 * <p>
 * <b>Achtung:</b> Die Ermittlung von {@link Object#hashCode() Streuwerte} und {@link Object#equals(Object) Äquivalenz} der Elemente erfolgt nicht wie in
 * {@link Set} beschrieben über die Methoden der Elemente, sondern über die Methoden {@link #customHash(Object)} bzw. {@link #customEqualsKey(int, Object)}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public abstract class AbstractHashSet<GItem> extends AbstractHashData<GItem, GItem> implements Set<GItem> {

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwaltet werden kann.
	 *
	 * @param capacity Anzahl der maximal verwaltbaren Elemente.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Elementen ist. */
	public void allocate(final int capacity) throws IllegalArgumentException {
		this.allocateImpl(capacity);
	}

	/** Diese Methode gibt die Anzahl der Elemente zurück, die ohne erneuter Speicherreservierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	public int capacity() {
		return this.capacityImpl();
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum. */
	public void compact() {
		this.allocateImpl(this.countImpl());
	}

	/** Diese Methode liefert das zum gegebenen Element äquivalente und in diesem {@link Set} verwaltete Element. Wenn das {@link Set} kein solches Element
	 * enthält, wird das gegebene Element dem {@link Set} hinzugefügt und zurückgegeben.<br>
	 * Durch Überschreiben von {@link #customInstallKey(Object)} kann beeinflusst werden, welches Element hinzugefügt und zurückgegeben wird. Zudem kann durch
	 * Überschreiben von {@link #customReuseEntry(int)} auf die Wiederverwendung des gelieferten Elements reagiert werden.
	 *
	 * @param item gesuchtes Element.
	 * @return enthaltenes und ggf. eingefügtes Element. */
	public GItem install(final GItem item) {
		return this.customGetKey(this.installImpl(item));
	}

	/** Diese Methode liefert das zum gegebenen Element äquivalente und in diesem {@link Set} verwaltete Element. Wenn das {@link Set} kein solches Element
	 * enthält, wird das über {@code installItem} aus dem gegebenen Element abgeleitete Element dem {@link Set} hinzugefügt und zurückgegeben.
	 *
	 * @param item gesuchtes Element.
	 * @param installItem Methode zur Überführung des gegebenen Elements in das einzutragende Element.
	 * @return enthaltenes und ggf. eingefügtes Element. */
	public GItem install(final GItem item, Getter<? super GItem, ? extends GItem> installItem) {
		return this.customGetKey(this.installImpl(item, installItem, Getters.<GItem>neutral()));
	}

	@Override
	protected GItem customGetValue(final int entryIndex) {
		return null;
	}

	@Override
	protected GItem customSetValue(final int entryIndex, final GItem value) {
		return null;
	}

	@Override
	public int size() {
		return this.countImpl();
	}

	@Override
	public boolean isEmpty() {
		return this.countImpl() == 0;
	}

	@Override
	public void clear() {
		this.clearImpl();
	}

	@Override
	public boolean add(final GItem item) {
		return this.putKeyImpl(item);
	}

	@Override
	public boolean addAll(final Collection<? extends GItem> items) {
		final int count = this.countImpl();
		for (final GItem item: items) {
			this.putKeyImpl(item);
		}
		return count != this.countImpl();
	}

	@Override
	public boolean contains(final Object item) {
		return this.hasKeyImpl(item);
	}

	@Override
	public boolean containsAll(final Collection<?> items) {
		return this.newKeysImpl().containsAll(items);
	}

	@Override
	public boolean remove(final Object item) {
		return this.popKeyImpl(item);
	}

	@Override
	public boolean removeAll(final Collection<?> items) {
		return this.newKeysImpl().removeAll(items);
	}

	@Override
	public boolean retainAll(final Collection<?> items) {
		return this.newKeysImpl().retainAll(items);
	}

	@Override
	public Iterator<GItem> iterator() {
		return this.newKeysIteratorImpl();
	}

	@Override
	public int hashCode() {
		return this.newKeysImpl().hashCode();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Set<?>)) return false;
		final Set<?> that = (Set<?>)object;
		if (that.size() != this.size()) return false;
		return this.containsAll(that);
	}

	@Override
	public Object[] toArray() {
		return this.newKeysImpl().toArray();
	}

	@Override
	public <GItem> GItem[] toArray(final GItem[] result) {
		return this.newKeysImpl().toArray(result);
	}

	@Override
	public String toString() {
		return this.newKeysImpl().toString();
	}

}
