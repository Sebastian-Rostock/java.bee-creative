package bee.creative.str;

import java.util.Map.Entry;
import java.util.function.Consumer;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterator2;

public class STRValues implements Iterable2<Entry<Integer, String>> {

	public STRState owner() {
		return this.owner;
	}

	@Override
	public void forEach(Consumer<? super Entry<Integer, String>> action) {
		// TODO
		Iterable2.super.forEach(action);
	}

	public void forEach(RUN task) {
		// TODO
	}

	@Override
	public Iterator2<Entry<Integer, String>> iterator() {
		// TODO
		return null;
	}

	public static interface RUN {

		void run(int ref, String value);

	}

	STRValues(STRState owner) {
		this.owner = owner;
	}

	private STRState owner;

}