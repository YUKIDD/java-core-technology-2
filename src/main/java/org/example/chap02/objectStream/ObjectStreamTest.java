package org.example.chap02.objectStream;

/**
 * 对象的序列化
 */

import java.io.*;

public class ObjectStreamTest {
    public static void main(String[] args) throws FileNotFoundException {
        Employee harry = new Employee("Harry Hacker",50000,1980,10,1);
        Manager carl = new Manager("Carl Cracker",80000,1987,12,15);
        carl.setSecretary(harry);
        Manager tony = new Manager("Tony Tester",40000,1990,3,15);
        tony.setSecretary(harry);

        Employee[] staff = new Employee[3];

        staff[0] = carl;
        staff[1] = harry;
        staff[2] = tony;

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("src/main/java/org/example/chap02/objectStream/employee.dat"))){
            out.writeObject(staff);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("src/main/java/org/example/chap02/objectStream/employee.dat"))) {
            Employee[] newStaff = (Employee[]) in.readObject();

            newStaff[1].raiseSalary(10);

            for (int i = 0; i < newStaff.length; i++) {
                System.out.println(newStaff[i]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
