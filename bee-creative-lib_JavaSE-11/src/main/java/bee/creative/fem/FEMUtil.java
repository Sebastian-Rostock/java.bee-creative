package bee.creative.fem;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import bee.creative.util.HashMap;

/** Diese Klasse implementiert mit {@link #FUNCTIONS} eine statische Abbildung zur Verwaltung benannter Funktionen sowie einige generische Funktionen zur
 * Steuerung von Kontrollfluss und Handhabung von Variablen.
 *
 * @see FEMFunction
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface FEMUtil {

	/** Dieses Feld bildet von Namen auf Funktionen ab, die auch bequem über {@link #put(FEMFunction, String...)} modifiziert und über {@link #get(String)}
	 * gelesen werden können. */
	Map<String, FEMFunction> FUNCTIONS = Collections.synchronizedMap(new HashMap<>());

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (condition: FEMBoolean; trueResult: FEMValue; falseResult: FEMValue): FEMValue}, deren
	 * Ergebniswert über {@code (condition ? trueResult : falseResult)} ermittelt wird. */
	FEMFunction IF = FEMUtil.put(frame -> {
		FEMUtil.assertSize(frame, 3, 3);
		var con = frame.context().dataFrom(frame.get(0), FEMBoolean.TYPE);
		var res = frame.get(con.value() ? 1 : 2).result();
		return res;
	}, "if", "IF");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (variable: FEMVariable; value: FEMValue): FEMValue}, welche über
	 * {@link FEMVariable#set(FEMValue)} den Wert einer gegebenen {@link FEMVariable} setzt und den gegebenen Ergebniswert {@code value} liefert. */
	FEMFunction SET = FEMUtil.put(frame -> {
		FEMUtil.assertSize(frame, 2, 2);
		var var = frame.context().dataFrom(frame.get(0), FEMVariable.TYPE);
		var res = frame.get(1);
		var.set(res);
		return res;
	}, "set", "SET");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (variable: FEMVariable): FEMValue}, deren Ergebniswert über {@link FEMVariable#get()}
	 * ermittelt wird. */
	FEMFunction GET = FEMUtil.put(frame -> {
		FEMUtil.assertSize(frame, 1, 1);
		var var = frame.context().dataFrom(frame.get(0), FEMVariable.TYPE);
		var res = var.get();
		return res;
	}, "get", "GET");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (value: FEMValue): FEMVariable}, deren Ergebniswert über {@code FEMVariable.from(value)}
	 * ermittelt wird. */
	FEMFunction VAR = FEMUtil.put(frame -> {
		FEMUtil.assertSize(frame, 1, 1);
		var val = frame.get(0);
		var res = FEMVariable.from(val);
		return res;
	}, "var", "VAR");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (param1; ...; paramN; result: FEMValue): FEMValue}, welche die gegebenen Parameter in der
	 * gegebenen Reihenfolge {@link FEMValue#result(boolean) rekursiv auswertet} und den letzten als Ergebniswert liefert. */
	FEMFunction EVAL = FEMUtil.put(frame -> {
		FEMUtil.assertSize(frame, 1, Integer.MAX_VALUE);
		var size = frame.size() - 1;
		for (var i = 0; i < size; i++) {
			frame.get(i).result();
		}
		var res = frame.get(size).result();
		return res;
	}, "eval", "EVAL");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (method: FEMHandler; params: FEMArray): FEMValue}, deren Ergebniswert via
	 * {@code method(params[0], params[1], ...)} ermittelt wird, d.h. über den Aufruf der als ersten Parameter gegeben Funktion mit den im zweiten Parameter
	 * gegebenen Parameterwertliste. */
	FEMFunction CALL = FEMUtil.put(frame -> {
		FEMUtil.assertSize(frame, 2, 2);
		var arr = frame.context().dataFrom(frame.get(1), FEMArray.TYPE);
		var fun = frame.get(0).toFunction();
		var res = fun.invoke(frame.withParams(arr));
		return res;
	}, "call", "CALL");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (param1; ...; paramN: FEMValue; method: FEMHandler): FEMValue}, deren Ergebniswert via
	 * {@code method(param1, ..., paramN)} ermittelt wird, d.h. über den Aufruf der als letzten Parameter gegeben Funktion mit den davor liegenden Parametern. */
	FEMFunction APPLY = FEMUtil.put(frame -> {
		FEMUtil.assertSize(frame, 1, Integer.MAX_VALUE);
		var idx = frame.size() - 1;
		var arr = frame.params().section(0, idx);
		var fun = frame.get(idx).toFunction();
		var res = fun.invoke(frame.withParams(arr));
		return res;
	}, "apply", "APPLY");

	/** Dieses Feld speichert eine Funktion mit der Signatur {@code (method: FEMHandler): FEMInteger}, deren Ergebniswert der Anzahl der parameterlosen Aufrufe
	 * der gegebenen Methode entspricht. Die Methode wird mindestens ein Mal aufgerufen und muss immer einen {@link FEMBoolean} liefern. Sie wird nur dann
	 * wiederholt aufgerufen, wenn sie {@link FEMBoolean#TRUE} liefert. Die Ermittlung des Ergebniswerts {@code result} entspricht damit in etwa
	 * {@code for(result = 1; method(); result++);} */
	FEMFunction REPEAT = FEMUtil.put(frame -> {
		FEMUtil.assertSize(frame, 1, 1);
		var ctx = frame.context();
		var fun = frame.get(0).toFunction();
		for (var count = 1; true; count++) {
			var rep = ctx.dataFrom(fun.invoke(frame.withoutParams()), FEMBoolean.TYPE);
			if (!rep.value()) return FEMInteger.from(count);
		}
	}, "repeat", "REPEAT");

	/** Diese Methode gibt die über {@link #put(FEMFunction, String...)} unter dem gegebenen Namen registrierte Funktion bzw. {@code null} zurück. Sie ist eine
	 * Abkürzung für {@code INSTANCES.get(name)}.
	 *
	 * @param name Name.
	 * @return Funktion oder {@code null}. */
	static FEMFunction get(String name) {
		return FEMUtil.FUNCTIONS.get(name);
	}

	/** Diese Methode registriert die gegebene Funktion unter den gegebenen Namen in {@link #FUNCTIONS} und gibt die Funktion zurück.
	 *
	 * @param function Funktion.
	 * @param names Namen.
	 * @return {@code function}.
	 * @throws NullPointerException Wenn {@code names} {@code null} ist. */
	static FEMFunction put(FEMFunction function, String... names) throws NullPointerException {
		for (var name: names) {
			FEMUtil.FUNCTIONS.put(name, function);
		}
		return function;
	}

	/** Diese Methode setzt die {@link FEMProxy#set(FEMFunction) aufzurufende Funktion} des gegebenen {@link FEMProxy}, wenn diesem noch keinen zugeordnet ist,
	 * d.h. wenn {@link FEMProxy#get()} {@code null} liefert. Die aufzurufende Funktion des {@link FEMProxy} wird dazu über dessen {@link FEMProxy#name() Namen}
	 * ermittelt, d.h. {@link #get(String) FEMUtil.get(proxy.getName().toString())}.
	 *
	 * @see #get(String)
	 * @see FEMProxy#get()
	 * @see FEMProxy#set(FEMFunction)
	 * @param proxy Platzhalter.
	 * @throws NullPointerException Wenn {@code proxy} {@code null} ist. */
	static void update(FEMProxy proxy) throws NullPointerException {
		if (proxy.get() != null) return;
		proxy.set(FEMUtil.get(proxy.name().toString()));
	}

	/** Diese Methode ist eine Abkürzung für {@code updateAll(Arrays.asList(proxies))}.
	 *
	 * @see #updateAll(Iterable)
	 * @param proxies Platzhalter.
	 * @throws NullPointerException Wenn {@code proxies} {@code null} ist. */
	static void updateAll(FEMProxy... proxies) throws NullPointerException {
		FEMUtil.updateAll(Arrays.asList(proxies));
	}

	/** Diese Methode setzt über {@link #update(FEMProxy)} die {@link FEMProxy#set(FEMFunction) aufzurufende Funktionen} der gegebenen {@link FEMProxy}.
	 *
	 * @see #update(FEMProxy)
	 * @param proxies Platzhalter.
	 * @throws NullPointerException Wenn {@code proxies} {@code null} ist. */
	static void updateAll(Iterable<FEMProxy> proxies) throws NullPointerException {
		for (var proxy: proxies) {
			FEMUtil.update(proxy);
		}
	}

	/** Diese Methode prüft die Anzahl der {@link FEMFrame#size() zugesicherten Parameter} des gegebenen {@link FEMFrame Stapelrahmen}.
	 *
	 * @param frame Stapelrahmen.
	 * @param minSize minimale zulässige Anzahl.
	 * @param maxSize maximale zulässige Anzahl.
	 * @throws IllegalArgumentException Wenn die Parameteranzahl ungültig ist. */
	static void assertSize(FEMFrame frame, int minSize, int maxSize) throws IllegalArgumentException {
		var size = frame.size();
		if (size < minSize) throw new IllegalArgumentException("frame.size() < " + minSize);
		if (size > maxSize) throw new IllegalArgumentException("frame.size() > " + maxSize);
	}

}
