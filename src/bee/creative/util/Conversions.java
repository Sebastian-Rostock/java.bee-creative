package bee.creative.util;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion von {@link Conversion
 * Eingabe-Ausgabe-Paaren}.
 * 
 * @see Converter
 * @see Conversion
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Conversions {

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Conversion Eingabe-Ausgabe-Paar}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static abstract class BaseConversion<GInput, GOutput> implements Conversion<GInput, GOutput> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.input());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			final Conversion<?, ?> data = (Conversion<?, ?>)object;
			return Objects.equals(this.input(), data.input());
		}

	}

	/**
	 * Diese Klasse implementiert ein statisches {@link Conversion Eingabe-Ausgabe-Paar}, dessen Eingabe und Ausgabe
	 * konstant sind.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static public final class StaticConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die Eingabe.
		 */
		final GInput input;

		/**
		 * Dieses Feld speichert die Ausgeb.
		 */
		final GOutput output;

		/**
		 * Dieser Konstrukteur initialisiert Eingabe und Ausgabe.
		 * 
		 * @param input Eingabe.
		 * @param output Ausgabe.
		 */
		public StaticConversion(final GInput input, final GOutput output) {
			this.input = input;
			this.output = output;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GInput input() {
			return this.input;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput output() {
			return this.output;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("staticConversion", this.input, this.output);
		}

	}

	/**
	 * Diese Klasse implementiert ein inverses {@link Conversion Eingabe-Ausgabe-Paar}, dessen Ein- und Ausgabe beim Lesen
	 * aus der Aus- bzw. Eingabe eines gegebenen {@link Conversion Eingabe-Ausgabe-Paars} ermittelt wird.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static public final class InverseConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput> {

		/**
		 * Dieses Feld speichert das {@link Conversion Eingabe-Ausgabe-Paar}.
		 */
		final Conversion<? extends GOutput, ? extends GInput> conversion;

		/**
		 * Dieser Konstrukteur initialisiert das {@link Conversion Eingabe-Ausgabe-Paar}.
		 * 
		 * @param conversion {@link Conversion Eingabe-Ausgabe-Paar}.
		 * @throws NullPointerException Wenn der gegebenen {@link Conversion Eingabe-Ausgabe-Paar} <code>null</code> ist.
		 */
		public InverseConversion(final Conversion<? extends GOutput, ? extends GInput> conversion) throws NullPointerException {
			if(conversion == null) throw new NullPointerException();
			this.conversion = conversion;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GInput input() {
			return this.conversion.output();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput output() {
			return this.conversion.input();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("inverseConversion", this.conversion);
		}

	}

	/**
	 * Diese Klasse implementiert ein dynamisches {@link Conversion Eingabe-Ausgabe-Paar}, dessen Ausgabe beim Lesen mit
	 * Hilfe eines gegebenen {@link Converter Converters} aus der gegebenen Eingabe neu ermittelt wird.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static public final class DynamicConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die Eingabe.
		 */
		final GInput input;

		/**
		 * Dieses Feld speichert den {@link Converter Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstrukteur initialisiert Eingabe und {@link Converter Converter}.
		 * 
		 * @param input Eingabe.
		 * @param converter {@link Converter Converter}.
		 * @throws NullPointerException Wenn der gegebenen {@link Converter Converter} <code>null</code> ist.
		 */
		public DynamicConversion(final GInput input, final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
			if(converter == null) throw new NullPointerException();
			this.input = input;
			this.converter = converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GInput input() {
			return this.input;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput output() {
			return this.converter.convert(this.input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || ((object instanceof DynamicConversion<?, ?>) && super.equals(object));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("dynamicConversion", this.input, this.converter);
		}

	}

	/**
	 * Diese Methode erzeugt ein statisches {@link Conversion Eingabe-Ausgabe-Paar}, dessen Eingabe und Ausgabe konstant
	 * sind, und gibt es zurück.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param input Eingabe.
	 * @param output Ausgabe.
	 * @return {@link StaticConversion statisches Eingabe-Ausgabe-Paar}.
	 */
	static public final <GInput, GOutput> Conversion<GInput, GOutput> staticConversion(final GInput input, final GOutput output) {
		return new StaticConversion<GInput, GOutput>(input, output);
	}

	/**
	 * Diese Methode erzeugt ein inverses {@link Conversion Eingabe-Ausgabe-Paar}, dessen Ein- und Ausgabe beim Lesen aus
	 * der Aus- bzw. Eingabe des gegebenen {@link Conversion Eingabe-Ausgabe-Paars} ermittelt wird, und gibt es zurück.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param conversion {@link Conversion Eingabe-Ausgabe-Paar}.
	 * @return {@link InverseConversion inverses Eingabe-Ausgabe-Paar}.
	 * @throws NullPointerException Wenn der gegebenen {@link Conversion Eingabe-Ausgabe-Paar} <code>null</code> ist.
	 */
	static public final <GInput, GOutput> Conversion<GInput, GOutput> inverseConversion(final Conversion<? extends GOutput, ? extends GInput> conversion) {
		return new InverseConversion<GInput, GOutput>(conversion);
	}

	/**
	 * Diese Methode erzeugt ein dynamisches {@link Conversion Eingabe-Ausgabe-Paar}, dessen Ausgabe beim Lesen mit Hilfe
	 * des gegebenen {@link Converter Converters} aus der gegebenen Eingabe neu ermittelt wird, und gibt es zurück.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param input Eingabe.
	 * @param converter {@link Converter Converter}.
	 * @return {@link DynamicConversion dynamisches Eingabe-Ausgabe-Paar}.
	 * @throws NullPointerException Wenn der gegebenen {@link Converter Converter} <code>null</code> ist.
	 */
	static public final <GInput, GOutput> Conversion<GInput, GOutput> dynamicConversion(final GInput input, final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return new DynamicConversion<GInput, GOutput>(input, converter);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Conversions() {
	}

}
