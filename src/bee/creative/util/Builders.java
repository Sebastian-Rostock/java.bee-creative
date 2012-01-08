package bee.creative.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import bee.creative.util.Converters.ConverterLink;

public class Builders {

	static abstract class SetBuilder<GEntry, GData extends Set<GEntry>> implements Builder<GData> {

		final GData data;

		public SetBuilder(final GData data) {
			if(data == null) throw new NullPointerException();
			this.data = data;
		}

		public SetBuilder<GEntry, GData> clear() {
			this.data.clear();
			return this;
		}

		public SetBuilder<GEntry, GData> append(final GEntry e) {
			this.data.add(e);
			return this;
		}

		public SetBuilder<GEntry, GData> appendAll(final GEntry... entries) {
			return this.appendAll(Arrays.asList(entries));
		}

		public SetBuilder<GEntry, GData> appendAll(final Iterable<? extends GEntry> entries) {
			return this.appendAll(entries.iterator());
		}

		public SetBuilder<GEntry, GData> appendAll(final Iterator<? extends GEntry> entries) {
			while(entries.hasNext()){
				this.data.add(entries.next());
			}
			return this;
		}

		public SetBuilder<GEntry, GData> remove(final GEntry entry) {
			this.data.remove(entry);
			return this;
		}

		public SetBuilder<GEntry, GData> removeAll(final GEntry... entries) {
			return this.removeAll(Arrays.asList(entries));
		}

		public SetBuilder<GEntry, GData> removeAll(final Iterable<? extends GEntry> entries) {
			return this.removeAll(entries.iterator());
		}

		public SetBuilder<GEntry, GData> removeAll(final Iterator<? extends GEntry> entries) {
			while(entries.hasNext()){
				this.data.remove(entries.next());
			}
			return this;
		}

	}

	public static class TreeSetBuilder<GEntry> extends SetBuilder<GEntry, TreeSet<GEntry>> {

		/**
		 * Dieser Konstrukteur initialisiert das .
		 * 
		 * @param set
		 */
		public TreeSetBuilder(final TreeSet<GEntry> set) {
			super(set);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TreeSet<GEntry> build() {
			return new TreeSet<GEntry>(this.data);
		}

	}

	public static class HashSetBuilder<E> extends SetBuilder<E, HashSet<E>> {

		public HashSetBuilder(final HashSet<E> s) {
			super(s);
		}

		@Override
		public HashSet<E> build() {
			return new HashSet<E>(this.data);
		}

	}

	public static class LinkedHashSetBuilder<E> extends SetBuilder<E, LinkedHashSet<E>> {

		public LinkedHashSetBuilder(final LinkedHashSet<E> s) {
			super(s);
		}

		@Override
		public LinkedHashSet<E> build() {
			return new LinkedHashSet<E>(this.data);
		}

	}

	static abstract class MapBuilder<GKey, GValue, GData extends Map<GKey, GValue>> implements Builder<GData> {

		final GData data;

		public MapBuilder(final GData data) {
			if(data == null) throw new NullPointerException();
			this.data = data;
		}

		public MapBuilder<GKey, GValue, GData> clear() {
			this.data.clear();
			return this;
		}

		public MapBuilder<GKey, GValue, GData> copy(final GKey sourceKey, final GKey targetKey) {
			if(this.data.containsKey(sourceKey)){
				this.data.put(targetKey, this.data.get(sourceKey));
			}else{
				this.data.remove(targetKey);
			}
			return this;
		}

		public MapBuilder<GKey, GValue, GData> copyAll(final GKey... args) {
			return this.copyAll(Arrays.asList(args));
		}

		public MapBuilder<GKey, GValue, GData> copyAll(final Iterable<? extends GKey> args) {
			return this.copyAll(args.iterator());
		}

		public MapBuilder<GKey, GValue, GData> copyAll(final Iterator<? extends GKey> args) {
			while(args.hasNext()){
				final GKey sourceKey = args.next();
				if(!args.hasNext()) throw new IllegalStateException();
				final GKey targetKey = args.next();
				this.copy(sourceKey, targetKey);
			}
			return this;
		}

		public MapBuilder<GKey, GValue, GData> move(final GKey sourceKey, final GKey targetKey) {
			if(this.data.containsKey(sourceKey)){
				this.data.put(targetKey, this.data.remove(sourceKey));
			}else{
				this.data.remove(targetKey);
			}
			return this;
		}

		/**
		 * Diese Methode verschiebt die über ihre Schlüssel gegebenen Werte und gibt <code>this</code> zurück. Die Folge der
		 * Schlüssel alterniert dabei immer zwischen einem Quell- und einem Zielschlüssel.
		 * 
		 * @see MapBuilder#move(Object, Object)
		 * @param args Schlüsselfolge.
		 * @return <code>this</code>.
		 */
		public MapBuilder<GKey, GValue, GData> moveAll(final GKey... args) {
			return this.moveAll(Arrays.asList(args));
		}

		public MapBuilder<GKey, GValue, GData> moveAll(final Iterable<? extends GKey> args) {
			return this.moveAll(args.iterator());
		}

		public MapBuilder<GKey, GValue, GData> moveAll(final Iterator<? extends GKey> args) {
			while(args.hasNext()){
				final GKey sourceKey = args.next();
				if(!args.hasNext()) throw new IllegalStateException();
				final GKey targetKey = args.next();
				this.move(sourceKey, targetKey);
			}
			return this;
		}

		public MapBuilder<GKey, GValue, GData> append(final GKey key, final GValue value) {
			this.data.put(key, value);
			return this;
		}

		public MapBuilder<GKey, GValue, GData> appendAll(final Object... args) {
			return this.appendAll(Arrays.asList(args));
		}

		public MapBuilder<GKey, GValue, GData> appendAll(final Iterable<?> args) {
			return this.appendAll(args.iterator());
		}

		@SuppressWarnings ("unchecked")
		public MapBuilder<GKey, GValue, GData> appendAll(final Iterator<?> args) {
			while(args.hasNext()){
				final GKey key = (GKey)args.next();
				if(!args.hasNext()) throw new IllegalStateException();
				final GValue value = (GValue)args.next();
				this.data.put(key, value);
			}
			return this;
		}

		public MapBuilder<GKey, GValue, GData> appendAll(final GKey[] keys, final GValue[] values) {
			return this.appendAll(Arrays.asList(keys), Arrays.asList(values));
		}

		public MapBuilder<GKey, GValue, GData> appendAll(final Iterable<GKey> keys, final Iterable<GValue> values) {
			return this.appendAll(keys.iterator(), values.iterator());
		}

		public MapBuilder<GKey, GValue, GData> appendAll(final Iterator<GKey> keys, final Iterator<GValue> values) {
			// TODO
			return this;
		}

		public MapBuilder<GKey, GValue, GData> appendAll(final Map<? extends GKey, ? extends GValue> map) {
			this.data.putAll(map);
			return this;
		}

		public MapBuilder<GKey, GValue, GData> remove(final GKey key) {
			this.data.remove(key);
			return this;
		}

		public MapBuilder<GKey, GValue, GData> removeAll(final GKey... keys) {
			return this.removeAll(Arrays.asList(keys));
		}

		public MapBuilder<GKey, GValue, GData> removeAll(final Iterable<? extends GKey> keys) {
			return this.removeAll(keys.iterator());
		}

		public MapBuilder<GKey, GValue, GData> removeAll(final Iterator<? extends GKey> keys) {
			while(keys.hasNext()){
				this.data.remove(keys.next());
			}
			return this;
		}

	}

	static abstract class ListBuilder<GEntry, GData extends List<GEntry>> implements Builder<GData> {

		final GData data;

		public ListBuilder(final GData data) {
			if(data == null) throw new NullPointerException();
			this.data = data;
		}

		public ListBuilder<GEntry, GData> set(final int index, final GEntry entry) {
			this.data.set(index, entry);
			return this;
		}

		/**
		 * Diese Methode gibt das zurück.
		 * 
		 * @see Collections#sort(List, Comparator)
		 * @param comparator
		 * @return
		 */
		public ListBuilder<GEntry, GData> sort(final Comparator<? super GEntry> comparator) {
			Collections.sort(this.data, comparator);
			return this;
		}

		/**
		 * Diese Methode gibt das zurück.
		 * 
		 * @see Collections#swap(List, int, int)
		 * @param source
		 * @param target
		 * @return
		 */
		public ListBuilder<GEntry, GData> swap(final int source, final int target) {
			Collections.swap(this.data, source, target);
			return this;
		}

		public ListBuilder<GEntry, GData> move(final int source, final int target) {

			this.data.add(target, this.data.remove(source));
			return this;
		}

		public ListBuilder<GEntry, GData> clear() {
			this.data.clear();
			return this;
		}

		/**
		 * Diese Methode gibt das zurück.
		 * 
		 * @see Collections#rotate(List, int)
		 * @param distance
		 * @return
		 */
		public ListBuilder<GEntry, GData> rotate(final int distance) {
			Collections.rotate(this.data, distance);
			return this;
		}

		/**
		 * Diese Methode gibt das zurück.
		 * 
		 * @see Collections#shuffle(List, Random)
		 * @param random
		 * @return
		 */
		public ListBuilder<GEntry, GData> shuffle(final Random random) {
			Collections.shuffle(this.data, random);
			return this;
		}

		/**
		 * Diese Methode gibt das zurück.
		 * 
		 * @see Collections#reverse(List)
		 * @return
		 */
		public ListBuilder<GEntry, GData> reverse() {
			Collections.reverse(this.data);
			return this;
		}

		/**
		 * Diese Methode gibt das zurück.
		 * 
		 * @see Collections#replaceAll(List, Object, Object)
		 * @param source
		 * @param target
		 * @return
		 */
		public ListBuilder<GEntry, GData> replace(final GEntry source, final GEntry target) {
			for(final ListIterator<GEntry> iterator = this.data.listIterator(); iterator.hasNext();){
				if(Objects.equals(source, iterator.next())){
					iterator.set(target);
				}
			}
			return this;
		}

		public ListBuilder<GEntry, GData> insert(final int source, final int target) {
			this.data.add(target, this.data.get(source));
			return this;
		}

		public ListBuilder<GEntry, GData> append(final GEntry e) {
			this.data.add(e);
			return this;
		}

		public ListBuilder<GEntry, GData> appendAll(final GEntry... entries) {
			return this.appendAll(Arrays.asList(entries));
		}

		public ListBuilder<GEntry, GData> appendAll(final Iterable<? extends GEntry> entries) {
			return this.appendAll(entries.iterator());
		}

		public ListBuilder<GEntry, GData> appendAll(final Iterator<? extends GEntry> entries) {
			while(entries.hasNext()){
				this.data.add(entries.next());
			}
			return this;
		}

		public ListBuilder<GEntry, GData> insert(final int index, final GEntry e) {
			this.data.add(index, e);
			return this;
		}

		public ListBuilder<GEntry, GData> insertAll(final int index, final GEntry... entries) {
			return this.insertAll(index, Arrays.asList(entries));
		}

		public ListBuilder<GEntry, GData> insertAll(final int index, final Iterable<? extends GEntry> entries) {
			return this.insertAll(index, entries.iterator());
		}

		public ListBuilder<GEntry, GData> insertAll(final int index, final Iterator<? extends GEntry> entries) {
			for(int i = index; entries.hasNext(); i++){
				this.data.add(i, entries.next());
			}
			return this;
		}

		public ListBuilder<GEntry, GData> remove(final int index) {
			this.data.remove(index);
			return this;
		}

		public ListBuilder<GEntry, GData> remove(final GEntry entry) {
			this.data.remove(entry);
			return this;
		}

		public ListBuilder<GEntry, GData> removeAll(final GEntry... entries) {
			return this.removeAll(Arrays.asList(entries));
		}

		public ListBuilder<GEntry, GData> removeAll(final Iterable<? extends GEntry> entries) {
			return this.removeAll(entries.iterator());
		}

		public ListBuilder<GEntry, GData> removeAll(final Iterator<? extends GEntry> entries) {
			while(entries.hasNext()){
				this.data.remove(entries.next());
			}
			return this;
		}

	}

	public static class TreeMapBuilder<GKey, GValue> extends MapBuilder<GKey, GValue, TreeMap<GKey, GValue>> {

		public TreeMapBuilder(final TreeMap<GKey, GValue> data) {
			super(data);
		}

		@Override
		public TreeMap<GKey, GValue> build() {
			return new TreeMap<GKey, GValue>(this.data);
		}

	}

	public static final class CachedBuilder<GData> implements Builder<GData> {

		final int mode;

		/**
		 * Dieses Feld speichert den {@link Builder Builder}.
		 */
		final Builder<? extends GData> builder;

		Pointer<GData> pointer;

		public CachedBuilder(final int mode, final Builder<? extends GData> builder) {
			this.mode = mode;
			this.builder = builder;
		}

		public int mode() {
			return this.mode;
		}

		public Builder<? extends GData> builder() {
			return this.builder;
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

	}

	public static final class ConvertedBuilder<GInput, GOutput> implements Builder<GOutput> {

		/**
		 * Dieses Feld speichert den {@link Builder Builder}.
		 */
		final Builder<? extends GInput> builder;

		/**
		 * Dieses Feld speichert den {@link Converter Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		public ConvertedBuilder(final Converter<? super GInput, ? extends GOutput> converter,
			final Builder<? extends GInput> builder) throws NullPointerException {
			if(converter == null || builder == null) throw new NullPointerException();
			this.builder = builder;
			this.converter = converter;
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

	}

	public static <GKey extends Comparable<?>, GValue> MapBuilder<GKey, GValue, TreeMap<GKey, GValue>> treeMapBuilder() {
		return new TreeMapBuilder<GKey, GValue>(new TreeMap<GKey, GValue>());
	}

	public static <GKey, GValue> MapBuilder<GKey, GValue, TreeMap<GKey, GValue>> treeMapBuilder(
		final Comparator<? super GKey> comparator) {
		if(comparator == null) throw new NullPointerException();
		return new TreeMapBuilder<GKey, GValue>(new TreeMap<GKey, GValue>(comparator));
	}

}
