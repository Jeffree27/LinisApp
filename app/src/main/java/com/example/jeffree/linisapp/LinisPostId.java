package com.example.jeffree.linisapp;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class LinisPostId {

    @Exclude
    public String LinisPostId;

    public <T extends LinisPostId> T withId(@NonNull final String id) {
        this.LinisPostId = id;
        return (T) this;
    }

}

