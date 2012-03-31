package bee.creative.util;

import java.util.List;

class Beispiel {

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
