package com.faynely.framework.context;

import com.faynely.framework.beans.BeanDefinition;
import com.faynely.framework.context.support.BeanDefinitionReader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author NickFayne 2018-05-20 10:19
 */
public class ApplicationContext {

    private String[] configLocations;

    private BeanDefinitionReader reader;

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public ApplicationContext(String ... locations){
        this.configLocations = locations;
        refresh();
    }

    private void refresh(){
        //定位
        reader = new BeanDefinitionReader(configLocations);
        //加载
        List<String> beanDefinitions = reader.loadBeanDefinitions();
        //注册
        doRegistry(beanDefinitions);
        //依赖注入
    }

    private void doRegistry(List<String> beanDefinitions) {
        try {
            for(String className : beanDefinitions){
                Class<?> clazz = Class.forName(className);

                if(clazz.isInterface()){
                    continue;
                }

                BeanDefinition beanDefinition = reader.registerBean(className);
                if(beanDefinition != null){
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
                }

                Class<?>[] interfaces = clazz.getInterfaces();
                for(Class<?> i : interfaces){
                    this.beanDefinitionMap.put(i.getName(), beanDefinition);
                }
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
