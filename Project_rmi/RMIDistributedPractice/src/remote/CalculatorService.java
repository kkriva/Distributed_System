package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

// 远程接口：必须继承 Remote
public interface CalculatorService extends Remote {
    // 加法方法
    int add(int a, int b) throws RemoteException;
}