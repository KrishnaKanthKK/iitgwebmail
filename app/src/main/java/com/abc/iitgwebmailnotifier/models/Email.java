package com.abc.iitgwebmailnotifier.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aarkay0602 on 13/2/17.
 */

public class Email implements Parcelable{
    private String Subject;
    private String From;
    private String Email_Id;
    private String body;
    private  int MessageNumber;
    private String SentDate;
    private int position;
    private long UID;
    private boolean selected;
    private String fromFolder;
    private boolean Seen;
    private boolean attatchment;
    private int totalMails;
    private List<String> toAddresses;

    public Email(){

    }

    protected Email(Parcel in) {
        Subject = in.readString();
        From = in.readString();
        body = in.readString();
        Email_Id = in.readString();
        MessageNumber = in.readInt();
        SentDate = in.readString();
        position = in.readInt();
        UID = in.readLong();
        selected = in.readByte() != 0;
        fromFolder = in.readString();
        Seen = in.readByte() != 0;
        attatchment = in.readByte() != 0;
        totalMails = in.readInt();
        toAddresses = in.createStringArrayList();

    }

    public static final Parcelable.Creator<Email> CREATOR = new Parcelable.Creator<Email>() {
        @Override
        public Email createFromParcel(Parcel in) {
            return new Email(in);
        }

        @Override
        public Email[] newArray(int size) {
            return new Email[size];
        }
    };

    public String getFromFolder() {
        return fromFolder;
    }

    public void setFromFolder(String fromFolder) {
        this.fromFolder = fromFolder;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public boolean isSelected() {
        return selected;
    }

    public boolean isAttatchment() {
        return attatchment;
    }

    public void setAttatchment(boolean attatchment) {
        this.attatchment = attatchment;
    }

    public boolean isSeen() {
        return Seen;
    }

    public void setSeen(boolean seen) {
        Seen = seen;
    }

    public long getUID() {
        return UID;
    }

    public void setUID(long UID) {
        this.UID = UID;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getSentDate() {
        return SentDate;
    }

    public void setSentDate(String sentDate) {
        SentDate = sentDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getMessageNumber() {
        return MessageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        MessageNumber = messageNumber;
    }

    public String getEmail_Id() {
        return Email_Id;
    }

    public void setEmail_Id(String email_Id) {
        Email_Id = email_Id;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public int getTotalMails() {
        return totalMails;
    }

    public void setTotalMails(int totalMails) {
        this.totalMails = totalMails;
    }

    public List<String> getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(List<String> toAddresses) {
        this.toAddresses = toAddresses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Subject);
        dest.writeString(From);
        dest.writeString(body);
        dest.writeString(Email_Id);
        dest.writeInt(MessageNumber);
        dest.writeString(SentDate);
        dest.writeInt(position);
        dest.writeLong(UID);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeString(fromFolder);
        dest.writeByte((byte) (Seen ? 1 : 0));
        dest.writeByte((byte) (attatchment ? 1 : 0));
        dest.writeInt(totalMails);
        dest.writeStringList(toAddresses);
    }


}
