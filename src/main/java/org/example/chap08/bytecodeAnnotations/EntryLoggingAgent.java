package org.example.chap08.bytecodeAnnotations;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;

import java.lang.instrument.Instrumentation;

public class EntryLoggingAgent {
    public static void premain(final String arg, Instrumentation instr) {
        instr.addTransformer(((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (!className.replace("/",".").equals(arg)) {
                return null;
            }
            ClassReader reader = new ClassReader(classfileBuffer);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            EntryLogger el = new EntryLogger(writer,className);
            reader.accept(el,ClassReader.EXPAND_FRAMES);
            return writer.toByteArray();
        }));
    }
}
