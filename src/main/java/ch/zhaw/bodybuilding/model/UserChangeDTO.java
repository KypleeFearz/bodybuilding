package ch.zhaw.bodybuilding.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserChangeDTO {
    private String userName;
    private String trainingId;
}