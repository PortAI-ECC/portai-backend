package com.portai.global.util;

import com.portai.global.exception.CustomException;
import com.portai.global.exception.ErrorCode;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 로컬 디스크에 파일을 저장/조회하는 임시 구현체.
 * TODO: 배포 환경에서는 S3 등 외부 스토리지로 교체 예정. 도메인 코드는 이 컴포넌트만 바라보므로 교체 시 영향 범위가 최소화됨.
 */
@Component
public class LocalFileStorage {

    private static final String BASE_DIR = "uploads";

    // 파일을 저장하고, 이후 조회에 사용할 경로(문자열)를 반환
    public String store(MultipartFile file, String subDir) {
        try {
            Path dir = Paths.get(BASE_DIR, subDir);
            Files.createDirectories(dir);

            String originalFilename = StringUtils.cleanPath(
                    file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
            String storedFilename = UUID.randomUUID() + "_" + originalFilename;

            Path target = dir.resolve(storedFilename);
            file.transferTo(target);

            return target.toString();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_STORAGE_ERROR);
        }
    }

    // 저장된 경로로부터 다운로드 가능한 Resource를 조회
    public Resource loadAsResource(String storedPath) {
        try {
            Path file = Paths.get(storedPath);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        } catch (MalformedURLException e) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }
    }
}
