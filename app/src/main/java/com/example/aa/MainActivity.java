package com.example.aa;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private Button btn1;
    private Button btn_add;
    private String result;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0:
                    Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_LONG).show();
                case 1:
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button) findViewById(R.id.btn1);
        btn_add = (Button) findViewById(R.id.btn_add);

        btn_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setAddDialog();
                hideButton(true);
            }
        });


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnClick();
            }
        });
    }

    private void setAddDialog() {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_add);
        dialog.setTitle("输入添加的竞赛信息");
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);


        final EditText cTitleEditText = (EditText) dialog.findViewById(R.id.editText1);
        final EditText cStatusEditText = (EditText) dialog.findViewById(R.id.editText2);
        final EditText cHostEditText = (EditText) dialog.findViewById(R.id.editText3);
        final EditText cLevelEditText = (EditText) dialog.findViewById(R.id.editText4);
        final EditText cTimeEditText = (EditText) dialog.findViewById(R.id.editText5);
        final EditText cTimingEditText = (EditText) dialog.findViewById(R.id.editText6);
        Button btnConfirm = (Button) dialog.findViewById(R.id.button1);
        Button btnCancel = (Button) dialog.findViewById(R.id.button2);

        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //dbUtil.insertCompetitionInfo(cTitleEditText.getText().toString(), cStatusEditText.getText().toString(), cHostEditText.getText().toString(), cLevelEditText.getText().toString(), cTimeEditText.getText().toString(), cTimingEditText.getText().toString());
                //dialog.dismiss();
                Toast.makeText(MainActivity.this, "成功添加数据", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 设置button的可见性
     */
    private void hideButton(boolean result) {
        if (result) {
            btn1.setVisibility(View.GONE);
            btn_add.setVisibility(View.GONE);
            //btn3.setVisibility(View.GONE);
        } else {
            btn1.setVisibility(View.VISIBLE);
            btn_add.setVisibility(View.VISIBLE);
            //btn3.setVisibility(View.VISIBLE);
        }

    }


    private void BtnClick() {

        final  String SERVICE_NS = "http://tempuri.org/";//命名空间
        final  String SOAP_ACTION = "http://tempuri.org/selectAllCompetitonInfo";//用来定义消息请求的地址，也就是消息发送到哪个操作 //SOAP Action
        final  String SERVICE_URL = "http://192.168.1.6:8020/WebService1.asmx";//URL地址，这里写发布的网站的本地地址  // EndPoint
        String methodName = "selectAllCompetitonInfo";//调用的方法名
        //创建HttpTransportSE传输对象，该对象用于调用Web Service操作
        final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
        ht.debug = true;
        //使用SOAP1.1协议创建Envelop对象。从名称上来看,SoapSerializationEnvelope代表一个SOAP消息封包；但ksoap2-android项目对
        //SoapSerializationEnvelope的处理比较特殊，它是HttpTransportSE调用Web Service时信息的载体--客户端需要传入的参数，需要通过
        //SoapSerializationEnvelope对象的bodyOut属性传给服务器；服务器响应生成的SOAP消息也通过该对象的bodyIn属性来获取。
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        //实例化SoapObject对象，创建该对象时需要传入所要调用的Web Service的命名空间、Web Service方法名
        SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
        //对dotnet webservice协议的支持,如果dotnet的webservice
        envelope.dotNet = true;
        //调用SoapSerializationEnvelope的setOutputSoapObject()方法，或者直接对bodyOut属性赋值，将前两步创建的SoapObject对象设为
        //SoapSerializationEnvelope的付出SOAP消息体
        envelope.bodyOut = soapObject;

        new Thread(){
            @Override
            public void run() {
                try {
                    //调用WebService，调用对象的call()方法，并以SoapSerializationEnvelope作为参数调用远程Web Service
                    ht.call(SOAP_ACTION, envelope);
                    if(envelope.getResponse() != null){
                        //获取服务器响应返回的SOAP消息，调用完成后，访问SoapSerializationEnvelope对象的bodyIn属性，该属性返回一个
                        //SoapObject对象，该对象就代表了Web Service的返回消息。解析该SoapObject对象，即可获取调用Web Service的返回值
                        SoapObject so = (SoapObject) envelope.bodyIn;
                        //接下来就是从SoapObject对象中解析响应数据的过程了
                        result = so.getPropertyAsString(0);
                        Message msg = new Message();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    else{
                        Message msg=new Message();
                        msg.what=0;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        
    }
}
