package com.ibm.sttcustomization.model;

import com.ibm.sttcustomization.utils.Utils;
import org.json.JSONObject;

public class GenericException extends Exception {
    public enum ErrorCode {
        GENERAL,
        HTTPSTATUSCODEEXCEPTION
    }

    private static final long serialVersionUID = 7718828512143293558L;

    private final ErrorCode code;

    public GenericException(ErrorCode code) {
        super();
        this.code = code;
    }

    public GenericException(String message, Throwable cause, ErrorCode code) {
        super(message, cause);
        this.code = code;
    }

    public GenericException(JSONObject jso, Throwable cause, ErrorCode code) {
        super(Utils.extractError(jso), cause);
        this.code = code;
    }

    public GenericException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }

    public GenericException(Throwable cause, ErrorCode code) {
        super(cause);
        this.code = code;
    }

    public ErrorCode getCode() {
        return this.code;
    }
}