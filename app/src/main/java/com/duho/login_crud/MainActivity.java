package com.duho.login_crud;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    Button btn_getData, btn_Remove, btn_Add, btn_Update;
    TextView tvDisplay;
    EditText txtEditText;
    ListView listView;
    List<String> _list;

    List<Student> studentArrayList;

    String url = "https://5fd4f2a566125e0016500048.mockapi.io/api/v1/user";
//    String url_Del = "https://5fd44806e9cda40016f5bd90.mockapi.io/Students/6";
    //String url = "http://5b7e85ceadf2070014bfa383.mockapi.io/users/21";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = (TextView) findViewById(R.id.tvDisplay);
        btn_getData = (Button) findViewById(R.id.btn_getData);
        btn_Remove = (Button) findViewById(R.id.btn_Remove);
        txtEditText = findViewById(R.id.txt_PutName);
        listView = findViewById(R.id.list_view);
        btn_Add = (Button) findViewById(R.id.btn_Add);
        btn_Update = (Button) findViewById(R.id.btn_update);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            txtEditText.setSelection(position);
        });
        //get all firstName
        btn_getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetArrayJson(url);
            }
        });
        //delete by id
        btn_Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteApi(url);
            }
        });

        // add put firstName && auto generation id, lastName, emailId
        btn_Add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PostApi(url);
                GetArrayJson(url);
            }
        });
        btn_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PutApi(url);
                GetArrayJson(url);
            }
        });



    }

    private void GetData(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                tvDisplay.setText(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error make by API server!", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    private void GetJson(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            tvDisplay.setText(response.getString("id").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error by get JsonObject...", Toast.LENGTH_SHORT).show();
            }
        }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
    private void GetArrayJson(String url){
        _list = new ArrayList<>();

        studentArrayList = new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length(); i++){
                    try {
                        JSONObject object = (JSONObject) response.get(i);
                      //  Toast.makeText(MainActivity.this, object.toString(), Toast.LENGTH_SHORT).show();
                        Student student = new Student();
                        student.setFirstName((object.getString("firstName").toString()));
                        System.out.println("ccccc" + student.toString());
                      _list.add(student.getFirstName().toString());
                        studentArrayList.add(student);
                        System.out.println(_list.size());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("List String");
                _list.forEach(x->{
                    System.out.println(x);
                });

                System.out.println(_list.size());
                System.out.println(studentArrayList.size());
                ArrayAdapter<String> _list_adapter = new ArrayAdapter<>
                        (MainActivity.this, android.R.layout.simple_list_item_1,_list);

                listView.setAdapter(_list_adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error by get Json Array!", Toast.LENGTH_SHORT).show();
            }
        });
//        _list.add("a");
//        _list.add("a");
//        _list.add("a");
//        _list.add("a");

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
    private void PostApi(String url){
        String name = txtEditText.getText().toString();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error by Post data!", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("firstName", name);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void PutApi(String url){
        String id = txtEditText.getText().toString();
        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                url + "/" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error by Put data!", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams()
                    throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("firstName", "updated__:))");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void DeleteApi(String url){

        StringRequest stringRequest = new StringRequest(

                Request.Method.DELETE, url + '/' + txtEditText.getText().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error by Post data!", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}