import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

class ClientReceiver extends Thread {
    Socket socket;
    String nickname, filePath;
    BufferedReader in;
    ClientReceiver(Socket socket, String nickname, String filePath, BufferedReader in) {
        this.socket = socket;
        this.nickname = nickname;
        this.filePath = filePath;
        this.in = in;
        System.out.println("받는 중");
    }
    @Override
    public void run() {
        while (true) {
            try {
                String data = in.readLine();
                StringTokenizer st = new StringTokenizer(data, "|");
                int protocol = Integer.parseInt(st.nextToken());
                System.out.println(protocol);

                switch (protocol) {
                    case Protocol.LOGINACCEPT:
                    case Protocol.CONNECTION:
                    case Protocol.MESSAGE:
                        String message = st.nextToken();
                        System.out.println(message);
                        break;
                }

            } catch (Exception e) {
                try {
                    System.out.println("ClientReceiver에서 문제 발생!!");
                    e.printStackTrace();
                    socket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
}