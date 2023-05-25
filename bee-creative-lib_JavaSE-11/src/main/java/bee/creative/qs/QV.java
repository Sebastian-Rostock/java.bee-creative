package bee.creative.qs;

import java.time.Duration;
import java.time.Period;
import bee.creative.lang.Objects;

/*
 * TODO filter für QNSet, welcher über einen index provider erzeugt werden kann. über den indexprovider können den knoten mit textwert externe indizierte daten
 * zugeordnet werden
 */

// QN.value -> integer (long, biginteger)
// QN.value -> decimal (
// QN.value -> datetime (date, time,datetime)x(zone,local)
// QN.value -> duration (months, millis)
public interface QV<T> {

	public interface QVT<T> extends QV<T> {

	}

	public interface QVR<T> extends QV<T> {

	}

	QVT<Long> AS_LONG = () -> Long.class;

	QVR<Long> AS_LONG_LESS_THEN = () -> Long.class;

	QVR<Long> AS_LONG_LESS_THEN_OR_EQUAL_TO = () -> Long.class;

	QVR<Long> AS_LONG_GRATER_THEN = () -> Long.class;

	QVR<Long> AS_LONG_GRATER_THEN_OR_EQUAL_TO = () -> Long.class;

 


	Class<T> type();

}
