package bee.creative.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import bee.creative.util.Getter;
import bee.creative.util.Getters;
import bee.creative.util.Setter;

/** Diese Klasse stellt einige statische Methoden zur Verarbeitung von regulären Ausdrücken und Zeichenketten zur Verfügung.
 *
 * @see Pattern
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Strings {

	/** Diese Methode gibt die gegebenen Zeichenkette mit erhöhtem Einzug zurück. Dazu wird jedes Vorkommen von {@code "\n"} durch {@code "\n  "} ersetzt.
	 *
	 * @param value Zeichenkette.
	 * @return Zeichenkette mit erhöhtem Einzug. */
	public static String indent(final String value) {
		if (value == null) return "null";
		final var result = new StringBuilder();
		int last = -1, next = 0;
		final var size = value.length();
		while ((next = value.indexOf('\n', next)) >= 0) {
			result.append(value.substring(last + 1, last = next)).append("\n  ");
			next++;
		}
		return result.append(value.substring(last + 1, size)).toString();
	}

	/** Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte zurück. Der Rückgabewert entspricht
	 * {@code Strings.join("", items)}.
	 *
	 * @see Strings#join(String, Object...)
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static String join(final Object[] items) throws NullPointerException {
		return Strings.join("", items);
	}

	/** Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte mit dem gegebenen Trennzeichen zurück. Das
	 * Trennzeichen wird zwischen die {@link Object#toString() Textdarstelungen} aufeinanderfolgender Objekte platziert.
	 *
	 * @see Strings#join(String, Iterable)
	 * @param space Trennzeichen.
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn {@code space} bzw. {@code items} {@code null} ist. */
	public static String join(final String space, final Object... items) throws NullPointerException {
		return Strings.join(space, Arrays.asList(items));
	}

	/** Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte zurück. Der Rückgabewert entspricht
	 * {@code Strings.join("", items)}.
	 *
	 * @see Strings#join(String, Iterable)
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static String join(final Iterable<?> items) throws NullPointerException {
		return Strings.join("", items);
	}

	/** Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte mit dem gegebenen Trennzeichen zurück. Das
	 * Trennzeichen wird hierbei zwischen die {@link Object#toString() Textdarstelungen} aufeinanderfolgender Objekte platziert.
	 *
	 * @param space Trennzeichen.
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn {@code space} bzw. {@code items} {@code null} ist. */
	public static String join(final String space, final Iterable<?> items) throws NullPointerException {
		final StringBuilder result = new StringBuilder();
		Strings.join(result, space, items);
		return result.toString();
	}

	/** Diese Methode fügt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte mit dem gegebenen Trennzeichen an den gegebenen
	 * {@link StringBuilder} an. Das Trennzeichen wird hierbei zwischen die {@link Object#toString() Textdarstelungen} aufeinanderfolgender Objekte platziert.
	 *
	 * @param result Verkettungstext.
	 * @param space Trennzeichen.
	 * @param items Objekte.
	 * @throws NullPointerException Wenn {@code result}, {@code space} bzw. {@code items} {@code null} ist. */
	public static void join(final StringBuilder result, final String space, final Iterable<?> items) throws NullPointerException {
		final Iterator<?> iter = items.iterator();
		if (!iter.hasNext()) return;
		result.append(iter.next());
		while (iter.hasNext()) {
			result.append(space).append(iter.next());
		}
	}

	/** Diese Methode fügt die Verkettung der Textdarstelungen der gegebenen Elemente mit dem gegebenen Trennzeichen an den gegebenen {@link StringBuilder} an.
	 * Das Trennzeichen wird hierbei zwischen die Textdarstelungen aufeinanderfolgender Elemente platziert. Die Textdarstelung jedes Elements wird dazu über den
	 * gegebenen {@link Setter} an den gegebenen {@link StringBuilder} angefügt.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param result Verkettungstext.
	 * @param space Trennzeichen.
	 * @param items Elemente.
	 * @param printer Methode zum Anfügen der Textdarstelung eines Elements.
	 * @throws NullPointerException Wenn {@code result}, {@code space}, {@code items} bzw. {@code printer} {@code null} ist. */
	public static <GItem> void join(final StringBuilder result, final String space, final Iterable<GItem> items,
		final Setter<? super StringBuilder, ? super GItem> printer) throws NullPointerException {
		final Iterator<GItem> iter = items.iterator();
		if (!iter.hasNext()) return;
		printer.set(result, iter.next());
		while (iter.hasNext()) {
			printer.set(result.append(space), iter.next());
		}
	}

	/** Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Zeichenketten zurück, die vom regulären
	 * Ausdruck nicht getroffen wurden. Der Rückgabewert entspricht {@code Strings.split(regex, string, 0)}.
	 *
	 * @see Strings#split(String, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn {@code regex} bzw. {@code string} {@code null} ist. */
	public static List<String> split(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.apply(regex, string, 0, true, false);
	}

	/** Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Zeichenketten zurück, die von der
	 * {@code index}-ten Gruppen des regulären Ausdrucks nicht getroffen wurden. Das folgende Beispiel zeigt das kontextsensitive Spalten einer Zeichenkette am
	 * Komma hinter einer Ziffer: <pre>
	 * Strings.split("\\d(,)", "12,3x,56", 1); // [ "12", "3x,56" ]
	 * </pre>
	 *
	 * @see Strings#split(Pattern, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn {@code regex} bzw. {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code index < 0} ist. */
	public static List<String> split(final String regex, final CharSequence string, final int index) throws NullPointerException, IllegalArgumentException {
		return Strings.apply(regex, string, index, true, false);
	}

	/** Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Zeichenketten zurück, die vom
	 * regulären Ausdruck nicht getroffen wurden. Der Rückgabewert entspricht {@code Strings.split(pattern, string, 0)}.
	 *
	 * @see Strings#split(Pattern, CharSequence, int)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn {@code pattern} bzw. {@code string} {@code null} ist. */
	public static List<String> split(final Pattern pattern, final CharSequence string) throws NullPointerException {
		return Strings.apply(pattern, string, 0, true, false);
	}

	/** Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Zeichenketten zurück, die von
	 * der {@code index}-ten Gruppen des regulären Ausdrucks nicht getroffen wurden. Das folgende Beispiel zeigt das kontextsensitive Spalten einer Zeichenkette
	 * am Komma hinter einer Ziffer: <pre>
	 * Strings.split(Pattern.compile("\\d(,)"), "12,3x,56", 1); // [ "12", "3x,56" ]
	 * </pre>
	 *
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn {@code pattern} bzw. {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code index < 0} ist. */
	public static List<String> split(final Pattern pattern, final CharSequence string, final int index) throws NullPointerException, IllegalArgumentException {
		return Strings.apply(pattern, string, index, true, false);
	}

	/** Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Listen von Zeichenketten zurück, die von den
	 * Gruppen des regulären Ausdrucks nicht getroffen wurden. Für eine Guppe ohne Treffer wird dabei {@code null} in die Liste eingetragen. Das folgende Beispiel
	 * zeigt die Analyse einer Zeichenkette: <pre>
	 * Strings.splitAll("(\\d+)-(\\d+)?", "x12-3, x4-yz"); // [ [ "x", "x", "x12-" ], [ ", x", "-3, x" ], [ "yz", "-yz", ", x4-yz" ] ]
	 * </pre>
	 *
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Listen der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn {@code regex} bzw. {@code string} {@code null} ist. */
	public static List<List<String>> splitAll(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.applyAll(regex, string, true, false);
	}

	/** Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Listen von Zeichenketten
	 * zurück, die von den Gruppen des regulären Ausdrucks nicht getroffen wurden. Für eine Guppe ohne Treffer wird dabei {@code null} in die Liste eingetragen.
	 * Das folgende Beispiel zeigt die Analyse einer Zeichenkette: <pre>
	 * Strings.splitAll(Pattern.compile("(\\d+)-(\\d+)?"), "x12-3, x4-yz"); // [ [ "x", "x", "x12-" ], [ ", x", "-3, x" ], [ "yz", "-yz", ", x4-yz" ] ]
	 * </pre>
	 *
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Listen der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn {@code pattern} bzw. {@code string} {@code null} ist. */
	public static List<List<String>> splitAll(final Pattern pattern, final CharSequence string) throws NullPointerException {
		return Strings.applyAll(pattern, string, true, false);
	}

	/** Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Zeichenketten zurück, die vom regulären
	 * Ausdruck getroffen wurden. Der Rückgabewert entspricht {@code Strings.match(regex, string, 0)}.
	 *
	 * @see Strings#match(String, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn {@code regex} bzw. {@code string} {@code null} ist. */
	public static List<String> match(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.apply(regex, string, 0, false, true);
	}

	/** Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Zeichenketten zurück, die von der
	 * {@code index}-ten Gruppen des regulären Ausdrucks getroffen wurden. Das folgende Beispiel zeigt das kontextsensitive Extrahieren einer Zahl vor einem Euro:
	 * <pre>
	 * Strings.match("(\\d+)€", "..nur 12€!", 1); // [ "12" ]
	 * </pre>
	 *
	 * @see Strings#match(Pattern, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn {@code regex} bzw. {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code index < 0} ist. */
	public static List<String> match(final String regex, final CharSequence string, final int index) throws NullPointerException, IllegalArgumentException {
		return Strings.apply(regex, string, index, false, true);
	}

	/** Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Zeichenketten zurück, die vom regulären
	 * Ausdruck getroffen wurden. Der Rückgabewert entspricht: {@code Strings.match(pattern, string, 0)}
	 *
	 * @see Strings#match(Pattern, CharSequence, int)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn {@code pattern} bzw. {@code string} {@code null} ist. */
	public static List<String> match(final Pattern pattern, final CharSequence string) throws NullPointerException {
		return Strings.apply(pattern, string, 0, false, true);
	}

	/** Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Zeichenketten zurück, die von
	 * der {@code index}-ten Gruppen des regulären Ausdrucks getroffen wurden. Das folgende Beispiel zeigt das kontextsensitive Extrahieren einer Zahl vor einem
	 * Euro: <pre>
	 * Strings.match(Pattern.compile("(\\d+)€"), "..nur 12€!", 1); // [ "12" ]
	 * </pre>
	 *
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn {@code pattern} bzw. {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code index < 0} ist. */
	public static List<String> match(final Pattern pattern, final CharSequence string, final int index) throws NullPointerException, IllegalArgumentException {
		return Strings.apply(pattern, string, index, false, true);
	}

	/** Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Listen von Zeichenketten zurück, die von den
	 * Gruppen des regulären Ausdrucks getroffen wurden. Für eine Guppe ohne Treffer wird dabei {@code null} in die Liste eingetragen. Das folgende Beispiel zeigt
	 * die Analyse einer Zeichenkette: <pre>
	 * Strings.matchAll("(\\d+)-(\\d+)?", "x12-3, x4-yz"); // [ [ "x12-3", "12", "3"  ], [ "4-", "4", null ] ]
	 * </pre>
	 *
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Listen der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn {@code regex} bzw. {@code string} {@code null} ist. */
	public static List<List<String>> matchAll(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.applyAll(regex, string, false, true);
	}

	/** Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Listen von Zeichenketten
	 * zurück, die von den Gruppen des regulären Ausdrucks getroffen wurden. Für eine Guppe ohne Treffer wird dabei {@code null} in die Liste eingetragen. Das
	 * folgende Beispiel zeigt die Analyse einer Zeichenkette: <pre>
	 * Strings.matchAll(Pattern.compile("(\\d+)-(\\d+)?"), "x12-3, x4-yz"); // [ [ "x12-3", "12", "3"  ], [ "4-", "4", null ] ]
	 * </pre>
	 *
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Listen der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn {@code pattern} bzw. {@code string} {@code null} ist. */
	public static List<List<String>> matchAll(final Pattern pattern, final CharSequence string) throws NullPointerException {
		return Strings.applyAll(pattern, string, false, true);
	}

	/** Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Zeichenketten zurück, die vom regulären
	 * Ausdrucks getroffen bzw. nicht getroffenen wurden. Der Rückgabewert entspricht {@code Strings.splatch(regex, string, 0)}.
	 *
	 * @see Strings#splatch(String, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn {@code regex} bzw. {@code string} {@code null} ist. */
	public static List<String> splatch(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.apply(regex, string, 0, true, true);
	}

	/** Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Zeichenketten zurück, die von der
	 * {@code index}-ten Gruppen des regulären Ausdrucks getroffen bzw. nicht getroffenen wurden. Für eine Guppe ohne Treffer wird dabei {@code null} in die Liste
	 * eingetragen. Das folgende Beispiel zeigt die Analyse einer Zeichenkette: <pre>
	 * Strings.splatch("\\d(,)", "12,3x,56", 1); // [ "12", ",", "3x,56" ]
	 * </pre>
	 *
	 * @param regex regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn {@code regex} bzw. {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code index < 0} ist. */
	public static List<String> splatch(final String regex, final CharSequence string, final int index) throws NullPointerException, IllegalArgumentException {
		return Strings.apply(regex, string, index, true, true);
	}

	/** Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Zeichenketten zurück, die vom
	 * regulären Ausdrucks getroffen bzw. nicht getroffenen wurden. Der Rückgabewert entspricht: {@code Strings.splatch(pattern, string, 0)}.
	 *
	 * @see Strings#splatch(Pattern, CharSequence, int)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn {@code pattern} bzw. {@code string} {@code null} ist. */
	public static List<String> splatch(final Pattern pattern, final CharSequence string) throws NullPointerException {
		return Strings.apply(pattern, string, 0, true, true);
	}

	/** Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Zeichenketten zurück, die von
	 * der {@code index}-ten Gruppen des regulären Ausdrucks getroffen bzw. nicht getroffenen wurden. Für eine Guppe ohne Treffer wird dabei {@code null} in die
	 * Liste eingetragen. Das folgende Beispiel zeigt die Analyse einer Zeichenkette: <pre>
	 * Strings.splatch(Pattern.compile("\\d(,)"), "12,3x,56", 1); // [ "12", ",", "3x,56" ]
	 * </pre>
	 *
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn {@code pattern} bzw. {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code index < 0} ist. */
	public static List<String> splatch(final Pattern pattern, final CharSequence string, final int index) throws NullPointerException, IllegalArgumentException {
		return Strings.apply(pattern, string, index, true, true);
	}

	/** Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Listen von Zeichenketten zurück, die von den
	 * Gruppen des regulären Ausdrucks getroffen bzw. nicht getroffenen wurden. Für eine Guppe ohne Treffer wird dabei {@code null} in die Liste eingetragen. Das
	 * folgende Beispiel zeigt die Analyse einer Zeichenkette: <pre>
	 * Strings.splatchAll("(\\d+)-(\\d+)?", "x12-3, x4-yz"); // [ [ "x", "x", "x12-" ], [ "12-3", "12", "3" ], [ ", x", "-3, x" ], [ "4-", "4", null ], [ "yz", "-yz", ", x4-yz" ] ]
	 * </pre>
	 *
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Listen der Zeichenketten.
	 * @throws NullPointerException Wenn {@code regex} bzw. {@code string} {@code null} ist. */
	public static List<List<String>> splatchAll(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.applyAll(regex, string, true, true);
	}

	/** Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der Listen von Zeichenketten
	 * zurück, die von den Gruppen des regulären Ausdrucks getroffen bzw. nicht getroffenen wurden. Für eine Guppe ohne Treffer wird dabei {@code null} in die
	 * Liste eingetragen. Das folgende Beispiel zeigt die Analyse einer Zeichenkette: <pre>
	 * Strings.splatchAll(Pattern.compile("(\\d+)-(\\d+)?"), "x12-3, x4-yz"); // [ [ "x", "x", "x12-" ], [ "12-3", "12", "3" ], [ ", x", "-3, x" ], [ "4-", "4", null ], [ "yz", "-yz", ", x4-yz" ] ]
	 * </pre>
	 *
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Listen der Zeichenketten.
	 * @throws NullPointerException Wenn {@code pattern} bzw. {@code string} {@code null} ist. */
	public static List<List<String>> splatchAll(final Pattern pattern, final CharSequence string) throws NullPointerException {
		return Strings.applyAll(pattern, string, true, true);
	}

	/** Diese Methode gibt den Abschnitt der gegebenen Zeichenkette nach dem ersten Vorkommen des gegebenen Trennzeichens zurück. Wenn das Trennzeichen nicht
	 * gefunden wird, wird die gegebene Zeichenkette geliefert.
	 *
	 * @param string Zeichenkette.
	 * @param token Trennzeichen.
	 * @return Abschnitt oder Zeichenkette. */
	public static String substringAfterFirst(final String string, final char token) {
		final int index = string.indexOf(token);
		return index < 0 ? string : string.substring(index + 1);
	}

	/** Diese Methode gibt den Abschnitt der gegebenen Zeichenkette nach dem letzten Vorkommen des gegebenen Trennzeichens zurück. Wenn das Trennzeichen nicht
	 * gefunden wird, wird die gegebene Zeichenkette geliefert.
	 *
	 * @param string Zeichenkette.
	 * @param token Trennzeichen.
	 * @return Abschnitt oder Zeichenkette. */
	public static String substringAfterLast(final String string, final char token) {
		final int index = string.lastIndexOf(token);
		return index < 0 ? string : string.substring(index + 1);
	}

	/** Diese Methode gibt den Abschnitt der gegebenen Zeichenkette vor dem ersten Vorkommen des gegebenen Trennzeichens zurück. Wenn das Trennzeichen nicht
	 * gefunden wird, wird die gegebene Zeichenkette geliefert.
	 *
	 * @param string Zeichenkette.
	 * @param token Trennzeichen.
	 * @return Abschnitt oder Zeichenkette. */
	public static String substringBeforeFirst(final String string, final char token) {
		final int index = string.indexOf(token);
		return index < 0 ? string : string.substring(0, index);
	}

	/** Diese Methode gibt den Abschnitt der gegebenen Zeichenkette vor dem letzten Vorkommen des gegebenen Trennzeichens zurück. Wenn das Trennzeichen nicht
	 * gefunden wird, wird die gegebene Zeichenkette geliefert.
	 *
	 * @param string Zeichenkette.
	 * @param token Trennzeichen.
	 * @return Abschnitt oder Zeichenkette. */
	public static String substringBeforeLast(final String string, final char token) {
		final int index = string.lastIndexOf(token);
		return index < 0 ? string : string.substring(0, index);
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der seine Eingabe über {@link Pattern#compile(String, int)} in einen kompilierten regulären Ausdruck
	 * umwandelt.
	 *
	 * @see Pattern#compile(String, int)
	 * @param flags Flags ({@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL}, {@link Pattern#UNICODE_CASE},
	 *        {@link Pattern#CANON_EQ}, {@link Pattern#UNIX_LINES}, {@link Pattern#LITERAL}, {@link Pattern#COMMENTS})
	 * @return {@link Pattern}-Compiler. */
	public static Getter<String, Pattern> patternCompiler(final int flags) {
		return new PatternCompiler(flags);
	}

	/** Diese Methode ist eine Abkürzung für {@code parseSequence(string, maskSymbol, maskSymbol, maskSymbol)}.
	 *
	 * @param string Zeichenkette.
	 * @param maskSymbol Maskierungszeichen.
	 * @return geparste Zeichenkette oder {@code null}.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public static String parseSequence(final CharSequence string, final char maskSymbol) throws NullPointerException {
		return Strings.parseSequence(string, maskSymbol, maskSymbol, maskSymbol);
	}

	/** Diese Methode parst die gegebene Zeichenkette mit den gegebenen Symbolen und gibt die geparste Zeichenkette zurück. Sie realisiert dazu die
	 * Umkehroperation zu {@link #printSequence(CharSequence, char, char, char)} und liefert {@code null}, wenn das Format ungültig ist. Das Parsen erwartet, dass
	 * die gegebene Zeichenkette mit {@code openSymbol} beginnt, mit {@code closeSymbol} endet und dass vor allen Vorkommen von {@code openSymbol},
	 * {@code maskSymbol} und {@code closeSymbol} zwischen dem ersten und letzten Symbol der Zeichenkette ein {@code maskSymbol} steht.
	 *
	 * @see #parseSequence(CharSequence, char, char, char)
	 * @param string Zeichenkette.
	 * @param openSymbol Erstes Symbol der gegebenen Zeichenkette.
	 * @param maskSymbol Symbol vor jedem Vorkommen von {@code maskSymbol} und {@code closeSymbol} zwischen dem erste und letzten Symbol der gegebenen
	 *        Zeichenkette.
	 * @param closeSymbol Letztes Symbol der gegebenen Zeichenkette.
	 * @return geparste Zeichenkette oder {@code null}.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public static String parseSequence(final CharSequence string, final char openSymbol, final char maskSymbol, final char closeSymbol)
		throws NullPointerException {
		final int length = string.length();
		if ((length < 2) || (string.charAt(0) != openSymbol)) return null;
		final char[] result = new char[length - 2];
		int index = 1, offset = 0;
		while (index < length) {
			char symbol = string.charAt(index++);
			if (symbol == maskSymbol) {
				if (index == length) return maskSymbol == closeSymbol ? new String(result, 0, offset) : null;
				symbol = string.charAt(index++);
				if ((symbol != openSymbol) && (symbol != maskSymbol) && (symbol != closeSymbol)) return null;
				result[offset++] = symbol;
			} else if ((symbol == openSymbol) && (openSymbol != closeSymbol)) {
				break;
			} else if (symbol != closeSymbol) {
				result[offset++] = symbol;
			} else return index == length ? new String(result, 0, offset) : null;
		}
		return null;
	}

	/** Diese Methode ist eine Abkürzung für {@link #printSequence(CharSequence, char, char, char) printSequence(string, maskSymbol, maskSymbol, maskSymbol)}.
	 *
	 * @param string Zeichenkette.
	 * @param maskSymbol Maskierungszeichen.
	 * @return formatierte Zeichenkette.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public static String printSequence(final CharSequence string, final char maskSymbol) throws NullPointerException {
		return Strings.printSequence(string, maskSymbol, maskSymbol, maskSymbol);
	}

	/** Diese Methode formatiert die gegebene Zeichenkette mit den gegebenen Symbolen und gibt die formatierte Zeichenkette zurück. Beim Formatieren werden das
	 * {@code openSymbol} vorn und das {@code closeSymbol} hinten an die Zeichenkette angefügt sowie allen Vorkommen von {@code openSymbol}, {@code maskSymbol}
	 * und {@code closeSymbol} ein {@code maskSymbol} vorangestellt.
	 *
	 * @see #parseSequence(CharSequence, char, char, char)
	 * @param string Zeichenkette.
	 * @param openSymbol Erstes Symbol der formatierten Zeichenkette.
	 * @param maskSymbol Symbol vor jedem Vorkommen von {@code maskSymbol} und {@code closeSymbol} innerhalb der gegebenen Zeichenkette.
	 * @param closeSymbol Letztes Symbol der formatierten Zeichenkette.
	 * @return formatierte Zeichenkette.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public static String printSequence(final CharSequence string, final char openSymbol, final char maskSymbol, final char closeSymbol)
		throws NullPointerException {
		final int length = string.length();
		int offset = length + 2;
		for (int i = length; i != 0;) {
			final char symbol = string.charAt(--i);
			if ((symbol == openSymbol) || (symbol == maskSymbol) || (symbol == closeSymbol)) {
				++offset;
			}
		}
		final char[] result = new char[offset];
		result[--offset] = closeSymbol;
		for (int i = length; i != 0;) {
			final char symbol = string.charAt(--i);
			result[--offset] = symbol;
			if ((symbol == openSymbol) || (symbol == maskSymbol) || (symbol == closeSymbol)) {
				result[--offset] = maskSymbol;
			}
		}
		result[--offset] = openSymbol;
		return new String(result);
	}

	/** Diese Methode liefert ein Objekt, dessen {@link Object#toString() Textdarstellung} über {@link String#format(String, Object...) String.format(format,
	 * args)} ermittelt wird. */
	public static Object formatFuture(final String format, final Object... args) {
		return new FormatFuture(format, args);
	}

	private static final class FormatFuture {

		final String format;

		final Object[] args;

		FormatFuture(final String format, final Object[] args) {
			this.format = Objects.notNull(format);
			this.args = args;
		}

		@Override
		public String toString() {
			return String.format(this.format, this.args);
		}

	}

	private static class PatternCompiler implements Getter<String, Pattern> {

		final int flags;

		PatternCompiler(final int flags) {
			this.flags = flags;
		}

		@Override
		public Pattern get(final String input) {
			return Pattern.compile(input, this.flags);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.flags);
		}

	}

	/** Dieses Feld speichert einen synchronisierten, gepufferten {@link #patternCompiler(int)} mit Flag {@code 0}. Dieser wird von den Methoden in
	 * {@link #Strings()} genutzt, die einen regulärer Ausdruck kompilieren müssen. */
	public static final Getter<String, Pattern> PATTERN_COMPILER = Getters.synchronize(Getters.buffer(Strings.patternCompiler(0)));

	/** Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt eine Liste von Zeichenketten zurück. Mit den beiden
	 * Schaltern kann dazu entschieden werden, ob die von der {@code index} -ten Gruppen des regulären Ausdrucks getroffenen bzw. nicht getroffenen Zeichenkette
	 * in diese Liste eingetragen werden sollen.
	 *
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @param index Index.
	 * @param split {@code true}, wenn die nicht getroffenen Zeichenkette eingetragen werden sollen.
	 * @param match {@code true}, wenn die getroffen getroffenen Zeichenkette eingetragen werden sollen.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn {@code regex} bzw. {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code index < 0} ist. */
	static List<String> apply(final String regex, final CharSequence string, final int index, final boolean split, final boolean match)
		throws NullPointerException, IllegalArgumentException {
		if (index < 0) throw new IllegalArgumentException("index < 0");
		return Strings.apply(Strings.PATTERN_COMPILER.get(regex), string, index, split, match);
	}

	/** Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt eine Liste von Zeichenketten zurück. Mit den
	 * beiden Schaltern kann dazu entschieden werden, ob die von der {@code index} -ten Gruppen des regulären Ausdrucks getroffenen bzw. nicht getroffenen
	 * Zeichenkette in diese Liste eingetragen werden sollen.
	 *
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @param index Index.
	 * @param split {@code true}, wenn die nicht getroffenen Zeichenkette eingetragen werden sollen.
	 * @param match {@code true}, wenn die getroffen getroffenen Zeichenkette eingetragen werden sollen.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn {@code pattern} bzw. {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code index < 0} ist. */
	static List<String> apply(final Pattern pattern, final CharSequence string, final int index, final boolean split, final boolean match)
		throws NullPointerException, IllegalArgumentException {
		if (index < 0) throw new IllegalArgumentException("index < 0");
		final Matcher matcher = pattern.matcher(string);
		if (index > matcher.groupCount()) throw new IllegalArgumentException();
		final List<String> result = new ArrayList<>();
		int cursor = 0;
		while (matcher.find()) {
			final int from = matcher.start(index);
			if (from >= 0) {
				final int last = matcher.end(index);
				if (split) {
					result.add(string.subSequence(cursor, from).toString());
					cursor = last;
				}
				if (match) {
					result.add(string.subSequence(from, last).toString());
				}
			}
		}
		if (split) {
			result.add(string.subSequence(cursor, string.length()).toString());
		}
		return result;
	}

	/** Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt eine Liste von Listen von Zeichenketten zurück. Mit den
	 * beiden Schaltern kann dazu entschieden werden, ob die Listen der von den Gruppen des regulären Ausdrucks getroffenen bzw. der nicht getroffenen
	 * Zeichenketten in die Ergebnisliste eingetragen werden sollen.
	 *
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @param split {@code true}, wenn die nicht getroffenen Zeichenkette eingetragen werden sollen.
	 * @param match {@code true}, wenn die getroffen getroffenen Zeichenkette eingetragen werden sollen.
	 * @return Liste der Listen der Zeichenketten.
	 * @throws NullPointerException Wenn {@code regex} bzw. {@code string} {@code null} ist. */
	static List<List<String>> applyAll(final String regex, final CharSequence string, final boolean split, final boolean match) throws NullPointerException {
		return Strings.applyAll(Strings.PATTERN_COMPILER.get(regex), string, split, match);
	}

	/** Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt eine Liste von Listen von Zeichenketten
	 * zurück. Mit den beiden Schaltern kann dazu entschieden werden, ob die Listen der von den Gruppen des regulären Ausdrucks getroffenen bzw. nicht getroffenen
	 * Zeichenketten in die Ergebnisliste eingetragen werden sollen.
	 *
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @param split {@code true}, wenn die nicht getroffenen Zeichenkette eingetragen werden sollen.
	 * @param match {@code true}, wenn die getroffen getroffenen Zeichenkette eingetragen werden sollen.
	 * @return Liste der Listen der Zeichenketten.
	 * @throws NullPointerException Wenn {@code pattern} bzw. {@code string} {@code null} ist. */
	static List<List<String>> applyAll(final Pattern pattern, final CharSequence string, final boolean split, final boolean match) throws NullPointerException {
		final Matcher matcher = pattern.matcher(string);
		final int count = matcher.groupCount() + 1;
		final List<List<String>> result = new ArrayList<>();
		final int[] cursorList = new int[count];
		while (matcher.find()) {
			final List<String> spliList = (split ? new ArrayList<>() : null);
			final List<String> matchList = (match ? new ArrayList<>() : null);
			for (int i = 0; i < count; i++) {
				final int from = matcher.start(i);
				if (from >= 0) {
					final int last = matcher.end(i);
					if (split) {
						spliList.add(string.subSequence(cursorList[i], from).toString());
						cursorList[i] = last;
					}
					if (match) {
						matchList.add(string.subSequence(from, last).toString());
					}
				} else if (match) {
					matchList.add(null);
				}
			}
			if (split) {
				result.add(spliList);
			}
			if (match) {
				result.add(matchList);
			}
		}
		if (split) {
			final List<String> splitList = new ArrayList<>();
			final int last = string.length();
			for (int i = 0; i < count; i++) {
				splitList.add(string.subSequence(cursorList[i], last).toString());
			}
			result.add(splitList);
		}
		return result;
	}

}