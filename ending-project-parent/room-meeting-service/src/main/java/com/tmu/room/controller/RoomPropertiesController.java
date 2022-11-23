package com.tmu.room.controller;

import com.tmu.message.MessageContent;
import com.tmu.message.ResponseMessage;
import com.tmu.room.model.RoomProperties;
import com.tmu.room.model.dto.AuthorizationResponseDTO;
import com.tmu.room.service.RoomPropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.util.List;
import java.util.Map;


@Controller
public class RoomPropertiesController extends BaseController{

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomPropertiesController.class);

    @Autowired
    private RoomPropertiesService roomPropertiesService;

    public ResponseMessage getAllRoomProperties(Map<String, String>headerParam,String requestPath, String method, String urlParam, Map<String, Object>bodyParam )throws ParseException{
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenticateToken(headerParam);
        if(dto == null){
            response = new ResponseMessage(new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập", null));
        }else{

            List<RoomProperties> allRoomProperties = roomPropertiesService.findAll();
            if(allRoomProperties == null || allRoomProperties.isEmpty()){
                response = new ResponseMessage(new MessageContent(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Đã có lỗi xảy ra!"));
            }else{
                response = new ResponseMessage(new MessageContent(HttpStatus.OK.value(), HttpStatus.OK.toString(), allRoomProperties));
            }
        }

        return  response;
    }



}
