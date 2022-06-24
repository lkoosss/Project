package com.example.common.util;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


@Service
public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    // 암호화, 복호화 관련 변수
    private static String key;

    @Value("${fileManager.keyValue}")
    public void setKey(String value) {
        this.key = value;
    }
    /**
     * <pre>
     * saveDataToFile
     * 데이터를 파일로 저장
     * </pre>
     *
     * @param saveData
     * @param destinationPath
     */
    public static boolean saveDataToFile(byte[] saveData, String destinationPath) {
        Boolean result = true;                      // 메서드의 실행결과
        Path toPath = Paths.get(destinationPath);   // Data를 저장할 경로의 Path 클래스 생성

        try {
            // 유효성 검사
            if (saveData.length == 0) {                     // 저장할 Data가 없으면 result = false
                LOGGER.error("saveData to file failed, input data is empty");
                result = false;
            } else if (!Files.exists(toPath.getParent())) { // 저장할 위치에 디렉토리가 없으면 생성해준다.
                Files.createDirectories(toPath.getParent());
                LOGGER.info("Creating Directory : {} completed", toPath.getParent());
            }

            // 유효성 검사에 이상이 없으면
            if (result == true) {
                // Data를 저장한 파일 생성
                Files.write(toPath, saveData);
            }
        } catch (IOException e) {
            LOGGER.error("saveData to file failed, saveDataToFile Exception : {}", e.getMessage());
            result = false;
        } finally {
            // 사용한 지역변수를 정리
            destinationPath = null;
            saveData = null;
            toPath = null;
        }

        return result;
    }

    /**
     * <pre>
     * loadDataFromFile
     * 파일을 String으로 읽기
     * </pre>
     *
     * @param sourcePath
     */
    public static String loadDataFromFile(String sourcePath) {
        Boolean result  = true;                           // 메서드 실행결과
        Path fromPath = Paths.get(sourcePath);            // 불러올 파일 경로의 Path 클래스
        File fromFile = null;                             // 불러올 파일의 File 클래스
        String loadData = null;                           // 불러온 파일의 내용을 저장할 String 클래스

        // 유효성 검사
        if (!Files.exists(fromPath)) {                                                     // 파일이 없으면 result = false
            LOGGER.error("File read Failed, Source File : {}, not exist", fromPath);
            result = false;
        } else if (Files.isDirectory(fromPath)) {                                          // 파일이 아니라 디렉토리이면 result = false
            LOGGER.error("File read Failed, Source File : {}, is a directory", fromPath);
            result = false;
        }

        // 유효성 검사 후 이상없을 때
        if (result == true) {
            try {
                fromFile = fromPath.toFile();                                           // 불러올 파일의 File 클래스를 생성
                loadData = FileUtils.readFileToString(fromFile, "UTF-8");      // FilesUtils를 이용해 읽어온 내용을 String으로 저장한다
            } catch (IOException e) {
                LOGGER.error("File read Failed, loadDataFromFile Exception : {}", e.getMessage());
                result = false;
            }
        }

        // 사용한 지역변수 정리
        sourcePath = null;
        fromPath = null;
        fromFile = null;
        result = null;

        return loadData;
    }

    /**
     * <pre>
     * compressFromFiles
     * 파일들을 압축한다.
     * </pre>
     *
     * @param sourcePathList
     * @param destinationPath
     */
    public static boolean compressFromFiles(List<String> sourcePathList, String destinationPath) {
        Boolean result = true;                              // 메서드 실행 결과
        List<Path> fromPathList = new ArrayList<Path>();    // 압축할 파일들의 절대경로 Path 클래스 리스트 생성
        Path toPath = Paths.get(destinationPath);           // 생성할 압축파일의 절대경로 Path 클래스 생성

        // 압축할 파일들의 유효성 검사 & Path 리스트에 넣기
        for (String sourcePath : sourcePathList) {
            if (!Files.exists(Paths.get(sourcePath))) {                 // 압축할 파일이 존재하지 않으면 Skip
                LOGGER.error("Files compression Failed, Source File : {}, not exist", sourcePath);
            } else if (Files.isDirectory(Paths.get(sourcePath))) {      // 압축할 파일이 디렉토리이면 Skip
                LOGGER.error("Files compression Failed, Source File : {}, is a directory", sourcePath);
            } else {                                                    // 압축할 파일이 정상적인 경우
                fromPathList.add(Paths.get(sourcePath));                // Path 리스트에 해당 압축파일 Path를 추가한다
            }
        }

        // 정상적으로 압축할 파일이 존재할 때
        if (!fromPathList.isEmpty()) {
            try(OutputStream fOut = Files.newOutputStream(toPath);
                BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
                GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
                TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut);) {

                // 압축할 파일들 수만큼 반복
                for (Path fromPath : fromPathList) {
                    TarArchiveEntry tarEntry = new TarArchiveEntry(fromPath.toFile(), fromPath.getFileName().toString());

                    tOut.putArchiveEntry(tarEntry);
                    Files.copy(fromPath,tOut);
                    tOut.closeArchiveEntry();
                }

                tOut.finish();

            } catch (IOException e) {
                LOGGER.error("Files compression Failed, compressFromFiles Exception : {}", e.getMessage());
                result = false;
            }
        } else {            // 정상적인 파일이 없을 때
            result = false;
        }
        // 사용한 지역변수 정리
        destinationPath = null;
        sourcePathList  = null;
        toPath          = null;
        fromPathList    = null;

        return result;
    }

    /**
     * <pre>
     * compressFromDirectory
     * 디렉토리를 압축한다.
     * </pre>
     * @param sourcePath
     * @param destinationPath
     */
    public static boolean compressFromDirectory(String sourcePath, String destinationPath) {
        Boolean result = true;                      // 메서드 실행 결과
        Path fromPath = Paths.get(sourcePath);      // 압축할 디렉토리 절대경로 Path 클래스
        Path toPath = Paths.get(destinationPath);   // 생성할 압축파일의 절대경로 Path 클래스


        // 압축할 디렉토리의 유효성 체크
        if (!Files.exists(fromPath)) {              // 압축할 디렉토리가 없으면 result = false
            LOGGER.error("Directory compression Failed, Source Directory : {}, is not exists", fromPath);
            result = false;
        } else if (!Files.isDirectory(fromPath)) {  // 압축할 디렉토리가 파일이면 result = false
            LOGGER.error("Directory compression Failed, Source Directory : {}, is not a Directory", fromPath);
            result = false;
        }

        // 압축할 디렉토리가 정상적일 때
        if (result == true) {
            // tar.gz로 압축 진행
            try(OutputStream fOut = Files.newOutputStream(toPath);
                BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
                GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
                TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut);) {

                Path finalFromPath = fromPath;
                Files.walkFileTree(fromPath, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                        if (attributes.isSymbolicLink()) {
                            return FileVisitResult.CONTINUE;
                        }
                        Path targetFile = finalFromPath.relativize(file);

                        try {
                            TarArchiveEntry tarEntry = new TarArchiveEntry(file.toFile(), targetFile.toString());
                            tOut.putArchiveEntry(tarEntry);
                            Files.copy(file,tOut);          // 압축 경로로 압축한 내용 전달(Write)
                            tOut.closeArchiveEntry();       // tarOutputStream 종료
                        } catch(IOException e) {
                            LOGGER.error("Directory compression Failed, compressFromDirectory Exception : {}",e.getMessage());
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
                        LOGGER.error("Directory compression Failed, compressFromDirectory Exception : {}",exc.getMessage());
                        return FileVisitResult.CONTINUE;
                    }
                });
                tOut.finish();

            } catch (IOException e) {
                LOGGER.error("Directory compression Failed, compressFromDirectory Exception : {}", e.getMessage());
                result = false;
            }
        }
        // 사용한 지역변수 정리
        toPath = null;
        fromPath = null;
        destinationPath = null;
        sourcePath = null;

        return result;
    }

    /**
     * <pre>
     * decompressFromArchiveFile
     * 압축파일을 해제한다.
     * </pre>
     * @param sourcePath
     * @param destinationPath
     */
    public static boolean decompressFromArchiveFile(String sourcePath, String destinationPath) {
        Boolean result = true;                      // 메서드 실행 결과
        Path fromPath = Paths.get(sourcePath);      // 압축파일 절대경로 Path 클래스
        Path toPath = Paths.get(destinationPath);   // 압축해제 할 파일을 저장할 절대경로 Path클래스

        //압축파일, 저장할 경로 유효성 검사
        if (!Files.exists(fromPath)) {                      // 압축파일이 없으면 유효성 = False
            LOGGER.error("ArchiveFile decompression failed, Source File : {}, not exist", fromPath);
            result = false;
        } else if (Files.isDirectory(fromPath)) {           // 압축파일이 디렉토리면 유효성 = false
            LOGGER.error("ArchiveFile decompression failed, Destination Path : {}, not directory", toPath);
            result = false;
        } else if (!Files.exists(toPath.getParent())) {     // 압축해제 할 파일을 저장할 경로가 없으면 경로 생성
            LOGGER.info("Create Destination Path : {}", toPath);
            try {
                Files.createDirectories(toPath);            // 경로 생성
            } catch (IOException e) {                       // 기타 예외 발생시 유효성 = false
                LOGGER.error("Create Destination Path failed, decompressFromArchiveFile Exception : {}", e.getMessage());
                result = false;
            }
        }

        // 압축파일이 정상적일 때
        if (result == true) {
            // tar.gz 압축해제 시작
            try(InputStream fi = Files.newInputStream(fromPath);
                BufferedInputStream bi = new BufferedInputStream(fi);
                GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
                TarArchiveInputStream ti = new TarArchiveInputStream(gzi)) {

                ArchiveEntry entry;

                // 압축파일에 들어간 파일, 디렉토리를 하나씩 꺼내서 아래 로직을 실행
                while ((entry = ti.getNextEntry()) != null) {
                    Path newPath = toPath.resolve(entry.getName());

                    if (entry.isDirectory()) {              // 꺼낸 내용이 디렉토리 일 때
                        Files.createDirectories(newPath);
                    } else {                                // 꺼낸 내용이 파일일 때
                        if (!Files.exists(newPath.getParent())) {                     // 꺼낸 파일의 상위 디렉토리가 없을 때
                            Files.createDirectories(newPath.getParent());
                        }
                        Files.copy(ti, newPath, StandardCopyOption.REPLACE_EXISTING); // 꺼낸 파일을 생성한다(덮어쓰기)
                    }
                }
            } catch (IOException e) {
                LOGGER.error("ArchiveFile decompression Failed, decompressFromArchiveFile Exception : {}", e.getMessage());
                result = false;
            }
        }

        // 사용한 변수 정리
        sourcePath      = null;
        destinationPath = null;
        fromPath        = null;
        toPath          = null;
        return result;
    }

    /**
     * <pre>
     * encryptFromFile
     * 파일을 암호화한다.
     * </pre>
     * @param sourcePath
     * @param destinationPath
     */
    public static boolean encryptFromFile(String sourcePath, String destinationPath) {
        Boolean result = true;                      // 메서드 실행 결과
        Path fromPath = Paths.get(sourcePath);      // 원본파일 절대경로 Path 클래스
        Path toPath = Paths.get(destinationPath);   // 생성할 암호화된 파일 절대경로 Path 클래스

        //암호관련 변수
        String iv = key.substring(0,16);;
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");;
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());;

        // 원본파일 유효성 체크
        if (!Files.exists(fromPath)) {
            LOGGER.error("File encryption failed, Source File : {}, not exists", fromPath);     // 원본파일이 존재하지 않으면 result = false
            result = false;
        } else if (Files.isDirectory(fromPath)) {
            LOGGER.error("File encryption failed, Source File : {}, is a directory", fromPath); // 파일이 아니라 디렉토리면 result = false
            result = false;
        }

        // 유효성 검사 통과 할 때
        if (result == true) {
            // 원본파일 암호화 시작
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

                // 원본파일의 내용을 byte로 저장한다.
                File fromFile = fromPath.toFile();
                byte[] fileContent = FileUtils.readFileToByteArray(fromFile);

                // 암호화된 원본파일 내용을 byte로 저장 후 파일로 저장한다.
                byte[] encryptedFileContent = cipher.doFinal(fileContent);
                Files.write(toPath, encryptedFileContent);

            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException |
                     IllegalBlockSizeException | IOException | BadPaddingException e) {
                LOGGER.error("File encryption failed, {}", e.getMessage());
                result = false;
            }
        }

        // 사용한 변수 정리
        ivParameterSpec     = null;
        keySpec             = null;
        iv                  = null;
        toPath              = null;
        fromPath            = null;
        destinationPath     = null;
        sourcePath          = null;

        return result;
    }

    /**
     * <pre>
     * decryptFromFile
     * 암호화 파일을 복호화한다.
     * </pre>
     * @param sourcePath
     * @param destinationPath
     */
    public static boolean decryptFromFile(String sourcePath, String destinationPath) {
        Boolean result = true;                      // 유효성 여부 저장
        Path fromPath = Paths.get(sourcePath);      // 암호화 된 파일 절대경로 Path 클래스
        Path toPath = Paths.get(destinationPath);   // 복호화 된 파일이 저장될 절대경로 Path zmffotm

        //암호관련 변수
        String iv = key.substring(0,16);;
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());

        // 암호화 파일 유효성 체크
        if (!Files.exists(fromPath)) {                  // 파일이 존재하지 않으면 result = false
            LOGGER.error("File encryption failed, Source File : {}, not exists", fromPath);
            result = false;
        } else if (Files.isDirectory(fromPath)) {       // 파일이 아니라 디렉토리면 result = false
            LOGGER.error("File encryption Failed, Source File : {}, is a directory", fromPath);
            result = false;
        }

        // 유효성 검사 통과 할 때
        if (result == true) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

                // 암호화 된 파일의 내용을 Byte로 저장
                File fromFile = fromPath.toFile();
                byte[] encryptedFileContent = FileUtils.readFileToByteArray(fromFile);

                // 복호화 된 파일의 내용을 Byte로 저장 후 파일로 저장
                byte[] decryptedFileContent = cipher.doFinal(encryptedFileContent);
                Files.write(toPath, decryptedFileContent);

            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException |
                     IOException | IllegalBlockSizeException | BadPaddingException e) {
                LOGGER.error("File encryption failed, {}", e.getMessage());
                result = false;
            }
        }

        // 사용한 변수 정리
        ivParameterSpec = null;
        keySpec         = null;
        iv              = null;
        toPath          = null;
        fromPath        = null;
        destinationPath = null;
        sourcePath      = null;

        return result;
    }
    
}
