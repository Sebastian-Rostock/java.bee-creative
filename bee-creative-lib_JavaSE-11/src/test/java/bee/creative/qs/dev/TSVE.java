package bee.creative.qs.dev;

import java.util.ArrayList;
import bee.creative.lang.Objects;
import bee.creative.qs.QE;
import bee.creative.qs.QN;
import bee.creative.qs.QS;

/** Diese Klasse implementiert den Versionseintrag (Triple-Store-Version-Entry) eines {@code TSVH Versionsverlaufs}.
 * <p>
 * Ein {@link #nextVersion() nachfolgender} Versionseintrag verweist im {@link TSVM#domainContext} mit dem {@link TSVM#sourcePredicate} auf seinen
 * {@link #prevVersion() vorhergehenden} Versionseintrag. Durch das {@link #fork() Verzweigen} wird die {@link #nextVersion()} der {@link #prevVersion()} durch
 * einen neuen {@link #nextBranch()} ersetzt.
 *
 * @author [cc-by] 2022 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class TSVE {

	final TSVH owner;

	/** Dieses Feld speichert den {@link QE#subject() Subjektknoten} der Version. Diesem ist in {@link TSVM#insertEdges} der {@link #insert}, in
	 * {@link TSVM#deleteEdges} der {@link #delete} und in {@link TSVM#sourceEdges} die Vorgängerversion jeweils als {@link QE#object() Objektknoten}
	 * zugeordnet. */
	final QN node;

	/** Dieses Feld speichert den {@link QE#context() Kontextknoten} der {@link QE Hyperkanten}, die gegenüber der vorherigen Version eingefügt wurden. */
	final QN insert;

	/** Dieses Feld speichert den {@link QE#context() Kontextknoten} der {@link QE Hyperkanten}, die gegenüber der vorherigen Version entfernt wurden. */
	final QN delete;

	/** Dieses Feld puffert die Zweigversion. */
	TSVE nextBranch = this;

	/** Dieses Feld puffert die Nachfolgeversion. */
	TSVE nextVersion = this;

	TSVE prevBranch = this;

	/** Dieses Feld puffert die Vorgängerversion. */
	TSVE prevVersion = this;

	TSVE[] twigs;

	TSVE(final TSVH versionHistory) {
		this.owner = versionHistory;
		final TSVM versionManager = versionHistory.owner;
		final QS versionStore = versionManager.target;
		this.node = versionStore.newNode();
		this.insert = versionStore.newNode();
		this.delete = versionStore.newNode();
		versionStore.newEdge(versionManager.domainContext, versionManager.insertPredicate, this.node, this.insert).put();
		versionStore.newEdge(versionManager.domainContext, versionManager.deletePredicate, this.node, this.delete).put();
	}

	TSVE(final TSVH versionHistory, final QN node) {
		this.owner = versionHistory;
		this.node = node;
		final TSVM versionManager = versionHistory.owner;
		this.insert = Objects.notNull(versionManager.insertEdges.havingSubject(node).objects().first());
		this.delete = Objects.notNull(versionManager.deleteEdges.havingSubject(node).objects().first());
	}

	/** Diese Methode entfernt diesen Versionseintrag und alle seine Nachfolgeversionen. */
	public void pop() {
		// TODO aus kete der branches entfernen, diese wieder zusammenführen
		synchronized (this.owner) {

			TSVE next = this;
			final ArrayList<QN> contexts = new ArrayList<>(10), subjects = new ArrayList<>(10);
			while (next != null) {
				subjects.add(next.node);
				contexts.add(next.insert);
				contexts.add(next.delete);
				next = next.nextVersion();
			}
			if (this.prevVersion != null) {
				this.prevVersion.nextVersion = null;

			}
			this.nextVersion = this;
			this.owner.owner.targetEdges.havingContexts(this.owner.owner.target.newNodes(contexts)).popAll();
			this.owner.owner.sourceEdges.havingSubjects(this.owner.owner.target.newNodes(subjects)).popAll();
		}
	}

	// TODO neuer node zwischen prevVersioin und this sowie zwischen prevBranch und this; dann node liefern
	public TSVE fork() {
		synchronized (this.owner) {
			final TSVE forkBranch = new TSVE(this.owner), prevBranch = this.prevBranch(), prevVersion = this.prevVersion();
			if (prevBranch != null) {

			} else {

			}
			if (prevVersion != null) {

			} else {

				final TSVM versionManager = this.owner.owner;
				final QS versionStore = versionManager.target;
				versionStore.newEdge(versionManager.branchPredicate, versionManager.insertPredicate, this.node, this.insert).put();
				versionStore.newEdge(versionManager.domainContext, versionManager.deletePredicate, this.node, this.delete).put();

			}

			return null;
		}
	}

	public QN node() {
		return this.node;
	}

	/** Diese Methode liefert die Vorgängerversion. Wenn diese nicht existiert, wird {@code null} geliefert. */
	public TSVE prevVersion() {
		synchronized (this.owner) {
			if (this.prevVersion != this) return this.prevVersion;
			final QN prevNode = this.owner.owner.sourceEdges.havingSubject(this.node).objects().first();
			if (prevNode == null) return this.prevVersion = null;
			(this.prevVersion = new TSVE(this.owner, prevNode)).nextVersion = this;
			return this.prevVersion;
		}
	}

	/** Diese Methode liefert die Nachfolgeversion. Wenn diese nicht existiert, wird {@code null} geliefert. */
	public TSVE nextVersion() {
		synchronized (this.owner) {
			if (this.nextVersion != this) return this.nextVersion;
			final QN nextNode = this.owner.owner.sourceEdges.havingObject(this.node).subjects().first();
			if (nextNode == null) return this.nextVersion = null;
			(this.nextVersion = new TSVE(this.owner, nextNode)).prevVersion = this;
			return this.nextVersion;
		}
	}

	public TSVE prevBranch() {
		synchronized (this.owner) {
			if (this.prevBranch != this) return this.prevBranch;
			final QN prevNode = this.owner.owner.branchEdges.havingSubject(this.node).objects().first();
			if (prevNode == null) return this.prevBranch = null;
			(this.prevBranch = new TSVE(this.owner, prevNode)).nextBranch = this;
			return this.prevBranch;
		}
	}

	public TSVE nextBranch() {
		synchronized (this.owner) {
			if (this.nextBranch != this) return this.nextBranch;
			final QN nextNode = this.owner.owner.branchEdges.havingObject(this.node).subjects().first();
			if (nextNode == null) return this.nextBranch = null;
			(this.nextBranch = new TSVE(this.owner, nextNode)).prevBranch = this;
			return this.nextBranch;
		}
	}

}