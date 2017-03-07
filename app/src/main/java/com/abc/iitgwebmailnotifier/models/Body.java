package com.abc.iitgwebmailnotifier.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aarkay0602 on 4/3/17.
 */

public class Body {
    private String Content;

    private boolean html=false;

    private List<String> recipients= new ArrayList<>();

    public String getContent() {
        return Content;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void addRecipient(String recipient){
        this.recipients.add(recipient);
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public void setContent(String content) {
        Content = content;
    }

    public boolean isHtml() {
        return html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }
}
