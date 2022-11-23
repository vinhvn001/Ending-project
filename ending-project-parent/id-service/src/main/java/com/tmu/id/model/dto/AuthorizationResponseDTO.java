package com.tmu.id.model.dto;

import com.tmu.id.auth.CustomUserDetails;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AuthorizationResponseDTO {

    private String accessToken;

    private String refreshToken;

    private Long id;

    private String username;

    private String fullName;

    private String roleName;

    private Date createdAt;

    public AuthorizationResponseDTO(CustomUserDetails userDetails, String accessToken, String refreshToken){
        this.accessToken = accessToken;

        this.refreshToken = refreshToken;

        this.id = userDetails.getUser().getId();

        this.username = userDetails.getUser().getUserName();

        this.fullName = userDetails.getUser().getFullName();

        this.roleName = userDetails.getUser().getRoleName();

        this.createdAt = userDetails.getUser().getCreatedAt();

    }
}
