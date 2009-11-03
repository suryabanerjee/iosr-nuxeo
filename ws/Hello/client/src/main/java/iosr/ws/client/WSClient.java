package iosr.ws.client;

import org.example.contract.hello.HelloPortType;
import org.example.contract.hello.HelloService;

public class WSClient {
	public static void main (String[] args) {
        HelloService service = new HelloService();
        HelloPortType port = service.getHelloPort();           

        sayHello(port, "Tomek");
        sayHello(port, "Mary");
        sayHello(port, "Bartek");
    } 
    
    public static void sayHello(HelloPortType port, 
            String nameToHello) {
        String resp = port.sayHello(nameToHello);
        System.out.println("The response for " + nameToHello + " is: " 
            + resp);
    }
}
