package net.hofrichter.javamag.javadoc.taglets;

import java.util.Map;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * <p>
 * Zur Aktivierung dieses Taglets muss der eben genannte Aufrufparameter durch
 * diese Sequenz ersetzt werden:
 * </p>
 * 
 * <pre>
 * javadoc -tagletpath=... -taglet net.hofrichter.javamag.javadoc.taglets.AuthorTaglet ...
 * </pre>
 * <p>
 * Wichtiger Hinweis: das Taglet "author" wird im javadoc standardm&auml;&szlig;
 * nicht eingebunden und muss entweder per Kommandozeile oder in der
 * Konfiguration des javadoc-Plugins des jeweiligen Buildtools aktiviert werden.
 * </p>
 * 
 * <pre>
 * javadoc ... -tag author:a:"Author:"
 * </pre>
 * 
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings("restriction")
public class AuthorTaglet implements Taglet {

	protected static final String TAGLET_NAME = "author";

	public String getName() {
		return TAGLET_NAME;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void register(Map tagletMap) {
		tagletMap.put(TAGLET_NAME, new AuthorTaglet());
	}

	public boolean inConstructor() {
		return false;
	}

	public boolean inField() {
		return false;
	}

	public boolean inMethod() {
		return false;
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

	public boolean isInlineTag() {
		return false;
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
		return tag.text();
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
			for (int i = 0; i < tags.length; i++) {
				sb.append("<dd>");
				sb.append(renderAuthor(tags[i]));
				sb.append("</dd>");
			}
		}
		if (sb.length() > 0) {	
			StringBuffer prefix = new StringBuffer();
			prefix.append("<dt style=\"font-weight:bold;text-decoration:underline\">");
			prefix.append(tags.length > 1 ? "Autoren" : "Autor");
			prefix.append(":</dt>");
			prefix.append(sb.toString());
			sb = prefix;
		}
		return sb.toString();
	}

	private String renderAuthor(Tag tag) {
		String author = toString(tag);
		if (author == null) {
			return "";
		}
		Doc doc = tag.holder();
		String className = doc.name();
		
		// E-Mail-Adressen werden in mailto-Links umgewandelt:
		author = author
		        .replaceAll(
		                "(^|[^a-zA-Z0-9_.+-])([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]{2,4})($|[^a-zA-Z0-9-.])",
		                "$1<a href=\"mailto:$2?subject=Anmerkung zur Javaklasse '"
		                        + className + "'\">$2</a>$3");
		return author;
	}
}
