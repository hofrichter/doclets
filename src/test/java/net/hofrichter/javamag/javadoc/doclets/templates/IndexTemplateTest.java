package net.hofrichter.javamag.javadoc.doclets.templates;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import junit.framework.TestCase;

public class IndexTemplateTest extends TestCase {
    
    @Test
    public void testTree() throws Exception {
        IndexTemplate template = new IndexTemplate(null, null);
        Set<String> set = new TreeSet<String>();
        set.add("net");
        set.add("net.hofrichter");
        set.add("net.hofrichter.test1");
        set.add("net.hofrichter.test1.test11");
        set.add("net.hofrichter.test1.test11.test111");
        set.add("net.hofrichter.test1.test11.test112");
        set.add("net.hofrichter.test1.test11.test113");
        set.add("net.hofrichter.test1.test12");
        set.add("net.hofrichter.test1.test12.test121");
        set.add("net.hofrichter.test1.test12.test122");
        set.add("net.hofrichter.test1.test12.test123");
        set.add("net.hofrichter.test1.test13");
        set.add("net.hofrichter.test1.test13.test131");
        set.add("net.hofrichter.test1.test13.test132");
        set.add("net.hofrichter.test1.test13.test133");
        Map map = template.prepare(set);
        System.out.println(map);
    }

}
