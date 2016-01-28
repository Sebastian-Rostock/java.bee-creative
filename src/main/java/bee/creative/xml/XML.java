package bee.creative.xml;

/**
 * Diese Klasse implementiert Hilfsmethoden zur zur Erzeugung von Konfiguratoren für das Parsen, Formatieren, Transformieren, Modifizieren, Evaluieren und
 * Validieren von XML Dokumenten und Knoten.
 * 
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class XML {

	/**
	 * Diese Methode gibt einen neuen {@link XMLNode} zurück.
	 * 
	 * @return {@link XMLNode}.
	 */
	public static final XMLNode newNode() {
		return new XMLNode();
	}

	/**
	 * Diese Methode gibt einen neuen {@link XMLParser} zurück.
	 * 
	 * @return {@link XMLParser}.
	 */
	public static final XMLParser newParser() {
		return new XMLParser();
	}

	/**
	 * Diese Methode gibt einen neuen {@link XMLResult} zurück.
	 * 
	 * @return {@link XMLResult}.
	 */
	public static final XMLResult newResult() {
		return new XMLResult();
	}

	/**
	 * Diese Methode gibt einen neuen {@link XMLSource} zurück.
	 * 
	 * @return {@link XMLSource}.
	 */
	public static final XMLSource newSource() {
		return new XMLSource();
	}

	/**
	 * Diese Methode gibt einen neuen {@link XMLSchema} zurück.
	 * 
	 * @return {@link XMLSchema}.
	 */
	public static final XMLSchema newSchema() {
		return new XMLSchema();
	}

	/**
	 * Diese Methode gibt einen neuen {@link XMLEvaluator} zurück.
	 * 
	 * @return {@link XMLEvaluator}.
	 */
	public static final XMLEvaluator newEvaluator() {
		return new XMLEvaluator();
	}

	/**
	 * Diese Methode gibt einen neuen {@link XMLFormatter} zurück.
	 * 
	 * @return {@link XMLFormatter}.
	 */
	public static final XMLFormatter newFormatter() {
		return new XMLFormatter();
	}

	/**
	 * Diese Methode gibt einen neuen {@link XMLMarshaller} zurück.
	 * 
	 * @return {@link XMLMarshaller}.
	 */
	public static final XMLMarshaller newMarshaller() {
		return new XMLMarshaller();
	}

	/**
	 * Diese Methode gibt einen neuen {@link XMLUnmarshaller} zurück.
	 * 
	 * @return {@link XMLUnmarshaller}.
	 */
	public static final XMLUnmarshaller newUnmarshaller() {
		return new XMLUnmarshaller();
	}

}
