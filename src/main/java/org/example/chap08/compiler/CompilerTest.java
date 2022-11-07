package org.example.chap08.compiler;

import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.tools.*;
import javax.tools.JavaFileObject.*;

public class CompilerTest {
    public static void main(String[] args) throws ClassNotFoundException, IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        ArrayList<ByteArrayClass> classFileObjects = new ArrayList<>();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        JavaFileManager fileManager = compiler.getStandardFileManager(diagnostics,null,null);
        fileManager = new ForwardingJavaFileManager<JavaFileManager>(fileManager){
            public JavaFileObject getJavaFileForOutput(Location location,
                                                       String className,Kind kind,FileObject sibling) throws IOException {
                if (kind == Kind.CLASS) {
                    ByteArrayClass fileObject = new ByteArrayClass(className);
                    classFileObjects.add(fileObject);
                    return fileObject;
                } else {
                    return super.getJavaFileForOutput(location,className,kind,sibling);
                }
            }
        };

        String frameClassName = args.length == 0 ? "src.main.java.org.example.chap08.buttons2.ButtonFrame" : args[0];

        StandardJavaFileManager fileManager2 = compiler.getStandardFileManager(null,null,null);
        ArrayList<JavaFileObject> sources = new ArrayList<JavaFileObject>();
        for (JavaFileObject o : fileManager2.getJavaFileObjectsFromStrings(
                List.of(frameClassName.replace(".","/") + ".java")
        )) {
            sources.add(o);
        }

        JavaFileObject source = buildSource(frameClassName);
        JavaCompiler.CompilationTask task = compiler.getTask(null,fileManager,diagnostics,null,null,List.of(source));
        Boolean result = task.call();

        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()){
            System.out.println(d.getKind() + ": " + d.getMessage(null));
        }
        fileManager.close();
        if (!result) {
            System.out.println("Compilation failed.");
            System.exit(1);
        }

        ByteArrayClassLoader loader = new ByteArrayClassLoader(classFileObjects);
        JFrame frame = (JFrame) loader.loadClass("x.Frame").getConstructor().newInstance();

        EventQueue.invokeLater(() -> {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("CompilerTest");
            frame.setVisible(true);
        });
    }

    static JavaFileObject buildSource(String superclassName) throws IOException,ClassNotFoundException {
        StringBuilder builder = new StringBuilder();
        builder.append("package x;\n\n");
        builder.append("public class Frame extends " + superclassName + " {\n");
        builder.append("protected void addEventHandlers() {\n");
        Properties props = new Properties();
        props.load(Files.newInputStream(Paths.get(superclassName.replace(".","/")).getParent().resolve("action.properties")));
        for (Map.Entry<Object,Object> e : props.entrySet()){
            String beanName = (String) e.getKey();
            String eventCode = (String) e.getValue();
            builder.append(beanName + ".addActionListener(event -> {\n");
            builder.append(eventCode);
            builder.append(";\n} );\n");
        }
        builder.append("} }\n");
        return new StringSource("x.Frame",builder.toString());
    }
}
