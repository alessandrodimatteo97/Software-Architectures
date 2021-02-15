import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Random;


public class Main {

    public static int gen(){
        int check = new Random().nextInt(10);
        if (check==9) return 0;
        return 1;

        // rseturn r;
    }
    public static void main(String[] args) throws InterruptedException {
        String ip;
        if(args.length == 0) ip="mosquitto";
        else {ip = args[0];}
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
            String content      = String.valueOf(gen());

            System.out.println("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            message.setQos(qos);
            //sampleClient.publish("Messages/"+clientId+"/timestamp", timestampMessage);
            System.out.println("Message published");
            sampleClient.disconnect();
            sampleClient.close();
            System.out.println("Finished");
            } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        }
     catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}