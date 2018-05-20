# fyspring 概述

fyspring 是一个简化版的 spring，主要是学习 spring 优秀的编程思想。这个同样也和 fybatis 一样，从零开始手写。

# 反射基础知识

获取类全限定名：`clazz.getName();`

获取类简单名称：`clazz.getSimpleName();`

获取类所在包名：`clazz.getPackage().getName();`

获取类中所有方法（包含所有父类）：`clazz.getMethods();`

获取类中所有方法（不包含父类，Declared 表示只是本类的）：`clazz.getDeclaredMethods();`

通过 java 路径，获取资源，并输出为 stream：`clazz.getClassLoader().getResourceAsStream();`

通过 java 路径，获取资源，并输出为 URL：`clazz.getClassLoader().getResource();`

# v1.0.0

spring 的 IOC 流程主要分为三步：定位，加载，注册。

定位：找到配置文件。

加载：将配置文件中的类名存入 List 中。

注册：将标记为需要 spring 托管的类进行实例化，以全限定类名为 key，类实例为 value，存入 IOC 容器。（具体 spring 并不是这样做的，这里简化了一下）

![](https://github.com/NickFayne9/git-resource/blob/master/fyspring/customSpring1.png?raw=true)

这里看一下 spring 中的类名 List 和 IOC 容器 Map 之间的关系。

![](https://github.com/NickFayne9/git-resource/blob/master/fyspring/customSpringClassRelation%20.png?raw=true)

下面请看 v1.0.0 的类图。其实就一个类。

![](https://github.com/NickFayne9/git-resource/blob/master/fyspring/customSpringClass.png?raw=true)

# v2.0.0

在第一版中，所有的方法都集中在一个类中，这样明显是不合理的。

在真正的 Spring 中，有好多个类来管理这4个流程。（定位，加载，注册，依赖反转）

