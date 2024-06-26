//package bee.creative.qs.dev;
//
//import java.util.ArrayList;
//import bee.creative.lang.Objects;
//import bee.creative.qs.QE;
//import bee.creative.qs.QESet;
//import bee.creative.qs.QN;
//import bee.creative.qs.QS;
//
////
///** Diese Klasse implementiert einen Versionsverlauf (Triple-Store-Version-History), welcher Änderungen an den Hyperkanten eines Hypergraphen dritter Ordnung in
// * Versionen erfassen kann und zudem die Navigation zwischen diesen Versionen erlaubt.
// *
// * @author [cc-by] 2022 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
//public class TSVH {
//
//	// neu anlegen
//	TSVH(final TSVM context) {
//		this(context.target.newNode(), context);
//		this.selectImpl(new TSVE(this));
//	}
//
//	// aus knoten des verlaufs laden
//	TSVH(final TSVM owner, final QN context) {
//		this(context, owner);
//		this.check();
//	}
//
//	private TSVH(final QN context, final TSVM owner) {
//		this.owner = owner;
//		this.context = context;
//		this.edges = owner.targetEdges.havingContext(context);
//	}
//
//	final TSVM owner;
//
//	final QN context;
//
//	final QESet edges;
//
//	/** Dieses Feld speichert den aktiven Versionseintrag. */
//	private TSVE version;
//
//	/** Diese Methode setzt und aktiviert den gegebenen Versionseintrag. */
//	private void selectImpl(final TSVE version) {
//		this.version = Objects.notNull(version);
//		this.owner.activeEdges.havingSubject(this.context).popAll();
//		this.owner.target.newEdge(this.owner.domainContext, this.owner.activePredicate, this.context, version.node).put();
//	}
//
//	// TODO wenn es keinen nachfolger gibt, bleibt alles wie es ist
//	// andernfalls wird neue version erzeugt, diese gewählt und die alte nachfolge als zwei angebunden
//	/** Diese Methode verwirft alle {@link TSVE#nextVersion Nachfolgeversionen}. Dabei werden folgende Hyperkanten entfernt:
//	 * <ul>
//	 * <li>Alle aus {@link TSVM#sourceEdges} mit dem Objektknoten {@link TSVE#node}.</li>
//	 * <li>Alle aus {@link TSVM#targetEdges} mit dem Kontextknoten {@link TSVE#insert}.</li>
//	 * <li>Alle aus {@link TSVM#targetEdges} mit dem Kontextknoten {@link TSVE#delete}.</li>
//	 * </ul>
//	 * Sie sollte zu Beginn jeder Änderung aufgerufen werden. */
//	protected void todo() {
//
//		TSVE next = this.version.nextVersion();
//		if (next == null) return;
//		final ArrayList<QN> contexts = new ArrayList<>(10), subjects = new ArrayList<>(10);
//		while (next != null) {
//			subjects.add(next.node);
//			contexts.add(next.insert);
//			contexts.add(next.delete);
//			next = next.nextVersion();
//		}
//		this.version.nextVersion = null;
//		this.owner.targetEdges.havingContexts(this.owner.target.newNodes(contexts)).popAll();
//		this.owner.sourceEdges.havingSubjects(this.owner.target.newNodes(subjects)).popAll();
//	}
//
//	/** Diese Methode kehrt zur {@link TSVE#nextVersion() Nachfolgeversion} zurück. Dabei werden alle darin gemachten Änderungen wiederhergestellt.
//	 *
//	 * @return {@code true}, nur wenn das Wiederherstellen der nachfolgenden Version möglich war. */
//	public synchronized boolean redo() {
//		final TSVE prev = this.version, next = prev.nextVersion();
//		if (next == null) return false;
//		this.owner.targetEdges.havingContext(next.insert).withContext(this.context).putAll();
//		this.owner.targetEdges.havingContext(next.delete).withContext(this.context).popAll();
//		this.selectImpl(next);
//		return true;
//	}
//
//	/** Diese Methode kehrt zur {@link TSVE#prevVersion() Vorgängerversion} zurück. Dabei werden alle bis dahin gemachten Änderungen zurückgenommen.
//	 *
//	 * @return {@code true}, nur wenn das Wiederherstellen der vorhergehenden Version möglich war. */
//	public synchronized boolean undo() {
//		final TSVE next = this.version, prev = next.prevVersion();
//		if (prev == null) return false;
//		this.owner.targetEdges.havingContext(next.insert).withContext(this.context).popAll();
//		this.owner.targetEdges.havingContext(next.delete).withContext(this.context).putAll();
//		this.selectImpl(prev);
//		return true;
//	}
//
//	/** Diese Methode schließt die akteulle Version ab, ersetzt alle {@link TSVE#nextVersion Nachfolgeversionen} durch eine neue Version und wählt diese als
//	 * aktuelle. */
//	public synchronized void done() {
//		final TSVE prev = this.version, next = new TSVE(this);
//		this.todo();
//		this.owner.target.newEdge(this.owner.domainContext, this.owner.sourcePredicate, prev.node, next.node).put();
//		this.selectImpl(next);
//	}
//
//	/** Diese Methode sollte nach dem Zurücknehmen von Änderungen im grundlegenden {@link QS Graphspeicher} auferufen werden. */
//	public synchronized void check() {
//		this.version = new TSVE(this, this.owner.activeEdges.havingSubject(this.context).objects().first());
//	}
//
//	//
//
//	void select(final TSVE version) {
//		// branch übernehmen
//	}
//
//	/** Diese Methode liefert die diesen Versionsverlauf besitzende Versionsverwaltung. */
//	public TSVM owner() {
//		return this.owner;
//	}
//
//	public QS target() {
//		return this.owner.target;
//	}
//
//	/** Diese Methode liefert den Hyperknoten dieses Versionsverlaufs. Dieser wird als {@link QE#context() Kontextknoten} für alle {@link #edges() Hyperkanten}
//	 * des Versionsverlaufs verwendet.
//	 *
//	 * @return Verlaufsknoten. */
//	public QN context() {
//		return this.context;
//	}
//
//	/** Diese Methode liefert den Hyperknoten der aktuellen Version.
//	 *
//	 * @return Versionsknoten */
//	public synchronized TSVE version() {
//		return this.version;
//	}
//
//	/** Diese Methode liefert die Hyperkanten dieses Versionsverlaufs. Diese enthalten stets das Wissen der aktuellen Version. */
//	public QESet edges() {
//		return this.edges;
//	}
//
//	public void putEdges(final QE... edges) {
//		this.putEdges(this.owner.target.newEdges(edges));
//	}
//
//	public synchronized void putEdges(final Iterable<? extends QE> edges) {
//		this.todo();
//		final QESet putEdges = this.owner.target.newEdges(edges).withContext(this.context).except(this.edges).copy();
//		putEdges.putAll();
//		putEdges.withContext(this.version.delete).popAll();
//		putEdges.withContext(this.version.insert).putAll();
//	}
//
//	public void popEdges(final QE... edges) {
//		this.popEdges(this.owner.target.newEdges(edges));
//	}
//
//	public synchronized void popEdges(final Iterable<? extends QE> edges) {
//		this.todo();
//		final QESet popEdges = this.owner.target.newEdges(edges).withContext(this.context).intersect(this.edges).copy();
//		popEdges.popAll();
//		popEdges.withContext(this.version.insert).popAll();
//		popEdges.withContext(this.version.delete).putAll();
//	}
//
//}
