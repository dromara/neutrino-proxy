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
package org.dromara.neutrinoproxy.server.controller;

import org.dromara.neutrinoproxy.server.base.page.PageInfo;
import org.dromara.neutrinoproxy.server.base.page.PageQuery;
import org.dromara.neutrinoproxy.server.controller.req.log.ClientConnectRecordListReq;
import org.dromara.neutrinoproxy.server.controller.res.log.ClientConnectRecordListRes;
import org.dromara.neutrinoproxy.server.service.ClientConnectRecordService;
import org.dromara.neutrinoproxy.server.util.ParamCheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * @author: aoshiguchen
 * @date: 2022/11/26
 */
@Slf4j
@Mapping("/client-connect-record")
@Controller
public class ClientConnectRecordController {
    @Inject
    private ClientConnectRecordService clientConnectRecordService;

    @Get
    @Mapping("/page")
    public PageInfo<ClientConnectRecordListRes> page(PageQuery pageQuery, ClientConnectRecordListReq req) {
        ParamCheckUtil.checkNotNull(pageQuery, "pageQuery");
        return clientConnectRecordService.page(pageQuery, req);
    }
}
