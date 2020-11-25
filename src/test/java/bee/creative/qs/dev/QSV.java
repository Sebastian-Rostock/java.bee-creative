package bee.creative.qs.dev;

import bee.creative.qs.QE;
import bee.creative.qs.QN;

// version node
public class QSV   {

	
	QSVM qsvm;
	
	  QN activeVersion;
	/** Dieses Feld speichert den {@link QE#context() Kontextknoten} der {@link QE Hyperkanten}, die gegenüber der vorherigen in der {@link #activeVersion
	 * aktuellen Version} eingefügt wurden. */
	QN insertContext;
	/** Dieses Feld speichert den {@link QE#context() Kontextknoten} der {@link QE Hyperkanten}, die gegenüber der vorherigen in der {@link #activeVersion
	 * aktuellen Version} entfernt wurden. */
	QN deleteContext;
	/** Dieses Feld speichert den {@link QE#context() Kontextknoten} der {@link QE Hyperkanten} in der {@link #activeVersion aktuellen Version}. Diese beinhaltet
	 * die Hyperkanten der vorherigen Version mit den darin {@link #insertContext eingefügten} und ohne den daraus {@link #deleteContext entfernten}. */
	QN mergedContext;

}
