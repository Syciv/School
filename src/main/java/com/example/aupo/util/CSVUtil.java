package com.example.aupo.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class CSVUtil {


    /**
     * Просто достаёот данные из файла, если не удаётся, выкидывает IOException
     */
    public String getFileContent(MultipartFile file) throws IOException {
        byte[] bytes;
        String fileContent;
        bytes = file.getBytes();
        fileContent = new String(bytes, "cp1251");
        return fileContent;
    }

    /**
     * Возвращает лист - строк массивов - элементов строк csv файла
     */
    public List<String[]> parseCSV(String csvContent, String sep){
        String[] lines = csvContent.split("\r\n");
        return Arrays.stream(lines).map(line -> line.split(sep)).toList();
    }


}
