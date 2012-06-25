package bee.creative.compact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import bee.creative.array.CompactIntegerArray;
import bee.creative.util.Builder;
import bee.creative.util.Comparators;
import bee.creative.util.Tests;
import bee.creative.util.Tests.A1Test;

public class Main {

	static int COUNT =
	// 32
		128 * 1024;

	static final Random RANDOM = new Random();

	static enum MapTest implements A1Test<Map<Integer, Integer>> {

		addIndex {

			@Override
			public void run(final Map<Integer, Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.put(new Integer(i), new Integer(i));
				}
			}

		},

		addRandom {

			@Override
			public void run(final Map<Integer, Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.put(new Integer(Main.RANDOM.nextInt(Main.COUNT)), new Integer(i));
				}
			}

		},

		removeIndex {

			@Override
			public void run(final Map<Integer, Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.remove(new Integer(i));
				}
			}

		},

		removeRandom {

			@Override
			public void run(final Map<Integer, Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.remove(new Integer(Main.RANDOM.nextInt(Main.COUNT)));
				}
			}

		},

		containsIndex {

			@Override
			public void run(final Map<Integer, Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.get(new Integer(i));
				}
			}

		},

		containsRandom {

			@Override
			public void run(final Map<Integer, Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.get(new Integer(Main.RANDOM.nextInt(Main.COUNT)));
				}
			}

		};

	}

	static enum MapBuilder implements Builder<Map<Integer, Integer>> {

		hashMap {

			@Override
			public Map<Integer, Integer> build() {
				return new HashMap<Integer, Integer>(0);
			}

		},

		treeMap {

			@Override
			public Map<Integer, Integer> build() {
				return new TreeMap<Integer, Integer>();
			}

		},

		compactEntryHashMap {

			@Override
			public Map<Integer, Integer> build() {
				return new CompactEntryHashMap<Integer, Integer>(0);
			}

		},

		compactNavigableEntryMap {

			@Override
			public Map<Integer, Integer> build() {
				return new CompactNavigableEntryMap<Integer, Integer>(0, Comparators.naturalComparator());
			}

		}

	}

	static enum SetTest implements A1Test<Set<Integer>> {

		addIndex {

			@Override
			public void run(final Set<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.add(new Integer(i));
				}
			}

		},

		addRandom {

			@Override
			public void run(final Set<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.add(new Integer(Main.RANDOM.nextInt(Main.COUNT)));
				}
			}

		},

		removeIndex {

			@Override
			public void run(final Set<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.remove(new Integer(i));
				}
			}

		},

		removeRandom {

			@Override
			public void run(final Set<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.remove(new Integer(Main.RANDOM.nextInt(Main.COUNT)));
				}
			}

		},

		containsIndex {

			@Override
			public void run(final Set<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.contains(new Integer(i));
				}
			}

		},

		containsRandom {

			@Override
			public void run(final Set<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.contains(new Integer(Main.RANDOM.nextInt(Main.COUNT)));
				}
			}

		};

	}

	static enum SetBuilder implements Builder<Set<Integer>> {

		hashSet {

			@Override
			public Set<Integer> build() {
				return new HashSet<Integer>(0);
			}

		},

		treeSet {

			@Override
			public Set<Integer> build() {
				return new TreeSet<Integer>();
			}

		},

		compactHashSet {

			@Override
			public Set<Integer> build() {
				return new CompactHashSet<Integer>(0);
			}

		},

		compactNavigableSet {

			@Override
			public Set<Integer> build() {
				return new CompactNavigableSet<Integer>(0, Comparators.naturalComparator());
			}

		}

	}

	static enum ListTest implements A1Test<List<Integer>> {

		addStartIndex {

			@Override
			public void run(final List<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.add(0, new Integer(i));
				}
			}

		},

		addFinalIndex {

			@Override
			public void run(final List<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.add(data.size(), new Integer(i));
				}
			}

		},
		addCenterIndex {

			@Override
			public void run(final List<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.add(data.size() / 2, new Integer(i));
				}
			}

		},

		addRandomIndex {

			@Override
			public void run(final List<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.add(Main.RANDOM.nextInt(data.size() + 1), new Integer(i));
				}
			}

		},

		removeStartIndex {

			@Override
			public void run(final List<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.remove(0);
				}
			}

		},

		removeFinalIndex {

			@Override
			public void run(final List<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.remove(data.size() - 1);
				}
			}

		},

		removeCenterIndex {

			@Override
			public void run(final List<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.remove(data.size() / 2);
				}
			}

		},

		removeRandomIndex {

			@Override
			public void run(final List<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.remove(Main.RANDOM.nextInt(data.size() + 1));
				}
			}

		},
		containsIndex {

			@Override
			public void run(final List<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.contains(new Integer(i));
				}
			}

		},
		containsRandom {

			@Override
			public void run(final List<Integer> data) throws Throwable {
				for(int i = 0; i < Main.COUNT; i++){
					data.contains(new Integer(Main.RANDOM.nextInt(Main.COUNT)));
				}
			}

		};

	}

	static enum ListBuilder implements Builder<List<Integer>> {

		arrayList {

			@Override
			public List<Integer> build() {
				return new ArrayList<Integer>(0);
			}

		},

		compactList {

			@Override
			public List<Integer> build() {
				return new CompactList<Integer>(0);
			}

		},

		compactArray {

			@Override
			public List<Integer> build() {
				return new CompactIntegerArray(0).values();
			}

		}

	};

	private static <GValue> void printARTest(A1Test<? super GValue> appendTest1, A1Test<? super GValue> removeTest2,
		Builder<? extends GValue> valueBuilder) {
		GValue value = valueBuilder.build();
		Tests.printA1Test(appendTest1, value);
		Tests.printA1Test(removeTest2, value);
	}

	private static <GValue> void printACRTest(A1Test<? super GValue> appendTest, A1Test<? super GValue> removeTest,
		A1Test<? super GValue> containsTest, Builder<? extends GValue> valueBuilder) {
		GValue value = valueBuilder.build();
		Tests.printA1Test(appendTest, value);
		Tests.printA1Test(containsTest, value);
		Tests.printA1Test(removeTest, value);
	}

	/**
	 * Diese Methode gibt das zurück.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {

		Tests.printA1Title();

		COUNT = 1024 * 1024;

		printACRTest(MapTest.addIndex, MapTest.removeIndex, MapTest.containsIndex, MapBuilder.hashMap);
		printACRTest(MapTest.addIndex, MapTest.removeIndex, MapTest.containsIndex, MapBuilder.treeMap);
		printACRTest(MapTest.addIndex, MapTest.removeIndex, MapTest.containsIndex, MapBuilder.compactEntryHashMap);
		printACRTest(MapTest.addIndex, MapTest.removeIndex, MapTest.containsIndex, MapBuilder.compactNavigableEntryMap);

		COUNT = 128 * 1024;

		printACRTest(MapTest.addRandom, MapTest.removeRandom, MapTest.containsRandom, MapBuilder.hashMap);
		printACRTest(MapTest.addRandom, MapTest.removeRandom, MapTest.containsRandom, MapBuilder.treeMap);
		printACRTest(MapTest.addRandom, MapTest.removeRandom, MapTest.containsRandom, MapBuilder.compactEntryHashMap);
		printACRTest(MapTest.addRandom, MapTest.removeRandom, MapTest.containsRandom, MapBuilder.compactNavigableEntryMap);

		COUNT = 1024 * 1024;

		printACRTest(SetTest.addIndex, SetTest.removeIndex, SetTest.containsIndex, SetBuilder.hashSet);
		printACRTest(SetTest.addIndex, SetTest.removeIndex, SetTest.containsIndex, SetBuilder.treeSet);
		printACRTest(SetTest.addIndex, SetTest.removeIndex, SetTest.containsIndex, SetBuilder.compactHashSet);
		printACRTest(SetTest.addIndex, SetTest.removeIndex, SetTest.containsIndex, SetBuilder.compactNavigableSet);

		COUNT = 128 * 1024;

		printACRTest(SetTest.addRandom, SetTest.removeRandom, SetTest.containsRandom, SetBuilder.hashSet);
		printACRTest(SetTest.addRandom, SetTest.removeRandom, SetTest.containsRandom, SetBuilder.treeSet);
		printACRTest(SetTest.addRandom, SetTest.removeRandom, SetTest.containsRandom, SetBuilder.compactHashSet);
		printACRTest(SetTest.addRandom, SetTest.removeRandom, SetTest.containsRandom, SetBuilder.compactNavigableSet);

		COUNT = 32 * 1024;

		printARTest(ListTest.addStartIndex, ListTest.removeStartIndex, ListBuilder.arrayList);
		printARTest(ListTest.addStartIndex, ListTest.removeStartIndex, ListBuilder.compactList);
		printARTest(ListTest.addStartIndex, ListTest.removeStartIndex, ListBuilder.compactArray);

		COUNT = 32 * 1024;

		printARTest(ListTest.addFinalIndex, ListTest.removeFinalIndex, ListBuilder.arrayList);
		printARTest(ListTest.addFinalIndex, ListTest.removeFinalIndex, ListBuilder.compactList);
		printARTest(ListTest.addFinalIndex, ListTest.removeFinalIndex, ListBuilder.compactArray);

		COUNT = 16 * 1024;

		printACRTest(ListTest.addCenterIndex, ListTest.removeCenterIndex, ListTest.containsIndex, ListBuilder.arrayList);
		printACRTest(ListTest.addCenterIndex, ListTest.removeCenterIndex, ListTest.containsIndex, ListBuilder.compactList);
		printACRTest(ListTest.addCenterIndex, ListTest.removeCenterIndex, ListTest.containsIndex, ListBuilder.compactArray);

		COUNT = 32 * 1024;

		printACRTest(ListTest.addRandomIndex, ListTest.removeRandomIndex, ListTest.containsRandom, ListBuilder.arrayList);
		printACRTest(ListTest.addRandomIndex, ListTest.removeRandomIndex, ListTest.containsRandom, ListBuilder.compactList);
		printACRTest(ListTest.addRandomIndex, ListTest.removeRandomIndex, ListTest.containsRandom, ListBuilder.compactArray);

	}

}
