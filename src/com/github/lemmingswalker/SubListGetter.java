package com.github.lemmingswalker;

import java.util.List;

/**
 * Created by doekewartena on 12/3/14.
 */
public class SubListGetter<T> {

    int fromIndex, toIndex;
    List<T> target;

    public SubListGetter (int fromIndex, int toIndex, List<T> target) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.target = target;
    }

    public List<T> getSubList() {
        return target.subList(fromIndex, toIndex);
    }

}
