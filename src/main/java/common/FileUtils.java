package common;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 10/7/2019
 */
public class FileUtils {

    private static Logger logger = Logger.getLogger(FileUtils.class);

    public static void showFileName(List<File> files) {
        logger.info("=============files============");
        for (File file : files) {
            logger.info(file.getName());
        }
        logger.info("===========files end==========");
    }

    public static void delete(String filePath) {
        Path path = Paths.get(filePath);
        try {
            Files.delete(path);
            logger.info("delete success！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(String srcPath, String targetPath) {
        Path path1 = Paths.get(srcPath);
        Path path2 = Paths.get(targetPath);
        try {
            Files.copy(path1, path2);
            logger.info(srcPath + "copy to :" + targetPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void move(String srcPath, String targetPath) {
        Path path1 = Paths.get(srcPath);
        Path path2 = Paths.get(targetPath);
        try {
            Files.move(path1, path2);
            logger.info(srcPath + "move to :" + targetPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * move all gz_files from folder
     * @param srcFolderPath
     * @param targetFolderPath
     */
    public static void moveGzFiles(String srcFolderPath, String targetFolderPath) {
        logger.info("moveGzFiles start : " + srcFolderPath);
        int count = 0;
        File file = new File(srcFolderPath);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.getName().endsWith(".gz")) {
                move(f.getAbsolutePath(), targetFolderPath + "\\" + f.getName());
                logger.info(f.getAbsolutePath() + " to " + targetFolderPath + "\\" + f.getName());
                count++;
            }
        }
        logger.info("moveGzFiles end : " + targetFolderPath + "   gzFileCount:" + count);
    }


    public static boolean moveAndRenameFile(File srcFile, String destPath, String uuid) {
        // Destination directory
        File dir = new File(destPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // Move file to new directory
        boolean success = srcFile.renameTo(new File(dir, srcFile.getName()
                + "__" + uuid));
        logger.info("file move success: " + srcFile.getName() + "   " + destPath);
        return success;
    }

    public static String getFileType(String fileName) {
        String[] strArray = fileName.split("\\.");
        int suffixIndex = strArray.length - 1;
        return strArray[suffixIndex];
    }



    public static List<File> getLocalAbsFiles(String localPath) {
        File localfile = new File(localPath);
        File[] iFiles = localfile.listFiles();
        if (iFiles != null) {
            List<File> filelist = Arrays.asList(iFiles);
            Collections.sort(filelist, (o1, o2) -> {
                if (o1.isDirectory() && o2.isFile()) {
                    return -1;
                }
                if (o1.isFile() && o2.isDirectory()) {
                    return 1;
                }
                return o1.getName().compareTo(o2.getName());
            });
            return filelist;
        } else {
            return null;
        }

    }
}
