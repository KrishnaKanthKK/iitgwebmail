package com.abc.iitgwebmailnotifier.models;

/**
 * Created by aarkay0602 on 4/3/17.
 */

public class Body {
    private String Content;
    private boolean html=false;

    public String getContent() {
        return Content;
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
