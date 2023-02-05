package bee.creative.doc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

/** Diese Klasse implementiert ein {@link Doclet}, welches die Inhalte einer {@link RootDoc} in eine {@code html}-Datei exportiert.<br>
 * Der Name dieser Datei wird über die Option {@value #OPTION_TARGET} angegeben.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("javadoc")
@Deprecated
  class Deprecated_JavaDocGen extends Doclet {

	/** Dieses Feld speichert den Namen der {@link RootDoc#options() Option}, die den Dateinamen der Ausgabedatei angibt. */
	public static final String OPTION_TARGET = "-targetfile";
	public static void main(final String[] args) throws Exception {
		com.sun.tools.javadoc.Main.execute("TESSSSSS", Deprecated_JavaDocGen.class.getCanonicalName(),
			new String[]{ //
				"-protected", //
				OPTION_TARGET, "D:/projects/java/bee-creative (javadoc)/doc.html", //
				"-sourcepath", "D:/projects/java/bee-creative/src/main/java", //
				"bee.creative.util"});

	}
	public static boolean start(final RootDoc rootDoc) {
		try {
			final Deprecated_JavaDocGen gen = new Deprecated_JavaDocGen();
			gen.loadOptions(rootDoc);
			gen.writeRoot(rootDoc);
			return true;
		} catch (final Throwable cause) {
			cause.printStackTrace();
			return false;
		}
	}

	public static int optionLength(final String option) {
		if (Deprecated_JavaDocGen.OPTION_TARGET.equals(option)) return 2;
		return 0;
	}

	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}

	String targetFile;

	final void loadOptions(final RootDoc rootDoc) {
		for (final String[] option: rootDoc.options()) {
			if ((option.length == 2) && Deprecated_JavaDocGen.OPTION_TARGET.equals(option[0])) {
				this.targetFile = option[1];
			}
		}
	}

	Writer writer;

	/** Diese Methode schreibt das gegebene Zeichen direkt.
	 *
	 * @param src Zeichen.
	 * @throws IOException - */
	final void write(final char src) throws IOException {
		this.writer.write(src);
	}

	/** Diese Methode schreibt die gegebene Zeichenkette maskiert.<br>
	 * Genauer werden die Zeichen <code>'&lt;'</code>, {@code '>'} und {@code '&'} mit {@code "&lt;"}, {@code "&gt;"} bzw. {@code "&amp;"} maskiert.
	 *
	 * @param src Zeichenkette.
	 * @throws IOException - */
	final void writeText(final String src) throws IOException {
		for (int i = 0, length = src.length(); i < length; i++) {
			final char s = src.charAt(i);
			switch (s) {
				case '<':
					this.writeHtml("&lt;");
				break;
				case '>':
					this.writeHtml("&gt;");
				break;
				case '&':
					this.writeHtml("&amp;");
				break;
				default:
					this.write(s);
				break;
			}
		}
	}

	/** Diese Methode schreibt die gegebene Zeichenkette direkt.
	 *
	 * @param src Zeichenkette.
	 * @throws IOException - */
	final void writeHtml(final String src) throws IOException {
		this.writer.write(src);
	}

	final List<List<String>> typevarStack = new LinkedList<>();

	/** Diese Methode entfernt die zuletzt via {@link #typevarPush(TypeVariable[])} hinzugefügten {@link TypeVariable}s vom Kellerspeicher von
	 * {@link #typevarIgnore(TypeVariable)}. */
	final void typevarPop() {
		this.typevarStack.remove(0);
	}

	/** Diese Methode leert den Kellerspeicher von {@link #typevarIgnore(TypeVariable)}. */
	final void typevarClear() {
		this.typevarStack.clear();
	}

	/** Diese Methode fügt die gegebenen {@link TypeVariable}s auf den Kellerspeicher von {@link #typevarIgnore(TypeVariable)}.
	 *
	 * @param src {@link TypeVariable}s. */
	final void typevarPush(final TypeVariable[] src) {
		final List<String> vars = new LinkedList<>();
		for (final TypeVariable src2: src) {
			vars.add(src2.typeName());
		}
		this.typevarStack.add(0, vars);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebene {@link TypeVariable} ohne ihre {@link TypeVariable#bounds()} ausgegeben werden
	 * soll.<br>
	 * Die hierfür markierten {@link TypeVariable}s werden in einem Kellerspeicher verwaltet, um der relativen Sichtbarket seitens der Klassen und Methoden
	 * Rechnung zu tragen.
	 *
	 * @param src {@link TypeVariable}.
	 * @return {@code true}, wenn nur der Name der {@link TypeVariable} ausgegeben werden soll. */
	final boolean typevarIgnore(final TypeVariable src) {
		for (final List<?> list: this.typevarStack) {
			if (list.contains(src.typeName())) return true;
		}
		return false;
	}

	final void writeRoot(final RootDoc src) {
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(this.targetFile), "UTF-8")) {
			this.writer = writer;
			this.writeRoot_head(src);
			this.writeRoot_body(src);
			writer.flush();
		} catch (final IOException cause) {
			System.out.println(cause);
			src.printError(cause.toString());
		}
	}

	final void writeRoot_head(final RootDoc rootDoc) throws IOException {
		this.writeHtml("<!DOCTYPE html>\n");
		this.writeHtml("<html>\n");
		this.writeHtml("<head><meta charset='UTF-8'><style>\n");
		this.writeHtml(this.loadFile("JavaDocGen.css"));
		this.writeHtml("\n</style></head>\n");
	}

	final void writeRoot_body(final RootDoc rootDoc) throws IOException {
		this.writeHtml("<body>\n");
		this.writeRoot_PACKAGES(rootDoc);
		this.writeHtml("<script type='text/javascript'>\n");
		this.writeHtml(this.loadFile("JavaDocGen.js"));
		this.writeHtml("\n</script></body>\n</html>");
	}

	final void writeRoot_PACKAGES(final RootDoc rootDoc) throws IOException {
		final PackageDoc[] array = rootDoc.specifiedPackages();
		Arrays.sort(array);
		for (final PackageDoc item: array) {
			this.writePackage(item);
		}
	}

	final void writePackage(final PackageDoc src) throws IOException {
		this.writePackage_h1(src);
		this.writeHtml("<div>\n");
		this.writePackage_div(src);
		this.writePackage_CLASSES(src);
		this.writeHtml("\n</div>\n");
	}

	final void writePackage_h1(final PackageDoc src) throws IOException {
		this.writeHtml("<h1>");
		this.writeAnchor_OPEN(src.name());
		this.writeHtml(src.name());
		this.writeAnchor_CLOSE();
		this.writeHtml("</h1>\n");
	}

	final void writePackage_div(final PackageDoc src) throws IOException {
		this.writeHtml("<div>\n");
		this.putInfo_ITEM(src);
		this.writeInfo_SEEALSO(src);
		this.writeHtml("\n</div>\n");
	}

	final void writePackage_CLASSES(final PackageDoc packageDoc) throws IOException {
		final ClassDoc[] array = packageDoc.allClasses();
		Arrays.sort(array);
		for (final ClassDoc item: array) {
			this.writeClass(item);
		}
	}

	final void writeClass(final ClassDoc src) throws IOException {
		this.writeClass_h2(src);
		this.writeHtml("<div>\n");
		this.writeClass_h3(src);
		this.writeClass_div(src);
		this.writeClass_FIELDS(src, true);
		this.writeClass_METHODS(src, true);
		this.writeClass_FIELDS(src, false);
		this.writeClass_CONSTRS(src);
		this.writeClass_METHODS(src, false);
		this.typevarClear();
		this.writeHtml("\n</div>\n");
	}

	final void writeClass_h2(final ClassDoc classDoc) throws IOException {
		this.writeHtml("<h2>");
		this.writeAnchor_OPEN(this.calcHref(classDoc));
		this.writeHtml(classDoc.name());
		this.writeAnchor_CLOSE();
		this.writeHtml("</h2>\n");
	}

	final void writeClass_h3(final ClassDoc classDoc) throws IOException {
		this.writeHeader_OPEN(classDoc, true, classDoc.isAbstract());
		this.writeClass_CLASSPATH(classDoc, false);
		String comma = "<br>: ";
		final Type superClassType = classDoc.superclassType();
		if ((superClassType != null) && !Object.class.getName().equals(superClassType.qualifiedTypeName())) {
			this.writeHtml(comma);
			this.writeType(superClassType);
			comma = ", ";
		}
		for (final Type superInterfaceType: classDoc.interfaceTypes()) {
			this.writeHtml(comma);
			this.writeType(superInterfaceType);
			comma = ", ";
		}
		this.writeHeader_CLOSE();
		this.writeHtml("\n");
	}

	final void writeClass_div(final ClassDoc classDoc) throws IOException {
		this.writeHtml("<div>\n");
		this.putInfo_ITEM(classDoc);
		this.putInfo_PARAM(classDoc.typeParamTags());
		this.writeInfo_SEEALSO(classDoc);
		this.writeHtml("</div>\n");
	}

	final void writeClass_FIELDS(final ClassDoc classDoc, final boolean isStatic) throws IOException {
		for (final FieldDoc fieldDoc: classDoc.fields()) {
			if (fieldDoc.isStatic() == isStatic) {
				this.writeField(fieldDoc);
			}
		}
	}

	final void writeClass_CONSTRS(final ClassDoc classDoc) throws IOException {
		for (final ConstructorDoc constructorDoc: classDoc.constructors()) {
			this.writeConstructor(constructorDoc);
		}
	}

	final void writeClass_METHODS(final ClassDoc classDoc, final boolean isStatic) throws IOException {
		for (final MethodDoc methodDoc: classDoc.methods()) {
			if (methodDoc.isStatic() == isStatic) {
				this.putMethod(methodDoc);
			}
		}
	}

	final void writeClass_CLASSPATH(final ClassDoc classDoc, final boolean ignoreParams) throws IOException {
		final ClassDoc superClassDoc = classDoc.containingClass();
		if (superClassDoc != null) {
			this.writeClass_CLASSPATH(superClassDoc, classDoc.isStatic());
		} else {
			final PackageDoc packageDoc = classDoc.containingPackage();
			this.writeLink(this.calcHref(packageDoc), this.calcName(packageDoc));
		}
		this.writeHtml(".");
		this.writeLink(this.calcHref(classDoc), this.calcName(classDoc));
		if (ignoreParams) return;
		this.typevarPush(classDoc.typeParameters());
		this.writeType_PARAMS(classDoc.typeParameters(), false);
	}

	final void writeField(final FieldDoc src) throws IOException {
		this.writeField_h3(src);
		this.writeField_div(src);
	}

	final void writeField_h3(final FieldDoc src) throws IOException {
		final String href = this.calcHref(src);
		this.writeHeader_OPEN(src, src.isStatic(), false);
		this.writeAnchor_OPEN(href);
		this.writeLink(href, src.name());
		this.writeHtml(": ");
		this.writeType(src.type());
		this.writeAnchor_CLOSE();
		this.writeHeader_CLOSE();
		this.writeHtml("\n");
	}

	final void writeField_div(final FieldDoc src) throws IOException {
		this.writeHtml("<div>\n");
		this.putInfo_ITEM(src);
		this.writeInfo_SEEALSO(src);
		this.writeHtml("\n</div>\n");
	}

	final void putMethod(final MethodDoc src) throws IOException {
		// if (this.hasInfo_EMPTY(src)) return;
		this.typevarPush(src.typeParameters());
		this.putMethod_h3(src);
		this.putMethod_div(src);
		this.typevarPop();
	}

	final void putMethod_h3(final MethodDoc src) throws IOException {
		final String href = this.calcHref(src);
		this.writeHeader_OPEN(src, src.isStatic(), src.isAbstract());
		this.writeAnchor_OPEN(href);
		this.writeLink_OPEN(href);
		this.writeHtml(src.name());
		this.writeLink_CLOSE();
		this.writeType_PARAMS(src.typeParameters(), false);
		this.writeArg_PARAMS(src.parameters(), href);
		this.writeHtml(": ");
		this.writeType(src.returnType());
		this.writeAnchor_CLOSE();
		this.writeHeader_CLOSE();
		this.writeHtml("\n");
	}

	final void putMethod_div(final MethodDoc src) throws IOException {
		this.writeHtml("<div>\n");
		this.putInfo_ITEM(src);
		this.putInfo_PARAM(src);
		this.writeInfo_RETURN(src);
		this.writeInfo_THROWS(src);
		this.writeInfo_SEEALSO(src);
		this.writeHtml("\n</div>\n");
	}

	final void writeConstructor(final ConstructorDoc src) throws IOException {
		// if (this.hasInfo_EMPTY(src)) return;
		this.typevarPush(src.typeParameters());
		this.writeConstructor_h3(src);
		this.writeConstructor_div(src);
		this.typevarPop();
	}

	final void writeConstructor_h3(final ConstructorDoc src) throws IOException {
		final String href = this.calcHref(src);
		this.writeHeader_OPEN(src, true, false);
		this.writeAnchor_OPEN(href);
		this.writeLink(href, src.name());
		this.writeType_PARAMS(src.typeParameters(), false);
		this.writeArg_PARAMS(src.parameters(), href);
		this.writeAnchor_CLOSE();
		this.writeHeader_CLOSE();
		this.writeHtml("\n");
	}

	final void writeConstructor_div(final ConstructorDoc src) throws IOException {
		this.writeHtml("<div>\n");
		this.putInfo_ITEM(src);
		this.putInfo_PARAM(src);
		this.writeInfo_RETURN(src);
		this.writeInfo_THROWS(src);
		this.writeInfo_SEEALSO(src);
		this.writeHtml("</div>\n");
	}

	final void writeArg(final Parameter src, final String href) throws IOException {
		final String name = src.name();
		this.writeLink(href + "-" + name, name);
		this.writeHtml(": ");
		this.writeType(src.type());
	}

	final void writeArg_PARAMS(final Parameter[] src, final String href) throws IOException {
		final int count = src.length;
		if (count == 0) {
			this.writeHtml("()");
		} else {
			this.writeHtml("(");
			this.writeArg(src[0], href);
			for (int i = 1; i < count; i++) {
				this.writeHtml(", ");
				this.writeArg(src[i], href);
			}
			this.writeHtml(")");
		}
	}

	final void writeType(final Type type) throws IOException {
		if (type == null) return;
		// System.out.println(type);
		if (type.asParameterizedType() != null) {
			// System.out.println(" PT");
			this.writeType(type.asParameterizedType());
		} else if (type.asTypeVariable() != null) {
			// System.out.println(" TV");
			this.writeType(type.asTypeVariable(), true);
		} else if (type.asWildcardType() != null) {
			// System.out.println(" WC");
			this.writeType(type.asWildcardType());
		} else if (type.asClassDoc() != null) {
			// System.out.println(" CD");
			this.writeType(type.asClassDoc());
			this.writeHtml(type.dimension());
		} else {
			// System.out.println(" ST");
			this.writeHtml(type.simpleTypeName());
			this.writeHtml(type.dimension());
		}
	}

	final void writeType(final ClassDoc src5) throws IOException {
		this.writeLink(this.calcHref(src5), this.calcName(src5));
	}

	final void writeType(final WildcardType src4) throws IOException {
		this.writeHtml("?");
		this.writeType_BOUNDS(src4.superBounds(), "% ");
		this.writeType_BOUNDS(src4.extendsBounds(), ": ");
	}

	final void writeType(final ParameterizedType parameterizedType) throws IOException {
		this.writeLink(this.calcHref(parameterizedType.asClassDoc()), this.calcName(parameterizedType));
		this.writeType_PARAMS(parameterizedType.typeArguments());
		this.writeHtml(parameterizedType.dimension());
	}

	final void writeType(final TypeVariable src, final boolean asReference) throws IOException {
		this.writeLink(this.calcHref(src), this.calcName(src));
		if (!asReference || !this.typevarIgnore(src)) {
			this.writeType_BOUNDS(src.bounds(), ": ");
		}
		this.writeHtml(src.dimension());
	}

	final void writeType_BOUNDS(final Type[] src, final String operator) throws IOException {
		final int count = src.length;
		if (count == 0) return;
		this.writeHtml(operator);
		this.writeType(src[0]);
		for (int i = 1; i < count; i++) {
			this.writeHtml("&amp;");
			this.writeType(src[i]);
		}
	}

	final void writeType_PARAMS(final Type[] src) throws IOException {
		final int count = src.length;
		if (count == 0) return;
		this.writeHtml("&lt;");
		this.writeType(src[0]);
		for (int i = 1; i < count; i++) {
			this.writeHtml(", ");
			this.writeType(src[i]);
		}
		this.writeHtml("&gt;");
	}

	final void writeType_PARAMS(final TypeVariable[] src, final boolean asReference) throws IOException {
		final int count = src.length;
		if (count == 0) return;
		this.writeHtml("&lt;");
		this.writeType(src[0], asReference);
		for (int i = 1; i < count; i++) {
			this.writeHtml(", ");
			this.writeType(src[i], asReference);
		}
		this.writeHtml("&gt;");
	}

	void putInfo_ITEM(final Doc src) throws IOException {
		if (src == null) return;
		this.putInfo_ITEM(src.inlineTags());
	}

	void putInfo_ITEM(final Tag[] src) throws IOException {
		for (final Tag item: src) {
			final String name = item.name();
			if ("Text".equals(name)) {
				this.writeHtml(item.text());
			} else if ("@code".equals(name)) {
				this.writeHtml("<code>");
				this.writeText(item.text());
				this.writeHtml("</code>");
			} else if ("@literal".equals(name)) {
				this.writeText(item.text());
			} else if ("@link".equals(name)) {
				final SeeTag item2 = (SeeTag)item;
				final String href = this.calcHref(item2);
				final String text = item2.label();
				final String text2 = (text != null) && !text.isEmpty() ? text : this.calcName(item2);
				this.writeLink(href, text2);
			} else if ("@inheritDoc".equals(name)) {
				final Doc doc = item.holder();
				if ((doc != null) && doc.isMethod()) {
					// putInfo_ITEM(((MethodDoc)doc).overriddenMethod());
				}
			} else {
				this.writeHtml("{");
				this.writeText(item.name());
				this.writeHtml(":");
				this.writeHtml(item.text());
				this.writeHtml("}");
			}
		}
	}

	void putInfo_PARAM(final Doc src) throws IOException {
		this.putInfo_PARAM(src.tags("@param"));
	}

	void putInfo_PARAM(final Tag[] src) throws IOException {
		if (src.length == 0) return;
		this.writeHtml("\n<dl class='PARAMS'>\n");
		for (final Tag src2: src) {
			final ParamTag src3 = (ParamTag)src2;
			this.writeHtml("<dt>");
			this.writeAnchor_OPEN(this.calcHref(src3));
			this.writeHtml(src3.parameterName());
			this.writeAnchor_CLOSE();
			this.writeHtml("</dt>\n<dd>");
			this.putInfo_ITEM(src3.inlineTags());
			this.writeHtml("</dd>\n");
		}
		this.writeHtml("</dl>");
	}

	final void writeInfo_RETURN(final Doc src) throws IOException {
		this.writeInfo_RETURN(src.tags("@return"));
	}

	final void writeInfo_RETURN(final Tag[] src) throws IOException {
		if (src.length == 0) return;
		this.writeHtml("\n<dl class='RETURN'>\n");
		for (final Tag src2: src) {
			this.writeHtml("<dd>");
			this.putInfo_ITEM(src2.inlineTags());
			this.writeHtml("</dd>\n");
		}
		this.writeHtml("</dl>");
	}

	final void writeInfo_THROWS(final Doc src) throws IOException {
		this.writeInfo_THROWS(src.tags("@throws"));
	}

	void writeInfo_THROWS(final Tag[] src) throws IOException {
		if (src.length == 0) return;
		this.writeHtml("\n<dl class='THROWS'>\n");
		for (final Tag src2: src) {
			final ThrowsTag src3 = (ThrowsTag)src2;
			this.writeHtml("<dt>");
			this.writeType(src3.exceptionType());
			this.writeHtml("</dt>\n<dd>");
			this.putInfo_ITEM(src3.inlineTags());
			this.writeHtml("</dd>\n");
		}
		this.writeHtml("</dl>");
	}

	void writeInfo_SEEALSO(final Doc src) throws IOException {
		this.writeInfo_SEEALSO(src.tags("@see"));
	}

	void writeInfo_SEEALSO(final Tag[] src) throws IOException {
		if (src.length == 0) return;
		this.writeHtml("\n<dl class='SEEALSO'>\n");
		for (final Tag src2: src) {
			final SeeTag src3 = (SeeTag)src2;
			final String href = this.calcHref(src3);
			final String name = this.calcName(src3);
			this.writeHtml("<dt>");
			this.writeLink(href, name);
			this.writeHtml("</dt>\n<dd>");
			this.writeHtml(src3.label());
			this.writeHtml("</dd>\n");
		}
		this.writeHtml("</dl>");
	}

	final void writeLink(final String href, final String name) throws IOException {
		if (href != null) {
			this.writeLink_OPEN(href);
			this.writeText(name);
			this.writeLink_CLOSE();
		} else {
			this.writeHtml("<a>");
			this.writeText(name);
			this.writeHtml("</a>");
		}
	}

	/** Diese Methode schreibt den öffnenden Teil des Verweises eines Elements ({@code <a href='#...'>}).
	 *
	 * @param name Adresse des Elements.
	 * @throws IOException - */
	final void writeLink_OPEN(final String name) throws IOException {
		this.writeHtml("<a href='#");
		this.writeHtml(name);
		this.writeHtml("'>");
	}

	/** Diese Methode schreibt den schließenden Teil eines Verweises ({@code </a>}).
	 *
	 * @throws IOException - */
	final void writeLink_CLOSE() throws IOException {
		this.writeHtml("</a>");
	}

	/** Diese Methode schreibt den öffnenden Teil des Ankers eines Elements ({@code <a name='...'>}).
	 *
	 * @param name Adresse des Elements.
	 * @throws IOException - */
	final void writeAnchor_OPEN(final String name) throws IOException {
		this.writeHtml("<a name='");
		this.writeHtml(name);
		this.writeHtml("'>");
	}

	/** Diese Methode schreibt den schließenden Teil eines Ankers ({@code </a>}).
	 *
	 * @throws IOException - */
	final void writeAnchor_CLOSE() throws IOException {
		this.writeHtml("</a>");
	}

	/** Diese Methode schreibt den Öffnenden Teil der Signatur eines Elements {@code
	 *
	<h3 class='...'>}.
	 *
	 * @param src Element.
	 * @param isStatic {@code true}, wenn das Element statisch ist.
	 * @param isAbstract {@code true}, wenn das Element abstrakt ist.
	 * @throws IOException - */
	final void writeHeader_OPEN(final ProgramElementDoc src, final boolean isStatic, final boolean isAbstract) throws IOException {
		this.writeHtml("<h3 class='S");
		this.write(isStatic ? 'T' : 'F');
		this.writeHtml(" A");
		this.write(isAbstract ? 'T' : 'F');
		this.writeHtml("'>");
		this.writeHtml(src.isPublic() ? "+ " : src.isPrivate() ? "- " : src.isProtected() ? "# " : "~ ");
	}

	/** Diese Methode schreibt den schließenden Teil einer Signatur ({@code </h3>}).
	 *
	 * @throws IOException - */
	final void writeHeader_CLOSE() throws IOException {
		this.writeHtml("</h3>");
	}

	final String loadFile(final String name) throws IOException {
		try (InputStream stream = Deprecated_JavaDocGen.class.getResourceAsStream(name)) {
			try (Scanner scanner = new Scanner(stream, "UTF8")) {
				return scanner.useDelimiter("\\Z").next();
			}
		}
	}

	/** Diese Methode gibt die gegebene Zeichenkette ohne generische Parameter zurück.
	 *
	 * @param src Zeichenkette.
	 * @return Zeichenkette ohne {@code '<'}, {@code '>'} und die Texte dazwischen. */
	final String calcHref(final String src) {
		final int count = src.length();
		final StringBuilder result = new StringBuilder(count);
		for (int i = 0, level = 0; i < count; i++) {
			final char token = src.charAt(i);
			if (token == '<') {
				level++;
			} else if (token == '>') {
				level--;
			} else if ((level == 0) && (token != ' ')) {
				result.append(token);
			}
		}
		return result.toString();
	}

	String calcHref(final PackageDoc src) {
		if (!src.isIncluded()) return null;
		return src.name();
	}

	String calcHref(final Object src) {
		if (src instanceof SeeTag) return this.calcHref((SeeTag)src);
		if (src instanceof ParamTag) return this.calcHref((ParamTag)src);
		if (src instanceof ThrowsTag) return this.calcHref((ThrowsTag)src);
		if (src instanceof Doc) return this.calcHref((Doc)src);
		if (src instanceof TypeVariable) return this.calcHref((TypeVariable)src);
		if (src instanceof PackageDoc) return this.calcHref((PackageDoc)src);
		return null;
	}

	String calcHref(final FieldDoc src) {
		if (!src.isIncluded()) return null;
		return src.qualifiedName() + ':' + src.type().typeName() + src.type().dimension();
	}

	String calcHref(final ClassDoc src) {
		if (!src.isIncluded()) return null;
		return src.qualifiedName();
	}

	String calcHref(final MethodDoc src) {
		if (!src.isIncluded()) return null;
		return src.qualifiedName() + this.calcHref(src.flatSignature()) + ':' + src.returnType().typeName() + src.returnType().dimension();
	}

	String calcHref(final ConstructorDoc src) {
		if (!src.isIncluded()) return null;
		return src.qualifiedName() + this.calcHref(src.flatSignature());
	}

	String calcHref(final Doc src) {
		if (src.isField() || src.isEnumConstant()) return this.calcHref((FieldDoc)src);
		if (src.isMethod()) return this.calcHref((MethodDoc)src);
		if (src.isConstructor()) return this.calcHref((ConstructorDoc)src);
		return this.calcHref((ClassDoc)src);
	}

	String calcHref(final SeeTag src) {
		final MemberDoc src2 = src.referencedMember();
		if (src2 != null) return this.calcHref(src2);
		final ClassDoc src3 = src.referencedClass();
		if (src3 != null) return this.calcHref(src3);
		final PackageDoc src4 = src.referencedPackage();
		if (src4 != null) return this.calcHref(src4);
		return null;
	}

	String calcHref(final ParamTag src) {
		final String href = this.calcHref(src.holder());
		if (href == null) return null;
		return href + "-" + src.parameterName();
	}

	String calcHref(final ThrowsTag src) {
		final ClassDoc src2 = src.exception();
		if ((src2 == null) || !src2.isIncluded()) return null;
		return this.calcHref(src.exceptionType());
	}

	String calcHref(final TypeVariable src) {
		final String href = this.calcHref(src.owner());
		if (href == null) return null;
		return href + "-" + src.typeName();
	}

	String calcName(final SeeTag src) {
		final String s = src.label();
		if ((s != null) && !s.isEmpty()) return s;
		final MemberDoc src2 = src.referencedMember();
		if (src2 != null) return this.calcName(src2);
		final ClassDoc src3 = src.referencedClass();
		if (src3 != null) return this.calcName(src3);
		final PackageDoc src4 = src.referencedPackage();
		if (src4 != null) return this.calcName(src4);
		return "-";
	}

	String calcName(final MemberDoc src) {
		if (src.isField() || src.isEnumConstant()) return this.calcName((FieldDoc)src);
		if (src.isMethod()) return this.calcName((MethodDoc)src);
		if (src.isConstructor()) return this.calcName((ConstructorDoc)src);
		return "???";
	}

	String calcName(final ClassDoc src) {
		return src.simpleTypeName();
	}

	String calcName(final FieldDoc src) {
		return this.calcName(src.containingClass()) + "." + src.name();
	}

	String calcName(final MethodDoc src) {
		return this.calcName(src.containingClass()) + "." + src.name() + src.flatSignature();
	}

	String calcName(final ConstructorDoc src) {
		return this.calcName(src.containingClass()) + "." + src.name() + src.flatSignature();
	}

	String calcName(final PackageDoc src) {
		return src.name();
	}

	String calcName(final TypeVariable src) {
		return src.typeName();
	}

	private String calcName(final Type type) {
		return type.simpleTypeName();
	}

	boolean hasInfo_EMPTY(final Doc src) {
		for (final Tag item: src.inlineTags()) {
			if (!item.text().isEmpty() && !item.name().equals("@inheritDoc")) return false;
		}
		return true;
	}

}
