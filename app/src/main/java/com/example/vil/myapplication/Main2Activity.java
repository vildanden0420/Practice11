package com.example.vil.myapplication;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
    String date = "";
    Button btnsave;
    int pos;
    String path;


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
        //Toast.makeText(getApplicationContext(), sdPath, Toast.LENGTH_SHORT).show();
        return sdPath;
    }

    private void checkPermission() {
        int permissioninfo = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissioninfo == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),
                    "쓰기권한 있음", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "권한 설명", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SD Card 쓰기권한 승인", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SD Card 쓰기권한 거부", Toast.LENGTH_SHORT).show();
            }

        }
    }

    void init() {
        linear1 = (LinearLayout) findViewById(R.id.linear1);
        linear2 = (LinearLayout) findViewById(R.id.linear2);
        listView = (ListView) findViewById(R.id.listview);
        et = (EditText) findViewById(R.id.editText);
        count = (TextView) findViewById(R.id.tvCount);
        btnsave = (Button) findViewById(R.id.btnsave);

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        data = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
        path = getExternalPath();


        final String path = getExternalPath();

        File file = new File(path + "mydiary");
        file.mkdir();
        File list[] = file.listFiles();

        for (int i = 0; i < list.length; i++) {
            data.add(list[i].getName());
        }

        adapter.notifyDataSetChanged();
        setCount();


//        String msg = "디렉터리 생성";
//        if(file.isDirectory() == false) msg="디렉터리 생성 오류";
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                linear1.setVisibility(View.GONE);
                linear2.setVisibility(View.VISIBLE);
                btnsave.setText("수정");
                pos = position;

                try {
                    String path = getExternalPath();
                    String filename = data.get(position);
                    //Toast.makeText(getApplication(), data.get(position), Toast.LENGTH_SHORT).show();

                    BufferedReader br = new BufferedReader(new FileReader(path + "mydiary/" + filename));
                    String readStr = "";
                    String str = null;
                    while ((str = br.readLine()) != null) readStr += str + "\n";
                    br.close();
                    et.setText(readStr);


                    String[] days = data.get(position).replace(".memo", "").split("-");
                    String year = "20" + days[0];
                    String month = days[1];
                    String day = days[2];
                    if (days[1].startsWith("0")) {
                        month = days[1].substring(1);
                    }

                    if (days[2].startsWith("0")) {
                        day = days[2].substring(1);
                    }

                    int setYear = Integer.parseInt(year);
                    int setMonth = Integer.parseInt(month);
                    int setDay = Integer.parseInt(day);


                    datePicker.updateDate(setYear, setMonth - 1, setDay);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //btnsave.setText("저장");

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Main2Activity.this);
                dlg.setMessage("삭제하시겠습니까?").setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                        String filename = data.get(position);
                        File file = new File(path + "mydiary/" + filename);
                        file.delete();
                        data.remove(position);
                        adapter.notifyDataSetChanged();
                        setCount();

                    }
                }).setPositiveButton("취소", null).show();


                return true;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick(View v) {
        if (v.getId() == R.id.btn1) {
            linear1.setVisibility(View.GONE);
            linear2.setVisibility(View.VISIBLE);
            btnsave.setText("저장");
            et.setText("");
            clearDate();

            //datePicker.init(DatePicker.OnDateChangedListener);


        } else if (v.getId() == R.id.btnsave) {


            int year = datePicker.getYear();
            int month = datePicker.getMonth() + 1;
            int day = datePicker.getDayOfMonth();
            String year_s = String.valueOf(year).substring(2);
            String month_s = "";
            String day_s = "";
            if (month < 10) {
                month_s = "0" + month;
            } else {
                month_s = String.valueOf(month);
            }

            if (day < 10) {
                day_s = "0" + day;
            } else {
                day_s = String.valueOf(day);
            }

            Boolean exist = false;
            int i_pos = 0;

            date = year_s + "-" + month_s + "-" + day_s;

            if(btnsave.getText().toString().equals("저장")) {
                for (int i = 0; i < data.size(); i++) {
                    if ((date + ".memo").equals(data.get(i))) {
                        exist = true;
                        i_pos = i;
                    }
                }
            }


            if (exist) {
                try {
                    btnsave.setText("수정");
                    BufferedReader br = new BufferedReader(new FileReader(path + "mydiary/" + data.get(i_pos)));
                    String readStr = "";
                    String str = null;
                    while ((str = br.readLine()) != null) readStr += str + "\n";

                    br.close();
                    et.setText(readStr + et.getText().toString());

//                    String filename = data.get(i_pos);
//                    File file = new File(path + "mydiary/" + filename);
//                    file.delete();
//
//                    data.remove(i_pos);
//                    adapter.notifyDataSetChanged();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                if (btnsave.getText().toString().equals("수정")) {
                    //String path = getExternalPath();
                    String filename = data.get(i_pos);
                    File file = new File(path + "mydiary/" + filename);
                    file.delete();

                    //et.setText("");

                    data.remove(i_pos);
                    adapter.notifyDataSetChanged();

                }
                try {
                    String path = getExternalPath();
                    String filename = date + ".memo";
                    BufferedWriter bw = null;
                    bw = new BufferedWriter(new FileWriter(path + "mydiary/" + filename, true));
                    bw.write(et.getText().toString());
                    bw.close();
                    Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
                    data.add(filename);
                    adapter.notifyDataSetChanged();
                    et.setText("");
                    setCount();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, e.getMessage() + ":" + getFilesDir(), Toast.LENGTH_SHORT).show();
                }

                linear1.setVisibility(View.VISIBLE);
                linear2.setVisibility(View.GONE);

            }


            clearDate();

        } else {
            linear1.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.GONE);
            btnsave.setText("저장");
        }
    }

    public void setCount() {
        count.setText("등록된 메모 개수: " + data.size());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clearDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePicker.updateDate(year, month, day);
    }
}
