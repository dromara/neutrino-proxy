chrome.runtime.onMessage.addListener(function (request, sender, sendResponse){
    try{
        System.log.debug('chrome.runtime.onMessage:', request, sender);

        switch(request.action){
            case System.Constant.Action.Runtime.DOMContentLoaded: {
                System.log.info('页面加载完毕!');
                break;
            }
        }
    }catch(e){
        System.log.error('chrome.runtime.onMessage error:', e);
    }
    sendResponse('ans:ok');
});

chrome.browserAction.onClicked.addListener(function(tab) {
    System.log.debug('chrome.browserAction.onClicked:', tab);
    chrome.tabs.sendMessage(tab.id, { action: System.Constant.Action.Extension.SHOW }, function (response) {

    });
});

// 获取当前选项卡ID
function getCurrentTabId(callback){
    chrome.tabs.query({active: true, currentWindow: true}, function(tabs){
        if(callback) callback(tabs.length ? tabs[0].id: null);
    });
}

// 当前标签打开某个链接
function openUrlCurrentTab(url){
    getCurrentTabId(tabId => {
        chrome.tabs.update(tabId, {url: url});
    })
}

// 新标签打开某个链接
function openUrlNewTab(url){
    chrome.tabs.create({url: url});
}

//判断某个标签页是否存在
function isExistTab(callback){
    chrome.tabs.getAllInWindow(null, function(tabs)
    {
        bexist = false;
        for ( var i = 0; i <tabs.length; i++)
        {
            url = tabs[i].url;
            if(url.indexOf("pan.bitqiu.com") != -1)
            {
                bexist = true;
                break;
            }
        }

        if(callback) callback(bexist)
    });
}


function checkNotification(){
    if (!("Notification" in window)) {
        alert("This browser does not support desktop notification");
    }
// check whether notification permissions have alredy been granted
    else if (Notification.permission === "granted") {
// If it's okay let's create a notification
        new Notification("标题!");
    }
// Otherwise, ask the user for permission
    else if (Notification.permission !== 'denied') {
        Notification.requestPermission(function (permission) {
// If the user accepts, let's create a notification
            if (permission === "granted") {
                new Notification("Request granted!");
            }
        });
    }
}

