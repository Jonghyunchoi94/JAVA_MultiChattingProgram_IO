import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChattingClient {
    public static void main(String[] args) {
        ChattingClient chattingClient = new ChattingClient();
        chattingClient.startClient();
    }
    Socket socket;
    String filePath, nickname;
    BufferedReader in;
    OutputStream out;
    void startClient() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Scanner scanner = new Scanner(System.in);
                    while (true) {
                        System.out.print("파일을 받을 디렉토리를 입력해주세요: ");
                        filePath = scanner.nextLine();

                        File file = new File(filePath);
                        if (file.exists() && file.isDirectory()) {
                            System.out.println("성공적으로 디렉토리를 지정하였습니다.");
                            break;
                        }
                        System.out.println("해당 디렉토리 경로가 존재하지 않습니다.");
                    }
                    socket = new Socket();
                    System.out.println("[연결 요청]");
                    socket.connect(new InetSocketAddress("localhost", 5001));
                    System.out.println("[연결 성공]");

                    System.out.print("사용하실 닉네임을 입력해주세요: ");
                    nickname = scanner.nextLine();

                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = socket.getOutputStream();

                    Thread loginValidation = new LoginValidation(socket, nickname, out);
                    loginValidation.start();

                    Thread clientReceiver = new ClientReceiver(socket, nickname, filePath, in);
                    Thread clientSender = new ClientSender(socket, nickname, out);

                    clientReceiver.start();
                    clientSender.start();



                } catch (Exception e) {
                    System.out.println("ChattingClient의 startClient에서 문제 발생!!");
                    if (!socket.isClosed()) {
                        stopClient();
                    }
                }
            }
        };
        thread.start();
    }

    void stopClient() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("ChattingClient의 stopClient에서 문제 발생!!");
            e.printStackTrace();
        }
    }

}
