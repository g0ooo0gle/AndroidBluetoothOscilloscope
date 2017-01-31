package com.example.nakamurakeisuke.androidbluetoothoscilloscope;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    TextView textView , statusView;
    LineChart signalChart;
    BluetoothSPP bt;
    Menu menu;
    boolean connectFlag = false;
    String statusString;


    //グラフ用
    ArrayList<Entry> values = new ArrayList<Entry>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View content_view = findViewById(R.id.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView)content_view.findViewById(R.id.textView);
        statusView = (TextView)content_view.findViewById(R.id.statusView);
        signalChart = (LineChart) content_view.findViewById(R.id.linechart);

        //チャート初期化
        drawChartinit();


        bt = new BluetoothSPP(this);

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                //textView.append(message + "\n");
                if (message.contains("e")){
                    values.clear();
                    signalChart.getData().notifyDataChanged();
                }
                isValueMonitored(message);

//                //テスト表示部
//                try{
//
//                    if (message.contains(",")){
//                        String[] dataarray = message.split("\n");
//                        for (int i = 0; i < dataarray.length; i++) {
//
//                            String[] datas = dataarray[i].split(",");
//                            //textView.append("data;"+ datas[i]+"data2;"+ datas[1]+"\n");
//                            float voltage = (Float.valueOf(datas[1])*5)/1024;
//                            textView.setText("Time:" + datas[0]+ "[μs] " + "Voltage"+ voltage +"[V]");
//                            //textView.append("count;"+i+"\n");
//
//                            if (datas.length < 2){
//                                break;
//                            }
//
////                        values.add(new Entry(Float.valueOf(datas[0]),Float.valueOf(datas[1])));//データ値リストに追加(x,y)
//
//
//                        }
//                    }
//
//
//                }catch (NumberFormatException e){
//
//                //Toast.makeText(MainActivity.this,"format error"+ data,Toast.LENGTH_SHORT).show();
//
//            }

            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                statusView.setText("Status : Not connect");
                statusString = "Status : 接続されていません。";
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_connection, menu);
                connectFlag = false;
            }

            public void onDeviceConnectionFailed() {
                statusView.setText("Status : Connection failed");
                statusString = "Status : 接続失敗";
                connectFlag = false;
            }

            public void onDeviceConnected(String name, String address) {
                statusView.setText("Status : Connected to " + name);
                statusString = "Status : "+ name + "に接続されています。" ;
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_disconnection, menu);
                connectFlag = true;

            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(connectFlag == true) {
                    //Toast.makeText(getApplicationContext(), "Bluetoothうごいてる", Toast.LENGTH_SHORT).show();
                    Snackbar.make(view, statusString , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                else {
                    //Toast.makeText(getApplicationContext(), "とーすとおしたけどBluetoothうごいてないぞ", Toast.LENGTH_SHORT).show();
                    Snackbar.make(view, statusString, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

            }
        });

    }

    //@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        //getMenuInflater().inflate(R.menu.menu_connection, menu);
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        if(id == R.id.menu_android_connect) {
//            bt.setDeviceTarget(BluetoothState.DEVICE_ANDROID);
//			/*
//			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
//    			bt.disconnect();*/
//            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
//            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
//        } else if(id == R.id.menu_device_connect) {
//            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
//			/*
//			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
//    			bt.disconnect();*/
//            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
//            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
//        } else if(id == R.id.menu_disconnect) {
//            if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
//                bt.disconnect();
//        }
//        //noinspection SimplifiableIfStatement
//        //if (id == R.id.action_settings) {
//        //    return true;
//        //}
//
//        return super.onOptionsItemSelected(item);
//    }
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if(id == R.id.menu_device_connect) {
        bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
			/*
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();*/
        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    } else if(id == R.id.menu_disconnect) {
        if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
            bt.disconnect();
    }
    //noinspection SimplifiableIfStatement
    //if (id == R.id.action_settings) {
    //    return true;
    //}

    return super.onOptionsItemSelected(item);
}

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                //               setup();//送信用
            }
        }
    }
//送信用のやつ
/*

    public void setup() {
        Button btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(etMessage.getText().length() != 0) {
                    bt.send(etMessage.getText().toString(), true);
                    etMessage.setText("");
                }
            }
        });
    }
*/

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                //setup();//とめてる
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public  void drawChartinit(){
        signalChart.setVisibleXRangeMaximum(256);


        //X軸設定
        XAxis rightAxis = signalChart.getXAxis();
        rightAxis.setTextColor(Color.BLACK);
        rightAxis.setAxisMaxValue(1500f);
        rightAxis.setAxisMinimum(0f);

        //Y軸設定
        YAxis leftAxis = signalChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaxValue(6.0f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setStartAtZero(true);
        leftAxis.setDrawGridLines(true);

        YAxis yAxisRight = signalChart.getAxisRight();
        yAxisRight.setEnabled(false);


    }

    public void isValueMonitored(String data) {
        int counter =0;
        try {

                String[] dataarray = data.split("\n");

                for (int i = 0; i < dataarray.length; i++) {
                    String[] datas = dataarray[i].split(",");

                    if (datas.length < 2){
                        break;
                    }
                    //ログ出力
                    //Log.d("Recived",datas[0]+","+datas[1]);

                    //分けた配列0番目の処理

                    //textView.setText(datas[1]);
                    float voltage = (Float.valueOf(datas[1])*5)/1024;
                    //values.add(new Entry(Float.valueOf(datas[0]),Float.valueOf(datas[1])));//データ値リストに追加(x,y)
                    values.add(new Entry(Float.valueOf(datas[0]),voltage));//データ値リストに追加(x,y)

                    //リアルタイム表示用
                    textView.setText("Time:" + datas[0]+ "[μs] " + "Voltage"+ voltage +"[V]");


                    //Log.d("debugデータ値のリスト要素数", String.valueOf(values.size()));

                    //画面描画時に波形が動き始める値の調整用
                    //先頭の値削除
                    //if (values.size() > 100){
                    //    signalChart.getLineData().getDataSets().get(0).removeFirst();
                    //}

                    //if (Float.valueOf(datas[0]) == 0){
                    //    signalChart.invalidate(); // refresh
                    //}


                    //chart初期化
                    //touch gesture設定
                    signalChart.setTouchEnabled(true);
                    // スケーリング&ドラッグ設定
                    signalChart.setDragEnabled(true);
                    signalChart.setScaleEnabled(true);
                    signalChart.setDrawBorders(true);
                    //背景
                    signalChart.setDrawGridBackground(false);

                    //XAxis xAxis = signalChart.getXAxis();
                    //xAxis.setTextColor(Color.BLACK);

                    LineDataSet set1 = new LineDataSet(values,"波形");

                    //点線設定
                    //set1.enableDashedLine(10f, 5f, 0f);
                    //set1.enableDashedHighlightLine(10f, 5f, 0f);

                    set1.setColor(Color.GREEN);
                    set1.setDrawValues(false);          //値ラベル表示しない
                    set1.setLineWidth(2f);

                    //プロット点設定
                    set1.setCircleRadius(1f);
                    set1.setDrawCircleHole(false);
                    set1.setCircleColor(Color.GREEN);

                    set1.setValueTextSize(9f);
                    set1.setDrawFilled(false);
                    set1.setFormLineWidth(1f);
                    set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                    set1.setFormSize(15.f);

                    set1.setDrawValues(false);


                    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

                    dataSets.add(set1);

                    dataSets.get(0).getXMax();

                    LineData lineData = new LineData(dataSets);

                    signalChart.setData(lineData);

                    signalChart.getData().notifyDataChanged();
                    lineData.notifyDataChanged();

                    //最新データまで移動
                    signalChart.moveViewToX(lineData.getEntryCount());

                    counter++;

                }

        }catch (NumberFormatException e){

            //Toast.makeText(MainActivity.this,"format error"+ data,Toast.LENGTH_SHORT).show();
            statusView.setText("[Warn!]Format error:" + data);


        }

    }

}
