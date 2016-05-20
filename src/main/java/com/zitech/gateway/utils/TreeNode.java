package com.zitech.gateway.utils;

import java.util.List;

/**
 * Created by bobo on 4/17/16.
 */
public interface TreeNode {

    Integer getId();

    Integer getPid();

    Integer getRank();

    List<TreeNode> getChildren();

    TreeNode getParent();

    void setParent(TreeNode parent);
}
