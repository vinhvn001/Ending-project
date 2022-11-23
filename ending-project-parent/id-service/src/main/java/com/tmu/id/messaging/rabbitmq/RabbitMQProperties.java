package com.tmu.id.messaging.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProperties {

    //RPC Timeout
    @Value("${rpc.timeout}")
    public static Integer RPC_TIMEOUT = 5;

    //id worker queue
    @Value("${user.worker.queue}")
    public static String USER_WORKER_QUEUE;

    @Autowired
    public RabbitMQProperties(@Value("${rpc.timeout}") Integer rpcTimeout,
                              @Value("${user.worker.queue}") String userWorkerQueue){
        //RPC Timeout
        RPC_TIMEOUT = rpcTimeout;

        //Recognition
        USER_WORKER_QUEUE = userWorkerQueue;
    }
}
