package com.bandwidth.androidreference;

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

    public static final String APPLICATION_SERVER_URL = "https://enigmatic-sea-2283.herokuapp.com";

    public static User getUser(String username, String password) {
        User user = null;
        try {
            Gson gson = new Gson();
            Login login = new Login(username, password);
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(APPLICATION_SERVER_URL + "/users");
            StringEntity requestBody = new StringEntity(gson.toJson(login));
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
