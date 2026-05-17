//package Demo.Aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class AioEchoServer {
    public static final int DEFAULT_PORT = 8890;

    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (RuntimeException ex) {
            port = DEFAULT_PORT;
        }

        AsynchronousServerSocketChannel serverChannel;
        try {
            serverChannel = AsynchronousServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(port);
            serverChannel.bind(address);
            serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, 4 * 1024);
            serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            System.out.println("AioEchoServer已启动，端口：" + port);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        serverChannel.accept(null, new AcceptCompletionHandler(serverChannel));

        // 保持主线程存活，异步操作由 CompletionHandler 处理
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignored) {
        }
    }

    private static class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {
        private final AsynchronousServerSocketChannel serverChannel;

        AcceptCompletionHandler(AsynchronousServerSocketChannel serverChannel) {
            this.serverChannel = serverChannel;
        }

        @Override
        public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
            System.out.println("AioEchoServer接受客户端的连接：" + clientChannel);
            // 继续接收下一个连接
            serverChannel.accept(null, this);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            clientChannel.read(buffer, buffer, new ReadCompletionHandler(clientChannel));
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            System.err.println("Accept 异常：" + exc.getMessage());
            exc.printStackTrace();
        }
    }

    private static class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousSocketChannel clientChannel;

        ReadCompletionHandler(AsynchronousSocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }

        @Override
        public void completed(Integer bytesRead, ByteBuffer buffer) {
            if (bytesRead == -1) {
                closeChannel();
                return;
            }
            if (bytesRead > 0) {
                buffer.flip();
                byte[] bytes = new byte[buffer.limit()];
                buffer.get(bytes);
                String message = new String(bytes, StandardCharsets.UTF_8);
                System.out.println("AioEchoServer 接收到：" + message);

                ByteBuffer writeBuffer = ByteBuffer.wrap(bytes);
                clientChannel.write(writeBuffer, writeBuffer, new WriteCompletionHandler(clientChannel));
            } else {
                buffer.clear();
                clientChannel.read(buffer, buffer, this);
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            System.err.println("Read 异常：" + exc.getMessage());
            exc.printStackTrace();
            closeChannel();
        }

        private void closeChannel() {
            try {
                clientChannel.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousSocketChannel clientChannel;

        WriteCompletionHandler(AsynchronousSocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }

        @Override
        public void completed(Integer bytesWritten, ByteBuffer buffer) {
            if (buffer.hasRemaining()) {
                clientChannel.write(buffer, buffer, this);
                return;
            }
            buffer.clear();
            clientChannel.read(buffer, buffer, new ReadCompletionHandler(clientChannel));
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            System.err.println("Write 异常：" + exc.getMessage());
            exc.printStackTrace();
            try {
                clientChannel.close();
            } catch (IOException ignored) {
            }
        }
    }
}
