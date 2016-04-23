package ru.bonsystems.yandexcontest.internet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import ru.bonsystems.yandexcontest.Controller;
import ru.bonsystems.yandexcontest.R;

/**
 * Created by Kolomeytsev Anton on 08.04.2016.
 * This class is a part of Smart Quest Remastered project.
 * Класс, работающий с сетью. Лучше пользоваться оболочкой этого класса:
 * @see YApiMethods
 */
public class YApi {
    private static YApi ourInstance = new YApi();

    private YApi() {
    }

    public final static String GET_ARTISTS = Controller.getInstance().getString(R.string.yandex_get_artists_url);

    private final static String TAG = "YApi";

    public static GetRequest get(String link) {
        return new GetRequest(link);
    }

    public static PostRequest post(String link) {
        return new PostRequest(link);
    }

    public interface SuccessListener<R> {
        void onSuccess(R response);
    }

    public interface FailListener {
        void onFail();
    }

    public static abstract class Request<R> {
        public final String link;
        public final String requestMethod;
        public SuccessListener<R> successListener;
        public FailListener failListener;
        public Map<String, String> data;
        protected int attempts = 3;

        public Request(String link, String requestMethod) {
            this.link = link;
            this.requestMethod = requestMethod;
        }

        public Request setSuccessListener(@Nullable SuccessListener listener) {
            this.successListener = listener;
            return this;
        }

        public Request setFailListener(@Nullable FailListener failListener) {
            this.failListener = failListener;
            return this;
        }

        public Request setData(@Nullable Map<String, String> data) {
            this.data = data;
            return this;
        }

        public Request setData(@Nullable String... data) {
            if (data == null) {
                this.data = null;
                return this;
            }
            if (data.length % 2 != 0) throw new IllegalArgumentException();
            this.data = new HashMap<>();
            for (int i = 0; i < data.length; i+=2)
                this.data.put(data[i], data[i + 1]);
            return this;
        }

        public Request setAttempts(int count) {
            attempts = count;
            return this;
        }

        public void executeAsync() {
            Thread asyncThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    execute();
                }
            });
            asyncThread.setDaemon(true);
            asyncThread.start();
        }

        public abstract void execute();

        protected void fail() {
            if (attempts-- > 0)
                execute();
            else if (failListener != null)
                failListener.onFail();
        }
    }

    public static class GetRequest extends Request<String> {

        public GetRequest(String link) {
            super(link, "GET");
        }

        @Override
        public void execute() {
            try {
                // формируем и шлём запрос
                URL url = new URL(link + getParams());
                Log.d(requestMethod, url.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(requestMethod);
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                // читаем данные сервера
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                // сообщаем о получении данных
                String stringResponse = response.toString();
                Log.d(requestMethod, stringResponse);
                if (successListener != null) successListener.onSuccess(stringResponse);

            } catch (IOException e) {
                // ошибка подключения к серверу, либо соединение было прервано
                e.printStackTrace();
                fail();

            }
        }

        private String getParams() {
            if (data == null) return "";
            StringBuilder builder = new StringBuilder("?");
            for (String key : data.keySet())
                builder.append(key).append('=').append(data.get(key));
            return builder.toString();
        }

    }

    public static class PostRequest extends Request<String> {

        public PostRequest(String link) {
            super(link, "POST");
        }

        @Override
        public void execute() {
            try {
                // формируем и шлём запрос
                URL url = new URL(link);
                Log.d(requestMethod, url.toString());
                String query = getParams();
                Log.d(requestMethod, query);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(requestMethod);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);

                if (query != null) {
                    connection.setFixedLengthStreamingMode(query.getBytes().length);
                    PrintWriter stream = new PrintWriter(connection.getOutputStream());
                    stream.print(query);
                    stream.close();
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                // читаем данные сервера
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                // сообщаем о получении данных
                if (successListener != null) successListener.onSuccess(response.toString());

            } catch (IOException e) {
                // ошибка подключения к серверу, либо соединение было прервано
                e.printStackTrace();
                fail();

            }
        }

        private String getParams() throws IOException {
            if (data == null) return null;
            StringBuilder builder = new StringBuilder();
            int i = 0;
            //Uri.Builder builder = new Uri.Builder();
            for (String key : data.keySet()) {
                if (i > 0) builder.append("&");
                builder.append(key).append("=").append(URLEncoder.encode(data.get(key), "utf-8"));
                i++;
            }
            return builder.toString();
        }

    }

    public static class BitmapRequest extends Request<Bitmap> {

        public BitmapRequest(String link) {
            super(link, "GET");
        }

        @Override
        public void execute() {
            try {
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                if (successListener != null)
                    successListener.onSuccess(BitmapFactory.decodeStream(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
                fail();
            }
        }
    }
}
