package bee.creative.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse stellt einige statische Methoden zur Verarbeitung von regulären Ausdrücken mit Zeichenketten zur
 * Verfügung.
 * 
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Strings {

	/**
	 * Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte zurück. Der
	 * Rückgabewert entspricht:
	 * 
	 * <pre>Strings.join(&quot;&quot;, items)</pre>
	 * 
	 * @see Strings#join(String, Object...)
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn das gegebenen {@link Array Array} <code>null</code> ist.
	 */
	public static String join(final Object... items) throws NullPointerException {
		if(items == null) throw new NullPointerException();
		return Strings.join("", items);
	}

	/**
	 * Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte mit dem
	 * gegebenen Trennzeichen zurück. Das Trennzeichen wird zwischen die {@link Object#toString() Textdarstelungen}
	 * aufeinanderfolgender Objekte platziert.
	 * 
	 * @see Strings#join(String, Iterable)
	 * @param space Trennzeichen.
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn das gegebenen {@link Array Array} bzw. das gegebene Trennzeichen
	 *         <code>null</code> ist.
	 */
	public static String join(final String space, final Object... items) throws NullPointerException {
		if((space == null) || (items == null)) throw new NullPointerException();
		return Strings.join(space, Arrays.asList(items));
	}

	/**
	 * Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte zurück. Der
	 * Rückgabewert entspricht:
	 * 
	 * <pre>Strings.join(&quot;&quot;, items)</pre>
	 * 
	 * @see Strings#join(String, Iterable)
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable Iterable} <code>null</code> ist.
	 */
	public static String join(final Iterable<?> items) {
		if(items == null) throw new NullPointerException();
		return Strings.join("", items);
	}

	/**
	 * Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte mit dem
	 * gegebenen Trennzeichen zurück. Das Trennzeichen wird hierbei zwischen die {@link Object#toString()
	 * Textdarstelungen} aufeinanderfolgender Objekte platziert.
	 * 
	 * @param space Trennzeichen.
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable Iterable} bzw. das gegebene Trennzeichen
	 *         <code>null</code> ist.
	 */
	public static String join(final String space, final Iterable<?> items) {
		if((space == null) || (items == null)) throw new NullPointerException();
		final StringBuilder builder = new StringBuilder();
		if(!space.isEmpty()){
			String join = "";
			for(final Object item: items){
				builder.append(join).append(item);
				join = space;
			}
		}else{
			for(final Object item: items){
				builder.append(item);
			}
		}
		return builder.toString();
	}

	static List<String> splatch(final String regex, final CharSequence string, final int index, final boolean split,
		final boolean match) throws NullPointerException, IllegalArgumentException {
		if((regex == null) || (string == null)) throw new NullPointerException();
		if(index < 0) throw new IllegalArgumentException();
		return Strings.splatch(Pattern.compile(regex), string, index, split, match);
	}

	static List<String> splatch(final Pattern pattern, final CharSequence string, final int index, final boolean split,
		final boolean match) throws NullPointerException, IllegalArgumentException {
		if((pattern == null) || (string == null)) throw new NullPointerException();
		if(index < 0) throw new IllegalArgumentException();
		final Matcher matcher = pattern.matcher(string);
		if(index > matcher.groupCount()) throw new IllegalArgumentException();
		final List<String> stringList = new ArrayList<String>();
		int cursor = 0;
		while(matcher.find()){
			final int from = matcher.start(index);
			if(from >= 0){
				final int last = matcher.end(index);
				if(split){
					stringList.add(string.subSequence(cursor, from).toString());
					cursor = last;
				}
				if(match){
					stringList.add(string.subSequence(from, last).toString());
				}
			}
		}
		if(split){
			stringList.add(string.subSequence(cursor, string.length()).toString());
		}
		return stringList;
	}

	static List<List<String>> splatches(final String regex, final CharSequence string, final boolean split,
		final boolean match) throws NullPointerException, IllegalArgumentException {
		if((regex == null) || (string == null)) throw new NullPointerException();
		return Strings.splatches(Pattern.compile(regex), string, split, match);
	}

	static List<List<String>> splatches(final Pattern pattern, final CharSequence string, final boolean split,
		final boolean match) throws NullPointerException, IllegalArgumentException {
		if((pattern == null) || (string == null)) throw new NullPointerException();
		final Matcher matcher = pattern.matcher(string);
		final int count = matcher.groupCount() + 1;
		final List<List<String>> stringListList = new ArrayList<List<String>>();
		final int[] cursorList = new int[count];
		while(matcher.find()){
			final List<String> spliList = (split ? new ArrayList<String>() : null);
			final List<String> matchList = (match ? new ArrayList<String>() : null);
			for(int i = 0; i < count; i++){
				final int from = matcher.start(i);
				if(from >= 0){
					final int last = matcher.end(i);
					if(split){
						spliList.add(string.subSequence(cursorList[i], from).toString());
						cursorList[i] = last;
					}
					if(match){
						matchList.add(string.subSequence(from, last).toString());
					}
				}else if(match){
					matchList.add(null);
				}
			}
			if(split){
				stringListList.add(spliList);
			}
			if(match){
				stringListList.add(matchList);
			}
		}
		if(split){
			final List<String> splitList = new ArrayList<String>();
			final int last = string.length();
			for(int i = 0; i < count; i++){
				splitList.add(string.subSequence(cursorList[i], last).toString());
			}
			stringListList.add(splitList);
		}
		return stringListList;
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Zeichenketten zurück, die vom regulären Ausdruck nicht getroffen wurden. Der Rückgabewert entspricht:
	 * 
	 * <pre>Strings.split(regex, string, 0);</pre>
	 * 
	 * @see Strings#split(String, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette <code>null</code>
	 *         ist.
	 */
	public static List<String> split(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.splatch(regex, string, 0, true, false);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Zeichenketten zurück, die von der <code>index</code>-ten Gruppen des regulären Ausdrucks nicht getroffen wurden.
	 * Das folgende Beispiel zeigt das kontextsensitive Spalten einer Zeichenkette am Komma hinter einer Ziffer:
	 * 
	 * <pre>Strings.split(&quot;\\d(,)&quot;, &quot;12,3x,56&quot;, 1); // [ &quot;12&quot;, &quot;3x,56&quot; ]</pre>
	 * 
	 * @see Strings#split(Pattern, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette <code>null</code>
	 *         ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static List<String> split(final String regex, final CharSequence string, final int index)
		throws NullPointerException, IllegalArgumentException {
		return Strings.splatch(regex, string, index, true, false);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die
	 * Liste der Zeichenketten zurück, die vom regulären Ausdruck nicht getroffen wurden. Der Rückgabewert entspricht:
	 * 
	 * <pre>Strings.split(pattern, string, 0);</pre>
	 * 
	 * @see Strings#split(Pattern, CharSequence, int)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         <code>null</code> ist.
	 */
	public static List<String> split(final Pattern pattern, final CharSequence string) throws NullPointerException {
		return Strings.splatch(pattern, string, 0, true, false);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die
	 * Liste der Zeichenketten zurück, die von der <code>index</code>-ten Gruppen des regulären Ausdrucks nicht getroffen
	 * wurden. Das folgende Beispiel zeigt das kontextsensitive Spalten einer Zeichenkette am Komma hinter einer Ziffer:
	 * 
	 * <pre>Strings.split(Pattern.compile(&quot;\\d(,)&quot;), &quot;12,3x,56&quot;, 1); // [ &quot;12&quot;, &quot;3x,56&quot; ]</pre>
	 * 
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         <code>null</code> ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static List<String> split(final Pattern pattern, final CharSequence string, final int index)
		throws NullPointerException, IllegalArgumentException {
		return Strings.splatch(pattern, string, index, true, false);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Zeichenketten zurück, die vom regulären Ausdruck getroffen wurden. Der Rückgabewert entspricht:
	 * 
	 * <pre>Strings.match(regex, string, 0)</pre>
	 * 
	 * @see Strings#match(String, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette <code>null</code>
	 *         ist.
	 */
	public static List<String> match(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.splatch(regex, string, 0, false, true);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Zeichenketten zurück, die von der <code>index</code>-ten Gruppen des regulären Ausdrucks getroffen wurden. Das
	 * folgende Beispiel zeigt das kontextsensitive Extrahieren einer Zahl vor einem Euro:
	 * 
	 * <pre>Strings.match(&quot;(\\d+)€&quot;, &quot;..nur 12€!&quot;, 1); // [ &quot;12&quot; ]</pre>
	 * 
	 * @see Strings#match(Pattern, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette <code>null</code>
	 *         ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static List<String> match(final String regex, final CharSequence string, final int index)
		throws NullPointerException, IllegalArgumentException {
		return Strings.splatch(regex, string, index, false, true);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Zeichenketten zurück, die vom regulären Ausdruck getroffen wurden. Der Rückgabewert entspricht:
	 * 
	 * <pre> Strings.match(regex, string, 0) </pre>
	 * 
	 * @see Strings#match(String, CharSequence, int)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         <code>null</code> ist.
	 */
	public static List<String> match(final Pattern pattern, final CharSequence string) throws NullPointerException {
		return Strings.splatch(pattern, string, 0, false, true);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die
	 * Liste der Zeichenketten zurück, die von der <code>index</code>-ten Gruppen des regulären Ausdrucks getroffen
	 * wurden. Das folgende Beispiel zeigt das kontextsensitive Extrahieren einer Zahl vor einem Euro:
	 * 
	 * <pre>Strings.match(Pattern.compile(&quot;(\\d+)€&quot;), &quot;..nur 12€!&quot;, 1); // [ &quot;12&quot; ]</pre>
	 * 
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         <code>null</code> ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static List<String> match(final Pattern pattern, final CharSequence string, final int index)
		throws NullPointerException, IllegalArgumentException {
		return Strings.splatch(pattern, string, index, false, true);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Listen von Zeichenketten zurück, die von den Gruppen des regulären Ausdrucks getroffen wurden. Für eine Guppe ohne
	 * Treffer wird dabei <code>null</code> in die Liste eingetragen. Das folgende Beispiel zeigt die Analyse einer
	 * Zeichenkette:
	 * 
	 * <pre>Strings.matches(&quot;(\\d+)-(\\d+)?&quot;, &quot;x12-3, x4-yz&quot;); // [ [ &quot;x12-3&quot;, &quot;12&quot;, &quot;3&quot;  ], [ &quot;4-&quot;, &quot;4&quot;, null ] ]</pre>
	 * 
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Gruppentexte.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette <code>null</code>
	 *         ist.
	 */
	public static List<List<String>> matches(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.splatches(regex, string, false, true);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die
	 * Liste der Listen von Zeichenketten zurück, die von den Gruppen des regulären Ausdrucks getroffen wurden. Für eine
	 * Guppe ohne Treffer wird dabei <code>null</code> in die Liste eingetragen. Das folgende Beispiel zeigt die Analyse
	 * einer Zeichenkette:
	 * 
	 * <pre>Strings.matches(Pattern.compile(&quot;(\\d+)-(\\d+)?&quot;), &quot;x12-3, x4-yz&quot;); // [ [ &quot;x12-3&quot;, &quot;12&quot;, &quot;3&quot;  ], [ &quot;4-&quot;, &quot;4&quot;, null ] ]</pre>
	 * 
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Treffer.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         <code>null</code> ist.
	 */
	public static List<List<String>> matches(final Pattern pattern, final CharSequence string)
		throws NullPointerException {
		return Strings.splatches(pattern, string, false, true);
	}

	public static List<String> splatch(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.splatch(regex, string, 0, true, true);
	}

	public static List<String> splatch(final String regex, final CharSequence string, final int index)
		throws NullPointerException, IllegalArgumentException {
		return Strings.splatch(regex, string, index, true, true);
	}

	public static List<String> splatch(final Pattern pattern, final CharSequence string) throws NullPointerException {
		return Strings.splatch(pattern, string, 0, true, true);
	}

	// * Diese Methode sucht die Treffer der <tt>index</tt>-ten Gruppen des gegebenen kompilierten regulären Ausdrucks in
	// * der gegebenen Zeichenkette, fügt diese Treffer zusammen mit dem darum liegenden Text in eine Liste ein und gibt
	// * diese Liste zurück. In der Liste befinden sich damit die Treffer der Gruppe an jeder ungeraden Position.
	/**
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         <code>null</code> ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static List<String> splatch(final Pattern pattern, final CharSequence string, final int index)
		throws NullPointerException, IllegalArgumentException {
		return Strings.splatch(pattern, string, index, true, true);
	}

	public static List<List<String>> splatches(final String regex, final CharSequence string)
		throws NullPointerException, IllegalArgumentException {
		return Strings.splatches(regex, string, true, true);
	}

	public static List<List<String>> splatches(final Pattern pattern, final CharSequence string)
		throws NullPointerException, IllegalArgumentException {
		return Strings.splatches(pattern, string, true, true);
	}

	public static Converter<String, Pattern> cachedPatternCompiler(final int flags) {
		return Converters.cachedConverter(new Converter<String, Pattern>() {

			@Override
			public Pattern convert(final String input) {
				return Pattern.compile(input, flags);
			}

		});
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Strings() {
	}

}