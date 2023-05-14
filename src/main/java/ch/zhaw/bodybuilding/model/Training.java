package ch.zhaw.bodybuilding.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Document("training")
public class Training {
    @Id
    private String id;
    @NonNull
    private String ubung;
    @NonNull
    private String satz;
    @NonNull
    private String wiederholung;
    
    private Integer pause;

}
