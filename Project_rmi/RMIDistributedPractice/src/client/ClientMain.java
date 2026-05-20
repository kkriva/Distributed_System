package client;

import remote.CalculatorService;
import remote.HelloService;
import remote.InvalidNameException;

import java.rmi.Naming;

public class ClientMain {
    public static void main(String[] args) {
        try {
            // 1. 获取远程服务
            HelloService helloService = (HelloService) Naming.lookup("rmi://localhost:1099/HelloService");
            CalculatorService calcService = (CalculatorService) Naming.lookup("rmi://localhost:1099/CalculatorService");

            // 2. 调用 Hello 服务
            try {
                String res = helloService.sayHello("小明");
                System.out.println("客户端收到：" + res);
            } catch (InvalidNameException e) {
                System.out.println("异常：" + e.getMessage());
            }

            // 3. 调用计算器服务
            int a = 10, b = 20;
            int sum = calcService.add(a, b);
            System.out.println(a + " + " + b + " = " + sum);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}