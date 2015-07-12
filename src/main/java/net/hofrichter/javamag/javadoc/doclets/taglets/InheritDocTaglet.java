package net.hofrichter.javamag.javadoc.doclets.taglets;

import java.text.MessageFormat;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

/**
 * Implementierung des Link-Taglets.
 *
 * @author Sven Hofrichter - 15.05.2015 - intial version
 */
@SuppressWarnings("restriction")
public class InheritDocTaglet extends WrapperTaglet {

    private static final String TAGLET_CLASS = "com.sun.tools.doclets.internal.toolkit.taglets.InheritDocTaglet";

    public  static final String TAG_TEMPLATE = "<span class=\"inheritDoc\">{0}</span>";

    public InheritDocTaglet() throws Exception {
        super(TAGLET_CLASS);
    }

    /** {@inheritDoc} */
    @Override
    public String toString(Tag tag) {
        Doc doc = tag.holder();
        String commentText = null;
        if (doc instanceof MethodDoc) {
            commentText = ((MethodDoc) doc).overriddenMethod().commentText();
        } else if (doc instanceof ClassDoc) {
            commentText = ((ClassDoc) doc).superclass().commentText();
        }
        return MessageFormat.format(TAG_TEMPLATE, commentText);
    }
}
