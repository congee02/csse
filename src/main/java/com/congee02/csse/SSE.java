package com.congee02.csse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Server-sent Event
 * <p>
 * @author gn
 */
@SuppressWarnings("unused")
public final class SSE {

    private SSE() {}

    /* SSE 全局配置 ---- 开始 */
    private static SseConfig globalSseConfig = new SseConfig();

    public static SseConfig getGlobalSseConfig() {
        return globalSseConfig;
    }

    public static void setGlobalSseConfig(SseConfig globalSseConfig) {
        SSE.globalSseConfig = globalSseConfig;
    }

    /* SSE 全局配置 ---- 结束 */

    /* SSE 工具类方法 ---- 开始 */
    public static void handle(final HttpServletResponse response,
                              final ThreadPoolExecutor functionPool, final ThreadPoolExecutor writerPool,
                              final Function<Object, String> converter,
                              final Supplier<?>... suppliers) {
        SseConfig sseConfig
                = new SseConfig().setConverter(converter);
        handle(response, functionPool, writerPool, sseConfig);
    }

    public static void handle(final HttpServletResponse response,
                              final ThreadPoolExecutor functionPool, final ThreadPoolExecutor writerPool,
                              final SseConfig config,
                              final Supplier<?>... suppliers) {
        contentType(response);
        try (PrintWriter writer = response.getWriter()) {
            final CountDownLatch latch = new CountDownLatch(suppliers.length);
            final ReentrantLock lock = new ReentrantLock();
            for (Supplier<?> supplier : suppliers) {
                CompletableFuture
                        .supplyAsync(
                                supplier,
                                functionPool
                        )
                        .thenAcceptAsync(
                                constructResultConsumer(
                                        latch, lock,
                                        writer,
                                        config
                                ),
                                writerPool
                        )
                ;
            }
            latch.await();
        } catch (IOException e) {
            throw new SseException("IO 错误", e);
        } catch (InterruptedException e) {
            throw new SseException("线程中断错误", e);
        }
    }

    public static void handle(final HttpServletResponse response,
                              final ThreadPoolExecutor functionPool, final ThreadPoolExecutor writerPool,
                              final Supplier<?>... suppliers) {
        handle(response, functionPool, writerPool, globalSseConfig, suppliers);
    }

    /* SSE 工具类方法 ---- 结束 */

    /* SSE 私有逻辑 ---- 开始 */
    private static Consumer<Object> constructResultConsumer(final CountDownLatch latch, final ReentrantLock lock,
                                                            final PrintWriter writer,
                                                            final SseConfig config) {
        return result -> {
            Object preProcessedResult =
                    config.getRawDataPreProcessor().apply(result);
            String convertedResult =
                    config.getConverter().apply(preProcessedResult);
            lock.lock();
            try {
                writer.write(convertedResult + "\n");
                writer.flush();
            } finally {
                lock.unlock();
                latch.countDown();
            }
        };
    }

    private static void contentType(HttpServletResponse response) {
        response.setContentType("text/event-stream;charset=UTF-8");
    }
    /* SSE 私有逻辑 ---- 结束 */

}
