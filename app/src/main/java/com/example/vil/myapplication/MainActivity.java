package com.example.vil.myapplication;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    TextView et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (TextView) findViewById(R.id.editText);
        checkPermission();
    }

    private void checkPermission(){
        int permissioninfo = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissioninfo == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),
                    "쓰기권한 있음", Toast.LENGTH_SHORT).show();
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this, "권한 설명", Toast.LENGTH_SHORT).show();
            }else{
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            }
        }
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "SD Card 쓰기권한 승인", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "SD Card 쓰기권한 거부", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public String getExternalPath(){
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        }else
            sdPath = getFilesDir()+"";
        Toast.makeText(getApplicationContext(), sdPath, Toast.LENGTH_SHORT).show();
        return sdPath;
    }

    public void onClick(View v){
        if(v.getId() == R.id.button2 ){
            try {
                BufferedWriter bw = new BufferedWriter(
                        new FileWriter(getFilesDir()+ "test.txt", true));
                bw.write("안녕하세요 Hello");
                bw.close();
                Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }else if(v.getId()==R.id.button1) {
            try {
                BufferedReader br = new BufferedReader(
                        new FileReader(getFilesDir() + "test.txt"));
                String readStr = "";
                String str = null;
                while ((str = br.readLine()) != null)
                    readStr += str + "\n";
                br.close();

                Toast.makeText(this, readStr.substring(0, readStr.length() - 1),
                        Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(v.getId()==R.id.button3){
            InputStream is = getResources().openRawResource(R.raw.about);
            getResources().openRawResource(R.raw.about);
            byte[] readStr = new byte[0];
            try {
                readStr = new byte[is.available()];
                is.read(readStr);
                is.close();
                Toast.makeText(this, new String(readStr), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(v.getId()==R.id.button5){
            try {
                String path = getExternalPath();
                String filename = new SimpleDateFormat("YYMMdd").format(new Date())+".txt";
                BufferedWriter bw = null;
                bw = new BufferedWriter(new FileWriter(path +"mydiary/"+filename, true));
                bw.write("안녕하세요 SDCard Hello");
                bw.close();
                Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage()+":"+getFilesDir(), Toast.LENGTH_SHORT).show();
            }
        }else if(v.getId() == R.id.button4){
            try {
                String path = getExternalPath();
                String filename = new SimpleDateFormat("YYMMdd").format(new Date())+".txt";
                BufferedReader br = new BufferedReader(new FileReader(path +"mydiary/"+filename));
                String readStr = "";
                String str = null;
                while((str = br.readLine()) != null) readStr += str + "\n";
                br.close();
                Toast.makeText(this, readStr.substring(0, readStr.length()-1),
                        Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(v.getId() == R.id.button6){
            String path = getExternalPath();

            File file = new File(path + "mydiary");
            file.mkdir();

            String msg = "디렉터리 생성";
            if(file.isDirectory() == false) msg="디렉터리 생성 오류";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }else{
            String path = getExternalPath();

            File[] files = new File(path+"mydiary").listFiles();

            String str = "";
            for(File f:files)
                str += f.getName() + "\n";
            et.setText(str);
        }
    }

}
