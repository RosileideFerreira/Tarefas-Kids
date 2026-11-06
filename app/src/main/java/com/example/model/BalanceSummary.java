package com.example.model;

import java.io.Serializable;
import java.util.Locale;

/**
 * Resumo consolidado de saldos (semanal, mensal, acumulado e disponível) para uma criança.
 */
public class BalanceSummary implements Serializable {
    private long childId;
    private String childName;
    private String avatarEmoji;
    private long weeklyEarningsCents;
    private long monthlyEarningsCents;
    private long totalApprovedEarningsCents;
    private long approvedRedemptionsCents;
    private long pendingRedemptionsCents;
    private int pendingTasksCount;
    private int pendingRedemptionsCount;

    public BalanceSummary(long childId, String childName, String avatarEmoji) {
        this.childId = childId;
        this.childName = childName;
        this.avatarEmoji = avatarEmoji;
    }

    public long getChildId() {
        return childId;
    }

    public String getChildName() {
        return childName;
    }

    public String getAvatarEmoji() {
        return avatarEmoji;
    }

    public long getWeeklyEarningsCents() {
        return weeklyEarningsCents;
    }

    public void setWeeklyEarningsCents(long weeklyEarningsCents) {
        this.weeklyEarningsCents = weeklyEarningsCents;
    }

    public long getMonthlyEarningsCents() {
        return monthlyEarningsCents;
    }

    public void setMonthlyEarningsCents(long monthlyEarningsCents) {
        this.monthlyEarningsCents = monthlyEarningsCents;
    }

    public long getTotalApprovedEarningsCents() {
        return totalApprovedEarningsCents;
    }

    public void setTotalApprovedEarningsCents(long totalApprovedEarningsCents) {
        this.totalApprovedEarningsCents = totalApprovedEarningsCents;
    }

    public long getApprovedRedemptionsCents() {
        return approvedRedemptionsCents;
    }

    public void setApprovedRedemptionsCents(long approvedRedemptionsCents) {
        this.approvedRedemptionsCents = approvedRedemptionsCents;
    }

    public long getPendingRedemptionsCents() {
        return pendingRedemptionsCents;
    }

    public void setPendingRedemptionsCents(long pendingRedemptionsCents) {
        this.pendingRedemptionsCents = pendingRedemptionsCents;
    }

    public int getPendingTasksCount() {
        return pendingTasksCount;
    }

    public void setPendingTasksCount(int pendingTasksCount) {
        this.pendingTasksCount = pendingTasksCount;
    }

    public int getPendingRedemptionsCount() {
        return pendingRedemptionsCount;
    }

    public void setPendingRedemptionsCount(int pendingRedemptionsCount) {
        this.pendingRedemptionsCount = pendingRedemptionsCount;
    }

    /**
     * Saldo disponível para novas recompensas:
     * Total aprovado menos recompensas já aprovadas ou aguardando aprovação.
     */
    public long getAvailableBalanceCents() {
        long available = totalApprovedEarningsCents - approvedRedemptionsCents - pendingRedemptionsCents;
        return Math.max(0, available);
    }

    public String getFormattedWeekly() {
        return formatCurrency(weeklyEarningsCents);
    }

    public String getFormattedMonthly() {
        return formatCurrency(monthlyEarningsCents);
    }

    public String getFormattedTotalApproved() {
        return formatCurrency(totalApprovedEarningsCents);
    }

    public String getFormattedAvailable() {
        return formatCurrency(getAvailableBalanceCents());
    }

    public String getFormattedApprovedRedemptions() {
        return formatCurrency(approvedRedemptionsCents);
    }

    private static String formatCurrency(long cents) {
        double reais = cents / 100.0;
        return String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", reais);
    }
}
