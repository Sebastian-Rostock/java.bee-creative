package bee.creative.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein abstraktes Objekt zur Ermittlung und Verwaltung einzigartiger Ausgabeobjekte zu gegebenen Eingabeobjekten. Dazu werden
 * gegebene Eingabeobjekte {@link #customSource(Object) optimiert} und mit den daraus {@link #customTarget(Object) berechneten} Ausgabeobjekten über in einer
 * {@link Map Abbildung} verwaltet. Wenn über {@link #get(Object)} das mit einem gegebenen Eingabeobjekten assoziierte Ausgabeobjekt ermittelt werden soll und
 * dieses zuvor bereits {@link #customTarget(Object) erzeugt} wurde, wird dessen Wiederverwendung über {@link #customReuse(Object, Object)} signalisiert.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ der Eingabe.
 * @param <GTarget> Typ der Ausgabe. */
public abstract class Unique<GSource, GTarget> implements Field<GSource, GTarget>, Hasher, Comparator<GSource>, Iterable<GTarget> {

	/** Diese Klasse implementiert {@link Unique#fromTreeMap(Comparator, Getter, Setter)} */
	@SuppressWarnings ("javadoc")
	public static class TreeMapUnique<GSource, GTarget> extends Unique<GSource, GTarget> {

		public final Comparator<? super GSource> sorter;

		public final Setter<? super GSource, ? super GTarget> reuser;

		public final Getter<? super GSource, ? extends GTarget> builder;

		public TreeMapUnique(final Comparator<? super GSource> sorter, final Setter<? super GSource, ? super GTarget> reuser,
			final Getter<? super GSource, ? extends GTarget> builder) throws NullPointerException {
			super(new TreeMap<GSource, GTarget>(sorter));
			this.sorter = Objects.notNull(sorter);
			this.reuser = Objects.notNull(reuser);
			this.builder = Objects.notNull(builder);
		}

		@Override
		public int compare(final GSource source1, final GSource source2) throws NullPointerException {
			return this.sorter.compare(source1, source2);
		}

		@Override
		protected GTarget customTarget(final GSource source) {
			return this.builder.get(source);
		}

		@Override
		protected void customReuse(final GSource source, final GTarget target) {
			this.reuser.set(source, target);
		}

	}

	/** Diese Klasse implementiert {@link Unique#fromHashMap(Hasher, Getter, Setter)} */
	@SuppressWarnings ("javadoc")
	public static class HashMapUnique<GSource, GTarget> extends Unique<GSource, GTarget> {

		public final Hasher hasher;

		public final Setter<? super GSource, ? super GTarget> reuser;

		public final Getter<? super GSource, ? extends GTarget> builder;

		public HashMapUnique(final Hasher hasher, final Setter<? super GSource, ? super GTarget> reuser, final Getter<? super GSource, ? extends GTarget> builder)
			throws NullPointerException {
			super(HashMap2.<GSource, GTarget>from(hasher));
			this.hasher = hasher;
			this.reuser = Objects.notNull(reuser);
			this.builder = Objects.notNull(builder);
		}

		@Override
		public int hash(final Object source) throws NullPointerException {
			return this.hasher.hash(source);
		}

		@Override
		public boolean equals(final Object source1, final Object source2) throws NullPointerException {
			return this.hasher.equals(source1, source2);
		}

		@Override
		protected void customReuse(final GSource source, final GTarget target) {
			this.reuser.set(source, target);
		}

		@Override
		protected GTarget customTarget(final GSource source) {
			return this.builder.get(source);
		}

	}

	/** Diese Methode gibt ein neues ordnungsbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromTreeMap(Comparator, Getter, Setter)
	 * fromTreeMap(Comparators.naturalComparator(), Getters.neutralGetter(), Setters.emptySetter())}. */
	public static <GSource extends Comparable<? super GSource>> Unique<GSource, GSource> fromTreeMap() {
		return Unique.fromTreeMap(Comparators.<GSource>natural(), Getters.<GSource>neutral(), Setters.empty());
	}

	/** Diese Methode gibt ein neues ordnungsbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromTreeMap(Comparator, Getter, Setter)
	 * fromTreeMap(sorter, Getters.neutralGetter(), Setters.emptySetter())}. */
	public static <GSource> Unique<GSource, GSource> fromTreeMap(final Comparator<? super GSource> sorter) throws NullPointerException {
		return Unique.fromTreeMap(sorter, Getters.<GSource>neutral(), Setters.empty());
	}

	/** Diese Methode gibt ein neues ordnungsbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromTreeMap(Comparator, Getter, Setter)
	 * fromTreeMap(Comparators.naturalComparator(), builder, Setters.emptySetter())}. */
	public static <GSource extends Comparable<? super GSource>, GTarget> Unique<GSource, GTarget> fromTreeMap(
		final Getter<? super GSource, ? extends GTarget> builder) throws NullPointerException {
		return Unique.fromTreeMap(Comparators.<GSource>natural(), builder, Setters.empty());
	}

	/** Diese Methode gibt ein neues ordnungsbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromTreeMap(Comparator, Getter, Setter)
	 * fromTreeMap(sorter, builder, Setters.emptySetter())}. */
	public static <GSource extends Comparable<? super GSource>, GTarget> Unique<GSource, GTarget> fromTreeMap(final Comparator<? super GSource> sorter,
		final Getter<? super GSource, ? extends GTarget> builder) throws NullPointerException {
		return Unique.fromTreeMap(sorter, builder, Setters.empty());
	}

	/** Diese Methode gibt ein neues ordnungsbasiertes {@link Unique} zurück, dessen Eingaben über den gegebenen {@link Comparator} miteinander verglichen und
	 * über den gegebenen {@link Getter} in die Ausgaben überführt werden. Die Wiederverwendung einer Ausgabe wird dem gegebenen {@link Setter} signalisiert.
	 *
	 * @param <GSource> Typ der Eingabe.
	 * @param <GTarget> Typ der Ausgabe.
	 * @param sorter {@link Comparator} zur Ermittlung der Ordnung der Eingaben in {@link #compare(Object, Object)}.
	 * @param builder {@link Getter} zur Überführung der Eingaben in Ausgaben in {@link #customTarget(Object)}.
	 * @param reuser {@link Setter} zur Signalisierung der Wiederverwendung von Einträgen in {@link #customReuse(Object, Object)}.
	 * @return ordnungsbasiertes {@link Unique}. */
	public static <GSource, GTarget> Unique<GSource, GTarget> fromTreeMap(final Comparator<? super GSource> sorter,
		final Getter<? super GSource, ? extends GTarget> builder, final Setter<? super GSource, ? super GTarget> reuser) throws NullPointerException {
		return new TreeMapUnique<>(sorter, reuser, builder);
	}

	/** Diese Methode gibt ein neues streuwertbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromHashMap(Hasher, Getter, Setter)
	 * fromHashMap(Objects.HASHER, Getters.neutralGetter(), Setters.emptySetter())}. */
	public static <GSource> Unique<GSource, GSource> fromHashMap() {
		return Unique.fromHashMap(Objects.HASHER, Getters.<GSource>neutral(), Setters.empty());
	}

	/** Diese Methode gibt ein neues streuwertbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromHashMap(Hasher, Getter, Setter)
	 * fromHashMap(hasher, Getters.neutralGetter(), Setters.emptySetter())}. */
	public static <GSource> Unique<GSource, GSource> fromHashMap(final Hasher hasher) throws NullPointerException {
		return Unique.fromHashMap(hasher, Getters.<GSource>neutral(), Setters.empty());
	}

	/** Diese Methode gibt ein neues streuwertbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromHashMap(Hasher, Getter, Setter)
	 * fromHashMap(Objects.HASHER, builder, Setters.emptySetter())}. */
	public static <GSource, GTarget> Unique<GSource, GTarget> fromHashMap(final Getter<? super GSource, ? extends GTarget> builder) throws NullPointerException {
		return Unique.fromHashMap(Objects.HASHER, builder, Setters.empty());
	}

	/** Diese Methode gibt ein neues streuwertbasiertes {@link Unique} zurück und ist eine Abkürzung für {@link #fromHashMap(Hasher, Getter, Setter)
	 * fromHashMap(hasher, builder, Setters.emptySetter())}. */
	public static <GSource, GTarget> Unique<GSource, GTarget> fromHashMap(final Hasher hasher, final Getter<? super GSource, ? extends GTarget> builder)
		throws NullPointerException {
		return Unique.fromHashMap(hasher, builder, Setters.empty());
	}

	/** Diese Methode gibt ein neues streuwertbasiertes {@link Unique} zurück, dessen Eingaben über den gegebenen {@link Hasher} miteinander verglichen und über
	 * den gegebenen {@link Getter} in die Ausgaben überführt werden. Die Wiederverwendung einer Ausgabe wird dem gegebenen {@link Setter} signalisiert.
	 *
	 * @param <GSource> Typ der Eingabe.
	 * @param <GTarget> Typ der Ausgabe.
	 * @param hasher {@link Hasher} zur Ermittlung von Streuwert und Äquivalenz der Eingaben in {@link #hash(Object)} und {@link #equals(Object, Object)}.
	 * @param builder {@link Getter} zur Überführung der Eingaben in Ausgaben in {@link #customTarget(Object)}.
	 * @param reuser {@link Setter} zur Signalisierung der Wiederverwendung von Einträgen in {@link #customReuse(Object, Object)}.
	 * @return streuwertbasiertes {@link Unique}.
	 * @throws NullPointerException Wenn einer der Parameter {@code null} ist. */
	public static <GSource, GTarget> Unique<GSource, GTarget> fromHashMap(final Hasher hasher, final Getter<? super GSource, ? extends GTarget> builder,
		final Setter<? super GSource, ? super GTarget> reuser) throws NullPointerException {
		return new HashMapUnique<>(hasher, reuser, builder);
	}

	/** Dieses Feld bildet von Ein- auf Ausgaben ab. */
	protected final Map<GSource, GTarget> mapping;

	/** Dieser Konstruktor initialisiert die {@link #mapping() Abbildung} mit einer {@link HashMap2}. */
	public Unique() {
		this(true);
	}

	/** /** Dieser Konstruktor initialisiert die {@link #mapping() Abbildung} mit einer {@link HashMap2} oder {@link TreeMap}.
	 *
	 * @param useHashMap {@code true} für {@link HashMap2}; {@code false} für {@link TreeMap}. */
	public Unique(final boolean useHashMap) {
		this.mapping = useHashMap ? HashMap2.<GSource, GTarget>from(this) : new TreeMap<GSource, GTarget>(this);
	}

	/** Dieser Konstruktor initialisiert die intere Abbildung.
	 *
	 * @param mapping Abbildung. */
	public Unique(final Map<GSource, GTarget> mapping) throws NullPointerException {
		this.mapping = Objects.notNull(mapping);
	}

	/** Diese Methode gibt die mit der gegebenen Eingabe in der {@link #mapping() angebundenen Abbildung} assoziierte Ausgabe zurück. Wenn der gegebenen Eingabe
	 * bereits eine Ausgabe zugeordnet {@link Map#get(Object) ist}, wird deren Wiederverwendung {@link #customReuse(Object, Object) signalisiert}. Sollte der
	 * Eingabe noch keine Ausgabe zugeordnet sein, wird diese {@link #customTarget(Object) erzeugt} und mit der Eingabe {@link Map#put(Object, Object)
	 * assoziiert}.
	 *
	 * @see Map#get(Object)
	 * @see Map#containsKey(Object)
	 * @see Map#put(Object, Object)
	 * @param source Eingabe oder {@code null}.
	 * @return Ausgabe oder {@code null}.
	 * @throws RuntimeException Wenn die gegebene Eingabe bzw. die erzeugte Ausgabe ungültig ist. */
	@Override
	public GTarget get(GSource source) throws RuntimeException {
		GTarget target = this.mapping.get(source);
		if ((target != null) || this.mapping.containsKey(source)) {
			this.customReuse(source, target);
		} else {
			source = this.customSource(source);
			target = this.customTarget(source);
			this.set(source, target);
		}
		return target;
	}

	@Override
	public void set(final GSource source, final GTarget target) {
		this.mapping.put(source, target);
	}

	/** Diese Methode gibt die angebundenen Abbildung zurück.
	 *
	 * @return Abbildung von Ein- auf Ausgaben. */
	public Map<GSource, GTarget> mapping() {
		return this.mapping;
	}

	/** Diese Methode überführt die gegebene Eingabe in das als Schlüssel im {@link #mapping} einzusetzende Objekt und gibt dieses zurück.
	 *
	 * @see Unique#get(Object)
	 * @param source Eingabe oder {@code null}.
	 * @return Schlüssel oder {@code null}. */
	protected GSource customSource(final GSource source) {
		return source;
	}

	/** Diese Methode überführt die gegebene Eingabe in die zugeordnete Ausgabe und gibt diese zurück.
	 *
	 * @see Unique#get(Object)
	 * @param source Eingabe oder {@code null}.
	 * @return Ausgabe oder {@code null}. */
	protected abstract GTarget customTarget(GSource source);

	/** Diese Methode wird bei der Wiederverwendung der gegebenen Ausgabe für die gegebene Eingabe von aufgerufen.
	 *
	 * @see Unique#get(Object)
	 * @param source Eingabe oder {@code null}.
	 * @param target Ausgabe oder {@code null}. */
	protected void customReuse(final GSource source, final GTarget target) {
	}

	@Override
	public int hash(final Object source) throws NullPointerException {
		return Objects.hash(source);
	}

	@Override
	public boolean equals(final Object source1, final Object source2) throws NullPointerException {
		return Objects.equals(source1, source2);
	}

	@Override
	@SuppressWarnings ({"rawtypes", "unchecked"})
	public int compare(final GSource source1, final GSource source2) throws NullPointerException {
		return Comparators.compare((Comparable)source1, (Comparable)source2);
	}

	@Override
	public Iterator<GTarget> iterator() {
		return this.mapping.values().iterator();
	}

	@Override
	public String toString() {
		return Objects.toString(true, this.mapping);
	}

}
