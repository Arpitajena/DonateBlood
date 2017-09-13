package com.atrio.donateblood;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atrio.donateblood.model.RecipientDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotifiyActivity extends AppCompatActivity {
    TextView rec_tv;
    Button btn_yes,btn_no;
    private DatabaseReference db_ref;
    private FirebaseDatabase db_instance;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    OkHttpClient mClient;
    String state_data, blood_data, emailid, phoneno, date_req, city_data, other_detail,
            token_id,msg_id,imsg_id=null,message1,donor_phn,recipient_phn,city_donor,blood_group_donor,donor_msgid;

    public static final String MyPREFERENCES = "BloodDonate" ;
    public static final String city = "cityKey";
    public static final String state = "stateKey";
    public static final String blood_group = "blood_groupKey";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiy);
        rec_tv=(TextView)findViewById(R.id.tv_detail);
        btn_yes=(Button) findViewById(R.id.btn_no);
        btn_no=(Button) findViewById(R.id.btn_yes);

        mClient = new OkHttpClient();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        donor_phn = user.getPhoneNumber();

        sharedpreferences = getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        city_donor = sharedpreferences.getString(city,"");
        blood_group_donor = sharedpreferences.getString(blood_group,"");
        /*Log.i("city_donor44",""+city_donor);
        Log.i("blood_group_donor44",""+blood_group_donor);*/


        Log.i("phoneno2",""+donor_phn);

        if (getIntent().getExtras() != null) {
           /* for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.i("data88", "Key: " + key + " Value: " + value);

            }*/
            imsg_id= getIntent().getExtras().getString("msg_id");
                    Log.i("other_detail2",""+imsg_id);

            token_id= getIntent().getExtras().getString("token_id");
            blood_data = getIntent().getExtras().getString("token_id");
            recipient_phn = getIntent().getExtras().getString("pho_no");
            Log.i("recipient_phn26",""+recipient_phn);

        }
   /*     Intent intent = getIntent();
        imsg_id = intent.getStringExtra("msg_id");
        token_id = intent.getStringExtra("token_id");*/



            db_instance = FirebaseDatabase.getInstance();
            db_ref = db_instance.getReference();


            Query getnotifi=db_ref.child("Notifications").child("Recipient").child(city_donor).child(blood_group_donor).orderByChild("msg_id").equalTo(imsg_id);
            Log.i("other_query",""+getnotifi);


            getnotifi.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount() !=0) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Log.i("dataif",child.toString());
                            RecipientDetail recipientDetail = child.getValue(RecipientDetail.class);

                            date_req= recipientDetail.getReq_date();
                            emailid= recipientDetail.getEmailid();
                            phoneno=  recipientDetail.getPhoneno();
                            msg_id=  recipientDetail.getMsg_id();
                            other_detail=  recipientDetail.getOther_detail();
                            blood_data=   recipientDetail.getBloodgroup();
                            state_data=   recipientDetail.getState();
                            city_data=  recipientDetail.getCity();

                            String message = "There is requirement of blood group " + blood_data + " in "+city_data+ " on "+date_req+
                                    ".\n\n\nDetails of Recipient:\n\nEmail-Id:"+emailid+"\nPhone No: "+phoneno+"\nOther Details: "+other_detail;

                            rec_tv.setText(message);
//                        dialog.dismiss();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

//        Log.i("other_detail",""+other_detail);



        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(NotifiyActivity.this, "sent", Toast.LENGTH_SHORT).show();

                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            JSONObject root = new JSONObject();
                            JSONObject notification = new JSONObject();
              message1 = " I am willing to donate blood."+"\n"+" contact me"+"\n"+donor_phn;
                            notification.put("body", message1);
                            notification.put("title", "Response");
                            notification.put("icon", "myicon");
                            notification.put("click_action","Notifiy_Reciever");
//                    JSONObject message_id=new JSONObject();

                            JSONObject data = new JSONObject();
                            data.put("body",message1);
//
                            root.put("notification", notification);
                            root.put("data", data);
                            root.put("priority","high");
                            root.put("to",token_id);

                            Log.i("Messageid","" + root.toString());

                            String result = postToFCM(root.toString());
                            Log.i("Mainresult: ","" + result);

                            return result;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        Log.i("Mainresult:45689 ","" + result);


                        Query readqery = db_ref.child("Notifications").child("Donor").child(recipient_phn).orderByKey();
                        readqery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getChildrenCount()==0) {
                                    donor_msgid = "D001";
                                    sendDataToDatabase(donor_msgid);


                                }else {

                                    long countchild = dataSnapshot.getChildrenCount();
                                    countchild++;
                                    donor_msgid="D"+String.format("%03d",countchild);

                                    sendDataToDatabase(donor_msgid);

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                }.execute();
            }
        });

    }

    private void sendDataToDatabase(String donor_msgid) {

        RecipientDetail recipientDetail=new RecipientDetail();

        recipientDetail.setPhoneno(donor_phn);
        recipientDetail.setBody(message1);
        recipientDetail.setMsg_id(imsg_id);
        recipientDetail.setRec_phn(recipient_phn);
        recipientDetail.setBloodgroup(blood_group_donor);

        db_ref.child("Notifications").child("Donor").child(recipient_phn).child(donor_msgid).setValue(recipientDetail);

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

}
