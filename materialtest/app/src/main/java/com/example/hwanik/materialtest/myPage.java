package com.example.hwanik.materialtest;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;

import com.facebook.Profile;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import android.graphics.*;
public class myPage extends Fragment {

    private LinearLayout linearLayout;
    private String user_ID;
    private String profileName;

    private URL image_value;
    private Bitmap profile_img;
    //private TextView userNameView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parse.initialize(getActivity().getApplicationContext(), "USjhdBZW0Jsm8jvedZIoc4zm0OdZRvI0lMWNoRUt", "eUkreRV5NNa6iruqmLnbpTqVG6F5Z3MZDT0bWJxo");
//        ParseFacebookUtils.initialize("810766125683106");
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        user_ID="2";

        if(android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            image_value = new URL("https://graph.facebook.com/"+user_ID+"/picture" );
            profile_img = BitmapFactory.decodeStream(image_value.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        linearLayout=(LinearLayout)inflater.inflate(R.layout.fragment_mypage,container,false);
        ImageView profile=(ImageView)linearLayout.findViewById(R.id.profile_img);



        profile.setImageBitmap(profile_img);

        return linearLayout;
    }


}
