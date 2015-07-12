package net.hofrichter.javamag.javadoc.doclets;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.hofrichter.javamag.javadoc.utils.FileUtil;

@SuppressWarnings("restriction")
public class BaseDocletTester {

    public static void main(String[] args) throws IOException {
        if (args.length != 1 || !new File(args[0]).exists()) {
            System.err.println();
            System.err.println("java " + BaseDocletTester.class.getSimpleName() + " <maven-repo>");
            System.err.println();
            System.exit(1);
        }
        
        
        URL location = BaseDocletTester.class.getProtectionDomain().getCodeSource().getLocation();
        String root = location.getPath();
        Path options = Paths.get(location.toExternalForm(), "options");
        Path targetFile = Files.createTempFile("options", null);
        String content = FileUtil.read(options);
        content = content.replaceAll("\\{\\{root\\}\\}", options.getParent().getParent().getParent().toString());
        content = content.replaceAll("\\{\\{repo\\}\\}", args[0]);
        FileUtil.write(targetFile, content);
        
        String target = Paths.get(".", "target", "apidocs").toAbsolutePath().normalize().toFile().getAbsolutePath();
        
        String optionsFile = targetFile.toAbsolutePath().toString();
        
        System.out.println("Using options-file: " + optionsFile);
        
        com.sun.tools.javadoc.Main.main("-d", target, "@" + optionsFile, "@" + root + "packages");
    }
}