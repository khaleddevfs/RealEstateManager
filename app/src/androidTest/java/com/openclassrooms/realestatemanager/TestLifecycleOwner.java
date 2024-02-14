package com.openclassrooms.realestatemanager;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public class TestLifecycleOwner implements LifecycleOwner {
    private LifecycleRegistry lifecycleRegistry;

    public TestLifecycleOwner() {
        lifecycleRegistry = new LifecycleRegistry(this);
    }

    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}
