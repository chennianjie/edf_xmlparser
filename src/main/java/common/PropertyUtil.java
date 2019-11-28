package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @Description:
 * @Author: nianjie.chen
 * @Date: 11/27/2019
 */
public class PropertyUtil {

    private static HashMap<String, String> map;
    static {
        if (map == null) {
           map = new HashMap<>();
        }

        Properties properties = new Properties();
        InputStream in = PropertyUtil.class.getResourceAsStream("/appConfig-dev");
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));
        try {
            properties.load(bf);
            Enumeration<String> enumeration = (Enumeration<String>) properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String n = enumeration.nextElement();
                String v = properties.getProperty(n);
                map.put(n,v);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get property_ids
     * @return
     */
    public static ArrayList<Long> getPropertyIds(){
        String propertyIds = getPropValue("PropertyIds");
        String[] split = trimComma(propertyIds).split(",");
        ArrayList<Long> list = new ArrayList<>();
        for (String s : split) {
            list.add(Long.parseLong(s));

        }
        return list;
    }

    public static String getPropValue(String key) {
        return map.get(key);
    }

    public static HashMap<String, String> getMap() {
        return map;
    }

    /**
     * comma delete
     * @param value
     * @return
     */
    private static String trimComma(String value) {
        int len = value.length();
        int st = 0;
        char[] val = value.toCharArray();

        while ((st < len) && (val[st] <= ',')) {
            st++;
        }
        while ((st < len) && (val[len - 1] <= ',')) {
            len--;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;
    }

    public static void main(String[] args) {
        HashMap<String, String> map = PropertyUtil.getMap();
        Set<String> strings = map.keySet();
        for (String n : strings) {
            System.out.println(n + " " + map.get(n));
        }
    }
}
