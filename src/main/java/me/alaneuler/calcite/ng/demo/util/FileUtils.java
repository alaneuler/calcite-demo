package me.alaneuler.calcite.ng.demo.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtils {
  public static Collection<File> listFiles(String dir) {
    return org.apache.commons.io.FileUtils.listFiles(getFile(dir), null, false);
  }

  public static String getFileContent(String filePath) {
    return getFilesContents(filePath).get(0);
  }

  public static List<String> getFilesContents(String dir) {
    return listFiles(dir).stream()
        .map(
            file -> {
              try {
                return org.apache.commons.io.FileUtils.readFileToString(
                    file, StandardCharsets.UTF_8);
              } catch (Throwable e) {
                return "";
              }
            })
        .collect(Collectors.toList());
  }

  public static File getFile(String path) {
    try {
      return new File(FileUtils.class.getClassLoader().getResource(path).toURI());
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}
