/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.beans.XMLEncoder;
import java.io.*;
import java.net.*;
import java.util.*;
import serialization.Person;

public class QuoteServerThread extends Thread {

    protected DatagramSocket socket = null;
    protected boolean moreQuotes = true;
    protected Random randomGenerator = new Random();

    public QuoteServerThread() throws IOException {
        this("QuoteServerThread");
    }

    public QuoteServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
    }

    public void run() {

        while (moreQuotes) {
            try {
                byte[] buf = new byte[256];

                System.out.println("================ The server is waiting for a client. ================");

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + received);

                // figure out response
                String response = getResponse(received);
                buf = response.getBytes();

                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                // print the packet's address and port
                System.out.printf("from address: %s\t port: %d\n", address.toString(), port);

                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
                System.out.println("Sent: " + response);

                ////////////////////////////////////////////////////////////////
                // The code below is for XML serialization
                ////////////////////////////////////////////////////////////////

                System.out.println("\nSerialize to XML");

                // serialize to XML
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                XMLEncoder encoder = new XMLEncoder(os);
                List<Person> list = new ArrayList<Person>();
                list.add(new Person("John", "Denver", 1997 - 1943));
                list.add(new Person("Bob", "Dylan", Calendar.getInstance().get(Calendar.YEAR) - 1941));
                encoder.writeObject(list);
                encoder.close();

                // print the XML text
                System.out.println(os.toString());

                // send the buffer size first
                int size = os.toByteArray().length;
                System.out.printf("The buffer size is %d. Send data to the %s:%d...\n", size, address.toString(), port);
                buf = Integer.toString(size).getBytes();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);

                // send the buffef itself
                buf = os.toByteArray();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);

                System.out.println("Job done. The data is sent.");
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
        }
        socket.close();
    }

    protected String getResponse(String message) {
        String response = null;
        if (randomGenerator.nextInt(2) == 0) {
            response = message + " and the server said YES at " + getTime();
        } else {
            response = message + " but the server said NO at " + getTime();
        }
        return response;
    }

    protected String getTime() {
        Calendar calendar = new GregorianCalendar();
        String am_pm;
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if (calendar.get(Calendar.AM_PM) == 0) {
            am_pm = "AM";
        } else {
            am_pm = "PM";
        }
        return String.format("%d:%d:%d %s", hour, minute, second, am_pm);
    }
}
