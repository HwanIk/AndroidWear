package com.example.hwanik.Yosee;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hwanik.materialtest.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

//네비게이션 프래그 먼트 클래스
public class NavigationDrawerFragment extends Fragment {

    private RecyclerView recyclerView; //RecyclerView step1
    public static final String PREF_FILE_NAME="testpref";
    public static final String KEY_USER_LEARNED_DRAWER="user_learned_drawer";
    private ActionBarDrawerToggle mDrawerToggle;    // 네비게이션 바가 활성화 됐는지 판단하는 변수
    private DrawerLayout mDrawerLayout;             // DrawerLayout
    private VivzAdapter adapter;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer=Boolean.valueOf(readFromPreferences(getActivity(),KEY_USER_LEARNED_DRAWER,"false"));
        if(savedInstanceState!=null){
            mFromSavedInstanceState=true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //RecyclerView step2 layout에 리턴값을 넣고, layout에서 RecyclerView에 대한 아이디 값을 받아와서 반환한다.
        View layout=inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView=(RecyclerView)layout.findViewById(R.id.drawerList);
        adapter=new VivzAdapter(getActivity(),getData());   //getData에서 넣어준 값들을 반환 받고 adapter를 생성한다.
        recyclerView.setAdapter(adapter);                   //recyclerView에 Adater를 연결
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); //recyclerView에 보여줄 형태를 Linear로 설정한다.ㄱ
        return layout;
    }

//  데이터를 넣고 Information을 List형태로 만들어서 넣어준 값들을 반환 받는다.
    public static List<Information> getData(){
        List<Information> data=new ArrayList<>();
        int[] icons={R.drawable.ic_number1,R.drawable.ic_number2,R.drawable.ic_number3,R.drawable.ic_number4};
        String[] titles={"Kim","Hwan","Ik","Good"};
        for(int i=0;i<100;i++){
            Information current=new Information();
            current.iconId=icons[i%icons.length];
            current.title=titles[i%titles.length];
            data.add(current);
        }
        return data;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar ) {

        containerView=getActivity().findViewById(fragmentId);
        mDrawerLayout=drawerLayout;
        mDrawerToggle=new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.drawer_open,R.string.drawe_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer){
                    mUserLearnedDrawer=true;
                    saveToPreferences(getActivity(),KEY_USER_LEARNED_DRAWER,mUserLearnedDrawer+"");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
//                DrawerLayout의 움직임에 따라 0에서 1까지 slideOffset 값이 변화하게 된다.
                Log.d("VIVZ","offset"+slideOffset);
                if(slideOffset<0.6) {                   //0.6의 투명도 값 전까지 흐려진다.
                    toolbar.setAlpha(1 - slideOffset);  //위의 toolbar의 알파값(즉 투명도)가 drawerlayout이 나오는 만큼 투명도가 낮아진다.(흐려짐)
                }
            }
        };
        if(!mUserLearnedDrawer && !mFromSavedInstanceState){
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //DrawerLayout에 Toggle을 붙인다.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences=context.getSharedPreferences(PREF_FILE_NAME,context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(preferenceName,preferenceValue);
        editor.apply();
    }
    public static String readFromPreferences(Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences=context.getSharedPreferences(PREF_FILE_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName,defaultValue);
    }
}
