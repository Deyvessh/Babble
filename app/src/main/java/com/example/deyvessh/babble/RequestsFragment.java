package com.example.deyvessh.babble;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFragment extends Fragment {

    private RecyclerView mRequestsList;

    private DatabaseReference mRootRef;

    private DatabaseReference FriendsDatabaseRef;
    private DatabaseReference mRequestDatabase;
    private DatabaseReference mUserReference;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference FriendsReqDatabaseRef;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrent_user;

    String list_users_id;

    private String mCurrent_user_id;
    String online_user_id;
    //final String user_id = getActivity().getIntent().getStringExtra("user_id");

    private View mMainView;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mRequestsList = (RecyclerView) mMainView.findViewById(R.id.request_list);
        mAuth = FirebaseAuth.getInstance();

        online_user_id = mAuth.getCurrentUser().getUid();
        list_users_id = mAuth.getCurrentUser().getUid();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mRootRef = FirebaseDatabase.getInstance().getReference();


        mRequestDatabase = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("friend_req").child(mCurrent_user_id);
        mRequestDatabase.keepSynced(true);
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserReference.keepSynced(true);
        FriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("friends");
        FriendsDatabaseRef.keepSynced(true);
        FriendsReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("friend_req");
        FriendsReqDatabaseRef.keepSynced(true);

        mRequestsList.setHasFixedSize(true);
        mRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<Friends, RequestViewHolder> requestRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, RequestViewHolder>(

                Friends.class,
                R.layout.request_single_layout,
                RequestViewHolder.class,
                mRequestDatabase


        ) {

            @Override
            protected void populateViewHolder(final RequestViewHolder requestViewHolder, Friends friends, final int position) {

                list_users_id = getRef(position).getKey();

                DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();
                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            String request_type = dataSnapshot.getValue().toString();

                            if (request_type.equals("received")) {

                                mUserReference.child(list_users_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                        String userStatus = dataSnapshot.child("status").getValue().toString();

                                        requestViewHolder.setName(userName);
                                        requestViewHolder.setUserImage(userThumb, getContext());
                                        requestViewHolder.setStatus(userStatus, getContext());

                                      /*  requestViewHolder.mAcceptReqBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final String saveCurrentDate = DateFormat.getDateTimeInstance().format(new Date());
                                               // Calendar callForDate = Calendar.getInstance();
                                               // SimpleDateFormat currentDate = new SimpleDateFormat("dd-mmmm-yyyy");
                                               // final String saveCurrentDate = currentDate.format(callForDate.getTime());

                                                FriendsDatabaseRef.child(online_user_id).child(list_users_id).child("date").setValue(saveCurrentDate)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                FriendsDatabaseRef.child(list_users_id).child(online_user_id).child("date").setValue(saveCurrentDate)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {

                                                                                FriendsDatabaseRef.child(online_user_id).child(list_users_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if (task.isSuccessful()) {

                                                                                            FriendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                                            if (task.isSuccessful()) {

                                                                                                                Toast.makeText(getContext(), "Friend Request Accepted Successfully", Toast.LENGTH_SHORT).show();

                                                                                                            }

                                                                                                        }
                                                                                                    });
                                                                                        }

                                                                                    }
                                                                                });

                                                                            }
                                                                        });

                                                            }
                                                        });

                                            }
                                        });
                                        requestViewHolder.mDeclineRequestBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                FriendsReqDatabaseRef.child(online_user_id).child(list_users_id).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    FriendsReqDatabaseRef.child(list_users_id).child(online_user_id).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if(task.isSuccessful()){

                                                                                        Toast.makeText(getContext(), "Friend Request Declined", Toast.LENGTH_SHORT).show();

                                                                                    }

                                                                                }
                                                                            });

                                                                }

                                                            }
                                                        });

                                            }
                                        }); */



                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                /*
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        requestViewHolder.setName(userName);
                        requestViewHolder.setStauts(status);
                        requestViewHolder.setUserImage(userThumb, getContext());

                        requestViewHolder.mAcceptReqBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                /*
                                final String currentDmate = DateFormat.getDateTimeInstance().format(new Date());

                                Map friendsMap = new HashMap();
                                friendsMap.put("friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
                                friendsMap.put("friends/" + user_id + "/"  + mCurrent_user.getUid() + "/date", currentDate);


                                friendsMap.put("friend_req/" + mCurrent_user.getUid() + "/" + user_id, null);
                                friendsMap.put("friend_req/" + user_id + "/" + mCurrent_user.getUid(), null);


                                mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                        if(databaseError == null){
                                            //UPDATE LIST ITEM
                                            Toast.makeText(getContext(), "Accepted Friend Request", Toast.LENGTH_SHORT).show();
                                            databaseReference.removeValue();


                                        } else {

                                            String error = databaseError.getMessage();

                                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();


                                        }

                                    }
                                });
                                Toast.makeText(getContext(), "Accepted Friend Request", Toast.LENGTH_SHORT).show();
                            }
                        });

                        requestViewHolder.mDeclineRequestBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                mFriendRequestDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mFriendRequestDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                //UPDATE LIST
                                                Toast.makeText(getContext(), "Declined Friend Request", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });

                                Toast.makeText(getContext(), "Declined Friend Request", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                */

            }
        };

        mRequestsList.setAdapter(requestRecyclerViewAdapter);

    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Button mAcceptReqBtn, mDeclineRequestBtn;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            //mAcceptReqBtn = (Button) mView.findViewById(R.id.accept_req_btn);
            //mDeclineRequestBtn = (Button) mView.findViewById(R.id.decline_req_btn);

        }


        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.shinchen).into(userImageView);

        }


        public void setStatus(String userStatus, Context context) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(userStatus);
        }
    }


}


//MyAdapter.notifyDatasetChanged(); for updating UI
