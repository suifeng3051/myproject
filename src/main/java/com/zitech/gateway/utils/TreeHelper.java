package com.zitech.gateway.utils;


import com.zitech.gateway.common.LogicalException;

import java.util.*;

/**
 * Created by bobo on 4/17/16.
 */
public class TreeHelper {

    /**
     * build a tree
     * @param nodeList
     * @return the root of the tree
     */
    public static TreeNode buildTree(List<? extends TreeNode> nodeList) {
        TreeNode root = null;
        Map<Integer, TreeNode> nodesHashMap = new HashMap<>();

        // initialize node hash map
        for (TreeNode node : nodeList) {
            nodesHashMap.put(node.getId(), node);
            if (node.getPid() == null || node.getPid() == 0) {
                if (root == null)
                    root = node;
                else
                    throw new LogicalException(1, "too many root");
            }
        }

        if (root == null)
            return null;

        // process children
        for (TreeNode node : nodeList) {
            Integer pid = node.getPid();

            TreeNode parent = nodesHashMap.get(pid);
            if (parent == null)
                continue;

            node.setParent(parent);
            parent.getChildren().add(node);
        }

        // sort children
        nodeList.stream().filter(node -> node.getChildren().size() > 0).forEach(node -> {
            node.getChildren().sort((n1, n2) -> n1.getRank() - n2.getRank());
        });

        return root;
    }

    /**
     * 从根节点, 根据id获取子节点
     * @param root
     * @param id
     * @return 子节点或者null
     */
    public static TreeNode getNodeById(TreeNode root, Integer id) {
        if(root.getId().equals(id))
            return root;

        List<TreeNode> children = root.getChildren();
        TreeNode result = null;
        for (TreeNode node : children) {
            if(result != null)
                break;

            if (node.getId().equals(id))
                result = node;

            if (node.getChildren().size() > 0) {
                result = getNodeById(node, id);
            }
        }
        return result;
    }

    /**
     * 获取所有的父节点, 包含子节点自身
     * @param root
     * @param id
     * @return 父节点(从根 -> 子节点的顺序)
     */
    public static List<TreeNode> getParentNodes(TreeNode root, Integer id) {
        TreeNode cur = TreeHelper.getNodeById(root, id);
        if(cur == null) {
            throw new LogicalException(1, "节点不存在");
        }

        List<TreeNode> parents = new ArrayList<>();
        parents.add(cur);

        TreeNode parent = cur.getParent();
        while (parent != null) {
            parents.add(parent);
            parent = parent.getParent();
        }
        Collections.reverse(parents);
        return parents;
    }
}
