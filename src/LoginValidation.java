import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

class LoginValidation extends Thread{
    Socket socket;
    String nickname;
    OutputStream out;

    LoginValidation(Socket socket, String nickname, OutputStream out) {
        this.socket = socket;
        this.nickname = nickname;
        this.out = out;
        System.out.println("로그인 검증 시작!!");
    }

    @Override
    public void run() {
        while (true) {
            try {
                out.write((Protocol.LOGINREQUEST + "|" + nickname + "\n").getBytes());
                out.flush();
            } catch (Exception e) {
                try {
                    System.out.println("LoginValidation에서 문제 발생!!");
                    e.printStackTrace();
                    socket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }

            }
        }
    }
}
