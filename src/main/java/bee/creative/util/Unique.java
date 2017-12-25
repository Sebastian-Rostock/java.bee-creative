package bee.creative.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import javax.xml.crypto.Data;

/** Diese Klasse implementiert ein abstraktes Objekt zur Ermittlung und Verwaltung einzigartiger Ausgaben zu gegebenen Eingaben. Hierfür werden gegebene
 * Eingaben über eine interne {@link Map Abbildung} mit daraus {@link #build(Object) berechneten} Ausgaben assoziiert. Wenn via {@link #get(Object)} die mit
 * einer gegebenen Eingabe assoziierte Ausgabe ermittelt werden soll und diese Ausgabe zuvor bereits {@link #build(Object) erzeugt} wurde, wird deren
 * Wiederverwendung via {@link #reuse(Object, Object)} signalisiert.
 *
 * @see HashMap#from(Filter, Hasher)
 * @see TreeMap#from(Filter, Comparator)
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingabe.
 * @param <GOutput> Typ der Ausgabe. */
public abstract class Unique<GInput, GOutput> implements Getter<GInput, GOutput>, Hasher<GInput>, Filter<Object>, Comparator<GInput>, Iterable<GOutput> {

	/** Diese Methode ist eine Abkürzung für {@link #newHashed(Filter, Hasher, Getter, Setter) newHashed(null, null, null, null)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput> Unique<GInput, GInput> newHashed() {
		return Unique.newHashed(null, null, null, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #newHashed(Filter, Hasher, Getter, Setter) newHashed(null, hasher, null, null)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput, GOutput> Unique<GInput, GOutput> newHashed(final Hasher<? super Object> hasher) {
		return Unique.newHashed(null, hasher, null, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #newHashed(Filter, Hasher, Getter, Setter) newHashed(filter, hasher, null, null)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput, GOutput> Unique<GInput, GOutput> newHashed(final Filter<? super Object> filter, final Hasher<? super GInput> hasher) {
		return Unique.newHashed(filter, hasher, null, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #newHashed(Filter, Hasher, Getter, Setter) newHashed(null, null, compiler, null)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput, GOutput> Unique<GInput, GOutput> newHashed(final Getter<? super GInput, ? extends GOutput> compiler) {
		return Unique.newHashed(null, null, compiler, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #newHashed(Filter, Hasher, Getter, Setter) newHashed(null, hasher, compiler, null)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput, GOutput> Unique<GInput, GOutput> newHashed(final Hasher<? super Object> hasher,
		final Getter<? super GInput, ? extends GOutput> compiler) {
		return Unique.newHashed(null, hasher, compiler, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #newHashedImpl(Filter, Hasher, Getter, Setter) newHashed(filter, hasher, compiler, null)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput, GOutput> Unique<GInput, GOutput> newHashed(final Filter<? super Object> filter, final Hasher<? super GInput> hasher,
		final Getter<? super GInput, ? extends GOutput> compiler) {
		return Unique.newHashed(filter, hasher, compiler, null);
	}

	/** Diese Methode gibt ein neues streuwertbasiertes {@link Unique} zurück, dessen Eingaben über den gegebenen {@link Filter} geprüft, über den gegebenen
	 * {@link Hasher} miteinander verglichen und über den gegebenen {@link Getter} in die Ausgaben überführt werden. Die Wiederverwendung einer Ausgabe wird dem
	 * gegebenen {@link Setter} signalisiert.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param filter {@link Filter} zur Erkennugn der akzeptierten Eingaben, welche als {@code GInput} interpretiert werden können in {@link #accept(Object)}.<br>
	 *        Bei {@code null} wird {@link Filters#ACCEPT_FILTER} verwendet, sodass alle Eingaben in {@link #hash(Object)} und {@link #equals(Object, Object)}
	 *        akzeptiert werden.
	 * @param hasher {@link Hasher} zur Ermittlung von Streuwert und Äquivalenz der Eingaben in {@link #hash(Object)} und {@link #equals(Object, Object)}.<br>
	 *        Bei {@code null} wird {@link Objects#DEFAULT_HASHER} verwendet.
	 * @param compiler {@link Getter} zur Überführung der Eingaben in Ausgaben in {@link #build(Object)}.<br>
	 *        Bei {@code null} wird {@link Getters#NEUTRAL_GETTER} verwendet, sodass die Ausgaben gleich den Eingaben sind.
	 * @param reuser {@link Setter} zur Signalisierung der Wiederverwendung von Einträgen in {@link #reuse(Object, Object)}.<br>
	 *        Bei {@code null} wird {@link Setters#NEUTRAL_SETTER} verwendet.
	 * @return streuwertbasierte {@link Unique}-Map. */
	public static <GInput, GOutput> Unique<GInput, GOutput> newHashed(final Filter<? super Object> filter, final Hasher<? super GInput> hasher,
		final Getter<? super GInput, ? extends GOutput> compiler, final Setter<? super GInput, ? super GOutput> reuser) {
		@SuppressWarnings ("unchecked")
		final Unique<GInput, GOutput> result = Unique.<GInput, GOutput>newHashedImpl( //
			filter != null ? filter : Filters.ACCEPT_FILTER, //
			(Hasher<? super GInput>)(hasher != null ? hasher : Objects.DEFAULT_HASHER), //
			(Getter<? super GInput, ? extends GOutput>)(compiler != null ? compiler : Getters.NEUTRAL_GETTER), //
			(Setter<? super GInput, ? super GOutput>)(reuser != null ? reuser : Setters.NEUTRAL_SETTER));
		return result;
	}

	@SuppressWarnings ("javadoc")
	static <GInput, GOutput> Unique<GInput, GOutput> newHashedImpl(final Filter<? super Object> filter, final Hasher<? super GInput> hasher,
		final Getter<? super GInput, ? extends GOutput> compiler, final Setter<? super GInput, ? super GOutput> reuser) {
		return new Unique<GInput, GOutput>(Unique.HASHED) {

			@Override
			public boolean accept(final Object input) {
				return filter.accept(input);
			}

			@Override
			public int hash(final GInput input) throws NullPointerException {
				return hasher.hash(input);
			}

			@Override
			public boolean equals(final GInput input1, final GInput input2) throws NullPointerException {
				return hasher.equals(input1, input2);
			}

			@Override
			public void reuse(final GInput input, final GOutput output) {
				reuser.set(input, output);
			}

			@Override
			public GOutput build(final GInput input) {
				return compiler.get(input);
			}

		};
	}

	/** Diese Methode ist eine Abkürzung für {@link #newSorted(Filter, Hasher, Getter, Setter) newSorted(null, null, null, null)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput extends Comparable<? super GInput>> Unique<GInput, GInput> newSorted() {
		return Unique.newSorted(null, null, null, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #newSorted(Filter, Hasher, Getter, Setter) newSorted(null, comparator, null, null)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput> Unique<GInput, GInput> newSorted(final Comparator<? super Object> comparator) {
		return Unique.newSorted(null, comparator, null, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #newSorted(Filter, Hasher, Getter, Setter) newSorted(filter, comparator, null, null)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput> Unique<GInput, GInput> newSorted(final Filter<? super Object> filter, final Comparator<? super GInput> comparator) {
		return Unique.newSorted(filter, comparator, null, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #newSorted(Filter, Hasher, Getter, Setter) newSorted(null, null, compiler, null)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput extends Comparable<? super GInput>, GOutput> Unique<GInput, GOutput> newSorted(
		final Getter<? super GInput, ? extends GOutput> compiler) {
		return Unique.newSorted(null, null, compiler, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #newSorted(Filter, Hasher, Getter, Setter) newSorted(null, comparator, compiler, null)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput, GOutput> Unique<GInput, GOutput> newSorted(final Comparator<? super Object> comparator,
		final Getter<? super GInput, ? extends GOutput> compiler) {
		return Unique.newSorted(null, comparator, compiler, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #newSorted(Filter, Hasher, Getter, Setter) newSorted(filter, comparator, compiler, null)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput, GOutput> Unique<GInput, GOutput> newSorted(final Filter<? super Object> filter, final Comparator<? super GInput> comparator,
		final Getter<? super GInput, ? extends GOutput> compiler) {
		return Unique.newSorted(filter, comparator, compiler, null);
	}

	/** Diese Methode gibt ein neues ordnungsbasiertes {@link Unique} zurück, dessen Eingaben über den gegebenen {@link Filter} geprüft, über den gegebenen
	 * {@link Comparator} miteinander verglichen und über den gegebenen {@link Getter} in die Ausgaben überführt werden. Die Wiederverwendung einer Ausgabe wird
	 * dem gegebenen {@link Setter} signalisiert.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param filter {@link Filter} zur Erkennugn der akzeptierten Eingaben, welche als {@code GInput} interpretiert werden können in {@link #accept(Object)}.<br>
	 *        Bei {@code null} wird {@link Filters#ACCEPT_FILTER} verwendet, sodass alle Eingaben in {@link #hash(Object)} und {@link #equals(Object, Object)}
	 *        akzeptiert werden.
	 * @param comparator {@link Comparator} zur Ermittlung des Vergleichswerts der Eingaben in {@link #compare(Object, Object)}.<br>
	 *        Bei {@code null} wird {@link Comparators#NATURAL_COMPARATOR} verwendet.
	 * @param compiler {@link Getter} zur Überführung der Eingaben in Ausgaben in {@link #build(Object)}.<br>
	 *        Bei {@code null} wird {@link Getters#NEUTRAL_GETTER} verwendet, sodass die Ausgaben gleich den Eingaben sind.
	 * @param reuser {@link Setter} zur Signalisierung der Wiederverwendung von Einträgen in {@link #reuse(Object, Object)}.<br>
	 *        Bei {@code null} wird {@link Setters#NEUTRAL_SETTER} verwendet.
	 * @return streuwertbasierte {@link Unique}-Map. */
	public static <GInput, GOutput> Unique<GInput, GOutput> newSorted(final Filter<Object> filter, final Comparator<? super GInput> comparator,
		final Getter<? super GInput, ? extends GOutput> compiler, final Setter<? super GInput, ? super GOutput> reuser) {
		@SuppressWarnings ("unchecked")
		final Unique<GInput, GOutput> result = Unique.newSortedImpl(//
			filter != null ? filter : Filters.ACCEPT_FILTER, //
			(Comparator<? super GInput>)(comparator != null ? comparator : Comparators.NATURAL_COMPARATOR), //
			(Getter<? super GInput, ? extends GOutput>)(compiler != null ? compiler : Getters.NEUTRAL_GETTER), //
			(Setter<? super GInput, ? super GOutput>)(reuser != null ? reuser : Setters.NEUTRAL_SETTER));
		return result;
	}

	@SuppressWarnings ("javadoc")
	static <GInput, GOutput> Unique<GInput, GOutput> newSortedImpl(final Filter<Object> filter, final Comparator<? super GInput> comparator,
		final Getter<? super GInput, ? extends GOutput> compiler, final Setter<? super GInput, ? super GOutput> reuser) {
		return new Unique<GInput, GOutput>(Unique.SORTED) {

			@Override
			public boolean accept(final Object input) {
				return filter.accept(input);
			}

			@Override
			public int compare(final GInput input1, final GInput input2) throws NullPointerException {
				return comparator.compare(input1, input2);
			}

			@Override
			public void reuse(final GInput input, final GOutput output) {
				reuser.set(input, output);
			}

			@Override
			public GOutput build(final GInput input) {
				return compiler.get(input);
			}

		};
	}

	{}

	/** Dieses Feld speichert den Modus zur Erzeugung eines {@link Unique} mit ordnungsbasierter Abbildung.<br>
	 * Derartige Nutzdaten basieren auf {@link #compare(Object, Object)}. */
	public static final boolean SORTED = true;

	/** Dieses Feld speichert den Modus zur Erzeugung eines {@link Unique} mit streuwertbasierter Abbildung.<br>
	 * Derartige Nutzdaten basieren auf {@link #hash(Object)} und {@link #equals(Object, Object)}. */
	public static final boolean HASHED = false;

	{}

	/** Dieses Feld speichert die {@link Data}. */
	protected final Map<GInput, GOutput> mapping;

	/** Dieser Konstruktor initialisiert das {@link Unique} streuwertbasiert. */
	public Unique() {
		this.mapping = HashMap.from(this, this);
	}

	/** Dieser Konstruktor initialisiert das {@link Unique} entsprechend dem gegebenen Modus.
	 *
	 * @param mappingType {@value #HASHED}, wenn intern eine streuwertbasierte Abbildung genutzt werden soll;<br>
	 *        {@link #SORTED}, wenn intern eine ordnungsbasierte Abbildung genutzt werden soll. */
	public Unique(final boolean mappingType) {
		this.mapping = mappingType == Unique.HASHED ? HashMap.<GInput, GOutput>from(this, this) : TreeMap.<GInput, GOutput>from(this, this);
	}

	/** Dieser Konstruktor initialisiert die intere Abbildung über den gegebenen {@link Getter}, der mit {@code this} aufgerufen wird.
	 *
	 * @param mappingBuilder {@link Getter} zur Erzeugung der {@link Map Abbildung}. */
	public Unique(final Getter<Unique<GInput, GOutput>, Map<GInput, GOutput>> mappingBuilder) {
		this.mapping = Objects.assertNotNull(mappingBuilder.get(this));
	}
 
	{}

	/** Diese Methode gibt die mit der gegebenen Eingabe assoziierte Ausgabe zurück. Wenn der gegebenen Eingabe bereits eine Ausgabe zugeordnet
	 * {@link Map#get(Object) ist}, wird deren Wiederverwendung via {@link Unique#reuse(Object, Object)} signalisiert. Sollte der Eingabe noch keine Ausgabe
	 * zugeordnet sein, wird diese via {@link Unique#build(Object)} erzeugt und mit der Eingabe {@link Map#put(Object, Object) assoziiert}.
	 *
	 * @see Unique#hash(Object)
	 * @see Unique#equals(Object, Object)
	 * @see Unique#accept(Object)
	 * @see Unique#compare(Object, Object)
	 * @param input Eingabe oder {@code null}.
	 * @return Ausgabe oder {@code null}.
	 * @throws RuntimeException Wenn die gegebene Eingabe bzw. die erzeugte Ausgabe ungültig ist. */
	@Override
	public GOutput get(final GInput input) throws RuntimeException {
		GOutput output = this.mapping.get(input);
		if ((output != null) || this.mapping.containsKey(input)) {
			this.reuse(input, output);
		} else {
			output = this.build(input);
			this.mapping.put(input, output);
		}
		return output;
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
	protected abstract GOutput build(GInput input);

	/** Diese Methode wird bei der Wiederverwendung der gegebenen Ausgabe für die gegebene Eingabe von aufgerufen.
	 *
	 * @see Unique#get(Object)
	 * @param input Eingabe oder {@code null}.
	 * @param output Ausgabe oder {@code null}. */
	protected void reuse(final GInput input, final GOutput output) {
	}

	{}

	/** {@inheritDoc}<br>
	 * Sie wird in {@link #mapping()} zur Prüfung der Schlüssel eingesetzt und soll nur dann {@code true} liefern, wenn ein Schlüssel als {@code GInput}
	 * interpretiert werden kann. */
	@Override
	public boolean accept(final Object input) {
		return true;
	}

	/** {@inheritDoc} **/
	@Override
	public int hash(final GInput input) throws NullPointerException {
		return Objects.hash(input);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final GInput input1, final GInput input2) throws NullPointerException {
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
		return Objects.toInvokeString(this, this.mapping);
	}

}
