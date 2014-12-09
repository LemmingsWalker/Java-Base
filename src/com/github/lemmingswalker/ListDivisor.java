package com.github.lemmingswalker;

import java.util.List;

/**
 * Created by doekewartena on 9/5/14.
 */
public class ListDivisor<T> {

    List<T> list;

    int subListStartIndex = 0;
    int currentGetIndex = 0;

    InstanceHelper instanceHelper;


    public ListDivisor(List<T> list, InstanceHelper<T> instanceHelper) {
        this.list = list;
        this.instanceHelper = instanceHelper;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


    public void reset() {
        subListStartIndex = 0;
        currentGetIndex = 0;

        if (instanceHelper.doResetInstances()) {
            for (T obj : list) {
                instanceHelper.resetInstance(obj);
            }
        }
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


    public SubListGetter<T> getSubListGetter(int size) {

        int fromIndex = subListStartIndex; // inclusive
        int toIndex = fromIndex + size; // exclusive

        for (int i = list.size(); i < toIndex; i++) {
            list.add((T) instanceHelper.createInstance());
        }

        subListStartIndex = toIndex;
        currentGetIndex = toIndex;

        //return list.subList(fromIndex, toIndex);
        return new SubListGetter(fromIndex, toIndex, list);
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    /**
     * Returns a subList starting where the previous subList ended till
     * the latest object added till then.
     *
     * @return
     */
    public SubListGetter<T> getSubListGetter() {
        return getSubListGetter(currentGetIndex - subListStartIndex);
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


    public T getNext() {

        if (currentGetIndex >= list.size()) {
           list.add((T)instanceHelper.createInstance());
        }

        return list.get(currentGetIndex++);

    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


    public void clear() {
        list.clear();
        reset();
    }


    // =====================================================================

    public interface InstanceHelper<T> {
        public T createInstance();
        public boolean doResetInstances();
        public void resetInstance(T obj);
    }

    // =====================================================================





}
