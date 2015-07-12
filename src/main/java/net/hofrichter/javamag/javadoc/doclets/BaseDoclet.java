package net.hofrichter.javamag.javadoc.doclets;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.tools.doclets.Taglet;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.hofrichter.javamag.javadoc.doclets.taglets.InheritDocTaglet;
import net.hofrichter.javamag.javadoc.doclets.taglets.LinkTaglet;
import net.hofrichter.javamag.javadoc.doclets.taglets.WrapperTaglet;
import net.hofrichter.javamag.javadoc.doclets.templates.ClassTemplate;
import net.hofrichter.javamag.javadoc.doclets.templates.IndexTemplate;
import net.hofrichter.javamag.javadoc.doclets.templates.PackageTemplate;
import net.hofrichter.javamag.javadoc.utils.FileUtil;

/**
 * Die Klasse ist unsere 'Main'.
 * 
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings("restriction")
public class BaseDoclet extends Doclet {

    private static String templateDirectory = null;

    public final static String[] DEFAULT_RESOURCES = { "res/javamagazin-javadoc.js", "res/javamagazin-javadoc.css",
            "res/jquery-2.1.4.min.js", "res/bootstrap-3.3.4.min.js", "res/bootstrap-3.3.4.min.css" };

    private static DocErrorReporter errorReporter;

    @SuppressWarnings("serial")
    private final static Map<String, Integer> DUMMIES_FOR_MAVEN = new HashMap<String, Integer>() {
        {
            put("-classpath", 2);
            put("-doclet", 2);
            put("-docletpath", 2);
            put("-encoding", 2);
            put("-protected", 1);
            put("-sourcepath", 2);
            put("-use", 1);
            put("-version", 1);
            put("-author", 1);
            put("-bottom", 2);
            put("-linkoffline", 3);
            put("-charset", 2);
            put("-docencoding", 2);
            put("-doctitle", 2);
            put("-windowtitle", 2);
            put("-tagletpath", 2);
        }
    };

    private final static String[] DEFAULT_TAGLETS = new String[] {
            "com.sun.tools.doclets.internal.toolkit.taglets.CodeTaglet",
            "com.sun.tools.doclets.internal.toolkit.taglets.DeprecatedTaglet",
            "com.sun.tools.doclets.internal.toolkit.taglets.DocRootTaglet",
            "com.sun.tools.doclets.internal.toolkit.taglets.ExpertTaglet",
            // "com.sun.tools.doclets.internal.toolkit.taglets.InheritableTaglet",
            "com.sun.tools.doclets.internal.toolkit.taglets.InheritDocTaglet",
            // "com.sun.tools.doclets.internal.toolkit.taglets.LegacyTaglet",
            "com.sun.tools.doclets.internal.toolkit.taglets.LiteralTaglet",
            "com.sun.tools.doclets.internal.toolkit.taglets.ParamTaglet",
            "com.sun.tools.doclets.internal.toolkit.taglets.PropertyGetterTaglet",
            "com.sun.tools.doclets.internal.toolkit.taglets.PropertySetterTaglet",
            "com.sun.tools.doclets.internal.toolkit.taglets.ReturnTaglet",
            "com.sun.tools.doclets.internal.toolkit.taglets.SeeTaglet",
            // "com.sun.tools.doclets.internal.toolkit.taglets.SimpleTaglet",
            "com.sun.tools.doclets.internal.toolkit.taglets.ThrowsTaglet",
            "com.sun.tools.doclets.internal.toolkit.taglets.ValueTaglet" };

    public static Map<String, Taglet> taglets = new HashMap<String, Taglet>();

    /** Ist das Ausgabeverzeichnis. */
    public static File outputDir;

    /** Template f&uuml;r die Hauptseite (die index.html im Ergebnis). */
    public static IndexTemplate indexTemplate;

    /**
     * Template f&uuml;r die Klassendetails (jede Klasse bekommt ihre eigene
     * HTML-Seite).
     */
    public static ClassTemplate classTemplate;

    /**
     * Template f&uuml;r die Package-Details (jedes Package bekommt seine eigene
     * HTML-Seite).
     */
    public static PackageTemplate packageTemplate;

    /**
     * Diese Methode ist DER Einstiegspunkt in die Doclet-Logik. In ihm wird aus
     * der &uuml;bergebebenen {@link RootDoc}-Instanz die gesamte Dokumentation
     * erstellt.
     *
     * @param root ist die {@link RootDoc}-Instanz die uns alles bereitstellt,
     *            was wir f&uuml;r die Erstellung ben&ouml;tigen
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean start(RootDoc root) {
        try {
            ClassDoc[] classDocs = root.classes();
            List classDocList = new ArrayList(Arrays.asList(classDocs));
            PackageDoc[] packages = root.specifiedPackages();
            new IndexTemplate(templateDirectory, root).render(classDocs);
            for (PackageDoc packageDoc : packages) {
                packageTemplate.render(packageDoc);
                ClassDoc[] allClasses = packageDoc.allClasses(false);
                if (allClasses != null && allClasses.length > 0) {
                    classDocList.addAll(Arrays.asList(allClasses));
                }
            }
            for (Object classDoc : classDocList) {
                classTemplate.render((ClassDoc) classDoc);
            }
            return true;
        } catch (Exception e) {
            printError("Failed to create javadoc!", e);
        }
        return false;
    }

    /**
     * Diese Methode dient der validierung der &uuml;bergebenen
     * Kommandozeilenparameter, nachdem vom Javadoc-Tool mittels
     * {@link #optionLength(String)} die zul&auml;ssige Anzahl festgelegt wurde.
     *
     * @param options sind die Parameter
     * @param reporter ist die Instanz, der die Validierungsfehler
     *            &uuml;bergeben werden
     * @return {@code true} wenn die verwendeten Kommandozeilenparameter der
     *         Definition entsprachen, andernfalls {@code false}
     */
    public static boolean validOptions(String options[][], DocErrorReporter reporter) {
        errorReporter = reporter;
        boolean result = true;
        initDefaultTaglets(reporter);
        try {
            for (String[] option : options) {
                StringBuffer sb = new StringBuffer("Kommandozeilen-Parameter: ");
                for (String op : option) {
                    sb.append(" ").append(op);
                }

                // seit Java7 möglich: nicht mehr nur Konstanten, sondern auch
                // bspw.
                // Strings in switch-Anweisungen:
                switch (option[0]) {
                case "-d":
                    outputDir = new File(option[1]);
                    break;
                case "-t":
                    templateDirectory = "file:" + option[1];
                    break;

                case "-taglet":
                    initCustomTaglet(option[1], reporter);
                    break;
                // Von Maven und anderen Buildtools werden bei Minimalparame-
                // trisierung in der Regel weitere Javadoc-Standard-Parameter
                // eingereicht, die wir allerdings nicht unterstützen bzw.
                // nachimplementieren wollen. Die von Maven eingereichten
                // haben wir in die Variable DUMMIES_FOR_MAVEN ausgelagert
                // und verbinden diese mit der Ausgabe einer Information,
                // dass wir diese nicht unterstützen
                default:
                    if (DUMMIES_FOR_MAVEN.containsKey(option[0])) {
                        String msg = trim(sb.toString(), 60) + " >> Dummy wird verwendet.";
                        printNotice(msg);
                    } else {
                        String msg = trim(sb.toString(), 60) + " >> Parameter wird nicht unterstuetzt!!!";
                        printError(msg);
                        result = false;
                    }
                }
            }
            if (!result) {
                return result;
            }
            if (outputDir == null) {
                // Javadoc wurde ohne "-d" Option gestartet. Wir verwenden in
                // diesem Fall das aktuelle Arbeitsverzeichnis des Anwenders
                outputDir = Paths.get(".").toAbsolutePath().normalize().toFile();
            }
            if (templateDirectory == null) {
                String pwd = BaseDoclet.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                if (new File(pwd).isDirectory()) {
                    templateDirectory = "file://" + new File(pwd).getAbsolutePath();
                } else {
                    templateDirectory = "jar:file:" + new File(pwd).getAbsolutePath() + "!";
                }
                for (String res : DEFAULT_RESOURCES) {
                    FileUtil.copy(res, outputDir + "/" + res);
                }
            }
            
            
            printNotice("Templates-Quelle: " + templateDirectory);
            printNotice("Zielverzeichnis:  " + outputDir.getAbsolutePath());
            classTemplate = new ClassTemplate(templateDirectory, reporter);
            packageTemplate = new PackageTemplate(templateDirectory, reporter);
            indexTemplate = new IndexTemplate(templateDirectory, reporter);

        } catch (Exception e) {
            printError("Startfehler!", e);
            result = false;
        }
        return result;
    }

    /**
     * Diese Methode k&uuml;rzt die Zeichenkette auf die angegebene L&auml;nge
     * und h&auml;ngt drei Punkte an, wenn die Zeichenkette l&auml;nger als die
     * gew&uuml;nschte Maximall&auml;nge war.
     * 
     * @param text ist die zu k&uuml;rzende Zeichenkette
     * @param length ist die maximal zul&auml;ssige L&auml;nge der Zeichenkette.
     * @return die gek&uuml;rzte Zeichenkette
     */
    private static String trim(String text, int length) {
        if (text.length() > length) {
            text = text.substring(0, length - 3) + "...";
        }
        return text;
    }

    /**
     * Hilfsmethode f&uuml;r {@link #validOptions(String[][], DocErrorReporter)}
     * .
     *
     * @param reporter ist die {@link DocErrorReporter}-Instanz, die zur Ausgabe
     *            von Fehlern angesrpeochen werden soll
     * @return Ergebnis der INitialisierung, wobei {@code false} im Fehlerfall
     *         zur&uuml;ckgegeben wird, andernfalls {@code true}
     */
    private static void initDefaultTaglets(DocErrorReporter reporter) {
        for (String tagletName : DEFAULT_TAGLETS) {
            try {
                WrapperTaglet taglet = new WrapperTaglet(tagletName);
                taglets.put(taglet.getName(), taglet);
            } catch (Exception e) {
                printWarning("Standard-Taglet '" + tagletName
                        + "' ist nicht verfügbar, weil die Initialisierung fehlschlug! ", e);
            }
        }
        LinkTaglet linkTaglet = new LinkTaglet();
        taglets.put(linkTaglet.getName(), linkTaglet);
        try {
            InheritDocTaglet inheritDocTaglet = new InheritDocTaglet();
            taglets.put(inheritDocTaglet.getName(), inheritDocTaglet);
        } catch (Exception e) {
            printWarning("Taglet 'InheritDoc' ist nicht verfügbar, weil die Initialisierung fehlschlug! ", e);
        }
    }

    /**
     * Diese Methode dient der Initialisiuerung von Taglets, die wir im weiteren
     * Verlauf unterst&uuml;tzen wollen.
     * 
     * @param name ist der Name des Taglets
     * @param reporter ist die Instanz, &uuml;ber die die Methode im Fehlerfall
     *            Details ausgibt
     */
    @SuppressWarnings("unchecked")
    private static boolean initCustomTaglet(String tagletName, DocErrorReporter reporter) {
        try {
            Class<Taglet> taglet = (Class<Taglet>) Class.forName(tagletName);
            Method method = taglet.getMethod("register", Map.class);
            method.invoke(taglet, taglets);
            return true;
        } catch (Exception e) {
            printError(
                    "Das Taglet '" + tagletName + "' konnte nicht geladen werden! ", e);
            return false;
        }
    }

    /**
     * Diese Methode legt f&uuml;r jedes Attribut die zul&auml;ssige Anzahl der
     * Angaben fest. Pro zul&auml;ssiges Argument ist mindestens eine 1
     * zur&uuml;ckzugeben, was einem Parameter ohne Werte (quasi einem Flag)
     * gleichkommt.
     *
     * @param option ist der jeweilige Kommandozeilenparameter
     * @return die Anzahl der Angaben, die dieser Parameter mindestens erwartet
     */
    public static int optionLength(String option) {
        switch (option) {
        case "-d":
            return 2;
        case "-t":
            return 2;
        case "-taglet":
            return 2;
        case "-tagletpath":
            return 2;

        // Die nachfolgenden Parameter werden von Maven's javadoc-Plugin
        // adressiert und wurden in diesem Beispiel nur als dummies
        // eingefuegt. Hierzu zaehlen: -doclet, -docletpath, -classpath,
        // -encoding, -protected, -sourcepath
        default:
            if (DUMMIES_FOR_MAVEN.containsKey(option)) {
                return DUMMIES_FOR_MAVEN.get(option);
            }
            return 2;
        }
    }

    /**
     * Diese Methode definiert die Version, f&uuml;r die dieses Doclet geeignet
     * ist.
     * 
     * @return Liefert den Wert {@code LanguageVersion#JAVA_1_5}
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    /**
     * Diese Methode dient der Ausgabe von Fehler-Nachrichten.
     * 
     * @param msg ist die Nachricht, die ausgegeben werden soll
     */
    public static void printError(String msg) {
        errorReporter.printError(msg);
    }

    /**
     * Diese Methode dient der Ausgabe von Fehler-Nachrichten.
     * 
     * @param msg ist die Nachricht, die ausgegeben werden soll
     * @param pos ist die Stelle in der jeweiligen Javaklasse, auf die sich die
     *            Nachricht bezieht
     */
    public static void printError(SourcePosition pos, String msg) {
        errorReporter.printError(pos, msg);
    }

    /**
     * Diese Methode dient der Ausgabe von Fehler-Nachrichten.
     * 
     * @param msg ist die Nachricht, die ausgegeben werden soll
     * @param e ist die Exception-Instanz
     */
    public static void printError(String msg, Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errorReporter.printError(msg + " " + errors.toString());
    }

   /**
     * Diese Methode dient der Ausgabe von Warnungen.
     * 
     * @param msg ist die Nachricht, die ausgegeben werden soll
     */
    public static void printWarning(String msg) {
        errorReporter.printWarning(msg);
    }

    /**
     * Diese Methode dient der Ausgabe von Warnungen.
     * 
     * @param msg ist die Nachricht, die ausgegeben werden soll
     * @param pos ist die Stelle in der jeweiligen Javaklasse, auf die sich die
     *            Nachricht bezieht
     */
    public static void printWarning(SourcePosition pos, String msg) {
        errorReporter.printWarning(pos, msg);
    }

    /**
     * Diese Methode dient der Ausgabe von Warnungen.
     * 
     * @param msg ist die Nachricht, die ausgegeben werden soll
     * @param e ist die Exception-Instanz
     */
    public static void printWarning(String msg, Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        errorReporter.printWarning(msg + " " + errors.toString());
    }

    /**
     * Diese Methode dient der Ausgabe von Informationen.
     * 
     * @param msg ist die Nachricht, die ausgegeben werden soll
     */
    public static void printNotice(String msg) {
        errorReporter.printNotice(msg);
    }

    /**
     * Diese Methode dient der Ausgabe von Informationen.
     * 
     * @param msg ist die Nachricht, die ausgegeben werden soll
     * @param pos ist die Stelle in der jeweiligen Javaklasse, auf die sich die
     *            Nachricht bezieht
     */
    public static void printNotice(SourcePosition pos, String msg) {
        errorReporter.printNotice(pos, msg);
    }

}