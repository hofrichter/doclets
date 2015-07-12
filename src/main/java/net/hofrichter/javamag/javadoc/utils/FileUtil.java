package net.hofrichter.javamag.javadoc.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;

import org.apache.log4j.chainsaw.Main;

public final class FileUtil {

    /**
     * Diese Methode implementiert das Lesen einer Datei.
     * 
     * @param path ist der Pfad zur Datei
     * @return den Inhalt der Datei als String
     * @throws IOException wird bei Fehlern geworfen, die beim Zugriff auf die
     *             Datei entstehen k&uuml;nnen
     */
    public static String read(Path path) throws IOException {
        URL url = new URL(path.toString());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            StringBuilder sb = new StringBuilder();
            String line = null;
            String nl = System.lineSeparator();
            while ((line = br.readLine()) != null) {
                sb.append(line).append(nl);
            }
            return sb.toString();
        } catch (IOException e) {
            // anti pattern, aber es ist nur ein Beispiel!
            throw e;
        }
    }

    /**
     * Diese Methode schreibt die &uuml;bergebenen Daten in die angegebene
     * Datei.
     * 
     * @param targetFile ist die Zieldatei
     * @param data sind die Daten, die in die Datei geschrieben werden sollen
     * @return {@code true}, bei Erfolg, andernfalls {@code false}
     * @throws IOException wird bei Fehlern geworfen, die beim Zugriff auf die
     *             Datei entstehen k&uuml;nnen
     */
    public static boolean write(Path targetFile, String data) throws IOException {
        File file = targetFile.toFile();
        if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
            try (FileWriter writer = new FileWriter(targetFile.toUri().toURL().getPath());) {
                writer.write(data);
                writer.flush();
            }
            // Files.write(targetFile, data.getBytes());
            return true;
        }
        return false;
    }

    /**
     * Diese Methode kopiert interne Dateien in das Dateisystem. Sie wird
     * verwendet, um die zu den internen Templates gehörenden Dateien dem apidoc
     * beizustellen.
     * 
     * @param source ist der Name der Datei im JAR
     * @param target ist der Name der Datei im Dateisystem
     */
    public static void copy(String resource, String destination) {
        File resDestFile = new File(destination);
        resDestFile.getParentFile().mkdirs();
        try (InputStream in = Main.class.getClassLoader().getResourceAsStream(resource);
                OutputStream out = new FileOutputStream(resDestFile);) {
            int readBytes;
            byte[] buffer = new byte[4096];
            while ((readBytes = in.read(buffer)) > 0) {
                out.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {

        }
    }
}
