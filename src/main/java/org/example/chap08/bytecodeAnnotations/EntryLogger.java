package org.example.chap08.bytecodeAnnotations;

import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EntryLogger extends ClassVisitor {

    private String className;

    public EntryLogger(ClassWriter writer, String className) {
        super(Opcodes.ASM5,writer);
        this.className = className;
    }
    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        MethodVisitor mv = cv.visitMethod(i,s,s1,s2,strings);
        return new AdviceAdapter(Opcodes.ASM5,mv,i,s,s1) {

            private String loggerName;

            @Override
            public AnnotationVisitor visitAnnotation(String s, boolean b) {
                return new AnnotationVisitor(Opcodes.ASM5) {
                    @Override
                    public void visit(String s1, Object o) {
                        if(s.equals("LbytecodeAnnotations/LogEntry;") && s1.equals("logger")) {
                            loggerName = o.toString();
                        }
                    }
                };
            }

            public void onMethodEnter() {
                if (loggerName != null) {
                    visitLdcInsn(loggerName);
                    visitMethodInsn(INVOKESTATIC,"java/util/logging/Logger","getLogger",
                            "(Ljava/lang/String;)Ljava/util/logging/Logger;",false);
                    visitLdcInsn(className);
                    visitLdcInsn(s);
                    visitMethodInsn(INVOKEVIRTUAL,"java/util/logging/Logger","entering",
                            "(Ljava/lang/String;Ljava/lang/String;)V",false);
                    loggerName = null;
                }
            }
        };
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("USAGE java bytecodeAnnotations.EntryLogger classfile");
            System.exit(1);
        }
        Path path = Paths.get(args[0]);
        ClassReader reader = new ClassReader(Files.newInputStream(path));
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        EntryLogger entryLogger = new EntryLogger(writer,path.toString().replace(".class","").replaceAll("[/\\\\]]","."));
        reader.accept(entryLogger,ClassReader.EXPAND_FRAMES);
        Files.write(Paths.get(args[0]),writer.toByteArray());
    }
}
