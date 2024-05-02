package com.debug;

import com.sun.source.doctree.SystemPropertyTree;

import java.lang.instrument.Instrumentation;

public class Agent {

    public static void log(String fmt, Object ...args) {
        System.out.printf("[agent] " + fmt + "\n", args);
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        Agent.log("enter premain, agentArgs=%s", agentArgs);
        if (!inst.isRetransformClassesSupported()) {
            Agent.log("Class retransformation is not supported.");
            return;
        }

        Config conf = new Config(agentArgs);
        HookTransformer.replace(inst, conf.className, conf.methodName, conf.methodImpl);
    }

}
