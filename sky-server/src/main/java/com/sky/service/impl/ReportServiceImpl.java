package com.sky.service.impl;

import com.aliyun.oss.common.utils.StringUtils;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkSpaceService workSpaceService;


    @Override

    public TurnoverReportVO getTurnover(LocalDate begin,LocalDate end){
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)) {
            //日期计算，计算指定日期的后一天对应的日期
            begin = begin.plusDays(1);
            dateList.add(begin);

        }

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("status", Orders.COMPLETED);
            map.put("begin",beginTime);
            map.put("end", endTime);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
            log.info("查询到的营业额：{}", turnover);
        }
        // 将 LocalDate 转成 String 列表
        List<String> dateStrList = dateList.stream()
                .map(d -> d.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .collect(Collectors.toList());

        // 将 Double 转成 String 列表
        List<String> turnoverStrList = turnoverList.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        // 拼接字符串
        String dateStr = StringUtils.join(",", dateStrList); // 注意：阿里云的 join(separator, collection)
        String turnoverStr = StringUtils.join(",", turnoverStrList);

        return TurnoverReportVO.builder()
                .dateList(dateStr)
                .turnoverList(turnoverStr)
                .build();
    }

    @Override
    public UserReportVO getUser(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> newUserList = new ArrayList<>(); // 新增用户数
        List<Integer> totalUserList = new ArrayList<>(); // 总用户数

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 新增用户数量：创建时间在当天的用户
            Integer newUser = getUserCount(beginTime, endTime);
            // 总用户数量：截止到当天的所有用户
            Integer totalUser = getUserCount(null, endTime);

            newUserList.add(newUser == null ? 0 : newUser);
            totalUserList.add(totalUser == null ? 0 : totalUser);
        }

        // 转换为 String 列表
        List<String> dateStrList = dateList.stream()
                .map(d -> d.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .collect(Collectors.toList());

        List<String> newUserStrList = newUserList.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        List<String> totalUserStrList = totalUserList.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        // 使用 Java 自带的 String.join 拼接（推荐）
        String dateStr = String.join(",", dateStrList);
        String newUserStr = String.join(",", newUserStrList);
        String totalUserStr = String.join(",", totalUserStrList);

        return UserReportVO.builder()
                .dateList(dateStr)
                .newUserList(newUserStr)
                .totalUserList(totalUserStr)
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 每天订单总数集合
        List<Integer> orderCountList = new ArrayList<>();
        // 每天有效订单数集合
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // 查询每天的总订单数
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            // 查询每天的有效订单数
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount == null ? 0 : orderCount);
            validOrderCountList.add(validOrderCount == null ? 0 : validOrderCount);
        }

        // 时间区间内的总订单数
        Integer totalOrderCount = orderCountList.stream().mapToInt(Integer::intValue).sum();
        // 时间区间内的总有效订单数
        Integer validOrderTotal = validOrderCountList.stream().mapToInt(Integer::intValue).sum();

        // 计算订单完成率
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 :
                (double) validOrderTotal / totalOrderCount;

        // 转换为字符串列表
        List<String> dateStrList = dateList.stream()
                .map(d -> d.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .collect(Collectors.toList());

        List<String> orderCountStrList = orderCountList.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        List<String> validOrderStrList = validOrderCountList.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        // 使用 Java 原生 String.join（推荐）
        String dateStr = String.join(",", dateStrList);
        String orderCountStr = String.join(",", orderCountStrList);
        String validOrderStr = String.join(",", validOrderStrList);

        return OrderReportVO.builder()
                .dateList(dateStr)
                .orderCountList(orderCountStr)
                .validOrderCountList(validOrderStr)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderTotal)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 添加日志，打印查询的时间范围
        log.info("Querying sales top 10 from {} to {}", beginTime, endTime);

        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);

        // 打印结果集大小
        log.info("Retrieved {} records", goodsSalesDTOList.size());

        if (goodsSalesDTOList.isEmpty()) {
            log.warn("No sales data found for the given date range.");
        }

        String nameList = String.join(",", goodsSalesDTOList.stream()
                .map(GoodsSalesDTO::getName)
                .collect(Collectors.toList()));

        String numberList = String.join(",", goodsSalesDTOList.stream()
                .map(goods -> String.valueOf(goods.getNumber()))
                .collect(Collectors.toList()));

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**导出近30天的运营数据报表
     * @param response
     **/
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        //查询概览运营数据，提供给Excel模板文件
        BusinessDataVO businessData = workSpaceService.getBusinessData(LocalDateTime.of(begin,LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            //基于提供好的模板文件创建一个新的Excel表格对象
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //获得Excel文件中的一个Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            sheet.getRow(1).getCell(1).setCellValue(begin + "至" + end);
            //获得第4行
            XSSFRow row = sheet.getRow(3);
            //获取单元格
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                //准备明细数据
                businessData = workSpaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            //通过输出流将文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.flush();
            out.close();
            excel.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 根据时间区间统计用户数量
     * @param beginTime
     * @param endTime
     * @return
     */
    private Integer getUserCount(LocalDateTime beginTime, LocalDateTime endTime) {
        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("end", endTime);
        return userMapper.countByMap(map);
    }

    /**
     * 根据时间区间统计指定状态的订单数量
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status) {
        Map map = new HashMap();
        map.put("status", status);
        map.put("begin",beginTime);
        map.put("end", endTime);
        return orderMapper.countByMap(map);
    }
}
