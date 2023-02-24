import java.io.*;
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
    }
    @Override
    public void run() {
        while (in != null) {
            try {
                String data = in.readLine();
                System.out.println(data);
                StringTokenizer st = new StringTokenizer(data, "|");
                int protocol = Integer.parseInt(st.nextToken());

                switch (protocol) {
                    case Protocol.LOGINACCEPT:
                    case Protocol.CONNECTION:
                        String loginMessage = st.nextToken();
                        System.out.println(loginMessage);
                        break;
                    case Protocol.MESSAGE:
                        String messageNickname = st.nextToken();
                        String message = st.nextToken();
                        System.out.println(messageNickname + ": " + message);
                        break;
                    case Protocol.FILE:
                        String filename = st.nextToken();
                        String fileContent = st.nextToken();
                        fileWrite(filename, fileContent);
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
    synchronized void fileWrite(String filename, String fileContent) {
        try {
            File file = new File(filePath + File.separator + filename);
            FileOutputStream fos = new FileOutputStream(file,true);

            fos.write(Integer.parseInt(fileContent));

        } catch (Exception e) {
            System.out.println("ClientReceiver의 fileWrite에서 문제 발생!!");
        }



    }
}
