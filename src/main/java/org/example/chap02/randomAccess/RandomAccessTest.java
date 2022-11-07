package org.example.chap02.randomAccess;

/**
 * 随机读取文件
 */

import java.io.*;
import java.time.LocalDate;
import java.util.Locale;

public class RandomAccessTest {
    public static void main(String[] args) {
        Employee[] staff = new Employee[3];

        staff[0] = new Employee("Carl Cracker",75000,1987,12,15);
        staff[1] = new Employee("Harry Hacker",50000,1989,10,1);
        staff[2] = new Employee("Tony Tester",40000,1990,3,15);

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream("src/main/java/org/example/chap02/randomAccess/employee.dat"))) {
            for (Employee e : staff) {
                writeData(out,e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (RandomAccessFile in = new RandomAccessFile("src/main/java/org/example/chap02/randomAccess/employee.dat","r")) {
            int n = (int)(in.length() / Employee.RECORD_SIZE);
            Employee[] newStaff = new Employee[n];

            for (int i = n - 1; i >= 0; i--) {
                newStaff[i] = new Employee();
                in.seek(i * Employee.RECORD_SIZE);
                newStaff[i] = readData(in);
            }

            for (Employee e : newStaff) {
                System.out.println(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeData(DataOutputStream out,Employee e) throws IOException {
        DataIO.writeFixedString(e.getName(),Employee.NAME_SIZE,out);
        out.writeDouble(e.getSalary());

        LocalDate hireDay = e.getHireDay();
        out.writeInt(hireDay.getYear());
        out.writeInt(hireDay.getMonthValue());
        out.writeInt(hireDay.getDayOfMonth());
    }

    public static Employee  readData(DataInput in) throws IOException {
        String name = DataIO.readFixedString(Employee.NAME_SIZE,in);
        double salary = in.readDouble();
        int y = in.readInt();
        int m = in.readInt();
        int d = in.readInt();
        return new Employee(name,salary,y,m - 1,d);
    }
}
