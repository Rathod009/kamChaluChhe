package com.example.augmentedreality.Modules.ObjectPlacer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.example.augmentedreality.R;
import com.example.augmentedreality.RecycleViewAdapter;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import java.util.ArrayList;

public class ObjectPlacer extends AppCompatActivity implements View.OnClickListener{

    private ArFragment arFragment;
    private RecyclerView recyclerView;
    private ArrayList<Integer> objectCycleImage;
    private ArrayList<String> objectCycleSFB;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_placer);
        getSupportActionBar().hide();

        //instance of ARFragment
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        //instance of recycler view
        recyclerView = findViewById(R.id.recyclerIcon);

        //setting image data
        initImageData();

        //to set sfb data
        initSFBData();

        //iniatilazition of recycler view
        initRecyleView();


        //OnTap Action Listener
        arFragment.setOnTapArPlaneListener(((hitResult, plane, motionEvent) -> {
            //Crating an Anchor on location where user has tapped
            Anchor anchor = hitResult.createAnchor();

            ModelRenderable.builder()
                    .setSource(this, Uri.parse(objectCycleSFB.get(RecycleViewAdapter.counter)))
                    .build()
                    .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable));
        }));

    }

    private void initImageData() {

        objectCycleImage = new ArrayList<>();
        objectCycleImage.add(R.drawable.burger);
        objectCycleImage.add(R.drawable.oldcar);
        objectCycleImage.add(R.drawable.ufo);
        objectCycleImage.add(R.drawable.coffee);
        objectCycleImage.add(R.drawable.burger);
        objectCycleImage.add(R.drawable.oldcar);
        objectCycleImage.add(R.drawable.ufo);

    }

    private void initSFBData() {
        objectCycleSFB = new ArrayList<>();

        objectCycleSFB.add("hamburger.sgb");
        objectCycleSFB.add("model.sfb");
        objectCycleSFB.add("flyingsacuer.sfb");
        objectCycleSFB.add("coffee.sfb");
        objectCycleSFB.add("hamburger.sfb");
        objectCycleSFB.add("model.sfb");
        objectCycleSFB.add("flyingsacuer.sfb");

    }



    //Method to Render Model On Real World
    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {

        AnchorNode anchorNode = new AnchorNode(anchor);

        //to move-resize it
        TransformableNode transformableNode = new TransformableNode((arFragment.getTransformationSystem()));
        transformableNode.setLocalScale(new Vector3(15f,15f,15f));
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }



    private void initRecyleView() {

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecycleViewAdapter adapter = new RecycleViewAdapter(this,objectCycleImage);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }
}
