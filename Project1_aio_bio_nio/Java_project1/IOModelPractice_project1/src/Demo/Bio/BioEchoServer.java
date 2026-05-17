//package Demo.Bio;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BioEchoServer {
    public static int DEFAULT_PORT = 8888;

    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (RuntimeException ex) {
            port = DEFAULT_PORT;
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("BlockingEchoServer已启动，端口:" + port);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("接受客户端连接: " + clientSocket.getRemoteSocketAddress());

                    Thread handler = new Thread(() -> {
                        try (InputStream in = clientSocket.getInputStream();
                             OutputStream out = clientSocket.getOutputStream()) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = in.read(buffer)) != -1) {
                                out.write(buffer, 0, len);
                                out.flush();
                                String msg = new String(buffer, 0, len);
                                System.out.println("BlockingEchoServer->" + clientSocket.getRemoteSocketAddress() + ":" + msg);
                            }
                        } catch (IOException e) {
                            System.out.println("处理客户端异常: " + e.getMessage());
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }, "bio-echo-handler");
                    handler.start();

                } catch (IOException e) {
                    System.out.println("Accept 异常: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("BlockingEchoServer启动异常, 端口 " + port);
            System.out.println(e.getMessage());
        }
    }
}
