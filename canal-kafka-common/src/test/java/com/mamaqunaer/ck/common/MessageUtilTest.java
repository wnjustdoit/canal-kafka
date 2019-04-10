package com.caiya.ck.common;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * MessageUtilTest.
 *
 * @author wangnan
 * @since 1.0
 */
public class MessageUtilTest {


    @Test
    @Ignore
    public void test() {
        Message message = new Message(1);
        List<CanalEntry.Entry> entries = Lists.newArrayList();

        CanalEntry.Column beforeColumn = CanalEntry.Column.newBuilder().setName("title").setValue("这是商品标题").build();
        CanalEntry.Column afterColumn = CanalEntry.Column.newBuilder().setName("title").setValue("这是商品标题1").build();
        CanalEntry.RowData rowData = CanalEntry.RowData.newBuilder().addBeforeColumns(beforeColumn).addAfterColumns(afterColumn).build();

        CanalEntry.RowChange rowChange = CanalEntry.RowChange.newBuilder().setEventType(CanalEntry.EventType.UPDATE).setRowDatas(0, rowData).build();


        CanalEntry.Entry entry = CanalEntry.Entry.newBuilder().setEntryType(CanalEntry.EntryType.ROWDATA).setStoreValue(null).build();
        entries.add(entry);
        message.setEntries(entries);

        List<RowData> rowDatas = MessageUtil.extractMsg(message);
        Assert.assertNotNull(rowDatas);
        Assert.assertTrue(rowDatas.size() == 1);
    }

    @Test
    public void test1() {
        Map<String, Object> beforeColumns = Maps.newHashMap();
        beforeColumns.put("name", "哈哈哈1");
        beforeColumns.put("id", 5);
        beforeColumns.put("updated", "2018-07-19 19:24:28");

        Map<String, Object> afterColumns = Maps.newHashMap();
        afterColumns.put("name", "哈哈哈");
        afterColumns.put("id", 5);
        afterColumns.put("updated", "2018-07-19 19:24:28");

        Map<String, Object> changedAfterColumns = MessageUtil.filterChangedAfterColumns(beforeColumns, afterColumns);
        Assert.assertTrue(changedAfterColumns.size() == 1);
        Assert.assertTrue(Objects.equals(changedAfterColumns.get("name"), "哈哈哈"));
    }


}
