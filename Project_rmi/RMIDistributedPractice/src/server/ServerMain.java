package server;

import remote.CalculatorService;
import remote.CalculatorServiceImpl;
import remote.HelloService;
import remote.HelloServiceImpl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) {
        try {
            // 1. 启动 RMI 注册表
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI 注册表启动成功，端口：1099");

            // 2. 创建两个远程服务对象
            HelloService helloService = new HelloServiceImpl();
            CalculatorService calculatorService = new CalculatorServiceImpl();

            // 3. 绑定两个服务
            String helloURL = "rmi://localhost:1099/HelloService";
            String calcURL = "rmi://localhost:1099/CalculatorService";

            Naming.rebind(helloURL, helloService);
            Naming.rebind(calcURL, calculatorService);

            System.out.println("HelloService 绑定成功：" + helloURL);
            System.out.println("CalculatorService 绑定成功：" + calcURL);
            System.out.println("=====================================");

            // 4. 输入 unbind 注销服务
            Scanner scanner = new Scanner(System.in);
            System.out.println("输入 'unbind' 注销所有远程对象...");

            if (scanner.next().equals("unbind")) {
                Naming.unbind(helloURL);
                Naming.unbind(calcURL);
                System.out.println("所有服务已注销");
                System.exit(0);
            }
            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}