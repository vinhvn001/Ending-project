package com.tmu.id.controller;



import com.tmu.id.auth.jwt.JwtTokenProvider;
import com.tmu.id.messaging.rabbitmq.RabbitMQClient;
import com.tmu.id.model.User;
import com.tmu.id.model.dto.AuthorizationResponseDTO;
import com.tmu.id.model.dto.UserDTO;
import com.tmu.id.service.AuthService;
import com.tmu.id.service.UserService;
import com.tmu.id.validation.UserValidation;
import com.tmu.message.MessageContent;
import com.tmu.message.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
public class UserController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthService authService;

    @Autowired
    private RabbitMQClient rabbitMQClient;

    @GetMapping("/user/findAll")
    public ResponseMessage getAllUser( @RequestHeader Map<String, String> headerParam) {
        ResponseMessage response =null;

        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {

            if (dto.getRoleName().equalsIgnoreCase("admin")) {
                List<User> allUser = userService.findAll();
                if (allUser == null || allUser.isEmpty()) {
                    response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(),
                            new MessageContent(HttpStatus.OK.value(), HttpStatus.OK.toString(), null));
                } else
                    response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.toString(),
                            new MessageContent(HttpStatus.OK.value(), HttpStatus.OK.toString(),
                                    allUser));
            }else {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền quản lý danh sách tài khoản",
                        new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền quản lý danh sách tài khoản", null));
            }
        }
        return response;
    }

    @GetMapping("/user")
    public ResponseMessage getDetailUser(@RequestHeader Map<String, String> headerParam) {
        ResponseMessage response;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            Long id = dto.getId();
            User user = userService.findById(id);
            if (user == null) {
                response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Không tìm thấy user",
                        new MessageContent(HttpStatus.NOT_FOUND.value(), "Không tìm thấy user", null));
            } else {
                UserDTO detailDTO = new UserDTO(user);
                response = new ResponseMessage(new MessageContent(detailDTO));
            }
        }
        return response;
    }

    @GetMapping("/user/findUser/{id}")
    public ResponseMessage getDetailUser(@PathVariable String id, @RequestHeader Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            String role = dto.getRoleName();
            Long ID = dto.getId();
            Long Id = Long.parseLong(id);
            if(role.equalsIgnoreCase("admin") || ID == Id){
                    User user = userService.findById(Id);
                    if (user == null) {
                        response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "User không tồn tại",
                                new MessageContent(HttpStatus.NOT_FOUND.value(), "User không tồn tại", null));
                    } else {
                        UserDTO detailDTO = new UserDTO(user);
                        response = new ResponseMessage(new MessageContent(detailDTO));
                    }
                }else {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền xem chi tiết tài khoản này",
                        new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền xem chi tiết tài khoản này", null));
            }
        }
        return response;
    }

    @PostMapping("/user/create")
    public ResponseMessage createUser(@RequestBody Map<String, Object> bodyParam, @RequestHeader Map<String, String> headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            String role = dto.getRoleName();
            if(role.equalsIgnoreCase("admin") ){
                if (bodyParam == null || bodyParam.isEmpty()) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Invalid param value",
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), "Invalid param value", null));
                }else{
                    String username = (String)bodyParam.get("username");
                    String password = (String)bodyParam.get("password");
                    String fullName = (String)bodyParam.get("fullName");
                    String roleName = (String)bodyParam.get("roleName");

                    User user = new User();
                    user.setUserName(username);
                    user.setPassword(password);
                    user.setFullName(fullName);
                    user.setRoleName(roleName);

                    String invalidData = new UserValidation().validateUpsertUser(user);
                    if (invalidData != null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                    }else{
                        User existUser = null;
                        // check if username already exist
                        if(user.getUserName() != null){
                            existUser = userService.findByUsername(user.getUserName());
                        }
                        if(existUser != null){
                            invalidData = " Đã tồn tại username trên hệ thống";
                            response = new ResponseMessage(HttpStatus.CONFLICT.value(), invalidData,
                                    new MessageContent(HttpStatus.CONFLICT.value(), invalidData,
                                            null));
                        }else{
                            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
                            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                            try {
                                userService.save(user);
                                response = new ResponseMessage(HttpStatus.CREATED.value(), HttpStatus.CREATED.getReasonPhrase(),
                                        new MessageContent(user));
                            } catch (Exception ex){
                                response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                        new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null));
                            }
                        }
                    }
                }
            }else {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền tạo user mới",
                        new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền tạo user mới", null));
            }
        }
        return response;
    }

    @PutMapping("/user/{id}")
    public ResponseMessage updateUser(@PathVariable String id, @RequestBody Map<String, Object> bodyParam, @RequestHeader Map<String, String > headerParam){
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        }else{
            String role = dto.getRoleName();
            if(role.equalsIgnoreCase("admin") ){
                Long Id = Long.parseLong(id);
                User currentUser = userService.findById(Id);
                if(currentUser == null ){
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Invalid param value",
                            new MessageContent(HttpStatus.NOT_FOUND.value(), "Invalid param value", null));
                }
                if (bodyParam == null || bodyParam.isEmpty()) {
                    response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), "Invalid param value",
                            new MessageContent(HttpStatus.BAD_REQUEST.value(), "Invalid param value", null));
                }else{
                    String username = (String)bodyParam.get("username");
                    String password = (String)bodyParam.get("password");
                    String fullName = (String)bodyParam.get("fullName");
                    String roleName = (String)bodyParam.get("roleName");

                    currentUser.setUserName(username);
                    currentUser.setPassword(password);
                    currentUser.setFullName(fullName);
                    currentUser.setRoleName(roleName);

                    String invalidData = new UserValidation().validateUpsertUser(currentUser);
                    if (invalidData != null) {
                        response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), invalidData,
                                new MessageContent(HttpStatus.BAD_REQUEST.value(), invalidData, null));
                    }else{
                        currentUser.setPassword(new BCryptPasswordEncoder().encode(currentUser.getPassword()));
                        currentUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                        try {
                            userService.save(currentUser);
                            response = new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),
                                    new MessageContent(currentUser));
                        } catch (Exception ex){
                            response = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                    new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null));
                        }
                    }
                }

            }else{
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền cập nhật user",
                        new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền cập nhật user", null));
            }
        }
        return response;
    }

    @DeleteMapping("/user/{id}")
    public ResponseMessage deleteUser(@PathVariable String id, @RequestHeader Map<String, String > headerParam) {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = getAuthorFromToken(headerParam);
        if (dto == null) {
            response = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập",
                    new MessageContent(HttpStatus.UNAUTHORIZED.value(), "Bạn chưa đăng nhập", null));
        } else {
            String role = dto.getRoleName();
            if (role.equalsIgnoreCase("admin")) {

                Long Id = Long.parseLong(id);
                User currentUser = userService.findById(Id);
                if(currentUser == null ){
                    response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "Invalid param value",
                            new MessageContent(HttpStatus.NOT_FOUND.value(), "Invalid param value", null));
                }
                if(currentUser.getRoleName().equalsIgnoreCase("admin")){
                    response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn không thể xóa user admin của hệ thống",
                            new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn không thể xóa user admin của hệ thống", null));
                }else{
                    userService.remove(currentUser);
                    response =  new ResponseMessage(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),
                            new MessageContent(null));
                }

            } else {
                response = new ResponseMessage(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền xóa user",
                        new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền xóa user", null));
            }
        }
        return response;
    }

    @PostMapping("/user/exist")
    public ResponseMessage checkExistUser( @RequestBody Map<String,Object> bodyParam) {
        ResponseMessage response = null;
        String username = (String)bodyParam.get("username");
        User user = userService.findByUsername(username);
        if(user == null){
            response = new ResponseMessage(HttpStatus.NOT_FOUND.value(), "User not exist",
                    new MessageContent(HttpStatus.NOT_FOUND.value(), "User not exist", null));
        }else{
            UserDTO userDTO = new UserDTO(user);
            response = new ResponseMessage(HttpStatus.OK.value(), "OK",
                    new MessageContent(HttpStatus.OK.value(), "OK", userDTO));
        }

        return response;
    }
}