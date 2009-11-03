package iosr.ws.service;

import javax.jws.WebService;
import org.example.contract.hello.HelloPortType;

@WebService(targetNamespace = "http://www.example.org/contract/Hello", 
        portName="HelloPort",
        serviceName="HelloService", 
        endpointInterface="org.example.contract.hello.HelloPortType")
        
public class HelloPortTypeImpl implements HelloPortType{
	
	@Override
	public String sayHello(String nameToHello) {
		return "Hello " + nameToHello + " !";
	}
	
}
