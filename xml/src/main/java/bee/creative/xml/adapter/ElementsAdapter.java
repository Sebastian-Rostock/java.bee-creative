package bee.creative.xml.adapter;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import bee.creative.xml.view.ChildView;
import bee.creative.xml.view.ChildrenView;
import bee.creative.xml.view.ElementView;

/**
 * Diese Klasse implementiert die {@link NodeList} für {@link Element#getElementsByTagName(String)}, {@link Element#getElementsByTagNameNS(String, String)},
 * {@link Document#getElementsByTagName(String)} und {@link Document#getElementsByTagNameNS(String, String)} mit der entsprechenden Sematik beim Zusammenstellen
 * der {@link Element}s.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class ElementsAdapter implements NodeList {

	/**
	 * Diese Methode sammelt alle {@link ElementView}s.
	 * 
	 * @param children {@link ChildrenView}.
	 */
	final void collectElements(final ChildrenView children) {
		for(final ChildView child: children){
			final ElementView element = child.asElement();
			if(element != null){
				this.elements.add(element);
				this.collectElements(element.children());
			}
		}
	}

	/**
	 * Diese Methode sammelt alle {@link ElementView}s mit passender {@link #uri}.
	 * 
	 * @param children {@link ChildrenView}.
	 */
	final void collectElementsByUri(final ChildrenView children) {
		for(final ChildView child: children){
			final ElementView element = child.asElement();
			if(element != null){
				if(this.uri.equals(element.uri())){
					this.elements.add(element);
				}
				this.collectElements(element.children());
			}
		}
	}

	/**
	 * Diese Methode sammelt alle {@link ElementView}s mit passendem {@link #name}.
	 * 
	 * @param children {@link ChildrenView}.
	 */
	final void collectElementsByName(final ChildrenView children) {
		for(final ChildView child: children){
			final ElementView element = child.asElement();
			if(element != null){
				if(this.name.equals(element.name())){
					this.elements.add(element);
				}
				this.collectElements(element.children());
			}
		}
	}

	/**
	 * Diese Methode sammelt alle {@link ElementView}s mit passender {@link #uri} und passendem {@link #name}.
	 * 
	 * @param children {@link ChildrenView}.
	 */
	final void collectElementsByLabel(final ChildrenView children) {
		for(final ChildView child: children){
			final ElementView element = child.asElement();
			if(element != null){
				if(this.name.equals(element.name()) && this.uri.equals(element.uri())){
					this.elements.add(element);
				}
				this.collectElements(element.children());
			}
		}
	}

	/**
	 * Dieses Feld speichert die gesuchte {@link ElementView#uri()} oder {@code "*"}.
	 */
	protected final String uri;

	/**
	 * Dieses Feld speichert den gesuchten {@link ElementView#name()} oder {@code "*"}.
	 */
	protected final String name;

	/**
	 * Dieses Feld speichert die gesammelten {@link ElementView}s.
	 */
	protected final List<ElementView> elements;

	/**
	 * Dieser Konstruktor initialisiert die Parameter zur Zusammenstellung der {@link ElementView}s.
	 * 
	 * @param children {@link ChildrenView} der rekursiv analysierten Kindknoten.
	 * @param uri Uri oder {@code "*"}.
	 * @param name Name oder {@code "*"}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public ElementsAdapter(final ChildrenView children, final String uri, final String name) throws NullPointerException {
		if((children == null) || (uri == null) || (name == null)) throw new NullPointerException();
		this.uri = uri;
		this.name = name;
		this.elements = new ArrayList<ElementView>();
		if("*".equals(uri)){
			if("*".equals(name)){
				this.collectElements(children);
			}else{
				this.collectElementsByName(children);
			}
		}else{
			if("*".equals(name)){
				this.collectElementsByUri(children);
			}else{
				this.collectElementsByLabel(children);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node item(final int index) {
		final List<ElementView> elements = this.elements;
		if(index < elements.size()) return new ElementAdapter(this.elements.get(index));
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLength() {
		return this.elements.size();
	}

}