package com.abc.iitgwebmailnotifier.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abc.iitgwebmailnotifier.Activities.EmailBodyAcitivity;
import com.abc.iitgwebmailnotifier.Activities.MainActivity;
import com.abc.iitgwebmailnotifier.R;
import com.abc.iitgwebmailnotifier.Services.UserSessionManager;
import com.abc.iitgwebmailnotifier.loadRecentMails;
import com.abc.iitgwebmailnotifier.models.Email;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.abc.iitgwebmailnotifier.R.color.secondLevelGrey;

/**
 * Created by aarkay0602 on 13/2/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.myViewHolder> {
    public static List<Email> emails = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private SharedPreferences preferences;
    private String folderName;
    public static List<Email> checkedEmails = new ArrayList<>();


    public RecyclerAdapter(Context context,String folderName,List<Email> emails){
        preferences = context.getSharedPreferences(UserSessionManager.PREFER_NAME,context.getApplicationContext().MODE_PRIVATE);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.folderName = folderName;
        this.emails = emails;
/*
        loadRecentMails asyncTask = (loadRecentMails) new loadRecentMails(new loadRecentMails.AsyncResponse(){

            @Override
            public void processFinish(List<Email> output) {
                emails = output;
            }

        },activity,username,password,server,folderName).execute();

        notifyDataSetChanged();
*/  }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.mail_list_item,parent,false);
        myViewHolder holder = new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final myViewHolder holder, final int position) {
        final Email current = emails.get(position);
        current.setPosition(position);
        if (folderName.equals("Sent")){
            holder.From.setText(current.getToAddresses().get(0));
        }else {
            holder.From.setText(current.getFrom());
        }
        holder.Subject.setText(current.getSubject());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, EmailBodyAcitivity.class);
                i.putExtra("position",position);
                i.putExtra("email",current);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                holder.tick.setImageResource(R.drawable.ic_done_all_black_24dp);
            }
        });
        holder.timestamp.setText(current.getSentDate());
        holder.checkBox.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.checkBox.setChecked(emails.get(position).isSelected());

        if (current.isSeen()){
            holder.tick.setImageResource(R.drawable.ic_done_all_black_24dp);
        }else{
            holder.tick.setImageResource(R.drawable.ic_done_black_24dp);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                emails.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.checkBox.isChecked()){
                    checkedEmails.add(current);
                }else{
                    checkedEmails.remove(current);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        TextView From,Subject,timestamp;
        LinearLayout linearLayout;
        CheckBox checkBox;
        ImageView tick;
        public myViewHolder(View itemView) {
            super(itemView);
            From = (TextView) itemView.findViewById(R.id.textview_from);
            Subject = (TextView) itemView.findViewById(R.id.textview_subject);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            timestamp =(TextView) itemView.findViewById(R.id.timestamp);
            tick = (ImageView) itemView.findViewById(R.id.tick_image);
        }
        public void bind(){
            checkBox.setChecked(false);
        }

    }
    public void clear(){
        emails.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<Email> emails){
        emails.addAll(emails);
        notifyDataSetChanged();
    }


}
