/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serialization;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ark
 */
public class Serialization {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // serialize to XML
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(os);
        List<Person> list = new ArrayList<Person>();
        list.add(new Person("John", "Denver", 1997 - 1943));
        list.add(new Person("Bob", "Dylan", Calendar.getInstance().get(Calendar.YEAR) - 1941));
        encoder.writeObject(list);
        encoder.close();

        // print messages
        System.out.println("\nSerialize to XML");
        System.out.println(os.toString());
        System.out.println("Deserialize from XML");

        // deserialize from XML
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        XMLDecoder decoder = new XMLDecoder(is);
        List<Person> list2 = (List<Person>) decoder.readObject();
        decoder.close();

        // print the list
        Iterator itr = list2.iterator();
        while (itr.hasNext()) {
            Person p = (Person) itr.next();
            System.out.printf("%s %s is %d years old.\n", p.getFirstName(), p.getLastName(), p.getAge());
        }
    }
}
