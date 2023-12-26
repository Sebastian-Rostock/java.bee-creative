package bee.creative.qs;

import java.util.Arrays;

public interface QOSet2<ITEM> extends QO {

	boolean clear() throws NullPointerException, IllegalArgumentException;

	default boolean insert(ITEM item) throws NullPointerException, IllegalArgumentException {
		return insertAll(Arrays.asList(item));
	}

	boolean insertAll(Iterable<? extends ITEM> items) throws NullPointerException, IllegalArgumentException;

	default boolean delete(ITEM item) throws NullPointerException, IllegalArgumentException {
		return deleteAll(Arrays.asList(item));
	}

	boolean deleteAll(Iterable<? extends ITEM> items) throws NullPointerException, IllegalArgumentException;

}
