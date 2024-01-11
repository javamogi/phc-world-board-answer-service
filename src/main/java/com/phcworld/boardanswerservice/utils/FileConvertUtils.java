package com.phcworld.boardanswerservice.utils;



import com.phcworld.boardanswerservice.exception.model.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class FileConvertUtils {
    public static String getFileData(String imgName){
        // 임시 업로드 폴더
        // 추후 aws s3 연동 또는 다른 곳으로
        String filePath = "src/main/resources/static/image/";
        File file = new File(filePath + imgName);
        if(!file.isFile()){
            throw new NotFoundException();
        }
        String imgData = "";
        try {
            byte[] bytesFile = Files.readAllBytes(file.toPath());
            imgData = Base64.getEncoder().encodeToString(bytesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String fileExtension = imgName.substring(imgName.lastIndexOf(".") + 1);
        // image 이외의 파일은 어떻게 처리할 것인가?
        String data = "data:image/" + fileExtension + ";base64,";
        return data + imgData;
//        return imgData;
    }
}
