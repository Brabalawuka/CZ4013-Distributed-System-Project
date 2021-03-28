package com.company.cz4013.dto.response;

import com.company.cz4013.base.dto.BaseXYZZObject;
import com.company.cz4013.base.dto.XYZZFieldAnnotation;

/**
 * Response for unexpected errors occurred during handling the request
 */
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
