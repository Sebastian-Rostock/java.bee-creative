package bee.creative.util;

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
	 * Diese Methode gibt die Verkettung der gegebenen Zeichenketten zurück.
	 * 
	 * @see Strings#join(Object[], String)
	 * @param items Zeichenketten.
	 * @return Verkettung der Zeichenketten.
	 */
	static public final String join(final Object[] items) {
		return Strings.join(items, null);
	}

	/**
	 * Diese Methode gibt die Verkettung der Zeichenketten in <code>sequences</code> mit dem Separator
	 * <code>separator</code> zurück.
	 * 
	 * @param items Zeichenkettenliste.
	 * @param join Separator.
	 * @return Verkettung.
	 */
	static public final String join(final Object[] items, final String join) {
		return Strings.join(Arrays.asList(items), join);
	}

	/**
	 * Diese Methode gibt die Verkettung der Zeichenketten in <code>sequences</code> zurück.
	 * 
	 * @param items Zeichenkettenliste.
	 * @return Verkettung.
	 */
	static public final String join(final Iterable<?> items) {
		return Strings.join(items, null);
	}

	/**
	 * Diese Methode gibt die Verkettung der Zeichenketten in <code>sequences</code> mit dem Separator
	 * <code>separator</code> zurück.
	 * 
	 * @param items Zeichenkettenliste.
	 * @param join Separator.
	 * @return Verkettung.
	 */
	static public final String join(final Iterable<?> items, final String join) {
		final StringBuilder builder = new StringBuilder();
		if((join != null) && !join.isEmpty()){
			String space = "";
			for(final Object item: items){
				builder.append(space).append(item);
				space = join;
			}
		}else{
			for(final Object item: items){
				builder.append(item);
			}
		}
		return builder.toString();
	}

	/**
	 * Diese Methode teilt die Zeichenkette <tt>sequence</tt> an den Treffern des regulären Ausdrucks <tt>regexp</tt> und
	 * gibt die Teile als Liste zurück.
	 * 
	 * @param regexp Regulärer Ausdruck.
	 * @param sequence Eingabezeichenkette.
	 * @return Teileliste.
	 */
	static public final List<String> split(final String regexp, final CharSequence sequence) {
		return Strings.split(regexp, sequence, 0);
	}

	/**
	 * Diese Methode teilt die Zeichenkette <tt>sequence</tt> an den Treffern der <tt>index</tt>-ten Gruppen des regulären
	 * Ausdrucks <tt>regexp</tt> und gibt die Teile als Liste zurück.
	 * 
	 * @param regexp Regulärer Ausdruck.
	 * @param sequence Eingabezeichenkette.
	 * @param index Gruppenindex.
	 * @return Teileliste.
	 */
	static public final List<String> split(final String regexp, final CharSequence sequence, final int index) {
		return Strings.split(Pattern.compile(regexp), sequence, index);
	}

	/**
	 * Diese Methode teilt die Zeichenkette <tt>sequence</tt> an den Treffern des regulären Ausdruck Musters
	 * <tt>pattern</tt> und gibt die Teile als Liste zurück.
	 * 
	 * @param pattern Regulärer Ausdruck Muster.
	 * @param sequence Eingabezeichenkette.
	 * @return Teileliste.
	 */
	static public final List<String> split(final Pattern pattern, final CharSequence sequence) {
		return Strings.split(pattern, sequence, 0);
	}

	/**
	 * Diese Methode teilt die Zeichenkette <tt>sequence</tt> an den Treffern der <tt>index</tt>-ten Gruppen des regulären
	 * Ausdruck Musters <tt>pattern</tt> und gibt die Teile als Liste zurück.
	 * 
	 * @param pattern Regulärer Ausdruck Muster.
	 * @param sequence Eingabezeichenkette.
	 * @param index Gruppenindex.
	 * @return Teileliste.
	 */
	static public final List<String> split(final Pattern pattern, final CharSequence sequence, final int index) {
		final Matcher matcher = pattern.matcher(sequence);
		final List<String> result = new ArrayList<String>();
		int last = 0;
		while(matcher.find()){
			result.add(sequence.subSequence(last, matcher.start(index)).toString());
			last = matcher.end(index);
		}
		result.add(sequence.subSequence(last, sequence.length()).toString());
		return result;
	}

	/**
	 * Diese Methode gibt die Liste der Treffer des regulären Ausdrucks <tt>regexp</tt> in der Zeichenkette
	 * <tt>sequence</tt> zurück.
	 * 
	 * @param regexp Regulärer Ausdruck.
	 * @param sequence Eingabezeichenkette.
	 * @return Trefferliste.
	 */
	static public final List<String> match(final String regexp, final CharSequence sequence) {
		return Strings.match(Pattern.compile(regexp), sequence, 0);
	}

	/**
	 * Diese Methode gibt die Liste der Treffer des regulären Ausdruck Musters <tt>pattern</tt> in der Zeichenkette
	 * <tt>sequence</tt> zurück.
	 * 
	 * @param pattern Regulärer Ausdruck Muster.
	 * @param sequence Eingabezeichenkette.
	 * @return Trefferliste.
	 */
	static public final List<String> match(final Pattern pattern, final CharSequence sequence) {
		return Strings.match(pattern, sequence, 0);
	}

	/**
	 * Diese Methode gibt die Liste der Treffer der <tt>index</tt>-ten Gruppe des regulären Ausdrucks <tt>regexp</tt> in
	 * der Zeichenkette <tt>sequence</tt> zurück.
	 * 
	 * @param regexp Regulärer Ausdruck.
	 * @param sequence Eingabezeichenkette.
	 * @param index Gruppenindex.
	 * @return Trefferliste.
	 */
	static public final List<String> match(final String regexp, final CharSequence sequence, final int index) {
		return Strings.match(Pattern.compile(regexp), sequence, index);
	}

	/**
	 * Diese Methode gibt die Liste der Treffer der <tt>index</tt>-ten Gruppe des regulären Ausdruck Musters
	 * <tt>pattern</tt> in der Zeichenkette <tt>sequence</tt> zurück.
	 * 
	 * @param pattern Regulärer Ausdruck Muster.
	 * @param sequence Eingabezeichenkette.
	 * @param index Gruppenindex.
	 * @return Trefferliste.
	 */
	static public final List<String> match(final Pattern pattern, final CharSequence sequence, final int index) {
		final Matcher matcher = pattern.matcher(sequence);
		final ArrayList<String> result = new ArrayList<String>();
		while(matcher.find()){
			result.add(matcher.group(index));
		}
		return result;
	}

	/**
	 * Diese Methode gibt die Liste der Treffer aller Gruppen des regulären Ausdrucks <tt>regexp</tt> in der Zeichenkette
	 * <tt>sequence</tt> zurück.
	 * 
	 * @param regexp Regulärer Ausdruck.
	 * @param sequence Eingabezeichenkette.
	 * @return Gruppentrefferliste.
	 */
	static public final List<List<String>> matches(final String regexp, final CharSequence sequence) {
		return Strings.matches(Pattern.compile(regexp), sequence);
	}

	/**
	 * Diese Methode gibt die Liste der Treffer aller Gruppen des regulären Ausdruck Musters <tt>pattern</tt> in der
	 * Zeichenkette <tt>sequence</tt> zurück.
	 * 
	 * @param pattern Regulärer Ausdruck Muster.
	 * @param sequence Eingabezeichenkette.
	 * @return Gruppentrefferliste.
	 */
	static public final List<List<String>> matches(final Pattern pattern, final CharSequence sequence) {
		final Matcher matcher = pattern.matcher(sequence);
		final List<List<String>> results = new ArrayList<List<String>>();
		while(matcher.find()){
			final int count = matcher.groupCount() + 1;
			final List<String> result = new ArrayList<String>(count);
			results.add(result);
			for(int i = 0; i < count; i++){
				result.add(matcher.group(i));
			}
		}
		return results;
	}

	/**
	 * Diese Methode sucht alle Treffer des regulären Ausdrucks <tt>regexp</tt> in der Zeichenkette <tt>sequence</tt> ,
	 * fügt diese in die Trefferliste <code>matches</code> ein, ersetzt die Treffer in der Zeichenkette durch deren
	 * Referenz und gibt die so veränderte Zeichenkette zurück. Eine Referenz besteht dabei aus der Vorsilbe
	 * <code>prefix</code>, dem via <code>encodeIndex()</code> kodierten Index des Treffers in der Trefferliste und der
	 * Nachsilbe <code>suffix</code>.
	 * 
	 * @param regexp Regulärer Ausdruck.
	 * @param sequence Eingabezeichenkette.
	 * @param prefix Vorsilbe.
	 * @param suffix Nachsilbe.
	 * @param matches Trefferliste.
	 * @return Ausgabezeichenkette.
	 */
	static public final String extractMatch(final String regexp, final CharSequence sequence, final String prefix, final String suffix, final List<String> matches) {
		return Strings.extractMatch(Pattern.compile(regexp), sequence, prefix, suffix, matches, 0);
	}

	/**
	 * Diese Methode sucht alle Treffer des regulären Ausdrucks <tt>regexp</tt> in der Zeichenkette <tt>sequence</tt> ,
	 * fügt deren <tt>index</tt>-ten Gruppen in die Trefferliste <code>matches</code> ein, ersetzt die Treffer in der
	 * Zeichenkette durch deren Referenz und gibt die so veränderte Zeichenkette zurück. Eine Referenz besteht dabei aus
	 * der Vorsilbe <code>prefix</code>, dem via <code>encodeIndex()</code> kodierten Index des Treffers in der
	 * Trefferliste und der Nachsilbe <code>suffix</code>.
	 * 
	 * @param regexp Regulärer Ausdruck.
	 * @param sequence Eingabezeichenkette.
	 * @param prefix Vorsilbe.
	 * @param suffix Nachsilbe.
	 * @param matches Trefferliste.
	 * @param index Gruppenindex.
	 * @return Ausgabezeichenkette.
	 */
	static public final String extractMatch(final String regexp, final CharSequence sequence, final String prefix, final String suffix, final List<String> matches, final int index) {
		return Strings.extractMatch(Pattern.compile(regexp), sequence, prefix, suffix, matches, index);
	}

	/**
	 * Diese Methode sucht alle Treffer des regulären Ausdruck Musters <tt>pattern</tt> in der Zeichenkette
	 * <tt>sequence</tt>, fügt diese in die Trefferliste <code>matches</code> ein, ersetzt die Treffer in der Zeichenkette
	 * durch deren Referenz und gibt die so veränderte Zeichenkette zurück. Eine Referenz besteht dabei aus der Vorsilbe
	 * <code>prefix</code>, dem via <code>encodeIndex()</code> kodierten Index des Treffers in der Trefferliste und der
	 * Nachsilbe <code>suffix</code>.
	 * 
	 * @param pattern Regulärer Ausdruck Muster.
	 * @param sequence Eingabezeichenkette.
	 * @param prefix Vorsilbe.
	 * @param suffix Nachsilbe.
	 * @param matches Trefferliste.
	 * @return Ausgabezeichenkette.
	 */
	static public final String extractMatch(final Pattern pattern, final CharSequence sequence, final String prefix, final String suffix, final List<String> matches) {
		return Strings.extractMatch(pattern, sequence, prefix, suffix, matches, 0);
	}

	/**
	 * Diese Methode sucht alle Treffer des regulären Ausdruck Musters <tt>pattern</tt> in der Zeichenkette
	 * <tt>sequence</tt>, fügt deren <tt>index</tt>-ten Gruppen in die Trefferliste <code>matches</code> ein, ersetzt die
	 * Treffer in der Zeichenkette durch deren Referenz und gibt die so veränderte Zeichenkette zurück. Eine Referenz
	 * besteht dabei aus der Vorsilbe <code>prefix</code>, dem via <code>encodeIndex()</code> kodierten Index des Treffers
	 * in der Trefferliste und der Nachsilbe <code>suffix</code>.
	 * 
	 * @param pattern Regulärer Ausdruck Muster.
	 * @param sequence Eingabezeichenkette.
	 * @param prefix Vorsilbe.
	 * @param suffix Nachsilbe.
	 * @param matches Trefferliste.
	 * @param index Gruppenindex.
	 * @return Ausgabezeichenkette.
	 */
	static public final String extractMatch(final Pattern pattern, final CharSequence sequence, final String prefix, final String suffix, final List<String> matches, final int index) {
		final Matcher matcher = pattern.matcher(sequence);
		final StringBuilder builder = new StringBuilder();
		int last = 0;
		while(matcher.find()){
			final int from = matcher.start(index);
			builder.append(sequence.subSequence(last, from)).append(prefix).append(Strings.encodeIndex(matches.size())).append(suffix);
			last = matcher.end(index);
			matches.add(sequence.subSequence(from, last).toString());
		}
		builder.append(sequence.subSequence(last, sequence.length()));
		return builder.toString();
	}

	/**
	 * Diese Methode sucht alle Treffer des regulären Ausdrucks <tt>regexp</tt> in der Zeichenkette <tt>sequence</tt> ,
	 * fügt deren Gruppen in die Trefferliste <code>matches</code> ein, ersetzt die Treffer in der Zeichenkette durch
	 * deren Referenz und gibt die so veränderte Zeichenkette zurück. Eine Referenz besteht dabei aus der Vorsilbe
	 * <code>prefix</code>, dem via <code>encodeIndex()</code> kodierten Index des Treffers in der Trefferliste und der
	 * Nachsilbe <code>suffix</code>.
	 * 
	 * @param regexp Regulärer Ausdruck.
	 * @param sequence Eingabezeichenkette.
	 * @param prefix Vorsilbe.
	 * @param suffix Nachsilbe.
	 * @param matches Trefferliste.
	 * @return Ausgabezeichenkette.
	 */
	static public final String extractMatches(final String regexp, final CharSequence sequence, final String prefix, final String suffix, final List<List<String>> matches) {
		return Strings.extractMatches(Pattern.compile(regexp), sequence, prefix, suffix, matches);
	}

	/**
	 * Diese Methode sucht alle Treffer des regulären Ausdruck Musters <tt>pattern</tt> in der Zeichenkette
	 * <tt>sequence</tt>, fügt deren Gruppen in die Trefferliste <code>matches</code> ein, ersetzt die Treffer in der
	 * Zeichenkette durch deren Referenz und gibt die so veränderte Zeichenkette zurück. Eine Referenz besteht dabei aus
	 * der Vorsilbe <code>prefix</code>, dem via <code>encodeIndex()</code> kodierten Index des Treffers in der
	 * Trefferliste und der Nachsilbe <code>suffix</code>.
	 * 
	 * @param pattern Regulärer Ausdruck Muster.
	 * @param sequence Eingabezeichenkette.
	 * @param prefix Vorsilbe.
	 * @param suffix Nachsilbe.
	 * @param matches Trefferliste.
	 * @return Ausgabezeichenkette.
	 */
	static public final String extractMatches(final Pattern pattern, final CharSequence sequence, final String prefix, final String suffix, final List<List<String>> matches) {
		final Matcher matcher = pattern.matcher(sequence);
		final StringBuilder builder = new StringBuilder();
		int last = 0;
		while(matcher.find()){
			builder.append(sequence.subSequence(last, matcher.start(0))).append(prefix).append(Strings.encodeIndex(matches.size())).append(suffix);
			last = matcher.end(0);
			final int count = matcher.groupCount() + 1;
			final List<String> value = new ArrayList<String>(count);
			matches.add(value);
			for(int i = 0; i < count; i++){
				value.add(matcher.group(i));
			}
		}
		builder.append(sequence.subSequence(last, sequence.length()));
		return builder.toString();
	}

	/**
	 * Diese Methode kodiert den Index <code>index</code> in eine Dezimalzahlzeichenkette und gibt diese zurück.
	 * 
	 * @see Integer#toString()
	 * @param index Index.
	 * @return Zeichenkette.
	 */
	static public final String encodeIndex(final int index) {
		return Integer.toString(index);
	}

	/**
	 * Diese Methode dekodiert die Dezimalzahlzeichenkette <code>index</code> in eine Zahl und gibt diese zurück.
	 * 
	 * @see Integer#parseInt(String)
	 * @param index Index.
	 * @return Zahl.
	 */
	static public final int decodeIndex(final String index) {
		return Integer.parseInt(index);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Strings() {
	}

}