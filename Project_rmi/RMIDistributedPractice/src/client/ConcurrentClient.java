package client;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import remote.CalculatorService;
import java.rmi.Naming;

public class ConcurrentClient extends AbstractJavaSamplerClient {

    private CalculatorService service;

    @Override
    public void setupTest(JavaSamplerContext context) {
        try {
            service = (CalculatorService) Naming.lookup("rmi://localhost:1099/CalculatorService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();
        result.setSampleLabel("RMI测试");

        try {
            result.sampleStart();
            int sum = service.add(10, 20);
            result.sampleEnd();

            result.setSuccessful(true);
            result.setResponseCode("200");
            result.setResponseData(("结果：" + sum).getBytes());
        } catch (Exception e) {
            result.sampleEnd();
            result.setSuccessful(false);
        }
        return result;
    }

    @Override
    public Arguments getDefaultParameters() {
        return new Arguments();
    }
}