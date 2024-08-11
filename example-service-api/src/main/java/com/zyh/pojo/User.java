package com.zyh.pojo;

import lombok.*;

import java.io.Serializable;

/**
 * @author zhuyh
 * @version v1.0
 * @description
 * @date 2024/7/24
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class User implements Serializable {

    private static final long serialVersionUID = -713296915795079477L;
    private String name;

    private int age;

}
