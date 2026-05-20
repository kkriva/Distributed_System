package remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CalculatorServiceImpl extends UnicastRemoteObject implements CalculatorService {

    // public 构造方法，解决跨包权限问题
    public CalculatorServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public int add(int a, int b) throws RemoteException {
        System.out.println("服务端计算：" + a + " + " + b + " = " + (a + b));
        return a + b;
    }
}