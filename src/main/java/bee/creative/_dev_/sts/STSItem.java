package bee.creative._dev_.sts;

import bee.creative.util.Comparators;

/** Diese abstrakte Klasse implementiert einen Datensatz, der in einem {@link #store() Graphspeicher} verwaltet wird.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class STSItem {

	/** Dieses Feld speichert den Graphspeicher, der dieses Objekt verwaltet. */
	protected final STSStore store;

	/** Dieses Feld speichert die Position dieses Datensatzes in der ihn vorhaltenden Datenstruktur des {@link #store Graphspeichers}. */
	protected final int index;

	@SuppressWarnings ("javadoc")
	protected STSItem(final STSStore store, final int index) {
		this.store = store;
		this.index = index;
	}

	{}

	/** Diese Methode gibt den dieses Objekt verwaltenden Graphspeicher zurück.
	 *
	 * @return Graphspeicher. */
	public STSStore store() {
		return this.store;
	}

	/** Diese Methode gibt den Streuwert zurück, welcher auf {@link STSStore#hash dem des Graphspeichers} sowie der {@link #index Position} dieses Datensatzes
	 * beruht.
	 *
	 * @return Streuwert. */
	protected final int hashImpl() {
		return this.store.hash ^ this.index;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn dieser Datensatz äquivalent zum gegeben ist. Dies ist nur dann gegeben, wenn er den gleichen
	 * {@link #store Graphspeicher} sowie die gleiche {@link #index Position} besitzt.
	 *
	 * @param that Datensatz.
	 * @return Äquivalenz.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	protected final boolean equalsImpl(final STSItem that) throws NullPointerException {
		return (this.store == that.store) && (this.index == that.index);
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn die Ordnung dieses Datensatzes kleiner, gleich oder größer als die der gegebenen
	 * Datensatzes ist.
	 *
	 * @param that Datensatz.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	protected final int compareImpl(final STSItem that) throws NullPointerException {
		final int result = Comparators.compare(this.index, that.index);
		if (result != 0) return result;
		return Comparators.compare(this.store.hash, that.store.hash);
	}

}
