package icu.baidu.aria2.repo;

public interface Caller {
    String call(String uri);
    default void clear(String uri) {}
}
