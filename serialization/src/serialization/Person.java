/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serialization;

/**
 *
 * @author ark
 */
public class Person {

    private String firstName;
    private String lastName;
    private int age;

    public Person() {
        firstName = lastName = "";
        age = 0;
    }

    public Person(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
