package com.debug;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    public String className = "";
    public String methodName = "";
    public String methodImpl = "";
    public String methodImplFile = "";

    Config(String agentArgs) throws RuntimeException {
        if (agentArgs != null) {
            Agent.log("Parsing hook options from agentArgs: %s", agentArgs);
            for (String arg: agentArgs.split(";")) {
                String[] parts = arg.split("=");
                switch (parts[0]) {
                    case "className":
                        this.className = parts[1];
                        break;
                    case "methodName":
                        this.methodName = parts[1];
                        break;
                    case "methodImpl":
                        this.methodImpl = parts[1];
                        break;
                    case "methodImplFile":
                        this.methodImplFile = parts[1];
                        break;
                    default:
                        break;
                }
            }
        } else {
            Agent.log("Parsing hook options from properties.");
            this.className = System.getProperty("hook.className", "");
            this.methodName = System.getProperty("hook.methodName", "");
            this.methodImpl = System.getProperty("hook.methodImpl", "");
            this.methodImplFile = System.getProperty("hook.methodImplFile", "");
        }

        if (isEmpty(methodImpl) && !isEmpty(methodImplFile)) {
            try {
                Agent.log("Reading methodImpl from %s", methodImplFile);
                this.methodImpl = Files.readString(Path.of(this.methodImplFile));
            } catch (IOException e) {
                Agent.log("Error reading %s: %s", this.methodImplFile, e);
            }
        }
        Agent.log("Loaded %s->%s: %s", this.className, this.methodName, this.methodImpl);
        if (isEmpty(className) || isEmpty(methodName) || isEmpty(methodImpl)) {
            throw new RuntimeException("Invalid arguments");
        }
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
