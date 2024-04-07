package com.master.chat.api.base.config;

import com.master.chat.api.base.enums.ChatModelEnum;
import com.master.chat.api.moonshot.MoonshotClient;
import com.master.chat.api.openai.OpenAiClient;
import com.master.chat.api.openai.OpenAiStreamClient;
import com.master.chat.api.openai.function.KeyRandomStrategy;
import com.master.chat.api.openai.interceptor.OpenAILogger;
import com.master.chat.api.qianwen.QianWenClient;
import com.master.chat.api.wenxin.WenXinClient;
import com.master.chat.api.wenxin.config.WenXinConfig;
import com.master.chat.api.xfyun.SparkClient;
import com.master.chat.api.zhipu.ZhiPuClient;
import com.master.chat.gpt.constant.BaseConfigConstant;
import com.master.chat.gpt.mapper.OpenkeyMapper;
import com.master.chat.gpt.pojo.vo.OpenkeyVO;
import com.master.chat.sys.pojo.dto.config.BaseInfoDTO;
import com.master.chat.sys.service.IBaseConfigService;
import com.master.chat.common.constant.StringPoolConstant;
import com.master.chat.common.enums.IntegerEnum;
import com.master.chat.common.validator.ValidatorUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 初始化大模型bean
 *
 * @author: Yang
 * @date: 2023/9/7
 * @version: 1.0.0
 * https://www.panday94.xyz
 * Copyright Ⓒ 2023 曜栋网络科技工作室 Limited All rights reserved.
 */
@Slf4j
@Configuration
public class InitBean {
    @Autowired
    private OpenkeyMapper openkeyMapper;
    @Autowired
    private IBaseConfigService baseConfigService;

    @Bean
    public OpenAiStreamClient openAiStreamClient() {
        List<OpenkeyVO> openkeys = openkeyMapper.listOpenkeyByModel(ChatModelEnum.CHAT_GPT.getValue());
        if (ValidatorUtil.isNullIncludeArray(openkeys)) {
            log.error("未加载到ChatGpt模型token数据");
            return new OpenAiStreamClient();
        }
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                // 如使用代理 请更换为代理地址
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8080)))
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();
        BaseInfoDTO baseInfo = baseConfigService.getBaseConfigByName(BaseConfigConstant.BASE_INFO, BaseInfoDTO.class);
        String apiHost = null;
        if (baseInfo.getProxyType().equals(IntegerEnum.THREE.getValue()) && ValidatorUtil.isNotNull(baseInfo.getProxyAddress())) {
            if (!baseInfo.getProxyAddress().contains(StringPoolConstant.COLON)) {
                log.error("代理地址错误");
                return new OpenAiStreamClient();
            }
            String[] proxyAddress = baseInfo.getProxyAddress().split(StringPoolConstant.COLON);
            okHttpClient = new OkHttpClient
                    .Builder()
                    // 如使用代理 请更换为代理地址
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress[0], Integer.valueOf(proxyAddress[1]))))
                    .addInterceptor(httpLoggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(600, TimeUnit.SECONDS)
                    .readTimeout(600, TimeUnit.SECONDS)
                    .build();
        } else if (baseInfo.getProxyType().equals(IntegerEnum.TWO.getValue()) && ValidatorUtil.isNotNull(baseInfo.getProxyServer())) {
            apiHost = baseInfo.getProxyServer();
        }
        return OpenAiStreamClient
                .builder()
                .apiHost(apiHost)
                .apiKey(openkeys.stream().map(v -> v.getAppKey()).collect(Collectors.toList()))
                //自定义key使用策略 默认随机策略
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                .build();
    }

    @Bean
    public OpenAiClient openAiClient() {
        List<OpenkeyVO> openkeys = openkeyMapper.listOpenkeyByModel(ChatModelEnum.CHAT_GPT.getValue());
        if (ValidatorUtil.isNullIncludeArray(openkeys)) {
            log.error("未加载到ChatGpt模型token数据");
            return new OpenAiClient();
        }
        //本地开发需要配置代理地址
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 如使用代理 请更换为代理地址
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8080)))
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();
        BaseInfoDTO baseInfo = baseConfigService.getBaseConfigByName(BaseConfigConstant.BASE_INFO, BaseInfoDTO.class);
        String apiHost = null;
        if (baseInfo.getProxyType().equals(IntegerEnum.THREE.getValue()) && ValidatorUtil.isNotNull(baseInfo.getProxyAddress())) {
            if (!baseInfo.getProxyAddress().contains(StringPoolConstant.COLON)) {
                log.error("代理地址错误");
                return new OpenAiClient();
            }
            String[] proxyAddress = baseInfo.getProxyAddress().split(StringPoolConstant.COLON);
            okHttpClient = new OkHttpClient
                    .Builder()
                    // 如使用代理 请更换为代理地址
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress[0], Integer.valueOf(proxyAddress[1]))))
                    .addInterceptor(httpLoggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(600, TimeUnit.SECONDS)
                    .readTimeout(600, TimeUnit.SECONDS)
                    .build();
        } else if (baseInfo.getProxyType().equals(IntegerEnum.TWO.getValue()) && ValidatorUtil.isNotNull(baseInfo.getProxyServer())) {
            apiHost = baseInfo.getProxyServer();
        }
        return OpenAiClient
                .builder()
                .apiHost(apiHost)
                // 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
                .apiKey(openkeys.stream().map(v -> v.getAppKey()).collect(Collectors.toList()))
                //自定义key使用策略 默认随机策略
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                .build();
    }

    /**
     * 文心一言
     *
     * @return
     */
    @Bean
    public WenXinClient wenXinClient() {
        List<OpenkeyVO> openkeys = openkeyMapper.listOpenkeyByModel(ChatModelEnum.WENXIN.getValue());
        if (ValidatorUtil.isNullIncludeArray(openkeys)) {
            log.error("未加载到文心一言模型token数据");
            return new WenXinClient();
        }
        OpenkeyVO openkey = openkeys.get(0);
        if (ValidatorUtil.isNull(openkey.getAppKey()) || ValidatorUtil.isNull(openkey.getAppSecret())) {
            log.error("未获取到文心一言模型token数据");
            return new WenXinClient();
        }
        String accessToken = WenXinConfig.getAccessToken(openkey.getAppKey(), openkey.getAppSecret());
        return WenXinClient.builder()
                .logLevel(HttpLoggingInterceptor.Level.BASIC)
                .accessToken(accessToken).apiKey(openkey.getAppKey()).build();
    }

    /**
     * 通义千问
     *
     * @return
     */
    @Bean
    public QianWenClient qianWenClient() {
        List<OpenkeyVO> openkeys = openkeyMapper.listOpenkeyByModel(ChatModelEnum.QIANWEN.getValue());
        if (ValidatorUtil.isNullIncludeArray(openkeys)) {
            log.error("未加载到通义千问模型token数据");
            return new QianWenClient();
        }
        OpenkeyVO openkey = openkeys.get(0);
        return new QianWenClient(openkey.getAppKey());
    }

    /**
     * 讯飞星火
     *
     * @return
     */
    @Bean
    public SparkClient sparkClient() {
        List<OpenkeyVO> openkeys = openkeyMapper.listOpenkeyByModel(ChatModelEnum.SPARK.getValue());
        if (ValidatorUtil.isNullIncludeArray(openkeys)) {
            log.error("未加载到智谱清言模型token数据，请添加后需要重启系统");
            return new SparkClient();
        }
        OpenkeyVO openkey = openkeys.get(0);
        return new SparkClient(openkey.getAppId(), openkey.getAppKey(), openkey.getAppSecret());
    }

    /**
     * 智谱清言
     *
     * @return
     */
    @Bean
    public ZhiPuClient zhiPuClient() {
        List<OpenkeyVO> openkeys = openkeyMapper.listOpenkeyByModel(ChatModelEnum.ZHIPU.getValue());
        if (ValidatorUtil.isNullIncludeArray(openkeys)) {
            log.error("未加载到智谱清言模型token数据，请添加后需要重启系统");
            return new ZhiPuClient();
        }
        OpenkeyVO openkey = openkeys.get(0);
        if (ValidatorUtil.isNull(openkey.getAppKey()) || ValidatorUtil.isNull(openkey.getAppSecret())) {
            log.error("未获取到智谱清言模型token数据");
            return new ZhiPuClient();
        }
        return ZhiPuClient.builder().appKey(openkey.getAppKey()).appSecret(openkey.getAppSecret()).build();
    }

    /**
     * 月之暗面
     *
     * @return
     */
    @Bean
    public MoonshotClient moonshotClient() {
        List<OpenkeyVO> openkeys = openkeyMapper.listOpenkeyByModel(ChatModelEnum.MOONSHOT.getValue());
        if (ValidatorUtil.isNullIncludeArray(openkeys)) {
            log.error("未加载到月之暗面模型token数据，请添加后需要重启系统");
            return new MoonshotClient();
        }
        OpenkeyVO openkey = openkeys.get(0);
        if (ValidatorUtil.isNull(openkey.getAppKey())) {
            log.error("未获取到月之暗面模型token数据");
            return new MoonshotClient();
        }
        return MoonshotClient.builder().apiKey(openkey.getAppKey()).build();
    }

}
