package ch.zhaw.bodybuilding.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ForumChangeDTO {
    private String text;
    private String creator;
    private String beitragCreator;
}