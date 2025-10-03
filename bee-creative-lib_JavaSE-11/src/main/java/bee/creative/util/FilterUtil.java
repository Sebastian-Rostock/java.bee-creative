package bee.creative.util;

import bee.creative.lang.Objects;

/** @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class FilterUtil {

	static final Filter<?> emptyFilter = item -> item != null;

	static final Filter<?> acceptFilter = item -> true;

	static final Filter<?> rejectFilter = item -> false;

	static class SynchronizedFilter<ITEM> extends AbstractFilter<ITEM> {

		public SynchronizedFilter(Filter<? super ITEM> that, Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public boolean accept(ITEM item) {
			synchronized (this.mutex) {
				return this.that.accept(item);
			}
		}

		private final Filter<? super ITEM> that;

		private final Object mutex;

	}

}
