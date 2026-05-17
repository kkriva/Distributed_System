//package Demo.Bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class BioEchoClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8888;
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(host, port);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            String message = "Hello BIO!";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            out.write(data);
            out.flush();

            byte[] buffer = new byte[1024];
            int len = in.read(buffer);
            if (len == -1) {
                System.out.println("服务器关闭连接，未收到响应");
            } else {
                String response = new String(buffer, 0, len, StandardCharsets.UTF_8);
                System.out.println("从服务器收到响应: " + response);
            }

        } catch (IOException e) {
            System.err.println("客户端异常: " + e.getMessage());
        }
    }
}