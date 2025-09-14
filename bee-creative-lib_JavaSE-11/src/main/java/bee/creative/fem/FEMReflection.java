package bee.creative.fem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Funktion kann zum Lesen und Schreiben von {@link Field nativen Datenfeldern} sowie zum Aufrufen von {@link Method nativen Methoden} und
 * {@link Constructor nativen Konstruktoren} eingesetzt werden. Der dieser Funktion zugrundeliegende {@link Member} kann hierbei als {@link Field},
 * {@link Method} oder {@link Constructor} gegeben sein.
 * <h4>Datenfelder</h4>
 * <p>
 * Native Funktionen zu klassengebundenen Datenfeldern nutzen zum Lesen die Signatur {@code (): FEMNative} und liefern den Ergebniswert
 * {@code new FEMNative(this.member().get(null))}. Zum Schreiben verwenden sie dagegen die Signatur {@code (value: FEMNative): FEMNative}, führen
 * {@code this.member().set(null, value.data())} aus und liefern {@link FEMNative#NULL}. Analog dazu nutzen die Funktionen zu instanzgebundenen Datenfeldern zum
 * Lesen die Signatur {@code (object: FEMNative): FEMNative} und liefern den Ergebniswert {@code new FEMNative(this.member().get(object.data()))}. Zum Schreiben
 * verwenden sie dann die Signatur {@code (object, value: FEMNative): FEMNative}, führen {@code this.member().set(object.data(), value.data())} aus und liefern
 * ebenfalls {@link FEMNative#NULL}.
 * </p>
 * <h4>Methoden</h4>
 * <p>
 * Native Funktionen zu klassengebundenen Methoden haben die Signatur {@code (param1, ..., paramN: FEMNative): FEMNative} und liefern den Ergebniswert
 * {@code new FEMNative(this.member().invoke(null, param1.data(), ..., paramN.data()))}. Analog dazu haben die Funktionen zu instanzgebundenen Methoden die
 * Signatur {@code (object, param1, ..., paramN: FEMNative): FEMNative} und liefern den Ergebniswert
 * {@code new FEMNative(this.member().invoke(object.data(), param1.data(), ..., paramN.data()))}.
 * <p>
 * <h4>Konstruktoren</h4>
 * <p>
 * Native Funktionen zu Konstruktoren haben die Signatur {@code (param1, ..., paramN: FEMNative): FEMNative} und liefern den Ergebniswert
 * {@code new FEMNative(this.member().newInstance(param1.data(), ..., paramN.data()))}.
 * <p>
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMReflection implements FEMFunction {

	/** Diese Methode gibt eine Funktion zurück, mit welcher der Wert des gegebenen Datenfelds gelesen sowie geschrieben werden kann.
	 *
	 * @param field Datenfeld.
	 * @return Funktion zum gegebenen Datenfeld.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static FEMReflection from(Field field) throws NullPointerException {
		return Modifier.isStatic(field.getModifiers()) ? new StaticField(field) : new InstanceField(field);
	}

	/** Diese Methode gibt die native Funktion zur gegebenen Pfadangabe zurück. Die Pfadangabe kodiert hierbei eine Funktion, die eine Klasse liefert, an eine
	 * Methode bzw. einen Konstruktor delegiert oder ein Datenfeld liest bzw. schreibt. Die unterstützten Pfadangaben sind bei {@link Natives#parseNative(String)}
	 * beschrieben.
	 *
	 * @see Natives#parseNative(String)
	 * @see #from(Field)
	 * @see #from(Method)
	 * @see #from(Constructor)
	 * @param memberPath Pfad einer Klasse, einer Methode, eines Konstruktors oder eines Datenfelds.
	 * @return {@link FEMReflection}.
	 * @throws NullPointerException Wenn {@link Natives#parseNative(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link Natives#parseNative(String)} eine entsprechende Ausnahme auslöst. */
	public static FEMFunction from(String memberPath) throws NullPointerException, IllegalArgumentException {
		var object = Natives.parseNative(memberPath);
		if (object instanceof Class<?>) return new FEMNative(object);
		if (object instanceof Constructor<?>) return FEMReflection.from((Constructor<?>)object);
		if (object instanceof Method) return FEMReflection.from((Method)object);
		return FEMReflection.from((Field)object);
	}

	/** Diese Methode gibt eine Funktion zurück, die an die gegebene Methode delegiert.
	 *
	 * @param method Methode.
	 * @return Funktion zur gegebenen Methode.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist. */
	public static FEMReflection from(Method method) throws NullPointerException {
		return Modifier.isStatic(method.getModifiers()) ? new StaticMethod(method) : new InstanceMethod(method);
	}

	/** Diese Methode gibt eine Funktion zurück, die an den gegebenen Konstruktor delegiert.
	 *
	 * @param constructor Konstruktor.
	 * @return Funktion zum gegebenen Konstruktor.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist. */
	public static FEMReflection from(Constructor<?> constructor) throws NullPointerException {
		return new StaticConstructor(Objects.notNull(constructor));
	}

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

	@Override
	public final int hashCode() {
		return this.member().hashCode();
	}

	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMReflection)) return false;
		var that = (FEMReflection)object;
		return this.member().equals(that.member());
	}

	public static final class StaticField extends FEMReflection {

		public final Field field;

		@Override
		public FEMValue invoke(FEMFrame frame) {
			try {
				switch (frame.size()) {
					case 0: {
						var getValue = this.field.get(null);
						return new FEMNative(getValue);
					}
					case 1: {
						var setValue = frame.get(0).data();
						this.field.set(null, setValue);
						return FEMNative.NULL;
					}
					default:
						throw new IllegalArgumentException();
				}
			} catch (Exception cause) {
				throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
			}
		}

		@Override
		public Field member() {
			return this.field;
		}

		@Override
		public String toString() {
			return Natives.printField(this.field);
		}

		StaticField(final Field field) {
			this.field = field;
		}

	}

	public static final class StaticMethod extends FEMReflection {

		public final Method method;

		@Override
		public FEMValue invoke(FEMFrame frame) {
			try {
				var params = FEMReflection.params(frame, false);
				var result = this.method.invoke(null, params);
				return new FEMNative(result);
			} catch (Exception cause) {
				throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
			}
		}

		@Override
		public Method member() {
			return this.method;
		}

		@Override
		public String toString() {
			return Natives.printMethod(this.method);
		}

		StaticMethod(Method method) {
			this.method = method;
		}

	}

	public static final class StaticConstructor extends FEMReflection {

		public final Constructor<?> constructor;

		@Override
		public FEMValue invoke(FEMFrame frame) {
			try {
				var params = FEMReflection.params(frame, false);
				var result = this.constructor.newInstance(params);
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
		public String toString() {
			return Natives.printConstructor(this.constructor);
		}

		StaticConstructor(Constructor<?> constructor) {
			this.constructor = constructor;
		}

	}

	public static final class InstanceField extends FEMReflection {

		public final Field field;

		@Override
		public FEMValue invoke(FEMFrame frame) {
			try {
				switch (frame.size()) {
					case 1: {
						var getInput = frame.get(0).data();
						var getValue = this.field.get(getInput);
						return new FEMNative(getValue);
					}
					case 2: {
						var setInput = frame.get(0).data();
						var setValue = frame.get(1).data();
						this.field.set(setInput, setValue);
						return FEMNative.NULL;
					}
					default:
						throw new IllegalArgumentException();
				}
			} catch (Exception cause) {
				throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
			}
		}

		@Override
		public Field member() {
			return this.field;
		}

		@Override
		public String toString() {
			return Natives.printField(this.field);
		}

		InstanceField(Field field) {
			this.field = field;
		}

	}

	public static final class InstanceMethod extends FEMReflection {

		public final Method method;

		@Override
		public FEMValue invoke(FEMFrame frame) {
			try {
				var params = FEMReflection.params(frame, true);
				var input = frame.get(0).data();
				var result = this.method.invoke(input, params);
				return new FEMNative(result);
			} catch (Exception cause) {
				throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
			}
		}

		@Override
		public Method member() {
			return this.method;
		}

		@Override
		public String toString() {
			return Natives.printMethod(this.method);
		}

		InstanceMethod(Method method) {
			this.method = method;
		}

	}

	private static Object[] params(FEMFrame frame, boolean skipFirst) {
		var offset = skipFirst ? 1 : 0;
		var length = frame.size() - offset;
		var result = new Object[length];
		for (var i = 0; i < length; i++) {
			result[i] = frame.get(i + offset).data();
		}
		return result;
	}

}