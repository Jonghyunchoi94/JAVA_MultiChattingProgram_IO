import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChattingServer {
    public static void main(String[] args) {
        ChattingServer chattingServer = new ChattingServer();
        chattingServer.startServer();
    }
    ExecutorService executorService;
    ServerSocket serverSocket;
    List<Client> connections = new Vector<Client>();
    List<String> nicknameStorage = new Vector<String>();
    void startServer() {
        executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );

        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("localhost", 5001));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ChattingServer의 startServer에서 문제 발생!!");
            if (!serverSocket.isClosed()) {
                stopServer();
            }
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("[연결 기다림]");
                        Socket socket = serverSocket.accept();
                        String message = "[연결 수락: " + socket.getRemoteSocketAddress() + ": " + Thread.currentThread().getName() + "]";
                        System.out.println(message);


                        Client client = new Client(socket);

                    } catch (Exception e) {
                        System.out.println("ChattingServer의 startServer에서 문제 발생!!");
                        if (!serverSocket.isClosed()) {
                            stopServer();
                        }
                    }
                }
            }
        };
        executorService.submit(runnable);
    }

    void stopServer() {

    }

    class Client {
        Socket socket;
        BufferedReader in;
        OutputStream out;
        Client(Socket socket) {
            this.socket = socket;

            receive();
        }

        void receive() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        out = socket.getOutputStream();
                        while (in != null) {
                            String data = in.readLine();
                            System.out.println(data);
                            StringTokenizer st = new StringTokenizer(data, "|");
                            int protocol = Integer.parseInt(st.nextToken());

                            switch (protocol) {
                                case Protocol.LOGINREQUEST:
                                    String loginNickname = st.nextToken();
                                    connections.add(Client.this);
                                    sendTo(Protocol.LOGINACCEPT + "|" + "닉네임이 성공적으로 생성되었습니다!!");
                                    sendAll(Protocol.CONNECTION + "|" + loginNickname + "님이 입장하셨습니다.");
                                    sendAll(Protocol.CONNECTION + "|" + "현재 인원은 " + connections.size() + "명입니다.");

                                    break;
                                case Protocol.MESSAGE:
                                    String messageNickname = st.nextToken();
                                    String message = st.nextToken();
                                    sendAll(Protocol.MESSAGE + "|" + messageNickname + "|" + message);
                                    break;
                                case Protocol.FILE:
                                    break;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ChattingServer의 Client의 receive에서 문제 발생!!");
                    }
                }
            };

            executorService.submit(runnable);
        }

        void sendTo(String message) {
            try {
                out.write((message + "\n").getBytes());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ChattingServer의 Client의 sendTo에서 문제 발생!!");
            }
        }

        void sendAll(String message) {
            try {
                for (Client client: connections) {
                    client.sendTo(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ChattingServer의 Client의 sendAll에서 문제 발생!!");
            }
        }
    }


}
