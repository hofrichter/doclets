package net.hofrichter.javamag.javadoc.doclets.templates;

import com.sun.javadoc.Doc;

/**
 * Interface zur Typisierung der *Template-Klassen.
 * 
 * @author Sven Hofrichter - 15.05.2015 - intial version
 *
 * @param <D> ist der Generic Type, der von {@link Doc} erben sein muss.
 */
@SuppressWarnings("restriction")
public interface ITemplate<D extends Doc> {
}
