package bee.creative.fem;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Diese Klasse implementiert mit {@link #INSTANCES} eine statische Abbildung zur Verwaltung benannter Funktionen sowie einige generische Funktionen zur
 * Steuerung von Kontrollfluss und Variablen.
 * 
 * @see FEMFunction
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMUtil {

	/** Dieses Feld bildet von Namen auf Funktionen ab, die auch bequem über {@link #put(FEMFunction, String...)} modifiziert und über {@link #get(String)} gelesen
	 * werden können. */
	public static final Map<String, FEMFunction> INSTANCES = Collections.synchronizedMap(new HashMap<String, FEMFunction>());

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (condition: FEMBoolean; trueResult: FEMValue; falseResult: FEMValue): FEMValue}, deren
	 * Ergebniswert via {@code (condition ? trueResult : falseResult)} ermittelt wird. */
	public static final FEMFunction IF = FEMUtil.put(new FEMFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			FEMUtil.assertSize(frame, 3, 3);
			final FEMBoolean condition = FEMBoolean.from(frame.get(0), frame.context());
			final FEMValue result = frame.get(condition.value() ? 1 : 2).result();
			return result;
		}

	}, "if", "IF");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (variable: FEMVariable; value: FEMValue): FEMValue}, welche über
	 * {@link FEMVariable#update(FEMValue)} den Wert einer gegebenen {@link FEMVariable} setzt und den gegebenen Ergebniswert {@code value} liefert. */
	public static final FEMFunction SET = FEMUtil.put(new FEMFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			FEMUtil.assertSize(frame, 2, 2);
			final FEMVariable variable = FEMVariable.from(frame.get(0), frame.context());
			final FEMValue value = frame.get(1);
			variable.update(value);
			return value;
		}

	}, "set", "SET");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (variable: FEMVariable): FEMValue}, deren Ergebniswert über {@link FEMVariable#value()}
	 * ermittelt wird. */
	public static final FEMFunction GET = FEMUtil.put(new FEMFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			FEMUtil.assertSize(frame, 1, 1);
			final FEMVariable variable = FEMVariable.from(frame.get(0), frame.context());
			return variable.value();
		}

	}, "get", "GET");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (value: FEMValue): FEMVariable}, deren Ergebniswert via {@code FEMVariable.from(value)}
	 * ermittelt wird. */
	public static final FEMFunction VAR = FEMUtil.put(new FEMFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			FEMUtil.assertSize(frame, 1, 1);
			final FEMValue value = frame.get(0);
			final FEMVariable result = FEMVariable.from(value);
			return result;
		}

	}, "var", "VAR");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (param1; ...; paramN; result: FEMValue): FEMValue}, welche die gegebenen Parameter in der
	 * gegebenen Reigenfolge {@link FEMValue#result(boolean) rekursiv auswertet} und den letzten als Ergebniswert liefert. */
	public static final FEMFunction EVAL = FEMUtil.put(new FEMFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			FEMUtil.assertSize(frame, 1, Integer.MAX_VALUE);
			final int size = frame.size() - 1;
			for (int i = 0; i < size; i++) {
				frame.get(i).result();
			}
			final FEMValue result = frame.get(size).result();
			return result;
		}

	}, "eval", "EVAL");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (method: FEMHandler; params: FEMArray): FEMValue}, deren Ergebniswert via
	 * {@code method(params[0], params[1], ...)} ermittelt wird, d.h. über den Aufruf der als ersten Parameter gegeben Funktion mit den im zweiten Parameter
	 * gegebenen Parameterwertliste. */
	public static final FEMFunction CALL = FEMUtil.put(new FEMFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			FEMUtil.assertSize(frame, 2, 2);
			final FEMContext context = frame.context();
			final FEMArray array = FEMArray.from(frame.get(1), context);
			final FEMFunction method = FEMHandler.from(frame.get(0), context).value();
			final FEMFrame params = frame.withParams(array);
			return method.invoke(params);
		}

	}, "call", "CALL");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (param1; ...; paramN: FEMValue; method: FEMHandler): FEMValue}, deren Ergebniswert via
	 * {@code method(param1, ..., paramN)} ermittelt wird, d.h. über den Aufruf der als letzten Parameter gegeben Funktion mit den davor liegenden Parametern. */
	public static final FEMFunction APPLY = FEMUtil.put(new FEMFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			FEMUtil.assertSize(frame, 1, Integer.MAX_VALUE);
			final int index = frame.size() - 1;
			final FEMContext context = frame.context();
			final FEMArray array = frame.params().section(0, index);
			final FEMFunction method = FEMHandler.from(frame.get(index), context).value();
			final FEMFrame params = frame.withParams(array);
			return method.invoke(params);
		}

	}, "apply", "APPLY");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (method: FEMHandler): FEMInteger}, deren Ergebniswert der Anzahl der parameterlosen Aufrufe der
	 * gegebenen Methode entspricht. Die Methode wird mindestens ein Mal aufgerufen und muss immer einen {@link FEMBoolean} liefern. Sie wird nur dann wiederholt
	 * aufgerufen, wenn sie {@link FEMBoolean#TRUE} liefert. Die Ermittlung des Ergebniswerts {@code result} entspricht damit in etwa
	 * {@code for(result = 1; method(); result++);} */
	public static final FEMFunction REPEAT = FEMUtil.put(new FEMFunction() {

		@Override
		public FEMValue invoke(final FEMFrame frame) throws NullPointerException {
			FEMUtil.assertSize(frame, 1, 1);
			final FEMContext context = frame.context();
			final FEMFunction method = FEMHandler.from(frame.get(0), context).value();
			for (int count = 0; true; count++) {
				final FEMBoolean repeat = FEMBoolean.from(method.invoke(frame.withoutParams()), context);
				if (!repeat.value()) return FEMInteger.from(count);
			}
		}

	}, "repeat", "REPEAT");

	{}

	/** Diese Methode gibt die via {@link #put(FEMFunction, String...)} unter dem gegebenen Namen registrierte Funktion bzw. {@code null} zurück.<br>
	 * Sie ist eine Abkürzung für {@code INSTANCES.get(name)}.
	 * 
	 * @param name Name.
	 * @return Funktion oder {@code null}. */
	public static FEMFunction get(final String name) {
		return FEMUtil.INSTANCES.get(name);
	}

	/** Diese Methode registriert die gegebene Funktion unter den gegebenen Namen in {@link #INSTANCES} und gibt die Funktion zurück.
	 * 
	 * @param function Funktion.
	 * @param names Namen.
	 * @return {@code function}.
	 * @throws NullPointerException Wenn {@code names} {@code null} ist. */
	public static FEMFunction put(final FEMFunction function, final String... names) throws NullPointerException {
		for (final String name: names) {
			FEMUtil.INSTANCES.put(name, function);
		}
		return function;
	}

	/** Diese Methode setzt die {@link FEMProxy#set(FEMFunction) aufzurufende Funktion} des gegebenen {@link FEMProxy}, wenn diesem noch keinen zugeordnet ist,
	 * d.h. wenn {@link FEMProxy#get()} {@code null} liefert. Die aufzurufende Funktion des {@link FEMProxy} wird dazu über dessen {@link FEMProxy#name() Namen}
	 * ermittelt, d.h. {@link #get(String) FEMUtil.get(proxy.name())}.
	 * 
	 * @see #get(String)
	 * @see FEMProxy#get()
	 * @see FEMProxy#set(FEMFunction)
	 * @param proxy Platzhalter.
	 * @throws NullPointerException Wenn {@code proxy} {@code null} ist. */
	public static void update(final FEMProxy proxy) throws NullPointerException {
		if (proxy.get() != null) return;
		proxy.set(FEMUtil.get(proxy.name()));
	}

	/** Diese Methode ist eine Abkürzung für {@code updateAll(Arrays.asList(proxies))}.
	 * 
	 * @see #updateAll(Iterable)
	 * @param proxies Platzhalter.
	 * @throws NullPointerException Wenn {@code proxies} {@code null} ist. */
	public static void updateAll(final FEMProxy... proxies) throws NullPointerException {
		FEMUtil.updateAll(Arrays.asList(proxies));
	}

	/** Diese Methode setzt über {@link #update(FEMProxy)} die {@link FEMProxy#set(FEMFunction) aufzurufende Funktionen} der gegebenen {@link FEMProxy}.
	 * 
	 * @see #update(FEMProxy)
	 * @param proxies Platzhalter.
	 * @throws NullPointerException Wenn {@code proxies} {@code null} ist. */
	public static void updateAll(final Iterable<FEMProxy> proxies) throws NullPointerException {
		for (final FEMProxy proxy: proxies) {
			FEMUtil.update(proxy);
		}
	}

	/** Diese Methode prüft die Anzahl der {@link FEMFrame#size() zugesicherten Parameter} des gegebenen {@link FEMFrame Stapelrahmen}.
	 * 
	 * @param frame Stapelrahmen.
	 * @param minSize minimale zulässige Anzahl.
	 * @param maxSize maximale zulässige Anzahl.
	 * @throws IllegalArgumentException Wenn die Parameteranzahl ungültig ist. */
	public static void assertSize(final FEMFrame frame, final int minSize, final int maxSize) throws IllegalArgumentException {
		final int size = frame.size();
		if (size < minSize) throw new IllegalArgumentException("frame.size() < " + minSize);
		if (size > maxSize) throw new IllegalArgumentException("frame.size() > " + maxSize);
	}

}
