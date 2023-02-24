import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

class ClientSender extends Thread {
    Socket socket;
    OutputStream out;
    String nickname;

    ClientSender(Socket socket, String nickname, OutputStream out) {
        this.socket = socket;
        this.nickname = nickname;
        this.out = out;
    }

    @Override
    public void run() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            try {
                String message = scanner.nextLine();
                String[] messageSplit = message.split("\\.");
                String extension = messageSplit[messageSplit.length - 1].toLowerCase();

                if (FileExtension.EXTENSION.contains(extension)) {
                    File file = new File(message);

                    if (file.exists() && file.isFile()) {
                        String[] fileNameSplit = message.split("/");
                        String fileName = fileNameSplit[fileNameSplit.length - 1];
                        fileRead(fileName, file);
                    }
                    else {
                        out.write((Protocol.MESSAGE + "|" + nickname + "|" + message + "\n").getBytes());
                    }

                } else {
                    out.write((Protocol.MESSAGE + "|" + nickname + "|" + message + "\n").getBytes());
                }

            } catch (Exception e) {
                try {
                    System.out.println("ClientSender에서 문제 발생!!");
                    e.printStackTrace();
                    socket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }

            }
        }
    }

    synchronized void fileRead(String fileName, File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            int data;
            while ((data = fis.read()) != -1) {
                out.write((Protocol.FILE + "|" + fileName + "|" + data + "\n").getBytes());
            }
            out.flush();

        } catch (Exception e) {
            System.out.println("ClientSender의 fileRead에서 문제 발생!!");
        }

    }
}
