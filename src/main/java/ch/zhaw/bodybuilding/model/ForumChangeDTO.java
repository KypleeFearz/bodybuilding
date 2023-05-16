package ch.zhaw.bodybuilding.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ForumChangeDTO {
    private String user;
    private String text;
    private String creator;
}