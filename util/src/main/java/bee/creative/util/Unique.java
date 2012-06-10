package bee.creative.util;

import java.util.Map;

public abstract class Unique<GInput, GOutput> {

	static private final class UniqueKey<GInput> {

		private final Unique<GInput, ?> owner;

		private final GInput input;

		public UniqueKey(final Unique<GInput, ?> owner, final GInput input) {
			this.owner = owner;
			this.input = input;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.owner.hash(this.input);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked"})
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof UniqueKey<?>)) return false;
			return this.owner.equals(this.input, ((UniqueKey<GInput>)object).input);
		}

	}

	static private final Object NULL = new Object();

	// input -> kompilat =>map=> 1: value -> output

	private final Map<Object, Object> cache;

	public Unique(final Map<Object, Object> cache) throws NullPointerException {
		if(cache == null) throw new NullPointerException("cache is null");
		this.cache = cache;
	}

	@SuppressWarnings ("unchecked")
	public GOutput get(final GInput input) {
		final Object key = new UniqueKey<GInput>(this, input);
		final Object value = this.cache.get(key);
		if(value == Unique.NULL){
			this.reuse(input, null);
			return null;
		}
		final GOutput output = ((value == null) ? this.compile(input) : (GOutput)value);
		this.reuse(input, output);
		return output;
	}

	protected int hash(final GInput input) {
		return Objects.hash(input);
	}

	protected void reuse(final GInput input, final GOutput output) {
	}

	protected boolean equals(final GInput input1, final GInput input2) {
		return Objects.equals(input1, input2);
	}

	protected abstract GOutput compile(GInput input);

}
