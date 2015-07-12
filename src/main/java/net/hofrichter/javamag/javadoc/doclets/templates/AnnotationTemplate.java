package net.hofrichter.javamag.javadoc.doclets.templates;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;

import net.hofrichter.javamag.javadoc.doclets.taglets.InheritDocTaglet;
import net.hofrichter.javamag.javadoc.utils.DocletUtil;

/**
 * Die Klasse ist unsere Klasse, die die HTML-Seite f&uuml;r Klassen erstellt.
 * 
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings("restriction")
public class AnnotationTemplate extends AbstractTemplate<AnnotationTypeDoc> {

    private ProgramElementDoc holdingDoc;

    /**
     * Dieser Constructor instanziiert die Template-Klasse für
     * Annotation-javadoc.
     * 
     * @param templateDir ist das Verzeichnis, in dem sich die Templates
     *            befinden
     * @param reporter ist die {@DocErrorReporter}-Instanz
     * @param
     * @throws IOException wird geworfen, wenn beim Lesen des Inhalts der
     *             Templatedatei ein Fehler auftrat.
     */
    public AnnotationTemplate(String templateDir, DocErrorReporter reporter, ProgramElementDoc holdingDoc)
            throws IOException {
        super(templateDir, "annotation.tmpl.html", reporter);
        this.holdingDoc = holdingDoc;
    }

    @Override
    protected Map<PLACE_HOLDER, String[]> getDetails(AnnotationTypeDoc doc) {
        Map<PLACE_HOLDER, String[]> map = new HashMap<>();

        String commentText = holdingDoc.commentText();
        if ("Override".equals(doc.name()) && doc.elements() != null) {
            if (holdingDoc instanceof MethodDoc && (commentText == null || commentText.isEmpty())) {
                MethodDoc methodDoc = (MethodDoc) holdingDoc;
                ClassDoc classDoc = methodDoc.containingClass();
                String uniqueName = uniqueMethodNameBuilder(methodDoc);
                MethodDoc parentMethodDoc = findParentMethodDoc(uniqueName, classDoc.superclass());
                if (parentMethodDoc == null) {
                    parentMethodDoc = findParentMethodDoc(uniqueName, classDoc.interfaces());
                }
                String renderedComment = DocletUtil.renderComment(parentMethodDoc);
                if (!renderedComment.isEmpty()) {
                    commentText = MessageFormat.format(InheritDocTaglet.TAG_TEMPLATE, renderedComment);
                } else if (parentMethodDoc != null) {
                    commentText = MessageFormat.format(InheritDocTaglet.TAG_TEMPLATE,
                            "Konnte nicht ermittelt werden. Bitte im Internet nach \"javadoc\" und \""
                                    + uniqueMethodNameBuilder(parentMethodDoc) + "\" suchen.");
                }
            }
        }

        buildDetail(map, PLACE_HOLDER.ANNOTATION_NAME, doc.qualifiedName());
        buildDetail(map, PLACE_HOLDER.COMMENT, commentText);
        if (doc.containingClass() != null) {
            buildDetail(map, PLACE_HOLDER.CLASS_NAME, doc.containingClass().name());
        }
        buildDetail(map, PLACE_HOLDER.CANONICAL_NAME, doc.qualifiedName());
        buildDetail(map, PLACE_HOLDER.PACKAGE_NAME, doc.containingPackage().name());

        return map;
    }

    /**
     * Hilfsmethode zur Ermittlung der &uuml;bergeordneten Methode im Fall der
     * Annotion <code>@Override</code>.
     * 
     * @param methodName ist der Wert von {@link MethodDoc#flatSignature()} des
     *            in dieser Instanz behandelten MethodDoc's
     * @param classDocs sind die Instanzen, die sich aus
     *            {@link ClassDoc#interfaces()} und
     *            {@link ClassDoc#superclass()} ergeben.
     * @return die MethodDoc-Instanz, wenn eine passende, &uuml;bergeordnete
     *         gefunden wurde
     */
    private MethodDoc findParentMethodDoc(String methodName, ClassDoc... classDocs) {
        if (classDocs != null) {
            for (ClassDoc classDoc : classDocs) {
                for (MethodDoc methodDoc : classDoc.methods(false)) {
                    if (uniqueMethodNameBuilder(methodDoc).equals(methodName)) {
                        return methodDoc;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Hilfsmethode, die aus dem MethodDoc die eindeutige Signatur in seiner
     * Stringrepr&auml;sentation zur&uuml;ckgibt.
     * 
     * @param doc ist die MethodDoc Instanz
     * @return die Signatur der Methode
     */
    private String uniqueMethodNameBuilder(MethodDoc doc) {
        StringBuilder sb = new StringBuilder();
        sb.append(doc.modifiers()).append(" ");
        sb.append(doc.returnType().qualifiedTypeName()).append(" ");
        sb.append(doc.name());
        sb.append(doc.flatSignature());
        return sb.toString();
    }

}