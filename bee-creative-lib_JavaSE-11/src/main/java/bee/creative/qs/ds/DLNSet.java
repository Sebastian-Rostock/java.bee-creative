package bee.creative.qs.ds;

import bee.creative.qs.QE;
import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;

/** Diese Schnittstelle definiert eine {@link QNSet Menge} von {@link QN Hyperknoten}, die über ein {@link #link() Datenfeld} einem {@link #source()
 * Subjektknoten} bzw. einem {@link #target() Objektknoten} zugeordnet ist.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DLNSet extends DSNSet, DO {

	/** Diese Methode liefert das Datenfeld, dessen {@link DL#node() Hyperknoten} in den {@link #edges() Hyperkanten} verwendet wird.
	 *
	 * @return Datenfeld. */
	DL link();

	@Override
	default DM parent() {
		return this.link().parent();
	}

	/** Diese Methode liefert die Mengensicht auf alle {@link DL#edges() Hyperkanten des Datenfeldes}, die als {@link QE#subject() Subjektknoten} bzw. als
	 * {@link QE#object() Objektknoten} den dieses Objekts verwenden.
	 *
	 * @return Hyperkanten mit Subjekt- bzw. Objektbindung. */
	QESet edges();

	/** Diese Methode liefert den als {@link QE#object() Objektnoten} der {@link #edges() Hyperkanten} verwendeten {@link QN Hyperknoten} oder {@code null}.
	 *
	 * @return Objektnoten oder {@code null}. */
	QN target();

	/** Diese Methode liefert den als {@link QE#subject() Subjektnoten} der {@link #edges() Hyperkanten} verwendeten {@link QN Hyperknoten} oder {@code null}.
	 *
	 * @return Subjektnoten oder {@code null}. */
	QN source();

}
