package com.caiya.ck.common;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 消息工具类.
 *
 * @author wangnan
 * @since 1.0
 */
public final class MessageUtil {

    private static final Logger logger = LoggerFactory.getLogger(MessageUtil.class);

    /**
     * 解析消息成自定义消息格式.
     *
     * @param message 原始消息
     * @return 按照新的格式解析后的消息
     */
    public static List<RowData> extractMsg(Message message) {
        List<RowData> result = Lists.newArrayList();

        List<Entry> entryList = message.getEntries();
        for (Entry entry : entryList) {
            // 事务多余处理的过滤
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChange;
            try {
                rowChange = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("RowChange parse error, data:" + entry.toString());
            }
            CanalEntry.EventType eventType = rowChange.getEventType();
            // 变更事件的过滤
            if (!(eventType.equals(CanalEntry.EventType.INSERT) || eventType.equals(CanalEntry.EventType.UPDATE) || eventType.equals(CanalEntry.EventType.DELETE))) {
                continue;
            }

//            if (logger.isDebugEnabled()) {
//                logger.debug("current binlog info, logFileName:{}, logFileOffset:{}, schemaName:{}, tableName:{}, eventType:{}", entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset()
//                        , entry.getHeader().getSchemaName(), entry.getHeader().getTableName(), eventType);
//            }

            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                RowData row = new RowData();
                if (eventType == CanalEntry.EventType.DELETE) {
                    row.setBeforeColumns(extractColumn(rowData.getBeforeColumnsList()));
                    row.setEventType(EventType.DELETE);
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    row.setAfterColumns(extractColumn(rowData.getAfterColumnsList()));
                    row.setEventType(EventType.INSERT);
                } else {
                    // UPDATE
                    row.setBeforeColumns(extractColumn(rowData.getBeforeColumnsList()));
                    row.setAfterColumns(extractColumn(rowData.getAfterColumnsList()));
                    row.setEventType(EventType.DELETE);
                }

                result.add(row);
            }
        }

        return result;
    }

    private static Map<String, Object> extractColumn(List<Column> columns) {
        Map<String, Object> row = new HashMap<>();
        for (Column column : columns) {
            // if (logger.isDebugEnabled()) { logger.debug("column name and value:{}:{}", column.getName(), column.getValue());}
            row.put(column.getName(), column.getValue());
        }

//        if (logger.isDebugEnabled()) {
//            logger.debug("current row:{}", row);
//        }

        return row;
    }

    /**
     * 过滤未改变的列数据,留下变更的列数据
     *
     * @param beforeColumns 变更新的列数据
     * @param afterColumns  变更后的列数据
     * @return 过滤后的列数据
     */
    public static Map<String, Object> filterChangedAfterColumns(Map<String, Object> beforeColumns, Map<String, Object> afterColumns) {
        Map<String, Object> result = Maps.newHashMap();
        if (beforeColumns == null) {
            result.putAll(afterColumns);
        } else if (afterColumns != null) {
            afterColumns.entrySet().stream().filter(entry -> !Objects.equals(entry.getValue(), beforeColumns.get(entry.getKey()))).forEach(entry -> {
                result.put(entry.getKey(), entry.getValue());
            });
        }

        return result;
    }


    private MessageUtil() {
    }
}
