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

package fun.asgc.neutrino.proxy.server.proxy.monitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class MetricsCollector {

    private static Map<Integer, MetricsCollector> metricsCollectors = new ConcurrentHashMap<Integer, MetricsCollector>();

    private Integer port;

    private AtomicLong readBytes = new AtomicLong();

    private AtomicLong wroteBytes = new AtomicLong();

    private AtomicLong readMsgs = new AtomicLong();

    private AtomicLong wroteMsgs = new AtomicLong();

    private AtomicInteger channels = new AtomicInteger();

    private MetricsCollector() {
    }

    public static MetricsCollector getCollector(Integer port) {
        MetricsCollector collector = metricsCollectors.get(port);
        if (collector == null) {
            synchronized (metricsCollectors) {
                collector = metricsCollectors.get(port);
                if (collector == null) {
                    collector = new MetricsCollector();
                    collector.setPort(port);
                    metricsCollectors.put(port, collector);
                }
            }
        }

        return collector;
    }

    public static List<Metrics> getAndResetAllMetrics() {
        List<Metrics> allMetrics = new ArrayList<Metrics>();
        Iterator<Entry<Integer, MetricsCollector>> ite = metricsCollectors.entrySet().iterator();
        while (ite.hasNext()) {
            allMetrics.add(ite.next().getValue().getAndResetMetrics());
        }

        return allMetrics;
    }

    public static List<Metrics> getAllMetrics() {
        List<Metrics> allMetrics = new ArrayList<Metrics>();
        Iterator<Entry<Integer, MetricsCollector>> ite = metricsCollectors.entrySet().iterator();
        while (ite.hasNext()) {
            allMetrics.add(ite.next().getValue().getMetrics());
        }

        return allMetrics;
    }

    public Metrics getAndResetMetrics() {
        Metrics metrics = new Metrics();
        metrics.setChannels(channels.get());
        metrics.setPort(port);
        metrics.setReadBytes(readBytes.getAndSet(0));
        metrics.setWroteBytes(wroteBytes.getAndSet(0));
        metrics.setTimestamp(System.currentTimeMillis());
        metrics.setReadMsgs(readMsgs.getAndSet(0));
        metrics.setWroteMsgs(wroteMsgs.getAndSet(0));

        return metrics;
    }

    public Metrics getMetrics() {
        Metrics metrics = new Metrics();
        metrics.setChannels(channels.get());
        metrics.setPort(port);
        metrics.setReadBytes(readBytes.get());
        metrics.setWroteBytes(wroteBytes.get());
        metrics.setTimestamp(System.currentTimeMillis());
        metrics.setReadMsgs(readMsgs.get());
        metrics.setWroteMsgs(wroteMsgs.get());

        return metrics;
    }

    public void incrementReadBytes(long bytes) {
        readBytes.addAndGet(bytes);
    }

    public void incrementWroteBytes(long bytes) {
        wroteBytes.addAndGet(bytes);
    }

    public void incrementReadMsgs(long msgs) {
        readMsgs.addAndGet(msgs);
    }

    public void incrementWroteMsgs(long msgs) {
        wroteMsgs.addAndGet(msgs);
    }

    public AtomicInteger getChannels() {
        return channels;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}
