package remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// 继承UnicastRemoteObject，构造方法需抛出RemoteException
public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {

    // ✅ 把 protected 改成 public！开放跨包访问权限
    public HelloServiceImpl() throws RemoteException {
        super(); // 调用父类构造方法，导出远程对象（绑定端口）
    }

    @Override
    public String sayHello(String name) throws RemoteException, InvalidNameException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidNameException("Name cannot be empty!");
        }
        System.out.println("服务端收到请求：name=" + name);
        return "Hello, " + name + "! [From RMI Server]";
    }
}