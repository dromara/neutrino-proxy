package org.dromara.neutrinoproxy.server.service;

import org.dromara.neutrinoproxy.core.util.LockUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流量报表服务
 * @author: aoshiguchen
 * @date: 2022/10/26
 */
@Slf4j
@Component
public class FlowReportService {
    private Map<Integer/*licenseId*/, AtomicInteger/*writeByte*/> writeByteMap = new HashMap<>();
    private Map<Integer/*licenseId*/, AtomicInteger/*readByte*/> readByteMap = new HashMap<>();

    private AtomicInteger getWriteByte(Integer licenseId) {
        return LockUtil.doubleCheckProcessForNoException(() -> !writeByteMap.containsKey(licenseId),
            licenseId,
            () -> {
                writeByteMap.put(licenseId, new AtomicInteger());
            },
            () -> writeByteMap.get(licenseId));
    }

    private AtomicInteger getReadByte(Integer licenseId) {
        return LockUtil.doubleCheckProcessForNoException(() -> !readByteMap.containsKey(licenseId),
                licenseId,
                () -> {
                    readByteMap.put(licenseId, new AtomicInteger());
                },
                () -> readByteMap.get(licenseId));
    }

    public void addWriteByte(Integer licenseId, Integer writeByte) {
        getWriteByte(licenseId).addAndGet(writeByte);
    }

    public void addReadByte(Integer licenseId, Integer readByte) {
        getReadByte(licenseId).addAndGet(readByte);
    }

    public Integer getAndResetWriteByte(Integer licenseId) {
        return getWriteByte(licenseId).getAndSet(0);
    }

    public Integer getAndResetReadByte(Integer licenseId) {
        return getReadByte(licenseId).getAndSet(0);
    }

}
