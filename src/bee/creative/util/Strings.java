package bee.creative.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse stellt einige statische Methoden bzur Verarbeitung von regulären Ausdrücken mit Zeichenketten zur
 * Verfügung.
 * 
 * @see Pattern
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Strings {

	/**
	 * Diese Klasse implementiert einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#join(String, Iterable)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class JoinConverter implements Converter<Iterable<?>, String> {

		/**
		 * Dieses Feld speichert das Trennzeichen.
		 */
		final String space;

		/**
		 * Dieser Konstrukteur initialisiert das Trennzeichen.
		 * 
		 * @param space Trennzeichen.
		 * @throws NullPointerException Wenn das gegebene Trennzeichen {@code null} ist.
		 */
		public JoinConverter(final String space) throws NullPointerException {
			if(space == null) throw new NullPointerException("space is null");
			this.space = space;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String convert(final Iterable<?> input) {
			return Strings.join(this.space, input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.space);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof JoinConverter)) return false;
			final JoinConverter data = (JoinConverter)object;
			return Objects.equals(this.space, data.space);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("joinConverter", this.space);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Converter}, der seine Eingabe mit Hilfe eines kompilierten
	 * regulären Ausdrucks in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static abstract class BaseConverter<GOutput> implements Converter<CharSequence, GOutput> {

		/**
		 * Dieses Feld speichert den kompilierten regulären Ausdruck.
		 */
		final Pattern pattern;

		/**
		 * Dieses Feld speichert {@code true}, wenn die nicht getroffenen Zeichenkette eingetragen werden sollen.
		 */
		final boolean split;

		/**
		 * Dieses Feld speichert {@code true}, wenn die getroffen getroffenen Zeichenkette eingetragen werden sollen.
		 */
		final boolean match;

		/**
		 * Dieser Konstrukteur initialisiert den kompilierten regulären Ausdruck.
		 * 
		 * @param pattern kompilierter regulärer Ausdruck.
		 * @param split {@code true}, wenn die nicht getroffenen Zeichenkette eingetragen werden sollen.
		 * @param match {@code true}, wenn die getroffen getroffenen Zeichenkette eingetragen werden sollen.
		 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
		 */
		public BaseConverter(final Pattern pattern, final boolean split, final boolean match) throws NullPointerException {
			if(pattern == null) throw new NullPointerException("pattern is null");
			this.pattern = pattern;
			this.split = split;
			this.match = match;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			final BaseConverter<?> data = (BaseConverter<?>)object;
			return (this.split == data.split) && (this.match == data.match) && Objects.equals(this.pattern, data.pattern);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#apply(Pattern, CharSequence, int, boolean, boolean)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class ApplyConverter extends BaseConverter<List<String>> {

		/**
		 * Dieses Feld speichert den Index.
		 */
		final int index;

		/**
		 * Dieser Konstrukteur initialisiert den kompilierten regulären Ausdruck und den Index.
		 * 
		 * @param pattern kompilierter regulärer Ausdruck.
		 * @param index Index.
		 * @param split {@code true}, wenn die nicht getroffenen Zeichenkette eingetragen werden sollen.
		 * @param match {@code true}, wenn die getroffen getroffenen Zeichenkette eingetragen werden sollen.
		 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
		 */
		public ApplyConverter(final Pattern pattern, final int index, final boolean split, final boolean match)
			throws NullPointerException, IllegalArgumentException {
			super(pattern, split, match);
			if((index < 0) || (index > pattern.matcher("").groupCount()))
				throw new IllegalArgumentException("index out of range: " + index);
			this.index = index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<String> convert(final CharSequence input) {
			return Strings.apply(this.pattern, input, this.index, this.split, this.match);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.index, this.pattern, this.split, this.match);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ApplyConverter)) return false;
			final ApplyConverter data = (ApplyConverter)object;
			return super.equals(object) && (this.index == data.index);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#applyAll(Pattern, CharSequence, boolean, boolean)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class ApplyAllConverter extends BaseConverter<List<List<String>>> {

		/**
		 * Dieser Konstrukteur initialisiert den kompilierten regulären Ausdruck.
		 * 
		 * @param pattern kompilierter regulärer Ausdruck.
		 * @param split {@code true}, wenn die nicht getroffenen Zeichenkette eingetragen werden sollen.
		 * @param match {@code true}, wenn die getroffen getroffenen Zeichenkette eingetragen werden sollen.
		 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
		 */
		public ApplyAllConverter(final Pattern pattern, final boolean split, final boolean match)
			throws NullPointerException, IllegalArgumentException {
			super(pattern, split, match);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final List<List<String>> convert(final CharSequence input) {
			return Strings.applyAll(this.pattern, input, this.split, this.match);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int hashCode() {
			return Objects.hash(this.pattern, this.split, this.match);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ApplyAllConverter)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#split(Pattern, CharSequence, int)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class SplitConverter extends ApplyConverter {

		/**
		 * Dieser Konstrukteur initialisiert den kompilierten regulären Ausdruck und den Index.
		 * 
		 * @param pattern kompilierter regulärer Ausdruck.
		 * @param index Index.
		 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
		 */
		public SplitConverter(final Pattern pattern, final int index) throws NullPointerException, IllegalArgumentException {
			super(pattern, index, true, false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("splitConverter", this.pattern, this.index);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#splitAll(Pattern, CharSequence)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class SplitAllConverter extends ApplyAllConverter {

		/**
		 * Dieser Konstrukteur initialisiert den kompilierten regulären Ausdruck.
		 * 
		 * @param pattern kompilierter regulärer Ausdruck.
		 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
		 */
		public SplitAllConverter(final Pattern pattern) throws NullPointerException {
			super(pattern, true, false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("splitAllConverter", this.pattern);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#match(Pattern, CharSequence, int)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MatchConverter extends ApplyConverter {

		/**
		 * Dieser Konstrukteur initialisiert den kompilierten regulären Ausdruck und den Index.
		 * 
		 * @param pattern kompilierter regulärer Ausdruck.
		 * @param index Index.
		 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
		 */
		public MatchConverter(final Pattern pattern, final int index) throws NullPointerException, IllegalArgumentException {
			super(pattern, index, false, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("matchConverter", this.pattern, this.index);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#matchAll(Pattern, CharSequence)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MatchAllConverter extends ApplyAllConverter {

		/**
		 * Dieser Konstrukteur initialisiert den kompilierten regulären Ausdruck.
		 * 
		 * @param pattern kompilierter regulärer Ausdruck.
		 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
		 */
		public MatchAllConverter(final Pattern pattern) throws NullPointerException {
			super(pattern, false, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("matchAllConverter", this.pattern);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#splatch(Pattern, CharSequence, int)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class SplatchConverter extends ApplyConverter {

		/**
		 * Dieser Konstrukteur initialisiert den kompilierten regulären Ausdruck und den Index.
		 * 
		 * @param pattern kompilierter regulärer Ausdruck.
		 * @param index Index.
		 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
		 */
		public SplatchConverter(final Pattern pattern, final int index) throws NullPointerException,
			IllegalArgumentException {
			super(pattern, index, true, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("splatchConverter", this.pattern, this.index);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#splatchAll(Pattern, CharSequence)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class SplatchAllConverter extends ApplyAllConverter {

		/**
		 * Dieser Konstrukteur initialisiert den kompilierten regulären Ausdruck.
		 * 
		 * @param pattern kompilierter regulärer Ausdruck.
		 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
		 */
		public SplatchAllConverter(final Pattern pattern) throws NullPointerException {
			super(pattern, true, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("splatchAllConverter", this.pattern);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, der seine Eingabe via {@link Pattern#compile(String, int)} in
	 * einen kompilierten regulären Ausdruck umwandelt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class PatternConverter implements Converter<String, Pattern> {

		/**
		 * Dieses Feld speichert die Bitmaske ({@link Pattern #CASE_INSENSITIVE}, {@link Pattern#MULTILINE},
		 * {@link Pattern#DOTALL}, {@link Pattern#UNICODE_CASE}, {@link Pattern#CANON_EQ}, {@link Pattern#UNIX_LINES},
		 * {@link Pattern#LITERAL}, {@link Pattern#UNICODE_CHARACTER_CLASS}, {@link Pattern#COMMENTS}).
		 */
		final int flags;

		/**
		 * Dieser Konstrukteur initialisiert die Bitmaske.
		 * 
		 * @param flags Bitmaske ({@link Pattern #CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL},
		 *        {@link Pattern#UNICODE_CASE}, {@link Pattern#CANON_EQ}, {@link Pattern#UNIX_LINES},
		 *        {@link Pattern#LITERAL}, {@link Pattern#UNICODE_CHARACTER_CLASS}, {@link Pattern#COMMENTS})
		 */
		public PatternConverter(final int flags) {
			this.flags = flags;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Pattern convert(final String input) {
			return Pattern.compile(input, this.flags);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.flags;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof PatternConverter)) return false;
			final PatternConverter data = (PatternConverter)object;
			return (this.flags == data.flags);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("patternConverter", this.flags);
		}

	}

	/**
	 * Dieses Feld speichert den {@link Converter} zur Kompilation der {@link Pattern Pattern}.
	 */
	static final Converter<String, Pattern> CACHED_PATTERN_CONVERTER = Converters.synchronizedConverter(Converters
		.cachedConverter(Strings.patternConverter(0)));

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt eine Liste von
	 * Zeichenketten zurück. Mit den beiden Schaltern kann dazu entschieden werden, ob die von der {@code index} -ten
	 * Gruppen des regulären Ausdrucks getroffenen bzw. nicht getroffenen Zeichenkette in diese Liste eingetragen werden
	 * sollen.
	 * 
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @param index Index.
	 * @param split {@code true}, wenn die nicht getroffenen Zeichenkette eingetragen werden sollen.
	 * @param match {@code true}, wenn die getroffen getroffenen Zeichenkette eingetragen werden sollen.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	static List<String> apply(final String regex, final CharSequence string, final int index, final boolean split,
		final boolean match) throws NullPointerException, IllegalArgumentException {
		if(regex == null) throw new NullPointerException("regex is null");
		if(string == null) throw new NullPointerException("string is null");
		if(index < 0) throw new IllegalArgumentException("index out of range: " + index);
		return Strings.apply(Strings.CACHED_PATTERN_CONVERTER.convert(regex), string, index, split, match);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt eine
	 * Liste von Zeichenketten zurück. Mit den beiden Schaltern kann dazu entschieden werden, ob die von der {@code index}
	 * -ten Gruppen des regulären Ausdrucks getroffenen bzw. nicht getroffenen Zeichenkette in diese Liste eingetragen
	 * werden sollen.
	 * 
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @param index Index.
	 * @param split {@code true}, wenn die nicht getroffenen Zeichenkette eingetragen werden sollen.
	 * @param match {@code true}, wenn die getroffen getroffenen Zeichenkette eingetragen werden sollen.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	static List<String> apply(final Pattern pattern, final CharSequence string, final int index, final boolean split,
		final boolean match) throws NullPointerException, IllegalArgumentException {
		if(pattern == null) throw new NullPointerException("pattern is null");
		if(string == null) throw new NullPointerException("string is null");
		if(index < 0) throw new IllegalArgumentException("index out of range: " + index);
		final Matcher matcher = pattern.matcher(string);
		if(index > matcher.groupCount()) throw new IllegalArgumentException("index out of range: " + index);
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

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt eine Liste von
	 * Listen von Zeichenketten zurück. Mit den beiden Schaltern kann dazu entschieden werden, ob die Listen der von den
	 * Gruppen des regulären Ausdrucks getroffenen bzw. der nicht getroffenen Zeichenketten in die Ergebnisliste
	 * eingetragen werden sollen.
	 * 
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @param split {@code true}, wenn die nicht getroffenen Zeichenkette eingetragen werden sollen.
	 * @param match {@code true}, wenn die getroffen getroffenen Zeichenkette eingetragen werden sollen.
	 * @return Liste der Listen der Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette {@code null} ist.
	 */
	static List<List<String>> applyAll(final String regex, final CharSequence string, final boolean split,
		final boolean match) throws NullPointerException {
		if(regex == null) throw new NullPointerException("regex is null");
		if(string == null) throw new NullPointerException("string is null");
		return Strings.applyAll(Strings.CACHED_PATTERN_CONVERTER.convert(regex), string, split, match);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt eine
	 * Liste von Listen von Zeichenketten zurück. Mit den beiden Schaltern kann dazu entschieden werden, ob die Listen der
	 * von den Gruppen des regulären Ausdrucks getroffenen bzw. nicht getroffenen Zeichenketten in die Ergebnisliste
	 * eingetragen werden sollen.
	 * 
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @param split {@code true}, wenn die nicht getroffenen Zeichenkette eingetragen werden sollen.
	 * @param match {@code true}, wenn die getroffen getroffenen Zeichenkette eingetragen werden sollen.
	 * @return Liste der Listen der Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         {@code null} ist.
	 */
	static List<List<String>> applyAll(final Pattern pattern, final CharSequence string, final boolean split,
		final boolean match) throws NullPointerException {
		if(pattern == null) throw new NullPointerException("pattern is null");
		if(string == null) throw new NullPointerException("string is null");
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
	 * Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte zurück. Der
	 * Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Strings.join(&quot;&quot;, items);
	 * </pre>
	 * 
	 * @see Strings#join(String, Object...)
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn das gegebenen {@link Array} {@code null} ist.
	 */
	public static String join(final Object... items) throws NullPointerException {
		if(items == null) throw new NullPointerException("items is null");
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
	 * @throws NullPointerException Wenn das gegebenen {@link Array} bzw. das gegebene Trennzeichen {@code null} ist.
	 */
	public static String join(final String space, final Object... items) throws NullPointerException {
		if(space == null) throw new NullPointerException("space is null");
		if(items == null) throw new NullPointerException("items is null");
		return Strings.join(space, Arrays.asList(items));
	}

	/**
	 * Diese Methode gibt die Verkettung der {@link Object#toString() Textdarstelungen} der gegebenen Objekte zurück. Der
	 * Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Strings.join(&quot;&quot;, items);
	 * </pre>
	 * 
	 * @see Strings#join(String, Iterable)
	 * @param items Objekte.
	 * @return Verkettungstext.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} {@code null} ist.
	 */
	public static String join(final Iterable<?> items) {
		if(items == null) throw new NullPointerException("items is null");
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
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. das gegebene Trennzeichen {@code null} ist.
	 */
	public static String join(final String space, final Iterable<?> items) {
		if(space == null) throw new NullPointerException("space is null");
		if(items == null) throw new NullPointerException("items is null");
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

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#join(String, Iterable)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Strings#join(String, Iterable)
	 * @param space Trennzeichen.
	 * @return {@link Strings#join(String, Iterable)}-{@link Converter}.
	 */
	public static Converter<Iterable<?>, String> joinConverter(final String space) {
		return new JoinConverter(space);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Zeichenketten zurück, die vom regulären Ausdruck nicht getroffen wurden. Der Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Strings.split(regex, string, 0);
	 * </pre>
	 * 
	 * @see Strings#split(String, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette {@code null} ist.
	 */
	public static List<String> split(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.apply(regex, string, 0, true, false);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Zeichenketten zurück, die von der {@code index}-ten Gruppen des regulären Ausdrucks nicht getroffen wurden. Das
	 * folgende Beispiel zeigt das kontextsensitive Spalten einer Zeichenkette am Komma hinter einer Ziffer:
	 * 
	 * <pre>
	 * Strings.split(&quot;\\d(,)&quot;, &quot;12,3x,56&quot;, 1); // [ &quot;12&quot;, &quot;3x,56&quot; ]
	 * </pre>
	 * 
	 * @see Strings#split(Pattern, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static List<String> split(final String regex, final CharSequence string, final int index)
		throws NullPointerException, IllegalArgumentException {
		return Strings.apply(regex, string, index, true, false);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die
	 * Liste der Zeichenketten zurück, die vom regulären Ausdruck nicht getroffen wurden. Der Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Strings.split(pattern, string, 0);
	 * </pre>
	 * 
	 * @see Strings#split(Pattern, CharSequence, int)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         {@code null} ist.
	 */
	public static List<String> split(final Pattern pattern, final CharSequence string) throws NullPointerException {
		return Strings.apply(pattern, string, 0, true, false);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die
	 * Liste der Zeichenketten zurück, die von der {@code index}-ten Gruppen des regulären Ausdrucks nicht getroffen
	 * wurden. Das folgende Beispiel zeigt das kontextsensitive Spalten einer Zeichenkette am Komma hinter einer Ziffer:
	 * 
	 * <pre>
	 * Strings.split(Pattern.compile(&quot;\\d(,)&quot;), &quot;12,3x,56&quot;, 1); // [ &quot;12&quot;, &quot;3x,56&quot; ]
	 * </pre>
	 * 
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static List<String> split(final Pattern pattern, final CharSequence string, final int index)
		throws NullPointerException, IllegalArgumentException {
		return Strings.apply(pattern, string, index, true, false);
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#split(Pattern, CharSequence, int)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Strings#split(Pattern, CharSequence, int)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param index Index.
	 * @return {@link Strings#split(Pattern, CharSequence, int)}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static Converter<CharSequence, List<String>> splitConverter(final Pattern pattern, final int index)
		throws NullPointerException, IllegalArgumentException {
		return new SplitConverter(pattern, index);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Listen von Zeichenketten zurück, die von den Gruppen des regulären Ausdrucks nicht getroffen wurden. Für eine Guppe
	 * ohne Treffer wird dabei {@code null} in die Liste eingetragen. Das folgende Beispiel zeigt die Analyse einer
	 * Zeichenkette:
	 * 
	 * <pre>
	 * Strings.splitAll(&quot;(\\d+)-(\\d+)?&quot;, &quot;x12-3, x4-yz&quot;); // [ [ &quot;x&quot;, &quot;x&quot;, &quot;x12-&quot; ], [ &quot;, x&quot;, &quot;-3, x&quot; ], [ &quot;yz&quot;, &quot;-yz&quot;, &quot;, x4-yz&quot; ] ]
	 * </pre>
	 * 
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Listen der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette {@code null} ist.
	 */
	public static List<List<String>> splitAll(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.applyAll(regex, string, true, false);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die
	 * Liste der Listen von Zeichenketten zurück, die von den Gruppen des regulären Ausdrucks nicht getroffen wurden. Für
	 * eine Guppe ohne Treffer wird dabei {@code null} in die Liste eingetragen. Das folgende Beispiel zeigt die Analyse
	 * einer Zeichenkette:
	 * 
	 * <pre>
	 * Strings.splitAll(Pattern.compile(&quot;(\\d+)-(\\d+)?&quot;), &quot;x12-3, x4-yz&quot;); // [ [ &quot;x&quot;, &quot;x&quot;, &quot;x12-&quot; ], [ &quot;, x&quot;, &quot;-3, x&quot; ], [ &quot;yz&quot;, &quot;-yz&quot;, &quot;, x4-yz&quot; ] ]
	 * </pre>
	 * 
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Listen der nicht getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         {@code null} ist.
	 */
	public static List<List<String>> splitAll(final Pattern pattern, final CharSequence string)
		throws NullPointerException {
		return Strings.applyAll(pattern, string, true, false);
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#splitAll(Pattern, CharSequence)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Strings#splitAll(Pattern, CharSequence)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @return {@link Strings#splitAll(Pattern, CharSequence)}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
	 */
	public static Converter<CharSequence, List<List<String>>> splitAllConverter(final Pattern pattern)
		throws NullPointerException {
		return new SplitAllConverter(pattern);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Zeichenketten zurück, die vom regulären Ausdruck getroffen wurden. Der Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Strings.match(regex, string, 0);
	 * </pre>
	 * 
	 * @see Strings#match(String, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette {@code null} ist.
	 */
	public static List<String> match(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.apply(regex, string, 0, false, true);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Zeichenketten zurück, die von der {@code index}-ten Gruppen des regulären Ausdrucks getroffen wurden. Das folgende
	 * Beispiel zeigt das kontextsensitive Extrahieren einer Zahl vor einem Euro:
	 * 
	 * <pre>
	 * Strings.match(&quot;(\\d+)€&quot;, &quot;..nur 12€!&quot;, 1); // [ &quot;12&quot; ]
	 * </pre>
	 * 
	 * @see Strings#match(Pattern, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static List<String> match(final String regex, final CharSequence string, final int index)
		throws NullPointerException, IllegalArgumentException {
		return Strings.apply(regex, string, index, false, true);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Zeichenketten zurück, die vom regulären Ausdruck getroffen wurden. Der Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Strings.match(pattern, string, 0);
	 * </pre>
	 * 
	 * @see Strings#match(Pattern, CharSequence, int)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         {@code null} ist.
	 */
	public static List<String> match(final Pattern pattern, final CharSequence string) throws NullPointerException {
		return Strings.apply(pattern, string, 0, false, true);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die
	 * Liste der Zeichenketten zurück, die von der {@code index}-ten Gruppen des regulären Ausdrucks getroffen wurden. Das
	 * folgende Beispiel zeigt das kontextsensitive Extrahieren einer Zahl vor einem Euro:
	 * 
	 * <pre>
	 * Strings.match(Pattern.compile(&quot;(\\d+)€&quot;), &quot;..nur 12€!&quot;, 1); // [ &quot;12&quot; ]
	 * </pre>
	 * 
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static List<String> match(final Pattern pattern, final CharSequence string, final int index)
		throws NullPointerException, IllegalArgumentException {
		return Strings.apply(pattern, string, index, false, true);
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#match(Pattern, CharSequence, int)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Strings#match(Pattern, CharSequence, int)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param index Index.
	 * @return {@link Strings#match(Pattern, CharSequence, int)}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static Converter<CharSequence, List<String>> matchConverter(final Pattern pattern, final int index)
		throws NullPointerException, IllegalArgumentException {
		return new MatchConverter(pattern, index);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Listen von Zeichenketten zurück, die von den Gruppen des regulären Ausdrucks getroffen wurden. Für eine Guppe ohne
	 * Treffer wird dabei {@code null} in die Liste eingetragen. Das folgende Beispiel zeigt die Analyse einer
	 * Zeichenkette:
	 * 
	 * <pre>
	 * Strings.matchAll(&quot;(\\d+)-(\\d+)?&quot;, &quot;x12-3, x4-yz&quot;); // [ [ &quot;x12-3&quot;, &quot;12&quot;, &quot;3&quot;  ], [ &quot;4-&quot;, &quot;4&quot;, null ] ]
	 * </pre>
	 * 
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Listen der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette {@code null} ist.
	 */
	public static List<List<String>> matchAll(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.applyAll(regex, string, false, true);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die
	 * Liste der Listen von Zeichenketten zurück, die von den Gruppen des regulären Ausdrucks getroffen wurden. Für eine
	 * Guppe ohne Treffer wird dabei {@code null} in die Liste eingetragen. Das folgende Beispiel zeigt die Analyse einer
	 * Zeichenkette:
	 * 
	 * <pre>
	 * Strings.matchAll(Pattern.compile(&quot;(\\d+)-(\\d+)?&quot;), &quot;x12-3, x4-yz&quot;); // [ [ &quot;x12-3&quot;, &quot;12&quot;, &quot;3&quot;  ], [ &quot;4-&quot;, &quot;4&quot;, null ] ]
	 * </pre>
	 * 
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Listen der getroffenen Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         {@code null} ist.
	 */
	public static List<List<String>> matchAll(final Pattern pattern, final CharSequence string)
		throws NullPointerException {
		return Strings.applyAll(pattern, string, false, true);
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#matchAll(Pattern, CharSequence)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Strings#matchAll(Pattern, CharSequence)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @return {@link Strings#matchAll(Pattern, CharSequence)}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
	 */
	public static Converter<CharSequence, List<List<String>>> matchAllConverter(final Pattern pattern)
		throws NullPointerException {
		return new MatchAllConverter(pattern);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Zeichenketten zurück, die vom regulären Ausdrucks getroffen bzw. nicht getroffenen wurden. Der Rückgabewert
	 * entspricht:
	 * 
	 * <pre>
	 * Strings.splatch(regex, string, 0);
	 * </pre>
	 * 
	 * @see Strings#splatch(String, CharSequence, int)
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette {@code null} ist.
	 */
	public static List<String> splatch(final String regex, final CharSequence string) throws NullPointerException {
		return Strings.apply(regex, string, 0, true, true);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Zeichenketten zurück, die von der {@code index}-ten Gruppen des regulären Ausdrucks getroffen bzw. nicht
	 * getroffenen wurden. Für eine Guppe ohne Treffer wird dabei {@code null} in die Liste eingetragen. Das folgende
	 * Beispiel zeigt die Analyse einer Zeichenkette:
	 * 
	 * <pre>
	 * Strings.splatch(&quot;\\d(,)&quot;, &quot;12,3x,56&quot;, 1); // [ &quot;12&quot;, &quot;,&quot;, &quot;3x,56&quot; ]
	 * </pre>
	 * 
	 * @param regex regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static List<String> splatch(final String regex, final CharSequence string, final int index)
		throws NullPointerException, IllegalArgumentException {
		return Strings.apply(regex, string, index, true, true);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die
	 * Liste der Zeichenketten zurück, die vom regulären Ausdrucks getroffen bzw. nicht getroffenen wurden. Der
	 * Rückgabewert entspricht:
	 * 
	 * <pre>
	 * Strings.splatch(pattern, string, 0);
	 * </pre>
	 * 
	 * @see Strings#splatch(Pattern, CharSequence, int)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         {@code null} ist.
	 */
	public static List<String> splatch(final Pattern pattern, final CharSequence string) throws NullPointerException {
		return Strings.apply(pattern, string, 0, true, true);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die
	 * Liste der Zeichenketten zurück, die von der {@code index}-ten Gruppen des regulären Ausdrucks getroffen bzw. nicht
	 * getroffenen wurden. Für eine Guppe ohne Treffer wird dabei {@code null} in die Liste eingetragen. Das folgende
	 * Beispiel zeigt die Analyse einer Zeichenkette:
	 * 
	 * <pre>
	 * Strings.splatch(Pattern.compile(&quot;\\d(,)&quot;), &quot;12,3x,56&quot;, 1); // [ &quot;12&quot;, &quot;,&quot;, &quot;3x,56&quot; ]
	 * </pre>
	 * 
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param index Index.
	 * @param string Zeichenkette.
	 * @return Liste der Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static List<String> splatch(final Pattern pattern, final CharSequence string, final int index)
		throws NullPointerException, IllegalArgumentException {
		return Strings.apply(pattern, string, index, true, true);
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#splatch(Pattern, CharSequence, int)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Strings#splatch(Pattern, CharSequence, int)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param index Index.
	 * @return {@link Strings#splatch(Pattern, CharSequence, int)}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Index ungültig ist.
	 */
	public static Converter<CharSequence, List<String>> splatchConverter(final Pattern pattern, final int index)
		throws NullPointerException, IllegalArgumentException {
		return new SplatchConverter(pattern, index);
	}

	/**
	 * Diese Methode wendet den gegebenen regulären Ausdruck auf die gegebene Zeichenkette an und gibt die Liste der
	 * Listen von Zeichenketten zurück, die von den Gruppen des regulären Ausdrucks getroffen bzw. nicht getroffenen
	 * wurden. Für eine Guppe ohne Treffer wird dabei {@code null} in die Liste eingetragen. Das folgende Beispiel zeigt
	 * die Analyse einer Zeichenkette:
	 * 
	 * <pre>
	 * Strings.splatchAll(&quot;(\\d+)-(\\d+)?&quot;, &quot;x12-3, x4-yz&quot;); // [ [ &quot;x&quot;, &quot;x&quot;, &quot;x12-&quot; ], [ &quot;12-3&quot;, &quot;12&quot;, &quot;3&quot; ], [ &quot;, x&quot;, &quot;-3, x&quot; ], [ &quot;4-&quot;, &quot;4&quot;, null ], [ &quot;yz&quot;, &quot;-yz&quot;, &quot;, x4-yz&quot; ] ]
	 * </pre>
	 * 
	 * @param regex regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Listen der Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen reguläre Ausdruck bzw. die gegebene Zeichenkette {@code null} ist.
	 */
	public static List<List<String>> splatchAll(final String regex, final CharSequence string)
		throws NullPointerException {
		return Strings.applyAll(regex, string, true, true);
	}

	/**
	 * Diese Methode wendet den gegebenen kompilierten regulären Ausdruck auf die gegebene Zeichenkette an und gibt die
	 * Liste der Listen von Zeichenketten zurück, die von den Gruppen des regulären Ausdrucks getroffen bzw. nicht
	 * getroffenen wurden. Für eine Guppe ohne Treffer wird dabei {@code null} in die Liste eingetragen. Das folgende
	 * Beispiel zeigt die Analyse einer Zeichenkette:
	 * 
	 * <pre>
	 * Strings.splatchAll(Pattern.compile(&quot;(\\d+)-(\\d+)?&quot;), &quot;x12-3, x4-yz&quot;); // [ [ &quot;x&quot;, &quot;x&quot;, &quot;x12-&quot; ], [ &quot;12-3&quot;, &quot;12&quot;, &quot;3&quot; ], [ &quot;, x&quot;, &quot;-3, x&quot; ], [ &quot;4-&quot;, &quot;4&quot;, null ], [ &quot;yz&quot;, &quot;-yz&quot;, &quot;, x4-yz&quot; ] ]
	 * </pre>
	 * 
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @param string Zeichenkette.
	 * @return Liste der Listen der Zeichenketten.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck bzw. die gegebene Zeichenkette
	 *         {@code null} ist.
	 */
	public static List<List<String>> splatchAll(final Pattern pattern, final CharSequence string)
		throws NullPointerException {
		return Strings.applyAll(pattern, string, true, true);
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Strings#splatchAll(Pattern, CharSequence)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Strings#splatchAll(Pattern, CharSequence)
	 * @param pattern kompilierter regulärer Ausdruck.
	 * @return {@link Strings#splatchAll(Pattern, CharSequence)}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebenen kompilierte reguläre Ausdruck {@code null} ist.
	 */
	public static Converter<CharSequence, List<List<String>>> splatchAllConverter(final Pattern pattern)
		throws NullPointerException {
		return new SplatchAllConverter(pattern);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der seine Eingabe via {@link Pattern#compile(String, int)} in einen
	 * kompilierten regulären Ausdruck umwandelt, und gibt ihn zurück.
	 * 
	 * @see Converter
	 * @see Pattern#compile(String, int)
	 * @param flags Bitmaske ({@link Pattern #CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL},
	 *        {@link Pattern#UNICODE_CASE}, {@link Pattern#CANON_EQ}, {@link Pattern#UNIX_LINES}, {@link Pattern#LITERAL},
	 *        {@link Pattern#UNICODE_CHARACTER_CLASS}, {@link Pattern#COMMENTS})
	 * @return {@link Pattern#compile(String, int)}-{@link Converter}.
	 */
	public static Converter<String, Pattern> patternConverter(final int flags) {
		return new PatternConverter(flags);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Strings() {
	}

}