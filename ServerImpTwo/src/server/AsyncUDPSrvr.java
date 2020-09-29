package server;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.Iterator;

public class AsyncUDPSrvr {
    static int BUF_SZ = 1024 * 8;

    class Con {
        ByteBuffer req;
        ByteBuffer resp;
        SocketAddress sa;

        public Con() {
            req = ByteBuffer.allocate(BUF_SZ);
        }
    }

    static int PORT = 55399;

    public void process() {
        try {

            Selector selector = Selector.open();
            DatagramChannel channel = DatagramChannel.open();
            InetAddress hostIP = InetAddress.getLocalHost();
            InetSocketAddress socketAddress;
            socketAddress = new InetSocketAddress(hostIP, PORT);
            channel.socket().bind(socketAddress);
            channel.configureBlocking(false);
            SelectionKey clientKey = channel.register(selector, SelectionKey.OP_READ);
            clientKey.attach(new Con());
            while (true) {
                try {
                    selector.select();
                    Iterator selectedKeys = selector.selectedKeys().iterator();
                    while (selectedKeys.hasNext()) {
                        try {
                            SelectionKey key = (SelectionKey) selectedKeys.next();
                            selectedKeys.remove();
                            if (!key.isValid())
                                continue;
                            if (key.isReadable()) {
                                read(key);
                                key.interestOps(SelectionKey.OP_WRITE);
                            } else if (key.isWritable()) {
                                write(key);
                                key.interestOps(SelectionKey.OP_READ);
                            }

                        } catch (IOException e) {
                            System.err.println("loop while 1 (selectedKeys.hashNext()) error " + (e.getMessage()) != null ? e.getLocalizedMessage() : "unrecognized error detected");
                        }
                    }
                } catch (IOException e) {
                    System.err.println("loop while 0 (true) error " + (e.getMessage()) != null ? e.getLocalizedMessage() : "unrecognized error detected");
                }
            }
        } catch (IOException e) {
            System.err.println("network error " + (e.getMessage()) != null ? e.getLocalizedMessage() : "unrecognized error detected");
        }
    }

    private void read(SelectionKey key) throws IOException {
        DatagramChannel chan = (DatagramChannel) key.channel();
        Con con = (Con) key.attachment();
        con.req.clear();
        con.sa = chan.receive(con.req);
        System.out.println(new String(con.req.array(), "UTF-8"));
        con.resp = Charset.forName("UTF-8").newEncoder().encode(CharBuffer.wrap("send the same string"));
    }

    private void write(SelectionKey key)throws IOException {
        DatagramChannel chan = (DatagramChannel) key.channel();
        Con con = (Con) key.attachment();
        int send = chan.send(con.resp, con.sa);
    }
}