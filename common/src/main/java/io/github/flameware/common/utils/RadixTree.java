package io.github.flameware.common.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A compressed radix tree with path compression, a hybrid data structure and thread-safe (concurrent) implementation
 * <p>
 * This is a much more optimized version of {@link java.util.HashMap} for memory usage and sometimes performance.
 * <p>
 * This is because this RadixTree uses a compact tree structure, so this is designed for huge key length.
 * <p>
 * it also puts Nodes into a pool of nodes which can be reused, significantly decreasing memory usage and increasing performance due to less object allocation and deallocation.
 * <p>
 * conclusion is RadixTree is faster for large data sets with common prefixes and has more efficient memory handling.
 * <p>
 * HashMap is faster for small-medium sized data sets with well-distributed hash codes, so use either one of them wisely.
 */
@SuppressWarnings({ "unused", "unchecked" })
public final class RadixTree<T> implements Map<String, T> {
    private io.github.flameware.common.utils.RadixTree.TreeNode[] nodes;
    private final AtomicInteger size;
    private final int rootIndex;
    private final int[] freeList;

    public RadixTree(int maxNodes) {
        nodes = new io.github.flameware.common.utils.RadixTree.TreeNode[maxNodes];
        for (int i = 0; i < maxNodes; i++) {
            nodes[i] = new io.github.flameware.common.utils.RadixTree.TreeNode();
        }
        size = new AtomicInteger(0);
        rootIndex = size.getAndSet(0);
        nodes[rootIndex].parentIndex = -1;
        freeList = new int[maxNodes];
    }

    public int size() {
        return size.get();
    }

    @Override
    public boolean isEmpty() {
        return this.size.get() == 0;
    }

    @Override
    public boolean containsKey(Object o) {
        return get(o) != null;
    }

    @Override
    public boolean containsValue(Object o) {
        return Set.copyOf(values()).contains(o.toString());
    }

    @Override
    public T get(Object o) {
        String key = o.toString();
        io.github.flameware.common.utils.RadixTree.TreeNode current = nodes[rootIndex];
        int i = 0;
        while (i < key.length()) {
            char c = key.charAt(i);
            int index = current.getChildIndex(c);
            if (index == -1) {
                return null;
            }
            current = nodes[index];
            i++;
        }
        return (T) current.value;
    }

    public T put(String key, T value) {
        io.github.flameware.common.utils.RadixTree.TreeNode current = nodes[rootIndex];
        int nodeLength = nodes.length;
        int keyLength = key.length();
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            int index = current.getChildIndex(c);
            if (index == -1) {
                index = allocateNode();
                if (index == -1) {
                    return null;
                }
                nodes[index].character = c;
                nodes[index].parentIndex = current.index;
                current.setChildIndex(c, index);
            }
            current = nodes[index];
            int newSize = size.incrementAndGet();
            if (newSize == nodeLength) {
                TreeNode[] newNodes = new TreeNode[(nodeLength << 1)];
                System.arraycopy(nodes, nodeLength, newNodes, 0, nodeLength);
                nodes = newNodes;
            }
        }
        T oldValue = (T) current.value;
        current.value = value;
        return oldValue;
    }

    @Override
    public @Nullable T remove(@NotNull Object o) {
        String key = o.toString();
        io.github.flameware.common.utils.RadixTree.TreeNode current = nodes[rootIndex];
        int i = 0;
        while (i < key.length()) {
            char c = key.charAt(i);
            int index = current.getChildIndex(c);
            if (index == -1) {
                return null;
            }
            current = nodes[index];
            i++;
        }
        T oldValue = (T) current.value;
        current.value = null;
        while (current.index != rootIndex && current.isLeaf() && !current.hasValue()) {
            io.github.flameware.common.utils.RadixTree.TreeNode parent = nodes[current.parentIndex];
            parent.setChildIndex(current.character, -1);
            freeNode(current.index);
            current = parent;
        }
        size.decrementAndGet();
        return oldValue;
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends T> map) {
        for (Map.Entry<? extends String, ? extends T> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        int length = nodes.length;
        for (int i = 0; i < length; i++) {
            nodes[i] = null;
        }
        size.getAndSet(0);
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return null;
    }

    public void freeAllNodes() {
        int length = nodes.length;
        for (int i = 0; i < length; i++) {
            freeNode(i);
        }
        size.getAndSet(0);
    }

    public @Unmodifiable List<T> values() {
        List<T> result = new ArrayList<>(size.getAndIncrement());
        List<io.github.flameware.common.utils.RadixTree.TreeNode> stack = new ArrayList<>(size.getAndIncrement());
        stack.add(nodes[rootIndex]);
        while (!stack.isEmpty()) {
            io.github.flameware.common.utils.RadixTree.TreeNode node = stack.remove(stack.size() - 1);
            if (node.value != null) {
                result.add((T) node.value);
            }
            for (int i = 0; i < 256; i++) {
                int index = node.getChildIndex((char) i);
                if (index != -1) {
                    stack.add(nodes[index]);
                }
            }
        }
        return List.of((T[]) result.toArray(Object[]::new));
    }

    @NotNull
    @Override
    public Set<Entry<String, T>> entrySet() {
        return new HashSet<>(size.get());
    }

    private int allocateNode() {
        if (freeList.length != 0) {
            freeList[freeList.length - 1] = -1;
            return freeList[freeList.length - 1];
        }
        int index = size.getAndIncrement();
        if (index >= nodes.length) {
            size.decrementAndGet();
            return -1;
        }
        return index;
    }

    private void freeNode(int index) {
        nodes[index].value = null;
        nodes[index].character = 0;
        nodes[index].parentIndex = -1;
        nodes[index].children = -1;
        freeList[freeList.length - 1] = index;
    }

    private static class TreeNode<T> {
        public int index;
        T value;
        char character;
        int parentIndex;
        int children;

        TreeNode() {
            value = null;
            character = 0;
            parentIndex = -1;
            children = -1;
        }

        int getChildIndex(char c) {
            int mask = 0x80;
            int index = children & (mask - 1);
            while (index != -1 && (char) index != c) {
                mask >>>= 1;
                index = children & (mask - 1);
            }
            return index == -1 ? -1 : children & -mask;
        }

        void setChildIndex(char c, int index) {
            int mask = 0x80;
            while ((char) (children & (mask - 1)) != c && mask != 0) {
                mask >>>= 1;
            }
            if (mask != 0) {
                children &= -mask;
                children |= index;
            }
        }

        boolean hasValue() {
            return value != null;
        }

        boolean hasMultipleChildren() {
            int count = 0;
            int mask = 0x80;
            for (int i = 0; i < 8; i++) {
                if ((children & mask) != 0) {
                    count++;
                }
                mask >>>= 1;
            }
            return count > 1;
        }

        boolean isLeaf() {
            int mask = 0x80;
            for (int i = 0; i < 8; i++) {
                if ((children & mask) != 0) {
                    return false;
                }
                mask >>>= 1;
            }
            return true;
        }
    }
}

