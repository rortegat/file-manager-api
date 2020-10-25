package com.riot.fileManagerApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PictureDTO {
    private String filename;
    private String url;
    private String type;
    private long size;
}
