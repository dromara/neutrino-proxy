// 日志对象
System.log = Asgc.Logger("NeutrinoProxyClient");
System.log.setLevelAll();

// 发送ajax请求，未传回调方法时采用同步方式
System.ajax = function(url, method, headers, params, successCallback, errorCallback){
    let result = undefined;
    System.log.debug("[ajax] request url:", url, "method:", method, "params:", params);
    $.ajax({
        url: url,
        data: params,
        type: method,
        async: !!successCallback,
        dataType: "json",
        success: function(data) {
            result = data;
            System.log.debug("[ajax] response:", data);
            if(!!successCallback){
                successCallback.call(this, Array.prototype.slice.apply(arguments));
            }
        },
        error: function(e){
            System.log.debug("[ajax] error:", e);
            if(!!errorCallback){
                errorCallback.call(this, Array.prototype.slice.apply(arguments));
            }
        }
    });
    return result;
};

// 发送axios请求
System.axios = function(url, method, headers, responseType, params, successCallback, errorCallback){
    System.log.debug("[axios] request url:", url, "method:", method, "params:", params);
    var options = {
        url: url,
        method: method,
        headers: headers,
        data: params
    };

    if(responseType){
        options.responseType = responseType;
    }

    return axios(options).then(function (res){
        System.log.debug("[axios] response:", res.data);
        if(!!successCallback){
            successCallback(res.data);
        }
    });
};

// 获取注释块
Function.prototype.getCommentBlockString = function(){
    var block = new String(this);
    block = block.substring(block.indexOf("/*") + 3,block.lastIndexOf("*/"));
    return block;
}

// 获取用户信息
System.util.getUserInfo = function(){
    return Asgc.cache.getJson(System.Constant.SessionKeys.USER_INFO);
};

// 获取待注入的js片段
System.util.getInjectJS = function(name){
    var fun = System.inject.js[name];
    if(!fun){
        System.log.error('获取待注入的JS片段异常: ' + name + '不存在!');
    }
    return fun.getCommentBlockString();
};

// 获取待注入的html片段
System.util.getInjectHTML = function(name){
    var fun = System.inject.html[name];
    if(!fun){
        System.log.error('获取待注入的HTML片段异常: ' + name + '不存在!');
    }
    return fun.getCommentBlockString();
};

// 获取待注入的css代码片段
System.util.getInjectCSS = function(name){
    var fun = System.inject.css[name];
    if(!fun){
        System.log.error('获取待注入的CSS片段异常: ' + name + '不存在!');
    }
    return fun.getCommentBlockString();
};

// 获取html content
System.util.getHtmlContent = function(){
    let login = System.util.getUserInfo();
    if(!login){
        return System.util.getInjectHTML('loginBeforeContent');
    }
    return System.util.getInjectHTML('loginAfterContent');
};

// 获取content内容
System.util.getRenderHtml = function(){
    var html = '<div id="lpBack" class="lm-back tst" >';
    html += System.util.getHtmlContent();
    html += '</div>';
    return html;
};

// 获取当前简历页面类型
System.util.getCurrentResumeType = function(){
    var resumeType = System.Constant.Lieme.RESUME_TYPE.UNKNOWN;
    for(var type in System.Constant.ResumePageUrl){
        var urls = System.Constant.ResumePageUrl[type];
        for(var url of urls){
            if(location.href.indexOf(url) !== -1){
                return System.Constant.Lieme.RESUME_TYPE[type];
            }
        }
    }

    return resumeType;
};
