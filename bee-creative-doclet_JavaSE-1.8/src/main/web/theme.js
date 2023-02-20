



var JAVADOC_ = createJavadocNode(JAVADOC);




/** Diese Methode schreibt die gegebene Zeichenkette maskiert.<br>
	 * Genauer werden die Zeichen <code>'&lt;'</code>, {@code '>'} und {@code '&'} mit {@code "&lt;"}, {@code "&gt;"} bzw. {@code "&amp;"} maskiert.
	 *
	 * @param src Zeichenkette.
	 * @throws IOException - */



Node.prototype._newElem = function (name) { let res = document.createElement(name); this.appendChild(res); return res; };
Node.prototype._newText = function (text) { let res = document.createTextNode(text); this.appendChild(res); return res; };

Node.prototype._addElem = function (name) { this._newElem(name); return this; };
Node.prototype._addText = function (text) { this._newText(text); return this; };

Node.prototype._setAttr = function (name, value) { this.setAttribute(name, value); return this; };
Node.prototype._addAnchor = function (name, text) {
	this._newElem("a")._setAttr("name", name)._newText(text);
	return this;
};
JAVADOC_.nodePackages.forEach(p => {
	let ps = document.body._newElem("section");
	ps.appendChild(p.nodeTitle);
	let pd = ps._newElem("div");
	pd.appendChild(p.nodeInfos);
	p.nodeClasses.forEach(c => {
		let cs = pd._newElem("section");
		cs.appendChild(c.nodeTitle);

		let cd = cs._newElem("div");
		cd.appendChild(c.nodeLabel);
		cd.appendChild(c.nodeInfos);
		c.nodeMembers.forEach(m => {
			let ms = cd._newElem("section");
			ms.appendChild(m.nodeLabel);
			ms.appendChild(m.nodeInfos);
		})
	})
})



function THEME(javadoc) {
	this.pool = {};
	this.javadoc = javadoc;
	this.setupMemberPackages(javadoc);
	THEME_forEach(document.body, javadoc.packages, null, THEME_addPackage);
}

THEME.prototype.updatePool = function (array) {
	return this.updateArray(array, item => this.pool[item.href] = item);
}

THEME.prototype.updateArray = function (array, method) {
	return (array.forEach(method), array);
}

// sortiert nach href 
THEME.prototype.orderMemberHref = function (array) {
	return array.sort((a, b) => a.href.localeCompare(b.href));
}

// liefert datenfeld, initialisiert leere liste
THEME.prototype.setupMemberArray = function (object, field) {
	return object[field] || (object[field] = []);
}

// setzt parent
THEME.prototype.setupMemberParent = function (array, parent) {
	return this.updateArray(array, item => item.parent = parent);
}

THEME.prototype.setupMemberPackages = function (object) {
	return (this.updateArray(
		this.updatePool(this.orderMemberHref(this.setupMemberArray(object, "packages"))),
		item => (item.isPackage = true, this.setupMemberClasses(item))
	), object);
}

THEME.prototype.setupMemberClasses = function (object) {
	this.updateArray(
		this.updatePool(this.orderMemberHref(this.setupMemberArray(object, "classes"))),
		item => (item.parent = this.pool[item.parent] || object,
			this.setupMemberVars(item),
			this.setupMemberFields(item),
			this.setupMemberMethods(item)
		)
	);
}

THEME.prototype.setupMemberVars = function (object) {
	object.varsMap = {};
	this.updatePool(this.updateArray(
		this.setupMemberParent(this.setupMemberArray(object, "vars"), object),
		item => (item.href = object.href + "-" + item.name, object.varsMap[item.name] = item)
	));
}

THEME.prototype.setupMemberFields = function (object) {
	this.updatePool(this.orderMemberHref(this.setupMemberParent(this.setupMemberArray(object, "fields"), object)));
}

THEME.prototype.setupMemberMethods = function (object) {
	this.updateArray(
		this.updatePool(this.orderMemberHref(this.setupMemberParent(this.setupMemberArray(object, "methods"), object))),
		item => this.setupMemberVars(item)
	);
}




function THEME_toHtml(src) {
	return (src || "").replace(/&/g, "&amp;").replace(/</g, "&lt;");
}



function THEME_forEach(node, array, parent, builder) {
	(array || []).forEach(item => builder(node, item, parent));
}

function THEME_addAnchor(node, name, text) {
	return node._newElem("a")._setAttr("name", name)._addText(text);
}

function THEME_addPackage(node, src) {
	let head = node._newElem("h1"), body = node._newElem("div"), section = new SECTION(head, body);
	THEME_addAnchor(head, src.name, src.name);
	THEME_addPackage_info(body, src);
	THEME_forEach(body, src.classes, section, THEME_addClass);
}

function THEME_addPackage_info(node, src) {
	var info = node._newElem("div");
	THEME_addDocs(info, src.docs);
	THEME_addTags_see(info, src.tags);
	return info;
}


function THEME_addClass(node, src, parent) {
	let head = node._newElem("h2"), body = node._newElem("div"), section = new SECTION(head, body, parent);
	THEME_addAnchor(head, src.href, src.name);
	THEME_addClass_head(body, src);
	THEME_addClass_info(body, src);
	//	this.writeClass_FIELDS(body,src, true);
	//	this.writeClass_METHODS(body,src, true);
	//	this.writeClass_FIELDS(body,src, false);
	//	this.writeClass_CONSTRS(body,src);
	//	this.writeClass_METHODS(body,src, false);
}

function THEME_addClass_head(node, src) {
	let head = THEME_addHeader3(node, { ...src, ...{ isStatic: true } });
	//	this.writeClass_CLASSPATH(classDoc, false);
	//	 comma = "<br>: ";
	//	  superClassType = classDoc.superclassType();
	//	if ((superClassType != null) && !Object.class.getName().equals(superClassType.qualifiedTypeName())) {
	//		this.writeHtml(comma);
	//		this.writeType(superClassType);
	//		comma = ", ";
	//	}
	//	for (final Type superInterfaceType: classDoc.interfaceTypes()) {
	//		this.writeHtml(comma);
	//		this.writeType(superInterfaceType);
	//		comma = ", ";
	//	}
}

function THEME_addClass_info(node, src) {
	let info = node._newElem("div");
	THEME_addDocs(info, src.docs);
	THEME_addTags_see(info, src.tags);
}


/*
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
		this.setDocs(src);
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
		this.setDocs(src);
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
		this.setDocs(src);
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
 */

function THEME_addDocs(node, docs) {
	let res = [], doc;
	for (doc of (docs || [])) {
		if (doc.href) {
			res.push("<a href=\"#", doc.href, "\">", THEME_toHtml(doc.text), "</a>");
		} else if (doc.type == "@code") {
			res.push("<code>", THEME_toHtml(doc.text), "</code>");
		} else if (doc.type == "@literal") {
			res.push(THEME_toHtml(doc.text));
		} else {
			res.push(doc.text);
		}
	}
	node.innerHTML = res.join('');
};

/*
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
			this.setDocs(src3.inlineTags());
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
			this.setDocs(src2.inlineTags());
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
			this.setDocs(src3.inlineTags());
			this.writeHtml("</dd>\n");
		}
		this.writeHtml("</dl>");
	}
*/
function THEME_addTags_see(node, tags) {
	tags = (tags || []).filter(tag => !!tag.href);
	if (!tags.length) return;
	let body = node._newElem("dl")._setAttr("class", "SEEALSO");
	tags.forEach(tag => {
		body._newElem("dt")._newElem("a")._setAttr("href", "#" + tag.href)._newText(tag.text);
		THEME_addDocs(body._newElem("dd"), tag.docs);
	});
}

/*
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
	
*/
function THEME_addHeader3(node, src) {
	return node._newElem("h3")._setAttr("class", "S" + (src.isStatic ? "T" : "F") + " A" + (src.isAbstract ? "T" : "F"))._addText(src.isPublic ? "+ " : src.isPrivate ? "- " : src.isProtected ? "# " : "~ ");
}


function _JAVADOC_(src) {

	this.body = document.body;
	this.packages = (src.packages || []).map(val => new _PACKAGE_(val, this))
}

function _PACKAGE_(src, parent) {
	this.parent = parent;
	this.head = parent.body._newElem("h1");
	this.head._newElem("a")._setAttr("name", src.name)._newText(src.name);
	this.body = parent.body._newElem("div");
	this.body._newElem("div")._setDocs(src.docs);
	this.section = new SECTION(this.head, this.body);

	this.classes = (src.classes || []).map(val => new _CLASS_(val, this))
	/*	this.writeHtml("<div>\n");
		
		this.writeHtml("<div>\n");
		this.setDocs(src.docs);
		this.writeInfo_SEEALSO(src);
		this.writeHtml("\n</div>\n");
		
		
		this.writePackage_CLASSES(src);
		this.writeHtml("\n</div>\n");*/
}


function _CLASS_(src, parent) {
	this.parent = parent;
	(this.head = parent.body._newElem("h2"))._newElem("a")._setAttr("name", src.key)._newText(src.name);
	(this.body = parent.body._newElem("div"))._newElem("div")._setDocs(src.docs);
	this.section = new SECTION(this.head, this.body, parent.section);


	/*	
		this.writeInfo_SEEALSO(src);
		this.writeHtml("\n</div>\n");
		 */
}
/*
function writeInfo_SEEALSO(src){
	src = src.filter(val => val.name="@see");
	if (src.length == 0) return;
	let res = newNode({name:"dl", atts:{"class": "SEEALSO"}});
	src.forEach(val => {
		newNode({name:"dt", cont:[
			{name:"a", atts:{href:val.href}, cont:[val.name]}
		]},res);
		
		newNode({name:"dt" ,res);
		
		
		this.writeHtml("<dt>");
		this.writeLink(val.href, val.name);
		this.writeHtml("</dt>\n<dd>");
		this.writeHtml(src3.label());
		this.writeHtml("</dd>\n");
	}
	return res;
}
*/
function newNode(src, parent) {
	var res;
	if (typeof src == 'string') {
		res = document.createTextNode(src);
	} else {
		res = document.createElement(src.name);
		let cont = src.cont || [], atts = src.atts || {};
		for (let key in atts) res.setAttribute(key, atts[key]);
		cont.forEach(val => newNode(val, res));
	}
	if (parent) parent.appendChild(res);
	return res;
}


function SECTION(head, body, parentOrNull) {
	this.head = head;
	this.body = body;
	this.parent = parentOrNull;
	this.children = [];
	head._section_ = this;
	body._section_ = this;
	head.onclick = JDGItem.clickHead;
	parentOrNull ? parentOrNull.children.push(this) : null;
	//this.hideFlat();
}

// SECTION.from(Element): SECTION
SECTION.from = function (elem) {
	for (let res; elem; elem = elem.parentNode) if (res = elem._section_) return res;
	return null;
};

//SECTION.hideFlat(): Void
SECTION.prototype.hideFlat = function () {
	this.visible = false;
	JDGItem.updateClass(this.head, 'HEAD_HIDE');
	JDGItem.updateClass(this.body, 'BODY_HIDE');
};

//SECTION.hideDeep(): Void
SECTION.prototype.hideDeep = function () {
	this.children.forEach(val => {
		val.hideFlat();
		val.hideDeep();
	});
};

//JDGItem.showFlat(): Void
SECTION.prototype.showFlat = function () {
	this.visible = true;
	JDGItem.updateClass(this.head, 'HEAD_SHOW');
	JDGItem.updateClass(this.body, 'BODY_SHOW');
};

//JDGItem.showPath(): Void
SECTION.prototype.showPath = function () {
	this.showFlat();
	if (this.parent) this.parent.showPath();
};

//JDGItem.showDeep(): Void
SECTION.prototype.showDeep = function () {
	this.children.map(function (Item) {
		Item.showFlat();
		Item.showDeep();
	});
};



// JDGItem(DOMNode)
// JDGItem(DOMNode, JDGItem)
function JDGItem(Elem, Parent) {
	this.children = [];
	if (Parent) {
		this.head = Elem;
		this.body = Elem.nextElementSibling;
		this.head._section_ = this;
		this.body._section_ = this;
		this.parent = Parent;
		this.hideFlat();
		Elem.onclick = JDGItem.clickHead;
		Parent.children.push(this);
		if (!JDGItem.findNodes(Elem, 'a[@name][1]')[0]) Parent.showChildItem = this;
	} else {
		this.body = Elem;
		function createChildren(Parent, XPath, Method) {
			JDGItem.findNodes(Parent.body, XPath).map(function (Node) {
				var item = new JDGItem(Node, Parent);
				Parent.children.push(item);
				if (Method) Method(item);
			});
		}
		createChildren(this, 'h1', function (Item) {
			createChildren(Item, 'h2', function (Item) {
				createChildren(Item, 'h3');
			});
		});
	}
}



// JDGItem.findNodes(Elem: DOMNode, XPath: String): DOMNode[]
JDGItem.findNodes = document.evaluate ? function (Elem, XPath) {
	if (!Elem) return [];
	var nodes = document.evaluate(XPath, Elem, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null), result = [];
	for (i = 0, size = nodes.snapshotLength; i < size; i++)
		result.push(nodes.snapshotItem(i));
	return result;
} : function (Elem, XPath) {
	if (!Elem) return [];
	var nodes = Elem.selectNodes(XPath), result = [];
	for (i = 0, size = nodes.length; i < size; i++)
		result.push(nodes.item(i++));
	return result;
};

// JDGItem.clickHead(DOMEvent): Void
JDGItem.clickHead = function (Evt) {
	if ((this != Evt.target) && !Evt.target.name) return;
	var item = this._section_;
	item.visible ? item.hideFlat() : item.showFlat();
};

// JDGItem.updateClass(DOMNode, String): Void
JDGItem.updateClass = function (Elem, Class) {
	if (Elem) Elem.className = Class;
};

// JDGItem.ROOT: JDGItem
JDGItem.ROOT = new JDGItem(document.body);

// JDGMenu()
function JDGMenu() {
	this.node = document.createElement('div');
	this.names = {};
	this.node.className = 'TOOL_MENU';
	this.node.innerHTML = '<datalist id="itemnames"></datalist>\
		<input onclick="JDGItem.ROOT.hideDeep()" type="button" value="\u25b2" title="Alle Elemente reduzieren"/>\
		<input onclick="JDGItem.ROOT.showDeep()" type="button" value="\u25bc" title="Alle Elemente erweitern"/>\
		<input oninput="JDGMenu.MENU.checkText()" type="text" list="itemnames" placeholder="field, method, ..."/>\
		<input oninput="JDGMenu.MENU.checkOptn()" type="checkbox" title="Details flach darstellen"/>\
		<input oninput="JDGMenu.MENU.checkOptn()" type="checkbox" title="Signatur ausblenden"/>\
		<input oninput="JDGMenu.MENU.checkOptn()" type="checkbox" title="Referenzen ausblenden"/>\
	';
	document.body.appendChild(this.node);
	var children = this.node.children;
	this.textNode = children[3];
	this.listNode = children[0];
	this.flatenNode = children[4];
	this.signatureNode = children[5];
	this.referencesNode = children[6];
	JDGItem.findNodes(document.body, '//a[@name]').map(function (Node) {
		var name = Node.name;
		this.names['#' + name] = Node;
		if (name.indexOf('-') >= 0) return;
		var child = document.createElement('option');
		child.value = name;
		this.listNode.appendChild(child);
	}, this);
	window.onhashchange = JDGMenu.checkHash;
}

// JDGMenu.checkHash(): Void
JDGMenu.prototype.checkHash = function () {
	var node = this.names[unescape(window.location.hash)], item = SECTION.from(node), item2 = item ? item.showChildItem || item : null;
	if (item2) item2.showPath();
	if (node) node.scrollIntoView();
};

// JDGMenu.checkText(): Void
JDGMenu.prototype.checkText = function () {
	var name = '#' + this.textNode.value;
	if (this.names[name]) window.location.href = name;
};

// JDGMenu.checkText(): Void
JDGMenu.prototype.checkOptn = function () {
	document.body.className = [
		this.flatenNode.checked ? 'INFO_FLAT' : 'INFO_TREE',
		this.signatureNode.checked ? 'SIGN_HIDE' : 'SIGN_SHOW',
		this.referencesNode.checked ? 'REFS_HIDE' : 'REFS_SHOW'
	].join(' ');
};

// JDGMenu.MENU: JDGMenu
JDGMenu.MENU = new JDGMenu();
JDGMenu.MENU.checkHash();
JDGMenu.MENU.checkOptn();

new THEME(JAVADOC);






