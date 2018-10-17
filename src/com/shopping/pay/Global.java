package com.shopping.pay;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by zhangqi on 2017/5/11.
 */
public class Global {
    private static Resource resource = new ClassPathResource("/resource/config.properties");
    private static Properties props = null;

    public Global() {
    }

    public static String get(String key) {
        if(props == null) {
            try {
                props = PropertiesLoaderUtils.loadProperties(resource);
            } catch (IOException var2) {
                var2.printStackTrace();
                return "";
            }
        }

        return props.getProperty(key);
    }

    public static Long getSessionTimeOut() {
        return Long.valueOf(Long.parseLong(get("sessionTimeOut")));
    }

    public static void main(String[] args) {
        System.out.println(get("sessionTimeOut"));
    }
}
