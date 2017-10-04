package com.atrio.donateblood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

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
    ImageView img_drop,img_bgdrop;
    TextView tv_notiNo;
    Button btn_donate,btn_recive,btn_notify;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference db_ref;
    private FirebaseDatabase db_instance;
    private SpotsDialog dialog;
    String[] permissions;
    SharedPreferences sharedpreferences;
    long count=0;

    String city_donor=null,blood_group_donor=null,countnoti;
    public static final String MyPREFERENCES = "BloodDonate" ;
    public static final String city = "cityKey";
    public static final String state = "stateKey";
    public static final String blood_group = "blood_groupKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        img_drop = (ImageView) findViewById(R.id.img_animation);
        img_bgdrop = (ImageView) findViewById(R.id.img_bgdrop);
        tv_notiNo = (TextView) findViewById(R.id.tv_notiNo);
        btn_notify = (Button) findViewById(R.id.btn_notify);
        btn_donate = (Button) findViewById(R.id.btn_doner);
        btn_recive = (Button) findViewById(R.id.btn_reciver);
        btn_donate.setVisibility(View.GONE);
        btn_recive.setVisibility(View.GONE);
        img_bgdrop.setVisibility(View.GONE);
        permissions = new String[]{
                android.Manifest.permission.CALL_PHONE,
        };
        checkPermissions();
        db_instance = FirebaseDatabase.getInstance();
        db_ref = db_instance.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dialog = new SpotsDialog(HomeActivity.this, R.style.Custom);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.drop);

        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 5.2f);
        animation.setDuration(3000);  // animation duration
        animation.setFillAfter(true);
        img_drop.startAnimation(animation);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        city_donor = sharedpreferences.getString(city, "");
        blood_group_donor = sharedpreferences.getString(blood_group, "");
        Log.i("data45",""+city_donor+blood_group_donor);
        countnoti= String.valueOf(count);
        tv_notiNo.setVisibility(View.GONE);
        tv_notiNo.setText(String.valueOf(count));



//        Query query_noticount = db_ref.child("Notifications").child("Recipient").child(city_donor).orderByChild(blood_group_donor).limitToLast(5);
        Log.i("data415",""+city_donor+blood_group_donor);


/*
        query_noticount.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                count=dataSnapshot.getChildrenCount();
                tv_notiNo.setText(String.valueOf(count));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                count=dataSnapshot.getChildrenCount();
                tv_notiNo.setText(String.valueOf(count));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tv_notiNo.setText(String.valueOf(count));
            }
        });
*/
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                img_drop.setVisibility(View.GONE);
                img_bgdrop.setVisibility(View.VISIBLE);
                btn_donate.setVisibility(View.VISIBLE);
                btn_donate.startAnimation(myAnim);
                btn_recive.setVisibility(View.VISIBLE);
                btn_recive.startAnimation(myAnim);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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

                    Query query_id = db_ref.child("Recipient").orderByKey().equalTo(user.getPhoneNumber());
                    query_id.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() != 0) {
                                dialog.dismiss();
                                try {
                                    Intent intent = new Intent(HomeActivity.this, RecipientActivity.class);
                                    startActivity(intent);
                                }catch (Exception e){

                                }

                            } else {
                                dialog.dismiss();
                                Intent intent = new Intent(HomeActivity.this, RecipentDetailsActivity.class);
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
                startActivity(intent);
            }
        });
    }
    private boolean checkPermissions() {
//        Log.i("permissioncheck1",""+checkPermissions());

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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*Log.i("permissioncheck","permission_granted");*/
            }
            return;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
