package com.tmu.id.controller;



import com.tmu.id.auth.CustomUserDetails;
import com.tmu.id.auth.jwt.JwtTokenProvider;
import com.tmu.id.messaging.rabbitmq.RabbitMQClient;
import com.tmu.id.model.User;
import com.tmu.id.service.AuthService;
import com.tmu.id.service.UserService;
import com.tmu.id.utils.JWTutils;
import com.tmu.id.validation.UserValidation;
import com.tmu.message.MessageContent;
import com.tmu.message.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.tmu.id.model.dto.AuthorizationResponseDTO;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import java.util.Map;


@RestController
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    private RabbitMQClient rabbitMQClient;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    AuthenticationManager authenticationManager;

    //Login
    @RequestMapping(value ="/user/login", method = RequestMethod.POST)
    public ResponseMessage userLogin( @RequestBody Map<String, Object> bodyParam) {
        ResponseMessage response = null;
        if (bodyParam == null || bodyParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Invalid param value",
                    new MessageContent(HttpStatus.BAD_REQUEST.value(), "Invalid param value", null));
        } else {
            String username = (String) bodyParam.get("username");
            String password = (String) bodyParam.get("password");

            String invalidData = new UserValidation().validateLogin(username, password);
            if (invalidData != null) {
                response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                        new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
            } else {
                // Check exist account with user_name
                User existUser = userService.findByUsername(username);
                if (existUser == null) {
                    invalidData = "T??i kho???n kh??ng t???n t???i";
                    return new ResponseMessage(HttpStatus.NOT_FOUND.value(), invalidData,
                            new MessageContent(HttpStatus.NOT_FOUND.value(), invalidData, null));
                } else {

                    // X??c th???c th??ng tin ng?????i d??ng Request l??n, n???u kh??ng x???y ra exception t???c l?? th??ng tin h???p l???
                    Authentication authentication = null;
                    try {
                        authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
                    } catch (AuthenticationException ex) {
                        LOGGER.error(ex.toString());
                        invalidData = "T??i kho???n ho???c m???t kh???u kh??ng ????ng";
                        return new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), invalidData,
                                new MessageContent(HttpStatus.UNAUTHORIZED.value(), invalidData, null));
                    }
                    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // Tr??? v??? jwt cho ng?????i d??ng.
                        String accessJwt = tokenProvider.generateToken(userDetails);
                        String refreshJwt = JWTutils.createToken(userDetails.getUser().getId().toString());
                        //LoginResponse loginResponse = new LoginResponse(accessJwt, refreshJwt);
                        AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO(userDetails, accessJwt, refreshJwt);
                        response = new ResponseMessage(new MessageContent(responseDTO));

                }
            }
        }
        return response;
    }

    //Authentication
    @PostMapping("/user/authentication")
    public ResponseMessage authorized( @RequestHeader Map<String, String> headerParam) {
        ResponseMessage response = null;
        if (headerParam == null || headerParam.isEmpty()) {
            response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                    new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), null));
        } else {
            String bearerToken = headerParam.get("authorization");
            // Ki???m tra xem header Authorization c?? ch???a th??ng tin jwt kh??ng
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                try {
                    long start = System.currentTimeMillis();
                    String jwt = bearerToken.substring(7);
                    LOGGER.info("jwt => " + jwt);
                    Long id = tokenProvider.getUserIdFromJWT(jwt);
                    long end = System.currentTimeMillis();
                    LOGGER.info("Parse token in: {} ms", (end-start), jwt);
                    UserDetails userDetails = authService.loadUserById(id);
                    end = System.currentTimeMillis();
                    LOGGER.info("Get UserDetails in: {} ms", (end-start), jwt);
                    if (userDetails != null) {
                        User user = ((CustomUserDetails) userDetails).getUser();
                        
                            UsernamePasswordAuthenticationToken authentication
                                    = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            end = System.currentTimeMillis();
                            LOGGER.info("Save authentication for Spring security in: {} ms", (end-start), jwt);
                            AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO((CustomUserDetails) authentication.getPrincipal(), null, null);
                            end = System.currentTimeMillis();
                            LOGGER.info("Authen JWT Token in: {}ms for {}", (end-start), jwt);
                            response = new ResponseMessage(new MessageContent(responseDTO));

                    } else {
                        response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(),
                                new MessageContent(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null));
                    }
                } catch (Exception ex) {
                    LOGGER.error("failed on set user authentication", ex);
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                            new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), null));
                }
            } else {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                        new MessageContent(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), null));
            }
        }
        return response;
    }

}
