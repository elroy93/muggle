package com.onemuggle.dag.example;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.onemuggle.dag.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DagTestExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        for (int i = 0; i < 5; i++) {
            doTest();
            System.err.println("\n=======================================================\n");
            Thread.sleep(1 * 1000);
        }
        System.exit(9);

    }

    private static void doTest() throws InterruptedException, ExecutionException {

        long start = System.currentTimeMillis();

        Map<String, String> ctx = new LinkedHashMap<>();
        DagResult dagResult = dagExecutor.submit(ctx);
        Object result = dagResult.getFuture().get();
        List<DagNodeMonitor> monitors = dagResult.getMonitors();

        System.out.println(StrUtil.format("============================== \n " +
                        "耗时: {}ms \n result : {}",
                System.currentTimeMillis() - start, result));

        System.out.println("==============================");
        monitors.forEach(monitor -> System.out.println(((DefaultDagNodeMonitor)monitor).prettyPrint()));

    }


    private static IDagNode<Map<String, String>> instanceClazz(Class<? extends IDagNode> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }














    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50,
            50,
            5,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(1000),
            new NamedThreadFactory("执行线程-", false));
    public static ThreadPoolExecutor monitorThreadPoolExecutor = new ThreadPoolExecutor(1,
            1,
            5,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(1000),
            new NamedThreadFactory("监控线程-", false));

    public static List<IDagNode<Map<String, String>>> nodes = Lists.newArrayList
            (A.class, A2.class, B.class, B2_Async.class, C.class)
            .stream()
            .map(clazz -> instanceClazz(clazz))
            .collect(Collectors.toList());

    public static IDagExecutor dagExecutor = new SimpleDagExecutor<>(threadPoolExecutor, monitorThreadPoolExecutor, nodes, () -> Lists.newArrayList(new DefaultDagNodeMonitor()));




}
