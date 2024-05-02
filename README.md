# HookAgent

A simple Java Agent template with javassist support.

Command line example:
```shell
# pass hook arguments via agentArgs.
java -Xmx16G --add-opens java.base/java.lang=ALL-UNNAMED -Xverify:none \
     -javaagent:HookAgent.jar=className=com.example.Foo;methodName=bar;methodImplFile=hook.js \
     target.jar
# or pass hook arguments via Properties.
java -Xmx16G --add-opens java.base/java.lang=ALL-UNNAMED -Xverify:none \
     -javaagent:HookAgent.jar \
     -Dhook.className=com.example.Foo \
     -Dhook.methodName=bar \
     -Dhook.methodImplFile=hook.js \
     target.jar
```

The `hook.js` example:
```js
{
    System.out.println("[agent-hook] skip check: " + new java.util.Date());
    return 0;
}
```