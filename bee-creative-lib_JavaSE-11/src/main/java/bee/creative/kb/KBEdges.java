package bee.creative.kb;

import java.util.function.Consumer;
import bee.creative.lang.Objects;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterator2;

/** Diese Klasse implementiert das {@link Iterable2} der {@link KBEdge Kanten} eines {@link KBState Wissensstandes}.
 * 
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class KBEdges implements Iterable2<KBEdge> {

	/** Diese Methode liefert den {@link KBState Wissensstand}, dessen {@link KBEdge Kanten} iteriert werden. */
	public KBState owner() {
		return this.owner;
	}

	/** Diese Methode übergibt die Referenzen aller {@link KBEdge Kanten} an {@link RUN#run(int, int, int) task.run()}. */
	public void forEach(RUN task) {
		this.owner.forEachEdge(task);
	}

	@Override
	public void forEach(Consumer<? super KBEdge> action) {
		this.owner.forEachEdge((RUN)(sourceRef, targetRef, relationRef) -> action.accept(new KBEdge(sourceRef, targetRef, relationRef)));
	}

	@Override
	public Iterator2<KBEdge> iterator() {
		return this.owner.edgeIterator();
	}

	Iterator2<KBEdge> iterator(REFSET sourceRefs, REFSET targetRefs, REFSET relationRefs) {
		return this.owner.edgeIterator(sourceRefs, targetRefs, relationRefs);
	}

	@Override
	public String toString() {
		return Objects.printIterable(false, this);
	}

	/** Diese Schnittstelle definiert den Empfänger der Referenzen für {@link KBEdges#forEach(RUN)}. */
	public interface RUN {

		/** Diese Methode verarbeitet die gegebene Quellreferenz {@code sourceRef}, Zielreferenz {@code targetRef} und Beziehungsreferenz {@code relationRef} einer
		 * {@link KBEdge Kante}. */
		void run(int sourceRef, int targetRef, int relationRef);

	}

	KBEdges(KBState owner) {
		this.owner = owner;
	}

	final KBState owner;

}
