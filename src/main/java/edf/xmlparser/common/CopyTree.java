package edf.xmlparser.common;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.EnumSet;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * move file by nio
 */
public class CopyTree implements FileVisitor {
    private static Logger logger = Logger.getLogger(CopyTree.class);

    private final Path copyFrom;
    private final Path copyTo;

    public CopyTree(Path copyFrom, Path copyTo) {
        this.copyFrom = copyFrom;
        this.copyTo = copyTo;
    }

    static void copySubTree(Path copyFrom, Path copyTo) throws IOException {
        try {
            Files.copy(copyFrom, copyTo, REPLACE_EXISTING, COPY_ATTRIBUTES);
        } catch (IOException e) {
            System.err.println("Unable to copy " + copyFrom + " [" + e + "]");
        }

    }

    @Override
    public FileVisitResult postVisitDirectory(Object dir, IOException exc)
            throws IOException {
        if (exc == null) {
            Path newdir = copyTo.resolve(copyFrom.relativize((Path) dir));
            try {
                FileTime time = Files.getLastModifiedTime((Path) dir);
                Files.setLastModifiedTime(newdir, time);
            } catch (IOException e) {
                System.err.println("Unable to copy all attributes to: " + newdir+" ["+e+ "]");
            }
        } else {
            throw exc;
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs)
            throws IOException {
        logger.info("Copy directory: " + (Path) dir);
        Path newdir = copyTo.resolve(copyFrom.relativize((Path) dir));
        try {
            Files.copy((Path) dir, newdir, REPLACE_EXISTING, COPY_ATTRIBUTES);
        } catch (IOException e) {
            System.err.println("Unable to create " + newdir + " [" + e + "]");
            return FileVisitResult.SKIP_SUBTREE;
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Object file, BasicFileAttributes attrs)
            throws IOException {
        logger.info("Copy file: " + (Path) file);
        copySubTree((Path) file, copyTo.resolve(copyFrom.relativize((Path) file)));
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Object file, IOException exc)
            throws IOException {
        if (exc instanceof FileSystemLoopException) {
            System.err.println("Cycle was detected: " + (Path) file);
        } else {
            System.err.println("Error occurred, unable to copy:" +(Path) file+" ["+ exc + "]");
        }

        return FileVisitResult.CONTINUE;
    }

    /**
     * move all file of folder
     * @param srcPath
     * @param targetPath
     * @throws IOException
     */
    public void movefolder(String srcPath, String targetPath) throws IOException {
        //"C:\\JayChen\\baiduYunDownload"
        Path copyFrom = Paths.get(srcPath);
        //"C:\\JayChen\\New folder (2)"
        Path copyTo = Paths.get(targetPath);

        CopyTree walk = new CopyTree(copyFrom, copyTo);
        EnumSet opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);

        Files.walkFileTree(copyFrom, opts, Integer.MAX_VALUE, walk);
    }
}