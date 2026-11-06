package com.example.model;

/**
 * Status das conclusões de tarefas e solicitações de recompensas.
 */
public enum CompletionStatus {
    PENDING("pending", "Aguardando aprovação", "🕐"),
    APPROVED("approved", "Aprovada", "✅"),
    REJECTED("rejected", "Recusada", "❌");

    private final String code;
    private final String label;
    private final String emoji;

    CompletionStatus(String code, String label, String emoji) {
        this.code = code;
        this.label = label;
        this.emoji = emoji;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getFullLabel() {
        return label + " " + emoji;
    }

    public static CompletionStatus fromCode(String code) {
        if (code == null) return PENDING;
        for (CompletionStatus s : values()) {
            if (s.code.equalsIgnoreCase(code) || s.name().equalsIgnoreCase(code)) {
                return s;
            }
        }
        return PENDING;
    }
}
