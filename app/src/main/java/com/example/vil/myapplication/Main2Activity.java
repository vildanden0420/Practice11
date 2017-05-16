package com.example.vil.myapplication;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {

    LinearLayout linear1, linear2;
    EditText et;
    TextView count;
    ListView listView;
    ArrayList<String> data;
    ArrayAdapter<String> adapter;
    DatePicker datePicker;
    String date="";
    Button btnsave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setTitle("내맘대로 메모장");

        checkPermission();
        init();

    }
    public String getExternalPath() {
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        } else
            sdPath = getFilesDir() + "";
        Toast.makeText(getApplicationContext(), sdPath, Toast.LENGTH_SHORT).show();
        return sdPath;
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

    void init(){
        linear1 = (LinearLayout)findViewById(R.id.linear1);
        linear2 = (LinearLayout)findViewById(R.id.linear2);
        listView = (ListView)findViewById(R.id.listview);
        et = (EditText)findViewById(R.id.editText);
        count = (TextView)findViewById(R.id.tvCount);
        btnsave = (Button)findViewById(R.id.btnsave);

        datePicker = (DatePicker)findViewById(R.id.datePicker);
        data = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);



        String path = getExternalPath();

        File file = new File(path + "mydiary");
        file.mkdir();

//        String msg = "디렉터리 생성";
//        if(file.isDirectory() == false) msg="디렉터리 생성 오류";
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                linear1.setVisibility(View.GONE);
                linear2.setVisibility(View.VISIBLE);
                btnsave.setText("수정");

            }
        });
    }

    public void onClick(View v){
        if(v.getId() == R.id.btn1){
            linear1.setVisibility(View.GONE);
            linear2.setVisibility(View.VISIBLE);
            

            datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                    new DatePicker.OnDateChangedListener() {


                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            date = String.format("%d-%d-%d", year, monthOfYear+1, dayOfMonth);
                        }
                    });

        }else if(v.getId() == R.id.btnsave){
            linear1.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.GONE);

            try {
                String path = getExternalPath();
                String filename = date+".memo";
                BufferedWriter bw = null;
                bw = new BufferedWriter(new FileWriter(path +"mydiary/"+filename, true));
                bw.write(et.getText().toString());
                bw.close();
                Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
                data.add(filename);
                adapter.notifyDataSetChanged();
                et.setText("");
                count.setText("등록된 메모 개수: "+data.size());

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage()+":"+getFilesDir(), Toast.LENGTH_SHORT).show();
            }

        }else {
            linear1.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.GONE);
        }
    }
}
