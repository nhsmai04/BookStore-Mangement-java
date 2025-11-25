package org.librarymanagement.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class NotEnoughBookException extends RuntimeException{
    private  final Map<Integer,String> fieldErrors;
    public NotEnoughBookException(Map<Integer,String> fieldErrors){
        super("Không thể mượn sách");
        this.fieldErrors = fieldErrors;
    }
}

