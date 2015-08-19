package net.samagames.core.rest.response;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class ErrorResponse extends Response
{
    private String error;
    private String errorMessage;

    public ErrorResponse()
    {

    }

    public ErrorResponse(String error, String errorMessage)
    {
        this.error = error;
        this.errorMessage = errorMessage;
    }
}
