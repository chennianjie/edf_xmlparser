package common;



import org.apache.log4j.Logger;

import java.io.*;
import java.util.zip.GZIPInputStream;

import static org.springframework.util.StreamUtils.BUFFER_SIZE;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 9/17/2019
 */
public class ZipTools {

    private static Logger logger = Logger.getLogger(ZipTools.class);

    public static void unGzipFile(String sourcedir) {
        String ouputfile;
        try {
            FileInputStream fin = new FileInputStream(sourcedir);
            GZIPInputStream gzin = new GZIPInputStream(fin);
            ouputfile = sourcedir.substring(0,sourcedir.lastIndexOf('.'));
            FileOutputStream fout = new FileOutputStream(ouputfile);
            int num;
            byte[] buf=new byte[BUFFER_SIZE];
            while ((num = gzin.read(buf,0,buf.length)) != -1)
            {
                fout.write(buf,0,num);
            }
            if (fin != null) {
                fin.close();
            }
            if (gzin != null) {
                gzin.close();
            }
            if (fout != null) {
                fout.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public static void unzipFolder(String filePath) {
        int count = 0;
        logger.info("unzip start : " + filePath);
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.getName().endsWith(".gz")) {
                unGzipFile(f.getAbsolutePath());
                logger.info(f.getAbsolutePath());
                count++;
            }
        }
        logger.info("unzip end : " + filePath + "   unzip file num:" + count);
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        unzipFolder("C:\\Users\\U6079438\\Desktop\\PDP");
        long end = System.currentTimeMillis();
        logger.info(end - start);
    }
}
