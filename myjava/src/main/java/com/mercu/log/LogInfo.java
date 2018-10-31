package com.mercu.log;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
@Data
@Entity
@Table(name = "LOG_INFO")
@NoArgsConstructor
public class LogInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private Date dateTime;
    private String type;
    private String description;
    private String brief;

}
