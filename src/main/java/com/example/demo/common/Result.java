package com.example.demo.common;

import lombok.Data;

/**
 * 通用响应结果类
 */
@Data
public class Result<T> {
  private boolean success;
  private int code;
  private String message;
  private T data;

  private Result() {
  }

  public static <T> Result<T> success() {
    Result<T> result = new Result<>();
    result.success = true;
    result.code = 200;
    result.message = "操作成功";
    return result;
  }

  public static <T> Result<T> success(T data) {
    Result<T> result = success();
    result.data = data;
    return result;
  }

  public static <T> Result<T> success(String message, T data) {
    Result<T> result = success(data);
    result.message = message;
    return result;
  }

  public static <T> Result<T> error(int code, String message) {
    Result<T> result = new Result<>();
    result.success = false;
    result.code = code;
    result.message = message;
    result.data = null;
    return result;
  }

  public static <T> Result<T> error(String message) {
    return error(400, message);
  }
}