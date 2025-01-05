package cn.edu.nju.modules.evaluate;

import cn.edu.nju.util.Log;
import cn.edu.nju.util.manager.ResourcesManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class EvaluatorImpl implements Evaluator {
    ResourcesManager resourcesManager;
    @Override
    public void eval(Map<Integer, Double> epoch2Coverage, String output) {
        // 1. 创建数据集
        DefaultCategoryDataset dataset = createDataset(epoch2Coverage);

        // 2. 创建折线图
        JFreeChart chart = createChart(dataset);

        // 3. 将图表显示在一个面板上
        displayChart(chart);

        // 保存
        saveChartAsImage(chart, System.getProperty("user.dir") + "/" + output + "/coverage.png");
    }

    @Override
    public void eval(Map<Integer, Integer> epoch2Crash, String output, String dummy) {
        // 1. 创建数据集
        DefaultCategoryDataset dataset = createDataset4Crash(epoch2Crash);

        // 2. 创建折线图
        JFreeChart chart = createChart4Crash(dataset);

        // 3. 将图表显示在一个面板上
        displayChart(chart);

        // 保存
        saveChartAsImage(chart, System.getProperty("user.dir") + "/" + output + "/crash.png");
    }

    @Override
    public void monitor(String msg) {
        Log.info(msg);
    }

    @Override
    public void register(ResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
    }

    private DefaultCategoryDataset createDataset(Map<Integer, Double> epoch2Coverage) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 将数据填充到数据集
        for (Map.Entry<Integer, Double> entry : epoch2Coverage.entrySet()) {
            Integer epoch = entry.getKey();
            Double coverage = entry.getValue();

            // "Epoch" 是 x 轴标签，"Coverage" 是 y 轴标签
            dataset.addValue(coverage, "Coverage", epoch);
        }

        return dataset;
    }

    private DefaultCategoryDataset createDataset4Crash(Map<Integer, Integer> epoch2Coverage) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 将数据填充到数据集
        for (Map.Entry<Integer, Integer> entry : epoch2Coverage.entrySet()) {
            Integer epoch = entry.getKey();
            Integer coverage = entry.getValue();

            // "Epoch" 是 x 轴标签，"Coverage" 是 y 轴标签
            dataset.addValue(coverage, "Crash", epoch);
        }

        return dataset;
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {
        // 创建折线图
        return ChartFactory.createLineChart(
                "Coverage Over Epochs",  // 图表标题
                "Epoch",                 // x 轴标签
                "Coverage",              // y 轴标签
                dataset,                 // 数据集
                PlotOrientation.VERTICAL, // 图表方向
                true,                    // 是否显示图例
                true,                    // 是否生成工具提示
                false                    // 是否生成 URL 链接
        );
    }

    private JFreeChart createChart4Crash(DefaultCategoryDataset dataset) {
        // 创建折线图
        return ChartFactory.createLineChart(
                "Crash Over Epochs",  // 图表标题
                "Epoch",                 // x 轴标签
                "Crash",              // y 轴标签
                dataset,                 // 数据集
                PlotOrientation.VERTICAL, // 图表方向
                true,                    // 是否显示图例
                true,                    // 是否生成工具提示
                false                    // 是否生成 URL 链接
        );
    }

    private void displayChart(JFreeChart chart) {
        // 创建面板
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        // 创建一个窗口，显示图表
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * 将图表保存为图像文件（PNG格式）
     *
     * @param chart 需要保存的图表
     * @param output 图像文件的输出路径
     */
    private void saveChartAsImage(JFreeChart chart, String output) {
        try {
            // 设定保存图像的文件路径
            File outputFile = new File(output);

            // 创建目标文件夹（如果不存在的话）
            outputFile.getParentFile().mkdirs();

            // 保存为PNG格式，指定图像的宽度和高度
            ChartUtils.saveChartAsPNG(outputFile, chart, 800, 600);
            System.out.println("Chart saved to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving chart: " + e.getMessage());
        }
    }
}
