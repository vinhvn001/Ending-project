package com.tmu.room.model.dto;



import com.tmu.utils.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class AuthorizationResponseDTO {



    private Long id;

    private String username;

    private String fullName;

    private String roleName;

    private Date createdAt;

    public AuthorizationResponseDTO(Map<String, Object> map){

        if (map != null && map.size() > 0) {
            if(map.containsKey("username")){
                this.username = (String) map.get("username");
            }
        }
        if (map != null && map.size() > 0) {
            if(map.containsKey("fullName")){
                this.fullName = (String) map.get("fullName");
            }
        }
        if (map != null && map.size() > 0) {
            if(map.containsKey("roleName")){
                this.roleName = (String) map.get("roleName");
            }
        }
        if (map != null && map.size() > 0) {
            if(map.containsKey("createdAt") && map.get("createdAt") != null ){
                this.createdAt = DateUtil.getDateTime((String) map.get("createdAt"), "yyyy-MM-dd HH:mm:ss");
            }
        }

    }
}
