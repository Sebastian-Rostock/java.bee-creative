package bee.creative.qs.dev;

import java.util.ArrayList;
import bee.creative.lang.Objects;
import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QS;

//
/** Diese Klasse implementiert einen Versionsverlauf (Triple-Store-Version-History), welcher Änderungen an den Hyperkanten eines Hypergraphen dritter Ordnung in
 * Versionen erfassen kann und zudem die Navigation zwischen diesen Versionen erlaubt.
 *
 * @author [cc-by] 2022 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class TSVH {

	// neu anlegen
	TSVH(final TSVM tsvm) {
		this(tsvm.target.newNode(), tsvm);
		this.select(new TSVE(this.owner));
	}

	// aus knoten des verlaufs laden
	TSVH(final TSVM tsvm, final QN node) {
		this(node, tsvm);
		this.check();
	}

	private TSVH(final QN node, final TSVM tsvm) {
		this.owner = tsvm;
		this.node = node;
		this.edges = tsvm.domainEdges.havingContext(this.node);
	}

	/** Dieses Feld speichert die diesen Versionsverlauf besitzende Versionsverwaltung. */
	final TSVM owner;

	/** Dieses Feld speichert den {@link QE#context() Kontextknoten} dieses Versionsverlaufs. */
	final QN node;

	final QESet edges;

	/** Dieses Feld speichert den aktiven Versionseintrag. */
	private TSVE version;

	/** Diese Methode liefert und aktualisiert die {@link TSVE#next Nachfolgeversion} des gegebenen Versionseintrags, sofern eine solche existiert. */
	private TSVE next(final TSVE prev) {
		if (prev.next != null) return prev.next;
		final QN node = this.owner.sourceEdges.havingObject(prev.entry).subjects().first();
		return node != null ? prev.next = new TSVE(this.owner, node) : null;
	}

	/** Diese Methode setzt und aktiviert den gegebenen Versionseintrags. */
	private void select(final TSVE version) {
		this.version = Objects.notNull(version);
		this.owner.activeEdges.havingSubject(this.node).popAll();
		this.owner.target.newEdge(this.owner.domainContext, this.owner.activePredicate, this.node, version.entry).put();
	}

	/** Diese Methode verwirft alle {@link TSVE#next Nachfolgeversionen}. Dabei werden folgende Hyperkanten entfernt:
	 * <ul>
	 * <li>Alle aus {@link TSVM#sourceEdges} mit dem Objektknoten {@link TSVE#entry}.</li>
	 * <li>Alle aus {@link TSVM#targetEdges} mit dem Kontextknoten {@link TSVE#insert}.</li>
	 * <li>Alle aus {@link TSVM#targetEdges} mit dem Kontextknoten {@link TSVE#delete}.</li>
	 * </ul>
	 * Sie sollte zu Beginn jeder Änderung aufgerufen werden. */
	protected void todo() {
		TSVE next = this.version.next;
		if (next == null) return;
		final ArrayList<QN> contexts = new ArrayList<>(10), subjects = new ArrayList<>(10);
		while (next != null) {
			subjects.add(next.entry);
			contexts.add(next.insert);
			contexts.add(next.delete);
			next = this.next(next);
		}
		this.version.next = null;
		this.owner.targetEdges.havingContexts(this.owner.target.newNodes(contexts)).popAll();
		this.owner.sourceEdges.havingSubjects(this.owner.target.newNodes(subjects)).popAll();
	}

	/** Diese Methode kehrt zur nachfolgenden {@link #version() Version} zurück. Dabei werden alle darin gemachten Änderungen wiederhergestellt.
	 *
	 * @return {@code true}, nur wenn das Wiederherstellen der nachfolgenden Version möglich war. */
	public synchronized boolean redo() {
		final TSVE prev = this.version, next = this.next(prev);
		if (next == null) return false;
		this.owner.targetEdges.havingContext(next.insert).withContext(this.node).putAll();
		this.owner.targetEdges.havingContext(next.delete).withContext(this.node).popAll();
		this.select(next);
		return true;
	}

	/** Diese Methode kehrt zur vorherigen {@link #version() Version} zurück. Dabei werden alle bis dahin gemachten Änderungen zurückgenommen.
	 *
	 * @return {@code true}, nur wenn das Wiederherstellen der vorhergehenden Version möglich war. */
	public synchronized boolean undo() {
		final TSVE next = this.version;
		final QN node = this.owner.sourceEdges.havingSubject(next.entry).objects().first();
		if (node == null) return false;
		final TSVE prev = new TSVE(this.owner, node);
		prev.next = next;
		this.owner.targetEdges.havingContext(next.insert).withContext(this.node).popAll();
		this.owner.targetEdges.havingContext(next.delete).withContext(this.node).putAll();
		this.select(prev);
		return true;
	}

	/** Diese Methode schließt die akteulle Version ab, ersetzt alle {@link TSVE#next Nachfolgeversionen} durch eine neue Version und wählt diese als aktuelle. */
	public synchronized void done() {
		final TSVE prev = this.version, next = new TSVE(this.owner);
		this.todo();
		this.owner.target.newEdge(this.owner.domainContext, this.owner.sourcePredicate, prev.entry, next.entry).put();
		this.select(next);
	}

	/** Diese Methode sollte nach dem Zurücknehmen von Änderungen im grundlegenden {@link QS Graphspeicher} auferufen werden. */
	public synchronized void check() {
		this.version = new TSVE(this.owner, this.owner.activeEdges.havingSubject(this.node).objects().first());
		this.version.next = this.next(this.version);
	}

	/** Diese Methode liefert die diesen Versionsverlauf besitzende Versionsverwaltung. */
	public TSVM owner() {
		return this.owner;
	}

	/** Diese Methode liefert den Hyperknoten dieses Versionsverlaufs. Dieser wird als {@link QE#context() Kontextknoten} für alle über
	 * {@link #newEdge(QN, QN, QN)} erzeugten sowie über {@link #edges()} gelieferten Hyperkanten verwendet.
	 *
	 * @return Verlaufsknoten. */
	public QN history() {
		return this.node;
	}

	/** Diese Methode liefert den Hyperknoten der aktuellen Version.
	 *
	 * @return Versionsknoten */
	public synchronized QN version() {
		return this.version.entry;
	}

	/** Diese Methode liefert die Hyperkanten dieses Versionsverlaufs. Diese enthalten stets das Wissen der aktuellen Version. */
	public QESet edges() {
		return this.edges;
	}

	public QE newEdge(final QN predicate, final QN subject, final QN object) {
		return this.owner.target.newEdge(this.node, predicate, subject, object);
	}

	public void putEdges(final QE... edges) {
		this.putEdges(this.owner.target.newEdges(edges));
	}

	public synchronized void putEdges(final Iterable<? extends QE> edges) {
		this.todo();
		final QESet newEdges = this.owner.target.newEdges(edges).withContext(this.node);
		final QESet putEdges = newEdges.except(this.edges).copy();
		putEdges.putAll();
		putEdges.withContext(this.version.delete).popAll();
		putEdges.withContext(this.version.insert).putAll();
	}

	public void popEdges(final QE... edges) {
		this.popEdges(this.owner.target.newEdges(edges));
	}

	public synchronized void popEdges(final Iterable<? extends QE> edges) {
		this.todo();
		final QESet newEdges = this.owner.target.newEdges(edges).withContext(this.node);
		final QESet popEdges = this.edges.except(newEdges).copy();
		popEdges.popAll();
		popEdges.withContext(this.version.insert).popAll();
		popEdges.withContext(this.version.delete).putAll();
	}

}
