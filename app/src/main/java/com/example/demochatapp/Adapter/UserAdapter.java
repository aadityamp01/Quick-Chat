package com.example.demochatapp.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demochatapp.MessageActivity;
import com.example.demochatapp.Model.Chat;
import com.example.demochatapp.Model.User;
import com.example.demochatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context mContext;
    List<User> mUsers;
    boolean ischat;
    String theLastMessage;

    public UserAdapter(Context mContext, List<User> mUsers, boolean ischat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if("default".equals(user.getImageURL())){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }
        if(ischat){
            lastmessage(user.getId(),holder.last_msg);
        }else{
            holder.last_msg.setVisibility(View.GONE);
        }
        if(ischat){
            if(user.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }
            else{
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }

        }else{

            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MessageActivity.class);
            intent.putExtra("userid",user.getId());
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    static class  ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        private final ImageView img_on;
        private final ImageView img_off;
        private final TextView last_msg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on= itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    private void lastmessage(final String userid, final TextView last_msg){
        theLastMessage="default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    assert firebaseUser != null;
                    assert chat != null;
                    if((firebaseUser.getUid().equals(chat.getReceiver())&& userid.equals(chat.getSender())|| (firebaseUser.getUid().equals(chat.getSender())) && userid.equals(chat.getReceiver()))){
                        theLastMessage = chat.getMessage();
                    }

                }
                if ("default".equals(theLastMessage)) {
                    last_msg.setText("No Message");
                } else {
                    last_msg.setText(theLastMessage);
                }
                theLastMessage ="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
