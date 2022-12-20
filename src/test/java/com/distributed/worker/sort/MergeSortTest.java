package com.distributed.worker.sort;

import java.io.IOException;

public class MergeSortTest {
    public static void main(String[] args) throws IOException {
        MergeSort mergeSort = new MergeSort();
        mergeSort.memorySort();
        mergeSort.mergeSort();

    }
}
