package com.example.augmentedreality.Modules.FaceFilter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.augmentedreality.R;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FaceFilter extends AppCompatActivity {

    private static ModelRenderable modelRenderable;
    private static Texture texture;
    public static boolean isAdded = false;
    private static Context context;
    private RelativeLayout filtermenulayout;
    private Button menu;
    private Button cameraclick;
    private MediaPlayer mp,rec1,rec2;
    private static Animation myAnim,recordinganim;

    private Boolean islongpressed=false;
    private VideoRecorder videoRecorder;
    private CardView recordercardview;
    private Thread thread;
    private int px;

    private static CustomArFragment customARFragment;
    static AugmentedFaceNode augmentedFaceNode;

    private static int counterformenu=0;
    private static int counterforbuttons=0;

    private static ArrayList<Integer> filtersfb = new ArrayList<Integer>();
    private static ArrayList<Integer> mImageUrls = new ArrayList<>();

    public static HashMap<AugmentedFace, AugmentedFaceNode> faceNodeMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_filter);


        context = this;
        final float scale = this.getResources().getDisplayMetrics().density;
        px = (int) (90 * scale + 0.5f);

        counterformenu=0;
        filtermenulayout = findViewById(R.id.relativelayoutfiltermenu);
        //filtermenulayout.getLayoutParams().height=px;

        // get menu button
        menu = findViewById(R.id.filtermenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getmenubar();
            }
        });

        recordinganim = AnimationUtils.loadAnimation(context,R.anim.zoomout);
        rec1 = MediaPlayer.create(context,R.raw.beep2);
        rec2 = MediaPlayer.create(context,R.raw.beep1);
        recordercardview = findViewById(R.id.recordercardview);
        //recordercardview.setVisibility(View.INVISIBLE);
        //setrecinvisibility();

        // get Camera click button
        cameraclick = (Button) findViewById(R.id.imagebuttoncamera);
        cameraclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savephoto();
            }
        });
        cameraclick.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(!islongpressed)
                {
                    setrecvisibility();
                    cameraclick.setBackgroundResource(R.drawable.recordig);
                    cameraclick.setAnimation(recordinganim);
                    rec1.start();
                    Toast toast=Toast.makeText(context," Recording Started !",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
                    toast.getView().setBackgroundColor(0xFFFFFF);
                    toast.show();
                    islongpressed=true;

                    startrecording();
                    startTimer();
                }
                return true;
            }

        });

        myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
        mp = MediaPlayer.create(context, R.raw.camera);

        getImages();
        getSFBs();
        // call methode for creating recyclerview
        initRecyclerView();

        customARFragment = (CustomArFragment)getSupportFragmentManager().findFragmentById(R.id.arfragmentfacefilter);
        augmentedFaceNode=null;



    }




    public static void removefromdisplay()
    {
        Iterator<Map.Entry<AugmentedFace, AugmentedFaceNode>> iter =
                faceNodeMap.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry<AugmentedFace, AugmentedFaceNode> entry = iter.next();
            AugmentedFace face = entry.getKey();

            AugmentedFaceNode faceNode = entry.getValue();
            faceNode.setParent(null);
            iter.remove();

        }
        isAdded=false;
    }

    public static void placefilter(int i)
    {
        ModelRenderable.builder()
                .setSource(context,filtersfb.get(i))
                .build()
                .thenAccept(modelRenderable1 -> {
                    modelRenderable = modelRenderable1;
                    modelRenderable.setShadowCaster(false);
                    modelRenderable.setShadowReceiver(false);
                });

        customARFragment.getArSceneView().setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);

        customARFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {

            if(modelRenderable ==null)
                return;

            Frame frame = customARFragment.getArSceneView().getArFrame();

            Collection<AugmentedFace> augmentedFaces = frame.getUpdatedTrackables(AugmentedFace.class);

            for(AugmentedFace augmentedFace: augmentedFaces){

                if(isAdded)
                    return;

                augmentedFaceNode = new AugmentedFaceNode(augmentedFace);
                augmentedFaceNode.setParent(customARFragment.getArSceneView().getScene());
                augmentedFaceNode.setFaceRegionsRenderable(modelRenderable);
                faceNodeMap.put(augmentedFace, augmentedFaceNode);

                isAdded = true;
            }
        });
    }


    private void getSFBs(){

        Field[] fields=R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            try{
                if(fields[count].getName().substring(0,6).equals("filter")) {
                    // geting IDs of images
                    filtersfb.add(fields[count].getInt(fields[count]));
                }
            }
            catch(Exception e)
            {
            }

        }

    }

    private void getImages()
    {

        Field[] fields=R.drawable.class.getFields();
        for(int count=0; count < fields.length; count++){
            try{
                if(fields[count].getName().substring(0,6).equals("filter")) {
                    // geting IDs of images
                    mImageUrls.add(fields[count].getInt(fields[count]));
                }
            }
            catch(Exception e)
            {
            }

        }

    }


    private void initRecyclerView(){

        //Toast.makeText(context,"initRecyclerview",Toast.LENGTH_SHORT).show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewfilter);
        recyclerView.setLayoutManager(layoutManager);
        // creating class object
        RecyclerViewAdapterfilter adapter = new RecyclerViewAdapterfilter(context, mImageUrls);
        recyclerView.setAdapter(adapter);

    }


    public void getmenubar()
    {
        counterformenu++;
        if(counterformenu%2==1)
        {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) filtermenulayout.getLayoutParams();
// Changes the height and width to the specified *pixels*
            params.height = px;
            filtermenulayout.setLayoutParams(params);

            menu.setBackgroundResource(R.drawable.menu2);
        }
        else
        {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) filtermenulayout.getLayoutParams();
// Changes the height and width to the specified *pixels*
            params.height = 1;
            filtermenulayout.setLayoutParams(params);

            menu.setBackgroundResource(R.drawable.menu1);
        }
    }

    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/" + date + "_screenshot.jpg";
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }

    private void takePhoto() {
        final String filename = generateFilename();
        /*ArSceneView view = fragment.getArSceneView();*/
        //mSurfaceView = findViewById(R.id.arfragmentfacefilter);
        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(customARFragment.getArSceneView().getWidth(), customARFragment.getArSceneView().getHeight(),
                Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(customARFragment.getArSceneView(), bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename);

                } catch (IOException e) {
                    Toast toast = Toast.makeText(context, e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                /*Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Photo saved", Snackbar.LENGTH_LONG);
                snackbar.setAction("Open in Photos", v -> {
                    File photoFile = new File(filename);
                    Uri photoURI = FileProvider.getUriForFile(this,
                            this.getPackageName() + ".ar.codelab.name.provider",
                            photoFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                    intent.setDataAndType(photoURI, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                });
                snackbar.show();*/

            } else {
                Log.d("DrawAR", "Failed to copyPixels: " + copyResult);
                Toast toast = Toast.makeText(context,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }

    private void startTimer() {

        TextView timer = findViewById(R.id.reccount);

        thread = new Thread(() -> {
            int second = 0;

            while(islongpressed){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                second++;
                int minutesPassed = second/60;
                int secondsPassed = second %60;

                this.runOnUiThread(
                        ()-> timer.setText(String.format("%02d:%02d",minutesPassed,secondsPassed)));
            }
        });
        thread.start();
        /*new Thread( () -> {
            int second = 0;
            while(islongpressed){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                second++;
                int minutesPassed = second/60;
                int secondsPassed = second %60;
                this.runOnUiThread(
                        ()-> timer.setText(String.format("%d:%02d",minutesPassed,secondsPassed)));
            }
        }).start();
         */

    }

    private void startrecording()
    {
        if(videoRecorder == null)
        {
            videoRecorder = new VideoRecorder();
            videoRecorder.setSceneView(customARFragment.getArSceneView());

            int orientation = getResources().getConfiguration().orientation;

            videoRecorder.setVideoQuality(CamcorderProfile.QUALITY_HIGH, orientation);
        }

        boolean isrec = videoRecorder.onToggleRecord();

    }

    private void savephoto()
    {
        // no long press detected
        if(!islongpressed)
        {
            cameraclick.startAnimation(myAnim);
            mp.start();
            takePhoto();
            Toast toast=Toast.makeText(context,"Image Captured!",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
            toast.getView().setBackgroundColor(0xFFFFFF);
            toast.show();
        }
        else
        {
            rec2.start();
            cameraclick.setBackgroundResource(R.drawable.captureimage);
            Toast toast=Toast.makeText(context," Recording Saved !",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
            toast.getView().setBackgroundColor(0xFFFFFF);
            toast.show();
            islongpressed=false;
            setrecinvisibility();
            startrecording();
        }

    }

    private void setrecinvisibility()
    {
        recordercardview.setVisibility(View.INVISIBLE);
    }
    private void setrecvisibility()
    {
        recordercardview.setVisibility(View.VISIBLE);
    }
}
