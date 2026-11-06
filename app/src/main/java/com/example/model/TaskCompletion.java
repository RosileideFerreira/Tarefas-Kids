package com.example.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Registro de conclusão de tarefa realizada pela criança.
 */
public class TaskCompletion implements Serializable {
    private long id;
    private String parentId;
    private long taskId;
    private long childId;
    private String taskTitle;
    private String taskRoom;
    private long rewardCents;
    private CompletionStatus status;
    private long completedAt;
    private Long reviewedAt;

    public TaskCompletion() {
        this.status = CompletionStatus.PENDING;
        this.completedAt = System.currentTimeMillis();
    }

    public TaskCompletion(long id, String parentId, long taskId, long childId, String taskTitle,
                          String taskRoom, long rewardCents, CompletionStatus status,
                          long completedAt, Long reviewedAt) {
        this.id = id;
        this.parentId = parentId;
        this.taskId = taskId;
        this.childId = childId;
        this.taskTitle = taskTitle;
        this.taskRoom = taskRoom;
        this.rewardCents = rewardCents;
        this.status = status;
        this.completedAt = completedAt;
        this.reviewedAt = reviewedAt;
    }

    public TaskCompletion(String parentId, long taskId, long childId, String taskTitle,
                          String taskRoom, long rewardCents) {
        this(0, parentId, taskId, childId, taskTitle, taskRoom, rewardCents,
             CompletionStatus.PENDING, System.currentTimeMillis(), null);
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

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskRoom() {
        return taskRoom;
    }

    public void setTaskRoom(String taskRoom) {
        this.taskRoom = taskRoom;
    }

    public long getRewardCents() {
        return rewardCents;
    }

    public void setRewardCents(long rewardCents) {
        this.rewardCents = rewardCents;
    }

    public CompletionStatus getStatus() {
        return status;
    }

    public void setStatus(CompletionStatus status) {
        this.status = status;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }

    public Long getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Long reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getFormattedReward() {
        double reais = rewardCents / 100.0;
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", reais);
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR"));
        return sdf.format(new Date(completedAt));
    }
}
