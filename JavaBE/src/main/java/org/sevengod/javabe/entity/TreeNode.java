package org.sevengod.javabe.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeNode {
    private Long id;
    private String name;
    private Long parentId;
    private List<TreeNode> children = new ArrayList<>();
}
