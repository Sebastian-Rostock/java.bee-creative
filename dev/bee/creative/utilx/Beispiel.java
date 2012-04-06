package bee.creative.utilx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import bee.creative.util.Builder;
import bee.creative.util.Builders;
import bee.creative.util.Comparables;
import bee.creative.util.Comparators;
import bee.creative.util.Comparators.ConvertedComparator;
import bee.creative.util.Conversion;
import bee.creative.util.Conversions;
import bee.creative.util.Converter;
import bee.creative.util.Filter;
import bee.creative.util.Filters;
import bee.creative.util.Iterables;
import bee.creative.util.Iterables.ConvertedIterable;
import bee.creative.util.Pointers;

class Beispiel {

	static class Beispiele1 {

		static public class NamedEntry {

			String name;

			// ...

		}

		public void work() {
			final String prefix = ""; // ...
			final String suffix = ""; // ...
			final Iterable<NamedEntry> namedEntris = null;// ...

			final Filter<String> prefixFilter = new Filter<String>() {

				@Override
				public boolean accept(final String input) {
					return input.startsWith(prefix);
				}

			};

			final Filter<String> suffixFilter = new Filter<String>() {

				@Override
				public boolean accept(final String input) {
					return input.endsWith(suffix);
				}

			};

			final Converter<NamedEntry, String> nameConverter = new Converter<NamedEntry, String>() {

				@Override
				public String convert(final NamedEntry input) {
					return input.name;
				}

			};

			final Filter<NamedEntry> namedPrefixFilter = Filters.convertedFilter(nameConverter, prefixFilter);
			final Filter<NamedEntry> namedSuffixFilter = Filters.convertedFilter(nameConverter, suffixFilter);
			final Filter<NamedEntry> filter = Filters.disjunctionFilter(namedPrefixFilter, namedSuffixFilter);

			final Iterable<NamedEntry> filteredNamedEntris = Iterables.filteredIterable(filter, namedEntris);

		}

	}

	static class Beispiele2 {

		static class ComplexEntry {

			static final Converter<ComplexEntry, String> ComplexFormatConverter = new Converter<ComplexEntry, String>() {

				@Override
				public String convert(final ComplexEntry input) {
					final StringBuilder builder = new StringBuilder();
					// ...
					return builder.toString();
				}

			};

			// ...

		}

		static public interface ComplexContext {

			public Iterable<ComplexEntry> getComplexEntries();

			public void setSortedComplexEntryList(List<ComplexEntry> list);

			public void setSortedComplexFormatList(List<String> list);

			// ...

		}

		public void work(final ComplexContext context) {
			final Iterable<ComplexEntry> complexEntries = context.getComplexEntries();
			final List<ComplexEntry> sortedComplexEntryList = new ArrayList<ComplexEntry>();
			final List<String> sortedComplexFormatList = new ArrayList<String>();
			context.setSortedComplexEntryList(sortedComplexEntryList);
			context.setSortedComplexFormatList(sortedComplexFormatList);

			final List<Conversion<ComplexEntry, String>> conversionList = new ArrayList<Conversion<ComplexEntry, String>>();
			{ // Cache
				final Converter<ComplexEntry, Conversion<ComplexEntry, String>> conversionConverter =
					Conversions.staticConversionConverter(ComplexEntry.ComplexFormatConverter);

				final ConvertedIterable<ComplexEntry, Conversion<ComplexEntry, String>> conversionIterable =
					Iterables.convertedIterable(conversionConverter, complexEntries);

				Iterables.appendAll(conversionList, conversionIterable);
			}
			{ // Output
				final Converter<Conversion<?, ? extends String>, String> conversionOutputConverter =
					Conversions.conversionOutputConverter();

				final ConvertedComparator<Conversion<?, ? extends String>, String> conversionOutputComparator =
					Comparators.convertedComparator(conversionOutputConverter, Comparators.stringAlphanumericalComparator());

				Collections.sort(conversionList, conversionOutputComparator);

				final ConvertedIterable<Conversion<ComplexEntry, String>, String> conversionOutputIterable =
					Iterables.convertedIterable(conversionOutputConverter, conversionList);

				Iterables.appendAll(sortedComplexFormatList, conversionOutputIterable);
			}
			{ // Input
				final Converter<Conversion<? extends ComplexEntry, ?>, ComplexEntry> conversionInputConverter =
					Conversions.<ComplexEntry>conversionInputConverter();

				final ConvertedIterable<Conversion<ComplexEntry, String>, ComplexEntry> conversionInputIterable =
					Iterables.convertedIterable(conversionInputConverter, conversionList);

				Iterables.appendAll(sortedComplexEntryList, conversionInputIterable);
			}
		}

	}

	public static final class Helper {

		public static final Builder<Helper> BUILDER = Builders.synchronizedBuilder(Builders.cachedBuilder(Pointers.SOFT,
			new Builder<Helper>() {

				@Override
				public Helper build() {
					return new Helper();
				}

			}));

		public static Helper get() {
			return Helper.BUILDER.build();
		}

		Helper() {
			// ...
		}

	}

	public void work() {
		final Helper helper = Helper.get();
		// ...
	}

	public static class Range {

		public int from; // Startposition der Range

		public int size; // Größe/Länge der Range

	}

	public Range find(final List<Range> ranges, final int position) {
		final int index = Comparables.binarySearch(ranges, new Comparable<Range>() {

			@Override
			public int compareTo(final Range o) {
				if(o.from > position) return 1;
				if((o.from + o.size) <= position) return -1;
				return 0;
			}

		});
		if(index < 0) return null;
		return ranges.get(index);
	}

}
