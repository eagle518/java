/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.genelet.framework;

import java.lang.reflect.Method;

/**
 *
 * @author Peter
 */
public class Invoke {
    
    //invoke("Class1", "say", new Class[] {String.class, String.class}, new Object[]{new String("Hello"), new String("World")});
    public static void invoke0(String aClass, String aMethod, Class[] params, Object[] args) throws Exception {
        Class<?> c = Class.forName(aClass);
        Method m = c.getDeclaredMethod(aMethod, params);
        Object i = c.newInstance();
        Object r = m.invoke(i, args);
    }

    public static Object invoke(String aClass, String aMethod, Class[] params, Object[] args) throws Exception {
        Class<?> c = Class.forName(aClass);
        Method m = c.getDeclaredMethod(aMethod, params);
        Object i = c.newInstance();
        Object r = m.invoke(i, args);
        return r;
    }

    public static Object invokeGet(String aClass, String aMethod) throws Exception {
        return invoke(aClass, aMethod, new Class[]{}, new Object[]{});
    }

    public static void invokeSet(String aClass, String aMethod, Class c, Object o) throws Exception {
        invoke0(aClass, aMethod, new Class[]{c}, new Object[]{o});
    }

    public static void invokeInit(String aClass, String aMethod) throws Exception {
        invoke0(aClass, aMethod, new Class[]{}, new Object[]{});
    }
    
    public static void invoke0(Class<?> c, Object i, String aMethod, Class[] params, Object[] args) throws Exception {
        Method m = c.getMethod(aMethod, params);
        Object r = m.invoke(i, args);
    }

    public static Object invoke(Class<?> c, Object i, String aMethod, Class[] params, Object[] args) throws Exception {
        Method m = c.getMethod(aMethod, params);
        Object r = m.invoke(i, args);
        return r;
    }

    public static Object invokeGet(Class<?> c, Object i, String aMethod) throws Exception {
        return invoke(c, i, aMethod, new Class[]{}, new Object[]{});
    }

    public static void invokeSet(Class<?> c, Object i, String aMethod, Class cls, Object obj) throws Exception {
        invoke0(c, i, aMethod, new Class[]{cls}, new Object[]{obj});
    }
    
    public static void invokeInit(Class<?> c, Object i, String aMethod) throws Exception {
        invoke0(c, i, aMethod, new Class[]{}, new Object[]{});
    }
}
