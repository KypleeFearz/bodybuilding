package ch.zhaw.bodybuilding.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Document("forum")
public class Forum {
@Id
private String id;

@NonNull
private String creator;

private Beitrag[]beitraege=new Beitrag[0];


}
