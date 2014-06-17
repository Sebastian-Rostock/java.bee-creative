package bee.creative.xml;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import bee.creative.util.Builder;
import bee.creative.util.Builders;
import bee.creative.util.Builders.CachedBuilder;
import bee.creative.util.Builders.SynchronizedBuilder;
import bee.creative.util.Converter;
import bee.creative.util.Converters;
import bee.creative.util.Converters.CachedConverter;
import bee.creative.util.Objects;
import bee.creative.util.Pointer;
import bee.creative.util.Pointers;
import bee.creative.util.Pointers.SoftPointer;

/**
 * Diese Klasse implementiert Hilfsmethoden zur zur Erzeugung und Formatierung von {@link Document DOM-Dokumenten}, zur Verarbeitung von {@link XPath
 * XPath-Auswertungsumgebungen}, {@link Node DOM-Knoten}, {@link Templates XSL-Templates} und {@link Transformer XSL-Transformern}.
 * 
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class XML {

	/**
	 * Diese Klasse implementiert die Optionen eines Builders bzw. einer Factory mit Featuren.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class FeatureOptions {

		/**
		 * Dieses Feld speichert die Feature.
		 * 
		 * @see XPathFactory#setFeature(String, boolean)
		 * @see TransformerFactory#setFeature(String, boolean)
		 * @see DocumentBuilderFactory#setFeature(String, boolean)
		 */
		Map<String, Boolean> featureMap;

		/**
		 * Diese Methode gibt die gegebene Abbildung nur dann zurück, wenn diese nicht {@code null} ist. Anderenfalls wird eine neue Abbildung erzeugt und zurück
		 * gegeben.
		 * 
		 * @param <GValue> Typ der Werte in der Abbildung.
		 * @param map Abbildung oder {@code null}.
		 * @return Abbildung.
		 */
		final <GValue> Map<String, GValue> map(final Map<String, GValue> map) {
			return ((map == null) ? new HashMap<String, GValue>(0) : map);
		}

		/**
		 * Diese Methode gibt den Wert des Eintrags mit dem gegebenen Namen aus der gegebenen Abbildung oder {@code null} zurück.
		 * 
		 * @param <GValue> Typ der Werte in der Abbildung.
		 * @param map Abbildung oder {@code null}.
		 * @param name Name.
		 * @return Wert oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene Name {@code null} ist.
		 */
		final <GValue> GValue get(final Map<String, GValue> map, final String name) throws NullPointerException {
			return ((map == null) ? null : map.get(name));
		}

		/**
		 * Diese Methode setzt den Wert des Eintrags mit dem gegebenen Namen in der gegebenen Abbildung und gibt die Abbildung zurück. Wenn der Wert {@code null}
		 * ist, wird der Eintrag aus der Abbildung entfernt. Wenn die Abbildung {@code null} und der Wert nicht {@code null} sind, werden eine neue Abbildung
		 * erzeugt, der gegebene Wert unter dem gegebenen Namen in die erzeute Abbildung eingefügt und die erzeugte Abbildung zurück gegeben.
		 * 
		 * @param <GValue> Typ der Werte in der Abbildung.
		 * @param map Abbildung oder {@code null}.
		 * @param name Name.
		 * @param value Wert oder {@code null}.
		 * @return Abbildung.
		 * @throws NullPointerException Wenn der gegebene Name {@code null} ist.
		 */
		final <GValue> Map<String, GValue> set(Map<String, GValue> map, final String name, final GValue value) throws NullPointerException {
			if(name == null) throw new NullPointerException("name is null");
			if(value == null){
				if(map == null) return null;
				map.remove(name);
				return map;
			}else{
				map = this.map(map);
				map.put(name, value);
				return map;
			}
		}

		/**
		 * Diese Methode entfernt alle Einträge aus der gegebene Abbildung, überträgt die gegebenen Werte auf die Abbildung und gibt die gibt Abbildung zurück.
		 * 
		 * @param <GValue> Typ der Werte in der Abbildung.
		 * @param map Abbildung oder {@code null}.
		 * @param value Werte oder {@code null}.
		 * @return Abbildung.
		 * @throws NullPointerException Wenn ein Name der gegebenen Abbildung {@code null} ist.
		 */
		final <GValue> Map<String, GValue> set(Map<String, GValue> map, final Map<String, GValue> value) throws NullPointerException {
			final Iterable<Entry<String, GValue>> entries = ((value == null) ? Collections.<Entry<String, GValue>>emptySet() : value.entrySet());
			if(map != null){
				map.clear();
			}
			for(final Entry<String, GValue> entry: entries){
				map = this.set(map, entry.getKey(), entry.getValue());
			}
			return map;
		}

		/**
		 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Abbildungen zurück. Hierbei sind {@code null}-Werte und leere synonym.
		 * 
		 * @see Objects#equals(Object, Object)
		 * @param <GValue> Typ der Werte.
		 * @param map1 Abbildung 1.
		 * @param map2 Abbildung 2.
		 * @return {@link Object#equals(Object) Äquivalenz}.
		 */
		final <GValue> boolean equals(final Map<String, GValue> map1, final Map<String, GValue> map2) {
			return (map1 == map2) || ((map1 == null) && map2.isEmpty()) || ((map2 == null) && map1.isEmpty()) || Objects.equals(map1, map2);
		}

		/**
		 * Diese Methode setzt alle Optionen zurück und gibt {@code this} zurück.
		 * 
		 * @return {@code this}.
		 */
		public abstract FeatureOptions reset();

		/**
		 * Diese Methode gibt den Zustand des Features mit dem gegebenen Namen zurück.
		 * 
		 * @param name Feature-Name.
		 * @return Feature-Zustand.
		 * @throws NullPointerException Wenn der gegebene Feature-Name {@code null} ist.
		 */
		public boolean getFeature(final String name) throws NullPointerException {
			return Boolean.TRUE.equals(this.get(this.featureMap, name));
		}

		/**
		 * Diese Methode setzt den Zustand des Features mit dem gegebenen Namen und gibt {@code this} zurück.
		 * 
		 * @param name Feature-Name.
		 * @param value Feature-Zustand.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn der gegebene Feature-Name {@code null} ist.
		 */
		public FeatureOptions setFeature(final String name, final boolean value) throws NullPointerException {
			return this.setFeature(name, Boolean.valueOf(value));
		}

		/**
		 * Diese Methode setzt den Zustand des Features mit dem gegebenen Namen und gibt {@code this} zurück. Mit dem Feature-Zustand {@code null} wird das Feature
		 * entfernt.
		 * 
		 * @param name Feature-Name.
		 * @param value Feature-Zustand oder {@code null}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn der gegebene Feature-Name {@code null} ist.
		 */
		public FeatureOptions setFeature(final String name, final Boolean value) throws NullPointerException {
			this.featureMap = this.set(this.featureMap, name, value);
			return this;
		}

		/**
		 * Diese Methode gibt die Feature zurück.
		 * 
		 * @return Feature.
		 */
		public Map<String, Boolean> getFeatureMap() {
			return this.featureMap = this.map(this.featureMap);
		}

		/**
		 * Diese Methode setzt alle Feature und gibt {@code this} zurück. Wenn die gegebene Feature-Abbildung {@code null} ist, werden alle Feature entfernt.
		 * 
		 * @param valueMap Feature-Abbildung oder {@code null}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn ein Feature-Name der gegebenen Feature-Abbildung {@code null} ist.
		 */
		public FeatureOptions setFeatureMap(final Map<String, Boolean> valueMap) throws NullPointerException {
			this.featureMap = this.set(this.featureMap, valueMap);
			return this;
		}

		/**
		 * Diese Methode gibt den Zustand des Features {@link XMLConstants#FEATURE_SECURE_PROCESSING} zurück.
		 * 
		 * @see XMLConstants#FEATURE_SECURE_PROCESSING
		 * @return Feature-Zustand.
		 */
		public boolean getFeatureSecureProcessing() {
			return this.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING);
		}

		/**
		 * Diese Methode setzt den Zustand des Features {@link XMLConstants#FEATURE_SECURE_PROCESSING} und gibt {@code this} zurück.
		 * 
		 * @see XMLConstants#FEATURE_SECURE_PROCESSING
		 * @param value Feature-Zustand.
		 * @return {@code this}.
		 */
		public FeatureOptions setFeatureSecureProcessing(final boolean value) {
			return this.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.featureMap);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof FeatureOptions)) return false;
			final FeatureOptions data = (FeatureOptions)object;
			return this.equals(this.featureMap, data.featureMap);
		}

	}

	/**
	 * Diese Klasse implementiert die Optionen eines Builders bzw. einer Factory mit Attributen.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class AttributeOptions extends FeatureOptions {

		/**
		 * Dieses Feld speichert die Attribute.
		 */
		Map<String, String> attributeMap;

		/**
		 * Diese Methode gibt den Wert des Attributs mit dem gegebenen Namen zurück.
		 * 
		 * @param name Attribut-Name.
		 * @return Attribut-Wert.
		 * @throws NullPointerException Wenn der gegebene Attribut-Name {@code null} ist.
		 */
		public String getAttribute(final String name) throws NullPointerException {
			return this.get(this.attributeMap, name);
		}

		/**
		 * Diese Methode setzt den Wert des Attributs mit dem gegebenen Namen und gibt {@code this} zurück. Mit dem Attributs-Wert {@code null} wird das Feature
		 * entfernt.
		 * 
		 * @param name Attribut-Name.
		 * @param value Attribut-Wert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn der gegebene Attribut-Name {@code null} ist.
		 */
		public AttributeOptions setAttribute(final String name, final String value) throws NullPointerException {
			this.attributeMap = this.set(this.attributeMap, name, value);
			return this;
		}

		/**
		 * Diese Methode gibt die Attribute zurück.
		 * 
		 * @return Attribute.
		 */
		public Map<String, String> getAttributeMap() {
			return this.attributeMap = this.map(this.attributeMap);
		}

		/**
		 * Diese Methode setzt die Attribute und gibt {@code this} zurück. Wenn die gegebene Attribut-Abbildung {@code null} ist, werden alle Attribute entfernt.
		 * 
		 * @param value Attribut-Abbildung oder {@code null}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn ein Attribut-Name der gegebenen Attribut-Appildung {@code null} ist.
		 */
		public AttributeOptions setAttributeMap(final Map<String, String> value) throws NullPointerException {
			this.attributeMap = this.set(this.attributeMap, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return super.hashCode() ^ Objects.hash(this.attributeMap);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof AttributeOptions)) return false;
			final AttributeOptions data = (AttributeOptions)object;
			return super.equals(object) && this.equals(this.attributeMap, data.attributeMap);
		}

	}

	/**
	 * Diese Klasse implementiert eine gepufferte {@link XPath XPath-Auswertungsumgebung}, die die von einer gegebenen {@link XPath XPath-Auswertungsumgebung}
	 * erzeugten {@link XPathExpression XPath-Ausdrücke} in einer {@link Map Abbildung} von Schlüsseln auf Werte verwaltet. Die Schlüssel werden dabei über
	 * {@link Pointer} auf Zeichenketten und die Werte als {@link Pointer} auf die {@link XPathExpression XPath-Ausdrücke} des gegebenen {@link XPath
	 * XPath-Auswertungsumgebung} realisiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see Converters#cachedConverter(int, int, int, Converter)
	 */
	public static final class CachedXPath implements XPath, Converter<String, XPathExpression> {

		/**
		 * Dieses Feld speichert die {@link XPath XPath-Auswertungsumgebung}.
		 */
		final XPath xpath;

		/**
		 * Dieses Feld speichert den {@link CachedConverter Cached-Converter}.
		 */
		final CachedConverter<String, XPathExpression> converter;

		/**
		 * Dieser Konstruktor initialisiert die {@link XPath XPath-Auswertungsumgebung}.
		 * 
		 * @see Pointers#pointer(int, Object)
		 * @param limit Maximum für die Anzahl der Einträge in der {@link Map}.
		 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der Abbildung erzeugt werden.
		 * @param outputMode Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der Abbildung erzeugt werden.
		 * @param xpath {@link XPath XPath-Auswertungsumgebung}.
		 */
		public CachedXPath(final int limit, final int inputMode, final int outputMode, final XPath xpath) {
			this.xpath = xpath;
			this.converter = new CachedConverter<String, XPathExpression>(limit, inputMode, outputMode, this);
		}

		/**
		 * Diese Methode leert die Abbildung.
		 * 
		 * @see CachedConverter#clear()
		 */
		public void clear() {
			this.converter.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void reset() {
			this.converter.clear();
			this.xpath.reset();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathExpression convert(final String input) {
			try{
				return this.xpath.compile(input);
			}catch(final XPathExpressionException e){
				throw new RuntimeException(e);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathExpression compile(final String expression) throws XPathExpressionException {
			try{
				return this.converter.convert(expression);
			}catch(final RuntimeException e){
				final Throwable cause = e.getCause();
				if(cause instanceof XPathExpressionException) throw (XPathExpressionException)cause;
				throw e;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object evaluate(final String expression, final Object item, final QName returnType) throws XPathExpressionException {
			if(expression == null) throw new NullPointerException("expression is null");
			if(returnType == null) throw new NullPointerException("returnType is null");
			return this.compile(expression).evaluate(item, returnType);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String evaluate(final String expression, final Object item) throws XPathExpressionException {
			if(expression == null) throw new NullPointerException("expression is null");
			return this.compile(expression).evaluate(item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object evaluate(final String expression, final InputSource source, final QName returnType) throws XPathExpressionException {
			if(expression == null) throw new NullPointerException("expression is null");
			if(source == null) throw new NullPointerException("source is null");
			if(returnType == null) throw new NullPointerException("returnType is null");
			return this.compile(expression).evaluate(source, returnType);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String evaluate(final String expression, final InputSource source) throws XPathExpressionException {
			if(expression == null) throw new NullPointerException("expression is null");
			if(source == null) throw new NullPointerException("source is null");
			return this.compile(expression).evaluate(source);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathVariableResolver getXPathVariableResolver() {
			return this.xpath.getXPathVariableResolver();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setXPathVariableResolver(final XPathVariableResolver resolver) {
			this.converter.clear();
			this.xpath.setXPathVariableResolver(resolver);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathFunctionResolver getXPathFunctionResolver() {
			return this.xpath.getXPathFunctionResolver();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setXPathFunctionResolver(final XPathFunctionResolver resolver) {
			this.converter.clear();
			this.xpath.setXPathFunctionResolver(resolver);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NamespaceContext getNamespaceContext() {
			return this.xpath.getNamespaceContext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setNamespaceContext(final NamespaceContext nsContext) {
			this.converter.clear();
			this.xpath.setNamespaceContext(nsContext);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.xpath);
		}

	}

	/**
	 * Diese Klasse implementiert die Optionen einer {@link XPath XPath-Auswertungsumgebung} sowie einer {@link XPathFactory XPath-Factory}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class XPathOptions extends FeatureOptions {

		/**
		 * Dieses Feld speichert die {@link FormatOptions Standard-Optionen}.
		 */
		static final XPathOptions DEFAULT = new XPathOptions();

		/**
		 * Dieses Feld speichert den {@link XPathVariableResolver XPath-Variablen-Kontext}.
		 */
		XPathVariableResolver variableResolver;

		/**
		 * Dieses Feld speichert den {@link XPathFunctionResolver XPath-Funktionen-Kontext}.
		 */
		XPathFunctionResolver functionResolver;

		/**
		 * Dieses Feld speichert den {@link NamespaceContext Namensraum-Kontext}.
		 */
		NamespaceContext namespaceContext;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathOptions reset() {
			return XPathOptions.DEFAULT.applyTo(this);
		}

		/**
		 * Diese Methode überträgt die Optionen auf die gegebenen {@link XPathOptions XPath-Optionen} und gibt diese zurück.
		 * 
		 * @param target {@link XPathOptions XPath-Optionen}.
		 * @return {@link XPathOptions XPath-Optionen}.
		 * @throws NullPointerException Wenn die gegebenen {@link XPathOptions XPath-Optionen} {@code null} sind.
		 */
		public XPathOptions applyTo(final XPathOptions target) throws NullPointerException {
			if(target == null) throw new NullPointerException("target is null");
			return target.setFeatureMap(this.featureMap).setVariableResolver(this.variableResolver).setFunctionResolver(this.functionResolver)
				.setNamespaceContext(this.namespaceContext);
		}

		/**
		 * Diese Methode überträgt die Optionen auf die gegebene {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück.
		 * 
		 * @param target {@link XPath XPath-Auswertungsumgebung}.
		 * @return {@link XPath XPath-Auswertungsumgebung}.
		 * @throws NullPointerException Wenn die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
		 */
		public XPath applyTo(final XPath target) throws NullPointerException {
			if(target == null) throw new NullPointerException("target is null");
			if(this.namespaceContext != null){
				target.setNamespaceContext(this.namespaceContext);
			}
			if(this.variableResolver != null){
				target.setXPathVariableResolver(this.variableResolver);
			}
			if(this.functionResolver != null){
				target.setXPathFunctionResolver(this.functionResolver);
			}
			return target;
		}

		/**
		 * Diese Methode überträgt die Optionen auf die gegebene {@link XPathFactory XPath-Factory} und gibt diese zurück.
		 * 
		 * @param target {@link XPathFactory XPath-Factory}.
		 * @return {@link XPathFactory XPath-Factory}.
		 * @throws NullPointerException Wenn die gegebene {@link XPathFactory XPath-Factory} {@code null} ist.
		 * @throws XPathFactoryConfigurationException Wenn eines der Feature nicht unterstützt wird.
		 */
		public XPathFactory applyTo(final XPathFactory target) throws NullPointerException, XPathFactoryConfigurationException {
			if(target == null) throw new NullPointerException("target is null");
			if(this.variableResolver != null){
				target.setXPathVariableResolver(this.variableResolver);
			}
			if(this.functionResolver != null){
				target.setXPathFunctionResolver(this.functionResolver);
			}
			if(this.featureMap != null){
				for(final Entry<String, Boolean> entry: this.featureMap.entrySet()){
					target.setFeature(entry.getKey(), entry.getValue().booleanValue());
				}
			}
			return target;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see XPathFactory#getFeature(String)
		 */
		@Override
		public boolean getFeature(final String name) throws NullPointerException {
			return super.getFeature(name);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see XPathFactory#setFeature(String, boolean)
		 */
		@Override
		public XPathOptions setFeature(final String name, final Boolean value) throws NullPointerException {
			super.setFeature(name, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see XPathFactory#setFeature(String, boolean)
		 */
		@Override
		public XPathOptions setFeature(final String name, final boolean value) throws NullPointerException {
			super.setFeature(name, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see XPathFactory#getFeature(String)
		 */
		@Override
		public Map<String, Boolean> getFeatureMap() {
			return super.getFeatureMap();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see XPathFactory#setFeature(String, boolean)
		 */
		@Override
		public XPathOptions setFeatureMap(final Map<String, Boolean> valueMap) throws NullPointerException {
			super.setFeatureMap(valueMap);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathOptions setFeatureSecureProcessing(final boolean value) {
			super.setFeatureSecureProcessing(value);
			return this;
		}

		/**
		 * Diese Methode gibt den {@link XPathVariableResolver XPath-Variablen-Kontext} zurück.
		 * 
		 * @see XPath#getXPathVariableResolver()
		 * @return {@link XPathVariableResolver XPath-Variablen-Kontext}.
		 */
		public XPathVariableResolver getVariableResolver() {
			return this.variableResolver;
		}

		/**
		 * Diese Methode setzt den {@link XPathVariableResolver XPath-Variablen-Kontext} und gibt {@code this} zurück.
		 * 
		 * @see XPath#setXPathVariableResolver(XPathVariableResolver)
		 * @see XPathFactory#setXPathVariableResolver(XPathVariableResolver)
		 * @param value {@link XPathVariableResolver XPath-Variablen-Kontext} oder {@code null}.
		 * @return {@code this}.
		 */
		public XPathOptions setVariableResolver(final XPathVariableResolver value) {
			this.variableResolver = value;
			return this;
		}

		/**
		 * Diese Methode gibt den {@link XPathFunctionResolver XPath-Funktionen-Kontext} zurück.
		 * 
		 * @see XPath#getXPathFunctionResolver()
		 * @return {@link XPathFunctionResolver XPath-Funktionen-Kontext}.
		 */
		public XPathFunctionResolver getFunctionResolver() {
			return this.functionResolver;
		}

		/**
		 * Diese Methode settz den {@link XPathFunctionResolver XPath-Funktionen-Kontext} und gibt {@code this} zurück.
		 * 
		 * @see XPath#setXPathFunctionResolver(XPathFunctionResolver)
		 * @see XPathFactory#setXPathFunctionResolver(XPathFunctionResolver)
		 * @param value {@link XPathFunctionResolver XPath-Funktionen-Kontext} oder null.
		 * @return {@code this}.
		 */
		public XPathOptions setFunctionResolver(final XPathFunctionResolver value) {
			this.functionResolver = value;
			return this;
		}

		/**
		 * Diese Methode gibt den {@link NamespaceContext Namensraum-Kontext} zurück.
		 * 
		 * @see XPath#getNamespaceContext()
		 * @return {@link NamespaceContext Namensraum-Kontext}.
		 */
		public NamespaceContext getNamespaceContext() {
			return this.namespaceContext;
		}

		/**
		 * Diese Methode setzt den {@link NamespaceContext Namensraum-Kontext} und gibt {@code this} zurück.
		 * 
		 * @see XPath#setNamespaceContext(NamespaceContext)
		 * @param value {@link NamespaceContext Namensraum-Kontext} oder null.
		 * @return {@code this}.
		 */
		public XPathOptions setNamespaceContext(final NamespaceContext value) {
			this.namespaceContext = value;
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return super.hashCode() ^ Objects.hash(this.variableResolver) ^ Objects.hash(this.functionResolver) ^ Objects.hash(this.namespaceContext);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof XPathOptions)) return false;
			final XPathOptions data = (XPathOptions)object;
			return super.equals(object) && Objects.equals(this.variableResolver, data.variableResolver)
				&& Objects.equals(this.functionResolver, data.functionResolver) && Objects.equals(this.namespaceContext, data.namespaceContext);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCallFormat(false, true, this, new Object[]{"featureMap", this.featureMap, "variableResolver", this.variableResolver,
				"functionResolver", this.functionResolver, "namespaceContext", this.namespaceContext});
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur vereinfachten Befüllung eines {@link Document}s mit {@link Attr}-, {@link Text}-, {@link Comment} und
	 * {@link Element}-Knoten. Die Methoden zur Erzeugung von Knoten geben diesen oder einen neuen {@link NodeBuilder} zurück, sodass mehrere Befehle analog zu
	 * einem {@link StringBuilder} hintereinander geschrieben werden können.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NodeBuilder {

		/**
		 * Dieses Feld speichert den aktuelle Knoten.
		 */
		final Node node;

		/**
		 * Dieses Feld speichert den aufrufenden {@link NodeBuilder}.
		 */
		final NodeBuilder owner;

		/**
		 * Dieses Feld speichert die aktuelle Position.
		 */
		int index;

		/**
		 * Dieser Konstruktor initialisiert den aktuellen Knoten und den aufrufenden {@link NodeBuilder}.
		 * 
		 * @param node aktuellen Knoten.
		 * @param owner aufrufender {@link NodeBuilder}
		 */
		private NodeBuilder(final Node node, final NodeBuilder owner) {
			this.node = node;
			this.owner = owner;
		}

		/**
		 * Dieser Konstruktor initialisiert das {@link Document}, das über diesen {@link NodeBuilder} befüllt werden soll.
		 * 
		 * @param document {@link Document}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public NodeBuilder(final Document document) throws NullPointerException {
			if(document == null) throw new NullPointerException();
			this.node = document;
			this.owner = null;
		}

		/**
		 * Diese Methode gibt den aktuellen Knoten zurück, in dennen Kontext Kind- und Attributknoten erzeugt werden. Dieser kann nur ein {@link Element} oder ein
		 * {@link Document} sein.
		 * 
		 * @see Element
		 * @see Document
		 * @return aktueller Knoten.
		 */
		public Node node() {
			return this.node;
		}

		/**
		 * Diese Methode gibt das {@link Document} des {@link #node() aktuellen Knoten} zurück.
		 * 
		 * @see Node#getOwnerDocument()
		 * @return {@link Document} des {@link #node() aktuellen Knoten}.
		 */
		public Document document() {
			return this.node().getOwnerDocument();
		}

		/**
		 * Diese Methode setzt die {@link #index() aktuelle Position} auf {@code index() - 1} aus und gibt {@code this} das zurück.
		 * 
		 * @see #hasPrev()
		 * @return {@code this}.
		 * @throws IllegalStateException wenn es keine vorherige Position gibt.
		 */
		public NodeBuilder prev() throws IllegalStateException {
			if(!this.hasPrev()) throw new IllegalStateException();
			this.index--;
			return this;
		}

		/**
		 * Diese Methode setzt die {@link #index() aktuelle Position} auf {@code index() + 1} aus und gibt {@code this} das zurück.
		 * 
		 * @see #hasNext()
		 * @return {@code this}.
		 * @throws IllegalStateException wenn es keine nächste Position gibt.
		 */
		public NodeBuilder next() throws IllegalStateException {
			if(!this.hasNext()) throw new IllegalStateException();
			this.index++;
			return this;
		}

		/**
		 * Diese Methode setzt die {@link #index() aktuelle Position}, an welcher neue Kindelemente eingefügt werden. Negative Positionen zählen vom Ende der
		 * Kindknotenliste, d.h. {@code -1 => length()}, {@code -2 => (length() - 1)} usw.
		 * 
		 * @see #index()
		 * @see #length()
		 * @param index Positioh.
		 * @return {@code this}.
		 * @throws IllegalArgumentException Wenn {@code index > length()} oder {@code (-index - 1) > length()}.
		 */
		public NodeBuilder seek(final int index) throws IllegalArgumentException {
			final int length = this.length(), offset = index >= 0 ? index : length + index + 1;
			if((offset < 0) || (offset > length)) throw new IllegalArgumentException();
			this.index = offset;
			return this;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #index() aktuelle Position} größer als {@code 0} ist.
		 * 
		 * @see #index()
		 * @see #length()
		 * @return {@code true}, wenn {@code index() > 0}.
		 */
		public boolean hasPrev() {
			return this.index > 0;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #index() aktuelle Position} kleiner als {@link #length()} ist.
		 * 
		 * @see #index()
		 * @see #length()
		 * @return {@code true}, wenn {@code index() < length()}.
		 */
		public boolean hasNext() {
			return this.index < this.length();
		}

		/**
		 * Diese Methode gibt die aktuelle Position zurück, an welcher neue Kindelemente eingefügt werden. für eine gültige Position gilt
		 * {@code 0 <= index() <= length()}.
		 * 
		 * @see #seek(int)
		 * @see #length()
		 * @return aktuelle Position.
		 */
		public int index() {
			return this.index;
		}

		/**
		 * Diese Methode gibt die Anzahl der Kondknoten zurück.
		 * 
		 * @see Node#getChildNodes()
		 * @see NodeList#getLength()
		 * @return Anzahl der Kondknoten.
		 */
		public int length() {
			return this.node().getChildNodes().getLength();
		}

		/**
		 * Diese Methode gibt einen neuen {@link NodeBuilder} zu dem {@link Element} zurück, das sich in der Kondknotenliste an der {@link #index() aktuellen
		 * Position} befindet.
		 * 
		 * @see #index()
		 * @return {@link NodeBuilder} zum {@link Element} an der {@link #index() aktuellen Position}.
		 * @throws IllegalStateException Wenn die Kondknotenliste an der {@link #index() aktuellen Position} kein {@link Element} enthält.
		 */
		public NodeBuilder open() throws IllegalStateException {
			final Node node = NodeBuilder.this.node().getChildNodes().item(NodeBuilder.this.index);
			if(!(node instanceof Element)) throw new IllegalStateException();
			return new NodeBuilder(node, this);
		}

		/**
		 * Diese Methode schließt die Bearbeitung des {@link #node() aktuelle Knoten} ab und gibt den {@link NodeBuilder} zurück, von dem aus dieser erzeugt wurde.
		 * 
		 * @see #open()
		 * @see #element(String)
		 * @see #element(String, String)
		 * @return erzeugenden {@link NodeBuilder}.
		 * @throws IllegalStateException Wenn es keinen erzeugenden {@link NodeBuilder} gibt.
		 */
		public NodeBuilder close() throws IllegalStateException {
			if(this.owner == null) throw new IllegalStateException();
			return this.owner;
		}

		/**
		 * Diese Methode fügt den gegebenen {@link Node} an der {@link #index() aktuellen Position} ein, bewegt die {@link #index() aktuelle Position} hinter den
		 * eingefügten Knoten und gibt {@code this} zurück.
		 * 
		 * @see #index()
		 * @see Node#insertBefore(Node, Node)
		 * @param node {@link Node}.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public NodeBuilder insert(final Node node) throws DOMException {
			final Node root = this.node(), next = root.getChildNodes().item(this.index);
			root.insertBefore(node, next);
			this.index++;
			return this;
		}

		/**
		 * Diese Methode entfernt den Kindknoten an der {@link #index() aktuellen Position} und gibt {@code this} zurück.
		 * 
		 * @see #index()
		 * @see Node#removeChild(Node)
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 * @throws IllegalStateException Wenn die Kondknotenliste an der {@link #index() aktuellen Position} kein Knoten existiert.
		 */
		public NodeBuilder delete() throws DOMException, IllegalStateException {
			final Node root = this.node(), next = root.getChildNodes().item(this.index);
			if(next == null) throw new IllegalStateException();
			root.removeChild(next);
			return this;
		}

		/**
		 * Diese Methode fügt an der {@link #index() aktuellen Position} einen neuen {@link Text} mit dem gegebenen Wert ein, bewegt die {@link #index() aktuelle
		 * Position} hinter den eingefügten Knoten und gibt {@code this} zurück.
		 * 
		 * @see #insert(Node)
		 * @see Document#createTextNode(String)
		 * @param text Wert.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public NodeBuilder text(final String text) throws DOMException {
			return this.insert(this.document().createTextNode(text));
		}

		/**
		 * Diese Methode fügt an der {@link #index() aktuellen Position} einen neuen {@link Comment} mit dem gegebenen Wert ein, bewegt die {@link #index() aktuelle
		 * Position} hinter den eingefügten Knoten und gibt {@code this} zurück.
		 * 
		 * @see #insert(Node)
		 * @see Document#createComment(String)
		 * @param text Wert.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public NodeBuilder comment(final String text) throws DOMException {
			return this.insert(this.document().createComment(text));
		}

		/**
		 * Diese Methode fügt an der {@link #index() aktuellen Position} ein neues {@link Element} mit dem gegebenen Namen ein, bewegt die {@link #index() aktuelle
		 * Position} hinter den eingefügten Knoten und gibt einen neuen {@link NodeBuilder} zum eingefügten {@link Element} zurück.
		 * 
		 * @see Document#createElement(String)
		 * @param name Name.
		 * @return {@link NodeBuilder} zum erzeugten Knoten.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public NodeBuilder element(final String name) throws DOMException {
			return this.insert(this.document().createElement(name)).prev().open();
		}

		/**
		 * Diese Methode fügt an der {@link #index() aktuellen Position} ein neues {@link Element} mit den gegebenen Eigenschaften ein, bewegt die {@link #index()
		 * aktuelle Position} hinter den eingefügten Knoten und gibt einen neuen {@link NodeBuilder} zum eingefügten {@link Element} zurück.
		 * 
		 * @see Document#createElementNS(String, String)
		 * @param uri Uri.
		 * @param name Name.
		 * @return {@link NodeBuilder} zum erzeugten Knoten.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public NodeBuilder element(final String uri, final String name) throws DOMException {
			return this.insert(this.document().createElementNS(uri, name)).prev().open();
		}

		/**
		 * Diese Methode modifiziert das {@link Attr} mit den gegebenen Eigenschaften und gibt {@code this} zurück. Wenn der gegebene Wert {@code null} ist, wird
		 * das {@link Attr} entfernt. Andernfalls wird es erzeugt bzw. geändert.
		 * 
		 * @see Element#setAttribute(String, String)
		 * @see Element#removeAttribute(String)
		 * @param name Name.
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 * @throws IllegalStateException Wenn das {@link #node() aktuelle Knoten} kein {@link Element} ist.
		 */
		public NodeBuilder attribute(final String name, final String value) throws DOMException, IllegalStateException {
			if(this.owner == null) throw new IllegalStateException();
			if(value == null){
				((Element)this.node).removeAttribute(name);
			}else{
				((Element)this.node).setAttribute(name, value);
			}
			return this;
		}

		/**
		 * Diese Methode modifiziert das {@link Attr} mit den gegebenen Eigenschaften und gibt {@code this} zurück. Wenn der gegebene Wert {@code null} ist, wird
		 * das {@link Attr} entfernt. Andernfalls wird es erzeugt bzw. geändert.
		 * 
		 * @see Element#setAttributeNS(String, String, String)
		 * @see Element#removeAttributeNS(String, String)
		 * @param uri Uri.
		 * @param name Name.
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 * @throws IllegalStateException Wenn das {@link #node() aktuelle Knoten} kein {@link Element} ist.
		 */
		public NodeBuilder attribute(final String uri, final String name, final String value) throws DOMException, IllegalStateException {
			if(this.owner == null) throw new IllegalStateException();
			if(value == null){
				((Element)this.node).removeAttributeNS(uri, name);
			}else{
				((Element)this.node).setAttributeNS(uri, name, value);
			}
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert die Optionen eines {@link DocumentBuilder Document-Builder} sowie einer {@link DocumentBuilderFactory Document-Builder-Factory}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class ParseOptions extends AttributeOptions {

		/**
		 * Dieses Feld speichert die {@link ParseOptions Standard-Optionen}.
		 */
		static final ParseOptions DEFAULT = new ParseOptions();

		/**
		 * Dieses Feld speichert das {@link Schema}.
		 * 
		 * @see DocumentBuilderFactory#getSchema()
		 */
		Schema schema;

		/**
		 * Dieses Feld speichert den {@code Validating}-Zustand.
		 * 
		 * @see DocumentBuilderFactory#isValidating()
		 */
		boolean validating;

		/**
		 * Dieses Feld speichert den {@code Coalescing}-Zustand.
		 * 
		 * @see DocumentBuilderFactory#isCoalescing()
		 */
		boolean coalescing;

		/**
		 * Dieses Feld speichert den {@link ErrorHandler ErrorHandler}.
		 * 
		 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
		 */
		ErrorHandler errorHandler;

		/**
		 * Dieses Feld speichert den {@link EntityResolver EntityResolver}.
		 * 
		 * @see DocumentBuilder#setEntityResolver(EntityResolver)
		 */
		EntityResolver entityResolver;

		/**
		 * Dieses Feld speichert den {@code ExpandEntityReferences}-Zustand.
		 * 
		 * @see DocumentBuilderFactory#isExpandEntityReferences()
		 */
		boolean expandEntityReferences = true;

		/**
		 * Dieses Feld speichert den {@code IgnoringComments}-Zustand.
		 * 
		 * @see DocumentBuilderFactory#isIgnoringComments()
		 */
		boolean ignoringComments;

		/**
		 * Dieses Feld speichert den {@code IgnoringElementContentWhitespace}-Zustand.
		 * 
		 * @see DocumentBuilderFactory#isIgnoringElementContentWhitespace()
		 */
		boolean ignoringElementContentWhitespace;

		/**
		 * Dieses Feld speichert den {@code XIncludeAware}-Zustand.
		 * 
		 * @see DocumentBuilderFactory#isXIncludeAware()
		 */
		boolean xIncludeAware;

		/**
		 * Dieses Feld speichert den {@code NamespaceAware}-Zustand.
		 * 
		 * @see DocumentBuilderFactory#isNamespaceAware()
		 */
		boolean namespaceAware;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ParseOptions reset() {
			return ParseOptions.DEFAULT.applyTo(this);
		}

		/**
		 * Diese Methode überträgt die Optionen auf die gegebenen {@link ParseOptions Parse-Optionen} und gibt diese zurück.
		 * 
		 * @param target {@link ParseOptions Parse-Optionen}.
		 * @return {@link ParseOptions Parse-Optionen}.
		 * @throws NullPointerException Wenn die gegebenen {@link ParseOptions Parse-Optionen} {@code null} sind.
		 */
		public ParseOptions applyTo(final ParseOptions target) throws NullPointerException {
			if(target == null) throw new NullPointerException("target is null");
			return target.setFeatureMap(this.featureMap).setAttributeMap(this.attributeMap).setSchema(this.schema).setValidating(this.validating)
				.setCoalescing(this.coalescing).setErrorHandler(this.errorHandler).setEntityResolver(this.entityResolver)
				.setExpandEntityReferences(this.expandEntityReferences).setIgnoringComments(this.ignoringComments)
				.setIgnoringElementContentWhitespace(this.ignoringElementContentWhitespace).setXIncludeAware(this.xIncludeAware).setNamespaceAware(this.namespaceAware);
		}

		/**
		 * Diese Methode überträgt die Optionen auf den gegebenen {@link DocumentBuilder Document-Builder} und gibt diesen zurück.
		 * 
		 * @param target {@link DocumentBuilder Document-Builder}.
		 * @return {@link DocumentBuilder Document-Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link DocumentBuilder Document-Builder} {@code null} ist.
		 */
		public DocumentBuilder applyTo(final DocumentBuilder target) throws NullPointerException {
			if(target == null) throw new NullPointerException("target is null");
			target.setErrorHandler(this.errorHandler);
			target.setEntityResolver(this.entityResolver);
			return target;
		}

		/**
		 * Diese Methode überträgt die Optionen auf die gegebene {@link DocumentBuilderFactory Document-Builder-Factory} und gibt diese zurück.
		 * 
		 * @param target {@link DocumentBuilderFactory Document-Builder-Factory}.
		 * @return {@link DocumentBuilderFactory Document-Builder-Factory}.
		 * @throws NullPointerException Wenn die gegebene {@link DocumentBuilderFactory Document-Builder-Factory} {@code null} ist.
		 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
		 */
		public DocumentBuilderFactory applyTo(final DocumentBuilderFactory target) throws NullPointerException, ParserConfigurationException {
			if(target == null) throw new NullPointerException("target is null");
			target.setSchema(this.schema);
			target.setValidating(this.validating);
			target.setCoalescing(this.coalescing);
			target.setXIncludeAware(this.xIncludeAware);
			target.setNamespaceAware(this.namespaceAware);
			target.setIgnoringComments(this.ignoringComments);
			target.setIgnoringElementContentWhitespace(this.ignoringElementContentWhitespace);
			target.setExpandEntityReferences(this.expandEntityReferences);
			if(this.featureMap != null){
				for(final Entry<String, Boolean> entry: this.featureMap.entrySet()){
					target.setFeature(entry.getKey(), entry.getValue().booleanValue());
				}
			}
			if(this.attributeMap != null){
				for(final Entry<String, String> entry: this.attributeMap.entrySet()){
					target.setAttribute(entry.getKey(), entry.getValue());
				}
			}
			return target;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see DocumentBuilderFactory#getFeature(String)
		 */
		@Override
		public boolean getFeature(final String name) throws NullPointerException {
			return super.getFeature(name);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see DocumentBuilderFactory#setFeature(String, boolean)
		 */
		@Override
		public ParseOptions setFeature(final String name, final boolean value) throws NullPointerException {
			super.setFeature(name, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see DocumentBuilderFactory#setFeature(String, boolean)
		 */
		@Override
		public ParseOptions setFeature(final String name, final Boolean value) throws NullPointerException {
			super.setFeature(name, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see DocumentBuilderFactory#getFeature(String)
		 */
		@Override
		public Map<String, Boolean> getFeatureMap() {
			return super.getFeatureMap();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see DocumentBuilderFactory#setFeature(String, boolean)
		 */
		@Override
		public ParseOptions setFeatureMap(final Map<String, Boolean> value) throws NullPointerException {
			super.setFeatureMap(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ParseOptions setFeatureSecureProcessing(final boolean value) {
			super.setFeatureSecureProcessing(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see DocumentBuilderFactory#getAttribute(String)
		 */
		@Override
		public String getAttribute(final String name) throws NullPointerException {
			return super.getAttribute(name);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see DocumentBuilderFactory#setAttribute(String, Object)
		 */
		@Override
		public ParseOptions setAttribute(final String name, final String value) throws NullPointerException {
			super.setAttribute(name, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see DocumentBuilderFactory#getAttribute(String)
		 */
		@Override
		public Map<String, String> getAttributeMap() {
			return super.getAttributeMap();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see DocumentBuilderFactory#setAttribute(String, Object)
		 */
		@Override
		public ParseOptions setAttributeMap(final Map<String, String> value) throws NullPointerException {
			super.setAttributeMap(value);
			return this;
		}

		/**
		 * Diese Methode gibt das {@link Schema Schema} zurück.
		 * 
		 * @return {@link Schema Schema}.
		 */
		public Schema getSchema() {
			return this.schema;
		}

		/**
		 * Diese Methode setzt das {@link Schema Schema} und gibt {@code this}das zurück.
		 * 
		 * @param value {@link Schema Schema}.
		 * @return {@code this}.
		 */
		public ParseOptions setSchema(final Schema value) {
			this.schema = value;
			return this;
		}

		/**
		 * Diese Methode gibt den {@code Validating}-Zustand zurück.
		 * 
		 * @see DocumentBuilderFactory#isValidating()
		 * @return den {@code Validating}-Zustand.
		 */
		public boolean getValidating() {
			return this.validating;
		}

		/**
		 * Diese Methode setzt den {@code Validating}-Zustand und gibt {@code this} zurück.
		 * 
		 * @see DocumentBuilderFactory#setValidating(boolean)
		 * @param value den {@code Validating}-Zustand.
		 * @return {@code this}.
		 */
		public ParseOptions setValidating(final boolean value) {
			this.validating = value;
			return this;
		}

		/**
		 * Diese Methode gibt den {@code Coalescing}-Zustand zurück.
		 * 
		 * @see DocumentBuilderFactory#isCoalescing()
		 * @return {@code Coalescing}-Zustand.
		 */
		public boolean getCoalescing() {
			return this.coalescing;
		}

		/**
		 * Diese Methode setzt den {@code Coalescing}-Zustand und gibt {@code this} zurück.
		 * 
		 * @see DocumentBuilderFactory#setCoalescing(boolean)
		 * @param value {@code Coalescing}-Zustand.
		 * @return {@code this}.
		 */
		public ParseOptions setCoalescing(final boolean value) {
			this.coalescing = value;
			return this;
		}

		/**
		 * Diese Methode gibt den {@link ErrorHandler ErrorHandler} zurück.
		 * 
		 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
		 * @return {@link ErrorHandler ErrorHandler}.
		 */
		public ErrorHandler getErrorHandler() {
			return this.errorHandler;
		}

		/**
		 * Diese Methode setzt den {@link ErrorHandler ErrorHandler} und gibt {@code this} zurück.
		 * 
		 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
		 * @param value {@link ErrorHandler ErrorHandler}.
		 * @return {@code this}.
		 */
		public ParseOptions setErrorHandler(final ErrorHandler value) {
			this.errorHandler = value;
			return this;
		}

		/**
		 * Diese Methode gibt den {@link EntityResolver EntityResolver} zurück.
		 * 
		 * @see DocumentBuilder#setEntityResolver(EntityResolver)
		 * @return {@link EntityResolver EntityResolver}.
		 */
		public EntityResolver getEntityResolver() {
			return this.entityResolver;
		}

		/**
		 * Diese Methode setzt den {@link EntityResolver EntityResolver} und gibt {@code this} zurück.
		 * 
		 * @see DocumentBuilder#setEntityResolver(EntityResolver)
		 * @param value {@link EntityResolver EntityResolver}.
		 * @return {@code this}.
		 */
		public ParseOptions setEntityResolver(final EntityResolver value) {
			this.entityResolver = value;
			return this;
		}

		/**
		 * Diese Methode gibt den {@code ExpandEntityReferences}-Zustand zurück.
		 * 
		 * @see DocumentBuilderFactory#isExpandEntityReferences()
		 * @return {@code ExpandEntityReferences}-Zustand.
		 */
		public boolean getExpandEntityReferences() {
			return this.expandEntityReferences;
		}

		/**
		 * Diese Methode setzt den {@code ExpandEntityReferences}-Zustand und gibt {@code this} zurück.
		 * 
		 * @see DocumentBuilderFactory#setExpandEntityReferences(boolean)
		 * @param value {@code ExpandEntityReferences}-Zustand.
		 * @return {@code this}.
		 */
		public ParseOptions setExpandEntityReferences(final boolean value) {
			this.expandEntityReferences = value;
			return this;
		}

		/**
		 * Diese Methode gibt den {@code IgnoringComments}-Zustand zurück.
		 * 
		 * @see DocumentBuilderFactory#isIgnoringComments()
		 * @return {@code IgnoringComments}-Zustand.
		 */
		public boolean getIgnoringComments() {
			return this.ignoringComments;
		}

		/**
		 * Diese Methode setzt den {@code IgnoringComments}-Zustand und gibt {@code this} zurück.
		 * 
		 * @see DocumentBuilderFactory#setIgnoringComments(boolean)
		 * @param value {@code IgnoringComments}-Zustand.
		 * @return {@code this}.
		 */
		public ParseOptions setIgnoringComments(final boolean value) {
			this.ignoringComments = value;
			return this;
		}

		/**
		 * Diese Methode gibt den {@code IgnoringElementContentWhitespace}-Zustand zurück.
		 * 
		 * @see DocumentBuilderFactory#isIgnoringElementContentWhitespace()
		 * @return {@code IgnoringElementContentWhitespace}-Zustand.
		 */
		public boolean getIgnoringElementContentWhitespace() {
			return this.ignoringElementContentWhitespace;
		}

		/**
		 * Diese Methode setzt den {@code IgnoringElementContentWhitespace}-Zustand und gibt {@code this} zurück.
		 * 
		 * @see DocumentBuilderFactory#setIgnoringElementContentWhitespace(boolean)
		 * @param value {@code IgnoringElementContentWhitespace}-Zustand.
		 * @return {@code this}.
		 */
		public ParseOptions setIgnoringElementContentWhitespace(final boolean value) {
			this.ignoringElementContentWhitespace = value;
			return this;
		}

		/**
		 * Diese Methode gibt den {@code XIncludeAware}-Zustand zurück.
		 * 
		 * @see DocumentBuilderFactory#isXIncludeAware()
		 * @return {@code XIncludeAware}-Zustand.
		 */
		public boolean getXIncludeAware() {
			return this.xIncludeAware;
		}

		/**
		 * Diese Methode setzt den {@code XIncludeAware}-Zustand und gibt {@code this} zurück.
		 * 
		 * @see DocumentBuilderFactory#setXIncludeAware(boolean)
		 * @param value {@code XIncludeAware}-Zustand.
		 * @return {@code this}.
		 */
		public ParseOptions setXIncludeAware(final boolean value) {
			this.xIncludeAware = value;
			return this;
		}

		/**
		 * Diese Methode gibt den {@code NamespaceAware}-Zustand zurück.
		 * 
		 * @see DocumentBuilderFactory#isNamespaceAware()
		 * @return {@code NamespaceAware}-Zustand.
		 */
		public boolean getNamespaceAware() {
			return this.namespaceAware;
		}

		/**
		 * Diese Methode setztden {@code NamespaceAware}-Zustand und gibt {@code this} zurück.
		 * 
		 * @see DocumentBuilderFactory#setNamespaceAware(boolean)
		 * @param value {@code NamespaceAware}-Zustand.
		 * @return {@code this}.
		 */
		public ParseOptions setNamespaceAware(final boolean value) {
			this.namespaceAware = value;
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return super.hashCode()
				^ Objects.hash(this.schema)
				^ Objects.hash(this.errorHandler)
				^ Objects.hash(this.entityResolver)
				^ Boolean
					.valueOf(
						this.validating != this.coalescing != this.expandEntityReferences != this.ignoringComments != this.ignoringElementContentWhitespace != this.xIncludeAware != this.namespaceAware)
					.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ParseOptions)) return false;
			final ParseOptions data = (ParseOptions)object;
			return super.equals(object) && Objects.equals(this.schema, data.schema) && Objects.equals(this.errorHandler, data.errorHandler)
				&& Objects.equals(this.entityResolver, data.entityResolver) && (this.validating == data.validating) && (this.coalescing == data.coalescing)
				&& (this.expandEntityReferences == data.expandEntityReferences) && (this.ignoringComments == data.ignoringComments)
				&& (this.ignoringElementContentWhitespace == data.ignoringElementContentWhitespace) && (this.xIncludeAware == data.xIncludeAware)
				&& (this.namespaceAware == data.namespaceAware);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCallFormat(false, true, this, new Object[]{"featureMap", this.featureMap, "attributeMap", this.attributeMap, "schema",
				this.schema, "validating", this.validating, "coalescing", this.coalescing, "errorHandler", this.errorHandler, "entityResolver", this.entityResolver,
				"expandEntityReferences", this.expandEntityReferences, "ignoringComments", this.ignoringComments, "ignoringElementContentWhitespace",
				this.ignoringElementContentWhitespace, "xIncludeAware", this.xIncludeAware, "namespaceAware", this.namespaceAware});
		}

	}

	/**
	 * Diese Klasse implementiert die Optionen eines {@link Transformer XSL-Transformers} sowie einer {@link TransformerFactory XSL-Transformer-Factory}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class FormatOptions extends AttributeOptions {

		/**
		 * Dieses Feld speichert die {@link FormatOptions Standard-Optionen}.
		 */
		static final FormatOptions DEFAULT = new FormatOptions();

		/**
		 * Dieses Feld speichert die Parameter.
		 * 
		 * @see Transformer#setParameter(String, Object)
		 */
		Map<String, Object> parameterMap;

		/**
		 * Dieses Feld speichert das Ausgabeformat.
		 * 
		 * @see Transformer#getOutputProperties()
		 */
		Map<String, String> outputPropertyMap;

		/**
		 * Dieses Feld speichert den {@link URIResolver URIResolver}.
		 * 
		 * @see Transformer#getURIResolver()
		 */
		URIResolver uriResolver;

		/**
		 * Dieses Feld speichert den {@link ErrorListener ErrorListener}.
		 * 
		 * @see Transformer#getErrorListener()
		 */
		ErrorListener errorListener;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FormatOptions reset() {
			return FormatOptions.DEFAULT.applyTo(this);
		}

		/**
		 * Diese Methode überträgt die Optionen auf die gegebenen {@link FormatOptions Format-Optionen} und gibt diese zurück.
		 * 
		 * @param target {@link FormatOptions Format-Optionen}.
		 * @return {@link FormatOptions Format-Optionen}.
		 * @throws NullPointerException Wenn die gegebenen {@link FormatOptions Format-Optionen} {@code null} sind.
		 */
		public FormatOptions applyTo(final FormatOptions target) throws NullPointerException {
			if(target == null) throw new NullPointerException("target is null");
			return target.setFeatureMap(this.featureMap).setAttributeMap(this.attributeMap).setParameterMap(this.parameterMap)
				.setOutputPropertyMap(this.outputPropertyMap).setUriResolver(this.uriResolver).setErrorListener(this.errorListener);
		}

		/**
		 * Diese Methode überträgt die Optionen auf den gegebenen {@link Transformer XSL-Transformer} und gibt diesen zurück.
		 * 
		 * @param target {@link Transformer XSL-Transformer}.
		 * @return {@link Transformer XSL-Transformer}.
		 * @throws NullPointerException Wenn der gegebene {@link Transformer XSL-Transformer} {@code null} ist.
		 */
		public Transformer applyTo(final Transformer target) throws NullPointerException {
			if(target == null) throw new NullPointerException("target is null");
			if(this.parameterMap != null){
				for(final Entry<String, Object> entry: this.parameterMap.entrySet()){
					target.setParameter(entry.getKey(), entry.getValue());
				}
			}
			if(this.outputPropertyMap != null){
				for(final Entry<String, String> entry: this.outputPropertyMap.entrySet()){
					target.setOutputProperty(entry.getKey(), entry.getValue());
				}
			}
			return target;
		}

		/**
		 * Diese Methode überträgt die Optionen auf die gegebene {@link TransformerFactory XSL-Transformer-Factory} und gibt diese zurück.
		 * 
		 * @param target {@link TransformerFactory XSL-XSL-Transformer-Factory}.
		 * @return {@link TransformerFactory XSL-Transformer-Factory}.
		 * @throws NullPointerException Wenn die gegebene {@link TransformerFactory XSL-Transformer-Factory} {@code null} ist.
		 * @throws TransformerConfigurationException Wenn eines der Feature nicht unterstützt wird.
		 */
		public TransformerFactory applyTo(final TransformerFactory target) throws NullPointerException, TransformerConfigurationException {
			if(target == null) throw new NullPointerException("target is null");
			target.setURIResolver(this.uriResolver);
			if(this.errorListener != null){
				target.setErrorListener(this.errorListener);
			}
			if(this.featureMap != null){
				for(final Entry<String, Boolean> entry: this.featureMap.entrySet()){
					target.setFeature(entry.getKey(), entry.getValue().booleanValue());
				}
			}
			if(this.attributeMap != null){
				for(final Entry<String, String> entry: this.attributeMap.entrySet()){
					target.setAttribute(entry.getKey(), entry.getValue());
				}
			}
			return target;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FormatOptions setFeature(final String name, final Boolean value) throws NullPointerException {
			super.setFeature(name, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FormatOptions setFeature(final String name, final boolean value) throws NullPointerException {
			super.setFeature(name, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FormatOptions setFeatureMap(final Map<String, Boolean> value) throws NullPointerException {
			super.setFeatureMap(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FormatOptions setFeatureSecureProcessing(final boolean value) {
			super.setFeatureSecureProcessing(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FormatOptions setAttribute(final String name, final String value) throws NullPointerException {
			super.setAttribute(name, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FormatOptions setAttributeMap(final Map<String, String> value) throws NullPointerException {
			super.setAttributeMap(value);
			return this;
		}

		/**
		 * Diese Methode gibt den Wert des Parameters mit dem gegebenen Namen zurück.
		 * 
		 * @param name Parameter-Name.
		 * @return Parameter-Wert.
		 * @throws NullPointerException Wenn der gegebene Parameter-Name {@code null} ist.
		 */
		public Object getParameter(final String name) {
			return this.get(this.parameterMap, name);
		}

		/**
		 * Diese Methode setzt den Wert des Parameters mit dem gegebenen Namen und gibt {@code this} zurück. Mit dem Parameter-Wert {@code null} wird der Parameter
		 * entfernt.
		 * 
		 * @param name Parameter-Name.
		 * @param value Parameter-Wert oder {@code null}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn der gegebene Parameter-Name {@code null} ist.
		 */
		public FormatOptions setParameter(final String name, final Object value) {
			this.parameterMap = this.set(this.parameterMap, name, value);
			return this;
		}

		/**
		 * Diese Methode gibt die Parameter zurück.
		 * 
		 * @return Parameter.
		 */
		public Map<String, Object> getParameterMap() {
			return this.parameterMap = this.map(this.parameterMap);
		}

		/**
		 * Diese Methode setzt alle Parameter und gibt {@code this} zurück. Wenn die gegebene Parameter-Abbildung {@code null} ist, werden alle Parameter entfernt.
		 * 
		 * @param valueMap Parameter-Abbildung oder {@code null}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn ein Parameter-Name der gegebenen Parameter-Abbildung {@code null} ist.
		 */
		public FormatOptions setParameterMap(final Map<String, Object> valueMap) {
			this.parameterMap = this.set(this.parameterMap, valueMap);
			return this;
		}

		/**
		 * Diese Methode gibt den Wert der Ausgabeoption mit dem gegebenen Namen zurück.
		 * 
		 * @see Transformer#getOutputProperty(String)
		 * @param name Ausgabeoption-Name.
		 * @return Ausgabeoption-Wert.
		 * @throws NullPointerException Wenn der gegebene Ausgabeoption-Name {@code null} ist.
		 */
		public String getOutputProperty(final String name) {
			return this.get(this.outputPropertyMap, name);
		}

		/**
		 * Diese Methode setzt den Wert der Ausgabeoption mit dem gegebenen Namen und gibt {@code this} zurück.
		 * 
		 * @see Transformer#setOutputProperty(String, String)
		 * @param name Ausgabeoption-Name.
		 * @param value Ausgabeoption-Wert.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn der gegebene Ausgabeoption-Name {@code null} ist.
		 */
		public FormatOptions setOutputProperty(final String name, final boolean value) {
			return this.setOutputProperty(name, value ? "yes" : "no");
		}

		/**
		 * Diese Methode setzt den Wert der Ausgabeoption mit dem gegebenen Namen und gibt {@code this} zurück. Mit dem Ausgabeoption-Wert {@code null} wird die
		 * Ausgabeoption entfernt.
		 * 
		 * @see Transformer#setOutputProperty(String, String)
		 * @param name Ausgabeoption-Name.
		 * @param value Ausgabeoption-Wert oder {@code null}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn der gegebene Ausgabeoption-Name {@code null} ist.
		 */
		public FormatOptions setOutputProperty(final String name, final String value) {
			this.outputPropertyMap = this.set(this.outputPropertyMap, name, value);
			return this;
		}

		/**
		 * Diese Methode gibt die Ausgabeoptionen zurück.
		 * 
		 * @see Transformer#getOutputProperty(String)
		 * @return Ausgabeoptionen.
		 */
		public Map<String, String> getOutputPropertyMap() {
			return this.outputPropertyMap = this.map(this.outputPropertyMap);
		}

		/**
		 * Diese Methode setzt alle Ausgabeoptionen und gibt {@code this} zurück. Wenn die gegebene Ausgabeoption-Abbildung {@code null} ist, werden alle
		 * Ausgabeoptionen entfernt.
		 * 
		 * @see Transformer#setOutputProperty(String, String)
		 * @param valueMap Ausgabeoption-Abbildung oder {@code null}.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn ein Ausgabeoption-Name der gegebenen Ausgabeoption-Abbildung {@code null} ist.
		 */
		public FormatOptions setOutputPropertyMap(final Map<String, String> valueMap) {
			this.outputPropertyMap = this.set(this.outputPropertyMap, valueMap);
			return this;
		}

		/**
		 * Diese Methode setzt den Wert der Ausgabeoption {@link OutputKeys#INDENT} und gibt {@code this} zurück.
		 * 
		 * @see OutputKeys#INDENT
		 * @see Transformer#setOutputProperty(String, String)
		 * @param value Ausgabeoption-Wert oder {@code null}.
		 * @return {@code this}.
		 */
		public FormatOptions setOutputIndent(final String value) {
			return this.setOutputProperty(OutputKeys.INDENT, value);
		}

		/**
		 * Diese Methode setzt den Wert der Ausgabeoption {@link OutputKeys#METHOD} und gibt {@code this} zurück. Mit dem Ausgabeoption-Wert {@code null} wird die
		 * Ausgabeoption entfernt.
		 * 
		 * @see OutputKeys#METHOD
		 * @see Transformer#setOutputProperty(String, String)
		 * @param value Ausgabeoption-Wert oder {@code null}.
		 * @return {@code this}.
		 */
		public FormatOptions setOutputMethod(final String value) {
			return this.setOutputProperty(OutputKeys.METHOD, value);
		}

		/**
		 * Diese Methode setzt den Wert der Ausgabeoption {@link OutputKeys#ENCODING} und gibt {@code this} zurück. Mit dem Ausgabeoption-Wert {@code null} wird die
		 * Ausgabeoption entfernt.
		 * 
		 * @see OutputKeys#ENCODING
		 * @see Transformer#setOutputProperty(String, String)
		 * @param value Ausgabeoption-Wert oder {@code null}.
		 * @return {@code this}.
		 */
		public FormatOptions setOutputEncoding(final String value) {
			return this.setOutputProperty(OutputKeys.ENCODING, value);
		}

		/**
		 * Diese Methode gibt den {@link URIResolver URIResolver} zurück.
		 * 
		 * @see Transformer#getURIResolver()
		 * @see TransformerFactory#getURIResolver()
		 * @return {@link URIResolver URIResolver}.
		 */
		public URIResolver getUriResolver() {
			return this.uriResolver;
		}

		/**
		 * Diese Methode setzt den {@link URIResolver URIResolver} und gibt {@code this} zurück.
		 * 
		 * @see Transformer#setURIResolver(URIResolver)
		 * @see TransformerFactory#setURIResolver(URIResolver)
		 * @param value {@link URIResolver URIResolver}.
		 * @return {@code this}.
		 */
		public FormatOptions setUriResolver(final URIResolver value) {
			this.uriResolver = value;
			return this;
		}

		/**
		 * Diese Methode gibt den {@link ErrorListener ErrorListener} zurück.
		 * 
		 * @see Transformer#getErrorListener()
		 * @see TransformerFactory#getErrorListener()
		 * @return {@link ErrorListener ErrorListener}.
		 */
		public ErrorListener getErrorListener() {
			return this.errorListener;
		}

		/**
		 * Diese Methode setut den {@link ErrorListener ErrorListener} und gibt {@code this} zurück.
		 * 
		 * @see Transformer#setErrorListener(ErrorListener)
		 * @see TransformerFactory#setErrorListener(ErrorListener)
		 * @param value {@link ErrorListener ErrorListener}.
		 * @return {@code this}.
		 */
		public FormatOptions setErrorListener(final ErrorListener value) {
			this.errorListener = value;
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return super.hashCode() ^ Objects.hash(this.parameterMap) ^ Objects.hash(this.outputPropertyMap) ^ Objects.hash(this.uriResolver)
				^ Objects.hash(this.errorListener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof FormatOptions)) return false;
			final FormatOptions data = (FormatOptions)object;
			return super.equals(object) && this.equals(this.parameterMap, data.parameterMap) && this.equals(this.outputPropertyMap, data.outputPropertyMap)
				&& Objects.equals(this.uriResolver, data.uriResolver) && Objects.equals(this.errorListener, data.errorListener);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCallFormat(false, true, this, new Object[]{"featureMap", this.featureMap, "attributeMap", this.attributeMap, "parameterMap",
				this.parameterMap, "outputPropertyMap", this.outputPropertyMap, "uriResolver", this.uriResolver, "errorListener", this.errorListener});
		}

	}

	/**
	 * Dieses Feld speichert den {@link CachedBuilder Cached-Builder} zur Erzeugung bzw. Bereitstellung einer {@link CachedXPath Cached-XPath-Auswertungsumgebung}
	 * .
	 */
	static final Builder<XPath> CACHED_XPATH_BUILDER = Builders.synchronizedBuilder(Builders.cachedBuilder(new Builder<XPath>() {

		@Override
		public XPath build() {
			try{
				return XML.cachedXPath();
			}catch(final XPathFactoryConfigurationException e){
				throw new IllegalStateException(e);
			}
		}

		@Override
		public String toString() {
			return Objects.toStringCall("XpathBuilder");
		}

	}));

	/**
	 * Diese Methode gibt den {@link Node#getTextContent() Knotenwert} des gegebenen {@link Node DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten}
	 * {@code null} ist, wird der gegebenen Standardwert zurück gegeben.
	 * 
	 * @see Node#getTextContent()
	 * @param node {@link Node DOM-Knoten}.
	 * @param defaultValue Standardwert.
	 * @return {@link Node#getTextContent() Knotenwert}.
	 */
	public static String value(final Node node, final String defaultValue) {
		return ((node == null) ? defaultValue : node.getTextContent());
	}

	/**
	 * Diese Methode gibt den {@link Node#getTextContent() Knotenwert} des über seinen Namen gegebenen {@link Attr DOM-Attributs} des gegebenen {@link Node
	 * DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist oder kein {@link Attr DOM-Attribut} mit dem gegebenen Namen gefunden werden
	 * kann, wird der gegebenen Standardwert zurück gegeben.
	 * 
	 * @see XML#value(Node, String)
	 * @see Element#getAttributeNode(String)
	 * @param node {@link Node DOM-Knoten}.
	 * @param name Attribut-Name.
	 * @param defaultValue Standardwert.
	 * @return {@link Node#getTextContent() Knotenwert} oder Standardwert.
	 * @throws NullPointerException Wenn der gegebene Attribut-Name {@code null} ist.
	 */
	public static String value(final Node node, final String name, final String defaultValue) throws NullPointerException {
		if(name == null) throw new NullPointerException("name is null");
		if((node == null) || (node.getNodeType() != Node.ELEMENT_NODE)) return defaultValue;
		return XML.value(((Element)node).getAttributeNode(name), defaultValue);
	}

	/**
	 * Diese Methode gibt den {@link Node#getTextContent() Knotenwert} des über seinen Namen und Namensraum gegebenen {@link Attr DOM-Attributs} des gegebenen
	 * {@link Node DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist oder kein {@link Attr DOM-Attribut} mit dem gegebenen Namen und
	 * Namensraum gefunden werden kann, wird der gegebenen Standardwert zurück gegeben.
	 * 
	 * @see XML#value(Node, String)
	 * @see Element#getAttributeNodeNS(String, String)
	 * @param node {@link Node DOM-Knoten}.
	 * @param localName Attribut-Name.
	 * @param namespaceURI Namensraum.
	 * @param defaultValue Standardwert.
	 * @return {@link Node#getTextContent() Knotenwert} oder Standardwert.
	 * @throws NullPointerException Wenn der gegebene Attribut-Name {@code null} ist.
	 */
	public static String value(final Node node, final String namespaceURI, final String localName, final String defaultValue) throws NullPointerException {
		if(namespaceURI == null) throw new NullPointerException("namespaceURI is null");
		if(localName == null) throw new NullPointerException("localName is null");
		if((node == null) || (node.getNodeType() != Node.ELEMENT_NODE)) return defaultValue;
		return XML.value(((Element)node).getAttributeNodeNS(namespaceURI, localName), defaultValue);
	}

	/**
	 * Diese Methode erzeugt neue {@link XPathOptions} und gibt diese zurück.
	 * 
	 * @see XPathOptions#XPathOptions()
	 * @return {@link XPathOptions}.
	 */
	public static XPathOptions xpathOptions() {
		return new XPathOptions();
	}

	/**
	 * Diese Methode erzeugt neue {@link ParseOptions} und gibt diese zurück.
	 * 
	 * @see ParseOptions#ParseOptions()
	 * @return {@link ParseOptions}.
	 */
	public static ParseOptions parseOptions() {
		return new ParseOptions();
	}

	/**
	 * Diese Methode erzeugt neue {@link FormatOptions} und gibt diese zurück.
	 * 
	 * @see FormatOptions#FormatOptions()
	 * @return {@link FormatOptions}.
	 */
	public static FormatOptions formatOptions() {
		return new FormatOptions();
	}

	/**
	 * Diese Methode erzeugt einen {@link NodeBuilder} für das gegebene {@link Document} und gibt ihn zurück.
	 * 
	 * @param document {@link Document}.
	 * @return {@link NodeBuilder}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static NodeBuilder builder(final Document document) throws NullPointerException {
		return new NodeBuilder(document);
	}

	/**
	 * Diese Methode erzeugt eine {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück.
	 * 
	 * @see XPathOptions#DEFAULT
	 * @return {@link XPath XPath-Auswertungsumgebung}.
	 * @throws XPathFactoryConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static XPath createXPath() throws XPathFactoryConfigurationException {
		return XML.createXPath(XPathOptions.DEFAULT);
	}

	/**
	 * Diese Methode erzeugt eine {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück.
	 * 
	 * @param options {@link XPathOptions XPath-Kontext-Optionen}.
	 * @return {@link XPath XPath-Auswertungsumgebung}.
	 * @throws NullPointerException Wenn die gegebenen {@link XPathOptions XPath-Kontext-Optionen} {@code null} sind.
	 * @throws XPathFactoryConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static XPath createXPath(final XPathOptions options) throws NullPointerException, XPathFactoryConfigurationException {
		if(options == null) throw new NullPointerException("options is null");
		return options.applyTo(options.applyTo(XPathFactory.newInstance()).newXPath());
	}

	/**
	 * Diese Methode erzeugt eine gepufferte {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück.
	 * 
	 * @see XPathOptions#DEFAULT
	 * @see XML#cachedXPath(XPathOptions)
	 * @return {@link CachedXPath Cached-XPath-Auswertungsumgebung}.
	 * @throws XPathFactoryConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static XPath cachedXPath() throws XPathFactoryConfigurationException {
		return XML.cachedXPath(XPathOptions.DEFAULT);
	}

	/**
	 * Diese Methode erzeugt eine gepufferte {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück. Die erzeugte {@link XPath XPath-Auswertungsumgebung}
	 * verwaltet die von der gegebenen {@link XPath XPath-Auswertungsumgebung} erzeugten Ausgaben in einer {@link Map} von Schlüsseln auf Werte. Die Schlüssel
	 * werden dabei über {@link SoftPointer} auf Zeichenketten und die Werte als {@link SoftPointer} auf die {@link XPathExpression XPath-Ausdrücke} der gegebenen
	 * {@link XPath XPath-Auswertungsumgebung} realisiert. Die Anzahl der Einträge in der {@link Map} sind nicht beschränkt.
	 * 
	 * @see XML#cachedXPath(int, int, int, XPath)
	 * @param xpath {@link XPath XPath-Auswertungsumgebung}.
	 * @return {@link CachedXPath Cached-XPath-Auswertungsumgebung}.
	 * @throws NullPointerException wenn die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 */
	public static XPath cachedXPath(final XPath xpath) throws NullPointerException {
		if(xpath == null) throw new NullPointerException("xpath is null");
		return XML.cachedXPath(-1, Pointers.SOFT, Pointers.SOFT, xpath);
	}

	/**
	 * Diese Methode erzeugt eine gepufferte {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück.
	 * 
	 * @see XML#createXPath(XPathOptions)
	 * @see XML#cachedXPath(int, int, int, XPath)
	 * @param options {@link XPathOptions XPath-Kontext-Optionen}.
	 * @return {@link CachedXPath Cached-XPath-Auswertungsumgebung}.
	 * @throws NullPointerException Wenn die gegebenen {@link XPathOptions XPath-Kontext-Optionen} {@code null} sind.
	 * @throws XPathFactoryConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static XPath cachedXPath(final XPathOptions options) throws NullPointerException, XPathFactoryConfigurationException {
		if(options == null) throw new NullPointerException("options is null");
		return XML.cachedXPath(XML.createXPath(options));
	}

	/**
	 * Diese Methode erzeugt eine gepufferte {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück. Die erzeugte {@link XPath XPath-Auswertungsumgebung}
	 * verwaltet die von der gegebenen {@link XPath XPath-Auswertungsumgebung} erzeugten {@link XPathExpression XPath-Ausdrücke} in einer {@link Map} von
	 * Schlüsseln auf Werte. Die Schlüssel werden dabei über {@link Pointer} auf Zeichenketten und die Werte als {@link Pointer} auf die {@link XPathExpression
	 * XPath-Ausdrücke} des gegebenen {@link XPath XPath-Auswertungsumgebung} realisiert.
	 * 
	 * @param limit Maximum für die Anzahl der Einträge in der {@link Map}.
	 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der {@link Map} erzeugt werden.
	 * @param outputMode Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der {@link Map} erzeugt werden.
	 * @param xpath {@link XPath XPath-Auswertungsumgebung}.
	 * @return {@link CachedXPath Cached-XPath-Auswertungsumgebung}.
	 * @throws NullPointerException wenn die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 */
	public static XPath cachedXPath(final int limit, final int inputMode, final int outputMode, final XPath xpath) throws NullPointerException {
		if(xpath == null) throw new NullPointerException("xpath is null");
		return new CachedXPath(limit, inputMode, outputMode, xpath);
	}

	/**
	 * Diese Methode erzeugt eine gepufferte {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück.
	 * 
	 * @see XML#createXPath(XPathOptions)
	 * @see XML#cachedXPath(int, int, int, XPath)
	 * @param limit Maximum für die Anzahl der Einträge in der {@link Map}.
	 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der {@link Map} erzeugt werden.
	 * @param outputMode Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der {@link Map} erzeugt werden.
	 * @param options {@link XPathOptions XPath-Kontext-Optionen}.
	 * @return {@link CachedXPath Cached-XPath-Auswertungsumgebung}.
	 * @throws NullPointerException Wenn die gegebenen {@link XPathOptions XPath-Kontext-Optionen} {@code null} sind.
	 * @throws XPathFactoryConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static XPath cachedXPath(final int limit, final int inputMode, final int outputMode, final XPathOptions options) throws NullPointerException,
		XPathFactoryConfigurationException {
		if(options == null) throw new NullPointerException("options is null");
		return XML.cachedXPath(limit, inputMode, outputMode, XML.createXPath(options));
	}

	/**
	 * Diese Methode gibt den {@link SynchronizedBuilder Synchronized-}-{@link CachedBuilder Cached}-{@link Builder Builder} zur Erzeugung bzw. Bereitstellung
	 * einer {@link CachedXPath Cached-XPath-Auswertungsumgebung} zurück.
	 * 
	 * @return {@link CachedXPath Cached-XPath-Auswertungsumgebung}-{@link Builder}.
	 */
	public static Builder<XPath> cachedXPathBuilder() {
		return XML.CACHED_XPATH_BUILDER;
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link Node DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}.
	 * 
	 * @see XML#cachedXPathBuilder()
	 * @see XML#evaluateNode(XPath, String, Node)
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Node DOM-Knoten}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Node evaluateNode(final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if(script == null) throw new NullPointerException("script is null");
		return XML.evaluateNode(XML.CACHED_XPATH_BUILDER.build(), script, input);
	}

	/**
	 * Diese Methode wertet den gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das Ergebnis als
	 * {@link Node DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren {@link Document
	 * DOM-Dokument}.
	 * 
	 * @see XPathConstants#NODE
	 * @see XPathExpression#evaluate(Object, QName)
	 * @param source {@link XPathExpression XPath-Ausdruck}.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Node DOM-Knoten}.
	 * @throws NullPointerException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Node evaluateNode(final XPathExpression source, final Node input) throws NullPointerException, XPathExpressionException {
		if(source == null) throw new NullPointerException("source is null");
		synchronized(source){
			return (Node)source.evaluate(input, XPathConstants.NODE);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link Node DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}. Der {@link XPathExpression XPath-Ausdruck} wird über die gegebene {@link XPath XPath-Auswertungsumgebung} erzeugt.
	 * 
	 * @see XPath#compile(String)
	 * @see XML#evaluateNode(XPathExpression, Node)
	 * @param source {@link XPath XPath-Auswertungsumgebung}.
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Node DOM-Knoten}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette bzw. die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Node evaluateNode(final XPath source, final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if(source == null) throw new NullPointerException("source is null");
		if(script == null) throw new NullPointerException("script is null");
		synchronized(source){
			return XML.evaluateNode(source.compile(script), input);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link NodeList DOM-Knotenliste} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}.
	 * 
	 * @see XML#cachedXPathBuilder()
	 * @see XML#evaluateNodeSet(XPath, String, Node)
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link NodeList DOM-Knotenliste}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static NodeList evaluateNodeSet(final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if(script == null) throw new NullPointerException("script is null");
		return XML.evaluateNodeSet(XML.CACHED_XPATH_BUILDER.build(), script, input);
	}

	/**
	 * Diese Methode wertet den gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das Ergebnis als
	 * {@link NodeList DOM-Knotenliste} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}.
	 * 
	 * @see XPathConstants#NODESET
	 * @see XPathExpression#evaluate(Object, QName)
	 * @param source {@link XPathExpression XPath-Ausdruck}.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link NodeList DOM-Knotenliste}.
	 * @throws NullPointerException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static NodeList evaluateNodeSet(final XPathExpression source, final Node input) throws NullPointerException, XPathExpressionException {
		if(source == null) throw new NullPointerException("source is null");
		synchronized(source){
			return (NodeList)source.evaluate(input, XPathConstants.NODESET);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link NodeList DOM-Knotenliste} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}. Der {@link XPathExpression XPath-Ausdruck} wird über die gegebene {@link XPath XPath-Auswertungsumgebung} erzeugt.
	 * 
	 * @see XPath#compile(String)
	 * @see XML#evaluateNodeSet(XPathExpression, Node)
	 * @param source {@link XPath XPath-Auswertungsumgebung}.
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link NodeList DOM-Knotenliste}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette bzw. die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static NodeList evaluateNodeSet(final XPath source, final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if(source == null) throw new NullPointerException("source is null");
		if(script == null) throw new NullPointerException("script is null");
		synchronized(source){
			return XML.evaluateNodeSet(source.compile(script), input);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link String Textwert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}.
	 * 
	 * @see XML#cachedXPathBuilder()
	 * @see XML#evaluateString(XPath, String, Node)
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link String Textwert}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static String evaluateString(final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if(script == null) throw new NullPointerException("script is null");
		return XML.evaluateString(XML.CACHED_XPATH_BUILDER.build(), script, input);
	}

	/**
	 * Diese Methode wertet den gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das Ergebnis als
	 * {@link String Textwert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren {@link Document
	 * DOM-Dokument}.
	 * 
	 * @see XPathConstants#NODE
	 * @see XPathExpression#evaluate(Object, QName)
	 * @param source {@link XPathExpression XPath-Ausdruck}.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link String Textwert}.
	 * @throws NullPointerException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static String evaluateString(final XPathExpression source, final Node input) throws NullPointerException, XPathExpressionException {
		if(source == null) throw new NullPointerException("source is null");
		synchronized(source){
			return (String)source.evaluate(input, XPathConstants.STRING);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link String Textwert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}. Der {@link XPathExpression XPath-Ausdruck} wird über die gegebene {@link XPath XPath-Auswertungsumgebung} erzeugt.
	 * 
	 * @see XPath#compile(String)
	 * @see XML#evaluateString(XPathExpression, Node)
	 * @param source {@link XPath XPath-Auswertungsumgebung}.
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link String Textwert}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette bzw. die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static String evaluateString(final XPath source, final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if(source == null) throw new NullPointerException("source is null");
		if(script == null) throw new NullPointerException("script is null");
		synchronized(source){
			return XML.evaluateString(source.compile(script), input);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link Boolean Wahrheitswert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}.
	 * 
	 * @see XML#cachedXPathBuilder()
	 * @see XML#evaluateBoolean(XPath, String, Node)
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Boolean Wahrheitswert}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Boolean evaluateBoolean(final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if(script == null) throw new NullPointerException("script is null");
		return XML.evaluateBoolean(XML.CACHED_XPATH_BUILDER.build(), script, input);
	}

	/**
	 * Diese Methode wertet den gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das Ergebnis als
	 * {@link Boolean Wahrheitswert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren {@link Document
	 * DOM-Dokument}.
	 * 
	 * @see XPathConstants#BOOLEAN
	 * @see XPathExpression#evaluate(Object, QName)
	 * @param source {@link XPathExpression XPath-Ausdruck}.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Boolean Wahrheitswert}.
	 * @throws NullPointerException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Boolean evaluateBoolean(final XPathExpression source, final Node input) throws NullPointerException, XPathExpressionException {
		if(source == null) throw new NullPointerException("source is null");
		synchronized(source){
			return (Boolean)source.evaluate(input, XPathConstants.BOOLEAN);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link Boolean Wahrheitswert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}. Der {@link XPathExpression XPath-Ausdruck} wird über die gegebene {@link XPath XPath-Auswertungsumgebung} erzeugt.
	 * 
	 * @see XPath#compile(String)
	 * @see XML#evaluateBoolean(XPathExpression, Node)
	 * @param source {@link XPath XPath-Auswertungsumgebung}.
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Boolean Wahrheitswert}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette bzw. die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Boolean evaluateBoolean(final XPath source, final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if(source == null) throw new NullPointerException("source is null");
		if(script == null) throw new NullPointerException("script is null");
		synchronized(source){
			return XML.evaluateBoolean(source.compile(script), input);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link Number Zahlenwert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}.
	 * 
	 * @see XML#cachedXPathBuilder()
	 * @see XML#evaluateNumber(XPath, String, Node)
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Number Zahlenwert}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Number evaluateNumber(final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if(script == null) throw new NullPointerException("script is null");
		return XML.evaluateNumber(XML.CACHED_XPATH_BUILDER.build(), script, input);
	}

	/**
	 * Diese Methode wertet den gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das Ergebnis als
	 * {@link Number Zahlenwert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren {@link Document
	 * DOM-Dokument}.
	 * 
	 * @see XPathConstants#NUMBER
	 * @see XPathExpression#evaluate(Object, QName)
	 * @param source {@link XPathExpression XPath-Ausdruck}.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Number Zahlenwert}.
	 * @throws NullPointerException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Number evaluateNumber(final XPathExpression source, final Node input) throws NullPointerException, XPathExpressionException {
		if(source == null) throw new NullPointerException("source is null");
		synchronized(source){
			return (Number)source.evaluate(input, XPathConstants.NUMBER);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link Number Zahlenwert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}. Der {@link XPathExpression XPath-Ausdruck} wird über die gegebene {@link XPath XPath-Auswertungsumgebung} erzeugt.
	 * 
	 * @see XPath#compile(String)
	 * @see XML#evaluateNumber(XPathExpression, Node)
	 * @param source {@link XPath XPath-Auswertungsumgebung}.
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Number Zahlenwert}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette bzw. die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Number evaluateNumber(final XPath source, final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if(source == null) throw new NullPointerException("source is null");
		if(script == null) throw new NullPointerException("script is null");
		synchronized(source){
			return XML.evaluateNumber(source.compile(script), input);
		}
	}

	/**
	 * Diese Methode erzeugt ein neues {@link Document DOM-Dokument} und gibt es zurück.
	 * 
	 * @see ParseOptions#DEFAULT
	 * @see DocumentBuilderFactory#newInstance()
	 * @see DocumentBuilder#newDocument()
	 * @return {@link Document DOM-Dokument}.
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument() throws ParserConfigurationException {
		return XML.createDocument(ParseOptions.DEFAULT);
	}

	/**
	 * Diese Methode erzeugt ein neues {@link Document Document} und gibt es zurück.
	 * 
	 * @param options {@link ParseOptions ParseOptions}.
	 * @see DocumentBuilderFactory#newInstance()
	 * @see DocumentBuilder#newDocument()
	 * @return {@link Document Document}.
	 * @throws NullPointerException Wenn die gegebenen {@link ParseOptions Parse-Optionen} {@code null} sind.
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final ParseOptions options) throws NullPointerException, ParserConfigurationException {
		if(options == null) throw new NullPointerException("options is null");
		return options.applyTo(options.applyTo(DocumentBuilderFactory.newInstance()).newDocumentBuilder()).newDocument();
	}

	/**
	 * Diese Methode konvertiert das als {@link String Zeichenkette} gegebene Quelldokument zu einem {@link Document DOM-Dokument} und gibt dieses zurück.
	 * 
	 * @see ParseOptions#DEFAULT
	 * @see XML#createDocument(String, ParseOptions)
	 * @param source Quelldokument als {@link String Zeichenkette}.
	 * @return {@link Document DOM-Dokument}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument {@code null} ist.
	 * @throws SAXException Wenn die Konvertierung des gegebenen Quelldokuments fehlschlägt.
	 * @throws IOException Wenn das Lesen des gegebenen Quelldokuments fehlschlägt
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final String source) throws NullPointerException, SAXException, IOException, ParserConfigurationException {
		if(source == null) throw new NullPointerException("source is null");
		return XML.createDocument(source, ParseOptions.DEFAULT);
	}

	/**
	 * Diese Methode konvertiert das als {@link String Zeichenkette} gegebene Quelldokument zu einem {@link Document DOM-Dokument} und gibt dieses zurück.
	 * 
	 * @see XML#createDocument(Reader, ParseOptions)
	 * @param source Quelldokument als {@link String Zeichenkette}.
	 * @param options {@link ParseOptions ParseOptions}.
	 * @return {@link Document DOM-Dokument}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument oder die gegebenen {@link ParseOptions Parse-Optionen} {@code null} sind.
	 * @throws SAXException Wenn die Konvertierung des gegebenen Quelldokuments fehlschlägt
	 * @throws IOException Wenn das Lesen des gegebenen Quelldokuments fehlschlägt.
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final String source, final ParseOptions options) throws NullPointerException, SAXException, IOException,
		ParserConfigurationException {
		if(source == null) throw new NullPointerException("source is null");
		if(options == null) throw new NullPointerException("options is null");
		return XML.createDocument(new StringReader(source), options);
	}

	/**
	 * Diese Methode konvertiert das als {@link InputSource InputSource} gegebene Quelldokument zu einem {@link Document DOM-Dokument} und gibt dieses zurück.
	 * 
	 * @see ParseOptions#DEFAULT
	 * @see XML#createDocument(Reader, ParseOptions)
	 * @param source Quelldokument als {@link InputSource InputSource}.
	 * @return {@link Document DOM-Dokument}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument {@code null} ist.
	 * @throws SAXException Wenn die Konvertierung des gegebenen Quelldokuments fehlschlägt.
	 * @throws IOException Wenn das Lesen des gegebenen Quelldokuments fehlschlägt
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final Reader source) throws NullPointerException, SAXException, IOException, ParserConfigurationException {
		if(source == null) throw new NullPointerException("source is null");
		return XML.createDocument(source, ParseOptions.DEFAULT);
	}

	/**
	 * Diese Methode konvertiert das als {@link Reader Reader} gegebene Quelldokument zu einem {@link Document DOM-Dokument} und gibt dieses zurück.
	 * 
	 * @see XML#createDocument(InputSource, ParseOptions)
	 * @param source Quelldokument als {@link Reader Reader}.
	 * @param options {@link ParseOptions ParseOptions}.
	 * @return {@link Document DOM-Dokument}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument oder die gegebenen {@link ParseOptions Parse-Optionen} {@code null} sind.
	 * @throws SAXException Wenn die Konvertierung des gegebenen Quelldokuments fehlschlägt
	 * @throws IOException Wenn das Lesen des gegebenen Quelldokuments fehlschlägt.
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final Reader source, final ParseOptions options) throws NullPointerException, SAXException, IOException,
		ParserConfigurationException {
		if(source == null) throw new NullPointerException("source is null");
		if(options == null) throw new NullPointerException("options is null");
		return XML.createDocument(new InputSource(source), options);
	}

	/**
	 * Diese Methode konvertiert das als {@link InputSource InputSource} gegebene Quelldokument zu einem {@link Document DOM-Dokument} und gibt dieses zurück.
	 * 
	 * @see ParseOptions#DEFAULT
	 * @see XML#createDocument(InputSource, ParseOptions)
	 * @param source Quelldokument als {@link InputSource InputSource}.
	 * @return {@link Document DOM-Dokument}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument {@code null} ist.
	 * @throws SAXException Wenn die Konvertierung des gegebenen Quelldokuments fehlschlägt.
	 * @throws IOException Wenn das Lesen des gegebenen Quelldokuments fehlschlägt
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final InputSource source) throws NullPointerException, SAXException, IOException, ParserConfigurationException {
		if(source == null) throw new NullPointerException("source is null");
		return XML.createDocument(source, ParseOptions.DEFAULT);
	}

	/**
	 * Diese Methode konvertiert das als {@link InputSource InputSource} gegebene Quelldokument zu einem {@link Document DOM-Dokument} und gibt dieses zurück.
	 * 
	 * @param source Quelldokument als {@link InputSource InputSource}.
	 * @param options {@link ParseOptions ParseOptions}.
	 * @return {@link Document DOM-Dokument}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument oder die gegebenen {@link ParseOptions Parse-Optionen} {@code null} sind.
	 * @throws SAXException Wenn die Konvertierung des gegebenen Quelldokuments fehlschlägt
	 * @throws IOException Wenn das Lesen des gegebenen Quelldokuments fehlschlägt.
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final InputSource source, final ParseOptions options) throws NullPointerException, SAXException, IOException,
		ParserConfigurationException {
		if(source == null) throw new NullPointerException("source is null");
		if(options == null) throw new NullPointerException("options is null");
		return options.applyTo(options.applyTo(DocumentBuilderFactory.newInstance()).newDocumentBuilder()).parse(source);
	}

	/**
	 * Diese Methode konvertiert das als {@link String} gegebene Quelldokument zu einem {@link Templates XSL-Template} und gibt dieses zurück.
	 * 
	 * @see FormatOptions#DEFAULT
	 * @see XML#createTemplates(String, FormatOptions)
	 * @param source Quelldokument als {@link String}.
	 * @return {@link Templates XSL-Template}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument {@code null} ist.
	 * @throws TransformerConfigurationException Wenn die Erzeugung des {@link Templates XSL-Templates} fehlschlägt.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static Templates createTemplates(final String source) throws NullPointerException, TransformerConfigurationException,
		TransformerFactoryConfigurationError {
		if(source == null) throw new NullPointerException("source is null");
		return XML.createTemplates(source, FormatOptions.DEFAULT);
	}

	/**
	 * Diese Methode konvertiert das als {@link String} gegebene Quelldokument zu einem {@link Templates XSL-Template} und gibt dieses zurück.
	 * 
	 * @see StringReader
	 * @see XML#createTemplates(Reader)
	 * @param source Quelldokument als {@link String}.
	 * @param options {@link FormatOptions Format-Optionen}.
	 * @return {@link Templates XSL-Template}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument oder die {@link FormatOptions Format-Optionen} {@code null} sind.
	 * @throws TransformerConfigurationException Wenn die Erzeugung des {@link Templates XSL-Templates} fehlschlägt.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static Templates createTemplates(final String source, final FormatOptions options) throws NullPointerException, TransformerConfigurationException,
		TransformerFactoryConfigurationError {
		if(source == null) throw new NullPointerException("source is null");
		if(options == null) throw new NullPointerException("options is null");
		return XML.createTemplates(new StringReader(source), options);
	}

	/**
	 * Diese Methode konvertiert das als {@link Node Node} gegebene Quelldokument zu einem {@link Templates XSL-Template} und gibt dieses zurück.
	 * 
	 * @see FormatOptions#DEFAULT
	 * @see XML#createTemplates(Node, FormatOptions)
	 * @param source Quelldokument als {@link Node Node}.
	 * @return {@link Templates XSL-Template}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument {@code null} ist.
	 * @throws TransformerConfigurationException Wenn die Erzeugung des {@link Templates XSL-Templates} fehlschlägt.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static Templates createTemplates(final Node source) throws NullPointerException, TransformerConfigurationException,
		TransformerFactoryConfigurationError {
		if(source == null) throw new NullPointerException("source is null");
		return XML.createTemplates(source, FormatOptions.DEFAULT);
	}

	/**
	 * Diese Methode konvertiert das als {@link Node Node} gegebene Quelldokument zu einem {@link Templates XSL-Template} und gibt dieses zurück.
	 * 
	 * @see DOMSource
	 * @see XML#createTemplates(Source)
	 * @param source Quelldokument als {@link Node Node}.
	 * @param options {@link FormatOptions Format-Optionen}.
	 * @return {@link Templates XSL-Template}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument oder die {@link FormatOptions Format-Optionen} {@code null} sind.
	 * @throws TransformerConfigurationException Wenn die Erzeugung des {@link Templates XSL-Templates} fehlschlägt.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static Templates createTemplates(final Node source, final FormatOptions options) throws NullPointerException, TransformerConfigurationException,
		TransformerFactoryConfigurationError {
		if(source == null) throw new NullPointerException("source is null");
		if(options == null) throw new NullPointerException("options is null");
		return XML.createTemplates(new DOMSource(source), options);
	}

	/**
	 * Diese Methode konvertiert das als {@link Reader Reader} gegebene Quelldokument zu einem {@link Templates XSL-Template} und gibt dieses zurück.
	 * 
	 * @see FormatOptions#DEFAULT
	 * @see XML#createTemplates(Reader, FormatOptions)
	 * @param source Quelldokument als {@link Reader Reader}.
	 * @return {@link Templates XSL-Template}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument {@code null} ist.
	 * @throws TransformerConfigurationException Wenn die Erzeugung des {@link Templates XSL-Templates} fehlschlägt.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static Templates createTemplates(final Reader source) throws NullPointerException, TransformerConfigurationException,
		TransformerFactoryConfigurationError {
		if(source == null) throw new NullPointerException("source is null");
		return XML.createTemplates(source, FormatOptions.DEFAULT);
	}

	/**
	 * Diese Methode konvertiert das als {@link Reader Reader} gegebene Quelldokument zu einem {@link Templates XSL-Template} und gibt dieses zurück.
	 * 
	 * @see StreamSource
	 * @see XML#createTemplates(Source)
	 * @param source Quelldokument als {@link Reader Reader}.
	 * @param options {@link FormatOptions Format-Optionen}.
	 * @return {@link Templates XSL-Template}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument oder die {@link FormatOptions Format-Optionen} {@code null} sind.
	 * @throws TransformerConfigurationException Wenn die Erzeugung des {@link Templates XSL-Templates} fehlschlägt.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static Templates createTemplates(final Reader source, final FormatOptions options) throws NullPointerException, TransformerConfigurationException,
		TransformerFactoryConfigurationError {
		if(source == null) throw new NullPointerException("source is null");
		if(options == null) throw new NullPointerException("options is null");
		return XML.createTemplates(new StreamSource(source), options);
	}

	/**
	 * Diese Methode konvertiert das als {@link Source Source} gegebene Quelldokument zu einem {@link Templates XSL-Template} und gibt dieses zurück.
	 * 
	 * @see FormatOptions#DEFAULT
	 * @see XML#createTemplates(Source, FormatOptions)
	 * @param source Quelldokument als {@link Source Source}.
	 * @return {@link Templates XSL-Template}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument {@code null} ist.
	 * @throws TransformerConfigurationException Wenn die Erzeugung des {@link Templates XSL-Templates} fehlschlägt.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static Templates createTemplates(final Source source) throws NullPointerException, TransformerConfigurationException,
		TransformerFactoryConfigurationError {
		if(source == null) throw new NullPointerException("source is null");
		return XML.createTemplates(source, FormatOptions.DEFAULT);
	}

	/**
	 * Diese Methode konvertiert das als {@link Source Source} gegebene Quelldokument zu einem {@link Templates XSL-Template} und gibt dieses zurück.
	 * 
	 * @param source Quelldokument als {@link Source Source}.
	 * @param options {@link FormatOptions Format-Optionen}.
	 * @return {@link Templates XSL-Template}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument oder die {@link FormatOptions Format-Optionen} {@code null} sind.
	 * @throws TransformerConfigurationException Wenn die Erzeugung des {@link Templates XSL-Templates} fehlschlägt.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static Templates createTemplates(final Source source, final FormatOptions options) throws NullPointerException, TransformerConfigurationException,
		TransformerFactoryConfigurationError {
		if(source == null) throw new NullPointerException("source is null");
		if(options == null) throw new NullPointerException("options is null");
		return options.applyTo(TransformerFactory.newInstance()).newTemplates(source);
	}

	/**
	 * Diese Methode transformiert das als {@link Node Node} gegebenen Quelldokument in ein Zieldokument als {@link String Zeichenkette} und gibt dieses zurück.
	 * Zur Transformation wird ein {@link TransformerFactory#newTransformer() neuer} {@link Transformer XSL-Transformer} verwendet.
	 * 
	 * @see XML#transform(Writer, Node)
	 * @param source Quelldokument als {@link Node Node}.
	 * @return Zieldokument als {@link String Zeichenkette}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument bzw. das gegebene Zieldokument {@code null} ist.
	 * @throws TransformerException Wenn bei der Transformation ein Fehler eintritt.
	 * @throws TransformerConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static String transform(final Node source) throws NullPointerException, TransformerException, TransformerConfigurationException,
		TransformerFactoryConfigurationError {
		if(source == null) throw new NullPointerException("source is null");
		final Writer target = new StringWriter();
		XML.transform(target, source);
		return target.toString();
	}

	/**
	 * Diese Methode transformiert das als {@link Node Node} gegebenen Quelldokument in ein Zieldokument als {@link String Zeichenkette} und gibt dieses zurück.
	 * Zur Transformation wird ein neuer {@link Transformer XSL-Transformer} verwendet, welcher über das gegebene {@link Templates XSL-Template} erzeugt wurde.
	 * 
	 * @see XML#transform(Writer, Node, Templates)
	 * @param source Quelldokument als {@link Node Node}.
	 * @param script {@link Templates XSL-Template}.
	 * @return Zieldokument als {@link String Zeichenkette}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument, das gegebene Zieldokument bzw. das gegebene {@link Templates XSL-Template} {@code null} ist.
	 * @throws TransformerException Wenn bei der Transformation ein Fehler eintritt.
	 */
	public static String transform(final Node source, final Templates script) throws NullPointerException, TransformerException {
		if(source == null) throw new NullPointerException("source is null");
		if(script == null) throw new NullPointerException("script is null");
		final Writer target = new StringWriter();
		XML.transform(target, source, script);
		return target.toString();
	}

	/**
	 * Diese Methode transformiert das als {@link Node Node} gegebenen Quelldokument in ein Zieldokument als {@link String Zeichenkette} und gibt dieses zurück.
	 * Zur Transformation wird der gegebenen {@link Transformer XSL-Transformer} verwendet.
	 * 
	 * @see XML#transform(Result, Source, Transformer)
	 * @param source Quelldokument als {@link Node Node}.
	 * @param script {@link Transformer XSL-Transformer}.
	 * @return Zieldokument als {@link String Zeichenkette}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument, das gegebene Zieldokument bzw. der gegebene {@link Transformer XSL-Transformer} {@code null}
	 *         ist.
	 * @throws TransformerException Wenn bei der Transformation ein Fehler eintritt.
	 */
	public static String transform(final Node source, final Transformer script) throws NullPointerException, TransformerException {
		if(source == null) throw new NullPointerException("source is null");
		if(script == null) throw new NullPointerException("script is null");
		final Writer target = new StringWriter();
		XML.transform(new StreamResult(target), new DOMSource(source), script);
		return target.toString();
	}

	/**
	 * Diese Methode transformiert das als {@link Writer Writer} gegebenen Quelldokument in ein Zieldokument als {@link String Zeichenkette} und gibt dieses
	 * zurück. Zur Transformation wird ein {@link TransformerFactory#newTransformer() neuer} {@link Transformer XSL-Transformer} verwendet, welcher über die
	 * gegebenen {@link FormatOptions Format-Optionen} konfiguriert wurde.
	 * 
	 * @see XML#transform(Result, Source, FormatOptions)
	 * @see TransformerFactory#newTransformer()
	 * @param source Quelldokument als {@link Node Node}.
	 * @param options {@link FormatOptions Format-Optionen}.
	 * @return Zieldokument als {@link String Zeichenkette}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument, das gegebene Zieldokument bzw. die gegebenen {@link FormatOptions Format-Optionen}
	 *         {@code null} sind.
	 * @throws TransformerException Wenn bei der Transformation ein Fehler eintritt.
	 * @throws TransformerConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static String transform(final Node source, final FormatOptions options) throws NullPointerException, TransformerException,
		TransformerConfigurationException, TransformerFactoryConfigurationError {
		if(source == null) throw new NullPointerException("source is null");
		if(options == null) throw new NullPointerException("options is null");
		final Writer target = new StringWriter();
		XML.transform(target, source, options);
		return target.toString();
	}

	/**
	 * Diese Methode transformiert das als {@link Node Node} gegebenen Quelldokument in ein Zieldokument als {@link Writer Writer}. Zur Transformation wird ein
	 * {@link TransformerFactory#newTransformer() neuer} {@link Transformer XSL-Transformer} verwendet.
	 * 
	 * @see XML#transform(Result, Source)
	 * @param target Zieldokument als {@link Writer Writer}.
	 * @param source Quelldokument als {@link Node Node}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument bzw. das gegebene Zieldokument {@code null} ist.
	 * @throws TransformerException Wenn bei der Transformation ein Fehler eintritt.
	 * @throws TransformerConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static void transform(final Writer target, final Node source) throws NullPointerException, TransformerException, TransformerConfigurationException,
		TransformerFactoryConfigurationError {
		if(target == null) throw new NullPointerException("target is null");
		if(source == null) throw new NullPointerException("source is null");
		XML.transform(new StreamResult(target), new DOMSource(source));
	}

	/**
	 * Diese Methode transformiert das als {@link Node Node} gegebenen Quelldokument in ein Zieldokument als {@link Writer Writer}. Zur Transformation wird ein
	 * neuer {@link Transformer XSL-Transformer} verwendet, welcher über das gegebene {@link Templates XSL-Template} erzeugt wurde.
	 * 
	 * @see XML#transform(Result, Source, Templates)
	 * @param target Zieldokument als {@link Writer Writer}.
	 * @param source Quelldokument als {@link Node Node}.
	 * @param script {@link Templates XSL-Template}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument, das gegebene Zieldokument bzw. das gegebene {@link Templates XSL-Template} {@code null} ist.
	 * @throws TransformerException Wenn bei der Transformation ein Fehler eintritt.
	 */
	public static void transform(final Writer target, final Node source, final Templates script) throws NullPointerException, TransformerException {
		if(target == null) throw new NullPointerException("target is null");
		if(source == null) throw new NullPointerException("source is null");
		if(script == null) throw new NullPointerException("script is null");
		XML.transform(new StreamResult(target), new DOMSource(source), script);
	}

	/**
	 * Diese Methode transformiert das als {@link Node Node} gegebenen Quelldokument in ein Zieldokument als {@link Writer Writer}. Zur Transformation wird der
	 * gegebenen {@link Transformer XSL-Transformer} verwendet.
	 * 
	 * @see XML#transform(Result, Source, Transformer)
	 * @param target Zieldokument als {@link Writer Writer}.
	 * @param source Quelldokument als {@link Node Node}.
	 * @param script {@link Transformer XSL-Transformer}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument, das gegebene Zieldokument bzw. der gegebene {@link Transformer XSL-Transformer} {@code null}
	 *         ist.
	 * @throws TransformerException Wenn bei der Transformation ein Fehler eintritt.
	 */
	public static void transform(final Writer target, final Node source, final Transformer script) throws NullPointerException, TransformerException {
		if(target == null) throw new NullPointerException("target is null");
		if(source == null) throw new NullPointerException("source is null");
		if(script == null) throw new NullPointerException("script is null");
		XML.transform(new StreamResult(target), new DOMSource(source), script);
	}

	/**
	 * Diese Methode transformiert das als {@link Node Node} gegebenen Quelldokument in ein Zieldokument als {@link Writer Writer}. Zur Transformation wird ein
	 * {@link TransformerFactory#newTransformer() neuer} {@link Transformer XSL-Transformer} verwendet, welcher über die gegebenen {@link FormatOptions
	 * Format-Optionen} konfiguriert wurde.
	 * 
	 * @see XML#transform(Result, Source, FormatOptions)
	 * @see TransformerFactory#newTransformer()
	 * @param target Zieldokument als {@link Writer Writer}.
	 * @param source Quelldokument als {@link Node Node}.
	 * @param options {@link FormatOptions Format-Optionen}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument, das gegebene Zieldokument bzw. die gegebenen {@link FormatOptions Format-Optionen}
	 *         {@code null} sind.
	 * @throws TransformerException Wenn bei der Transformation ein Fehler eintritt.
	 * @throws TransformerConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static void transform(final Writer target, final Node source, final FormatOptions options) throws NullPointerException, TransformerException,
		TransformerConfigurationException, TransformerFactoryConfigurationError {
		if(target == null) throw new NullPointerException("target is null");
		if(source == null) throw new NullPointerException("source is null");
		if(options == null) throw new NullPointerException("options is null");
		XML.transform(new StreamResult(target), new DOMSource(source), options);
	}

	/**
	 * Diese Methode transformiert das als {@link Source Source} gegebenen Quelldokument in ein Zieldokument als {@link Result Result}. Zur Transformation wird
	 * ein {@link TransformerFactory#newTransformer() neuer} {@link Transformer XSL-Transformer} verwendet.
	 * 
	 * @see XML#transform(Result, Source, FormatOptions)
	 * @see FormatOptions#DEFAULT
	 * @see TransformerFactory#newTransformer()
	 * @param target Zieldokument als {@link Result Result}.
	 * @param source Quelldokument als {@link Source Source}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument bzw. das gegebene Zieldokument {@code null} ist.
	 * @throws TransformerException Wenn bei der Transformation ein Fehler eintritt.
	 * @throws TransformerConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static void transform(final Result target, final Source source) throws NullPointerException, TransformerException, TransformerConfigurationException,
		TransformerFactoryConfigurationError {
		if(target == null) throw new NullPointerException("target is null");
		if(source == null) throw new NullPointerException("source is null");
		XML.transform(target, source, FormatOptions.DEFAULT);
	}

	/**
	 * Diese Methode transformiert das als {@link Source Source} gegebenen Quelldokument in ein Zieldokument als {@link Result Result}. Zur Transformation wird
	 * ein neuer {@link Transformer XSL-Transformer} verwendet, welcher über das gegebene {@link Templates XSL-Template} erzeugt wurde.
	 * 
	 * @see Templates#newTransformer()
	 * @param target Zieldokument als {@link Result Result}.
	 * @param source Quelldokument als {@link Source Source}.
	 * @param script {@link Templates XSL-Template}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument, das gegebene Zieldokument bzw. das gegebene {@link Templates XSL-Template} {@code null} ist.
	 * @throws TransformerException Wenn bei der Transformation ein Fehler eintritt.
	 */
	public static void transform(final Result target, final Source source, final Templates script) throws NullPointerException, TransformerException {
		if(target == null) throw new NullPointerException("target is null");
		if(source == null) throw new NullPointerException("source is null");
		if(script == null) throw new NullPointerException("script is null");
		XML.transform(target, source, script.newTransformer());
	}

	/**
	 * Diese Methode transformiert das als {@link Source Source} gegebenen Quelldokument in ein Zieldokument als {@link Result Result}. Zur Transformation wird
	 * der gegebenen {@link Transformer XSL-Transformer} verwendet.
	 * 
	 * @param target Zieldokument als {@link Result Result}.
	 * @param source Quelldokument als {@link Source Source}.
	 * @param script {@link Transformer XSL-Transformer}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument, das gegebene Zieldokument bzw. der gegebene {@link Transformer XSL-Transformer} {@code null}
	 *         ist.
	 * @throws TransformerException Wenn bei der Transformation ein Fehler eintritt.
	 */
	public static void transform(final Result target, final Source source, final Transformer script) throws NullPointerException, TransformerException {
		if(target == null) throw new NullPointerException("target is null");
		if(source == null) throw new NullPointerException("source is null");
		if(script == null) throw new NullPointerException("script is null");
		synchronized(script){
			script.transform(source, target);
		}
	}

	/**
	 * Diese Methode transformiert das als {@link Source Source} gegebenen Quelldokument in ein Zieldokument als {@link Result Result}. Zur Transformation wird
	 * ein {@link TransformerFactory#newTransformer() neuer} {@link Transformer XSL-Transformer} verwendet, welcher über die gegebenen {@link FormatOptions
	 * Format-Optionen} konfiguriert wurde.
	 * 
	 * @see TransformerFactory#newTransformer()
	 * @param target Zieldokument als {@link Result Result}.
	 * @param source Quelldokument als {@link Source Source}.
	 * @param options {@link FormatOptions Format-Optionen}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument, das gegebene Zieldokument bzw. die gegebenen {@link FormatOptions Format-Optionen}
	 *         {@code null} sind.
	 * @throws TransformerException Wenn bei der Transformation ein Fehler eintritt.
	 * @throws TransformerConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 * @throws TransformerFactoryConfigurationError Wenn keine {@link TransformerFactory XSL-Transformer-Factory} Implementation verfügbar ist.
	 */
	public static void transform(final Result target, final Source source, final FormatOptions options) throws NullPointerException, TransformerException,
		TransformerConfigurationException, TransformerFactoryConfigurationError {
		if(target == null) throw new NullPointerException("target is null");
		if(source == null) throw new NullPointerException("source is null");
		if(options == null) throw new NullPointerException("options is null");
		XML.transform(target, source, options.applyTo(options.applyTo(TransformerFactory.newInstance()).newTransformer()));
	}

	/**
	 * Dieser Konstruktor erzwingt die Nichtinstanziierbarkeit.
	 */
	XML() {
	}

}
