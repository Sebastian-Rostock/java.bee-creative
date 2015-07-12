package bee.creative.xml;

import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert die Optionen eines {@link DocumentBuilder Document-Builder} sowie einer {@link DocumentBuilderFactory Document-Builder-Factory}.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class XMLParseOptions {

	final XMLFeatureData<XMLParseOptions> featureData = //
		new XMLFeatureData<XMLParseOptions>(this);

	public XMLFeatureData<XMLParseOptions> openFeatureData() {
		return this.featureData;
	}

	XMLAttributeData<XMLParseOptions> attributeData = //
		new XMLAttributeData<XMLParseOptions>(this);

	/**
	 * Dieses Feld speichert das {@link Schema}.
	 * 
	 * @see DocumentBuilderFactory#getSchema()
	 */
	Schema schema;

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
	public XMLParseOptions reset() {
		return XMLParseOptions.DEFAULT.applyTo(this);
	}

	/**
	 * Diese Methode überträgt die Optionen auf die gegebenen {@link XMLParseOptions Parse-Optionen} und gibt diese zurück.
	 * 
	 * @param target {@link XMLParseOptions Parse-Optionen}.
	 * @return {@link XMLParseOptions Parse-Optionen}.
	 * @throws NullPointerException Wenn die gegebenen {@link XMLParseOptions Parse-Optionen} {@code null} sind.
	 */
	public XMLParseOptions applyTo(final XMLParseOptions target) throws NullPointerException {
		if (target == null) throw new NullPointerException("target is null");
		return target.setFeatureMap(this.getFeatureMap()).setAttributeMap(this.attributeMap).setSchema(this.schema).setValidating(this.validating)
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
		if (target == null) throw new NullPointerException("target is null");
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
		if (target == null) throw new NullPointerException("target is null");
		target.setSchema(this.schema);
		target.setValidating(this.validating);
		target.setCoalescing(this.coalescing);
		target.setXIncludeAware(this.xIncludeAware);
		target.setNamespaceAware(this.namespaceAware);
		target.setIgnoringComments(this.ignoringComments);
		target.setIgnoringElementContentWhitespace(this.ignoringElementContentWhitespace);
		target.setExpandEntityReferences(this.expandEntityReferences);
		for (final Entry<String, Boolean> entry: this.getFeatureMap().entrySet()) {
			target.setFeature(entry.getKey(), entry.getValue().booleanValue());
		}
		if (this.attributeMap != null) {
			for (final Entry<String, String> entry: this.attributeMap.entrySet()) {
				target.setAttribute(entry.getKey(), entry.getValue());
			}
		}
		return target;
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
	public XMLParseOptions setSchema(final Schema value) {
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
	public XMLParseOptions setValidating(final boolean value) {
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
	public XMLParseOptions setCoalescing(final boolean value) {
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
	public XMLParseOptions setErrorHandler(final ErrorHandler value) {
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
	public XMLParseOptions setEntityResolver(final EntityResolver value) {
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
	public XMLParseOptions setExpandEntityReferences(final boolean value) {
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
	public XMLParseOptions setIgnoringComments(final boolean value) {
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
	public XMLParseOptions setIgnoringElementContentWhitespace(final boolean value) {
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
	public XMLParseOptions setXIncludeAware(final boolean value) {
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
	public XMLParseOptions setNamespaceAware(final boolean value) {
		this.namespaceAware = value;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCallFormat(false, true, this, new Object[]{"featureMap", this.getFeatureMap(), "attributeMap", this.attributeMap, "schema",
			this.schema, "validating", this.validating, "coalescing", this.coalescing, "errorHandler", this.errorHandler, "entityResolver", this.entityResolver,
			"expandEntityReferences", this.expandEntityReferences, "ignoringComments", this.ignoringComments, "ignoringElementContentWhitespace",
			this.ignoringElementContentWhitespace, "xIncludeAware", this.xIncludeAware, "namespaceAware", this.namespaceAware});
	}

}