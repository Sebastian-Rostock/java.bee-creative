package bee.creative.xml;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import bee.creative.util.Builders.BaseValueBuilder;

/** Diese Klasse implementiert den Konfigurator eines {@link Charset}. Initialisiert wird dieses {@link Charset} über {@link #useDEFAULT()}.
 *
 * @see Charset#forName(String)
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class CharsetBuilder<GOwner> extends BaseValueBuilder<Charset, GOwner> {

	public static abstract class Value<GOwner> extends CharsetBuilder<GOwner> {

		Charset value;

		@Override
		public Charset get() {
			return this.value;
		}

		@Override
		public void set(final Charset value) {
			this.value = value;
		}

	}

	public static abstract class Proxy<GOwner> extends CharsetBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public Charset get() {
			return this.value().get();
		}

		@Override
		public void set(final Charset value) {
			this.value().set(value);
		}

	}

	/** Diese Methode setzt den Wert auf {@link Charset#forName(String) Charset.forName(charsetName)} und gibt {@link #owner()} zurück.
	 *
	 * @see #useValue(Object)
	 * @see Charset#forName(String)
	 * @param charsetName Name des {@link Charset}.
	 * @return {@link #owner()}.
	 * @throws NullPointerException Wenn {@link Charset#forName(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link Charset#forName(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalCharsetNameException Wenn {@link Charset#forName(String)} eine entsprechende Ausnahme auslöst.
	 * @throws UnsupportedCharsetException Wenn {@link Charset#forName(String)} eine entsprechende Ausnahme auslöst. */
	public GOwner use(final String charsetName) throws NullPointerException, IllegalArgumentException, IllegalCharsetNameException, UnsupportedCharsetException {
		return this.useValue(Charset.forName(charsetName));
	}

	/** Diese Methode setzt den Wert auf {@link StandardCharsets#UTF_8} und gibt {@link #owner()} zurück.
	 *
	 * @see #useValue(Object)
	 * @return {@link #owner()}. */
	public GOwner useUTF_8() {
		return this.useValue(StandardCharsets.UTF_8);
	}

	/** Diese Methode setzt den Wert auf {@link StandardCharsets#UTF_16} und gibt {@link #owner()} zurück.
	 *
	 * @see #useValue(Object)
	 * @return {@link #owner()}. */
	public GOwner useUTF_16() {
		return this.useValue(StandardCharsets.UTF_16);
	}

	/** Diese Methode setzt den Wert auf {@link StandardCharsets#UTF_16BE} und gibt {@link #owner()} zurück.
	 *
	 * @see #useValue(Object)
	 * @return {@link #owner()}. */
	public GOwner useUTF_16BE() {
		return this.useValue(StandardCharsets.UTF_16BE);
	}

	/** Diese Methode setzt den Wert auf {@link StandardCharsets#UTF_16LE} und gibt {@link #owner()} zurück.
	 *
	 * @see #useValue(Object)
	 * @return {@link #owner()}. */
	public GOwner useUTF_16LE() {
		return this.useValue(StandardCharsets.UTF_16LE);
	}

	/** Diese Methode setzt den Wert auf {@link StandardCharsets#US_ASCII} und gibt {@link #owner()} zurück.
	 *
	 * @see #useValue(Object)
	 * @return {@link #owner()}. */
	public GOwner useUS_ASCII() {
		return this.useValue(StandardCharsets.US_ASCII);
	}

	/** Diese Methode setzt den Wert auf {@link StandardCharsets#ISO_8859_1} und gibt {@link #owner()} zurück.
	 *
	 * @see #useValue(Object)
	 * @return {@link #owner()}. */
	public GOwner useISO_8859_1() {
		return this.useValue(StandardCharsets.ISO_8859_1);
	}

	/** Diese Methode setzt den Wert auf {@link Charset#defaultCharset()} und gibt {@link #owner()} zurück.
	 *
	 * @see #useValue(Object)
	 * @return {@link #owner()}. */
	public GOwner useDEFAULT() {
		return this.useValue(Charset.defaultCharset());
	}

}