package bee.creative.util;

/**
 * Diese Klasse implementiert Methoden zur Bereitstellung eines {@link Iterable}-{@link Builder}s.
 * 
 * @see IterableBuilder1
 * @see IterableBuilder2
 * @see IterableBuilder4
 * @see IterableBuilder5
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class IterableBuilder {

	/**
	 * Diese Schnittstelle definiert einen {@link Iterable}-{@link Builder}, der ein konfiguriertes {@link Iterable} in ein {@code chained}-, {@code unique}-, {@code limited}-, {@code filtered}-, {@code converted}- oder {@code unmodifiable}-{@link Iterable} umwandeln kann.
	 * 
	 * @see Iterables#chainedIterable(Iterable, Iterable)
	 * @see Iterables#uniqueIterable(Iterable)
	 * @see Iterables#limitedIterable(int, Iterable)
	 * @see Iterables#filteredIterable(Filter, Iterable)
	 * @see Iterables#convertedIterable(Converter, Iterable)
	 * @see Iterables#unmodifiableIterable(Iterable)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static interface IterableBuilder1<GEntry> extends IterableBuilder6<GEntry>, Builder<Iterable<GEntry>> {

		/**
		 * Diese Methode gibt das konfigurierte {@link Iterable} zurück.
		 * 
		 * @return {@link Iterable}.
		 */
		@Override
		public Iterable<GEntry> build() throws IllegalStateException;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder4<GEntry, IterableBuilder1<GEntry>> chained();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder1<GEntry> asUniqueIterable();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder1<GEntry> asLimitedIterable(int count);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder1<GEntry> asFilteredIterable(Filter<? super GEntry> filter);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder1<GEntry> asUnmodifiableIterable();

		/**
		 * Diese Methode konvertiert das konfigurierte {@link Iterable} via {@link Iterables#convertedIterable(Converter, Iterable)}.
		 * 
		 * @see Iterables#convertedIterable(Converter, Iterable)
		 * @param <V2> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Elemente des konfigurierten {@link Iterable}s.
		 * @param converter {@link Converter}.
		 * @return {@link Iterable}-{@link Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
		 */
		public <V2> IterableBuilder1<V2> asConvertedIterable(final Converter<? super GEntry, ? extends V2> converter) throws NullPointerException;

	}

	/**
	 * Diese Schnittstelle definiert einen {@link Iterable}-{@link Builder}, der ein konfiguriertes {@link Iterable} in ein {@code chained}-, {@code unique}-, {@code limited}-, {@code filtered}- oder {@code unmodifiable}-{@link Iterable} umwandeln kann.
	 * 
	 * @see Iterables#chainedIterable(Iterable, Iterable)
	 * @see Iterables#uniqueIterable(Iterable)
	 * @see Iterables#limitedIterable(int, Iterable)
	 * @see Iterables#filteredIterable(Filter, Iterable)
	 * @see Iterables#unmodifiableIterable(Iterable)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 * @param <GParent> Typ des {@code Parent}-{@link Iterable}-{@link Builder}s.
	 */
	public static interface IterableBuilder2<GEntry, GParent> extends IterableBuilder6<GEntry> {

		/**
		 * Diese Methode beginnt die Konfiguration eines via {@link Iterables#chainedIterable(Iterable, Iterable)} angehängten {@link Iterable}s.
		 * 
		 * @return {@link Iterable}-{@link Builder}.
		 */
		public IterableBuilder5<GEntry, IterableBuilder2<GEntry, GParent>> and();

		/**
		 * Diese Methode beginnt die Konfiguration des via {@link Iterables#chainedIterable(Iterable, Iterable)} angehängten {@link Iterable}s.
		 * 
		 * @param iterable angehängtes {@link Iterable}.
		 * @return {@link Iterable}-{@link Builder}.
		 */
		public IterableBuilder2<GEntry, GParent> and(Iterable<? extends GEntry> iterable);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder4<GEntry, IterableBuilder2<GEntry, GParent>> chained();

		/**
		 * Diese Methode schließt die Konfiguration des via {@link Iterables#chainedIterable(Iterable, Iterable)} angehängten {@link Iterable}s ab.
		 * 
		 * @return {@code Parent}-{@link Iterable}-{@link Builder}.
		 */
		public GParent commitChain();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder2<GEntry, GParent> asUniqueIterable();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder2<GEntry, GParent> asLimitedIterable(int count);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder2<GEntry, GParent> asFilteredIterable(Filter<? super GEntry> filter);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder2<GEntry, GParent> asUnmodifiableIterable();

	}

	/**
	 * Diese Schnittstelle definiert einen {@link Iterable}-{@link Builder}, der ein konfiguriertes {@link Iterable} in ein {@code chained}-, {@code unique}-, {@code limited}-, {@code filtered}- oder {@code unmodifiable}-{@link Iterable} umwandeln kann.
	 * 
	 * @see Iterables#chainedIterable(Iterable, Iterable)
	 * @see Iterables#uniqueIterable(Iterable)
	 * @see Iterables#limitedIterable(int, Iterable)
	 * @see Iterables#filteredIterable(Filter, Iterable)
	 * @see Iterables#unmodifiableIterable(Iterable)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 * @param <GParent> Typ des {@code Parent}-{@link Iterable}-{@link Builder}s.
	 */
	public static interface IterableBuilder3<GEntry, GParent> extends IterableBuilder6<GEntry> {

		/**
		 * Diese Methode schließt die Konfiguration des via {@link Iterables#convertedIterable(Converter, Iterable)} angehängten {@link Iterable}s ab.
		 * 
		 * @return {@code Parent}-{@link Iterable}-{@link Builder}.
		 */
		public GParent commitConversion();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder4<GEntry, IterableBuilder3<GEntry, GParent>> chained();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder3<GEntry, GParent> asUniqueIterable();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder3<GEntry, GParent> asLimitedIterable(int count);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder3<GEntry, GParent> asFilteredIterable(Filter<? super GEntry> filter);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder3<GEntry, GParent> asUnmodifiableIterable();

	}

	/**
	 * Diese Schnittstelle definiert Methoden zur Konfiguration eines via {@link Iterables#chainedIterable(Iterable, Iterable)} angehängten {@link Iterable}s.
	 * 
	 * @see Iterables#chainedIterable(Iterable, Iterable)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 * @param <GParent> Typ des {@code Parent}-{@link Iterable}-{@link Builder}s.
	 */
	public static interface IterableBuilder4<GEntry, GParent> {

		/**
		 * Diese Methode beginnt die Konfiguration eines via {@link Iterables#chainedIterable(Iterable, Iterable)} angehängten {@link Iterable}s.
		 * 
		 * @see Iterables#chainedIterable(Iterable, Iterable)
		 * @return {@link Iterable}-{@link Builder}.
		 */
		public IterableBuilder5<GEntry, IterableBuilder2<GEntry, GParent>> with();

		/**
		 * Diese Methode beginnt die Konfiguration des via {@link Iterables#chainedIterable(Iterable, Iterable)} angehängten {@link Iterable}s.
		 * 
		 * @see Iterables#chainedIterable(Iterable, Iterable)
		 * @param iterable angehängtes {@link Iterable}.
		 * @return {@link Iterable}-{@link Builder}.
		 */
		public IterableBuilder2<GEntry, GParent> with(Iterable<? extends GEntry> iterable);

	}

	/**
	 * Diese Schnittstelle definiert Methoden zur Definition eines {@link Iterable} über ein gegebenes Element, einen {@link Builder} oder ein {@link Iterable}.
	 * 
	 * @see Iterables#entryIterable(Object)
	 * @see Iterables#builderIterable(Builder)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 * @param <GParent> Typ des {@code Parent}-{@link Iterable}-{@link Builder}s.
	 */
	public static interface IterableBuilder5<GEntry, GParent> {

		/**
		 * Diese Methode definiert das {@link Iterable} über das gegebene.
		 * 
		 * @param iterable {@link Iterable}.
		 * @return {@link Iterable}-{@link Builder}.
		 * @throws NullPointerException Wenn das gegebene {@link Iterable} {@code null} ist.
		 */
		public GParent iterable(Iterable<? extends GEntry> iterable) throws NullPointerException;

		/**
		 * Diese Methode gibt den {@link Iterable}-{@link Builder} zur Konfiguration des zweiten Arguments für {@link Iterables#convertedIterable(Converter, Iterable)} zurück.
		 * 
		 * @see Iterables#convertedIterable(Converter, Iterable)
		 * @param <GEntry2> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des konfigurierten {@link Iterable}s.
		 * @param converter {@link Converter}.
		 * @return {@link Iterable}-{@link Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
		 */
		public <GEntry2> IterableBuilder5<GEntry2, IterableBuilder3<GEntry2, GParent>> converted(final Converter<? super GEntry2, ? extends GEntry> converter)
			throws NullPointerException;

		/**
		 * Diese Methode definiert das {@link Iterable} über {@link Iterables#entryIterable(Object)}.
		 * 
		 * @see Iterables#entryIterable(Object)
		 * @param entry Element.
		 * @return {@link Iterable}-{@link Builder}.
		 */
		public GParent entryIterable(final GEntry entry);

		/**
		 * Diese Methode definiert das {@link Iterable} über {@link Iterables#builderIterable(Builder)}.
		 * 
		 * @see Iterables#builderIterable(Builder)
		 * @param builder {@link Builder}.
		 * @return {@link Iterable}-{@link Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
		 */
		public GParent builderIterable(final Builder<? extends GEntry> builder) throws NullPointerException;

	}

	/**
	 * Diese Schnittstelle definiert einen {@link Iterable}-{@link Builder}, der ein konfiguriertes {@link Iterable} in ein {@code unique}-, {@code limited}-, {@code filtered}- oder {@code unmodifiable}-{@link Iterable} umwandeln kann.
	 * 
	 * @see Iterables#limitedIterable(int, Iterable)
	 * @see Iterables#filteredIterable(Filter, Iterable)
	 * @see Iterables#unmodifiableIterable(Iterable)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static interface IterableBuilder6<GEntry> {

		/**
		 * Diese Methode gibt den {@link Iterable}-{@link Builder} zur Konfiguration des zweiten Arguments für {@link Iterables#chainedIterable(Iterable, Iterable)} zurück.
		 * 
		 * @return {@link Iterable}-{@link Builder}.
		 */
		public IterableBuilder4<GEntry, ? extends IterableBuilder6<GEntry>> chained();

		/**
		 * Diese Methode konvertiert das konfigurierte {@link Iterable} via {@link Iterables#uniqueIterable(Iterable)}.
		 * 
		 * @see Iterables#uniqueIterable(Iterable)
		 * @return {@link Iterable}-{@link Builder}.
		 */
		public IterableBuilder6<GEntry> asUniqueIterable();

		/**
		 * Diese Methode konvertiert das konfigurierte {@link Iterable} via {@link Iterables#limitedIterable(int, Iterable)}.
		 * 
		 * @param count Anzahl.
		 * @see Iterables#limitedIterable(int, Iterable)
		 * @return {@link Iterable}-{@link Builder}.
		 * @throws IllegalArgumentException Wenn die gegebene Anzahl negativ ist.
		 */
		public IterableBuilder6<GEntry> asLimitedIterable(int count) throws IllegalArgumentException;

		/**
		 * Diese Methode konvertiert das konfigurierte {@link Iterable} via {@link Iterables#filteredIterable(Filter, Iterable)}.
		 * 
		 * @param filter {@link Filter}.
		 * @see Iterables#filteredIterable(Filter, Iterable)
		 * @return {@link Iterable}-{@link Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter} {@code null} ist.
		 */
		public IterableBuilder6<GEntry> asFilteredIterable(Filter<? super GEntry> filter) throws NullPointerException;

		/**
		 * Diese Methode konvertiert das konfigurierte {@link Iterable} via {@link Iterables#unmodifiableIterable(Iterable)}.
		 * 
		 * @see Iterables#unmodifiableIterable(Iterable)
		 * @return {@link Iterable}-{@link Builder}.
		 */
		public IterableBuilder6<GEntry> asUnmodifiableIterable();

	}

	/**
	 * Diese Klasse implementiert {@link IterableBuilder1}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static class IterableBuilderAImpl<GEntry> extends IterableBuilderEImpl<GEntry> implements IterableBuilder1<GEntry> {

		/**
		 * Dieser Konstruktor initialisiert das {@link Iterable}.
		 * 
		 * @param iterable {@link Iterable}.
		 */
		public IterableBuilderAImpl(final Iterable<? extends GEntry> iterable) {
			this.iterable = Iterables.iterable(iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterable<GEntry> build() {
			if(this.iterable == null) throw new IllegalStateException();
			return this.iterable;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public IterableBuilder4<GEntry, IterableBuilder1<GEntry>> chained() {
			return new IterableBuilderBImpl(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder1<GEntry> asUniqueIterable() {
			super.asUniqueIterable();
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder1<GEntry> asLimitedIterable(final int count) {
			super.asLimitedIterable(count);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder1<GEntry> asFilteredIterable(final Filter<? super GEntry> filter) {
			super.asFilteredIterable(filter);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <GEntry2> IterableBuilder1<GEntry2> asConvertedIterable(final Converter<? super GEntry, ? extends GEntry2> converter) {
			return new IterableBuilderAImpl<GEntry2>(Iterables.convertedIterable(converter, this.iterable));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder1<GEntry> asUnmodifiableIterable() {
			super.asUnmodifiableIterable();
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert {@link IterableBuilder2} für {@link IterableBuilder2#chained()}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 * @param <GParent> Typ des {@code Parent}-{@link Builder}s.
	 */
	@SuppressWarnings ("unchecked")
	static class IterableBuilderBImpl<GEntry, GParent extends IterableBuilderEImpl<GEntry>> extends IterableBuilderDImpl<GEntry, GParent> {

		/**
		 * Dieser Konstruktor initialisiert den {@code Parent}-{@link Builder}.
		 * 
		 * @param parent {@code Parent}-{@link Builder}.
		 */
		public IterableBuilderBImpl(final GParent parent) {
			super(parent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GParent commitChain() {
			this.parent.iterable = Iterables.chainedIterable(this.parent.iterable, this.iterable);
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GParent commitConversion() {
			throw new IllegalStateException();
		}

	}

	/**
	 * Diese Klasse implementiert {@link IterableBuilder5} für {@link IterableBuilder5#converted(Converter)}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des konfigurierten {@link Iterable}s.
	 * @param <GEntry2> Typ der Eingabe des {@code Parent}-{@link Builder}s.
	 * @param <GParent> Typ des {@code Parent}-{@link Builder}s.
	 */
	@SuppressWarnings ("unchecked")
	static class IterableBuilderCImpl<GEntry, GEntry2, GParent extends IterableBuilderDImpl<GEntry2, ?>> extends IterableBuilderDImpl<GEntry, GParent> {

		/**
		 * Dieses Feld speichert den {@link Converter} von {@link #converted(Converter)}.
		 */
		Converter<? super GEntry, ? extends GEntry2> converter;

		/**
		 * Dieser Konstruktor initialisiert {@code Parent}-{@link Builder} und {@link Converter}.
		 * 
		 * @param parent {@code Parent}-{@link Builder}.
		 * @param converter {@link Converter}.
		 */
		public IterableBuilderCImpl(final GParent parent, final Converter<? super GEntry, ? extends GEntry2> converter) {
			super(parent);
			this.converter = converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GParent commitChain() {
			throw new IllegalStateException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GParent commitConversion() {
			this.parent.iterable = Iterables.convertedIterable(this.converter, this.iterable);
			return this.parent;
		}

	}

	/**
	 * Diese Klasse implementiert {@link IterableBuilder2}, {@link IterableBuilder3}, {@link IterableBuilder4} und {@link IterableBuilder5}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 * @param <GParent> Typ des {@code Parent}-{@link Builder}s.
	 */
	static abstract class IterableBuilderDImpl<GEntry, GParent> extends IterableBuilderEImpl<GEntry> implements IterableBuilder2<GEntry, GParent>,
		IterableBuilder3<GEntry, GParent>, IterableBuilder4<GEntry, GParent>, IterableBuilder5<GEntry, IterableBuilderDImpl<GEntry, GParent>> {

		/**
		 * Dieses Feld speichert den {@code Parent}-{@link Builder}.
		 */
		GParent parent;

		/**
		 * Dieser Konstruktor initialisiert den {@code Parent}-{@link Builder}.
		 * 
		 * @param parent {@code Parent}-{@link Builder}.
		 */
		public IterableBuilderDImpl(final GParent parent) {
			this.parent = parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder5<GEntry, IterableBuilder2<GEntry, GParent>> and() {
			this.commitChain();
			return this.with();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder2<GEntry, GParent> and(final Iterable<? extends GEntry> iterable) {
			this.commitChain();
			return this.with(iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public IterableBuilder5<GEntry, IterableBuilder2<GEntry, GParent>> with() {
			return (IterableBuilder5)this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder2<GEntry, GParent> with(final Iterable<? extends GEntry> iterable) {
			this.iterable = Iterables.chainedIterable(this.iterable, iterable);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public IterableBuilder4 chained() {
			return new IterableBuilderBImpl(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public <GEntry2> IterableBuilder5<GEntry2, IterableBuilder3<GEntry2, IterableBuilderDImpl<GEntry, GParent>>> converted(
			final Converter<? super GEntry2, ? extends GEntry> converter) throws NullPointerException {
			return new IterableBuilderCImpl(this, converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilderDImpl<GEntry, GParent> iterable(final Iterable<? extends GEntry> iterable) throws NullPointerException {
			if(iterable == null) throw new NullPointerException();
			this.iterable = Iterables.iterable(iterable);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilderDImpl<GEntry, GParent> entryIterable(final GEntry entry) {
			this.iterable = Iterables.entryIterable(entry);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilderDImpl<GEntry, GParent> builderIterable(final Builder<? extends GEntry> builder) throws NullPointerException {
			this.iterable = Iterables.builderIterable(builder);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilderDImpl<GEntry, GParent> asUniqueIterable() {
			super.asUniqueIterable();
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilderDImpl<GEntry, GParent> asLimitedIterable(final int count) throws IllegalArgumentException {
			super.asLimitedIterable(count);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilderDImpl<GEntry, GParent> asFilteredIterable(final Filter<? super GEntry> filter) throws NullPointerException {
			super.asFilteredIterable(filter);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilderDImpl<GEntry, GParent> asUnmodifiableIterable() {
			super.asUnmodifiableIterable();
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert {@link IterableBuilder6}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static abstract class IterableBuilderEImpl<GEntry> implements IterableBuilder6<GEntry> {

		/**
		 * Dieses Feld speichert das konfigurierte {@link Iterable}.
		 */
		Iterable<GEntry> iterable;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder6<GEntry> asUniqueIterable() {
			this.iterable = Iterables.uniqueIterable(this.iterable);
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder6<GEntry> asLimitedIterable(final int count) throws IllegalArgumentException {
			this.iterable = Iterables.limitedIterable(count, this.iterable);
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder6<GEntry> asFilteredIterable(final Filter<? super GEntry> filter) throws NullPointerException {
			this.iterable = Iterables.filteredIterable(filter, this.iterable);
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IterableBuilder6<GEntry> asUnmodifiableIterable() {
			this.iterable = Iterables.unmodifiableIterable(this.iterable);
			return null;
		}

	}

	/**
	 * Diese Methode gibt einen neuen {@link IterableBuilder} zurück.
	 * 
	 * @return {@link IterableBuilder}.
	 */
	public static IterableBuilder use() {
		return new IterableBuilder();
	}

	/**
	 * Diese Methode gibt einen {@link Iterable}-{@link Builder} für das gegebene {@link Iterable} zurück.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@link Iterable}-{@link Builder}.
	 * @throws NullPointerException Wenn das gegebene {@link Iterable} {@code null} ist.
	 */
	public <GEntry> IterableBuilder1<GEntry> iterable(final Iterable<? extends GEntry> iterable) throws NullPointerException {
		if(iterable == null) throw new NullPointerException();
		return new IterableBuilderAImpl<GEntry>(iterable);
	}

	/**
	 * Diese Methode gibt einen {@link Iterable}-{@link Builder} für das leere {@link Iterable} zurück.
	 * 
	 * @see Iterables#voidIterable()
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link Iterable}-{@link Builder}.
	 */
	public <GEntry> IterableBuilder1<GEntry> voidIterable() {
		return new IterableBuilderAImpl<GEntry>(Iterables.<GEntry>voidIterable());
	}

	/**
	 * Diese Methode gibt einen {@link Iterable}-{@link Builder} für ein {@link Iterable} über das gegebene Element zurück.
	 * 
	 * @see Iterables#entryIterable(Object)
	 * @param <GEntry> Typ der Elemente.
	 * @param entry Element.
	 * @return {@link Iterable}-{@link Builder}.
	 */
	public <GEntry> IterableBuilder1<GEntry> entryIterable(final GEntry entry) {
		return new IterableBuilderAImpl<GEntry>(Iterables.entryIterable(entry));
	}

	/**
	 * Diese Methode gibt einen {@link Iterable}-{@link Builder} für ein {@link Iterable} über das durch den gegebenen {@link Builder} bereitgestellte Element zurück.
	 * 
	 * @see Iterables#builderIterable(Builder)
	 * @param <GEntry> Typ der Elemente.
	 * @param builder {@link Builder}.
	 * @return {@link Iterable}-{@link Builder}.
	 * @throws NullPointerException Wenn das gegebene {@link Iterable} {@code null} ist.
	 */
	public <GEntry> IterableBuilder1<GEntry> builderIterable(final Builder<? extends GEntry> builder) throws NullPointerException {
		return new IterableBuilderAImpl<GEntry>(Iterables.builderIterable(builder));
	}

}
