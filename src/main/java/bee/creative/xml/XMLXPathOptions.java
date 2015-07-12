package bee.creative.xml;

import java.util.Map;
import java.util.Map.Entry;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert die Optionen einer {@link XPath XPath-Auswertungsumgebung} sowie einer {@link XPathFactory XPath-Factory}.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class XMLXPathOptions  {

	XMLFeatureData<XMLXPathOptions> featureData;
	
	
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

	public XMLFeatureData<XMLXPathOptions> openFeatureData() {
		return featureData;
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
	public XMLXPathOptions setVariableResolver(final XPathVariableResolver value) {
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
	public XMLXPathOptions setFunctionResolver(final XPathFunctionResolver value) {
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
	public XMLXPathOptions setNamespaceContext(final NamespaceContext value) {
		this.namespaceContext = value;
		return this;
	}

	/**
	 * Diese Methode überträgt die Optionen auf die gegebenen {@link XMLXPathOptions XPath-Optionen} und gibt diese zurück.
	 * 
	 * @param target {@link XMLXPathOptions XPath-Optionen}.
	 * @return {@link XMLXPathOptions XPath-Optionen}.
	 * @throws NullPointerException Wenn die gegebenen {@link XMLXPathOptions XPath-Optionen} {@code null} sind.
	 */
	public XMLXPathOptions applyTo(final XMLXPathOptions target) throws NullPointerException {
		if (target == null) throw new NullPointerException("target is null");
		return target.setFeatureMap(this.getFeatureMap()).setVariableResolver(this.variableResolver).setFunctionResolver(this.functionResolver)
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
		if (target == null) throw new NullPointerException("target is null");
		if (this.namespaceContext != null) {
			target.setNamespaceContext(this.namespaceContext);
		}
		if (this.variableResolver != null) {
			target.setXPathVariableResolver(this.variableResolver);
		}
		if (this.functionResolver != null) {
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
		if (target == null) throw new NullPointerException("target is null");
		if (this.variableResolver != null) {
			target.setXPathVariableResolver(this.variableResolver);
		}
		if (this.functionResolver != null) {
			target.setXPathFunctionResolver(this.functionResolver);
		}
			for (final Entry<String, Boolean> entry: this.getFeatureMap().entrySet()) {
				target.setFeature(entry.getKey(), entry.getValue().booleanValue());
		}
		return target;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCallFormat(false, true, this, new Object[]{"featureMap", this.getFeatureMap(), "variableResolver", this.variableResolver,
			"functionResolver", this.functionResolver, "namespaceContext", this.namespaceContext});
	}

}