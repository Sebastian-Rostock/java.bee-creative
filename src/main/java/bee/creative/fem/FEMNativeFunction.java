package bee.creative.fem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.util.Natives;

/** Diese Funktion kann zum Lesen und Schreiben von {@link Field nativen Datenfeldern} sowie zum Aufrufen von {@link Method nativen Methoden} und
 * {@link Constructor nativen Konstruktoren} eingesetzt werden. Der dieser Funktion zugrundeliegende {@link Member} kann hierbei als {@link Field},
 * {@link Method} oder {@link Constructor} gegeben sein.<br>
 * <h4>Datenfelder</h4>
 * <p>
 * Native Funktionen zu klassengebundenen Datenfeldern nutzen zum Lesen die Signatur
 * {@code (): FEMNative» und liefern den Ergebniswert «new FEMNative(this.member().get(null))}. Zum Schreiben verwenden sie dagegen die Signatur
 * {@code (value: FEMNative): FEMNative}, führen {@code this.member().set(null, value.data())} aus und liefern {@link FEMNative#NULL}. Analog dazu nutzen die
 * Funktionen zu instanzgebundenen Datenfeldern zum Lesen die Signatur {@code (object: FEMNative): FEMNative} und liefern den Ergebniswert
 * {@code new FEMNative(this.member().get(object.data()))}. Zum Schreiben verwenden sie dann die Signatur {@code ( object, value: FEMNative): FEMNative}, führen
 * {@code this.member().set(object.data(), value.data())} aus und liefern ebenfalls {@link FEMNative#NULL}.
 * </p>
 * <h4>Methoden</h4>
 * <p>
 * Native Funktionen zu klassengebundenen Methoden haben die Signatur {@code (param1, ..., paramN: FEMNative): FEMNative} und liefern den Ergebniswert
 * {@code new FEMNative(this.member().invoke(null, param1.data(), …, paramN.data()))}. Analog dazu haben die Funktionen zu instanzgebundenen Methoden die
 * Signatur {@code (object, param1, ..., paramN: FEMNative): FEMNative} und liefern den Ergebniswert
 * {@code new FEMNative-(this.member().invoke(object.data(), param1.data(), …, paramN.data()))}.
 * <p>
 * <h4>Konstruktoren</h4>
 * <p>
 * Native Funktionen zu Konstruktoren haben die Signatur {@code (param1, ..., paramN: FEMNative): FEMNative} und liefern den Ergebniswert
 * {@code new FEMNative(this.member().newInstance(param1.data(), …, paramN.data()))}.
 * <p>
 * Diese Klasse stellt {@link FEMFunction Funktionen} zum Lesen und Schreiben von sowie zum Aufrufen von und bereit.
 * 
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMNativeFunction extends FEMBaseFunction {

	@SuppressWarnings ("javadoc")
	static final class NativeStaticField extends FEMNativeFunction {

		public final Field field;

		NativeStaticField(final Field field) {
			this.field = field;
		}

		{}

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			try {
				switch (frame.size()) {
					case 0:
						final Object getValue = this.field.get(null);
						return new FEMNative(getValue);
					case 1:
						final Object setValue = frame.get(0).data();
						this.field.set(null, setValue);
						return FEMNative.NULL;
				}
				throw new IllegalArgumentException();
			} catch (final Exception cause) {
				throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
			}
		}

		@Override
		public final Field member() {
			return this.field;
		}

		@Override
		public final String toString() {
			return Natives.formatField(this.member());
		}

	}

	@SuppressWarnings ("javadoc")
	static final class NativeObjectField extends FEMNativeFunction {

		public final Field field;

		NativeObjectField(final Field field) {
			this.field = field;
		}

		{}

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			try {
				switch (frame.size()) {
					case 1:
						final Object getInput = frame.get(0).data();
						final Object getValue = this.field.get(getInput);
						return new FEMNative(getValue);
					case 2:
						final Object setInput = frame.get(0).data();
						final Object setValue = frame.get(1).data();
						this.field.set(setInput, setValue);
						return FEMNative.NULL;
				}
				throw new IllegalArgumentException();
			} catch (final Exception cause) {
				throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
			}
		}

		@Override
		public Field member() {
			return this.field;
		}

		@Override
		public final String toString() {
			return Natives.formatField(this.member());
		}

	}

	@SuppressWarnings ("javadoc")
	static final class NativeStaticMethod extends FEMNativeFunction {

		public final Method method;

		NativeStaticMethod(final Method method) {
			this.method = method;
		}

		{}

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			try {
				final Object[] params = FEMNativeFunction._params_(frame, false);
				final Object result = this.method.invoke(null, params);
				return new FEMNative(result);
			} catch (final Exception cause) {
				throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
			}
		}

		@Override
		public Method member() {
			return this.method;
		}

		@Override
		public final String toString() {
			return Natives.formatMethod(this.member());
		}

	}

	@SuppressWarnings ("javadoc")
	static final class NativeObjectMethod extends FEMNativeFunction {

		public final Method method;

		NativeObjectMethod(final Method method) {
			this.method = method;
		}

		{}

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			try {
				final Object[] params = FEMNativeFunction._params_(frame, true);
				final Object input = frame.get(0).data();
				final Object result = this.method.invoke(input, params);
				return new FEMNative(result);
			} catch (final Exception cause) {
				throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
			}
		}

		@Override
		public Method member() {
			return this.method;
		}

		@Override
		public final String toString() {
			return Natives.formatMethod(this.member());
		}

	}

	@SuppressWarnings ("javadoc")
	static final class NativeConstructor extends FEMNativeFunction {

		public final Constructor<?> constructor;

		NativeConstructor(final Constructor<?> constructor) {
			this.constructor = constructor;
		}

		{}

		@Override
		public FEMValue invoke(final FEMFrame frame) {
			try {
				final Object[] params = FEMNativeFunction._params_(frame, false);
				final Object result = this.constructor.newInstance(params);
				return new FEMNative(result);
			} catch (final Exception cause) {
				throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
			}
		}

		@Override
		public Constructor<?> member() {
			return this.constructor;
		}

		@Override
		public final String toString() {
			return Natives.formatConstructor(this.member());
		}

	}

	{}

	/** Diese Methode gibt die native Funktion zur gegebenen Pfadangabe zurück.<br>
	 * Die Pfadangabe kodiert hierbei eine Funktion, die eine Klasse liefert, an eine Methode bzw. einen Konstruktor delegiert oder ein Datenfeld liest bzw.
	 * schreibt. Die folgenden Pfadangaben werden unterstützt:
	 * <p>
	 * <h4>{@code "CLASS_PATH.class"}</h4> Dieser Pfad ergibt {@link FEMNative#from(Object) from(CLASS_PATH.class)}.
	 * <p>
	 * <h4>{@code "CLASS_PATH.FIELD_NAME"}</h4> Dieser Pfad ergibt {@link #from(Field) from(CLASS_PATH.class.getDeclaredField("FIELD_NAME"))}.
	 * <p>
	 * <h4>{@code "CLASS_PATH.new(TYPE_1,...,TYPE_N)"}</h4> Dieser Pfad ergibt {@link #from(Constructor)
	 * from(CLASS_PATH.class.getDeclaredConstructor(TYPE_1.class, ..., TYPE_N.class))}.
	 * <p>
	 * <h4>{@code "CLASS_PATH.METHOD_NAME(TYPE1_1,...,TYPE_N)"}</h4> Dieser Pfad ergibt {@link #from(Method)
	 * from(CLASS_PATH.class.getDeclaredMethod("METHOD_NAME", TYPE_1.class, ..., TYPE_N.class))}.
	 * <p>
	 * 
	 * @see #from(Field)
	 * @see #from(Method)
	 * @see #from(Constructor)
	 * @param memberPath Pfad einer Klasse, einer Methode, eines Konstruktord oder eines Datenfelds.
	 * @return {@link FEMNativeFunction}.
	 * @throws NullPointerException Wenn {@code memberPath} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link Natives#parseMethod(String)} oder {@link Natives#parseConstructor(String)} eine entsprechende Ausnahme
	 *         auslöst.
	 * @throws ReflectiveOperationException Wenn {@link Natives#parseField(String)}, {@link Natives#parseMethod(String)}, {@link Natives#parseConstructor(String)}
	 *         oder {@link Natives#parseClass(String)} eine entsprechende Ausnahme auslöst. */
	public static FEMFunction from(final String memberPath) throws NullPointerException, IllegalArgumentException, ReflectiveOperationException {
		if (memberPath.endsWith(".class")) return new FEMNative(Natives.parseClass(memberPath.substring(0, memberPath.length() - 6)));
		if (memberPath.contains(".new(")) return FEMNativeFunction.from(Natives.parseConstructor(memberPath));
		if (memberPath.contains("(")) return FEMNativeFunction.from(Natives.parseMethod(memberPath));
		return FEMNativeFunction.from(Natives.parseField(memberPath));
	}

	/** Diese Methode gibt eine Funktion zurück, mit welcher der Wert des gegebenen Datenfelds gelesen sowie geschrieben werden kann.
	 * 
	 * @param field Datenfeld.
	 * @return Funktion zum gegebenen Datenfeld.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static FEMNativeFunction from(final Field field) throws NullPointerException {
		return Modifier.isStatic(field.getModifiers()) ? new NativeStaticField(field) : new NativeObjectField(field);
	}

	/** Diese Methode gibt eine Funktion zurück, die an die gegebene Methode delegiert.
	 * 
	 * @param method Methode.
	 * @return Funktion zur gegebenen Methode.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist. */
	public static FEMNativeFunction from(final Method method) throws NullPointerException {
		return Modifier.isStatic(method.getModifiers()) ? new NativeStaticMethod(method) : new NativeObjectMethod(method);
	}

	/** Diese Methode gibt eine Funktion zurück, die an den gegebenen Konstruktor delegiert.
	 * 
	 * @param constructor Konstruktor.
	 * @return Funktion zum gegebenen Konstruktor.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist. */
	public static FEMNativeFunction from(final Constructor<?> constructor) throws NullPointerException {
		if (constructor == null) throw new NullPointerException("constructor = null");
		return new NativeConstructor(constructor);
	}

	@SuppressWarnings ("javadoc")
	static Object[] _params_(final FEMFrame frame, final boolean skipFirst) {
		final int offset = skipFirst ? 1 : 0, length = frame.size() - offset;
		final Object[] result = new Object[length];
		for (int i = 0; i < length; i++) {
			result[i] = frame.get(i + offset).data();
		}
		return result;
	}

	{}

	/** Diese Methode gibt den {@link Member} zurück, auf den sich die Methode {@link #invoke(FEMFrame)} bezieht.
	 * 
	 * @return {@link Member}. */
	public abstract Member member();

	/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #member()} {@code static} ist.
	 * 
	 * @return Bezugskennzeichnung. */
	public final boolean isStatic() {
		return Modifier.isStatic(this.member().getModifiers());
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #member()} ein {@link Field} ist.
	 * 
	 * @return Feldkennzeichnung. */
	public final boolean isField() {
		return this.member() instanceof Field;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #member()} eine {@link Method} ist.
	 * 
	 * @return Methodenkennzeichung. */
	public final boolean isMethod() {
		return this.member() instanceof Method;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #member()} ein {@link Constructor} ist.
	 * 
	 * @return Konstruktorkennzeichung. */
	public final boolean isConstructor() {
		return this.member() instanceof Constructor<?>;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.put(this.toString());
	}

}