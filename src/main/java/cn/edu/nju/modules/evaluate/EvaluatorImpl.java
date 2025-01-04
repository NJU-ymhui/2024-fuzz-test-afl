package cn.edu.nju.modules.evaluate;

import cn.edu.nju.util.Log;
import cn.edu.nju.util.manager.ResourcesManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.Map;

public class EvaluatorImpl implements Evaluator {
    ResourcesManager resourcesManager;
    @Override
    public void eval(Map<Integer, Double> epoch2Coverage) {
        // 1. 创建数据集
        DefaultCategoryDataset dataset = createDataset(epoch2Coverage);

        // 2. 创建折线图
        JFreeChart chart = createChart(dataset);

        // 3. 将图表显示在一个面板上
        displayChart(chart);
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
}
