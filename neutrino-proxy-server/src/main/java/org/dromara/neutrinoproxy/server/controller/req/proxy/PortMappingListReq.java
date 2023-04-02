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
package org.dromara.neutrinoproxy.server.controller.req.proxy;

import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.OnlineStatusEnum;
import lombok.Data;

/**
 * 端口映射列表请求
 * @author: aoshiguchen
 * @date: 2022/8/8
 */
@Data
public class PortMappingListReq {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * licenseId
     */
    private Integer licenseId;
    /**
     * 服务端口号
     */
    private Integer serverPort;

    /**
     * 是否在线
     * {@link OnlineStatusEnum}
     */
    private Integer isOnline;
    /**
     * 启用状态
     * {@link EnableStatusEnum}
     */
    private Integer enable;
}
