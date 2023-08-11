package com.thoughtworks.fast.rent.model.thirdparty.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningManagementResponse {

    private String code;

    private String message;

    @JsonIgnore
    public boolean isSuccess(){
        return StringUtils.equalsIgnoreCase("SUCCESS", code);
    }

    public static LearningManagementResponse success(){
        return LearningManagementResponse.builder().code("SUCCESS").message("同步成功").build();
    }
}
