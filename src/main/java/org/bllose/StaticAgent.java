package org.bllose;

import java.lang.instrument.Instrumentation;

public class StaticAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new MyClassTransformer());
    }
}
