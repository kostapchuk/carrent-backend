package com.ostapchuk.car.rent.controller;

import com.ostapchuk.car.rent.dto.UploadRequestDto;
import com.ostapchuk.car.rent.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private static final String MESSAGE_1 = "Uploaded the file successfully";

    private final DocumentService documentService;

    @PostMapping
    public String save(@RequestParam("file") final MultipartFile multipartFile,
                       @RequestBody final UploadRequestDto requestDto) {
        documentService.save(multipartFile, requestDto);
        return MESSAGE_1;
    }
}
