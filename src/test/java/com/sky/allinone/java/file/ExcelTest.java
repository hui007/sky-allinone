package com.sky.allinone.java.file;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelTest {
    @Test
    public void testExcel() throws IOException {
        // result1和result2必须为同一份文件
        int keyIndex1 = 0;
        int keyIndex2 = 0;
        int keyIndex3 = 1;
        Map<String, List<Map<Integer, String>>> result1 = parseExcelToMap("/Users/jianghui/Downloads/111-hf.xls", keyIndex1); // 公司id为key
        Map<String, List<Map<Integer, String>>> result2 = parseExcelToMap("/Users/jianghui/Downloads/111-hf.xls", keyIndex2); // 业务编码为key
//        Map<String, List<Map<Integer, String>>> result3 = parseExcelToMap("/Users/jianghui/Downloads/111-hf.xls", keyIndex3); // 业务编码为key
        Map<String, List<Map<Integer, String>>> result3 = null; // 业务编码为key

        printExcelParseResult(result1);
        printExcelParseResult(result2);
//        groupByTwoExcel(result1, result2, 0, 2, 3, "10248");
        groupByOneExcel(result1, keyIndex1, 2, 3, "10424", "-v2", "10424");

        if (1 == 1) {
            return;
        }

        System.out.println(result1.keySet().toString());
        printExcelParseResult(result1);
        printExcelParseResult(result2);
        printExcelParseResult(result3);

        // 连接两个excel，类似于数据库的join操作。
//        Map<String, List<String>> resultJoin = new HashMap<String, List<String>>();
        Map<String, List<Map<Integer, String>>> resultJoin = new HashMap<String, List<Map<Integer, String>>>();
        result2.forEach((s, maps) -> {
//            List<String> oneCellValues = new ArrayList<String>();
//            result3.getOrDefault(s, new ArrayList<Map<Integer, String>>()).forEach(integerStringMap -> {
//                oneCellValues.add(integerStringMap.get(1));
//            });

            resultJoin.put(s, result3.getOrDefault(s, new ArrayList<Map<Integer, String>>()));
        });
        printExcelParseResult(resultJoin);

        // 将已经join的结果，再按另外一个主键group
        String curl = "curl -H \"Content-Type:application/json\" -X POST -d '{\"receiptType\": \"01\", \"busiSystemId\": \"deliver\", \"thirdpaySystemId\": \"hf\", \"payeruserId\":\"11400\", \"downloadId\":\"%s\", \"qryType\":\"1\", \"qryNos\":\"%s\"}' http://10.169.120.33:8008/front/recepit/req";
        result1.forEach((s1, l1) -> {

            List<String> resultGroup = new ArrayList<String>();
            l1.forEach(m1 -> {
                resultJoin.get(m1.get(keyIndex2)).forEach(mJoin -> {
                    resultGroup.add(mJoin.get(keyIndex3));
                });
            });
            int groupSize = resultGroup.size();
            System.out.println(String.format("主键：%s，包含的记录数：%d，join后的记录数：%d", s1, l1.size(), groupSize));
            String groupStr = "";
//            System.out.println(String.format("主键：%s，所有记录group后的结果：%s", s1, groupStr));
            int maxNum = 2000;
            if (groupSize > maxNum) {
                int count = groupSize / maxNum;
                for (int i = 0; i <= count + 1 && i * maxNum <= groupSize; i++){
                    groupStr = resultGroup.subList(i * maxNum > groupSize ? groupSize : i * maxNum, (i + 1) * maxNum > groupSize ? groupSize : (i + 1) * maxNum).toString();
                    System.out.println(String.format("" + curl, s1.trim() + "-" + i, groupStr.replaceAll("\\[|\\]|\\s", "")));
                }
            } else {
                groupStr = resultGroup.toString();
                System.out.println(String.format("" + curl, s1.trim(), groupStr.replaceAll("\\[|\\]|\\s", "")));
            }
        });
    }

    private void groupByOneExcel(Map<String, List<Map<Integer, String>>> result1, int keyIndex, int valueIndex,
                                 int cellIgnoreIndex1, String cellIgnoreValue1, String downloadId_postfix, String payuserId){
        // 将已经join的结果，再按另外一个主键group
        String curl = "curl -H \"Content-Type:application/json\" -X POST -d '{\"receiptType\": \"01\", \"busiSystemId\": \"deliver\", \"thirdpaySystemId\": \"hf\", \"payeruserId\":\"%s\", \"downloadId\":\"%s\", \"qryType\":\"1\", \"qryNos\":\"%s\"}' http://10.169.120.33:8008/front/recepit/req";
        result1.forEach((s1, l1) -> {

            List<String> resultGroup = new ArrayList<String>();
            l1.forEach(m1 -> {
                if (StringUtils.equals(cellIgnoreValue1, m1.get(cellIgnoreIndex1))) {
                    return;
                }

                resultGroup.add(m1.get(valueIndex));
            });
            int groupSize = resultGroup.size();
            System.out.println(String.format("主键：%s，包含的记录数：%d，join后的记录数：%d", s1, l1.size(), groupSize));
            String groupStr = "";
//            System.out.println(String.format("主键：%s，所有记录group后的结果：%s", s1, groupStr));
            int maxNum = 2000;
            if (groupSize > maxNum) {
                int count = groupSize / maxNum;
                for (int i = 0; i <= count + 1 && i * maxNum <= groupSize; i++){
                    groupStr = resultGroup.subList(i * maxNum > groupSize ? groupSize : i * maxNum, (i + 1) * maxNum > groupSize ? groupSize : (i + 1) * maxNum).toString();
                    System.out.println(String.format("" + curl, payuserId, s1.trim() + "-" + i + StringUtils.defaultString(downloadId_postfix, ""), groupStr.replaceAll("\\[|\\]|\\s", "")));
                }
            } else {
                groupStr = resultGroup.toString();
                System.out.println(String.format("" + curl, payuserId, s1.trim() + StringUtils.defaultString(downloadId_postfix, ""), groupStr.replaceAll("\\[|\\]|\\s", "")));
            }
        });
    }

    private void groupByTwoExcel(Map<String, List<Map<Integer, String>>> result1, Map<String, List<Map<Integer, String>>> result2, int cellIndex1, int cellIndex2,
        int cellIgnoreIndex1, String cellIgnoreValue1){
        // 将已经join的结果，再按另外一个主键group
        String curl = "curl -H \"Content-Type:application/json\" -X POST -d '{\"receiptType\": \"01\", \"busiSystemId\": \"deliver\", \"thirdpaySystemId\": \"hf\", \"payeruserId\":\"11400\", \"downloadId\":\"%s\", \"qryType\":\"1\", \"qryNos\":\"%s\"}' http://10.169.120.33:8008/front/recepit/req";
        result1.forEach((s1, l1) -> {

            List<String> resultGroup = new ArrayList<String>();
            l1.forEach(m1 -> {
                if (StringUtils.equals(cellIgnoreValue1, m1.get(cellIgnoreIndex1))) {
                    return;
                }

                result2.get(m1.get(cellIndex1)).forEach(m2 -> {
                    resultGroup.add(m2.get(cellIndex2));
                });
            });
            int groupSize = resultGroup.size();
            System.out.println(String.format("主键：%s，包含的记录数：%d，join后的记录数：%d", s1, l1.size(), groupSize));
            String groupStr = "";
//            System.out.println(String.format("主键：%s，所有记录group后的结果：%s", s1, groupStr));
            int maxNum = 2000;
            if (groupSize > maxNum) {
                int count = groupSize / maxNum;
                for (int i = 0; i <= count + 1 && i * maxNum <= groupSize; i++){
                    groupStr = resultGroup.subList(i * maxNum > groupSize ? groupSize : i * maxNum, (i + 1) * maxNum > groupSize ? groupSize : (i + 1) * maxNum).toString();
                    System.out.println(String.format("" + curl, s1.trim() + "-" + i, groupStr.replaceAll("\\[|\\]|\\s", "")));
                }
            } else {
                groupStr = resultGroup.toString();
                System.out.println(String.format("" + curl, s1.trim(), groupStr.replaceAll("\\[|\\]|\\s", "")));
            }
        });
    }

    /*
     * 遍历该sheet的行，将excel转换为Map<String, List<Map<Sring, String>>>
     */
    private Map<String, List<Map<Integer, String>>> parseExcelToMap(String file, int keyIndex) throws IOException {
        Map<String, List<Map<Integer, String>>> result = new HashMap<String, List<Map<Integer, String>>>();
        try (InputStream stream = new FileInputStream(file)){
            //读取一个excel表的内容
            POIFSFileSystem fs = new POIFSFileSystem(stream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);

            //获取excel表的第一个sheet
            HSSFSheet sheet = wb.getSheetAt(0);
            if (sheet == null) {
                return result;
            }

            //遍历该sheet的行，将excel转换为Map<String, List<Map<Sring, String>>>
            for (int totalRowNum = 1; totalRowNum <= sheet.getLastRowNum(); totalRowNum++) {
                HSSFRow row = sheet.getRow(totalRowNum);
                HSSFCell keyCell = row.getCell(keyIndex);
                CellType keyCellTypeEnum = keyCell.getCellTypeEnum();
                String keyValue = "";
                if (CellType.STRING.equals(keyCellTypeEnum)) {
                    keyValue = keyCell.getStringCellValue();
                } else if(CellType.NUMERIC.equals(keyCellTypeEnum)){
                    keyValue = new Double(keyCell.getNumericCellValue()).intValue() + "";
                }

                if (!result.containsKey(keyValue)) {
                    result.put(keyValue, new ArrayList<Map<Integer, String>>());
                }

                Map<Integer, String> rowData = new HashMap<>();
                row.forEach(cell -> {
                    CellType cellTypeEnum = cell.getCellTypeEnum();
                    if (CellType.STRING.equals(cellTypeEnum)) {
                        rowData.put(cell.getColumnIndex(), cell.getStringCellValue());
                    } else if(CellType.NUMERIC.equals(cellTypeEnum)){
                        rowData.put(cell.getColumnIndex(), cell.getNumericCellValue() + "");
                    }

                });
                result.get(keyValue).add(rowData);
            }

//            printExcelParseResult(result);
        }

        return result;
    }

    private void printExcelParseResult(Map<String, List<Map<Integer, String>>> result) {
        Map<String, Integer> resultTotalSize = new HashMap<String, Integer>();
        resultTotalSize.put("size", 0);
        System.out.println("主键数量：" + result.size());
        result.forEach((s, maps) -> {
            resultTotalSize.put("size", resultTotalSize.get("size") + maps.size());
            System.out.println(String.format("主键：%s，包含的记录数：%s", s, maps.size() + ""));
        });
        System.out.println("总记录数：" + resultTotalSize.get("size"));
    }
}
