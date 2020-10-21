package com.example.poem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :  yuhao
 * @date: 2020/10/20
 * @description:
 */
public class TestAlg {
}

class ListNode{
    int val;
    ListNode next;
    ListNode(int val){this.val = val;};
    ListNode(int val, ListNode next){
        this.val = val;
        this.next = next;
    }

    public void reorderList(ListNode head) {
        List<ListNode> list = new ArrayList<>();
        while (head != null){
            list.add(head);
            head = head.next;
        }
        int size = list.size();
        ListNode pre = new ListNode(0,null);
        if (size % 2 == 0){
            for (int i = 0; i < size / 2; i++) {
                pre.next = list.get(i);
                list.get(i).next = list.get(size - i - 1);
                pre = list.get(size -i - 1);
            }
            list.get(size/2).next = null;
        }else {
            for (int i = 0; i < (size - 1) / 2; i++) {
                pre.next = list.get(i);
                list.get(i).next = list.get(size - i - 1);
                pre = list.get(size - i - 1);
            }
            pre.next = list.get(size / 2);
            list.get(size / 2).next = null;
        }
        head = list.get(0);
    }
}
