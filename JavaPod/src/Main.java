import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Main {

    public static int gen(){
        int check = new Random().nextInt(10);
        if (check==9) return 0;
        return 1;

       // rseturn r;
    }
    public static void main(String[] args) throws InterruptedException {
        TimeUnit.SECONDS.sleep(20);
        String ip;
        if(args.length == 0) ip="mosquitto";
    	else {ip = args[0];}
        int timestamp = 0;
        int qos             = 2;
        String broker       = "tcp://"+ip+":1883";
        String clientId     = String.valueOf(new Random().nextInt(10000));
        MemoryPersistence persistence = new MemoryPersistence();
        String topic        = "Messages/"+clientId;
        System.out.println("clientId: "+clientId);

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            while(true) {
                System.out.println("Timestamp: "+timestamp++);
                String content      = String.valueOf(gen());

                System.out.println("Publishing message: " + content);
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);
                sampleClient.publish(topic, message);
                MqttMessage timestampMessage = new MqttMessage(String.valueOf(timestamp).getBytes());
                message.setQos(qos);
              //sampleClient.publish("Messages/"+clientId+"/timestamp", timestampMessage);
                System.out.println("Message published");
                TimeUnit.SECONDS.sleep(5);
            }

        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}