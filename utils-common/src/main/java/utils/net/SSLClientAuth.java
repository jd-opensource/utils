package utils.net;

/**
 * @description: 客户端认证配置
 * @author: imuge
 * @date: 2021/11/10
 **/
public enum SSLClientAuth {

    // 单向认证
    NONE,
    // 严格双向认证
    NEED,
    // 非严格双向认证
    WANT

}
