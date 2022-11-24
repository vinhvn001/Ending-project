package com.tmu.room.rabbitmq;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmu.constant.ResourcePath;
import com.tmu.message.RequestMessage;
import com.tmu.message.ResponseMessage;
import com.tmu.room.controller.RoomOrderController;
import com.tmu.room.exception.ValidationException;
import com.tmu.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;


public class RpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    @Autowired
    RoomOrderController roomOrderController;

    @RabbitListener(queues = "${order.rpc.queue}")
    public String processService(String json) throws ValidationException {
        try {
            LOGGER.info(" [-->] Server received request for " + json);
            ObjectMapper mapper = new ObjectMapper();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapper.setDateFormat(df);
            RequestMessage request = mapper.readValue(json, RequestMessage.class);

            //Process here
            ResponseMessage response = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), null);
            if (request != null) {
                String requestPath = request.getRequestPath().replace(request.getVersion() != null
                        ? request.getVersion() : ResourcePath.VERSION, "");
                String urlParam = request.getUrlParam();
                String pathParam = request.getPathParam();
                Map<String, Object> bodyParam = request.getBodyParam();
                Map<String, String> headerParam = request.getHeaderParam();
                //GatewayDebugUtil.debug(requestPath, urlParam, pathParam, bodyParam, headerParam);
                LOGGER.info(" [-->] Server received requestPath =========>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + requestPath);

                switch (request.getRequestMethod()) {
                    case "GET":
                        if ("/order/getTimeOrder".equalsIgnoreCase(requestPath) ) // Get details
                        {
                            response = roomOrderController.getTimeOrdered(headerParam, pathParam, request.getRequestMethod(), urlParam, bodyParam );
                        }
                        break;
                    case "POST":
                        if ("/order/orderRoom".equalsIgnoreCase(requestPath) ) // Get details
                        {
                            response = roomOrderController.orderRoom(headerParam, pathParam, request.getRequestMethod(), urlParam, bodyParam );
                        }

                        break;
                    case "PUT":
                        if ("/order/updateOrder".equalsIgnoreCase(requestPath) ) // Get details
                        {
                            response = roomOrderController.updateOrdered(headerParam, pathParam, request.getRequestMethod(), urlParam, bodyParam );
                        }
                        break;
                    case "PATCH":
                        break;
                    case "DELETE":
                        if ("/order/deleteOrder".equalsIgnoreCase(requestPath) ) // Get details
                        {
                            response = roomOrderController.deleteOrdered(headerParam, pathParam, request.getRequestMethod(), urlParam, bodyParam );
                        }
                        break;
                    default:
                        break;
                }
            }
            LOGGER.info(" [<--] Server returned " + response != null ? response.toJsonString() : "null");
            return response != null ? response.toJsonString() : null;
        } catch (Exception ex) {
            LOGGER.error("Error to processService >>> " + StringUtil.printException(ex));
            ex.printStackTrace();
        }
        return null;
    }
}
