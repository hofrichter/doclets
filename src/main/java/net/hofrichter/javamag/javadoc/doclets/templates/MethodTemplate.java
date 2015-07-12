package net.hofrichter.javamag.javadoc.doclets.templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

import net.hofrichter.javamag.javadoc.doclets.BaseDoclet;
import net.hofrichter.javamag.javadoc.utils.DocletUtil;

/**
 * Die Klasse ist unsere Klasse, die die HTML-Seite f&uuml;r Klassen erstellt.
 * 
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings("restriction")
public class MethodTemplate extends AbstractTemplate<MethodDoc> {

    /**
     * Dieser Constructor instanziiert die Template-Klasse für Methoden-javadoc.
     * 
     * @param templateDir ist das Verzeichnis, in dem sich die Templates
     *            befinden
     * @param reporter ist die {@DocErrorReporter}-Instanz
     * @throws IOException wird geworfen, wenn beim Lesen des Inhalts der
     *             Templatedatei ein Fehler auftrat.
     */
    public MethodTemplate(String templateDir, DocErrorReporter reporter) throws IOException {
        super(templateDir, "method.tmpl.html", reporter);
    }

    @Override
    protected Map<PLACE_HOLDER, String[]> getDetails(MethodDoc doc) {
        Map<PLACE_HOLDER, String[]> map = new HashMap<>();
        buildDetail(map, PLACE_HOLDER.MODIFIER, doc.modifiers() + " <span class=\"return\">" + doc.returnType().toString() + "</span>");
        buildDetail(map, PLACE_HOLDER.METHOD_NAME, doc.name());
        buildDetail(map, PLACE_HOLDER.COMMENT, DocletUtil.renderComment(doc));
        buildDetail(map, PLACE_HOLDER.CANONICAL_NAME, doc.qualifiedName());
        buildDetail(map, PLACE_HOLDER.CLASS_NAME, doc.containingClass().name());
        buildDetail(map, PLACE_HOLDER.PACKAGE_NAME, doc.containingPackage().name());

        List<AnnotationTypeDoc> annotationDescs = new ArrayList<>();
        for (AnnotationDesc annotation : doc.annotations()) {
            annotationDescs.add(annotation.annotationType());
        }
        try {
            StringBuffer method = new StringBuffer();
            Parameter[] params = doc.parameters();
            for (int i = 0; i < params.length; i++) {
                method.append(i > 0 ? ", " : "").append(params[i].toString());
            }
            buildDetail(map, PLACE_HOLDER.METHOD_PARAMETERS, method.toString());

            Tag[] returnTags = doc.tags("return");
            Taglet returnTaglet = BaseDoclet.taglets.get("@return");
            StringBuffer methodReturn = new StringBuffer();
            if (returnTags.length > 0) {
                methodReturn.append("<li><strong>").append(doc.returnType().qualifiedTypeName()).append("</strong> ");
                doc.returnType().qualifiedTypeName();
                if (returnTaglet != null) {
                    methodReturn.append(returnTaglet.toString(returnTags[0]));
                } else {
                    methodReturn.append(returnTags[0].text());
                }
                methodReturn.append("</li>");
            }
            buildDetail(map, PLACE_HOLDER.METHOD_RETURN, methodReturn.toString());

            StringBuffer fields = new StringBuffer();
            ParamTag[] tags = doc.paramTags();
            for (ParamTag tag : tags) {
                if (tag.parameterName().isEmpty()) {
                    continue;
                }
                fields.append("<li><strong>").append(tag.parameterName()).append("</strong> ");
                fields.append(tag.parameterComment()).append("</li>");
            }
            
            buildDetail(map, PLACE_HOLDER.CHILD_FIELDS, fields.toString());

            String annotations = renderDetailsHelper(new AnnotationTemplate(templateDir, reporter, doc),
                    annotationDescs.toArray(new AnnotationTypeDoc[] {}));
            buildDetail(map, PLACE_HOLDER.CHILD_ANNOTATIONS, annotations);

        } catch (Exception e) {
            reporter.printError(
                    "Failed to create javadoc for members of " + doc.qualifiedName() + "! " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return map;
    }

}