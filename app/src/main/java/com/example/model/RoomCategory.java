package com.example.model;

/**
 * Categorias de cômodos suportadas pelo Tarefas Kids.
 */
public enum RoomCategory {
    QUARTO("Quarto", "🛏️"),
    SALA_DE_ESTAR("Sala de estar", "🛋️"),
    COZINHA("Cozinha", "🍽️"),
    BANHEIRO("Banheiro", "🛁"),
    AREA_EXTERNA("Área externa", "🌳"),
    ESTUDOS("Estudos", "📚");

    private final String displayName;
    private final String emoji;

    RoomCategory(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getLabelWithEmoji() {
        return emoji + " " + displayName;
    }

    public static RoomCategory fromDisplayName(String name) {
        if (name == null) return QUARTO;
        for (RoomCategory cat : values()) {
            if (cat.displayName.equalsIgnoreCase(name.trim()) || cat.name().equalsIgnoreCase(name.trim())) {
                return cat;
            }
        }
        return QUARTO;
    }
}
