


/** @param {JavadocInfo} javadocInfo
 * @returns {JavadocNode} */
function createJavadocNode(javadocInfo) {

    /** @type {JavadocNode} */
    var javadocNode = { ...javadocInfo, nodeItems: {}, nodePackages: [] };

    /**
      * @param {string} name */
    var createElem = (name) => document.createElement(name);

    var createItems = () => {

        var nodeItems = javadocNode.nodeItems;

        /**
         * @param {{href: string}} a 
         * @param {{href: string}} b 
         * @returns {number} */
        var compareHref = (a, b) => a.href.localeCompare(b.href);

        /**
         * @param {any} a 
         * @param {any} b 
         * @param {string} key 
         * @returns {number} */
        var compareField = (a, b, key) => a[key] ? (b[key] ? 0 : +1) : (b[key] ? -1 : 0);

        /**
       * @template {{href: string}} T
       * @param {T[] | undefined} infos 
       * @param {(item: T) => unknown} setup */
        var createNodes = (infos, setup) => (infos || [])?.map(info => (setup(nodeItems[info.href] = (info = { ...info })), info));

        /**
         * @param {MemberNode} memberNode 
         * @param {string} asMemberKey */
        var createMemberNode = (memberNode, asMemberKey) => {
            memberNode.nodeLabel = createElem("H3");
            memberNode.nodeInfos = createElem("div");
            memberNode.nodeAsMember = memberNode;
            memberNode[asMemberKey] = memberNode;
        };

        /** 
         * @param {ClassNode} classNode */
        var createClassNode = (classNode) => {
            classNode.nodeAsClass = classNode;
            classNode.nodeTitle = createElem("H2");
            classNode.nodeLabel = createElem("H3");
            classNode.nodeInfos = createElem("div");
            classNode.nodeMembers = [
                ...createNodes(classNode.fields, node => createMemberNode(node, "nodeAsField")),
                ...createNodes(classNode.methods, node => createMemberNode(node, "nodeAsMethod")),
                ...createNodes(classNode.constructors, node => createMemberNode(node, "nodeAsConstructor"))
            ].sort((a, b) => 0 ||
                compareField(b, a, "isPublic") ||
                compareField(b, a, "isProtected") ||
                compareField(a, b, "isPrivate") ||
                compareField(b, a, "isStatic") ||
                compareField(b, a, "nodeAsField") ||
                compareField(a, b, "nodeAsMethod") ||
                compareHref(a, b)
            );
        }

        /**
         * @param {PackageNode} packageNode  */
        var createPackageNode = (packageNode) => {
            packageNode.nodeAsPackage = packageNode;
            packageNode.nodeTitle = createElem("H1");
            packageNode.nodeInfos = createElem("div");
            packageNode.nodeClasses = createNodes(packageNode.classes, createClassNode).sort(compareHref);
        };

        javadocNode.nodePackages.push(...createNodes(javadocInfo.packages, createPackageNode).sort(compareHref));

    };

    var updateNodes = () => {

        javadocNode.nodeAsJavadoc = javadocNode;

        javadocNode.nodePackages.forEach(packageNode => {
            packageNode.nodeParent = javadocNode;
            packageNode.nodeClasses.forEach(classNode => {
                classNode.nodeParent = javadocNode.nodeItems[classNode.parent] || packageNode;
                classNode.nodeMembers.forEach(memberNode => memberNode.nodeParent = classNode);
            });
        });

    };

    var updateIndos = () => {






        /** 
         * @param {HTMLElement} element 
         * @param {(html: string[]) => unknown} updateHtml
         * @param {((html: string[]) => unknown)|undefined} updateClass */
        var updateElem = (element, updateHtml, updateClass) => {
            let html = [], attr = [];
            updateHtml(html);
            updateClass?.(attr);
            element.innerHTML = html.join('');
            element.className = attr.join('');
        };





        var toText = (src) => (toTextNode.textContent = src, toTextNode.innerHTML);

        var toTextNode = createElem("div");

        /**
         * @template T
         * @param {string[]} res 
         * @param {T[]|undefined} items 
         * @param {string} enter 
         * @param {string} comma 
         * @param {string} leave
         * @param {(res: string[], item: T, index?: number) => unknown} update */
        var pushList = (res, items, enter, comma, leave, update) => {
            if (!items?.length) return;
            res.push(enter);
            items.forEach((item, index) => (index && res.push(comma), update(res, item, index)));
            res.push(leave);
        };

        /**
          * @param {string[]} res 
         * @param {DocInfo[]|undefined} docs */
        var pushDocs = (res, docs) => {
            if (!docs?.length) return;
            docs.forEach(({ text, code, href, type, html }) => {
                if (text) {
                    res.push(toText(text));
                } else if (code) {
                    res.push('<code>', toText(code), '</code>');
                } else if (href) {
                    res.push('<a href="#', href, '">', html, '</a>');
                } else if (type) {
                    res.push('<span class="TYPE-', type, '">', html, '</span>');
                } else {
                    res.push(html);
                }
            });
        };

        /**
         * 
         * @param {string[]} res 
         * @param {TagInfo[]|undefined} tags */
        var pushTags = (res, tags) => pushList(res, tags, '<dl class="TAGS">', '', '</dl>', ({ type, href, html, docs }) => {
            res.push('<dt class="TAG-', type, '">');
            href ? res.push('<a href="#', href, '">', html, '</a>') : res.push(type);
            res.push('</dt><dd>');
            pushDocs(res, docs);
            res.push('</dd>');
        });

        /**
         * 
         * @param {string[]} res 
         * @param {TypeInfo2|undefined} type */
        var pushType = (res, type) => {
            if (!type) return;
            if (type.super?.length) {
                pushList(res, type.super, '?% ', '&amp;', '', pushType);
            } else if (type.extends) {
                pushList(res, type.extends, '?: ', '&amp;', '', pushType);
            } else {
                // TODO link
                var info = javadocNode.nodeItems[type.href];
                if (info) {
                    res.push(info.name)
                } else
                    res.push(type.name)
                pushList(res, type.args, '&lt;', ', ', '&gt;', pushType);
                res.push(type.dims || "");
            }
        };

        /**
         * 
         * @param {string[]} res 
         * @param {ClassNode} classNode */
        var pushName = (res, classNode) => {
            res.push(classNode.name);
        };


        /*
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
       }*/



        /**
         * @param {ItemNode | undefined} itemNode 
         * @returns {ItemNode[]} */
        var computePath = (itemNode) => itemNode ? [...computePath(itemNode.nodeParent), itemNode] : [];



        javadocNode.nodePackages.forEach(packageNode => {
            updateElem(packageNode.nodeTitle, html => {
                html.push('<a name="', packageNode.href, '">', packageNode.name, '</a>');
            });
            updateElem(packageNode.nodeInfos, html => {
                pushDocs(html, packageNode.docs);
                pushTags(html, packageNode.tags);
            });
            packageNode.nodeClasses.forEach(classNode => {
                updateElem(classNode.nodeTitle, res => {
                    res.push('<a name="', classNode.href, '">');
                    pushList(res, computePath(classNode).slice(2), '', '.', '', pushName);
                    res.push('</a>');



                });
                updateElem(classNode.nodeLabel, res => {
                    res.push(classNode.isPublic ? '+ ' : classNode.isProtected ? '# ' : classNode.isPrivate ? '- ' : '~ ');
                    pushList(res, computePath(classNode).slice(1), '', '.', '', pushName);
                    res.push(" TODO  vars"); // TODO

                    // pushType

                    //	this.writeClass_CLASSPATH(classDoc, false);

                    pushList(res, [...(classNode.superclass ? [classNode.superclass] : []), ...(classNode.interfaces || [])], '<br>: ', ', ', '', (res, typeInfo) => {

                    });
                }, res => {
                    res.push('S', classNode.isStatic ? 'T' : 'F', ' A', classNode.isAbstract ? 'T' : 'F');
                });
                updateElem(classNode.nodeInfos, html => {
                    pushDocs(html, classNode.docs);
                    pushTags(html, classNode.tags);
                    //  TODO docs, tags, vars
                });
                classNode.nodeMembers.forEach(memberNode => {
                    updateElem(memberNode.nodeLabel, res => {
                        res.push(classNode.isPublic ? '+ ' : classNode.isProtected ? '# ' : classNode.isPrivate ? '- ' : '~ ');
                        //  TODO  name<get>(...): type, ...
                    }, res => {
                        res.push('S', memberNode.isStatic ? 'T' : 'F', ' A', memberNode.isAbstract ? 'T' : 'F');
                    });
                    updateElem(memberNode.nodeInfos, html => {
                        pushDocs(html, memberNode.docs);
                        pushTags(html, memberNode.tags);
                        //  TODO docs, tags, vars, params, returns, throws
                    });
                });
            });
        });

    };

    createItems();
    updateNodes();
    updateIndos();

    console.log(javadocNode);

    return javadocNode;
}







class JavadocFlags {

    constructor(flags) {
        flags && Object.keys(this).forEach(flag => flags[flag] && (this[flag] = true));
    }

    isPackage = false
    isClass = false
    isField = false
    isMethod = false
    isConstructor = false
    isFinal = false
    isStatic = false
    isPublic = false
    isPrivate = false
    isAbstract = false
    isProtected = false
    isError = false
    isException = false
    isInterface = false
    isSerializable = false
    isExternalizable = false
    isNative = false
    isSynthetic = false
    isSynchronized = false
    isVarargs = false

    toString() {
        return Object.entries(this).filter(([_, val]) => val).map(([key]) => "ITEM-" + key).join(' ');
    }

}


