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
	 * @param <GHelper> Typ des Hilfsobjekts.
	 */
	static abstract class BaseConversion<GInput, GOutput, GHelper> implements Conversion<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die Eingabe.
		 */
		final GInput input;

		/**
		 * Dieses Feld speichert des Hilfsobjekt.
		 */
		final GHelper helper;

		/**
		 * Dieser Konstrukteur initialisiert Eingabe und Hilfsobjekt.
		 * 
		 * @param input Eingabe.
		 * @param helper Hilfsobjekt.
		 */
		public BaseConversion(final GInput input, final GHelper helper) {
			this.input = input;
			this.helper = helper;
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
		public int hashCode() {
			return Objects.hash(this.input) + (31 * Objects.hash(this.helper));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			final BaseConversion<?, ?, ?> data = (BaseConversion<?, ?, ?>)object;
			return Objects.equals(this.input, data.input) && Objects.equals(this.helper, data.helper);
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
	static public final class StaticConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput, GOutput> {

		/**
		 * Dieser Konstrukteur initialisiert Eingabe und Ausgabe.
		 * 
		 * @param input Eingabe.
		 * @param output Ausgabe.
		 */
		public StaticConversion(final GInput input, final GOutput output) {
			super(input, output);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput output() {
			return this.helper;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || ((object instanceof StaticConversion<?, ?>) && super.equals(object));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("staticConversion", this.input, this.helper);
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
	static public final class DynamicConversion<GInput, GOutput> extends BaseConversion<GInput, GOutput, Converter<? super GInput, ? extends GOutput>> {

		/**
		 * Dieser Konstrukteur initialisiert Eingabe und {@link Converter Converter}.
		 * 
		 * @param input Eingabe.
		 * @param converter {@link Converter Converter}.
		 * @throws NullPointerException Wenn der gegebenen {@link Converter Converter} <code>null</code> ist.
		 */
		public DynamicConversion(final GInput input, final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
			super(input, converter);
			if(converter == null) throw new NullPointerException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput output() {
			return this.helper.convert(this.input);
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
			return Objects.toStringCall("dynamicConversion", this.input, this.helper);
		}

	}

	/**
	 * Diese Methode erzeugt ein statisches {@link Conversion Eingabe-Ausgabe-Paar}, dessen Eingabe und Ausgabe konstant
	 * sind, und gibt dieses zurück.
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
	 * Diese Methode erzeugt ein dynamisches {@link Conversion Eingabe-Ausgabe-Paar}, dessen Ausgabe beim Lesen mit Hilfe
	 * des gegebenen {@link Converter Converters} aus der gegebenen Eingabe neu ermittelt wird, und gibt dieses zurück.
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
