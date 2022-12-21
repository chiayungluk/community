package io.chiayungluk.community;

import lombok.Data;

@Data
public class PostDto {
    private String id;
    private long timestamp;
    private String username;
    private String userId;
    private String payload;

}
