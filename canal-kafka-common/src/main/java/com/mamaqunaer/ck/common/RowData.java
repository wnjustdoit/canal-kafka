package com.caiya.ck.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 行变更记录信息.
 *
 * @author wangnan
 * @since 1.0
 */
@Data
public class RowData implements Serializable {

    private static final long serialVersionUID = -642004150101747901L;


    private Map<String, Object> beforeColumns;

    private Map<String, Object> afterColumns;

    private EventType eventType;



}
