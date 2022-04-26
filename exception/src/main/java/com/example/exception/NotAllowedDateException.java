package com.example.exception;

public class NotAllowedDateException extends  RuntimeException{

    public NotAllowedDateException(String message){
        super(message);
    }
}
