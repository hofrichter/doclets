package net.hofrichter.javamag.javadoc.doclets.templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;

import net.hofrichter.javamag.javadoc.utils.DocletUtil;

/**
 * Die Klasse ist unsere Klasse, die die HTML-Seite f&uuml;r Klassen erstellt.
 * 
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings("restriction")
public class ClassTemplate extends AbstractTemplate<ClassDoc> {

    /**
     * Dieser Constructor instanziiert die Template-Klasse für Klassen-javadoc.
     * 
     * @param templateDir ist das Verzeichnis, in dem sich die Templates
     *            befinden
     * @param reporter ist die {@DocErrorReporter}-Instanz
     * @throws IOException wird geworfen, wenn beim Lesen des Inhalts der
     *             Templatedatei ein Fehler auftrat.
     */
    public ClassTemplate(String templateDir, DocErrorReporter reporter) throws IOException {
        super(templateDir, "class.tmpl.html", reporter);
    }

    @Override
    protected Map<PLACE_HOLDER, String[]> getDetails(ClassDoc doc) {
        Map<PLACE_HOLDER, String[]> map = new HashMap<>();
        buildDetail(map, PLACE_HOLDER.CLASS_NAME, doc.name());
        if (doc.superclass() != null && !"Object".equals(doc.superclass().name())) {
            buildDetail(map, PLACE_HOLDER.SUPER_CLASS_NAME, "extends " + doc.superclass().name());
        }
        if (doc.interfaces() != null && doc.interfaces().length > 0) {
            StringBuffer sb = new StringBuffer("implements ");
            for (ClassDoc iface : doc.interfaces()) {
                sb.append(iface.name());
            }
            buildDetail(map, PLACE_HOLDER.SUPER_INTERFACE_NAME, sb.toString());
        }
        buildDetail(map, PLACE_HOLDER.MODIFIER, doc.modifiers());
        buildDetail(map, PLACE_HOLDER.COMMENT, DocletUtil.renderComment(doc));
        buildDetail(map, PLACE_HOLDER.CANONICAL_NAME, doc.qualifiedTypeName());
        buildDetail(map, PLACE_HOLDER.PACKAGE_NAME, doc.containingPackage().name());
        
        List<AnnotationTypeDoc> annotationDescs = new ArrayList<>();
        for (AnnotationDesc annotation : doc.annotations()) {
            annotationDescs.add(annotation.annotationType());
        }
        try {
            String annotations = renderDetailsHelper(new AnnotationTemplate(templateDir, reporter, doc),
                    annotationDescs.toArray(new AnnotationTypeDoc[] {}));
            buildDetail(map, PLACE_HOLDER.CHILD_ANNOTATIONS, annotations);

            String methods = renderDetailsHelper(new MethodTemplate(templateDir, reporter), doc.methods());
            buildDetail(map, PLACE_HOLDER.CHILD_METHODS, methods);

            String fields = renderDetailsHelper(new FieldTemplate(templateDir, reporter), doc.fields());
            buildDetail(map, PLACE_HOLDER.CHILD_FIELDS, fields);
        } catch (Exception e) {
            reporter.printError("Failed to create javadoc for members of " + doc.qualifiedTypeName() + "! " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return map;
    }
}