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
                        connections.add(client);

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
        String nickname, message;
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
                        while (true) {
                            String data = in.readLine();
                            System.out.println(data);
                            StringTokenizer st = new StringTokenizer(data, "|");
                            int protocol = Integer.parseInt(st.nextToken());

                            switch (protocol) {
                                case Protocol.LOGINREQUEST:
                                    nickname = st.nextToken();
                                    sendTo(Protocol.LOGINACCEPT + "|" + "닉네임이 성공적으로 생성되었습니다!!");
                                    sendAll(Protocol.CONNECTION + "|" + nickname + "님이 입장하셨습니다.");
                                    sendAll(Protocol.CONNECTION + "|" + "현재 인원은 " + connections.size() + "명입니다.");
                                    break;
                                case Protocol.MESSAGE:
                                    message = st.nextToken();
                                    sendAll(Protocol.MESSAGE + "|" +  message);
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
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        out.write((message + "\n").getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ChattingServer의 Client의 sendTo에서 문제 발생!!");
                    }
                }
            };
            executorService.submit(runnable);
        }

        void sendAll(String message) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        for (Client client: connections) {
                            client.sendTo(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ChattingServer의 Client의 sendAll에서 문제 발생!!");
                    }
                }
            };
            executorService.submit(runnable);
        }
    }


}
