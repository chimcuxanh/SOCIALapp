package com.example.socialapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
    RecyclerView recyclerView ;
    Adapteruser adapterUser;
    List<Modeluser> modeluserList;
    FirebaseAuth firebaseAuth;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        recyclerView = view.findViewById(R.id.user_recyclerview);
        //set properties
        recyclerView.setHasFixedSize(true); // thay doi kich thuoc sao cho phu hop
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // cung cap che do xem cho user . dang luoi va dang danh sach
        //init user list
        modeluserList =  new ArrayList<>();
        //get all user
        getalluser();

        return view;
    }

    private void getalluser() {
        //get current user
        final FirebaseUser Fuser = firebaseAuth.getCurrentUser();
        //get path of data " name '
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modeluserList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    Modeluser modeluser = ds.getValue(Modeluser.class);
                    //get all users expert currently signed in user
                    if(!modeluser.getUid().equals(Fuser.getUid()))
                    {
                        //adapter
                        adapterUser =  new Adapteruser(getActivity(), modeluserList);
                        //set adapter to recyclerview
                        recyclerView.setAdapter(adapterUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
