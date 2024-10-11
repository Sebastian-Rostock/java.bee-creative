package bee.creative.str;

import java.util.function.Consumer;
import bee.creative.lang.Objects;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterator2;

public class STREdges implements Iterable2<STREdge> {

	public STRState owner() {
		return this.owner;
	}

	/** Diese Methode übergibt die Referenzen aller {@link STREdge Hyperkanten} an {@link RUN#run(int, int, int) task.run()}. */
	public void forEach(RUN task) {
		this.owner.forEachEdge(task);
	}

	@Override
	public void forEach(Consumer<? super STREdge> action) {
		this.owner.forEachEdge((RUN)(sourceRef, targetRef, relationRef) -> action.accept(new STREdge(sourceRef, targetRef, relationRef)));
	}

	@Override
	public Iterator2<STREdge> iterator() {
		return this.owner.edgeIterator();
	}

	Iterator2<STREdge> iterator(REFSET sourceRefs, REFSET targetRefs, REFSET relationRefs) {
		return this.owner.edgeIterator(sourceRefs, targetRefs, relationRefs);
	}

	@Override
	public String toString() {
		return Objects.printIterable(false, this);
	}

	STREdges(STRState owner) {
		this.owner = owner;
	}

	private STRState owner;

	/** Diese Schnittstelle definiert den Empfänger der Referenzen für {@link #forEach(RUN)}. */
	public static interface RUN {

		/** Diese Methode verarbeitet die gegebenen Referenzen einer {@link STREdge Hyperkante}. */
		void run(int sourceRef, int targetRef, int relationRef);

	}

}
