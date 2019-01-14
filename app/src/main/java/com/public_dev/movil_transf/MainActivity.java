package com.public_dev.movil_transf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private ImageView oimage;
    private TextView ocomment;
    private Button obtn_Attach;
    private Button obtn_Photo;
    private Button obtn_Send;
    private Button obtn_Exit;
    public static final int PICK_OPTION = 13;
    private int PICK_IMAGE_REQUEST = 1;

    private static Context oContext;
    private Uri oFileUri;
    private Bitmap oBitmap;

    public static final String UPLOAD_URL = "http://201.218.103.202/flam/upload.php";

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

        this.obtn_Attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione la imagen"), PICK_IMAGE_REQUEST);
            }
        });

        this.obtn_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();
            }
        });

        this.obtn_Exit.setOnClickListener(new View.OnClickListener() {
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
        if (requestCode == PICK_OPTION && resultCode == RESULT_OK && data != null && data.getData() != null) {

            try {
                oimage.setImageURI(Uri.parse(Global.cLastFullPathFilePhoto));

            } catch (NullPointerException e) {
                Global.oLastSelectedImageId = null;
                Global.cLastFilePhoto = "";
                Global.cLastFullPathFilePhoto = "";
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            this.oFileUri = data.getData();
            try {
                oBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), this.oFileUri);
                oimage.setImageBitmap(oBitmap);

                GetInfoFromUri(this, this.oFileUri );

            } catch (IOException e) {
                Global.oLastSelectedImageId = null;
                Global.cLastFilePhoto = "";
                Global.cLastFullPathFilePhoto = "";
                e.printStackTrace();
            }
        }
    }

    public void GetInfoFromUri(Context oContext, Uri oInfo){
        String cRealPath;
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11)
            cRealPath = RealPathUtil.getRealPathFromURI_BelowAPI11(oContext, oInfo);
            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT <= 19)
            cRealPath = RealPathUtil.getRealPathFromURI_API11to18(oContext, oInfo);
            // SDK > 19 (Android 4.4)
        else
            cRealPath = RealPathUtil.getRealPathFromURI_API19(oContext, oInfo);

        Global.cLastFullPathFilePhoto = cRealPath;
        Global.oLastSelectedImageId = oInfo;

        File file = new File(cRealPath);
        Global.cLastFilePhoto = file.getName();
        file = null;

    };

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Subiendo Imagen...", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String, String> data = new HashMap<>();

                data.put("db_name", "one2009_1");
                data.put("num_soc", "001");
                data.put("img_blo", uploadImage);
                data.put("fle_nam", Global.cLastFilePhoto);
                data.put("detalle", ocomment.getText().toString().trim());


                String result = rh.sendPostRequest(UPLOAD_URL, data);


                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(oBitmap);
    }

}
