"use strict";
exports.__esModule = true;
exports.Tool = void 0;
var Tool = /** @class */ (function () {
    function Tool() {
    }
    /**
     * 空校验 null或""都返回true
     */
    Tool.isEmpty = function (obj) {
        if ((typeof obj === 'string')) {
            return !obj || obj.replace(/\s+/g, "") === "";
        }
        else {
            return (!obj || JSON.stringify(obj) === "{}" || obj.length === 0);
        }
    };
    /**
     * 非空校验
     */
    Tool.isNotEmpty = function (obj) {
        return !this.isEmpty(obj);
    };
    /**
     * 对象复制
     * @param obj
     */
    Tool.copy = function (obj) {
        if (Tool.isNotEmpty(obj)) {
            return JSON.parse(JSON.stringify(obj));
        }
    };
    /**
     * 使用递归将数组转为树形结构
     * 父ID属性为parent
     */
    Tool.array2Tree = function (array, parentId) {
        if (Tool.isEmpty(array)) {
            return [];
        }
        var result = [];
        for (var i = 0; i < array.length; i++) {
            var c = array[i];
            // console.log(Number(c.parent), Number(parentId));
            if (Number(c.parent) === Number(parentId)) {
                result.push(c);
                // 递归查看当前节点对应的子节点
                var children = Tool.array2Tree(array, c.id);
                if (Tool.isNotEmpty(children)) {
                    c.children = children;
                }
            }
        }
        return result;
    };
    /**
     * 随机生成[len]长度的[radix]进制数
     * @param len
     * @param radix 默认62
     * @returns {string}
     */
    Tool.uuid = function (len, radix) {
        if (radix === void 0) { radix = 62; }
        var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
        var uuid = [];
        radix = radix || chars.length;
        for (var i = 0; i < len; i++) {
            uuid[i] = chars[0 | Math.random() * radix];
        }
        return uuid.join('');
    };
    return Tool;
}());
exports.Tool = Tool;
