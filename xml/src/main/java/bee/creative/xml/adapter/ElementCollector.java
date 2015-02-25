package bee.creative.xml.adapter;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import bee.creative.xml.view.NodeListView;
import bee.creative.xml.view.NodeView;

/**
 * Diese Klasse implementiert die {@link NodeList} für {@link Element#getElementsByTagName(String)}, {@link Element#getElementsByTagNameNS(String, String)},
 * {@link Document#getElementsByTagName(String)} und {@link Document#getElementsByTagNameNS(String, String)} mit der entsprechenden Sematik beim Zusammenstellen
 * der Elementknoten.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class ElementCollector implements NodeList {

	/**
	 * Diese Methode sammelt alle Elementknoten.
	 * 
	 * @param children {@link NodeListView}.
	 */
	final void collectElements(final NodeListView children) {
		for (final NodeView nodeView: children) {
			if (nodeView.type() == NodeView.TYPE_ELEMENT) {
				this.list.add(nodeView);
				this.collectElements(nodeView.children());
			}
		}
	}

	/**
	 * Diese Methode sammelt alle Elementknoten mit passendem {@link #uri}.
	 * 
	 * @param children {@link NodeListView}.
	 */
	final void collectElementsByUri(final NodeListView children) {
		for (final NodeView nodeView: children) {
			if (nodeView.type() == NodeView.TYPE_ELEMENT) {
				if (this.uri.equals(nodeView.uri())) {
					this.list.add(nodeView);
				}
				this.collectElementsByUri(nodeView.children());
			}
		}
	}

	/**
	 * Diese Methode sammelt alle Elementknoten mit passendem {@link #name}.
	 * 
	 * @param children {@link NodeListView}.
	 */
	final void collectElementsByName(final NodeListView children) {
		for (final NodeView nodeView: children) {
			if (nodeView.type() == NodeView.TYPE_ELEMENT) {
				if (this.name.equals(nodeView.name())) {
					this.list.add(nodeView);
				}
				this.collectElementsByName(nodeView.children());
			}
		}
	}

	/**
	 * Diese Methode sammelt alle Elementknoten mit passendem {@link #uri} und {@link #name}.
	 * 
	 * @param children {@link NodeListView}.
	 */
	final void collectElementsByLabel(final NodeListView children) {
		for (final NodeView nodeView: children) {
			if (nodeView.type() == NodeView.TYPE_ELEMENT) {
				if (this.name.equals(nodeView.name()) && this.uri.equals(nodeView.uri())) {
					this.list.add(nodeView);
				}
				this.collectElementsByLabel(nodeView.children());
			}
		}
	}

	/**
	 * Dieses Feld speichert die gesuchte {@link NodeView#uri()} oder {@code "*"}.
	 */
	protected final String uri;

	/**
	 * Dieses Feld speichert den gesuchten {@link NodeView#name()} oder {@code "*"}.
	 */
	protected final String name;

	/**
	 * Dieses Feld speichert die gesammelten Elementknoten.
	 */
	protected final List<NodeView> list;

	/**
	 * Dieses Feld speichert die Anzahl der gesammelten Elementknoten.
	 */
	protected final int size;

	/**
	 * Dieser Konstruktor initialisiert die Parameter zur Zusammenstellung der {@link NodeView}s.
	 * 
	 * @param children {@link NodeListView} der rekursiv analysierten Kindknoten.
	 * @param uri Uri oder {@code "*"}.
	 * @param name Name oder {@code "*"}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public ElementCollector(final NodeListView children, final String uri, final String name) throws NullPointerException {
		if ((children == null) || (uri == null) || (name == null)) throw new NullPointerException();
		this.uri = uri;
		this.name = name;
		this.list = new ArrayList<NodeView>();
		if ("*".equals(uri)) {
			if ("*".equals(name)) {
				this.collectElements(children);
			} else {
				this.collectElementsByName(children);
			}
		} else {
			if ("*".equals(name)) {
				this.collectElementsByUri(children);
			} else {
				this.collectElementsByLabel(children);
			}
		}
		this.size = this.list.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node item(final int index) {
		if (index < this.size) return new ElementAdapter(this.list.get(index));
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLength() {
		return this.size;
	}

}