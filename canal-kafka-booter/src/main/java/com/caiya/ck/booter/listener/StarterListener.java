package com.caiya.ck.booter.listener;

import com.alibaba.otter.canal.client.CanalConnector;

import static com.alibaba.otter.canal.protocol.CanalEntry.*;

import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.caiya.ck.booter.component.KafkaProperties;
import com.caiya.ck.common.canal.CanalClient;
import com.caiya.ck.booter.component.CanalProperties;
import com.caiya.kafka.ProducerFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 程序执行入口.
 *
 * @author wangnan
 * @since 1.0
 */
@Component
public class StarterListener implements CommandLineRunner, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(StarterListener.class);

    private static final int BATCH_SIZE = 100;

    private static final long INTERVAL_SLEEP_MILLIS = 200L;

    private static volatile boolean running = false;

    @Resource
    private CanalProperties canalProperties;

    @Resource
    private ProducerFactory<String, Message> producerFactory;

    @Resource
    private KafkaProperties kafkaProperties;

    /**
     * 仅生成一个客户端实例,单线程使用,在spring生命周期内不用关闭
     */
    private Producer<String, Message> kafkaProducer;

    private CanalConnector canalConnector;

    @Override
    public void run(String... args) throws Exception {
        if (!running) {
            synchronized (StarterListener.class) {
                if (!running) {
                    start();
                    running = true;
                }
            }
        }
    }

    private void start() {
        logger.info("the canal-kafka application is starting..");

        // 获取canal客户端
        canalConnector = new CanalClient(canalProperties.getZkServers(), canalProperties.getDestination(), canalProperties.getUsername(), canalProperties.getPassword())
                .getConnectedAndSubscribedConnector();
        // 获取kafka生产者客户端
        kafkaProducer = producerFactory.createProducer();

        // 通过canal对kafka生产消息
        while (true) {
            Message message = canalConnector.getWithoutAck(BATCH_SIZE);
            long batchId = message.getId();
            List<Entry> entryList = message.getEntries();
            if (entryList.isEmpty()) {
                // 此时batchId为-1;
                // 某次轮询如果没有数据,则休眠200ms
                sleep(INTERVAL_SLEEP_MILLIS);
                continue;
            }

            if (logger.isDebugEnabled()) {
                logger.debug("current batchId:{} entryList.size:{}", batchId, entryList.size());
            }
            try {
                processBiz(message);

                // 提交服务端消息确认
                canalConnector.ack(batchId);
            } catch (Exception e) {
                logger.error("业务处理失败,当前message:{}", message, e);
                // TODO 容错处理

            }

        }
    }

    private void processBiz(Message message) {
        // 发送kafka消息队列
        ProducerRecord<String, Message> record = new ProducerRecord<>(kafkaProperties.getCanalTopic(), String.valueOf(message.getId()), message);
        kafkaProducer.send(record);

    }

    private void sleep(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            logger.error("sleep timeout:{} error", timeout, e);
        }
    }

    @Override
    public void destroy() throws Exception {
        try {
            if (kafkaProducer != null)
                kafkaProducer.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        try {
            if (canalConnector != null)
                canalConnector.disconnect();
        } catch (CanalClientException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
