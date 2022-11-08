package org.example.chap10.classLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClassLoaderTest {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new ClassLoaderFrame();
            frame.setTitle("ClassLoaderTest");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}

class ClassLoaderFrame extends JFrame {
    private JTextField keyField = new JTextField("3",4);
    private JTextField nameField = new JTextField("Calculator",30);
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 200;

    public ClassLoaderFrame() {
        setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
        setLayout(new GridBagLayout());
        add(new JLabel("Class"),new GBC(0,0).setAnchor(GBC.EAST));
        add(nameField,new GBC(1,0).setWeight(100,0).setAnchor(GBC.WEST));
        add(new JLabel("Key"),new GBC(0,1).setAnchor(GBC.EAST));
        add(keyField,new GBC(1,1).setWeight(100,0).setAnchor(GBC.WEST));
        JButton loadButton = new JButton("Load");
        add(loadButton,new GBC(0,2,2,1));
        loadButton.addActionListener(event -> runClass(nameField.getText(),keyField.getText()));
        pack();
    }

    public void runClass(String name,String key) {
        try {
            ClassLoader loader = new CryptoClassLoader(Integer.parseInt(key));
            Class<?> c = loader.loadClass(name);
            Method m = c.getMethod("main",String[].class);
            m.invoke(null,(Object) new String[] {});
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(this,t);
        }
    }
}

class CryptoClassLoader extends ClassLoader {
    private int key;

    public CryptoClassLoader(int key) {
        this.key = key;
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] classBytes = null;
            classBytes = loadClassBytes(name);
            Class<?> cl = defineClass(name,classBytes,0,classBytes.length);
            if (cl == null) {
                throw new ClassNotFoundException(name);
            }
            return cl;
        } catch (IOException e) {
            throw new ClassNotFoundException(name);
        }
    }

    private byte[] loadClassBytes(String name) throws IOException {
        name = "src.main.java.org.example.chap10." + name;
        String cname = name.replace('.','/') + ".caesar";
        byte[] bytes = Files.readAllBytes(Paths.get(cname));
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (bytes[i] - key);
        }
        return bytes;
    }
}