package com.public_dev.movil_transf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageView oimage;
    private TextView ocomment;
    private Button obtn_Attach;
    private Button obtn_Photo;
    private Button obtn_Send;
    private Button obtn_Exit;
    public static final int PICK_OPTION = 13;

    private static Context oContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Global.Init_Vars();

        Global.oActual_Context = null;
        Global.oActual_Context = this.getApplicationContext();
        oContext = this.getApplicationContext();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        Locale.setDefault(new Locale("en", "US"));

        oimage = (ImageView) findViewById(R.id.oImage);
        ocomment = (TextView) findViewById(R.id.oComment);
        obtn_Attach = (Button) findViewById(R.id.oBtn_Attach);
        obtn_Photo = (Button) findViewById(R.id.oBtn_Photo);
        obtn_Send = (Button) findViewById(R.id.oBtn_Send);
        obtn_Exit = (Button) findViewById(R.id.oBtn_Exit);


        this.obtn_Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Int_TakePhoto = new Intent(getApplicationContext(), take_photo.class);
                Int_TakePhoto.putExtra("show_image", 1);
                Int_TakePhoto.putExtra("pAction_Take", 0);
                startActivityForResult(Int_TakePhoto, PICK_OPTION);
            }
        });

        this.obtn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        obtn_Exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == PICK_OPTION) {
            try {
                oimage.setImageURI(Uri.parse(Global.cLastFullPathFilePhoto) );

                /*
                 */
            } catch (NullPointerException e) {
                e.printStackTrace();
                Global.oLastSelectedImageId = null;
                Global.cLastFilePhoto = "";
                Global.cLastFullPathFilePhoto = "";
            }
        }

    }

}
