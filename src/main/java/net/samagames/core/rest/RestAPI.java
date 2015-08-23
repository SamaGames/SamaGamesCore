package net.samagames.core.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.samagames.core.rest.request.Request;
import net.samagames.core.rest.response.ErrorResponse;
import net.samagames.core.rest.response.Response;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class RestAPI
{
    private static final RestAPI INSTANCE = new RestAPI();
    private static final String ENDPOINT = "http://localhost:2000/";
    private static final Gson GSON = new GsonBuilder().create();
    static String user, pass;

    private RestAPI()
    {

    }

    public static RestAPI getInstance()
    {
        return INSTANCE;
    }


    public void setup(String newUser, String newPass)
    {
        user = newUser;
        pass = newPass;
    }

    public String createRequestJSON(Request request)
    {
        JsonElement tmp = GSON.toJsonTree(request);
        tmp.getAsJsonObject().addProperty("apiLogin", user);
        tmp.getAsJsonObject().addProperty("apiPassword", pass);
        return GSON.toJson(tmp);
    }

    public Response sendRequest(String point, Request request, Class<? extends Response> responseClass, String method)
    {
        String json = createRequestJSON(request);
        byte[] postDataByte = json.getBytes();

        // Open the connection and specify the headers
        try
        {
            URL url = new URL(ENDPOINT + point);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", Integer.toString(postDataByte.length));
            connection.setConnectTimeout(2000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Write the data
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(postDataByte);
            wr.flush();
            wr.close();
            // True if no problem, false otherwise
            boolean status = connection.getResponseCode() == 200;

            BufferedReader br = new BufferedReader(new InputStreamReader(status ? connection.getInputStream() : connection.getErrorStream()));

            if (status)
            {
                return responseClass == null ? new Response() : GSON.fromJson(br, responseClass);
            }
            else
            {
                return GSON.fromJson(br, ErrorResponse.class);
            }
        } catch (IOException e)
        {
            return new ErrorResponse("request_error", e.getLocalizedMessage());
        }
    }

    public static Gson getGSON()
    {
        return GSON;
    }
}
