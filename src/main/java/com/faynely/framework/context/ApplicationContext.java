package com.faynely.framework.context;

import com.faynely.framework.annotation.Autowired;
import com.faynely.framework.annotation.Controller;
import com.faynely.framework.annotation.Service;
import com.faynely.framework.beans.BeanDefinition;
import com.faynely.framework.beans.BeanPostProcessor;
import com.faynely.framework.beans.BeanWrapper;
import com.faynely.framework.context.support.BeanDefinitionReader;
import com.faynely.framework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author NickFayne 2018-05-20 10:19
 */
public class ApplicationContext implements BeanFactory {

    private String[] configLocations;

    private BeanDefinitionReader reader;

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private Map<String, Object> beanCacheMap = new HashMap<>();

    /**
     * 用来存储所有的被代理过的对象
     */
    private Map<String, BeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();

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
        doAutoWired();

        System.out.println();
    }

    private void populateBean(String beanName, Object instance){
        Class clazz = instance.getClass();

        if(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class)){
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields){
                if(!field.isAnnotationPresent(Autowired.class)){
                    continue;
                }

                Autowired autowired = field.getAnnotation(Autowired.class);

                String autoWiredBeanName = autowired.value().trim();

                if("".equals(autoWiredBeanName)){
                    autoWiredBeanName = field.getType().getName();
                }

                field.setAccessible(true);

                try {
                    field.set(instance, this.beanWrapperMap.get(autoWiredBeanName).getWrappedInstance());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }else{
            return;
        }

    }

    private void doAutoWired() {
        for(Map.Entry<String, BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();

            if(!beanDefinitionEntry.getValue().getLazyInit()){
                getBean(beanName);
            }
        }
    }

    private void doRegistry(List<String> beanDefinitions) {
        try {
            for(String className : beanDefinitions){
                Class<?> clazz = Class.forName(className);

                if(clazz.isInterface() || !clazz.isAnnotationPresent(Controller.class) || !clazz.isAnnotationPresent(Service.class)){
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

    /**
     * 依赖注入从这里开始
     * @param beanName
     * @return
     */
    @Override
    public Object getBean(String beanName) {

        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        //String className = beanDefinition.getBeanClassName();

        try{
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();


            Object instance = instantiateBean(beanDefinition);
            if(null == instance){
                return null;
            }

            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

            BeanWrapper beanWrapper = new BeanWrapper(instance);
            beanWrapper.setBeanPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName, beanWrapper);

            beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            populateBean(beanName, instance);
            return this.beanWrapperMap.get(beanName).getWrappedInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private Object instantiateBean(BeanDefinition beanDefinition){
        Object instance = null;

        String className = beanDefinition.getBeanClassName();
        try{
            Class<?> clazz = Class.forName(className);
            if(this.beanCacheMap.containsKey(className)){
                instance = this.beanCacheMap.get(className);
            }else{
                instance = clazz.newInstance();
                this.beanCacheMap.put(className, instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;
    }
}
