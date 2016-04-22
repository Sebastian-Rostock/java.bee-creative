package bee.creative.bex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import bee.creative.bex.BEXBuilder.BEXFileBuilder;
import bee.creative.bex.BEXLoader.BEXFileLoader;
import bee.creative.data.DataTarget;
import bee.creative.iam.IAMBuilder;
import bee.creative.iam.IAMBuilder.IAMIndexBuilder;
import bee.creative.iam.IAMBuilder.IAMListingBuilder;
import bee.creative.util.Comparators;
import bee.creative.util.IO;
import bee.creative.util.Objects;
import bee.creative.util.Unique.UniqueMap;

/** Diese Klasse implementiert die Algorithmen zur Kodierung der {@link BEX} Datenstrukturen und kann als Konfigurator zur {@link #encode() Überführung} eines
 * {@link #useSource(Object) XML-Dokuments} in ein {@link #useTarget(Object) BEX-Dokument} eingesetzt werden.
 * 
 * @see BEXFileEncoder
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class BEXCodec {

	/** Diese Klasse implementiert die Optionen für {@link BEXCodec#useOptions(BEXOption...)}.
	 * 
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static enum BEXOption {

		/** Diese Option kennzeichnet die Aktivierung der URI von Attributknoten.
		 * 
		 * @see BEXFileEncoder#useAttrUriEnabled(boolean) */
		EnableAttrUri,

		/** Diese Option kennzeichnet die Aktivierung der Elternknoten von Attributknoten.
		 * 
		 * @see BEXFileEncoder#useAttrParentEnabled(boolean) */
		EnableAttrParent,

		/** Diese Option kennzeichnet die Aktivierung der URI von Kindknoten.
		 * 
		 * @see BEXFileEncoder#useChldUriEnabled(boolean) */
		EnableChldUri,

		/** Diese Option kennzeichnet die Aktivierung der Elternknoten von Kindknoten.
		 * 
		 * @see BEXFileEncoder#useChldParentEnabled(boolean) */
		EnableChldParent;

	}

	{}

	/** Dieses Feld speichert die Bytereihenfolge. */
	ByteOrder _order_;

	/** Dieses Feld speichert die Eingabedaten. */
	Object _source_;

	/** Dieses Feld speichert die Ausgabedaten. */
	Object _target_;

	/** Dieses Feld speichert die Optionen. */
	Set<BEXOption> _options_ = new HashSet<>();

	{}

	/** Diese Methode gibt die Bytereihenfolge zurück.
	 * 
	 * @see #useOrder(ByteOrder)
	 * @return Bytereihenfolge. */
	public final ByteOrder getOrder() {
		return this._order_;
	}

	/** Diese Methode setzt die Bytereihenfolge und gibt {@code this} zurück.<br>
	 * Wenn diese {@code null} ist, wird {@link ByteOrder#nativeOrder()} verwendet.
	 * 
	 * @see BEXFileEncoder#encode(ByteOrder)
	 * @param order Bytereihenfolge.
	 * @return {@code this}. */
	public final BEXCodec useOrder(final ByteOrder order) {
		this._order_ = order;
		return this;
	}

	/** Diese Methode gibt die Eingabedaten zurück.
	 * 
	 * @see #useSource(Object)
	 * @return Eingabedaten. */
	public final Object getSource() {
		return this._source_;
	}

	/** Diese Methode setzt die Eingabedaten (XML-Dokument) und gibt {@code this} zurück.<br>
	 * Wenn die Eingabedaten ein {@link Document} sind, wird dieses {@link BEXFileEncoder#putNode(Node) eingelesen}. Andernfalls werden die Eingabedaten in eine
	 * {@link Reader} {@link IO#inputReaderFrom(Object) überführt}.
	 * 
	 * @see IO#inputReaderFrom(Object)
	 * @param source Eingabedaten.
	 * @return {@code this}. */
	public final BEXCodec useSource(final Object source) {
		this._source_ = source;
		return this;
	}

	/** Diese Methode gibt die Ausgabedaten zurück.
	 * 
	 * @see #useTarget(Object)
	 * @return Ausgabedaten. */
	public final Object getTarget() {
		return this._target_;
	}

	/** Diese Methode setzt die Ausgabedaten (BEX-Dokument) und gibt {@code this} zurück.
	 * 
	 * @see IO#outputDataFrom(Object)
	 * @param target Ausgabedaten.
	 * @return {@code this}. */
	public final BEXCodec useTarget(final Object target) {
		this._target_ = target;
		return this;
	}

	/** Diese Methode gibt die Optionen zurück.
	 * 
	 * @see #useOptions(BEXOption...)
	 * @return Optionen. */
	public final Set<BEXOption> getOptions() {
		return this._options_;
	}

	/** Diese Methode setzt die Optionen und gibt {@code this} zurück.
	 * 
	 * @see BEXFileEncoder#useAttrUriEnabled(boolean)
	 * @see BEXFileEncoder#useAttrParentEnabled(boolean)
	 * @see BEXFileEncoder#useChldUriEnabled(boolean)
	 * @see BEXFileEncoder#useChldParentEnabled(boolean)
	 * @param options Optionen.
	 * @return {@code this}. */
	public final BEXCodec useOptions(final BEXOption... options) {
		this._options_ = new HashSet<>();
		if (options == null) return this;
		this._options_.addAll(Arrays.asList(options));
		return this;
	}

	/** Diese Methode überführt die {@link #getSource() Eingabedaten} (XML-Dokument) in die {@link #getTarget() Ausgabedaten} (BEX-Dokument) und gibt {@code this}
	 * zurück.
	 * 
	 * @see #encodeSource()
	 * @see #encodeTarget(BEXFileEncoder)
	 * @return {@code this}.
	 * @throws IOException Wenn {@link #encodeSource()} bzw. {@link #encodeTarget(BEXFileEncoder)} eine entsprechende Ausnahme auslöst.
	 * @throws SAXException Wenn {@link #encodeSource()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link #encodeTarget(BEXFileEncoder)} eine entsprechende Ausnahme auslöst. */
	public final BEXCodec encode() throws IOException, SAXException, IllegalArgumentException {
		return this.encodeTarget(this.encodeSource());
	}

	/** Diese Methode überführt die {@link #getSource() Eingabedaten} in einen {@link BEXFileEncoder} und gibt diesen zurück.
	 * 
	 * @return {@link IAMIndexBuilder}.
	 * @throws IOException Wenn die Eingabedaten nicht gelesen werden können.
	 * @throws SAXException Wenn {@link BEXFileEncoder#putNode(InputSource, XMLReader)} eine entsprechende Ausnahme auslöst. */

	public final BEXFileBuilder encodeSource() throws IOException, SAXException {
		final Object source = this._source_;
		final Set<?> options = this._options_;
		final BEXFileBuilder result = new BEXFileBuilder();
		result.useAttrUriEnabled(options.contains(BEXOption.EnableAttrUri));
		result.useAttrParentEnabled(options.contains(BEXOption.EnableAttrParent));
		result.useChldUriEnabled(options.contains(BEXOption.EnableChldUri));
		result.useChldParentEnabled(options.contains(BEXOption.EnableChldParent));
		if (source instanceof Document) {
			result.putNode((Document)source);
		} else {
			result.putNode(new InputSource(IO.inputReaderFrom(source)), XMLReaderFactory.createXMLReader());
		}
		return result;
	}

	/** Diese Methode überführt den gegebenen {@link BEXFileEncoder} in die {@link #getTarget() Ausgabedaten} und gibt {@code this} zurück.<br>
	 * Hierbei wird die über {@link #useOrder(ByteOrder)} bestimmte Bytereihenfolge verwendet.
	 * 
	 * @see BEXFileEncoder#encode(ByteOrder)
	 * @param source {@link BEXFileEncoder}.
	 * @return {@code this}.
	 * @throws IOException Wenn die Ausgabedaten nicht erzeigt oder geschrieben werden können.
	 * @throws IllegalArgumentException Wenn {@link BEXFileEncoder#encode(ByteOrder)} eine entsprechende Ausnahme auslöst. */
	public final BEXCodec encodeTarget(final BEXFileBuilder source) throws IOException, IllegalArgumentException {
		try (DataTarget target = IO.outputDataFrom(this._target_)) {
			final ByteOrder order = this._order_;
			final byte[] bytes = source.toBytes(order);
			target.write(bytes);
		}
		return this;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this._order_, this._source_, this._target_);
	}

}
