package com.tmu.id.messaging.rabbitmq;


import com.tmu.constant.ResourcePath;
import com.tmu.id.controller.AuthController;
import com.tmu.id.controller.UserController;
import com.tmu.id.exception.ValidationException;
import com.tmu.message.RequestMessage;
import com.tmu.message.ResponseMessage;
import com.tmu.utils.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class RpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    @Autowired
    private AuthController authController;

    @Autowired
    private UserController userController;



    @RabbitListener(queues = "${user.rpc.queue}")
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
                        if ("/user".equalsIgnoreCase(requestPath) ) // Get details
                        {
                            response = userController.getDetailUser(headerParam);
                        } else if ("/user/findAll".equalsIgnoreCase(requestPath)) // Find all
                        {
                            response = userController.getAllUser(headerParam);
                        }else if ("/user/findUser".equalsIgnoreCase(requestPath)) // Find by id
                        {
                            response = userController.getDetailUser(pathParam,headerParam);
                        }
                        break;
                    case "POST":
                        if ("/user/create".equalsIgnoreCase(requestPath)) // Insert
                        {
                            response = userController.createUser(bodyParam, headerParam);
                        } else if ("/user/login".equalsIgnoreCase(requestPath)) //Login
                        {
                            response = authController.userLogin( bodyParam);
                        }else if ("/user/authentication".equalsIgnoreCase(requestPath)) //Login
                        {
                            response = authController.authorized( headerParam);
                        }else if ("/user/exist".equalsIgnoreCase(requestPath)) //Login
                        {
                            response = userController.checkExistUser(bodyParam);
                        }


                        break;
                    case "PUT":
                        if ("/user".equalsIgnoreCase(requestPath) && !StringUtil.isNullOrEmpty(pathParam))//Update via service
                        {
                            response = userController.updateUser(pathParam, bodyParam, headerParam);
                        }
                        break;
                    case "PATCH":
                        break;
                    case "DELETE":
                        if ("/user".equalsIgnoreCase(requestPath) && pathParam != null && pathParam.length() > 0) // Delete by id
                        {
                            response = userController.deleteUser(pathParam,headerParam);
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
