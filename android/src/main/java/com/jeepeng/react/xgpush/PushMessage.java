package com.jeepeng.react.xgpush;

import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.util.Log;

public class PushMessage {
    private static final PushMessage ourInstance = new PushMessage();

    public static PushMessage getInstance() {
        return ourInstance;
    }

    private String Title = null;
    private String Content = null;
    private String CustomContent = null;
    public boolean hasValue = false;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getCustomContent() {
        return CustomContent;
    }

    public void setCustomContent(String customContent) {
        CustomContent = customContent;
    }

    public void clearAll() {
        CustomContent = null;
        Content = null;
        Title = null;
        hasValue = false;
    }

    public void setAllValue (String title, String content, String customContent) {
        this.Title = title;
        this.Content = content;
        this.CustomContent = customContent;
        this.hasValue = true;
    }



    private PushMessage() {
    }

    public void setAllValueIntentUrl (Uri pushUri) {

        if(pushUri != null) {

            String title= pushUri.getQueryParameter("title");
            String content= pushUri.getQueryParameter("content");
            String customContent = pushUri.getQueryParameter("custom_content");

            Log.d("XGQQ_PushMessage", "1. " + title);
            Log.d("XGQQ_PushMessage", "2. " + content);
            Log.d("XGQQ_PushMessage", "3. " + customContent);

            setAllValue(title, content, customContent);
        }
    }

}
