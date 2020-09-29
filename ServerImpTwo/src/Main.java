import server.AsyncUDPSrvr;

public class Main {
    public static void main(String[] argc){
        AsyncUDPSrvr svr = new AsyncUDPSrvr();
        svr.process();
    }
}

