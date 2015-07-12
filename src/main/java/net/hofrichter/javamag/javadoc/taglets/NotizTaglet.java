package net.hofrichter.javamag.javadoc.taglets;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * <p>
 * Zur Aktivierung dieses Taglets muss der eben genannte Aufrufparameter durch
 * diese Sequenz ersetzt werden:
 * </p>
 * 
 * <pre>
 * javadoc -tagletpath=... -taglet net.hofrichter.javamag.javadoc.taglets.NotizTaglet ...
 * </pre>
 * 
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings({"restriction", "unchecked", "rawtypes"})
public class NotizTaglet implements Taglet {

	protected final static String TAGLET_NAME = "notiz";

	public static void register(Map tagletMap) {
		tagletMap.put(TAGLET_NAME, new NotizTaglet());
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
		return "<i>" + tag.text() + "</i>";
	}

	/**
	 * Wird f&uuml;r alle nicht "inline"-Taglets (also block-Taglets) verwendet,
	 * um das HTML-Fragment zu generieren.
	 * 
	 * @param tags sind die einzelnen Taglet-Tags, deren Werte wir hier
	 *        auswerten wollen.
	 * @return das HTML-Fragment, welches die einzelnen Taglet-Angaben zum
	 *         Einf&uuml;gen ins Javadoc formatiert.
	 */
	public String toString(Tag[] tags) {
		StringBuffer sb = new StringBuffer();
		if (tags != null && tags.length > 0) {
			sb.append("<dt style=\"font-weight:bold;text-decoration:underline\">");
			sb.append(tags.length > 1 ? "Notizen" : "Notiz");
			sb.append(":</dt>");
			for (int i = 0; i < tags.length; i++) {
				sb.append("<dd>- ");
				sb.append(toString(tags[i]));
				sb.append("</dd>");
			}
		}
		return sb.toString();
	}

	/**
	 * Name des Taglets. Bitte beachten, dass ein Taglet-Name ohne Punkt dazu
	 * f&uuml;hrt, dass man einen Hinweis bekommt, der dazu dient,
	 * unbeabsichtigte Konflikte mit Standard-Taglets zu vermeiden.
	 */
	public String getName() {
		return TAGLET_NAME;
	}

	public boolean isInlineTag() {
		return false;
	}

	public boolean inConstructor() {
		return true;
	}

	public boolean inField() {
		return true;
	}

	public boolean inMethod() {
		return true;
	}

	public boolean inOverview() {
		return true;
	}

	public boolean inPackage() {
		return true;
	}

	public boolean inType() {
		return true;
	}

}
