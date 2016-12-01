package bee.creative.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Diese Klasse stellt einige statische Methoden bzur Verarbeitung von regulären Ausdrücken mit Zeichenketten zur Verfügung.
 *
 * @see Pattern
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Strings {

	/** Dieses Feld speichert die Zehnerstelle der ersten 100 positiven Dezimanzahlen. */
	final static char[] _digitTenArray_ = {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2',
		'2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5',
		'5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8',
		'8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'};

	/** Dieses Feld speichert die Einerstelle der ersten 100 positiven Dezimanzahlen. */
	final static char[] _digitOneArray_ = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3',
		'4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4',
		'5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
		'6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

	/** Dieses Feld speichert einen synchronisierten, gepufferten {@link #patternCompiler(int)} mit Flag {@code 0}. Dieser wird von den Methoden in
	 * {@link #Strings()} genutzt, die einen regulärer Ausdruck kompilieren müssen. */
	public static final Converter<String, Pattern> PATTERN_COMPILER = Converters.synchronizedConverter(Converters.bufferedConverter(Strings.patternCompiler(0)));
	{}

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
	static List<String> _apply_(final String regex, final CharSequence string, final int index, final boolean split, final boolean match)
		throws NullPointerException, IllegalArgumentException {
		if (regex == null) throw new NullPointerException("regex = null");
		if (string == null) throw new NullPointerException("string = null");
		if (index < 0) throw new IllegalArgumentException("index < 0");
		return Strings._apply_(Strings.PATTERN_COMPILER.convert(regex), string, index, split, match);
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
	static List<String> _apply_(final Pattern pattern, final CharSequence string, final int index, final boolean split, final boolean match)
		throws NullPointerException, IllegalArgumentException {
		if (pattern == null) throw new NullPointerException("pattern = null");
		if (string == null) throw new NullPointerException("string = null");
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
	static List<List<String>> _applyAll_(final String regex, final CharSequence string, final boolean split, final boolean match) throws NullPointerException {
		if (regex == null) throw new NullPointerException("regex = null");
		if (string == null) throw new NullPointerException("string = null");
		return Strings._applyAll_(Strings.PATTERN_COMPILER.convert(regex), string, split, match);
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
	static List<List<String>> _applyAll_(final Pattern pattern, final CharSequence string, final boolean split, final boolean match) throws NullPointerException {
		if (pattern == null) throw new NullPointerException("pattern = null");
		if (string == null) throw new NullPointerException("string = null");
		final Matcher matcher = pattern.matcher(string);
		final int count = matcher.groupCount() + 1;
		final List<List<String>> result = new ArrayList<>();
		final int[] cursorList = new int[count];
		while (matcher.find()) {
			final List<String> spliList = (split ? new ArrayList<String>() : null);
			final List<String> matchList = (match ? new ArrayList<String>() : null);
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

	/** Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte zurück. Der Rückgabewert entspricht
	 * {@code Strings.join("", items)}.
	 *
	 * @see Strings#join(String, Object...)
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static String join(final Object... items) throws NullPointerException {
		if (items == null) throw new NullPointerException("items = null");
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
		if (space == null) throw new NullPointerException("space = null");
		if (items == null) throw new NullPointerException("items = null");
		return Strings.join(space, Arrays.asList(items));
	}

	/** Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte zurück. Der Rückgabewert entspricht
	 * {@code Strings.join("", items)}.
	 *
	 * @see Strings#join(String, Iterable)
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static String join(final Iterable<?> items) {
		if (items == null) throw new NullPointerException("items = null");
		return Strings.join("", items);
	}

	/** Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte mit dem gegebenen Trennzeichen zurück. Das
	 * Trennzeichen wird hierbei zwischen die {@link Object#toString() Textdarstelungen} aufeinanderfolgender Objekte platziert.
	 *
	 * @param space Trennzeichen.
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn {@code space} bzw. {@code items} {@code null} ist. */
	public static String join(final String space, final Iterable<?> items) {
		if (space == null) throw new NullPointerException("space = null");
		if (items == null) throw new NullPointerException("items = null");
		final StringBuilder builder = new StringBuilder();
		if (!space.isEmpty()) {
			String join = "";
			for (final Object item: items) {
				builder.append(join).append(item);
				join = space;
			}
		} else {
			for (final Object item: items) {
				builder.append(item);
			}
		}
		return builder.toString();
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
		return Strings._apply_(regex, string, 0, true, false);
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
		return Strings._apply_(regex, string, index, true, false);
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
		return Strings._apply_(pattern, string, 0, true, false);
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
		return Strings._apply_(pattern, string, index, true, false);
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
		return Strings._applyAll_(regex, string, true, false);
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
		return Strings._applyAll_(pattern, string, true, false);
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
		return Strings._apply_(regex, string, 0, false, true);
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
		return Strings._apply_(regex, string, index, false, true);
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
		return Strings._apply_(pattern, string, 0, false, true);
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
		return Strings._apply_(pattern, string, index, false, true);
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
		return Strings._applyAll_(regex, string, false, true);
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
		return Strings._applyAll_(pattern, string, false, true);
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
		return Strings._apply_(regex, string, 0, true, true);
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
		return Strings._apply_(regex, string, index, true, true);
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
		return Strings._apply_(pattern, string, 0, true, true);
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
		return Strings._apply_(pattern, string, index, true, true);
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
		return Strings._applyAll_(regex, string, true, true);
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
		return Strings._applyAll_(pattern, string, true, true);
	}

	/** Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe via {@link Pattern#compile(String, int)} in einen kompilierten regulären Ausdruck
	 * umwandelt.
	 *
	 * @see Pattern#compile(String, int)
	 * @param flags Flags ({@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL}, {@link Pattern#UNICODE_CASE},
	 *        {@link Pattern#CANON_EQ}, {@link Pattern#UNIX_LINES}, {@link Pattern#LITERAL}, {@link Pattern#COMMENTS})
	 * @return {@link Pattern}-Compiler. */
	public static Converter<String, Pattern> patternCompiler(final int flags) {
		return new Converter<String, Pattern>() {

			@Override
			public Pattern convert(final String input) {
				return Pattern.compile(input, flags);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("patternCompiler", flags);
			}

		};
	}

	/** Diese Methode schreibt die Zeichenkette der gegebene positiven Dezimalzahl vor der gegebenen Position in den gegebenen Puffer.
	 *
	 * @param value positiven Dezimalzahl.
	 * @param offset Position des ersten Zeichens hinter der geschriebenen Dezimalzahl.
	 * @param buffer Puffer für die Zeichenkette. */
	public static void toString(int value, int offset, final char[] buffer) {
		int div, mod;
		while (true) {
			div = value / 100;
			mod = value % 100;
			if (div != 0) {
				buffer[--offset] = Strings._digitOneArray_[mod];
				buffer[--offset] = Strings._digitTenArray_[mod];
				value = div;
			} else {
				buffer[--offset] = Strings._digitOneArray_[mod];
				if (mod < 10) return;
				buffer[--offset] = Strings._digitTenArray_[mod];
				return;
			}
		}
	}

	/** Diese Methode schreibt die Zeichenkette der gegebene positiven Dezimalzahl mit der gegebenen Länge um führenden Nullen ergänzt an die gegebenen Position
	 * in den gegebenen Puffer.
	 *
	 * @param value positiven Dezimalzahl.
	 * @param offset Position des ersten zu schreibenden Zeichens.
	 * @param length Anzahl der zu schreibenden Zeichen, welche mindestend der für die gegebene Dezimalzahl neötigten Anzahl an Zeichen entsprechen muss.
	 * @param buffer Puffer für die Zeichenkette. */
	public static void toString(final int value, final int offset, final int length, final char[] buffer) {
		int fill = length - Strings.stringSize(value);
		while (--fill >= 0) {
			buffer[offset + fill] = '0';
		}
		Strings.toString(value, offset + length, buffer);
	}

	/** Diese Methode schreibt die Zeichenkette der gegebene positiven Dezimalzahl vor der gegebenen Position in den gegebenen Puffer.
	 *
	 * @param value positiven Dezimalzahl.
	 * @param offset Position des ersten Zeichens hinter der geschriebenen Dezimalzahl.
	 * @param buffer Puffer für die Zeichenkette. */
	public static void toString(long value, int offset, final char[] buffer) {
		long div, mod;
		while (true) {
			div = value / 1000000000;
			mod = value % 1000000000;
			if (div != 0) {
				Strings.toString((int)mod, offset, 9, buffer);
				value = div;
				offset -= 9;
			} else {
				Strings.toString((int)mod, offset, buffer);
				return;
			}
		}
	}

	/** Diese Methode schreibt die Zeichenkette der gegebene positiven Dezimalzahl mit der gegebenen Länge um führenden Nullen ergänzt an die gegebenen Position
	 * in den gegebenen Puffer.
	 *
	 * @param value positiven Dezimalzahl.
	 * @param offset Position des ersten zu schreibenden Zeichens.
	 * @param length Anzahl der zu schreibenden Zeichen, welche mindestend der für die gegebene Dezimalzahl neötigten Anzahl an Zeichen entsprechen muss.
	 * @param buffer Puffer für die Zeichenkette. */
	public static void toString(final long value, final int offset, final int length, final char[] buffer) {
		int fill = length - Strings.stringSize(value);
		while (--fill >= 0) {
			buffer[offset + fill] = '0';
		}
		Strings.toString(value, offset + length, buffer);
	}

	/** Diese Methode gibt die Anzahl an Zeichen zurück, die zur Darstellung der gegebenen positiven Dezimalzahl nötig sind.
	 *
	 * @param value positive Dezimalzahl.
	 * @return Zeichenanzahl. */
	public static int stringSize(final int value) {
		return (value > 99999 //
			? (value > 9999999 //
				? (value > 999999999//
					? 10 //
					: (value > 99999999 ? 9 : 8)) //
				: (value > 999999 ? 7 : 6)) //
			: (value > 99 //
				? (value > 9999 //
					? 5 //
					: (value > 999 ? 4 : 3)) //
				: (value > 9 ? 2 : 1)));
	}

	/** Diese Methode gibt die Anzahl an Zeichen zurück, die zur Darstellung der gegebenen positiven Dezimalzahl nötig sind.
	 *
	 * @param value positive Dezimalzahl.
	 * @return Zeichenanzahl. */
	public static int stringSize(final long value) {
		final long div = value / 1000000000, mod = value % 1000000000;
		return div != 0 ? Strings.stringSize(div) + 9 : Strings.stringSize((int)mod);
	}

}