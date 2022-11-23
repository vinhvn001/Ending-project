package com.tmu.id.model.dto;

import com.tmu.id.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {


    private Long id;


    private String username;


    private String fullName;


    private String roleName;

    public UserDTO(User user){
        this.id = user.getId();
        this.username =user.getUserName();
        this.fullName = user.getFullName();
        this.roleName = user.getRoleName();
    }

    public UserDTO(AuthorizationResponseDTO user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.roleName = user.getRoleName();
    }



}
