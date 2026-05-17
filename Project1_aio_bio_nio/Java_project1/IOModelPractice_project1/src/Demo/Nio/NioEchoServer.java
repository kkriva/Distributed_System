//package Demo.Nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioEchoServer {
    public static int DEFAULT_PORT = 8889;

    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (RuntimeException ex) {
            port = DEFAULT_PORT;
        }

        ServerSocketChannel serverChannel;
        Selector selector;

        try {
            // 1. 打开服务端通道
            serverChannel = ServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(port);
            serverChannel.bind(address);
            // 非阻塞模式
            serverChannel.configureBlocking(false);

            // 2. 打开选择器
            selector = Selector.open();
            // 注册 接受连接 事件
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("NioEchoServer 已启动，端口：" + port);

        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        // 循环处理事件
        while (true) {
            try {
                // 阻塞等待事件
                selector.select();
            } catch (IOException e) {
                System.out.println("selector 异常：" + e.getMessage());
                e.printStackTrace();
                break;
            }

            // 获取就绪事件集合
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove(); // 必须移除，避免重复处理

                try {
                    // 1. 有客户端连接
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("客户端已连接：" + client);

                        client.configureBlocking(false);
                        // 注册读写事件
                        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    }

                    // 2. 通道可读（收到客户端消息）
                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();

                        int len = client.read(buffer);
                        if (len > 0) {
                            buffer.flip();
                            String msg = new String(buffer.array(), 0, len);
                            System.out.println(client.getRemoteAddress() + " 发来消息：" + msg);

                            // 切换为写事件，准备回显
                            key.interestOps(SelectionKey.OP_WRITE);
                        } else {
                            // 客户端断开
                            client.close();
                            key.cancel();
                        }
                    }

                    // 3. 通道可写（回显消息给客户端）
                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();

                        client.write(buffer);
                        buffer.clear();

                        // 写完切换回读
                        key.interestOps(SelectionKey.OP_READ);
                    }

                } catch (IOException ex) {
                    // 异常就关闭连接
                    try {
                        key.channel().close();
                    } catch (IOException ignored) {}
                    key.cancel();
                }
            }
        }
    }
}