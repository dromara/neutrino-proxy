((global) => {
    let Asgc = {};
    global.Asgc = Asgc;

    // 类型判断工具
    Asgc.types = (() => {
        let types = ['String', 'Null', 'Number', 'Boolean', 'Undefined', 'Array', 'Object', 'Function', 'Window', 'Arguments',
            'ArrayBuffer', 'Blob', 'File', 'Date'];
        let ret = {
            getType(val) {
                let typeStrig = toString.call(val);
                return typeStrig.slice(8,typeStrig.length - 1);
            },
            isType(val,type) {
                return this.getType(val) === type;
            }
        };

        for (let t of types) {
            ret[`is${t}`] =  function (val) {
                return this.isType(val, t);
            };
        }
        return ret;
    })();

    Asgc.util = (function () {
        return {
            dateFormat: function (date, fmt) {
                // 默认格式
                fmt = fmt ? fmt : 'yyyy-MM-dd hh:mm:ss';

                let o = {
                    "M+" : date.getMonth()+1,                 // 月份
                    "d+" : date.getDate(),                    // 日
                    "h+" : date.getHours(),                   // 小时
                    "m+" : date.getMinutes(),                 // 分
                    "s+" : date.getSeconds(),                 // 秒
                    "q+" : Math.floor((date.getMonth()+3)/3), // 季度
                    "S+"  : date.getMilliseconds()             // 毫秒
                };
                if(/(y+)/.test(fmt)) {
                    fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substring(4 - RegExp.$1.length));
                }
                for(let k in o) {
                    if(new RegExp("("+ k +")").test(fmt)){
                        fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) :
                            RegExp.$1.length === 2 ? (("00"+ o[k]).substring((""+ o[k]).length)) : (("000"+ o[k]).substring((""+ o[k]).length))
                        );
                    }
                }
                return fmt;
            }
        }
    })();

    // 日志工具
    Asgc.Logger = function(name){
        return {
            /**
             * all
             * debug
             * info
             * warn
             * error
             * off
             * */

            level: 'all',
            setLevel: function(level){
                this.level = level;
            },
            setLevelAll: function(){
                this.level = 'all';
            },
            setLevelDebug: function(){
                this.level = 'debug';
            },
            setLevelInfo: function(){
                this.level = 'info';
            },
            setLevelWarn: function(){
                this.level = 'warn';
            },
            setLevelError: function(){
                this.level = 'error';
            },
            setLevelOff: function(){
                this.level = 'off';
            },
            out: function(curLevel, msgs) {
                if(Asgc.Logger.LEVEL[curLevel].v < Asgc.Logger.LEVEL[this.level].v) return;
                let prefix = `%c [Asgc Log][${Asgc.util.dateFormat(new Date(), 'yyyy-MM-dd hh:mm:ss.SSS')}][${name}][level:${curLevel}]`;
                let params = [];
                params.push(prefix);
                params.push('color:' + Asgc.Logger.LEVEL[curLevel].color);
                for(let msg of msgs){
                    params.push(msg);
                }
                console.log.apply(this,params);
            },
            debug: function(){
                this.out('debug', Array.prototype.slice.apply(arguments));
            },
            info: function(){
                this.out('info', Array.prototype.slice.apply(arguments));
            },
            warn: function(){
                this.out('warn', Array.prototype.slice.apply(arguments));
            },
            error: function(){
                this.out('error', Array.prototype.slice.apply(arguments));
            }
        };
    };

    Asgc.Logger.LEVEL = {
        all: {
            v: 0
        },
        debug: {
            v: 1,
            color: 'gray'
        },
        info: {
            v: 2,
            color: 'green'
        },
        warn: {
            v: 3,
            color: 'blue'
        },
        error: {
            v: 4,
            color: 'red'
        },
        off: {
            v: 5
        }
    };

    Asgc.cache = (function(){
    let storage = window.localStorage;

    return {
        set: function(k,v){
          storage.setItem(k,v);
          return this;
        },

        get: function(k){
          return storage.getItem(k) || '';
        },

        clear: function(){
            storage.clear();

            return this;
        },

        switchLocalStorage: function(){
            storage = window.localStorage;
            return this;
        },

        switchSessionStorage: function(){
            storage = window.sessionStorage;
            return this;
        },

        setJson: function(k,v){
          storage.setItem(k,JSON.stringify(v));
          return this;
        },

        getJson: function(k){

          let val = storage.getItem(k);

          if(!val) return null;
          else return JSON.parse(val);
        },

        setJsonProperty: function(k,property,v){
            let data = this.getJson(k);
            data[property] = v;
            this.setJson(k,data);
            return this;
        },

        getJsonProperty: function(k,property){
            let data = this.getJson(k);

            return data[property];
        },

        getString: function(k){
            return this.get(k);
        },

        getInt: function(k){
            return parseInt(this.get(k));
        },

        getFloat: function(k){
            return parseFloat(this.get(k));
        },

        getNumber: function(k){
            return new Number(this.get(k));
        },

        getBoolean: function(k){
            return this.get(k) === 'true';
        },

        getDate: function(k){
            return new Date(this.get(k));
        },

        //超级设值
        //如：s('a.b.c',10); => a:{b:{c:10}}
        //暂不支持数组下标
        s: function(k,v){
            if(!k) return this;

            let ks = k.split('.');

            if(ks.length === 1) return this.set(k,v);

            let data = this.getJson(ks[0]);

            function solve(data,ks,n){
                if(n === ks.length - 1){
                    data[ks[n]] = v;
                    return;
                }

                if(!data[ks[n]]) data[ks[n]] = {};
                if(!Asgc.types.isObject(data[ks[n]])){
                    logger.error('错误的引用! k:' + k);
                    return;
                }
                solve(data[ks[n]],ks,n+1);

            }

            solve(data,ks,1);

            this.setJson(ks[0],data);

            return this;
        },
        //超级取值
        //如：a:{b:{c:10}} g('a.b.c') => 10
        //暂不支持数组下标
        g: function(k){
            if(!k) return '';
            let ks = k.split('.');

            if(ks.length === 1) return this.get(k);

            let data = this.getJson(ks[0]);

            function solve(data,ks,n){
                if(n === ks.length - 1){
                    return data[ks[n]];
                }

                if(!data[ks[n]]) return '';
                if(!Asgc.types.isObject(data[ks[n]])){
                    logger.error('错误的引用! k:' + k);
                    return '';
                }

                return solve(data[ks[n]],ks,n+1);

            }

            return JSON.stringify(solve(data,ks,1));
        },

        sJson: function(k,v){
          this.s(k,JSON.stringify(v));

          return this;
        },

        gJson: function(k){
          let val = this.g(k);

          if(!val) return null;
          else return JSON.parse(val);
        },

        gString: function(k){
            return this.g(k);
        },

        gInt: function(k){
            return parseInt(this.g(k));
        },

        gFloat: function(k){
            return parseFloat(this.g(k));
        },

        gNumber: function(k){
            return new Number(this.g(k));
        },

        gBoolean: function(k){
            return this.g(k) === 'true';
        },

        gDate: function(k){
            return new Date(this.g(k));
        },

   };
})();

})(window);
