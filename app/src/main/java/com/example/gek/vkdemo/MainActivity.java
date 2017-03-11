package com.example.gek.vkdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


// id 5917418
public class MainActivity extends AppCompatActivity {

    private Button btnFriends;
    private TextView tvResult;
    public static final String TAG = "VK_DEMO";

    private static final String[] sMyScope = new String[]{
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS,
            VKScope.MESSAGES,
            VKScope.DOCS,
            VKScope.EMAIL
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = (TextView) findViewById(R.id.tvResult);
        btnFriends = (Button) findViewById(R.id.btnFriends);
        btnFriends.setOnClickListener(friendsListener);

        VKSdk.login(this, sMyScope);
    }

    View.OnClickListener friendsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // формируем параметры, которые хотим получить в запросе
            //  id,first_name,last_name,sex,bdate,city
            VKParameters fields = VKParameters.from(VKApiConst.FIELDS, "first_name, last_name, bdate");
            // формируем запрос
            VKRequest vkRequest = VKApi.friends().get(fields);

            // выполняем запрос, устанавливая лисенер, который сработает по результату работы запроса
            vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    //tvResult.setText(response.json.toString());

                    JSONObject jsonResponse = response.json;

                    ArrayList<Friend> friends = parseFriends(jsonResponse);
                    String s = "";
                    for (Friend friend: friends) {
                        s += friend.getFirst_name() + " " + friend.getLast_name() +
                                " (" + friend.getBdate() + ")\n";
                    }
                    tvResult.setText(s);
                }

                @Override
                public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                    super.attemptFailed(request, attemptNumber, totalAttempts);
                }

                @Override
                public void onError(VKError error) {
                    super.onError(error);
                }

                @Override
                public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                    super.onProgress(progressType, bytesLoaded, bytesTotal);
                }
            });
        }
    };


    // Парсим весь json ответ с сервера, выбирая записи друзей
    private ArrayList<Friend> parseFriends(JSONObject jsonResponse){
        ArrayList<Friend> friends = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonResponse.getJSONObject("response").getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                friends.add(jsonToFriend(jsonArray.optJSONObject(i)));
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
        return friends;
    }


    // Преобразуем json строку в модель
    private Friend jsonToFriend(JSONObject jsonObject){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(jsonObject.toString(), Friend.class);
    }



    // Результат возвращаемый окном авторизации ВКонтакте
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
                Log.d(TAG, "onResult: authentication successful: \n EMAIL = " + res.email);
                btnFriends.setEnabled(true);
            }
            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Log.d(TAG, "onError: authentication" +  error.toString());
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
