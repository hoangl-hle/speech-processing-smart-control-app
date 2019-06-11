package com.example.accontroller;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttConnect;

import java.util.UUID;

public class MyMqttClient {
    public MqttAndroidClient mqttAndroidClient;
    final public int TOSUBCRIBE = 1;
    final public int TOPUBLISH = 0;

    final String defaultServer = "iot.eclipse.org";
    final String defaultPort = "1883";

    private int target = TOSUBCRIBE;
    private String randomClientId;
    private String opServer = defaultServer;
    private String opUsername = "1";
    private String opPassword = "12345678";
    private String opPort;
    private String defaultSubcribeTopic = "user/" + "k61iotlab" + "/" + 2 + "/+/+/+/data";
    //            "user/k61iotlab/2/bedroom/humidity_sensor/68:c6:3a:c2:cc:64-1/data";
            /*"user/" + "k61iotlab" + "/" + 2 + "/+/+/+/data";*/
    /*"user/" + user + "/" + homeID + "/+/+/+/data"*/
    /* mosquitto_pub -t "user/k61iotlab/2/bedroom/humidity_sensor/68:c6:3a:c2:cc:64-1/data" -m {\"room_humidity\":\"75\",\"installation_location\":\"bedroom\"}*/
    private MqttConnectOptions mqttConnectOptions;
    private IMqttActionListener iMqttActionListenerConnect;


    public MyMqttClient(Context context, String brokerAdd, MqttConnectOptions mqttConnectOptions
            , IMqttActionListener iMqttActionListenerConnect, MqttCallbackExtended mqttCallbackExtended, int target){
        this.mqttConnectOptions = mqttConnectOptions;
        this.iMqttActionListenerConnect = iMqttActionListenerConnect;
        randomClientId = UUID.randomUUID().toString();
        //Log.w("sss", brokerAdd);
        opServer = "tcp://" + brokerAdd + ":"+ defaultPort;

        mqttAndroidClient = new MqttAndroidClient(context,opServer,randomClientId);

        mqttAndroidClient.setCallback(mqttCallbackExtended);
        connect();
    }
    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private void connect(){
        try {

            mqttAndroidClient.connect(mqttConnectOptions, null, iMqttActionListenerConnect);
        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    /*private*/public void subscribeToTopic(String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    Log.w("Mqtt_MyMqttClient_java","Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt_MyMqttClient_java", "Subscribed fail!");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception subscribing");
            ex.printStackTrace();
        }
    }
    synchronized public void publishMessage(String message, String topic){
        try {
            //System.out.println(">>> Message before published: ");
            //System.out.println(message);
            if(message == null){
                //System.out.println("null String ");
                Thread.sleep(3000);
                return;
            }

            MqttMessage messageMqtt = new MqttMessage();
            messageMqtt.setPayload(message.getBytes());
            messageMqtt.setQos(0);
            System.out.println(">> Message published: ");
            System.out.println(messageMqtt);
            mqttAndroidClient.publish(topic, messageMqtt, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.w("Mqtt_MyMqttClient_java","published");
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.w("Mqtt_MyMqttClient_java","error - unpublished" );
                }
            });
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void setDisconnectBufferOpts(DisconnectedBufferOptions disconnectedBufferOptions){
        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
    }

}
