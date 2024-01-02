package com.congee02.csse;

import com.alibaba.fastjson.JSON;

import java.util.function.Function;

/**
 * SSE 配置
 * @author gn
 */
@SuppressWarnings("unused")
public class SseConfig {


    /**
     * 将原始数据转为字符串
     */
    private Function<Object /*原始数据*/, String /*转化的字符串*/> converter;

    /**
     * 对原始数据进行预处理
     */
    private Function<Object, Object> rawDataPreProcessor;

    public SseConfig(Function<Object, String> converter, Function<Object, Object> rawDataPreProcessor) {
        this.converter = converter;
        this.rawDataPreProcessor = rawDataPreProcessor;
    }

    private static final Function<Object, String> DEFAULT_CONVERTER = JSON::toJSONString;
    private static final Function<Object, Object> DEFAULT_AFTER_CONVERTING = Function.identity();
    public SseConfig() {
        this(
                DEFAULT_CONVERTER,
                DEFAULT_AFTER_CONVERTING
        );
    }

    public Function<Object, String> getConverter() {
        return converter;
    }

    public SseConfig setConverter(Function<Object, String> converter) {
        this.converter = converter;
        return this;
    }

    public Function<Object, Object> getRawDataPreProcessor() {
        return rawDataPreProcessor;
    }

    public SseConfig setRawDataPreProcessor(Function<Object, Object> rawDataPreProcessor) {
        this.rawDataPreProcessor = rawDataPreProcessor;
        return this;
    }
}
