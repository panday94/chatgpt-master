package com.master.chat.framework.third;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.master.chat.common.validator.ValidatorUtil;
import org.springframework.stereotype.Component;

/**
 * 获取微信服务文件处理类
 *
 * @author: Yang
 * @date: 2023/2/1
 * @version: 1.0.0
 * https://www.panday94.xyz
 * Copyright Ⓒ 2023 曜栋网络科技工作室 Limited All rights reserved.
 */
@Component
public class WxServiceHandler {

    /**
     * 获取微信小程序配置
     *
     * @param clientId 应用id
     * @return 配置信息
     */
    public WxMaService getMaService(String clientId) {
        WxMaService service = WxMaServiceKit.getMaService(clientId);
        if (ValidatorUtil.isNull(service) || ValidatorUtil.isNull(service.getWxMaConfig().getAppid())) {
            service = new WxMaServiceImpl();
            WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
            config.setAppid(WxAppConstant.APP_ID);
            config.setSecret(WxAppConstant.APP_SECRET);
            service.setWxMaConfig(config);
            WxMaServiceKit.setMaService(WxAppConstant.APP_ID, service);
        }
        return service;
    }

}
