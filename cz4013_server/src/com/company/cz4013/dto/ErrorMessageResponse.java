package com.company.cz4013.dto;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

public class ErrorMessageResponse extends BaseXYZZObject {

    @XYZZFieldAnnotation(order = 1)
    private final String errorMsg;

    public ErrorMessageResponse(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

}
