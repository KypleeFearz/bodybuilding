package ch.zhaw.bodybuilding.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ForumChangeDTO {
    private String text;
    private String creator;
    private String beitragCreator;
}