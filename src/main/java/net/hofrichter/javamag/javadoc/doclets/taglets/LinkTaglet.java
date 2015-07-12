package net.hofrichter.javamag.javadoc.doclets.taglets;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * Implementierung des Link-Taglets.
 *
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings("restriction")
public class LinkTaglet implements Taglet {

    private static final String TAGLET_NAME = "link";

    @Override
    public String getName() {
        return TAGLET_NAME;
    }

    @Override
    public boolean inConstructor() {
        return true;
    }

    @Override
    public boolean inField() {
        return true;
    }

    @Override
    public boolean inMethod() {
        return true;
    }

    @Override
    public boolean inType() {
        return true;
    }

    @Override
    public boolean inPackage() {
        return true;
    }

    @Override
    public boolean inOverview() {
        return true;
    }

    @Override
    public boolean isInlineTag() {
        return true;
    }

    /**
     * Diese Methode wird aufgerufen, sobald das Taglet als inline-Taglet
     * verwendet wurde, um die entsprechende HTML-Ausgabe generieren zu lassen.
     * 
     * @param tag ist das Taglet.
     * @return das HTML-Fragment, welches den Wert des Taglets zum Einf&uuml;gen
     *         ins Javadoc formatiert.
     */
    public String toString(Tag tag) {
        Doc doc = tag.holder();
        ClassDoc classDoc = null;

        if (doc instanceof ClassDoc) {
            classDoc = (ClassDoc) doc;
        } else if (doc instanceof ProgramElementDoc) {
            classDoc = ((ProgramElementDoc) doc).containingClass();
        }
        if (classDoc != null && !tag.text().startsWith("#")) {
            String className = tag.text().replaceAll("\\#(.*)$", "");
            classDoc = classDoc.findClass(className);
        }
        StringBuffer sb = new StringBuffer();
        if (classDoc != null) {
            sb.append("<a title=\"").append(classDoc.qualifiedName()).append("#")
                    .append("\" href=\"javascript:void(0);\" onclick=\"navigateTo('").append(classDoc.qualifiedName())
                    .append("');\">");
            sb.append(tag.text()).append("</a>");
        }
        return sb.toString();
    }

    /**
     * Wird f&uuml;r alle nicht "inline"-Taglets (also block-Taglets) verwendet,
     * um das HTML-Fragment zu generieren.
     * 
     * @param tags sind die einzelnen Taglet-Tags, deren Werte wir hier
     *            auswerten wollen.
     * @return das HTML-Fragment, welches die einzelnen Taglet-Angaben zum
     *         Einf&uuml;gen ins Javadoc formatiert.
     */
    public String toString(Tag[] tags) {
        StringBuffer sb = new StringBuffer();
        if (tags != null && tags.length > 0) {
            for (Tag tag : tags) {
                sb.append("<dd>").append(tag).append("</dd>");
            }
        }
        return sb.toString();
    }
}
