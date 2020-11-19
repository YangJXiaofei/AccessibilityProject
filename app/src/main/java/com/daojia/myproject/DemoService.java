package com.daojia.myproject;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class DemoService extends AccessibilityService {

    public static DemoService demoService;


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        demoService = this;

    }

    /**
     * 辅助功能是否启动
     */
    public static boolean isStart() {
        return demoService != null;
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //查找是有有"微信红包的text"
        AccessibilityNodeInfo accessibilityNodeInfo = findText("微信红包");
        //如果不为空就走点击微信红包事件 并释放掉accessibilityNodeInfo
        if (accessibilityNodeInfo != null) {
            click(accessibilityNodeInfo);
            accessibilityNodeInfo.recycle();
        }
        //获取根视图
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        //从跟视图获取ContentDescription 语义是开的view
        AccessibilityNodeInfo secondNodeInfo = findContentDescription(rootInActiveWindow, "开");
        //如果不为空的话就去点击
        if (secondNodeInfo != null) {
            click(secondNodeInfo);
            secondNodeInfo.recycle();
        }

    }

    /**
     * 递归查看当前accessibilityBodeInfo是否可点击  如果不可点击就取parent的去点击
     * @param accessibilityNodeInfo
     * @return
     */
    private boolean click(AccessibilityNodeInfo accessibilityNodeInfo) {

        if (accessibilityNodeInfo != null) {
            if (accessibilityNodeInfo.isClickable()) {
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return true;
            } else {
                AccessibilityNodeInfo parent = accessibilityNodeInfo.getParent();
                if (parent != null) {
                    Boolean b = click(parent);
                    parent.recycle();
                    if (b) return true;
                }

            }

        }
        return false;
    }

    /**
     * 获取根视图 通过findAccessibilityNodeInfosByText 查找包含该txt中集合的最后一个 因为抢前面几个的也没有用了
     * @param txt
     * @return
     */
    private AccessibilityNodeInfo findText(String txt) {
        //获取视图跟节点
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        if (rootInActiveWindow == null) return null;
        //获取最后一个微信红包文字的accessibilityNodeInfo
        List<AccessibilityNodeInfo> accessibilityNodeInfosByText = rootInActiveWindow.findAccessibilityNodeInfosByText(txt);
        if (accessibilityNodeInfosByText == null || accessibilityNodeInfosByText.isEmpty())
            return null;
        for (int i = 0; i < accessibilityNodeInfosByText.size() - 1; i++) {
            accessibilityNodeInfosByText.get(i).recycle();
        }
        AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfosByText.get(accessibilityNodeInfosByText.size() - 1);
        return accessibilityNodeInfo;

    }

    /**
     * 递归从根布局一级一级往下查找contentDescription为开的AccessibilityNodeInfo 并返回
     * @param parent
     * @param text
     * @return
     */
    private AccessibilityNodeInfo findContentDescription(AccessibilityNodeInfo parent, String text) {
        if (parent == null) return null;

        for (int i = 0; i < parent.getChildCount(); i++) {


            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null) continue;
            CharSequence contentDescription = child.getContentDescription();
            if (contentDescription != null) {
            }
            if (contentDescription != null && contentDescription.toString().equals(text)) {

                return child;
            } else {

                AccessibilityNodeInfo childChild = findContentDescription(child, text);
                child.recycle();
                if (childChild != null) {
                    return childChild;
                }
            }

        }
        return null;
    }


    @Override
    public void onInterrupt() {
        demoService = null;

    }


}
