package eu.dbortoluzzi.commons.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoutingElement {
    private boolean master;
    private String name;
}