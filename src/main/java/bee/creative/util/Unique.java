package bee.creative.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/** Diese Klasse implementiert ein abstraktes Objekt zur Ermittlung und Verwaltung einzigartiger Ausgabeobjekte zu gegebenen Eingabeobjekten.<br>
 * Dazu werden gegebene Eingabeobjekte mit daraus {@link #customBuild(Object) berechneten} Ausgabeobjekten über in einer {@link Map Abbildung} verwaltet. Wenn
 * via {@link #get(Object)} das mit einem gegebenen Eingabeobjekten assoziierte Ausgabeobjekt ermittelt werden soll und dieses zuvor bereits
 * {@link #customBuild(Object) erzeugt} wurde, wird dessen Wiederverwendung via {@link #customReuse(Object, Object)} signalisiert.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingabe.
 * @param <GOutput> Typ der Ausgabe. */
public abstract class Unique<GInput, GOutput> implements Field<GInput, GOutput>, Hasher, Comparator<GInput>, Iterable<GOutput> {

	/** Diese Methode gibt ein neues streuwertbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromHashMap(Hasher, Getter, Setter)
	 * fromHashMap(Objects.OBJECT_HASHER, Getters.neutralGetter(), Setters.emptySetter())}. */
	@SuppressWarnings ("javadoc")
	public static <GInput> Unique<GInput, GInput> fromHashMap() {
		return Unique.fromHashMap(Objects.HASHER, Getters.<GInput>neutralGetter(), Setters.emptySetter());
	}

	/** Diese Methode gibt ein neues streuwertbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromHashMap(Hasher, Getter, Setter)
	 * fromHashMap(hasher, Getters.neutralGetter(), Setters.emptySetter())}. */
	@SuppressWarnings ("javadoc")
	public static <GInput> Unique<GInput, GInput> fromHashMap(final Hasher hasher) throws NullPointerException {
		return Unique.fromHashMap(hasher, Getters.<GInput>neutralGetter(), Setters.emptySetter());
	}

	/** Diese Methode gibt ein neues streuwertbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromHashMap(Hasher, Getter, Setter)
	 * fromHashMap(Objects.OBJECT_HASHER, compiler, Setters.emptySetter())}. */
	@SuppressWarnings ("javadoc")
	public static <GInput, GOutput> Unique<GInput, GOutput> fromHashMap(final Getter<? super GInput, ? extends GOutput> compiler) throws NullPointerException {
		return Unique.fromHashMap(Objects.HASHER, compiler, Setters.emptySetter());
	}

	/** Diese Methode gibt ein neues streuwertbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromHashMapImpl(Hasher, Getter, Setter)
	 * fromHashMap(hasher, compiler, Setters.emptySetter())}. */
	@SuppressWarnings ("javadoc")
	public static <GInput, GOutput> Unique<GInput, GOutput> fromHashMap(final Hasher hasher, final Getter<? super GInput, ? extends GOutput> compiler)
		throws NullPointerException {
		return Unique.fromHashMap(hasher, compiler, Setters.emptySetter());
	}

	/** Diese Methode gibt ein neues streuwertbasiertes {@link Unique} zurück, dessen Eingaben über den gegebenen {@link Filter} geprüft, über den gegebenen
	 * {@link Hasher} miteinander verglichen und über den gegebenen {@link Getter} in die Ausgaben überführt werden. Die Wiederverwendung einer Ausgabe wird dem
	 * gegebenen {@link Setter} signalisiert.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param hasher {@link Hasher} zur Ermittlung von Streuwert und Äquivalenz der Eingaben in {@link #hash(Object)} und {@link #equals(Object, Object)}.
	 * @param compiler {@link Getter} zur Überführung der Eingaben in Ausgaben in {@link #customBuild(Object)}.
	 * @param reuser {@link Setter} zur Signalisierung der Wiederverwendung von Einträgen in {@link #customReuse(Object, Object)}.
	 * @return streuwertbasiertes {@link Unique}.
	 * @throws NullPointerException Wenn einer der Parameter {@code null} ist. */
	public static <GInput, GOutput> Unique<GInput, GOutput> fromHashMap(final Hasher hasher, final Getter<? super GInput, ? extends GOutput> compiler,
		final Setter<? super GInput, ? super GOutput> reuser) throws NullPointerException {
		Objects.assertNotNull(hasher);
		Objects.assertNotNull(compiler);
		Objects.assertNotNull(reuser);
		return new Unique<GInput, GOutput>(HashMap2.<GInput, GOutput>from(hasher)) {

			@Override
			public int hash(final Object input) throws NullPointerException {
				return hasher.hash(input);
			}

			@Override
			public boolean equals(final Object input1, final Object input2) throws NullPointerException {
				return hasher.equals(input1, input2);
			}

			@Override
			public void customReuse(final GInput input, final GOutput output) {
				reuser.set(input, output);
			}

			@Override
			public GOutput customBuild(final GInput input) {
				return compiler.get(input);
			}

		};
	}

	/** Diese Methode gibt ein neues ordnungsbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromTreeMap(Comparator, Getter, Setter)
	 * fromTreeMap(Comparators.naturalComparator(), Getters.neutralGetter(), Setters.emptySetter())}. */
	@SuppressWarnings ("javadoc")
	public static <GInput extends Comparable<? super GInput>> Unique<GInput, GInput> fromTreeMap() throws NullPointerException {
		return Unique.fromTreeMap(Comparators.<GInput>naturalComparator(), Getters.<GInput>neutralGetter(), Setters.emptySetter());
	}

	/** Diese Methode gibt ein neues ordnungsbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromTreeMap(Comparator, Getter, Setter)
	 * fromTreeMap(comparator, Getters.neutralGetter(), Setters.emptySetter())}. */
	@SuppressWarnings ("javadoc")
	public static <GInput> Unique<GInput, GInput> fromTreeMap(final Comparator<? super GInput> comparator) throws NullPointerException {
		return Unique.fromTreeMap(comparator, Getters.<GInput>neutralGetter(), Setters.emptySetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromTreeMap(Comparator, Getter, Setter) fromTreeMap(Comparators.naturalComparator(), compiler,
	 * Setters.emptySetter())}. */
	@SuppressWarnings ("javadoc")
	public static <GInput extends Comparable<? super GInput>, GOutput> Unique<GInput, GOutput> fromTreeMap(
		final Getter<? super GInput, ? extends GOutput> compiler) throws NullPointerException {
		return Unique.fromTreeMap(Comparators.<GInput>naturalComparator(), compiler, Setters.emptySetter());
	}

	/** Diese Methode gibt ein neues ordnungsbasiertes {@link Unique} zurück, dessen Eingaben über den gegebenen {@link Comparator} miteinander verglichen und
	 * über den gegebenen {@link Getter} in die Ausgaben überführt werden. Die Wiederverwendung einer Ausgabe wird dem gegebenen {@link Setter} signalisiert.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param comparator {@link Comparator} zur Ermittlung des Vergleichswerts der Eingaben in {@link #compare(Object, Object)}.
	 * @param compiler {@link Getter} zur Überführung der Eingaben in Ausgaben in {@link #customBuild(Object)}.
	 * @param reuser {@link Setter} zur Signalisierung der Wiederverwendung von Einträgen in {@link #customReuse(Object, Object)}.
	 * @return streuwertbasiertes {@link Unique}.
	 * @throws NullPointerException Wenn einer der Parameter {@code null} ist. */
	public static <GInput, GOutput> Unique<GInput, GOutput> fromTreeMap(final Comparator<? super GInput> comparator,
		final Getter<? super GInput, ? extends GOutput> compiler, final Setter<? super GInput, ? super GOutput> reuser) throws NullPointerException {
		Objects.assertNotNull(comparator);
		Objects.assertNotNull(compiler);
		Objects.assertNotNull(reuser);
		return new Unique<GInput, GOutput>(new TreeMap<GInput, GOutput>(comparator)) {

			@Override
			public int compare(final GInput input1, final GInput input2) throws NullPointerException {
				return comparator.compare(input1, input2);
			}

			@Override
			public GOutput customBuild(final GInput input) {
				return compiler.get(input);
			}

			@Override
			public void customReuse(final GInput input, final GOutput output) {
				reuser.set(input, output);
			}

		};
	}

	/** Dieses Feld bildet von Ein- auf Ausgabeobjekte ab. */
	protected final Map<GInput, GOutput> mapping;

	/** Dieser Konstruktor initialisiert die {@link #mapping() Abbildung} mit einer {@link HashMap2}. */
	public Unique() {
		this(true);
	}

	/** /** Dieser Konstruktor initialisiert die {@link #mapping() Abbildung} mit einer {@link HashMap2} oder {@link TreeMap}.
	 *
	 * @param useHashMap {@code true} für {@link HashMap2}; {@code false} für {@link TreeMap}. */
	public Unique(final boolean useHashMap) {
		this.mapping = useHashMap ? HashMap2.<GInput, GOutput>from(this) : new TreeMap<GInput, GOutput>(this);
	}

	/** Dieser Konstruktor initialisiert die intere Abbildung.
	 *
	 * @param mapping Abbildung.
	 * @throws NullPointerException Wenn {@code mapping} {@code null} ist. */
	public Unique(final Map<GInput, GOutput> mapping) throws NullPointerException {
		this.mapping = Objects.assertNotNull(mapping);
	}

	/** Diese Methode gibt die mit der gegebenen Eingabe in der {@link #mapping() angebundenen Abbildung} assoziierte Ausgabe zurück.<br>
	 * Wenn der gegebenen Eingabe bereits eine Ausgabe zugeordnet {@link Map#get(Object) ist}, wird deren Wiederverwendung via
	 * {@link Unique#customReuse(Object, Object)} signalisiert.<br>
	 * Sollte der Eingabe noch keine Ausgabe zugeordnet sein, wird diese via {@link Unique#customBuild(Object)} erzeugt und mit der Eingabe
	 * {@link Map#put(Object, Object) assoziiert}.
	 *
	 * @see Map#get(Object)
	 * @see Map#containsKey(Object)
	 * @see Map#put(Object, Object)
	 * @param input Eingabe oder {@code null}.
	 * @return Ausgabe oder {@code null}.
	 * @throws RuntimeException Wenn die gegebene Eingabe bzw. die erzeugte Ausgabe ungültig ist. */
	@Override
	public GOutput get(final GInput input) throws RuntimeException {
		GOutput output = this.mapping.get(input);
		if ((output != null) || this.mapping.containsKey(input)) {
			this.customReuse(input, output);
		} else {
			output = this.customBuild(input);
			this.set(input, output);
		}
		return output;
	}

	/** {@inheritDoc}<br>
	 * Dazu ruft sie {@link Map#put(Object, Object)} der {@link #mapping() angebundenen Abbildung} auf. */
	@Override
	public void set(final GInput input, final GOutput output) {
		this.mapping.put(input, output);
	}

	/** Diese Methode gibt die interne {@link Map} zurück.
	 *
	 * @return {@link Map} der Einträge. */
	public final Map<GInput, GOutput> mapping() {
		return this.mapping;
	}

	/** Diese Methode kompiliert die gegebene Eingabe in die zugeordnete Ausgabe und gibt diese zurück.
	 *
	 * @see Unique#get(Object)
	 * @param input Eingabe oder {@code null}.
	 * @return Ausgabe oder {@code null}. */
	protected abstract GOutput customBuild(GInput input);

	/** Diese Methode wird bei der Wiederverwendung der gegebenen Ausgabe für die gegebene Eingabe von aufgerufen.
	 *
	 * @see Unique#get(Object)
	 * @param input Eingabe oder {@code null}.
	 * @param output Ausgabe oder {@code null}. */
	protected void customReuse(final GInput input, final GOutput output) {
	}

	/** {@inheritDoc} **/
	@Override
	public int hash(final Object input) throws NullPointerException {
		return Objects.hash(input);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object input1, final Object input2) throws NullPointerException {
		return Objects.equals(input1, input2);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings ({"rawtypes", "unchecked"})
	public int compare(final GInput input1, final GInput input2) throws NullPointerException {
		return Comparators.compare((Comparable)input1, (Comparable)input2);
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<GOutput> iterator() {
		return this.mapping.values().iterator();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toString(true, this.mapping);
	}

}
