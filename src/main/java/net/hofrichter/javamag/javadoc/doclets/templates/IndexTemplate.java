package net.hofrichter.javamag.javadoc.doclets.templates;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.hofrichter.javamag.javadoc.doclets.BaseDoclet;
import net.hofrichter.javamag.javadoc.utils.FileUtil;

/**
 * Diese Klasse erstellt den Navigationsbaum. Sie leitet als einzigste
 * Templateklasse NICHT vom {@link AbstractTemplate} ab.
 * 
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings({ "restriction", "rawtypes", "unchecked" })
public class IndexTemplate {

    private String templateDir;

    /**
     * Konstruktor der Klasse.
     * 
     * @param templateDir ist das Verzeichnis, in dem sich die Templates
     *            befinden
     * @param reporter ist die {@link DocErrorReporter}-Instanz, &uuml;ber die
     *            Fehlermeldungen, Warnungen oder Informationen kommuniziert
     *            werden
     */
    public IndexTemplate(String templateDir, DocErrorReporter reporter) {
        this.templateDir = templateDir;
    }

    /**
     * Diese Methode erstellt den Navigationsbaum.
     *
     * @param classDocs sind die ClassDoc-Instanzen
     * @return {@code true} wird zur&uuml;ckgegeben, wenn keine Fehler
     *         auftraten, andernfalls {@code false}
     */
    public String render(ClassDoc[] classDocs) throws IOException {
        Set<String> tree = new TreeSet<>();
        for (ClassDoc classDoc : classDocs) {
            tree.add(classDoc.qualifiedName());
        }
        Map<String, Map> map = prepare(tree);

        String template = FileUtil.read(Paths.get(templateDir + "/index.tmpl.html"));
        String result = buildHtml("", map);
        template = template.replaceAll("\\{\\{NAVIGATION_TREE\\}\\}", result);
        Path targetFile = Paths.get(BaseDoclet.outputDir.getAbsolutePath(), "index.html");
        FileUtil.write(targetFile, template);
        return template;
    }

    /**
     * Diese Methode erstellt aus dem &uuml;bergebenen Set eine verschachtelte
     * Map, die f&uuml;r das Rendern des Navigationsbaums herangezogen wird.
     * 
     * @param rawTree ist ein Set, dass die vollqualifizierten Namen aller zu
     *            dokumentierenden Java-Elemente enth&auml;lt
     * @return die verschachtelte Map-Struktur, aus der die verschachtelte
     *         UL-LI-HTML-Struktur des Navigationsbaums erzeugt wird.
     */
    protected Map<String, Map> prepare(Set<String> rawTree) {
        Map<String, Map> map = new HashMap<>();
        for (String item : rawTree) {
            prepareHelper(map, Arrays.asList(item.split("\\.")));
        }
        return map;
    }

    /**
     * Hilfsmethode f&uuml;r die Methode {@link #prepare(Set)}.
     * 
     * @param map ist die Teilmap
     * @param simpleNames ist das Ergebnis von {@link String#split(String)} als
     *            List-Instanz
     */
    private void prepareHelper(Map<String, Map> map, List<String> simpleNames) {
        if (simpleNames == null || simpleNames.isEmpty()) {
            return;
        }
        if (!map.containsKey(simpleNames.get(0))) {
            map.put(simpleNames.get(0), new HashMap<String, Map<String, Map>>());
        }
        if (simpleNames.size() > 1) {
            Map<String, Map> subMap = map.get(simpleNames.get(0));
            prepareHelper(subMap, simpleNames.subList(1, simpleNames.size()));
        }
    }

    /**
     * Diese Methode erstellt aus der verschachtelten Map-Struktur das finale
     * UL-LI-HTML-Struktur des Navigationsbaums. Die Methode arbeitet rekursiv.
     * Der Parameter {@code parent} f&uuml;hrt dabei immer den voll
     * qualifizierten Namen der aktuellen Untertruktur, die mit dem Parameter
     * {@code map} &uuml;bergeben wird.
     * 
     * @param parent ist der voll qualifierte Name des Parents
     * @param map ist die Teilstruktur, die unterhalb des Parents vorliegt
     * @return die verschachtelte UL-LI-Struktur
     */
    protected String buildHtml(String parent, Map<String, Map> map) {
        if (map.isEmpty()) {
            return "";
        }
        parent += parent.isEmpty() ? "" : ".";
        StringBuffer result = new StringBuffer("<ul>");
        for (Entry<String, Map> entry : map.entrySet()) {
            result.append("<li><span title=\"").append(parent);
            result.append(entry.getKey()).append("\">");
            result.append(entry.getKey()).append("</span>");
            if (!entry.getValue().isEmpty()) {
                result.append(buildHtml(parent + entry.getKey(), entry.getValue()));
            }
            result.append("</li>");
        }
        return result.append("</ul>").toString();
    }

}