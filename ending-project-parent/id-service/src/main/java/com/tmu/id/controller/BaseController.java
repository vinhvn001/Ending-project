package com.tmu.id.controller;

import com.tmu.id.auth.CustomUserDetails;
import com.tmu.id.auth.jwt.JwtTokenProvider;
import com.tmu.id.messaging.rabbitmq.RabbitMQClient;
import com.tmu.id.model.User;
import com.tmu.id.model.dto.AuthorizationResponseDTO;
import com.tmu.id.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;


import java.util.*;


public class BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private RabbitMQClient rabbitMQClient;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthService authService;

    
    public AuthorizationResponseDTO getAuthorFromToken(Map<String, String> headerParam) {
        if (headerParam == null || (!headerParam.containsKey("authorization")
                && !headerParam.containsKey("Authorization"))) {
            return null;
        }
        String bearerToken = headerParam.get("authorization");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            try {
                String jwt = bearerToken.substring(7);
                Long id = tokenProvider.getUserIdFromJWT(jwt);
                UserDetails userDetails = authService.loadUserById(id);
                if (userDetails != null) {
                    User user = ((CustomUserDetails) userDetails).getUser();

                        UsernamePasswordAuthenticationToken authentication
                                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO((CustomUserDetails) authentication.getPrincipal(), null, null);
                        return responseDTO;

                }
            } catch (Exception ex) {
                LOGGER.error("failed on set user authentication", ex);
                return null;
            }
        }
        return null;
    }
}
