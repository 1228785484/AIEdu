package org.sevengod.javabe.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AjaxResult {
    private static final int SUCCESS = 200;
    private static final int ERROR = 500;
    
    private int code;
    private String msg;
    private Object data;
    
    public static AjaxResult success() {
        return new AjaxResult().setCode(SUCCESS);
    }
    
    public static AjaxResult success(String msg) {
        return new AjaxResult().setCode(SUCCESS).setMsg(msg);
    }
    
    public static AjaxResult success(Object data) {
        return new AjaxResult().setCode(SUCCESS).setData(data);
    }
    
    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult().setCode(SUCCESS).setMsg(msg).setData(data);
    }
    
    public static AjaxResult error() {
        return new AjaxResult().setCode(ERROR);
    }
    
    public static AjaxResult error(String msg) {
        return new AjaxResult().setCode(ERROR).setMsg(msg);
    }
} 