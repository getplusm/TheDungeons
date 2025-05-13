package t.me.p1azmer.plugin.dungeons.core.common.file;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@UtilityClass
public class FileUtils {

    public void copy(@NotNull InputStream inputStream, @NotNull File file) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] array = new byte[1024];
            int read;
            while ((read = inputStream.read(array)) > 0) {
                outputStream.write(array, 0, read);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean create(@NotNull File file) {
        if (file.exists()) return false;

        File parent = file.getParentFile();
        if (parent == null) return false;

        parent.mkdirs();
        try {
            return file.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public @NotNull List<File> getConfigFiles(@NotNull String path) {
        return getConfigFiles(path, false);
    }

    public @NotNull List<File> getConfigFiles(@NotNull String path, boolean deep) {
        return getFiles(path, ".yml", deep);
    }

    public @NotNull List<File> getFiles(@NotNull String path) {
        return getFiles(path, false);
    }

    public @NotNull List<File> getFiles(@NotNull String path, boolean deep) {
        return getFiles(path, null, deep);
    }

    public @NotNull List<File> getFiles(@NotNull String path, @Nullable String extension, boolean deep) {
        List<File> files = new ArrayList<>();

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) return files;

        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (extension == null || file.getName().endsWith(extension)) {
                    files.add(file);
                }
            } else if (file.isDirectory() && deep) {
                files.addAll(getFiles(file.getPath(), true));
            }
        }
        return files;
    }

    public @NotNull List<File> getFolders(@NotNull String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) return Collections.emptyList();

        return Stream.of(listOfFiles).filter(File::isDirectory).toList();
    }

    public boolean deleteRecursive(@NotNull String path) {
        return deleteRecursive(new File(path));
    }

    public boolean deleteRecursive(@NotNull File dir) {
        if (!dir.exists()) return false;

        File[] inside = dir.listFiles();
        if (inside != null) {
            for (File file : inside) {
                deleteRecursive(file);
            }
        }
        return dir.delete();
    }

    public void extractResources(@NotNull File pluginFile, @NotNull String fromPath, @NotNull File destination) {
        if (!destination.exists()) {
            if (!destination.mkdirs()) {
                return;
            }
        }

        try {
            JarFile jar = new JarFile(pluginFile);
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String path = entry.getName();
                if (entry.isDirectory() || !path.startsWith(fromPath)) continue;

                File file = new File(destination, path.replaceFirst(fromPath, ""));
                if (file.exists()) continue;

                FileUtils.create(file);
                InputStream inputStream = jar.getInputStream(entry);
                FileOutputStream outputStream = new FileOutputStream(file);

                while (inputStream.available() > 0) {
                    outputStream.write(inputStream.read());
                }
                outputStream.close();
                inputStream.close();
            }

            jar.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public @NotNull File resolveRelative(@NotNull File file) {
        if (!file.exists()) {
            return new File(relativize(file.getPath()));
        }
        return file;
    }

    public @NotNull String relativize(@NotNull String path) {
        String[] split = path.split(Pattern.quote(File.separator));
        StringBuilder out = new StringBuilder();
        int skip = 0;
        int len = split.length - 1;
        for (int i = len; i >= 0; i--) {
            if (skip > 0) {
                skip--;
            } else {
                String arg = split[i];
                if (arg.equals("..")) {
                    skip++;
                } else {
                    out.insert(0, arg + (i == len ? "" : File.separator));
                }
            }
        }
        return out.toString();
    }
}