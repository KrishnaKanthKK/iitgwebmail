package com.abc.iitgwebmailnotifier.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by aarkay0602 on 17/2/17.
 */

public class Folder implements Parcelable {
    private List<Email> emails;
    private String name;

    public Folder(){

    }

    protected Folder(Parcel in) {
        in.readList(emails,null);
        in.writeString(name);
    }

    public static final Parcelable.Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel in) {
            return new Folder(in);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(emails);
        dest.writeString(name);
    }
}
