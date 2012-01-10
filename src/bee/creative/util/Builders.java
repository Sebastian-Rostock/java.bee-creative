package bee.creative.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import bee.creative.util.Converters.ConverterLink;

public class Builders {

	static abstract class BaseBuilder<GData> implements Builder<GData> {

		/**
		 * Dieses Feld speichert den {@link Builder Builder}.
		 */
		final Builder<? extends GData> builder;

		public BaseBuilder(final Builder<? extends GData> builder) throws NullPointerException {
			if(builder == null) throw new NullPointerException();
			this.builder = builder;
		}

		/**
		 * Diese Methode gibt den {@link Builder Builder} zurück.
		 * 
		 * @return {@link Builder Builder}.
		 */
		public Builder<? extends GData> builder() {
			return this.builder;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			final BaseBuilder<?> data = (BaseBuilder<?>)object;
			return Objects.equals(this.builder, data.builder);
		}

	}

	public static final class CachedBuilder<GData> extends BaseBuilder<GData> {

		/**
		 * Dieses Feld speichert den {@link Pointer Pointer}-Modus.
		 */
		final int mode;

		Pointer<GData> pointer;

		/**
		 * Dieser Konstrukteur initialisiert {@link Pointer Pointer}-Modus und {@link Builder Builder}.
		 * 
		 * @param mode {@link Pointer Pointer}-Modus.
		 * @param builder {@link Builder Builder}.
		 */
		public CachedBuilder(final int mode, final Builder<? extends GData> builder) throws NullPointerException,
			IllegalArgumentException {
			super(builder);
			Pointers.pointer(mode, null);
			this.mode = mode;
		}

		/**
		 * Diese Methode gibt den {@link Pointer Pointer}-Modus zurück.
		 * 
		 * @see Pointers#pointer(int, Object)
		 * @return {@link Pointer Pointer}-Modus.
		 */
		public int mode() {
			return this.mode;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData build() {
			if(this.pointer != null){
				if(this.pointer == Pointers.NULL_POINTER) return null;
				final GData data = this.pointer.data();
				if(data != null) return data;
			}
			final GData data = this.builder.build();
			this.pointer = Pointers.pointer(this.mode, data);
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if((object == this) || this.builder.equals(object)) return true;
			if(!(object instanceof CachedBuilder<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("cachedBuilder", this.builder);
		}

	}

	public static final class ConvertedBuilder<GInput, GOutput> extends ConverterLink<GInput, GOutput> implements
		Builder<GOutput> {

		/**
		 * Dieses Feld speichert den {@link Builder Builder}.
		 */
		final Builder<? extends GInput> builder;

		public ConvertedBuilder(final Converter<? super GInput, ? extends GOutput> converter,
			final Builder<? extends GInput> builder) throws NullPointerException {
			super(converter);
			if(builder == null) throw new NullPointerException();
			this.builder = builder;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput build() {
			return this.converter.convert(this.builder.build());
		}

		/**
		 * Diese Methode gibt den {@link Builder Builder} zurück.
		 * 
		 * @return {@link Builder Builder}.
		 */
		public Builder<? extends GInput> builder() {
			return this.builder;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.builder, this.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ConvertedBuilder<?, ?>)) return false;
			final ConvertedBuilder<?, ?> data = (ConvertedBuilder<?, ?>)object;
			return super.equals(object) && Objects.equals(this.builder, data.builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("ConvertedBuilder", this.converter, this.builder);
		}

	}

	public static final class SynchronizedBuilder<GData> extends BaseBuilder<GData> {

		public SynchronizedBuilder(final Builder<? extends GData> builder) {
			super(builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData build() {
			synchronized(this.builder){
				return this.builder.build();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if((object == this) || this.builder.equals(object)) return true;
			if(!(object instanceof SynchronizedBuilder<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("synchronizedBuilder", this.builder);
		}

	}

	static final Builder<?> NULL_BUILDER = new Builder<Object>() {

		@Override
		public Object build() {
			return null;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("nullBuilder");
		}

	};

	@SuppressWarnings ("unchecked")
	public static <GData> Builder<GData> nullBuilder() {
		return (Builder<GData>)Builders.NULL_BUILDER;
	}

	public static <GData> Builder<GData> cachedBuilder(final Builder<? extends GData> builder)
		throws NullPointerException {
		return Builders.cachedBuilder(Pointers.SOFT, builder);
	}

	public static <GData> Builder<GData> cachedBuilder(final int mode, final Builder<? extends GData> builder)
		throws NullPointerException, IllegalArgumentException {
		return new CachedBuilder<GData>(mode, builder);
	}

	public static <GInput, GOutput> Builder<GOutput> convertedBuilder(
		final Converter<? super GInput, ? extends GOutput> converter, final Builder<? extends GInput> builder)
		throws NullPointerException {
		return new ConvertedBuilder<GInput, GOutput>(converter, builder);
	}

	public static <GData> Builder<GData> synchronizedBuilder(final Builder<? extends GData> builder)
		throws NullPointerException {
		return new SynchronizedBuilder<GData>(builder);
	}

}
