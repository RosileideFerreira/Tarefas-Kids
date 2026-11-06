package com.example.model;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * Modelo de dados para a Tarefa Doméstica no Tarefas Kids.
 */
public class TaskItem implements Serializable {
    private long id;
    private String parentId;
    private String title;
    private String room;
    private long rewardCents;
    private boolean active;
    private String description;
    private Long assignedChildId; // null ou 0L significa "Todas as crianças"
    private String assignedChildName; // "Todas as crianças" ou nome da criança específica
    private long createdAt;
    private Integer targetWeek; // null = auto detectado por data de criação; 1..5 = Semana específica; -1 = Todas as semanas

    public TaskItem() {
        this.active = true;
        this.assignedChildName = "Todas as crianças";
        this.createdAt = System.currentTimeMillis();
        this.targetWeek = null;
    }

    public TaskItem(long id, String parentId, String title, String room, long rewardCents,
                    boolean active, String description, Long assignedChildId,
                    String assignedChildName, long createdAt, Integer targetWeek) {
        this.id = id;
        this.parentId = parentId;
        this.title = title;
        this.room = room;
        this.rewardCents = rewardCents;
        this.active = active;
        this.description = description;
        this.assignedChildId = assignedChildId;
        this.assignedChildName = (assignedChildName == null || assignedChildName.trim().isEmpty())
                ? "Todas as crianças" : assignedChildName;
        this.createdAt = createdAt;
        this.targetWeek = targetWeek;
    }

    public TaskItem(long id, String parentId, String title, String room, long rewardCents,
                    boolean active, String description, Long assignedChildId,
                    String assignedChildName, long createdAt) {
        this(id, parentId, title, room, rewardCents, active, description, assignedChildId, assignedChildName, createdAt, null);
    }

    public TaskItem(long id, String parentId, String title, String room, long rewardCents,
                    boolean active, String description, long createdAt) {
        this(id, parentId, title, room, rewardCents, active, description, null, "Todas as crianças", createdAt);
    }

    public TaskItem(String parentId, String title, String room, long rewardCents, String description) {
        this(0, parentId, title, room, rewardCents, true, description, null, "Todas as crianças", System.currentTimeMillis());
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public long getRewardCents() {
        return rewardCents;
    }

    public void setRewardCents(long rewardCents) {
        this.rewardCents = rewardCents;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAssignedChildId() {
        return assignedChildId;
    }

    public void setAssignedChildId(Long assignedChildId) {
        this.assignedChildId = assignedChildId;
    }

    public String getAssignedChildName() {
        return (assignedChildName == null || assignedChildName.trim().isEmpty())
                ? "Todas as crianças" : assignedChildName;
    }

    public void setAssignedChildName(String assignedChildName) {
        this.assignedChildName = assignedChildName;
    }

    public boolean isAssignedTo(long childId) {
        return assignedChildId == null || assignedChildId == 0L || assignedChildId == childId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTargetWeek() {
        return targetWeek;
    }

    public void setTargetWeek(Integer targetWeek) {
        this.targetWeek = targetWeek;
    }

    /**
     * Retorna a semana efetiva (1..5) onde a tarefa se aplica.
     */
    public int getEffectiveWeek() {
        if (targetWeek != null && targetWeek > 0) {
            return targetWeek;
        }
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(createdAt);
        int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
        if (day <= 7) return 1;
        if (day <= 14) return 2;
        if (day <= 21) return 3;
        if (day <= 28) return 4;
        return 5;
    }

    public String getWeekBadgeLabel() {
        if (targetWeek != null && targetWeek == -1) {
            return "📅 Todas as semanas";
        }
        return "📅 Semana " + getEffectiveWeek();
    }

    /**
     * Retorna o valor formatado em Reais (Ex: R$ 1,50).
     */
    public String getFormattedReward() {
        double reais = rewardCents / 100.0;
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", reais);
    }

    /**
     * Define o valor a partir de um valor em Reais.
     */
    public void setRewardFromReais(double reais) {
        this.rewardCents = Math.round(reais * 100);
    }

    public RoomCategory getRoomCategory() {
        return RoomCategory.fromDisplayName(room);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskItem)) return false;
        TaskItem taskItem = (TaskItem) o;
        return id == taskItem.id && rewardCents == taskItem.rewardCents && Objects.equals(title, taskItem.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, rewardCents);
    }
}
