package com.master.chat.common.exception;

import com.master.chat.common.enums.ResponseEnum;
import lombok.Data;

/**
 * 系统异常
 *
 * @author: Yang
 * @date: 2020/3/4
 * @version: 1.0.0
 * Copyright Ⓒ 2023 Master Computer Corporation Limited All rights reserved.
 */
@Data
public class ErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;

    public ErrorException() {
        this.code = ResponseEnum.ERROR.getCode();
        this.msg = ResponseEnum.ERROR.getMsg();
    }

    public ErrorException(String msg) {
        super(msg);
        this.code = ResponseEnum.ERROR.getCode();
        this.msg = msg;
    }

}
