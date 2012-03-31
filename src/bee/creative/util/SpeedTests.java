package bee.creative.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import bee.creative.util.Compact.CompactList;

class SpeedTests {

	static abstract class TestCase<T> {

		public abstract void run(T data);

		public Tester tester(final T data) {
			return new Tester(new Runnable() {

				@Override
				public void run() {
					TestCase.this.run(data);
				}

			});
		}

		public void execute(final T data) {
			final Tester tester = this.tester(data);
			System.out.println(String.format("%s: %s", this.getClass().getSimpleName(), tester));
		}

	}

	static abstract class ListTestCase extends TestCase<List> {

	}

	public static void main(final String[] args) {

		final List list1 = new ArrayList();

		final List list2 = new CompactList();

		final Random random = new Random();

		final int insertCount = 150000;

		final int removeCount = 10000;

		final int getCount = 1500000;

		final int setCount = 1500000;

		final int iteratorCount = 60;

		final int hashCount = 20;

		for(int i = 1; i <= insertCount; i++){
			final int p = random.nextInt(i);
			final Object o = random.nextInt();
			list1.add(p, o);
			list2.add(p, o);
		}

		System.out.println(list1.equals(list2));
		System.out.println(list2.equals(list1));

		list1.clear();
		list2.clear();

		class List_append extends ListTestCase {

			@Override
			public void run(final List data) {
				for(int i = 1; i <= insertCount; i++){
					data.add(random.nextInt(i), null);
				}
			}

		}
		class List_get extends ListTestCase {

			@Override
			public void run(final List data) {
				for(int i = 1; i <= getCount; i++){
					data.get(random.nextInt(data.size()));
				}
			}

		}
		class List_set extends ListTestCase {

			@Override
			public void run(final List data) {
				for(int i = 1; i <= setCount; i++){
					data.set(random.nextInt(data.size()), null);
				}
			}

		}
		class List_remove extends ListTestCase {

			@Override
			public void run(final List data) {
				for(int i = 1; i <= removeCount; i++){
					data.remove(random.nextInt(data.size()));
				}
			}

		}
		Iterators.filteredIterator(Filters.nullFilter(), Iterators.voidIterator());
		class List_iterator extends ListTestCase {

			@Override
			public void run(final List data) {
				for(int i = 1; i <= iteratorCount; i++){
					for(final Iterator iterator = Iterators.filteredIterator(Filters.nullFilter(), data.iterator()); false && iterator
						.hasNext(); iterator.next()){}
				}
			}

		}
		class List_hashCode extends ListTestCase {

			@Override
			public void run(final List data) {
				for(int i = 1; i <= hashCount; i++){
					data.hashCode();
				}
			}

		}
		class List_clear extends ListTestCase {

			@Override
			public void run(final List data) {
				data.clear();
			}

		}

		new List_append().execute(list1);
		new List_append().execute(list2);

		new List_get().execute(list1);
		new List_get().execute(list2);

		new List_set().execute(list1);
		new List_set().execute(list2);

		new List_iterator().execute(list1);
		new List_iterator().execute(list2);

		new List_hashCode().execute(list1);
		new List_hashCode().execute(list2);

		new List_iterator().execute(list1);
		new List_iterator().execute(list2);

		new List_remove().execute(list1);
		new List_remove().execute(list2);

		new List_clear().execute(list1);
		new List_clear().execute(list2);

	}

}
