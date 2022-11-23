package com.tmu.room.controller;


import com.tmu.message.MessageContent;
import com.tmu.message.ResponseMessage;
import com.tmu.room.model.RoomOrder;
import com.tmu.room.model.dto.AuthorizationResponseDTO;
import com.tmu.room.service.RoomOrderService;
import com.tmu.room.service.RoomPropertiesService;
import com.tmu.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class RoomOrderController extends BaseController{

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomOrderController.class);

    @Autowired
    private RoomOrderService roomOrderService;

    @Autowired
    private RoomPropertiesService roomPropertiesService;

    public ResponseMessage orderRoom(Map<String, String> headerParam, String requestPath, String method, String urlParam, Map<String, Object>bodyParam )throws ParseException {
        LOGGER.info("Create a Order!");
        ResponseMessage response = null;

        AuthorizationResponseDTO dto = authenticateToken(headerParam);
        if(dto == null){
            response = new ResponseMessage(new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập", null));
        }else {

            DateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date start = dff.parse((String) bodyParam.get("start"));
            Date end = dff.parse((String) bodyParam.get("end"));
            String roomName = (String) bodyParam.get("roomName");

            for (RoomOrder roomOrders : roomOrderService.findAll()) {
                // check if room is available
                if (start.before(roomOrders.getEnd())) {
                    response = new ResponseMessage(new MessageContent(HttpStatus.BAD_REQUEST.value(), "Đặt lịch thất bại, thời gian bạn chọn đã có người sử dụng", null));
                    LOGGER.info("Wrong time payload");
                }else{
                    RoomOrder roomOrder = new RoomOrder();
                    roomOrder.setRoomName(roomName);
                    roomOrder.setStart(start);
                    roomOrder.setEnd(end);
                    roomOrder.setUsername(dto.getUsername());

                    roomOrderService.save(roomOrder);

                    response = new ResponseMessage(new MessageContent(HttpStatus.OK.value(),"Đặt lịch thành công", roomOrder));
                    LOGGER.info("Create success");
                }
                break;
            }
        }
        return  response;
    }

    public ResponseMessage getTimeOrdered(Map<String, String> headerParam, String requestPath, String method, String urlParam, Map<String, Object>bodyParam )throws ParseException {
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = authenticateToken(headerParam);
        if(dto == null){
            response = new ResponseMessage(new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập", null));
        }else{
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            String roomName = params.get("roomName");
            DateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date now = new Date();

            List<Date> listTime = new ArrayList<>();
            if(roomName != null){
                List<RoomOrder> roomOrderByName = roomOrderService.findByRoomName(roomName);
                if(roomOrderByName != null) {
                    for (RoomOrder roomOrders : roomOrderByName) {
                        if (now.before(roomOrders.getEnd())) {
                            listTime.add(roomOrders.getEnd());
                        }
                    }
                }
            }else {
                for (RoomOrder roomOrders : roomOrderService.findAll()) {
                    if (now.before(roomOrders.getEnd())) {
                        listTime.add(roomOrders.getEnd());
                    }
                }
            }
            if (listTime == null || listTime.isEmpty()) {
                response = new ResponseMessage(new MessageContent(HttpStatus.NO_CONTENT.value(), "Hiện tại không có lịch đặt mới", null));
            } else {
                response = new ResponseMessage(new MessageContent(HttpStatus.OK.value(), HttpStatus.OK.toString(), listTime));
            }
        }

        return response;
    }
    public ResponseMessage updateOrdered(Map<String, String> headerParam, String requestPath, String method, String urlParam, Map<String, Object>bodyParam )throws ParseException {
        LOGGER.info("Update a order!");
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = authenticateToken(headerParam);
        if(dto == null){
            response = new ResponseMessage(new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập", null));
        }else{
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            DateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date start = dff.parse((String) bodyParam.get("start"));
            Date end = dff.parse((String) bodyParam.get("end"));
            String roomName = (String) bodyParam.get("roomName");
            Long id = Long.parseLong(params.get("id"));
            RoomOrder currentRoomOrder = roomOrderService.findById(id);
            if(currentRoomOrder != null) {
                if (dto.getRoleName().equalsIgnoreCase("admin")) {
                    currentRoomOrder.setRoomName(roomName);
                    currentRoomOrder.setStart(start);
                    currentRoomOrder.setEnd(end);
                    roomOrderService.save(currentRoomOrder);
                }else if(dto.getUsername().equalsIgnoreCase(currentRoomOrder.getUsername()) ){
                    currentRoomOrder.setRoomName(roomName);
                    currentRoomOrder.setStart(start);
                    currentRoomOrder.setEnd(end);
                    roomOrderService.save(currentRoomOrder);
                }else{
                    response = new ResponseMessage(new MessageContent(HttpStatus.FORBIDDEN.value(),"Bạn không có quyền thay đổi thông tin bản ghi", null));
                }

            }else{
                response = new ResponseMessage(new MessageContent(HttpStatus.BAD_REQUEST.value(), "Trong hệ thống không tồn tại bản ghi order này!", null));
            }
        }
        return response;
    }

    public ResponseMessage deleteOrdered(Map<String, String> headerParam, String requestPath, String method, String urlParam, Map<String, Object>bodyParam )throws ParseException {
        LOGGER.info("Delete a order!");
        ResponseMessage response = null;
        AuthorizationResponseDTO dto = authenticateToken(headerParam);
        if(dto == null){
            response = new ResponseMessage(new MessageContent(HttpStatus.FORBIDDEN.value(), "Bạn chưa đăng nhập", null));
        }else{
            Map<String, String> params = StringUtil.getUrlParamValues(urlParam);
            Long id = Long.parseLong(params.get("id"));
            RoomOrder currentRoomOrder = roomOrderService.findById(id);
            if(currentRoomOrder != null) {
                if (dto.getRoleName().equalsIgnoreCase("admin")) {
                    roomOrderService.remove(id);
                } else if (dto.getUsername().equalsIgnoreCase(currentRoomOrder.getUsername()))  {
                    roomOrderService.remove(id);
                }else{
                    response = new ResponseMessage(new MessageContent(HttpStatus.FORBIDDEN.value(),"Bạn không có quyền thay xóa bản ghi", null));
                }
            }else{
                response = new ResponseMessage(new MessageContent(HttpStatus.BAD_REQUEST.value(), "Trong hệ thống không tồn tại bản ghi order này!", null));
            }
        }
        return response;
    }

//    public static void main (String args[]){
//        Date now = new Date();
//        DateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        String a = dff.format(now);
//        System.out.println(a);
//    }
}
