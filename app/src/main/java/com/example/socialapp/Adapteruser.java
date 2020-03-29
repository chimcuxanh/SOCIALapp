package com.example.socialapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapteruser extends RecyclerView.Adapter<Adapteruser.myholder> {

    Context context;
    List<Modeluser> userlist;
    private int layout;

    //constructor


    public Adapteruser(Context context, List<Modeluser> userlist) {
        this.context = context;
        this.userlist = userlist;
    }

    @NonNull
    @Override
    public myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout (row_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_users , parent,false);

        return new myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myholder holder, int position) {
        //get data
        String userimage = userlist.get(position).getImage();
        final String username = userlist.get(position).getName();
        String userEmail = userlist.get(position).getEmail();

        //set data
        holder.mNametv.setText(username);
        holder.memailtv.setText(userEmail);
        try{
            Picasso.get().load(userimage)
                    .placeholder(R.drawable.ic_default_image)
                    .into(holder.AvatarIv);
        }
        catch (Exception e)
        {}
        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+username, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    class myholder extends RecyclerView.ViewHolder{

        ImageView AvatarIv;
        TextView mNametv, memailtv;

        public myholder(@NonNull View itemView) {
            super(itemView);

            AvatarIv = itemView.findViewById(R.id.avataruser);
            mNametv = itemView.findViewById(R.id.nametv);
            memailtv = itemView.findViewById(R.id.emailtv);

        }
    }
}
