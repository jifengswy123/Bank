package com.example.finance.common;

public class R<T> {
    private int code;        // 200=成功，其他=失败
    private String message;  // 提示信息
    private T data;          // 业务数据

    private R(int code, String message, T data) {
        this.code = code; this.message = message; this.data = data;
    }
    public static <T> R<T> ok()                  { return new R<>(200, "success", null); }
    public static <T> R<T> ok(T data)            { return new R<>(200, "success", data); }
    public static <T> R<T> fail(String message)  { return new R<>(500, message, null); }
    public static <T> R<T> fail(int code, String message) { return new R<>(code, message, null); }

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}