package com.example.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Solicitação de troca do saldo acumulado por uma recompensa.
 */
public class RewardRedemption implements Serializable {
    private long id;
    private String parentId;
    private long childId;
    private String childName;
    private String title;
    private long costCents;
    private CompletionStatus status;
    private long createdAt;
    private Long reviewedAt;

    public RewardRedemption() {
        this.status = CompletionStatus.PENDING;
        this.createdAt = System.currentTimeMillis();
    }

    public RewardRedemption(long id, String parentId, long childId, String childName,
                            String title, long costCents, CompletionStatus status,
                            long createdAt, Long reviewedAt) {
        this.id = id;
        this.parentId = parentId;
        this.childId = childId;
        this.childName = childName;
        this.title = title;
        this.costCents = costCents;
        this.status = status;
        this.createdAt = createdAt;
        this.reviewedAt = reviewedAt;
    }

    public RewardRedemption(String parentId, long childId, String childName, String title, long costCents) {
        this(0, parentId, childId, childName, title, costCents,
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

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCostCents() {
        return costCents;
    }

    public void setCostCents(long costCents) {
        this.costCents = costCents;
    }

    public CompletionStatus getStatus() {
        return status;
    }

    public void setStatus(CompletionStatus status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Long reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getFormattedCost() {
        double reais = costCents / 100.0;
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", reais);
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR"));
        return sdf.format(new Date(createdAt));
    }
}
