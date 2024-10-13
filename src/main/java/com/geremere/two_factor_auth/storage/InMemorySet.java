package com.geremere.two_factor_auth.storage;


//java cosplay design
public class InMemorySet<T> {
    private final Cache<T, T> set;

    public InMemorySet(long cleanUpIntervalMinutes) {
        this.set = new Cache<>(cleanUpIntervalMinutes);
    }

    public boolean contains(T key) {
        return set.get(key) != null;
    }

    public void put(T value) {
        set.put(value, value);
    }

    public T get(T key) {
        return set.get(key);
    }

    public void remove(T key) {
        set.remove(key);
    }

    public void shutdown() {
        set.shutdown();
    }
}
