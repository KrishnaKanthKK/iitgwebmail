package com.abc.iitgwebmailnotifier.Services;

import android.util.Log;

import com.abc.iitgwebmailnotifier.models.Email;
import com.abc.iitgwebmailnotifier.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * Created by aarkay0602 on 14/2/17.
 */

public class JSONSerializer {
    public User createUserObject(String username,String password,String server){
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setServer(server);
        return user;
    }

    public List<Email> createEmailListObject(Message[] messages, int count,String position){
        List<Email> emails = new ArrayList<>();
        try {
            for (int i = 0; i < count; i++){
                Email email = new Email();
                email.setFrom(messages[i].getFrom()[0].toString());
                email.setBody((String) messages[i].getContent());
                email.setSubject(messages[i].getSubject());
                emails.add(email);
            }
        }catch (MessagingException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emails;

    }

}
