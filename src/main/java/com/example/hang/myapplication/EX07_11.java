package com.example.hang.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EX07_11 extends Activity  {
  /*锟斤拷锟斤拷锟侥革拷ImageButton*/
  private ImageButton myButton1;
  private ImageButton myButton2;
  private ImageButton myButton3;
  private ImageButton myButton4;
  private ListView myListView1;
  private String strTempFile = "ex07_11_";
  private File myRecAudioFile;
  private File myRecAudioDir;
  private File myPlayFile;
  private boolean runwairtime = false;
  private MediaRecorder mMediaRecorder01;
  private ArrayList<String> recordFiles;
  private ArrayAdapter<String> adapter;
  private TextView myTextView1;
  private boolean sdCardExit;
  private boolean isStopRecord;
  private int timetrue;
  private Spinner spn;
  private Boolean autotime = false;
  private boolean timeEnd = false; //是否暂停运行runable
  //private int time ;//每个录音的时长
  //最大录音时长，3分钟
  private static final int MAX_LENGTH = 3*60*1000;
  //录音总时长
  private static int voiceLength;
  //等待时长
  private int waittime;
  private Handler handler= new Handler();
  private GoogleApiClient client;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);//
    setContentView(R.layout.main);
    myButton1 = (ImageButton) findViewById(R.id.ImageButton01);//录音
    myButton2 = (ImageButton) findViewById(R.id.ImageButton02);
    myButton3 = (ImageButton) findViewById(R.id.ImageButton03);
    myButton4 = (ImageButton) findViewById(R.id.ImageButton04);
    myListView1 = (ListView) findViewById(R.id.ListView01);
    myTextView1 = (TextView) findViewById(R.id.TextView01);
    spn = (Spinner) findViewById(R.id.spinner);
    myButton2.setEnabled(false);
    myButton3.setEnabled(false);
    myButton4.setEnabled(false);
    sdCardExit = Environment.getExternalStorageState().equals(
            Environment.MEDIA_MOUNTED);
    if (sdCardExit)
      //获取到的路径
      myRecAudioDir = Environment.getExternalStorageDirectory();
    /* 取得SD Card目录里的所有.amr文件,200行代码方法 */
    getRecordFiles();
    adapter = new ArrayAdapter<String>(this,
            R.layout.my_simple_list_item, recordFiles);
    myListView1.setAdapter(adapter);
    spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
          case 0:
            autotime =false;
            waittime = 0;
            runwairtime = false;
            break;
          case 1:
            waittime = 10*1000;
            toWaittimerun();
            break;
          case 2:
            waittime = 5*60*1000;
            toWaittimerun();
            break;
          case 3:
            waittime = 10*60*1000;
            toWaittimerun();
            break;
          case 4:
            waittime = 30*60*1000;
            toWaittimerun();
            break;
          default:
            break;
        }
      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });

    /* 录音按钮监听*/
    myButton1.setOnClickListener(new ImageButton.OnClickListener() {

      public void onClick(View arg0) {
        if (!sdCardExit) {
          Toast.makeText(EX07_11.this, "请插入SD Card",
                  Toast.LENGTH_LONG).show();
          return;
        }
        //开启录音
        autotime = false;
        startAudio();
      }
    });
    /* 停止按钮监听 */
    myButton2.setOnClickListener(new ImageButton.OnClickListener() {

      public void onClick(View arg0) {
        if (myRecAudioFile != null) {
          //暂停
          stopVideo();
        }
      }
    });
    myButton3.setOnClickListener(new ImageButton.OnClickListener() {
      public void onClick(View arg0) {

        if (myPlayFile != null && myPlayFile.exists()) {
          openFile(myPlayFile);
        }

      }
    });

    myButton4.setOnClickListener(new ImageButton.OnClickListener() {
      public void onClick(View arg0) {
        // TODO Auto-generated method stub
        if (myPlayFile != null) {
        /* 因将Adapter移除文件名 */
          adapter.remove(myPlayFile.getName());

          if (myPlayFile.exists())
            myPlayFile.delete();
          myTextView1.setText("完成删除");
        }

      }
    });

    myListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> arg0, View arg1,
                              int arg2, long arg3) {
        myButton3.setEnabled(true);
        myButton4.setEnabled(true);
        myPlayFile = new File(myRecAudioDir.getAbsolutePath()
                + File.separator
                + ((CheckedTextView) arg1).getText());
        myTextView1.setText("你选的是："
                + ((CheckedTextView) arg1).getText());
      }
    });

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
  }

  @Override
  protected void onStop() {
    if (mMediaRecorder01 != null && !isStopRecord) {
      /* 停止*/
      mMediaRecorder01.stop();
      mMediaRecorder01.release();
      mMediaRecorder01 = null;
    }
    super.onStop();
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    Action viewAction = Action.newAction(
            Action.TYPE_VIEW, // TODO: choose an action type.
            "EX07_11 Page", // TODO: Define a title for the content shown.
            // TODO: If you have web page content that matches this app activity's content,
            // make sure this auto-generated web page URL is correct.
            // Otherwise, set the URL to null.
            Uri.parse("http://host/path"),
            // TODO: Make sure this auto-generated app deep link URI is correct.
            Uri.parse("android-app://com.example.hang.myapplication/http/host/path")
    );
    AppIndex.AppIndexApi.end(client, viewAction);
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client.disconnect();
  }
  private void getRecordFiles() {
    //一个ArrayList，用作适配，显示lustview。
    recordFiles = new ArrayList<String>();
    if (sdCardExit) {
     // 返回一个抽象路径名数组，这些路径名表示此抽象路径名表示的目录中的文件。
      File files[] = myRecAudioDir.listFiles();
      if (files != null) {
        for (int i = 0; i < files.length; i++) {
          if (files[i].getName().indexOf(".") >= 0) {
            //substring截取字符串的一个方法
            String fileS = files[i].getName().substring(
                    files[i].getName().indexOf("."));
            //toLowerCase() 方法用于把字符串转换为小写
            if (fileS.toLowerCase().equals(".amr"))
              recordFiles.add(files[i].getName());
            }
        }
      }
    }
  }
//隐式打开activity，该activity是可以代开audio的应用。
  private void openFile(File f) {
    Intent intent = new Intent();
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setAction(Intent.ACTION_VIEW);
    String type = getMIMEType(f);
    intent.setDataAndType(Uri.fromFile(f), type);
    startActivity(intent);
  }
    //得到文件类型
  private String getMIMEType(File f) {
    String end = f.getName().substring(
            f.getName().lastIndexOf(".") + 1, f.getName().length())
            .toLowerCase();
    String type = "";
    if (end.equals("mp3") || end.equals("aac") || end.equals("aac")
            || end.equals("amr") || end.equals("mpeg")
            || end.equals("mp4")) {
      type = "audio";
    } else if (end.equals("jpg") || end.equals("gif")
            || end.equals("png") || end.equals("jpeg")) {
      type = "image";
    } else {
      type = "*";
    }
    type += "/*";
    return type;
  }
  /** 等待时间*/
  private void waittimerun(){
      Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
          if (runwairtime) {
            //如果音频录音的总长度临近总时长，颜色变红
            if (waittime <= 0) {
              Toast.makeText(getApplicationContext(),"开始录音",Toast.LENGTH_LONG).show();
              startAudio();
              Log.d("hello", "startAudio");
            } else {
              //尚未等待结束，毫秒转化为mm:ss
              waittime -= 1000;
              SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
              String time = formatter.format(waittime);
              myTextView1.setText("等待:" + time);
              myTextView1.setTextColor(Color.RED);
              Log.d("hello", time);
              //1000毫秒延迟，再次执行
              handler.postDelayed(this, 1000);
            }
          }
          else{
            return;
          }
        }
  };
    handler.postDelayed(runnable1,1000);
  }
  /**
   *
   * 录音计时间功能
   */
  private void timing() {
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
          if (timeEnd) {
             if(autotime){
               timecount(this, 60 * 1000);
             }else {
               //开始录音计时
               timecount(this, 0);
             }
        } else {
            if(autotime){
              waittime =timetrue;
              toWaittimerun();
            }
            return;
          }
      }
    };
    handler.postDelayed(runnable,1000);
  }
  /**
   *
   * @param runnable
   * @param endtime 输入录音时长，为0这不会自动结束
   */
  private void  timecount(Runnable runnable ,int endtime ){
    //路过时间

    voiceLength += 1000;
    //如果音频录音的总长度临近总时长，颜色变红
    if (voiceLength >= (MAX_LENGTH - 10 * 1000)) {
      myTextView1.setTextColor(Color.RED);
    } else {
      //否则，显示为蓝色
      myTextView1.setTextColor(Color.BLUE);
    }
    //如果超出总时长 ，停止
    if (voiceLength > MAX_LENGTH) {
      voiceLength = 0;
      stopVideo();
    } else {
      //尚未超过时长，毫秒转化为mm:ss
      SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
     String time = formatter.format(voiceLength);
      //异步修改Ui String time =
      myTextView1.setText("录音中:" + time);
      Log.d("hello", time);
      //1000毫秒延迟，再次执行
      Log.d("hello",voiceLength+"");
      if(voiceLength >= endtime && endtime!=0){
        stopVideo();
      }
      handler.postDelayed(runnable,1000);
    }
  }

  /** 建立录音档 */
  private void startAudio(){
    try {
      myRecAudioFile = File.createTempFile(strTempFile, ".amr",
              myRecAudioDir);
    } catch (IOException e) {
      e.printStackTrace();
    }
    timeEnd =true;
    timing();
    mMediaRecorder01 = new MediaRecorder();
          /* 设定录音来源为麦克风 */
    mMediaRecorder01
            .setAudioSource(MediaRecorder.AudioSource.MIC);
    mMediaRecorder01
            .setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
    mMediaRecorder01
            .setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

    mMediaRecorder01.setOutputFile(myRecAudioFile
            .getAbsolutePath());
    try {
      mMediaRecorder01.prepare();
    } catch (IOException e) {
      e.printStackTrace();
    }
    mMediaRecorder01.start();
    myButton1.setEnabled(false);
    myButton2.setEnabled(true);
    myButton3.setEnabled(false);
    myButton4.setEnabled(false);
    isStopRecord = false;
  }
  /** 停止 */
  private void stopVideo(){
    timeEnd =false;
    runwairtime = false;
    voiceLength = 0;
    mMediaRecorder01.stop();
       /* 将录音文件名给Adapter */
    adapter.add(myRecAudioFile.getName());
    mMediaRecorder01.release();
    mMediaRecorder01 = null;
    myTextView1.setText("停止" + myRecAudioFile.getName());
    myButton1.setEnabled(true);
    myButton2.setEnabled(false);
    isStopRecord = true;
    delateFile();
  }
/**
 */
  private void toWaittimerun(){
    if (!runwairtime){
      autotime = true;
      runwairtime=true;
      timetrue = waittime;
      waittimerun();
    }
  }
  private void delateFile() {
    if (recordFiles.size() > 20) {
      Log.d("hello", "到达上限20");
             /* 因将Adapter移除文件名 */
      for (int i = 0; i < recordFiles.size() - 20; i++) {
        myPlayFile = new File(myRecAudioDir.getAbsolutePath()
                + File.separator
                + ((recordFiles.get(0))));
        if (myPlayFile != null) {
          if (myPlayFile.exists())
            myPlayFile.delete();
          Toast.makeText(getApplicationContext(), "删除文件", Toast.LENGTH_LONG);
        }
        adapter.remove(myPlayFile.getName());
      }
    }
  }
    Thread t = new Thread(){

    };
  @Override
  public void onStart() {
    super.onStart();

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client.connect();
    Action viewAction = Action.newAction(
            Action.TYPE_VIEW, // TODO: choose an action type.
            "EX07_11 Page", // TODO: Define a title for the content shown.
            // TODO: If you have web page content that matches this app activity's content,
            // make sure this auto-generated web page URL is correct.
            // Otherwise, set the URL to null.
            Uri.parse("http://host/path"),
            // TODO: Make sure this auto-generated app deep link URI is correct.
            Uri.parse("android-app://com.example.hang.myapplication/http/host/path")
    );
    AppIndex.AppIndexApi.start(client, viewAction);
  }
}

