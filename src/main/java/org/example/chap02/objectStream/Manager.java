package org.example.chap02.objectStream;



public class Manager extends Employee {
    private Employee secretary;

    public Manager(String n, double s, int year, int month, int day) {
        super(n, s, year, month, day);
        this.secretary = null;
    }

    public void setSecretary(Employee secretary) {
        this.secretary = secretary;
    }

    @Override
    public String toString() {
        return super.toString() + " {" +
                "secretary=" + secretary +
                '}';
    }
}
