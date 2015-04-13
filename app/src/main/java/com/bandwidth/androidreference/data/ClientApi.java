package com.bandwidth.androidreference.data;

import android.content.Context;

import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.data.Login;
import com.bandwidth.androidreference.data.User;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ClientApi {

    public static User getUser(Context context, String username, String password) {
        User user = null;
        try {
            Gson gson = new Gson();
            Login login = new Login(username, password);
            HttpClient client = new DefaultHttpClient();
            String serverUrl = context.getResources().getString(R.string.application_server_url);
            HttpPost post = new HttpPost(serverUrl + "/users");
            StringEntity requestBody = new StringEntity(gson.toJson(login));
            requestBody.setContentType("application/json");
            post.setEntity(requestBody);
            HttpResponse response = client.execute(post);
            String responseBody = EntityUtils.toString(response.getEntity());
            user = gson.fromJson(responseBody, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}
