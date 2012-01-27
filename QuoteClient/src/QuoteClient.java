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

import java.beans.XMLDecoder;
import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.List;
import serialization.Person;

public class QuoteClient {

    protected static String message = "I have a dream";

    public static void main(String[] args) throws IOException {
        // get host name from args[0], or connect to local host if host name is not specified.
        String hostname = null;
        if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            System.out.println("Hostname is not specified in args[0], so connect to local host instead.");
            InetAddress netAddress = getInetAddress();
            System.out.println("local host ip: " + getHostIp(netAddress));
            System.out.println("local host name: " + getHostName(netAddress));
            hostname = getHostName(netAddress);
        } else {
            hostname = args[0];
            System.out.println("Connect to " + hostname);
        }

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        // send request
        byte[] buf = new byte[256];
        buf = message.getBytes();
        InetAddress address = InetAddress.getByName(hostname);

        System.out.printf("\nSend to address: %s\n", address.toString());

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);
        System.out.println("Sent: " + message);

        // get response
        buf = new byte[256]; // a new byte[256] is required for receiving the datagram
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Received: " + received);

        ////////////////////////////////////////////////////////////////
        // The code below is for XML serialization
        ////////////////////////////////////////////////////////////////

        // get the buffer size first
        buf = new byte[256]; // a new byte[256] is required for receiving the datagram
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        received = new String(packet.getData(), 0, packet.getLength());
        System.out.printf("The buffer size is %s. Get data from the server...\n", received);

        // get the buffer itself
        int size = Integer.parseInt(received);
        buf = new byte[size];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        System.out.println("Deserialize from XML");

        // deserialize from XML
        ByteArrayInputStream is = new ByteArrayInputStream(buf);
        XMLDecoder decoder = new XMLDecoder(is);
        List<Person> list = (List<Person>) decoder.readObject();
        decoder.close();
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            Person p = (Person) itr.next();
            System.out.printf("%s %s is %d years old.\n", p.getFirstName(), p.getLastName(), p.getAge());
        }

        socket.close();
    }

    public static InetAddress getInetAddress() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("unknown host!");
        }
        return null;
    }

    public static String getHostIp(InetAddress netAddress) {
        if (null == netAddress) {
            return null;
        }
        String ip = netAddress.getHostAddress(); //get the ip address
        return ip;
    }

    public static String getHostName(InetAddress netAddress) {
        if (null == netAddress) {
            return null;
        }
        String name = netAddress.getHostName(); //get the host address
        return name;
    }
}
