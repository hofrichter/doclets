package net.hofrichter.javamag.javadoc.doclets.templates;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.PackageDoc;

import net.hofrichter.javamag.javadoc.utils.DocletUtil;

/**
 * Die Klasse ist unsere Klasse, die die HTML-Seite f&uuml;r Packages erstellt.
 * 
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings("restriction")
public class PackageTemplate extends AbstractTemplate<PackageDoc> {

    /**
     * Dieser Constructor instanziiert die Template-Klasse für Package-javadoc.
     * 
     * @param templateDir ist das Verzeichnis, in dem sich die Templates
     *            befinden
     * @param reporter ist die {@DocErrorReporter}-Instanz
     * @throws IOException wird geworfen, wenn beim Lesen des Inhalts der
     *             Templatedatei ein Fehler auftrat.
     */
    public PackageTemplate(String templateDir, DocErrorReporter reporter) throws IOException {
        super(templateDir, "package.tmpl.html", reporter);
    }

    @Override
    protected Map<PLACE_HOLDER, String[]> getDetails(PackageDoc doc) {
        Map<PLACE_HOLDER, String[]> map = new HashMap<>();
        buildDetail(map, PLACE_HOLDER.PACKAGE_NAME, doc.name());
        buildDetail(map, PLACE_HOLDER.COMMENT, DocletUtil.renderComment(doc));
        StringBuffer sb = new StringBuffer();
        ClassDoc[] docs = doc.allClasses(false);
        if (docs != null) {
            for (ClassDoc classDoc : docs) {
                sb.append("<li>");
                sb.append("<a href=\"javascript:void(0);\" onclick=\"navigateTo('").append(classDoc.qualifiedName()).append("');\">");
                sb.append(classDoc.qualifiedName()).append("</a>");
                sb.append("</li>");
            }
        }
        buildDetail(map, PLACE_HOLDER.CHILD_CLASSES, sb.toString());
        return map;
    }
}