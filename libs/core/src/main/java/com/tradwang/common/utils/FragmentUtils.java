package com.tradwang.common.utils;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentUtils {

    private static final int TYPE_ADD_FRAGMENT = 0x01;
    private static final int TYPE_REMOVE_FRAGMENT = 0x01 << 1;
    private static final int TYPE_REPLACE_FRAGMENT = 0x01 << 2;
    private static final int TYPE_HIDE_FRAGMENT = 0x01 << 3;
    private static final int TYPE_SHOW_FRAGMENT = 0x01 << 4;
    private static final int TYPE_HIDE_SHOW_FRAGMENT = 0x01 << 5;
    private static final int TYPE_POP_ADD_FRAGMENT = 0x01 << 6;
    private static final String ARGS_ID = "args_id";
    private static final String ARGS_IS_HIDE = "args_is_hide";
    private static final String ARGS_IS_ADD_STACK = "args_is_add_stack";

    /**
     * 新增fragment
     *
     * @param fragmentManager fragment管理器
     * @param containerId     布局Id
     * @param fragment        lifecycleOwner
     * @return lifecycleOwner
     */
    public static Fragment addFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int containerId) {
        return addFragment(fragmentManager, fragment, containerId, false);
    }

    /**
     * 新增fragment
     *
     * @param fragmentManager fragment管理器
     * @param containerId     布局Id
     * @param fragment        lifecycleOwner
     * @param isHide          是否显示
     * @return lifecycleOwner
     */
    public static Fragment addFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int containerId, boolean isHide) {
        return addFragment(fragmentManager, fragment, containerId, isHide, false);
    }

    /**
     * 新增fragment
     *
     * @param fragmentManager fragment管理器
     * @param containerId     布局Id
     * @param fragment        lifecycleOwner
     * @param isHide          是否显示
     * @param isAddStack      是否入回退栈
     * @return lifecycleOwner
     */
    public static Fragment addFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int containerId, boolean isHide, boolean isAddStack) {
        putArgs(fragment, new Args(containerId, isHide, isAddStack));
        return operateFragment(fragmentManager, null, fragment, TYPE_ADD_FRAGMENT);
    }

    /**
     * 新增fragment
     *
     * @param fragmentManager fragment管理器
     * @param containerId     布局Id
     * @param fragment        lifecycleOwner
     * @param isAddStack      是否入回退栈
     * @param sharedElement   共享元素
     * @return lifecycleOwner
     */
    public static Fragment addFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int containerId, boolean isAddStack, SharedElement... sharedElement) {
        putArgs(fragment, new Args(containerId, false, isAddStack));
        return operateFragment(fragmentManager, null, fragment, TYPE_ADD_FRAGMENT, sharedElement);
    }

    /**
     * 移除fragment
     *
     * @param fragment lifecycleOwner
     * @return lifecycleOwner
     */
    public static Fragment removeFragment(@NonNull Fragment fragment) {
        return operateFragment(fragment.getFragmentManager(), null, fragment, TYPE_REMOVE_FRAGMENT);
    }

    /**
     * 替换fragment
     *
     * @param srcFragment  源fragment
     * @param destFragment 目标fragment
     * @param isAddStack   是否入回退栈
     * @return {@code null} 失败
     */
    public static Fragment replaceFragment(@NonNull Fragment srcFragment,
                                           @NonNull Fragment destFragment,
                                           boolean isAddStack) {
        if (srcFragment.getArguments() == null) return null;
        int containerId = srcFragment.getArguments().getInt(ARGS_ID);
        if (containerId == 0) return null;
        return replaceFragment(srcFragment.getFragmentManager(), containerId, destFragment, isAddStack);
    }

    /**
     * 替换fragment
     *
     * @param fragmentManager fragment管理器
     * @param containerId     布局Id
     * @param fragment        lifecycleOwner
     * @param isAddStack      是否入回退栈
     * @return lifecycleOwner
     */
    public static Fragment replaceFragment(@NonNull FragmentManager fragmentManager,
                                           int containerId,
                                           @NonNull Fragment fragment,
                                           boolean isAddStack) {
        putArgs(fragment, new Args(containerId, false, isAddStack));
        return operateFragment(fragmentManager, null, fragment, TYPE_REPLACE_FRAGMENT);
    }

    /**
     * 出栈fragment
     *
     * @param fragmentManager fragment管理器
     * @return {@code true}: 出栈成功
     * {@code false}: 出栈失败
     */
    public static boolean popFragment(@NonNull FragmentManager fragmentManager) {
        return fragmentManager.popBackStackImmediate();
    }

    /**
     * 出栈到指定fragment
     *
     * @param fragmentManager fragment管理器
     * @param fragmentClass   Fragment类
     * @param isIncludeSelf   是否包括Fragment类自己
     * @return {@code true}: 出栈成功
     * {@code false}: 出栈失败
     */
    public static boolean popToFragment(@NonNull FragmentManager fragmentManager,
                                        Class<? extends Fragment> fragmentClass,
                                        boolean isIncludeSelf) {
        return fragmentManager.popBackStackImmediate(fragmentClass.getName(), isIncludeSelf ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
    }

    /**
     * 先出栈后新增fragment
     *
     * @param fragmentManager fragment管理器
     * @param containerId     布局Id
     * @param fragment        lifecycleOwner
     * @param isAddStack      是否入回退栈
     * @return lifecycleOwner
     */
    public static Fragment popAddFragment(@NonNull FragmentManager fragmentManager,
                                          int containerId,
                                          @NonNull Fragment fragment,
                                          boolean isAddStack,
                                          SharedElement... sharedElement) {
        putArgs(fragment, new Args(containerId, false, isAddStack));
        return operateFragment(fragmentManager, null, fragment, TYPE_POP_ADD_FRAGMENT, sharedElement);
    }

    /**
     * 先出栈后新增fragment
     *
     * @param fragmentManager fragment管理器
     * @param containerId     布局Id
     * @param fragment        lifecycleOwner
     * @param isAddStack      是否入回退栈
     * @return lifecycleOwner
     */
    public static Fragment popAddFragment(@NonNull FragmentManager fragmentManager,
                                          int containerId,
                                          @NonNull Fragment fragment,
                                          boolean isAddStack) {
        putArgs(fragment, new Args(containerId, false, isAddStack));
        return operateFragment(fragmentManager, null, fragment, TYPE_POP_ADD_FRAGMENT);
    }

    /**
     * 隐藏fragment
     *
     * @param fragment lifecycleOwner
     */
    public static Fragment hideFragment(@NonNull Fragment fragment) {
        Args args = getArgs(fragment);
        if (args != null) {
            putArgs(fragment, new Args(args.id, true, args.isAddStack));
        }
        if (fragment.getFragmentManager() != null) {
            return operateFragment(fragment.getFragmentManager(), null, fragment, TYPE_HIDE_FRAGMENT);
        } else {
            return null;
        }
    }

    /**
     * 显示fragment
     *
     * @param fragment lifecycleOwner
     */
    public static Fragment showFragment(@NonNull Fragment fragment) {
        Args args = getArgs(fragment);
        if (args != null) {
            putArgs(fragment, new Args(args.id, false, args.isAddStack));
        }
        if (fragment.getFragmentManager() != null) {
            return operateFragment(fragment.getFragmentManager(), null, fragment, TYPE_SHOW_FRAGMENT);
        } else {
            return null;
        }
    }

    /**
     * 先隐藏后显示fragment
     *
     * @param hideFragment 需要hide的Fragment,如果为null则把栈中的fragment都隐藏
     * @param showFragment 需要show的Fragment
     */
    public static Fragment hideShowFragment(@NonNull Fragment hideFragment, @NonNull Fragment showFragment) {
        Args args = getArgs(hideFragment);
        if (args != null) {
            putArgs(hideFragment, new Args(args.id, true, args.isAddStack));
        }
        args = getArgs(showFragment);
        if (args != null) {
            putArgs(showFragment, new Args(args.id, false, args.isAddStack));
        }
        if (showFragment.getFragmentManager() != null) {
            return operateFragment(showFragment.getFragmentManager(), hideFragment, showFragment, TYPE_HIDE_SHOW_FRAGMENT);
        } else {
            return null;
        }
    }

    /**
     * 传参
     *
     * @param fragment lifecycleOwner
     * @param args     参数
     */
    private static void putArgs(@NonNull Fragment fragment, Args args) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null) {
            bundle = new Bundle();
            fragment.setArguments(bundle);
        }
        bundle.putInt(ARGS_ID, args.id);
        bundle.putBoolean(ARGS_IS_HIDE, args.isHide);
        bundle.putBoolean(ARGS_IS_ADD_STACK, args.isAddStack);
    }

    /**
     * 获取参数
     *
     * @param fragment lifecycleOwner
     */
    private static Args getArgs(@NonNull Fragment fragment) {
        Bundle bundle = fragment.getArguments();
        if (bundle == null || bundle.getInt(ARGS_ID) == 0) return null;
        return new Args(bundle.getInt(ARGS_ID), bundle.getBoolean(ARGS_IS_HIDE), bundle.getBoolean(ARGS_IS_ADD_STACK));
    }

    /**
     * 操作fragment
     *
     * @param fragmentManager fragment管理器
     * @param srcFragment     源fragment
     * @param destFragment    目标fragment
     * @param type            操作类型
     * @param sharedElements  共享元素
     * @return destFragment
     */
    private static Fragment operateFragment(@NonNull FragmentManager fragmentManager, Fragment srcFragment, @NonNull Fragment destFragment, int type, SharedElement... sharedElements) {
        if (srcFragment == destFragment) return null;
        if (srcFragment != null && srcFragment.isRemoving()) {
            return null;
        }
        String name = destFragment.getClass().getName();
        Bundle args = destFragment.getArguments();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (sharedElements == null || sharedElements.length == 0) {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        } else {
            for (SharedElement element : sharedElements) {// 添加共享元素动画
                transaction.addSharedElement(element.sharedElement, element.name);
            }
        }
        if (args == null) return null;
        switch (type) {
            case TYPE_ADD_FRAGMENT:
                transaction.add(args.getInt(ARGS_ID), destFragment, name);
                if (args.getBoolean(ARGS_IS_HIDE)) transaction.hide(destFragment);
                if (args.getBoolean(ARGS_IS_ADD_STACK)) transaction.addToBackStack(name);
                break;
            case TYPE_REMOVE_FRAGMENT:
                transaction.remove(destFragment);
                break;
            case TYPE_POP_ADD_FRAGMENT:
                popFragment(fragmentManager);
                if (args.getBoolean(ARGS_IS_ADD_STACK)) transaction.addToBackStack(name);
                transaction.add(args.getInt(ARGS_ID), destFragment, name);
                break;
            case TYPE_HIDE_FRAGMENT:
                transaction.hide(destFragment);
                break;
            case TYPE_SHOW_FRAGMENT:
                transaction.show(destFragment);
                break;
            case TYPE_HIDE_SHOW_FRAGMENT:
                transaction.hide(srcFragment).show(destFragment);
                break;
            case TYPE_REPLACE_FRAGMENT:
                if (args.getBoolean(ARGS_IS_ADD_STACK)) transaction.addToBackStack(name);
                transaction.replace(args.getInt(ARGS_ID), destFragment, name);
                break;
        }
        transaction.commitAllowingStateLoss();
        return destFragment;
    }

    /**
     * 获取最后加入的fragment
     *
     * @param fragmentManager fragment管理器
     * @return 栈顶fragment
     */
    public static Fragment getLastAddFragment(@NonNull FragmentManager fragmentManager) {
        List<Fragment> fragments = getFragments(fragmentManager);
        if (fragments.isEmpty()) return null;
        for (int i = fragments.size() - 1; i >= 0; i--) {
            if (fragments.get(i) != null) {
                return fragments.get(i);
            }
        }
        return null;
    }

    /**
     * 获取栈中最后加入的fragment
     *
     * @param fragmentManager  fragmentManager
     * @return 最后加入的fragment
     */
    public static Fragment getLastAddFragmentInStack(@NonNull FragmentManager fragmentManager) {
        List<Fragment> fragments = getFragments(fragmentManager);
        if (fragments.isEmpty()) return null;
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment.getArguments() != null && fragment.getArguments().getBoolean(ARGS_IS_ADD_STACK)) {
                return fragment;
            }
        }
        return null;
    }

    /**
     * 获取顶层可见fragment
     *
     * @param fragmentManager fragment管理器
     * @return 栈顶可见fragment
     */
    public static Fragment getTopShowFragment(@NonNull FragmentManager fragmentManager) {
        return getTopShowFragment(fragmentManager, null);
    }

    /**
     * 获取顶层可见fragment
     *
     * @param fragmentManager fragment管理器
     * @param parentFragment  父fragment
     * @return 顶层可见fragment
     */
    private static Fragment getTopShowFragment(@NonNull FragmentManager fragmentManager, Fragment parentFragment) {
        List<Fragment> fragments = getFragments(fragmentManager);
        if (fragments.isEmpty()) return parentFragment;
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment != null && fragment.isResumed() && fragment.isVisible() && fragment.getUserVisibleHint()) {
                return getTopShowFragment(fragment.getChildFragmentManager(), fragment);
            }
        }
        return parentFragment;
    }

    /**
     * 获取栈中顶层可见fragment
     *
     * @param fragmentManager fragment管理器
     * @return 栈顶可见fragment
     */
    public static Fragment getTopShowFragmentInStack(@NonNull FragmentManager fragmentManager) {
        return getTopShowFragmentInStack(fragmentManager, null);
    }

    /**
     * 获取栈中顶层可见fragment
     *
     * @param fragmentManager fragment管理器
     * @param parentFragment  父fragment
     * @return 顶层可见fragment
     */
    private static Fragment getTopShowFragmentInStack(@NonNull FragmentManager fragmentManager,
                                                      Fragment parentFragment) {
        List<Fragment> fragments = getFragments(fragmentManager);
        if (fragments.isEmpty()) return parentFragment;
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment == null) return null;
            if (fragment.getArguments() != null && fragment.isResumed() && fragment.isVisible() && fragment.getUserVisibleHint() && fragment.getArguments().getBoolean(ARGS_IS_ADD_STACK)) {
                return getTopShowFragmentInStack(fragment.getChildFragmentManager(), fragment);
            }
        }
        return parentFragment;
    }

    /**
     * 查找fragment
     *
     * @param fragmentManager fragment管理器
     */
    public static Fragment findFragment(@NonNull FragmentManager fragmentManager, Class<? extends Fragment> fragmentClass) {
        List<Fragment> fragments = getFragments(fragmentManager);
        if (fragments.isEmpty()) return null;
        return fragmentManager.findFragmentByTag(fragmentClass.getName());
    }

    /**
     * 获取目标fragment的前一个fragment
     *
     * @param fragment 目标fragment
     */
    public static Fragment getPreFragment(@NonNull Fragment fragment) {
        FragmentManager fragmentManager = fragment.getFragmentManager();
        if (fragmentManager == null) return null;
        List<Fragment> fragments = getFragments(fragmentManager);
        if (fragments.isEmpty()) return null;
        int index = fragments.indexOf(fragment);
        for (int i = index - 1; i >= 0; i--) {
            if (fragments.get(i) != null) {
                return fragments.get(i);
            }
        }
        return null;
    }

    /**
     * 获取同级别的fragment
     *
     * @param fragmentManager fragment管理器
     * @return 同级别的fragments
     */
    public static List<Fragment> getFragments(@NonNull FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null || fragments.isEmpty()) return Collections.emptyList();
        return fragments;
    }

    /**
     * 获取所有fragment
     *
     * @param fragmentManager fragment管理器
     * @return 所有fragment
     */
    public static List<FragmentNode> getAllFragments(@NonNull FragmentManager fragmentManager) {
        return getAllFragments(fragmentManager, new ArrayList<FragmentNode>());
    }

    /**
     * 获取所有fragment
     *
     * @param fragmentManager fragment管理器
     * @param result          结果
     * @return 所有fragment
     */
    private static List<FragmentNode> getAllFragments(@NonNull FragmentManager fragmentManager,
                                                      List<FragmentNode> result) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null || fragments.isEmpty()) return Collections.emptyList();
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragments.get(i) == null) continue;
            result.add(new FragmentNode(fragment, getAllFragments(fragment.getChildFragmentManager(), new ArrayList<FragmentNode>())));
        }
        return result;
    }

    /**
     * 获取回退栈中的fragment
     * 需之前对fragment的操作都借助该工具类
     *
     * @param fragmentManager fragment管理器
     * @return 回退栈中的fragment
     */
    public static List<FragmentNode> getAllFragmentsInStack(@NonNull FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null || fragments.isEmpty()) return Collections.emptyList();
        List<FragmentNode> result = new ArrayList<>();
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragments.get(i) != null && fragment.getArguments().getBoolean(ARGS_IS_ADD_STACK)) {
                result.add(new FragmentNode(fragment, getAllFragments(fragment.getChildFragmentManager(), new ArrayList<FragmentNode>())));
            }
        }
        return result;
    }

    /**
     * 处理fragment回退键
     * 如果fragment实现了OnBackClickListener接口,返回{@code true}: 表示已消费回退键事件,反之则没消费
     * 具体示例见FragmentActivity
     *
     * @param fragment lifecycleOwner
     * @return 是否消费回退事件
     */
    public static boolean dispatchBackPress(@NonNull Fragment fragment) {
        return fragment.getFragmentManager() != null && dispatchBackPress(fragment.getFragmentManager());
    }

    /**
     * 处理fragment回退键
     * 如果fragment实现了OnBackClickListener接口,返回{@code true}: 表示已消费回退键事件,反之则没消费
     * 具体示例见FragmentActivity
     *
     * @param fragmentManager fragment管理器
     * @return 是否消费回退事件
     */
    public static boolean dispatchBackPress(@NonNull FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments == null || fragments.isEmpty()) return false;
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            if (fragment != null
                    &&
                    fragment.isResumed()
                    &&
                    fragment.isVisible()
                    &&
                    fragment.getUserVisibleHint()
                    &&
                    fragment instanceof OnBackClickListener
                    &&
                    ((OnBackClickListener) fragment).onBackClick()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置背景色
     *
     * @param fragment lifecycleOwner
     * @param color    背景色
     */
    public static void setBackgroundColor(@NonNull Fragment fragment, @ColorInt int color) {
        View view = fragment.getView();
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }

    /**
     * 设置背景资源
     *
     * @param fragment lifecycleOwner
     * @param resId    资源Id
     */
    public static void setBackgroundResource(@NonNull Fragment fragment, @DrawableRes int resId) {
        View view = fragment.getView();
        if (view != null) {
            view.setBackgroundResource(resId);
        }
    }

    /**
     * 设置背景
     *
     * @param fragment   lifecycleOwner
     * @param background 背景
     */
    public static void setBackground(@NonNull Fragment fragment, Drawable background) {
        View view = fragment.getView();
        if (view != null) {
            view.setBackground(background);
        }
    }

    static class Args {
        int id;
        boolean isHide;
        boolean isAddStack;

        Args(int id, boolean isHide, boolean isAddStack) {
            this.id = id;
            this.isHide = isHide;
            this.isAddStack = isAddStack;
        }
    }

    public static class SharedElement {
        View sharedElement;
        String name;

        public SharedElement(View sharedElement, String name) {
            this.sharedElement = sharedElement;
            this.name = name;
        }
    }

    static class FragmentNode {
        Fragment fragment;
        List<FragmentNode> next;

        public FragmentNode(Fragment fragment, List<FragmentNode> next) {
            this.fragment = fragment;
            this.next = next;
        }

        @Override
        public String toString() {
            return fragment.getClass().getSimpleName() + "->" + ((next == null || next.isEmpty()) ? "no child" : next.toString());
        }
    }

    public interface OnBackClickListener {
        boolean onBackClick();
    }
}