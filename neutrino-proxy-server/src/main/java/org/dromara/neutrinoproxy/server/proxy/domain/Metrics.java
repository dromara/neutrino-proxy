package org.dromara.neutrinoproxy.server.proxy.domain;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Data
public class Metrics implements Serializable {
    private static final long serialVersionUID = 1L;
    private int port;
    private long readBytes;
    private long wroteBytes;
    private long readMsgs;
    private long wroteMsgs;
    private int channels;
    private long timestamp;
}
