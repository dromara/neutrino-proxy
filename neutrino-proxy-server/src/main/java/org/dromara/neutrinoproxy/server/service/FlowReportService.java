/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
