package com.comfest.cf18;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by ishan on 4/2/18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();

    public MessageAdapter(List<Messages> mMessageList)
    {
        this.mMessageList= mMessageList;
    }



    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;

        public ImageButton downimage;



        public MessageViewHolder(View itemView) {

            super(itemView);

            messageText= (TextView) itemView.findViewById(R.id.message_text_layout);

            downimage= (ImageButton) itemView.findViewById(R.id.downloadimage);

        }
    }
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int i) {

        String curuserid= mAuth.getCurrentUser().getUid();

    final Messages c= mMessageList.get(i);
        String from_user_id= c.getFrom();
        String message_type= c.getType();

        if(from_user_id.equals(curuserid))
        {

            holder.messageText.setBackgroundResource(R.drawable.message_shape2);


            holder.messageText.setTextColor(Color.BLACK);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.messageText.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }
            holder.messageText.setLayoutParams(params);


        }
        else
        {
            holder.messageText.setBackgroundResource(R.drawable.message_shape);
            holder.messageText.setTextColor(Color.WHITE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.messageText.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
            holder.messageText.setLayoutParams(params);

        }
        if(message_type.equals("text")) {
            holder.messageText.setText(c.getMessage());

            holder.downimage.setVisibility(View.INVISIBLE);
            holder.downimage.setEnabled(false);


        }
        else if(message_type.equals("image"))
        {
            holder.messageText.setBackgroundResource(R.drawable.message_shape3);
            holder.messageText.setTextColor(Color.WHITE);
            holder.messageText.setText("Sent a picture");
            holder.downimage.setVisibility(View.VISIBLE);
            holder.downimage.setEnabled(true);
            holder.downimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent myint= new Intent(holder.messageText.getContext(), ImageActivity.class);
                    myint.putExtra("image", c.getMessage());
                    holder.messageText.getContext().startActivity(myint);
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}
