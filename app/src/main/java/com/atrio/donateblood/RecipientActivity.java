package com.atrio.donateblood;

import android.app.DatePickerDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.atrio.donateblood.model.RecipientDetail;
import com.atrio.donateblood.model.UserDetail;
import com.atrio.donateblood.sendmail.SendMail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecipientActivity extends AppCompatActivity {
    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
    Spinner spin_state, sp_bloodgr;
    Button btn_send;
    EditText et_phoneno, et_emailid, et_date, et_remark;
    String state_data, blood_data, emailid, phoneno, date_req, city_data, other_detail, send_mail, regId,msg_id,message1,
            condition =null;
    private DatabaseReference db_ref;
    private FirebaseDatabase db_instance;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private SpotsDialog dialog;
    ArrayList<String> store_list;
    OkHttpClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dialog = new SpotsDialog(RecipientActivity.this, R.style.Custom);
        spin_state = (Spinner) findViewById(R.id.spin_state);
        sp_bloodgr = (Spinner) findViewById(R.id.spin_bloodGrp);
        btn_send = (Button) findViewById(R.id.bt_reg);
        et_phoneno = (EditText) findViewById(R.id.input_phoneno);
        et_emailid = (EditText) findViewById(R.id.input_email);
        et_date = (EditText) findViewById(R.id.input_date);
        et_remark = (EditText) findViewById(R.id.et_remark);

        et_phoneno.setEnabled(false);
        et_phoneno.setText(user.getPhoneNumber());
        atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        atvPlaces.setThreshold(1);
        store_list = new ArrayList<>();
        regId = FirebaseInstanceId.getInstance().getToken();
        mClient = new OkHttpClient();
        sp_bloodgr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                blood_data = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                DatePickerDialog datePickerDialog = new DatePickerDialog(RecipientActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                et_date.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });
        spin_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state_data = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (state_data.equals("Select Your State")) {
                    Toast.makeText(getApplicationContext(), "Select Your state", Toast.LENGTH_LONG).show();
                } else {
                    placesTask = new PlacesTask();
                    placesTask.execute(s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        db_instance = FirebaseDatabase.getInstance();
        db_ref = db_instance.getReference();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialog.show();
                ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo == null) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                } else {
                    dialog.dismiss();
                    if (validate()) {
                        phoneno = et_phoneno.getText().toString();
                        emailid = et_emailid.getText().toString();
                        city_data = atvPlaces.getText().toString().toLowerCase();
                        date_req = et_date.getText().toString();
                        other_detail = et_remark.getText().toString();
                        dialog.show();
                        message1 = "There is requirement of blood group " + blood_data + " in " + city_data + " on " + date_req;

                        Query readqery = db_ref.child("Donor").child(state_data).child(city_data).orderByChild("bloodgroup").equalTo(blood_data);

                        readqery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() == 0) {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "No Donor Available", Toast.LENGTH_LONG).show();
                                } else {
                                    Iterator<DataSnapshot> item = dataSnapshot.getChildren().iterator();
                                    while (item.hasNext()) {
                                        DataSnapshot items = item.next();
                                        UserDetail user_info = items.getValue(UserDetail.class);
                                        send_mail = user_info.getEmailid();
                                        store_list.add(send_mail);
                                    }
                                  dialog.dismiss();
                                    Query readqery = db_ref.child("Notifications").child("Recipient").child(city_data).child(blood_data).orderByKey();
                                    readqery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.getChildrenCount()==0) {
                                                msg_id = "001";
                                                createRecipientDetail(state_data, blood_data, emailid, phoneno, date_req, city_data, other_detail, msg_id, message1);
                                            }else {
                                                long countchild = dataSnapshot.getChildrenCount();
                                                countchild++;
                                                msg_id=String.format("%03d",countchild);
                                                createRecipientDetail(state_data, blood_data, emailid, phoneno, date_req, city_data, other_detail, msg_id, message1);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    Toast.makeText(RecipientActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    public void sendNotificationToUser(final String regId) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {

                    String topic_grpAp,topic_grpAn,topic_grpBp,topic_grpBn,topic_grpABp,topic_grpABn,topic_grpOp,topic_grpOn;
                    String topic_positive = null,topic_negative = null,topic;
                    String groupFirst = blood_data.substring(0,blood_data.length()-1);
                    String grouplast = blood_data.substring(blood_data.length()-1);
                    String data1;

                    switch (blood_data){
                        case "A+":
                            topic_grpAp = state_data.replace(" ","")+"A"+"positive";
                            topic_grpAn = state_data.replace(" ","")+"A"+"negative";
                            topic_grpOp = state_data.replace(" ","")+"O"+"positive";
                            topic_grpOn = state_data.replace(" ","")+"O"+"negative";
                            data1 ="1";
                            conditionTopic(topic_grpAp,topic_grpAn,null,null,topic_grpOp,topic_grpOn,null,null,data1);
                            break;
                        case "O+":
                            topic_grpOp = state_data.replace(" ","")+"O"+"positive";
                            topic_grpOn = state_data.replace(" ","")+"O"+"negative";
                            data1 ="2";
                            conditionTopic(null,null,null,null,topic_grpOp,topic_grpOn,null,null, data1);
                            break;
                        case "B+":
                            topic_grpBp = state_data.replace(" ","")+"B"+"positive";
                            topic_grpBn = state_data.replace(" ","")+"B"+"negative";
                            topic_grpOp = state_data.replace(" ","")+"O"+"positive";
                            topic_grpOn = state_data.replace(" ","")+"O"+"negative";
                            data1 ="3";
                            conditionTopic(null,null,topic_grpBp,topic_grpBn,topic_grpOp,topic_grpOn,null,null, data1);

                            break;
                        case "AB+":
                            topic_grpAp = state_data.replace(" ","")+"A"+"positive";
                            topic_grpAn = state_data.replace(" ","")+"A"+"negative";
                            topic_grpBp = state_data.replace(" ","")+"B"+"positive";
                            topic_grpBn = state_data.replace(" ","")+"B"+"negative";
                            topic_grpOp = state_data.replace(" ","")+"O"+"positive";
                            topic_grpOn = state_data.replace(" ","")+"O"+"negative";
                            topic_grpABp = state_data.replace(" ","")+"AB"+"positive";
                            topic_grpABn = state_data.replace(" ","")+"AB"+"negative";
                            data1 ="4";
                            conditionTopic(topic_grpAp,topic_grpAn,topic_grpBp,topic_grpBn,topic_grpOp,topic_grpOn,topic_grpABp,topic_grpABn, data1);
                            break;
                        case "A-":
                            topic_grpAn = state_data.replace(" ","")+"A"+"negative";
                            topic_grpOn = state_data.replace(" ","")+"O"+"negative";
                            data1 ="5";
                            conditionTopic(null,topic_grpAn,null,null,null,topic_grpOn,null,null, data1);

                            break;
                        case "O-":
                            topic_grpOn = state_data.replace(" ","")+"O"+"negative";
                            data1 ="6";
                            conditionTopic(null,null,null,null,null,topic_grpOn,null,null, data1);

                            break;
                        case "B-":
                            topic_grpBn = state_data.replace(" ","")+"B"+"negative";
                            topic_grpOn = state_data.replace(" ","")+"O"+"negative";
                            data1 ="7";
                            conditionTopic(null,null,null,topic_grpBn,null,topic_grpOn,null,null, data1);

                            break;
                        case "AB-":
                            topic_grpABn = state_data.replace(" ","")+"AB"+"negative";
                            topic_grpAn = state_data.replace(" ","")+"A"+"negative";
                            topic_grpBn = state_data.replace(" ","")+"B"+"negative";
                            topic_grpOn = state_data.replace(" ","")+"O"+"negative";
                            data1 ="8";
                            conditionTopic(null,topic_grpAn,null,topic_grpBn,null,topic_grpOn,null,topic_grpABn, data1);
                            break;
                    }




                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();

                    notification.put("body", message1);
                    notification.put("title", "Donate Blood");
                    notification.put("icon", "http://res.cloudinary.com/ddky6bjui/image/upload/v1505451080/ic_stat_ic_notification_qcawdk.png");
                    notification.put("click_action","Notifiy_Activity");

                    JSONObject data = new JSONObject();
                    data.put("token_id", regId);
                    data.put("msg_id", msg_id);
                    data.put("pho_no",phoneno);
                    root.put("notification", notification);
                    root.put("data", data);
                    root.put("priority","high");
                    /*root.put("to","/topics/"+topic_negative);*/
                    root.put("condition",condition);




                    String result = postToFCM(root.toString());
                    Log.i("result55",root.toString());
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                        sendmail(store_list);
            }
        }.execute();
    }

    private void conditionTopic(String topic_grpAp, String topic_grpAn, String topic_grpBp, String topic_grpBn, String topic_grpOp,
                                String topic_grpOn, String topic_grpABp, String topic_grpABn, String data) {

        Log.i("data11",""+data);

        if ((data.equals("1"))) {
            condition = topic_grpAp + " in topics && " + topic_grpAn + " in topics && " + topic_grpOp
                    + " in topics && " + topic_grpOn + " in topics";
            Log.i("cond33", "" + condition);
        }
            if (data.equals("2")) {
                condition = topic_grpOp + " in topics && " + topic_grpOn + " in topics";
                Log.i("cond33", "" + condition);
            }if (data.equals("3")){
                    condition = topic_grpBp +" in topics && "+ topic_grpBn+" in topics && "+topic_grpOp +" in topics && "+ topic_grpOn+" in topics";
                    Log.i("cond33",""+condition);
                }if(data.equals("4")){

                    condition = topic_grpAp +" in topics && "+ topic_grpAn+" in topics"+topic_grpBp +" in topics && "+ topic_grpBn+" in topics"
                   + topic_grpOp +" in topics && "+ topic_grpOn+" in topics && "+topic_grpABp +" in topics && "+ topic_grpABn+" in topics" ;
                    Log.i("cond33",""+condition);

                }
                if (data.equals("5") ) {
                    condition = topic_grpAn +" in topics && "+ topic_grpOn+" in topics";
                    Log.i("cond33",""+condition);
                }
                if (data.equals("6")){
                    condition = topic_grpOn +" in topics";
                    Log.i("cond33",""+condition);

                }
                if (data.equals("7")){
                    condition = topic_grpBn +" in topics && "+ topic_grpOn+" in topics";
                    Log.i("cond33",""+condition);
                }if(data.equals("8")){
                    condition = topic_grpABn +" in topics && "+ topic_grpAn+" in topics && "+topic_grpBn +" in topics && "+ topic_grpAn+" in topics";
                    Log.i("cond33",""+condition);

                }





    }


    String postToFCM(String bodyString) throws IOException {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("Authorization", "key=AIzaSyB0xP6z55MHoQJkx2uK6rgbXcuYouBNPXM")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    private void createRecipientDetail(String state_data, String blood_data, String emailid, String phoneno, String date_req, String city_data, String other_detail, String msg_id,String body) {
        RecipientDetail recipientDetail=new RecipientDetail();

        recipientDetail.setReq_date(date_req);
        recipientDetail.setEmailid(emailid);
        recipientDetail.setPhoneno(phoneno);
        recipientDetail.setMsg_id(msg_id);
        recipientDetail.setOther_detail(other_detail);
        recipientDetail.setBloodgroup(blood_data);
        recipientDetail.setState(state_data);
        recipientDetail.setCity(city_data);
        recipientDetail.setBody(body);

//        db_ref.child("RecipientNotification").child("Recipient").child(city_data).child(blood_data).child(phoneno).child(msg_id).setValue(recipientDetail);
        db_ref.child("Notifications").child("Recipient").child(city_data).child(blood_data).child(msg_id).setValue(recipientDetail);
        sendNotificationToUser(regId);
    }

    private void sendmail(final ArrayList<String> store_list) {

        String email = "info@atriodata.com";
        String mail_subject = "Blood Required";
        String message = "There is requirement of blood group " + blood_data + " in " + city_data + " on " + date_req +
                ".\n\n\nDetails of Recipient:\n\nEmail-Id:" + emailid + "\nPhone No: " + phoneno + "\nOther Details: " + other_detail;
        SendMail sm = new SendMail(this, email, mail_subject, message, store_list);
        sm.execute();

        et_date.setText("");
        et_emailid.setText("");
        atvPlaces.setText("");

            et_remark.setText("");
        sp_bloodgr.setSelection(0);
        spin_state.setSelection(0);
        dialog.dismiss();
    }


    private boolean validate() {
        if (blood_data.equals("Select Your Blood Group")) {
            Toast.makeText(getApplicationContext(), "Select Your Blood Group", Toast.LENGTH_LONG).show();
            return false;

        } else if (et_date.getText().toString().trim().length() < 1) {
            et_date.setError("Please Fill This Field");
            et_date.requestFocus();
            return false;

        } else if (state_data.equals("Select Your State")) {
            Toast.makeText(getApplicationContext(), "Select Your state", Toast.LENGTH_LONG).show();
            return false;

        } else if (atvPlaces.getText().toString().trim().length() < 1) {
            atvPlaces.setError("Please Fill This Field");
            atvPlaces.requestFocus();
            return false;

        } else if (et_emailid.getText().toString().trim().length() < 1 || isEmailValid(et_emailid.getText().toString()) == false) {
            et_emailid.setError("Invalid Email Address");
            et_emailid.requestFocus();
            return false;

        } else if (et_phoneno.getText().toString().trim().length() < 1 ) {
            et_phoneno.setError("Please Fill This Field");
            et_phoneno.requestFocus();
            return false;
        } else if (et_remark.getText().toString().trim().length() < 1) {
            et_remark.setError("Please Fill This Field");
            et_remark.requestFocus();
            return false;

        } else
            return true;

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }

    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            String data = "";
            String key = "key=AIzaSyAG-AdjAgToyXceK6-ghWS38ho8cALPaUw";
            String input = "";
            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            String types = "types=(cities)";
            String sensor = "sensor=false";
            String parameters = input + "&" + types + "&" + sensor + "&" + key;
            String output = "json";
            String url =
                    "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters + "&components=country:IN";
            try {
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser(state_data);
            try {
                jObject = new JSONObject(jsonData[0]);
                places = placeJsonParser.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
            atvPlaces.setAdapter(adapter);
            synchronized (adapter) {
                adapter.notifyDataSetChanged();
            }
        }
    }

}

