package bee.creative.xml;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import bee.creative.util.Builders.BaseValueBuilder;

/** Diese Klasse implementiert den Konfigurator eines {@link Charset}.<br>
 * Initialisiert wird dieses {@link Charset} über {@link #useDEFAULT()}.
 *
 * @see Charset#forName(String)
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseCharsetData<GThis> extends BaseValueBuilder<Charset, GThis> {

	/** Dieser Konstruktor initialisiert das {@link Charset} über {@link #useDEFAULT()}. */
	public BaseCharsetData() {
		this.useDEFAULT();
	}

	/** Diese Methode setzt den Wert auf {@link Charset#forName(String) Charset.forName(charsetName)} und gibt {@code this} zurück.
	 *
	 * @see #use(Object)
	 * @see Charset#forName(String)
	 * @param charsetName Name des {@link Charset}.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@link Charset#forName(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link Charset#forName(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalCharsetNameException Wenn {@link Charset#forName(String)} eine entsprechende Ausnahme auslöst.
	 * @throws UnsupportedCharsetException Wenn {@link Charset#forName(String)} eine entsprechende Ausnahme auslöst. */
	public GThis use(final String charsetName) throws NullPointerException, IllegalArgumentException, IllegalCharsetNameException, UnsupportedCharsetException {
		return this.use(Charset.forName(charsetName));
	}

	/** Diese Methode setzt den Wert auf {@link StandardCharsets#UTF_8} und gibt {@code this} zurück.
	 *
	 * @see #use(Object)
	 * @return {@code this}. */
	public GThis useUTF8() {
		return this.use(StandardCharsets.UTF_8);
	}

	/** Diese Methode setzt den Wert auf {@link StandardCharsets#UTF_16} und gibt {@code this} zurück.
	 *
	 * @see #use(Object)
	 * @return {@code this}. */
	public GThis useUTF16() {
		return this.use(StandardCharsets.UTF_16);
	}

	/** Diese Methode setzt den Wert auf {@link StandardCharsets#UTF_16BE} und gibt {@code this} zurück.
	 *
	 * @see #use(Object)
	 * @return {@code this}. */
	public GThis useUTF16BE() {
		return this.use(StandardCharsets.UTF_16BE);
	}

	/** Diese Methode setzt den Wert auf {@link StandardCharsets#UTF_16LE} und gibt {@code this} zurück.
	 *
	 * @see #use(Object)
	 * @return {@code this}. */
	public GThis useUTF16LE() {
		return this.use(StandardCharsets.UTF_16LE);
	}

	/** Diese Methode setzt den Wert auf {@link StandardCharsets#US_ASCII} und gibt {@code this} zurück.
	 *
	 * @see #use(Object)
	 * @return {@code this}. */
	public GThis useUSASCII() {
		return this.use(StandardCharsets.US_ASCII);
	}

	/** Diese Methode setzt den Wert auf {@link StandardCharsets#ISO_8859_1} und gibt {@code this} zurück.
	 *
	 * @see #use(Object)
	 * @return {@code this}. */
	public GThis useISO88591() {
		return this.use(StandardCharsets.ISO_8859_1);
	}

	/** Diese Methode setzt den Wert auf {@link Charset#defaultCharset()} und gibt {@code this} zurück.
	 *
	 * @see #use(Object)
	 * @return {@code this}. */
	public GThis useDEFAULT() {
		return this.use(Charset.defaultCharset());
	}

}