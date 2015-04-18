package com.example.hwanik.materialtest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

/**
 * Created by hwanik on 2015. 4. 13..
 */
public class VivzAdapter extends RecyclerView.Adapter<VivzAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<Information> data= Collections.emptyList();

    //생성자 컨텍스트와 어댑터 생성시 받는 매개변수를 data넣는다.
    public VivzAdapter(Context context, List<Information> data){
        inflater = LayoutInflater.from(context);
        this.data=data;
        this.context=context;
    }

    public void delete(int position){
        data.remove(position);
        notifyItemRemoved(position );
    }

    //어떠한 데이터를 보여줄 것인가를 정의하는 함수. ViewHolder를 생성하는 작업을 한다.
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("hwanik", "onCreateViewHolder called");
        //inflater를 통해 현재 보여줄 custom_row를 view에 붙인다.
        View view=inflater.inflate(R.layout.custom_row,parent,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    //getView와 동일한 기능을 하는 함수이다. 화면에 뿌려주는 역할
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d("hwanik", "onBindViewHolder called "+ position);

        //Inforation클래스의 현재 데이터를 position 값에 따라 current에 저장한다.
        Information current=data.get(position);
        //데이터를 뿌려준다.
        holder.title.setText(current.title);
        holder.icon.setImageResource(current.iconId);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        ImageView icon;
        RelativeLayout layout;
        public MyViewHolder(View itemView) {
            super(itemView);
            title= (TextView) itemView.findViewById(R.id.listText);
            icon= (ImageView) itemView.findViewById(R.id.listIcon);
            layout=(RelativeLayout)itemView.findViewById(R.id.custom_row_layout);
            layout.setOnClickListener(this);
//          icon 클릭 시 아래의 onClick함수를 실행하게 된다.
        }

//      icon이 클릭함에 따라 수행하는 함수이다.
        @Override
        public void onClick(View view) {
//          Toast.makeText(context,"item clicked at "+getPosition(),Toast.LENGTH_SHORT).show();
            delete(getPosition());
        }
    }
}
