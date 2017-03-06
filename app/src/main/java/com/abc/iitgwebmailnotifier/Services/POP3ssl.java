package com.abc.iitgwebmailnotifier.Services;

import android.util.Log;

import com.abc.iitgwebmailnotifier.models.Body;
import com.abc.iitgwebmailnotifier.models.Email;
import com.sun.mail.imap.IMAPFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by aarkay0602 on 15/2/17.
 */

public class POP3ssl {

    private List<Email> EmailsInList = new ArrayList<>();

    private List<String> FolderNames = new ArrayList<>();

    private Store emailStore;

    private Body body;

    public POP3ssl(){

    }

    public String loginuser(String username, String password, String server,String protocol,Boolean flag){
        // connect to my pop3 inbox
        String a = "javax.net.ssl.SSLSocketFactory";
        try {
            Properties props = new Properties();
            props.setProperty("mail."+protocol+".host" , server);
            props.setProperty("mail."+protocol+".port" , "993");
            props.setProperty("mail."+protocol+".user" , username);
            // Start SSL connection
            props.setProperty("mail."+protocol+".ssl.enable", "true");
            props.put("mail."+protocol+".socketFactory" , "993");
            props.setProperty("mail."+protocol+".socketFactory.class" , a );

            Session session = Session.getInstance(props);


            emailStore = session.getStore(protocol+"s");
            emailStore.connect(server,993,username, password);
            if (flag){
                emailStore.close();
            }
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (AuthenticationFailedException e){
            e.printStackTrace();
            return "Failed";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "FAILED";

        } catch (Exception e){
            e.printStackTrace();
            return "FAILED";
        }
        return "OK";
    }
    public String copyEmails(String user,String pass,String server,List<Email> emails,String fromFolder,String toFolder){
        loginuser(user,pass,server,"imap",false);
        try {
            IMAPFolder from;
            if (fromFolder.equals("Sent")){
                from = (IMAPFolder) emailStore.getFolder("INBOX");
                from.open(Folder.READ_WRITE);
                from = (IMAPFolder) from.getFolder("Sent");
                from.open(Folder.READ_WRITE);
            }else {
                from = (IMAPFolder) emailStore.getFolder(fromFolder);
                from.open(Folder.READ_WRITE);
            }
            IMAPFolder to = (IMAPFolder) emailStore.getFolder(toFolder);
            long[] uids = new long[emails.size()];
            for (int i = 0; i < emails.size(); i++) {
                uids[i] = emails.get(i).getUID();
            }
            from.copyMessages(from.getMessagesByUID(uids),to);
            from.close(true);
            emailStore.close();
        } catch (MessagingException e) {
            e.printStackTrace();
            return "FAILED";
        } catch (Exception e){
            e.printStackTrace();
            return "FAILED";
        }
        return "OK";
    }

    public String moveEmails(String user,String pass,String server,List<Email> emails,String fromFolder,String toFolder){
        loginuser(user,pass,server,"imap",false);
        try {
            IMAPFolder from;
            if (fromFolder.equals("Sent")){
                from = (IMAPFolder) emailStore.getFolder("INBOX");
                from.open(Folder.READ_WRITE);
                from = (IMAPFolder) from.getFolder("Sent");
                from.open(Folder.READ_WRITE);
            }else {
                from = (IMAPFolder) emailStore.getFolder(fromFolder);
                from.open(Folder.READ_WRITE);
            }
            IMAPFolder to = (IMAPFolder) emailStore.getFolder(toFolder);
            long[] uids = new long[emails.size()];
            for (int i=0;i<emails.size();i++){
                uids[i] = emails.get(i).getUID();
            }
            from.copyMessages(from.getMessagesByUID(uids),to);
            for (Message m: from.getMessagesByUID(uids)){
                m.setFlag(Flags.Flag.DELETED, true);
            }
            from.close(true);
            emailStore.close();
        }catch (MessagingException e){
            e.printStackTrace();
            return "FAILED";
        }
        return "OK";

    }

    public String deleteEmails(String user,String pass,String server,List<Email> emails,String folder){
        loginuser(user,pass,server,"imap",false);
        try {
            IMAPFolder inbox;
            if (folder.equals("Sent")){
                inbox = (IMAPFolder) emailStore.getFolder("INBOX");
                inbox.open(Folder.READ_WRITE);
                inbox = (IMAPFolder) inbox.getFolder("Sent");
                inbox.open(Folder.READ_WRITE);
            }else {
                inbox = (IMAPFolder) emailStore.getFolder(folder);
                inbox.open(Folder.READ_WRITE);
            }
            for (Email e: emails){
                Message message = inbox.getMessageByUID(e.getUID());
                message.setFlag(Flags.Flag.DELETED, true);
            }
            inbox.close(true);
            emailStore.close();
        } catch (MessagingException e) {
            e.printStackTrace();
            return "FAILED";
        } catch (Exception e){
            e.printStackTrace();
            return "FAILED";
        }
        return "OK";
    }
    public Body loadBody(String username,String password,String server,long UID,String folder) {
        if (loginuser(username, password, server,"imap", false).equals("OK")){
            String content = null;
            try {
                body = new Body();
                IMAPFolder inbox;
                if (folder.equals("Sent")){
                    inbox = (IMAPFolder) emailStore.getFolder("INBOX");
                    inbox.open(Folder.READ_ONLY);
                    inbox = (IMAPFolder) inbox.getFolder("Sent");
                    inbox.open(Folder.READ_ONLY);
                }else {
                    inbox = (IMAPFolder) emailStore.getFolder(folder);
                    inbox.open(Folder.READ_WRITE);
                }
                Message message = inbox.getMessageByUID(UID);
                Object msgContent = message.getContent();
                content = "";
     /* Check if content is pure text/html or in parts */
                if (msgContent instanceof Multipart) {
                    Log.e("1","1");
                    Multipart multipart = (Multipart) msgContent;
                    for (int j = 0; j < multipart.getCount(); j++) {
                        Log.e("2","2");
                        BodyPart bodyPart = multipart.getBodyPart(j);
                        String disposition = bodyPart.getDisposition();
                        if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) {
                            System.out.println("Mail have some attachment");
                            DataHandler handler = bodyPart.getDataHandler();
                            System.out.println("file name : " + handler.getName());
                        } else {
                            Log.e("3","3");
                            content = getText(bodyPart).getContent();// the changed code
                            body.setContent(content);
                        }
                    }
                } else {
                    Log.e("4","4");
                    content = message.getContent().toString();
                    body.setContent(content);
                    body.setHtml(false);
                }
                inbox.close(true);
                emailStore.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("5","5");
            return body;
        }else{
            body.setContent("error");
            Log.e("6","6");
            return body;
        }

    }

    public String createFolder(String user,String pass,String server,String folderName) {
        boolean isCreated = true;
        loginuser(user,pass,server,"imap",false);
        try {
            Folder defaultFolder = emailStore.getDefaultFolder();
            Folder newFolder = defaultFolder.getFolder(folderName);
            isCreated = newFolder.create(Folder.HOLDS_MESSAGES);
        } catch (MessagingException e) {
            e.printStackTrace();
            return "FAILED";
        } catch (Exception e){
            e.printStackTrace();
            return "FAILED";
        }return "OK";

    }

    public List<String> getFolderNames(String user, String pass, String server){
        loginuser(user, pass, server,"imap", false);
        try {
            Folder[] f = emailStore.getDefaultFolder().list();
            for (Folder a : f){
                FolderNames.add(a.getName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return FolderNames;
    }
    public String deleteFolder(String user,String pass,String server,String folderName){
        loginuser(user,pass,server,"imap",false);
        boolean isDeleted=false;
        try {
            IMAPFolder folder = (IMAPFolder) emailStore.getFolder(folderName);
            folder.delete(true);
            Log.e("delete", String.valueOf(isDeleted));
            emailStore.close();
        } catch (MessagingException e) {
            e.printStackTrace();
            return "FAILED";
        } catch (Exception e){
            e.printStackTrace();
            return "FAILED";
        }
        return "OK";
    }
    public List<Email> getEmails(String username, String password, String server, int count,
                                 String position,String folderName,int mailset){
        loginuser(username, password, server,"imap", false);
        try {
            IMAPFolder inbox;
            if (folderName.equals("Sent")){
                inbox = (IMAPFolder) emailStore.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);
                inbox = (IMAPFolder) inbox.getFolder("Sent");
                inbox.open(Folder.READ_ONLY);
            }else {
                inbox = (IMAPFolder) emailStore.getFolder(folderName);
                inbox.open(Folder.READ_ONLY);
            }
            Message[] messages = inbox.getMessages();
            int messageCount = inbox.getMessageCount();
            for (int i = messageCount-1-(mailset-1)*count; i >= (messageCount-count-(mailset-1)*count); i--) {
                Email email = new Email();
                email.setFrom(String.valueOf(messages[i].getFrom()[0]).replaceAll("[\"]",""));
                email.setSubject(messages[i].getSubject());
                email.setUID(inbox.getUID(messages[i]));
                email.setSeen(messages[i].isSet(Flags.Flag.SEEN));
                Log.e("id", String.valueOf(inbox.getUID(messages[i])));
                email.setMessageNumber(messages[i].getMessageNumber());
                email.setSentDate(getDate(messages[i].getSentDate().getTime()));
                email.setFromFolder(folderName);
                email.setTotalMails(messageCount);
                List<String> toAddresses = new ArrayList<String>();
                Address[] recipients = messages[i].getRecipients(Message.RecipientType.TO);
                for (Address address : recipients) {
                    toAddresses.add(address.toString());
                }
                email.setToAddresses(toAddresses);
                EmailsInList.add(email);
            }
            inbox.close(true);
            emailStore.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e ){
            e.printStackTrace();
        }
        return EmailsInList;
    }

    private String getDate(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String year = android.text.format.DateFormat.format("yyyy",calendar).toString();
        String date = android.text.format.DateFormat.format("dd-MM-yyyy",calendar).toString();
        long timestamp = System.currentTimeMillis();
        String d = "";
        if (!getYear(timestamp).equals(year)){
            d = android.text.format.DateFormat.format("dd/MM/yy",calendar).toString();
        }else if (!getDateCalender(timestamp).equals(date)){
            d = android.text.format.DateFormat.format("MMM dd",calendar).toString();

        }else if (getDateCalender(timestamp).equals(date)){
            int hour = Integer.parseInt(android.text.format.DateFormat.format("HH",calendar).toString());
            if (hour>12){
                hour -= 12;
                d = android.text.format.DateFormat.format(hour+":mm",calendar).toString();
                d += " PM";
            }else{
                d = android.text.format.DateFormat.format("HH:mm",calendar).toString();
                d += " AM";
            }

        }

        return d;

    }
    private String getYear(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String year = android.text.format.DateFormat.format("yyyy",calendar).toString();
        return year;
    }
    private String getDateCalender(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = android.text.format.DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }


    private Body getText(Part p) throws
            MessagingException, IOException {
        boolean textIsHtml = false;
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            textIsHtml = p.isMimeType("text/html");
            body.setHtml(textIsHtml);
            body.setContent(s);
            return body;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp).getContent();
                        body.setContent(text);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp).getContent();
                    body.setHtml(bp.isMimeType("text/html"));
                    body.setContent(s);
                    if (s != null)
                        return body;
                } else {
                    body.setContent(getText(bp).getContent());
                    return body;
                }
            }
            return body;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i)).getContent();
                body.setContent(s);
                if (s != null)
                    return body;
            }
        }

        return null;
    }


    public void SendEmail (String protocol){

            // Recipient's email ID needs to be mentioned.
            Log.e("started","started");
            String to = "rajinikanth0602@gmail.com";
            // Sender's email ID needs to be mentioned
            String from = "m.rajanikanth@iitg.ernet.in";
            final String username = "m.rajanikanth";//change accordingly
            final String password = "rk_normal";//change accordingly

            // Assuming you are sending email through relay.jangosmtp.net
            String host = "manas.iitg.ernet.in";

        String a = "javax.net.ssl.SSLSocketFactory";
        Properties props = new Properties();
        props.setProperty("mail."+protocol+".host" , host);
        props.setProperty("mail."+protocol+".port" , "465");
        props.setProperty("mail."+protocol+".user" , username);
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props);
            try {
                // Create a default MimeMessage object.
                Message message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(from));

                // Set To: header field of the header.
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));

                // Set Subject: header field
                message.setSubject("Testing Subject");

                // Now set the actual message
                message.setText("Hello, this is sample for to check send " +
                        "email using JavaMailAPI ");
                Log.e("before tr ","before tr ");
                Transport tr = session.getTransport("smtp");
                Log.e("after","after");
                tr.connect("manas.iitg.ernet.in", username, password);
                Log.e("connected","connected");
                message.saveChanges();
                tr.sendMessage(message, message.getAllRecipients());
                tr.close();

                Log.e("asda","Sent message successfully....");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

    }




}
