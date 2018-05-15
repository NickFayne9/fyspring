package com.faynely.servlet;

import com.faynely.annotation.Autowired;
import com.faynely.annotation.Controller;
import com.faynely.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author NickFayne 2018-05-13 22:54
 */
@WebServlet(urlPatterns = "/*", initParams = {@WebInitParam(name = "contextConfigLocation", value = "classpath:application-context.properties")})
public class DispatchServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    private Map<String, Object> beanDefinitionMap = new ConcurrentHashMap<>();

    private List<String> classNames = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("-----------------------------");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        //定位
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //加载
        doScanner(contextConfig.getProperty("scanPackages"));

        //注册
        doRegistry();

        //自动依赖注入
        doAutowired();


        initHandlerMapping();
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replace("classpath:", ""));
        try {
            contextConfig.load(inputStream);
            System.out.println("加载资源完成");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));

        File classDir = new File(url.getFile());

        File[] classFiles = classDir.listFiles();

        for(File file : classFiles){
            if(file.isDirectory()){
                doScanner(packageName + "." + file.getName());
            }else {
                classNames.add(packageName + "." + file.getName().replace(".class", ""));
            }
        }
    }

    private void doRegistry() {
        if(classNames.isEmpty()){
            return;
        }

        try {
            for(String className : classNames){
                Class<?> clazz = Class.forName(className);

                if(clazz.isAnnotationPresent(Controller.class)){
                    String beanName = lowerFirstCase(clazz.getSimpleName());

                    beanDefinitionMap.put(beanName, clazz.newInstance());
                } else if(clazz.isAnnotationPresent(Service.class)){
                    Service service = clazz.getAnnotation(Service.class);

                    String beanName = service.value();
                    if("".equals(beanName)){
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }

                    Object instance = clazz.newInstance();
                    beanDefinitionMap.put(beanName, instance);

                    Class<?>[] interfaces = clazz.getInterfaces();

                    for(Class<?> i : interfaces){
                        beanDefinitionMap.put(i.getName(), instance);
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void doAutowired() {
        if(beanDefinitionMap.isEmpty()){
            return;
        }

        for(Map.Entry<String, Object> entry : beanDefinitionMap.entrySet()){
            Field[] fileds = entry.getValue().getClass().getDeclaredFields();

            for(Field field : fileds){
                if(!(field.isAnnotationPresent(Autowired.class))){
                    continue;
                }

                Autowired autowired = field.getAnnotation(Autowired.class);
                String beanName = autowired.value().trim();
                if("".equals(beanName)){
                    beanName = field.getType().getName();
                }

                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), beanDefinitionMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initHandlerMapping() {
        

    }

    private String lowerFirstCase(String str){
        char [] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }


}
