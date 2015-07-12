package net.hofrichter.javamag.javadoc.doclets.templates;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.FieldDoc;

import net.hofrichter.javamag.javadoc.utils.DocletUtil;

/**
 * Die Klasse ist unsere Klasse, die die HTML-Seite f&uuml;r Klassen erstellt.
 * 
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings("restriction")
public class FieldTemplate extends AbstractTemplate<FieldDoc> {

    /**
     * Dieser Constructor instanziiert die Template-Klasse für Feld-/Attribut-javadoc.
     * 
     * @param templateDir ist das Verzeichnis, in dem sich die Templates
     *            befinden
     * @param reporter ist die {@DocErrorReporter}-Instanz
     * @throws IOException wird geworfen, wenn beim Lesen des Inhalts der
     *             Templatedatei ein Fehler auftrat.
     */
    public FieldTemplate(String templateDir, DocErrorReporter reporter) throws IOException {
        super(templateDir, "field.tmpl.html", reporter);
    }
    
    @Override
    protected Map<PLACE_HOLDER, String[]> getDetails(FieldDoc doc) {
        Map<PLACE_HOLDER, String[]> map = new HashMap<>();
        buildDetail(map, PLACE_HOLDER.FIELD_NAME, doc.name());
        buildDetail(map, PLACE_HOLDER.MODIFIER, doc.modifiers());
        buildDetail(map, PLACE_HOLDER.COMMENT, DocletUtil.renderComment(doc));
        buildDetail(map, PLACE_HOLDER.CANONICAL_NAME, doc.qualifiedName());
        buildDetail(map, PLACE_HOLDER.CLASS_NAME, doc.containingClass().name());
        buildDetail(map, PLACE_HOLDER.PACKAGE_NAME, doc.containingPackage().name());
        return map;
    }
    
}