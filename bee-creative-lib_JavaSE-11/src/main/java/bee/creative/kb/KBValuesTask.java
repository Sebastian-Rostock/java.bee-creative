package bee.creative.kb;

import bee.creative.fem.FEMString;

/** Diese Schnittstelle definiert den Empfänger für {@link KBValues#forEach(KBValuesTask)}. */
public interface KBValuesTask {

	/** Diese Methode verarbeitet den gegebenen Textwert {@code valueStr} mit der gegebenen Textreferenz {@code valueRef}. */
	void run(int valueRef, FEMString valueStr);

}