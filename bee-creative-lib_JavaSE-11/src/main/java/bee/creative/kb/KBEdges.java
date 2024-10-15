package bee.creative.kb;

import java.util.function.Consumer;
import bee.creative.lang.Objects;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterator2;

/** Diese Klasse implementiert das {@link Iterable2} der {@link KBEdge Kanten} eines {@link KBState Wissensstands}.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class KBEdges implements Iterable2<KBEdge> {

	/** Diese Methode liefert den {@link KBState Wissensstand}, dessen {@link KBEdge Kanten} iteriert werden. */
	public KBState owner() {
		return this.owner;
	}

	/** Diese Methode liefert einen {@link KBEdges Kantenauswahl}, die nur {@link KBEdge Kanten} mit den {@link KBEdge#sourceRef() Quellreferenzen} liefert, die
	 * in den gegebenen Referenzen {@code selectSourceRefs} enthalten sind. */
	public KBEdges selectSourceRefs(int... selectSourceRefs) {
		return KBState.computeSelect(this.acceptSourceRefset, this.refuseSourceRefset, selectSourceRefs, acceptSourceRefset -> new KBEdges(this.owner,
			acceptSourceRefset, null, this.acceptTargetRefset, this.refuseTargetRefset, this.acceptRelationRefset, this.refuseRelationRefset));
	}

	/** Diese Methode liefert einen {@link KBEdges Kantenauswahl}, die nur {@link KBEdge Kanten} mit den {@link KBEdge#sourceRef() Quellreferenzen} liefert, die
	 * nicht in den gegebenen Referenzen {@code exceptSourceRefs} enthalten sind. */
	public KBEdges exceptSourceRefs(int... exceptSourceRefs) {
		return KBState.computeExcept(this.acceptSourceRefset, this.refuseSourceRefset, exceptSourceRefs,
			acceptSourceRefset -> new KBEdges(this.owner, acceptSourceRefset, null, this.acceptTargetRefset, this.refuseTargetRefset, this.acceptRelationRefset,
				this.refuseRelationRefset),
			refuseSourceRefset -> new KBEdges(this.owner, null, refuseSourceRefset, this.acceptTargetRefset, this.refuseTargetRefset, this.acceptRelationRefset,
				this.refuseRelationRefset));
	}

	/** Diese Methode übergibt die Referenzen der {@link KBEdge Kanten} an {@link RUN#run(int, int, int) task.run()}. */
	public void forEach(RUN task) {
		this.owner.forEachEdge(this.acceptSourceRefset, this.refuseSourceRefset, this.acceptTargetRefset, this.refuseTargetRefset, this.acceptRelationRefset,
			this.refuseRelationRefset, task);
	}

	@Override
	public void forEach(Consumer<? super KBEdge> action) {
		this.forEach((sourceRef, targetRef, relationRef) -> action.accept(new KBEdge(sourceRef, targetRef, relationRef)));
	}

	@Override
	public Iterator2<KBEdge> iterator() {
		return this.owner.edgeIterator(this.acceptSourceRefset, this.refuseSourceRefset, this.acceptTargetRefset, this.refuseTargetRefset,
			this.acceptRelationRefset, this.refuseRelationRefset);
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
		this(owner, null, null, null, null, null, null);
	}

	private KBEdges(KBState owner, int[] acceptSourceRefset, int[] refuseSourceRefset, int[] acceptTargetRefset, int[] refuseTargetRefset,
		int[] acceptRelationRefset, int[] refuseRelationRefset) {
		this.owner = owner;
		this.acceptSourceRefset = acceptSourceRefset;
		this.refuseSourceRefset = refuseSourceRefset;
		this.acceptTargetRefset = acceptTargetRefset;
		this.refuseTargetRefset = refuseTargetRefset;
		this.acceptRelationRefset = acceptRelationRefset;
		this.refuseRelationRefset = refuseRelationRefset;
	}

	private final KBState owner;

	private final int[] acceptSourceRefset;

	private final int[] refuseSourceRefset;

	private final int[] acceptTargetRefset;

	private final int[] refuseTargetRefset;

	private final int[] acceptRelationRefset;

	private final int[] refuseRelationRefset;

}
