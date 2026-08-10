$(function () {
    $("#btnGet").click(function () {
        // chrome.tabs.query可以通过回调函数获得当前页面的信息tabs

        chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
            // 发送一个copy消息出去
            chrome.tabs.sendMessage(tabs[0].id, { action: System.Constant.Action.Extension.COPY }, function (response) {
                // 这里的回调函数接收到了要抓取的值，获取值得操作在下方content-script.js
                // 将值存在background.js的data属性里面。
                // console.log(response);
                var win = chrome.extension.getBackgroundPage();
                win.data= response;
                // export_raw('test.html', response);
                $.ajax({
                    url: 'http://www.hzcms.com/api/demo/test1',
                    type: 'POST',
                    data: {'html': response},
                    dataType: 'json',
                }).then(function(res){
                    console.log(res);
                }, function(error){
                    console.log(error);
                });
            });
        });
    });
    $("#btnLogin").click(function () {

        let account = $('input[name=account]').val();
        let password = $('input[name=password]').val();


        console.log()
        // 登录逻辑
        window.localStorage.setItem('login','1');
        changeLogin();
    });

    $('#updateAll').click(function () {
        // 更新全部
    });
    $('#updateItem').click(function (e) {
        // 更新
        console.log(e.currentTarget.dataset.value);
    });
});
console.log('popup...')

// chrome-extension://mkonfdhljckogillahgannggmlmkegjp/popup.html

// changeLogin();



// function changeLogin() {
//     let login = window.localStorage.getItem('login');
//     if (login && login == '1') {
//         $('#logined').attr('style','');
//         $('#login').attr('style','display: none;');
//     } else {
//         $('#logined').attr('style','display: none;');
//         $('#login').attr('style','');
//     }
// }




