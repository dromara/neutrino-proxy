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
package fun.asgc.neutrino.proxy.server.job;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.quartz.IJobHandler;
import fun.asgc.neutrino.core.quartz.annotation.JobHandler;
import fun.asgc.neutrino.proxy.server.dal.FlowReportMinuteMapper;
import fun.asgc.neutrino.proxy.server.dal.LicenseMapper;
import fun.asgc.neutrino.proxy.server.service.FlowReportService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: aoshiguchen
 * @date: 2022/10/28
 */
@Slf4j
@NonIntercept
@Component
@JobHandler(name = "FlowReportForHourJob", cron = "0 0 */1 * * ?", param = "")
public class FlowReportForHourJob implements IJobHandler {
    @Autowired
    private FlowReportService flowReportService;
    @Autowired
    private LicenseMapper licenseMapper;
    @Autowired
    private FlowReportMinuteMapper flowReportMinuteMapper;

    @Override
    public void execute(String param) throws Exception {
        // TODO
    }

}
