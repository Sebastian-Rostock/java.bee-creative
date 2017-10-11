package bee.creative.fem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.util.Natives;
import bee.creative.util.Objects;

/** Diese Funktion kann zum Lesen und Schreiben von {@link Field nativen Datenfeldern} sowie zum Aufrufen von {@link Method nativen Methoden} und
 * {@link Constructor nativen Konstruktoren} eingesetzt werden. Der dieser Funktion zugrundeliegende {@link Member} kann hierbei als {@link Field},
 * {@link Method} oder {@link Constructor} gegeben sein.<br>
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
public abstract class FEMReflection extends FEMFunction {

	@SuppressWarnings ("javadoc")
	public static final class StaticField extends FEMReflection {

		public final Field field;

		StaticField(final Field field) {
			this.field = field;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) {
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
	public static final class StaticMethod extends FEMReflection {

		public final Method method;

		StaticMethod(final Method method) {
			this.method = method;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			try {
				final Object[] params = FEMReflection.params(frame, false);
				final Object result = this.method.invoke(null, params);
				return new FEMNative(result);
			} catch (final Exception cause) {
				throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
			}
		}

		@Override
		public final Method member() {
			return this.method;
		}

		@Override
		public final String toString() {
			return Natives.formatMethod(this.member());
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class StaticConstructor extends FEMReflection {

		public final Constructor<?> constructor;

		StaticConstructor(final Constructor<?> constructor) {
			this.constructor = constructor;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			try {
				final Object[] params = FEMReflection.params(frame, false);
				final Object result = this.constructor.newInstance(params);
				return new FEMNative(result);
			} catch (final Exception cause) {
				throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
			}
		}

		@Override
		public final Constructor<?> member() {
			return this.constructor;
		}

		@Override
		public final String toString() {
			return Natives.formatConstructor(this.member());
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class InstanceField extends FEMReflection {

		public final Field field;

		InstanceField(final Field field) {
			this.field = field;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) {
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
		public final Field member() {
			return this.field;
		}

		@Override
		public final String toString() {
			return Natives.formatField(this.member());
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class InstanceMethod extends FEMReflection {

		public final Method method;

		InstanceMethod(final Method method) {
			this.method = method;
		}

		{}

		@Override
		public final FEMValue invoke(final FEMFrame frame) {
			try {
				final Object[] params = FEMReflection.params(frame, true);
				final Object input = frame.get(0).data();
				final Object result = this.method.invoke(input, params);
				return new FEMNative(result);
			} catch (final Exception cause) {
				throw FEMException.from(cause).useContext(frame.context()).push(this.toString());
			}
		}

		@Override
		public final Method member() {
			return this.method;
		}

		@Override
		public final String toString() {
			return Natives.formatMethod(this.member());
		}

	}

	{}

	/** Diese Methode gibt die native Funktion zur gegebenen Pfadangabe zurück.<br>
	 * Die Pfadangabe kodiert hierbei eine Funktion, die eine Klasse liefert, an eine Methode bzw. einen Konstruktor delegiert oder ein Datenfeld liest bzw.
	 * schreibt. Die unterstützten Pfadangaben sind bei {@link Natives#parse(String)} beschrieben.
	 *
	 * @see Natives#parse(String)
	 * @see #from(Field)
	 * @see #from(Method)
	 * @see #from(Constructor)
	 * @param memberPath Pfad einer Klasse, einer Methode, eines Konstruktord oder eines Datenfelds.
	 * @return {@link FEMReflection}.
	 * @throws NullPointerException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst. */
	public static FEMFunction from(final String memberPath) throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberPath);
		if (object instanceof Class<?>) return new FEMNative(object);
		if (object instanceof Constructor<?>) return FEMReflection.from((Constructor<?>)object);
		if (object instanceof Method) return FEMReflection.from((Method)object);
		return FEMReflection.from((Field)object);
	}

	/** Diese Methode gibt eine Funktion zurück, mit welcher der Wert des gegebenen Datenfelds gelesen sowie geschrieben werden kann.
	 *
	 * @param field Datenfeld.
	 * @return Funktion zum gegebenen Datenfeld.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static FEMReflection from(final Field field) throws NullPointerException {
		return Modifier.isStatic(field.getModifiers()) ? new StaticField(field) : new InstanceField(field);
	}

	/** Diese Methode gibt eine Funktion zurück, die an die gegebene Methode delegiert.
	 *
	 * @param method Methode.
	 * @return Funktion zur gegebenen Methode.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist. */
	public static FEMReflection from(final Method method) throws NullPointerException {
		return Modifier.isStatic(method.getModifiers()) ? new StaticMethod(method) : new InstanceMethod(method);
	}

	/** Diese Methode gibt eine Funktion zurück, die an den gegebenen Konstruktor delegiert.
	 *
	 * @param constructor Konstruktor.
	 * @return Funktion zum gegebenen Konstruktor.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist. */
	public static FEMReflection from(final Constructor<?> constructor) throws NullPointerException {
		return new StaticConstructor(Objects.assertNotNull(constructor));
	}

	@SuppressWarnings ("javadoc")
	static Object[] params(final FEMFrame frame, final boolean skipFirst) {
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
	public final int hashCode() {
		return this.member().hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMReflection)) return false;
		return this.member().equals(((FEMReflection)object).member());
	}

}