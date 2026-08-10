let System = {
    Constant: {
        RequestMethod: {
            GET: 'GET',
            POST: 'POST'
        },
        SessionKeys: {
            USER_INFO: 'lm-user-info'
        },
        Action: {
            Runtime: {
                DOMContentLoaded: 'DOMContentLoaded',
            },
            Extension: {
                COPY: 'copy',
                URL: 'url',
                SHOW: 'show'
            }
        },
        Lieme: {
            API_BASE_URL: 'https://resume.api.yuujob.com',
            RESUME_TYPE: {
                BOSS: 'boss',
                ZHI_LIE_BANG: 'zhiliebang',
                QIAN_CHENG: 'qiancheng',
                ZHI_LIAN: 'zhilian',
                ZHUO_PIN: 'zhuopin',
                LA_GOU: 'lagou',
                LIE_PIN: 'liepin',
                UNKNOWN: 'Unknown'
            }
        },
        ResumePageUrl: {
            LIE_PIN: ['h.liepin.com/resume/showresumedetail', 'h.liepin.cn/resume/showresumedetail']
        }
    },
    inject: {
        js: {},
        html: {},
        css: {}
    },
    util: {},
    api: {
        base: {},
        // boss
        boss: {},
        // 直猎邦
        zhiliebang: {},
        // 前程无忧
        qiancheng: {},
        // 智联
        zhilian: {},
        // 卓聘
        zhuopin: {},
        // 拉勾网
        lagou: {},
        // 猎聘
        liepin: {},
    }
};
window.System = System;

