package kz.jarkyn.backend.model.user;

public enum Permission {
    GROUP_VIEW("group-view"),
    GROUP_CREATE("group-create"),
    GROUP_EDIT("group-edit"),
    GROUP_DELETE("group-delete");

    private final String name;

    Permission(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
