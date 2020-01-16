package com.hudzah.snapapaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraX;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.view.TextureViewMeteringPointFactory;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;


import static com.hudzah.snapapaper.R.drawable.cameraon;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int REQUEST_CODE_PERMISSIONS = 101;

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.INTERNET"};

    TextureView textureView;

    Preview preview;

    Button torchButton;

    File file;

    ImageCapture imgCap;

    String codeText;

    LoadingDialog loadingDialog;

    ImageView cameraImage;

    String[] splitText;

    String paperCode;

    Map<String, String> examCodesMap = new HashMap<String, String>();

    String examLevel;

    String pdfUrl;

    String pdfUrlMs;

    String paperCodeMs;

    ListAdapter adapter;

    AlertDialog.Builder choiceBuilder;

    Boolean isQp;

    DrawerLayout drawerLayout;


    public void torchAction(View view){


        if(preview.isTorchOn() != true){

            preview.enableTorch(true);

            torchButton = (Button)findViewById(R.id.torchButton);

            //int id = getResources().getIdentifier("yourpackagename:drawable/" + StringGenerated, null, null);

            torchButton.setBackgroundResource(R.drawable.flashon);
        }
        else{

            preview.enableTorch(false);
            torchButton.setBackgroundResource(R.drawable.flashoff);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingDialog = new LoadingDialog(MainActivity.this);

        cameraImage = (ImageView)findViewById(R.id.imgCapture);

        textureView = findViewById(R.id.view_finder);

        paperCode = "";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar , R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        toolbar.setNavigationIcon(R.drawable.whitemenuverysmall);


        examCodesMap.put("7707" , "Accounting (7707)");
        examCodesMap.put("7707" ,"Accounting (7707)");
        examCodesMap.put("4037","Add-Maths (4037)");
        examCodesMap.put("5038","Agriculture (5038)");
        examCodesMap.put("3180","Arabic (3180)");
        examCodesMap.put("6090","Art and Design (BD, MV, MU, PK) (6090)");
        examCodesMap.put("7094","Bangladesh Studies (7094)");
        examCodesMap.put("3204","Bengali (3204)");
        examCodesMap.put("5090","Biology (5090)");
        examCodesMap.put("7115","Business studies (7115)");
        examCodesMap.put("7048","CDT Design and Communication (7048)");
        examCodesMap.put("5070","Chemistry (5070)");
        examCodesMap.put("7100","Commerce (7100)");
        examCodesMap.put("7101","Commercial Studies (7101)");
        examCodesMap.put("2210","Computer Science (2210)");
        examCodesMap.put("7010","Computer Studies (7010)");
        examCodesMap.put("4024","D-Maths (4024)");
        examCodesMap.put("6043","Design and Technology (6043)");
        examCodesMap.put("2281","Economics (2281)");
        examCodesMap.put("1123","English (1123)");
        examCodesMap.put("5014","Environmental Management (5014)");
        examCodesMap.put("6130","Fashion and Textiles (6130)");
        examCodesMap.put("6065","Food and Nutrition (6065)");
        examCodesMap.put("3015","French (3015)");
        examCodesMap.put("2217","Geography (2217)");
        examCodesMap.put("3025","German (3025)");
        examCodesMap.put("2069","Global Perspectives (2069)");
        examCodesMap.put("2055","Hinduism (2055)");
        examCodesMap.put("2134","History (Modern World Affairs) (2134)");
        examCodesMap.put("2158","History World Affairs, 1917-1991 (2158)");
        examCodesMap.put("2056","Islamic Religion and Culture (2056)");
        examCodesMap.put("2068","Islamic Studies (2068)");
        examCodesMap.put("2058","Islamiyat (2058)");
        examCodesMap.put("2010","Literature in English (2010)");
        examCodesMap.put("5180","Marine Science (5180)");
        examCodesMap.put("3202","Nepali (3202)");
        examCodesMap.put("2059","Pakistan Studies (2059)");
        examCodesMap.put("5054","Physics (5054)");
        examCodesMap.put("7110","Principles of Accounts (7110)");
        examCodesMap.put("2048","Religious Studies (2048)");
        examCodesMap.put("5129","Science - Combined (5129)");
        examCodesMap.put("3158","Setswana (3158)");
        examCodesMap.put("3205","Sinhala (3205)");
        examCodesMap.put("2251","Sociology (2251)");
        examCodesMap.put("3035","Spanish (3035)");
        examCodesMap.put("4040","Statistics (4040)");
        examCodesMap.put("3162","Swahili (3162)");
        examCodesMap.put("3226","Tamil (3226)");
        examCodesMap.put("7096","Travel and Tourism (7096)");
        examCodesMap.put("3247","Urdu - First Language (3247)");
        examCodesMap.put("3248","Urdu - Second Language (3248)");
        examCodesMap.put("9706","Accounting (9706)");
        examCodesMap.put("9679","Afrikaans (9679)");
        examCodesMap.put("8779","Afrikaans - First Language (AS Level only) (8779)");
        examCodesMap.put("8679","Afrikaans - Language (AS Level only) (8679)");
        examCodesMap.put("9713","Applied Information and Communication Technology (9713)");
        examCodesMap.put("9680","Arabic (9680)");
        examCodesMap.put("8680","Arabic - Language (AS Level only) (8680)");
        examCodesMap.put("9479","Art & Design (9479)");
        examCodesMap.put("9704","Art & Design (9704)");
        examCodesMap.put("9700","Biology (9700)");
        examCodesMap.put("9609","Business (for first examination in 2016) (9609)");
        examCodesMap.put("9707","Business Studies (9707)");
        examCodesMap.put("9980","Cambridge International Project Qualification (9980)");
        examCodesMap.put("9701","Chemistry (9701)");
        examCodesMap.put("9715","Chinese (A Level only) (9715)");
        examCodesMap.put("8681","Chinese - Language (AS Level only) (8681)");
        examCodesMap.put("9274","Classical Studies (9274)");
        examCodesMap.put("9608","Computer Science (for final examination in 2021) (9608)");
        examCodesMap.put("9618","Computer Science (for first examination in 2021) (9618)");
        examCodesMap.put("9691","Computing (9691)");
        examCodesMap.put("9631","Design & Textiles (9631)");
        examCodesMap.put("9705","Design and Technology (9705)");
        examCodesMap.put("9481","Digital Media & Design (9481)");
        examCodesMap.put("9011","Divinity (9011)");
        examCodesMap.put("8041","Divinity (AS Level only) (8041)");
        examCodesMap.put("9708","Economics (9708)");
        examCodesMap.put("9093","English - Language AS and A Level (9093)");
        examCodesMap.put("8695","English - Language and Literature (AS Level only) (8695)");
        examCodesMap.put("9695","English - Literature (9695)");
        examCodesMap.put("8021","English General Paper (AS Level only) (8021)");
        examCodesMap.put("8291","Environmental Management (AS only) (8291)");
        examCodesMap.put("9336","Food Studies (9336)");
        examCodesMap.put("9716","French (A Level only) (9716)");
        examCodesMap.put("8682","French - Language (AS Level only) (8682)");
        examCodesMap.put("8670","French - Literature (AS Level only) (8670)");
        examCodesMap.put("8001","General Paper 8001 (AS Level only) (8001)");
        examCodesMap.put("8004","General Paper 8004 (AS Level only) (8004)");
        examCodesMap.put("9696","Geography (9696)");
        examCodesMap.put("9717","German (A Level only) (9717)");
        examCodesMap.put("8683","German - Language (AS Level only) (8683)");
        examCodesMap.put("9239","Global Perspectives & Research (9239)");
        examCodesMap.put("9687","Hindi (A Level only) (9687)");
        examCodesMap.put("8687","Hindi - Language (AS Level only) (8687)");
        examCodesMap.put("8675","Hindi - Literature (AS Level only) (8675)");
        examCodesMap.put("9014","Hinduism (9014)");
        examCodesMap.put("8058","Hinduism (AS level only) (8058)");
        examCodesMap.put("9489","History (9489)");
        examCodesMap.put("9389","History (for final examination in 2021) (9389)");
        examCodesMap.put("9626","Information Technology (9626)");
        examCodesMap.put("9013","Islamic Studies (9013 & 8053)");
        examCodesMap.put("9488","Islamic Studies (9488)");
        examCodesMap.put("8281","Japanese Language (AS Level only) (8281)");
        examCodesMap.put("9084","Law (9084)");
        examCodesMap.put("9693","Marine Science (9693)");
        examCodesMap.put("9709","Mathematics (9709)");
        examCodesMap.put("9231","Mathematics - Further (9231)");
        examCodesMap.put("9607","Media Studies (9607)");
        examCodesMap.put("9483","Music (9483)");
        examCodesMap.put("9703","Music (9703)");
        examCodesMap.put("8663","Music (AS Level only) (8663)");
        examCodesMap.put("8024","Nepal Studies (AS Level only) (8024)");
        examCodesMap.put("9396","Physical Education (9396)");
        examCodesMap.put("9702","Physics (9702)");
        examCodesMap.put("9718","Portuguese (A Level only) (9718)");
        examCodesMap.put("8684","Portuguese - Language (AS Level only) (8684)");
        examCodesMap.put("8672","Portuguese - Literature (AS Level only) (8672)");
        examCodesMap.put("9698","Psychology (9698)");
        examCodesMap.put("9990","Psychology (9990)");
        examCodesMap.put("9699","Sociology (9699)");
        examCodesMap.put("9719","Spanish (A Level only) (9719)");
        examCodesMap.put("8665","Spanish - First Language (AS Level only) (8665)");
        examCodesMap.put("8685","Spanish - Language (AS Level only) (8685)");
        examCodesMap.put("8673","Spanish - Literature (AS Level only) (8673)");
        examCodesMap.put("9689","Tamil (9689)");
        examCodesMap.put("8689","Tamil - Language (AS Level only) (8689)");
        examCodesMap.put("9694","Thinking Skills (9694)");
        examCodesMap.put("9395","Travel and Tourism (9395)");
        examCodesMap.put("9676","Urdu (A Level only) (9676)");
        examCodesMap.put("8686","Urdu - Language (AS Level only) (8686)");
        examCodesMap.put("9686","Urdu - Pakistan only (A Level only) (9686)");
        examCodesMap.put("0452","Accounting (0452)");
        examCodesMap.put("0508","Arabic - First Language (0508)");
        examCodesMap.put("0400","Art and Design (0400)");
        examCodesMap.put("0610","Biology (0610)");
        examCodesMap.put("0450","Business Studies (0450)");
        examCodesMap.put("0620","Chemistry (0620)");
        examCodesMap.put("0509","Chinese - First Language (0509)");
        examCodesMap.put("0523","Chinese - Second Language (0523)");
        examCodesMap.put("0478","Computer Science (0478)");
        examCodesMap.put("0420","Computer Studies (0420)");
        examCodesMap.put("0445","Design and Technology (0445)");
        examCodesMap.put("0453","Development Studies (0453)");
        examCodesMap.put("0411","Drama (0411)");
        examCodesMap.put("0455","Economics (0455)");
        examCodesMap.put("0500","English - First Language (0500)");
        examCodesMap.put("0627","English - First Language (9-1) (UK only) (0627)");
        examCodesMap.put("0522","English - First Language (UK) (0522)");
        examCodesMap.put("0486","English - Literature (0486)");
        examCodesMap.put("0477","English - Literature (9-1) (UK only) (0477)");
        examCodesMap.put("0510","English - Second Language (oral endorsement) (0510)");
        examCodesMap.put("0454","Enterprise (0454)");
        examCodesMap.put("0680","Environmental Management (0680)");
        examCodesMap.put("0501","French - First Language (0501)");
        examCodesMap.put("0520","French - Foreign Language (0520)");
        examCodesMap.put("0460","Geography (0460)");
        examCodesMap.put("0525","German - Foreign Language (0525)");
        examCodesMap.put("0457","Global Perspectives (0457)");
        examCodesMap.put("0549","Hindi as a Second Language (0549)");
        examCodesMap.put("0470","History (0470)");
        examCodesMap.put("0447","India Studies (0447)");
        examCodesMap.put("0417","Information and Communication Technology (0417)");
        examCodesMap.put("0580","Mathematics (0580)");
        examCodesMap.put("0606","Mathematics - Additional (0606)");
        examCodesMap.put("0607","Mathematics - International (0607)");
        examCodesMap.put("0413","Physical Education (0413)");
        examCodesMap.put("0652","Physical Science (0652)");
        examCodesMap.put("0625","Physics (0625)");
        examCodesMap.put("0490","Religious Studies (0490)");
        examCodesMap.put("0653","Science - Combined (0653)");
        examCodesMap.put("0654","Sciences - Co-ordinated (Double) (0654)");
        examCodesMap.put("0408","World Literature (0408)");




        if(allPermissionsGranted()){
            startCamera(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        String itemName = (String) item.getTitle();

        closeDrawer();

        switch (item.getItemId()){

            case R.id.item_a:
                break;
            case R.id.item_b:
                break;
            case R.id.item_c:
                break;
            case R.id.item_d:
                break;
            case R.id.item_e:
                break;

        }

        return true;
    }

    private void closeDrawer(){

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer(){

        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){

            closeDrawer();
        }
        super.onBackPressed();
    }

    private void startCamera() {

        CameraX.unbindAll();



        //CameraControl cameraControl = CameraX.getCameraControl(CameraX.LensFacing.BACK);

        Rational aspectRatio = new Rational (textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight()); //size of the screen

        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).setTargetRotation(Surface.ROTATION_0).build();
        preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    //to update the surface texture we  have to destroy it first then re-add it
                    @Override
                    public void onUpdated(Preview.PreviewOutput output){
                        ViewGroup parent = (ViewGroup) textureView.getParent();
                        parent.removeView(textureView);
                        parent.addView(textureView, 0);

                        textureView.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                });

        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() != MotionEvent.ACTION_UP){

                    return false;
                }
//
                Log.i("Touched", "View touched");
//                TextureViewMeteringPointFactory factory = new TextureViewMeteringPointFactory(textureView);
//                MeteringPoint point = factory.createPoint(event.getX(), event.getY());
//                FocusMeteringAction action = FocusMeteringAction.Builder.from(point).build();
//
//                return true;


                final float x = (event != null) ? event.getX() : v.getX() + v.getWidth() / 2f;
                final float y = (event != null) ? event.getY() : v.getY() + v.getHeight() / 2f;

                TextureViewMeteringPointFactory pointFactory = new TextureViewMeteringPointFactory(textureView);
                float afPointWidth = 1.0f / 6.0f;  // 1/6 total area
                float aePointWidth = afPointWidth * 1.5f;
                MeteringPoint afPoint = pointFactory.createPoint(x, y, afPointWidth, 1.0f);
                MeteringPoint aePoint = pointFactory.createPoint(x, y, aePointWidth, 1.0f);

                try {
                    CameraX.getCameraControl(CameraX.LensFacing.BACK).startFocusAndMetering(
                            FocusMeteringAction.Builder.from(afPoint, FocusMeteringAction.MeteringMode.AF_ONLY)
                                    .addPoint(aePoint, FocusMeteringAction.MeteringMode.AE_ONLY)
                                    .build());
                } catch (CameraInfoUnavailableException e) {
                    Log.d("Error", "cannot access camera", e);
                }

                return true;
            }
        });

        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        imgCap = new ImageCapture(imageCaptureConfig);

        findViewById(R.id.imgCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cameraImage.setImageResource(R.drawable.cameraon);

                loadingDialog.startLoadingDialog();

                file = new File(Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".png");

                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {

                        String filePath = file.getPath();

                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                        rotateImage(bitmap);

                    }

                    @Override
                    public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Image Capture Failed : " + message;
                        Toast.makeText(getBaseContext(), msg,Toast.LENGTH_LONG).show();
                        if(cause != null){
                            cause.printStackTrace();
                        }
                    }
                });
            }

        });


        //bind to lifecycle:
        CameraX.bindToLifecycle((LifecycleOwner)this, preview, imgCap);

        
    }

    public void processImage(Bitmap bitmap){


        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                                Log.i("Text Recognized" , firebaseVisionText.getText());

                                splitCode(firebaseVisionText);

                            }
                        }
                )
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        }
                );

    }

    private void splitCode(FirebaseVisionText firebaseVisionText) {

        loadingDialog.dismissDialog();

        cameraImage.setImageResource(R.drawable.cameraoff);

        boolean deleted = file.delete();

        if(deleted){

            Log.i("Deleted File", "Deleted");
        }

        String text = firebaseVisionText.getText();

        text = text.replaceAll("\\s+", "").replaceAll("\n", "").replaceAll("\r", "");

        Log.i("Whitespaces removed", text);

        boolean isMatching = Pattern.compile("\\d{4}\\/\\d{2}\\/\\w\\/\\w\\/\\d{2}").matcher(text).find();

        Log.i("Matching", String.valueOf(isMatching));

        if(isMatching){

            Log.i("in here", "In here");

            Pattern pattern = Pattern.compile("\\d{4}\\/\\d{2}\\/\\w\\/\\w\\/\\d{2}");
            Matcher matcher = pattern.matcher(text);

            while(matcher.find()){

                codeText = matcher.group(0);
                Log.i("Matched text", codeText);

                preview.enableTorch(false);
                torchButton.setBackgroundResource(R.drawable.flashoff);

                splitText = codeText.split("/");

                System.out.println(Arrays.toString(splitText));

                // Splits into 9709, 42, F, M, 19
                // Splits into 9709, 42, M, J, 19
                // Splits into 9709, 42, O, N, 19


                paperCode = splitText[0] + "_";

                Log.i("Test value", splitText[0] + splitText[1]  + splitText[2] + splitText[3] + splitText[4]);

                if(splitText[2].equals("F")){

                    paperCode = paperCode + "m" + splitText[4] + "_qp_" + splitText[1];

                }
                else if(splitText[2].equals("M")){

                    paperCode = paperCode + "s" + splitText[4] + "_qp_" + splitText[1];
                }
                else if(splitText[2].equals("O")) {

                    paperCode = paperCode + "w" + splitText[4] + "_qp_" + splitText[1];
                }
                else if(Integer.valueOf(splitText[2]) == 0){

                    paperCode = paperCode + "w" + splitText[4] + "_qp_" + splitText[1];
                }
                else{

                    Log.i("paperCode", "Code is none");
                }

                Log.i("Paper", paperCode);

                Boolean paperCodeValid = Pattern.compile("\\d{4}\\/\\d{2}\\/\\w\\/\\w\\/\\d{2}").matcher(text).find();

                if (paperCodeValid != true){

                    Toast.makeText(this, "Error in finding past paper, please try again", Toast.LENGTH_SHORT).show();
                }

                else {

                    CameraX.unbind(preview);

                    paperCodeMs = paperCode.replace("_qp_", "_ms_");

                    String pdfUrlPart = examCodesMap.get(splitText[0]);

                    if (pdfUrlPart != null) {

                        Log.i("pdfUrlPart", pdfUrlPart);

                        pdfUrlPart = pdfUrlPart.replaceAll("\\s+", "%20");

                        Log.i("pdfUrlPart", pdfUrlPart);

                        if (Integer.valueOf(splitText[0]) > 8000) {

                            examLevel = "A%20Levels";
                        } else if (Integer.valueOf(splitText[0]) < 1000) {

                            examLevel = "IGCSE";
                        } else {

                            examLevel = "O%20Levels";
                        }

                        pdfUrl = "https://papers.gceguide.com/" + examLevel + "/" + pdfUrlPart + "/" + paperCode + ".pdf";

                        pdfUrlMs = pdfUrl.replace("_qp_", "_ms_");

                        Log.i("pdfURl", pdfUrl + "MS IS " + pdfUrlMs);

                        choiceBuilder = new AlertDialog.Builder(this);

                        // add a list

                        String[] items = getResources().getStringArray(R.array.choice_names);

                        choiceBuilder.setCancelable(true);
                        choiceBuilder.setTitle("Select an option for \n" + codeText);
                        //choiceBuilder.setMessage("Choose an option");
                        choiceBuilder.setItems(items, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which == 0){
                                            isQp = true;
                                            downloadPdf(which, pdfUrl, paperCode, isQp);
                                        }
                                        else if(which == 1){
                                            isQp = false;
                                            downloadPdf(which, pdfUrlMs, paperCodeMs, isQp);
                                        }
                                        else if(which == 2){
                                            // Download both
                                        }
                                        else{
                                            startCamera();
                                        }
                                    }

                                }).show();

                    }

                }

                startCamera();
            }

        }

        else{
            loadingDialog.dismissDialog();

            Toast.makeText(this, "Text is not a valid exam code, please try again", Toast.LENGTH_LONG).show();
            loadingDialog.dismissDialog();

        }

    }

    public void downloadPdf(int which, String url, String fileName, Boolean isQp){

        loadingDialog.dismissDialog();

        Log.i("Which", String.valueOf(which));


        Log.i("Downloader", "Download pdf " + url);

        File downloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName + ".pdf");

        if(downloadFile.exists()){

            Toast.makeText(this, "File Already Exists", Toast.LENGTH_LONG).show();
        }

        else {

            try {

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                request.setMimeType("application/pdf");

                if(isQp) {

                    request.setTitle(paperCode);
                    request.setDescription(paperCode);
                }else if(isQp == false){

                    request.setTitle(paperCodeMs);
                    request.setDescription(paperCodeMs);
                }
                request.allowScanningByMediaScanner();

                request.setVisibleInDownloadsUi(true);

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + ".pdf");

                // get download service and enqueue file
                downloadManager.enqueue(request);

                Toast.makeText(this, "Downloading : " + fileName + ".pdf", Toast.LENGTH_LONG).show();

                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


            } catch (Exception e) {

                Log.d("DOWNLOAD INFO", e.getMessage());
                e.printStackTrace();
            }

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(onComplete);
    }


    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {

            Toast.makeText(ctxt, "received", Toast.LENGTH_SHORT).show();

            if(isQp) {
                openPdf(paperCode); //issue

            }else if(isQp == false){

                openPdf(paperCodeMs);
            }
        }
    };

    public void openPdf(String fileName){

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName + ".pdf");

        Log.i("pdf file name", fileName + ".pdf");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }


    private void updateTransform(){
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int)textureView.getRotation();

        switch(rotation){
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float)rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }

    private void rotateImage(Bitmap bitmap){

        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(file.getAbsolutePath());
        }
        catch (IOException e){

            e.printStackTrace();
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();

        switch(orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            default:

        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        processImage(rotatedBitmap);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }


}
