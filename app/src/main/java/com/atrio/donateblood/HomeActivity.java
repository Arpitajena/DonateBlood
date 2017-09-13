package com.atrio.donateblood;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ImageView img_drop,img_bgdrop;
    Button btn_donate,btn_recive,btn_notify;
    String token,imsg_id;
    String[] permissions;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference db_ref;
    private FirebaseDatabase db_instance;
    private SpotsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        img_drop = (ImageView) findViewById(R.id.img_animation);
        img_bgdrop = (ImageView) findViewById(R.id.img_bgdrop);
        btn_notify = (Button) findViewById(R.id.btn_notify);
        btn_donate = (Button) findViewById(R.id.btn_doner);
        btn_recive = (Button) findViewById(R.id.btn_reciver);
        btn_donate.setVisibility(View.GONE);
        btn_recive.setVisibility(View.GONE);
        img_bgdrop.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Log.i("printUser11",""+user);   dialog = new SpotsDialog(HomeActivity.this, R.style.Custom);


        permissions = new String[]{
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,};
       // checkPermissions();
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.drop);

        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 5.2f);
//                TranslateAnimation(0, 0,0, Animation.RELATIVE_TO_PARENT);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(3000);  // animation duration
        animation.setFillAfter(true);
//        animation.setRepeatCount(2);  // animation repeat msg_id
//        animation.setRepeatMode(1);   // repeat animation (left to right, right to left )
//      animation.setFillAfter(true);

        img_drop.startAnimation(animation);
//        img_drop.setVisibility(View.INVISIBLE);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                img_drop.setVisibility(View.GONE);
                img_bgdrop.setVisibility(View.VISIBLE);
//                img_bgdrop.startAnimation(myAnim);
                btn_donate.setVisibility(View.VISIBLE);
                btn_donate.startAnimation(myAnim);
                btn_recive.setVisibility(View.VISIBLE);
                btn_recive.startAnimation(myAnim);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



        if (getIntent().getExtras() != null) {

            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                imsg_id= getIntent().getExtras().getString("msg_id");
                Log.i("other_detail26",""+imsg_id);

               /* if (key.equals("click_action") && value.equals("Notifiy_Activity")) {
                    Intent intent = new Intent(HomeActivity.this, NotifiyActivity.class);
                    intent.putExtra("msg_id", imsg_id);
                    startActivity(intent);
                    finish();
                }*/

            }
            
        }


/*
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                }  */
/*if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }*//*

            }
        };
*/

//        displayFirebaseRegId();
        btn_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ResigrationActivity.class);
                startActivity(intent);
            }
        });

        btn_recive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo == null) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                } else {
                    dialog.dismiss();
                    db_instance = FirebaseDatabase.getInstance();

                    db_ref = db_instance.getReference();

                    Query query_id = db_ref.child("Recipient").orderByKey().equalTo(user.getPhoneNumber());
                    query_id.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() != 0) {


                                Intent intent = new Intent(HomeActivity.this, RecipientActivity.class);
                                // intent.putExtra("tokenid",token);
                                startActivity(intent);


                            } else {

                                Intent intent = new Intent(HomeActivity.this, RecipentDetailsActivity.class);
                                // intent.putExtra("tokenid",token);
                                startActivity(intent);
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


            }
        });


        btn_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
//                intent.putExtra("tokenid",token);
                startActivity(intent);
            }
        });

    }

/*  if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");*//*

    }
*/




    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            return;
        }
    }

}
