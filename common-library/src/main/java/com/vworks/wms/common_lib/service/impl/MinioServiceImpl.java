package com.vworks.wms.common_lib.service.impl;

import com.vworks.wms.common_lib.service.MinioService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;


@Service
@Slf4j
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;

    @Override
    public String uploadFileToMinio(MultipartFile file, String bucketName, String pathFile) {
        try {
            // Tạo bucket nếu chưa tồn tại
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(pathFile)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            // Lưu file lên MinIO
            minioClient.putObject(objectArgs);

            return "/" + bucketName + "/" + pathFile;

        } catch (Exception e) {
            log.error("{} UploadFileToMinio exception: {}", getClass().getSimpleName(), e);
            return null;
        }
    }

    @Override
    public String uploadImageMaterialToMinio(MultipartFile file, String bucketName, String folderParent, String folderChild, String pathFile) {
        try {
            // Tạo bucket nếu chưa tồn tại
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                System.out.println("Created bucketName: " + bucketName);
            }

            // Kiểm tra xem "folder" đã tồn tại chưa
            boolean folderExists = false;
            String folderPath = folderParent + "/" + folderChild + "/"; // Định nghĩa đường dẫn thư mục

            Iterable<Result<Item>> objects = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).prefix(folderPath).maxKeys(1).build()
            );

            for (Result<Item> result : objects) {
                folderExists = true;
                break; // Chỉ cần tìm thấy một object là đủ
            }

            // Nếu folder chưa tồn tại, tạo một object rỗng để giả lập folder
            if (!folderExists) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(folderPath) // Tạo thư mục giả lập
                                .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                                .build()
                );
                System.out.println("Created folder: " + folderPath);
            }

            // Upload file vào thư mục đã tạo
            String fullPath = folderPath + pathFile; // Định nghĩa đường dẫn đầy đủ cho file

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullPath) // Đường dẫn đầy đủ
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            System.out.println("File uploaded: " + fullPath);
            return "/" + bucketName + "/" + fullPath;

        } catch (Exception e) {
            log.error("{} UploadFileToMinio exception: {}", getClass().getSimpleName(), e);
            return null;
        }
    }

}
