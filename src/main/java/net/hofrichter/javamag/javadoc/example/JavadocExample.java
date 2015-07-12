package net.hofrichter.javamag.javadoc.example;

import net.hofrichter.javamag.javadoc.taglets.NotizTaglet;

/**
 * Dieses Beispiel zeigt die Verwendung von Taglets in Form einer
 * Implementierung (siehe {@link NotizTaglet}), als auch ein per
 * javadoc-Parameter "-tag" gesteuertes Taglet.
 * 
 * @notiz Notiz Nummer eins
 * @notiz Notiz Nummer zwei
 * 
 * @author Sven Hofrichter (javamagazin@hofrichter.net) - 15.05.2015 - intial version
 */
public class JavadocExample {

	/**
	 * Diese Methode dient nur als Beispiel.
	 * 
	 * @param args werden in diesem Fall nicht ausgewertet
	 * @ohneTagletImpl Ein Beispiel eines benutzerspezifischen Taglets, dass per
	 *                 Kommandozeilenaufruf interpretiert wird
	 */
	public static void main(final String[] args) {
	}
}
