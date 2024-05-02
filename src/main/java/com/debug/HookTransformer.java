package com.debug;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

public class HookTransformer implements ClassFileTransformer {

    private final CtClass ctClass;
    private final CtMethod ctMethod;
    private final byte[] byteCode;

    private final String qualifiedClassName;

    public static void replace(Instrumentation inst, String className, String methodName, String implementation) {
        try {
            ClassPool cp = ClassPool.getDefault();
            // cp.insertClassPath(new LoaderClassPath(loader));
            CtClass cc = cp.get(className);
            CtMethod cm = cc.getDeclaredMethod(methodName);
            Agent.log("CtMethod: %s", cm);
            // cm.insertBefore("System.out.println(\"[Hook] skip check.\"); return 0;");
            cm.setBody(implementation);
            byte[] byteCode = cc.toBytecode();
            Agent.log("byteCode size: %d", byteCode.length);
            cc.detach();

            HookTransformer ht = new HookTransformer(cc, cm, byteCode);
            inst.addTransformer(ht, true);
            inst.retransformClasses(cc.getClass());
            Agent.log("Hooked: %s->%s", cc.getName(), cm.getName());
        } catch (Exception e) {
            Agent.log("Failed to hook %s->%s: %s", className, methodName, e);
        }
    }

    public static void preCheck(String className, String methodName, Instrumentation instrumentation) {
        Class<?> targetClass = null;
        Method targetMethod = null;
        try {
            targetClass = Class.forName(className);
        } catch (Exception ex) {
            Agent.log("Class not found with Class.forName: %s", className);
        }
        if (targetClass == null) {
            for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
                if (clazz.getName().equals(className)) {
                    targetClass = clazz;
                    break;
                }
            }
        }

        if (targetClass == null) {
            throw new RuntimeException("Failed to find class [" + className + "]");
        }
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                targetMethod = method;
                break;
            }
        }
        if (targetMethod == null) {
            throw new RuntimeException("Failed to find Method: [" + methodName + "]");
        }
    }

    HookTransformer(CtClass ctClass, CtMethod ctMethod, byte[] byteCode) {
        this.ctClass = ctClass;
        this.ctMethod = ctMethod;
        this.byteCode = byteCode;
        this.qualifiedClassName = ctClass.getName().replace(".", "/");
    }

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        if (!this.qualifiedClassName.equals(className)) {
            return classfileBuffer;
        }
        Agent.log("transform: %s->%s", ctClass.getName(), ctMethod.getName());
        return byteCode;
    }

}
