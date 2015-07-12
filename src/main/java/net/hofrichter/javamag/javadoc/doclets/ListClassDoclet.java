package net.hofrichter.javamag.javadoc.doclets;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.standard.Standard;

/**
 * Das Doclet leitet von {@link Standard}-Doclet ab, um gewisse Standard-Parameter
 * bereitzustellen, die man sonst nachbauen m&uuml;sste. Unter anderem wird von
 * {@link Standard}-Doclet der Parameter "-d" interpretiert, der das Verzeichnis der
 * generierten HTML-Seiten definiert.
 * 
 * @author Hofrichter, Sven (FI-APM)
 */
@SuppressWarnings("restriction")
public class ListClassDoclet extends Standard {

	public static boolean start(RootDoc root) {
		ClassDoc[] classes = root.classes();
		for (int i = 0; i < classes.length; ++i) {
			System.out.println(classes[i]);
		}
		return Standard.start(root);
	}
}