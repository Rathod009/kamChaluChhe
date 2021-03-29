package com.example.augmentedreality.Modules.ObjectPlacer;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.augmentedreality.R;
import com.example.augmentedreality.RecycleViewAdapter;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import java.util.ArrayList;

public class ObjectPlacer extends Fragment implements View.OnClickListener{

    private ArFragment arFragment;
    private RecyclerView recyclerView;
    private ArrayList<Integer> objectCycleImage;
    private ArrayList<String> objectCycleSFB;
    private LinearLayoutManager linearLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            //inflate view
            View view  = inflater.inflate(R.layout.fragment_object_placer, container, false);

            //instance of ARFragment
            arFragment = (ArFragment) getChildFragmentManager().findFragmentById(R.id.arFragment);

            //instance of recycler view
            recyclerView = view.findViewById(R.id.recyclerIcon);

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
                        .setSource(getActivity(), Uri.parse(objectCycleSFB.get(RecycleViewAdapter.counter)))
                        .build()
                        .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable));
            }));
            return view;

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

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecycleViewAdapter adapter = new RecycleViewAdapter(getContext(),objectCycleImage);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }
}
