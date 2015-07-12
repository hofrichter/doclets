package net.hofrichter.javamag.javadoc.utils;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;

import net.hofrichter.javamag.javadoc.doclets.BaseDoclet;

/**
 * Diese Klasse implementiert diverse Hilfsfunktionen, rund um {@link Tag}- und
 * {@link Doc}-Instanzen.
 * 
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings("restriction")
public final class DocletUtil {

    /**
     * Diese Methode gibt den relativen Pfad der HTML-Seite, in die die Inhalte
     * der {@link Doc}-Instanz geschrieben werden.
     * 
     * @param docc ist die {@link Doc}-Instanz
     * @return der Pfad zur Datei
     */
    public static String getDocPage(Doc doc) {
        String name = null;
        if (doc instanceof ProgramElementDoc) {
            name = ((ProgramElementDoc) doc).qualifiedName();
        } else if (doc instanceof PackageDoc) {
            name = doc.name();
        } else {
            name = doc.name();
        }
        String targetFile = name.replace('.', '/') + ".html";
        return targetFile;
    }

    /**
     * Diese Methode ermittelt aus der &uuml;bergebenen {@link Doc}-Instanz den
     * den relativen Pfad zum Einstiegsverzeichnis, ausgehend von der zu
     * erwartenden Ergebnis-HTML-Seite.
     * 
     * @param docc ist die {@link Doc}-Instanz
     * @return der Pfad zur Datei
     */
    public static String getDocRoot(Doc doc) {
        String str = getDocPage(doc);
        String path = str.replaceAll("\\/*[^\\/]+\\.html$", "");
        path = path.replaceAll("[^\\/\\\\]+", "..") + "/";
        return path;
    }

    /**
     * Diese Methode ermittelt den URL zum Element in unserem Javadoc, das sich
     * hinter dem {@link Doc} verbirgt.
     * 
     * @param tag ist der {@link Doc}-Instanz verbirgt
     * @return die URL, relativ zur aktuellen Position
     */
    public static String getUrl(Doc doc) {
        return getDocRoot(doc) + getDocPage(doc);
    }

    /**
     * Diese Methode ermittelt den URL zum Element in unserem Javadoc, das sich
     * hinter dem {@link Tag} verbirgt.
     * 
     * @param tag ist der {@link Tag}-Instanz verbirgt
     * @return die URL, relativ zur aktuellen Position
     */
    public static String getUrl(Tag tag) {
        String url = tag.text();
        if (!tag.text().startsWith("#")) {
            String className = tag.text().replaceAll("\\#(.*)$", "");
            Doc doc = tag.holder();
            ClassDoc classDoc = null;
            if (doc instanceof ClassDoc) {
                classDoc = (ClassDoc) doc;
            } else if (doc instanceof ProgramElementDoc) {
                classDoc = ((ProgramElementDoc) doc).containingClass();
            }
            if (classDoc != null) {
                ClassDoc referencedClass = classDoc.findClass(className);
                String root = getDocRoot(classDoc);
                if (root != null) {
                    // Fallback-Loesung, falls das Auffinden der Klasse nicht
                    // erfolgreich verlief, weil keine voll qualifizierte
                    // Information vorlag:
                    if (referencedClass == null) {
                        for (ClassDoc packageClass : classDoc.containingPackage().allClasses()) {
                            if (packageClass.name().equals(className)) {
                                referencedClass = packageClass;
                                break;
                            }
                        }
                    }
                    // Sollte die Klasse bis hierhin nicht gefunden worden sein,
                    // kann es nur zwei Ursachen haben:
                    // 1. der Entwickler hat einen Fehler in seinem
                    // @link-Kommentar
                    // 2. die Klasse wird von Javadoc nicht aufgegriffen, weil
                    // sie explizit aufgrund ihres Scopes (public, protected,
                    // <default>, private) ausgesteuert wird
                    if (referencedClass == null) {
                        BaseDoclet.printWarning(tag.position(),
                                "Fehler bei der Ermittlung des Javadoc-Links in " + doc.name()
                                        + "! Die angegebene Referenz '" + tag.text() + "' kann nicht aufgelöst werden, "
                                        + "weil sie entweder nicht existiert oder im falschen "
                                        + "Scope (private, protected oder <default> (package-protected) "
                                        + "vorliegt.");
                    } else {
                        url = root + getUrl(referencedClass);
                    }
                }
            }
        }
        if (tag.text().contains("#")) {
            url += tag.text();
        }
        return url;
    }


    /**
     * Hilfsmethode f&uuml;r das rendern des Kommentartextes. Diese Methode
     * &uuml;bernimmt das Ersetzen der Tags durch ihr jeweiliges Renderergebnis.
     *
     * @param doc ist die Doc-Instanz
     */
    public static String renderComment(Doc doc) {
        if (doc != null && doc.inlineTags() != null) {
            return renderCommentInlineTags(doc.inlineTags());
        }
        return "";
    }

    /**
     * Hilfsmethode f&uuml;r das rendern des Kommentartextes anhand von
     * Inline-Tags. Diese Methode &uuml;bernimmt das Ersetzen der Tags durch ihr
     * jeweiliges Renderergebnis.
     *
     * @param tags sind die Tag-Instanzen
     */
    public static String renderCommentInlineTags(Tag... tags) {
        StringBuffer sb = new StringBuffer();
        if (tags != null) {
            for (Tag tag : tags) {
                if (tag.name().equalsIgnoreCase("text")) {
                    sb.append(tag.text() == null ? "" : tag.text());
                    continue;
                }
                String tagName = String.valueOf(tag.name()).replaceFirst("^@", "");
                if (BaseDoclet.taglets.containsKey(tagName)) {
                    sb.append(BaseDoclet.taglets.get(tagName).toString(tag));
                } else {
                    sb.append(tag.text() == null ? "" : tag.text());
                }
            }
        }
        return sb.toString();
    }
}
