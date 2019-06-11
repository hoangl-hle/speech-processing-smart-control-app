package com.example.accontroller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class VoidController extends AppCompatActivity {

    MyMqttClient mqttSubcribe;
    MyMqttClient mqttPublish;
    String topic = "user/" + "k61iotlab" + "/" + 1 + "/+/+/+/data";
    String brokerAdd = "iot.eclipse.org";

    ImageView imgSupporter;
    ImageButton micButton;
    String NAME_USER = "k61iotlab";
    String PASSWORD = "12345678";
    String acTopic = "";
    TextView txtRoomTemp;
    TextView textACTemp;
    TextView txtACstatus;
    ImageView dry_mode;
    ImageView auto_mode;
    ImageView heat_mode;
    ImageView wind_mode;
    ImageView cool_mode;
    int old_ACTemp = -100;
    final String localeVi = "vi";
    //vi =  jaVietname vi_VN = vietnamese as spoken VietNam
    int CODE_FOR_SPEECH_RESULT = 1998;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private TextToSpeech tts;
    private SpeechRecognizer speechRecog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.void_controller);
        startToConnectMqtt();

        micButton = findViewById(R.id.imageButton);
        imgSupporter = findViewById(R.id.img_supporter);
        onShakeImage(imgSupporter);
        txtRoomTemp = findViewById(R.id.txtRoomTemp);
        textACTemp = findViewById(R.id.txtACTemp);
        txtACstatus = findViewById(R.id.txtACstatus);
        wind_mode = findViewById(R.id.wind_mode);
        heat_mode = findViewById(R.id.heat_mode);
        auto_mode = findViewById(R.id.auto_mode);
        dry_mode = findViewById(R.id.dry_mode);
        cool_mode = findViewById(R.id.cool_mode);
//        a.setImageResource(R.drawable.ic_wind_fill);

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopShakeImage(imgSupporter);
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(VoidController.this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(VoidController.this,
                            Manifest.permission.RECORD_AUDIO)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(VoidController.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeVi);
                    speechRecog.startListening(intent);
                }
            }
        });

        initializeTextToSpeech();
        initializeSpeechRecognizer();
    }
    private void onShakeImage(ImageView image) {
        Animation shake;
        shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake2);
        image.startAnimation(shake); // starts animation
    }
    private void onStopShakeImage(ImageView image){
        image.getAnimation().cancel();
//        image.clearAnimation();
        image.getAnimation().setFillAfter(true);

    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecog = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecog.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int error) {

                }

                @Override
                public void onResults(Bundle results) {
                    List<String> result_arr = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    processResult(result_arr.get(0));
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
        }
    }

    private void processResult(String result_message) {
        result_message = result_message.toLowerCase();
//        } else if (result_message.indexOf("browser") != -1){
//            speak("Opening a browser right away master.");
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/AnNJPf-4T70"));
//            startActivity(intent);
//        }
        if((result_message.indexOf("android") != -1)){
            if (result_message.indexOf("bật điều hòa") != -1){
                speak("Đã yêu cầu, bật điều hòa");
                if(result_message.indexOf("lạnh") != -1){
                    mqttPublish.publishMessage("{\"0xb0\":\"0x42\"}",acTopic);
                    speak("Đã yêu cầu bật, và chuyển điều hòa sang chế độ làm lạnh");
                } else if(result_message.indexOf("nóng") != -1){
                    mqttPublish.publishMessage("{\"0xb0\":\"0x43\"}",acTopic);
                    speak("Đã yêu cầu bật, và chuyển điều hòa sang chế độ làm nóng");
                } else if(result_message.indexOf("gió")!=-1){
                    mqttPublish.publishMessage("{\"0xb0\":\"0x45\"}",acTopic);
                    speak("Đã yêu cầu bật, và chuyển điều hòa sang chế độ quạt gió");
                } else if(result_message.indexOf("khô")!=-1){
                    mqttPublish.publishMessage("{\"0xb0\":\"0x44\"}",acTopic);
                    speak("Đã yêu cầu bật, và chuyển điều hòa sang chế độ làm khô");
                } else if(result_message.indexOf("tự động")!=-1){
                    mqttPublish.publishMessage("{\"0xb0\":\"0x41\"}",acTopic);
                    speak("Đã yêu cầu bật, và chuyển điều hòa sang chế độ tự động");
                }
                mqttPublish.publishMessage("{\"0x80\":\"0x30\"}",acTopic);
            } else if (result_message.indexOf("tắt điều hòa") != -1){
                speak("Đã yêu cầu, tắt điều hòa");
                mqttPublish.publishMessage("{\"0x80\":\"0x31\"}",acTopic);
            } else if(result_message.indexOf("chuyển") != -1){
                if(result_message.indexOf("lạnh") != -1){
                    mqttPublish.publishMessage("{\"0xb0\":\"0x42\"}",acTopic);
                    speak("Đã yêu cầu, chuyển điều hòa sang chế độ làm lạnh");
                } else if(result_message.indexOf("nóng") != -1){
                    mqttPublish.publishMessage("{\"0xb0\":\"0x43\"}",acTopic);
                    speak("Đã yêu cầu, chuyển điều hòa sang chế độ làm nóng");
                } else if(result_message.indexOf("gió")!=-1){
                    mqttPublish.publishMessage("{\"0xb0\":\"0x45\"}",acTopic);
                    speak("Đã yêu cầu, chuyển điều hòa sang chế độ quạt gió");
                } else if(result_message.indexOf("khô")!=-1){
                    mqttPublish.publishMessage("{\"0xb0\":\"0x44\"}",acTopic);
                    speak("Đã yêu cầu, chuyển điều hòa sang chế độ làm khô");
                } else if(result_message.indexOf("tự động")!=-1){
                    mqttPublish.publishMessage("{\"0xb0\":\"0x41\"}",acTopic);
                    speak("Đã yêu cầu, chuyển điều hòa sang chế độ tự động");
                }
            } else if (result_message.indexOf("tăng nhiệt độ") != -1){
                    int new_ACTemp = old_ACTemp + 1;
                    String newAC = "0x" + Integer.toHexString(new_ACTemp);
                    mqttPublish.publishMessage("{\"0xb3\":\""+ newAC + "\"}",acTopic);
                    speak("Đã yêu cầu tăng nhiệt độ điều hoà lên " + new_ACTemp + "độ xê");
            } else if (result_message.indexOf("giảm nhiệt độ") != -1){
                    int new_ACTemp = old_ACTemp - 1;
                    String newAC = "0x" + Integer.toHexString(new_ACTemp);
                    mqttPublish.publishMessage("{\"0xb3\":\""+ newAC + "\"}",acTopic);
                    speak("Đã yêu cầu giảm nhiệt độ điều hoà xuống " + new_ACTemp + "độ xê");
            } else {
                speak("Tôi không hiểu mệnh lệnh của bạn. Bạn có thể ra lệnh cho tôi bật tắt , chuyển chế độ" +
                        "hay tăng giảm nhiệt độ điều hòa");
            }
        }
    }

    private void initializeTextToSpeech() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (tts.getEngines().size() == 0 ){
                    Toast.makeText(VoidController.this, getString(R.string.tts_no_engines),Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    tts.setLanguage(new Locale("vn", "VN"));
                    speak("Chào bạn, tên tôi là Android. Hãy gọi tên tôi và mệnh lệnh đi kèm để thực hiện yêu cầu của bạn");
                }
            }
        });
    }

    private void speak(String message) {
        if(Build.VERSION.SDK_INT >= 21){
            tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Reinitialize the recognizer and tts engines upon resuming from background such as after openning the browser
        initializeSpeechRecognizer();
        initializeTextToSpeech();
        imgSupporter = findViewById(R.id.img_supporter);
        onShakeImage(imgSupporter);
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CODE_FOR_SPEECH_RESULT && resultCode==RESULT_OK && data !=null){
            ArrayList<String> resT = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            int temp;
            if(resT .get(0).equals("tăng nhiệt độ")){
                temp = Integer.parseInt(textView.getText().toString());
                temp = temp +1;
                textView.setText(""+temp);
                Log.i("cj",""+textView.getText().toString());


                Intent intent2 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent2.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                intent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeVi);
                if(intent2.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(intent2,CODE_FOR_SPEECH_RESULT);
                    Toast.makeText(VoidController.this, "Your asdfasdfasdf", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VoidController.this, "Your device do not support speech input", Toast.LENGTH_SHORT).show();
                }
            } else if(resT .get(0).equals("giảm nhiệt độ")){
                temp = Integer.parseInt(textView.getText().toString());
                temp = temp -1;
                textView.setText(""+temp);
                Log.i("mqttMainTextView",""+textView.getText().toString());
            }
        }
    }*/

    private void startToConnectMqtt(){
        MqttConnectOptions mqttConnectOptions = initMqttConnectOptions(NAME_USER,PASSWORD);
        mqttSubcribe = new MyMqttClient(getApplicationContext(), brokerAdd
                , mqttConnectOptions
                , setIMqttActionListenerConnect()
                , setMqttCallbackExtended()
                , 1);
        mqttPublish =  new MyMqttClient(getApplicationContext(), brokerAdd
                , mqttConnectOptions
                , setIMqttActionListenerConnect()
                , setMqttCallbackExtended()
                , 1);
    }

    private MqttConnectOptions initMqttConnectOptions(String username, String password){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        return  mqttConnectOptions;
    }

    private DisconnectedBufferOptions initDisconnectedBufferOptions(){
        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(false);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        return disconnectedBufferOptions;
    }
    private IMqttActionListener setIMqttActionListenerConnect(){
        IMqttActionListener iMqttActionListener = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {

                DisconnectedBufferOptions disconnectedBufferOptions = initDisconnectedBufferOptions();
                mqttSubcribe.setDisconnectBufferOpts(disconnectedBufferOptions);
                mqttSubcribe.subscribeToTopic(topic);
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w("mqttOnFailure", "Failed to connect to: "
                        + brokerAdd + " with exception: "+ exception.toString());
            }
        };
        return iMqttActionListener;
    }

    private MqttCallbackExtended setMqttCallbackExtended(){
        MqttCallbackExtended mqttCallbackExtended = new MqttCallbackExtended() {

            @Override
            public void connectComplete(boolean b, String s)
            {
                Log.w("mqttConnectComplete", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("mqttMessageArrived",mqttMessage.toString() + brokerAdd);
                String[] modifyTopic = topic.split("/");
                int lengthMTopic = modifyTopic.length;
                String deviceType = modifyTopic[lengthMTopic-3];
                JSONObject jsonObject = new JSONObject(mqttMessage.toString());
                if(deviceType.equals("home_air_conditioner")){
                    String commandTopic = topic.replace("data","command");
                    if(!acTopic.equals(commandTopic)){
                        acTopic=commandTopic;
                        System.out.println(mqttMessage.toString());
                    }
                    String current_status = jsonObject.getString("current_status");
                    String current_mode = jsonObject.getString("current_mode");
                    int current_temperature = Integer.parseInt(jsonObject.getString("current_temperature"));
                    if(current_status.equals("on")){
                        txtACstatus.setText("Trạng thái điều hòa: Bật");
                    } else {
                        txtACstatus.setText("Trạng thái điều hòa: Tắt");
                    }
                    if(old_ACTemp!=current_temperature){
                        old_ACTemp = current_temperature;
                        textACTemp.setText("Nhiệt độ thiết lâp ở điều hoà " + current_temperature + "\u00B0C");
                    }
                    auto_mode.setImageResource(R.drawable.ic_idea);
                    heat_mode.setImageResource(R.drawable.ic_heat_);
                    dry_mode.setImageResource(R.drawable.ic_dry);
                    cool_mode.setImageResource(R.drawable.ic_cold_);
                    wind_mode.setImageResource(R.drawable.ic_wind_);

                    if(current_mode.equals("auto")){
                        auto_mode.setImageResource(R.drawable.ic_idea_fill);
                    } else if(current_mode.equals("heat")){
                        heat_mode.setImageResource(R.drawable.ic_heat_fill);
                    } else if(current_mode.equals("dry")){
                        dry_mode.setImageResource(R.drawable.ic_dry_fill);
                    } else if(current_mode.equals("cool")){
                        cool_mode.setImageResource(R.drawable.ic_cold_fill);
                    } else if(current_mode.equals("wind")){
                        wind_mode.setImageResource(R.drawable.ic_wind_fill);
                    }
                }else if(deviceType.equals("temperature_sensor")){
                    String room_temperature = jsonObject.getString("room_temperature");
                    txtRoomTemp.setText("Nhiệt độ trong phòng  " + room_temperature + "\u00B0C" );
                }


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        };
        return mqttCallbackExtended;
    }
}
