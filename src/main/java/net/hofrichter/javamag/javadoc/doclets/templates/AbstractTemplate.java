package net.hofrichter.javamag.javadoc.doclets.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Pattern;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.ProgramElementDoc;

import net.hofrichter.javamag.javadoc.doclets.BaseDoclet;
import net.hofrichter.javamag.javadoc.utils.DocletUtil;
import net.hofrichter.javamag.javadoc.utils.FileUtil;

/**
 * Die Klasse ist unsere Basisklasse f&uuml;r die die verschiedenen
 * HTML-Template-Fragmente mit Leben f&uuml;llt.
 * 
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings("restriction")
abstract class AbstractTemplate<D extends Doc> implements ITemplate<D> {

    protected final String templateDir;

    protected final String template;

    protected final DocErrorReporter reporter;

    /**
     * Diese enum repr&auml;sentiert alle Platzhalter, die von dieser
     * "Template-Engine" unterst&uuml;tzt wird.
     * 
     * @author Sven Hofrichter - 15.05.2015 - intial version
     *
     */
    protected enum PLACE_HOLDER {
        CLASS_NAME, PACKAGE_NAME, ANNOTATION_NAME, METHOD_NAME, METHOD_RETURN, METHOD_PARAMETERS, FIELD_NAME, CANONICAL_NAME, MODIFIER, COMMENT, CHILD_CLASSES, CHILD_ANNOTATIONS, CHILD_METHODS, CHILD_FIELDS, SUPER_CLASS_NAME, SUPER_INTERFACE_NAME
    }

    /**
     * Konstruktor, der von der erbendenen Klasse mitteils {@code super}-Aufruf
     * die Initialisierung bestimmter Klassenvariablen vornimmt
     * 
     * @param templateDir ist das Verzeichnis, in dem die Templates vorliegen
     * @param templateFile ist das Template, dass von der erbenden Klasse mit
     *            Leben gef&uuml;llt wird
     * @param reporter ist die Instanz, &uuml;ber die Fehlernachrichten,
     *            Warnungen oder Informationen gemeldet werden
     * @throws IOException wird geworfen, wenn beim Zugriff auf das Template ein
     *             Fehler auftrat.
     */
    protected AbstractTemplate(String templateDir, String templateFile, DocErrorReporter reporter) throws IOException {
        this.reporter = reporter;
        this.templateDir = templateDir;
        this.template = getTemplateContent(Paths.get(templateDir + "/" + templateFile));
    }

    /**
     * Diese Methode wird von {@link #render(ClassDoc)} aufgerufen, um die
     * Details der &uuml;bergebene {@link Doc}zu ermitteln
     * 
     * @param doc ist die von {@link ProgramElementDoc} erbende Instanz, aus die
     *            Werte f&uuml;r den aktuellen Rendering Lauf die Daten bezogen
     *            werden.
     * @return die f&uuml;r das Template relevante Details zur
     *         {@link ProgramElementDoc}-Intstanz
     */
    protected abstract Map<PLACE_HOLDER, String[]> getDetails(D doc);

    /**
     * Diese Methode arbeitet intern mit
     * {@link #renderDetails(ProgramElementDoc)}, um das HTML-Template mit
     * Werten zu f&uuml;llen, schreibt das Ergebnis in die Zieldatei des
     * dokumentierten Javaelements.
     *
     * @param doc ist die {@link ProgramElementDoc}-Instanz, die die Daten des
     *            zu dokumentierenden Java-Elements repr&auml;sentiert
     * @return {@code true} wird zur&uuml;ckgegeben, wenn keine Fehler
     *         auftraten, andernfalls {@code false}
     */
    public boolean render(D doc) throws Exception {
        String fileName = doc.position() != null && doc.position().file() != null
                ? doc.position().file().getAbsolutePath() : doc.name();

        String docPage = DocletUtil.getDocPage(doc);
        Path targetFile = Paths.get(BaseDoclet.outputDir.getAbsolutePath(), docPage);

        boolean success = true;
        if (!targetFile.toFile().exists()) {
            reporter.printNotice("Erstelle Javadoc fuer '" + fileName + "'");
            String templateCopy = renderDetails(doc);
            success = FileUtil.write(targetFile, templateCopy);
            reporter.printNotice(" -> " + docPage);
        }
        return success;
    }

    /**
     * Hilfsmethode für den Konstructor dieser Klasse. Sie implementiert das
     * Lesen des Templates.
     * 
     * @param path ist der Pfad zum Template
     * @return den Inhalt des Templates als String
     * @throws IOException wird bei Fehlern geworfen, die beim Zugriff auf das
     *             Template entstehen k&uuml;nnen
     */
    private String getTemplateContent(Path path) throws IOException {
        URL url = new URL(path.toString());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            reporter.printNotice("Fehler beim Lesen des Templates '" + path.toString() + "'!");
            // anti pattern, aber es ist nur ein Beispiel!
            throw e;
        }
    }

    /**
     * Diese Methode ist eine Hilfsmethode, die von
     * {@link #render(ProgramElementDoc)} aufgerufen wird, um im HTML alle
     * Platzhalter durch Details der {@link ProgramElementDoc}-Intstanz zu
     * ersetzen.
     * 
     * @param doc ist die {@link ProgramElementDoc}-Intstanz
     * @return das aus dem Template resultierende HTML-Fragment
     */
    protected String renderDetails(D doc) {
        Map<PLACE_HOLDER, String[]> map = getDetails(doc);
        String templateCopy = replacePlaceHolders(map);
        return templateCopy;
    }

    /**
     * Diese Methode ersetzt die Platzhalter im Template durch die Werte der
     * &uuml;bergebenen Map-Instanz.
     * 
     * @param map sind die aus der Doc-Instanz extrahierten Details
     * @return HTML-Fragment
     */
    protected String replacePlaceHolders(Map<PLACE_HOLDER, String[]> map) {
        String templateCopy = template;
        if (map != null) {
            // Ersetze die Platzhalter durch die ermittelten Werte:
            for (Map.Entry<PLACE_HOLDER, String[]> entry : map.entrySet()) {
                StringBuffer sb = new StringBuffer();
                for (String value : entry.getValue()) {
                    sb.append(value).append("\n");
                }
                templateCopy = templateCopy.replaceAll("\\{\\{" + entry.getKey().name() + "\\}\\}", sb.toString());
            }
        }
        // Ersetze die uebrig gebliebenen Platzhalter durch einen leeren String:
        for (PLACE_HOLDER ph : PLACE_HOLDER.values()) {
            templateCopy = templateCopy.replaceAll("\\{\\{" + ph.name() + "\\}\\}", "");
        }
        return templateCopy;
    }

    /**
     * Eine Hilfsmethode f&uuml;r die individuellen Template-Klassen, um
     * Templates der Kindelemente zu rendern.
     * 
     * @param template ist die f&uuml;r die Kindelemente zust&auml;ndige
     *            {@link AbstractTemplate}-Instanz
     * @param docs sind die Kindelemente, die in einer Schleife abgearbeitet
     *            werden. Jedes "Kind" wird individuell gerendert und das
     *            Ergebnis angeh&auml;ngt
     * @return das HTML, dass alle HTML-Teilergebnisse beinhaltet
     * @throws IOException wird geworden, wenn es beim Zugriff auf das Template
     *             zu einem Fehler kam
     */
    @SuppressWarnings("unchecked")
    protected <P extends Doc> String renderDetailsHelper(AbstractTemplate<P> template, P... docs) throws IOException {
        if (docs == null || docs.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (P doc : docs) {
            String renderedDetails = template.renderDetails(doc);
            Pattern pattern = Pattern.compile(".*<body[^>]*>(.*)<\\/body>.*",
                    Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            renderedDetails = pattern.matcher(renderedDetails).replaceAll("$1");
            sb.append(renderedDetails);
        }
        return sb.toString();
    }

    /**
     * Ein einfacher Trick, um aus einer Liste von einzelnen Strings ein Array
     * zu machen.
     * 
     * @param values ist die kommaseparierte Liste jener Werte, die als Array
     *            zur&uuml;ckgegeben werden sollen.
     * @return das aus der Parametermenge resultierende Array
     */
    protected String[] buildDetail(Map<PLACE_HOLDER, String[]> map, PLACE_HOLDER key, String... values) {
        map.put(key, values);
        return values;
    }
}