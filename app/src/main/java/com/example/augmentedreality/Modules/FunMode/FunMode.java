package com.example.augmentedreality.Modules.FunMode;

import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.view.Display;
import android.widget.Button;
import android.widget.TextView;
import com.example.augmentedreality.R;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.Texture;


import java.util.Random;

public class FunMode extends Fragment {

    private Scene scene;
    private Camera camera;
    private ModelRenderable bulletRenderable;
    private boolean timerStarted = false;
    private int enemyLeft = 10;
    int second = 0;
    private Point point;
    private TextView enemyLeftTxt;
    private SoundPool soundPool;
    private int sound;
    private int soundBlast;
    private View view;
    private customFragment arFragment;
    private Dialog dialogStart;
    private Dialog dialogEnd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_fun_mode, container, false);

        //to get display size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        point = new Point();

        //end point of screen
        display.getRealSize(point);

        //arFragment with hidden object detaction and hand icon
        arFragment = (customFragment) getChildFragmentManager().findFragmentById(R.id.arFragment);

        //to get camera scene
        scene = arFragment.getArSceneView().getScene();
        camera = scene.getCamera();

        dialogStart = new Dialog(getActivity());
        dialogStart.show();
        dialogStart.setCanceledOnTouchOutside(false);
        dialogStart.setContentView(R.layout.popupstart);
        Button btnStart = dialogStart.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> {
                startTimer();
                timerStarted = true;
                dialogStart.dismiss();
        });

        enemyLeftTxt = view.findViewById(R.id.count);

        //to load sound
        loadSoundPool();
        //adding enemy in the scene
        addEnemyToScene();

        //rendering bullet model
        buildBulletModel();
        Button shoot = view.findViewById(R.id.btnShoot);

        shoot.setOnClickListener( v -> shoot());


        return view;
    }

    //setting sound pool
    private void loadSoundPool(){

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        sound = soundPool.load(getActivity(),R.raw.gunsound,1);
        soundBlast = soundPool.load(getActivity(),R.raw.blast,1);
    }


    //on shoot
    private void shoot() {
        //setting ray at center
        Ray ray = camera.screenPointToRay(point.x/2f, point.y/2f );

        Node node = new Node();
        node.setRenderable(bulletRenderable);
        scene.addChild(node);
        //playing sound on shooting!
        soundPool.play(sound, 0.8f,0.8f,1,0,1f);
        new Thread( () -> {
            for(int i =0 ; i < 200 ; i++){
                int dist = i;
                //background work
                getActivity().runOnUiThread( () -> {
                    Vector3 vector3 = ray.getPoint( dist * 0.07f);
                    node.setWorldPosition(vector3);
                    //to check bullet is hit to enemy or not
                    Node nodeInContact = scene.overlapTest(node);
                    if(nodeInContact != null){
                        enemyLeft--;
                        enemyLeftTxt.setText("Enemy Left : " +enemyLeft);
                        scene.removeChild(node);
                        scene.removeChild(nodeInContact);
                        //sound when enemy is killed
                        soundPool.play(soundBlast, 0.3f,0.3f,1,0,1f);

                    }
                });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getActivity().runOnUiThread( () -> scene.removeChild(node));
        }).start();


        if(enemyLeft == 0){
            dialogEnd = new Dialog(getActivity());
            dialogEnd.show();
            dialogEnd.setCanceledOnTouchOutside(false);
            dialogEnd.setContentView(R.layout.popupend);
            Button btnRestart = dialogEnd.findViewById(R.id.btnRestart);
            TextView score = dialogEnd.findViewById(R.id.score);
            score.setText(second/60 + ":" + second%60);
            btnRestart.setOnClickListener(v -> {
                startTimer();
                timerStarted = true;
                addEnemyToScene();
                enemyLeft = 10;
                dialogEnd.dismiss();
            });

        }
    }

    private void startTimer() {

        TextView timer = view.findViewById(R.id.timer);

        new Thread( () -> {
            second = 0;

            while(enemyLeft > 0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                second++;
                int minutesPassed = second/60;
                int secondsPassed = second %60;

                getActivity().runOnUiThread(
                        ()-> timer.setText("Time Taken\n" +minutesPassed + ":" + secondsPassed));
            }
        }).start();
    }

    private void buildBulletModel() {
        Texture.builder()
                .setSource(getActivity(),R.drawable.texture)
                .build()
                .thenAccept(texture -> {
                    MaterialFactory.makeOpaqueWithTexture(getActivity(),texture)
                            .thenAccept(material -> {
                                bulletRenderable = ShapeFactory
                                        .makeSphere(0.013f, new Vector3(0f,0f,0f),material);
                            });
                });
    }

    private void addEnemyToScene() {

        ModelRenderable
                .builder()
                .setSource(getActivity(), Uri.parse("flyingsacuer.sfb"))
                .build()
                .thenAccept( renderable -> {
                    for(int i = 0 ; i < 10 ; i++) {
                        Node node = new Node();
                        //random node setting
                        node.setRenderable(renderable);

                        Random random = new Random();
                        int x = random.nextInt(10);
                        int y = random.nextInt(10);
                        int z = random.nextInt(26);
                        z = -z;

                        node.setWorldPosition(new Vector3((float) x, (float) y / 10f, (float) z));
                        node.setLocalRotation(Quaternion.axisAngle(new Vector3(0,1f,0),230));
                        scene.addChild(node);
                    }
                });
    }
}
