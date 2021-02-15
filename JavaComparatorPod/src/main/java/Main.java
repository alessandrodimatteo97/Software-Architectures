import java.io.*;

import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.auth.ApiKeyAuth;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.proto.V1Batch;
import io.kubernetes.client.util.ClientBuilder;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;

import java.io.IOException;
import java.util.*;

public class Main implements MqttCallback  {

	/** The client id. */
	private static final String clientId = "clientId";

	/** The topic. */
	private static final String topic = "#";
	
	private int value1 = 100;
	
	private int id1 = 0;
	
	private int id2 = 0;
	
	private int value2 = 100;
	
	private Boolean third = false;

	public static void main(String[] args){

		String ip;
        if(args.length == 0) ip="127.0.0.1";
    	else {ip = args[0];}
        
        String brokerUrl = "tcp://"+ip+":1883";

		System.out.println("Subscriber running");
		new Main().subscribe(topic, brokerUrl);

	}

	public void subscribe(String topic, String brokerUrl) {
		//	logger file name and pattern to log
		MemoryPersistence persistence = new MemoryPersistence();

		try
		{

			MqttClient sampleClient = new MqttClient(brokerUrl, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);

			System.out.println("checking");
			System.out.println("Mqtt Connecting to broker: " + brokerUrl);

			sampleClient.connect(connOpts);
			System.out.println("Mqtt Connected");

			sampleClient.setCallback(this);
			sampleClient.subscribe(topic);

			System.out.println("Subscribed");
			System.out.println("Listening");

		} catch (MqttException me) {
			System.out.println(me);
		}
	}

	 //Called when the client lost the connection to the broker
	public void connectionLost(Throwable arg0) {
		
	}

	//Called when a outgoing publish is complete
	public void deliveryComplete(IMqttDeliveryToken arg0) {

	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {

		System.out.println("| Topic:" + topic);
		System.out.println("| Message: " +message.toString());
		System.out.println("-------------------------------------------------");
		
		int id = Integer.parseInt( topic.substring(topic.indexOf("/")+1));


		if (third && id != id1 && id != id2) {
		System.out.println("Il valore giusto era " + Integer.parseInt(  new String(message.getPayload()) ));
		third = false;
		}
		//Salviamo l'ID del primo messaggio arrivato come ID del primo processo.
		if(id1 == 0) {
			
			id1 = id;
			
		}
		
		//Se l'ID del primo processo � gi� stato salvato, lo salviamo come ID del secondo.
		
		else if(id2 == 0 && id1 != id ) {
			
			id2 = id;
		}
		
		if(value1 == 100) {
			
			if(id1 == id) {
				value1 = Integer.parseInt(  new String(message.getPayload()) );
			}
		}
		
		else {
			
			if(id2 == id) {
			value2 = Integer.parseInt(  new String(message.getPayload()) );
			compare(value1, value2 );
			
			//Resettiamo i valori al default dopo averli comparati
			value1 = 100;
			value2 = 100;
		}
		}
	}

	public void createPod()throws IOException{

		ApiClient client = Config.defaultClient();
		Configuration.setDefaultApiClient(client);
		//CoreV1Api api = new CoreV1Api();
		BatchV1Api api = new BatchV1Api();
		Map<String,String> label = new HashMap<String, String>();
		label.put("name", "thirdpod"+new Random().nextInt(1000));


		V1Job job = new V1JobBuilder().withApiVersion("batch/v1").withKind("Job")
				.withNewMetadata().withName(label.get("name"))
				.withLabels(label).endMetadata().withNewSpec().withTtlSecondsAfterFinished(2)
				.withNewTemplate().withNewSpec().addNewContainer().withName(label.get("name")).withImage("alessandrodimatteo97/thirdpod:latest").endContainer().withRestartPolicy("Never").endSpec().endTemplate().endSpec().build();

		try {
			api.createNamespacedJob("default", job, null, null, null);
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}
	
	public int compare(int value1, int value2) throws ApiException, IOException, ClassNotFoundException {

		boolean bool =  (value1 == value2);
		
		if (third) {return value1;}
		//Fare qualcosa con bool --> if (bool)
		
		if(bool) {
			return value1;}
		else {
			
			System.out.println("Error detected");
			//chiamare programma python che crea il pod
		    createPod();
			
			
			
			third = true;
			return 0;
			
			
		}
	}

}
