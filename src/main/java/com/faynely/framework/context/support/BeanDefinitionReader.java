package com.faynely.framework.context.support;

import com.faynely.framework.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author NickFayne 2018-05-20 10:36
 */
public class BeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registryBeanClasses = new ArrayList<>();

    private final String SCAN_PACKAGES = "scanPackages";

    public BeanDefinitionReader(String ... locations) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));

        try {
            config.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        doScanner(config.getProperty(SCAN_PACKAGES));

    }

    public List<String> loadBeanDefinitions(){
        return this.registryBeanClasses;
    }

    /**
     * 每注册一个 className，就返回一个 BeanDefinition
     * @param className
     * @return
     */
    public BeanDefinition registerBean(String className){
        if(this.registryBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setLazyInit(false);
            beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".") + 1)));
            return beanDefinition;
        }
        return null;
    }

    private void doScanner(String packageName){
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classFileDir = new File(url.getFile());

        File[] classFiles = classFileDir.listFiles();
        for(File classFile : classFiles){
            if(classFile.isDirectory()){
                doScanner(packageName + "." + classFile.getName());
            }else{
                registryBeanClasses.add(packageName + "." + classFile.getName().replace(".class", ""));
            }

        }
    }

    public Properties getConfig() {
        return config;
    }

    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
