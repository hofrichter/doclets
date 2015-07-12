package net.hofrichter.javamag.javadoc.doclets.taglets;

import java.lang.reflect.Method;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * Da wir mit unserer eigenen Doclet-Implementierung auf die Unterst&uuml;tzung
 * von Standard-Taglets verzichten m&uuml;ssen, bedienen wir uns der
 * Reflections-API und bauen mit Hilfe dieses Taglets wenigstens die
 * Eigenschaften nach. Das wurde so realisiert, dass im Konstruktor dieser
 * Klasse vom zu simulierenden Taglet eine Instanz erstellt wird und aus dieser
 * die Werte der Methoden {@link #getName()}, {@link #isInlineTag()},
 * {@link #inField()}, {@link #inConstructor()}, {@link #inMethod()},
 * {@link #inType()}, {@link #inPackage()} sowie {@link #inOverview()} ausgelesen
 * und in diese WrapperTaglet-Instanz &uuml;bernommen werden.
 *
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings("restriction")
public class WrapperTaglet implements Taglet {

	private final String tagletName;

	private final String blockCommentLabel;

	private final boolean isInlineTag;

	private final boolean inField;

	private final boolean inConstructor;

	private final boolean inMethod;

	private final boolean inType;

	private final boolean inPackage;

	private final boolean inOverview;

	/**
	 * Dieser Konstruktor ruft Methoden der &uuml;bergebenen Taglet-Klasse auf,
	 * ihre Eigenschaften in diese Instanz zu &uuml;bernehmen.
	 * 
	 * @param tagletClassName ist der voll qualifizierte KLassenname der
	 *         Taglet-Klasse aus dem Package
	 *        {@code com.sun.tools.doclets.internal.toolkit.taglets}
     * @throws Exception, wird geworfen, wenn beim Zugriff auf die
     *         &uuml;bergebene Klasse ein Fehler auftrat 
	 */
	public WrapperTaglet(String tagletClassName) throws Exception {
        Class<?> tagletClass = (Class<?>) Class.forName(tagletClassName);
        Object instance = tagletClass.newInstance();
		this.tagletName = invoke(instance, "getName", String.class);
		this.blockCommentLabel = tagletName.substring(0, 1).toUpperCase() + tagletName.substring(1);
		this.isInlineTag = invoke(instance, "isInlineTag", Boolean.class);
		this.inConstructor = invoke(instance, "inConstructor", Boolean.class);
		this.inField = invoke(instance, "inField", Boolean.class);
		this.inMethod = invoke(instance, "inMethod", Boolean.class);
		this.inType = invoke(instance, "inType", Boolean.class);
		this.inPackage = invoke(instance, "inPackage", Boolean.class);
		this.inOverview = invoke(instance, "inOverview", Boolean.class);
	}

	/**
     * Diese Methode wird vom Konstruktor verwendet, um die Eigenschaften der
     * von dieser Klasse nachgeahmten Taglet-Implementierung aus dem
     * Standard-Doclet-API nachzubauen. Dabei erfolgt die Ausgabe immer nach dem
     * gleichen Schema, wodurch sich die Ausgabe zwar nicht unterscheidet, der
     * jeweilige aber wenigsten im HTML ausgegeben wird.
	 * 
	 * @param instance ist die Instance der jeweiligen Klasse aus dem Package
	 *        {@code com.sun.tools.doclets.internal.toolkit.taglets} 
	 * @param methodName ist der Name jener Methode, deren Wert wir abfragen
	 *        wollen
	 * @param returnType ist der Return-Typ der aufgerufenen Methode
	 */
    @SuppressWarnings("unchecked")
    private static <T> T invoke(Object instance, String methodName, Class<T> returnType) throws Exception {
        Method method = instance.getClass().getMethod(methodName);
        return (T) method.invoke(instance);
    }

	@Override
	public String getName() {
		return tagletName;
	}

	@Override
	public boolean inConstructor() {
		return inConstructor;
	}

	@Override
	public boolean inField() {
		return inField;
	}

	@Override
	public boolean inMethod() {
		return inMethod;
	}

	@Override
	public boolean inType() {
		return inType;
	}

	@Override
	public boolean inPackage() {
		return inPackage;
	}

	@Override
	public boolean inOverview() {
		return inOverview;
	}

	@Override
	public boolean isInlineTag() {
		return isInlineTag;
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
		switch (tag.name()) {
			case "@code": return "<code>" + tag.text() + "</code>";
			default: return tag.text();
		}
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
			sb.append(blockCommentLabel);
			sb.append(":</dt>");
			for (Tag tag : tags) {
				sb.append("<dd>").append(tag).append("</dd>");
			}
		}
		return sb.toString();
	}
}
