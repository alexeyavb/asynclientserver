package AsyncClient;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ClientImpl {
    private static final int BUFFER_SIZE = 1024 * 8;
    private static String[] messages =
    {
        "The best way to predict the future is to create it.",
        "As you think, so shall you become.",
        "The noblest pleasure is the joy of understanding.",
        "Courage is grace under pressure.",
        "*exit*"
    };
    public void run() {
        logger("Starting MySelectorClientExample...");
        try {
            int port = 55399;
//            InetAddress hostIP = InetAddress.getLocalHost();
//            InetSocketAddress myAddress;
//            myAddress = new InetSocketAddress(hostIP, port);
//            SocketChannel myClient = SocketChannel.open(myAddress);

            Selector selector = Selector.open();
            DatagramChannel channel = DatagramChannel.open();
            InetAddress hostIP = InetAddress.getLocalHost();
            InetSocketAddress socketAddress;
            socketAddress = new InetSocketAddress(hostIP, port);
            //channel.socket().bind(socketAddress);
            channel.connect(socketAddress);
            channel.configureBlocking(false);
            logger(String.format("Trying to connect to %s:%d...",
                    socketAddress.getHostName(), socketAddress.getPort()));
            for (String msg: messages) {
                ByteBuffer myBuffer=ByteBuffer.allocate(BUFFER_SIZE);
                myBuffer.clear();
                myBuffer.put(msg.getBytes());
                myBuffer.flip();
                int bytesWritten = channel.send(myBuffer,socketAddress);
                logger(String
                                .format("Sending Message...: %s\nbytesWritten...: %d",
                                        msg, bytesWritten));
            }
            logger("Closing Client connection...");
            channel.close();
        } catch (IOException e) {
            logger(e.getMessage());
            e.printStackTrace();
        }
    }
    private void logger(String msg) {
        System.out.println(msg);
    }
}
