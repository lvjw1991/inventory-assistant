package com.example.recover.utils;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Cell;

import java.util.List;

public class PriceCompareStyleHandler implements CellWriteHandler {

    private final int priceCompareColumnIndex;

    public PriceCompareStyleHandler(int priceCompareColumnIndex) {
        this.priceCompareColumnIndex = priceCompareColumnIndex;
    }

    @Override
    public void afterCellDispose(
            WriteSheetHolder writeSheetHolder,
            WriteTableHolder writeTableHolder,
            List<WriteCellData<?>> cellDataList,
            Cell cell,
            Head head,
            Integer relativeRowIndex,
            Boolean isHead) {

        if (Boolean.TRUE.equals(isHead)
                || cell.getColumnIndex() != priceCompareColumnIndex
                || cellDataList == null
                || cellDataList.isEmpty()) {
            return;
        }

        String result = cell.toString().trim();

        Short color;
        switch (result) {
            case "高于POS":
                color = IndexedColors.RED.getIndex();
                break;
            case "低于POS":
                color = IndexedColors.GREEN.getIndex();
                break;
            case "价格缺失":
                color = IndexedColors.ORANGE.getIndex();
                break;
            default:
                return;
        }

        WriteCellData<?> cellData = cellDataList.get(0);
        WriteCellStyle style = new WriteCellStyle();

        WriteFont font = new WriteFont();
        font.setColor(color);
        font.setBold(true);

        style.setWriteFont(font);
        cellData.setWriteCellStyle(style);
    }
}