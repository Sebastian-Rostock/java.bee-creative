package bee.creative.str;

import java.util.Map.Entry;
import java.util.function.Consumer;
import bee.creative.fem.FEMString;
import bee.creative.lang.Objects;
import bee.creative.util.Entries;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterator2;

public class STRValues implements Iterable2<Entry<Integer, FEMString>> {

	public STRState owner() {
		return this.owner;
	}

	@Override
	public void forEach(Consumer<? super Entry<Integer, FEMString>> action) {
		this.forEach((ref, str) -> action.accept(Entries.from(ref, str)));
	}

	public void forEach(RUN task) {
		this.owner.forEachValue(task);
	}

	@Override
	public Iterator2<Entry<Integer, FEMString>> iterator() {
		return this.owner.valueIterator();
	}

	@Override
	public String toString() {
		return Objects.printIterable(false, this);
	}

	public static interface RUN {

		void run(int ref, FEMString str);

	}

	STRValues(STRState owner) {
		this.owner = owner;
	}

	private STRState owner;

}