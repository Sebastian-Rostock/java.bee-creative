package bee.creative.xml;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Validator;
import org.w3c.dom.Node;
import bee.creative.util.Builders.BaseValueBuilder;

/** Diese Klasse implementiert einen abstrakten Konfigurator eines {@link Result}, das für die Ausgabedaten eines {@link Transformer} oder {@link Validator}
 * genutzt wird.
 *
 * @see Validator#validate(Source, Result)
 * @see Transformer#transform(Source, Result)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class ResultBuilder<GOwner> extends BaseValueBuilder<Result, GOwner> {

	public static abstract class Value<GOwner> extends ResultBuilder<GOwner> {

		Result value;

		@Override
		public Result get() {
			return this.value;
		}

		@Override
		public void set(final Result value) {
			this.value = value;
		}

	}

	public static abstract class Proxy<GOwner> extends ResultBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public Result get() {
			return this.value().get();
		}

		@Override
		public void set(final Result value) {
			this.value().set(value);
		}

	}

	/** Diese Methode delegiert das gegebene Objekt abhängig von seinem Datentyp an eine der spezifischen Methoden und gibt {@code this} zurück. Unbekannte
	 * Datentypen werden ignoriert.
	 *
	 * @param object Quelldaten als { {@link File}, {@link Node}, {@link Writer}, {@link OutputStream}, {@link Result} oder {@link ResultBuilder}.
	 * @return {@code this}. */
	public GOwner use(final Object object) {
		if (object instanceof ResultBuilder<?>) return this.use((ResultBuilder<?>)object);
		if (object instanceof Result) return this.useValue((Result)object);
		if (object instanceof File) return this.useFile((File)object);
		if (object instanceof Node) return this.useNode((Node)object);
		if (object instanceof Writer) return this.useWriter((Writer)object);
		if (object instanceof OutputStream) return this.useStream((OutputStream)object);
		return this.owner();
	}

	/** Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit dem gegebenen {@link File} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see StreamResult#StreamResult(File)
	 * @param file {@link File}.
	 * @return {@code this}. */
	public GOwner useFile(final File file) {
		return this.useValue(new StreamResult(file));
	}

	/** Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit der gegebenen Datei und gibt {@code this} zurück.
	 *
	 * @see #useFile(File)
	 * @see File#File(String)
	 * @param file Datei.
	 * @return {@code this}. */
	public GOwner useFile(final String file) {
		return this.useFile(new File(file));
	}

	/** Diese Methode setzt die Ergebnisdaten auf ein {@link DOMResult} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see DOMResult#DOMResult()
	 * @return {@code this}. */
	public GOwner useNode() {
		return this.useValue(new DOMResult());
	}

	/** Diese Methode setzt die Ergebnisdaten auf ein {@link DOMResult} mit dem gegebenen {@link Node} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see DOMResult#DOMResult(Node)
	 * @param node {@link Node}.
	 * @return {@code this}. */
	public GOwner useNode(final Node node) {
		return this.useValue(new DOMResult(node));
	}

	/** Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit dem gegebenen {@link Writer} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see StreamResult#StreamResult(Writer)
	 * @param writer {@link Writer}.
	 * @return {@code this}. */
	public GOwner useWriter(final Writer writer) {
		return this.useValue(new StreamResult(writer));
	}

	/** Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit dem gegebenen {@link OutputStream} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see StreamResult#StreamResult(OutputStream)
	 * @param stream {@link OutputStream}.
	 * @return {@code this}. */
	public GOwner useStream(final OutputStream stream) {
		return this.useValue(new StreamResult(stream));
	}

}