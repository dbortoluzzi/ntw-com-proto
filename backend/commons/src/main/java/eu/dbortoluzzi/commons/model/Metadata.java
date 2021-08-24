package eu.dbortoluzzi.commons.model;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Metadata {
    private String instance;
    private String checksum;
}