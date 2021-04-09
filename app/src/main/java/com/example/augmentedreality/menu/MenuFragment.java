package com.example.augmentedreality.menu;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.augmentedreality.Modules.Doodler.Doodler;
import com.example.augmentedreality.Modules.FaceFilter.FaceFilter;
import com.example.augmentedreality.Modules.FunMode.FunMode;
import com.example.augmentedreality.Modules.ObjectPlacer.ObjectPlacer;
import com.example.augmentedreality.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MenuFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton menuBtn, doodlerBtn, objectPlacerBtn, faceFilterBtn, funModeBtn;
    private LinearLayout doodlerlayout,objectPlacerlayout,faceFilterlayout, funModelayout, menushowlayout;
    private Float translationY = 40f;
    private Boolean isMenuOpen = false;
    private View view;
    private int px;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //inflate layout in a view variable so can use attributes of view
        view =  inflater.inflate(R.layout.fragment_menu, container, false);
        //menu initialization
        initMenu();
        return view;
    }


    private void initMenu() {

        menuBtn = view.findViewById(R.id.menuButton);
        doodlerBtn = view.findViewById(R.id.doodler);
        objectPlacerBtn = view.findViewById(R.id.objectPlacer);
        faceFilterBtn = view.findViewById(R.id.faceFilter);
        funModeBtn = view.findViewById(R.id.funMode);

        menushowlayout = view.findViewById(R.id.menushowlayout);
        final float scale = this.getResources().getDisplayMetrics().density;
        px = (int) (250 * scale + 0.5f);

        doodlerlayout = view.findViewById(R.id.doodlerlayout);
        objectPlacerlayout = view.findViewById(R.id.objectPlacerlayout);
        faceFilterlayout = view.findViewById(R.id.faceFilterlayout);
        funModelayout = view.findViewById(R.id.funmodelayout);

        //animation for menu icons
        doodlerlayout.setAlpha(0f);
        objectPlacerlayout.setAlpha(0f);
        faceFilterlayout.setAlpha(0f);
        funModelayout.setAlpha(0f);

        doodlerlayout.setTranslationY(translationY);
        objectPlacerlayout.setTranslationY(translationY);
        faceFilterlayout.setTranslationY(translationY);
        funModelayout.setTranslationY(translationY);

        menuBtn.setOnClickListener(this);
        doodlerBtn.setOnClickListener(this);
        objectPlacerBtn.setOnClickListener(this);
        faceFilterBtn.setOnClickListener(this);
        funModeBtn.setOnClickListener(this);
    }


    //open - close menu animations
    private void menuChange(){

        //to open menu with animations
        if(!isMenuOpen){

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) menushowlayout.getLayoutParams();
            // Changes the height and width to the specified *pixels*
            params.height = px;
            menushowlayout.setLayoutParams(params);

            doodlerlayout.animate().translationY(0f).alpha(1f).setDuration(200).start();
            objectPlacerlayout.animate().translationY(0f).alpha(1f).setDuration(200).start();
            faceFilterlayout.animate().translationY(0f).alpha(1f).setDuration(200).start();
            funModelayout.animate().translationY(0f).alpha(1f).setDuration(200).start();
            menuBtn.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.close));


        }
        //to close menu with animations
        else{

            doodlerlayout.animate().translationY(translationY).alpha(0f).setDuration(200).start();
            objectPlacerlayout.animate().translationY(translationY).alpha(0f).setDuration(200).start();
            faceFilterlayout.animate().translationY(translationY).alpha(0f).setDuration(200).start();
            funModelayout.animate().translationY(translationY).alpha(0f).setDuration(200).start();
            menuBtn.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(),R.drawable.menu_icon));

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) menushowlayout.getLayoutParams();
            // Changes the height and width to the specified *pixels*
            params.height = 1;
            menushowlayout.setLayoutParams(params);

        }


        isMenuOpen = !isMenuOpen;
    }




    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.menuButton:
                menuChange();
                break;
            case R.id.doodler:
                startActivity(new Intent(getActivity(), Doodler.class));
                getActivity().finish();
                menuChange();
                break;

            case R.id.objectPlacer:
                startActivity(new Intent(getActivity(), ObjectPlacer.class));
                getActivity().finish();
                menuChange();
                break;
            case R.id.faceFilter:
                startActivity(new Intent(getActivity(), FaceFilter.class));
                getActivity().finish();
                menuChange();
                break;
            case R.id.funMode:
                startActivity(new Intent(getActivity(), FunMode.class));
                getActivity().finish();
                menuChange();
                break;
        }

    }
}