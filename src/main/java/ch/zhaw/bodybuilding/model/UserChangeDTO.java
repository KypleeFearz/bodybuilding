package ch.zhaw.bodybuilding.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserChangeDTO {
    private String userName;
    private String trainingId;
}