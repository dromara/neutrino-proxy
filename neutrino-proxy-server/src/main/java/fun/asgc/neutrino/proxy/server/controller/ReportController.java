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
package fun.asgc.neutrino.proxy.server.controller;

import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.web.annotation.GetMapping;
import fun.asgc.neutrino.core.web.annotation.RequestMapping;
import fun.asgc.neutrino.core.web.annotation.RestController;
import fun.asgc.neutrino.proxy.server.controller.res.ReportDataViewRes;

/**
 * 报表管理
 * @author: aoshiguchen
 * @date: 2022/9/12
 */
@NonIntercept
@RequestMapping("report")
@RestController
public class ReportController {

    @GetMapping("data-view")
    public ReportDataViewRes dataView() {
        return new ReportDataViewRes()
                .setUserOnlineNumber(2).setEnableUserNumber(3).setUserNumber(5)
                .setLicenseNumber(3).setEnableLicenseNumber(6).setLicenseNumber(6)
                .setServerPortOnlineNumber(3).setEnableServerPortNumber(5).setServerPortNumber(6)
                .setTotalUpstreamFlow("23K").setTotalDownwardFlow("47M")
                ;
    }
}
