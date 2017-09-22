package com.atrio.donateblood.sendmail;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by Arpita Patel on 03-04-2017.
 */

public class SendMail extends AsyncTask<Void, Void, Void> {

    private Context context;
    private Session session;
    private String email;
    private String mail_subject;
    private String message;
    private ArrayList<String> my2mail;
    private ProgressDialog progressDialog;

    public SendMail(Context context, String email, String mail_subject, String message, ArrayList<String> my2mail) {
        this.context = context;
        this.email = email;
        this.mail_subject = mail_subject;
        this.message = message;
        this.my2mail = my2mail;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Sending message", "Please wait...", false, false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
//        props.put("mail.smtp.from", "info@atriodata.com");

//        props.put("mail.smtp.host", "webmail.atriodata.com");
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.port", "465");
        Log.i("checkmail",""+props);

        session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            //Authenticating the password
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
            }
        });
        try {
            MimeMessage mm = new MimeMessage(session);
//            mm.setFrom(new InternetAddress("Blood Donate" + "<" + "no-reply@domain.com" + ">"));
//            mm.setFrom(new InternetAddress("BloodDonate","info@atriodata.com"));
//            MimeUtility.encodeText(Config.EMAIL);
//            mm.setReplyTo(new InternetAddress[] { new InternetAddress("info@atriodata.com") });
//            mm.setSender("info@atriodata.com");
//            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            for (int i = 0; i < my2mail.size(); i++) {
                mm.addRecipient(Message.RecipientType.BCC, new InternetAddress(my2mail.get(i)));
            }


            mm.setSubject(mail_subject);
            mm.setText(message);
            Log.i("checkmailfrom",""+mm);
            Transport.send(mm);
            Log.i("checkmailfrom1",""+mm);
        } catch (MessagingException e) {
            e.printStackTrace();
            Log.i("checkmailfromexception",""+e);

        } /*catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.i("checkmailfromexception1",""+e);

        }*/
        return null;
    }
}
