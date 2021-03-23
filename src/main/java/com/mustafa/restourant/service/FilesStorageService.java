package com.mustafa.restourant.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {

     void save(MultipartFile file,String fileName);

     Resource load(String filename);

     void deleteAll();

     Stream<Path> loadAll();
}
