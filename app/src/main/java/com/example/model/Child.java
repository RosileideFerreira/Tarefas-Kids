package com.example.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Modelo de dados para a criança no Tarefas Kids.
 */
public class Child implements Serializable {
    private long id;
    private String parentId;
    private String name;
    private String avatarEmoji;
    private long createdAt;

    public Child() {
        this.createdAt = System.currentTimeMillis();
    }

    public Child(long id, String parentId, String name, String avatarEmoji, long createdAt) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.avatarEmoji = avatarEmoji;
        this.createdAt = createdAt;
    }

    public Child(String parentId, String name, String avatarEmoji) {
        this(0, parentId, name, avatarEmoji, System.currentTimeMillis());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarEmoji() {
        return avatarEmoji;
    }

    public void setAvatarEmoji(String avatarEmoji) {
        this.avatarEmoji = avatarEmoji;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Child)) return false;
        Child child = (Child) o;
        return id == child.id && Objects.equals(name, child.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return avatarEmoji + " " + name;
    }
}
