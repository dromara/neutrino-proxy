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
package fun.asgc.neutrino.core.base;

/**
 * 该接口用于将多个channel连接，实现消息广播
 * 应用场景：
 * 项目初期，为了快速实现功能，不考虑多节点，可能不会接入太多的第三方依赖，如：redis、rocketMQ等。
 * 但是要保证后期需要的时候能够快速接入，而不需要对现有逻辑做太大的改动。
 *
 * 那么，前期你可以使用ApplicationEventChannel，来做业务解藕。
 * 当多次迭代后需要引入RocketMQ，则直接让ApplicationEventChannel连接RocketMQChannel，从而大大减少开发工作。
 *
 * 该方案适用于异步、解耦、削峰，不适用与事务消息、延时消息.
 *
 * 需要注意的是，该连接是单向连接。
 * 如：A连接B，则经过A的消息会广播给B，而经过B的消息不会广播给A
 * 如果需要，B也需要实现该接口，并且连接A
 *
 * @author: aoshiguchen
 * @date: 2022/10/7
 */
public interface ChannelConnector<C extends Channel> {
    /**
     * 连接channel
     * @param channel channel
     */
    void connectChannel(C channel);

    /**
     * 断开连接channel
     * @param channel channel
     */
    void disconnectChannel(C channel);
}
