package bee.creative.qs.ds;

import bee.creative.qs.QN;

/** Diese Schnittstelle definiert ein Domänenknoten (domain-node) als {@link #node() Hyperknoten} mit Bezug zu einem {@link #parent() Domänenmodell}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DN extends DO {

	/** Diese Methode liefert den dieses Objekt repräsentierenden Hyperknoten.
	 *
	 * @return Hyperknoten, über den der Zustand dieses Objets gespeichert ist. */
	QN node();

}
