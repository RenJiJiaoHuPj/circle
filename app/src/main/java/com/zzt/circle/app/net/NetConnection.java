package com.zzt.circle.app.net;

import android.os.AsyncTask;
import com.zzt.circle.app.Config;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Created by zzt on 15-5-25.
 */
public class NetConnection {
    /**
     * @param url
     * @param method
     * @param parameters
     */
    public NetConnection(final String url, final HttpMethod method, final SuccessCallBack successCallBack, final FailCallBack failCallBack, final Map<String, String> parameters) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                StringBuffer paramsStr = new StringBuffer();
                try {
                    for (Map.Entry<String, String> entry : parameters.entrySet()) {
                        paramsStr.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    }
                    paramsStr.deleteCharAt(paramsStr.length() - 1);
                    System.out.println("request: " + url + "\n" + paramsStr);
                    URLConnection uc;
                    switch (method) {
                        case POST:
                            uc = new URL(url).openConnection();
                            uc.setDoOutput(true);
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(uc.getOutputStream(), Config.CHARSET));
                            bw.write(paramsStr.toString());
                            bw.flush();
                            break;
                        default:
                            uc = new URL(url + "?" + paramsStr.toString()).openConnection();
                            break;
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream(), Config.CHARSET));
                    String line = null;
                    StringBuffer result = new StringBuffer();
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }
                    System.out.println("result: " + result);
                    return result.toString();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                if (result != null) {
                    if (successCallBack != null)
                        successCallBack.onSuccess(result);
                } else {
                    if (failCallBack != null)
                        failCallBack.onFail();
                }

                super.onPostExecute(result);
            }
        }.execute();
    }

    public interface SuccessCallBack {
        void onSuccess(String result);
    }

    public interface FailCallBack {
        void onFail();
    }
}
